package sketchy.ast.exp;

import sketchy.ast.visitor.Visitor;

import java.util.List;

public class RefId<T> extends IdExp<T> {

    private final Class<T> type;

    public RefId(Class<T> type, List<String> ids) {
        this(type, false, ids);
    }

    public RefId(Class<T> type, boolean exclude, List<String> ids) {
        super(exclude, ids);
        this.type = type;
    }

    @Override
    public Class<?> getIdType() {
        return type;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}
