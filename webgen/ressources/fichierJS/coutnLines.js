function afficheLines(){const graph = document.getElementById('graph').getContext('2d');let myChart = new Chart(graph, {type:"bar",data: {labels:["analyzer/src/main/java/up/visulog/analyzer/AnalyzerResult.java","analyzer/src/main/java/up/visulog/analyzer/CountMergeCommits.java","analyzer/src/main/java/up/visulog/analyzer/Analyzer.java","cli/resultats.html","config/src/main/java/up/visulog/config/Configuration.java","analyzer/src/main/java/up/visulog/analyzer/CountLines.java","analyzer/src/main/java/up/visulog/analyzer/CountCommitsPerAuthorPlugin.java","cli/toDelete.htm","cli/toDelete.html"],datasets: [{label:"Lignes ajoutées",data:["1","4","5","3","8","4","4","0","0"],backgroundColor:['#003f5c','#7a5195','#ef5675', '#ffa600'], hoverBorderWidth: 3,}],}});}