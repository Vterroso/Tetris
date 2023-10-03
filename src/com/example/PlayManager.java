package com.example;

import mino.*;

import java.awt.*;
import java.util.ArrayList;

public class PlayManager{
    //Main Play Area
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    //Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXT_MINO_X;
    final int NEXT_MINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    //Others
    public static int dropInterval = 60; // mino drops every 60 frames
    boolean gameOver;

    //Effect
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    //Score
    int level = 1;
    int lines;
    int score;

    public PlayManager(){
        //Main Play Area Frame
        left_x = (GamePanel.WIDTH/2) - (WIDTH/2); //1280/2 - 360/2 = 460
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + (WIDTH/2 - Block.SIZE);
        MINO_START_Y = top_y + Block.SIZE;

        NEXT_MINO_X = right_x + 175;
        NEXT_MINO_Y = top_y + 500;

        //Set the starting mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXT_MINO_X, NEXT_MINO_Y);

    }
    private Mino pickMino(){
        Mino mino = null;
        int rand = (int)(Math.random() * 7);
        switch (rand) {
            case 0 -> mino = new MinoL1();
            case 1 -> mino = new MinoL2();
            case 2 -> mino = new MinoSquare();
            case 3 -> mino = new MinoBar();
            case 4 -> mino = new MinoT();
            case 5 -> mino = new MinoZ1();
            case 6 -> mino = new MinoZ2();
        }
        return mino;
    }
    public void update(){
        if(!currentMino.active){
            //if the current mino is not active, put it in the static blocks array
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            //check if the mino is over
            if(currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y){
                gameOver = true;
                GamePanel.music.stop();
                GamePanel.se.play(2, false);
            }

            currentMino.deactivating = false;

            //replace the current mino with the next mino
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXT_MINO_X, NEXT_MINO_Y);
            //when mino becomes inactive, check if the line(s) is full
            checkDelete();
        }
        else {
            currentMino.update();
        }
    }
    private void checkDelete(){
        //check if the line(s) is full
        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;

        while (x < right_x && y < bottom_y){

            for (Block staticBlock : staticBlocks) {
                if (staticBlock.x == x && staticBlock.y == y) {
                    //if there is a block at the current x and y, increment blockCount
                    blockCount++;
                }
            }
            x += Block.SIZE;
            if(x == right_x){
                if(blockCount == 12){
                    //if the line is full, delete the line
                    effectCounterOn = true;
                    effectY.add(y);
                    for(int i = staticBlocks.size() - 1; i > -1; i--){
                        if(staticBlocks.get(i).y == y){
                            staticBlocks.remove(i);
                        }
                    }
                    lines++;
                    lineCount++;
                    //Drop speed increases every 10 lines
                    if(lines % 10 ==  0 && dropInterval > 1){
                        level++;
                        if(dropInterval > 10){
                            dropInterval -= 10;
                        }
                        else {
                            dropInterval--;
                        }
                    }

                    //move all the blocks above the deleted line down
                    for (Block staticBlock : staticBlocks) {
                        if (staticBlock.y < y) {
                            staticBlock.y += Block.SIZE;
                        }
                    }
                }

                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }
        }

        //update the score
        if(lineCount == 1){
            GamePanel.se.play(1, false);
            score += 40 * level;
        }
        else if(lineCount == 2){
            GamePanel.se.play(1, false);
            score += 100 * level;
        }
        else if(lineCount == 3){
            GamePanel.se.play(1, false);
            score += 300 * level;
        }
        else if(lineCount == 4){
            GamePanel.se.play(1, false);
            score += 1200 * level;
        }
    }
    public void draw(Graphics2D g2){
        //Draw Play Area Frame
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x - 4, top_y - 4, WIDTH + 8, HEIGHT + 8);

        // Draw Play Area Frame
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x + 60, y + 60);

        //Draw the score
        g2.drawRect(x, top_y, 250, 300);
        x += 40;
        y = top_y + 90;
        g2.drawString("LEVEL: " + level, x, y); y += 70;
        g2.drawString("LINES: " + lines, x, y); y += 70;
        g2.drawString("SCORE: " + score, x, y);

        //Draw the currentMino
        if(currentMino != null){
            currentMino.draw(g2);
        }

        //Draw the nextMino
        if(nextMino != null){
            nextMino.draw(g2);
        }

        //Draw the staticBlocks
        for (Block staticBlock : staticBlocks) {
            staticBlock.draw(g2);
        }
        //Draw the effect
        if(effectCounterOn){
            effectCounter++;

            g2.setColor(Color.white);
            for (Integer integer : effectY) {
                g2.fillRect(left_x, integer, WIDTH, Block.SIZE);
            }

            if(effectCounter == 10){
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }
        }
        //Draw Pause or Game Over
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(50f));
        if(gameOver){
            x = left_x + 25;
            y = top_y + 320;
            g2.drawString("GAME OVER", x, y);
        }
        if(KeyHandler.pausePressed){
            x = left_x + 70;
            y = top_y + 320;
            g2.drawString("PAUSED", x, y);
        }
        //Draw GameTitle
        x = 120;
        y = top_y + 320;
        g2.setColor(Color.white);
        g2.setFont(new Font("Times New Roman", Font.ITALIC, 60));
        g2.drawString("TETRIS", x, y);
    }
}
