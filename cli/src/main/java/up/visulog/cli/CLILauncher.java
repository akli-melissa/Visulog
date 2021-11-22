package up.visulog.cli;

import up.visulog.analyzer.Analyzer;
import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Optional;
import java.io.*;
import java.util.*;
import java.awt.Desktop;

public class CLILauncher {
    public static void main(String[] args) {
        var config = makeConfigFromCommandLineArgs(args);
        if (config.isPresent()) {
            var analyzer = new Analyzer(config.get());
            var results = analyzer.computeResults();
            try {
                File f = new File("resultats.html");
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                bw.write(results.toHTML());
                bw.close();
                Desktop.getDesktop().browse(f.toURI());
            } catch (Exception e) {
                System.out.println("Erreur");
            }

        } else
            displayHelpAndExit();
    }

    static Optional<Configuration> makeConfigFromCommandLineArgs(String[] args) {
        var gitPath = FileSystems.getDefault().getPath(".");
        var plugins = new HashMap<String, PluginConfig>();
        for (var arg : args) {
            if (arg.startsWith("--")) {// startWith methode de la classe String :evaluer prefix
                String[] parts = arg.split("=");
                if (parts.length != 2)
                    return Optional.empty();
                else {
                    String pName = parts[0];
                    String pValue = parts[1];
                    switch (pName) {
                    case "--addPlugin":
                        // TODO: parse argument and make an instance of PluginConfig

                        // Let's just trivially do this, before the TODO is fixed:

                        runAnalysis(plugins, pValue);

                        break;

                    case "--loadConfigFile":
                        // TODO (load options from a file)
                        LoadConfigFile(plugins);
                        break;

                    case "--justSaveConfigFile":
                        // TODO (save command line options to a file instead of running the analysis)
                        saveConfigFile(pValue);
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
        Scanner sc;
        System.out.println("Wrong command...");
        // TODO: print the list of options and their syntax
        try {
            sc = new Scanner(new File("Help.txt"));
            sc.useDelimiter("\n");
            while (sc.hasNext()) {
                System.out.println(sc.next());
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de l'ouverture du fichier:");
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    private static void runAnalysis(HashMap<String, PluginConfig> plugins, String pValue) {
        switch (pValue) {
            case "countLines":
                plugins.put("countLines", new PluginConfig(){
                //Ajout des conifigurations
                @Override
                public Map<String,String> config(){
                    Map<String,String> configurationPlugin = new HashMap<String,String>();
                    configurationPlugin.put("command","diff");//la commande git
                    configurationPlugin.put("start","HEAD~");//le dernier Commit
                    configurationPlugin.put("end","HEAD");
                    configurationPlugin.put("options","--numstat");//the options
                    return configurationPlugin;
                }
            });
            break;

            case "countMergeCommits":
                plugins.put("countMerge", new PluginConfig(){
                @Override
                    public Map<String,String> config(){
                        Map<String,String> configurationPlugin = new HashMap<String,String>();
                        configurationPlugin.put("command","log");//la commande git
                        return configurationPlugin;
                    }
                });
            break;


            case "countCommits": 
                plugins.put("countCommits", new PluginConfig() {
                @Override
                public Map<String,String> config(){
                    Map<String,String> configurationPlugin = new HashMap<String,String>();
                    configurationPlugin.put("command","log");//la commande git
                    return configurationPlugin;
                }
            });
            break;

            case "countCommitsPerDayOfWeek": 
                plugins.put("countCommitsPerDayOfWeek", new PluginConfig() {
                @Override
                public Map<String,String> config(){
                    Map<String,String> configurationPlugin = new HashMap<String,String>();
                    configurationPlugin.put("command","log");//la commande git
                    return configurationPlugin;
                }
            });
            break;

            case "countCommitsPerDayOfMonth": 
                plugins.put("countCommitsPerDayOfMonth", new PluginConfig() {
                @Override
                public Map<String,String> config(){
                    Map<String,String> configurationPlugin = new HashMap<String,String>();
                    configurationPlugin.put("command","log");//la commande git
                    return configurationPlugin;
                }
            });
            break;
            case "countCommitsPerHourOfDay": 
                plugins.put("countCommitsPerHourOfDay", new PluginConfig() {
                @Override
                public Map<String,String> config(){
                    Map<String,String> configurationPlugin = new HashMap<String,String>();
                    configurationPlugin.put("command","log");//la commande git
                    return configurationPlugin;
                }
            });
            break;

            default: return;
        }
    }

    private static void LoadConfigFile(HashMap<String, PluginConfig> plugins) {
        Scanner sc;
        try {// on recupere ligne par ligne les options sauvegardées
            sc = new Scanner(new File("ConfigFile.txt"));
            sc.useDelimiter("\n");
            while (sc.hasNext()) {
                runAnalysis(plugins, sc.next());// Et puis on fait l'analyse
            }
        } catch (Exception e) {// Si le fichier n'existe pas on revoie une erreur
            System.out.println("Erreur lors de l'ouverture du fichier:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void saveConfigFile(String pValue) {
        try {
            File file = new File("ConfigFile.txt");

            // création du fichier s'il n'existe pas
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter buffer = new BufferedWriter(fw);
            for (String s : pValue.split(",")) {// on recupere les options separées par des ','
                buffer.write(s + "\n");// pValue
            }
            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Younes Salhi accepte le merge
