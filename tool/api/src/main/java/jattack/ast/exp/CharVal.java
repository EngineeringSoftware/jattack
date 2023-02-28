package jattack.ast.exp;

import jattack.ast.exp.iterator.LitItr;
import jattack.ast.visitor.Visitor;
import jattack.driver.Driver;
import jattack.util.UniqueList;

import java.util.List;

/**
 * Char literal expression.
 */
public class CharVal extends LitExp<Character> {

    private final UniqueList<Character> vals;

    /**
     * Constructor only for random exploration.
     */
    public CharVal() {
        this.vals = null;
    }

    /**
     * Constructor only for systematic exploration.
     */
    public CharVal(List<Character> vals) {
        if (vals.isEmpty()) {
            throw new RuntimeException("No values that can be used!");
        }
        this.vals = new UniqueList<>(vals);
        // We leave initialization of val done in eval(). val will not
        // be initialized until next() or step() is called.
    }

    @Override
    public String asStr() {
        // Needs to escape single quote and backslash
        return "'" + (val == '\'' || val == '\\' ? ("\\" + val) : val) + "'";
    }

    @Override
    protected void setItr() {
        itr = new LitItr<>(vals) {
            @Override
            public void next() {
                super.next();
                val = iterator.next();
            }
        };
    }

    @Override
    public void stepRand() {
        val = Driver.rand.nextChar();
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}
