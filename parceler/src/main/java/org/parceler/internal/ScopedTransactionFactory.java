package org.parceler.internal;

import org.androidtransfuse.annotations.ScopeReference;
import org.androidtransfuse.config.EnterableScope;
import org.androidtransfuse.transaction.ScopedTransactionWorker;
import org.androidtransfuse.transaction.Transaction;
import org.androidtransfuse.transaction.TransactionWorker;

import javax.inject.Inject;
import javax.inject.Provider;

public class ScopedTransactionFactory {

    private final EnterableScope codeGenerationScope;

    @Inject
    public ScopedTransactionFactory(
            @ScopeReference(CodeGenerationScope.class) EnterableScope codeGenerationScope) {
        this.codeGenerationScope = codeGenerationScope;
    }

    public <V, R> Transaction<V, R> buildTransaction(V value, Provider<? extends TransactionWorker<V, R>> workerProvider) {
        return new Transaction<V, R>(value, new ScopedTransactionWorker<V, R>(codeGenerationScope, workerProvider));
    }
}