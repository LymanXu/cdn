package com.cacheserverdeploy.deploy;

public class Individual {

    static int defaultGeneLength = 64;
    //基因序列
    private byte[] genes = null;
    // 个体的 适应值
    private double fitness = 0;

    public Individual(int geneLength) {
        genes = new byte[geneLength];
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

        return resultForGA.getRight();
    }


    /* Getters and setters */
    // Use this if you want to create individuals with different gene lengths
    public static void setDefaultGeneLength(int length) {
        defaultGeneLength = length;
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
        this.genes = first;
    }

    /* Public methods */
    public int size() {
        return genes.length;
    }

    public double getFitness() {
        if (fitness == 0) {
            fitness = FitnessCalc.getFitness(this);
        }
        return fitness;
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

    @Override
    public String toString() {
        String geneString = "";
        for (int i = 0; i < size(); i++) {
            geneString += getGene(i);
        }
        return geneString;
    }
}