package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

public class CountMergeCommits implements AnalyzerPlugin{

    private Configuration config;//the configuration
    private Result resultats;//the result

    public CountMergeCommits(Configuration configs){
        this.config = configs;
    }

    static Result proceslog(List<Commit> listCommits) {
        Result resultat = new Result();
        int nbMerge;
        for (Commit commit : listCommits){
            if (commit.mergedFrom != null && commit.mergedFrom.length() > 0){
                nbMerge = resultat.MergeCommit.getOrDefault(commit.author, 0);
                resultat.MergeCommit.put(commit.author,nbMerge+1);
            }
        }
        return resultat;
    }

    @Override
    public void run(){
        if (this.config.getPluginConfig("countMerge").isPresent()){
            this.resultats = proceslog(Commit.parseLogFromCommand(this.config.getGitPath(), this.config.getPluginConfig("countMerge").get()));
        }
    }

    @Override
    public AnalyzerPlugin.Result getResult(){
        if (this.resultats == null) this.run();
        return this.resultats;
    }

    static class Result implements AnalyzerPlugin.Result{

        private final Map<String,Integer> MergeCommit;

        public Result(){
            this.MergeCommit = new HashMap<String,Integer>();
        }

        public Map<String,Integer> getMergeCommit(){
            return this.MergeCommit;
        }

        @Override
        public String getResultAsString(){
            return MergeCommit.toString();
        }

        @Override
        public String getResultAsHtmlDiv(){
            StringBuilder build = new StringBuilder("");
            build.append("<canvas id=\"graph\"></canvas>");
            build.append("<script src=\"countMerge.js\"></script>");
            build.append("<script>countMerge();</script>");
            this.parseJS("graph");
            return build.toString();
            
        }

        private String formatArrayString(String[] data) {
            String result = Arrays.asList(data).stream().reduce("", (a, b) -> "\"" + a + ",\"" + b + "\"");
            return "[" + result.substring(result.indexOf(",") + 1, result.length()) + "]";
        }


        private void parseJS(String id3){
            String[] names = new String[this.MergeCommit.size()];
            String[] NbMergeCommits = new String[this.MergeCommit.size()];
            int i = 0;
            
            for (Map.Entry<String, Integer> entry :this.MergeCommit.entrySet()){
                names[i] = entry.getKey();
                NbMergeCommits[i] =  String.valueOf(entry.getValue());
                i++;
        }


             StringBuilder data = new StringBuilder("function countMerge(){");
            data.append("const graph = document.getElementById('"+id3+"').getContext('2d');");
            data.append("let myChart = new Chart(graph, {");
            data.append("type:\"doughnut\",");
            data.append("data: {");
            data.append("labels:" + formatArrayString(names) + ",");
            data.append("datasets: [{");
            data.append("label:\"Nombre de Merge Commits par personne\",");
            data.append("data:" + formatArrayString(NbMergeCommits) + ",");
            data.append("backgroundColor:['#117a65','#7d3c98','#2e86c1', '#F4661B','#e74c3c','#f1c40f','#F3D617','#FF7F00','#E1CE9A'],");
            data.append(" hoverBorderWidth: 3,");
            data.append("}],");
            data.append("}");
            data.append("});}");

            try{
                File f = new File("");
                String path = f.getAbsoluteFile()+"/countMerge.js";
                BufferedWriter bw = new BufferedWriter(new FileWriter(path));
                bw.write(data.toString());
                bw.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
   }



}