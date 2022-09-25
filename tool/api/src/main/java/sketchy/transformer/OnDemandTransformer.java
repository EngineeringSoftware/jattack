package sketchy.transformer;

import com.github.javaparser.ast.CompilationUnit;
import sketchy.transformer.visitor.DeadCodeEliminator;
import sketchy.transformer.visitor.HoleFiller;

/**
 * Transform sketches in hot filling.
 */
public class OnDemandTransformer extends Transformer {

    /**
     * Flag if we perform dead code removal for all the always true or
     * false condition holes.
     */
    private final boolean deadCodeRemoval;

    /**
     * Flag if we fill holes.
     */
    private final boolean hotFilling;

    /**
     * Constructor.
     */
    public OnDemandTransformer(CompilationUnit origCu, boolean hotFilling, boolean deadCodeRemoval) {
        super(origCu);
        this.hotFilling = hotFilling;
        this.deadCodeRemoval = deadCodeRemoval;
    }

    /**
     * Transforms part of this sketch. This is incremental
     * transformation, which means we starts with the previously
     * transformed {@link Transformer#cu}.
     */
    @Override
    public void transform() {
        if (deadCodeRemoval) {
            // Remove dead code resulting from always true or false
            // condition holes.
            cu.accept(new DeadCodeEliminator(), null);
        }

        if (hotFilling) {
            cu.accept(new HoleFiller(), null);
        }
    }

    /**
     * Resets cu to a clone of origCu. This should be invoked before
     * a new run of the sketch.
     */
    public void resetCu() {
        cu = origCu.clone();
    }
}
