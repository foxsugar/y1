package test;

import com.googlecode.protobuf.format.JsonFormat;

/**
 * Created by SunXianping on 2016/8/11 0011.
 */
public class TestProtobuf {
    public static void main(String[] args) {
        //tel1
        Test.Tel.Builder tel = Test.Tel.newBuilder();
        tel.setNum("13888888888");
        //tel2
        Test.Tel tel2 = Test.Tel.newBuilder().setNum("13999999999").build();

        //user
        Test.User.Builder userBuilder = Test.User.newBuilder();
        userBuilder.setName("hello");
        userBuilder.setId(Test.MessageId.Login);
        userBuilder.setF(3.14F);
        userBuilder.setIsTrue(true);
        userBuilder.addTel(tel);
        userBuilder.addTel(tel2);

        //转成json
        String json = JsonFormat.printToString(userBuilder.build());
        System.out.println(json);

        //json转对象
        Test.User.Builder user2 = Test.User.newBuilder();
        try {
            JsonFormat.merge(json, user2);
            System.out.println("===================================");
            System.out.println(user2.toString());
        } catch (JsonFormat.ParseException e) {
            e.printStackTrace();
        }

    }
}
