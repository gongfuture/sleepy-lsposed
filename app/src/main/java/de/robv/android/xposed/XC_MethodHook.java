package de.robv.android.xposed;

/**
 * Stub class for Xposed callbacks
 * This is a compile-only stub - the real implementation comes from LSPosed at runtime
 */
public abstract class XC_MethodHook extends XCallback {
    public XC_MethodHook() {}
    
    public XC_MethodHook(int priority) {
        super(priority);
    }

    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {}
    
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {}

    public static class MethodHookParam extends XCallback.Param {
        public Object thisObject;
        public Object[] args;
        private Object result;
        private Throwable throwable;

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
        }
    }

    public static class Unhook {
        public void unhook() {}
    }
}
