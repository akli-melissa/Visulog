package up.visulog.analyzer;

import java.awt.Dimension;

import javax.swing.JFrame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.CategoryDataset;

public class CreateChart extends JFrame{
	
	// Constructeur du graphique pour plugin countCommitsPerAuthor (à changer à l'avenir pour que ça fonctionne avec d'autres plugins)
	public CreateChart(String windowTitle, String chartTitle, AnalyzerResult result) {
		
		// Appels des fonctions définies dans la classe
		DefaultCategoryDataset dataset = createDataset(result);
		JFreeChart chart = createChart(dataset, chartTitle);
		ChartPanel panel = new ChartPanel(chart);
		
		// Définition des préferrences d'affichage de la fenêtre
		panel.setPreferredSize(new Dimension(700, 500));
		setContentPane(panel);
		this.pack();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
	}
	
	// Création d'un dataset pour insertion dans graphique
	private DefaultCategoryDataset createDataset(AnalyzerResult result) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		// Résultat de CountCommitsPerAuthor sous forme de liste
		// Attention ! ne fonctionne que si countCommitsPerAuthor est le seul plugin entré en argument
		CountCommitsPerAuthorPlugin.Result list = (CountCommitsPerAuthorPlugin.Result) result.getSubResults().get(0);
		
		// Parcours de toutes les valeurs de la liste
		for (var item : list.getCommitsPerAuthor().entrySet()) {
			// Insertion des valeurs dans le dataset
			dataset.setValue(item.getValue(), "Commits", item.getKey());
		}
		return dataset;
	}
	
	// Création du graphique en barres
	private JFreeChart createChart(DefaultCategoryDataset dataset, String title) {
		JFreeChart chart = ChartFactory.createBarChart(title, "Auteur", "Commits", dataset, PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}
}