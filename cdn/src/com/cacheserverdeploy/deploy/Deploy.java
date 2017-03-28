package com.cacheserverdeploy.deploy;


import java.util.*;

public class Deploy
{
    /**
     * 你需要完成的入口
     * <功能详细描述>
     * @param graphContent 用例信息文件
     * @return [参数说明] 输出结果信息
     * @see [类、类#方法、类#成员]
     */
    public static int st;
    public static int ed;
    public static int allNeed;//总的流量需求
    public static Map<Integer,Integer> node_consumer=new HashMap<Integer,Integer>();

    public static String[] deployServer(String[] graphContent)
    {
        /**do your work here**/
        int[] servers = {7, 13, 22, 37, 38, 43};
//        servers.add(48);

        MyMinCost.Graph graph= init(servers,graphContent);//初始化
        MyMinCost myMinCost=new MyMinCost();
        while (myMinCost.Spfa(graph,st,ed)){//求最短路径
        }
        //初始化总费用
        graph.minCost=graph.serverCost*servers.length;

        String[] result=myMinCost.getRes(graph,st,ed);//返回路径数组
        System.out.println("最小费用："+graph.minCost/2);
        System.out.println("最大流量："+graph.maxFlow+" 需求："+allNeed);

        return new String[]{"17","\r\n","0 8 0 20"};
    }

    /*
     * 使用GA迭代选优
     */
    public static int[] searchServerPositionByGA(String[] graphContent){

        //@ 需要修改
        int[] serverPosition = new int[1];


        return serverPosition;
    }

    public static MyMinCost.Graph init(int[] servers, String[] graphContent){
        String[] temp=graphContent[0].split(" ");
        allNeed=0;

        int nodeNum=Integer.parseInt(temp[0]);
        int linkNum=Integer.parseInt(temp[1]);
        int consumerNum=Integer.parseInt(temp[2]);

        int serverCost=Integer.parseInt(graphContent[2])*2;

        st=nodeNum;
        ed=nodeNum+1;

//        int degeNum=linkNum+linkNum*2+servers.size()+consumerNum;
        int totalNum=nodeNum+2+linkNum;//总节点数

        MyMinCost.Graph graph=new MyMinCost.Graph(totalNum,0,consumerNum,serverCost);
        //导入边
        int i=4;
        int index=ed+1;
        while(!(graphContent[i].equals(""))){
            temp=graphContent[i].split(" ");
            //添加原始边
            graph.addEdge(Integer.parseInt(temp[0]) , Integer.parseInt(temp[1]),Integer.parseInt(temp[2]), Integer.parseInt(temp[3])*2);
            //添加反平行边
            int nodeId=index++;//增加id编号
            graph.addEdge(Integer.parseInt(temp[1]) , nodeId,Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
            graph.addEdge(nodeId , Integer.parseInt(temp[0]),Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
            i++;
        }
        i++;
        //增加从消费节点所在的网络节点到汇点的边
        while (i<graphContent.length){
            temp=graphContent[i].split(" ");
            graph.addEdge(Integer.parseInt(temp[1]) , ed,Integer.parseInt(temp[2]), 0);
            allNeed+=Integer.parseInt(temp[2]);
            node_consumer.put(Integer.parseInt(temp[1]),Integer.parseInt(temp[0]));
            i++;
        }

        //增加从源点到每一个网络节点的边
        for(i =0; i < servers.length; i++){
            graph.addEdge(st, servers[i], Integer.MAX_VALUE, 0);
        }

        return graph;
    }
}
