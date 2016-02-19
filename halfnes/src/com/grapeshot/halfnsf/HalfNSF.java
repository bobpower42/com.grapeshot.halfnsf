package com.grapeshot.halfnsf;

import java.io.File;

public class HalfNSF {

    
    public static NES nes;
    public HalfNSF() {
    	nes=new NES();
    }
    
    public void LoadNSF(File selection) {
    	  if (selection != null) {
    		  try{
    		  LoadNSF(selection.getAbsolutePath());
    		  }catch(Exception ex){
    			  
    		  }
    	    
    	  }
    }
    public void LoadNSF(String selection) {
    	if (selection != null) {
    		try{
    		 nes.loadROM(selection);
     	    Thread t=new Thread(nes);
     	    t.start();
    		}catch(Exception ex){
    		
    		}
    	}
    }

}
