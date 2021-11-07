package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;
import up.visulog.gitrawdata.Commit;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CountMergeCommits implements AnalyzerPlugin{

    private Configuration config;//the configuration
    private Result resultats;//the result
    PluginConfig pluginConfig;//the plugin config

    public CountMergeCommits(Configuration configs,PluginConfig pluginConfig){
        this.config = configs;
        this.pluginConfig = pluginConfig;
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
        this.resultats = proceslog(Commit.parseLogFromCommand(this.config.getGitPath(), this.pluginConfig));
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
            StringBuilder html = new StringBuilder("<div>MergeCommits per author: <ul>");
            for (Map.Entry<String,Integer> item:MergeCommit.entrySet()){
                html.append("<li>").append(item.getKey()).append(" : ").append(item.getValue()).append("</li>");
            }
            html.append("</ul></div>");
            return html.toString();
        }
    }
}