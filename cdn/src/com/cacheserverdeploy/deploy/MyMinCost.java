package com.cacheserverdeploy.deploy;

import java.util.*;

/**
 * Author: Wucheng
 * Date: 2017/3/20 13:15
 * Abstract:
 */
public class MyMinCost {
    /*
     * 最小费用最大流，使用spfa
     */

    public static boolean[] visited;//是否访问过

    public static int cost;
    //图的最小费用
    public static int mCost;
    public static int maxFlow;

    public static int st;//起始点编号
    public static int ed;//终止点编号
    public static Graph graph;

    public static Deque<Integer> q;
    public static int[] dist;


    static class Edge {
        /*
         * to: 这条边要到的顶点
         * flow: 这条边上的流量
         * cost: 这条边的单位租价
         * next: 目前没有用到
         * capacity: 这条边的容量
         */
        int to, cost,capatity;
        int realCap,realCost;
        Edge pair;

        public Edge(int to, int capatity, int cost,int realCap,int realCost) {
            this.to = to;
            this.capatity = capatity;
            this.cost = cost;
            this.realCap=realCap;
            this.realCost=realCost;
        }
    }

    static class Graph {
        int totalNodeNum, totalSourceNodeNum, totalConsumerNodeNum, serverCost, gEdgeCount;
        List<Edge>[] edgeArrList = null;
//        int[] gHead;

        // the max flow of this graph

        public Graph(int totalNodeNum, int totalSourceNodeNum, int totalConsumerNodeNum, int serverCost) {
            this.totalNodeNum = totalNodeNum;
            this.totalSourceNodeNum = totalSourceNodeNum;
            this.totalConsumerNodeNum = totalConsumerNodeNum;
            this.serverCost = serverCost;

            this.edgeArrList = new List[totalNodeNum];
            for (int i = 0; i < totalNodeNum; i++) {
                edgeArrList[i] = new ArrayList<Edge>();
            }
//            this.gHead = new int[totalNodeNum];
        }

        public void addEdge(int from, int to, int capatity, int cost) {

            Edge edge=new Edge(to, capatity, cost,capatity,cost);
            Edge pair=new Edge(from, 0, -cost,0,-cost);
            edge.pair=pair;
            pair.pair=edge;
            edgeArrList[from].add(edge);
            // 增加反向边
            edgeArrList[to].add(pair);
        }
    }

    public static int augment(int u, int flow) {
        if (u == ed) {
            maxFlow+=flow;
            mCost += cost * flow;
        } else {
            visited[u] = true;
            int left_flow = flow;
            for (Edge edge :graph.edgeArrList[u]) {
                if(edge.capatity != 0 &&edge.cost == 0 && !visited[edge.to]){
                    int delta = augment(edge.to, Math.min(left_flow , edge.capatity));
                    edge.capatity -= delta;
                    edge.pair.capatity += delta;
                    left_flow  -= delta;
                    if(left_flow==0)return flow;
                }
            }
            flow -= left_flow;
        }
        return flow;
    }
    public static boolean relabel() {
        int d=Integer.MAX_VALUE;
        for(int i=0;i<graph.totalNodeNum;i++){
            if(visited[i]){
                for(Edge edge:graph.edgeArrList[i]){
                    if(edge.capatity!=0&&!visited[edge.to]&&edge.cost<d){
                        d=edge.cost;
                    }
                }
            }

        }
        if(d==Integer.MAX_VALUE){
            return false;
        }

        for(int i=0;i<graph.totalNodeNum;i++){
            if(visited[i]) {
                for (Edge edge : graph.edgeArrList[i]) {
                    edge.cost -= d;
                    edge.pair.cost+=d;
                }
            }
        }
        cost+=d;
        return true;
    }
    public static void zkw_costflow(Graph graph1,int st1,int ed1) {
        mCost = 0;cost = 0;maxFlow=0;
        st =st1;ed=ed1;graph=graph1;
        dist=new int[graph.totalNodeNum];
        visited=new boolean[graph.totalNodeNum];
         do{
            do {
                Arrays.fill(visited,false);
            } while (augment(st, Integer.MAX_VALUE) != 0);
        }while (relabel());

    }


    /**
     * 输出路径信息
     */
    LinkedList<String> pathList = new LinkedList<String>();

    //    List<Integer> pathCostList=new ArrayList<Integer>();
    public String[] getRes(Graph graph, int start, int end) {
        List<Edge> list = null;

        for (Edge edge : graph.edgeArrList[start]) {
            while (edge.pair.capatity!=0) {//有流量下走，总能走到end
                StringBuilder sb = new StringBuilder();

                int minFlow = edge.pair.capatity;
//                int tempCost=0;//记录总的单位费用

                list = new ArrayList<Edge>();
                list.add(edge);

                sb.append(edge.to + " ");
                Edge tpEdge = edge;
                while (tpEdge.to != end) {
                    int to = tpEdge.to;
                    int i = 0;
                    tpEdge = graph.edgeArrList[to].get(i);
                    //如果真实容量=0，说明是反向边，不走
                    while (tpEdge.realCap==0||tpEdge.pair.capatity == 0 ) {
                        i++;
                        tpEdge = graph.edgeArrList[to].get(i);
                    }
                    list.add(tpEdge);
                    if (tpEdge.to < start) {//添加边
                        sb.append(tpEdge.to + " ");
                    }
                    minFlow = Math.min(minFlow, tpEdge.pair.capatity);
//                    tempCost+=tpEdge.cost;
                }
//                //计算这条路径费用并加入总费用
//                graph.minCost+=tempCost*minFlow;
                //回复容量
                for (Edge edgeTemp : list) {
                    edgeTemp.pair.capatity -= minFlow;
                }
                sb.append(Deploy.node_consumer.get(tpEdge.pair.to) + " " + minFlow);//加入消费节点并连接流量
                pathList.add(sb.toString());
            }
        }
        int pathNum = pathList.size();
        pathList.addFirst("");
        pathList.addFirst(pathNum + "");
        return pathList.toArray(new String[pathList.size()]);
    }

}
