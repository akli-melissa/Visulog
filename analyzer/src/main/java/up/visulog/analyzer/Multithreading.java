package up.visulog.analyzer;

class Multithreading extends Thread {
    AnalyzerPlugin plugin ;
    public Multithreading(AnalyzerPlugin plugin){
        this.plugin = plugin;
    }
    public void run()
    {
        try {

            this.plugin.run();

        

            }
        catch (Exception e) {
            // Throwing an exception
            System.out.println("Exception is caught");
        }
    }
}