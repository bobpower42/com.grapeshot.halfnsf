package com.grapeshot.halfnsf;
//HalfNES by Andrew Hoffman


import com.grapeshot.halfnsf.mappers.Mapper;

public class PPU {

    public Mapper mapper;
    //private int oamaddr, oamstart, readbuffer = 0;
    //private int loopyV = 0x0;//ppu memory pointer
    //private int loopyT = 0x0;//temp pointer
    //private int loopyX = 0;//fine x scroll
    public int scanline = 0;
    public int cycles = 0;
    //private int framecount = 0;
    private int div = 2;
    //private final int[] OAM = new int[256];
    /*
    secOAM = new int[32],
            spriteshiftregH = new int[8],
            spriteshiftregL = new int[8], spriteXlatch = new int[8],
            spritepals = new int[8], bitmap = new int[240 * 256];
      */      
    int bgShiftRegH, bgShiftRegL, bgAttrShiftRegH, bgAttrShiftRegL;
    //private final boolean[] spritebgflags = new boolean[8];
    //private boolean  nmicontrol, vblankflag;
            
    //private int emph;
    public final int[] pal = {0x09, 0x01, 0x00, 0x01, 0x00, 0x02, 0x02, 0x0D,
        0x08, 0x10, 0x08, 0x24, 0x00, 0x00, 0x04, 0x2C, 0x09, 0x01, 0x34, 0x03,
        0x00, 0x04, 0x00, 0x14, 0x08, 0x3A, 0x00, 0x02, 0x00, 0x20, 0x2C, 0x08};
    /*
     power-up pallette checked by Blargg's power_up_palette test. Different
     revs of NES PPU might give different initial results but there's a test
     expecting this set of values and nesemu1, BizHawk, RockNES, MyNes use it
     */
    
    //private int vraminc = 1;
    //private final static boolean PPUDEBUG = false;
    //private BufferedImage nametableView;
    //private final int[] bgcolors = new int[256];
    //private int openbus = 0; //the last value written to the PPU
    //private int nextattr;
    //private int linelowbits;
    //private int linehighbits;
    //private int penultimateattr;
    private int numscanlines;
    //private int vblankline;
    private int[] cpudivider;

    public PPU(final Mapper mapper) {
        this.mapper = mapper;
        //fill(OAM, 0xff);        
        setParameters();
    }

    final void setParameters() {
        //set stuff to NTSC or PAL or Dendy values
        switch (mapper.getTVType()) {
            case NTSC:
            default:
                numscanlines = 262;
                //vblankline = 241;
                cpudivider = new int[]{3, 3, 3, 3, 3};
                break;
            case PAL:
                numscanlines = 312;
                //vblankline = 241;
                cpudivider = new int[]{4, 3, 3, 3, 3};
                break;
            case DENDY:
                numscanlines = 312;
                //vblankline = 291;
                cpudivider = new int[]{3, 3, 3, 3, 3};
                break;
        }
    }

    public void runFrame() {
        for (int scanline = 0; scanline < numscanlines; ++scanline) {
        	for (cycles = 0; cycles < 341; ++cycles) {
                clock();
            }
        }
    }


  
    private int cpudividerctr = 0;

    /**
     * runs the emulation for one PPU clock cycle.
     */
    public final void clock() {
        //clock CPU, once every 3 ppu cycles
        //div = (div + 1) % cpudivider[cpudividerctr];
        div = (div + 1) % 3;
        if (div == 0) {
            mapper.cpu.runcycle(scanline, cycles);
            mapper.cpucycle(1);
            cpudividerctr = (cpudividerctr + 1) % cpudivider.length;
        }
        if (cycles == 257) {
            mapper.notifyscanline(scanline);
        } else if (cycles == 340) {
            scanline = (scanline + 1) % numscanlines;
            if (scanline == 0) {
                //++framecount;
            }
        }
    }
}
