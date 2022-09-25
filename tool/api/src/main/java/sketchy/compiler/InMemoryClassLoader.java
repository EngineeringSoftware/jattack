package sketchy.compiler;

import sketchy.util.TypeUtil;

/**
 * In-memory class loader.
 */
class InMemoryClassLoader extends ClassLoader {

    private final ClassBytes classBytes;

    public InMemoryClassLoader(ClassBytes bytesByClass, ClassLoader parent) {
        super(parent);
        this.classBytes = bytesByClass;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        String pkgName = TypeUtil.getPackageName(name);
        byte[] bytes = classBytes.get(name);
        if (bytes != null) {
                return super.defineClass(name, bytes, 0, bytes.length);
        } else {
            return super.loadClass(name);
        }
    }

    // TODO: (re)load classes with package-private access.

    // Package-private access prevents a different class loader from
    // accessing the class. In other words, when a reloaded class A in
    // this class loader accesses another class B in the same package
    // but loaded by parent classloader, which is a different class
    // loader from this one, an IllegalAccessError would occur. In
    // order to fix this, we have to reload again every class in the
    // same package with A in this class loader. However, this brought
    // more issues due to multiple-loading of a same class. Thus we
    // ignore the IlleglAccessError for now since it is not very
    // common to see.
    /*
      if (classBytes.getPackageNames().contains(pkgName)) {
          String resourcePath = TypeUtil.bin2Intern(name) + ".class";
          try {
              bytes = super.getResourceAsStream(resourcePath).readAllBytes();
          } catch (IOException e) {
              throw new RuntimeException(e);
          }
          return super.defineClass(name, bytes, 0, bytes.length);
      }
    */
}
