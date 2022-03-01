import velociraptor.protocol.BlockData;
import velociraptor.protocol.BlockDataContext;
import velociraptor.scheduling.io.*;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QueueByteOutputStreamTest {


    @Test
    public void test() throws IOException {
        ObjectListOutputStream<byte[],Byte> mom = new QueueByteOutputStream<byte[],Byte>().instanceArrayList();

        mom.write("abc".getBytes());
        mom.write("xoy".getBytes());
        System.out.println(new String(mom.get(mom.find(0, mom.size()))));

        mom.del(mom.find(2, 2));
        System.out.println(new String(mom.get(mom.find(0, mom.size()))));

        mom.write("cd,d".getBytes());
        System.out.println(new String(mom.get(mom.find(0, mom.size()))));

        System.out.println(mom.indexOf(1, (byte)','));
    }

    @Test
    public void testTypeCheck(){
        Byte a='a';
        byte b='a';
        System.out.println(a.equals(b));
    }

    @Test
    public void testStringBlock() throws IOException {

        BlockDataContext context = new BlockDataContext();
        ByteArrayOutputStream output =new ByteArrayOutputStream();

        BlockData block = new BlockData();
        block.setId( 1000);
        block.setTarget("24");
        block.setCharId("AB");
        block.setDataUTF8("Only");
        output.write( context.write().full(block));

        block.setId( 2000);
        block.setTarget( "55");
        block.setCharId("CD");
        block.setDataUTF8("Admin");
        output.write( context.write().full(block));

        context.reader().push(output.toByteArray());
        while ((block = (BlockData) context.reader().readBlock())!=null) {
            System.out.println("id:"+ block.getId()+"-"+block.getCharId());
            System.out.println("target:"+block.getTarget());
            System.out.println("data:" + block.getDataUTF8());
            System.out.println("---------------------------------------------");
        }

    }

}
