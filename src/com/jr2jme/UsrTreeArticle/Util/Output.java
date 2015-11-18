package com.jr2jme.UsrTreeArticle.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by K.H on 2015/11/09.
 */
public class Output {
    public static void outputbinary(File file, Object obj){
        try {
            FileOutputStream outFile = new FileOutputStream(file);
            ObjectOutputStream outObject = new ObjectOutputStream(outFile);
            outObject.writeObject(obj);
            outObject.close();
            outFile.close();
        } catch (IOException e) {
            System.out.println(file.getName());
            e.printStackTrace();
        }
    }
}
