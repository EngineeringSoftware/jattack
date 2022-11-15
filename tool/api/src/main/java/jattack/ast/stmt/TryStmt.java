package jattack.ast.stmt;

import jattack.ast.exp.iterator.ChainItr;
import jattack.ast.visitor.Visitor;

public class TryStmt<T extends Throwable> extends Stmt {

    // TODO: we hardcode catch (<exceptionType> e) for now
    private final Class<T> exceptionType;

    private final BlockStmt tryBlock;
    private final BlockStmt catchBlock;
    private final BlockStmt finallyBlock;

    public TryStmt(BlockStmt tryBlock, Class<T> exceptionType, BlockStmt catchBlock) {
        this(tryBlock, exceptionType, catchBlock, null);
    }

    public TryStmt(BlockStmt tryBlock, Class<T> exceptionType,
            BlockStmt catchBlock, BlockStmt finallyBlock) {
        this.tryBlock = tryBlock;
        this.catchBlock = catchBlock;
        this.finallyBlock = finallyBlock;
        this.exceptionType = exceptionType;
    }

    public Class<T> getExceptionType() {
        return exceptionType;
    }

    public BlockStmt getTryBlock() {
        return tryBlock;
    }

    public BlockStmt getCatchBlock() {
        return catchBlock;
    }

    public BlockStmt getFinallyBlock() {
        return finallyBlock;
    }

    public boolean hasFinally() {
        return finallyBlock != null;
    }

    @Override
    protected void setItr() {
        itr = new ChainItr(tryBlock.itr(), catchBlock.itr(), finallyBlock.itr());
    }

    @Override
    public void stepRand() {
        tryBlock.stepRand();
        catchBlock.stepRand();
        finallyBlock.stepRand();
    }

    @Override
    public boolean hasRandChoice() {
        return tryBlock.hasRandChoice()
                && catchBlock.hasRandChoice()
                && finallyBlock.hasRandChoice();
    }

    @Override
    public void accept(Visitor v) {
        v.visitStmt(this);
    }
}
