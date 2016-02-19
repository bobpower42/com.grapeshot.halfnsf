package com.grapeshot.halfnsf;

import java.io.File;

import processing.core.PApplet;





public class halfNSF  extends PApplet {

    
    public static NES nes;
    public static void main(String[] args) {
        String[] a = {"MAIN"};
        PApplet.runSketch( a, new halfNSF());
    }
    public void settings(){
        size(200,200,P2D); 
        smooth();
        
    }
    public void setup(){
    	nes=new NES();    
    }
    public void draw() {
        background(128); 
    }
    public void mousePressed(){
    	selectInput("Select a file to process:", "fileSelected");    
    }
    public void fileSelected(File selection) {
    	  if (selection == null) {
    	    println("Window was closed or the user hit cancel.");
    	  } else {
    	    //nes.loadROM(selection.getAbsolutePath());
    		  println("load");
    	    nes.loadROM(selection.getAbsolutePath());
    	    println(nes.getrominfo());
    	    Thread t=new Thread(nes);
    	    t.start();    	  }
    	}
    
    public void startNSF(){
    	nes.run();    
    }
}
