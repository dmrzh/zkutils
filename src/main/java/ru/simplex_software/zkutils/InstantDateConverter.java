package ru.simplex_software.zkutils;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import java.time.Instant;
import java.util.Date;

/**
 * Конвертер для преобразования Date в Instant.
 */
public class InstantDateConverter implements Converter {

    /**
     * Instant/Date.
     * @param beanProp
     * @param component
     * @param ctx
     * @return
     */
    @Override
    public Date coerceToUi(Object beanProp, Component component, BindContext ctx) {
        Instant instant = (Instant) beanProp;
        Date date = Date.from(instant);
        return date;
    }

    /**
     * Date/Instant.
     * @param compAttr
     * @param component
     * @param ctx
     * @return
     */
    @Override
    public Instant coerceToBean(Object compAttr, Component component, BindContext ctx) {
        Date date = (Date) compAttr;
        Instant instant = date.toInstant();
        return instant;
    }
}