package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CountCommitsPerHourOfDay implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;// classe interne

    public CountCommitsPerHourOfDay(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }
    static Result processLog(List<Commit> gitLog) {
        var result = new Result();
       for (Map.Entry<String,Integer> item:result.commitsPerHourOfDay.entrySet()){
            Map<String, Integer> commitsPerHour = new HashMap<>();// pour chaque dimanche precis(Mon 12/20 Jan)

            for (var commit : gitLog) {
                String [] dateTable=commit.date.split(" ");
                String hour =dateTable[3].split(":")[0];
                if(hour.equals(item.getKey())){
                    var nb = commitsPerHour.getOrDefault(hour, 0);
                    commitsPerHour.put(hour, nb + 1);
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
            result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(),
                    this.configuration.getPluginConfig("countCommitsPerHourOfDay").get()));
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
                commitsPerHourOfDay.put(String.valueOf(i),0);
            }
        }                                                                       // commits

        Map<String, Integer> getcommitsPerHourOfWeek() {
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
                BufferedReader in = new BufferedReader(new FileReader(dir+"/html/Graph.html"));
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
            return html.toString().replace("///data///",datapoints.toString());

        }
    }
}