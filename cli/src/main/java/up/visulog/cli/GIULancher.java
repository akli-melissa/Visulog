package up.visulog.cli;

import java.awt.*;
import javax.swing.*;

public class GIULancher extends JFrame{

    private JPanel mainContainer;
    private JComboBox<String> listechoix;
    private JLabel showInformations;
    private String commandToRun = "";
    private JPanel buttonContainer;
    private JTextField path;

    public GIULancher(){
        path = new JTextField("Enter the path");
        showInformations = new JLabel("<html>chose command <br>to run</html>");
        listechoix = new JComboBox<>(new String[]{"countLines","countMergeCommits","countCommits","countCommitsPerDayOfWeek","countCommitsPerDayOfMonth","countCommitsPerHourOfDay","countLinesPerAuthor","All"});
        mainContainer = new JPanel();
        buttonContainer = new JPanel();
        mainContainer.setLayout(null);
        path.setBounds(50,10,150,30);
        listechoix.setBounds(300,10,200,30);
        showInformations.setBounds(170,50,200,150);
        buttonContainer.setBounds(140,200,250,30);
        mainContainer.add(listechoix);
        mainContainer.add(showInformations);
        mainContainer.add(buttonContainer);
        mainContainer.add(path);
        addButton();
        initDeleteButton();
        initRunButton();
        setTitle("Visulog");
        setSize(600,300);
        setContentPane(mainContainer);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addButton(){
        JButton ajout = new JButton("add");
        ajout.addActionListener((e)->{
            String chosecommand = listechoix.getItemAt(listechoix.getSelectedIndex());
            if (!chosecommand.equals("All") && commandToRun.equals("All")) commandToRun = "";
            if (chosecommand.equals("All")) commandToRun = "All";
            else if (!containsString(chosecommand)) commandToRun += chosecommand +"<br>";
            showInformations.setText("<html>"+commandToRun+"</html>");
        });
        buttonContainer.setLayout(new GridLayout(0,3));
        buttonContainer.add(ajout);
    }

    private boolean containsString(String mot){
        String[] souschaines = commandToRun.split("<br>");
        for (String mots : souschaines)
            if (mots.equals(mot)) return true;
        return false;
    }

    private void initRunButton(){
        JButton runButton = new JButton("run");
        runButton.addActionListener((e)->{
            String commande = path.getText().equals("Enter the path")?".":path.getText();
            for (String arg : commandToRun.split("<br>"))
                commande += " --addPlugin="+arg;
            CLILauncher.run(commande.split(" "));
        });
        buttonContainer.add(runButton);
    }

    private void deleteFromCommand(String command){
        String newCommand = "";
        String[] souschaines = commandToRun.split("<br>");
        for (String chaine : souschaines){
            if (!chaine.equals(command)) newCommand += chaine + "<br>";
        }
        commandToRun = newCommand;
    }

    private void initDeleteButton(){
        JButton deletebutton = new JButton("delete");
        deletebutton.addActionListener((e)->{
            String chosecommand = listechoix.getItemAt(listechoix.getSelectedIndex());
            deleteFromCommand(chosecommand);
            showInformations.setText("<html>"+commandToRun+"</html>");
        });
        buttonContainer.add(deletebutton);
    }

    public static void main(String args[]){
        if (args.length>0 && !args[0].equals("ShowGUI")) CLILauncher.run(args);//CLILauncher.run(args);
        else{
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    //display the giu
                    (new GIULancher()).setVisible(true);
                }
            });
        }
    }
}