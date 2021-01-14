package mineSweeper;


import java.util.Random;
import javax.swing.ImageIcon;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Victor
 */
class MineField 
{
    protected int rows, cols, mines;
    protected Object [][] field;
    
   public MineField(int rows, int cols, int mines)
   {
      this.rows = rows;
      this.cols = cols;
      this.mines = mines;
      field = new Object[rows][cols];
      fillMineField();
   }
   
   public void reset()
   {
      for(int row = 0; row < rows; row++)
      {
         for(int col = 0; col < cols; col++)
         {
            field[row][col] = null;
         }
      }
      fillMineField();
   }
   
   public void fillMineField()
   {
      int minesLeft = mines;
      Random rand = new Random();
      while(minesLeft > 0)
      {
         int x = rand.nextInt(rows);
         int y = rand.nextInt(cols);
         if(!(field[x][y] instanceof Mine) )
         {
            field[x][y] = new Mine();
            addSides(x-1,y);
            addSides(x-1,y-1);
            addSides(x,y-1);
            addSides(x+1,y-1);
            addSides(x+1,y);
            addSides(x+1,y+1);
            addSides(x,y+1);
            addSides(x-1,y+1);
            minesLeft--;
         }
      }      
   }
   
   public ImageIcon getIcon(int x, int y)
   {
      int num = (int) field[x][y];
      switch(num)
      {
          case 1:
                return new ImageIcon("resources\\1.png");
          case 2:
                return new ImageIcon("resources\\2.png");
          case 3:
                return new ImageIcon("resources\\3.png");
          case 4: 
                return new ImageIcon("resources\\4.png");
          case 5: 
                return new ImageIcon("resources\\5.png");
          case 6:
                return new ImageIcon("resources\\6.png");
          case 7:
                return new ImageIcon("resources\\7.png");
          case 8: 
                return new ImageIcon("resources\\8.png");
          default:
                return null;
      }
   }
   
   //increments the sides of the mine by 1
   public void addSides(int x, int y)
   {
      if(x < 0|| x >= rows || y < 0 || y >= cols)
          return;
      if(field[x][y] instanceof Mine)
          return;
      if(field[x][y] == null)
      {
         field[x][y] = 1;
         return;
      }
      field[x][y] = 1 + (int)field[x][y];
   }
   
   public Object getIndex(int x, int y)
   {
      return field[x][y];
   }
   
   public int getFrameWidth()
   {
      return 60 + (35*cols);
   }
   
   public int getFrameHeight()
   {
      return 35 + (35*rows);
   }
   
}
