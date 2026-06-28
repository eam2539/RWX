from __future__ import annotations

import importlib.util
import subprocess
import time
from pathlib import Path
from types import ModuleType


def load_capture_module() -> ModuleType:
    script_path = Path(__file__).resolve().parents[1] / "scripts" / "capture_screenshots.py"
    spec = importlib.util.spec_from_file_location("capture_screenshots", script_path)
    assert spec is not None
    assert spec.loader is not None
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


def test_window_capture_uses_active_window_without_focus_by_default(monkeypatch, tmp_path) -> None:
    module = load_capture_module()
    calls: list[tuple[str, str | list[str]]] = []

    def fake_restore_dpms(timeout: float = 5.0) -> bool:
        calls.append(("dpms", str(timeout)))
        return True

    def fake_focus_window(title: str, timeout: float) -> bool:
        calls.append(("focus", title))
        return True

    def fake_wait_for_output(path: Path, timeout: float) -> bool:
        return True

    def fake_run(
        command: list[str],
        check: bool,
        stdout: int,
        stderr: int,
        text: bool,
        timeout: float,
    ) -> subprocess.CompletedProcess[str]:
        calls.append(("spectacle", command))
        return subprocess.CompletedProcess(command, 0, "", "")

    monkeypatch.setattr(module, "restore_dpms", fake_restore_dpms)
    monkeypatch.setattr(module, "focus_window", fake_focus_window)
    monkeypatch.setattr(module, "wait_for_output", fake_wait_for_output)
    monkeypatch.setattr(module.subprocess, "run", fake_run)

    mode = module.run_spectacle(tmp_path / "frame.png", "window", 1.0)

    assert mode == "activewindow"
    assert calls[0][0] == "dpms"
    assert calls[1][0] == "dpms"
    assert calls[2][0] == "spectacle"
    assert ("focus", "RWX Kool") not in calls
    assert "--activewindow" in calls[2][1]
    assert "--fullscreen" not in calls[2][1]


def test_window_capture_can_focus_explicit_window_title(monkeypatch, tmp_path) -> None:
    module = load_capture_module()
    calls: list[tuple[str, str]] = []

    def fake_restore_dpms(timeout: float = 5.0) -> bool:
        calls.append(("dpms", str(timeout)))
        return True

    def fake_focus_window(title: str, timeout: float) -> bool:
        calls.append(("focus", title))
        return True

    def fake_capture_x11_window(path: Path, title: str, timeout: float) -> bool:
        calls.append(("xwd", title))
        return True

    monkeypatch.setattr(module, "restore_dpms", fake_restore_dpms)
    monkeypatch.setattr(module, "focus_window", fake_focus_window)
    monkeypatch.setattr(module, "capture_x11_window", fake_capture_x11_window)

    mode = module.run_spectacle(tmp_path / "frame.png", "window", 1.0, window_title="RWX")

    assert mode == "xwd-window"
    assert calls == [("dpms", "1.0"), ("xwd", "RWX")]


def test_window_capture_with_title_does_not_fall_back_to_spectacle(monkeypatch, tmp_path) -> None:
    module = load_capture_module()
    calls: list[list[str]] = []

    monkeypatch.setattr(module, "restore_dpms", lambda timeout=5.0: True)
    monkeypatch.setattr(module, "capture_x11_window", lambda path, title, timeout: False)
    monkeypatch.setattr(module.subprocess, "run", lambda command, **kwargs: calls.append(command))

    try:
        module.run_spectacle(tmp_path / "frame.png", "window", 1.0, window_title="RWX")
    except RuntimeError as error:
        assert "Could not capture X11 window titled: RWX" in str(error)
    else:
        raise AssertionError("expected RuntimeError")

    assert calls == []


def test_capture_run_sets_unique_window_title_for_fullscreen(monkeypatch, tmp_path) -> None:
    module = load_capture_module()
    envs: list[dict[str, str]] = []
    calls: list[str] = []

    class FakeProcess:
        stdout = None

        def poll(self) -> None:
            return None

    def fake_popen(*args, **kwargs) -> FakeProcess:
        envs.append(kwargs["env"])
        calls.append("popen")
        return FakeProcess()

    monkeypatch.setattr(module, "restore_dpms", lambda timeout=5.0: True)
    monkeypatch.setattr(module.subprocess, "Popen", fake_popen)
    monkeypatch.setattr(module, "read_until_ready", lambda *args: False)
    monkeypatch.setattr(module, "stop_process_group", lambda process: calls.append("stop"))

    args = module.argparse.Namespace(
        out_dir=tmp_path,
        count=1,
        interval=0.0,
        prefix="frame",
        mode="fullscreen",
        window_title=None,
        timeout=1.0,
        ready_marker="ready",
        ready_count=1,
        ready_timeout=0.01,
        settle=0.0,
        screen=None,
        color_scheme=None,
        level_mode=None,
        settings_page=None,
        demo_dialog=False,
    )

    assert module.capture_run(args) == 1
    assert envs[0]["RWX_WINDOW_TITLE"].startswith("RWX Screenshot ")
    assert calls == ["popen", "stop"]


def test_capture_run_keep_running_writes_log_and_skips_stop(monkeypatch, tmp_path) -> None:
    module = load_capture_module()
    calls: list[str] = []
    stdout_names: list[str] = []

    class FakeProcess:
        pid = 12345
        stdout = None

        def poll(self) -> None:
            return None

    def fake_popen(*args, **kwargs) -> FakeProcess:
        stdout_names.append(kwargs["stdout"].name)
        calls.append("popen")
        return FakeProcess()

    monkeypatch.setattr(module, "restore_dpms", lambda timeout=5.0: True)
    monkeypatch.setattr(module.subprocess, "Popen", fake_popen)
    monkeypatch.setattr(module, "read_log_until_ready", lambda *args: True)
    monkeypatch.setattr(module, "run_spectacle", lambda *args: "activewindow")
    monkeypatch.setattr(module, "stop_process_group", lambda process: calls.append("stop"))

    log_file = tmp_path / "run.log"
    args = module.argparse.Namespace(
        out_dir=tmp_path,
        count=1,
        interval=0.0,
        prefix="frame",
        mode="window",
        window_title="RWX Test",
        timeout=1.0,
        keep_running=True,
        log_file=log_file,
        ready_marker="ready",
        ready_count=1,
        ready_timeout=0.01,
        settle=0.0,
        screen=None,
        color_scheme=None,
        level_mode=None,
        settings_page=None,
        demo_dialog=False,
    )

    assert module.capture_run(args) == 0
    assert calls == ["popen"]
    assert stdout_names == [str(log_file)]


def test_read_until_ready_times_out_without_output() -> None:
    module = load_capture_module()

    class BlockingStream:
        def __iter__(self):
            time.sleep(1.0)
            return iter(())

    class BlockingProcess:
        stdout = BlockingStream()

        def poll(self) -> None:
            return None

    started = time.monotonic()
    assert module.read_until_ready(BlockingProcess(), "ready", 0.05) is False
    assert time.monotonic() - started < 0.5


def test_capture_run_restores_dpms_when_ready_wait_fails(monkeypatch, tmp_path) -> None:
    module = load_capture_module()
    calls: list[str] = []

    class FakeProcess:
        pass

    def fake_restore_dpms(timeout: float = 5.0) -> bool:
        calls.append("dpms")
        return True

    def fake_popen(*args, **kwargs) -> FakeProcess:
        calls.append("popen")
        return FakeProcess()

    def fake_stop_process_group(process: FakeProcess) -> None:
        calls.append("stop")

    monkeypatch.setattr(module, "restore_dpms", fake_restore_dpms)
    monkeypatch.setattr(module.subprocess, "Popen", fake_popen)
    monkeypatch.setattr(module, "read_until_ready", lambda *args: False)
    monkeypatch.setattr(module, "stop_process_group", fake_stop_process_group)

    args = module.argparse.Namespace(
        out_dir=tmp_path,
        count=1,
        interval=0.0,
        prefix="frame",
        mode="window",
        window_title=None,
        timeout=1.0,
        ready_marker="ready",
        ready_count=1,
        ready_timeout=0.01,
        settle=0.0,
        screen=None,
        color_scheme=None,
        level_mode=None,
        settings_page=None,
        demo_dialog=False,
    )

    assert module.capture_run(args) == 1
    assert calls == ["dpms", "popen", "stop", "dpms"]
