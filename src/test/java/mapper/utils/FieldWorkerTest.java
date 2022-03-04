package mapper.utils;

import org.junit.jupiter.api.Test;
import ru.hse.homework4.annotations.Exported;
import ru.hse.homework4.annotations.Ignored;
import ru.hse.homework4.annotations.PropertyName;
import ru.hse.homework4.enums.NullHandling;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FieldWorkerTest {
    @Exported(nullHandling = NullHandling.EXCLUDE)
    class A {
        static String st;
        String string;
        @Ignored
        public double dob;
        int integer = 5;
        @PropertyName("biba")
        boolean boba = false;
        List<String> list;
    }

    @Exported(nullHandling = NullHandling.INCLUDE)
    class B {
        Set<Boolean> setata;
    }

    @Test
    void setFields() {
    }

    @Test
    void getFieldByNameIfThereIsNoSuchNamedFieldAtAllShouldReturnEmptyOptional() {
        assertFalse(FieldWorker.getFieldByName(A.class, "sch.uka").isPresent());
    }

    @Test
    void getFieldByNameIfThereIsNoSuchNamedMethodExceptPropNameAnnotShouldReturnCorrectField() throws NoSuchFieldException {
        assertTrue(FieldWorker.getFieldByName(A.class, "biba").isPresent());
        assertEquals(FieldWorker.getFieldByName(A.class, "biba").get(), A.class.getDeclaredField("boba"));
    }

    @Test
    void getFieldByNameOnCorrectValuesShouldReturnCorrectField() throws NoSuchFieldException {

        assertTrue(FieldWorker.getFieldByName(A.class, "list").isPresent());
        assertEquals(FieldWorker.getFieldByName(A.class, "list").get().getType(), A.class.getDeclaredField("list").getType());
    }

    @Test
    void classFieldFilterShouldExcludeStaticSyntheticIgnoredFields() throws NoSuchFieldException {
        assertFalse(FieldWorker.classFieldFilter(new A()).contains(A.class.getDeclaredField("dob")));
        assertFalse(FieldWorker.classFieldFilter(new A()).contains(A.class.getDeclaredField("st")));
    }

    @Test
    void classFieldFilterShouldExcludeNullValuesIfNullHandlingExclude() throws NoSuchFieldException {
        assertFalse(FieldWorker.classFieldFilter(new A()).contains(A.class.getDeclaredField("list")));
    }

    @Test
    void classFieldFilterShouldIncludeNullValuesIfNullHandlingInclude() throws NoSuchFieldException {
        assertTrue(FieldWorker.classFieldFilter(new B()).contains(B.class.getDeclaredField("setata")));
    }

    @Test
    void classFieldFilter() {
    }
}