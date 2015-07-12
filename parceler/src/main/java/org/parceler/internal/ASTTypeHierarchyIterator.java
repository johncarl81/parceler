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

import com.google.common.collect.ImmutableSet;
import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;

import java.util.Iterator;

/**
 * @author John Ericksen
 */
public class ASTTypeHierarchyIterator implements Iterator<ASTType> {

    private static final ASTType OBJECT_TYPE = new ASTStringType(Object.class.getCanonicalName());

    private final ImmutableSet<ASTType> analyze;
    private final ASTType root;

    private ASTType current;
    private boolean started;

    public ASTTypeHierarchyIterator(ASTType root, ImmutableSet<ASTType> analyze) {
        this.root = root;
        this.analyze = analyze;
    }

    @Override
    public boolean hasNext() {
        return calculateNext() != null;
    }

    @Override
    public ASTType next() {
        current = calculateNext();
        if(!started){
            started = true;
        }
        return current;
    }

    protected ASTType calculateNext(){
        ASTType next = null;
        if(current == null){
            if(!started) {
                next = root;
            }
        }
        else{
            next = current.getSuperClass();
        }

        while(next != null && !checkAnalysis(next)){
            next = next.getSuperClass();
        }

        return next;
    }

    private boolean checkAnalysis(ASTType type){
        return (analyze.isEmpty() || analyze.contains(type)) && !type.equals(OBJECT_TYPE);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
