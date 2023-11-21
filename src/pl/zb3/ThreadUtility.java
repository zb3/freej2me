package pl.zb3;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ThreadUtility {
    
    private static final boolean SUPPORTS_VIRTUAL_THREADS;
    private static Method startVirtualThreadMethod = null;

    static {
        boolean supportsVirtualThreads = false;
        try {
            startVirtualThreadMethod = Thread.class.getMethod("startVirtualThread", Runnable.class);
            supportsVirtualThreads = startVirtualThreadMethod != null;
        } catch (NoSuchMethodException e) {
            supportsVirtualThreads = false;
        }
        SUPPORTS_VIRTUAL_THREADS = supportsVirtualThreads;
    }

    public static Thread run(Runnable task) {
        if (SUPPORTS_VIRTUAL_THREADS) {
            try {
                System.out.println("runnin on a virtual thread");
                return (Thread) startVirtualThreadMethod.invoke(null, task);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Failed to start virtual thread", e);
            }
        } else {
            Thread thread = new Thread(task);
            thread.start();
            return thread;
        }
    }

    // Prevent instantiation
    private ThreadUtility() {
    }
}
