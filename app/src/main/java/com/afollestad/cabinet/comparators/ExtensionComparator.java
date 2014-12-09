package com.afollestad.cabinet.comparators;

import com.afollestad.cabinet.file.base.File;

/**
 * Sorts files by extension, alphabetically. Folders will be at the beginning.
 *
 * @author Aidan Follestad (afollestad)
 */
public class ExtensionComparator implements java.util.Comparator<File> {

    @Override
    public int compare(File lhs, File rhs) {
        // First, folders always come before files
        if (lhs.isDirectory() && !rhs.isDirectory()) {
            return -1;
        } else if (lhs.isDirectory() && rhs.isDirectory()) {
            return lhs.getName().compareTo(rhs.getName());
        } else if (!lhs.isDirectory() && rhs.isDirectory()) {
            return 1;
        } else {
            // Once folders are sorted, sort files by extension
            final int extensionCompare = lhs.getExtension().compareTo(rhs.getExtension());
            if (extensionCompare == 0) {
                // If the extensions are the same, sort by name
                return lhs.getName().compareTo(rhs.getName());
            }
            return extensionCompare;
        }
    }
}