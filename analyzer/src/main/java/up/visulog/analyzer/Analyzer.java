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
            Multithreading thread = new Multithreading(plugin);
            thread.run();
        }

        // store the results together in an AnalyzerResult instance and return it
        return new AnalyzerResult(plugins.stream().map(AnalyzerPlugin::getResult).collect(Collectors.toList()));//lisye de Result
    }

    // TODO: find a way so that the list of plugins is not hardcoded in this factory
    private Optional<AnalyzerPlugin> makePlugin(String pluginName, PluginConfig pluginConfig) {

        String[] plugin = pluginName.split("/");
        try{
            switch (plugin[0]) {
                case "countLines": return Optional.of( new CountLines(config) );
                case "countMerge": return Optional.of( new CountMergeCommits(config));
                case "userStats":
                if (plugin.length >1 ){
                    return Optional.of(new User_Activity(config,plugin[1]));                
                } 
                else throw new Exception();
                case "countCommits" : 
                if(plugin.length > 1){
                    return Optional.of( new CountCommitsPerAuthorPlugin(config,plugin[1],plugin[2]));
                }else{
                    return Optional.of( new CountCommitsPerAuthorPlugin(config));
                }
                case "countCommitsPerDayOfWeek" : return Optional.of( new CountCommitsPerDayOfWeek(config));
                case "countCommitsPerDayOfMonth" : return Optional.of( new CountCommitsPerDayOfMonth(config));
                case "countCommitsPerHourOfDay" : return Optional.of( new CountCommitsPerHourOfDay(config));
                case "countLinesPerAuthor" : return Optional.of( new CountLinesPerAuthor(config));
                default : return Optional.empty();
            }
        }catch(Exception e){
            System.out.println(e);
            return Optional.empty();

        }
    }
}