package up.visulog.analyzer;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

public class User_Activity implements AnalyzerPlugin{

    private Configuration configuration;
    private Result result;
    private String authorStat;
    public User_Activity(Configuration configuration,String author){
        this.configuration = configuration;
        authorStat = author;
    }
    

    //retourne le nombre de lignes ajoutées et supprimées par le commit
    private int[] getAddandDel(List<Commit> commits,String author){
        int[] result = {0,0};
        for (Commit commit : commits){
            String authorName = commit.author.substring(0, commit.author.indexOf("<"));
            if (!(authorName.replace(" ","").equals(authorStat.replace(" ", "")))) continue;
            String infoCommits = commit.commitInformations;
            if (infoCommits != null && infoCommits.length() > 0){
                String[] splitedInformations = infoCommits.split(",");
                if (splitedInformations.length == 3){
                    result[0] += Integer.parseInt(splitedInformations[1].split(" ")[1]);//lignes ajoutées
                    result[1] += Integer.parseInt(splitedInformations[2].split(" ")[1]);//lignes supprimées
                }else{
                    //juste des insertions ou des suppressions
                    if (infoCommits.contains("+")){
                        result[0] += Integer.parseInt(splitedInformations[1].split(" ")[1]);//lignes ajoutées
                    }else{
                        result[1] += Integer.parseInt(splitedInformations[1].split(" ")[1]);//lignes supprimées   
                    }
                }
            }
        }
           return result;
    }

    //Take the liste of commits and return the result wich is lines added and deleted for each user
    private int getAuthorMergecommit(List<Commit> commits,String author){
        int nbMerge =0;
        for (Commit commit : commits){
            String authorName = commit.author.substring(0, commit.author.indexOf("<"));
            if (!authorName.contains(author)) continue;
            if (commit.mergedFrom != null && commit.mergedFrom.length() > 0){
                nbMerge++;
            }
        }
        return nbMerge;
    }
    private int getCommits(List<Commit> commits,String author){
        var nb =0;
        for (var commit : commits) {
            String authorName = commit.author.substring(0, commit.author.indexOf("<"));
            if (authorName.replace(" ","").equals(authorStat.replace(" ", ""))){
                nb++;
            }
        }
        return nb;
    }

    private Result processDiff(List<Commit> commits){
        Result result = new Result();
        authorStat = authorStat.replace("_", " ");
        int [] linesAddandDel = this.getAddandDel(commits,authorStat);
        result.author = authorStat;
        result.results_dict.put("Lines added", linesAddandDel[0]);
        result.results_dict.put("Lines deleted", linesAddandDel[1]);
        result.results_dict.put("Merge commits", this.getAuthorMergecommit(commits, authorStat));
        result.results_dict.put("Commits", this.getCommits(commits, authorStat));

        return result;
    }

    @Override
    public void run() {
        if (configuration.getPluginConfig("userStats/"+authorStat).isPresent()) {
            System.out.println(authorStat);
            this.result = this.processDiff(
                Commit.parseLogFromCommand( configuration.getGitPath(), 
                                            configuration.getPluginConfig("userStats/"+authorStat).get()                  
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
        private final Map<String,Integer> results_dict = new HashMap<>();//String -> username ; arraylist->(lines added , lines deleted)
        String author;
        @Override
        public String getResultAsString() {
            return results_dict.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {

            String dir = System.getProperty("user.dir");
            dir = dir.replace("cli","");
            StringBuilder html = new StringBuilder("");
            try {
                BufferedReader in = new BufferedReader(new FileReader(dir+"/webgen/User.html"));
                String str;
                while ((str = in.readLine()) != null) {
                    html.append(str+"\n");
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String r = html.toString();
            r = r.replace("Yacine", author);
            r = r.replace("COMMIT", results_dict.get("Commits").toString());
            r = r.replace("DELINE",Integer.toString(results_dict.get("Lines deleted")));
            r = r.replace("COUNTMERGE",Integer.toString(results_dict.get("Merge commits")));
            r = r.replace("ADDLINE",Integer.toString(results_dict.get("Lines added")));
            return r.replace("_id","11");
        }

    }   
}
