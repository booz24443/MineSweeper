package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import com.company.resources.Repository;

public class Zone extends JButton {

    public boolean zoneMined; // if zone does have mine
    public boolean zoneRevealed; // if zone is clicked (left click)
    public boolean zoneMarked; // if zone if marked as a mine (right click)
    
    public int zoneValue; // the number of the zone based on side mines ( Range: 0-8 )
    public int zoneX; // zone X position starting from 1
    public int zoneY; // zone Y position starting from 1
    public int zoneSize;  // each zone is a square of zoneSize*zoneSize

    public Zone(int zoneX, int zoneY, int zoneSize) {
        super(); // not an obligation to call superclass Constructor :)

        zoneSize = zoneSize;
        
        // setting zone position with rectangle arguments
        setBounds((zoneX - 1) * zoneSize, (zoneY - 1) * zoneSize, zoneSize, zoneSize);
        reset();
        this.zoneX = zoneX;
        this.zoneY = zoneY;
    }

    /**
     * every time we call zone.repaint this function will be executed
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Color colorCurrent = new Color(0x000000);

        // True, if the zone's X and Y positions are both odd or even numbers
        boolean crossingPosition = (zoneX % 2 != 0 && zoneY % 2 != 0) || (zoneX % 2 == 0 && zoneY % 2 == 0);

        // setting zone color by checking zone status
        
        // zone color for normal state when zone is not clicked or marked
        colorCurrent = (!zoneRevealed && !zoneMarked) ? 
                (crossingPosition ? Repository.colorNormalLight : Repository.colorNormalDark) : colorCurrent;
        
        // zone color when zone is clicked and is not mined
        colorCurrent = (zoneRevealed && !zoneMined) ? 
                (crossingPosition ? Repository.colorRevealedLight : Repository.colorRevealedDark) : colorCurrent;
        
        // zone color when zone is marked
        colorCurrent = (zoneMarked) ? (crossingPosition ? 
                Repository.colorMarkedLight : Repository.colorMarkedDark) : colorCurrent;
        
        // zone color when zone is clicked and was mined
        colorCurrent = (zoneRevealed && zoneMined) ? 
                (crossingPosition ? Repository.colorMinedLight : Repository.colorMinedDark) : colorCurrent;

        
        // drawing zone
        Graphics2D g2 = (Graphics2D) g.create();
        GradientPaint gp = new GradientPaint(0, 0, colorCurrent, zoneSize, zoneSize, colorCurrent);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        if (zoneRevealed && zoneValue > 0) { // if zone was clicked and had number => render number in center
                
                String value = Integer.toString(zoneValue);
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(value, g2);
                
                // center position of zone 
                int x = (this.getWidth() - (int) r.getWidth()) / 2;
                int y = (this.getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                // font ascent is the distance from the font's baseline to the top
                
                g.setFont(Repository.zoneFont);
                g.drawString(value, x, y); // rendering number
        }
    }

    /**
     * Resets all zone properties to defaults
     */
    public void reset() {
        zoneMined = false;
        zoneRevealed = false;
        zoneMarked = false;
        zoneValue = 0;
    }
}