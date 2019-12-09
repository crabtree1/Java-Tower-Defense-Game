import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.application.Platform;

public class TowerDefenseController {

	private TowerDefenseModel model;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private TDNetworkMessage otherMessage;
	private boolean isTurn = true;
	private boolean isMultiplayer = false;
	
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
				otherMessage = null;
				try {
					System.out.println("Here");
					otherMessage = (TDNetworkMessage) ois.readObject();
					// after reading the object, it sends an update event to the main thread to update the gui
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							model.addTower(otherMessage.getTower(), otherMessage.getRow(), otherMessage.getColumn());
							// if the opponet won on the last turn, we show a lost message
							//if(model.hasWon() != 0) {
							//	view.showLost();
							//	controller.setTurn(false);
							// if the current object is a computer, we must run the computerTurn()
							//} else if(controller.isHuman == false) {
							//	controller.computerTurn();
							//}
						}
					});
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
				// after finishing getting the opponets data, it is the current objects turn
				setTurn(true);
			}
		});
		inputThread.start();
	}
	
	public void setModel(TowerDefenseModel model) {
		this.model = model;
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
	
	public void addTower(Tower currTowerClicked, double mouseX, double mouseY) {
		if((this.getMoney() - currTowerClicked.getCost()) < 0) {
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
		this.model.spendMoney(currTowerClicked.getCost());
		model.addTower(currTowerClicked, row, col);
		if(isMultiplayer) {
			System.out.print("Here");
			try {
				oos.writeObject(new TDNetworkMessage(row, col, currTowerClicked));
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.isTurn = false;
			startListening();
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
		if(this.model.towerAtPosition(row, col)) {
			this.model.removeTower(this.model.getTowerAtPost(row, col), row, col);
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
}
