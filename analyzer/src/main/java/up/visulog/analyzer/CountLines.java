package up.visulog.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Lines;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

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

        // to format the arraysString result ex: string["younes","younes"] ->
        // "['younes','younes']"
        private String formatArrayString(String[] data) {
            String result = Arrays.asList(data).stream().reduce("", (a, b) -> "\"" + a + ",\"" + b + "\"");
            return "[" + result.substring(result.indexOf(",") + 1, result.length()) + "]";
        }

        //generate the JsCode to draw the graph
        private String genJsCode(String params1[],String params2[],String id,String nameFonction,String title){
            StringBuilder data = new StringBuilder("function "+nameFonction+"(){");
            data.append("const graph = document.getElementById('"+id+"').getContext('2d');");
            data.append("let myChart = new Chart(graph, {");
            data.append("type:\"bar\",");
            data.append("data: {");
            data.append("labels:" + formatArrayString(params1) + ",");
            data.append("datasets: [{");
            data.append("label:\""+title+"\",");
            data.append("data:" + formatArrayString(params2) + ",");
            data.append("backgroundColor:['#003f5c','#7a5195','#ef5675', '#ffa600'],");
            data.append(" hoverBorderWidth: 3,");
            data.append("}],");
            data.append("}");
            data.append("});}\n");
            return data.toString();
        }

        // generation du code javaScript
        private void parseJs(String id1,String id2) {
            //get all the data from the map
            String filesName[] = new String[this.linesAddedDeleted.size()];
            String linesAdded[] = new String[this.linesAddedDeleted.size()];
            String linesDeleted[] = new String[this.linesAddedDeleted.size()];
            int i = 0;
            
            for (Map.Entry<String, Pair> entry :this.linesAddedDeleted.entrySet()){
                filesName[i] = entry.getKey();
                linesAdded[i] = String.valueOf(entry.getValue().a);
                linesDeleted[i] = String.valueOf(entry.getValue().b);
                i++;
            }

            String LinesAdded = this.genJsCode(filesName, linesAdded, id1 , "linesAdded" , "Ligne Ajoutées");
            String LinesDeleted = this.genJsCode(filesName, linesDeleted, id2 , "linesDeleted" , "Lignes Supprimées");

            //ecrire dans le fichier js
            try{
                File f = new File("..");
                String path = f.getAbsoluteFile()+"/webgen/ressources/fichierJS/countLines.js";
                BufferedWriter bw = new BufferedWriter(new FileWriter(path));
                String data = LinesAdded + "\n" + LinesDeleted;
                bw.write(data);
                bw.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        // retourn le resultat sous le format HTML
        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder builder = new StringBuilder("");
            builder.append("<canvas id=\"graphA\"></canvas>");
            builder.append("<canvas id=\"graphB\"></canvas>");
            builder.append("<script src=\"fichierJS/countLines.js\"></script>");
            builder.append("<script>linesAdded();linesDeleted();</script>");
            this.parseJs("graphA","graphB");//generer les graphs
            return builder.toString();
        }

    }

}