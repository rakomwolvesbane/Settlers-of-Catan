public class Board {
	// all array indexes represent row-major item locations on the map
	private static Tile[][] tiles = {new Tile[3], new Tile[4], new Tile[5], new Tile[4], new Tile[3]};
	private static final int total_tiles = 19;
	//even rows are diagonal, odd are vertical
	private static RoadNode[][] roads = {new RoadNode[6], new RoadNode[4], 
								  new RoadNode[8], new RoadNode[5], 
								  new RoadNode[10], new RoadNode[6], 
								  new RoadNode[10], new RoadNode[5],
								  new RoadNode[8], new RoadNode[4],
								  new RoadNode[6]};
	private static TownNode[][] towns = {new TownNode[7], new TownNode[9], new TownNode[11], new TownNode[11], new TownNode[9], new TownNode[7]};
	private static int ref_count = 0;
   private static Board the_board;
   public static Board getInstance(){
      if(the_board==null)
         the_board=new Board();
      return the_board;
   }
	public Board() {
		// put tiles onto the map
		int r = 0, c = 0;
		{
			//tile roll values: one 2, one 12, zero 7s, and two of each other roll
			int[][] tile_scores = {new int[3], new int[4], new int[5], new int[4], new int[2]};
			int count = 0;
			for (r = 0; r < tile_scores.length; r++) {
				for (c = 0; c < tile_scores[r].length; c++) {
					if (count < 1 ) {
						tile_scores[r][c] = 2;
					}
					else if (count < 3) {
						tile_scores[r][c] = 3;
					}
					else if (count < 5) {
						tile_scores[r][c] = 4;
					}
					else if (count < 7) {
						tile_scores[r][c] = 5;
					}
					else if (count < 9) {
						tile_scores[r][c] = 6;
					}
					else if (count < 11) {
						tile_scores[r][c] = 8;
					}
               else if (count < 13) {
						tile_scores[r][c] = 9;
					}
               else if (count < 15) {
						tile_scores[r][c] = 10;
					}
               else if (count < 17) {
						tile_scores[r][c] = 11;
					}
               else{
                  tile_scores[r][c] = 12;
               }
					count++;
				}
			}
			
			System.out.println("randomize the roll scores for the tiles");
			for (int x = 0; x < 10000; x++) {
				int[] rows = {(int)(Math.random() * 5.0), 
						   (int)(Math.random() * 5.0)};
				
				int[] cols = {(int)(Math.random() * (double)tile_scores[rows[0]].length), 
						        (int)(Math.random() * (double)tile_scores[rows[1]].length)};
				
				int temp = tile_scores[rows[0]][cols[0]];
				tile_scores[rows[0]][cols[0]] = tile_scores[rows[1]][cols[1]];
				tile_scores[rows[1]][cols[1]] = temp;
			}
			
			System.out.println("initial construction of tiles");
			int tile_count = 0;
			for (r = 0; r < tiles.length; r++) {
				for (c = 0; c < tiles[r].length; c++) {
					if (tile_count < 3) {
						tiles[r][c] = new Tile(Tile.BRICK, tile_scores[r][c]);
					}
					else if (tile_count < 6) {
						tiles[r][c] = new Tile(Tile.ORE, tile_scores[r][c]);
					}
					else if (tile_count < 10) {
						tiles[r][c] = new Tile(Tile.GRAIN, tile_scores[r][c]);
					}
					else if (tile_count < 14) {
						tiles[r][c] = new Tile(Tile.WOOL, tile_scores[r][c]);
					}
					else if (tile_count < 18) {
						tiles[r][c] = new Tile(Tile.LUMBER, tile_scores[r][c]);
					}
					else {
						tiles[r][c] = new Tile(Tile.DESERT, 0);
						tiles[r][c].giveRobber(true);
					}
					tile_count++;
				}
			}


			System.out.println("switch random tiles to randomize the map");
			for (int x = 0; x < 10000; x++) {
				int[] rows = {(int)(Math.random() * 5.0), 
						   (int)(Math.random() * 5.0)};
				
				int[] cols = {(int)(Math.random() * (double)tiles[rows[0]].length), 
						   (int)(Math.random() * (double)tiles[rows[1]].length)};
				
				Tile temp = tiles[rows[0]][cols[0]];
				tiles[rows[0]][cols[0]] = tiles[rows[1]][cols[1]];
				tiles[rows[1]][cols[1]] = temp;
			}
         
         /*for (r = 0; r < tiles.length; r++) {
            for (c = 0; c < tiles[r].length; c++) {
               System.out.println(tiles[r][c]);
            }
         }*/
     }
		
		System.out.println("create & instantiate the temporary construction arrays for the nodes");
		RoadNode[][][] road_road_constructors = new RoadNode[roads.length][][]; // for road constructor
		for (r = 0; r < roads.length; r++) {
			road_road_constructors[r] = new RoadNode[roads[r].length][]; // instantiate the varying length of the columns
		}
		TownNode[][][] road_town_constructors = new TownNode[roads.length][][];
		for (r = 0; r < roads.length; r++) {
			road_town_constructors[r] = new TownNode[roads[r].length][2];
		}
		RoadNode[][][] town_road_constructors = new RoadNode[towns.length][][]; // for town constructor, road parameter
		for (r = 0; r < town_road_constructors.length; r++) {
			town_road_constructors[r] = new RoadNode[towns[r].length][];
		}
		TownNode[][][] town_town_constructors = new TownNode[towns.length][][]; // for town constructor, town parameter
		for (r = 0; r < town_town_constructors.length; r++) {
			town_town_constructors[r] = 
			new TownNode[towns[r].length][];
		}
		Tile[][][] town_tile_constructors = new Tile[towns.length][][];
		for (r = 0; r < town_tile_constructors.length; r++) {
			town_tile_constructors[r] = new Tile[towns[r].length][];
		}
		
		System.out.println("instantiate the 3rd level arrays to their correct lengths");
		int length;
		for (r = 0; r < roads.length; r++) { // loop for the road constructors, road parameter
			for (c = 0; c < roads[r].length; c++) {
				// find the correct size of the array
				length = 4;
				if (r == 0 || r == roads.length - 1) {
					length--;
				}
				if (c == 0 || c == roads[r].length - 1) {
					length--;
				}
				road_road_constructors[r][c] = new RoadNode[length];
			}
		}
		// every road has the same # of adjacent towns, so there doesn't need to be an algorithm to determine the array length
		for (r = 0; r < towns.length; r++) { // loop for the town constructors, road parameter
			for (c = 0; c < towns[r].length; c++) {
				length = 3;
				if (r == 0 || r == towns.length - 1) {
					length--;
				}
				else if (c == 0 || c == towns[r].length - 1) { // else because all towns have at least 2 adj roads
					length--;
				}
				town_road_constructors[r][c] = new RoadNode[length];
			}
		}
		for (r = 0; r < towns.length; r++) { // loop for the town constructors, town parameter
			for (c = 0; c < towns[r].length; c++) {
				length = 3;
				if (r == 0 || r == towns.length - 1) {
					length--;
				}
				else if (c == 0 || c == towns[r].length - 1) {
					length--;
				}
				town_town_constructors[r][c] = new TownNode[length];
			}
		}
		for (r = 0; r < towns.length; r++) { // loop for town constructors, tile parameter
			for (c = 0; c < towns[r].length; c++) {
				if ((r == 0 || r == towns.length - 1) && c % 2 != 0 || c == 0 || c == towns[r].length - 1) {
					length = 1;
				}
				else if (c == 1 || c == towns[r].length - 2 || (r == 0 || r == towns.length - 1) && c % 2 == 0) {
					length = 2;
				}
				else {
					length = 3;
				}
				town_tile_constructors[r][c] = new Tile[length];
			}
		}
		
		System.out.println("instantiate all the roads");
		for (r = 0; r < roads.length; r++) {
			for (c = 0; c < roads[r].length; c++) {
				roads[r][c] = new RoadNode(road_road_constructors[r][c], road_town_constructors[r][c]);
			}
		}
		
		System.out.println("instantiate all the towns w/ trade values");
		for (r = 0; r < towns.length; r++) {
			for (c = 0; c < towns[r].length; c++) {
				// stupid annoying stuff to tell if the town has a trader
				if ((c == 0 || c == 1) && (r == 0 || r == 5) || (c == 10 && (r == 2 || r == 3))) {
					towns[r][c] = new TownNode(town_tile_constructors[r][c], town_road_constructors[r][c], town_town_constructors[r][c], 5); // 3:1 trader
				}
				// 2:1 traders
				else if (r == 0 && (c == 3 || c == 4)) {
					towns[r][c] = new TownNode(town_tile_constructors[r][c], town_road_constructors[r][c], town_town_constructors[r][c], Tile.WOOL);
				}
				else if (r == 1 && c == 0 || r == 2 && c == 1) {
					towns[r][c] = new TownNode(town_tile_constructors[r][c], town_road_constructors[r][c], town_town_constructors[r][c], Tile.ORE);
				}
				else if (r == 3 && c == 1 || r == 4 && c == 0) {
					towns[r][c] = new TownNode(town_tile_constructors[r][c], town_road_constructors[r][c], town_town_constructors[r][c], Tile.GRAIN);
				}
				else if (r == 4 && (c == 7 || c == 8)) {
					towns[r][c] = new TownNode(town_tile_constructors[r][c], town_road_constructors[r][c], town_town_constructors[r][c], Tile.BRICK);
				}
				else if (r == 5 && (c == 3 || c == 4)) {
					towns[r][c] = new TownNode(town_tile_constructors[r][c], town_road_constructors[r][c], town_town_constructors[r][c], Tile.LUMBER);
				}
				else { // no trader
					towns[r][c] = new TownNode(town_tile_constructors[r][c], town_road_constructors[r][c], town_town_constructors[r][c]);
				}
			}
		}
		System.out.println("link vertical roads");
		for (r = 1; r < roads.length; r += 2) {
			for (c = 0; c < roads[r].length; c++) {
				linkVerticalRoad(r, c, road_road_constructors, road_town_constructors, town_road_constructors, town_town_constructors);
			}
		}
		
		System.out.println("link horizontal roads");
		for (r = 0; r < roads.length; r += 2) {
			for (c = 0; c < roads[r].length; c++) {
				linkHorizontalRoad(r, c, road_road_constructors, road_town_constructors, town_road_constructors, town_town_constructors);
			}
		}
		System.out.println("instantiate the town nodes");
		for (r = 0; r < towns.length; r++) {
			for (c = 0; c < towns[r].length; c++) {
				linkTown(r, c, road_town_constructors, town_road_constructors, town_town_constructors, town_tile_constructors[r][c]);
			}
		}
      
      // reality check on references
      boolean good;
      for (r = 0; r < roads.length; r++) {
         for (c = 0; c < roads[r].length; c++) {
            for (int x = 0; x < roads[r][c].getAdjacentRoads().length; x++) {
               RoadNode ro = roads[r][c].getAdjacentRoads()[x];
               good = false;
               if (ro == null) {
                  System.out.println("null problem: road " + r + ", " + c + " references length: " + roads[r][c].getAdjacentRoads().length + " index: " + x);
               }
               /*for (int r2 = 0; r2 < towns.length; r2++) {
                  for (int c2 = 0; c2 < towns[r2].length; c2++) {
                     if (t.resource == towns[r2][c2].resource && t.roll == towns[r2][c2].roll) {
                        good = true;
                     }
                  }
               }
               System.out.println("problem: town " + r + ", " + c);*/
            }
            
         }
      }
		System.out.println("dependencies: " + ref_count);
	}
	
	// these are road construction helper methods
	
	// constructs vertical references and references of adjacent horizontals to the vertical road
	void linkVerticalRoad(final int r, final int c, final RoadNode[][][] road_road_constructors, final TownNode[][][] road_town_constructors, 
													final RoadNode[][][] town_road_constructors, final TownNode[][][] town_town_constructors) {
		int other_r, other_c; // used to store the other's location
		if (r == 5) { // at the equator
			// road-road links
			other_r = r - 1;
			other_c = c * 2 - 1;
			if (onBoard(roads, other_r, other_c)) {
				linkReference(road_road_constructors[r][c], road_road_constructors[other_r][other_c], roads[r][c], roads[other_r][other_c]);
			}
			other_c++;
			if (onBoard(roads, other_r, other_c)) {
				linkReference(road_road_constructors[r][c], road_road_constructors[other_r][other_c], roads[r][c], roads[other_r][other_c]);
			}
			other_r = r + 1;
			other_c = c * 2 - 1;
			if (onBoard(roads, other_r, other_c)) {
				linkReference(road_road_constructors[r][c], road_road_constructors[other_r][other_c], roads[r][c], roads[other_r][other_c]);
			}
			other_c++;
			if (onBoard(roads, other_r, other_c)) {
				linkReference(road_road_constructors[r][c], road_road_constructors[other_r][other_c], roads[r][c], roads[other_r][other_c]);
			}
			
         // road-town
			other_r = r / 2;
			other_c = c * 2;
			if (onBoard(towns, other_r, other_c)) {
				linkReference(road_town_constructors[r][c], town_road_constructors[other_r][other_c], roads[r][c], towns[other_r][other_c]);
			}
         else {
            System.out.println("this is stupid");
         }
			other_r++;
			if (onBoard(towns, other_r, other_c)) {
				linkReference(road_town_constructors[r][c], town_road_constructors[other_r][other_c], roads[r][c], towns[other_r][other_c]);
			}
		}
		else if (r < 5) { // above equator
			// road-road links
			other_r = r - 1;
			other_c = c * 2;
			if (onBoard(roads, other_r, other_c)) {
				linkReference(road_road_constructors[r][c], road_road_constructors[other_r][other_c], roads[r][c], roads[other_r][other_c]);
			}
			other_c--; // other_c = c * 2 + 1;
			if (onBoard(roads, other_r, other_c)) {
				linkReference(road_road_constructors[r][c], road_road_constructors[other_r][other_c], roads[r][c], roads[other_r][other_c]);
			}
			other_r = r + 1;
         other_c++;
			if (onBoard(roads, other_r, other_c)) {
				linkReference(road_road_constructors[r][c], road_road_constructors[other_r][other_c], roads[r][c], roads[other_r][other_c]);
			}
			other_c++; // other_c = c * 2 + 2
			if (onBoard(roads, other_r, other_c)) {
				linkReference(road_road_constructors[r][c], road_road_constructors[other_r][other_c], roads[r][c], roads[other_r][other_c]);
			}
			
			// road-town links
         other_r = r / 2;
			other_c = c * 2;
         System.out.println(r + ", " + c);
			if (onBoard(towns, other_r, other_c)) {
				linkReference(road_town_constructors[r][c], town_road_constructors[other_r][other_c], roads[r][c], towns[other_r][other_c]);
			}
			other_r++;
			other_c++;
			if (onBoard(towns, other_r, other_c)) {
				linkReference(road_town_constructors[r][c], town_road_constructors[other_r][other_c], roads[r][c], towns[other_r][other_c]);
			}
		}
		else { // below equator
			// road-road links
			other_r = r - 1;
			other_c = c * 2 + 1;
			if (onBoard(roads, other_r, other_c)) {
				linkReference(road_road_constructors[r][c], road_road_constructors[other_r][other_c], roads[r][c], roads[other_r][other_c]);
			}
			other_c++; // other_c = c * 2 + 2;
			if (onBoard(roads, other_r, other_c)) {
				linkReference(road_road_constructors[r][c], road_road_constructors[other_r][other_c], roads[r][c], roads[other_r][other_c]);
			}
			other_r = r + 1;
			other_c = c * 2;
			if (onBoard(roads, other_r, other_c)) {
				linkReference(road_road_constructors[r][c], road_road_constructors[other_r][other_c], roads[r][c], roads[other_r][other_c]);
			}
			other_c++; // other_c = c * 2 + 1;
			if (onBoard(roads, other_r, other_c)) {
				linkReference(road_road_constructors[r][c], road_road_constructors[other_r][other_c], roads[r][c], roads[other_r][other_c]);
			}
			
			// road-town links
			other_r = r / 2;
			other_c = c * 2 + 1;
			if (onBoard(towns, other_r, other_c)) {
				linkReference(road_town_constructors[r][c], town_road_constructors[other_r][other_c], roads[r][c], towns[other_r][other_c]);
			}
			other_r++;
			other_c--;
			if (onBoard(towns, other_r, other_c)) {
				linkReference(road_town_constructors[r][c], town_road_constructors[other_r][other_c], roads[r][c], towns[other_r][other_c]);
			}
		}
	}
	// completes the references of horizontal roads
	private void linkHorizontalRoad(final int r, final int c, final RoadNode[][][] road_road_constructors, final TownNode[][][] road_town_constructors,
															  final RoadNode[][][] town_road_constructors, final TownNode[][][] town_town_constructors) {
		// link road-road
		int other_c = c - 1;
		int other_r = r;
		if (onBoard(roads, other_r, other_c)) {
			linkReference(road_road_constructors[r][c], road_road_constructors[other_r][other_c], roads[r][c], roads[other_r][other_c]);
		}
		other_c = c + 1;
		if (onBoard(roads, r, other_c)) {
			linkReference(road_road_constructors[r][c], road_road_constructors[other_r][other_c], roads[r][c], roads[other_r][other_c]);
		}
		
		// link road-town
		other_r = r / 2;
		other_c = c;
		if (onBoard(towns, other_r, other_c)) {
			linkReference(road_town_constructors[r][c], town_road_constructors[other_r][other_c], roads[r][c], towns[other_r][other_c]);
		}
      else {
         System.out.println("can't be found horizontally, coords: " + r + ", " + c);
      }
		other_c = c + 1;
		if (onBoard(towns, other_r, other_c)) {
			linkReference(road_town_constructors[r][c], town_road_constructors[other_r][other_c], roads[r][c], towns[other_r][other_c]);
		}
	}
	
	// helper method for town construction, doesn't need to add road references
	private void linkTown(final int r, final int c, final TownNode[][][] road_town_constructors, final RoadNode[][][] town_road_constructors, final TownNode[][][] town_town_constructors, Tile[] tile_constructors) {
		int other_r = r, 
			other_c = c - 1;
		// link references to other towns
		if (onBoard(towns, other_r, other_c)) {
			linkReference(town_town_constructors[r][c], town_town_constructors[other_r][other_c], towns[r][c], towns[other_r][other_c]);
		}
		other_c = c + 1;
		if (onBoard(towns, other_r, other_c)) {
			linkReference(town_town_constructors[r][c], town_town_constructors[other_r][other_c], towns[r][c], towns[other_r][other_c]);
		}
		if (r < 2) { // in the north of the board
			if (c % 2 == 0) {
				other_r = r + 1;
				other_c = c + 1;
				if (onBoard(towns, other_r, other_c)) {
					linkReference(town_town_constructors[r][c], town_town_constructors[other_r][other_c], towns[r][c], towns[other_r][other_c]);
				}
			}
			else {
				other_r = r - 1;
				other_c = c - 1;
				if (onBoard(towns, other_r, other_c)) {
				linkReference(town_town_constructors[r][c], town_town_constructors[other_r][other_c], towns[r][c], towns[other_r][other_c]);
				}
			}
		}
      else if (r == 2) { // at the equator
         if (c % 2 == 0) {
				other_r = r + 1;
				other_c = c;
				if (onBoard(towns, other_r, other_c)) {
					linkReference(town_town_constructors[r][c], town_town_constructors[other_r][other_c], towns[r][c], towns[other_r][other_c]);
				}
			}
			else {
				other_r = r - 1;
				other_c = c - 1;
				if (onBoard(towns, other_r, other_c)) {
				linkReference(town_town_constructors[r][c], town_town_constructors[other_r][other_c], towns[r][c], towns[other_r][other_c]);
				}
			}
      }
      else if (r == 3) {
         if (c % 2 == 0) {
				other_r = r - 1;
				other_c = c;
				if (onBoard(towns, other_r, other_c)) {
					linkReference(town_town_constructors[r][c], town_town_constructors[other_r][other_c], towns[r][c], towns[other_r][other_c]);
				}
			}
			else {
				other_r = r + 1;
				other_c = c - 1;
				if (onBoard(towns, other_r, other_c)) {
				linkReference(town_town_constructors[r][c], town_town_constructors[other_r][other_c], towns[r][c], towns[other_r][other_c]);
				}
			}
      }
		else { // in the south of the board
			if (c % 2 == 0) {
				other_r = r - 1;
				other_c = c - 1;
				if (onBoard(towns, other_r, other_c)) {
					linkReference(town_town_constructors[r][c], town_town_constructors[other_r][other_c], towns[r][c], towns[other_r][other_c]);
				}
			}
			else {
				other_r = r + 1;
				other_c = c - 1;
				if (onBoard(towns, other_r, other_c)) {
					linkReference(town_town_constructors[r][c], town_town_constructors[other_r][other_c], towns[r][c], towns[other_r][other_c]);
				}
			}
		}
		
		// add references to tiles
		if (r < 3) { // in the north
			if (c % 2 == 0) {
				other_r = r - 1;
				other_c = c / 2 - 1;
				if (onBoard(tiles, other_r, other_c)) {
					addReference(tile_constructors, tiles[other_r][other_c]);
				}
				other_r++;
				if (onBoard(tiles, other_r, other_c)) {
					addReference(tile_constructors, tiles[other_r][other_c]);
				}
				other_c++;
				if (onBoard(tiles, other_r, other_c)) {
					addReference(tile_constructors, tiles[other_r][other_c]);
				}
			}
			else {
				other_r = r - 1;
				other_c = c / 2 - 1;
				if (onBoard(tiles, other_r, other_c)) {
					addReference(tile_constructors, tiles[other_r][other_c]);
				}
				other_c++;
				if (onBoard(tiles, other_r, other_c)) {
					addReference(tile_constructors, tiles[other_r][other_c]);
				}
				other_r++;
				if (onBoard(tiles, other_r, other_c)) {
					addReference(tile_constructors, tiles[other_r][other_c]);
				}
			}
		}
		else { // in the south
			if (c % 2 == 0) {
				other_r = r - 1;
				other_c = c / 2 - 1;
				if (onBoard(tiles, other_r, other_c)) {
					addReference(tile_constructors, tiles[other_r][other_c]);
				}
				other_c++;
				if (onBoard(tiles, other_r, other_c)) {
					addReference(tile_constructors, tiles[other_r][other_c]);
				}
				other_r++;
				other_c--;
				if (onBoard(tiles, other_r, other_c)) {
					addReference(tile_constructors, tiles[other_r][other_c]);
				}
			}
			else { // odd
				other_r = r - 1;
				other_c = c / 2;
				if (onBoard(tiles, other_r, other_c)) {
					addReference(tile_constructors, tiles[other_r][other_c]);
				}
				other_r++;
				other_c--;
				if (onBoard(tiles, other_r, other_c)) {
					addReference(tile_constructors, tiles[other_r][other_c]);
				}
				other_c++;
				if (onBoard(tiles, other_r, other_c)) {
					addReference(tile_constructors, tiles[other_r][other_c]);
				}
			}
		}
      
      // town-road references
      /*(other_r = r * 2;
      other_c = c;
      if (onBoard(roads, other_r, other_c)) {
         linkReference(town_road_constructors[r][c], road_town_constructors[other_r][other_c], towns[r][c], roads[other_r][other_c]);
      }
      other_c--;
      if (onBoard(roads, other_r, other_c)) {
         linkReference(town_road_constructors[r][c], road_town_constructors[other_r][other_c], towns[r][c], roads[other_r][other_c]);
      }*/
	}
	
	// parameters: the parts of the board to be checked, the location in those parts to be checked
	private boolean onBoard(final Object[][] parts, final int r, final int c) {
		return r >= 0 && r < parts.length && c >= 0 && c < parts[r].length;
	}
	
	private void addReference(final Object[] constructors, final Object other) {
		for (int x = 0; x < constructors.length; x++) {
			// this or allows this function to be always used without worrying about copied references
			if (constructors[x] == other) {
				break;
			}
			if (constructors[x] == null) {
				constructors[x] = other;
				ref_count++;
				break;
			}
		}
	}
	
	/*
	 * functions for setting up references between two nodes
	 * constructors & others_costructors: the construction arrays to be modified, 
	 * first & other: the objects to be linked
	 * precondition: parameters match up (e.g. others_constructors -> other)
	 */
	private void linkReference(final Node[] constructors, final Node[] others_constructors, final Node first, final Node other) {
		// add to first's references
		addReference(constructors, other);
		// add to other's references
		addReference(others_constructors, first);
	}
	
	public Tile getTileAt(int row, int col) {
		return tiles[row][col];
	}
	
	public void moveRobber(final int r, final int c) {
		for (int k=0; k < tiles.length; k++) 
         for (int j=0; j < tiles[k].length; j++)
            tiles[k][j].giveRobber(false);

		tiles[r][c].giveRobber(true);
	}
   
   public RoadNode[][] getRoads(){
      return roads;
   }
   
   public TownNode[][] getTowns(){
      return towns;
   }
	
}
