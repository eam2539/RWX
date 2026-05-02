package net.rudp.impl;

/* JADX INFO: renamed from: a.a.a.i */
/* JADX INFO: loaded from: game-lib.jar:a/a/a/i.class */
public class CustomTimerTask implements Runnable {

    /* JADX INFO: renamed from: a */
    boolean started;

    /* JADX INFO: renamed from: b */
    String name;

    /* JADX INFO: renamed from: c */
    private Runnable task;

    /* JADX INFO: renamed from: f */
    private boolean cancelled;

    /* JADX INFO: renamed from: g */
    private boolean scheduled;

    /* JADX INFO: renamed from: h */
    private boolean paused;

    /* JADX INFO: renamed from: i */
    private boolean stopped;

    /* JADX INFO: renamed from: j */
    private Object lock = new Object();

    /* JADX INFO: renamed from: d */
    private long delay = 0;

    /* JADX INFO: renamed from: e */
    private long period = 0;

    public CustomTimerTask(String str, Runnable runnable) {
        this.name = str;
        this.task = runnable;
    }

    /* JADX INFO: renamed from: a */
    public void start() {
        this.started = true;
        Thread thread = new Thread(this, this.name);
        thread.setDaemon(true);
        thread.start();
    }

    @Override // java.lang.Runnable
    public void run() {
        label111:
        while (!this.stopped) {
            synchronized (this) {
                while (true) {
                    if (!this.scheduled && !this.stopped) {
                        try {
                            this.wait();
                        } catch (InterruptedException var8) {
                            var8.printStackTrace();
                        }
                    } else {
                        if (this.stopped) {
                            break label111;
                        }
                        break;
                    }
                }
            }

            synchronized (this.lock) {
                this.paused = false;
                this.cancelled = false;
                if (this.delay > 0L) {
                    try {
                        this.lock.wait(this.delay);
                    } catch (InterruptedException var7) {
                        var7.printStackTrace();
                    }
                }

                if (this.cancelled) {
                    continue;
                }
            }

            if (!this.paused) {
                this.task.run();
            }

            if (this.period > 0L) {
                while (true) {
                    synchronized (this.lock) {
                        this.paused = false;

                        try {
                            this.lock.wait(this.period);
                        } catch (InterruptedException var6) {
                            var6.printStackTrace();
                        }

                        if (this.cancelled) {
                            break;
                        }

                        if (this.paused) {
                            continue;
                        }
                    }

                    this.task.run();
                }
            }
        }

        if (this.stopped) {
            this.task = null;
        }
    }

    /* JADX INFO: renamed from: a */
    public synchronized void scheduleAtFixedRate(long j) {
        schedule(j, 0L);
    }

    /* JADX INFO: renamed from: a */
    public synchronized void schedule(long j, long j2) {
        this.delay = j;
        this.period = j2;
        if (this.scheduled) {
            throw new IllegalStateException("already scheduled");
        }
        this.scheduled = true;
        notify();
        synchronized (this.lock) {
            this.lock.notify();
        }
    }

    /* JADX INFO: renamed from: b */
    public synchronized boolean isScheduled() {
        return this.scheduled;
    }

    /* JADX INFO: renamed from: c */
    public synchronized boolean isNotScheduled() {
        return !isScheduled();
    }

    /* JADX INFO: renamed from: d */
    public synchronized void pause() {
        synchronized (this.lock) {
            this.paused = true;
            this.lock.notify();
        }
    }

    /* JADX INFO: renamed from: e */
    public synchronized void cancel() {
        this.scheduled = false;
        synchronized (this.lock) {
            this.cancelled = true;
            this.lock.notify();
        }
    }

    /* JADX INFO: renamed from: f */
    public synchronized void stop() {
        cancel();
        this.stopped = true;
        notify();
    }
}
