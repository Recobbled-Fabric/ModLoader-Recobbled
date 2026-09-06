package io.github.fkononowicz.modloaderfix;

import java.io.File;
import java.util.Comparator;

public class ModSorter implements Comparator<File> {

    @Override
    public int compare(File file0, File file1) {
        return String.CASE_INSENSITIVE_ORDER.compare(file0.getName(), file1.getName());
    }

}
