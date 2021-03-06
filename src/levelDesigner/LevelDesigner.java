//TODO: Make mousePressed and mouseDragged call the same function.

package levelDesigner;

// ----------------TEST-------------------

/*
 * Add in different combo boxes based on object
 * 		terrain
 * 		object
 * 		living thing
 *
 * Add in option to change numOfRows/columns
 *
 * Add in ability to place objects on top of others
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.*;

@SuppressWarnings("serial")
public class LevelDesigner extends JPanel implements ButtonListener{
	private final int SIZE = 12;
	private Grid[][] theView;
	private ButtonPanel bp;
	public static int val;
    private int numOfRows, numOfCols;
	private JPanel gridPanel;
	private JFrame frame;
	private ImageIcon optionPaneIcon;
	String[] selectedTilesToPaint = {"void.png", "dirt.png", "grass.png", "floorStone.png",
            "woodFloorDark.png","woodFloorLight.png", "woodFloorMedium.png",
            "woodFloorRed.png", "wallStone.png", "wallStoneTorch.png", "wallWood.png",
            "houser0c0.png", "houser0c1.png", "houser0c2.png", "houser0c3.png",
            "houser0c4.png", "houser1c0.png", "houser1c1.png", "houser1c2.png",
            "houser1c3.png", "houser1c4.png", "houser2c0.png", "houser2c1.png",
            "houser2c3.png", "houser2c4.png", "doorInsideToInside.png",
            "doorInsideToOutside.png", "doorOutsideToInside.png", "portal.png",
            "bed.png", "bedWithCat.png", "chairLeftFacing.png", "chairRightFacing.png",
            "chest.png", "rock.png", "table.png", "tableLong.png", "tablewithfood.png",
            "tree.png", "treeAndShrubNorSBorder.png", "characterArmor.png",
            "characterNoArmor.png", "characterSwordAndShield.png", "girl.png",
            "girl0.png", "girl1.png", "girl2.png", "barkeep.png",
            "villager1.png", "enemyBull.png", "enemySkeleton.png", "enemyRat.png"};
    private int paintBrushSize = 0; // 0 Indicates one tile to be painted

    public int getNumOfRows() {
        return numOfRows;
    }

    public int getNumOfCols() {
        return numOfCols;
    }

	public LevelDesigner(int r, int c){
		frame = new JFrame();
		setVisible(true);
		setLayout(new BorderLayout());

		BufferedImage imageToPaint = null;
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("Images/epicCrawlerIcon.png");
		try{
			imageToPaint = ImageIO.read(input);
		}
		catch(IOException e){System.err.println("Failed to load main screen image");}
		optionPaneIcon = new ImageIcon(imageToPaint);

		numOfRows = r;
		numOfCols = c;
		theView = new Grid[r][c];   
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.setColumns(numOfCols);
		gridLayout.setRows(numOfRows);

		gridPanel = new JPanel();
		gridPanel.setVisible(true);
		gridPanel.setBackground(Color.BLACK);
		gridPanel.setPreferredSize(getMaximumSize());
		gridPanel.setLayout(gridLayout);

		for(int i = 0; i < r; i++){
			for(int j = 0; j < c; j++){
				theView[i][j] = new Grid(i, j, SIZE, selectedTilesToPaint);
				gridPanel.add(theView[i][j]);
			}
		}

		bp = new ButtonPanel(selectedTilesToPaint, this);

		this.add(bp, BorderLayout.SOUTH);
		this.add(gridPanel, BorderLayout.CENTER);

		frame.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me) {
                if(bp.fillCheckBox.isSelected())
                {
                    fill(me);
                }
                else
                {
                    paintBrushSize = (Integer)bp.iconPaintSizeCombo.getSelectedItem();

                    if(((String)bp.brushShapeCombo.getSelectedItem()).equals("Square Brush"))
                    {
                        for(int i = 0; i < numOfRows; ++i){
                            for(int j = 0; j < numOfCols; ++j){
                                Point pointOfClickDrag = SwingUtilities.convertPoint(frame, me.getPoint(), theView[i][j]);

                                if (theView[i][j].contains(pointOfClickDrag)){
                                    for(int colClick = 0; colClick < paintBrushSize; colClick++){
                                        for(int rowClick = 0; rowClick < paintBrushSize; rowClick++){
                                            if((i+rowClick < numOfRows) && (j+colClick < numOfCols)){
                                                theView[i+rowClick][j+colClick].actionPerformed(null);
                                            }
                                        }
                                    }
                                    return;
                                }
                            }
                        }
                    }
                    else
                    {
                        System.out.println((String)bp.brushShapeCombo.getSelectedItem() + " is not supported yet!");
                    }
                }
            }
		});

		frame.addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent me) {
                paintBrushSize = (Integer)bp.iconPaintSizeCombo.getSelectedItem();

                if(((String)bp.brushShapeCombo.getSelectedItem()).equals("Square Brush"))
                {
                    for(int i = 0; i < numOfRows; ++i){
                        for(int j = 0; j < numOfCols; ++j){
                            Point pointOfClickDrag = SwingUtilities.convertPoint(frame, me.getPoint(), theView[i][j]);

                            if (theView[i][j].contains(pointOfClickDrag)){
                                for(int colClick = 0; colClick < paintBrushSize; colClick++){
                                    for(int rowClick = 0; rowClick < paintBrushSize; rowClick++){
                                        if((i+rowClick < numOfRows) && (j+colClick < numOfCols)){
                                            theView[i+rowClick][j+colClick].actionPerformed(null);
                                        }
                                    }
                                }
                                return;
                            }
                        }
                    }
                }
                else
                {
                    System.out.println((String)bp.brushShapeCombo.getSelectedItem() + " is not supported yet!");
                }
			}


		});



		Dimension dim = new Dimension((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(), 
				(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 30);
		frame.setPreferredSize(dim);
		frame.setVisible(true);
		frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		frame.setTitle("Epic Crawl - Main Menu");
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close on exit
		frame.repaint();
		frame.add(this);



	}

    public void fill(MouseEvent me)
    {
        //TODO: I need to know how to get the tile of a grid location
        for(int i = 0; i < numOfRows; ++i){
            for(int j = 0; j < numOfCols; ++j){
                Point pointOfClickDrag = SwingUtilities.convertPoint(frame, me.getPoint(), theView[i][j]);

                if (theView[i][j].contains(pointOfClickDrag)){

                }
            }
        }
    }

    //Increases the paintBrushSize by 1
    //Resetting it to 1, if it gets bigger than 10...
    public void increasePaintBrushSize(){
        this.paintBrushSize += 1;

        if(this.getPaintBrushSize() > 10){
            this.paintBrushSize = 0;
        }
    }
    

    public int getPaintBrushSize(){
        return this.paintBrushSize;
    }
    

	public String toString(){
		String s = "";
		for(int r = 0; r < numOfRows; r++){
			for(int c = 0; c < numOfCols; c++){
				for(String imageName: theView[r][c].gridImageNames){
					if(imageName.equals("null"))
						s = s + "void.png,";
					else
						s = s + imageName + ",";
				}
				
				s = s.substring(0, s.length() - 1) + " ";
			}

			s = s + "\n";
		}

		return s;
	}
	

	public void saveGame(){
		String levelName = (String)JOptionPane.showInputDialog(this, 
				"Enter level name.", "Save level", JOptionPane.INFORMATION_MESSAGE, optionPaneIcon, null, null);

        if(levelName.equals("null"))
            return;

		levelName += ".txt";

		PrintWriter p = null;
		try{
			p = new PrintWriter(new File(levelName));
		} catch (FileNotFoundException e1){}
		p.println( this );
		p.close();   

		try{
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter("LevelsList.txt", true));
			bufWriter.write("\n" + levelName);
			bufWriter.newLine();
			bufWriter.flush();
			bufWriter.close();
		}
		catch(IOException e){System.out.println("Error saving level to LevelsList.txt");}
	}

    //TODO: Note: this function failed on my Netbook
	public void loadGame(){
		ArrayList<String> savedLevelsList = new ArrayList<String>();
		try{
			BufferedReader bufReader = new BufferedReader(new FileReader("LevelsList.txt"));
			String curLine = "";

			while((curLine = bufReader.readLine()) != null){
				if(!curLine.equals(""))
					savedLevelsList.add(curLine);
			}

			bufReader.close();
		}
		catch(Exception e){
			System.err.println("Can't open file to load levels, " + e);
		}

		String[]savedLevels = new String[savedLevelsList.size()];
		for(int i = 0; i < savedLevelsList.size(); ++i){
			savedLevels[i] = savedLevelsList.get(i);
		}

		String levelFileName = (String)JOptionPane.showInputDialog(this,
				"Select level to load", "Load Level", JOptionPane.INFORMATION_MESSAGE, optionPaneIcon, savedLevels, null);
		
		FileReader fileReader = null;
		try{
			fileReader = new FileReader(levelFileName);
		} 
		catch(FileNotFoundException e1){}

		Scanner lineScanner = new Scanner(fileReader);
		Scanner wordScanner = null;
		int curRow = 0, curCol = 0;
		String curString = "";

		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		while(lineScanner.hasNextLine()){
			wordScanner = new Scanner(lineScanner.nextLine());

			while(wordScanner.hasNext()){
				curString = wordScanner.next();

				ArrayList<String> wordList = new ArrayList<String>(Arrays.asList(curString.split(",")));

				for(String cur: wordList){

					InputStream input = this.getClass().getClassLoader().getResourceAsStream("Images/" + cur);
					try{
						theView[curRow][curCol].gridImages.add(ImageIO.read(input));
						theView[curRow][curCol].gridImageNames.add(cur);
					}
					catch(Exception e){System.err.println("Failed to load image");}

				}

				++curCol;
			}
			
			curCol = 0;
			++curRow;
		}

		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		lineScanner.close();
		wordScanner.close();
		repaint();
	}

    //Increases the length of the square paint brush size by 1
    //If The size of the brush will reset back to 0, once it hits 10
    public int changePaintBrushSize(){
        increasePaintBrushSize();
        return this.getPaintBrushSize();
    }


    //Clears the entire level, as if we just opened the levelDesigner
    public void clearLevel()
    {
        int response = JOptionPane.showConfirmDialog(null, "Do you want to clear all tiles?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.NO_OPTION) {
            return; //User cancelled
        } else if (response == JOptionPane.CLOSED_OPTION) {
            return; //User cancelled
        } else if (response == JOptionPane.YES_OPTION) {
            for(int col = 0; col < numOfCols; col++){
                for(int row = 0; row < numOfRows; row++){
                    if((row < numOfRows) && (col < numOfCols)){
                        theView[row][col].actionPerformed(null);    //TODO: Instead of filling the entire board with the selected tile, we want to fill the entire board with the tile the program starts with. Unsure of name
                    }
                }
            }
        }
        return;
    }


	public void buttonPressed(int id){
		if(id == -1) // Save
			saveGame();
		else if(id == -2) // Load
			loadGame();
        else if(id == -3)   //Clear
            clearLevel();
		else
			val = id;
	}

	@SuppressWarnings("unused")
	public static void main(String arg[]){ 
	     LevelDesigner levelDesigner = new LevelDesigner(50, 50);
	}
}
