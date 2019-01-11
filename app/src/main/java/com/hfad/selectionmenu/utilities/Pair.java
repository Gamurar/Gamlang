package com.hfad.selectionmenu.utilities;

public class Pair<A, B> {
    public final A originalContext;
    public final B translatedContext;

    public Pair(final A originalContext, final B translatedContext) {
        this.originalContext = originalContext;
        this.translatedContext = translatedContext;
    }

    // TODO: Override 'equals', 'hashcode' and 'toString'
}