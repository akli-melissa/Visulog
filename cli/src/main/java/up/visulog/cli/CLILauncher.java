package up.visulog.cli;

import up.visulog.analyzer.Analyzer;
import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Optional;
import java.io.*;
import java.util.*;


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

<<<<<<< HEAD
                            if (pValue.equals("countMergeCommits")) plugins.put("countMerge", new PluginConfig(){});
=======
                            if (pValue.equals("countMergeCommits")) plugins.put("MergeCommits", new PluginConfig(){});
>>>>>>> 2eff5286ce4633e01d5d4c0f5b6b3caad7e35554

                            if (pValue.equals("countCommits")) plugins.put("countCommits", new PluginConfig() {
                            });

                            break;
                        case "--loadConfigFile":
                            // TODO (load options from a file)
                            break;
                        case "--justSaveConfigFile":
                            // TODO (save command line options to a file instead of running the analysis)
                            try {
                                File file = new File("ConfigFile.txt");
                 
                                // création du fichier s'il n'existe pas
                                if (!file.exists()) {
                                 file.createNewFile();
                                }
                                FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
                                BufferedWriter buffer = new BufferedWriter(fw);
                                buffer.write(pValue);//pValue
                                buffer.close();
                               } catch (IOException e) {
                                e.printStackTrace();
                               }
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
        //TODO: print the list of options and their syntax
        try {
			sc = new Scanner(new File("Help.txt"));
			sc.useDelimiter("\n");
			while(sc.hasNext()) {
				System.out.println(sc.next());
			}
		}
		catch(Exception e) {
			System.out.println("Erreur lors de l'ouverture du fichier:");
			e.printStackTrace();
			System.exit(1);
		}
        System.exit(0);
    }
}


//Younes Salhi accepte le merge

