package com.translator.service.file;

import com.google.inject.Injector;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.impl.FileStatusProvider;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.ui.JBColor;
import com.translator.CodactorInjector;
import com.translator.model.modification.FileModification;
import com.translator.model.modification.FileModificationTracker;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class CustomFileColorStatusProvider implements FileStatusProvider {
    /@Override
    public FileStatus getFileStatus(@NotNull VirtualFile virtualFile) {
        /*Project[] projects = ProjectManager.getInstance().getOpenProjects();
        Project targetProject = null;
        for (Project project : projects) {
            if (VfsUtilCore.isAncestor(project.getBaseDir(), virtualFile, false)) {
                targetProject = project;
                break;
            }
        }

        if (targetProject != null) {
            Injector injector = CodactorInjector.getInstance().getInjector(targetProject);
            FileModificationTrackerService fileModificationTrackerService = injector.getInstance(FileModificationTrackerService.class);
            Map<String, FileModificationTracker> fileModificationTrackerMap = fileModificationTrackerService.getActiveModificationFiles();
            FileModificationTracker fileModificationTracker = fileModificationTrackerMap.get(virtualFile.getPath());
            if (fileModificationTracker != null) {
                System.out.println("This gets calledaba");
                boolean allModsDone = true;
                for (FileModification fileModification : fileModificationTracker.getModifications()) {
                    if (!fileModification.isDone()) {
                        allModsDone = false;
                        break;
                    }
                }
                Color highlightColor;
                if (allModsDone) {
                    highlightColor = Color.decode("#228B22");
                } else {
                    highlightColor = Color.decode("#009688");
                }
                if (allModsDone) {
                    // Create a custom FileStatus with your desired color and text
                    return new FileStatus() {
                        @Override
                        public String getText() {
                            return "Undergoing Modifications";
                        }

                        @Override
                        public Color getColor() {
                            return highlightColor;
                        }

                        @Override
                        public @NotNull ColorKey getColorKey() {
                            // Use a custom ColorKey for the highlight color
                            return ColorKey.createColorKey("UNDERGOING_MODIFICATIONS", highlightColor);
                        }

                        @Override
                        public @NotNull @NonNls String getId() {
                            // Use a unique ID for your custom FileStatus
                            return "UNDERGOING_MODIFICATIONS";
                        }
                    };
                }
            }
        }
        // Return the default FileStatus for other cases
        return FileStatus.NOT_CHANGED;*/
        return FileStatus.NOT_CHANGED;
    }
}