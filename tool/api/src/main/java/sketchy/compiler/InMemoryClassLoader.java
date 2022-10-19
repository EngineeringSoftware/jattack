package sketchy.compiler;

public class InMemoryClassLoader extends ClassLoader {

    private final ClassBytes classBytes;

    public InMemoryClassLoader(ClassBytes bytesByClass, ClassLoader parent) {
        super(parent);
        this.classBytes = bytesByClass;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = classBytes.getBytesByClass().get(name);
        if (bytes != null) {
            return defineClass(name, bytes, 0, bytes.length);
        }
        return super.findClass(name);
    }
}
