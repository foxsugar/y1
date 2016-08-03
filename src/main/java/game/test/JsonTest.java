package game.test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.googlecode.protobuf.format.JavaPropsFormat;
import com.googlecode.protobuf.format.JsonFormat;
import game.utils.JsonUtil;

import java.io.IOException;

/**
 * Created by sun on 2015/8/25.
 */
public class JsonTest {
    public static void test1() throws IOException {
        Holder holder = new Holder();
        Item item = new Item();
        Gift gift = new Gift();
        gift.giftId = "1";
        gift.id = 1;
        gift.name = "name";

        Gold gold = new Gold();

        holder.item = gold;

        String s = JsonUtil.mapper.writeValueAsString(holder);

        System.out.println(s);

        Holder hh = JsonUtil.mapper.readValue(s,Holder.class);


        System.out.println((hh.item.f()));




    }
    public static void main(String[] args) throws IOException {
        test1();
    }
}


@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
class Item{
    public int id ;
    public String name;
    public String f (){
        return "item";
    }
}

class Gift extends Item{
    public String giftId;
    public String f() {
        return "gift";
    }
}

class Gold extends Item{
    public String goldNum;
    public String f(){
        return "gold";
    }
}

class Holder{
    public Item item;
}