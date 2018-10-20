package me.creese.sport.util;

import java.io.File;
import java.io.FileFilter;

public class FilterRoutes implements FileFilter {
    public static final String EXTENSION = ".route";

    @Override
    public boolean accept(File pathname) {

        return pathname.getName().endsWith(EXTENSION);
    }
}
