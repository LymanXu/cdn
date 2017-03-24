package com.ga;

import java.util.Random;

/**
 * Date: 2017/3/24 17:54
 * Abstract:Rosenbrock个体实现
 */
class RosenbrockIndividual extends Individual {

    private int[] serverNodes;
    int chromlen;
    int serverCost;
    // 基因为所有网络节点的0，1组合，@chromlen:为网络节点的个数
    RosenbrockIndividual(int chromlen, int serverCost){
        //genelen = 10;
        genelen = chromlen;
        chrom = new Chromosome(chromlen);

        this.serverNodes = new int[chromlen];
        this.chromlen = chromlen;
        this.serverCost = serverCost;
    }

    //编码
    public void coding(){
       /*
        String code1,code2;
        code1 = codingVariable(x1);
        code2 = codingVariable(x2);

        chrom.setGene(0 , 9 , code1);
        chrom.setGene(10, 19 , code2);*/
        StringBuffer codeBuf = new StringBuffer();

        for(int i = 0; i < serverNodes.length; i++){
           codeBuf.append(serverNodes[i]);
        }
        chrom.setGene(0, chromlen, codeBuf.toString());

    }

    //解码
    public void decode(){
       /*
        String gene1,gene2;

        gene1 = chrom.getGene(0 , 9);
        gene2 = chrom.getGene(10 , 19);

        x1 = decodeGene(gene1);
        x2 = decodeGene(gene2);*/

        String gene = chrom.getGene(0, chromlen);

        for(int i = 0; i < gene.length(); i++){
            serverNodes[i] = Integer.parseInt(String.valueOf(gene.charAt(i)));
        }
    }

    //计算目标函数值
    public  void calTargetValue(){
        decode();
        targetValue = rosenbrock(serverNodes, serverCost);
    }

    //计算个体适应度
    public void calFitness(){
        fitness = getTargetValue();
    }

    /*
    private String codingVariable(double x){
        double y = (((x + 2.048) * 1023) / 4.096);
        String code = Integer.toBinaryString((int) y);

        StringBuffer codeBuf = new StringBuffer(code);
        for(int i = code.length(); i<genelen; i++)
            codeBuf.insert(0,'0');

        return codeBuf.toString();
    }

    private double decodeGene(String gene){
        int value ;
        double decode;
        value = Integer.parseInt(gene, 2);
        decode = value/1023.0*4.096 - 2.048;

        return decode;
    }*/

    public String toString(){
        String str = "";
        ///str = "基因型:" + chrom + "  ";
        ///str+= "表现型:" + "[x1,x2]=" + "[" + x1 + "," + x2 + "]" + "\t";
        str+="函数值:" + rosenbrock(serverNodes , serverCost) + "\n";

        return     str;
    }

    /**
     *@需要改写的适应度函数
     */
    public static double rosenbrock(int[] serverNodes, int serverCost){
        double fun = 0.0;

        // fun = 100*Math.pow((x1*x1 - x2) , 2) + Math.pow((1 - x1) , 2);

        return fun;
    }

    //随机产生个体
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

        //同步编码和适应度
        coding();
        calTargetValue();
        calFitness();
    }
}