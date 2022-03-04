package mapper.models;

import ru.hse.homework4.annotations.Exported;
import ru.hse.homework4.annotations.Ignored;
import ru.hse.homework4.enums.NullHandling;
import ru.hse.homework4.enums.UnknownPropertiesPolicy;

import java.util.Arrays;
import java.util.List;

@Exported(unknownPropertiesPolicy = UnknownPropertiesPolicy.FAIL,
        nullHandling = NullHandling.EXCLUDE)
public class ReviewComment {

    private static final String qwerty = "qwerty";
    private Some some = new Some("ksysya");
    public List<Some> someList = Arrays.asList(new Some("123"), new Some("234"));
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
                "some=" + some +
                ", someList=" + someList +
                ", comment='" + comment + '\'' +
                ", author='" + author + '\'' +
                ", resolved=" + resolved +
                '}';
    }
}
