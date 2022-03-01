package velociraptor.ClassRoute;

public class ClassAssignableFromSearching {

    /**
     * 检索过程中，对于"xxx$yyy"的类，同样也会被检索到。
     */
    public ClassPathSearching searching(final Class<?> dir, final Class<?> p) {
        ClassPathSearching searching = new ClassPathSearching((cls) -> cls != p && p.isAssignableFrom(cls));
        searching.addClassURL(dir);
        return searching;
    }


    /**
     * 检索过程中，排除符合"xxx$yyy"的类。
     */
    public ClassPathSearching searchingExclude$(final Class<?> dir, final Class<?> p){
        ClassPathSearching searching = new ClassPathSearching((cls)-> cls!=p && p.isAssignableFrom(cls) && !cls.getName().contains("$"));
        searching.addClassURL(dir);
        return searching;
    }

    /**
     * 检索过程中，对于"xxx$yyy"的类，同样也会被检索到。
     */
    public ClassPathSearching searching(final String dir, final Class<?> p) {
        ClassPathSearching searching = new ClassPathSearching((cls) -> cls != p && p.isAssignableFrom(cls));
        searching.addClassURL(dir);
        return searching;
    }


    /**
     * 检索过程中，排除符合"xxx$yyy"的类。
     */
    public ClassPathSearching searchingExclude$(final String dir, final Class<?> p){
        ClassPathSearching searching = new ClassPathSearching((cls)-> cls!=p && p.isAssignableFrom(cls) && !cls.getName().contains("$"));
        searching.addClassURL(dir);
        return searching;
    }
}
