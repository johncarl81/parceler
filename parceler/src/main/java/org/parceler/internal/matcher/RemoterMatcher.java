/**
 * Copyright 2011-2015 John Ericksen
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parceler.internal.matcher;


import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.util.matcher.Matcher;

/**
 * Matches a @Remoter inteface
 *
 * @author js
 */
public class RemoterMatcher implements Matcher<ASTType> {

    private static final ASTType REMOTER = new ASTStringType("remoter.annotations.Remoter");

    @Override
    public boolean matches(ASTType input) {
        return input.isInterface() && input.isAnnotated(REMOTER);
    }
}
