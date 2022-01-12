package up.visulog.analyzer;
/*
import org.junit.Test;
import up.visulog.gitrawdata.Commit;
import up.visulog.gitrawdata.CommitBuilder;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
*/
public class TestCountMergeCommits {
   /* @Test
    public void CheckMergeCommits() {
        ArrayList<Commit> log = new ArrayList<Commit>();
        String[] author = {"younes","yanis","racha","melissa"};
        var entires = 30;
        for (int i=0;i<entires;i++) {
            log.add(new CommitBuilder("").setAuthor(author[i%4]).setMergedFrom(author[i%4]).createCommit());
        }
        CountMergeCommits.Result result = CountMergeCommits.proceslog(log);
        assertEquals(author.length, result.getMergeCommit().size());
        int sum = result.getMergeCommit().values().stream().reduce(0,(a,b)->a+b);
        assertEquals(entires, sum);
    }*/
}