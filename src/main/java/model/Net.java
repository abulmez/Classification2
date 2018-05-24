package model;

import java.util.ArrayList;

public class Net {
    private ArrayList<Layer> layers;

    public Net(){
        layers = new ArrayList<>();
    }

    public ArrayList<Layer> getLayers() {
        return layers;
    }

    public void setLayers(ArrayList<Layer> layers) {
        this.layers = layers;
    }

    public void addLayer(Layer l){
        layers.add(l);
    }

    public Layer getLayer(Integer index){
        return layers.get(index);
    }

    public Integer getNumberOfLayers(){
        return layers.size();
    }
}
