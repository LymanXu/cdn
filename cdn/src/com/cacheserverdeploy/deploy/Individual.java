package com.cacheserverdeploy.deploy;

public class Individual {

    static int defaultGeneLength = 64;
    //基因序列
    private byte[] genes = null;
    // 个体的 适应值
    private double fitness = 0;

    public Individual(int geneLength) {
        genes = new byte[geneLength];
        fitness = 0;
    }

    // 创建一个随机的 基因个体
    public void generateIndividual() {

        for (int i = 0; i < size(); i++) {
            byte gene = (byte) Math.round(Math.random());
            genes[i] = gene;
        }

        //判断生成的解是否满足约束
        while(normalChrom()){
            // 当随机生成的解不满足最大流时，再次随机生成个体
            for (int i = 0; i < size(); i++) {
                byte gene = (byte) Math.round(Math.random());
                genes[i] = gene;
            }
        }

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
            fitness = 1.0/resultForGA.getCost();
        }

        return resultForGA.getRight();
    }

    public double getFitness() {
        if (fitness == 0) {
            fitness = FitnessCalc.getFitness(this);
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
        byte[] newGene = new byte[first.length];

        for(int i = 0; i < first.length; i++){
            newGene[i] = first[i];
        }

        this.genes = newGene;
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
}