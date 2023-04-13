package com.translator.service.modification;

import com.google.gson.Gson;
import com.translator.dao.modification.CodeModificationDao;
import com.translator.dao.modification.CodeModificationDaoImpl;
import com.translator.model.api.translator.modification.*;
import com.translator.model.modification.FileModificationSuggestionModificationRecord;
import com.translator.dao.firebase.FirebaseTokenService;

import javax.inject.Inject;

public class CodeModificationServiceImpl implements CodeModificationService {
    private CodeModificationDao codeModificationDao;

    @Inject
    public CodeModificationServiceImpl(CodeModificationDao codeModificationDao) {
        this.codeModificationDao = codeModificationDao;
    }

    @Override
    public DesktopCodeModificationResponseResource getModifiedCode(DesktopCodeModificationRequestResource desktopCodeModificationRequestResource) {
        /*if (desktopCodeModificationRequestResource.getCode().length() > 3600) {
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = new DesktopCodeModificationResponseResource();
            desktopCodeModificationResponseResource.setSpecifiedError("Prompt is too long");
            return desktopCodeModificationResponseResource;
        }*/
        int allowableFailureCounter = 0;
        do {
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationDao.getModifiedCode(desktopCodeModificationRequestResource);
            int latestResponseCode = desktopCodeModificationResponseResource.getResponseCode();
            if (desktopCodeModificationResponseResource.getModificationSuggestions() != null) {
                return desktopCodeModificationResponseResource;
            } else if (latestResponseCode == 400) {
                allowableFailureCounter++;
            } else {
                return desktopCodeModificationResponseResource;
            }
        } while (allowableFailureCounter < 3);
        return null;
    }

    @Override
    public FileModificationSuggestionModificationRecord getModifiedCodeModification(DesktopCodeModificationRequestResource desktopCodeModificationRequestResource) {
        /*if (desktopCodeModificationRequestResource.getCode().length() > 3600) {
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = new DesktopCodeModificationResponseResource();
            desktopCodeModificationResponseResource.setSpecifiedError("Prompt is too long");
            return desktopCodeModificationResponseResource;
        }*/
        int allowableFailureCounter = 0;
        do {
            FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = codeModificationDao.getModifiedCodeModification(desktopCodeModificationRequestResource);
            if (fileModificationSuggestionModificationRecord == null) {
                return null;
            }
            int latestResponseCode = fileModificationSuggestionModificationRecord.getResponseCode();
            if (fileModificationSuggestionModificationRecord.getEditedCode() != null) {
                return fileModificationSuggestionModificationRecord;
            } else if (latestResponseCode == 400) {
                allowableFailureCounter++;
            } else {
                return fileModificationSuggestionModificationRecord;
            }
        } while (allowableFailureCounter < 3);
        return null;
    }

    @Override
    public DesktopCodeModificationResponseResource getFixedCode(DesktopCodeModificationRequestResource desktopCodeModificationRequestResource) {
        /*if (desktopCodeModificationRequestResource.getCode().length() > 3530) {
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = new DesktopCodeModificationResponseResource();
            desktopCodeModificationResponseResource.setSpecifiedError("Prompt is too long");
            return desktopCodeModificationResponseResource;
        }*/
        int allowableFailureCounter = 0;
        do {
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = codeModificationDao.getFixedCode(desktopCodeModificationRequestResource);
            int latestResponseCode = desktopCodeModificationResponseResource.getResponseCode();
            if (desktopCodeModificationResponseResource.getModificationSuggestions() != null) {
                return desktopCodeModificationResponseResource;
            } else if (latestResponseCode == 400) {
                allowableFailureCounter++;
            } else {
                return desktopCodeModificationResponseResource;
            }
        } while (allowableFailureCounter < 4);
        return null;
    }

    @Override
    public FileModificationSuggestionModificationRecord getModifiedCodeFix(DesktopCodeModificationRequestResource desktopCodeModificationRequestResource) {
        /*if (desktopCodeModificationRequestResource.getCode().length() > 3530) {
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = new DesktopCodeModificationResponseResource();
            desktopCodeModificationResponseResource.setSpecifiedError("Prompt is too long");
            return desktopCodeModificationResponseResource;
        }*/
        int allowableFailureCounter = 0;
        do {
            FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = codeModificationDao.getModifiedCodeFix(desktopCodeModificationRequestResource);
            if (fileModificationSuggestionModificationRecord == null) {
                return null;
            }
            int latestResponseCode = fileModificationSuggestionModificationRecord.getResponseCode();
            if (fileModificationSuggestionModificationRecord.getEditedCode() != null) {
                return fileModificationSuggestionModificationRecord;
            } else if (latestResponseCode == 400) {
                allowableFailureCounter++;
            } else {
                return fileModificationSuggestionModificationRecord;
            }
        } while (allowableFailureCounter < 4);
        return null;
    }

    @Override
    public DesktopCodeCreationResponseResource getCreatedCode(DesktopCodeCreationRequestResource desktopCodeCreationRequestResource) {
        /*if (desktopCodeCreationRequestResource.getDescription().length() > 3580) {
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = new DesktopCodeModificationResponseResource();
            desktopCodeModificationResponseResource.setSpecifiedError("Prompt is too long");
            return desktopCodeModificationResponseResource;
        }*/
        int allowableFailureCounter = 0;
        do {
            DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationDao.getCreatedCode(desktopCodeCreationRequestResource);
            int latestResponseCode = desktopCodeCreationResponseResource.getResponseCode();
            if (desktopCodeCreationResponseResource.getModificationSuggestions() != null) {
                return desktopCodeCreationResponseResource;
            } else if (latestResponseCode == 400) {
                allowableFailureCounter++;
            } else {
                return desktopCodeCreationResponseResource;
            }
        } while (allowableFailureCounter < 4);
        return null;
    }

    @Override
    public FileModificationSuggestionModificationRecord getModifiedCodeCreation(DesktopCodeCreationRequestResource desktopCodeCreationRequestResource) {
        /*if (desktopCodeCreationRequestResource.getDescription().length() > 3580) {
            DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = new DesktopCodeModificationResponseResource();
            desktopCodeModificationResponseResource.setSpecifiedError("Prompt is too long");
            return desktopCodeModificationResponseResource;
        }*/
        int allowableFailureCounter = 0;
        do {
            FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = codeModificationDao.getModifiedCodeCreation(desktopCodeCreationRequestResource);
            if (fileModificationSuggestionModificationRecord == null) {
                return null;
            }
            int latestResponseCode = fileModificationSuggestionModificationRecord.getResponseCode();
            if (fileModificationSuggestionModificationRecord.getEditedCode() != null) {
                return fileModificationSuggestionModificationRecord;
            } else if (latestResponseCode == 400) {
                allowableFailureCounter++;
            } else {
                return fileModificationSuggestionModificationRecord;
            }
        } while (allowableFailureCounter < 4);
        return null;
    }

    @Override
    public DesktopCodeTranslationResponseResource getTranslatedCode(DesktopCodeTranslationRequestResource desktopCodeTranslationRequestResource) {
        int allowableFailureCounter = 0;
        do {
            DesktopCodeTranslationResponseResource desktopCodeTranslationResponseResource = codeModificationDao.getTranslatedCode(desktopCodeTranslationRequestResource);
            int latestResponseCode = desktopCodeTranslationResponseResource.getResponseCode();
            if (desktopCodeTranslationResponseResource.getModificationSuggestions() != null) {
                return desktopCodeTranslationResponseResource;
            } else if (latestResponseCode == 400) {
                allowableFailureCounter++;
            } else {
                return desktopCodeTranslationResponseResource;
            }
        } while (allowableFailureCounter < 4);
        return null;
    }
}
