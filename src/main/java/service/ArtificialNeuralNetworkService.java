package service;

import deepCopyUtil.DeepCopy;
import model.*;
import repository.FileRepo;

import java.util.ArrayList;
import java.util.Collections;

public class ArtificialNeuralNetworkService {

    private ActivationType activationType;
    private FileRepo repo;

    public ArtificialNeuralNetworkService(FileRepo repo){
        this.repo = repo;
    }

    private Double sigmoidFunction(Double x){
        return 1 / (1 + Math.exp(-x));
    }

    public Double activate(ArrayList<Double> inputs, ArrayList<Double> weights){
        Double result = 0.0;
        for(int i=0;i<inputs.size();i++){
            result += inputs.get(i) * weights.get(i);
        }
        result += weights.get(inputs.size());
        return result;
    }

    public Double transfer(Double value){
        if(activationType.equals(ActivationType.Linear)){
            return value;
        }
        else if(activationType.equals(ActivationType.Sigmoid)){
            return sigmoidFunction(value);
        }
        return null;
    }

    public Double transferInverse(Double value){
        if(activationType.equals(ActivationType.Linear)){
            return value;
        }
        else if(activationType.equals(ActivationType.Sigmoid)){
            return value * (1-value);
        }
        return null;
    }

    public Net initNet(Integer numberOfInputs,Integer numberOfOutputs,Integer numberOfHiddenLayers,Integer numberOfNeuronsPerHiddenLayer){
        Net net = new Net();
        for(int i=0;i<numberOfHiddenLayers;i++){
            Layer hiddenLayer = new Layer();
            for(int j=0;j<numberOfNeuronsPerHiddenLayer;j++){
                ArrayList<Double> weights = new ArrayList<>();
                for(int k=0;k<numberOfInputs+1;k++){
                    weights.add(Math.random());
                }
                Neuron neuron = new Neuron(weights);
                hiddenLayer.addNeuron(neuron);
            }
            net.addLayer(hiddenLayer);
        }
        Layer outputLayer = new Layer();
        for(int i=0;i<numberOfOutputs;i++){
            ArrayList<Double> weights = new ArrayList<>();
            for(int j=0;j<numberOfNeuronsPerHiddenLayer+1;j++){
                weights.add(Math.random());
            }
            Neuron neuron = new Neuron(weights);
            outputLayer.addNeuron(neuron);
        }
        net.addLayer(outputLayer);
        return net;
    }

    public ArrayList<Double> forwardPropagation(Net net,ArrayList<Double> inputs){
        for(Layer l:net.getLayers()){
            ArrayList<Double> newInputs = new ArrayList<>();
            for(Neuron n:l.getNeurons()){
                Double activation = activate(inputs,n.getWeights());
                n.setOutput(transfer(activation));
                newInputs.add(n.getOutput());
            }
            inputs = newInputs;
        }
        return inputs;
    }

    public void backwardsPropagation(Net net,ArrayList<Double> expected){
        for(int i=net.getNumberOfLayers()-1;i>=0;i--){
            Layer layer = net.getLayer(i);
            ArrayList<Double> errors = new ArrayList<>();
            if(i == net.getNumberOfLayers()-1){
                for(int j=0;j<layer.getNumberOfNeurons();j++){
                    Neuron neuron = layer.getNeuron(j);
                    errors.add(expected.get(j)-neuron.getOutput());
                }
            }
            else{
                for(int j=0;j<layer.getNumberOfNeurons();j++) {
                    Double crtError = 0.0;
                    Layer nextLayer = net.getLayer(i+1);
                    for(Neuron n:nextLayer.getNeurons()){
                        crtError += n.getWeight(j) * n.getDelta();
                    }
                    errors.add(crtError);
                }
            }
            for(int j=0;j<layer.getNumberOfNeurons();j++){
                layer.getNeuron(j).setDelta(errors.get(j)*transferInverse(layer.getNeuron(j).getOutput()));
            }
        }
    }

    public void updateWeights(Net net, ArrayList<Double> example, Double learningRate){
        for(int i=0;i<net.getNumberOfLayers();i++){
            ArrayList<Double> inputs =(ArrayList<Double>) DeepCopy.copy(example);
            if(i>0){
                ArrayList<Double> newInputs = new ArrayList<>();
                for(Neuron n:net.getLayer(i-1).getNeurons()){
                    newInputs.add(n.getOutput());
                }
                inputs = newInputs;
            }
            for(Neuron n:net.getLayer(i).getNeurons()){
                for(int j=0;j<inputs.size();j++){
                    n.setWeight(j,n.getWeight(j)+learningRate*n.getDelta()*inputs.get(j));
                }
                n.setWeight(n.getNumberOfWeights()-1,n.getWeight(n.getNumberOfWeights()-1)+learningRate*n.getDelta());
            }
        }
    }

    public void trainingMLP(Net net,Matrix<Double> data,Matrix<Double> results,Integer numberOfOutputTypes,Double learningRate,Integer numberOfEpochs){
        for(int i=0;i<numberOfEpochs;i++){
            Double sumError = 0.0;
            for(int j=0;j<data.getRows();j++){
                ArrayList<Double> inputs = data.getRow(j);
                ArrayList<Double> computedOutputs = forwardPropagation(net,inputs);
                ArrayList<Double> expected = new ArrayList<>();
                ArrayList<Double> computedLabels = new ArrayList<>();
                for(int k=0;k<numberOfOutputTypes;k++){
                    expected.add(0.0);
                    computedLabels.add(0.0);
                }
                if(results.get(j,0).equals(0.0))
                    expected.set(0,1.0);
                else if(results.get(j,0).equals(0.5))
                    expected.set(1,1.0);
                else
                    expected.set(2,1.0);


                computedLabels.set(computedOutputs.indexOf(Collections.max(computedOutputs)),1.0);
                computedOutputs = computedLabels;
                Double crtErr = 0.0;
                for(int k=0;k<expected.size();k++){
                    crtErr+=Math.pow(expected.get(k)-computedOutputs.get(k),2);
                }
                sumError += crtErr;
                backwardsPropagation(net,expected);
                updateWeights(net,inputs,learningRate);

            }
        }
    }

    public ArrayList<Double> evaluatingMLP(Net net,Matrix<Double> data,Matrix<Double> results,Integer numberOfOutputTypes){
        ArrayList<Double> computedOutputs = new ArrayList<>();
        for(int i=0;i<data.getRows();i++){
            ArrayList<Double> row = data.getRow(i);
            ArrayList<Double> computedOutput = new ArrayList<>();
            computedOutput = forwardPropagation(net,row);
            ArrayList<Double> computedLabels = new ArrayList<>();
            for(int j=0;j<numberOfOutputTypes;j++){
                computedLabels.add(0.0);
            }
            //computedLabels.set(computedOutput.indexOf(Collections.max(computedOutput)),1.0);
            //computedOutput = computedLabels;
            computedOutputs.add(computedOutput.indexOf(Collections.max(computedOutput))*0.5);
        }
        return computedOutputs;
    }

    public Double computePerformanceClassification(ArrayList<Double> computedOutputs,ArrayList<Double> realOutputs){
        Double numberOfMatches = 0.0;
        for(int i=0;i<computedOutputs.size();i++){
            if(computedOutputs.get(i).equals(realOutputs.get(i))){
                numberOfMatches++;
            }
        }
        return numberOfMatches/computedOutputs.size();
    }

    public Net runMLP(Double learningRate,Integer numberOfEpochs){
        activationType = ActivationType.Sigmoid;
        Matrix<Double> normalizedData = FileRepo.normalizeData(repo.getDataMatrix());
        Matrix<Double> normalizedResults = repo.getResultsMatrix();
        Net net = initNet(normalizedData.getColumns(),3,1,10);
        trainingMLP(net,normalizedData,normalizedResults,3,learningRate,numberOfEpochs);
        return net;
    }

    public double evaluateMLP(Net net){
        Matrix<Double> normalizedData = FileRepo.normalizeData(repo.getTestDataMatrix());
        Matrix<Double> normalizedResults = repo.getTestResultsMatrix();
        ArrayList<Double> computedOutputs = evaluatingMLP(net,normalizedData,normalizedResults,3);
        ArrayList<Double> realOutputs = new ArrayList<>();
        for(int i=0;i<normalizedResults.getRows();i++){
            realOutputs.add(normalizedResults.get(i,0));
        }
        return computePerformanceClassification(computedOutputs,realOutputs);
    }

}


