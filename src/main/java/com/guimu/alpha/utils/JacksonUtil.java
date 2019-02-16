package com.guimu.alpha.utils;

/**
 * Description:
 * Created by guimu on 2018/3/23 下午2:41
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class JacksonUtil {
    public static final ObjectMapper mapper = new ObjectMapper();

    public static String getString(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    public static <T> T getObject(String value, Class<T> t) throws IOException {
        return mapper.readValue(value, t);
    }
}
