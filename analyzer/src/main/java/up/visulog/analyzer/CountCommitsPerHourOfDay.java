package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountCommitsPerHourOfDay implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;// classe interne

    public CountCommitsPerHourOfDay(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(List<Commit> gitLog) {
        var result = new Result();
        for (var commit : gitLog) {
            String hour =commit.date.split(" ")[3].split(":")[0];
            var nb = result.commitsPerHourOfWeek.getOrDefault(hour, 0);
            result.commitsPerHourOfWeek.put(hour, nb + 1);
        }
        return result;
    }

    @Override
    public void run() {
        if (this.configuration.getPluginConfig("CountCommitsPerHourOfDay").isPresent()){
            result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(), this.configuration.getPluginConfig("CountCommitsPerHourOfDay").get()));
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
        private final Map<String, Integer> commitsPerHourOfWeek = new HashMap<>();// pour chaque user un nombre de commits

        Map<String, Integer> getcommitsPerHourOfWeek() {
            return commitsPerHourOfWeek;
        }

        @Override
        public String getResultAsString() {
            return commitsPerHourOfWeek.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {

            // HtmlFlow -> A ajouter
            StringBuilder html = new StringBuilder("<div>Commits per day of week: <ul>");
            for (var item : commitsPerHourOfWeek.entrySet()) {
                html.append("<li>").append(item.getKey()).append(": ").append(item.getValue()).append("</li>");
            }
            html.append("</ul></div>");
            return html.toString();
        }
    }
}