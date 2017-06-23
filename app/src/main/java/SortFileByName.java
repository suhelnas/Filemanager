package com.example.pc.filemanager;

import java.io.File;
import java.util.Comparator;

/**
 * Created by pc on 4/1/2017.
 */
public class SortFileByName implements Comparator<File> {
    @Override
    public int compare(File f1, File f2) {
        return f1.getName().compareTo(f2.getName());
    }
    }

