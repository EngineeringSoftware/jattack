package sketchy.compiler;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * Class to represent a source .java file in memory.
 */
public class JavaSourceFromString extends SimpleJavaFileObject {
    private final String code;

    public JavaSourceFromString(String classname, String code) {
        super(URI.create("string:///" +
                        classname.replace('.','/') +
                        Kind.SOURCE.extension),
                Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
