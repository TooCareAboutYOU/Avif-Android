package cn.android.avif;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import androidx.core.content.FileProvider;

/**
 * @author zhangshuai@attrsense.com
 * @date 2022/11/18 15:27
 * @description
 */
public class FileUtils {

    //打开类型的文件

    public static Intent openFile(Context context, String filePath) {

        if (filePath == null) {
            return null;
        }

        File file = new File(filePath);

        if (!file.exists()) {
            return null;
        }

        /* 取得扩展名 */

        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase();

        end = end.trim().toLowerCase();

        // System.out.println(end);

        /* 依扩展名的类型决定MimeType */

        if (end.equals("apk")) {

            return getApkFileIntent(context, filePath);

        } else if (end.equals("ppt")) {

            return getPptFileIntent(context, filePath);

        } else if (end.equals("xls")) {

            return getExcelFileIntent(context, filePath);

        } else if (end.equals("doc")) {

            return getWordFileIntent(context, filePath);

        } else if (end.equals("pdf")) {

            return getPdfFileIntent(context, filePath);

        } else if (end.equals("txt")) {

            return getTextFileIntent(context, filePath, false);

        } else {

            return getAllIntent(context, filePath);

        }

    }

    // Android获取一个用于打开APK文件的intent

    public static Intent getApkFileIntent(Context context, String param) {

        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Uri contentUri = getUri(context, param);

            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");

        } else {

            intent.setDataAndType(Uri.fromFile(new File(param)), "application/vnd.android.package-archive");

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        }

        return intent;

    }

    // Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(Context context, String param) {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Uri uri = Uri.fromFile(new File(param));

        Uri uri = getUri(context, param);

        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");

        return intent;

    }

    // Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(Context context, String param) {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Uri uri = Uri.fromFile(new File(param));

        Uri uri = getUri(context, param);

        intent.setDataAndType(uri, "application/vnd.ms-excel");

        return intent;

    }

    // Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(Context context, String param) {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Uri uri = Uri.fromFile(new File(param));

        Uri uri = getUri(context, param);

        intent.setDataAndType(uri, "application/msword");

        return intent;

    }

    // Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(Context context, String param) {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Uri uri = Uri.fromFile(new File(param));

        Uri uri = getUri(context, param);

        intent.setDataAndType(uri, "application/pdf");

        return intent;

    }

    // Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(Context context, String param, boolean paramBoolean) {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (paramBoolean) {

            Uri uri1 = Uri.parse(param);

            intent.setDataAndType(uri1, "text/plain");

        } else {

            // Uri uri2 = Uri.fromFile(new File(param));

            Uri uri2 = getUri(context, param);

            intent.setDataAndType(uri2, "text/plain");

        }

        return intent;

    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent(Context context, String param) {

        Intent intent = new Intent();

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setAction(android.content.Intent.ACTION_VIEW);

        // Uri uri = Uri.fromFile(new File(param));

        Uri uri = getUri(context, param);

        intent.setDataAndType(uri, "/");

        intent.setAction(android.content.Intent.ACTION_VIEW);

        // Uri uri = Uri.fromFile(new File(param));

        Uri uri1 = getUri(context, param);

        intent.setDataAndType(uri1, "/");
        return intent;
    }

    private static Uri getUri(Context context, String param) {

        return FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", new File(param));
    }

    /**
     * 获取文件
     *
     * @return
     */

    public static String getFileContent(String filePath) {
        ArrayList<Integer> list = new ArrayList<>();
        StringBuilder stringBuilder=new StringBuilder();
        File file = new File(filePath);
        try {
            InputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader reader = new InputStreamReader(inputStream, "GB2312");
                BufferedReader bufferedReader = new BufferedReader(reader);

                String line;
                while ((line = bufferedReader.readLine()) != null) {
//                    content += line + "\n";
                    list.add(Integer.parseInt(line));
                }
                inputStream.close();

                float average = getAverage(list);
                int max = getMax(list);
                int min = getMin(list);
                Log.i("print_logs", "平均值：" + average +"首张延迟："+ list.get(0) + "，最大值：" + max + ",最小值：" + min);
                stringBuilder.append("平均值：").append(average).append("\t\t");
                stringBuilder.append("最大值：").append(max).append("\t\t");
                stringBuilder.append("最小值：").append(min);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    //获取集合的平均值
    public static float getAverage(ArrayList<Integer> list) {
        int sum = 0;
        if (list == null || list.isEmpty()) {
            return 0;
        }
        for (Integer integer : list) {
            sum += integer;
        }
        return (float) sum / 100;
    }

    //获取集合中的最大值
    public static int getMax(ArrayList<Integer> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int max = list.get(0);
        for (Integer integer : list) {
            max = max > integer ? max : integer;
        }
        return max;
    }

    //获取集合中的最小值
    public static int getMin(ArrayList<Integer> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int min = list.get(0);
        for (Integer integer : list) {
            min = min < integer ? min : integer;
        }
        return min;
    }


}