package dev.smartshub.shpets.api.service.scheduler;

@FunctionalInterface
public interface CancellableTask {
    boolean run();
}