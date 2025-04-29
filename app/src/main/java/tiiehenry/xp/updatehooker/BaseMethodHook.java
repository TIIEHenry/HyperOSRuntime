package tiiehenry.xp.updatehooker;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class BaseMethodHook {
    public static final String TAG = "[AUH] ";
    private static XSharedPreferences sPrefs;
    public final String packageName;
    public final String hookName;

    public BaseMethodHook(String packageName, String hookName) {
        this.packageName = packageName;
        this.hookName = hookName;
    }


    public static void hookApplicationCreate(XC_LoadPackage.LoadPackageParam lpparam, XSharedPreferences pref) {
        sPrefs = pref;
    }

    public static void log(String msg) {
        XposedBridge.log(TAG + msg);
    }

    /**
     * 实现各自的 hook 逻辑
     *
     * @param lpparam
     * @throws ClassNotFoundException
     */
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {

    }

    public boolean hookOrigin(ClassLoader classLoader) {
        return true;
    }

    public void logd(String msg) {
        if (true) {
            XposedBridge.log("[AUH:" + hookName + "] " + msg);
        }
    }


}
