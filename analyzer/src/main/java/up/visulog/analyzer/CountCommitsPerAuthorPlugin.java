package up.visulog.analyzer;


import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.*;
import java.text.SimpleDateFormat; 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CountCommitsPerAuthorPlugin implements AnalyzerPlugin {
    private String extension = "";
    private Date debut=null;
    private Date fin=null;
    private final Configuration configuration;
    private Result result;// classe interne

    public CountCommitsPerAuthorPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }
    public CountCommitsPerAuthorPlugin(Configuration generalConfiguration, String dateDebut , String dateFin)throws Exception {
        this.configuration = generalConfiguration;
        extension = "/"+dateDebut+"/"+dateFin; 
        debut = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.ENGLISH).parse(dateDebut+" 00:00:00");
       fin = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.ENGLISH).parse(dateFin+" 23:59:59");
    }
    Result processLog(List<Commit> gitLog) throws Exception {
        var result = new Result();
        for (var commit : gitLog) {
            if(debut == null && fin == null){
                var nb = result.commitsPerAuthor.getOrDefault(commit.author, 0);
                result.commitsPerAuthor.put(commit.author, nb + 1);
            }
            else{
                Date date =commit.date;
                //Date date = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy z",Locale.ENGLISH).parse(sDate); 
                if(date.compareTo(debut) >= 0 && date.compareTo(fin) <= 0){
                    var nb = result.commitsPerAuthor.getOrDefault(commit.author, 0);
                    result.commitsPerAuthor.put(commit.author, nb + 1);
                 }
            }
        }
    return result;
}

    @Override
    public void run() {
        if (this.configuration.getPluginConfig("countCommits"+extension).isPresent()){
            try{
                result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(), this.configuration.getPluginConfig("countCommits"+extension).get()));
            }catch(Exception e){
                System.out.println(e);           }
            
        }
    }

    @Override
    public Result getResult() {
        if (result == null)
            run();
        return result;
    }

    // Implementation de la sous interface Result de AnalyzerPlugin
    class Result implements AnalyzerPlugin.Result {
        private final Map<String, Integer> commitsPerAuthor = new HashMap<>();// pour chaque user un nombre de commits

        Map<String, Integer> getCommitsPerAuthor() {
            return commitsPerAuthor;
        }

        @Override
        public String getResultAsString() {
            return commitsPerAuthor.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {

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
            
            for (Map.Entry<String,Integer> item:commitsPerAuthor.entrySet()){
                datapoints+="{y:"+ item.getValue() + " ,label: \'"+item.getKey()+"\'},";
            }
            String id = "9",title = "Commit Per User ";
            if (debut!=null && fin!=null){
                id = "10";
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String debutDate= formatter.format(debut);
                String findate= formatter.format(fin);
                title +=  "between " + debutDate + " and " + findate; 
            }
            return html.toString().replace("///data///",datapoints.toString()).replace("Commits",title).replace("//type_graph//","pie").replace("_id",id);
        }
    }
}