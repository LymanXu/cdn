package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.List;

public class Algorithm {

    /* GA 算法的参数 */
    private static final double uniformRate = 0.7; //交叉概率
    private static final double mutationRate = 0.003; //突变概率
    private static final int tournamentSize = 5; //淘汰数组的大小
    private static final boolean elitism = true; //精英主义

    public static Individual globalBestIndividual = null;
    public static Individual bestIndividualForSA = null;

    public static Population oldPopulation = null;
    public static Population childPopulation = null;
    private static int geneLength = 0;


    /*
     * 进化一个种群
     */
    public static void evolvePopulation() {


        // 1. 把父代最优秀的那个子代的第一个位置
        if (elitism) {
            childPopulation.getIndividual(0).setGene(oldPopulation.getFittest().getGene());
            childPopulation.getIndividual(0).setFitness(oldPopulation.getFittest().getFitness());
        }

        // Crossover population
        int elitismOffset;
        if (elitism) {
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }

        //2. 进行交叉操作，保存到子代
        for (int i = elitismOffset; i < oldPopulation.size(); i++) {
            double pick = rand();
            while (pick == 0) {
                pick = rand();
            }

            if (pick < uniformRate) {
                Individual indiv1 = tournamentSelection(oldPopulation, geneLength);
                Individual indiv2 = tournamentSelection(oldPopulation, geneLength);
                //进行交叉
                Individual newIndiv = crossover(indiv1, indiv2, geneLength);

                childPopulation.getIndividual(i).setGene(newIndiv.getGene());
            } else {
                childPopulation.getIndividual(i).setGene(oldPopulation.getIndividual(i).getGene());
            }

            //childPopulation.getIndividual(i).setFitness(newIndiv.getFitness());
        }

        // 3. 进行变异操作，使用子代操作
        for (int i = elitismOffset; i < childPopulation.size(); i++) {
            mutate(childPopulation.getIndividual(i));
        }

        // 4. 进行子代个体的检验，
        int rebackCount = 0;
        for (int i = 0; i < childPopulation.size(); i++) {
            if (!childPopulation.getIndividual(i).normalChrom()) {
                // 不满足约束条件时使用上一代的个体
                rebackCount++;

                childPopulation.getIndividual(i).setGene(oldPopulation.getIndividual(i).getGene());
                childPopulation.getIndividual(i).setFitness(oldPopulation.getIndividual(i).getFitness());
            }
        }
        System.out.println("@@@@ rebackCount: " + rebackCount);
    }

    /*
     * 结合模拟退火的遗传算法
     */
    public static void SAGA(int geneLength1) {

        geneLength = geneLength1;

        // 模拟退火算法参数
        double q = 0.8;
        double T0 = 200;
        double Tend = 1;

        double T = T0;
        int L = 100;  // 模拟退火的链长,和基因位一样后边用的

        // 遗传算法参数
        int sizePop = 20;
        int maxGen = 20;
        double pc = 0.7;
        double pm = 0.01;

        int generationCount = 0;

        int stayCount = 0;
        Boolean flag = false;
        int gen = 0;

        while (T > Tend){// && (System.currentTimeMillis() - Deploy.startTime) < 85000) {
            gen = 0;

            if (stayCount < 20) {
                while (gen < maxGen){// && (System.currentTimeMillis() - Deploy.startTime) < 85000) {
                    // 1. 调用GA进行进化
                    evolvePopulation();

                    // 2. 使用模拟退火确定可接受解
                    for (int i = 0; i < childPopulation.size(); i++) {

                        double childFit = childPopulation.getIndividual(i).getFitness();
                        double oldFit = oldPopulation.getIndividual(i).getFitness();

                        if (childFit < oldFit) {
                            oldPopulation.getIndividual(i).setGene(childPopulation.getIndividual(i).getGene());
                            oldPopulation.getIndividual(i).setFitness(childPopulation.getIndividual(i).getFitness());
                        } else {
                            double pick = rand();

                            if (pick <= Math.exp((oldFit - childFit) / T)) {
                                oldPopulation.getIndividual(i).setGene(childPopulation.getIndividual(i).getGene());
                                oldPopulation.getIndividual(i).setFitness(childPopulation.getIndividual(i).getFitness());
                            }
                        }

                    }

                    //3. 更新种群的平均适应度
                    oldPopulation.calTotalFitness();

                    // 4.将

                    // 更新种群最优解
                    Individual bestIndividual = oldPopulation.getFittest();
                    if (globalBestIndividual == null || bestIndividual.getFitness() < globalBestIndividual.getFitness()) {
                        globalBestIndividual = new Individual(geneLength);

                        globalBestIndividual.setGene(bestIndividual.getGene());
                        globalBestIndividual.setFitness(bestIndividual.getFitness());

                        flag = true;
                        stayCount = 0;

                    } else {
                        if (!flag) {
                            stayCount++;
                        }
                        flag = false;
                    }


                    generationCount++;
                    System.out.println("Generation: " + generationCount + " Fittest: "
                            + Algorithm.globalBestIndividual.getFitness() + " Average Fit: " + oldPopulation.getAverageFitness());
                    gen++;
                }
            } else {
                // 更新种群最优解
                if(bestIndividualForSA == null){
                    bestIndividualForSA = new Individual(geneLength);
                }
                bestIndividualForSA.setGene(globalBestIndividual.getGene());
                bestIndividualForSA.setFitness(globalBestIndividual.getFitness());

                newGeneMetropolis(L, T, pm);

                // 更新种群最优解
                if (bestIndividualForSA.getFitness() < globalBestIndividual.getFitness()) {
                    globalBestIndividual.setGene(bestIndividualForSA.getGene());
                    globalBestIndividual.setFitness(bestIndividualForSA.getFitness());
                }

                System.out.println("Generation: " + generationCount + " Fittest: "
                        + Algorithm.globalBestIndividual.getFitness() + " Average Fit: " + oldPopulation.getAverageFitness());

            }

            T = T * q;
        }
    }

    /*
     * 模拟退火产生新解并Metropolis选择
     */
    public static void newGeneMetropolis(int L, double T, double pm) {
        Individual tempNew = new Individual(geneLength);
        Individual tempStay = new Individual(geneLength);

        tempStay.setGene(bestIndividualForSA.getGene());
        tempStay.setFitness(bestIndividualForSA.getFitness());

        tempNew.setGene(bestIndividualForSA.getGene());
        tempNew.setFitness(bestIndividualForSA.getFitness());

        byte visited = (byte) 1;
        byte visiteNo = (byte) 0;


        int newAnswerCount = 0;

        for (int j = 0; j < L; j++) {

            tempNew.setGene(tempStay.getGene());

            int pos = rand(0, geneLength);
            if (tempNew.getGene(pos) == visited) {

                tempNew.setGene(pos, visiteNo);

            } else {

                // 交换位置
                tempNew.setGene(pos, visited);
                int posOfIN = findIndex(tempNew);
                tempNew.setGene(posOfIN, visiteNo);
            }

            if (tempNew.normalChrom()) {
                // 满足条件的可行解使用接受准则，else不处理
                double oldFit = tempStay.getFitness();

                if (tempNew.getFitness() < oldFit) {

                    tempStay.setGene(tempNew.getGene());
                    tempStay.setFitness(tempNew.getFitness());

                } else if (Math.exp((oldFit - tempNew.getFitness()) / T) >= rand()) {
                    // 以概率接受可行解
                    tempStay.setGene(tempNew.getGene());
                    tempStay.setFitness(tempNew.getFitness());
                }

                newAnswerCount++;
            }
        }

        bestIndividualForSA.setGene(tempStay.getGene());
        bestIndividualForSA.setFitness(tempStay.getFitness());

        //System.out.println("每个个体产生新解，成功次数为："+ newAnswerCount + " 失败次数："+ (L-newAnswerCount));

    }

    public static int findIndex(Individual tempIndi){
        List<Integer> indexs = new ArrayList<>();

        byte[] tempV = tempIndi.getGene();
        byte temp = 1;
        for(int i = 0; i < tempIndi.size();i++){

            if(tempV[i] == temp){
                indexs.add(i);
            }
        }


        int pos = rand(0,indexs.size());
        return indexs.get(pos);
    }
    /*
     * 生成全局的父代种群和子代种群
     */
    public static void initGlobalPopulation(int popSize, int geneLength) {
        oldPopulation = new Population(popSize);
        childPopulation = new Population(popSize);

        for (int i = 0; i < popSize; i++) {
            oldPopulation.saveIndividual(i, new Individual(geneLength));
            childPopulation.saveIndividual(i, new Individual(geneLength));
        }

    }

    // 进行两个个体的交叉 (暂且想象为make love的过程吧)。 交叉的概率为uniformRate
    private static Individual crossover(Individual indiv1, Individual indiv2, int geneLenght) {
        Individual newSol = new Individual(geneLenght);
        // 随机的从 两个个体中选择 
        for (int i = 0; i < indiv1.size(); i++) {
            if (Math.random() <= uniformRate) {
                newSol.setGene(i, indiv1.getGene(i));
            } else {
                newSol.setGene(i, indiv2.getGene(i));
            }
        }
        return newSol;
    }

    // 突变个体。 突变的概率为 mutationRate
    private static void mutate(Individual indiv) {
        byte tempIn = 1;
        byte tempNo = 0;

        for (int i = 0; i < indiv.size(); i++) {
            if (Math.random() <= mutationRate) {
                // 生成随机的 0 或 1
                byte gene = (byte) Math.round(Math.random());
                indiv.setGene(i, gene);
            }
        }
    }

    // 随机选择一个较优秀的个体，用了进行交叉
    private static Individual tournamentSelection(Population pop, int geneLenght) {
        // Create a tournament population
        Population tournamentPop = new Population(tournamentSize);
        //随机选择 tournamentSize 个放入 tournamentPop 中
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournamentPop.saveIndividual(i, pop.getIndividual(randomId));
        }
        // 找到淘汰数组中最优秀的
        Individual fittest = tournamentPop.getFittest();
        return fittest;
    }

    private static double rand() {
        return Math.random();
    }

    private static int rand(int start, int end) {
        return (int) (rand() * (end - start) + start);
    }

}