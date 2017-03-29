package com.simpleGa;

import com.cacheserverdeploy.deploy.Deploy;

public class FitnessCalc {

    static byte[] solution = new byte[64];

    /* Public methods */
    // 设置候选结果为一个 byte array
    public static void setSolution(byte[] newSolution) {
        solution = newSolution;
    }

    // 就是把01 字符串转换为 01数组， 放在 solution中
    static void setSolution(String newSolution) {
        solution = new byte[newSolution.length()];
        // Loop through each character of our string and save it in our byte 
        for (int i = 0; i < newSolution.length(); i++) {
            String character = newSolution.substring(i, i + 1);
            if (character.contains("0") || character.contains("1")) {
                solution[i] = Byte.parseByte(character);
            } else {
                solution[i] = 0;
            }
        }
    }

    // 通过和solution比较 ，计算个体的适应值
    static double getFitness(Individual individual) {
        int fitness = 0;

        // 将byte[]转换为int[]
        int[] servers = new int[individual.size()];

        for(int i = 0; i < individual.size(); i++){
            if(individual.getGene()[i] == 1){
                servers[i] = 1;
            }else{
                servers[i] = 0;
            }
        }

        // 调用mincost maxflow的算法
        ResultForGA resultForGA = Deploy.getMinCost(servers);
        double cost = resultForGA.getCost();

        return 1.0/cost;
    }


    /*最优的适应值，即为基因序列的长度
    static int getMaxFitness() {
        int maxFitness = solution.length;
        return maxFitness;
    }*/
}