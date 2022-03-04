package mapper.utils;

import ru.hse.homework4.annotations.Exported;
import ru.hse.homework4.annotations.Ignored;
import ru.hse.homework4.annotations.PropertyName;
import ru.hse.homework4.enums.NullHandling;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.*;

/**
 * Данный класс заведует рефлексивной работой с полями
 */
public class FieldWorker {

    /**
     * -----------------------------СТРОКА В ОБЪЕКТ---------------------------------------------------
     * /**
     * Данный метод осуществляет процедуру записи к врачу
     * Так, заново
     * Данный метод осуществляет процедуру задания полям своих значений
     * На простонародье этот процесс завется десериализацией
     *
     * @param target          владелей полей, полевладелец
     * @param fieldsAndValues мапа поле - значение в виде строки
     * @param <T>             параметризация типо полевладельца
     */
    public static <T> void setFields(T target, Map<Field, String> fieldsAndValues) {
        fieldsAndValues.keySet().stream()
                .filter(f -> !f.isAnnotationPresent(Ignored.class))
                .filter(f -> !f.isSynthetic())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(AccessibleObject::trySetAccessible)
                .forEach(f -> {
                    try {
                        // Тут метод положит начало рекурсивному восстановлению значений полей
                        // если поле подазумевает собой это действие разумеется
                        // Далее сконвертированное значение записывается в соответствующее поле.
                        f.set(target, ObjectConverter.convertFromString(f.getType(), fieldsAndValues.get(f)));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    /**
     * Метод, ищущий поле по его имени или по значению аннотации PropertyName
     *
     * @param clazz класс полевладельца
     * @param name  имя поля котторе спряталось
     * @return кот шредингера
     */
    public static Optional<Field> getFieldByName(Class<?> clazz, String name) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(PropertyName.class)
                        && Objects.equals(f.getAnnotation(PropertyName.class).value(), name)
                        || f.getName().equals(name))
                .findFirst();

    }

    /**
     * -------------------------------------ОБЪЕКТ В СТРОКУ-------------------------------------
     * /**
     * Метод фильтрации полей
     * Избранными становятся немногие
     * Ну или все как 2020м
     * Проверка на аннотцаии, статику, синтетику(осуждаю), обеспечение доступности
     *
     * @param object полевладелец
     * @return список полей
     */
    public static List<Field> classFieldFilter(Object object) {
        return Arrays.stream(object.getClass().getDeclaredFields())
                .filter(f -> !f.isSynthetic() && !Modifier.isStatic(f.getModifiers()))
                .filter(f -> !f.isAnnotationPresent(Ignored.class))
                .filter(AccessibleObject::trySetAccessible)
                .filter(f -> {
                    try {
                        return f.get(object) != null || object.getClass().getAnnotation(Exported.class).nullHandling() == NullHandling.INCLUDE;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    /*public static List<RecordComponent> componentFilter(Object object) {
        return Arrays.stream(object.getClass().getRecordComponents())
                .filter(r -> !r.isAnnotationPresent(Ignored.class))
                .filter(f -> f.);

    }

    public static <T> void setRecord(T target, Map<RecordComponent, String> recordComponentStringMap) {
        recordComponentStringMap.keySet().stream()
                .filter(r -> !r.isAnnotationPresent(Ignored.class))
                .
    }

    public static Optional<RecordComponent> getComponentByName(Class<?> clazz, String name) {

    }*/
}

