package sketchy.ast.exp;

import sketchy.Config;
import sketchy.ast.exp.iterator.ChainItr;
import sketchy.ast.exp.iterator.ExpItr;
import sketchy.ast.exp.iterator.Itr;
import sketchy.ast.nodetypes.NodeWithSideEffect;
import sketchy.ast.visitor.Visitor;
import sketchy.data.Data;
import sketchy.driver.Driver;

import java.lang.reflect.Array;

/**
 * Reference array access expression, evaluated to an instance of the
 * component type of this array.
 */
public class RefArrAccessExp<E, A> extends LHSExp<E> implements NodeWithSideEffect<E> {

    private final Exp<A> id;

    private Exp<Integer> index;

    private boolean inferIndices;

    public RefArrAccessExp(Exp<A> id) {
        // Auto infer valid indices
        this(id, null);
        inferIndices = true;
    }

    public RefArrAccessExp(Exp<A> id, Exp<Integer> index) {
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
    public void accept(Visitor v) {
        if (v.visit(this)) {
            index.accept(v);
            id.accept(v);
            v.endVisit(this);
        }
    }

    private void setIndexFromCurrentId() {
        int len = Config.staticGen ?
                // We have no way to know the length of an array if we
                // do static generation. Thus, we guess one.
                Driver.rand.nextInt(1, 1001) :
                Array.getLength(id.evaluate());
        if (len == 0) {
            // We force an index 0 when id is an empty array. After
            // all, we just want to fill it somehow so an
            // IndexOutOfBoundException can be thrown when the
            // generated program is executed.
            // TODO: but what is the expected thing to do?
            index = new ImIntVal(0);
            return;
        }
        index = new IntVal(0, len);
    }

    @Override
    public void updateVal(E val) {
        A arr = id.evaluate();
        int i = index.evaluate();
        Array.set(arr, i, val);
        Data.addToMemory(id.getJavaStr(), arr);
    }
}
