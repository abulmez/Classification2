package service;

import deepCopyUtil.DeepCopy;
import model.*;
import model.Queue;
import model.Stack;
import org.apache.commons.lang3.StringUtils;
import repository.FileRepo;

import java.rmi.MarshalException;
import java.util.*;

import static org.apache.commons.lang3.math.NumberUtils.isCreatable;

public class EvolutionaryAlgorithmService {

    private FileRepo repo;
    private Matrix<Double> normalizedData;
    private Matrix<Double> normalizedResults;

    public EvolutionaryAlgorithmService(FileRepo repo){
        normalizedData = FileRepo.normalizeData(repo.getDataMatrix());
        normalizedResults = repo.getResultsMatrix();
        this.repo = repo;
    }

    private Double sigmoidFunction(Double x){
        return 1 / (1 + Math.exp(-x));
    }

    private Double getValue(Matrix<Double> data,String coefficient,Integer row){
        if(coefficient.contains("x")){
            return data.get(row,Integer.parseInt(coefficient.split("x")[1]));
        }
        else return Double.parseDouble(coefficient);
    }

    private void processNode(Matrix<Double> data,Node<String> current,Stack<String> coefficients,Integer row){
        if (current.getData().contains("x") || isCreatable(current.getData())) {
            coefficients.push(current.getData());
        } else {
            if(current.getData().equals("sqrt")){

                Double arg1 = getValue(data,coefficients.pop(), row);
                if(arg1>=0)
                     coefficients.push(String.valueOf (Math.sqrt(arg1)));
                else coefficients.push(String.valueOf (arg1));
            }
            else if(current.getData().equals("sin")){
                Double arg1 = getValue(data,coefficients.pop(), row);
                coefficients.push(String.valueOf (Math.sin(arg1)));
            }
            else if(current.getData().equals("cos")){
                Double arg1 = getValue(data,coefficients.pop(), row);
                coefficients.push(String.valueOf (Math.cos(arg1)));
            }
            else {
                Double arg1 = getValue(data,coefficients.pop(), row);
                Double arg2 = getValue(data,coefficients.pop(), row);
                switch (current.getData()) {
                    case "add": {
                        coefficients.push(String.valueOf (arg1+arg2));
                        break;
                    }
                    case "subtract": {
                        coefficients.push(String.valueOf (arg1-arg2));
                        break;
                    }
                    case "multiply": {
                        coefficients.push(String.valueOf (arg1*arg2));
                        break;
                    }
                    case "power": {
                        coefficients.push(String.valueOf (Math.pow(arg1,arg2)));
                        break;
                    }
                }
            }
        }
    }

    private void cumsum(List<Double> list){
        for(int j=1;j<list.size();j++){
            list.set(j,list.get(j)+list.get(j-1));
        }
    }

    private Integer vasInv(List<Double> list, Double random){
        for(int i=0;i<list.size();i++){
            if(random<list.get(i)){
                return i;
            }
        }
        return -1;
    }

    public Double getDeterminedValue(Tree<String> tree,Matrix<Double> data,Integer row,Integer resultColumn) {
        Stack<Node> stack = new Stack<>();
        Stack<String> coefficients = new Stack<>();
        Node<String> node = tree.getRoot();
        stack.push(node);
        Node<String> prev = null;
        while (!stack.isEmpty()) {
            Node<String> current = stack.peek();
            if (prev == null || prev.getRightChild() == current ||
                    prev.getLeftChild() == current) {
                if (current.getLeftChild() != null)
                    stack.push(current.getLeftChild());
                else if (current.getRightChild() != null)
                    stack.push(current.getRightChild());
                else {
                    stack.pop();
                    processNode(data,current, coefficients, row);
                }
            } else if (current.getLeftChild() == prev) {
                if (current.getRightChild() != null)
                    stack.push(current.getRightChild());
                else {
                    stack.pop();
                    processNode(data,current, coefficients, row);
                }
            } else if (current.getRightChild() == prev) {
                stack.pop();
                processNode(data,current, coefficients, row);
            }
            prev = current;
        }
        return Double.valueOf(coefficients.pop());
    }

    public Double fitness(Chromosome c,Integer resultColumn){
        Double totalError = 0.0;
        for(int i=0;i<normalizedData.getRows();i++) {
            totalError += Math.abs(normalizedResults.get(i,resultColumn)-sigmoidFunction(getDeterminedValue(c.getTree(),normalizedData,i,resultColumn)));
        }
        return totalError;
    }

    public String getRandomFromStringArray(ArrayList<String> stringArray){
        Collections.shuffle(stringArray);
        return stringArray.get(0);
    }

    public Tree<String> generateTree(Integer depth, ArrayList<String> operations,ArrayList<String> operators){
        Tree<String> tree = new Tree<>();
        Node<String> root = new Node<>(getRandomFromStringArray(operations));
        tree.setRoot(root);
        Queue<Pair<Node<String>,Integer>> queue = new Queue<>();
        queue.push(new Pair<>(root,0));
        while(!queue.isEmpty()){
            Pair<Node<String>,Integer> topPair = queue.pop();
            Node<String> topNode = topPair.getFirst();
            Integer level = topPair.getSecond();
            if(!level.equals(depth)){
                if(!topNode.getData().equals("sqrt") && !topNode.getData().equals("sin") && !topNode.getData().equals("cos")){
                    Node<String> left = new Node<>(getRandomFromStringArray(operations));
                    Node<String> right = new Node<>(getRandomFromStringArray(operations));
                    topNode.setRightChild(right);
                    topNode.setLeftChild(left);
                    queue.push(new Pair<>(left,level+1));
                    queue.push(new Pair<>(right,level+1));
                }
                else{
                    Node<String> left = new Node<>(getRandomFromStringArray(operations));
                    topNode.setLeftChild(left);
                    queue.push(new Pair<>(left,level+1));
                }
            }
            else{
                if(!topNode.getData().equals("sqrt") && !topNode.getData().equals("sin") && !topNode.getData().equals("cos")) {
                    Random random = new Random();
                    Double value = ((double) (random.nextInt(9) + 1));
                    //Double value = Math.random();
                    if(Math.random()<0.5){
                        value*=-1;
                    }
                    Node<String> left = new Node<>(value.toString());
                    Node<String> right = new Node<>(getRandomFromStringArray(operators));
                    topNode.setRightChild(right);
                    topNode.setLeftChild(left);
                }
                else{
                    Node<String> left = new Node<>(getRandomFromStringArray(operators));
                    topNode.setLeftChild(left);
                }
            }
        }
        return tree;
    }

    private ArrayList<Double> evaluatePopulation(Population p, Integer resultColumn){
        ArrayList<Double> errorList = new ArrayList<>();
        for(Chromosome c:p.getChromosomesList()){
            errorList.add(fitness(c,resultColumn));
        }
        return errorList;
    }

    private ArrayList<Double> sortChances(ArrayList<Double> errorList,ArrayList<Double> chance){
        for(int i=0;i<chance.size();i++){
            for(int j=0;j<chance.size();j++){
                if(i!=j && errorList.get(i)<errorList.get(j) && chance.get(i)<chance.get(j)){
                    Double aux = chance.get(i);
                    chance.set(i,chance.get(j));
                    chance.set(j,aux);
                }
            }
        }
        return chance;
    }


    private ArrayList<String> initOperations(){
        ArrayList<String> operations = new ArrayList<>();
        operations.add("add");
        operations.add("subtract");
        operations.add("multiply");
        operations.add("sqrt");
        operations.add("sin");
        operations.add("cos");
        return operations;
    }

    private ArrayList<String> getSingleCoeficientOperations(){
        ArrayList<String> operations = new ArrayList<>();
        operations.add("sqrt");
        operations.add("sin");
        operations.add("cos");
        return operations;
    }

    private ArrayList<String> getDoubleCoeficientOperations(){
        ArrayList<String> operations = new ArrayList<>();
        operations.add("add");
        operations.add("subtract");
        operations.add("multiply");
        return operations;
    }


    private ArrayList<String> initOperators(){
        ArrayList<String> operators = new ArrayList<>();
        for(int i=0;i<normalizedData.getColumns();i++){
            operators.add("x"+i);
        }
        return operators;
    }

    private Pair<Chromosome,Chromosome> offspring(Chromosome dad, Chromosome mom,Integer treeDepth){
        Tree<String> dadTreeCopy = (Tree<String>) DeepCopy.copy(dad.getTree());
        Tree<String> momTreeCopy = (Tree<String>) DeepCopy.copy(mom.getTree());
        Random r = new Random();
        Integer dadCutLevel = r.nextInt(treeDepth-2);
        Integer momCutLevel = r.nextInt(treeDepth-2);
        Node<String> dadCurrentNode = dadTreeCopy.getRoot();
        Node<String> momCurrentNode = momTreeCopy.getRoot();
        getToLevel(dadCutLevel, dadCurrentNode);
        getToLevel(momCutLevel, momCurrentNode);
        if(Math.random()<0.5 && dadCurrentNode.getRightChild()!=null && momCurrentNode.getRightChild()!=null){
            Node<String> aux = dadCurrentNode.getRightChild();
            dadCurrentNode.setRightChild(momCurrentNode.getRightChild());
            momCurrentNode.setRightChild(aux);
        }
        else {
            Node<String> aux = dadCurrentNode.getLeftChild();
            dadCurrentNode.setLeftChild(momCurrentNode.getLeftChild());
            momCurrentNode.setLeftChild(aux);
        }
        return new Pair<>(new Chromosome(dadTreeCopy),new Chromosome(momTreeCopy));
    }

    public void mutate(Chromosome c,Integer treeDepth){
        Tree<String> tree = c.getTree();
        Random r = new Random();
        Integer mutateLevel = r.nextInt(treeDepth-2);
        Node<String> currentNode = tree.getRoot();
        getToLevel(mutateLevel,currentNode);
        if(currentNode.getData().equals("sqrt") || currentNode.getData().equals("sin") || currentNode.getData().equals("cos")){
            currentNode.setData(getRandomFromStringArray(getSingleCoeficientOperations()));
        }
        else{
            currentNode.setData(getRandomFromStringArray(getDoubleCoeficientOperations()));
        }


    }

    private void getToLevel(Integer level, Node<String> currentNode) {
        while(!level.equals(0)){
            if(Math.random()<0.5 && currentNode.getRightChild()!=null){
                currentNode = currentNode.getRightChild();
            }
            else{
                currentNode = currentNode.getLeftChild();
            }
            level-=1;
        }
    }

    private Integer getBestChromosomeInPopulation(ArrayList<Double> errorList){
        Double min= Double.MAX_VALUE;
        Integer minPosition = 0;
        for(int i=0;i<errorList.size();i++){
            if(errorList.get(i)<min){
                minPosition = i;
                min = errorList.get(i);
            }
        }
        return minPosition;
    }



    public Tree<String> solve(Integer numberOfGenerations, Integer populationSize,Integer treeDepth,Integer resultColumn){
        Random r = new Random();
        Population p = new Population();
        ArrayList<String> operations = initOperations();
        ArrayList<String> operators = initOperators();
        for(int i=0;i<populationSize;i++) {
            p.addChromosome(new Chromosome(generateTree(treeDepth,operations,operators)));
        }
        for(int i=0;i<numberOfGenerations;i++){
            Population offsprings = new Population();
            ArrayList<Double> chance = new ArrayList<>();
            ArrayList<Double> errorList = evaluatePopulation(p,resultColumn);
            Double errorSum = errorList.stream().mapToDouble(Double::doubleValue).sum();
            for(Double elem:errorList){
                chance.add(elem/errorSum);
            }
            sortChances(errorList,chance);
            cumsum(chance);
            for(int j=0;j<populationSize/2;j++) {
                if(Math.random()<0.5) {
                    Chromosome dad = p.getChromosome(vasInv(chance, Math.random()));
                    Chromosome mom = p.getChromosome(vasInv(chance, Math.random()));
                    Pair<Chromosome, Chromosome> offspring = offspring(dad, mom, treeDepth);
                    offsprings.addChromosome(offspring.getFirst());
                    offsprings.addChromosome(offspring.getSecond());
                }
                else{
                    Chromosome chromosomeCopy1 =  (Chromosome) DeepCopy.copy(p.getChromosome(vasInv(chance, Math.random())));
                    mutate(chromosomeCopy1,treeDepth);
                    Chromosome chromosomeCopy2 =  (Chromosome) DeepCopy.copy(p.getChromosome(vasInv(chance, Math.random())));
                    mutate(chromosomeCopy2,treeDepth);
                    offsprings.addChromosome(chromosomeCopy1);
                    offsprings.addChromosome(chromosomeCopy2);
                }
            }
            Integer bestChromosomePosition = getBestChromosomeInPopulation(errorList);
            Chromosome bestFromLastPopulation = (Chromosome) DeepCopy.copy(p.getChromosome(bestChromosomePosition));
            offsprings.getChromosomesList().remove(r.nextInt(populationSize));
            offsprings.addChromosome(bestFromLastPopulation);
            p = offsprings;
        }
        ArrayList<Double> errorList = evaluatePopulation(p,resultColumn);
        Integer bestChromosomePosition = getBestChromosomeInPopulation(errorList);
        return p.getChromosome(bestChromosomePosition).getTree();
    }


    public Double testAccuracy(Tree<String> tree, Integer resultColumn){
        Integer ok = 0;
        Matrix<Double> normalizedTestData = FileRepo.normalizeData(repo.getTestDataMatrix());
        Matrix<Double> normalizedTestDataResults = repo.getTestResultsMatrix();
        for(int i=0;i<normalizedTestData.getRows();i++){

            Double resultValue = getDeterminedValue(tree,normalizedTestData,i,resultColumn);
            //System.out.println(resultValue);
            resultValue = sigmoidFunction(resultValue);

            if(resultValue<=(1.0/3))
                resultValue = 0.0;
            else if(resultValue>(1.0/3) && resultValue<=(2.0/3) )
                resultValue = 0.5;
            else
                resultValue = 1.0;
            if(resultValue.equals(normalizedTestDataResults.get(i,resultColumn)))
                ok += 1;
        }
        return ok/(double)normalizedTestData.getRows();
    }



}
