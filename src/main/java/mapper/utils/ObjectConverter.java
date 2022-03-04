package mapper.utils;

import ru.hse.homework4.annotations.DateFormat;
import ru.hse.homework4.annotations.Exported;

import java.lang.reflect.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс занимающий руководящую должность по вопросам операций с объектами
 */
public class ObjectConverter {

    /**--------------------------ИЗ СТРОКИ В ОБЪЕКТ-----------------------------------------
    /**
     * Глав диспетчер(нет)
     * Любое затерявшееся в жсоне поле неизбежно попадает сюда
     * Тут главные умы сей планеты решают че это и че с ним делать
     * @param clazz типа объекта который достаем из строки
     * @param s строкове представление объекта типа clazz
     * @return достанный из пучины жсон объект
     */
    public static <T> Object convertFromString(Class<T> clazz, String s) {
        if (clazz.isAnnotationPresent(Exported.class)) {
            return stringToExportedClass(clazz, s);
        }
        System.out.println(clazz);
        if (clazz == String.class) {
            return (s.substring(1, s.length() - 1));
        }
        if (clazz == Double.TYPE){
            return Double.parseDouble(s);
        }
//        if (clazz == Enum.class) {
//            return Enum.valueOf(clazz, s);
//        }
        if(clazz == Byte.TYPE) {
            return Byte.parseByte(s);
        }
        if (clazz == Integer.TYPE) {
            return (Integer.parseInt(s));
        }
        if (clazz == LocalDate.class) {
            if (clazz.isAnnotationPresent(DateFormat.class)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(clazz.getAnnotation(DateFormat.class).value());
                return LocalDate.parse(s, formatter);
            }
            return LocalDate.parse(s);
        }
        if (clazz == LocalTime.class) {
            if (clazz.isAnnotationPresent(DateFormat.class)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(clazz.getAnnotation(DateFormat.class).value());
                return LocalTime.parse(s, formatter);
            }
            return LocalTime.parse(s);
        }
        if (clazz == LocalDateTime.class) {
            if (clazz.isAnnotationPresent(DateFormat.class)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(clazz.getAnnotation(DateFormat.class).value());
                return LocalDateTime.parse(s, formatter);
            }
            return LocalDateTime.parse(s);
        }
        if (clazz == Boolean.TYPE) {
            return Boolean.valueOf(s);
        }
        if (clazz == List.class || clazz == Set.class) {
            return collectionFromString(clazz, s);
        }

        return null;
    }

    /**
     * И че это. И зачем. Коллекции я все равно не десериализую.
     * А почему так? А потому что что делают типы параметризации в рантайме?
     * Правильно, пропадают.
     * А куда?...
     * Написал специальный сплиттер даже чтоб внутри фигурных скобочек запятые не беспокоить
     * А зачееем
     * @param clazz тут должен быть быть тип параметризации коллекции на увы
     * @param s строковое представление коллекции
     * @return как ни странно коллекцию
     */
    private static Collection<?> collectionFromString(Class<?> clazz, String s) {
        return JsonParser.jsonArraySplit(s).stream()
                .map(e -> convertFromString(clazz, e))
                .collect(Collectors.toList());
    }

    /**
     * Любоый класс (поле типо класса) помеченное Exported неизбежно сюда попадает
     * Правда если Ignored не стоит
     * @param clazz типа
     * @param input строковое представление класса
     * @param <T> параметризация
     * @return десериализованный объект класса
     */
    public static <T> T stringToExportedClass(Class<T> clazz, String input) {
        if (!clazz.isAnnotationPresent(Exported.class)) {
            throw new RuntimeException("Class not annotated with Exported");
        }
        T target;
        try {
            target = clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("No public no parameterized constructor",e);
        }

        try {
            // Используя экземпляр класса и получая мапу поле-строка
            // рекурсивно задаем полям значения
            // рекурсивно потому что бог знает насколько глубоко нам придется зайти
            FieldWorker.setFields(target, JsonParser.parseJson(clazz, input));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return target;
    }

    private static Date parseDate(Class<?> clazz, String s) {
        return new Date();
    }

    /**-----------------------------------ИЗ ОБЪЕКТА В СТРОКУ------------------------------------

     /**
     * Метод преобразующий объекты в жсон
     * Рекурсивно
     * @param o объект
     * @return строку - представление объекта
     */
    public static String convertToString(Object o) {
        if (o.getClass().isAnnotationPresent(Exported.class)) {
            return exportedClassToString(o);
        } else {
            // Зачем это я так надругался над строкой - незнаю
            // В примере так было
            // Да и наверное это по жсоновским понятиям
            if (o.getClass() == String.class) {
                return "\"" + String.valueOf(o) + "\"";
            }
            if(o == Enum.class) {
                return o.toString();
            }
            if (o.getClass() == LocalDate.class) {
                if (o.getClass().isAnnotationPresent(DateFormat.class)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(o.getClass().getAnnotation(DateFormat.class).value());
                    return ((LocalDate)o).format(formatter);
                }
                return ((LocalDate)o).toString();
            }
            if (o.getClass() == LocalTime.class) {
                if (o.getClass().isAnnotationPresent(DateFormat.class)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(o.getClass().getAnnotation(DateFormat.class).value());
                    return ((LocalTime)o).format(formatter);
                }
                return ((LocalTime)o).toString();
            }
            if (o.getClass() == LocalDateTime.class) {
                if (o.getClass().isAnnotationPresent(DateFormat.class)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(o.getClass().getAnnotation(DateFormat.class).value());
                    return ((LocalDateTime)o).format(formatter);
                }
                return ((LocalDateTime)o).toString();
            }
            // Вот пожалуйте тут вызывается этот же метод так как коллекция
            // это набор различных объектов которые тоже надо преобразовывать
            if (o instanceof Collection<?>) {
                return ((Collection<?>) o).stream()
                        .map(ObjectConverter::convertToString)
                        .collect(Collectors.joining(",", "[", "]"));
            }
            return o.toString();
        }
    }

    /**
     * Персональный метод для конвертации в строку для объектов типо EXPORTED
     * @param object обжект
     * @return стоковое представление обжекта
     */
    public static String exportedClassToString(Object object) {
        List<Field> availableFields;
        if (object.getClass().isAnnotationPresent(Exported.class)) {
            availableFields = FieldWorker.classFieldFilter(object);
        } else {
            throw new RuntimeException("Class not annotated with 'Exported'");
        }
        assert availableFields != null;
        return JsonParser.fieldsToJSONString(availableFields, object);
    }

//     public static String convertRecordToString(Object o) {
//        if (o.getClass().isAnnotationPresent(Exported.class)) {
//            return exportedRecordToString(o);
//        } else {
//            if (o.getClass() == String.class) {
//                return "\"" + String.valueOf(o) + "\"";
//            }
//            if (o.getClass() == Integer.class) {
//                return o.toString();
//            }
//            if (o instanceof Collection<?>) {
//                return ((Collection<?>) o).stream()
//                        .map(ObjectConverter::convertToString)
//                        .collect(Collectors.joining(",", "[", "]"));
//            }
//            return o.toString();
//        }
//    }


//    public static String exportedRecordToString(Object object) {
//        List<RecordComponent> components = new ArrayList<>();
//        if(object.getClass().isAnnotationPresent(Exported.class)) {
//            components = FieldWorker.componentFilter(object);
//        } else {
//            throw new RuntimeException("Class not annotated with 'Exported'");
//        }
//
//        return "";
//    }

    private static <T> T stringToExportedRecord(Class<T> clazz, String input) {
        if (!clazz.isAnnotationPresent(Exported.class)) {
            throw new RuntimeException("Record not annotated with Exported");
        }
        T target;
        try {
            target = clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        try {
            FieldWorker.setFields(target, JsonParser.parseJson(clazz, input));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return target;
    }
}
