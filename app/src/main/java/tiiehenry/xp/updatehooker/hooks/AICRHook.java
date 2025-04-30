package tiiehenry.xp.updatehooker.hooks;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import tiiehenry.xp.updatehooker.BaseMethodHook;

public class AICRHook extends BaseMethodHook {

    public AICRHook() {
        super("com.xiaomi.aicr", "AICR");
    }

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        super.hook(lpparam);
        hookCTA(lpparam);
        proxyClassLoader(lpparam);
    }

    private void hookCTA(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("com.xiaomi.aicr.common.permission.CommonPermissionUtils", lpparam.classLoader, "checkCTASwitch", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(true);
            }
        });
        XposedHelpers.findAndHookMethod("com.xiaomi.aicr.common.permission.CommonPermissionUtils", lpparam.classLoader, "checkCTAUpdate", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(0);
            }
        });
    }


}
