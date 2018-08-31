package tech.wetech.mybatis.generator.formatter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;
import java.util.Properties;

/**
 * @author cjbi
 * @deprecated
 */
public class FastJsonPropertyFormatter extends FreemarkerTemplateFormatter {

    private static final String DEFAULT_TEMPLATE_CACHE_NAME = "myTemplateName";

    private JSON resolveJSON(JSON json, Map<String, Object> params) {
        if (json instanceof JSONArray) {
            ((JSONArray) json).forEach(o -> {
                resolveJSON((JSON) o, params);
            });
        } else if (json instanceof JSONObject) {
            ((JSONObject) json).forEach((k, v) -> {
                if (v instanceof String) {
                    ((JSONObject) json).put(k, evaluateMarker((String) v, params));
                } else if (v instanceof JSON) {
                    resolveJSON((JSON) v, params);
                }
            });
        }
        return json;
    }

    private String evaluateMarker(String source, Map<String, Object> params) {
        try {
            return process(DEFAULT_TEMPLATE_CACHE_NAME, source, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return source;
    }

    @Override
    public <T> T resolveProperty(T object, Map<String, Object> params) throws Exception {
        if (object instanceof String) {
            return (T) process(DEFAULT_TEMPLATE_CACHE_NAME, object.toString(), params);
        }
        JSON json = (JSON) JSON.toJSON(object);
        resolveJSON(json, params);
        return (T) JSON.toJavaObject(json, object.getClass());
    }
}
