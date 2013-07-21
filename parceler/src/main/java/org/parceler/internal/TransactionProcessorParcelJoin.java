package org.parceler.internal;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.androidtransfuse.transaction.TransactionProcessor;
import org.androidtransfuse.transaction.TransactionProcessorPool;

import java.util.Map;

/**
 * @author John Ericksen
 */
public class TransactionProcessorParcelJoin<V, R> implements TransactionProcessor<V, R> {

    private TransactionProcessorPool<V, V> externalRepositoryProcessor;
    private TransactionProcessor<?, Map<V, R>> externalProcessor;
    private TransactionProcessor<V, R> parcelProcessor;

    public TransactionProcessorParcelJoin(TransactionProcessorPool<V, V> externalRepositoryProcessor,
                                          TransactionProcessor<?, Map<V, R>> externalProcessor,
                                          TransactionProcessor<V, R> parcelProcessor) {
        this.externalRepositoryProcessor = externalRepositoryProcessor;
        this.externalProcessor = externalProcessor;
        this.parcelProcessor = parcelProcessor;
    }

    @Override
    public void execute() {
        externalRepositoryProcessor.execute();
        if(externalRepositoryProcessor.isComplete() && !externalProcessor.isComplete()){
            externalProcessor.execute();
        }

        if (externalProcessor.isComplete() && !parcelProcessor.isComplete()) {
            parcelProcessor.execute();
        }
    }

    @Override
    public boolean isComplete() {
        return externalProcessor.isComplete() && parcelProcessor.isComplete();
    }

    @Override
    public ImmutableSet<Exception> getErrors() {
        ImmutableSet.Builder<Exception> builder = ImmutableSet.builder();
        builder.addAll(externalProcessor.getErrors());
        builder.addAll(parcelProcessor.getErrors());

        return builder.build();
    }

    @Override
    public Map<V, R> getResults() {
        ImmutableMap.Builder<V, R> builder = ImmutableMap.builder();

        for (Map<V, R> externalResults : externalProcessor.getResults().values()) {
            builder.putAll(externalResults);
        }

        builder.putAll(parcelProcessor.getResults());

        return builder.build();
    }
}
