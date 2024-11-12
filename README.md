## Bedienanweisung
Das Programm erwartet die Angabe der Instanz, des Algorithmus, der Zeit und einer Option in dem Format: \<instance> **\<algorithm>** **\<seconds>** <*option*> <p>

## Vor dem Start
Das Programm ist auf Github [Hier](https://github.com/9rlorenz6/CVRP)
zu finden.<br>

Für Lauffähigkeit muss das Programm kompiliert werden mit:
```Java
javac -d bin src/uebung_1/*.java
```
Es wurde mit Java <U>17.0.7</u> gebaut.

### Auswahl-Optionen (STRG+C):
1.  java -cp bin/ uebung_1.Cvrp_ls *src/Loggi-n401-k23.vrp* *taboo_search* **3**  **100**   
       - Tabusuche für **3 Sekunden** mit Tabu-Verwerfung nach **100 Runden**
2. java -cp bin/ uebung_1.Cvrp_ls src/Loggi-n401-k23.vrp genetic **3 0.99**
      -  Genetische Suche für **3 Sekunden** mit Fitness-Minimum **1% Besser** als vorherige Generationsinstanz
3. java -cp bin/ uebung_1.Cvrp_ls **greedy**
   -  einfacher Greedy

Alle Anderen Eingaben führen zu keiner aktiven Durchführung.
