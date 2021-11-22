package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.*;

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
            ArrayList<String> labels = new ArrayList<String>();
            ArrayList<Integer> data = new ArrayList<Integer>();
            for (Map.Entry<String,Integer> item:commitsPerDayOfWeek.entrySet()){
                labels.add("\""+item.getKey()+"\"");
                data.add(item.getValue());
            }
            StringBuilder html = new StringBuilder("<div style=\"width : 75%\">");
            html.append("\n<canvas id=\"myChart\"></canvas>");
            html.append("\n</div>");
            html.append("\n<script src=\"https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.9.3/Chart.bundle.min.js\" integrity=\"sha512-vBmx0N/uQOXznm/Nbkp7h0P1RfLSj0HQrFSzV8m7rOGyj30fYAOKHYvCNez+yM8IrfnW0TCodDEjRqf6fodf/Q==\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\"></script>");
            html.append("\n<script>");
           // html.append("\nChart.defaults.global.title.display = true;");
           // html.append("\nChart.defaults.global.title.text = \"PAS DE TITRE\";");
            html.append("\n</script>");
            html.append("\n<script>");
            html.append("\nvar ctx = new Chart(document.getElementById('myChart').getContext('2d'));");
            html.append("\n var data = {");
            html.append("\nlabels:").append(labels).append(",");
            html.append("\ndatasets: [{label: 'Commits per weekday',data:").append(data).append(",fill: false,borderColor: 'rgb(75, 192, 192)',tension: 0.1}]};");
            html.append("\nvar chart = new Chart(ctx,{");
            html.append("\ntype: 'line',");
            html.append("\ndata: data,");
            html.append("\noptions:{");
            html.append("\ntitle : {");
            //html.append("\ntext : \"Le titre de la charte graphique\"");
            html.append("\n} } });");
            html.append("\n </script>\n");
            
            return html.toString();
        }
    }
}