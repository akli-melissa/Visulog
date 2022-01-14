package up.visulog.cli;

import java.util.Scanner;
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

    private static String[] ALLCommands = {"countCommits","countCommits/10-12-2021/10-01-2022","countLines","countMergeCommits","countCommitsPerDayOfWeek","countCommitsPerDayOfMonth","countCommitsPerHourOfDay","countLinesPerAuthor","userStats/Salhi_Younes"};

    public static void run(String[] args) {
        var config = makeConfigFromCommandLineArgs(args);
        if (config == null) {}//Dans le cas d'un justSaveConfigFile
        else if (config.isPresent()) {
            var analyzer = new Analyzer(config.get());
            var results = analyzer.computeResults();
            if(results.getSubResults().size()!=0){
                try {
                    String path = (new File(System.getProperty("user.dir"))).getParentFile() + "/webgen/resultats.html";
                    File f2 = new File(path);
                    BufferedWriter bw = new BufferedWriter(new FileWriter(f2));
                    bw.write(results.toHTML());
                    bw.close();
                    Desktop.getDesktop().browse(f2.toURI());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }else{
                displayHelpAndExit();  
            }
        }else{
            displayHelpAndExit();  
        }
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
                        if(!runAnalysis(plugins, pValue)){//Si jamais l'option choisie n'existe pas
                            return Optional.empty();
                        }
                        break;

                    case "--loadConfigFile":
                        LoadConfigFile(plugins,pValue);
                        break;

                    case "--justSaveConfigFile":
                        if(!saveConfigFile(pValue)){//Afficher le help si jamais l'utilisateur n'a pas respecté la syntaxe
                            displayHelpAndExit();  
                        }
                        return null;

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

    private static void runAllCommand(HashMap<String, PluginConfig> plugins){
        for (String command:ALLCommands){
            runAnalysis(plugins,command);
        }
    }    

    private static boolean runAnalysis(HashMap<String, PluginConfig> plugins, String pValue) {
        String[] pValues = pValue.split("/");
        switch (pValues[0]) {
        case "All": 
            runAllCommand(plugins);
        break;
        case "countLinesPerAuthor": 
            plugins.put("countLinesPerAuthor", new PluginConfig() {
                @Override
                public Map<String, String> config() {
                    Map<String, String> configurationPlugin = new HashMap<String, String>();
                    configurationPlugin.put("command", "log");// la commande git
                    configurationPlugin.put("option1","--shortstat");//pour plus d'informations sur le commit
                    return configurationPlugin;
                }
            });
        break;
        case "userStats":
            plugins.put(pValue, new PluginConfig() {
                // Ajout des conifigurations
                @Override
                public Map<String, String> config() {
                    Map<String, String> configurationPlugin = new HashMap<String, String>();
                    configurationPlugin.put("command", "log");// la commande git
                    configurationPlugin.put("option1","--shortstat");//pour plus d'informations sur le commit
                    return configurationPlugin;
                }
            });
            break;

        case "countLines":
            plugins.put("countLines", new PluginConfig() {
                // Ajout des conifigurations
                @Override
                public Map<String, String> config() {
                    Map<String, String> configurationPlugin = new HashMap<String, String>();
                    configurationPlugin.put("command", "whatchanged");// la commande git
                    configurationPlugin.put("option1", "--numstat");// the options
                    configurationPlugin.put("option2", "--pretty=");// the options
                    return configurationPlugin;
                }
            });
            break;

        case "countMergeCommits":
            plugins.put("countMerge", new PluginConfig() {
                @Override
                public Map<String, String> config() {
                    Map<String, String> configurationPlugin = new HashMap<String, String>();
                    configurationPlugin.put("command", "log");// la commande git
                    return configurationPlugin;
                }
            });
            break;

            case "countCommits": 
                plugins.put(pValue, new PluginConfig() {
                @Override
                public Map<String, String> config() {
                    Map<String, String> configurationPlugin = new HashMap<String, String>();
                    configurationPlugin.put("command", "log");// la commande git
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

        default:
            return false ;
        }
        return true;

    }

    private static void LoadConfigFile(HashMap<String, PluginConfig> plugins,String fileName) {
        Scanner sc;
        try {// on recupere ligne par ligne les options sauvegardées
            File file =new File(fileName);
            sc = new Scanner(file);// Fichier spécifié par l'utilisateur
            sc.useDelimiter("\n");
            while (sc.hasNext()) {
                String data = sc.next();
                runAnalysis(plugins,data);// Et puis on fait l'analyse
            }
        }catch (Exception e) {// Si le fichier n'existe pas on revoie une erreur
            System.out.println("Erreur lors de l'ouverture du fichier:");
        }
    }

    private static boolean saveConfigFile(String pValue) {
        String[] pValues = pValue.split(":");
        if(pValues.length==2){
            String fileName=pValues[0];
            String optionValues=pValues[1];

            File file = new File(fileName);
            try {
                file.exists();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter buffer = new BufferedWriter(fw);
                for (String s : optionValues.split(",")) {// on recupere les options separées par des ','
                    buffer.write(s + "\n");// pValue
                }
                buffer.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;

    }
}

// Younes Salhi accepte le merge
