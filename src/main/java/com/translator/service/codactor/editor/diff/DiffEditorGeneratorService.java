package com.translator.service.codactor.editor.diff;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface DiffEditorGeneratorService {
    Editor createDiffEditor(String beforeCode, String afterCode);

    void updateDiffEditor(Editor editor, String beforeCode, String afterCode);

    Editor createDiffEditorWithMimickedIndentation(String beforeCode, String afterCode);
}
