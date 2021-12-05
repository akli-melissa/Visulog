package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class CountCommitsPerDayOfMonth implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;// classe interne

    public CountCommitsPerDayOfMonth(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(List<Commit> gitLog) {
        var result = new Result();
       for (Map.Entry<String,Integer> item:result.commitsPerDayOfMonth.entrySet()){
            Map<String, Integer> commitsPerDay = new HashMap<>();// pour chaque dimanche precis(Mon 12/20 Jan)

            for (var commit : gitLog) {
                String [] dateTable=commit.date.split(" ");
                String day =dateTable[1]+dateTable[2]+dateTable[4];
                if(dateTable[2].equals(item.getKey())){
                    var nb = commitsPerDay.getOrDefault(day, 0);
                    commitsPerDay.put(day, nb + 1);
                } 
            }
            if(commitsPerDay.size()!=0){
                int sum=0;
                for(int val:commitsPerDay.values()){
                    sum+=val;
                }
               result.commitsPerDayOfMonth.put(item.getKey(),sum/commitsPerDay.size());
            }
        }
        return result;
    }

    @Override
    public void run() {
        if (this.configuration.getPluginConfig("countCommitsPerDayOfMonth").isPresent()){
            result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(), this.configuration.getPluginConfig("countCommitsPerDayOfMonth").get()));
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
        private final LinkedHashMap<String, Integer> commitsPerDayOfMonth ;// pour chaque user un nombre de commits

        Result(){
            commitsPerDayOfMonth = new LinkedHashMap<>();
            for(int i=1;i<32;i++){
                commitsPerDayOfMonth.put(String.valueOf(i),0);
            }
        }
        Map<String, Integer> getcommitsPerDayOfMonth() {
            return commitsPerDayOfMonth;
        }

        @Override
        public String getResultAsString() {
            return commitsPerDayOfMonth.toString();
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
            
            for (Map.Entry<String,Integer> item:commitsPerDayOfMonth.entrySet()){
                datapoints+="{y:"+ item.getValue() + " ,label: \'"+item.getKey()+"\'},";
            }
            return html.toString().replace("///data///",datapoints.toString());

        }
    }
}