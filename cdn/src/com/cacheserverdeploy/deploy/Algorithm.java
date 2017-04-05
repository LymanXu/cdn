package com.cacheserverdeploy.deploy;

public class Algorithm {

    /* GA 算法的参数 */
    private static final double uniformRate = 0.7; //交叉概率
    private static final double mutationRate = 0.003; //突变概率
    private static final int tournamentSize = 5; //淘汰数组的大小
    private static final boolean elitism = true; //精英主义

    public static Individual globalBestIndividual = null;
    public static Population oldPopulation = null;
    public static Population childPopulation = null;
    private static int geneLength = 0;


    /*
     * 进化一个种群
     */
    public static void evolvePopulation(int geneLength1) {

        geneLength = geneLength1;

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
        /* 2. 进行选择操作，使适应度较高的个体进入下代
        selectChild();

        for (int i = elitismOffset; i < oldPopulation.size(); i++) {

            double pick = rand();
            while (pick == 0){
                pick = rand();
            }
            if(pick > uniformRate){
                continue;
            }
            //随机选择两个 优秀的个体
            int randomId1 = (int) (rand() * childPopulation.size());
            int randomId2 = (int) (rand() * childPopulation.size());
            Individual indiv1 = childPopulation.getIndividual(randomId1);
            Individual indiv2 = childPopulation.getIndividual(randomId2);
            //进行交叉
            Individual newIndiv = crossover(indiv1, indiv2, geneLenght);

            childPopulation.getIndividual(i).setGene(newIndiv.getGene());
            //childPopulation.getIndividual(i).setFitness(newIndiv.getFitness());
        }*/

        //2. 进行交叉操作，保存到子代
        for (int i = elitismOffset; i < oldPopulation.size(); i++) {
            double pick = rand();
            while (pick == 0){
                pick = rand();
            }

            if(pick < uniformRate){
                Individual indiv1 = tournamentSelection(oldPopulation, geneLength);
                Individual indiv2 = tournamentSelection(oldPopulation, geneLength);
                //进行交叉
                Individual newIndiv = crossover(indiv1, indiv2, geneLength);

                childPopulation.getIndividual(i).setGene(newIndiv.getGene());
            }else{
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
        for(int i = 0; i < childPopulation.size(); i++){
            if(!childPopulation.getIndividual(i).normalChrom()){
                // 不满足约束条件时使用上一代的个体
                rebackCount++;

                childPopulation.getIndividual(i).setGene(oldPopulation.getIndividual(i).getGene());
                childPopulation.getIndividual(i).setFitness(oldPopulation.getIndividual(i).getFitness());
            }
        }
        System.out.println("@@@@ rebackCount: " + rebackCount);
    }

    /*
     * 使较优个体进入下代
     */
    public static void selectChild(){

        double[] rouletteWheel; //赌盘

        oldPopulation.calRelativeFitness();

        //产生赌盘
        int popSize = oldPopulation.size();
        double[] oldRelativeFit = oldPopulation.getRelativeFitness();

        rouletteWheel = new double[popSize];
        rouletteWheel[0] = oldRelativeFit[0];
        for (int i = 1; i < popSize - 1; i++) {
            rouletteWheel[i] = oldRelativeFit[i] + rouletteWheel[i - 1];
        }
        rouletteWheel[popSize - 1] = 1;

        Individual tempChild = null;

        //进行赌盘选择,产生新种群
        for (int i = 1; i < popSize; i++) {

            double rnd = rand();
            for (int j = 0; j < popSize; j++) {

                tempChild = oldPopulation.getIndividual(j);
                if (rnd < rouletteWheel[j] && tempChild.getFitness() > oldPopulation.getAverageFitness()) {

                    childPopulation.getIndividual(i).setGene(tempChild.getGene());
                    childPopulation.getIndividual(i).setFitness(tempChild.getFitness());
                    break;
                }
            }
        }
    }

    /*
     * 结合模拟退火的遗传算法
     */
    public static void SAGA(int geneLenght){
        // 模拟退火算法参数
        double q = 0.8;
        double T0 = 100;
        double Tend = 1;

        double T = T0;
        int L = geneLenght;  // 模拟退火的链长

        // 遗传算法参数
        int sizePop = 20;
        int maxGen = 20;
        double pc = 0.7;
        double pm = 0.01;

        int generationCount =0;
        int gen = 0;

        while(T > Tend ){// (System.currentTimeMillis()-Deploy.startTime)<85000){
            gen = 0;

            while(gen < maxGen){//  && (System.currentTimeMillis()-Deploy.startTime)<85000){
                // 1. 调用GA进行进化
                evolvePopulation(geneLenght);

                // 2. 使用模拟退火确定可接受解
                for(int i = 0; i < childPopulation.size(); i++){

                    double childFit = childPopulation.getIndividual(i).getFitness();
                    double oldFit = oldPopulation.getIndividual(i).getFitness();

                    if(childFit < oldFit){
                        oldPopulation.getIndividual(i).setGene(childPopulation.getIndividual(i).getGene());
                        oldPopulation.getIndividual(i).setFitness(childPopulation.getIndividual(i).getFitness());
                    }else{
                        double pick = rand();

                        if(pick <= Math.exp((oldFit - childFit)/T)){
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
                if(globalBestIndividual == null || bestIndividual.getFitness() < globalBestIndividual.getFitness()){
                    globalBestIndividual = bestIndividual;
                }

                generationCount++;
                System.out.println("Generation: " + generationCount + " Fittest: "
                        + Algorithm.globalBestIndividual.getFitness() + " Average Fit: " + oldPopulation.getAverageFitness());

                /*
                System.out.println("该代中每个个体适应度：");
                for(int i = 0; i < childPopulation.size(); i++){
                    System.out.printf("%6.1f",1/childPopulation.getIndividual(i).getFitness());
                    System.out.print(" ");
                }
                System.out.println();*/
                gen++;
            }

            // 使用模拟退火进行随机新解
            newGeneMetropolis(L , T, pm);

            // 更新种群最优解
            Individual bestIndividual = oldPopulation.getFittest();
            if(globalBestIndividual == null || bestIndividual.getFitness() < globalBestIndividual.getFitness()){
                globalBestIndividual = bestIndividual;
            }


            T = T * q;
        }
    }

    /*
     * 模拟退火产生新解并Metropolis选择
     */
    public static void newGeneMetropolis(int L , double T ,double pm){
        Individual tempNew = new Individual(geneLength);

        byte visited = (byte) 1;
        byte visiteNo = (byte) 0;

        for(int i = 1; i < oldPopulation.size(); i++){

            int newAnswerCount = 0;

            for(int j = 0; j < L; j++){

                tempNew.setGene(oldPopulation.getIndividual(i).getGene());

                int pos = rand(0,geneLength);
                if(tempNew.getGene(pos) == visited){

                    tempNew.setGene(pos, (byte) rand());

                }else{

                    tempNew.setGene(pos, (byte) rand());
                }

                if(tempNew.normalChrom()){
                    // 满足条件的可行解使用接受准则，else不处理
                    double oldFit = oldPopulation.getIndividual(i).getFitness();

                    if(tempNew.getFitness() < oldFit){

                        oldPopulation.getIndividual(i).setGene(tempNew.getGene());
                        oldPopulation.getIndividual(i).setFitness(tempNew.getFitness());

                    }else if(Math.exp((oldFit - tempNew.getFitness())/T)>=rand()){
                        // 以概率接受可行解
                        oldPopulation.getIndividual(i).setGene(tempNew.getGene());
                        oldPopulation.getIndividual(i).setFitness(tempNew.getFitness());
                    }

                    newAnswerCount++;
                }
            }

            //System.out.println("每个个体产生新解，成功次数为："+ newAnswerCount + " 失败次数："+ (L-newAnswerCount));
        }

    }

    /*
     * 生成全局的父代种群和子代种群
     */
    public static void initGlobalPopulation(int popSize, int geneLength){
        oldPopulation = new Population(popSize);
        childPopulation = new Population(popSize);

        for(int i =0; i < popSize; i++){
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