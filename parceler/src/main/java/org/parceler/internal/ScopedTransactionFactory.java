/**
 * Copyright 2013 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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