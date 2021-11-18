package up.visulog.gitrawdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import up.visulog.config.PluginConfig;

//Cette classe execute les commandes git
//Par @Younes Salhi

public interface ExecuteCommande {
    //Retourne un BufferedReader
    public static BufferedReader run(Path path,PluginConfig pluginConfig){
        
        ProcessBuilder builder;
        
        builder = new ProcessBuilder(
            "git",
            pluginConfig.config().get("command"),//la commande
            pluginConfig.config().getOrDefault("start","-a"),//Commit de debut
            pluginConfig.config().getOrDefault("end","-a"),//Commit de fin
            pluginConfig.config().getOrDefault("options","-a")//Options supplémentaire
        );

        builder.directory(path.toFile());
        
        Process process;
        try {
            process = builder.start();
        } catch (IOException e) {
            throw new RuntimeException("Error running \"git "+pluginConfig.config().get("command")+" \".", e);
        }
        InputStream is = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is , StandardCharsets.UTF_8));//UTF-8
        return reader;
    }
}