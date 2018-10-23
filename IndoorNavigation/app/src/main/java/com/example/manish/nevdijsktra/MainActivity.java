package com.example.manish.nevdijsktra;

import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.rishabh.nevdijsktra.dijkstra.graph;
import com.example.rishabh.nevdijsktra.dijkstra.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    String sourceNode;
    String destinationNode;
    String msg = "Android : ";
    private PointF startPoint, endPoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        Button bfind = (Button) findViewById(R.id.bFind);

        final Capture surfaceV = (Capture) findViewById(R.id.surfaceV);
        inventory.drawnpoints = new ArrayList<PointF>();
        final Spinner spinnerSource = (Spinner)findViewById(R.id.spinnerSource);
        final Spinner spinnerDestination = (Spinner)findViewById(R.id.spinnerDestination);

        final ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSource.setAdapter(myAdapter);
        spinnerDestination.setAdapter(myAdapter);

        spinnerSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sourceNode = spinnerSource.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                sourceNode = myAdapter.getItem(0);
                // TODO Auto-generated method stub
            }
        });
        spinnerDestination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                destinationNode = spinnerDestination.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                sourceNode = myAdapter.getItem(1);
                // TODO Auto-generated method stub
            }
        });

        bfind.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                List<node> sortestPath=new LinkedList<> ();
                inventory.drawnpoints = new ArrayList<PointF>();

                surfaceV.temp = 0;
                Log.d(msg, "The onCreate() event rishabh "+sourceNode+" "+destinationNode);
                sortestPath =  findPath(sourceNode,destinationNode);
                int n = sortestPath.size();
                for(int i=0;i<n;i++){
                    Log.d(msg, "The onCreate() event rishabh "+sortestPath.get(i).getName()+" "+sortestPath.get(i).getXcoordinate()+" "+sortestPath.get(i).getYcoordinate());
                    //Log.d(msg,"The onCreate() event ACharya "+String.valueOf(n));
                    startPoint = new PointF(sortestPath.get(i).getXcoordinate(), sortestPath.get(i).getYcoordinate());
                    inventory.drawnpoints.add(startPoint);


                }


            }
        });

    }

    private List<node> findPath(String s, String d) {


        node nodeA = new node("HOD Room",60,875);
        node nodeB = new node("Entrance",330,875);
        node nodeC = new node("Junction1",320,718);
        node nodeD = new node("Wash room2",320,330);
        node nodeE = new node("Back door",320,183);
        node nodeF = new node("UG LAB gate",55,183);
        node nodeG = new node("Wash Room 1",320,83);
        //nodeA.setCoordinate(100.00,100.00);
        nodeA.addDestination(nodeB, 10);
          nodeB.addDestination(nodeA, 10);
        //nodeA.addDestination(nodeC, 15);
        //nodeC.addDestination(nodeA, 15);

        nodeB.addDestination(nodeC, 5);
        nodeC.addDestination(nodeB, 5);
        //nodeB.addDestination(nodeF, 15);
        //nodeF.addDestination(nodeB, 15);

        nodeC.addDestination(nodeD, 10);
        nodeD.addDestination(nodeC, 10);

        nodeD.addDestination(nodeE, 5);
        nodeE.addDestination(nodeD, 5);


        nodeF.addDestination(nodeE, 10);
        nodeE.addDestination(nodeF, 10);
        nodeE.addDestination(nodeG, 5);
        nodeG.addDestination(nodeE, 5);

        graph graph = new graph();

        graph.addNode(nodeA);
        graph.addNode(nodeB);
        graph.addNode(nodeC);
        graph.addNode(nodeD);
        graph.addNode(nodeE);
        graph.addNode(nodeF);
        graph.addNode(nodeG);


        node sourseN =null ;
        node destN = null ;
        for(node N:graph.nodes){
            if(N.getName().equals(s)){
                sourseN = N;
            }
            else if(N.getName().equals(d))
                destN = N;
        }
        //Log.d(msg,sourseN.getName()+"i m here ");
        graph = inventory.calculateShortestPathFromSource(graph, sourseN);
        for(node N:graph.nodes){
            if(N.getName().equals(d))
                destN = N;
        }
        destN.getShortestPath().add(destN);
        //endPoint = new PointF(destN.getXcoordinate(),destN.getYcoordinate());

        int n = destN.getShortestPath().size();
        for(node N:graph.nodes){
              Log.d(msg,String.valueOf(N.getName())+" "+String.valueOf(N.getDistance())+ " "+N.getXcoordinate()+" "+N.getYcoordinate());
            }

            //Log.d(msg,"The onCreate() event ACharya "+String.valueOf(n));
        return destN.getShortestPath();

    }

}
