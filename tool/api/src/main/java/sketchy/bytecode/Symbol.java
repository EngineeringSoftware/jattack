package sketchy.bytecode;

import java.util.Objects;

/**
 * A field or local variable, wrapping name and type together.
 */
public class Symbol {

    private final String name;
    private final String desc;

    public Symbol(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return Objects.equals(name, symbol.name) && Objects.equals(desc, symbol.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, desc);
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
