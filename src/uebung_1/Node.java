package uebung_1;

import java.util.ArrayList;

public class Node {
    private ArrayList<Neighbor> neighbors = new ArrayList<Neighbor>();
    private int id;
    private int demand = 0;
    private int coordX;
    private int coordY;
    private boolean cleared = false;


    public Node(int id, int coordX, int coordY) {
        this.id = id;
        this.coordX = coordX;
        this.coordY = coordY;
        if(id == 1){
            this.cleared = true;
        }    }

    public boolean isCleared() {
        return cleared;
    }

    public void setCleared(boolean cleared) {
        this.cleared = cleared;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public int getDemand() {
        return demand;
    }

    public void reduceDemand(int demand) {
        if (demand > this.demand) {
            this.demand = 0;
        } else {
            this.demand -= demand;
        }
    }
    public boolean hasDemandingNeighbors(){
        for (Neighbor neighbor : neighbors) {
            if (!(neighbor.getNode().isCleared())){
                return true;
            }
        }
        return false;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Neighbor> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(Neighbor nodeInfo) {
        neighbors.add(nodeInfo);
    }

    public int getCoordX() {
        return coordX;
    }

    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }
    public Neighbor getNeighborById(int id) {
        Neighbor n = null;
        for (int i = 0; i < neighbors.size(); i++) {
            if (neighbors.get(i).getNode().getId() == id) {
                n = neighbors.get(i);
                break;
            }
        }
        return n;
    }
    public Neighbor getClosestDemandingNeighbor() {
        int i = 0;  //Depot ist 1, daher bei 2 anfangen
        Neighbor closestNeighbor = null;
        if (neighbors.size() == 0) {
            return null;
        }
        while (i < neighbors.size()) {
           Neighbor nextNeighbor = neighbors.get(i);
            if (!nextNeighbor.getNode().isCleared()) { // voll versorgte Nachbarn überspringen
                if (closestNeighbor == null
                ||  nextNeighbor.getDistance() < closestNeighbor.getDistance()) {
                    //Erster Knoten mit Bedarf wird Vergleichsknoten
                   closestNeighbor = nextNeighbor;
                }
            }
            i++;
        }
        if ((closestNeighbor == null) && (this.id != 1)){
            //Sicherheitshalber; Rückkehr zu Depot, keine Nachbarn übrig
            closestNeighbor = this.getNeighborById(1);
        }
        return closestNeighbor;
    }

    public Neighbor GetRandomDemandingNeighbor() {
        Neighbor randomNeighbor = null;
        if (neighbors.size() == 0) {
            return null;
        }
        int id = (int) (Math.random() * neighbors.size());
        Neighbor nextNeighbor = neighbors.get(id);
        if (!nextNeighbor.getNode().isCleared()) { // voll versorgte Nachbarn überspringen
            if (randomNeighbor == null) {
                //Erster Knoten mit Bedarf wird Vergleichsknoten
                randomNeighbor = nextNeighbor;
                
            }
        }
        if ((randomNeighbor == null) && (this.id != 1)){
            //Sicherheitshalber; Rückkehr zu Depot, keine Nachbarn übrig
            randomNeighbor = this.getNeighborById(1);
        }
        return randomNeighbor;
    }
}
