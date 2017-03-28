package com.ga;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Date: 2017/3/24 17:56
 * Abstract:给定参数，测试遗传算法
 */
class GeneticAlgorithms{
    public static double crossoverRate;//交叉概率
    public static double mutateRate;//变异概率
    public static int maxGeneration;//进化代数
    public static int populationSize;//群体大小

    static {
        //crossoverRate = 0.6;
        //mutateRate = 0.001;
        //maxGeneration  = 100;
        //populationSize = 500;
        maxGeneration  = 100;
        populationSize = 500;
        crossoverRate = 0.6;
        mutateRate = 0.001;
    }

    public static void main(String[] args)throws IOException {

        /*
        FileWriter fw = new FileWriter("result.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter System.out = new PrintWriter(bw);*/

        // 个体的基因个数
        int chromlen = 20;

        Population pop = new Population(populationSize, chromlen);
        pop.initPopulation();

        System.out.println("初始种群:\n" + pop);
        while(!pop.isEvolutionDone()){
            pop.evolve();
            System.out.print("第" + pop.getGeneration() + "代Best:" + pop.bestIndividual );
            System.out.print("第" + pop.getGeneration()  + "代current:" + pop.currentBest );
            System.out.println("");
        }
        System.out.println();
        System.out.println("第"+ pop.getGeneration()  + "代群体:\n" + pop);

        System.out.close();
    }

    // mincostmaxflow 调用函数
    public static int[] myGA(int chromlen){

        Population pop = new Population(populationSize, chromlen);
        pop.initPopulation();

        System.out.println("初始种群:\n" + pop);
        while(!pop.isEvolutionDone()){
            pop.evolve();
            System.out.print("第" + pop.getGeneration() + "代Best:" + pop.bestIndividual );
            System.out.print("第" + pop.getGeneration()  + "代current:" + pop.currentBest );
            System.out.println("");
        }
        System.out.println();
        System.out.println("第"+ pop.getGeneration()  + "代群体:\n" + pop);

        RosenbrockIndividual individual = (RosenbrockIndividual) pop.currentBest;
        return individual.getServerNodes();
    }

}