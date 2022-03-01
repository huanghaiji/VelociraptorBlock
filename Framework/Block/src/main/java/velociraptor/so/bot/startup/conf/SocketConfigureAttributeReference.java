package velociraptor.so.bot.startup.conf;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 这是一个在FRA架构中，非常有意思的话题。
 * 当多个不同的FRA模块的配置之间彼此不继承，又不具有动态代理技术可buff时，那么如何使其当前程序所需要的配置从配置对象中获得。
 * 是个非常有趣的技术话题。
 *
 */
public class SocketConfigureAttributeReference {

    /**
     * 当配置对象不是同一个类型时，由于无法转换类型，获得属性，因此，需要创建该对象实例，并且加载配置对象同名同类型的同属性值。忽略大小写。
     */
    public static   <T> T onBuild(T dest, Object conf){
        Class<?> clz = conf.getClass();
        Class<?> destClz =  dest.getClass();

        Map<String,Field> map = new HashMap<>();
        for(Field field: clz.getFields())
            map.put(field.getName().toLowerCase(),field);

        for ( Field field : destClz.getFields()){
            String name = field.getName();
            Field confField =  map.getOrDefault( name.toLowerCase(), null);
            try {
                if (confField!=null)
                    field.set( dest, confField.get(conf));
            }catch (Throwable e){
                e.printStackTrace();
            }
        }
        return dest;
    }

}