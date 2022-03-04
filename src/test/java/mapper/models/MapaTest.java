package mapper.models;

import org.junit.jupiter.api.Test;
import ru.hse.homework4.annotations.Exported;
import ru.hse.homework4.annotations.Ignored;
import ru.hse.homework4.annotations.PropertyName;
import ru.hse.homework4.interfaces.Mapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MapaTest {

    @Exported
    class ReviewComment {
        @Exported
        class Some {
            String message;

            public Some() {
            }

            public Some(String str) {
                message = str;
            }
        }

        private static final String qwerty = "qwerty";
        private Some some = new Some("ksysya");
        private String comment;
        @Ignored
        private String author;
        private boolean resolved;

        public ReviewComment() {
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public void setResolved(boolean resolved) {
            this.resolved = resolved;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        @Override
        public String toString() {
            return "ReviewComment{" +
                    "comment='" + comment + '\'' +
                    ", author='" + author + '\'' +
                    ", resolved=" + resolved +
                    '}';
        }
    }

    Mapper mapper = new Mapa();
    String res;

    @org.junit.jupiter.api.Test
    void read() {
    }

    @org.junit.jupiter.api.Test
    void testRead() {
    }

    @org.junit.jupiter.api.Test
    void writeToString() {
        ReviewComment reviewComment = new ReviewComment();
        reviewComment.setComment("lolkek");
        reviewComment.setResolved(false);
        reviewComment.setAuthor("KrulTepes");
        res = mapper.writeToString(reviewComment);
        assertEquals("{\"some\":{\"message\":\"ksysya\"},\"comment\":\"lolkek\",\"resolved\":false}", res);
    }

    @Test
    void writeACollectionsFieldCorrectly() {
        @Exported
        class ClassWithCollectionField {
            List<String> list = List.of(new String[]{"abc", "defg", "hijk", "lmnop"});
        }
        ClassWithCollectionField o = new ClassWithCollectionField();
        res = mapper.writeToString(o);
        assertEquals("{\"list\":[\"abc\",\"defg\",\"hijk\",\"lmnop\"]}", res);
    }

    @Test
    void writeExportedFieldCorrectly() {
        @Exported
        class ClassWithExportedField {
            @Exported
            class ExportedClass {
                String string = "qwerty";
                @PropertyName("numbers")
                List<Integer> list = List.of(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
            }

            ExportedClass ex = new ExportedClass();
        }
        ClassWithExportedField cl = new ClassWithExportedField();
        res = mapper.writeToString(cl);
        assertEquals("{\"ex\":{\"string\":\"qwerty\",\"numbers\":[1,2,3,4,5,6,7,8,9]}}", res);
    }

    @Test
    void writeClassWithCollectionOfExportedCorrectly() {
        @Exported
        class ClassWithCollectionOfExported {
            @Exported
            class ExportedClass {
                ExportedClass(String string) {
                    this.string = string;
                }

                String string = "qwerty";
            }

            List<ExportedClass> list = Arrays.asList(new ExportedClass("abcde"), new ExportedClass("qwerty"));
        }
        ClassWithCollectionOfExported cl = new ClassWithCollectionOfExported();
        res = mapper.writeToString(cl);
        assertEquals("{\"list\":[{\"string\":\"abcde\"},{\"string\":\"qwerty\"}]}", res);
    }

    @Test
    void writeAnEmptyClassObjectIsNothing() {
        @Exported
        class Empty {
        }
        assertEquals("", mapper.writeToString(new Empty()));
    }

    @Test
    void writeAClassNotAnnotatedWithExportedShouldThrowException() {
        class NotAnnotated {
        }
        assertThrows(RuntimeException.class, () -> mapper.writeToString(new NotAnnotated()));
    }

    @org.junit.jupiter.api.Test
    void write() {
    }

    @org.junit.jupiter.api.Test
    void testWrite() {
    }
}