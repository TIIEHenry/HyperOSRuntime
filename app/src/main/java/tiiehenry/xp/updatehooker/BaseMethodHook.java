package tiiehenry.xp.updatehooker;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
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

    public static Application getCurrentApplication() {
        try {
            // 获取 ActivityThread 实例
            Object activityThread = XposedHelpers.callStaticMethod(
                    XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread");

            // 从 ActivityThread 获取 Application 实例
            return (Application) XposedHelpers.callMethod(activityThread, "getApplication");
        } catch (Throwable e) {
            XposedBridge.log("Failed to get current application: " + e.getMessage());
            return null;
        }
    }

    public static Context getApplicationContext() {
        Application app = getCurrentApplication();
        return app != null ? app.getApplicationContext() : null;
    }

    /**
     * 使用反射打印一个类实例的所有成员变量（字段）。
     *
     * @param object 待打印其成员变量的实例
     */
    public void printAllFields(Object object) {
        if (object == null) {
            Log.e(TAG, "Object is null, cannot print fields.");
            return;
        }

        Class<?> clazz = object.getClass();
        Log.d(TAG, "--- Printing fields for class: " + clazz.getName() + " ---");

        // getDeclaredFields() 返回Class对象所表示的类或接口所声明的所有字段，
        // 包括公共、保护、默认（包）访问和私有字段，但不包括继承的字段。
        Field[] fields = clazz.getFields();

        if (fields.length == 0) {
            Log.d(TAG, "No fields found in " + clazz.getName());
            return;
        }

        for (Field field : fields) {
            // 检查并设置私有字段的可访问性
            // 如果不设置，尝试访问私有字段会抛出 IllegalAccessException
            if (!field.isAccessible()) {
                field.setAccessible(true); // 允许访问私有字段
            }

            try {
                String fieldName = field.getName();
                Object fieldValue = field.get(object); // 获取字段在给定对象上的值
                String fieldType = field.getType().getName(); // 获取字段的类型

                Log.d(TAG, String.format("  Field: %s (Type: %s) = %s",
                        fieldName, fieldType, fieldValue));

            } catch (IllegalAccessException e) {
                Log.e(TAG, "Error accessing field " + field.getName() + ": " + e.getMessage());
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Error getting value for field " + field.getName() + ": " + e.getMessage());
            }
        }
        Log.d(TAG, "--- End of fields for class: " + clazz.getName() + " ---");
    }

}
