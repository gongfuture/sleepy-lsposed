package de.robv.android.xposed;

/**
 * Stub class for Xposed load package callback
 * This is a compile-only stub - the real implementation comes from LSPosed at runtime
 */
public class XC_LoadPackage {
    public static class LoadPackageParam extends XCallback.Param {
        public String packageName;
        public String processName;
        public ClassLoader classLoader;
        public android.content.pm.ApplicationInfo appInfo;
        public boolean isFirstApplication;
    }
}
