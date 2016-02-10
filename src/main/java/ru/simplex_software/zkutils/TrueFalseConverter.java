package ru.simplex_software.zkutils;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

/**.*/
public class TrueFalseConverter<C extends Component> implements Converter<String, Boolean,C> {

    private static TrueFalseConverter INSTANCE = new TrueFalseConverter();

    public static TrueFalseConverter getInstance(){
        return INSTANCE;
    }

    public Boolean coerceToBean(String compAttr, C component, BindContext ctx) {
        if(compAttr==null||compAttr.length()==0) {
            return null;
        }
        return "Да".equals(compAttr);

    }

    public String coerceToUi(Boolean beanProp, C component, BindContext ctx) {
        if(beanProp==null){
            return "";
        }
        if(beanProp){
            return "Да";
        }else{
            return "Нет";
        }
    }


}
