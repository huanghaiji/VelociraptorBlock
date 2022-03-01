package velociraptor.ClassRoute;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassPathSearching extends ArrayList<Class<?>> {

  private final ClassSearching opinion;

  public ClassPathSearching(ClassSearching opinion) {
    this.opinion = opinion;
  }

  public ClassPathSearching() {
    opinion = cls -> true;
  }

  /**
   * 从包package中获取所有的Class
   */
  public void addClassURL(String packageName) {
    String packageDirName = packageName.replace('.', '/');
    Enumeration<URL> dirs;
    try {
      dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
      while (dirs.hasMoreElements()) {
        URL url = dirs.nextElement();
        String protocol = url.getProtocol();
        switch (protocol) {
          case "file" -> findAddClassesInPackageByFile(packageName, url.getFile());//URLDecoder.decode(url.getFile(), "UTF-8")
          case "jar" -> findAddClassesInPackageByJar(packageDirName, packageName, url);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void addClassURL(Class<?> cls) {
    addClassURL(cls.getPackage().getName());
  }

  public Object instanceClass(int index) {
    try {
      Class<?> cls = get(index);
      return cls == null ? null : cls.getConstructor().newInstance();
    } catch (Throwable  e) {
      e.printStackTrace();
    }
    return null;
  }


  public Object instanceClass(Class<?> cls) {
    try {
      return cls.getConstructor().newInstance();
    } catch (Throwable  e) {
      e.printStackTrace();
    }
    return null;
  }

  private Class<?> loaderClass(String className) {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }


  private void findAddClassesInPackageByFile(String packageName, String packagePath) {
    File dir = new File(packagePath);
    if (dir.exists() && dir.isDirectory()) {
      File[] files = dir.listFiles(file -> (file.isDirectory()) || file.getName().endsWith(".class"));
      if (files != null) for (File file : files) {
        if (file.isDirectory()) {
          findAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath());
        } else {
          String className = file.getName().substring(0, file.getName().length() - 6);
          Class<?> cls = loaderClass(packageName + '.' + className);
          if (opinion.allowedAppend(cls)) this.add(cls);
        }
      }
    }
  }

  private void findAddClassesInPackageByJar(String packageDirName, String packageName, URL url) {
    JarFile jar;
    try {
      jar = ((JarURLConnection) url.openConnection()).getJarFile();
      Enumeration<JarEntry> entries = jar.entries();
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        String name = entry.getName();
        name = name.charAt(0) == '/' ? name.substring(1) : name;
        if (name.startsWith(packageDirName)) {
          int idx = name.lastIndexOf('/');
          packageName = idx != -1 ? name.substring(0, idx).replace('/', '.') : packageName;
          if (name.endsWith(".class") && !entry.isDirectory()) {
            String className = name.substring(packageName.length() + 1, name.length() - 6);
            Class<?> cls = loaderClass(packageName + '.' + className);
            if (opinion.allowedAppend(cls)) this.add(cls);
          }
        }
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

}