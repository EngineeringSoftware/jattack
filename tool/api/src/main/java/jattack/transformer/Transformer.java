package jattack.transformer;

import com.github.javaparser.ast.CompilationUnit;

/**
 * Abstract class for all transformers.
 */
public abstract class Transformer {

    protected final CompilationUnit origCu;
    protected CompilationUnit cu;

    public Transformer(CompilationUnit origCu) {
        this.origCu = origCu;
        this.cu = null;
    }

    public abstract void transform();

    public String getSrcCode() {
        return cu.toString();
    }

    public String transformAndGetSrcCode() {
        transform();
        return getSrcCode();
    }

    public CompilationUnit getCu() {
        return cu;
    }

    public CompilationUnit getOrigCu() {
        return origCu;
    }
}
