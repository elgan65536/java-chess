package chess.engine.pieces;

public enum PieceType {
	PAWN("P", 1),
	KNIGHT("N", 3.1),
	BISHOP("B", 3.2),
	ROOK("R", 5),
	QUEEN("Q", 9),
	KING("K", 255);
	
	private String pieceName;
	private double pieceValue;
	
	private PieceType(String s, double d) {
		this.pieceName = s;
		this.pieceValue = d;
	}
	
	public String toString() {
		return this.pieceName;
	}
	
	public double getValue() {
		return this.pieceValue;
	}
	
}
