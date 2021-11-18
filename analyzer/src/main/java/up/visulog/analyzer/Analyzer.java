package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Analyzer {
    private final Configuration config;

    public Analyzer(Configuration config) {
        //Configuration -> (Path , Map<String, PluginConfig> )
                                            // PluginConfig -> Interface à completer
        this.config = config;
    }

    public AnalyzerResult computeResults() {
        //AnalyzerResult
        List<AnalyzerPlugin> plugins = new ArrayList<>();
        for (var pluginConfigEntry: config.getPluginConfigs().entrySet()) {
            var pluginName = pluginConfigEntry.getKey(); 
            var pluginConfig = pluginConfigEntry.getValue();
            var plugin = makePlugin(pluginName, pluginConfig);
            plugin.ifPresent(plugins::add);
        }
        
        // run all the plugins
        for (var plugin: plugins){
            try {
                //add the thread
                Thread t = new Thread(){
                    @Override
                    public void run(){
                        plugin.run();
                    }
                };
                t.start();//start the thread
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // store the results together in an AnalyzerResult instance and return it
        return new AnalyzerResult(plugins.stream().map(AnalyzerPlugin::getResult).collect(Collectors.toList()));//lisye de Result
    }

    // TODO: find a way so that the list of plugins is not hardcoded in this factory
    private Optional<AnalyzerPlugin> makePlugin(String pluginName, PluginConfig pluginConfig) {
        switch (pluginName) {
            case "countLines": return Optional.of( new CountLines(config,pluginConfig) );
            case "countMerge": return Optional.of( new CountMergeCommits(config,pluginConfig));
            case "countCommits" : return Optional.of( new CountCommitsPerAuthorPlugin(config,pluginConfig));
            default : return Optional.empty();
        }
    }
}