/**
 * Road2 is a subclass of the Road class. This will specify a particular layout
 * for one of the game stages. 2s represent enemy portals, 1s are the path for
 * the enemies to follow, 0s are placeable squares for towers, and 3, 4, and
 * 5 represent squares for the start, pause and fast forward buttons
 * @author David Gonzales, Mario Verdugo, Luke Cernetic, Chris Crabtree
 *
 */

public class Road2 extends Road {

	/**
	 * Constructor for the Road2 class
	 */
	public Road2() {
		map = new int[13][15];
		
		int[] row0 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		int[] row1 = {0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0};
		int[] row2 = {0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0};
		int[] row3 = {0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0};
		int[] row4 = {0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0};
		int[] row5 = {2, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0};
		int[] row6 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0};
		int[] row7 = {2, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0};
		int[] row8 = {0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0};
		int[] row9 = {0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0};
		int[] row10 = {0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0};
		int[] row11 = {0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0};
		int[] row12 = {4, 3, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

		map[0] = row0;
		map[1] = row1;
		map[2] = row2;
		map[3] = row3;
		map[4] = row4;
		map[5] = row5;
		map[6] = row6;
		map[7] = row7;
		map[8] = row8;
		map[9] = row9;
		map[10] = row10;
		map[11] = row11;
		map[12] = row12;

		int[][] temp = {{5,0},{5,1},{5,2},{4,2},{3,2},{3,3},{3,4},{4,4},{5,4},
				{5,5},{5,6},{4,6},{3,6},{2,6},{1,6},{1,7},{1,8},{2,8},{3,8},{4,8},{5,8},{5,9},
				{5,10},{4,10},{3,10},{3,11},{3,12},{4,12},{5,12},{5,13},{6,13},{7,13},{7,12},{8,12},{9,12},{9,11},
				{9,10},{8,10},{7,10},{7,9},{7,8},{8,8},{9,8},{10,8},{11,8},{11,7},{11,6},{10,6},{9,6},{8,6},{7,6},
				{7,5},{7,4},{8,4},{9,4},{9,3},{9,2},{8,2},{7,2},{7,1},{7,0}};
		
		path = temp;
		
		int[] temp2 = {5,0};
		super.startingPos = temp2;
	}
	
	/**
	 * Getter to return the int value for a given point in the
	 * road
	 * @param row - row in the map to get the value from
	 * @param col - col in the map to get the value from
	 * @return the int value at row, col
	 */
	public int getValAtPos(int row, int col) {
		return map[row][col];
	}
}
