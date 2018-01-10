package cn.jants.core.holder;

/**
 * @author MrShun
 * @version 1.0
 */
public class ContextRequestManager {

    private final static ThreadLocal<ClientRequest> threadLocal = new ThreadLocal<>();

    public static void set(ClientRequest clientHolder) {
        threadLocal.set(clientHolder);
    }

    public static ClientRequest get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}
