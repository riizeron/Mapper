package mapper.utils;

import ru.hse.homework4.annotations.Exported;
import ru.hse.homework4.annotations.PropertyName;
import ru.hse.homework4.enums.UnknownPropertiesPolicy;

import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Этот класс рабирается за жсоны и все что с ними связано
 */
public class JsonParser {

    static String string;
    /**----------------------------------ИЗ СТРОКИ В ОБЪЕКТ---------------------------------------
    /**
     * Метод задающий исток парсу жсона сначала в мапу поле-строка(строковое значение поля)
     * @param clazz класс поля которого хотим прочитать в мапу
     * @param input строковое представление обжекта
     * @param <T> пармпармапарааадуемсянасвоемвекуция
     * @return мапу
     * @throws NoSuchFieldException метод ломается если поля с таким именем у класса нет
     */
    public static <T> Map<Field, String> parseJson(Class<T> clazz, String input) throws NoSuchFieldException {
        string = input;
        Map<Field, String> fieldStringMap = new HashMap<>();
        Pair<String, String> pair;
        Optional<Field> field;
        while ((pair = getKeyAndVal()) != null) {
            field = FieldWorker.getFieldByName(clazz, pair.key());
            if (field.isPresent()) {
                fieldStringMap.put(field.get(), pair.value());
            } else if (clazz.getAnnotation(Exported.class).unknownPropertiesPolicy()
                    == UnknownPropertiesPolicy.FAIL) {
                throw new NoSuchFieldException();
            }
        }
        return fieldStringMap;
    }

    /**
     * Щииикаааарный метод беганья по жсону и доставания оттуда пар имя поля - его строковое значени
     * Метод обалденный все делаю максимально ровно
     * Даже если проверяющий раскомментирует строки ниже он увидит
     * как все промежуточные значения выводятся по линейке
     * Чертовски нравится как ровно он работает
     * Хоть у тебя там коллекция в коллекции в коллекции и тп.
     * ЕМУ ВСЕ РАВНО, ОН УБИЙЦА
     * Рекурсивно вытащит даже черта из ада
     * КАЗАЛОСЬ БЫ ВСЕ ОКЕЙ
     * НО
     * Метод не способен себя проявить во всей красе в суровых условиях жабовских женериков
     * Из-за того что я не могу получить тип ХРАНЯЩИЙСЯ в коллекции я не могу восстановить
     * эту коллекцию
     * Отсюды вытекает вопрос ======> *** Зачем так ахиренно парсить коллекции если абсолютно не понятно
     * че потом с ними делать??? ***
     * Ну или я просто не понимаю чего-то
     * Такое впрочем тоже возможно
     * @return простой рекорд с двумя значениями имя поля(мтрока) - значение поля(строка)
     */
    private static Pair getKeyAndVal() {
        String key;
        String value;
        if (string.isEmpty() || string.startsWith("]") || string.startsWith("}")) return null;
        // System.out.println("***" + string);
        if (string.startsWith("{")) {
            string = string.substring(1, pairBranchIndex(string, '{', '}') - 1);
        }
        // System.out.println("+++" + string);
        key = string.substring(1, string.indexOf(":") - 1);
        // System.out.println("---" + key);
        string = string.substring(string.indexOf(":") + 1);
        if (string.startsWith("{")) {
            value = string.substring(1, pairBranchIndex(string,'{','}') - 1);
            string = string.substring(value.length() + 2);
        } else if (string.startsWith("[")) {
            value = string.substring(1, pairBranchIndex(string, '[',']') - 1);
            string = string.substring(value.length() + 2);
        } else if (string.startsWith("\"")) {
            value = string.substring(0, nextIndexOf(string,'\"') + 1);
            string = string.substring(value.length());
        } else if (string.indexOf(',') != -1){
                value = string.substring(0, string.indexOf(','));
                string = string.substring(string.indexOf(','));
        } else {
            value = string;
            string = "";
        }

        if (string.startsWith(",")) {
            string = string.substring(1);
        }
        // System.out.println(key + " -- " + value);
        return new Pair(key, value);
    }

    /**------------------------------------ОБЪЕКТ В СТРОКУ--------------------------------------------------
    /**
     * Метод собирающий из списка отобранных полей первого класса мапу поле - строковое значение
     * И затем эту мапу аккуратно пихает в жсон строку со всеми канонами
     * @param fields список полей первой свежести
     * @param object обжект - таргет, все эти поля - они его.
     * @return строковое жсон представление
     */
    public static String fieldsToJSONString(List<Field> fields, Object object) {
        Map<String, String> map = listToMap(fields, object);
        return map.isEmpty()
                ? ""
                : map.keySet().stream()
                .map(key -> "\"" + key + "\":" + map.get(key))
                .collect(Collectors.joining(",", "{", "}"));
    }

    /**
     * ПО-умному кастуем лист с полями к мапе имя поля - строковое значение поля
     * Так надо чтобы удобнее было все пихать в жсон
     * @param fields список отобранных полей
     * @param object обжект, владелец полей, земледелец крч
     * @return мапу имя-значения, везде строка
     */
    private static Map<String, String> listToMap(List<Field> fields, Object object) {
        return fields.stream()
                .collect(Collectors.toMap(field ->
                                field.isAnnotationPresent(PropertyName.class)
                                        ? field.getAnnotation(PropertyName.class).value()
                                        : field.getName(),
                        field -> {
                            try {
                                return ObjectConverter.convertToString(field.get(object));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }));
    }


    /**---------------------------------ВСЯКАЯ ВСЯЧИНА--------------------------------------------------
    /**
     * Метод сверхвычисления расстояния до парной скобки
     * Такое только на алгосах
     * Работает на стеке - все просто
     * @param input строка в которой спрятались поля
     * @param separatorFront открывающая скобка
     * @param separatorBack закрывающая скобка
     * @return индексация стипендии
     */
    private static int pairBranchIndex(String input, char separatorFront, char separatorBack) {
        Stack<Integer> stack = new Stack<>();
        int counter = 0;
        for (char c : input.toCharArray()) {
            counter++;
            if (c == separatorFront) {
                stack.push(1);
            } else if (c == separatorBack) {
                stack.pop();
            }
            if (stack.isEmpty()) {
                return counter;
            }
        }
        return counter;
    }

    /**
     * Легкий и непринужденный метод поиска первого вхождения символа за исключением того
     * на котором
     * ты
     * сейчас
     * находишься
     * @param input строка с прячущимися объектиками
     * @param s чар место которого нужно найти
     * @return индексация комплекса
     */
    private static int nextIndexOf(String input, char s) {
        for(int i = 1; i < input.length(); ++i) {
            if (input.charAt(i) == s) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Метод выделения массива в строке формата жсон
     * Основан на парности кавычек
     * Типо при сплите этого: {a,d,v},{d:"as", sd:["q", "w"]} этот метод вернет
     * это {a,d,v} и это {d:"as", sd:["q", "w"]}
     * а не черт знает что
     * Далее за счет рекурсивной реализации всей программы этот метод доберется до каждого []!!!
     * @param input исходная строка
     * @return спискок , не массив
     */
    public static List<String> jsonArraySplit(String input) {
        Stack<Integer> stack = new Stack<>();
        List<String> res = new ArrayList<>();
        int first = 0;
        for (int i = 0; i < input.length(); ++i) {
            if (input.charAt(i) == '{')
                stack.push(1);
            else if (input.charAt(i) == '}')
                stack.pop();
            if (stack.isEmpty()) {
                if (input.charAt(i) == ',') {
                    String str = input.substring(first, i);
                    res.add(str);
                    // System.out.println(":::" + str);
                    first = i + 1;
                } else if (i + 1 == input.length()) {
                    String str = input.substring(first, i + 1);
                    res.add(str);
                    // System.out.println(":::" + str);
                }
            }
        }
        return res;
    }
}
