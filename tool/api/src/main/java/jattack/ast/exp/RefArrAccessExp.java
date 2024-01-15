package jattack.ast.exp;

import jattack.Config;
import jattack.ast.exp.iterator.ChainItr;
import jattack.ast.exp.iterator.ExpItr;
import jattack.ast.exp.iterator.Itr;
import jattack.ast.visitor.Visitor;
import jattack.data.Data;
import jattack.driver.Driver;
import jattack.util.TypeUtil;

import java.lang.reflect.Array;

/**
 * Reference array access expression, evaluated to an instance of the
 * component type of this array.
 */
public class RefArrAccessExp<E, A> extends LHSExp<E> {

    private final Class<E> type;

    private final Exp<A> id;

    private Exp<Integer> index;

    private boolean inferIndices;

    public RefArrAccessExp(Class<E> type, Exp<A> id) {
        // Auto infer valid indices
        this(type, id, null);
        inferIndices = true;
    }

    public RefArrAccessExp(Class<E> type, Exp<A> id, Exp<Integer> index) {
        this.type = type;
        this.id = id;
        this.index = index;
        this.inferIndices = false;
    }

    @Override
    protected void setItr() {
        if (!inferIndices) {
            itr = new ChainItr(id.itr(), index.itr());
            return;
        }

        itr = new ExpItr() {

            private final Itr idItr = id.itr();
            private Itr indexItr;

            @Override
            public void next() {
                super.next();
                // iteration order: index then id
                if (idItr.isReset() || indexItr == null || !indexItr.hasNext()) {
                    idItr.next();
                    setIndexFromCurrentId();
                    indexItr = index.itr();
                }
                indexItr.next();
            }

            @Override
            public boolean hasNext() {
                if (isReset()) {
                    return idItr.hasNext();
                }
                return idItr.hasNext() || indexItr.hasNext();
            }

            @Override
            public void reset() {
                super.reset();
                idItr.reset();
                indexItr = null;
            }
        };
    }

    @Override
    public void stepRand() {
        id.stepRand();
        if (inferIndices) {
            setIndexFromCurrentId();
        }
        index.stepRand();
    }

    @Override
    public boolean hasRandChoice() {
        return id.hasRandChoice() && (inferIndices || index.hasRandChoice());
    }

    @Override
    public void updateVal(E val) {
        A arr = id.evaluate();
        int i = index.evaluate();
        TypeUtil.arraySet(arr, i, val);
        if (!(id instanceof LHSExp)) {
            throw new RuntimeException("Expected id is a LHSExp but it is " + id.getClass());
        }
        ((LHSExp<A>) id).updateVal(arr);
    }

    @Override
    public Class<E> getType() {
        return type;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            index.accept(v);
            id.accept(v);
            v.endVisit(this);
        }
    }

    private void setIndexFromCurrentId() {
        int len = 0;
        try {
            len = Config.staticGen ?
                    // We have no way to know the length of an array if we
                    // do static generation. Thus, we guess one.
                    Driver.rand.nextInt(1, 1001) :
                    Array.getLength(id.evaluate());
        } catch (Throwable e) {
            if (!Data.isInvocationTemplateException(e)) {
                throw e;
            }
            // If this is some runtime exception from evaluating the
            // template, e.g., divided by zero, array out of bound,
            // etc, we should not throw now because we still want to
            // fill the hole; otherwise we would leave the reachable
            // hole unfilled.
        }
        if (len == 0) {
            // 1) We force an index 0 when id is an empty array, so
            // executing the generated program will throw the same
            // IndexOutOfBoundException although with a different
            // index value.
            // 2) When some exception is thrown from evaluating id,
            // we should suppress the exception until we finish
            // filling in the hole. Any number for index works.
            index = new ImIntVal(0);
            return;
        }
        index = new IntVal(0, len);
    }
}
