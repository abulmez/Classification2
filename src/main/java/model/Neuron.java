package model;

import java.util.ArrayList;

public class Neuron {

    private ArrayList<Double> weights;
    private Double output;
    private Double delta;

    public Neuron(ArrayList<Double> weights) {
        this.weights = weights;
        output = 0.0;
        delta = 0.0;
    }

    public ArrayList<Double> getWeights() {
        return weights;
    }

    public void setWeights(ArrayList<Double> weights) {
        this.weights = weights;
    }

    public Double getOutput() {
        return output;
    }

    public void setOutput(Double output) {
        this.output = output;
    }

    public Double getDelta() {
        return delta;
    }

    public void setDelta(Double delta) {
        this.delta = delta;
    }

    public double getWeight(Integer index){
        return weights.get(index);
    }

    public void setWeight(Integer index,Double value){
        weights.set(index,value);
    }

    public Integer getNumberOfWeights(){
        return weights.size();
    }
}
