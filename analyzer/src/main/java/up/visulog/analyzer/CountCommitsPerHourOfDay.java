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
        for (var commit : gitLog) {
            String hour = commit.date.split(" ")[3].split(":")[0];
            var nb = result.commitsPerHourOfDay.getOrDefault(hour, 0);
            result.commitsPerHourOfDay.put(hour, nb + 1);
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
        private final Map<String, Integer> commitsPerHourOfDay = new HashMap<>();// pour chaque user un nombre de
                                                                                  // commits

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
            return html.toString().replace("///data///",datapoints.toString()).replace("_id", "3");

        }
    }
}