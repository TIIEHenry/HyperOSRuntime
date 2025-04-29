package tiiehenry.xp.updatehooker;

import java.io.File;

import dalvik.system.DexClassLoader;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MIUIFrameworkHook {

    private static final String TAG = "MIUIFrameworkHook";
    private static final String MIUI_FRAMEWORK_JAR = "/system_ext/framework/miui-framework.jar";

    public static DexClassLoader createClassLoader(XC_LoadPackage.LoadPackageParam lpparam, ClassLoader classLoader) {
        // 1. 创建 DexClassLoader
        File miuiFrameworkJarFile = new File(/*"/data/adb/modules/"+*/MIUI_FRAMEWORK_JAR);
//            if (!miuiFrameworkJarFile.exists()) {
//                XposedBridge.log(MIUI_FRAMEWORK_JAR + " not found.");
//                return;
//            }


        String dexPath = miuiFrameworkJarFile.getAbsolutePath();
        String dexOutputDir = lpparam.appInfo.dataDir + "/code_cache/miui-framework";
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, dexOutputDir, dexOutputDir, classLoader);
        return dexClassLoader;
    }

}
