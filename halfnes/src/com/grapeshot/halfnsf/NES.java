package com.grapeshot.halfnsf;

import com.grapeshot.halfnsf.mappers.BadMapperException;
import com.grapeshot.halfnsf.mappers.Mapper;
import com.grapeshot.halfnsf.mappers.NSFMapper;



/**
 *
 * @author Andrew Hoffman
 */
public class NES implements Runnable{

    private Mapper mapper;
    private APU apu;
    private CPU cpu;
    private CPURAM cpuram;
    private PPU ppu;    
    final public static String VERSION = "060";
    public boolean runEmulation = false;
    //private boolean dontSleep = false;
    public long frameStartTime, framecount, frameDoneTime;
    private boolean frameLimiterOn = true;
    private String curRomPath, curRomName;    
    

    public NES() {
    	//(new Thread(new NES())).start();
    }

    public void run(final String romtoload) {
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY + 1);
        //set thread priority higher than the interface thread
        curRomPath = romtoload;
        loadROM(romtoload);
        run();
    }

    public void run() {
        while (true) {
            if (runEmulation) {
                frameStartTime = System.nanoTime();
               
                runframe();
                
                frameDoneTime = System.nanoTime() - frameStartTime;
            }
        }
    }
    

    private synchronized void runframe() {
        

        //do end of frame stuff
        //dontSleep = apu.bufferHasLessThan(1000);
        //if the audio buffer is completely drained, don't sleep for this frame
        //this is to prevent the emulator from getting stuck sleeping too much
        //on a slow system or when the audio buffer runs dry.

        apu.finishframe();
        cpu.modcycles();

//        if (framecount == 13 * 60) {
//            cpu.startLog();
//            System.err.println("log on");
//        }
        

        //run cpu, ppu for active drawing time
        //render the frame
        ppu.runFrame();
        
        ++framecount;
        //System.out.println(framecount);
    }

    

    public void toggleFrameLimiter() {
        frameLimiterOn = !frameLimiterOn;
    }

    public synchronized void loadROM(final String filename) {
        runEmulation = false;
        if (FileUtils.exists(filename)
                && FileUtils.getExtension(filename).equalsIgnoreCase(".nsf")) {
            Mapper newmapper;
            try {
                final ROMLoader loader = new ROMLoader(filename);
                loader.parseHeader();
                newmapper = new NSFMapper();
                newmapper.setLoader(loader);
                newmapper.loadrom();
            } catch (BadMapperException e) {
                
                return;
            } catch (Exception e) {
                
                return;
            }
            if (apu != null) {
                //if rom already running save its sram before closing
                apu.destroy();                
                //also get rid of mapper etc.
                mapper.destroy();
                cpu = null;
                cpuram = null;
                ppu = null;
            }
            mapper = newmapper;
            //now some annoying getting of all the references where they belong
            cpuram = mapper.getCPURAM();
            
            cpu = mapper.cpu;
            ppu = mapper.ppu;
            apu = new APU(this, cpu, cpuram);
            cpuram.setAPU(apu);
            cpuram.setPPU(ppu);
            curRomPath = filename;
            curRomName = FileUtils.getFilenamefromPath(filename);

            framecount = 0;            
            //and start emulation
            cpu.init();
            mapper.init();
            setParameters();
            runEmulation = true;
        } else {
            
        }
    }

    

    public void quit() {
        //save SRAM and quit
        if (cpu != null && curRomPath != null) {
            runEmulation = false;            
        }
        System.exit(0);
    }

    public synchronized void reset() {
        if (cpu != null) {
            mapper.reset();
            cpu.reset();
            runEmulation = true;
            apu.pause();
            apu.resume();
        }
        //reset frame counter as well because PPU is reset
        //on Famicom, PPU is not reset when Reset is pressed
        //but some NES games expect it to be and you get garbage.
        framecount = 0;
    }

    public synchronized void reloadROM() {
        loadROM(curRomPath);
    }

    public synchronized void pause() {
        if (apu != null) {
            apu.pause();
        }
        runEmulation = false;
    }

    public long getFrameTime() {
        return frameDoneTime;
    }

    public String getrominfo() {
        if (mapper != null) {
            return mapper.getrominfo();
        }
        return null;
    }

    public synchronized void frameAdvance() {
        runEmulation = false;
        if (cpu != null) {
            runframe();
        }
    }

    public synchronized void resume() {
        if (apu != null) {
            apu.resume();
        }
        if (cpu != null) {
            runEmulation = true;
        }
    }

    public String getCurrentRomName() {
        return curRomName;
    }

    public boolean isFrameLimiterOn() {
        return frameLimiterOn;
    }

  

    public synchronized void setParameters() {
        if (apu != null) {
            apu.setParameters();
        }      
    }

 
}
