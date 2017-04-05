package com.cacheserverdeploy.deploy;

public class Population {
    int populationSize = 0;
    private Individual[] individuals;

    private double[] relativeFitness;
    private double totalFitness;
    private double averageFitness;

    // 交叉概率
    private double crossP = 0.6;
    private double pmutation = 0.5; //突变概率
    private Boolean initialise = false;

    /*
     * 构造方法
     */
    // 创建一个种群
    public Population(int populationSize){
        this.populationSize = populationSize;

        individuals = new Individual[populationSize];
        relativeFitness = new double[populationSize];
    }

    public Population(int populationSize, Boolean initialise, int genesLength) {
        this.populationSize = populationSize;

        individuals = new Individual[populationSize];
        relativeFitness = new double[populationSize];

        this.initialise = initialise;
        // 初始化种群
        if (initialise) {
            for (int i = 1; i < size(); i++) {
                Individual newIndividual = new Individual(genesLength);
                newIndividual.generateIndividual();
                saveIndividual(i, newIndividual);
            }
        }
    }

    /*
     * 为下一代进化挑选较优个体
     */

    public Population select() {
        double[] rouletteWheel; //赌盘
        Population childPopulation = new Population(populationSize);

        calRelativeFitness();

        //产生赌盘
        rouletteWheel = new double[populationSize];
        rouletteWheel[0] = relativeFitness[0];
        for (int i = 1; i < populationSize - 1; i++) {
            rouletteWheel[i] = relativeFitness[i] + rouletteWheel[i - 1];
        }
        rouletteWheel[populationSize - 1] = 1;

        Individual tempChild = null;

        //进行赌盘选择,产生新种群
        for (int i = 0; i < populationSize; i++) {

            tempChild = new Individual(individuals[i].size());

            double rnd = rand();
            for (int j = 0; j < populationSize; j++) {

                tempChild.setGene(individuals[i].getGene());
                tempChild.setFitness(individuals[i].getFitness());

                if (rnd < rouletteWheel[j] && tempChild.getFitness() > this.averageFitness) {
                    break;
                }
            }
            childPopulation.individuals[i] = tempChild;
        }

        return childPopulation;
    }

    /*
        * 进行种群中个体的交叉
        */
    public void cross() {
        for (int i = 0; i < size(); i++) {

            int pos1 = rand(0, size());
            int pos2 = rand(0, size());
            while (pos1 == pos2) {
                pos2 = rand(0, size());
            }

            // 通过概率选择是否进行交叉
            double pick = rand();
            while (pick == 0) {
                pick = rand();
            }

            if (pick > crossP) {
                continue;
            }

            //Individual childIndividual1 = individuals[pos1];
            //Individual childIndividual2 = individuals[pos2];

            //int flag = 0;
            //for(int j = 0; j < 3; j++){
            pick = rand();
            while (pick == 0) {
                pick = rand();
            }

            // 选择交叉位置
            int bitpos = (int) rand() * individuals[pos1].size();
            byte bit1 = individuals[pos1].getGene(bitpos);
            byte bit2 = individuals[pos2].getGene(bitpos);

            individuals[pos1].setGene(bitpos, bit2);
            individuals[pos2].setGene(bitpos, bit1);

        }
    }

    /*
     * 进行变异
     */
    // 突变个体。 突变的概率为 mutationRate
    public void mutate() {
        for (int i = 0; i < size(); i++) {

            double pick = rand();
            while (pick == 0) {
                pick = rand();
            }

            if (pick > pmutation) {
                continue;
            }

            for (int j = 0; j < individuals[i].size(); j++) {
                if (rand() <= pmutation) {
                    byte gene = (byte) Math.round(rand());
                    individuals[i].setGene(j, gene);
                }
            }
        }

    }

    /*
     * 结合题目的变异
     */
    public void myMutate(){
        for(int i = 0; i < size(); i++){
            double pick = rand();
            while(pick == 0){
                pick = rand();
            }

            if(pick > pmutation){
                continue;
            }

            // 对每个个体进行迭代的变异操作
            Individual oldIndividual = new Individual(individuals[i].size());
            oldIndividual.setGene(individuals[i].getGene());
            oldIndividual.setFitness(individuals[i].getFitness());

            int hasNode = 0;
            int nextSize = 0;
            int nextHasNode = 0;


            for(int j = 0; j < individuals[i].size(); j++){

                Boolean visited = false;
                if(individuals[i].getGene(j) == 1){
                    // 该点有服务器进行迭代变异
                    hasNode = j;
                    nextSize = Deploy.graph.edgeArrList[hasNode].size();

                    while(nextHasNode == Deploy.st || nextHasNode == Deploy.ed){
                        int pos = rand(0, nextSize);
                        nextHasNode = Deploy.graph.edgeArrList[hasNode].get(pos).to;
                    }

                    individuals[i].setGene(nextHasNode, (byte) 1);
                    individuals[i].setGene(j, (byte) 0);

                    visited = true;

                    // 如果当前变异不满足，跳出
                    if(!individuals[i].normalChrom()){
                        // 不满足约束条件时使用上一代的个体
                        individuals[i].setGene(nextHasNode, oldIndividual.getGene(nextHasNode));
                        individuals[i].setGene(j, (byte) 0);

                        individuals[i].setFitness(oldIndividual.getFitness());
                        break;
                    }
                    System.out.println("变异成功");
                }

                if(visited){
                    oldIndividual = new Individual(individuals[i].size());
                    oldIndividual.setGene(individuals[i].getGene());
                    oldIndividual.setFitness(individuals[i].getFitness());
                }
            }
        }
    }

    /*
     * 找到种群中最优的个体
     */
    public Individual getFittest() {
        Individual fittest = individuals[0];
        // Loop through individuals to find fittest
        for (int i = 0; i < size(); i++) {
            if (fittest.getFitness() > getIndividual(i).getFitness()) {
                fittest = getIndividual(i);
            }
        }
        return fittest;
    }

    /*
     * 计算种群的总适应度
     */
    public void calTotalFitness() {
        double total = 0;
        for (int i = 0; i < size(); i++) {
            total += individuals[i].getFitness();
        }

        totalFitness = total;
        averageFitness = totalFitness/size();
    }

    /*
     * 计算种群的相对适应度
     */
    public void calRelativeFitness() {
        calTotalFitness();

        for (int i = 0; i < size(); i++) {
            relativeFitness[i] = individuals[i].getFitness() / totalFitness;
        }
    }

    public double[] getRelativeFitness() {
        return relativeFitness;
    }

    public double getAverageFitness() {
        return averageFitness;
    }

    public double getTotalFitness() {
        return totalFitness;
    }

    private double rand() {
        return Math.random();
    }

    // 产生[start, end)的随机数
    private int rand(int start, int end) {
        return (int) (rand() * (end - start) + start);
    }

    // Get population size
    public int size() {
        return individuals.length;
    }

    // Save individual
    public void saveIndividual(int index, Individual indiv) {
        individuals[index] = indiv;
    }

    /* Getters */
    public Individual getIndividual(int index) {
        return individuals[index];
    }
}