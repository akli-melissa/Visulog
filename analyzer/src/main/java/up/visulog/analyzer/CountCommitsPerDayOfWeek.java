package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CountCommitsPerDayOfWeek implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;// classe interne

    public CountCommitsPerDayOfWeek(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(List<Commit> gitLog) {
        var result = new Result();

       for (Map.Entry<String,Integer> item:result.commitsPerDayOfWeek.entrySet()){
            Map<String, Integer> commitsPerDay = new HashMap<>();// pour chaque dimanche precis(Mon 12/20 Jan)

            for (var commit : gitLog) {
                String [] dateTable=commit.date.split(" ");
                String day =dateTable[0]+dateTable[1]+dateTable[2]+dateTable[4];
                if(day.contains(item.getKey())){
                    var nb = commitsPerDay.getOrDefault(day, 0);
                    commitsPerDay.put(day, nb + 1);
                } 
            }
            if(commitsPerDay.size()!=0){
                int sum=0;
                for(int val:commitsPerDay.values()){
                    sum+=val;
                }
               result.commitsPerDayOfWeek.put(item.getKey(),sum/commitsPerDay.size());
            }
        }
        return result;
    }

    @Override
    public void run() {
        if (this.configuration.getPluginConfig("countCommitsPerDayOfWeek").isPresent()){
            result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(), this.configuration.getPluginConfig("countCommitsPerDayOfWeek").get()));
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
        private final Map<String, Integer> commitsPerDayOfWeek ;// pour chaque user un nombre de commits

        Result(){
            commitsPerDayOfWeek = new HashMap<>();
            commitsPerDayOfWeek.put("Sun",0);
            commitsPerDayOfWeek.put("Mon",0);
            commitsPerDayOfWeek.put("Tue",0);
            commitsPerDayOfWeek.put("Wed",0);
            commitsPerDayOfWeek.put("Thu",0);
            commitsPerDayOfWeek.put("Fri",0);
            commitsPerDayOfWeek.put("Sat",0);

        }
        Map<String, Integer> getcommitsPerDayOfWeek() {
            return commitsPerDayOfWeek;
        }

        @Override
        public String getResultAsString() {
            return commitsPerDayOfWeek.toString();
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
            
            for (Map.Entry<String,Integer> item:commitsPerDayOfWeek.entrySet()){
                datapoints+="{y:"+ item.getValue() + " ,label: \'"+item.getKey()+"\'},";
            }
            return html.toString().replace("///data///",datapoints.toString());

        }
    }
}