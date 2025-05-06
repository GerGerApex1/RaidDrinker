package me.gergerapex1.raiddrinker;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayDeque;
import java.util.Queue;

public class TickScheduler {
    private final Queue<ScheduledTask> taskQueue = new ArrayDeque<>();

    public TickScheduler() {
        // Register a tick listener
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            // Process tasks each tick
            if (!taskQueue.isEmpty()) {
                ScheduledTask currentTask = taskQueue.peek();
                if (currentTask != null && --currentTask.ticksRemaining <= 0) {
                    currentTask.runnable.run();
                    taskQueue.poll(); // Remove the task from the queue
                }
            }
        });
    }

    public void schedule(int ticks, Runnable runnable) {
        taskQueue.add(new ScheduledTask(ticks, runnable));
    }

    private static class ScheduledTask {
        int ticksRemaining;
        final Runnable runnable;

        ScheduledTask(int ticks, Runnable runnable) {
            this.ticksRemaining = ticks;
            this.runnable = runnable;
        }
    }
}