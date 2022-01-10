package up.visulog.gitrawdata;
import java.util.*;
public class CommitBuilder {
    private final String id;
    private String author;
    private Date date;
    private String description;
    private String mergedFrom;
    private String commitInformations;

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

    public CommitBuilder setCommitInformations(String commitInformations){
        this.commitInformations = commitInformations;
        return this;
    }

    public Commit createCommit() {
        return new Commit(id, author, date, description, mergedFrom, commitInformations);
    }
}