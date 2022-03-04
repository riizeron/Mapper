package mapper.utils;

import org.junit.jupiter.api.Test;
import ru.hse.homework4.annotations.Exported;
import ru.hse.homework4.enums.UnknownPropertiesPolicy;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {
    @Exported
    class S {
        String name;
    }

    @Exported(unknownPropertiesPolicy = UnknownPropertiesPolicy.IGNORE)
    class Simple {
        String comment;
        boolean resolved;
        List<Integer> list;
        S sss;
    }

    String str = "{\"comment\":\"fuck you\",\"boba\":false,\"resolved\":false,\"list\":[1,2,3],\"sss\":{\"name\":\"ksysya\"}}";

    @Exported(unknownPropertiesPolicy = UnknownPropertiesPolicy.FAIL)
    class Bad {
        String str;
    }

    @Test
    void parseJsonWithUnknownFiledShouldThrowsNoSuchFieldExceptionIfUnknownPropIsFAIL() throws NoSuchFieldException {
        str = "{\"fstr\":\"qwe\"}";
        assertThrows(NoSuchFieldException.class, () -> JsonParser.parseJson(Bad.class, str));
    }

    @Test
    void parseJsonWithUnknownFiledShouldIgnoreIfUnknownPropIsIGNORE() throws NoSuchFieldException {
        JsonParser.parseJson(Simple.class, str);
        assert true;
    }

    @Test
    void fieldsToJSONStringShouldReturnCorrectJSONString() {
        Simple s = new Simple();
        s.comment = "qwerty";
        s.list = Arrays.asList(1, 3, 5, 13);
        s.sss = new S();
        s.sss.name = "yah";
        assertEquals(JsonParser.fieldsToJSONString(Arrays.asList(s.getClass().getDeclaredFields()), s),
                "{\"sss\":{\"name\":\"yah\"},\"this$0\":mapper.utils.JsonParserTest@79ad8b2f,\"comment\":\"qwerty\",\"list\":[1,3,5,13],\"resolved\":false}");
    }

    @Test
    void jsonArraySplitShouldntSplitLikeDefaultSplit() {
        String str = "{\"someList\":[{\"message\":\"sysya\",\"comments\":[{\"comment\":\"suck\"},{\"resolved\":true}]}]},{\"message\":\"slave\"}";
        List<String> arr = JsonParser.jsonArraySplit(str);
        assertEquals(arr.get(0), "{\"someList\":[{\"message\":\"sysya\",\"comments\":[{\"comment\":\"suck\"},{\"resolved\":true}]}]}");
        assertEquals(arr.get(1), "{\"message\":\"slave\"}");
    }
}