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

    private static String[] ALLCommands = {"countLines","countMergeCommits","countCommits","countCommitsPerDayOfWeek","countCommitsPerDayOfMonth","countCommitsPerHourOfDay","countLinesPerAuthor"};

    public static void main(String[] args) {
        var config = makeConfigFromCommandLineArgs(args);
        if (config.isPresent()) {
            var analyzer = new Analyzer(config.get());
            var results = analyzer.computeResults();
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


                        runAnalysis(plugins, pValue);

                        break;

                    case "--loadConfigFile":
                        // TODO (load options from a file)
                        LoadConfigFile(pValue);
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

    private static void runAllCommand(HashMap<String, PluginConfig> plugins){
        for (String command:ALLCommands){
            runAnalysis(plugins,command);
        }
    }    

    private static void runAnalysis(HashMap<String, PluginConfig> plugins, String pValue) {
        switch (pValue) {
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
                plugins.put("countCommits", new PluginConfig() {
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
            return;
        }
    }

    private static void LoadConfigFile(String fileName) {
        Scanner sc;
        try {// on recupere ligne par ligne les options sauvegardées
            sc = new Scanner(new File(fileName));// Fichier spécifié par l'utilisateur
            sc.useDelimiter("\n");
            while (sc.hasNext()) {
                String data = sc.next();
                
                //runAnalysis(plugins, sc.next());// Et puis on fait l'analyse
            }
        } catch (Exception e) {// Si le fichier n'existe pas on revoie une erreur
            System.out.println("Erreur lors de l'ouverture du fichier:");
           // e.printStackTrace();
            System.exit(1);
        }
    }

    private static void saveConfigFile(String pValue) {

        Scanner input = new Scanner(System.in);
        String fileName;
        // création du fichier s'il n'existe pas
        System.out.print("Entrez le nom du fichier");// Demande du nom du fichier à l'utilisateur
        fileName = input.nextLine();
        File file = new File(fileName);
        input.close();
        if (file.exists()) {// si le fichier existe déjà, demander si on veut le replace
            System.out.print("Ce fichier existe déjà, voulez vous le remplacer ?");
            System.out.println("(y/n)");
            Scanner sc = new Scanner(System.in);
            if (sc.hasNext("y") || sc.hasNext("o")) {// si l'utilisateur répond oui, on recrée le fichier
                sc.close();
                file.delete();
                file = new File(fileName);
            } else {// si l'utilisateur répond non, on préviens que le fichier n'a pas été créé
                System.out.print("Le fichier n'a pas été créé .");
            }

        }
        try {
            file.exists();

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
