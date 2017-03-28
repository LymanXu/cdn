package com.ga;

import com.cacheserverdeploy.deploy.Deploy;
import com.cacheserverdeploy.deploy.MyMinCost;

import java.util.Random;

/**
 * Date: 2017/3/24 17:54
 * Abstract:Rosenbrock个体实现
 */
class RosenbrockIndividual extends Individual {

    private int[] serverNodes;
    int chromlen;

    // 基因为所有网络节点的0，1组合，@chromlen:为网络节点的个数
    RosenbrockIndividual(int chromlen){
        genelen = chromlen;
        chrom = new Chromosome(chromlen);

        this.serverNodes = new int[chromlen];
        this.chromlen = chromlen;
    }

    //编码
    public void coding(){

        StringBuffer codeBuf = new StringBuffer();

        for(int i = 0; i < serverNodes.length; i++){
           codeBuf.append(serverNodes[i]);
        }
        chrom.setGene(0, chromlen, codeBuf.toString());

    }

    //解码
    public void decode(){

        String gene = chrom.getGene(0, chromlen);

        for(int i = 0; i < gene.length(); i++){
            serverNodes[i] = Integer.parseInt(String.valueOf(gene.charAt(i)));
        }
    }

    //计算目标函数值
    public  void calTargetValue(){
        decode();
        targetValue = rosenbrock();
    }

    //计算个体适应度
    public void calFitness(){

        fitness = getTargetValue();
    }

    public String toString(){
        String str = "";
        ///str = "基因型:" + chrom + "  ";
        ///str+= "表现型:" + "[x1,x2]=" + "[" + x1 + "," + x2 + "]" + "\t";
        str+="函数值:" + rosenbrock() + "\n";

        return     str;
    }

    /**
     *@需要改写的适应度函数
     */
    public  double rosenbrock(){
        ResultForGA resultForGA = getResult();

        return 0 - resultForGA.getScore();
    }

    /*
     * 与前面交互的函数
     *  @param：int[] servers
     *  @return: ResultForGA{Boolean right, double cost}
     */
    public ResultForGA getResult(){
        // 调用mincost maxflow的算法

        ResultForGA resultForGA = new ResultForGA();

        return resultForGA;
    }

    public Boolean normalChrom(){
        ResultForGA resultForGA = getResult();

        return resultForGA.getRight();
    }

    //随机产生个体，当个体的最大流不满足时抛掉
    public void generateIndividual(){
        // 在int[] serverNodes中随机产生0,1
        Random random = new Random();

        for(int i = 0; i < chromlen; i++){
            if(random.nextDouble() < 0.5){
                serverNodes[i] = 0;
            }else{
                serverNodes[i] = 1;
            }
        }

        while(normalChrom()){
            // 当随机生成的解不满足最大流时，再次随机生成个体
            for(int i = 0; i < chromlen; i++){
                if(random.nextDouble() < 0.5){
                    serverNodes[i] = 0;
                }else{
                    serverNodes[i] = 1;
                }
            }
        }

        //同步编码和计算目标函数值
        coding();
        calTargetValue();
        // 将个体适应度更新为目标函数值
        calFitness();
    }

    public int[] getServerNodes() {
        return serverNodes;
    }

    public void setServerNodes(int[] serverNodes) {
        this.serverNodes = serverNodes;
    }
}