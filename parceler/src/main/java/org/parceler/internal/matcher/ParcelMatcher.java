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
package org.parceler.internal.matcher;

import org.androidtransfuse.adapter.ASTArrayType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.util.matcher.Matcher;
import org.parceler.internal.ExternalParcelRepository;

/**
* @author John Ericksen
*/
public class ParcelMatcher implements Matcher<ASTType> {

    private final ExternalParcelRepository externalParcelRepository;

    public ParcelMatcher(ExternalParcelRepository externalParcelRepository) {
        this.externalParcelRepository = externalParcelRepository;
    }

    @Override
    public boolean matches(ASTType type) {
        return (!(type instanceof ASTArrayType)) && type.isAnnotated(org.parceler.Parcel.class) || externalParcelRepository.contains(type);
    }
}
