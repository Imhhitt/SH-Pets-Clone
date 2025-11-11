package dev.smartshub.shpets.api.service.scheduler;

public interface TaskScheduler {
    void runAsync(Runnable runnable, long delay, long period);
    void runSync(Runnable runnable, long delay, long period);
    void runAsyncLater(Runnable runnable, long delay);
    void runSyncLater(Runnable runnable, long delay);
    void runSyncRepeating(CancellableTask task, long delay, long period);
    void runAsyncRepeating(CancellableTask task, long delay, long period);
}