package com.kekejl.lib_util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文 件 名：FileToZipUtils
 * 创 建 人：Karen
 * 创建时间：2017/8/18
 */

public class FileToZipUtils {

    public static final String TAG = "ReportUtil";

    private FileToZipUtils() {
    }

    /**
     * 将存放在sourceFilePath目录下(不包含子目录)的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下
     *
     * @param sourceFilePath :待压缩的文件路径  "D:\\TestFile"
     * @param zipFilePath    :压缩后存放路径    "D:\\tmp"
     * @param fileName       :压缩后文件的名称  "12700153file"
     * @return
     */
    public static boolean fileToZip(String sourceFilePath, String zipFilePath, String fileName) {
        boolean flag = false;
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        if (sourceFile.exists() == false) {
            LogUtil.e(TAG, "待压缩的文件目录：" + sourceFilePath + "不存在.");
        } else {
            try {
                File zipFile = new File(zipFilePath + "/" + fileName);
                if (zipFile.exists()) {
                    LogUtil.e(TAG, zipFilePath + "目录下存在名字为:" + fileName + "打包文件.");
                } else {
                    File[] sourceFiles = sourceFile.listFiles();
                    if (null == sourceFiles || sourceFiles.length < 1) {
                        LogUtil.e(TAG, "待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩.");
                    } else {
                        fos = new FileOutputStream(zipFile);
                        zos = new ZipOutputStream(new BufferedOutputStream(fos));
                        byte[] bufs = new byte[1024 * 10];
                        if (sourceFiles.length > 0) {
                            for (int i = 0; i < sourceFiles.length; i++) {
                                //创建ZIP实体，并添加进压缩包
                                ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
                                zos.putNextEntry(zipEntry);
                                //读取待压缩的文件并写进压缩包里
                                fis = new FileInputStream(sourceFiles[i]);
                                bis = new BufferedInputStream(fis, 1024 * 10);
                                int read = 0;
                                while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                                    zos.write(bufs, 0, read);
                                }

                                if (null != bis) {
                                    bis.close();
                                    bis = null;
                                }
                                if (null != fis) {
                                    fis.close();
                                    fis = null;
                                }
                                zos.closeEntry();
                            }
                        }
                        flag = true;

                        if (null != zos) {
                            zos.close();
                            zos = null;
                        }
                        if (null != fos) {
                            fos.close();
                            fos = null;
                        }
                    }
                }
                zipFile = null;
            } catch (FileNotFoundException e) {
                LogUtil.e(TAG, "===FileNotFoundException===" + e.getMessage());
                throw new RuntimeException(e);
            } catch (IOException e) {
                LogUtil.e(TAG, "===IOException===" + e.getMessage());
                throw new RuntimeException(e);
            } finally {
                //关闭流
                try {
                    if (null != bis) {
                        bis.close();
                        bis = null;
                    }
                    if (null != zos) {
                        zos.close();
                        zos = null;
                    }
                    if (null != fis) {
                        fis.close();
                        fis = null;
                    }
                    if (null != fos) {
                        fos.close();
                        fos = null;
                    }
                    sourceFile = null;
                } catch (IOException e) {
                    LogUtil.e(TAG, "===finally===IOException===" + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
        return flag;
    }

    /**
     * 创建ZIP文件 目录下可以包含子目录
     *
     * @param sourcePath 文件或文件夹路径
     * @param zipPath    生成的zip文件存在路径（包括文件名）
     */
    public static void createZip(String sourcePath, String zipPath) {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipPath);
            zos = new ZipOutputStream(fos);
            writeZip(new File(sourcePath), "", zos);
        } catch (FileNotFoundException e) {
            LogUtil.e(TAG, "创建ZIP文件失败" + e.getMessage());
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                LogUtil.e(TAG, "创建ZIP文件失败" + e.getMessage());
            }
        }
    }

    private static void writeZip(File file, String parentPath, ZipOutputStream zos) {
        if (file.exists()) {
            if (file.isDirectory()) {//处理文件夹
                parentPath += file.getName() + File.separator;
                File[] files = file.listFiles();
                if (files.length != 0) {
                    for (File f : files) {
                        writeZip(f, parentPath, zos);
                    }
                } else {       //空目录则创建当前目录
                    try {
                        zos.putNextEntry(new ZipEntry(parentPath));
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } else {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    byte[] content = new byte[1024];
                    int len;
                    while ((len = fis.read(content)) != -1) {
                        zos.write(content, 0, len);
                        zos.flush();
                    }

                } catch (FileNotFoundException e) {
                    LogUtil.e(TAG,"创建ZIP文件失败" + e.getMessage());
                } catch (IOException e) {
                    LogUtil.e(TAG,"创建ZIP文件失败" + e.getMessage());
                } finally {
                    try {
                        if (fis != null) {
                            fis.close();
                        }
                    } catch (IOException e) {
                        LogUtil.e(TAG,"创建ZIP文件失败" + e.getMessage());
                    }
                }
            }
        }
    }
}
