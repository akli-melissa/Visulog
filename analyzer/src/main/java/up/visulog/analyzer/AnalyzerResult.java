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
        StringBuilder result = new StringBuilder("");
        result.append("<!DOCTYPE html>");
        result.append("<html lang=\"en\">");
        result.append("<head>");
        result.append("<meta charset=\"utf-8\" />");
        result.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">");
        result.append("<title>Visulog Statistics</title>");
        result.append("</head>");
        result.append("<body>");
        result.append("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/Chart.js/3.6.0/chart.min.js \" integrity=\"sha512-GMGzUEevhWh8Tc/njS0bDpwgxdCJLQBWG3Z2Ct+JGOpVnEmjvNx6ts4v6A2XJf1HOrtOsfhv3hBKpK9kE5z8AQ==\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\"></script>");
        result.append(subResults.stream().map(AnalyzerPlugin.Result::getResultAsHtmlDiv).reduce("", (acc, cur) -> acc + "\n" + cur));
        result.append("</body>");
        result.append("</html>");
        return result.toString();
    }
}
