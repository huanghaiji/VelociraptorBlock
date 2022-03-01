package velociraptor.ClassRoute;

/**
 * 判断类是否被特定注解。
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ClassAnnotationSearching {


  public ClassPathSearching searching(final Class dir, final Class annotation) {
    ClassPathSearching lis = new ClassPathSearching(cls -> cls.getAnnotation(annotation) != null);
    lis.addClassURL(dir);
    return lis;
  }

  public ClassPathSearching searching(final Class dir, final Class<?>... annotations) {
    ClassPathSearching lis = new ClassPathSearching(cls -> {
      for (Class annotation : annotations) {
        if (cls.getAnnotation(annotation) != null)
          return true;
      }
      return false;
    });
    lis.addClassURL(dir);
    return lis;
  }

  public ClassPathSearching searching(final String dir, final Class annotation) {
    ClassPathSearching lis = new ClassPathSearching(cls -> cls.getAnnotation(annotation) != null);
    lis.addClassURL(dir);
    return lis;
  }

  public  ClassPathSearching searching(final String dir, final Class... annotations) {
    ClassPathSearching lis = new ClassPathSearching(cls -> {
      for (Class annotation : annotations) {
        if (cls.getAnnotation(annotation) != null)
          return true;
      }
      return false;
    });
    lis.addClassURL(dir);
    return lis;
  }

}
