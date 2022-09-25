package sketchy.bytecode;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * Util class to transform bytecode (to get local variables) and to
 * get fields.
 */
public class VariableAnalyzer {

    public static byte[] transformBytecode(String classBinName, byte[] classfile) {
        ClassReader cr;
        ClassWriter cw;
        try {
            // Pass 1: Get local variables of each method
            cr = new ClassReader(classfile);
            cw = new ClassWriter(cr, 0); // readonly ClassWriter
            cr.accept(new LocalVariableTableClassVisitor(cw, classBinName), 0);

            // Pass 2: insert instructions to update memory before
            // each eval()
            cr = new ClassReader(cw.toByteArray());
            cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            cr.accept(new SaveLocalVarValuesClassVisitor(cw, classBinName), 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // DEBUGGING
        // try {
        //     IOUtil.saveBytecodeToFile(cw.toByteArray(), classBinName);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        return cw.toByteArray();
    }

    public static void staticAnalyze(String classBinName, byte[] classfile) {
        ClassReader cr;
        ClassWriter cw;
        try {
            cr = new ClassReader(classfile);
            cw = new ClassWriter(cr, 0); // readonly ClassWriter
            cr.accept(new StaticGenClassVisitor(cw, classBinName), 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
