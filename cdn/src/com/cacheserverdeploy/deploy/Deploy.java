package com.cacheserverdeploy.deploy;


import java.util.*;

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

    public static byte[] first;


    public static String[] deployServer(String[] graphContent) {
        /**do your work here**/
        //初始化路径
        init(graphContent);//初始化
        //调用ga

        //ga计算服务器位置
        int[] serverPosition= MainTest.myGA(nodeNum, first);//最优的服务器位置

//        //模拟GA服务器位置选定
//        int[] serverPosition=setMyServers();

        ResultForGA rf=getMinCost(serverPosition);
//        Test.test(graph);
        //返回路径数组
        String[] result = myMinCost.getRes(graph, st, ed);
//        System.out.println("最小费用：" + rf.getCost());
//        System.out.println("最大流量：" + myMinCost.maxFlow + " 需求：" + allNeed);
        return result;
    }

    public static void init(String[] graphContent) {
        String[] temp = graphContent[0].split(" ");
        allNeed = 0;

        nodeNum = Integer.parseInt(temp[0]);
        int linkNum = Integer.parseInt(temp[1]);
        int consumerNum = Integer.parseInt(temp[2]);

        int serverCost = Integer.parseInt(graphContent[2]);

        st = nodeNum;
        ed = nodeNum + 1;

//        int degeNum=linkNum+linkNum*2+servers.size()+consumerNum;
        int totalNum = nodeNum + 2;//总节点数

        graph = new MyMinCost.Graph(totalNum, 0, consumerNum, serverCost);
        //导入边
        int i = 4;
//        int index = T + 1;
        while (!(graphContent[i].equals(""))) {
            temp = graphContent[i].split(" ");
            //添加原始边
            graph.addEdge(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
            //添加反平行边
            graph.addEdge(Integer.parseInt(temp[1]), Integer.parseInt(temp[0]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
//            int nodeId = index++;//增加id编号
//            graph.addEdge(Integer.parseInt(temp[1]), nodeId, Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
//            graph.addEdge(nodeId, Integer.parseInt(temp[0]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
            i++;
        }
        i++;
        //增加从消费节点所在的网络节点到汇点的边
        first=new byte[nodeNum];
        Arrays.fill(first,(byte)0);
        while (i < graphContent.length) {
            temp = graphContent[i].split(" ");
            graph.addEdge(Integer.parseInt(temp[1]), ed, Integer.parseInt(temp[2]), 0);
            allNeed += Integer.parseInt(temp[2]);
            node_consumer.put(Integer.parseInt(temp[1]), Integer.parseInt(temp[0]));
            first[Integer.parseInt(temp[1])]=1;//将消费节点所连接的网络节点作为初始服务器位置
            i++;
        }
        //增加从源点到每一个网络节点的边，初始化容量0
        i = 0;
        while (i < nodeNum) {
            graph.addEdge(st, i,0, 0);
            i++;
        }
    }
    //清洗边，将流量置0，容量初始化
    public static void clear(){
        for(List<MyMinCost.Edge> edgeList:graph.edgeArrList){
            for(MyMinCost.Edge edge:edgeList){
                edge.capatity=edge.realCap;
                edge.cost=edge.realCost;
//                edge.capatity=edge.real;
            }
        }
    }
    //设置服务器位置,返回服务器数量
    public static int setServers(int[] serverNodes){
        int serverNum=0;
        for (MyMinCost.Edge edge:graph.edgeArrList[st]){
            if(serverNodes[edge.to]==1){//该点是服务器
                edge.capatity=Integer.MAX_VALUE;//容量置无穷大
                edge.realCap=Integer.MAX_VALUE;//容量置无穷大
                serverNum++;
            }else {
                edge.capatity=0;//容量置0
                edge.realCap=0;//容量置0
            }
        }
        return serverNum;
    }
    //获取结果：最大流和最小费用
    static boolean init=false;//记录是否第一次调用，第一次是不需要清洗的
    public static ResultForGA getMinCost(int[] serverNodes){//0：最大流，1：最小费用
        ResultForGA rf=new ResultForGA();

        if(init) {//如果初始化过
            clear();//初始化边的流量，容量，费用
        }else {
            init=true;
        }
        int serverNum=setServers(serverNodes);//设置服务器位置

        //求最短路径
//        long start=System.currentTimeMillis();
        myMinCost.zkw_costflow(graph, st, ed);
//        System.out.println(System.currentTimeMillis()-start);
        //获取最大流量
        rf.setRight(allNeed==myMinCost.maxFlow?true:false);
        //得到最小费用
        int cost=myMinCost.mCost;
        cost+=serverNum*graph.serverCost;
        rf.setCost(cost);
        return rf;
    }
    public static int[] setMyServers(){
        int[] temp={7,14,17,19,25,26,29,32,35,43,44,59,70,74,82,84,89,93,95,101,111,120,124,126,129,133,137,138,141,147,164,166,167,170,175,178,184,186,187,194,195,198,200,203,205,218,223,227,234,238,242,252,254,259,263,267,268,270,271,275,277,278,281,287,288,297,301,308,320,321,326,328,331,333,334,335,336,338,346,349,363,370,375,381,383,385,387,397,400,402,409,413,415,416,423,426,438,459,462,463,464,474,475,482,488,489,496,497,500,503,505,506,507,520,525,529,537,538,541,542,548,552,557,565,570,575,576,580,584,587,592,594,625,632,634,638,641,643,644,646,651,652,660,663,668,670,671,675,677,679,685,687,695,711,718,719,724,731,739,741,742,745,751,753,755,756,760,765,767,770,774,778,784,791,795,797};
//        int[] temp={7,13,15,22,37,38,43};
        int[] servers=new int[nodeNum];
        Arrays.fill(servers,0);
        for(int i=0;i<temp.length;i++){
            servers[temp[i]]=1;
        }
        return  servers;
    }

}
