package game.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import game.utils.JsonUtil;

/**
 * Created by sun on 2015/8/28.
 */
public class Pet  {

    private int id;
    public String convert2Json() {
        String s = "";
        try {
            s =  JsonUtil.mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return s;
    }


    public void parseFromJson(String json) {

    }
}
