package com.wutian3.test;

import com.Constants;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JsAssertHelper {


    private static Map<String, String> getJsUrls() {
        Map<String, String> map = new HashMap<>();
        String facebook = "http://dl.files.wshareit.com/download/js/facebook.js";
        map.put("facebook", facebook);
        String tiktok = "http://dl.files.wshareit.com/download/js/tiktok.js";
        map.put("tiktok", tiktok);
        String instagram = "http://dl.files.wshareit.com/download/js/instagram.js";
        map.put("instagram", instagram);
        String twitter = "http://dl.files.wshareit.com/download/js/twitter.js";
        map.put("twitter", twitter);
        String ted = "http://dl.files.wshareit.com/download/js/ted.js";
        map.put("ted", ted);
        String dailymotion = "http://dl.files.wshareit.com/download/js/dailymotion.js";
        map.put("dailymotion", dailymotion);
        String tumblr = "http://dl.files.wshareit.com/download/js/tumblr.js";
        map.put("tumblr", tumblr);
        return map;
    }


    public static void main(String[] args) {
        writeJsContent();
    }

    private static String PATH = "/Users/maxy/Android/workspace/XmlUtilsAs/app/src/main/assets/JS";
    public static void writeJsContent() {
        File jsDir = new File(PATH);
        if (!jsDir.exists())
            throw new RuntimeException(jsDir.getAbsolutePath() + "  not exit");

        Map<String, String> jsUrls = getJsUrls();
        Set<String> keys = jsUrls.keySet();
        CountDownLatch countDownLatch = new CountDownLatch(keys.size());
        for (String key : keys) {
            String jsUrl = jsUrls.get(key);
            String md5 = md5(jsUrl);
            System.out.println(key + "     " + md5);
            writeFile(jsDir, md5, jsUrl, countDownLatch);
        }

        compressFile(jsDir, countDownLatch);
    }

    private static void writeFile(final File jsDir, final String md5, final String jsUrl, final CountDownLatch countDownLatch) {
        final File jsFile = new File(jsDir, md5);
        new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(jsUrl);
                    InputStream in = url.openStream();
                    byte buff[] = new byte[1024];
                    try (OutputStream outputStream = new FileOutputStream(jsFile)) {
                        do {
                            int read = in.read(buff);
                            if (read <= 0)
                                break;
                            outputStream.write(buff, 0, read);
                        } while (true);
                    } catch (Exception e) {
                    } finally {
                    }
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            }
        }.run();
    }

    public static String md5(String input) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(input.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static void compressFile(File jsDir, CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("start compressFile");
//
        File zipFile = new File(jsDir, "assert_js_resource.zip");
        ZipOutputStream zipos = null;
        DataOutputStream os = null;
        try {
            zipos = new ZipOutputStream(new FileOutputStream(zipFile));
            zipos.setMethod(ZipOutputStream.DEFLATED);// 设置压缩方法DEFLATED
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 循环将文件写入压缩流

        for (File file : jsDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isHidden())
                return false;
                if (file.getName().endsWith(".zip"))
                    return false;
                return true;
            }
        })) {
            try {
                // 添加ZipEntry，并ZipEntry中写入文件流
                zipos.putNextEntry(new ZipEntry(file.getName()));
                os = new DataOutputStream(zipos);
                InputStream is = new FileInputStream(file);
                byte[] b = new byte[100];
                int length = 0;
                while ((length = is.read(b)) != -1) {
                    os.write(b, 0, length);
                }
                is.close();
                zipos.closeEntry();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        // 关闭流
        try {
            os.flush();
            os.close();
            zipos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}