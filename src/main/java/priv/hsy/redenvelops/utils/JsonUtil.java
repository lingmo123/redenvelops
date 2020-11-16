package priv.hsy.redenvelops.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    private static final ThreadLocal<ObjectMapper> OBJECT_MAPPER_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return objectMapper;
    });

    /**
     * 将实体类转化为JSON格式的字符串，解析异常则返回空值
     * @param object 实体类
     * @return JSON格式字符串
     */
    public static String getJsonStringOrNull(Object object) {
        try {
            return getJsonString(object);
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * 将JSON格式字符串转化为实体类，解析异常则返回空值
     * @param string JSON格式字符串
     * @param clazz 泛型类
     * @param <T> 泛型
     * @return 实体对象
     */
    public static <T> T getEntityOrNull(String string, Class<T> clazz) {
        try {
            return getEntity(string, clazz);
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * 将JSON格式字符串转化为实体类列表，解析异常则返回空值
     * @param string JSON格式字符串
     * @param clazz 泛型类
     * @param <T> 泛型
     * @return 实体对象列表
     */
    public static <T> List<T> getEntityListOrNull(String string, Class<T> clazz) {
        try {
            return getEntityList(string, clazz);
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * 将实体类转化为JSON格式的字符串，解析异常则抛出
     * @param object 实体类
     * @return JSON格式字符串
     */
    public static String getJsonString(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER_THREAD_LOCAL.get().writeValueAsString(object);
    }

    /**
     * 将JSON格式字符串转化为实体类，解析异常则抛出
     * @param string JSON格式字符串
     * @param clazz 泛型类
     * @param <T> 泛型
     * @return 实体对象
     */
    public static <T> T getEntity(String string, Class<T> clazz) throws JsonProcessingException {
        return OBJECT_MAPPER_THREAD_LOCAL.get().readValue(string, clazz);
    }

    /**
     * 将JSON格式字符串转化为实体类列表，解析异常则抛出
     * @param string JSON格式字符串
     * @param clazz 泛型类
     * @param <T> 泛型
     * @return 实体对象列表
     */
    public static <T> List<T> getEntityList(String string, Class<T> clazz) throws JsonProcessingException {
        ObjectMapper objectMapper = OBJECT_MAPPER_THREAD_LOCAL.get();
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
        return objectMapper.readerFor(collectionType).readValue(string);
    }
}
