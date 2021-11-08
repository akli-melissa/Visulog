package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CountMergeCommits implements AnalyzerPlugin{

    private Configuration config;//the configuration
    private AnalyzerPlugin.Result resultats;//the result

    public CountMergeCommits(Configuration configs){
        this.config = configs;
    }

    static AnalyzerPlugin.Result proceslog(List<Commit> listCommits) {
        Map<String,Integer> subresults = new HashMap<String, Integer>();
        int nbMerge;
        for (Commit commit : listCommits){
            if (commit.mergedFrom != null){
                nbMerge = subresults.getOrDefault(commit.author, 0);
                subresults.put(commit.author,nbMerge+1);
            }
        }

        AnalyzerPlugin.Result result =  new AnalyzerPlugin.Result(){
            //redefinitions des methodes de l'interace
            @Override
            public String getResultAsString(){
                return subresults.toString();
            }

            @Override
            public String getResultAsHtmlDiv(){
                StringBuilder html = new StringBuilder("<div>MergeCommits per author: <ul>");
                for (Map.Entry<String,Integer> item:subresults.entrySet()){
                    html.append("<li>").append(item.getKey()).append(" : ").append(item.getValue()).append("</li>");
                }
                html.append("</ul></div>");
                return html.toString();
            }
        };

        return result;
    }

    @Override
    public void run(){
        this.resultats = proceslog(Commit.parseLogFromCommand(this.config.getGitPath()));
    }

    @Override
    public AnalyzerPlugin.Result getResult(){
        if (this.resultats == null) this.run();
        return this.resultats;
    }
}