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
        StringBuilder html = new StringBuilder("");
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"en\">");
        html.append("<head>");
        html.append("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/Chart.js/3.6.0/chart.min.js\" integrity=\"sha512-GMGzUEevhWh8Tc/njS0bDpwgxdCJLQBWG3Z2Ct+JGOpVnEmjvNx6ts4v6A2XJf1HOrtOsfhv3hBKpK9kE5z8AQ==\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\"></script>");
        html.append("<meta charset=\"utf-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">");
        html.append("<title>Visulog Statistics</title>");
        html.append("</head>");
        html.append(subResults.stream().map(AnalyzerPlugin.Result::getResultAsHtmlDiv).reduce("", (acc, cur) -> acc +"\n"+ cur));
        html.append("</body>");
        html.append("</html>");
        //html.append("");
        //html.append("");
        //html.append("");
        //html.append("");
        return(html.toString());
        //return "<html><body>"+subResults.stream().map(AnalyzerPlugin.Result::getResultAsHtmlDiv).reduce("", (acc, cur) -> acc +"\n"+ cur) + "</body></html>";
    }
}
