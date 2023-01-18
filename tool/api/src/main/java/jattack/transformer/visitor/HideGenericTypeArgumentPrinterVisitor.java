package jattack.transformer.visitor;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.printer.DefaultPrettyPrinterVisitor;
import com.github.javaparser.printer.configuration.PrinterConfiguration;

public class HideGenericTypeArgumentPrinterVisitor extends DefaultPrettyPrinterVisitor {

    public HideGenericTypeArgumentPrinterVisitor(PrinterConfiguration conf) {
        super(conf);
    }

    @Override
    public void visit(ClassOrInterfaceType n, Void arg) {
        if (n.getScope().isPresent()) {
            n.getScope().get().accept(this, arg);
            printer.print(".");
        }
        printAnnotations(n.getAnnotations(), false, arg);

        n.getName().accept(this, arg);
    }
}
