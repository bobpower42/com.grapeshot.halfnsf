/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grapeshot.halfnsf.audio;

/**
 *
 * @author Andrew
 */
public interface ExpansionSoundChip {

    public void clock(final int cycles);

    public void write(int register, int data);

    public int getval();
}
