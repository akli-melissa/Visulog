package up.visulog.cli;

import up.visulog.analyzer.Analyzer;
import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Optional;
<<<<<<< HEAD
import java.util.Set ;
import java.util.Scanner;
import java.io.*;
import java.awt.Desktop;
=======
import java.util.Set;
import java.util.Scanner;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

import java.awt.Desktop;

>>>>>>> 046f41c1064b8d2c0eb9abb61f09e08e394fc948

public class CLILauncher {

    public static void main(String[] args) {
        var config = makeConfigFromCommandLineArgs(args);
<<<<<<< HEAD

        try {
            if (config.isPresent()) {
            var analyzer = new Analyzer(config.get());
            var results = analyzer.computeResults();

            File htmlFile = new File("../Pages/infoPage.html");
            htmlFile.getParentFile().mkdirs();

            htmlFile.createNewFile();
            System.out.println("File successfully created" + htmlFile.getAbsolutePath());
            FileWriter fileWriter = new FileWriter(htmlFile);
            fileWriter.write(results.toHTML());
            fileWriter.flush();
            fileWriter.close();

            if (Desktop.isDesktopSupported())
            {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(htmlFile);
            }
        }
        else
            displayHelpAndExit();
    }catch(Exception e){
        System.out.println("Error!");
    }
}




         /*var config = makeConfigFromCommandLineArgs(args);
        try {
       
=======
>>>>>>> 046f41c1064b8d2c0eb9abb61f09e08e394fc948
        if (config.isPresent()) {
            var analyzer = new Analyzer(config.get());
            var results = analyzer.computeResults();
             cli/src/main/java/up/visulog/cli/CLILauncher.java
            System.out.println(results.toHTML());

            File htmlFile = new File("../Pages/infoPage.html");
            htmlFile.getParentFile().mkdirs();

            htmlFile.createNewFile();
            System.out.println("File successfully created" + htmlFile.getAbsolutePath());
            FileWriter fileWriter = new FileWriter(htmlFile);
            fileWriter.write(results.toHTML());
            fileWriter.flush();
            fileWriter.close();

            if (Desktop.isDesktopSupported())
            {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(htmlFile);
            }
        }
        else
            displayHelpAndExit();
    }catch (Exception e) {
           System.out.println("Erreur");
           }*/
        
<<<<<<< HEAD
    
=======

            //System.out.println(results.toHTML());
            String content = results.toHTML();
            try{
                File f = new File("resultats.html");
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                bw.write(results.toHTML());
                bw.close();
                Desktop.getDesktop().browse(f.toURI());
            } catch(Exception e){System.out.println("Erreur");}
            
        } 
        else 
            displayHelpAndExit();
          catch (Exception e) {
           System.out.println("Erreur");
           }    
    }
>>>>>>> 046f41c1064b8d2c0eb9abb61f09e08e394fc948

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


                            if (pValue.equals("countMergeCommits")) plugins.put("countMerge", new PluginConfig(){});

                            if (pValue.equals("countMergeCommits")) plugins.put("MergeCommits", new PluginConfig(){});


                            if (pValue.equals("countCommits")) plugins.put("countCommits", new PluginConfig() {
                            });

                            break;
                        case "--loadConfigFile":
                            // TODO (load options from a file)
                            break;
                        case "--justSaveConfigFile":
                            // TODO (save command line options to a file instead of running the analysis)
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




