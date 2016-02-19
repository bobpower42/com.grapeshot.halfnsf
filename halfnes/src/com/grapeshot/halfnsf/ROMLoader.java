package com.grapeshot.halfnsf;
//HalfNES, Copyright Andrew Hoffman, October 2010

import com.grapeshot.halfnsf.mappers.BadMapperException;
import com.grapeshot.halfnsf.mappers.Mapper;

public class ROMLoader {
    //this is the oldest code in the project... I'm honestly ashamed
    //at how it's structured but for now it works.
    //TODO: fix this up

    //this SHOULD do just enough to figure out the file type
    //and the correct mapper for it, and no more.
    public String name;
    public int prgsize;
    public int chrsize;
    public Mapper.MirrorType scrolltype;
    public Mapper.TVType tvtype;
    public int mappertype;
    public int submapper;
    public int prgoff, chroff;
    public boolean savesram = false;
    public int[] header;
    private final int[] therom;

    public ROMLoader(String filename) {
        therom = FileUtils.readfromfile(filename);
        name = filename;
    }

    private void ReadHeader(int len) {
        // iNES header is 16 bytes, nsf header is 128,
        //other headers increasingly large
        header = new int[len];
        System.arraycopy(therom, 0, header, 0, len);
    }

    public void parseHeader() throws BadMapperException {
        ReadHeader(128);
        if (('N' == header[0]) && ('E' == header[1])
                && ('S' == header[2]) && ('M' == header[3])
                && (0x1a == header[4])) {
            //nsf file
            mappertype = -1;
            //reread header since it's 128 bytes            
            prgsize = therom.length - 128;
        } else if (header[0] == 'U') {
            throw new BadMapperException("This is a UNIF file with the wrong extension");
        } else {
            throw new BadMapperException("iNES Header Invalid");
        }
    }

    public int[] load(int size, int offset) {
        int[] bindata = new int[size];
        System.arraycopy(therom, offset + header.length, bindata, 0, size);
        return bindata;
    }

    public int romlen() {
        return therom.length - header.length;
    }
}
