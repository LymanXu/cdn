package com.cacheserverdeploy.deploy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainTest {

	public static int generationCount = 0;
	public static Population myPop;
	public static int chromlen;
	public static int maxGenerate;

	// mincostmaxflow 调用函数
	public static int[] myGA(int chromlen1, byte[] first,int maxGenerate1){
		/*
		 * @Param:种群大小、基因长度
		 */
		chromlen=chromlen1;
		maxGenerate=maxGenerate1;
		myPop = new Population(20,true, chromlen);
		for(int i = 0; i < myPop.size(); i++){
			Individual individual = new Individual(chromlen);
			individual.setGene(first);
			myPop.saveIndividual(i, individual);
		}
		//Individual individual = new Individual(chromlen);
		//individual.setServers(first);
		//myPop.saveIndividual(0, individual);
		// 不段迭代，进行进化操作。 直到找到期望的基因序列
//		long start=System.currentTimeMillis();
//		while (generationCount < maxGenerate&&(System.currentTimeMillis()-start)<80000) {

		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {

				while (generationCount < maxGenerate) {
					generationCount++;
//					System.out.println("Generation: " + generationCount + " Fittest: "
//							+ 1/myPop.getFittest().getFitness());
					myPop = Algorithm.evolvePopulation(myPop, chromlen);
				}

			}
		});
		thread.start();
		try {
			Thread.sleep(85000);
			System.out.println(generationCount);
			thread.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

//		System.out.println("Solution found!");
//		System.out.println("Generation: " + generationCount);
//		System.out.println("Final Fittest Genes:");
//		System.out.println(myPop.getFittest());

		Individual bestIndividual = myPop.getFittest();

		return bestIndividual.getServers();

	}
}