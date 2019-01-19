package com.company;

import java.awt.Component;
import javax.swing.*;
import jdk.nashorn.internal.codegen.CompilerConstants;

public class Game {
    
    private static Game game;
    private static boolean isInstantiated = false;
    private Minesweeper minesweeper;
    private DifficultyDialog difficultyDialog;
    private long startTime;
    private int mines;
    
    Game() {
        
       difficultyDialog = DifficultyDialog.getInstance();
       difficultyDialog.setVisible(true);
    }

    /**
     *  function to access game main instance
     * @return Game game 
     */
    public static Game getInstance() {
        
        if (isInstantiated) {
            return game;
            
        } else {
            game = new Game();
            isInstantiated = true;
            return game;
        }
    } 
    
    public void startGame(int width, int height, int mines) {
        
        mines = mines; // saving mines Count for score calculation
        
        // remove difficultyDialog
        difficultyDialog.setVisible(false);
        
        // Get current time
        startTime = System.currentTimeMillis();
        
        minesweeper = new Minesweeper();

        minesweeper.setWidth(width);
        minesweeper.setHeight(height);
        minesweeper.setNumberOfMines(mines);
        minesweeper.setZoneSize(30); // Size in pixels

        minesweeper.Start();
    }
    
    /**
     * Shows dialog displaying message about winning or losing the game, with restart possibility
     * @param won Boolean value to determine what message should be displayed
     */
    public void gameOver(boolean won, Zone[][] zone, int width, int height, Component board) {
        
        
        long elapsedTimeMillis = System.currentTimeMillis()-startTime;

        // Get elapsed time in seconds
        int elapsedTimeSec = (int)(elapsedTimeMillis/1000F);
        
        int revealedZones = 0;
        int markedZones = 0;
        int mineCount = 0;
        
        int score = 0;
        
        String dialogTitle;
        String dialogMessage;
        
        if (won) {
            dialogTitle = "You won the game";
            dialogMessage = "Congratulations, you won";
            
        } else {
            dialogTitle = "You lost the game";
            dialogMessage = "Sorry, you lost";
        }
        
        // Set red color on mined zones and get needed data for score calculation
        for (int i = 1; i <= width; i++) {
            for (int j = 1; j <= height; j++) {
                
                if (zone[i][j].zoneRevealed) revealedZones++;;
                if (zone[i][j].zoneMarked) markedZones++;
                
                // revealing all zones 
                if (zone[i][j].zoneMined) {
                    zone[i][j].zoneRevealed = true;
                    zone[i][j].repaint();
                } 
            }
        }
        
        score = (int) ((1000 - 10*(Math.pow(mines - markedZones, 2)))*revealedZones / Math.abs(Math.abs(elapsedTimeMillis)));

        // declaring Options for dialog
        Object[] options = {"Yes", "Exit"};
        
        int n = JOptionPane.showOptionDialog(board,
            dialogMessage
                    + "\nGame Ended in " + elapsedTimeSec + " seconds | " + elapsedTimeMillis + " miliSeconds"
                    + "\nYour score is " + score 
                    + "\nDo you want to restart the game?",
            dialogTitle,
            JOptionPane.YES_NO_OPTION, // option type
            JOptionPane.PLAIN_MESSAGE, // message type
            null, // icon
            options,
            options[0] // default selected option
        );
        
        if (n == JOptionPane.YES_OPTION) {
            minesweeper.newGame();
        } else if (n == JOptionPane.NO_OPTION) {
            System.exit(0);
        } else {
            System.exit(0);
        }
    }

}