package up.visulog.analyzer;

import org.junit.Test;
import up.visulog.gitrawdata.Commit;
import up.visulog.gitrawdata.CommitBuilder;
import java.util.*;
import java.text.SimpleDateFormat;
import static org.junit.Assert.assertEquals;
public class TestCountCommitPerDayOfMonth {
    @Test
    public void CheckCommitsDayOfMonth() throws Exception {
        ArrayList<Commit> log = new ArrayList<Commit>();
        Date[] dates = new Date[4];
        dates[0] = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH).parse("11/08/2020");
        dates[1] = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH).parse("13/08/2020");
        dates[2] = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH).parse("10/05/2011");
        dates[3] = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH).parse("20/03/2021");
        var entires = 30;
        for (int i=0;i<entires;i++) {
            log.add(new CommitBuilder("").setDate(dates[i%4]).createCommit());
        }
        CountCommitsPerDayOfMonth.Result result = CountCommitsPerDayOfMonth.processLog(log);
        int sum = 0;
        for(Map.Entry c : result.getcommitsPerDayOfMonth().entrySet()){
           sum=sum+(int)c.getValue();
        }
        assertEquals(entires, sum);
       
    }
}
