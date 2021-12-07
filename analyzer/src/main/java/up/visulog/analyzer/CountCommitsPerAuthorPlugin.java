package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.*;
import java.text.SimpleDateFormat; 


public class CountCommitsPerAuthorPlugin implements AnalyzerPlugin {
    private String dateDebut;
    private String dateFin;
    static private Date debut=null;
    static private Date fin=null;
    private final Configuration configuration;
    private Result result;// classe interne

    public CountCommitsPerAuthorPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }
    public CountCommitsPerAuthorPlugin(Configuration generalConfiguration, String dateDebut , String dateFin)throws Exception {
        this.configuration = generalConfiguration;
        this.dateDebut = dateDebut; 
        debut = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.ENGLISH).parse(dateDebut+" 00:00:00");
        this.dateFin = dateFin ;
        fin = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.ENGLISH).parse(dateFin+" 23:59:59");
    }
    static Result processLog(List<Commit> gitLog) throws Exception {
        var result = new Result();
        for (var commit : gitLog) {
            if(debut == null && fin == null){
                var nb = result.commitsPerAuthor.getOrDefault(commit.author, 0);
                result.commitsPerAuthor.put(commit.author, nb + 1);
            }
            else{
                String sDate =commit.date;
                Date date = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy z",Locale.ENGLISH).parse(sDate); 
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
        if (this.configuration.getPluginConfig("countCommits"+"/"+dateDebut+"/"+dateFin).isPresent()){
            try{
                result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(), this.configuration.getPluginConfig("countCommits"+"/"+dateDebut+"/"+dateFin).get()));
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
    static class Result implements AnalyzerPlugin.Result {
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
            return html.toString().replace("///data///",datapoints.toString()).replace("Commit","Commit Per User").replace("//type_graph//","pie").replace("_id","9");
        }
    }
}