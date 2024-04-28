package com.translator.service.codactor.ide.directory.copy;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class DirectoryCopierServiceImpl implements DirectoryCopierService {

    private File copiedDirectory;


    @Override
    public void copy(File source) {
        copiedDirectory = source;
    }
    @Override
    public void paste(File destination) throws IOException {
        if (copiedDirectory == null) {
            return;
        }
        if (copiedDirectory.isDirectory()) {
            String newName = copiedDirectory.getName() + "_copy";
            File destinationDirectory;
            if (destination.isDirectory()) {
                destinationDirectory = new File(destination, newName);
            } else {
                destinationDirectory = destination;
            }
            if (destinationDirectory.exists()) {
                int choice = JOptionPane.showConfirmDialog(null, "A file/folder with the same name already exists in the destination folder. Do you want to overwrite it?", "Overwrite?", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            copyDirectory(copiedDirectory, destinationDirectory);
            copiedDirectory = null;
        } else {
            String extension = "";
            int extensionIndex = copiedDirectory.getName().lastIndexOf('.');
            if (extensionIndex > 0) {
                extension = copiedDirectory.getName().substring(extensionIndex);
            }
            String newName = copiedDirectory.getName().replace(extension, "") + "_copy" + extension;
            File destinationFile;
            if (destination.isDirectory()) {
                destinationFile = new File(destination, newName);
            } else {
                destinationFile = destination;
            }
            if (destinationFile.exists()) {
                int choice = JOptionPane.showConfirmDialog(null, "A file/folder with the same name already exists in the destination folder. Do you want to overwrite it?", "Overwrite?", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            Files.copy(copiedDirectory.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            copiedDirectory = null;
        }
    }


    private void copyDirectory(File source, File destination) throws IOException {
        if (!destination.exists()) {
            destination.mkdir();
        }
        File[] files = source.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                copyDirectory(file, new File(destination.getAbsolutePath() + File.separator + file.getName()));
            } else {
                byte[] fileData = Files.readAllBytes(file.toPath());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(fileData);
                byte[] copiedData = out.toByteArray();
                File copiedFile = new File(destination.getAbsolutePath() + File.separator + file.getName());
                FileOutputStream fileOutputStream = new FileOutputStream(copiedFile);
                fileOutputStream.write(copiedData);
                fileOutputStream.close();
            }
        }
    }


    @Override
    public File getCopiedDirectory() {
        return copiedDirectory;
    }
}
