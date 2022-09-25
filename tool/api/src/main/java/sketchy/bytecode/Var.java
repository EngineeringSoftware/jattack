package sketchy.bytecode;

import java.util.Objects;

/**
 * Class to represent a local variable, which has name, descriptor,
 * local variable index, start bytecode offset and end bytecode
 * offset.
 */
public class Var {

    private final String name;
    private final String desc;
    private final int index;
    private final int start; // inclusive
    private final int end; // exclusive

    public Var(String name, String desc, int index, int start, int end) {
        this.name = name;
        this.desc = desc;
        this.index = index;
        this.start = start;
        this.end = end;
    }

    public Var(Var other) {
        this(other.name, other.desc, other.index, other.start, other.end);
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getIndex() {
        return index;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public boolean isReachableAt(int offset) {
        return offset >= start && offset < end;
    }

    public boolean isArg() {
        return start == -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Var var = (Var) o;
        return index == var.index
                && start == var.start
                && end == var.end
                && Objects.equals(name, var.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, index, start, end);
    }

    @Override
    public String toString() {
        return "Var{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", index=" + index +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
