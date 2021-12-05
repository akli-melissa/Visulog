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
            for (var commit : gitLog) {
                String day =commit.date.split(" ")[2];
                var nb = result.commitsPerDayOfMonth.getOrDefault(day, 0);
                result.commitsPerDayOfMonth.put(day, nb + 1);
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
        private final Map<String, Integer> commitsPerDayOfMonth = new HashMap<>();// pour chaque user un nombre de commits

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