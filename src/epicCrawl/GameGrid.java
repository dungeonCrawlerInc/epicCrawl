package epicCrawl;

//Imports
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;


public class GameGrid extends JPanel{
	private boolean _viewMode = false;

	private int _maxRows = 50, _maxColumns = 50; // Size of the available grid
	private int _rows = 15, _columns = 15;
	private int playerX, playerY, newX, newY;
    public int _characterLevel, recW, recH; // Draw the grid
    public String _characterName;

    private SoundManager _soundManager;

	private ArrayList<GridObject>[][] _grid; // Grid for current map
    GridObject grassSquare, dirtSquare, darkWoodFloorSquare, lightWoodFloorSquare, stoneWallSquare,
		voidSquare, mediumWoodFloorSquare, redWoodFloorSquare, treeAndShrubNorSBorderSquare,
        treeSquare, floorStone, wallStoneTorch, woodWallSquare;
	BufferedImage playerImage;
	private String curLevel;

    public class SearchNode{
        int xLoc;
        int yLoc;
        SearchNode parent;

        public SearchNode(int x, int y, SearchNode p){
            xLoc = x;
            yLoc = y;
            parent = p;
        }
    }

	// Room constructor
	@SuppressWarnings("unchecked")
	public GameGrid(){
        _characterLevel = 1;
        _soundManager = new SoundManager();

        grassSquare = new GridObject("grass", true);
		dirtSquare = new GridObject("dirt", true);
		darkWoodFloorSquare = new GridObject("woodFloorDark", true);
        mediumWoodFloorSquare = new GridObject("woodFloorMedium", true);
        redWoodFloorSquare = new GridObject("woodFloorRed", true);
		lightWoodFloorSquare = new GridObject("woodFloorLight", true);
		stoneWallSquare = new GridObject("wallStone", false);
		voidSquare = new GridObject("void", false);
        treeAndShrubNorSBorderSquare = new GridObject("treeAndShrubNorSBorder", false);
        treeSquare = new GridObject("tree", false);
        floorStone = new GridObject("floorStone", true);
        wallStoneTorch = new GridObject("wallStoneTorch", false);
        woodWallSquare = new GridObject("wallWood", false);
		
		setFocusable(true); // Not needed?
		setUpKeyBindings();

		_grid = (ArrayList<GridObject>[][])new ArrayList[_maxRows][_maxColumns]; // new ArrayList<GridObject>()[_maxRows][_maxColumns];
		
		for(int i = 0; i < _maxRows; ++i){
			for(int j = 0; j < _maxColumns; ++j){
				_grid[i][j] = new ArrayList<GridObject>();
			}
		}

        curLevel = "Town.txt";
		loadLevel(curLevel);

		playerX = 3;
		playerY = 2;

		BufferedImage bufImage = null;

		InputStream input = this.getClass().getClassLoader().getResourceAsStream("Images/" + "characterArmor.png");
		try{
			bufImage = ImageIO.read(input);
		}catch (IOException e){System.err.println("Failed to load image for grid square.");}

		playerImage = bufImage;

        setUpMouseListener();
	}

    private void setUpMouseListener(){
        this.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent me){
                int xLoc = me.getX() / recW, yLoc = me.getY() / recH;
                System.out.println("Clicked on x: " + xLoc + " y: " + yLoc);

                int drawPlayerX = 7, drawPlayerY = 7;

                if(playerX < 8)
                    drawPlayerX = playerX;
                if(playerY < 8)
                    drawPlayerY = playerY;
                if(playerX > _maxColumns - 8)
                    drawPlayerX = playerX - (_maxRows - _rows);
                if(playerY > _maxRows - 8)
                    drawPlayerY = playerY - (_maxColumns - _columns);

                int newX = playerX + xLoc - drawPlayerX, newY = playerY + yLoc -drawPlayerY;

                System.out.println("Top object name: " + getTopObject(newX, newY).getName());

                if(getTopObject(newX, newY) instanceof ClickableObject){
                    System.out.println("Found clickable object!");
                    ClickableObject clickableObject = ((ClickableObject)getTopObject(newX, newY));
                    clickableObject.click(playerX, playerY);

                    if(clickableObject instanceof Enemy && ((Enemy)clickableObject).wasAttacked()){
                        //_soundManager.playSound();

                        if(!((Enemy)clickableObject).isAlive()){
                            _grid[newX][newY].remove(clickableObject);
                            repaint();
                        }
                    }
                }
            }
        });
    }
	
	public void loadLevel(String levelFileName){
		levelFileName = "Levels/" + levelFileName;
		InputStream inStream = getClass().getClassLoader().getResourceAsStream(levelFileName);
		Scanner lineScanner = new Scanner(inStream);
		Scanner stringScanner = null;
		int curRow = 0, curCol = 0;
		String curString;

        for(int i = 0; i < _maxRows; ++i){
            for(int j = 0; j < _maxColumns; ++j){
                _grid[i][j].clear();
            }
        }

		while(lineScanner.hasNextLine()){
			stringScanner = new Scanner(lineScanner.nextLine());
			
			while(stringScanner.hasNext()){
				curString = stringScanner.next();
				
				// Inner while loop if there are multiple images in string ex: Dirt.png,chest.png
				ArrayList<String> wordList = new ArrayList<String>(Arrays.asList(curString.split(",")));

				for(String cur: wordList){
					if(cur.equals("dirt.png"))
						_grid[curCol][curRow].add(dirtSquare);
                    else if(cur.equals("bed.png"))
                        _grid[curCol][curRow].add(new GridObject("bed", false));
                    else if(cur.equals("rock.png"))
                        _grid[curCol][curRow].add(new GridObject("rock", false));
                    else if(cur.equals("bedWithCat.png"))
                        _grid[curCol][curRow].add(new GridObject("bedWithCat", false));
					else if(cur.equals("grass.png"))
						_grid[curCol][curRow].add(grassSquare);
                    else if(cur.equals("woodFloorMedium.png"))
                        _grid[curCol][curRow].add(mediumWoodFloorSquare);
                    else if(cur.equals("woodFloorRed.png"))
                        _grid[curCol][curRow].add(redWoodFloorSquare);
					else if(cur.equals("doorInsideToInside.png"))
						_grid[curCol][curRow].add(new GridObject("doorInsideToInside", true));
                    else if(cur.equals("doorInsideToOutside.png"))
                        _grid[curCol][curRow].add(new GridObject("doorInsideToOutside", true));
                    else if(cur.equals("doorOutsideToInside.png"))
                        _grid[curCol][curRow].add(new GridObject("doorOutsideToInside", true));
					else if(cur.equals("woodFloorLight.png"))
						_grid[curCol][curRow].add(lightWoodFloorSquare);
					else if(cur.equals("woodFloorDark.png"))
						_grid[curCol][curRow].add(darkWoodFloorSquare);
					else if(cur.equals("wallStone.png"))
						_grid[curCol][curRow].add(stoneWallSquare);
                    else if(cur.equals("wallWood.png"))
                        _grid[curCol][curRow].add(woodWallSquare);
					else if(cur.equals("chairLeftFacing.png"))
						_grid[curCol][curRow].add(new GridObject("chairLeftFacing", false));
					else if(cur.equals("chairRightFacing.png"))
						_grid[curCol][curRow].add(new GridObject("chairRightFacing", false));
					else if(cur.equals("chest.png"))
						_grid[curCol][curRow].add(new GridObject("chest", false));
					else if(cur.equals("tablewithfood.png"))
						_grid[curCol][curRow].add(new GridObject("tablewithfood", false));
					else if(cur.equals("void.png"))
						_grid[curCol][curRow].add(voidSquare);
					else if(cur.equals("table.png"))
						_grid[curCol][curRow].add(new GridObject("table", false));
					else if(cur.equals("portal.png"))
						_grid[curCol][curRow].add(new GridObject("portal", true));
                    else if(cur.equals("tableLong.png"))
                        _grid[curCol][curRow].add(new GridObject("tableLong", false));
                    else if(cur.equals("tree.png"))
                        _grid[curCol][curRow].add(treeSquare);
                    else if(cur.equals("treeAndShrubNorSBorder.png"))
                        _grid[curCol][curRow].add(treeAndShrubNorSBorderSquare);
                    else if(cur.equals("houser0c0.png"))
                        _grid[curCol][curRow].add(new GridObject("houser0c0", false));
                    else if(cur.equals("houser0c1.png"))
                        _grid[curCol][curRow].add(new GridObject("houser0c1", false));
                    else if(cur.equals("houser0c2.png"))
                        _grid[curCol][curRow].add(new GridObject("houser0c2", false));
                    else if(cur.equals("houser0c3.png"))
                        _grid[curCol][curRow].add(new GridObject("houser0c3", false));
                    else if(cur.equals("houser0c4.png"))
                        _grid[curCol][curRow].add(new GridObject("houser0c4", false));
                    else if(cur.equals("houser1c0.png"))
                        _grid[curCol][curRow].add(new GridObject("houser1c0", false));
                    else if(cur.equals("houser1c1.png"))
                        _grid[curCol][curRow].add(new GridObject("houser1c1", false));
                    else if(cur.equals("houser1c2.png"))
                        _grid[curCol][curRow].add(new GridObject("houser1c2", false));
                    else if(cur.equals("houser1c3.png"))
                        _grid[curCol][curRow].add(new GridObject("houser1c3", false));
                    else if(cur.equals("houser1c4.png"))
                        _grid[curCol][curRow].add(new GridObject("houser1c4", false));
                    else if(cur.equals("houser2c0.png"))
                        _grid[curCol][curRow].add(new GridObject("houser2c0", false));
                    else if(cur.equals("houser2c1.png"))
                        _grid[curCol][curRow].add(new GridObject("houser2c1", false));
                    else if(cur.equals("houser2c3.png"))
                        _grid[curCol][curRow].add(new GridObject("houser2c3", false));
                    else if(cur.equals("houser2c4.png"))
                        _grid[curCol][curRow].add(new GridObject("houser2c4", false));
                    else if(cur.equals("floorStone.png"))
                        _grid[curCol][curRow].add(floorStone);
                    else if(cur.equals("wallStoneTorch.png"))
                        _grid[curCol][curRow].add(wallStoneTorch);
                    // Enemies
                    else if(cur.equals("enemyRat.png"))
                        _grid[curCol][curRow].add(new Enemy("enemyRat", curCol, curRow, 0, 10));
                    else if(cur.equals("enemySkeleton.png"))
                        _grid[curCol][curRow].add(new Enemy("enemySkeleton", curCol, curRow, 1, 20));
                    else if(cur.equals("enemyBull.png"))
                        _grid[curCol][curRow].add(new Enemy("enemyBull", curCol, curRow, 2, 30));
                    // NPCs
                    else if(cur.equals("girl.png"))
                        _grid[curCol][curRow].add(new NPC("girl", curCol, curRow));
		            else if(cur.equals("girl0.png"))
                        _grid[curCol][curRow].add(new NPC("girl0", curCol, curRow));
		            else if(cur.equals("girl1.png"))
                        _grid[curCol][curRow].add(new NPC("girl1", curCol, curRow));
		            else if(cur.equals("girl2.png"))
                        _grid[curCol][curRow].add(new NPC("girl2", curCol, curRow));
                    else if(cur.equals("villager1.png"))
                        _grid[curCol][curRow].add(new NPC("villager1", curCol, curRow));
		            else if(cur.equals("barkeep.png"))
                        _grid[curCol][curRow].add(new NPC("barkeep", curCol, curRow));
				}
				
				++curCol;
			}
			
			curCol = 0;
			++curRow;
		}

		lineScanner.close();
		stringScanner.close();
	}

    private boolean isMovableSpace(int y, int x){
        if(x < 0 || x > _maxColumns -1 || y < 0 || y > _maxRows - 1)
            return false;

        return getTopObject(x, y).isPassable();
    }

    private void preLoadLevel(String str, int x, int y){
        curLevel = str;
        loadLevel(curLevel);
        playerX = x;
        playerY = y;
        repaint();
    }

    private boolean nextToPlayer(int x, int y){
        int deltaX = Math.abs(playerX - x), deltaY = Math.abs(playerY - y);

        return (deltaX < 2 && deltaY < 2 && (deltaX == 0 || deltaY == 0));
    }

    private void moveNPC(NPC curNPC){
        int x = curNPC.getXLoc(), y = curNPC.getYLoc(), counter = 0, newY, newX;
        boolean validMove = false;
        Random rand = new Random();

        while(!validMove && counter < 3){
            newY = 0;
            newX = rand.nextInt(3) - 1;

            if(newX == 0)
                newY = rand.nextInt(3) - 1;

            newX += curNPC.getXLoc();
            newY += curNPC.getYLoc();

            if((newX != playerX || newY != playerY) && isMovableSpace(newY, newX)){
                if(newX == curNPC.getXLoc() || newY == curNPC.getYLoc()){
                    validMove = true;
                    curNPC.setLoc(newX, newY);
                    _grid[x][y].remove(curNPC);
                    _grid[newX][newY].add(curNPC);
                }
            }
            ++counter;
        }
    }

    private boolean pVisited(int x, int y, ArrayList<Point> ps){
        for(Point curP: ps){
            if(curP.getX() == x && curP.getY() == y)
                return true;
        }

        return false;
    }

    // Performs a breadth first search, Finds quickest path from enemy
    // to player's current position.  Then moves enemy to the first move
    // in that path.
    // If this is really slow for any reason will be changing so enemies don't
    // go after player unless within view or smaller range.
    private void moveEnemy(Enemy curEnemy){
        // if next to enemy then just print out something
        int enemyX = curEnemy.getXLoc(), enemyY = curEnemy.getYLoc();

        if(Math.abs(playerX - enemyX) > 4 || Math.abs(playerY - enemyY) > 4) return;

        if(nextToPlayer(enemyX, enemyY)){
            System.out.println("Enemy next to player");
            // Enemy should attack player
            return;
        }

        SearchNode curNode = new SearchNode(enemyX, enemyY, null);

        ArrayList<Point> visitedPs = new ArrayList<Point>();
        ArrayList<SearchNode> nodes = new ArrayList<SearchNode>();
        nodes.add(curNode);

        int counter = 0;

        while(true){
            if(nodes.size() == 0){
                System.out.println("Ran out of nodes to move enemy");
                return;
            }

            curNode = nodes.remove(0);

            int nodeX = curNode.xLoc, nodeY = curNode.yLoc;
            visitedPs.add(new Point(nodeX, nodeY));

            if(nextToPlayer(nodeX, nodeY)){
                while(curNode.parent.parent != null)
                    curNode = curNode.parent;

                curEnemy.setLoc(curNode.xLoc, curNode.yLoc);
                _grid[enemyX][enemyY].remove(curEnemy);
                _grid[curNode.xLoc][curNode.yLoc].add(curEnemy);

                System.out.println("counter: " + counter);
                return;
            }

            if(!pVisited(nodeX, nodeY + 1, visitedPs)){
                if(getTopObject(nodeX, nodeY + 1).isPassable()){ // check up
                    nodes.add(new SearchNode(nodeX, nodeY + 1, curNode));
                    ++counter;
                }
            }
            if(!pVisited(nodeX, nodeY - 1, visitedPs)){
                if(getTopObject(nodeX, nodeY - 1).isPassable()){ // check down
                    nodes.add(new SearchNode(nodeX, nodeY - 1, curNode));
                    ++counter;
                }
            }
            if(!pVisited(nodeX + 1, nodeY, visitedPs)){
                if(getTopObject(nodeX + 1, nodeY).isPassable()){ // check right
                    nodes.add(new SearchNode(nodeX + 1, nodeY, curNode));
                    ++counter;
                }
            }
            if(!pVisited(nodeX - 1, nodeY, visitedPs)){
                if(getTopObject(nodeX - 1, nodeY).isPassable()){ // check left
                    nodes.add(new SearchNode(nodeX - 1, nodeY, curNode));
                    ++counter;
                }
            }

            // possibly store all visited points and don't check against those
            // handle case where player is completely surrounded
        }
    }

    private void moveLivingObjects(){
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        ArrayList<NPC> npcs = new ArrayList<NPC>();

        for(int x = 0; x < _maxColumns; ++x){ // Look through grid for moveable objects
            for(int y = 0; y < _maxRows; ++y){
                if(getTopObject(x, y) instanceof Enemy) // Store all enemies on grid
                    enemies.add((Enemy)getTopObject(x, y));
                else if(getTopObject(x, y) instanceof NPC) // Store all NPCs on grid
                    npcs.add((NPC)getTopObject(x, y));
            }
        }

        for(Enemy curEnemy: enemies) // Move all enemies
            moveEnemy(curEnemy);

        for(NPC curNPC: npcs) // Move all NPCs
            moveNPC(curNPC);
    }

	private void movePlayer(String direction){
		if(direction.equals("left")){
			newX = playerX - 1;
			newY = playerY;
		}
		else if(direction.equals("up")){
			newX = playerX;
			newY = playerY - 1;
		}
		else if(direction.equals("right")){
			newX = playerX + 1;
			newY = playerY;
		}
		else if(direction.equals("down")){
			newX = playerX;
			newY = playerY + 1;
		}

        if(!isMovableSpace(newY, newX)) return; // Move is blocked
		
		playerX = newX;
		playerY = newY;
		
		if(curLevel.equals("Town.txt") && playerX == 25 && playerY == 11) // Temporary portal jump
            preLoadLevel("House1.txt", 20, 24);
		else if(curLevel.equals("House1.txt") && playerX == 20 && playerY == 25)
            preLoadLevel("Town.txt", 25, 12);
        else if(curLevel.equals("Town.txt") && playerX == 43 && playerY == 14)
            preLoadLevel("House2.txt", 20, 24);
        else if(curLevel.equals("House2.txt") && playerX == 20 && playerY == 25)
            preLoadLevel("Town.txt", 43, 15);
        else if(curLevel.equals("Town.txt") && playerX == 39 && playerY == 37)
            preLoadLevel("Dungeon1.txt", 3, 6);
        else if(curLevel.equals("Dungeon1.txt") && playerX == 45 && playerY == 7)
            preLoadLevel("Town.txt", 27, 46);
        else{
            moveLivingObjects();
		    repaint();
        }
	}

    // Returns the object on top at the grid location passed in
    private GridObject getTopObject(int row, int column){
        return _grid[row][column].get(_grid[row][column].size() - 1);
    }

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g); // Important to call super class method
		g.clearRect(0, 0, getWidth(), getHeight()); // Clear the board

		if(!_viewMode){ // 15x15 view mode
			recW = getWidth() / _columns; // Draw the grid
			recH = getHeight() / _rows;

			for(int i = 0; i <  _rows; i++){ // Determine what to draw for each grid square on grid
				int viewX = (playerX - (7) + i);
				int xCord = i * recW; // Upper left corner of this terrain rect

				for(int j = 0; j < _columns; j++){
					int yCord = j * recH;
					int viewY = (playerY - (7) + j);

					if(playerX < 8)
						viewX = i;
					if(playerY < 8)
						viewY = j;
					if(playerX > _maxColumns - 8)
						viewX = _maxColumns - _columns + i;
					if(playerY > _maxRows - 8)
						viewY = _maxRows - _rows + j;

					for(int z = 0; z < _grid[viewX][viewY].size(); ++z)
						g.drawImage(_grid[viewX][viewY].get(z).getImage(), xCord, yCord, recW, recH, null);
				}
			}
			
			int drawPlayerX = 7, drawPlayerY = 7;
			
			if(playerX < 8)
				drawPlayerX = playerX;
			if(playerY < 8)
				drawPlayerY = playerY;
			if(playerX > _maxColumns - 8)
				drawPlayerX = playerX - (_maxRows - _rows);
			if(playerY > _maxRows - 8)
				drawPlayerY = playerY - (_maxColumns - _columns);
	
			g.drawImage(playerImage, drawPlayerX * recW, drawPlayerY * recH, recW, recH, null);
		}
		else{ // Full view mode
			int recW = getWidth() / _maxRows; // Draw the grid
			int recH = getHeight() / _maxColumns;

			for(int i = 0; i <  _maxRows; i++){ // Determine what to draw for each grid square on grid
				int xCord = i * recW; // Upper left corner of this terrain rect

				for(int j = 0; j < _maxColumns; j++){
					int yCord = j * recH;

					for(int z = 0; z < _grid[i][j].size(); ++z)
						g.drawImage(_grid[i][j].get(z).getImage(), xCord, yCord, recW, recH, null);
				}
			}

			g.drawImage(playerImage, playerX * recW, playerY * recH, recW, recH, null);
		}
	}

	private void setUpKeyBindings(){
        Action leftAction = new LeftAction();
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('a'), "doLeftAction");  
		getActionMap().put( "doLeftAction", leftAction );

        Action upAction = new UpAction();
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('w'), "doUpAction");  
		getActionMap().put( "doUpAction", upAction );

        Action rightAction = new RightAction();
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('d'), "doRightAction");  
		getActionMap().put( "doRightAction", rightAction );

        Action downAction = new DownAction();
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('s'), "doDownAction");  
		getActionMap().put( "doDownAction", downAction );
	}
	
	class LeftAction extends AbstractAction{
		public void actionPerformed(ActionEvent ae){movePlayer("left");}
	}
	
	class UpAction extends AbstractAction{
		public void actionPerformed(ActionEvent ae){movePlayer("up");}
	}
	
	class RightAction extends AbstractAction{
		public void actionPerformed(ActionEvent ae){movePlayer("right");}
	}
	
	class DownAction extends AbstractAction{
		public void actionPerformed(ActionEvent ae){movePlayer("down");}
	}

	public void setViewMode(boolean x){_viewMode = x;}
	
	public boolean getViewMode(){return _viewMode;}

    public void setCharacterName(String characterName){_characterName = characterName;}
	
	public static void main(java.lang.String[] args){
		JFrame frame = new JFrame();
		
		final GameGrid grid = new GameGrid();
        grid.setCharacterName("Hero");
		grid.setVisible(true);
		grid.repaint();
		
		JPanel mainPanel = new JPanel();
		mainPanel.setVisible(true);
		mainPanel.setLayout(new BorderLayout());
		
		JPanel devPanel = new JPanel();
		devPanel.setVisible(true);
		devPanel.setLayout(new FlowLayout());
		
		JButton viewModeButton = new JButton("Toggle View Mode");
		
		viewModeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent ae){
				if(grid.getViewMode())
					grid.setViewMode(false);
				else
					grid.setViewMode(true);
				grid.repaint();
			}
		});
		
		devPanel.add(viewModeButton);
		
		mainPanel.add(grid, BorderLayout.CENTER);
		mainPanel.add(devPanel, BorderLayout.NORTH);
		
		frame.add(mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close on exit
		frame.setTitle("Epic Crawl"); // Game title
		frame.setSize(700,700); // Size of play window on start, later support changing screen size
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.repaint();
	}
}