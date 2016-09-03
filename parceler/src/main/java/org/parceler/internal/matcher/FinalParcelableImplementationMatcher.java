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
package org.parceler.internal.matcher;

import org.androidtransfuse.adapter.ASTField;
import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.util.matcher.Matcher;

/**
 * @author John Ericksen
 */
public class FinalParcelableImplementationMatcher implements Matcher<ASTType> {

    private static final ASTType CREATOR_TYPE = new ASTStringType("android.os.Parcelable.Creator");
    private static final ASTType PARCELABLE_TYPE = new ASTStringType("android.os.Parcelable");

    @Override
    public boolean matches(ASTType input) {
        return input.isFinal() && input.inheritsFrom(PARCELABLE_TYPE) && isCreatorFieldImplemented(input);
    }

    private boolean isCreatorFieldImplemented(ASTType type) {
        ASTType creatorType = getTypeForField(type, "CREATOR");
        return creatorType != null && creatorType.extendsFrom(CREATOR_TYPE);
    }

    private ASTType getTypeForField(ASTType type, String name) {
        for (ASTField field : type.getFields()) {
            if(name.equals(field.getName())){
                return field.getASTType();
            }
        }
        return null;
    }
}
