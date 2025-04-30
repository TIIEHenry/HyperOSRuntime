package tiiehenry.xp.updatehooker;

import android.annotation.SuppressLint;

import java.lang.reflect.Field;

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
    public void proxyClassLoader(XC_LoadPackage.LoadPackageParam lpparam) {

        ClassLoader hostClassLoader = lpparam.classLoader;

// 创建一个新的ClassLoader链
        ClassLoader myClassLoader = getClass().getClassLoader();


// 使用反射修改宿主ClassLoader的parent
        try {
            @SuppressLint("DiscouragedPrivateApi") Field parentField = ClassLoader.class.getDeclaredField("parent");
            parentField.setAccessible(true);
            ClassLoader originParentClassLoader = (ClassLoader) parentField.get(hostClassLoader);

            ClassLoader newParent = new ClassLoader(originParentClassLoader) {
                //            ClassLoader newParent = new ClassLoader(MIUIFrameworkHook.createClassLoader(lpparam, (ClassLoader) originParentClassLoader)) {
                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
//                    if (name.startsWith("org.slf4j")) {
//                    Log.e("AAA", "findClass " + name);
//                    }
                    try {
                        return super.findClass(name);
                    } catch (ClassNotFoundException e) {
//                        Log.e("AAA", "failed");
//                        throw e;
                    }
                    return myClassLoader.loadClass(name);
                }
            };
            parentField.set(hostClassLoader, newParent);
        } catch (Exception e) {
//            e.printStackTrace();
            XposedBridge.log(e);
        }
    }


}
