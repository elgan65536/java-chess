package chess.engine.board;

public class BoardUtil {
	
	public static final int BOARD_RANKS = 8;
	public static final int BOARD_FILES = 8;
	public static final int BOARD_SQUARES = BOARD_RANKS * BOARD_FILES;
	public static final String RANK_NAMES[] = initializeRankNames();
	public static final String FILE_NAMES[] = initializeFileNames();

	private BoardUtil() {
		throw new RuntimeException("do not instantiate the BoardUtil class");
	}

	public static boolean isValidTileCoordinate(int coordinate) {
		return coordinate >= 0 && coordinate < BOARD_SQUARES;
	}
	
	public static int fileOf(final int position) {
		return Math.floorMod(position, BOARD_FILES);
	}
	
	public static int rankOf(final int position) {
		return position / BOARD_FILES;
	}
	
	public static int positionOf(final int rank, final int file) {
		return rank * BOARD_FILES + file;
	}
	
	public static String getAlgebraicNotation(int coordinate) {
		return FILE_NAMES[fileOf(coordinate)] + RANK_NAMES[rankOf(coordinate)];
	}
	
	public static boolean testEdgeCases(final int range, final int currentPosition, final int offset) {
		return Math.abs(fileOf(currentPosition) - fileOf(currentPosition + offset)) > range;
	}
	
	private static String[] initializeRankNames() {
		String[] result = new String[BOARD_RANKS];
		int c = BOARD_RANKS;
		for (int i = 0; i < BOARD_RANKS; i++) {
			result[i] = Integer.toString(c);
			c--;
		}
		return result;
	}
	
	private static String[] initializeFileNames() {
		String[] result = new String[BOARD_FILES];
		char c = 'a';
		for (int i = 0; i < BOARD_FILES; i++) {
			result[i] = Character.toString(c);
			c++;
		}
		return result;
	}

}
