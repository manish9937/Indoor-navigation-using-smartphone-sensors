package com.example.rishabh.nevdijsktra.dijkstra;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class node {

    private String name;
    private float xx,yy;
    private List<node> shortestPath = new LinkedList<>();

    private Integer distance = Integer.MAX_VALUE;

    Map<node, Integer> adjacentNodes = new HashMap<>();

    public void addDestination(node destination, int distance) {
        adjacentNodes.put(destination, distance);
    }

    public node(String name,float xx,float yy) {
        this.name = name;
        this.xx=xx;
        this.yy=yy;
    }
   // public  void setCoordinate(float xx,float yy){
     //   this.xx=xx;
     //   this.yy=yy;
  //  }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Map<node,Integer> getAdjacentNodes() {
        return adjacentNodes;
    }

    public Integer getDistance() {
        return distance;
    }

    public List<node> getShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(LinkedList<node> shortestPath) {
        this.shortestPath = shortestPath;
    }
    public String getName() {
        return name;
    }

    public float getXcoordinate(){
        return xx;
    }
    public float getYcoordinate(){
        return yy;
    }

    // getters and setters
}
