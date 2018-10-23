package com.example.manish.nevdijsktra;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.widget.Toast;

import com.example.rishabh.nevdijsktra.ToolBox.Location;
import com.example.rishabh.nevdijsktra.dijkstra.graph;
import com.example.rishabh.nevdijsktra.dijkstra.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class inventory {
    public static float X = 340;
    public static float Y = 860;
    static int maxHeight = 1000;
    static int maxWidth = 700;
    private static String msg = "Android : ";

    public enum Mode {
        scanning, navigate
    }

    public static Mode mode = Mode.scanning;

    public static List<PointF> drawnpoints = new ArrayList<PointF>();
    public static HashMap<String, Location> locationPoints = new HashMap<String, Location>();

    public static double lowpassFilter(double old_value, double new_value,
                                       double a) {
        return old_value + a * (new_value - old_value);
    }

    public static void toast(String msg, Context context, boolean longer) {
        if (longer)
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static Bitmap getMap(Context context){
        Bitmap map = BitmapFactory.decodeResource(context.getResources(),R.drawable.mp);

        Bitmap mapscale = Bitmap.createScaledBitmap(map,maxWidth,maxHeight,true);
        return mapscale;
    }
    public static Bitmap getArrow(Context context){
        Bitmap map = BitmapFactory.decodeResource(context.getResources(),R.drawable.arrow);

        return map;
    }
    public static Paint redPaint() {
        Paint p = new Paint();
        p.setColor(Color.RED);
        return p;
    }
    public static Paint bluePaint() {
        Paint p = new Paint();
        p.setColor(Color.BLUE);
        p.setStrokeWidth(20);
        // paintAlpha=Math.round((float)newAlpha/100*255);
        p.setAlpha(128);
        return p;
    }
    public static Paint cyanPaint() {
        Paint p = new Paint();
        p.setColor(Color.CYAN);
        return p;
    }

    public static Paint text50() {
        Paint p = new Paint();
        p.setTextSize(50.0f);
        return p;
    }

    public static Paint text50MAG() {
        Paint p = new Paint();
        p.setTextSize(50.0f);
        p.setColor(Color.MAGENTA);
        return p;
    }

    public static Paint text50BLUE() {
        Paint p = new Paint();
        p.setTextSize(50.0f);
        p.setColor(Color.BLUE);
        return p;
    }

    public static AlertDialog.Builder START_DIALOG(Context context) {
        return null;

    }

    public static int inc_id = 0;

    public static int createID() {

        return inc_id++;

    }

    public static int returnID() {
        return inc_id;
    }

    public static graph calculateShortestPathFromSource(graph graph, node source) {
        source.setDistance(0);

        //for(node N:graph.nodes){
          //  Log.d(msg,String.valueOf(N.getName())+" "+String.valueOf(N.getDistance()));
        //}

        Set<node> settledNodes = new HashSet<>();
        Set<node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            node currentNode = getLowestDistanceNode(unsettledNodes);
            //Log.d(msg,String.valueOf(currentNode.getName())+" "+String.valueOf(currentNode.getDistance())+"in while getLowestdistance ");

            unsettledNodes.remove(currentNode);

            for (Map.Entry< node, Integer> adjacencyPair:
                    currentNode.getAdjacentNodes().entrySet()) {
                //Log.d(msg,String.valueOf(adjacencyPair.getKey())+" "+String.valueOf(adjacencyPair.getValue())+"in while getLowestdistance ");
                node adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        return graph;
    }
    private static node getLowestDistanceNode(Set < node > unsettledNodes) {
        node lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (node node: unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }
    private static void calculateMinimumDistance(node evaluationNode,
                                                 Integer edgeWeigh, node sourceNode) {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }
    
}
