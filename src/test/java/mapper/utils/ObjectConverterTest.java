package mapper.utils;

import org.junit.jupiter.api.Test;
import ru.hse.homework4.annotations.DateFormat;
import ru.hse.homework4.annotations.Exported;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ObjectConverterTest {
    @Exported
    static
    class Dates {
        public Dates() {
        }

        @DateFormat
        LocalDate d1;
        LocalDateTime d2;
        @DateFormat("uuuu-MMM-dd")
        LocalTime d3;

        @Override
        public String toString() {
            return "Dates{" +
                    "d1=" + d1 +
                    ", d2=" + d2 +
                    ", d3=" + d3 +
                    '}';
        }
    }

    @Test
    void convertFromString() {
    }

    @Test
    void convertDataFromStringCorrectly() {
        String str = "{\"d1\":2002-07-09,\"d2\":2022-03-04T22:36:30.146045,\"d3\":22:36:30.146045}";
        Object object = ObjectConverter.stringToExportedClass(Dates.class, str);
        System.out.println(object);
    }

    @Test
    void convertFromStringOnClassWithoutSpecialConstructorShouldThrowsRuntimeException() {
        @Exported
        class S {
            String str;
        }
        String str = "{\"str\":\"qwerty\"}";
        assertThrows(RuntimeException.class, () -> ObjectConverter.convertFromString(S.class, str));
    }

    @Test
    void stringToExportedClassOnClassNotAnnotatedWithExportedShouldThrowsRuntimeException() {
        class S {
            String str;
        }
        String str = "{\"str\":\"qwerty\"}";
        assertThrows(RuntimeException.class, () -> ObjectConverter.stringToExportedClass(S.class, str));
    }

    @Test
    void stringToExportedClassInClassWithoutSpecialConstructorShouldThrowsRuntimeException() {
        @Exported
        class S {
            String str;
        }
        String str = "{\"str\":\"qwerty\"}";
        assertThrows(RuntimeException.class, () -> ObjectConverter.stringToExportedClass(S.class, str));
    }


    @Test
    void convertToStringOnClassWithoutSpecioalConstructorShouldThrowsRuntimeException() {
        @Exported
        class S {
            String str;
        }
        String str = "{\"str\":\"qwerty\"}";
        assertThrows(RuntimeException.class, () -> ObjectConverter.stringToExportedClass(S.class, str));
    }

    @Test
    void exportedClassToStringOnClassNotAnnotatedWithExportedShouldThrowsRuntimeException() {
        class S {
            public S() {
            }

            String str;
        }
        S s = new S();
        assertThrows(RuntimeException.class, () -> ObjectConverter.exportedClassToString(s));
    }

    @Test
    void convertDateTimeToStringCorrectly() {
        Dates d = new Dates();
        d.d1 = LocalDate.now();
        d.d2 = LocalDateTime.now();
        d.d3 = LocalTime.now();
        String str = ObjectConverter.convertToString(d);
        System.out.println(str);
    }
}