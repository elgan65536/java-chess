package chess.engine.player;

public enum MoveStatus {
	
	DONE,
	ILLEGAL,
	LEAVES_IN_CHECK;
	
	public boolean isDone() {
		return this == DONE;
	}
}
