package model;

import java.util.ArrayList;

public class Layer {
    ArrayList<Neuron> neurons;

    public Layer(){
        neurons = new ArrayList<>();
    }

    public ArrayList<Neuron> getNeurons() {
        return neurons;
    }

    public void setNeurons(ArrayList<Neuron> neurons) {
        this.neurons = neurons;
    }

    public void addNeuron(Neuron n){
        neurons.add(n);
    }

    public Neuron getNeuron(Integer index){
        return neurons.get(index);
    }

    public Integer getNumberOfNeurons(){
        return neurons.size();
    }


}
