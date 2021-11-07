package up.visulog.cli;

import up.visulog.analyzer.Analyzer;
import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CLILauncher {

    public static void main(String[] args) {
        var config = makeConfigFromCommandLineArgs(args);
        if (config.isPresent()) {
            var analyzer = new Analyzer(config.get());
            var results = analyzer.computeResults();
            System.out.println(results.toHTML());
        } else displayHelpAndExit();
    }

    static Optional<Configuration> makeConfigFromCommandLineArgs(String[] args) {
        var gitPath = FileSystems.getDefault().getPath(".");
        var plugins = new HashMap<String, PluginConfig>();
        for (var arg : args) {
            if (arg.startsWith("--")) {//startWith methode de la classe String :evaluer prefix
                String[] parts = arg.split("=");
                if (parts.length != 2) return Optional.empty();
                else {
                    String pName = parts[0];
                    String pValue = parts[1];
                    switch (pName) {
                        case "--addPlugin":
                            // TODO: parse argument and make an instance of PluginConfig

                            // Let's just trivially do this, before the TODO is fixed:
                            

                            if (pValue.equals("countLines")) plugins.put("countLines", new PluginConfig(){
                                @Override
                                public Map<String,String> config(){
                                    Map<String,String> configurationPlugin = new HashMap<String,String>();
                                    configurationPlugin.put("command","diff");
                                    configurationPlugin.put("start","HEAD~");
                                    configurationPlugin.put("end","HEAD");
                                    configurationPlugin.put("options","--numstat");//the options
                                    return configurationPlugin;
                                }
                            });
                            
                            if (pValue.equals("countMergeCommits")) plugins.put("countMerge", new PluginConfig(){
                                @Override
                                public Map<String,String> config(){
                                    Map<String,String> configurationPlugin = new HashMap<String,String>();
                                    configurationPlugin.put("command","log");
                                    //we can add some...
                                    return configurationPlugin;
                                }
                            });

                            if (pValue.equals("countCommits")) plugins.put("countCommits", new PluginConfig() {
                                @Override
                                public Map<String,String> config(){
                                    Map<String,String> configurationPlugin = new HashMap<String,String>();
                                    configurationPlugin.put("command","log");
                                    return configurationPlugin;
                                }
                            });

                            break;
                        case "--loadConfigFile":
                            // TODO (load options from a file)
                            break;
                        case "--justSaveConfigFile":
                            // TODO (save line options to a file instead of running the analysis)
                            break;
                        default:
                            return Optional.empty();
                    }
                }
            } else {
                gitPath = FileSystems.getDefault().getPath(arg);
            }
        }
        return Optional.of(new Configuration(gitPath, plugins));
    }

    private static void displayHelpAndExit() {
        System.out.println("Wrong command...");
        //TODO: print the list of options and their syntax
        System.exit(0);
    }
}


//Younes Salhi accepte le merge

