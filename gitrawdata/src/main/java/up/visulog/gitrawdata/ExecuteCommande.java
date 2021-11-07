package up.visulog.gitrawdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import up.visulog.config.PluginConfig;

public interface ExecuteCommande {
    public static BufferedReader run(Path path,PluginConfig pluginConfig){
        ProcessBuilder builder =
                new ProcessBuilder(
                "git",
                pluginConfig.config().get("command"),//the command 
                pluginConfig.config().getOrDefault("options", "-a")//the options -a (option par defaut)
                ).directory(path.toFile());
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
