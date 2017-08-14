package ru.simplex_software.zkutils;

import net.sf.autodao.Dao;
import org.springframework.context.ApplicationContext;
import org.zkoss.lang.Objects;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.zkplus.spring.SpringUtil;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Variable Resolver for AutoDao beans.
 */
public class DaoVariableResolver implements VariableResolver {

    /** Spring Application Context of application. */
    private ApplicationContext _ctx;

    public DaoVariableResolver() {
        _ctx = SpringUtil.getApplicationContext();
    }

    /**
     * Resolve bean for name.
     * @param name - name of variable.
     * @return - bean for given name or null, if nothing was found.
     * @throws XelException .
     */
    public Object resolveVariable(String name) throws XelException {
        if ("springContext".equals(name)) {
            return _ctx;
        }

        Object bean = SpringUtil.getBean(name);

        if (bean == null) {
            Map<String, Dao> autoDAOMap = _ctx.getBeansOfType(Dao.class);
            bean = autoDAOMap.entrySet().stream()
                    .filter(map -> name.equals(getBeanName(map.getKey())))
                    .findFirst().orElse(new AbstractMap.SimpleEntry<>("", null)).getValue();
        }

        return bean;
    }

    /** Return class name of a bean with small letter. */
    private String getBeanName(String fullName) {
        String result;

        result = fullName.substring(fullName.lastIndexOf('.')+1, fullName.lastIndexOf(']'));
        result = result.substring(0, 1).toLowerCase() + result.substring(1);

        return result;
    }

    public int hashCode() {
        return Objects.hashCode(getClass());
    }

    public boolean equals(Object obj) {
        return this == obj || (obj instanceof DaoVariableResolver && getClass() == obj.getClass());
    }
}
