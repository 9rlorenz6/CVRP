## Bedienanweisung
Das Programm erwartet die Angabe der Instanz, des Algorithmus, der Zeit und einer Option in dem Format: <instance> <algorithm> <seconds> [<option>*]
1. java -cp bin/ uebung_1.Cvrp_ls "loggi" "taboo_search" 3         --> Tabusuche für 3 Sekunden
2. java -cp bin/ uebung_1.Cvrp_ls "loggi" "genetic" 3              --> Genetische Suche für 3 Sekunden
3. java -cp bin/ uebung_1.Cvrp_ls  ODER unvollständig              --> einfacher Greedy
Alle anderen Eingaben führen zur Durchführung des Greedy-Algorithmus mit dem Stammdatenset "loggi".
## Folder Structure
- `src`: the folder to maintain sources

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).
