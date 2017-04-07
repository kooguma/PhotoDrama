package com.loopeer.android.photodrama4android.media.utils;


import android.graphics.Bitmap;
import android.text.TextUtils;

import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.media.mediaio.XmlDrama;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.android.photodrama4android.utils.LocalImageUtils;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.zeroturnaround.zip.ZipUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ZipUtils {

    public static void zipFile(Drama drama) {
        List<String> audioPaths = drama.getAudioPaths();
        List<String> imagePaths = drama.getImagePaths();

        List<File> files = new ArrayList<>();

        List<File> imageScaleFiles = new ArrayList<>();
        for (String path : imagePaths) {
            try {
                File f = new File(FileManager.getInstance().getDir(), clipFileName(path));
                f.createNewFile();
                Bitmap bitmap = BitmapFactory.getInstance().getBitmapFromMemCache(path);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                byte[] data = bos.toByteArray();
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(data);
                fos.flush();
                fos.close();
                imageScaleFiles.add(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (String path : audioPaths) {
            files.add(new File(path));
        }
        String dramaXmlPath = dramaToXml(drama);
        File dramaXml = new File(dramaXmlPath);
        files.add(dramaXml);
        files.addAll(imageScaleFiles);
        String name = DateUtils.getCurrentTimeString();
        ZipUtil.packEntries(files.toArray(new File[]{}), new File(FileManager.getInstance().getDir() + "/" + name + ".zip"));
        FileManager.deleteFile(imageScaleFiles.toArray(new File[]{}));
        FileManager.deleteFile(dramaXml);
        BitmapFactory.getInstance().loadImages(imagePaths.toArray(new String[]{}));
    }

    public static Drama unzipFile(String zipFilePath) {
        String unzipFilePackage = zipFilePath.replace(".zip", "");
        ZipUtil.unpack(new File(zipFilePath), new File(unzipFilePackage));
        Drama drama = xmlToDrama(unzipFilePackage);
        return drama;
    }

    public static String clipFileName(String path) {
        if (TextUtils.isEmpty(path)) return null;
        String[] results = path.split("/");
        return results[results.length - 1];
    }

    public static String pathFromPackageFile(String packagePath, String name) {
        return packagePath + File.separator + name;
    }

    public static String dramaToXml(Drama drama) {
        XmlDrama xmlDrama = drama.toXml();
        Serializer serializer = new Persister();
        String path = FileManager.getInstance().getDir() + "/drama.xml";
        File result = new File(path);
        try {
            serializer.write(xmlDrama, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public static Drama xmlToDrama(String xmlPackage) {
        String xmlPath = xmlPackage  + "/drama.xml";
        Serializer serializer = new Persister();
        File source = new File(xmlPath);
        XmlDrama xmlDrama = null;
        try {
            xmlDrama = serializer.read(XmlDrama.class, source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (xmlDrama == null) return null;
        Drama drama = xmlDrama.toDrama(xmlPackage);
        return drama;
    }
}
