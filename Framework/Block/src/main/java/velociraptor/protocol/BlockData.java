package velociraptor.protocol;

import velociraptor.block.Block;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class BlockData implements Block {

     String charId;
     long id;
     String target;
     byte[] data;
     int size;

     public BlockData(){}

    public BlockData(long id,String charId,String target){
         this.setBasicParams( id, charId, target);
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void setCharId(String charId) {
        this.charId = charId;
    }

    @Override
    public String getCharId() {
        return this.charId;
    }

    @Override
    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public void setBasicParams(long id, String charId, String target) {
        this.setId( id);
        this.setCharId( charId);
        this.setTarget( target);
    }

    @Override
    public void setData(byte[] data) {
         if (data!=null){
             this.data = data;
             this.size = data.length;
         }else {
             this.size=0;
         }
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "id:"+ id+" char id:"+charId+" target:"+target+" size:"+size;
    }
}
