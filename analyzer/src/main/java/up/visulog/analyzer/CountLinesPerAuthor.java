package up.visulog.analyzer;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

public class CountLinesPerAuthor implements AnalyzerPlugin{

    private Configuration configuration;
    private Result result;

    public CountLinesPerAuthor(Configuration configuration){
        this.configuration = configuration;
    }

    //retourne le nombre de lignes ajoutées et supprimées par le commit
    private int[] formatCommit(Commit commit){
        int[] result = new int[2];
        String infoCommits = commit.commitInformations;
        if (infoCommits != null && infoCommits.length() > 0){
            String[] splitedInformations = infoCommits.split(",");
            result[0] = Integer.parseInt(splitedInformations[1].split(" ")[1]);//lignes ajoutées
            result[1] = Integer.parseInt(splitedInformations[2].split(" ")[1]);//lignes supprimées
        }
        return result;
    }

    //Take the liste of commits and return the result wich is lines added and deleted for each user
    private Result processDiff(List<Commit> commits){
        Result result = new Result();
        int[] linesAddandDel;
        ArrayList<Integer> linesAddDel = null;
        for (Commit commit : commits){
            linesAddandDel = this.formatCommit(commit);
            linesAddDel = result.userLines.getOrDefault(commit.author, new ArrayList<Integer>(Arrays.asList(0, 0)));
            linesAddDel.set(0 ,linesAddDel.get(0) + linesAddandDel[0]);
            linesAddDel.set(1 ,linesAddDel.get(1) + linesAddandDel[1]);
            result.userLines.put( commit.author, linesAddDel );
        }
        return result;
    }

    @Override
    public void run() {
        if (configuration.getPluginConfig("countLinesPerAuthor").isPresent()) {
            this.result = this.processDiff(
                Commit.parseLogFromCommand( configuration.getGitPath(), 
                                            configuration.getPluginConfig("countLinesPerAuthor").get()                  
            ));
        }
    }

    @Override
    public Result getResult() {
        if (result == null)
            run();
        return result;
    }

    static class Result implements AnalyzerPlugin.Result {
        private final Map<String,ArrayList<Integer>> userLines = new HashMap<>();//String -> username ; arraylist->(lines added , lines deleted)
        
        @Override
        public String getResultAsString() {
            return userLines.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<div>Commits per author: <ul>");
            for (var item : userLines.entrySet()) {
                html.append("<li>").append(item.getKey()).append(": ").append(item.getValue().get(0)).append(";"+item.getValue().get(1)).append("</li>");
            }
            html.append("</ul></div>");
            return html.toString();
        }

    }
    
}
