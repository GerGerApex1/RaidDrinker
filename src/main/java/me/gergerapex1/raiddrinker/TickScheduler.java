package me.gergerapex1.raiddrinker;

import java.util.ArrayDeque;
import java.util.Queue;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class TickScheduler {

    private final Queue<ScheduledTask> taskQueue = new ArrayDeque<>();

    public TickScheduler() {
        // Register a tick listener
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
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

        final Runnable runnable;
        int ticksRemaining;

        ScheduledTask(int ticks, Runnable runnable) {
            this.ticksRemaining = ticks;
            this.runnable = runnable;
        }
    }
}