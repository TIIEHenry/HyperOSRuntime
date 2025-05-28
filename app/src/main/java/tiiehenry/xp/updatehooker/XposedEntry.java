package tiiehenry.xp.updatehooker;

import java.util.HashMap;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import tiiehenry.xp.updatehooker.hooks.AICRHook;
import tiiehenry.xp.updatehooker.hooks.AccountHook;
import tiiehenry.xp.updatehooker.hooks.HyperOSHook;
import tiiehenry.xp.updatehooker.hooks.StoreHook;

public class XposedEntry extends XC_MethodHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    public static final String MODULE_PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    public static final String TAG = "[XposedEntry] ";
    private static final HashMap<String, BaseMethodHook> hookers = new HashMap<>();
    public static XSharedPreferences sPrefs;

    static {
        addHooker(new HyperOSHook());
        addHooker(new AICRHook());
        addHooker(new StoreHook());
//        addHooker(new AccountHook());
    }

    public static void addHooker(BaseMethodHook hooker) {
        hookers.put(hooker.packageName, hooker);
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
//        sPrefs = new XSharedPreferences(MY_PACKAGE_NAME, PREFS);
//        sPrefs.makeWorldReadable();
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//        sPrefs.reload();
        String packageName = lpparam.packageName;
        if (packageName.equals(MODULE_PACKAGE_NAME)) {
            return;
        }

        //开屏广告
//        XposedHelpers.findAndHookMethod("com.stub.StubApp", lpparam.classLoader, "a", Context.class,
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        Log.e("AAA","args " + param.args[0]);
//                        Log.e("AAA","result " + param.getResult());
//                    }
//                });
        BaseMethodHook hooker = hookers.get(packageName);
        if (hooker == null) {
            hooker = hookers.get("*");
        }
        if (hooker != null) {
            hooker.hook(lpparam);
        }
//        BaseMethodHook.hookApplicationCreate(lpparam, sPrefs);
    }

    private void log(String msg) {
        XposedBridge.log(TAG + msg);
    }
}

