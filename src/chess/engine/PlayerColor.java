package chess.engine;

import static chess.engine.board.BoardUtil.*;

public enum PlayerColor {
	WHITE(-1, "W"), BLACK(1, "B");
	
	final int direction;
	final String name;
	
	private PlayerColor(int i, String s) {
		this.direction = i;
		this.name = s;
	}
	
	public int getDirection() {
		return this.direction;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public boolean isPromotionAllowed(int position) {
		if (this == WHITE) {
			return rankOf(position) == 0;
		}
		if (this == BLACK) {
			return rankOf(position) == BOARD_RANKS - 1;
		}
		return false;
	}
}
