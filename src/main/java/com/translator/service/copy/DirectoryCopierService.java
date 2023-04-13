package com.translator.service.copy;

import java.io.File;
import java.io.IOException;

public interface DirectoryCopierService {
    void copy(File source) throws IOException;

    void paste(File destination) throws IOException;

    File getCopiedDirectory();
}
