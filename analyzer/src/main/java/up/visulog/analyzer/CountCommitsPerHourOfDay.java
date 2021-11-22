package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.*;

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
        if (this.configuration.getPluginConfig("countCommitsPerHourOfDay").isPresent()){
            result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(), this.configuration.getPluginConfig("countCommitsPerHourOfDay").get()));
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
            ArrayList<String> labels = new ArrayList<String>();
            ArrayList<Integer> data = new ArrayList<Integer>();
            for (Map.Entry<String,Integer> item:commitsPerHourOfWeek.entrySet()){
                labels.add("\""+item.getKey()+"\"");
                data.add(item.getValue());
            }
            StringBuilder html = new StringBuilder("<div style=\"width : 75%\">");
            html.append("\n<canvas id=\"myChart\"></canvas>");
            html.append("\n</div>");
            html.append("\n<script src=\"https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.9.3/Chart.bundle.min.js\" integrity=\"sha512-vBmx0N/uQOXznm/Nbkp7h0P1RfLSj0HQrFSzV8m7rOGyj30fYAOKHYvCNez+yM8IrfnW0TCodDEjRqf6fodf/Q==\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\"></script>");
            html.append("\n<script>");
            html.append("\n</script>");
            html.append("\n<script>");
            html.append("\nvar ctx = new Chart(document.getElementById('myChart').getContext('2d'));");
            html.append("\n var data = {");
            html.append("\nlabels:").append(labels).append(",");
            html.append("\ndatasets: [{label: 'Commits per day hour',data:").append(data).append(",fill: false,borderColor: 'rgb(75, 192, 192)',tension: 0.1}]};");
            html.append("\nvar chart = new Chart(ctx,{");
            html.append("\ntype: 'line',");
            html.append("\ndata: data,");
            html.append("\noptions:{");
            html.append("\ntitle : {");
            html.append("\n} } });");
            html.append("\n </script>\n");
            
            return html.toString();
        }
    }
}
