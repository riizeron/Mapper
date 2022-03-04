package mapper.models;

import ru.hse.homework4.annotations.Exported;

import java.util.List;

@Exported
public class Some {

    String message;


    public Some() {
    }
    public List<ReviewComment> comments;
    public Some(String str) {
        message = str;
    }

    @Override
    public String toString() {
        return "Some{" +
                "message='" + message + '\'' +
                ", comments=" + comments +
                '}';
    }
}
