package com.translator.service.codactor.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringCodeOmittedRestorationManagementServiceImpl implements StringCodeOmittedRestorationManagementService {
    private DiffStringService diffStringService;
    private StringCodeOmittedRestorerService stringCodeOmittedRestorerService;
    private FusedMethodHandlerService fusedMethodHandlerService;

    public StringCodeOmittedRestorationManagementServiceImpl(DiffStringService diffStringService,
                                                             StringCodeOmittedRestorerService stringCodeOmittedRestorerService,
                                                             FusedMethodHandlerService fusedMethodHandlerService) {
        this.diffStringService = diffStringService;
        this.stringCodeOmittedRestorerService = stringCodeOmittedRestorerService;
        this.fusedMethodHandlerService = fusedMethodHandlerService;
    }

    @Override
    public String restoreOmittedString(String beforeCode, String afterCode) {
        String result = diffStringService.getDiffString(beforeCode, afterCode);
        //result = diffStringService.postProcessDiffString(result);
        result = fusedMethodHandlerService.fixImproperlyFusedMethods(beforeCode, afterCode, result);
        //result = stringCodeOmittedRestorerService.restoreOmittedString(result);
        return result;
    }
}
