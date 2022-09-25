package sketchy.ast.exp;

import sketchy.ast.exp.iterator.ChainItr;
import sketchy.ast.exp.iterator.ExpItr;
import sketchy.ast.exp.iterator.Itr;
import sketchy.ast.exp.iterator.LitItr;
import sketchy.ast.exp.iterator.RangeItr;
import sketchy.ast.nodetypes.TerminalNode;
import sketchy.ast.visitor.Visitor;
import sketchy.driver.Driver;
import sketchy.util.UniqueList;

import java.util.ArrayList;
import java.util.List;

public class IntArrVal extends Exp<int[]> implements TerminalNode<int[]> {

    private UniqueList<Integer> lenChoices;
    private UniqueList<Integer> elemChoices;

    private int[] val;
    private int len;

    /* Bounds. */
    private final boolean isLenBounded;
    private int lenLow;
    private int lenHigh;
    private final boolean isElemBounded;
    private int elemLow;
    private int elemHigh;

    /**
     * Constructor only for random exploration.
     */
    public IntArrVal() {
        this.len = -1;
        this.isLenBounded = false;
        this.isElemBounded = false;
    }

    /**
     * Constructor only for systematic exploration.
     */
    public IntArrVal(List<Integer> lenChoices, List<Integer> elemChoices) {
        this.len = -1;
        this.isLenBounded = false;
        this.isElemBounded = false;
        this.lenChoices = new UniqueList<>(lenChoices);
        this.elemChoices = new UniqueList<>(elemChoices);
    }

    /**
     * Constructor with len bounded and elem unbounded only for
     * systematic exploration.
     */
    public IntArrVal(int lenLow, int lenHigh, List<Integer> elemChoices) {
        checkLenBounds(lenLow, lenHigh);
        this.isLenBounded = true;
        this.isElemBounded = false;
        this.len = -1;
        this.lenLow = lenLow;
        this.lenHigh = lenHigh;
        this.elemChoices = new UniqueList<>(elemChoices);
    }

    /**
     * Constructor with len bounded and elem unbounded.
     */
    public IntArrVal(int lenLow, int lenHigh) {
        checkLenBounds(lenLow, lenHigh);
        this.isLenBounded = true;
        this.isElemBounded = false;
        this.len = -1;
        this.lenLow = lenLow;
        this.lenHigh = lenHigh;
    }

    /**
     * Constructor with both len and elem bounded.
     */
    public IntArrVal(int lenLow, int lenHigh, int elemLow, int elemHigh) {
        checkLenBounds(lenLow, lenHigh);
        checkElemBounds(elemLow, elemHigh);
        this.isLenBounded = true;
        this.isElemBounded = true;
        this.len = -1;
        this.lenLow = lenLow;
        this.lenHigh = lenHigh;
        this.elemLow = elemLow;
        this.elemHigh = elemHigh;
    }

    @Override
    public int[] getVal() {
        return val;
    }

    @Override
    public String asStr() {
        if (val == null)
            return "null";
        int iMax = val.length - 1;
        if (iMax == -1)
            return "new int[]{}";

        StringBuilder b = new StringBuilder();
        b.append("new int[]{");
        for (int i = 0; ; i++) {
            b.append(val[i]);
            if (i == iMax)
                return b.append('}').toString();
            b.append(", ");
        }
    }

    @Override
    protected void setItr() {
        itr = new ExpItr() {

            private final Itr lenItr = isLenBounded ?
                    new RangeItr(lenLow, lenHigh) {
                        @Override
                        public void next() {
                            super.next();
                            len = (int) getCurrent(); } } :
                    new LitItr<>(lenChoices) {
                        @Override
                        public void next() {
                            super.next();
                            len = iterator.next(); } };
            private ChainItr elemItrs;

            @Override
            public void next() {
                super.next();
                // iteration order: len then elem, low index first
                if (lenItr.isReset() || elemItrs == null || !elemItrs.hasNext()) {
                    // Advance lenItr
                    lenItr.next();

                    // Initialize val
                    val = new int[len];

                    // Initialize elemItrs with len
                    List<Itr> elemItrList = new ArrayList<>(len);
                    for (int i = 0; i < len; i++) {
                        int elemIndex = i;
                        Itr elemItr = isElemBounded ?
                                new RangeItr(elemLow, elemHigh) {
                                    @Override
                                    public void next() {
                                        super.next();
                                        val[elemIndex] = (int) getCurrent(); } } :
                                new LitItr<>(elemChoices) {
                                    @Override
                                    public void next() {
                                        super.next();
                                        val[elemIndex] = iterator.next(); } };
                        elemItrList.add(elemItr);
                    }
                    elemItrs = len == 0 ? null : new ChainItr(elemItrList);
                }

                if (elemItrs != null) {
                    // Advance elemItrs
                    elemItrs.next();
                }
            }

            @Override
            public boolean hasNext() {
                if (isReset()) {
                    return lenItr.hasNext();
                }
                return lenItr.hasNext() || (elemItrs != null && elemItrs.hasNext());
            }

            @Override
            public void reset() {
                super.reset();
                lenItr.reset();
                elemItrs = null;
            }
        };
    }

    @Override
    public void stepRand() {
        len = isLenBounded ?
                Driver.rand.nextInt(lenLow, lenHigh) :
                Driver.rand.nextNonNegativeInt();
        val = new int[len];
        for (int i = 0; i < len; i++) {
            val[i] = isElemBounded ?
                    Driver.rand.nextInt(elemLow, elemHigh) :
                    Driver.rand.nextInt();
        }
    }

    @Override
    public boolean hasRandChoice() {
        return true;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }

    private static void checkLenBounds(int lenLow, int lenHigh) {
        if (lenLow < 0) {
            throw new IllegalArgumentException("Bounds of len must be non-negative!");
        }
        if (lenLow >= lenHigh) {
            throw new IllegalArgumentException("low must be less than high!");
        }
    }

    private static void checkElemBounds(int elemLow, int elemHigh) {
        if (elemLow >= elemHigh) {
            throw new IllegalArgumentException("low must be less than high!");
        }
    }
}
