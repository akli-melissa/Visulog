package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

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

        public String getResultAsHtmlDiv(){
            String dir = System.getProperty("user.dir");
            dir = dir.replace("cli","");
            StringBuilder html = new StringBuilder("");
            String datapoints = "";
            try {
                BufferedReader in = new BufferedReader(new FileReader(dir+"/webgen/Graph.html"));
                String str;
                while ((str = in.readLine()) != null) {
                    html.append(str+"\n");
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            for (Map.Entry<String,Integer> item:MergeCommit.entrySet()){
                datapoints+="{y:"+ item.getValue() + " ,label: \'"+item.getKey()+"\'},";
            }
            return html.toString().replace("///data///",datapoints.toString()).replace("Commit","Merge Commit Per User").replace("//type_graph//","pie").replace("_id","8");
        }
   }



}