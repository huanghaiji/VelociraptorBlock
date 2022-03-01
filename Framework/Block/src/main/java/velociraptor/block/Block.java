package velociraptor.block;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public interface Block {

    void setId(long id);

    long getId();

    void setCharId(String charId);

    String getCharId();

    void setTarget(String target);

    String getTarget();

    void setBasicParams(long id,String charId,String target);

    void setData(byte[] data);

    byte[] getData();

    default void setDataUTF8(String str){
        this.setData(str.getBytes(StandardCharsets.UTF_8));
    }

    default String getDataUTF8(){
        byte[] bytes = getData();
        return bytes==null? null: new String( bytes,StandardCharsets.UTF_8);
    }

    default void setData(String str,String charset){
        try {
            this.setData( str.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    default   String getData(String charset){
        try {
            byte[] bytes = getData();
            return bytes==null? null: new String( bytes, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    default Block copyId(Block block) {
        block.setId(getId());
        block.setCharId(getCharId());
        return block;
    }

    default String key() {
        return getCharId() + getId();
    }

}
