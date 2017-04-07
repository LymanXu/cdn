package com.cacheserverdeploy.deploy;

/**
 * Author: Wucheng
 * Date: 2017/3/28 21:13
 * Abstract:
 */
public class ResultForGA {

    private Boolean right;
    private double cost;

    // 没有满足的消费点的个数
    private int currentNeedCount;

    public Boolean getRight() {
        return right;
    }

    public void setRight(Boolean right) {
        this.right = right;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getCurrentNeedCount() {
        return currentNeedCount;
    }

    public void setCurrentNeedCount(int currentNeedCount) {
        this.currentNeedCount = currentNeedCount;
    }
}
