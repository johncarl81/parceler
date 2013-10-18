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

import com.sun.codemodel.JCodeModel;
import org.androidtransfuse.adapter.ASTFactory;
import org.androidtransfuse.annotations.Bind;
import org.androidtransfuse.annotations.Bindings;
import org.androidtransfuse.annotations.Install;
import org.androidtransfuse.annotations.Provides;
import org.androidtransfuse.bootstrap.BootstrapModule;
import org.androidtransfuse.gen.InjectionBuilderContextFactory;
import org.androidtransfuse.gen.invocationBuilder.DefaultInvocationBuilderStrategy;
import org.androidtransfuse.gen.invocationBuilder.InvocationBuilderStrategy;
import org.androidtransfuse.gen.variableDecorator.VariableExpressionBuilderFactory;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.inject.Singleton;
import javax.lang.model.util.Elements;

@BootstrapModule
@Bindings({
        @Bind(type = InvocationBuilderStrategy.class, to = DefaultInvocationBuilderStrategy.class),
        @Bind(type = Elements.class, to = NoOpElements.class),
        @Bind(type = Filer.class, to = NoOpFiler.class)
})
@Install({
        ASTFactory.class,
        VariableExpressionBuilderFactory.class,
        InjectionBuilderContextFactory.class,
        InjectionBuilderContextFactory.class
})
public class TestParcelerModule {

    @Provides
    @Singleton
    public JCodeModel getCodeModel(){
        return new JCodeModel();
    }

    @Provides
    @Singleton
    public ErrorCheckingMessager getMessager(){
        return new ErrorCheckingMessager();
    }

    @Provides
    public Messager getMessager(ErrorCheckingMessager messager){
        return messager;
    }
}