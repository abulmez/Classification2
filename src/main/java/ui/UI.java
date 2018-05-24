package ui;

import model.Net;
import model.Tree;
import repository.FileRepo;
import service.ArtificialNeuralNetworkService;
import service.EvolutionaryAlgorithmService;

import java.util.ArrayList;
import java.util.Scanner;

public class UI {

    private EvolutionaryAlgorithmService evolutionaryAlgorithmService;
    private ArtificialNeuralNetworkService artificialNeuralNetworkService;

    private Scanner sc;
    public UI(){
        sc = new Scanner(System.in);
    }

    /**
     * Displays the main menu of the application
     */
    public void displayMainMenu() {

        int x=2;
        FileRepo repo = new FileRepo("column_3C_weka_data.arff", "column_3C_weka_test.arff");
        evolutionaryAlgorithmService = new EvolutionaryAlgorithmService(repo);
        artificialNeuralNetworkService =  new ArtificialNeuralNetworkService(repo);

        Boolean merge = true;
        while(merge) {

            System.out.println("Alegeti algoritmul de antrenare:");
            System.out.println("1.Retea neuronala artificiala");
            System.out.println("2.Programare genetica");
            System.out.println("3.Iesire");
            x = sc.nextInt();


            switch (x) {


                case 2: {
                    // System.out.print("done");
                    System.out.println("Dati numarul de generatii:");
                    int generationNumber = sc.nextInt();
                    System.out.println("Dati marimea populatiei:");
                    int populationSize = sc.nextInt();
                    System.out.println("Dati adancimea arborilor:");
                    int treeDepth = sc.nextInt();
                    int resultColumn = 0;
                    Tree<String> result = evolutionaryAlgorithmService.solve(generationNumber, populationSize, treeDepth , resultColumn );
                    System.out.println("Performanta algoritmului: " + (int)(evolutionaryAlgorithmService.testAccuracy(result,resultColumn)*100)+"%");
                    break;
                }


                case 1: {

                    int resultColumn = 0;
                    System.out.println("Dati gradul de invatare:");
                    Double learningRate = sc.nextDouble();
                    System.out.println("Dati numarul de iteratii:");
                    int numberOfIterations = sc.nextInt();
                    Net net = artificialNeuralNetworkService.runMLP(learningRate,numberOfIterations);
                    System.out.println();
                    System.out.println("Performanta algoritmului: " + (int)(artificialNeuralNetworkService.evaluateMLP(net)*100)+"%");
                    break;
                }
                case 3: {
                    merge = false;
                    break;
                }

                default:
                    System.out.println("Optiune invalida! Mai incercati!");
            }
        }


    }
}
