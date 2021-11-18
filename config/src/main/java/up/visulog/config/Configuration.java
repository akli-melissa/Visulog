package up.visulog.config;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public class Configuration {

    private final Path gitPath;
    private final Map<String, PluginConfig> plugins;

    public Configuration(Path gitPath, Map<String, PluginConfig> plugins) {
        this.gitPath = gitPath;
        this.plugins = Map.copyOf(plugins);//new HashMap<>(plugins);
    }

    public Path getGitPath() {
        return gitPath;
    }

    //get the plugins of a String
    public Optional<PluginConfig> getPluginConfig(String key){
        PluginConfig pluginConfig = this.plugins.getOrDefault(key,null);
        if (pluginConfig == null) return Optional.empty();
        else return Optional.of(pluginConfig);
    }

    public Map<String, PluginConfig> getPluginConfigs() {
        return plugins;
    }
}
