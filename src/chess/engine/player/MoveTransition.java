package chess.engine.player;

import chess.engine.board.Board;
import chess.engine.board.Move;

public class MoveTransition {
	private final Board transitionBoard;
	private final Move move;
	private final MoveStatus moveStatus;
	
	public MoveTransition (final Board b, final Move m, final MoveStatus s) {
		this.transitionBoard = b;
		this.move = m;
		this.moveStatus = s;
	}
	
	public Board getTransitionBoard() {
		return this.transitionBoard;
	}
	
	public Move getMove() {
		return this.move;
	}
	
	public MoveStatus getMoveStatus() {
		return this.moveStatus;
	}
	
	
}
