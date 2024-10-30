package uebung_1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Cvrp_ls {

    private static StringBuilder output_diagonal;

    public static void main(String[] args) {
        String filename;
        // Pfad zur Datei
        if (args.length == 0) {
            filename = "src/Loggi-n401-k23.vrp";
        } else {
            filename = args[0];
        }
        String line = "";
        int dimension = 0;
        int capacity = 0;
        try {
            FileReader file = new FileReader(filename);
            BufferedReader reader = new BufferedReader(file);
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Beginne mit dem Lesen der Koordinaten
                if (line.contains("DIMENSION")) {
                    dimension = Integer.parseInt(line.split(": ")[1]);
                    break;
                }
            }
            // Koordinatenspeicher
            Integer[][] distances = read_distances_from_txt(filename, dimension);
            ArrayList<Node> nodes = read_nodes_from_txt(reader);

            // TODO: Greedy-Algorithmus implementieren

            // Zuordnung von Knoten und Distanzen
            // Depot ist Knoten[0], das Erste Gewicht ist von Knoten[1] zu Depot
            // TODO: Knotenzuordnung implementieren
            /** row + 1 = untere Knoten-ID (zählt ab 1 nicht ab 0)
             *  col + 1 = obere Knoten-ID
             *  row/col     = Distanz von unterer zu oberer Knoten-ID 
             */
            for (int row = 0; row < distances.length-1; row++) {
                for (int col = 0; col < distances[row].length-1; col++) {
                    Node lower = nodes.get(col+1);    //Knoten der unteren Matrixhälfte
                    Node upper = nodes.get(row+1);     //Knoten der oberen  Matrixhälfte
                
                    lower.addNeighbor(new Neighbor(upper.getId(),
                                                   distances[row][col]));
                    upper.addNeighbor(new Neighbor(lower.getId(),
                                                   distances[row][col]));
                    
                }
            }
            /**
             * Testausgaben erste 5 Zeilen/Knoten
             */
            StringBuilder output_diagonal = new StringBuilder();
            StringBuilder output_nodes = new StringBuilder();
            StringBuilder output_final = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    output_diagonal.append(distances[i][j]).append("\t");

                }
                output_nodes.append("ID: ").append(nodes.get(i).getId()).append("\t")
                            .append("X = ").append(nodes.get(i).getCoordX()).append("\t")
                            .append("Y = ").append(nodes.get(i).getCoordY()).append("\n");
                output_diagonal.append("\n");
            }
            /**
             * Testausgabe erste 5 Knotenzuordnungen
             */
            for (int i = 0; i < 5; i++) {
               ArrayList<Neighbor> neighbors = nodes.get(i).getNeighbors();
               for (int j = 0; j < neighbors.size(); j++) {
                if(j < 5){
                    output_final.append("Knoten " + nodes.get(i).getId()).append("\n");
                    output_final.append("Nachbar ID:"+neighbors.get(j).getId()+"\t");
                    output_final.append("Distanz: " +  neighbors.get(j).getDistance()+"\n");
                }
               }
                
            }
            System.out.println(output_diagonal.toString() + "\n\n");
            System.out.println(output_nodes.toString());
            System.out.println(output_final.toString());
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Node> read_nodes_from_txt(BufferedReader reader) throws IOException {
        ArrayList<Node> nodes = new ArrayList<Node>();
        boolean coords = false;
        int id = 0; // Für Array-Platzlesung
        int coordx = 1; // Für Array-Platzlesung
        int coordy = 2; // Für Array-Platzlesung

        String line = "";
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            // Beginne mit dem Lesen der Koordinaten
            if (line.equals("NODE_COORD_SECTION")) {
                coords = true;
                break;
            }
        }
        while (coords == true) {
            line = reader.readLine();
            // Ende der Koordinatenrubrick
            if (line.equals("DEMAND_SECTION")) {
                coords = false;
                break;
            } else {
                // Werte der Koordinatensektion auslesen und Objektivieren
                int[] values = new int[3];
                values[id] = Integer.parseInt(line.split(" ")[id]);
                values[coordx] = Integer.parseInt(line.split(" ")[coordx]);
                values[coordy] = Integer.parseInt(line.split(" ")[coordy]);
                // innerhalb der Koordinatenrubrick
                nodes.add(new Node(values[id],
                        values[coordx],
                        values[coordy]));
            }
        }
        return nodes;
    }

    private static Integer[][] read_distances_from_txt(String filename, int dimension) throws IOException {
        Integer[][] distances = new Integer[dimension][dimension];
        boolean edges = false;
        FileReader file = new FileReader(filename);
        BufferedReader reader = new BufferedReader(file);
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            // Beginne mit dem Lesen der Kantendistanzen
            if (line.equals("EDGE_WEIGHT_SECTION")) {
                edges = true;
                break;
            }
        }
        int row = 0; // Reihe der Spiegelmatrix
        while (edges == true) {
            line = reader.readLine();
            // Ende der Kantenrubrick
            if (line.equals("NODE_COORD_SECTION")) {
                edges = false;
                break;
            } else {
                // Werte der Kantensektion auslesen und Objektivieren
                String[] txtvalues = line.split(" ");
                // Einordnung der Textzeile in Matrix
                for (int i = 0; i < txtvalues.length; i++) {
                    distances[row][i] = Integer.parseInt(txtvalues[i]); // Diagonale Wertkopie
                    distances[i][row] = Integer.parseInt(txtvalues[i]); // macht Zuordnung leichter später
                }
            }
            row++;
        }
        return distances;
    }
}
