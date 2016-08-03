package game.module;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import game.utils.JsonUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sun on 2015/8/27.
 *
 */
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY)
public class Equip  {


    private Map<Integer,String> info;
    private int id = 1;



    public Equip() {
        info = new HashMap<>();
        info.put(1,"1");
        info.put(2,"2");
    }

    public String convert2Json() {
        String result = "";
        try {
            result =  JsonUtil.mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void parseFromJson(Player player , String json) throws IOException {
        if(json == null || "".equals(json)){
            return;
        }
        player.setEquip(JsonUtil.mapper.readValue(json, this.getClass()));
    }


    public static void main(String[] args) {
        Equip equip = new Equip();
        equip.info = new HashMap<>();
        equip.info.put(1,"1");
        String result = null;
        try {
            result = JsonUtil.mapper.writeValueAsString(equip);
            result = JsonUtil.mapper.writeValueAsString(equip.info);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }


}
