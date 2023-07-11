package com.translator.service.codactor.transformer;

import com.translator.model.codactor.modification.queued.QueuedFileModificationObjectHolder;
import com.translator.model.codactor.modification.queued.QueuedFileModificationObjectReferenceHolder;
import com.translator.model.codactor.modification.queued.QueuedModificationObjectType;

import java.util.ArrayList;
import java.util.List;

public class QueuedFileModificationObjectHolderToQueuedFileModificationObjectReferenceHolderTransformerServiceImpl implements QueuedFileModificationObjectHolderToQueuedFileModificationObjectReferenceHolderTransformerService {
    @Override
    public QueuedFileModificationObjectReferenceHolder convert(QueuedFileModificationObjectHolder queuedFileModificationObjectHolder) {
        if (queuedFileModificationObjectHolder.getQueuedModificationObjectType() == QueuedModificationObjectType.FILE_MODIFICATION) {
            return new QueuedFileModificationObjectReferenceHolder(queuedFileModificationObjectHolder.getFileModification());
        } else if (queuedFileModificationObjectHolder.getQueuedModificationObjectType() == QueuedModificationObjectType.FILE_MODIFICATION_SUGGESTION_MODIFICATION) {
            return new QueuedFileModificationObjectReferenceHolder(queuedFileModificationObjectHolder.getFileModificationSuggestionModification());
        } else if (queuedFileModificationObjectHolder.getQueuedModificationObjectType() == QueuedModificationObjectType.MULTI_FILE_MODIFICATION) {
            return new QueuedFileModificationObjectReferenceHolder(queuedFileModificationObjectHolder.getMultiFileModification());
        } else {
            throw new RuntimeException("Unknown queued modification object type: " + queuedFileModificationObjectHolder.getQueuedModificationObjectType());
        }
    }

    @Override
    public List<QueuedFileModificationObjectReferenceHolder> convert(List<QueuedFileModificationObjectHolder> queuedFileModificationObjectHolders) {
        List<QueuedFileModificationObjectReferenceHolder> queuedFileModificationObjectReferenceHolders = new ArrayList<>();
        for (QueuedFileModificationObjectHolder queuedFileModificationObjectHolder : queuedFileModificationObjectHolders) {
            queuedFileModificationObjectReferenceHolders.add(convert(queuedFileModificationObjectHolder));
        }
        return queuedFileModificationObjectReferenceHolders;
    }
}
