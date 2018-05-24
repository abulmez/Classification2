import model.Chromosome;
import model.Tree;
import repository.FileRepo;
import service.EvolutionaryAlgorithmService;
import ui.UI;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args){

        /*
        FileRepo repo = new FileRepo("column_3C_weka_data.arff", "column_3C_weka_test.arff");
        EvolutionaryAlgorithmService evolutionaryAlgorithmService = new EvolutionaryAlgorithmService(repo);

        ArrayList<String> operations = new ArrayList<>();
        operations.add("add");
        operations.add("subtract");
        operations.add("multiply");
        operations.add("sqrt");

        ArrayList<String> operators = new ArrayList<>();
        for(int i=0;i<repo.getDataMatrix().getColumns();i++){
            operators.add("x"+i);
        }





      //  Tree<String> tree = evolutionaryAlgorithmService.generateTree(4,operations,operators);
       // for(int i=0;i<)
        //Double value = evolutionaryAlgorithmService.getDeterminedValue(tree,FileRepo.normalizeData(repo.getDataMatrix()),0,0);


        Tree<String> tree = evolutionaryAlgorithmService.solve(10,50,6,0);
        System.out.println((evolutionaryAlgorithmService.testAccuracy(tree,0))*100+"% acuratete");
        System.out.println("mere");
        */
        UI ui = new UI();
        ui.displayMainMenu();


    }
}
