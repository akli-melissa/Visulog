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
    private JMenuBar menubar;
    private String chosecommand;

    public GIULancher(){
        menubar = new JMenuBar();
        path = new JTextField("Enter the path");
        showInformations = new JLabel("<html>chose command <br>to run</html>");
        listechoix = new JComboBox<>(new String[]{"countLines","countMergeCommits","countCommits","countCommitsPerDayOfWeek","countCommitsPerDayOfMonth","countCommitsPerHourOfDay","countLinesPerAuthor","countCommitsPerDate","userStats","All"});
        mainContainer = new JPanel();
        buttonContainer = new JPanel();
        mainContainer.setLayout(null);
        path.setBounds(50,10,150,30);
        listechoix.setBounds(300,10,200,30);
        showInformations.setBounds(170,50,210,150);
        buttonContainer.setBounds(140,200,250,30);
        mainContainer.add(listechoix);
        mainContainer.add(showInformations);
        mainContainer.add(buttonContainer);
        mainContainer.add(path);
        addButton();
        initDeleteButton();
        initRunButton();
        setTitle("Visulog");
        setSize(600,350);
        setContentPane(mainContainer);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initMenu();
        setJMenuBar(menubar);
        setLocationRelativeTo(null);
    }

    private void initMenu(){
        JButton save = new JButton("save");
        JButton load = new JButton("load");
        JButton quit = new JButton("quit");
        load.addActionListener((e)->{
            class ResponseFrame extends JFrame{
                public ResponseFrame(){
                    JPanel mainContainer = new JPanel();
                    setSize(200,100);
                    setResizable(false);
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    setLocationRelativeTo(null);
                    JTextField fileName = new JTextField("file name");
                    JButton validate = new JButton("enter");
                    validate.addActionListener((e)->{
                        //close the window
                        setVisible(false);
                        dispose();
                    });
                    mainContainer.add(fileName);
                    mainContainer.add(validate);
                    setContentPane(mainContainer); 
                }
            }
            (new ResponseFrame()).setVisible(true);
        });
        quit.addActionListener((e)->{
            setVisible(false);
            dispose();
        });
        menubar.add(save);
        menubar.add(load);
        menubar.add(quit);
    }

    private void addButton(){
        JButton ajout = new JButton("add");
        ajout.addActionListener((e)->{
            boolean show = false;
            chosecommand = listechoix.getItemAt(listechoix.getSelectedIndex());
            if (chosecommand.equals("countCommitsPerDate") || chosecommand.equals("userStats")){
                if (chosecommand.equals("countCommitsPerDate")) showDate();
                else showName();
                show = true;
            }
            if (!chosecommand.equals("All") && commandToRun.equals("All")) commandToRun = "";
            if (chosecommand.equals("All")) commandToRun = "All";
            else if (!containsString(chosecommand) && !show) commandToRun += chosecommand +"<br>";
            if (!show) showInformations.setText("<html>"+commandToRun+"</html>");
        });
        buttonContainer.setLayout(new GridLayout(0,3));
        buttonContainer.add(ajout);
    }

    private void showName(){
        class ShowName extends JFrame{
            public ShowName(){
                JTextField name = new JTextField("name");
                JPanel mainContainer = new JPanel();
                JButton enter = new JButton("enter");
                setSize(200,100);
                setResizable(false);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setLocationRelativeTo(null);
                mainContainer.add(name);
                mainContainer.add(enter);
                enter.addActionListener((e)->{
                    chosecommand = "userStats/"+name.getText().replace(" ","_");
                    commandToRun += chosecommand +"<br>";
                    showInformations.setText("<html>"+commandToRun+"</html>");
                    setVisible(false);
                    dispose();
                });
                setContentPane(mainContainer);
            }
        }
        (new ShowName()).setVisible(true);
    }

    private void showDate(){
        class ShowDate extends JFrame{
            public ShowDate(){
                JTextField date1 = new JTextField("dd-mm-yyyy");
                JTextField date2 = new JTextField("dd-mm-yyyy");
                JPanel mainContainer = new JPanel();
                JButton enter = new JButton("enter");
                setSize(200,100);
                setResizable(false);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setLocationRelativeTo(null);
                mainContainer.add(date1);
                mainContainer.add(date2);
                mainContainer.add(enter);
                enter.addActionListener((e)->{
                    chosecommand = "countCommits/"+date1.getText()+"/"+date2.getText();
                    commandToRun += chosecommand +"<br>";
                    showInformations.setText("<html>"+commandToRun+"</html>");
                    setVisible(false);
                    dispose();
                });
                setContentPane(mainContainer);
            }
        }
        (new ShowDate()).setVisible(true);
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
        if (args.length > 0 && !args[0].toLowerCase().equals("showgui"))
            CLILauncher.run(args);
        else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // display the giu
                    (new GIULancher()).setVisible(true);
                }
            });
        }
    }
}