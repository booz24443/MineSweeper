package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

public class Minesweeper {

    private Zone[][] zone;
    private JFrame board;
    private MouseListener mouseListener;
    private int width;
    private int height;
    private int zoneSize;
    private int numberOfMines;
    private int numberOfMarked;
    private int numberOfRevealed;

    public Minesweeper() {
        
        // Handle mouse events
        mouseListener = new MouseListener() {
            
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {
                
                Zone _zone = (Zone) e.getSource(); // Casting Object to Zone

                if (!_zone.zoneRevealed) { // Check if zone is not already revealed
                    
                     // Open zone if left mouse button is clicked and zone isn't marked
                    if (SwingUtilities.isLeftMouseButton(e) && !_zone.zoneMarked) openZone(_zone);
                   
                    // Mark zone if right mouse button is clicked
                    else if (SwingUtilities.isRightMouseButton(e)) markZone(_zone); 
                }
                // Check if user won the game
                checkWinStatus();
            }

            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}

        };
    }

    // Getters
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getZoneSize() { return zoneSize; }
    public int getNumberOfMines() { return numberOfMines; }
    
    // Setters
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public void setZoneSize(int zoneSize) { this.zoneSize = zoneSize; }
    public void setNumberOfMines(int numberOfMines) { this.numberOfMines = numberOfMines; }


    /**
     * Starts the game and creates GUI
     * This method should be called once per Minesweeper object
     */
    public void Start() {
        createBoard();
        newGame();
    }

    /**
     * Creates actual GUI and places Zone objects on it
     * Note that .pack() method doesn't set correct size on Windows 8.1 (tested)
     */
    private void createBoard() {
        
        board = new JFrame("Minesweeper");
        board.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        // instead of using a JPanel
        Container pane = board.getContentPane();
        pane.setLayout(null); // test commenting this line

        // Creating zones
        zone = new Zone[width+1][height+1]; // just to start arrays from index 1 :)

        for (int i = 1; i <= width; i++) {
            for (int j = 1; j <= height; j++) {
                zone[i][j] = new Zone(i, j, zoneSize);
                pane.add(zone[i][j]);
                zone[i][j].addMouseListener(mouseListener);
            }
        }

        pane.setPreferredSize(new Dimension(width * zoneSize, height * zoneSize));
        board.pack(); // Causes this Window to be sized to fit the preferred size and layouts of its subcomponents
        board.setLocationRelativeTo(null); // Center the window position
        
        board.setResizable(false);
        board.setVisible(true);
    }

    /**
     * Resets all data from last game and starts new
     */
    public void newGame() {
        numberOfMarked = 0;
        numberOfRevealed = 0;
        resetZones();
        placeMines();
        placeNumbers();
    }

    /**
     * Resets all Zone objects to it's defaults
     */
    private void resetZones() {
        for (int i = 1; i <= width; i++) {
            for (int j = 1; j <= height; j++) {
                zone[i][j].reset();
                zone[i][j].repaint();
            }
        }
    }

    /**
     * Calculate random positions for mines
     */
    private void placeMines() {
        
        int randomX, randomY;
        int n = numberOfMines;
        
        while (n > 0) {
            randomX = randInt(1, width);
            randomY = randInt(1, height);
            
            if (!zone[randomX][randomY].zoneMined) { // if zone wasn't already mined
                zone[randomX][randomY].zoneMined = true;
                n--;
            }
        }
    }

    /**
     * Place numbers where required depending on mines' positions
     */
    private void placeNumbers() {
        
        int mines;
        
        for (int i = 1; i <= width; i++) {
            for (int j = 1; j <= height; j++) {
                
                if (zone[i][j].zoneMined) continue;

                mines = 0;
                ArrayList<Zone> neighbours = getNeighbours(zone[i][j]);

                for (Zone neighbour : neighbours) {
                    if (neighbour.zoneMined) {
                        mines++;
                    }
                }

                // setting zoneValue by number of mines aside (can be 0)
                zone[i][j].zoneValue = mines;
                
            }
        }
    }

    /**
     * Marks or unmarks zone
     * @param _zone Zone object that should be marked or unmarked
     */
    private void markZone(Zone _zone) {
        
        // changing numberOfMarked
        if (_zone.zoneMarked) {
            numberOfMarked--;
            
        } else {
            numberOfMarked++;
        }
        
        // reversing zoneMarked value
        _zone.zoneMarked = !_zone.zoneMarked;
        _zone.repaint();
    }

    /**
     * Opens Zone and it's neighbors
     * @param _zone Zone object that should be opened
     */
    private void openZone(Zone _zone) {
        
        // if zone was mined => game is lost
        if (_zone.zoneMined) {
            Game.getInstance().gameOver(false, zone, width, height, board);
            return;
        }

        // if zone wasn't marked and wasn't clicked before
        if (!_zone.zoneMarked && !_zone.zoneRevealed) {
            numberOfRevealed++;
            _zone.zoneRevealed = true;
            _zone.repaint();
            
            if (_zone.zoneValue == 0) { // if there are no mines aside this zone => open neighbors
                openNeighbors(_zone);
            }
        }
    }

    /**
     * Opens neighbours of given Zone object
     * @param _zone Zone object whose neighbours should be opened
     */
    private void openNeighbors(Zone _zone) {
        ArrayList<Zone> neighbours = getNeighbours(_zone);

        for (Zone neighbour : neighbours) {
            if (!neighbour.zoneRevealed) { // if zone wasn't clicked before
                openZone(neighbour);
            }
        }
    }

    /**
     * Searches for given Zone's neighbours and returns them 
     * @param _zone Zone object whose neighbours should be returned
     * @return ArrayList of Zone's containing all neighbours of given Zone
     */
    private ArrayList<Zone> getNeighbours(Zone _zone) {
        
        ArrayList<Zone> neighbours = new ArrayList<Zone>();
        
        // declaring X, Y for calulations
        int X = _zone.zoneX - 1;
        int Y = _zone.zoneY - 1;

        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 2; j++) {
                
                if (i == 1 && j == 1) continue; // position of mine itself
                
                // checking if position is in the board and not outside of board range
                if (X+i > 0 && Y+j > 0 && X+i <= width && Y+j <= height) {
                    neighbours.add(zone[X+i][Y+j]);
                }
            }
        }

        return neighbours;
    }

    /**
     * Checks if user has won the game & finishes the game
     */
    private void checkWinStatus() {
        if ((numberOfMarked + numberOfRevealed) == (width * height)) {
            Game.getInstance().gameOver(true, zone, width, height, board);
        }
    }

    /**
     * Generates random number in min - max range
     * @param min Minimum value of generated random number
     * @param max Maximum value of generated random number
     * @return
     */
    private static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
}