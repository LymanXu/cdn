package com.cacheserverdeploy.deploy;

import java.util.Arrays;

public class Individual {

    static int geneLength = 0;
    //基因序列
    private byte[] genes = null;
    // 个体的 适应值
    private double fitness = 0;

    private int consumerNum;
    private int consumerCost;

    public Individual(int geneLength) {
        this.geneLength =geneLength;

        genes = new byte[geneLength];
        fitness = 0;
    }

    // 创建一个随机的 基因个体
    public void generateIndividual() {

        for (int i = 0; i < size(); i++) {
            byte gene = (byte) Math.round(Math.random());
            genes[i] = gene;
        }

        /*判断生成的解是否满足约束
        while(!normalChrom()){
            // 当随机生成的解不满足最大流时，再次随机生成个体
            for (int i = 0; i < size(); i++) {
                byte gene = (byte) Math.round(Math.random());
                genes[i] = gene;
            }
        }*/

    }

    // 创建一个随机的 基因个体
    public void generateIndividual(int consumerNum) {

        Boolean[] visited = randPosition(consumerNum);


        for (int i = 0; i < size(); i++) {
            if(visited[i]){
                genes[i] = 1;
            }
        }

        //判断生成的解是否满足约束
        while(!normalChrom()){
            // 当随机生成的解不满足最大流时，再次随机生成个体
            visited = randPosition(consumerNum);

            for (int i = 0; i < size(); i++) {
                if(visited[i]){
                    genes[i] = 1;
                }
            }
        }

    }

    public Boolean[] randPosition(int consumer){

        Boolean[] visit = new Boolean[geneLength];
        Arrays.fill(visit, false);

        int num = rand(1,consumer);

        for(int i = 0; i < num; i++){
            int tempP = rand(0, geneLength);

            while(visit[tempP]){
                tempP = rand(0, geneLength);
            }
            visit[tempP] = true;
        }

        return visit;
    }


    // 通过和solution比较 ，计算个体的适应值
    public double calFitness() {
        double fitness = 0;

        // 将byte[]转换为int[]
        int[] servers = new int[size()];

        for(int i = 0; i < size(); i++){
            if(getGene()[i] == 1){
                servers[i] = 1;
            }else{
                servers[i] = 0;
            }
        }

        // 调用mincost maxflow的算法
        ResultForGA resultForGA = Deploy.getMinCost(servers);


        double cost = resultForGA.getCost();

        if(resultForGA.getRight()){
            fitness = Deploy.MAX_COST - resultForGA.getCost();
        }else{
            fitness = Deploy.MAX_COST - resultForGA.getCost() - resultForGA.getCurrentNeedCount()*consumerCost;
        }

        return fitness;
    }

    /*
     * 与前面交互的函数
     *  @param：int[] servers
     *  @return: ResultForGA{Boolean right, double cost}
     */
    public ResultForGA getResult(){

        // 将byte[]转换为int[]
        int[] servers = new int[size()];

        for(int i = 0; i < size(); i++){
            if(genes[i] == 1){
                servers[i] = 1;
            }else{
                servers[i] = 0;
            }
        }

        // 调用mincost maxflow的算法
        ResultForGA resultForGA = Deploy.getMinCost(servers);
        return resultForGA;
    }

    public Boolean normalChrom(){
        ResultForGA resultForGA = getResult();

        // 更新个体适应度
        if(resultForGA.getRight()){
            fitness = Deploy.MAX_COST - resultForGA.getCost();
        }

        return resultForGA.getRight();
    }


    public double getFitness() {
        if (fitness == 0) {
            fitness = calFitness();
        }
        return fitness;
    }

    public void setFitness(double fitness){
        this.fitness = fitness;
    }
    /*
     * 将byte[] 转化为int[]表示服务器的位置
     */
    public int[] getServers(){
        // 将byte[]转换为int[]
        int[] servers = new int[size()];

        for(int i = 0; i < size(); i++){
            if(genes[i] == 1){
                servers[i] = 1;
            }else{
                servers[i] = 0;
            }
        }

        return servers;
    }

    public void setServers(int[] firstGene){
        for(int i = 0; i < size(); i++){
            if(firstGene[i] == 1){
                genes[i] = 1;
            }else{
                genes[i] = 0;
            }
        }
    }

    public byte getGene(int index) {
        return genes[index];
    }

    public byte[] getGene(){
        return genes;
    }

    public void setGene(int index, byte value) {
        genes[index] = value;
        fitness = 0;
    }

    public void setGene(byte[] first){

        for(int i = 0; i < first.length; i++){
            genes[i] = first[i];
        }
    }

    /* Public methods */
    public int size() {
        return genes.length;
    }

    @Override
    public String toString() {
        String geneString = "";
        for (int i = 0; i < size(); i++) {
            geneString += getGene(i);
        }
        return geneString;
    }

    private double rand() {
        return Math.random();
    }

    // 产生[start, end)的随机数
    private int rand(int start, int end) {
        return (int) (rand() * (end - start) + start);
    }

}