import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javax.print.attribute.standard.MediaSize.Other;

import javafx.application.Platform;
import javafx.scene.image.Image;

public class TowerDefenseController {

	private TowerDefenseModel model;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private TDNetworkMessage otherMessage;
	private boolean isTurn = true;
	private boolean isMultiplayer = false;
	private boolean isClient = false;
	private int curTowerType = 0;
	private Image image = null;
	
	public TowerDefenseController(TowerDefenseModel model) {
		this.model = model;
	}
	
	/**
	 * This method initializes the data streams for network communications
	 * 
	 * This method initializes the ObjectOutputStream and ObjectInputSteam used
	 * to pass data through the network
	 * 
	 * @param socket: A socket from the current server connection
	 */
	public void initStreams(Socket socket) {
		 try {
			this.oos = new ObjectOutputStream(socket.getOutputStream());
			this.ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
		}
	}
	
	/**
	 * This method starts the turn of the current object
	 * 
	 * This method starts the current turn of an object by starting the thread that hold ois.readObject().
	 * We use a new thread because this is a blocking call, and it would cause the GUI to freeze. We also use
	 * Platfrom.runLater() to send GUI update commands to the GUI, which is the data from the opponents turn.
	 * 
	 * 
	 */
	public void startListening() {
		// creates a new thread to run ois.readObject()
		Thread inputThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					otherMessage = null;
					try {
						//System.out.println(isTurn);
						Object other = ois.readObject();
						// after reading the object, it sends an update event to the main thread to update the gui
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								if(other instanceof TDNetworkMessage) {
									otherMessage = (TDNetworkMessage) other;
								} else if (other instanceof ArrayList<?>) {
									model.setEnemies((ArrayList<Enemy>)other);
									return;
								}
								if(otherMessage.isPlaying()) {
									startRound();
								} else if(otherMessage.isPuased()) {
									changePaused();
								} else if(otherMessage.isRemoving()) {
									model.removeTowerNetwork(otherMessage.getRow(), otherMessage.getColumn());
								} else if(otherMessage.getTower() == 0) {
									image = new Image(new BirdPersonTower().getTowerPic());
									model.addTower(new BirdPersonTower(), otherMessage.getRow(), otherMessage.getColumn());
								} else if (otherMessage.getTower() == 1) {
									image = new Image(new JerryTower().getTowerPic());
									model.addTower(new JerryTower(), otherMessage.getRow(), otherMessage.getColumn());
								} else if (otherMessage.getTower() == 2) {
									image = new Image(new MeeseeksTower().getTowerPic());
									model.addTower(new MeeseeksTower(), otherMessage.getRow(), otherMessage.getColumn());
								} else if (otherMessage.getTower() == 3) {
									image = new Image(new MortyTower().getTowerPic());
									model.addTower(new MortyTower(), otherMessage.getRow(), otherMessage.getColumn());
								} else if (otherMessage.getTower() == 4) {
									image = new Image(new RickTower().getTowerPic());
									model.addTower(new RickTower(), otherMessage.getRow(), otherMessage.getColumn());
								} else {
									image = new Image(new SquanchyTower().getTowerPic());
									model.addTower(new SquanchyTower(), otherMessage.getRow(), otherMessage.getColumn());
								}
								// after finishing getting the opponets data, it is the current objects turn
								setTurn(true);
								//startListening();
							}
						});
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SocketException e) {
						System.exit(1);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		inputThread.start();
	}
	
	
	public void listenForMap() {
		try {
			otherMessage = (TDNetworkMessage) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(otherMessage.getMap() == 1) {
			setRoad(new Road1());
		} else {
			setRoad(new Road2());
		}
	}
	
	public void listenForPlay() {
		Thread inputThread = new Thread(new Runnable() {
			Object other = null;
			@Override
			public void run() {
				try {
					other = ois.readObject();
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
				if (other instanceof TDNetworkMessage) {
					otherMessage = (TDNetworkMessage) other;
					if(otherMessage.isPlaying()) {
						startRound();
					} else {
						run();
					}
				} else {
					run();
				}
			}
		});
		inputThread.start();
	}
	
	public void sendMap(TDNetworkMessage message) {
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendPlay() {
		System.out.print("Here1");
		try {
			oos.writeObject(new TDNetworkMessage(true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendEnimies() {
		try {
			oos.writeObject(model.getEnemies());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setModel(TowerDefenseModel model) {
		this.model = model;
	}
	
	public TowerDefenseModel getModel() {
		return model;
	}
	
	public void setTowerType(int curTower) {
		this.curTowerType = curTower;
	}
	
	public void setRoad(Road currRoad) {
		model.setRoad(currRoad);
	}
	
	/**
	 * This method returns a boolean indicating if it is its turn
	 * 
	 * @return A boolean indicating if it is its turn
	 */
	public boolean getTurn() {
		return this.isTurn ;
	}
	
	/**
	 * This method sets the turn of the current object
	 * 
	 * @param isTurn: A boolean represting the current turn of the object
	 */
	public void setTurn(boolean isTurn) {
		this.isTurn = isTurn;
	}
	
	public void setMulitplayer(boolean isMultiplayer) {
		this.isMultiplayer = isMultiplayer;
	}
	
	public Road getRoad() {
		return model.getRoad();
	}
	
	public Tower[][] getTowerMap(){
		return model.getTowerMap();
	}
	
	public void startRound() {
		model.startRound();
	}
	
	public Image getCurTower() {
		return this.image;
	}
	
	public void addTower(Tower currTowerClicked, double mouseX, double mouseY) {
		//System.out.println("Here1" + isTurn);
		if(!isTurn) {
			return;
		}
		if((getMoney() - currTowerClicked.getCost()) < 0) {
			return;
		}
		int row = 0;
		int col = 0;
		
		int lb;
		int ub;
		
		for (int i = 0; i < 15; i++) {
			lb = 47 * i;
			ub = 47 * (i + 1);
			if (mouseX >= lb && mouseX < ub) {
				col = i;
			}
			
			if (mouseY >= lb && mouseY < ub && i < 13) {
				row = i;
			}
		}
		if(!isClient && isMultiplayer) {
			if(col < 8) {
				return;
			}
				
		} else if (isClient && isMultiplayer) {
			if(col > 7) {
				return;
			}
		}
		this.model.spendMoney(currTowerClicked.getCost());
		model.addTower(currTowerClicked, row, col);
		if(isMultiplayer) {
			try {
				oos.writeObject(new TDNetworkMessage(row, col, curTowerType));
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.isTurn = false;
		}
	}
	
	public void sellTower(double mouseX, double mouseY) {
		int row = 0;
		int col = 0;
		
		int lb;
		int ub;
		
		for (int i = 0; i < 15; i++) {
			lb = 47 * i;
			ub = 47 * (i + 1);
			if (mouseX >= lb && mouseX < ub) {
				col = i;
			}
			
			if (mouseY >= lb && mouseY < ub && i < 13) {
				row = i;
			}
		}
		if(!isClient && isMultiplayer) {
			if(col < 8) {
				return;
			}
				
		} else if (isClient && isMultiplayer) {
			if(col > 7) {
				return;
			}
		}
		if(this.model.towerAtPosition(row, col)) {
			this.model.removeTower(this.model.getTowerAtPost(row, col), row, col);
		}
		if(isMultiplayer) {
			try {
				TDNetworkMessage temp = new TDNetworkMessage(row, col, curTowerType);
				temp.setRemove(true);
				oos.writeObject(temp);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//this.isTurn = false;
		}
	}
	
	public void increaseGameSpeed() {
		model.increaseGameSpeed();
	}
	
	public void changePaused() {
		model.changePaused();
	}
	
	public void findTowerAtPosition(int row, int col) {
		this.model.towerAtPosition(row, col);
	}
	
	
	public String getGamePhase() {
		return model.getGamePhase();
	}
	
	public void setIsClient(boolean isClient) {
		this.isClient = isClient;
	}
	
	public int getMoney() {
		return this.model.getMoney();
	}
	
	public int getHealth() {
		return this.model.getHealth();
	}
	
	public void addAttackMoney() {
		this.model.addAttackMoney();
	}
	
	public void takeHealth() {
		this.model.takeDamage();
	}
	
	public void towerAttack() {
		this.model.towerAttack();
	}
	
	public Integer getRound() {
		return this.model.getRound();
	}

	public void sendPause() {
		TDNetworkMessage temp = new TDNetworkMessage();
		temp.setPaused(true);
		try {
			oos.writeObject(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
