package ru.simplex_software.zkutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;


/**
 * Конвертирует enum в строку и обратно на основани его .properties файла.
 * .properties должен располагаться в папке resources и иметь имя полное_имя_enum_класса.properties
 * Структура .properties следующая: enum_значение = имя
 * */
public class EnumPropertiesConverter<T extends Enum<T>> implements Converter {

    private static final Logger LOG=LoggerFactory.getLogger(PropConverter.class);

    private Properties properties;

    private Class<T> enumClass;

    public EnumPropertiesConverter(Class<T> enumClass) {
        this.enumClass = enumClass;
        InputStream stream = PropConverter.class.getClassLoader().
                getResourceAsStream(String.format("%s.properties", enumClass.getName()));
        properties = new Properties();
        try {
            properties.load(stream);
        } catch (IOException e) {
            LOG.error(e.getMessage(),e);
        }

    }


    public Object coerceToUi(Object beanProp, Component c, BindContext ctx) {
        if(beanProp==null){
            return null;
        }

        return properties.getProperty(enumClass.cast(beanProp).name());

    }


    public T coerceToBean(Object compAttr, Component c, BindContext ctx) {
        for(Map.Entry e:properties.entrySet()){
            if (e.getValue().equals(compAttr)){
                return Enum.valueOf(enumClass, (String) e.getKey());
            }
        }
        return null;
    }
}
