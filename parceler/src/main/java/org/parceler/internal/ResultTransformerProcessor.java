/**
 * Copyright 2011-2015 John Ericksen
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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.transaction.Transaction;
import org.androidtransfuse.transaction.TransactionProcessorPool;

import javax.annotation.Nullable;
import javax.inject.Provider;
import java.util.Map;

/**
 * @author John Ericksen
 */
public class ResultTransformerProcessor<T> extends TransactionProcessorPool<Provider<ASTType>, T> {
    private final TransactionProcessorPool<Provider<ASTType>, T> delegate;
    private final Function<T, T> function;

    public ResultTransformerProcessor(TransactionProcessorPool<Provider<ASTType>, T> delegate, Function<T, T> function) {
        this.delegate = delegate;
        this.function = function;
    }

    @Override
    public void submit(Transaction<Provider<ASTType>, T> transaction) {
        delegate.submit(transaction);
    }

    @Override
    public void execute() {
        delegate.execute();
    }

    @Override
    public Map<Provider<ASTType>, T> getResults() {
        return Maps.filterValues(Maps.transformValues(delegate.getResults(), function),
                new Predicate<T>() {
                    @Override
                    public boolean apply(@Nullable T t) {
                        return t != null;
                    }
                });
    }

    @Override
    public boolean isComplete() {
        return delegate.isComplete();
    }

    @Override
    public ImmutableSet<Exception> getErrors() {
        return delegate.getErrors();
    }
}
