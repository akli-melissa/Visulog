package up.visulog.analyzer;

class Multithreading extends Thread {
    AnalyzerPlugin plugin ;
    public Multithreading(AnalyzerPlugin plugin){
        this.plugin = plugin;
    }
    public void run()
    {
        try {
            // Displaying the thread that is running
            System.out.println(
                "Thread " + Thread.currentThread().getId()
                + " is running");

            this.plugin.run();

            
            System.out.println(
                "Thread " + Thread.currentThread().getId()
                + " Finished");

            }
        catch (Exception e) {
            // Throwing an exception
            System.out.println("Exception is caught");
        }
    }
}