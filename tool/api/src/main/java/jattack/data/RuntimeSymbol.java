package jattack.data;

import java.util.Objects;

public final class RuntimeSymbol {
    private final String name;
    private final String desc;
    private Object value;

    RuntimeSymbol(String name, String desc, Object value) {
        this.name = name;
        this.desc = desc;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuntimeSymbol symbol = (RuntimeSymbol) o;
        return Objects.equals(name, symbol.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "RuntimeSymbol{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", value=" + value +
                '}';
    }
}