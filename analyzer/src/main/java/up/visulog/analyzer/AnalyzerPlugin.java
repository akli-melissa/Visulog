package up.visulog.analyzer;

public interface AnalyzerPlugin {

    //
    interface Result {// ~ Classe interne

        String getResultAsString();
        String getResultAsHtmlDiv();

    }
    /**
     * run this analyzer plugin
     */
    void run();

    /**
     *
     * @return the result of this analysis. Runs the analysis first if not already done.
     */
    Result getResult();
}
