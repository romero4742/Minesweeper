package mineSweeper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JOptionPane;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Victor
 */
@SuppressWarnings("serial")
public class MineSweeper extends javax.swing.JFrame implements ActionListener
{

    protected MineField mf;
    protected JButton [][] buttons;
    protected long startTime,currTime,bestTime;
    protected String highScoreName;
    protected int totalButtons;
    protected File file;
    /**
     * Creates new form MineSweeper
     */

    public MineSweeper() {
        initComponents();
        GamePanel.setVisible(false);
    }
   
    
    public void actionPerformed(ActionEvent e)
    {
        JButton bt = (JButton)e.getSource();
        int targetX = -1;
        int targetY= -1;
        for(int row = 0; row < mf.rows ; row++)
        {
           for(int col = 0; col < mf.cols; col++)
           {
              if(bt.equals(buttons[row][col]))
              {
                 targetX = row;
                 targetY = col;
                 row = mf.rows;
                 col = mf.cols;
              }
           }
        }
        if(buttons[targetX][targetY].getIcon() != null)
            return;
        lookMineField(targetX, targetY);
        if(totalButtons == 0)
        {
           gameWon();
        }
    }
    
    public void revealEmpty(int x ,int y)
    {
      if(x < 0 || x >= mf.rows || y < 0 || y >= mf.cols)
          return;
      if(!buttons[x][y].isEnabled() || buttons[x][y].getIcon() != null)
          return;
      if(mf.field[x][y] != null)
      {
          buttons[x][y].setIcon(mf.getIcon(x,y));
          totalButtons--;
          return;
      }
      
      buttons[x][y].setEnabled(false);
      totalButtons--;
      revealEmpty(x-1,y);
      revealEmpty(x+1,y);
      revealEmpty(x,y-1);
      revealEmpty(x,y+1);
      revealEmpty(x-1,y-1);
      revealEmpty(x-1,y+1);
      revealEmpty(x+1,y-1);
      revealEmpty(x+1,y+1);  
    }
    
    public void gameWon()
    {
       currTime = System.currentTimeMillis();
       currTime = currTime - startTime;
       revealMines();
       try
       {
          if(bestTime > currTime)
          {
             String name =JOptionPane.showInputDialog(GamePanel, "Enter your name", "NEW HIGH SCORE", JOptionPane.INFORMATION_MESSAGE);
             FileWriter fw = new FileWriter(file);
             PrintWriter pw = new PrintWriter(fw);
             pw.print(currTime);
             pw.print("  ");
             pw.print(name);
             pw.close();
             fw.close();
             highScore.setText(String.valueOf(TimeUnit.MILLISECONDS.toMinutes(currTime))+":"+String.valueOf(TimeUnit.MILLISECONDS.toSeconds(currTime)));
             highName.setText(name);
          }
       }
       catch(IOException e)
       {
          System.out.println(e);
       }
       int opt = JOptionPane.showConfirmDialog(GamePanel,"Congratulations, you won!!!...Would you like to play again?","Play again?",JOptionPane.YES_NO_OPTION);
       if(opt == 1)
           System.exit(0);
       mf.reset();
       resetFieldButtons();
    }
    
    public void readHighScore(File file) throws FileNotFoundException
    {      
       Scanner kb = new Scanner(file);
       bestTime = kb.nextLong();
       highScoreName = kb.nextLine();
       kb.close();
    }
    
    public void cheat()
    {
       for(int i = 0; i < mf.rows; i++)
       {
          for(int j = 0; j < mf.cols;j++)
          {
             if(mf.field[i][j] instanceof Mine)
                 buttons[i][j].setText("M");
          }
       }
    }
    
    public void gameLost()
    {
       revealMines();
       int opt = JOptionPane.showConfirmDialog(GamePanel,"Sorry, you lose..Would you like to play again?","Play again?",JOptionPane.YES_NO_OPTION);
       if(opt == 1)
           System.exit(0);
       totalButtons = mf.rows* mf.cols;
       mf.reset();
       resetFieldButtons();
    }
    
    public void revealMines()
    {
       for(int row = 0; row < mf.rows; row++)
       {
          for(int col = 0; col < mf.cols; col++)
          {
              Object obj = mf.field[row][col];
             if(obj instanceof Mine)
             {
                Mine m = (Mine) obj;
                buttons[row][col].setIcon(m.getMineIcon());
             }
          }
       }
    }
    
    public void resetFieldButtons()
    {
       for(int row = 0; row < mf.rows; row++)
       {
          for(int col = 0; col < mf.cols; col++)
          {
             buttons[row][col].setEnabled(true);
             buttons[row][col].setIcon(null);
          }
       }
    }
    
    public void revealNumber(int x,int y)
    {
       buttons[x][y].setIcon(mf.getIcon(x, y));
       totalButtons--;
    }
    
    public void lookMineField(int x, int y)
    {
        if(x < 0|| y < 0 )
        {
           throw new Error("Coordinates in buttons array are messed up!!");
        }
        Object obj = mf.getIndex(x, y);
        if(obj == null)
        {
            revealEmpty(x,y);            
            return;
        }
        if(obj instanceof Mine)
        {
            gameLost();
            return;
        }
        else
        {
        revealNumber(x,y);
        }
    }

    public void initFieldButtons()
    {
     //initialize buttons 
       for(int row = 0; row < mf.rows; row++)
       {
          for(int col = 0; col < mf.cols; col++)
          {
             JButton bt  = new JButton();
             buttons[row][col] = bt;
             buttons[row][col].addActionListener(this);
          }
       }
       setFieldButtons();
    }
    
    public void setFieldButtons()
    {
       int currX = 0;
       int currY = 0;
       for(int row = 0;row < mf.rows; row++)
       {
          for(int col = 0; col < mf.cols; col++)
          {
             JButton bt;
             bt = buttons[row][col];
             fieldPanel.add(bt);
             currX += 30;
             bt.setBounds(currX, currY, 35, 35);
          }
          currY += 30;
          currX = 0;
       }
       highScore.setText(String.valueOf(TimeUnit.MILLISECONDS.toMinutes(bestTime))+":"+String.valueOf(TimeUnit.MILLISECONDS.toSeconds(bestTime)));
       highName.setText(highScoreName);
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jInternalFrame1 = new javax.swing.JInternalFrame();
        FirstPanel = new javax.swing.JPanel();
        mineSweeperLabel = new javax.swing.JLabel();
        DifficultyPanel = new javax.swing.JPanel();
        DifficultyLabel = new javax.swing.JLabel();
        Easy = new javax.swing.JButton();
        Medium = new javax.swing.JButton();
        Hard = new javax.swing.JButton();
        GamePanel = new javax.swing.JPanel();
        fieldPanel = new javax.swing.JPanel();
        highScoreLabel = new javax.swing.JLabel();
        highScore = new javax.swing.JLabel();
        highName = new javax.swing.JLabel();
        by = new javax.swing.JLabel();

        jInternalFrame1.setVisible(true);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 272, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 188, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MineSweeper");

        FirstPanel.setBackground(new java.awt.Color(0, 0, 0));
        FirstPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        mineSweeperLabel.setFont(new java.awt.Font("Comic Sans MS", 1, 36)); // NOI18N
        mineSweeperLabel.setForeground(new java.awt.Color(0, 0, 255));
        mineSweeperLabel.setText("MINESWEEPER");

        DifficultyPanel.setBackground(new java.awt.Color(153, 153, 255));
        DifficultyPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.darkGray, java.awt.Color.gray, null, null));
        DifficultyPanel.setForeground(new java.awt.Color(153, 153, 255));

        DifficultyLabel.setFont(new java.awt.Font("Comic Sans MS", 2, 24)); // NOI18N
        DifficultyLabel.setText("Select Difficulty");

        Easy.setFont(new java.awt.Font("Comic Sans MS", 0, 18)); // NOI18N
        Easy.setForeground(new java.awt.Color(0, 51, 255));
        Easy.setText("Easy");
        Easy.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Easy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EasyActionPerformed(evt);
            }
        });

        Medium.setFont(new java.awt.Font("Comic Sans MS", 0, 18)); // NOI18N
        Medium.setForeground(new java.awt.Color(0, 0, 255));
        Medium.setText("Medium");
        Medium.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Medium.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MediumActionPerformed(evt);
            }
        });

        Hard.setFont(new java.awt.Font("Comic Sans MS", 0, 18)); // NOI18N
        Hard.setForeground(new java.awt.Color(0, 0, 255));
        Hard.setText("Hard");
        Hard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Hard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HardActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout DifficultyPanelLayout = new javax.swing.GroupLayout(DifficultyPanel);
        DifficultyPanel.setLayout(DifficultyPanelLayout);
        DifficultyPanelLayout.setHorizontalGroup(
            DifficultyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DifficultyPanelLayout.createSequentialGroup()
                .addContainerGap(87, Short.MAX_VALUE)
                .addGroup(DifficultyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Hard, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Medium, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Easy, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60))
        );
        DifficultyPanelLayout.setVerticalGroup(
            DifficultyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DifficultyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DifficultyLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Easy, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Medium, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Hard, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout FirstPanelLayout = new javax.swing.GroupLayout(FirstPanel);
        FirstPanel.setLayout(FirstPanelLayout);
        FirstPanelLayout.setHorizontalGroup(
            FirstPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FirstPanelLayout.createSequentialGroup()
                .addContainerGap(190, Short.MAX_VALUE)
                .addComponent(mineSweeperLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(130, 130, 130))
            .addGroup(FirstPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FirstPanelLayout.createSequentialGroup()
                    .addContainerGap(74, Short.MAX_VALUE)
                    .addComponent(DifficultyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(64, Short.MAX_VALUE)))
        );
        FirstPanelLayout.setVerticalGroup(
            FirstPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FirstPanelLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(mineSweeperLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(402, Short.MAX_VALUE))
            .addGroup(FirstPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FirstPanelLayout.createSequentialGroup()
                    .addContainerGap(108, Short.MAX_VALUE)
                    .addComponent(DifficultyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(45, Short.MAX_VALUE)))
        );

        GamePanel.setBackground(new java.awt.Color(0, 0, 0));
        GamePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        fieldPanel.setBackground(new java.awt.Color(153, 153, 255));
        fieldPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.darkGray, java.awt.Color.gray, null, null));
        fieldPanel.setForeground(new java.awt.Color(153, 153, 255));

        highScoreLabel.setText("Best Time:");

        by.setText("By:");

        javax.swing.GroupLayout fieldPanelLayout = new javax.swing.GroupLayout(fieldPanel);
        fieldPanel.setLayout(fieldPanelLayout);
        fieldPanelLayout.setHorizontalGroup(
            fieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fieldPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(highScoreLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(highScore, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(by)
                .addGap(18, 18, 18)
                .addComponent(highName, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(163, Short.MAX_VALUE))
        );
        fieldPanelLayout.setVerticalGroup(
            fieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fieldPanelLayout.createSequentialGroup()
                .addGap(0, 432, Short.MAX_VALUE)
                .addGroup(fieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(highScoreLabel)
                    .addComponent(highScore)
                    .addComponent(highName)
                    .addComponent(by)))
        );

        javax.swing.GroupLayout GamePanelLayout = new javax.swing.GroupLayout(GamePanel);
        GamePanel.setLayout(GamePanelLayout);
        GamePanelLayout.setHorizontalGroup(
            GamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(GamePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fieldPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        GamePanelLayout.setVerticalGroup(
            GamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(GamePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fieldPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 609, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(FirstPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(GamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(FirstPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addComponent(GamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void HardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HardActionPerformed
        mf = new MineField(16,30,99);
        totalButtons = 381;
        file = new File("resources\\bestTimes\\high.txt");
        try 
        {
            readHighScore(file);
        } catch (FileNotFoundException ex) 
        {
            Logger.getLogger(MineSweeper.class.getName()).log(Level.SEVERE, null, ex);
        }
        startTime = System.currentTimeMillis();
        buttons = new JButton[16][30];
        GamePanel.setSize(mf.getFrameWidth(),mf.getFrameHeight());
        this.setSize(mf.getFrameWidth(), mf.getFrameHeight()+30);
        initFieldButtons();
        FirstPanel.setVisible(false);
        GamePanel.setVisible(true);
    }//GEN-LAST:event_HardActionPerformed

    private void EasyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EasyActionPerformed
        mf = new MineField(9,9,10);
        totalButtons = 71;
        file = new File("resources\\bestTimes\\low.txt");
        try 
        {
            readHighScore(file);
        } catch (FileNotFoundException ex) 
        {
            Logger.getLogger(MineSweeper.class.getName()).log(Level.SEVERE, null, ex);
        }
        startTime = System.currentTimeMillis();
        buttons = new JButton [9][9];
        GamePanel.setSize(mf.getFrameWidth(),mf.getFrameHeight());
        this.setSize(mf.getFrameWidth(), mf.getFrameHeight()+30);
        initFieldButtons();
        FirstPanel.setVisible(false);
        GamePanel.setVisible(true);
    }//GEN-LAST:event_EasyActionPerformed

    private void MediumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MediumActionPerformed
        mf = new MineField(16,16,40);
        totalButtons = 216;
        file = new File("resources\\bestTimes\\Medium.txt");
        try 
        {
            readHighScore(file);
        } catch (FileNotFoundException ex) 
        {
            Logger.getLogger(MineSweeper.class.getName()).log(Level.SEVERE, null, ex);
        }
        startTime = System.currentTimeMillis();
        buttons = new JButton[16][16];
        GamePanel.setSize(mf.getFrameWidth(),mf.getFrameHeight());
        this.setSize(mf.getFrameWidth(), mf.getFrameHeight()+10);
        initFieldButtons();
        FirstPanel.setVisible(false);
        GamePanel.setVisible(true);
    }//GEN-LAST:event_MediumActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MineSweeper.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MineSweeper.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MineSweeper.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MineSweeper.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MineSweeper().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel DifficultyLabel;
    private javax.swing.JPanel DifficultyPanel;
    private javax.swing.JButton Easy;
    private javax.swing.JPanel FirstPanel;
    private javax.swing.JPanel GamePanel;
    private javax.swing.JButton Hard;
    private javax.swing.JButton Medium;
    private javax.swing.JLabel by;
    private javax.swing.JPanel fieldPanel;
    private javax.swing.JLabel highName;
    private javax.swing.JLabel highScore;
    private javax.swing.JLabel highScoreLabel;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel mineSweeperLabel;
    // End of variables declaration//GEN-END:variables

}
