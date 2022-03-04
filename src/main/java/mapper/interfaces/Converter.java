package mapper.interfaces;

import java.lang.reflect.Type;

public interface Converter {
    Object convertFromString(String s, Type t);
    String convertToString(Object o);
}
