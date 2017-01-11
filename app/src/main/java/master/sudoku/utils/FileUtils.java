package master.sudoku.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import master.sudoku.application.PuzzleMasterApp;

public class FileUtils {

    private static final String APP_DIR = "PuzzleMaster";

    private static final String CACHE_DIR = "Cache";

    private static final String CRASH_DIR = "crash";

    public static File getAppDir() {
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = new File(Environment.getExternalStorageDirectory(), APP_DIR);
        } else {
            file = new File(PuzzleMasterApp.getInstance().getCacheDir(), APP_DIR);
        }
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getCacheDir() {
        File file = new File(getAppDir(), CACHE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    @SuppressLint("NewApi")
    public static String getFilePathFromUri(Uri uri, Context ctx) {
        String result = uri.toString();
        if (uri.getScheme().compareTo("file") == 0) { // file:///开头的uri
            result = result.replace("file://", ""); // 替换file://
        } else {
            Cursor cursor = null;
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(ctx, uri)) {
                    String wholeID = DocumentsContract.getDocumentId(uri);
                    String id = wholeID.split(":")[1];
                    String[] column = {MediaStore.Images.Media.DATA};
                    String sel = MediaStore.Images.Media._ID + "=?";
                    cursor = ctx.getContentResolver().query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);
                    int columnIndex = cursor.getColumnIndex(column[0]);
                    if (cursor.moveToFirst()) {
                        result = cursor.getString(columnIndex);
                    }
                } else {
                    String[] proj = {MediaStore.Images.Media.DATA};
                    cursor = ctx.getContentResolver().query(uri, proj, null,
                            null, null);
                    int column_index = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    result = cursor.getString(column_index);
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return result;
    }

    public static String getCachePath() {
        return getCacheDir().getAbsolutePath();
    }

    public static File getCrashDir() {
        File file = new File(getAppDir(), CRASH_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static void clearDir(File dir) {
        if (dir == null || !dir.exists()) {
            return;
        }
        try {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        } catch (Exception ignore) {
        }
    }

    public static void clearImgcache() {
        File dir = getCacheDir();
        if (dir == null || !dir.exists()) {
            return;
        }
        try {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        } catch (Exception ignore) {
        }
    }

    public static String getFileTextData(File file) {
        if (file != null && file.exists()) {
            InputStreamReader isReader = null;
            BufferedReader reader = null;
            try {
                isReader = new InputStreamReader(new FileInputStream(file),
                        "utf-8");
                reader = new BufferedReader(isReader);
                StringBuffer sb = new StringBuffer("");
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                String result = sb.toString();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }
                }
                if (isReader != null) {
                    try {
                        isReader.close();
                    } catch (IOException e) {
                    }
                }
            }

        }
        return null;
    }

    public static void saveFileTextData(File file, String data) {
        if (file != null && data != null) {
            if (file.exists()) {
                file.delete();
            }
            OutputStream out = null;
            try {
                out = new FileOutputStream(file);
                out.write(data.getBytes("UTF-8"));
                out.flush();
            } catch (Exception e) {
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    /**
     * Get the directory size except the crop images
     */
    public static long getUnCropImgSize(File directory) {
        long size = 0;
        File[] fileList = directory.listFiles();
        for (File file : fileList) {
            if (file.isDirectory()) {
                size += getUnCropImgSize(file);
            } else {
                if (!file.getName().endsWith("crop.jpg")) {
                    size += file.length();
                }
            }
        }
        return size;
    }

    public static String Str2MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
        }
        return result;
    }
}
