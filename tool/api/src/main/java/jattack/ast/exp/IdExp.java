package jattack.ast.exp;

import jattack.ast.exp.iterator.LitItr;
import jattack.ast.nodetypes.NodeWithSideEffect;
import jattack.ast.nodetypes.TerminalNode;
import jattack.data.Data;
import jattack.driver.Driver;
import jattack.util.UniqueList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class IdExp<T> extends LHSExp<T>
        implements NodeWithSideEffect<T>, TerminalNode<T> {

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
        Data.addToMemory(id, val);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getVal() {
        return (T) Data.getFromMemoryValueOfVar(id);
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

    public abstract Class<?> getIdType();

    private UniqueList<String> ids() {
        if (ids == null) {
            // infer identifiers
            Set<String> availableIds = Data.getVarsOfType(getIdType());
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
