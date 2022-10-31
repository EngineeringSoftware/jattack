package sketchy.compiler;

import java.lang.instrument.Instrumentation;

public class Agent {

    private static Instrumentation sInst;

    public static Instrumentation getInst() {
        return sInst;
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        // System.out.println("Invoking premain method.");
        sInst = inst;
        StaticInitCopyTransformer t = new StaticInitCopyTransformer();
        inst.addTransformer(t);
    }
}
