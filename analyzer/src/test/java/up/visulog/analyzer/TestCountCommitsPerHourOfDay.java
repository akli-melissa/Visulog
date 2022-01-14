package up.visulog.analyzer;

import org.junit.Test;
import up.visulog.gitrawdata.Commit;
import up.visulog.gitrawdata.CommitBuilder;
import java.util.*;
import java.text.SimpleDateFormat;
import static org.junit.Assert.assertEquals;

public class TestCountCommitsPerHourOfDay {
    @Test
    public void CheckCommitsPerHourTest() throws Exception {
        ArrayList<Commit> log = new ArrayList<Commit>();
        Date[] dates = new Date[4];
        dates[0] = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.ENGLISH).parse("11/08/2020 15:30:50");
        dates[1] = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.ENGLISH).parse("11/08/2020 17:00:00");
        dates[2] = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.ENGLISH).parse("10/05/2011 16:30:50");
        dates[3] = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.ENGLISH).parse("20/03/2021 20:15:00");
        var entires = 30;
        for (int i=0;i<entires;i++) {
            log.add(new CommitBuilder("").setDate(dates[i%4]).createCommit());
        }
        CountCommitsPerHourOfDay.Result result = CountCommitsPerHourOfDay.processLog(log);
        int sum = 0;
        for(Map.Entry c : result.getcommitsPerHourOfDay().entrySet()){
           sum=sum+(int)c.getValue();
        }
        assertEquals(entires, sum);
       
    }
}