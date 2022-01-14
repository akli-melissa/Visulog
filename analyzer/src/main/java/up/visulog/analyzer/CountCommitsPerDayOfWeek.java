package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class CountCommitsPerDayOfWeek implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;// classe interne

    public CountCommitsPerDayOfWeek(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }
    static Result processLog(List<Commit> gitLog) throws Exception {
        var result = new Result();

       for (Map.Entry<String,Integer> item:result.commitsPerDayOfWeek.entrySet()){
            Map<String, Integer> commitsPerDay = new HashMap<>();// pour chaque dimanche precis(Mon 12/20 Jan)

            for (var commit : gitLog) {
                Date date=commit.date;
                SimpleDateFormat d = new SimpleDateFormat("EEEE",Locale.ENGLISH);
                SimpleDateFormat day = new SimpleDateFormat("EEE MMM d yyyy",Locale.ENGLISH);
                if(d.format(date).equals(item.getKey())){
                    var nb = commitsPerDay.getOrDefault(day.format(date), 0);
                    commitsPerDay.put(day.format(date), nb + 1);
                } 
            }
            if(commitsPerDay.size()!=0){
                int sum=0;
                for(int val:commitsPerDay.values()){
                    sum+=val;
                }
               result.commitsPerDayOfWeek.put(item.getKey(),sum/commitsPerDay.size());
            }
        }
        return result;
    }

    @Override
    public void run() {
        if (this.configuration.getPluginConfig("countCommitsPerDayOfWeek").isPresent()){
           try{
            result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(), this.configuration.getPluginConfig("countCommitsPerDayOfWeek").get()));
            }catch(Exception e){
                System.out.println(e);           
            }
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
        private final LinkedHashMap<String, Integer> commitsPerDayOfWeek ;// pour chaque user un nombre de commits

        Result(){
            commitsPerDayOfWeek = new LinkedHashMap<>();
            commitsPerDayOfWeek.put("Sunday",0);
            commitsPerDayOfWeek.put("Monday",0);
            commitsPerDayOfWeek.put("Tuesday",0);
            commitsPerDayOfWeek.put("Wednesday",0);
            commitsPerDayOfWeek.put("Thursday",0);
            commitsPerDayOfWeek.put("Friday",0);
            commitsPerDayOfWeek.put("Saturday",0);

        }
        Map<String, Integer> getcommitsPerDayOfWeek() {
            return commitsPerDayOfWeek;
        }

        @Override
        public String getResultAsString() {
            return commitsPerDayOfWeek.toString();
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
            
            for (Map.Entry<String,Integer> item:commitsPerDayOfWeek.entrySet()){
                datapoints+="{y:"+ item.getValue() + " ,label: \'"+item.getKey()+"\'},";
            }
            return html.toString().replace("///data///",datapoints.toString()).replace("_id","2").replace("Commits","Commits Per Day Of Week").replace("//type_graph//","line");

        }
    }
}