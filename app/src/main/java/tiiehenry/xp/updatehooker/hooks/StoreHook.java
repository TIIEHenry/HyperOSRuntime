package tiiehenry.xp.updatehooker.hooks;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import tiiehenry.xp.updatehooker.BaseMethodHook;
import tiiehenry.xp.updatehooker.utils.FileCopyUtil;

public class StoreHook extends BaseMethodHook {

    public StoreHook() {
        super("com.xiaomi.market", "Market");
    }

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        super.hook(lpparam);
        Class<?> aClass;
        try {
            aClass = lpparam.classLoader.loadClass("com.xiaomi.market.business_core.downloadinstall.data.DownloadInstallInfo");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        XposedBridge.hookAllMethods(XposedHelpers.findClass("com.xiaomi.market.common.compat.PackageManagerCompat", lpparam.classLoader), "deletePackage", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(null);
            }
        });
        XposedHelpers.findAndHookMethod("com.xiaomi.market.business_core.downloadinstall.TaskManager", lpparam.classLoader, "onDownloadFailed", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Context context = getApplicationContext();
                Object arg = param.args[0];
                Map<String, Object> mTasks = (Map<String, Object>) XposedHelpers.getObjectField(param.thisObject, "mTasks");
                Object task = mTasks.get(arg);
                Log.e(TAG, "task " + task);
                if (task == null) {
                    return;
                }
                Object info = XposedHelpers.getObjectField(task, "info");
                Log.e(TAG, "info " + info);
                List splitInfos = (List) XposedHelpers.getObjectField(info, "splitInfos");
                Log.e(TAG, "splitInfos " + splitInfos);
                if (splitInfos != null && splitInfos.size() > 0) {
                    Log.e(TAG, "splitInfos " + splitInfos.size());
                    for (Object splitInfo : splitInfos) {
                        Log.e(TAG, "splitInfo " + splitInfo);
                        printAllFields(splitInfo);

                        Object packageName = XposedHelpers.getObjectField(splitInfo, "packageName");
                        Object downloadUrl = XposedHelpers.getObjectField(splitInfo, "downloadUrl");
                        Object downloadPath = XposedHelpers.getObjectField(splitInfo, "downloadPath");
                        File downloadFile = new File(downloadPath.toString());
                        if (downloadFile.exists()) {
                            continue;
                        }
                        long id = startDownload(context, downloadUrl.toString(), downloadFile.getName(), packageName.toString());
                        final boolean[] downloaded = {false};
                        if (id == 0L) {
                            onDownloaded(context, splitInfo, packageName);
                            downloaded[0] = true;
                            continue;
                        }
                        BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                String action = intent.getAction();
                                if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                                    return;
                                }
                                // 获取下载任务的ID
                                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                                if (downloadId != id) {
                                    return;
                                }
                                context.unregisterReceiver(this);
                                onDownloaded(context, splitInfo, packageName);
                                downloaded[0] = true;
                            }
                        };
                        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                        context.registerReceiver(downloadCompleteReceiver, filter, Context.RECEIVER_EXPORTED);
                        while (!downloaded[0]) {
                            Thread.sleep(100);
                        }
                    }
                }

            }

            private boolean onDownloaded(Context context, Object splitInfo, Object packageName) {
                try {
                    XposedHelpers.callMethod(splitInfo, "setDownloadEndTime", System.currentTimeMillis());
                    XposedHelpers.callMethod(splitInfo, "updateDownloadDuration");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Lcom/xiaomi/market/service/DownloadCompleteService;->onDownloadComplete(Lcom/xiaomi/market/business_core/downloadinstall/data/DownloadSplitInfo;)V
                int verify = (int) XposedHelpers.callMethod(splitInfo, "verify");
                Log.e(TAG, "verify verified " + verify);
                if (verify == -1 || verify == -2) {
                    Object downloadPath2 = XposedHelpers.getObjectField(splitInfo, "downloadPath");
                    Log.e(TAG, "downloadPath2 verified " + downloadPath2);
                    File cacheApk = new File(downloadPath2.toString());
                    int indexOf = cacheApk.getName().indexOf(packageName.toString());
                    String substring = cacheApk.getName().substring(0, indexOf - 1);
                    if (!cacheApk.exists()) {
                        Toast.makeText(context, "apk不存在", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    File storeApk = new File(cacheApk.getParentFile(), substring + ".apk");
                    storeApk.getParentFile().mkdirs();
                    cacheApk.renameTo(storeApk);
                    File externalCacheDir = context.getExternalFilesDir("apk");
                    File apkInFiles = new File(externalCacheDir, cacheApk.getName());
                    FileCopyUtil.copyFile(cacheApk, apkInFiles);
                    assert apkInFiles.isFile();
                    XposedHelpers.setObjectField(splitInfo, "downloadPath", apkInFiles.getAbsolutePath());

                    Log.i(TAG, "storeApk " + storeApk);
                    if (!storeApk.isFile()) {
                        Toast.makeText(context, "apk复制失败" + storeApk, Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(context, "apk保存在：" + storeApk, Toast.LENGTH_LONG).show();

                    installApk(context, apkInFiles, lpparam);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
//                                                XposedHelpers.callMethod(proxy, "downloadSuccess");
                }
                return false;
            }
        });
        Class<?> DownloadInstallManager = XposedHelpers.findClass("com.xiaomi.market.data.DownloadInstallManager", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(DownloadInstallManager, "onDownloadInstallStart", aClass, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Object arg = param.args[0];
                Object downloadDirPath = XposedHelpers.getObjectField(arg, "downloadDirPath");
//                printAllFields(arg);
//                Log.e("AAA", "downloadDirPath " + downloadDirPath);
                XposedHelpers.setObjectField(arg, "downloadDirPath", getDownloadPath().getAbsolutePath());
            }
        });
        if (lpparam.packageName.equals("com.xiaomi.market")) {
            try {
                hookMarket(lpparam.classLoader);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        proxyClassLoader(lpparam);
    }

    private static void installApk(Context context, File apkInFiles, XC_LoadPackage.LoadPackageParam lpparam) {
        //                                        if (!context.getPackageManager().canRequestPackageInstalls()) {
//                                            // 如果没有此权限，引导用户去设置页面开启
//                                            Intent intent2 = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
//                                            intent2.setData(Uri.parse("package:" + context.getPackageName()));
//                                            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                            try {
//                                                context.startActivity(intent2);
//                                            } catch (Exception e) {
//                                                Log.e("AAA", "failed to handle apk", e);
//                                                Toast.makeText(context, "无法打开APK文件，请检查是否有合适的应用来处理。", Toast.LENGTH_LONG).show();
//                                            }
//                                            context.startActivity(intent2);
//                                            return;
//                                        }
        Intent intent2 = new Intent(Intent.ACTION_VIEW);
        intent2.addCategory(Intent.CATEGORY_DEFAULT);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Class fileProvider = XposedHelpers.findClass("androidx.core.content.FileProvider", lpparam.classLoader);
        intent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // 授予临时读取URI的权限

        // 设置APK文件的MIME类型
        Uri uri = (Uri) XposedHelpers.callStaticMethod(fileProvider, "getUriForFile", context, "com.xiaomi.market.fileprovider", apkInFiles);
        intent2.setDataAndType(uri, "application/vnd.android.package-archive");

        try {
            context.startActivity(intent2);
        } catch (Exception e) {
            Log.e(TAG, "failed to handle apk", e);
            Toast.makeText(context, "无法打开APK文件，请检查是否有合适的应用来处理。", Toast.LENGTH_LONG).show();
        }
    }

    private static File getDownloadPath() {
        File getDownloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return new File(getDownloadPath, "MiMarket");
    }

    private void hookMarket(ClassLoader classLoader) throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod("com.xiaomi.market.business_core.downloadinstall.DesktopProgressManagerMiui", classLoader, "notifyChange", classLoader.loadClass("com.xiaomi.market.model.DesktopProgressAppInfo"), new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(null);
            }
        });
        XposedHelpers.findAndHookMethod("com.xiaomi.market.business_core.downloadinstall.DesktopProgressManagerMiui", classLoader, "remove", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(null);
            }
        });
    }

    public static long startDownload(Context context, String fileUrl, String customFileName, String title) {
        try {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (downloadManager == null) {
                Toast.makeText(context, "DownloadManager服务不可用", Toast.LENGTH_SHORT).show();
                return 0;
            }

            Uri uri = Uri.parse(fileUrl);
            DownloadManager.Request request = new DownloadManager.Request(uri);

            // 1. 指定文件名和下载路径
            // 保存到公共的 Downloads 目录下
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "MiMarket/" + customFileName);

            // 2. 设置通知栏显示信息
            request.setTitle("Mi Market");
            request.setDescription("下载: " + title);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            // 3. 设置网络类型限制
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

            // 4. 允许被媒体扫描器扫描 (对于图片、视频等很重要)
            request.allowScanningByMediaScanner();

            // 5. 将下载请求加入队列
            long downloadId = downloadManager.enqueue(request);
            Toast.makeText(context, "下载已开始: " + customFileName, Toast.LENGTH_SHORT).show();
            return downloadId;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "下载失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return 0;
    }

}
