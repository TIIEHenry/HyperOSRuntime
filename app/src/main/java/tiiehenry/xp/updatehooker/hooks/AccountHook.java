package tiiehenry.xp.updatehooker.hooks;

import android.content.Context;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.xiaomi.security.devicecredential.ISecurityDeviceCredentialManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import tiiehenry.xp.updatehooker.BaseMethodHook;

@Deprecated
public class AccountHook extends BaseMethodHook {

    public AccountHook() {
        super("com.xiaomi.account", "AICR");
    }

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("a5.d", lpparam.classLoader, "a", android.content.Context.class, String[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Object[] args = param.args;
                Context context = (Context) args[0];
                File documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                File file = new File(documentsDir, "hyperos-oaid.txt");
                String uuid = readText(file.getAbsolutePath());
                if (uuid == null || uuid.isEmpty()) {
                    uuid = generateRandomString(32);
                    writeText(file.getAbsolutePath(), uuid);
                }
                param.setResult(uuid.trim());
            }
        });
        XposedHelpers.findAndHookMethod("com.xiaomi.security.devicecredential.d", lpparam.classLoader, "b", new XC_MethodHook() {
            private final ISecurityDeviceCredentialManager.Stub sdcm = new ISecurityDeviceCredentialManager.Stub() {

                @Override
                public IBinder asBinder() {
                    return null;
                }

                @Override
                public void forceReload() throws RemoteException {
                    Log.i(hookName, "forceReload");
                }

                @Override
                public String getSecurityDeviceId() throws RemoteException {
                    Log.i(hookName, "getSecurityDeviceId");
                    return "";
                }

                @Override
                public boolean isThisDeviceSupported() throws RemoteException {
                    Log.i(hookName, "isThisDeviceSupported");
                    return false;
                }

                @Override
                public byte[] sign(int i, byte[] bytes, boolean b) throws RemoteException {
                    Log.i(hookName, "sign " + i);
                    return new byte[0];
                }
            };

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(sdcm.asBinder());
            }
        });
//        XposedHelpers.findAndHookMethod("android.os.ServiceManager", lpparam.classLoader, "getService", String[].class, new XC_MethodHook() {
//            private final ISecurityDeviceCredentialManager.Stub sdcm = new ISecurityDeviceCredentialManager.Stub() {
//
//                @Override
//                public IBinder asBinder() {
//                    return null;
//                }
//
//                @Override
//                public void forceReload() throws RemoteException {
//                    Log.i(hookName, "forceReload");
//                }
//
//                @Override
//                public String getSecurityDeviceId() throws RemoteException {
//                    Log.i(hookName, "getSecurityDeviceId");
//                    return "";
//                }
//
//                @Override
//                public boolean isThisDeviceSupported() throws RemoteException {
//                    Log.i(hookName, "isThisDeviceSupported");
//                    return false;
//                }
//
//                @Override
//                public byte[] sign(int i, byte[] bytes, boolean b) throws RemoteException {
//                    Log.i(hookName, "sign " + i);
//                    return new byte[0];
//                }
//            };
//
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                Object[] args = param.args;
//                String serviceName = (String) args[0];
//                if ("miui.sedc".equals(serviceName)) {
//                    param.setResult(sdcm.asBinder());
//                }
//            }
//        });
        proxyClassLoader(lpparam);
        super.hook(lpparam);
    }

    public static String readText(String path) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(path)))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeText(String path, String content) {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(content.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; // 可自定义字符集
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }
}
