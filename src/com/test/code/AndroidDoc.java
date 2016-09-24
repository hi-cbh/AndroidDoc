package com.test.code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
 
public class AndroidDoc {
 
    private static final String ANDROID_SDK_HOME = "ANDROID_HOME";
    private static final String TAG = "<script src=\"http://www.google.com/jsapi\" type=\"text/javascript\"></script>";
    private static String androidSDKHome;
 
    public static void main(String[] args) {
        Map<String, String> map = System.getenv();
        androidSDKHome = map.get(ANDROID_SDK_HOME);
        if (androidSDKHome != null) {
            System.out.println("Detected the Android SDK Installation path:"
                    + androidSDKHome);
            File docs = new File(androidSDKHome + "\\docs");
            System.out.println("It will take several minutes, please wait...");
            traverse(docs);
            System.out
                    .println("Operation Down!!! Now you can visit Android Doc without waiting...");
        } else {
            System.out
                    .println("Please configure the Android SDK Environment variable (ANDROID_SDK_HOME)!");
        }
    }
 
    /**
     * 遍历Android SDK安装目录下的docs目录
     * 
     * @param dir
     */
    private static void traverse(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles(new FileFilter() {
 
                @Override
                public boolean accept(File file) {
                    if (file.isFile() && file.getName().endsWith(".html")) {
                        BufferedReader reader = null;
                        try {
                            reader = new BufferedReader(new FileReader(file));
                            String buffer;
                            while ((buffer = reader.readLine()) != null) {
                                if (buffer.trim().equalsIgnoreCase(TAG)) {
                                    return true;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (file.isDirectory()) {
                        return true;
                    }
                    return false;
                }
            });
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.println("Scanning Folder:"
                            + file.getAbsolutePath());
                    traverse(file);
                } else {
                    doChange(file);
                }
            }
        }
    }
 
    /**
     * 注释html中的某行Javascript代码
     * 
     * @param file
     * @return
     */
    private static boolean doChange(File file) {
        System.out.println("Extracting File:" + file.getAbsolutePath() + "...");
        String desPath = file.getParent() + "\\" + UUID.randomUUID().toString() + ".html";
        File des = new File(desPath);
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            des.createNewFile();
            reader = new BufferedReader(new FileReader(file));
            writer = new BufferedWriter(new FileWriter(des));
            String buffer;
            while ((buffer = reader.readLine()) != null) {
                if (buffer.trim().equalsIgnoreCase(TAG)) {
                    buffer = "<!--" + TAG + "-->";
                }
                writer.write(buffer + "\n");
                writer.flush();
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                writer.close();
                file.delete();
                des.renameTo(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
 
}
