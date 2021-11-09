package up.visulog.gitrawdata;
<<<<<<< HEAD
import java.util.*;

=======
import java.util.Date;
>>>>>>> 046f41c1064b8d2c0eb9abb61f09e08e394fc948
public class CommitBuilder {
    private final String id;
    private String author;
    private Date date;
    private String description;
    private String mergedFrom;

    public CommitBuilder(String id) {
        this.id = id;
    }

    public CommitBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    public CommitBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public CommitBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public CommitBuilder setMergedFrom(String mergedFrom) {
        this.mergedFrom = mergedFrom;
        return this;
    }

    public Commit createCommit() {
        return new Commit(id, author, date, description, mergedFrom);
    }
}
