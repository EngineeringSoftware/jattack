package jattack.ast;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import jattack.Config;
import jattack.ast.exp.LogExp;
import jattack.ast.exp.RelExp;
import jattack.ast.exp.iterator.Itr;
import jattack.ast.visitor.PrintVisitor;
import jattack.ast.visitor.Visitable;
import jattack.ast.visitor.Z3ExprBuilder;
import jattack.data.Data;
import jattack.driver.Driver;
import jattack.exception.InvokedFromNotDriverException;
import jattack.log.Log;
import jattack.util.Z3Util;

/**
 * Abstract class for all nodes.
 * @param <T> the type of value evaluated from this node, which could
 *        be {@link Void} if the node should not return any value,
 *        e.g., a statement.
 */
public abstract class Node<T> implements Visitable {

    protected Itr itr;

    /**
     * The unique identifier for this node. -1 means unset.
     */
    private int identifier = -1;

    /**
     * Fake, never be executed.
     */
    @SuppressWarnings("unchecked")
    public final T eval() {
        return (T) new Object();
    }

    /**
     * Before we really evaluate, we need to check if we have seen
     * this hole (only an AST node as hole will have eval() invoked)
     * in the current run.
     * 1) If we did see this hole before, we just get the stored ast
     *    for this hole from cache and evaluate it.
     * 2) Otherwise we still get the stored ast from cache, but we
     *    then have to explore the next status of the ast before
     *    evaluating.
     */
    @SuppressWarnings("unchecked")
    public final T eval(int holeId) {
        // We allow this to be invoked form only the Driver, so we can
        // know when unfilled holes are wrongly executed from
        // generated programs.
        if (!Driver.isDriven) {
            throw new InvokedFromNotDriverException();
        }

        // Set the unique identifier for this hole.
        if (identifier == -1) {
            identifier = holeId;
        }

        Node<T> ast;
        // Check if we have generated for this hole in the current
        // run. If so we find and reuse the ast for this hole from
        // cache.
        if (Data.isTheHoleFilled(identifier)) {
            ast = (Node<T>) Data.getASTOfHole(identifier);
        } else {
            // Otherwise this is the first time in the current run we see
            // this hole so we probably (depending on the search strategy)
            // explore next for the ast.
            // Be careful when the current run is the first time we reach
            // this hole.
            ast = (Node<T>) Data.addToASTCacheIfAbsent(identifier, this);
            // addToASTCacheIfAbsent() returns null when the key does not exist,
            // which means we this hole is reached the first time
            if (ast == null) {
                ast = this;
            }

            // Decide what is the next choice according to the given
            // search strategy
            ast.exploreNext();

            // Save the string representation of the ast node in cache
            ast.saveJavaStrInCache();

            if (Config.optSolverAid
                    && Data.isTheHoleACondition(identifier)) {
                // Invoke Z3 Solver to check if the condition is valid
                // or unsat, if so then document this hole
                ast.documentHoleId();
            }
        }

        // Do evaluation
        T val = ast.evaluate();

        // Dynamic collection
        if (Config.dynamicCollecting
                && (ast instanceof RelExp || ast instanceof LogExp)) {
            Data.collectCondVals(ast.identifier, (boolean) val);
        }
        if (Config.saveHoleValues
                && (ast instanceof RelExp || ast instanceof LogExp)) {
            Data.saveCondVals(ast.identifier, (boolean) val);
        }
        return val;
    }

    /**
     * Static "evaluation". Just step randomly and save string
     * representation of the ast node.
     */
    public final void staticEval(int holedId) {
        identifier = holedId;
        Data.setCurrHoleId(holedId);
        stepRand();
        saveJavaStrInCache();
    }

    /**
     * Explore next choice according to the given search strategy.
     */
    private void exploreNext() {
        switch (Config.ss) {
        case SYSTEMATIC: {
            // Explore next systematic choice considering all the
            // holes
            int holeIdx = Data.holeVector.size();
            Data.holeVector.add(identifier);
            if (holeIdx >= Data.firstHoleIdxThatShouldStepInNextRun) {
                // If this hole should go next, then step next
                // (including next after a reset).
                stepSys();
            }
            // If not, keep original
            break;
        }
        case RANDOM: {
            // Explore next random choice for this hole independently
            stepRand();
            break;
        }
        case SMART: {
            // Explore next systematic choice for this hole
            // independently
            stepSys();
            break;
        }
        default:
            throw new RuntimeException("Unrecognized search strategy: "
                    + Config.ss + "!");
        }
    }

    /**
     * Step to next systematic choice.
     */
    private void stepSys() {
        if (!itr().hasNext()) {
            itr().reset();
        }
        itr().next();
        if (!itr().hasNext()) {
            // Have explored all the possibilities
            Data.holesThatHaveExploredAll.add(identifier);
        }
    }

    /**
     * Return the iterator.
     */
    public final Itr itr() {
        if (itr == null) {
            setItr();
        }
        return itr;
    }

    /**
     * Set the iterator.
     */
    protected abstract void setItr();

    /**
     * Step to next random choice;
     */
    // TODO: maybe we can use a visitor
    public abstract void stepRand();

    /**
     * Returns if the exp has choices.
     */
    public final boolean hasChoice() {
        switch (Config.ss) {
        case SYSTEMATIC:
        case SMART:
            return itr().hasNext();
        case RANDOM:
            return hasRandChoice();
        default:
            throw new RuntimeException("Unrecognized search strategy: "
                    + Config.ss + "!");
        }
    }

    /**
     * Returns if the exp has choice under random search strategy.
     */
    // TODO: maybe we can use a visitor
    public abstract boolean hasRandChoice();

    protected abstract T evaluate();

    /**
     * Return the string representation of this ast node.
     */
    public final String getJavaStr() {
        PrintVisitor pv = new PrintVisitor();
        accept(pv);
        return pv.getResult();
    }

    /**
     * Save the string representation of the ast node generated in
     * cache.
     */
    private void saveJavaStrInCache() {
        Data.saveToStrCache(identifier, getJavaStr());
    }

    /**
     * Check satisfiability of this conditional expression
     */
    private void documentHoleId() {
        Z3Util.SatSatus status = checkSat();
        if (status == null) {
            // Z3 cannot build the corresponding expression.
            Log.debug("Z3 cannot build the expression " + getJavaStr());
            return;
        }
        switch (status) {
        case UNSATISFIABLE:
            Data.documentAlwaysTrueOrFalseHole(identifier, false);
            break;
        case VALID:
            Data.documentAlwaysTrueOrFalseHole(identifier, true);
            break;
        case CONTINGENT:
        case UNKNOWN:
            break;
        default:
            throw new RuntimeException("Unrecognized Status!");
        }
    }

    private Z3Util.SatSatus checkSat() {
        Z3ExprBuilder v = new Z3ExprBuilder();
        accept(v);
        Expr<? extends Sort> z3expr = v.getZ3Expr();
        return z3expr == null ?
                null :
                Z3Util.check((BoolExpr) z3expr, v.getContext());
    }
}
