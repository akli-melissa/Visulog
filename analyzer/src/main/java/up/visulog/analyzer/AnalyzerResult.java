package up.visulog.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class AnalyzerResult {
    public List<AnalyzerPlugin.Result> getSubResults() {
        return subResults;
    }

    private final List<AnalyzerPlugin.Result> subResults;

    public AnalyzerResult(List<AnalyzerPlugin.Result> subResults) {
        this.subResults = subResults;
    }

    @Override
    public String toString() {
        return subResults.stream().map(AnalyzerPlugin.Result::getResultAsString).reduce("", (acc, cur) -> acc + "\n" + cur);
    }

    public String toHTML() {
        //get the path of the head.html file
        StringBuilder result = new StringBuilder("<!-- Generated By  ---> ");
        String path = (new File(System.getProperty("user.dir"))).getParentFile() + "/webgen/head.html";
        try{
            BufferedReader in = new BufferedReader(new FileReader(path));
            String data = "";
            while ( (data = in.readLine()) != null){
                result.append(data + "\n");
            }
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        result.append("<body>" + "\n");
        result.append(subResults.stream().map(AnalyzerPlugin.Result::getResultAsHtmlDiv).reduce("", (acc, cur) -> acc + "\n" + cur));
        result.append(readShowPanel() + "\n");
        result.append("</div> "+ "\n");
        result.append("</div> "+ "\n");
        result.append("</body> "+ "\n");
        result.append("</html>");
        
        return result.toString();
    }

    private String readShowPanel(){
        StringBuilder result = new StringBuilder(""); 
        String dir = System.getProperty("user.dir");
        dir = dir.replace("cli","");
        try (BufferedReader in = new BufferedReader(new FileReader(dir+"/webgen/showPanel.html"))) {
            String str;
            while ((str = in.readLine()) != null) {
                result.append(str+"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}