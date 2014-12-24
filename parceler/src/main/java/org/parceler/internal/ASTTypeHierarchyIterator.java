package org.parceler.internal;

import com.google.common.collect.ImmutableSet;
import org.androidtransfuse.adapter.ASTType;

import java.util.Iterator;

/**
 * @author John Ericksen
 */
public class ASTTypeHierarchyIterator implements Iterator<ASTType> {

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
        return analyze.isEmpty() || analyze.contains(type);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
