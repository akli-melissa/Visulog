package up.visulog.config; //import du fichier config.java du projet

import java.util.Map;

// Cette classe accueillera les config des plugins pour l interface.

// TODO: define what this type should be (probably a Map: settingKey -> settingValue)
public interface PluginConfig {
    Map<String,String> config();
}

//j'accepte le merge
