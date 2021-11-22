package up.visulog.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Lines;

//Cette classe est utillisée pour le calcul des lignes ajoutées et supprimées
//Par @Younes Salhi

public class CountLines implements AnalyzerPlugin {

    private final Configuration config;
    private Result result;

    public CountLines(Configuration config) {
        this.config = config;
    }

    
    public Result procesdiff(List<Lines> listLines) {
        Result result = new Result();
        for (Lines line : listLines) {
            //On ajoute le fichier aves les lignes ajoutées et supprimées dans un Map
            result.linesAddedDeleted.put(line.path, new Result.Pair(line.numberAdded, line.numberDeleted));
        }
        return result;
    }

    @Override
    public void run() {
        if (config.getPluginConfig("countLines").isPresent()){
            this.result = procesdiff(Lines.parseDiffFromCommand(config.getGitPath(), config.getPluginConfig("countLines").get()));
        }
    }

    @Override
    public Result getResult() {
        if (result == null)
            run();
        return result;
    }

    static class Result implements AnalyzerPlugin.Result {

        //classe interne pour le sauvgarde du nombre de lignes ajoutées et supprimées
        static class Pair {
            public int a;
            public int b;

            public Pair(int a, int b) {
                this.a = a;
                this.b = b;
            }

            @Override
            public String toString() {
                return a + " , " + b;
            }
        }

        private final Map<String, Pair> linesAddedDeleted = new HashMap<>();

        public Map<String, Pair> getLinesAddedDeleted() {
            return this.linesAddedDeleted;
        }

        @Override
        public String getResultAsString() {
            return this.linesAddedDeleted.toString();
        }

        //retourn le resultat sous le format HTML
        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<div>Lines Added and deleted : <ul>");
            for (Map.Entry<String, Pair> item : linesAddedDeleted.entrySet()) {
                html.append("<li>").append("File : " + item.getKey()).append("<ul>").append("<li>")
                        .append(" Number Lines Added : ").append(item.getValue().a).append("</li>").append("<li>")
                        .append(" Number Lines Deleted : ").append(item.getValue().b).append("</li>").append("</ul>")
                        .append("</li>");
            }
            html.append("</ul></div>");
            return html.toString();
        }

    }

}