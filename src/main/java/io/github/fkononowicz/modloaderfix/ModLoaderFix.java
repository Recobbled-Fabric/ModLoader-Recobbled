package io.github.fkononowicz.modloaderfix;

import net.minecraft.src.ModLoader;

public class ModLoaderFix {

    /**
     * Returns a string representation of the package the ML class is in
     *
     * @implNote We get the package name by extracting it from the fully qualified ML class name.
     * If the ML class is not in a package it returns {@code null}
     */
    public static String getMLPackage() {
        String mlClassName = ModLoader.class.getName();
        int lastDotIndex = mlClassName.lastIndexOf('.');
        //noinspection ConstantValue
        if(lastDotIndex != -1) {
            return mlClassName.substring(
                    0,
                    lastDotIndex
            );
        }
        return null;
    }

}
