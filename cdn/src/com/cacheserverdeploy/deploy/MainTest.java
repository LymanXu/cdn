package com.cacheserverdeploy.deploy;

public class MainTest {

    public static int generationCount = 0;
    public static Population myPop;
    public static int geneLength;
    public static int popSize = 30;

    // mincostmaxflow 调用函数
    public static int[] myGA(int geneLength1, byte[] first, int consumerNum){

        geneLength = geneLength1;

        // 1. 为全局的父代种群和子代种群分配固定空间
        Algorithm.initGlobalPopulation(popSize, geneLength);

        // 2. 为全局父代种群生成初始解
        Algorithm.oldPopulation.getIndividual(0).setGene(first);
        for(int i =1; i < popSize; i++){
            Algorithm.oldPopulation.getIndividual(i).generateIndividual();
        }

        // 3. 全局父代种群同步到全局子代种群
        for(int i =0; i < popSize; i++){
            Algorithm.childPopulation.getIndividual(i).setGene(Algorithm.oldPopulation.getIndividual(i).getGene());
            Algorithm.childPopulation.getIndividual(i).setFitness(Algorithm.oldPopulation.getIndividual(i).getFitness());
        }

//		for(int i = 0; i < myPop.size(); i++){
//			Individual individual = new Individual(chromlen);
//			individual.setGene(first);
//			myPop.saveIndividual(i, individual);
//		}

        //Individual individual = new Individual(chromlen);
        //individual.setServers(first);
        //myPop.saveIndividual(0, individual);
        // 4. 遗传算法进行迭代求解
        //long start=System.currentTimeMillis();
        //while ((System.currentTimeMillis()-start)<85000) {

//		Thread thread=new Thread(new Runnable() {
//			@Override
//			public void run() {
//				while (true) {

        Algorithm.SAGA(geneLength);
        // }

//			}
//		});
//		thread.start();
//		try {
//			Thread.sleep(85000);
//			thread.stop();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

//		System.out.println("Solution found!");
//		System.out.println("Generation: " + generationCount);
//		System.out.println("Final Fittest Genes:");
//		System.out.println(myPop.getFittest());

        Individual bestIndividual = Algorithm.childPopulation.getFittest();

        return bestIndividual.getServers();

    }
}
