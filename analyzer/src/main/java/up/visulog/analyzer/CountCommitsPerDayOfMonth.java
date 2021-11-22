package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.*;

public class CountCommitsPerDayOfMonth implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;// classe interne

    public CountCommitsPerDayOfMonth(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(List<Commit> gitLog) {
        var result = new Result();
        for (var commit : gitLog) {
            String day =commit.date.split(" ")[2];
            var nb = result.commitsPerDayOfMonth.getOrDefault(day, 0);
            result.commitsPerDayOfMonth.put(day, nb + 1);
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
             // HtmlFlow -> A ajouter
            StringBuilder html = new StringBuilder("<div>Commits per day of month: <ul>");
            for (var item : commitsPerDayOfMonth.entrySet()) {
                html.append("<li>").append(item.getKey()).append(": ").append(item.getValue()).append("</li>");
            }
            html.append("</ul></div>");
            return html.toString();
        }
    }
}