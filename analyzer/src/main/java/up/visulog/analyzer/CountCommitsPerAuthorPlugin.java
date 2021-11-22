package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

public class CountCommitsPerAuthorPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;// classe interne

    public CountCommitsPerAuthorPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(List<Commit> gitLog) {
        var result = new Result();
        for (var commit : gitLog) {
            var nb = result.commitsPerAuthor.getOrDefault(commit.author, 0);
            result.commitsPerAuthor.put(commit.author, nb + 1);
        }
        return result;
    }

    @Override
    public void run() {
        if (this.configuration.getPluginConfig("countCommits").isPresent()){
            result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(), this.configuration.getPluginConfig("countCommits").get()));
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
        private final Map<String, Integer> commitsPerAuthor = new HashMap<>();// pour chaque user un nombre de commits

        Map<String, Integer> getCommitsPerAuthor() {
            return commitsPerAuthor;
        }

        @Override
        public String getResultAsString() {
            return commitsPerAuthor.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder builder = new StringBuilder("");
            builder.append("<canvas id=\"graph\"></canvas>");
            builder.append("<script src=\"../countCommits.js\"></script>");
            builder.append("<script>countCommits();</script>");
            String s = "graph";
            this.parseJS(s);
            return builder.toString();
        }

            // HtmlFlow -> A ajouter

        
    




         private String formatArrayString(String[] data) {
            String result = Arrays.asList(data).stream().reduce("", (a, b) -> "\"" + a + ",\"" + b + "\"");
            return "[" + result.substring(result.indexOf(",") + 1, result.length()) + "]";
        }

        private void parseJS(String id2){
            String[] names = new String[this.commitsPerAuthor.size()];
            String[] Nbcommits = new String[this.commitsPerAuthor.size()];
            int i = 0;
            
            for (Map.Entry<String, Integer> entry :this.commitsPerAuthor.entrySet()){
                names[i] = entry.getKey();
                Nbcommits[i] =  String.valueOf(entry.getValue());
                i++;
            }

            StringBuilder data = new StringBuilder("function countCommits(){");
            data.append("const graph = document.getElementById('"+id2+"').getContext('2d');");
            data.append("let myChart = new Chart(graph, {");
            data.append("type:\"pie\",");
            data.append("data: {");
            data.append("labels:" + formatArrayString(names) + ",");
            data.append("datasets: [{");
            data.append("label:\"Nombre de commits par personnes\",");
            data.append("data:" + formatArrayString(Nbcommits) + ",");
            data.append("backgroundColor:['#ED7F10','#FFCB60','#F1E2BE', '#F4661B','#B36700','#FEA347','#F3D617','#FF7F00','#E1CE9A'],");
            data.append(" hoverBorderWidth: 3,");
            data.append("}],");
            data.append("}");
            data.append("});}");

            try{
                File f = new File("..");
                String path = f.getAbsoluteFile()+"/countCommits.js";
                BufferedWriter bw = new BufferedWriter(new FileWriter(path));
                bw.write(data.toString());
                bw.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}