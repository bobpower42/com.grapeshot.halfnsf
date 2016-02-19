package com.grapeshot.halfnsf;

import java.io.File;

public class halfNSF {

    
    public static NES nes;
    public halfNSF() {
    	nes=new NES();
    }
    
    public void LoadNSF(File selection) {
    	  if (selection != null) {
    	  nes.loadROM(selection.getAbsolutePath());
    	    Thread t=new Thread(nes);
    	    t.start();    	  }
    	} 

}
