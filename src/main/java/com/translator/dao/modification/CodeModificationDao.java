/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.translator.dao.modification;

import com.translator.model.codactor.api.translator.modification.*;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModificationRecord;

/**
 *
 * @author zantehays
 */
public interface CodeModificationDao {
    DesktopCodeModificationResponseResource getModifiedCode(DesktopCodeModificationRequestResource desktopCodeModificationRequestResource);

    FileModificationSuggestionModificationRecord getModifiedCodeModification(DesktopCodeModificationRequestResource desktopCodeModificationRequestResource);

    DesktopCodeModificationResponseResource getFixedCode(DesktopCodeModificationRequestResource desktopCodeModificationRequestResource);

    FileModificationSuggestionModificationRecord getModifiedCodeFix(DesktopCodeModificationRequestResource desktopCodeModificationRequestResource);

    DesktopCodeCreationResponseResource getCreatedCode(DesktopCodeCreationRequestResource desktopCodeCreationRequestResource);

    FileModificationSuggestionModificationRecord getModifiedCodeCreation(DesktopCodeCreationRequestResource desktopCodeCreationRequestResource);

    DesktopCodeTranslationResponseResource getTranslatedCode(DesktopCodeTranslationRequestResource desktopCodeTranslationRequestResource);
}
