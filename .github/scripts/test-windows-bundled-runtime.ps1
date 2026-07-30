[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$ImagePath,

    [Parameter(Mandatory = $true)]
    [string]$ExternalJavaHome,

    [int]$TimeoutSeconds = 20
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Get-ProcessTreeIds([int]$RootProcessId) {
    $result = [Collections.Generic.HashSet[int]]::new()
    [void]$result.Add($RootProcessId)

    do {
        $countBefore = $result.Count
        Get-CimInstance Win32_Process | Where-Object {
            $result.Contains([int]$_.ParentProcessId)
        } | ForEach-Object {
            [void]$result.Add([int]$_.ProcessId)
        }
    } while ($result.Count -ne $countBefore)

    return $result
}

function Normalize-Path([string]$Path) {
    return [IO.Path]::GetFullPath($Path).TrimEnd([IO.Path]::DirectorySeparatorChar)
}

$resolvedImage = Normalize-Path((Resolve-Path -LiteralPath $ImagePath).Path)
$resolvedExternalJava = Normalize-Path((Resolve-Path -LiteralPath $ExternalJavaHome).Path)
$sourceLauncher = Join-Path $resolvedImage 'RWX.exe'
$sourceApp = Join-Path $resolvedImage 'app'
$sourceRuntime = Join-Path $resolvedImage 'runtime'

foreach ($requiredPath in @($sourceLauncher, $sourceApp, $sourceRuntime)) {
    if (-not (Test-Path -LiteralPath $requiredPath)) {
        throw "发布镜像缺少必需路径: $requiredPath"
    }
}
if (-not (Test-Path -LiteralPath (Join-Path $resolvedExternalJava 'bin\java.dll'))) {
    throw "外层 Java Home 缺少 bin\java.dll: $resolvedExternalJava"
}

$testRoot = Join-Path ([IO.Path]::GetTempPath()) ("rwx-launcher-collision-" + [Guid]::NewGuid().ToString('N'))
$testImage = Join-Path $testRoot 'RWX'
$testLauncher = Join-Path $testImage 'RWX.exe'
$launcherProcess = $null
$junctions = [Collections.Generic.List[string]]::new()

try {
    [void][IO.Directory]::CreateDirectory($testImage)
    Copy-Item -LiteralPath $sourceLauncher -Destination $testLauncher

    foreach ($directoryName in @('bin', 'conf', 'legal', 'lib')) {
        $target = Join-Path $resolvedExternalJava $directoryName
        if (Test-Path -LiteralPath $target) {
            $junction = Join-Path $testRoot $directoryName
            New-Item -ItemType Junction -Path $junction -Target $target | Out-Null
            $junctions.Add($junction)
        }
    }
    $imageDirectories = @(
        @{ Name = 'app'; Target = $sourceApp }
        @{ Name = 'runtime'; Target = $sourceRuntime }
    )
    foreach ($mapping in $imageDirectories) {
        $junction = Join-Path $testImage $mapping.Name
        New-Item -ItemType Junction -Path $junction -Target $mapping.Target | Out-Null
        $junctions.Add($junction)
    }

    $startArguments = @{
        FilePath = $testLauncher
        WorkingDirectory = $testImage
        WindowStyle = 'Hidden'
        PassThru = $true
    }
    $launcherProcess = Start-Process @startArguments

    $deadline = [DateTime]::UtcNow.AddSeconds($TimeoutSeconds)
    $loadedJvm = $null
    while ([DateTime]::UtcNow -lt $deadline -and $null -eq $loadedJvm) {
        foreach ($processId in Get-ProcessTreeIds $launcherProcess.Id) {
            $process = Get-Process -Id $processId -ErrorAction SilentlyContinue
            if ($null -eq $process) { continue }
            try {
                $loadedJvm = $process.Modules |
                    Where-Object { $_.ModuleName -ieq 'jvm.dll' } |
                    Select-Object -First 1 -ExpandProperty FileName
            } catch {
                continue
            }
            if ($null -ne $loadedJvm) { break }
        }
        if ($null -eq $loadedJvm) { Start-Sleep -Milliseconds 100 }
    }

    if ($null -eq $loadedJvm) {
        throw "在 $TimeoutSeconds 秒内没有观察到 RWX 加载 jvm.dll"
    }

    $normalizedJvm = Normalize-Path($loadedJvm)
    $allowedRuntimeRoots = @(
        (Normalize-Path((Join-Path $testImage 'runtime')))
        (Normalize-Path($sourceRuntime))
    )
    if (-not ($allowedRuntimeRoots | Where-Object {
        $normalizedJvm.StartsWith($_ + [IO.Path]::DirectorySeparatorChar, [StringComparison]::OrdinalIgnoreCase)
    })) {
        throw "RWX 加载了包外 JVM: $normalizedJvm"
    }

    Write-Host "RWX 使用包内 JVM: $normalizedJvm"
} finally {
    if ($null -ne $launcherProcess) {
        foreach ($processId in Get-ProcessTreeIds $launcherProcess.Id) {
            Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
        }
    }
    foreach ($junction in $junctions) {
        if (Test-Path -LiteralPath $junction) {
            [IO.Directory]::Delete($junction, $false)
        }
    }
    if (Test-Path -LiteralPath $testRoot) {
        [IO.Directory]::Delete($testRoot, $true)
    }
}
