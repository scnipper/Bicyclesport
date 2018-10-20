package me.creese.sport.util;

import android.content.Context;
import android.graphics.Bitmap;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Files {

    private File cacheDir;
    private final File fileApp;
    private File fileMedia;


    public Files(Context context) {

        cacheDir = context.getCacheDir();



        fileApp = context.getFilesDir();

        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs())
                System.out.println("Not create " + cacheDir.getAbsolutePath());
        }

    }


    /**
     * Удаление картинки из файловой системы
     *
     * @param name
     */
    public void deleteFile(String name) {
        File deleteFile = new File(cacheDir.getAbsolutePath() + "/" + name);
        deleteFile.delete();
    }


    /**
     * Сохранение в кэш
     * @param data
     * @param name
     * @return
     */
    public String saveDataCache(byte[] data,String name) {
        String path = cacheDir.getAbsolutePath() + "/" + name;
        save(path, data);
        return path;
    }

    /**
     * Сохраняет данные в files
     *
     * @param data
     * @return Идентификатор сохранненой картинки
     */
    public String saveDataInternal(byte[] data, String name) {
        String path = fileApp.getAbsolutePath() + "/" + name;
        save(path, data);
        return path;
    }


    /**
     * Общая функция сохранения
     *
     * @param directory
     * @param data
     */
    private void save(String directory, byte[] data) {
        File file = new File(directory);

        try {
            if (!file.exists()) file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Общаяя функция чтения
     *
     * @param directory
     * @return Данные считанные из файла
     */
    public byte[] read(String directory) {

        byte[] data = null;
        try {
            FileInputStream fis = new FileInputStream(
                    new File(directory));
            data = new byte[fis.available()];
            fis.read(data);
            fis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public File getFileApp() {
        return fileApp;
    }



}
