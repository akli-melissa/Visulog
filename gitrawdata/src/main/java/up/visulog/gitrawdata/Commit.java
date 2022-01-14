package up.visulog.gitrawdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.Optional;
import up.visulog.config.PluginConfig;
import java.text.*; 

public class Commit {
    public final String id;
    public final Date date;
    public String author;
    public final String description;
    public final String mergedFrom;
    public final String commitInformations;

    public Commit(String id, String author, Date date, String description, String mergedFrom, String commitInformations) {
        this.id = id;
        this.author = author;
        this.date = date;
        this.description = description;
        this.mergedFrom = mergedFrom;
        this.commitInformations = commitInformations;
    }

    // TODO: factor this out (similar code will have to be used for all git commands)
    public static List<Commit> parseLogFromCommand(Path gitPath, PluginConfig pluginConfig) {
        return parseLog(ExecuteCommande.run(gitPath, pluginConfig));
    }

    private static double percentOfSimilarity(String s1,String s2){
        int indexOfMail = s1.indexOf("<");
        String mail = s1.substring(indexOfMail,s1.length()-2);
        if (s2.contains(mail)) return 100.0;//its the same mail -> the same person
        s1 = s1.toLowerCase().substring(0,indexOfMail);
        int indexOfMail1 = s2.indexOf("<");
        s2 = s2.toLowerCase().substring(0,indexOfMail1);
        int matchCount = 0;
        for (int x = 0; x<s1.length() && x<s2.length() ; x++)
           if (s1.charAt(x) == s2.charAt(x)) matchCount++;
        int longestString = s1.length();
        if (s2.length() > longestString) longestString = s2.length();
        double result = matchCount * 100 / longestString;
        return result;
    }

    private static void solveDuplicate(List<Commit> listeC,Commit commit) {
        for (var cm:listeC){
            if (percentOfSimilarity(cm.author, commit.author) >= 70.0){
                commit.author = cm.author;
                return;
            }
        }
    }

    public static List<Commit> parseLog(BufferedReader reader) {
        var result = new ArrayList<Commit>();
        Optional<Commit> Optcommit = parseCommit(reader);
        Commit commit;
        while (Optcommit.isPresent()) {
            commit = Optcommit.get();
            solveDuplicate(result,commit);
            result.add(commit);
            Optcommit = parseCommit(reader);
        }
        return result;
    }

    /**
     * Parses a log item and outputs a commit object. Exceptions will be thrown in case the input does not have the proper format.
     * Returns an empty optional if there is nothing to parse anymore.
     */
    public static Optional<Commit> parseCommit(BufferedReader input) {
        try {

            var line = input.readLine();
            if (line == null) return Optional.empty(); // if no line can be read, we are done reading the buffer
            var idChunks = line.split(" ");
            if (!idChunks[0].equals("commit")) parseError();
            var builder = new CommitBuilder(idChunks[1]);

            line = input.readLine();
            while (!line.isEmpty()) {
                var colonPos = line.indexOf(":");
                var fieldName = line.substring(0, colonPos);
                var fieldContent = line.substring(colonPos + 1).trim();//pour l'indentation
                switch (fieldName) {
                    case "Author":
                        builder.setAuthor(fieldContent);
                        break;
                    case "Merge":
                        builder.setMergedFrom(fieldContent);
                        break;
                    case "Date":
                        try{
                            Date d = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy z",Locale.ENGLISH).parse(fieldContent); 
                            builder.setDate(d);
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                          }
                        break;
                    default: // TODO: warn the user that some field was ignored
                }
                line = input.readLine(); //prepare next iteration
                if (line == null) parseError(); // end of stream is not supposed to happen now (commit data incomplete)
            }

            // now read the commit message per se
            var description = input
                    .lines() // get a stream of lines to work with
                    .takeWhile(currentLine -> !currentLine.isEmpty()) // take all lines until the first empty one (commits are separated by empty lines). Remark: commit messages are indented with spaces, so any blank line in the message contains at least a couple of spaces.
                    .map(String::trim) // remove indentation
                    .reduce("", (accumulator, currentLine) -> accumulator + currentLine); // concatenate everything
            builder.setDescription(description);

            input.mark(1000);//mark tge current positions
            String currentLine = input.readLine();//read the line
            if (currentLine != null ){//if the line exsists
                if (!currentLine.startsWith("commit")){//if its not the debue of another commit
                    //get more informations from the commit
                    input.reset();
                    String commitInfo = input
                    .lines()
                    .takeWhile(currentline -> !currentline.isEmpty())
                    .map(String::trim)
                    .reduce("",(acc,cur)->acc+cur);
                    builder.setCommitInformations(commitInfo);
                }else input.reset();//else its a new commit get back to previous position
            }

            return Optional.of(builder.createCommit());
        } catch (IOException e) {
            e.printStackTrace();
            parseError();
        }
        return Optional.empty(); // this is supposed to be unreachable, as parseError should never return
    }

    // Helper function for generating parsing exceptions. This function *always* quits on an exception. It *never* returns.
    private static void parseError() {
        throw new RuntimeException("Wrong commit format.");
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy",Locale.ENGLISH);
        String d =sdf.format(date);
        return "Commit{" +
                "id='" + id + '\'' +
                (mergedFrom != null ? ("mergedFrom...='" + mergedFrom + '\'') : "") + //TODO: find out if this is the only optional field
                ", date='" + d + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}