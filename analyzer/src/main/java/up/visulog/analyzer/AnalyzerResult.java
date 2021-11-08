package up.visulog.analyzer;

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
//-----------------------------
      /*  System.out.println(new File("").getAbsolutePath());
                String html_code = "";
                html_code += Files.readString(Path.of("../webgen/src/main/java/up/visulog/webgen/Pages/basicPage_begining.html"));
                if(subResults.size() != 0) {
                    for(AnalyzerPlugin.Result result : subResults) {
                        html_code += "<h1 class=\"text-center\">"+result.getDisplayName()+"</h1>";
                        Webgen.Graph[] graphArray = result.getResultAsGraphArray();
                        html_code += "<div class=\"container\"><div class=\"row\">";
                        int c = 0;
                        for(Webgen.Graph graph : graphArray) {
                            if(c == 2) {
                                html_code += "</div><div class=\"row\">";
                                c = 0;
                            }
                            html_code += "<div class=\"col-4\"><p class=\"canvas-paragraph\">" + graph.getHTML() + "</p></div>";
                        }
                        html_code += "</div></div>";
                    }

                } else {
                    html_code += "<h2> Aucun plugin sélectionné! </h2>";
                }
                html_code += Files.readString(Path.of("../webgen/src/main/java/up/visulog/webgen/Pages/basicPage_end.html"));
                return html_code;*/
                 return "<html><body>"+subResults.stream().map(AnalyzerPlugin.Result::getResultAsHtmlDiv).reduce("", (acc, cur) -> acc + cur) + "</body></html>";
        //---------------------------------
    }
}
