package tiiehenry.xp.updatehooker.hooks;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.xiaomi.security.devicecredential.ISecurityDeviceCredentialManager;

import java.lang.reflect.Array;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import tiiehenry.xp.updatehooker.BaseMethodHook;

public class HyperOSHook extends BaseMethodHook {

    public HyperOSHook() {
        super("*", "HyperOSRuntime");
    }

    public HyperOSHook(String packageName, String hookName) {
        super(packageName, hookName);
    }

    private static void injectClassLoader(Object hostDexPathList, ClassLoader injectClassLoader) {
        Object pluginDexPathList = XposedHelpers.getObjectField(injectClassLoader, "pathList");

// 合并 dexElements
        Object hostDexElements = XposedHelpers.getObjectField(hostDexPathList, "dexElements");
        Object pluginDexElements = XposedHelpers.getObjectField(pluginDexPathList, "dexElements");
//        Log.e("AAA","pluginDexElements "+pluginDexElements);
        Object combinedDexElements = Array.newInstance(
                hostDexElements.getClass().getComponentType(),
                Array.getLength(hostDexElements) + Array.getLength(pluginDexElements)
        );

        System.arraycopy(hostDexElements, 0, combinedDexElements, 0, Array.getLength(hostDexElements));
        System.arraycopy(pluginDexElements, 0, combinedDexElements,
                Array.getLength(hostDexElements), Array.getLength(pluginDexElements));

// 替换宿主的 dexElements
        XposedHelpers.setObjectField(hostDexPathList, "dexElements", combinedDexElements);
    }


    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        super.hook(lpparam);// 获取宿主的 PathClassLoader
        ClassLoader hostClassLoader = lpparam.classLoader;
// 获取插件的 dexPathList 对象
        ClassLoader injectClassLoader = getClass().getClassLoader();

        // 获取宿主的 dexPathList 对象
        Object hostDexPathList = XposedHelpers.getObjectField(hostClassLoader, "pathList");

        injectClassLoader(hostDexPathList, injectClassLoader);

//        PathClassLoader pathClassLoader = new PathClassLoader("/system_ext/priv-app/RtMiCloudSDK/", injectClassLoader);
//        try {
//            pathClassLoader.loadClass("miui.cloud.os.SystemProperties");
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        injectClassLoader(hostDexPathList, pathClassLoader);

//        XposedHelpers.findAndHookMethod(
//                ClassLoader.class,
//                "findClass",
//                String.class,
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        Throwable throwable = param.getThrowable();
//                        if (throwable == null) {
//                            return;
//                        }
//                        String className = (String) param.args[0];
//                        Class<?> classInHooker = getClass().getClassLoader().loadClass(className);
//                        if (classInHooker != null) {
//                            param.setResult(classInHooker);
//                            param.setThrowable(null);
//                            return;
//                        }
//                    }
//                }
//        );
//        MIUIFrameworkHook.hookClassLoader(lpparam,lpparam.classLoader);


    }

}
