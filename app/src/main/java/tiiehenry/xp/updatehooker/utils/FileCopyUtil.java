package tiiehenry.xp.updatehooker.utils;


import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileCopyUtil {
    public static final String TAG = "FileCopyUtil ";

    /**
     * 使用 Java I/O 流复制文件。
     * 推荐使用带缓冲区的读写方式，效率更高。
     *
     * @param sourceFile 源文件
     * @param destFile   目标文件
     * @return true if copy successful, false otherwise
     */
    public static boolean copyFile(File sourceFile, File destFile) {
        if (sourceFile == null || !sourceFile.exists()) {
            Log.e(TAG,"Source file does not exist or is null.");
            return false;
        }
        if (destFile == null) {
            Log.e(TAG,"Destination file is null.");
            return false;
        }

        // 确保目标文件的父目录存在
        File parentDir = destFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                Log.e(TAG,"Failed to create destination directory: " + parentDir.getAbsolutePath());
                return false;
            }
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(sourceFile);
            fos = new FileOutputStream(destFile);

            byte[] buffer = new byte[4096]; // 4KB 缓冲区
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.flush(); // 确保所有数据写入磁盘
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"Error copying file: " + e.getMessage());
            return false;
        } finally {
            // 关闭流，确保资源释放
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 另一种更高效的文件复制方式：使用 FileChannel (NIO)。
     * 对于大文件，通常比传统流更快。
     *
     * @param sourceFile 源文件
     * @param destFile   目标文件
     * @return true if copy successful, false otherwise
     */
    public static boolean copyFileNio(File sourceFile, File destFile) {
        if (sourceFile == null || !sourceFile.exists()) {
            Log.e(TAG,"Source file does not exist or is null.");
            return false;
        }
        if (destFile == null) {
            Log.e(TAG,"Destination file is null.");
            return false;
        }

        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            // 确保目标文件的父目录存在
            File parentDir = destFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    Log.e(TAG,"Failed to create destination directory: " + parentDir.getAbsolutePath());
                    return false;
                }
            }

            sourceChannel = new FileInputStream(sourceFile).getChannel();
            destChannel = new FileOutputStream(destFile).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"Error copying file using NIO: " + e.getMessage());
            return false;
        } finally {
            try {
                if (sourceChannel != null) {
                    sourceChannel.close();
                }
                if (destChannel != null) {
                    destChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 示例用法
    public static void main(String[] args) {
        // 创建一个虚拟的源文件用于测试
        File source = new File("test_source.txt");
        try (FileOutputStream fos = new FileOutputStream(source)) {
            fos.write("Hello, this is a test file.".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        File destination = new File("test_destination.txt");

        // 使用传统IO流复制
        if (copyFile(source, destination)) {
            Log.i(TAG,"File copied successfully (traditional IO): " + destination.getAbsolutePath());
        } else {
            Log.i(TAG,"File copy failed (traditional IO).");
        }

        // 使用NIO复制
        File destinationNio = new File("test_destination_nio.txt");
        if (copyFileNio(source, destinationNio)) {
            Log.i(TAG,"File copied successfully (NIO): " + destinationNio.getAbsolutePath());
        } else {
            Log.i(TAG,"File copy failed (NIO).");
        }

        // 清理测试文件
        source.delete();
        destination.delete();
        destinationNio.delete();
    }
}