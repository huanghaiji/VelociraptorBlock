package velociraptor.so.top.role;

import velociraptor.block.BlockAction;
import velociraptor.block.target.BlockBusiness;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockUserActionConcurrentHashMap implements BlockUserActionMap {

    private final Map<String, BlockAction<BlockUser>> map = new ConcurrentHashMap<>();

    @Override
    public void pushAction(BlockAction<BlockUser> action) {
        Class<?> clz = action.getClass();
        BlockBusiness target =  clz.getAnnotation(BlockBusiness.class);
        String[] values = target.value();
        for (String value : values)
            map.put(value, action);
    }

    @Override
    public void addAction(String k, BlockAction<BlockUser> v) {
        this.map.put(k, v);
    }

    @Override
    public BlockAction<BlockUser> get(String key) {
        return this.map.get(key);
    }

    @Override
    public void removeAction(String k) {
        map.remove(k);
    }

}
