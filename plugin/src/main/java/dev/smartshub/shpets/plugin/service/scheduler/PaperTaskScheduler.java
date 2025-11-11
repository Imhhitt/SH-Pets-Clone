package dev.smartshub.shpets.plugin.service.scheduler;

import dev.smartshub.shpets.api.service.scheduler.CancellableTask;
import dev.smartshub.shpets.api.service.scheduler.TaskScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PaperTaskScheduler implements TaskScheduler {

    private final Plugin plugin;
    private final Set<ScheduledTask> activeTasks = ConcurrentHashMap.newKeySet();

    public PaperTaskScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runAsync(Runnable runnable, long delay, long period) {
        ScheduledTask task = plugin.getServer()
                .getAsyncScheduler()
                .runAtFixedRate(plugin,
                        scheduledTask -> {
                            try {
                                runnable.run();
                            } catch (Exception e) {
                                plugin.getLogger().severe("Error in async repeating task: " + e.getMessage());
                                e.printStackTrace();
                            }
                        },
                        ticksToMillis(delay),
                        ticksToMillis(period),
                        TimeUnit.MILLISECONDS
                );

        activeTasks.add(task);
    }

    @Override
    public void runSync(Runnable runnable, long delay, long period) {
        ScheduledTask task = plugin.getServer()
                .getGlobalRegionScheduler()
                .runAtFixedRate(plugin,
                        scheduledTask -> {
                            try {
                                runnable.run();
                            } catch (Exception e) {
                                plugin.getLogger().severe("Error in sync repeating task: " + e.getMessage());
                                e.printStackTrace();
                            }
                        },
                        Math.max(1L, delay),
                        Math.max(1L, period)
                );

        activeTasks.add(task);
    }

    @Override
    public void runAsyncLater(Runnable runnable, long delay) {
        ScheduledTask task = plugin.getServer()
                .getAsyncScheduler()
                .runDelayed(plugin,
                        scheduledTask -> {
                            try {
                                runnable.run();
                            } catch (Exception e) {
                                plugin.getLogger().severe("Error in async delayed task: " + e.getMessage());
                                e.printStackTrace();
                            } finally {
                                activeTasks.remove(scheduledTask);
                            }
                        },
                        ticksToMillis(delay),
                        TimeUnit.MILLISECONDS
                );

        activeTasks.add(task);
    }

    @Override
    public void runSyncLater(Runnable runnable, long delay) {
        ScheduledTask task = plugin.getServer()
                .getGlobalRegionScheduler()
                .runDelayed(plugin,
                        scheduledTask -> {
                            try {
                                runnable.run();
                            } catch (Exception e) {
                                plugin.getLogger().severe("Error in sync delayed task: " + e.getMessage());
                                e.printStackTrace();
                            } finally {
                                activeTasks.remove(scheduledTask);
                            }
                        },
                        delay
                );

        activeTasks.add(task);
    }

    @Override
    public void runSyncRepeating(CancellableTask task, long delay, long period) {
        ScheduledTask scheduledTask = plugin.getServer()
                .getGlobalRegionScheduler()
                .runAtFixedRate(plugin,
                        st -> {
                            try {
                                boolean shouldContinue = task.run();
                                if (!shouldContinue) {
                                    st.cancel();
                                    activeTasks.remove(st);
                                }
                            } catch (Exception e) {
                                plugin.getLogger().severe("Error in cancellable sync task: " + e.getMessage());
                                e.printStackTrace();
                                st.cancel();
                                activeTasks.remove(st);
                            }
                        },
                        Math.max(1L, delay),
                        Math.max(1L, period)
                );

        activeTasks.add(scheduledTask);
    }

    @Override
    public void runAsyncRepeating(CancellableTask task, long delay, long period) {
        ScheduledTask scheduledTask = plugin.getServer()
                .getAsyncScheduler()
                .runAtFixedRate(plugin,
                        st -> {
                            try {
                                boolean shouldContinue = task.run();
                                if (!shouldContinue) {
                                    st.cancel();
                                    activeTasks.remove(st);
                                }
                            } catch (Exception e) {
                                plugin.getLogger().severe("Error in cancellable async task: " + e.getMessage());
                                e.printStackTrace();
                                st.cancel();
                                activeTasks.remove(st);
                            }
                        },
                        ticksToMillis(delay),
                        ticksToMillis(period),
                        TimeUnit.MILLISECONDS
                );

        activeTasks.add(scheduledTask);
    }

    public void cancelAllTasks() {
        activeTasks.forEach(ScheduledTask::cancel);
        activeTasks.clear();
    }

    private long ticksToMillis(long ticks) {
        return ticks * 50L;
    }
}