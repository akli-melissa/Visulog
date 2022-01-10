package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class CountCommitsPerHourOfDay implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;// classe interne

    public CountCommitsPerHourOfDay(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }
    
    static Result processLog(List<Commit> gitLog) throws Exception {
        var result = new Result();
       for (Map.Entry<String,Integer> item:result.commitsPerHourOfDay.entrySet()){
            Map<String, Integer> commitsPerHour = new HashMap<>();// pour chaque dimanche precis(Mon 12/20 Jan)

            for (var commit : gitLog) {
                Date date=commit.date;
                SimpleDateFormat hour = new SimpleDateFormat("HH");
                SimpleDateFormat day = new SimpleDateFormat("MMM d HH yyyy");
                if(hour.format(date).equals(item.getKey())){
                    var nb = commitsPerHour.getOrDefault(day.format(date), 0);
                    commitsPerHour.put(day.format(date), nb + 1);
                } 
            }
            if(commitsPerHour.size()!=0){
                int sum=0;
                for(int val:commitsPerHour.values()){
                    sum+=val;
                }
               result.commitsPerHourOfDay.put(item.getKey(),sum/commitsPerHour.size());
            }

        }
        return result;
    }

    @Override
    public void run() {
        if (this.configuration.getPluginConfig("countCommitsPerHourOfDay").isPresent()) {
            try{
            result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(),
                    this.configuration.getPluginConfig("countCommitsPerHourOfDay").get()));
            }catch(Exception e){
                System.out.println(e);           
            }
        }
    }

    @Override
    public Result getResult() {
        if (result == null)
            run();
        return result;
    }

    // Implementation de la sous interface Result de AnalyzerPlugin
    static class Result implements AnalyzerPlugin.Result {
        private final Map<String, Integer> commitsPerHourOfDay ;// pour chaque user un nombre de
        Result(){
            commitsPerHourOfDay = new LinkedHashMap<>();
            for(int i=0;i<24;i++){
                String s = String.valueOf(i);
                if(s.length()==1){
                    s="0"+s;
                }
                commitsPerHourOfDay.put(s,0);
            }
        }                                                                       // commits

        Map<String, Integer> getcommitsPerHourOfDay() {
            return commitsPerHourOfDay ;
        }

        @Override
        public String getResultAsString() {
            return commitsPerHourOfDay.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            String dir = System.getProperty("user.dir");
            dir = dir.replace("cli","");
            StringBuilder html = new StringBuilder("");
            String datapoints = "";
            try {
                BufferedReader in = new BufferedReader(new FileReader(dir+"/webgen/Graph.html"));
                String str;
                while ((str = in.readLine()) != null) {
                    html.append(str+"\n");
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            for (Map.Entry<String,Integer> item:commitsPerHourOfDay.entrySet()){
                datapoints+="{y:"+ item.getValue() + " ,label: \'"+item.getKey()+"\'},";
            }
            return html.toString().replace("///data///",datapoints.toString()).replace("_id", "3").replace("Commits","Commits Per Hour Of Day").replace("//type_graph//","line");

        }
    }
}