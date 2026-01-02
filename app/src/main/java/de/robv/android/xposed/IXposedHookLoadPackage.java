package de.robv.android.xposed;

/**
 * Stub interface for Xposed module hooks
 * This is a compile-only stub - the real implementation comes from LSPosed at runtime
 */
public interface IXposedHookLoadPackage extends IXposedMod {
    void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable;
}
