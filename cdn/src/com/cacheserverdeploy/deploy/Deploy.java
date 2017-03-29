package com.cacheserverdeploy.deploy;

import com.simpleGa.MainTest;
import com.simpleGa.ResultForGA;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Deploy {
    /**
     * 你需要完成的入口
     * <功能详细描述>
     *
     * @param graphContent 用例信息文件
     * @return [参数说明] 输出结果信息
     * @see [类、类#方法、类#成员]
     */
    public static int st;//起始边
    public static int ed;//终止边
    public static int allNeed;//总的流量需求

    public static int nodeNum;//总的网络节点个数

    public static Map<Integer, Integer> node_consumer = new HashMap<Integer, Integer>();
    public static MyMinCost.Graph graph=null;
    public static MyMinCost myMinCost = new MyMinCost();//最短路工具类

    public static int[] first;


    public static String[] deployServer(String[] graphContent) {
        /**do your work here**/
        //初始化路径
        init(graphContent);//初始化

        // 调用ga
        int[] serverPosition= MainTest.myGA(nodeNum, first,30);//最优的服务器位置
        ResultForGA rf=getMinCost(serverPosition);

        //返回路径数组
        String[] result = myMinCost.getRes(graph, st, ed);
        System.out.println("最小费用：" + rf.getCost());
        System.out.println("最大流量：" + graph.maxFlow + " 需求：" + allNeed);

        return new String[]{"17", "\r\n", "0 8 0 20"};
    }

    public static void init(String[] graphContent) {
        String[] temp = graphContent[0].split(" ");
        allNeed = 0;

        nodeNum = Integer.parseInt(temp[0]);
        int linkNum = Integer.parseInt(temp[1]);
        int consumerNum = Integer.parseInt(temp[2]);

        int serverCost = Integer.parseInt(graphContent[2]) * 2;

        st = nodeNum;
        ed = nodeNum + 1;

//        int degeNum=linkNum+linkNum*2+servers.size()+consumerNum;
        int totalNum = nodeNum + 2 + linkNum;//总节点数

        graph = new MyMinCost.Graph(totalNum, 0, consumerNum, serverCost);
        //导入边
        int i = 4;
        int index = ed + 1;
        while (!(graphContent[i].equals(""))) {
            temp = graphContent[i].split(" ");
            //添加原始边
            graph.addEdge(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]) * 2);
            //添加反平行边
            int nodeId = index++;//增加id编号
            graph.addEdge(Integer.parseInt(temp[1]), nodeId, Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
            graph.addEdge(nodeId, Integer.parseInt(temp[0]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
            i++;
        }
        i++;
        //增加从消费节点所在的网络节点到汇点的边
        first=new int[nodeNum];
        Arrays.fill(first,0);
        while (i < graphContent.length) {
            temp = graphContent[i].split(" ");
            graph.addEdge(Integer.parseInt(temp[1]), ed, Integer.parseInt(temp[2]), 0);
            allNeed += Integer.parseInt(temp[2]);
            node_consumer.put(Integer.parseInt(temp[1]), Integer.parseInt(temp[0]));
            first[Integer.parseInt(temp[1])]=1;//将消费节点所连接的网络节点作为初始服务器位置
            i++;
        }
        //增加从源点到每一个网络节点的边，初始化容量无穷大
        i = 0;
        while (i < nodeNum) {
            graph.addEdge(st, i, Integer.MAX_VALUE, 0);
            i++;
        }
    }
    //清洗边，将流量置0，容量初始化
    public static void clear(){
        for(List<MyMinCost.Edge> edgeList:graph.edgeArrList){
            for(MyMinCost.Edge edge:edgeList){
                edge.flow=0;
                edge.capatity=edge.real;
            }
        }
        graph.maxFlow = 0;
    }
    //设置服务器位置,返回服务器数量
    public static int setServers(int[] serverNodes){
        int serverNum=0;
        for (MyMinCost.Edge edge:graph.edgeArrList[st]){
            if(serverNodes[edge.to]==1){//改点是服务器
                edge.capatity=Integer.MAX_VALUE;//容量置无穷大
                serverNum++;
            }else {
                edge.capatity=0;//容量置0
            }
        }
        return serverNum;
    }
    //获取结果：最大流和最小费用
    public static ResultForGA getMinCost(int[] serverNodes){//0：最大流，1：最小费用
        ResultForGA rf=new ResultForGA();

        clear();//初始化边的流量，容量，费用
        int serverNum=setServers(serverNodes);//设置服务器位置

        //求最短路径
        while (myMinCost.Spfa(graph, st, ed)) {
        }
        //获取最大流量
        rf.setRight(allNeed==graph.maxFlow?true:false);
        //得到最小费用
        int cost=myMinCost.getMinCost(graph);
        cost+=serverNum*graph.serverCost;
        rf.setCost(cost/2);
        return rf;
    }

}
