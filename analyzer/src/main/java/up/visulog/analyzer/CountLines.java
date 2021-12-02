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

    public Result procesdiff(List<Lines> listLines) {
        Result result = new Result();
        for (Lines line : listLines) {
            // On ajoute le fichier aves les lignes ajoutées et supprimées dans un Map
            result.linesAddedDeleted.put(line.path, new Result.Pair(line.numberAdded, line.numberDeleted));
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
            String path = (new File(System.getProperty("user.dir"))).getParentFile() + "/webgen/countLines.html";
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

            String fileNames = "";
            String data1 = "";
            String data2 = "";

            int i = 0;
            int size = this.linesAddedDeleted.size();

            for (Map.Entry<String,Pair> data:this.linesAddedDeleted.entrySet()) {
                i++;
                fileNames += "\"" + data.getKey() + ((i < size) ? "\"," : "\"");
                data1 += "\"" + String.valueOf(data.getValue().a) + ((i < size) ? "\"," : "\"");
                data2 += "\"" + String.valueOf(data.getValue().b) + ((i < size) ? "\"," : "\"");
            }

            String result = html.toString();

            result = result.replace("/*data_1*/",fileNames);
            result = result.replace("/*data_2*/",data1);
            result = result.replace("/*data_3*/",data2);

            return result;
        }
    }
}
