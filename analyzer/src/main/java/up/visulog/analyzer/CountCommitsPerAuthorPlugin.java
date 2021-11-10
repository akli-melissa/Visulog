package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountCommitsPerAuthorPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;//classe interne

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
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath()));
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }


    //Implementation de la sous interface Result de AnalyzerPlugin 
    static class Result implements AnalyzerPlugin.Result {
        private final Map<String, Integer> commitsPerAuthor = new HashMap<>();//pour chaque user un nombre de commits

        Map<String, Integer> getCommitsPerAuthor() {
            return commitsPerAuthor;
        }

        @Override
        public String getResultAsString() {
            return commitsPerAuthor.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {

            //HtmlFlow -> A ajouter
            StringBuilder html = new StringBuilder("<div>Commits per author: <ul>");
            StringBuilder style = new StringBuilder("<head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>Pie Chart</title><style>.piechart {display: block;width: 400px;height: 400px;border-radius: 50%;background-image: conic-gradient(");
            int red =  50;
            for (var item : commitsPerAuthor.entrySet()){
                style.append("rgb(168,"+String.valueOf(red)+",50) 15deg,");
            }
            style.append(");}.section {display: block;float: left;margin: 50px 50px 20px 50px;margin-left: 2%;}.box {float: left;height: 10px;width: 10px;margin: 3px 10px 0px 0px;border: 1px solid black;clear: both;background-color: red;}</style></head>");
            // for (var item : commitsPerAuthor.entrySet()) {
            //     html.append("<li>").append(item.getKey()).append(": ").append(item.getValue()).append("</li>");
            // }
            // html.append("</ul></div>");
            style.append("<div class=̣\"piechart\"></div>");
            // return html.toString();
            return style.toString();
        }
    }
}