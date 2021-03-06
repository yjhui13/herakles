package com.lcw.herakles.platform.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 序列化工具类
 * 
 * @author chenwulou
 *
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SerializeUtil {

//    private static final Logger LOGGER = LoggerFactory.getLogger(SerializeUtil.class);
//
//    private SerializeUtil() {
//
//    }

    /**
     * 对象序列化
     * 
     * @param object
     * @return
     */
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 对象反序列化
     * 
     * @param bytes
     * @return
     */
    public static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
