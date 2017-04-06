package com.loopeer.android.photodrama4android.media.utils;


import com.loopeer.android.photodrama4android.media.mediaio.XmlDrama;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.utils.FileManager;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;

public class ZipUtils {

    public static void zipFile() {
        String name = DateUtils.getCurrentTimeString();
        /*File file = new File(FileManager.getInstance().getDirDir() + "/" + name);
        if (!file.exists()) {
            file.mkdirs();
        }*/
        ZipUtil.packEntries( new File[]{new File("/storage/emulated/0/photodrama/example.xml")}, new File(FileManager.getInstance().getDirDir() + "/" + name + ".zip"));
//        ZipUtil.pack(file, new File(FileManager.getInstance().getDirDir() + "/" + name + ".zip"));

        /*ZipEntrySource[] entries = new ZipEntrySource[] {
                new FileSource("doc/readme.txt", new File("foo.txt")),
                new ByteSource("sample.txt", "bar".getBytes())
        };
        ZipUtil.addEntries(new File("/tmp/demo.zip"), entries, new File("/tmp/new.zip"));

        ZipUtil.pack(new File("/tmp/demo"), new File("/tmp/demo.zip"));*/
    }

    public static String dramaToXml(Drama drama) {
        XmlDrama xmlDrama = drama.toXml();
        Serializer serializer = new Persister();
        String path = FileManager.getInstance().getDirDir() + "/example.xml";
        File result = new File(path);
        try {
            serializer.write(xmlDrama, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public static Drama xmlToDrama(String xmlPath) {
        Serializer serializer = new Persister();
        File source = new File(xmlPath);
        XmlDrama xmlDrama = null;
        try {
            xmlDrama = serializer.read(XmlDrama.class, source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (xmlDrama == null) return null;
        Drama drama = xmlDrama.toDrama();
        return drama;
    }
}
