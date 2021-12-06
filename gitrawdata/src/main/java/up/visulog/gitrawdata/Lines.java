package up.visulog.gitrawdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import up.visulog.config.PluginConfig;

/*
Par @Younes Salhi
*/

public class Lines {
    public final int numberAdded;
    public final int numberDeleted;
    public final String path;

    public Lines (int numberAdded, int numberDeleted, String path) {
        this.numberAdded = numberAdded;
        this.numberDeleted = numberDeleted;
        this.path = path;
    }

    public static List<Lines> parseDiffFromCommand(Path gitPath, PluginConfig pluginconfig){
        return parseDiff(ExecuteCommande.run(gitPath, pluginconfig));
    }

    public static List<Lines> parseDiff(BufferedReader reader){
        List<Lines> result = new ArrayList<Lines>();
        Optional<Lines> line = parseLines(reader);
        while (line.isPresent()) {
            result.add(line.get());
            line = parseLines(reader);
        }
        return result;
    }

    public static Optional<Lines> parseLines(BufferedReader input){
        try{
            String line = input.readLine();
            if (line == null) return Optional.empty();
            String[] data = line.split("\\t");//pour la tabulation
            if (data.length != 3) return Optional.empty();
            if (!Character.isDigit(data[0].charAt(0))) data[0] = "0";
            if (!Character.isDigit(data[1].charAt(0))) data[1] = "0";
            
            return Optional.of(new Lines(Integer.parseInt(data[0]),Integer.parseInt(data[1]),data[2]));
        }catch(IOException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public String toString(){
        return "Line{"+
        this.numberAdded+
        ", "+this.numberDeleted+
        ", "+this.path+
        "}";
    }
}

//---------------------------------
