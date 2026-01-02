package de.robv.android.xposed;

/**
 * Stub class for Xposed callbacks
 * This is a compile-only stub - the real implementation comes from LSPosed at runtime
 */
public abstract class XCallback implements Comparable<XCallback> {
    public final int priority;

    public XCallback() {
        this.priority = PRIORITY_DEFAULT;
    }

    public XCallback(int priority) {
        this.priority = priority;
    }

    public static abstract class Param {
        public Object callbacks;
    }

    @Override
    public int compareTo(XCallback other) {
        if (this == other) {
            return 0;
        }
        if (other.priority != this.priority) {
            return other.priority - this.priority;
        }
        return System.identityHashCode(this) < System.identityHashCode(other) ? -1 : 1;
    }

    public static final int PRIORITY_DEFAULT = 50;
    public static final int PRIORITY_HIGHEST = 10000;
    public static final int PRIORITY_LOWEST = -10000;
}
