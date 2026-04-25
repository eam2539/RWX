package com.codedisaster.steamworks;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamSharedLibraryLoader.class */
class SteamSharedLibraryLoader {
    private final String libraryPath;
    static boolean alreadyLoaded = false;
    static File librarySystemPath;
    private static final String extractSubFolder = "steamworks4j/";

    private SteamSharedLibraryLoader(String str) {
        this.libraryPath = str;
    }

    private String getLibNameWindows(String str, boolean z) {
        return str + (z ? "64" : VariableScope.nullOrMissingString) + ".dll";
    }

    private String getLibNameLinux(String str, boolean z) {
        return "lib" + str + (z ? "64" : VariableScope.nullOrMissingString) + ".so";
    }

    private String getLibNameMac(String str) {
        return "lib" + str + ".dylib";
    }

    private void loadLibraries(String... strArr) throws IOException {
        String strExtractLibrary;
        String property = System.getProperty("os.name");
        String property2 = System.getProperty("os.arch");
        boolean zContains = property.contains("Windows");
        boolean zContains2 = property.contains("Linux");
        boolean zContains3 = property.contains("Mac");
        boolean z = property2.equals("amd64") || property2.equals("x86_64");
        String[] strArr2 = new String[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            if (zContains) {
                strArr2[i] = getLibNameWindows(strArr[i], z);
            } else if (zContains2) {
                strArr2[i] = getLibNameLinux(strArr[i], z);
            } else if (zContains3) {
                strArr2[i] = getLibNameMac(strArr[i]);
            } else {
                throw new IOException("Unrecognized system architecture: " + property + ", " + property2);
            }
        }
        if (this.libraryPath == null) {
            String strCrc = ".nohash";
            CRC32 crc32 = new CRC32();
            for (String str : strArr2) {
                strCrc = crc(crc32, getClass().getResourceAsStream("/" + str));
            }
            librarySystemPath = discoverExtractLocation(extractSubFolder + strCrc, UUID.randomUUID().toString());
            System.out.println("steam librarySystemPath name:" + librarySystemPath);
            if (librarySystemPath == null) {
                throw new IOException("Failed to create temp folder to extract native libraries");
            }
            librarySystemPath = librarySystemPath.getParentFile();
        } else {
            librarySystemPath = new File(this.libraryPath);
        }
        for (String str2 : strArr2) {
            if (this.libraryPath == null) {
                strExtractLibrary = extractLibrary(librarySystemPath, str2);
            } else {
                strExtractLibrary = librarySystemPath + "/" + str2;
            }
            System.load(new File(strExtractLibrary).getCanonicalPath());
        }
    }


    private String extractLibrary(File file, String string) throws IOException {
        InputStream inputStream;
        ZipFile zipFile;
        File file2;
        block7: {
            Object object;
            file2 = new File(file, string);
            zipFile = null;
            if (this.libraryPath != null) {
                System.out.println("steam extractLibrary zip:" + string);
                zipFile = new ZipFile(this.libraryPath);
                object = zipFile.getEntry(string);
                inputStream = zipFile.getInputStream((ZipEntry)object);
            } else {
                System.out.println("steam extractLibrary name:" + string);
                inputStream = SteamSharedLibraryLoader.class.getResourceAsStream("/" + string);
            }
            if (inputStream == null) {
                throw new IOException("Error extracting " + string + " from " + (this.libraryPath != null ? this.libraryPath : "resources"));
            }
            try {
                int n;
                object = new FileOutputStream(file2);
                byte[] byArray = new byte[4096];
                while ((n = inputStream.read(byArray)) != -1) {
                    ((FileOutputStream)object).write(byArray, 0, n);
                }
                ((FileOutputStream)object).close();
            } catch (IOException iOException) {
                if (file2.exists()) break block7;
                throw iOException;
            }
        }
        inputStream.close();
        if (zipFile != null) {
            zipFile.close();
        }
        return file2.getAbsolutePath();
    }

    private String crc(CRC32 crc32, InputStream inputStream) {
        byte[] bArr = new byte[4096];
        while (true) {
            try {
                try {
                    int i = inputStream.read(bArr);
                    if (i == -1) {
                        break;
                    }
                    crc32.update(bArr, 0, i);
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        inputStream.close();
                    } catch (IOException e2) {
                    }
                }
            } catch (Throwable th) {
                try {
                    inputStream.close();
                } catch (IOException e3) {
                }
                throw th;
            }
        }
        try {
            inputStream.close();
        } catch (IOException e4) {
        }
        return Long.toHexString(crc32.getValue());
    }

    static boolean loadLibraries(String str) throws SteamException {
        if (alreadyLoaded) {
            return true;
        }
        try {
            new SteamSharedLibraryLoader(str).loadLibraries("steam_api", "steamworks4j");
            alreadyLoaded = true;
            return true;
        } catch (Throwable th) {
            throw new SteamException(th);
        }
    }

    private static File discoverExtractLocation(String str, String str2) {
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + str, str2);
        if (canWrite(file)) {
            return file;
        }
        try {
            File fileCreateTempFile = File.createTempFile(str, null);
            if (fileCreateTempFile.delete()) {
                File file2 = new File(fileCreateTempFile, str2);
                if (canWrite(file2)) {
                    return file2;
                }
            }
        } catch (IOException e) {
        }
        File file3 = new File(System.getProperty("user.home") + "/." + str, str2);
        if (canWrite(file3)) {
            return file3;
        }
        File file4 = new File(".tmp/" + str, str2);
        if (canWrite(file4)) {
            return file4;
        }
        return null;
    }

    private static boolean canWrite(File file) {
        File parentFile = file.getParentFile();
        if (file.exists()) {
            if (!file.canWrite() || !canExecute(file)) {
                return false;
            }
        } else if ((!parentFile.exists() && !parentFile.mkdirs()) || !parentFile.isDirectory()) {
            return false;
        }
        File file2 = new File(parentFile, UUID.randomUUID().toString());
        try {
            new FileOutputStream(file2).close();
            boolean zCanExecute = canExecute(file2);
            file2.delete();
            return zCanExecute;
        } catch (IOException e) {
            file2.delete();
            return false;
        } catch (Throwable th) {
            file2.delete();
            throw th;
        }
    }

    private static boolean canExecute(File file) {
        try {
            if (file.canExecute()) {
                return true;
            }
            if (file.setExecutable(true)) {
                return file.canExecute();
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
