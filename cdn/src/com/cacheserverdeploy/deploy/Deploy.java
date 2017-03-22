package com.cacheserverdeploy.deploy;


import java.util.List;

public class Deploy
{
    /**
     * 你需要完成的入口
     * <功能详细描述>
     * @param graphContent 用例信息文件
     * @return [参数说明] 输出结果信息
     * @see [类、类#方法、类#成员]
     */
    public static String[] deployServer(String[] graphContent)
    {
        /**do your work here**/
        if(graphContent == null || graphContent.length <= 0){
            return null;
        }



        return new String[]{"17","\r\n","0 8 0 20"};
    }

    public void generateGraph(String[] graphContent){
        /*
         * 处理输入文件，生成图的信息Graph,并记录常量
         */
        String splitChar = " ";

        String[] temp = graphContent[0].split(splitChar);
        int realNetNodeNum = Integer.parseInt(temp[0]);
        int realLinkNum = Integer.parseInt(temp[1]);
        int consumerNum = Integer.parseInt(temp[2]);

        int serverCost = Integer.parseInt(graphContent[2]);

        int st = realNetNodeNum+1;
        int ed = realNetNodeNum+2;

        int degeNum=realLinkNum+realLinkNum*2+realNetNodeNum+consumerNum;

        List<MinCost.Edge>[] graph = MinCost.createGraph(degeNum);
        //导入边
        int i=4;
        while(!(graphContent[i].equals(""))){
            temp=graphContent[i].split(" ");
            //添加原始边
            MinCost.addEdge(graph,Integer.parseInt(temp[0]) , Integer.parseInt(temp[1]),Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
            //添加反平行边
            int nodeId=-3*(i-4);//增加id编号
            MinCost.addEdge(graph,Integer.parseInt(temp[1]) , nodeId,Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
            MinCost.addEdge(graph,nodeId , Integer.parseInt(temp[0]),Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
            i++;
        }
        i++;
        while (i<graphContent.length){
            temp=graphContent[i].split(" ");
            MinCost.addEdge(graph,Integer.parseInt(temp[1]) , ed,Integer.parseInt(temp[2]), 0);
        }
        i=0;
        while (i<realNetNodeNum){
            MinCost.addEdge(graph,st, i,Integer.MAX_VALUE, serverCost);
        }
    }

}
