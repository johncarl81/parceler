/**
 * Copyright 2013-2015 John Ericksen
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

import org.androidtransfuse.adapter.ASTAccessModifier;
import org.androidtransfuse.gen.invocationBuilder.*;
import org.androidtransfuse.validation.Validator;

import javax.inject.Inject;

/**
 * @author John Ericksen
 */
public class ParcelerInvocationBuilderStrategy implements InvocationBuilderStrategy {

    private final ModifiedInvocationBuilder publicInvocationBuilder;
    private final ModifiedInvocationBuilder privateInvocationBuilder;
    private final ModifiedInvocationBuilder protectedInvocationBuilder;

    @Inject
    public ParcelerInvocationBuilderStrategy(PublicInvocationBuilder publicInvocationBuilder,
                                             ProtectedInvocationBuilder protectedInvocationBuilder,
                                             ParcelerPrivateInvocationBuilder privateInvocationBuilder,
                                             Validator validator) {
        this.publicInvocationBuilder = publicInvocationBuilder;
        this.protectedInvocationBuilder = protectedInvocationBuilder;
        this.privateInvocationBuilder = new WarningInvocationBuilderDecorator(privateInvocationBuilder, validator);
    }

    @Override
    public ModifiedInvocationBuilder getInjectionBuilder(ASTAccessModifier modifier) {
        switch (modifier) {
            case PUBLIC:
                return publicInvocationBuilder;
            case PACKAGE_PRIVATE:
            case PROTECTED:
                return protectedInvocationBuilder;
            default:
                return privateInvocationBuilder;
        }
    }
}
