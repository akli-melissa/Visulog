package up.visulog.analyzer;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.io.FileReader;
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
        int[] result = {0,0};
        String infoCommits = commit.commitInformations;
        if (infoCommits != null && infoCommits.length() > 0){
            String[] splitedInformations = infoCommits.split(",");
            if (splitedInformations.length == 3){
                result[0] = Integer.parseInt(splitedInformations[1].split(" ")[1]);//lignes ajoutées
                result[1] = Integer.parseInt(splitedInformations[2].split(" ")[1]);//lignes supprimées
            }else{
                //juste des insertions ou des suppressions
                if (infoCommits.contains("+")){
                    result[0] = Integer.parseInt(splitedInformations[1].split(" ")[1]);//lignes ajoutées
                }else{
                    result[1] = Integer.parseInt(splitedInformations[1].split(" ")[1]);//lignes supprimées   
                }
            }
        }
        return result;
    }

    //Take the liste of commits and return the result wich is lines added and deleted for each user
    private Result processDiff(List<Commit> commits){
        Result result = new Result();
        int[] linesAddandDel;
        String authorName = "";
        ArrayList<Integer> linesAddDel = null;
        for (Commit commit : commits){
            authorName = commit.author.substring(0, commit.author.indexOf("<"));
            linesAddandDel = this.formatCommit(commit);
            linesAddDel = result.userLines.getOrDefault(authorName, new ArrayList<Integer>(Arrays.asList(0, 0)));
            linesAddDel.set(0 ,linesAddDel.get(0) + linesAddandDel[0]);
            linesAddDel.set(1 ,linesAddDel.get(1) + linesAddandDel[1]);
            result.userLines.put( authorName, linesAddDel );
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
            String path = (new File(System.getProperty("user.dir"))).getParentFile() + "/webgen/Graph.html";
            StringBuilder html = new StringBuilder("");
            try{
                BufferedReader in = new BufferedReader(new FileReader(path));
                String str = "";
                while ((str = in.readLine()) != null) {
                    html.append(str+"\n");
                }
                in.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            String datapointsA = "",datapointsB="";
            String userName = "";
            for (Map.Entry<String,ArrayList<Integer>> entry : userLines.entrySet()){
                userName = entry.getKey();
                datapointsA+="{y:"+ entry.getValue().get(0) + " ,label: \'"+userName+"\'},";
                datapointsB+="{y:"+ entry.getValue().get(1) + " ,label: \'"+userName+"\'},";
            }
            String graph2 = html.toString();
            String graph1 = html.toString();

            graph1 = graph2.replace("///data///",datapointsA).replace("_id","4")
                    .replace("Commits","Lines Added Per User").replace("commits","lines").replace("//type_graph//","line");
            graph2 = graph2.replace("///data///",datapointsB).replace("_id","5")
                    .replace("Commits","Lines Deleted Per User").replace("commits","lines").replace("//type_graph//","line");

            return graph1 + graph2;
        }

    }
    
}
