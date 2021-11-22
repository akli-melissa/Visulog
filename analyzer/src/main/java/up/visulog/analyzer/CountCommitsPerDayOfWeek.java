package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountCommitsPerDayOfWeek implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;// classe interne

    public CountCommitsPerDayOfWeek(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(List<Commit> gitLog) {
        var result = new Result();
        for (var commit : gitLog) {
            String day =commit.date.split(" ")[0];
            var nb = result.commitsPerDayOfWeek.getOrDefault(day, 0);
            result.commitsPerDayOfWeek.put(day, nb + 1);
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
        private final Map<String, Integer> commitsPerDayOfWeek = new HashMap<>();// pour chaque user un nombre de commits

        Map<String, Integer> getcommitsPerDayOfWeek() {
            return commitsPerDayOfWeek;
        }

        @Override
        public String getResultAsString() {
            return commitsPerDayOfWeek.toString();
        }
        
        @Override
        public String getResultAsHtmlDiv() {

            // HtmlFlow -> A ajouter
            StringBuilder html = new StringBuilder("<div>Commits per day of week: <ul>");
            for (var item : commitsPerDayOfWeek.entrySet()) {
                html.append("<li>").append(item.getKey()).append(": ").append(item.getValue()).append("</li>");
            }
            html.append("</ul></div>");
            return html.toString();
        }
    }
}