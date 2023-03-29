package xyz.chener.zp.common.config.unifiedReturn.encry.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.extern.slf4j.Slf4j;
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryField;
import xyz.chener.zp.common.config.unifiedReturn.encry.encryProcess.EncryInterface;
import xyz.chener.zp.common.utils.SecurityUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EncryCore {




    public static class EncryJacksonSerializerDispatch<T> extends JsonSerializer<T> implements ContextualSerializer {

        private final Class dispatchClass;

        public EncryJacksonSerializerDispatch(Class dispatchClass) {
            this.dispatchClass = dispatchClass;
        }

        @Override
        public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeNull();
        }

        @Override
        public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
            if (property != null){
                if (property.getType().getRawClass().equals(dispatchClass) && property.getAnnotation(EncryField.class)!=null){
                    return new EncryJacksonSerializer(property.getAnnotation(EncryField.class),property.getAnnotation(JsonFormat.class));
                }
                return new DefaultEncryJacksonSerializer<>(dispatchClass,property.getAnnotation(JsonFormat.class)) ;
            }
            return this;
        }
    }

    public static class DefaultEncryJacksonSerializer<T> extends JsonSerializer<T>{

        private final Class dispatchClass;

        private final JsonFormat jsonFormat;

        public DefaultEncryJacksonSerializer(Class dispatchClass, JsonFormat jsonFormat) {
            this.dispatchClass = dispatchClass;
            this.jsonFormat = jsonFormat;
        }

        @Override
        public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value != null && value.getClass().equals(dispatchClass)){
                if (value instanceof String v){
                    gen.writeString(v);
                }else if (value instanceof Integer v){
                    gen.writeNumber(v);
                }else if (value instanceof Long v){
                    gen.writeNumber(v);
                }else if (value instanceof Double v){
                    gen.writeNumber(v);
                }else if (value instanceof Float v){
                    gen.writeNumber(v);
                }else if (value instanceof Short v){
                    gen.writeNumber(v);
                }else  if (value instanceof Boolean v){
                    gen.writeBoolean(v);
                }else if (value instanceof BigDecimal v){
                    gen.writeNumber(v);
                }else if (value instanceof BigInteger v){
                    gen.writeNumber(v);
                }else if(value instanceof Date v){
                    if (jsonFormat != null){
                        SimpleDateFormat sdf = new SimpleDateFormat(jsonFormat.pattern());
                        gen.writeString(sdf.format(v));
                    }else {
                        gen.writeNumber(v.getTime());
                    }
                }
                else  {
                    gen.writeNull();
                }
            }else {
                gen.writeNull();
            }
        }
    }

    @Slf4j
    public static class EncryJacksonSerializer extends JsonSerializer<Object>{

        private final EncryField encryField;

        private final JsonFormat jsonFormat;

        public EncryJacksonSerializer(EncryField encryField,JsonFormat jsonFormat) {
            this.encryField = encryField;
            this.jsonFormat = jsonFormat;
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null){
                gen.writeNull();
                return;
            }

            String[] authority = encryField.hasAnyAuthority();

            if (authority.length > 0 && !SecurityUtils.hasAnyAuthority(authority)){
                gen.writeString("权限不足,无法显示");
                return;
            }

            if (!encryField.enableEncry()){
                gen.writeString(covertToString(value));
                return;
            }

            EncryInterface instance = null;
            try {
                instance = (EncryInterface) encryField.encryClass().getConstructor().newInstance();
            } catch (Exception ingored) {
                log.warn("{} 实例化失败",encryField.encryClass().getName());
            }

            Object result = null;
            if (instance !=null){
                result = instance.encry(covertToString(value),encryField);
            }
            if (result == null){
                gen.writeNull();
            }else {
                gen.writeString(result.toString());
            }
        }

        private String covertToString(Object value){
            if (value instanceof String v){
                return v;
            }else if (value instanceof Integer v){
                return v.toString();
            }else if (value instanceof Long v){
                return v.toString();
            }else if (value instanceof Double v){
                return v.toString();
            }else if (value instanceof Float v){
                return v.toString();
            }else if (value instanceof Short v){
                return v.toString();
            }else  if (value instanceof Boolean v){
                return v.toString();
            }else if (value instanceof BigDecimal v){
                return v.toString();
            }else if (value instanceof BigInteger v){
                return v.toString();
            }else if(value instanceof Date v){
                if (jsonFormat != null){
                    SimpleDateFormat sdf = new SimpleDateFormat(jsonFormat.pattern());
                    return sdf.format(v);
                }else {
                    return v.getTime()+"";
                }
            }
            return null;
        }

    }

}
