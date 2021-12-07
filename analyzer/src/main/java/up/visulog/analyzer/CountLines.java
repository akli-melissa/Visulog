package up.visulog.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Lines;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

//Cette classe est utillisée pour le calcul des lignes ajoutées et supprimées
//Par @Younes Salhi

public class CountLines implements AnalyzerPlugin {

    private final Configuration config;
    private Result result;

    public CountLines(Configuration config) {
        this.config = config;
    }

    public static Result procesdiff(List<Lines> listLines) {
        Result result = new Result();
        for (Lines line : listLines) {
            if (!line.path.contains("DS_Store")){
                // On ajoute le fichier aves les lignes ajoutées et supprimées dans un Map
                var data = result.linesAddedDeleted.getOrDefault(line.path, new Result.Pair(0, 0));
                data.a += line.numberAdded;
                data.b += line.numberDeleted;
                result.linesAddedDeleted.put( line.path , data );
            }
        }
        return result;
    }

    @Override
    public void run() {
        if (config.getPluginConfig("countLines").isPresent()) {
            this.result = procesdiff(
                    Lines.parseDiffFromCommand(config.getGitPath(), config.getPluginConfig("countLines").get())
            );
        }
    }

    @Override
    public Result getResult() {
        if (result == null)
            run();
        return result;
    }

    static class Result implements AnalyzerPlugin.Result {

        // classe interne pour le sauvgarde du nombre de lignes ajoutées et supprimées
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

        
        // retourn le resultat sous le format HTML
        @Override
        public String getResultAsHtmlDiv() {
            String path = (new File(System.getProperty("user.dir"))).getParentFile() + "/webgen/Graph.html";
            StringBuilder html = new StringBuilder("");
            try {
                BufferedReader in = new BufferedReader(new FileReader(path));
                String str = "";
                while ((str = in.readLine()) != null) {
                    html.append(str+"\n");
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String fileName = "";
            String dataPoint1 = "";
            String dataPoint2 = "";

            for (Map.Entry<String,Pair> data:this.linesAddedDeleted.entrySet()) {
                fileName = data.getKey() ;
                if (data.getValue().a!=0) dataPoint1 += "{y:"+ data.getValue().a + " ,label: \'"+fileName+"\'},";
                if (data.getValue().b!=0) dataPoint2 += "{y:"+ data.getValue().b + " ,label: \'"+fileName+"\'},";
            }

            String graph1 = html.toString();
            String graph2 = html.toString();

            graph1 = graph1.replace("///data///",dataPoint1).replace("_id","6")
                    .replace("Commits","Lines Added").replace("commits","lines").replace("//type_graph//","line");
            
            graph2 = graph2.replace("///data///",dataPoint2).replace("_id","7")
                .replace("Commits","Lines Deleted").replace("commits","lines").replace("//type_graph//","line");

            return graph1 + graph2;
        }
    }
}
