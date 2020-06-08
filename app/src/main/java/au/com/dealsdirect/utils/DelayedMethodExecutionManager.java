package au.com.dealsdirect.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Enumeration;
import java.util.Hashtable;

public class DelayedMethodExecutionManager {

    private static DelayedMethodExecutionManager instance = null;

    private Hashtable<String, Hashtable<String, Runnable>> delayedMethodCalls;

    public static DelayedMethodExecutionManager getInstance() {
        if (instance == null) {
            instance = new DelayedMethodExecutionManager();
        }
        return instance;
    }

    private DelayedMethodExecutionManager() {
        delayedMethodCalls = new Hashtable<>();
    }

    public void queueDelayedMethodCall(@NotNull String owner, @NotNull String key, @NotNull Runnable runnable) {
        Hashtable<String, Runnable> delayedMethodCallsOfObject = delayedMethodCalls.get(owner);
        if (delayedMethodCallsOfObject == null) {
            delayedMethodCallsOfObject = new Hashtable<>();
            delayedMethodCalls.put(owner, delayedMethodCallsOfObject);
        }
        delayedMethodCallsOfObject.put(key, runnable);
    }

    public void executeDelayedMethodCall(String owner, String key) {
        Hashtable<String, Runnable> delayedMethodCallsOfObject = delayedMethodCalls.get(owner);
        if (delayedMethodCallsOfObject == null) {
            return;
        }
        Runnable delayedMethodCall = delayedMethodCallsOfObject.get(key);
        if (delayedMethodCall == null) {
            return;
        }

        delayedMethodCall.run();
        delayedMethodCallsOfObject.remove(key);

        if (delayedMethodCallsOfObject.isEmpty()) {
            delayedMethodCalls.remove(owner);
        }
    }

    public void executeDelayedMethodCalls(String owner) {
        Hashtable<String, Runnable> delayedMethodCallsOfObject = delayedMethodCalls.get(owner);
        if (delayedMethodCallsOfObject != null) {
            Enumeration<String> keys = delayedMethodCallsOfObject.keys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                executeDelayedMethodCall(owner, key);
            }
        }
    }

    public void executeDelayedMethodCalls() {
        Enumeration<String> owners = delayedMethodCalls.keys();
        while (owners.hasMoreElements()) {
            executeDelayedMethodCalls(owners.nextElement());
        }
    }
}
