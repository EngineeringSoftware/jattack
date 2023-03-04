package jattack.ast.exp;

import jattack.ast.exp.iterator.LitItr;
import jattack.ast.nodetypes.NodeWithType;
import jattack.ast.nodetypes.TerminalNode;
import jattack.data.Data;
import jattack.driver.Driver;
import jattack.util.UniqueList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A base class for all identifier expressions.
 */
public abstract class IdExp<T> extends LHSExp<T>
        implements TerminalNode<T>, NodeWithType<T> {

    private UniqueList<String> ids;

    private final Set<String> excludedIds;

    private String id;

    public IdExp(List<String> ids) {
        this(false, ids);
    }

    public IdExp(boolean exclude, List<String> ids) {
        if (exclude) {
            this.ids = null;
            this.excludedIds = new HashSet<>(ids);
        } else {
            this.ids = ids.isEmpty() ? null : new UniqueList<>(ids);
            this.excludedIds = new HashSet<>();
        }
    }

    @Override
    public void updateVal(T val) {
        Data.updateMemory(id, val);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getVal() {
        return (T) Data.getFromMemoryValueOfSymbol(id);
    }

    @Override
    public String asStr() {
        return id;
    }

    @Override
    protected void setItr() {
        itr = new LitItr<>(ids()) {
            @Override
            public void next() {
                super.next();
                setId(iterator.next());
            }
        };
    }

    @Override
    public void stepRand() {
        setId(ids().pick(Driver.rand));
    }

    @Override
    public boolean hasRandChoice() {
        return !ids().isEmpty();
    }

    private UniqueList<String> ids() {
        if (ids == null) {
            // infer identifiers
            Set<String> availableIds = Data.getSymbolsOfType(getType());
            for (String id : excludedIds) {
                availableIds.remove(id);
            }
            ids = new UniqueList<>(availableIds);
        }
        return ids;
    }

    private void setId(String id) {
        this.id = id;
    }
}
