package com.cacheserverdeploy.deploy;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//        System.out.println(graphContent.toString());

        List<Integer> servers=new ArrayList<Integer>();
        servers.add(7);
        servers.add(15);
        servers.add(34);
        servers.add(38);
//        servers.add(41);
//        servers.add(48);
        MyMinCost.Graph graph= init(servers,graphContent);
        MyMinCost myMinCost=new MyMinCost();
        int i=0;
        while (myMinCost.Spfa(graph,st,ed)){//求最短路径
//            System.out.println(i++);
        }


        for (List<MyMinCost.Edge> edgeList:graph.edgeArrList) {
            for(MyMinCost.Edge edge:edgeList){
                if(edge.flow<0){
                    System.out.println(edge.flow);
                }
                if(edge.flow+edge.capatity!=edge.real&&edge.cost>0){
                    System.out.println(edge.flow);
                }
            }
        }


        String[] result=myMinCost.getRes(graph,st,ed);
        System.out.println("最小费用："+myMinCost.minCost);
        System.out.println("最大流量："+graph.maxFlow+" 需求："+allNeed);

        return result;
        // return new String[]{"17","\r\n","0 8 0 20"};
    }
    public static MyMinCost.Graph init(List<Integer> servers, String[] graphContent){
        String[] temp=graphContent[0].split(" ");
        allNeed=0;

        int nodeNum=Integer.parseInt(temp[0]);
        int linkNum=Integer.parseInt(temp[1]);
        int consumerNum=Integer.parseInt(temp[2]);

        int serverCost=Integer.parseInt(graphContent[2]);

        st=nodeNum;
        ed=nodeNum+1;

        int degeNum=linkNum+linkNum*2+servers.size()+consumerNum;
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
        i=0;
        int serversNum=servers.size();
        while (i<serversNum){
            graph.addEdge(st, servers.get(i),Integer.MAX_VALUE,0 );
            i++;
        }
        return graph;
    }

}
