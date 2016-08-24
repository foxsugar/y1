package game.test;

import com.googlecode.protobuf.format.JsonFormat;
import game.protobuf.*;

/**
 * Created by SunXianping on 2016/8/11 0011.
 */
public class T {

    public static void main(String[] args) {

        PB.Result.Builder result = PB.Result.newBuilder().setFlag(true);
        PB.Result r = result.build();
        PB.S2CMessage s2c = PB.S2CMessage.newBuilder().setRegisterResult(result).setMessageId(PB.MessageId.C2S_REGISTER).build();
        //转成json
        String json = JsonFormat.printToString(s2c);
        System.out.println(json);


        //json转对象
        PB.S2CMessage.Builder o2 = PB.S2CMessage.newBuilder();
        try {
            JsonFormat.merge(json, o2);
            System.out.println(o2.getRegisterResult().getFlag());
            System.out.println(o2.toString());
        } catch (JsonFormat.ParseException e) {
            e.printStackTrace();
        }


    }
}
