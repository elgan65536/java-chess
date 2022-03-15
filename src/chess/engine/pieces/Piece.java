package chess.engine.pieces;

import java.util.Collection;

import chess.engine.PlayerColor;
import chess.engine.board.Board;
import chess.engine.board.Move;

public abstract class Piece implements Comparable<Piece>{

	protected final int pieceCoordinate;
	protected final PlayerColor pieceColor;
	protected final PieceType pieceType;
	protected final boolean isFirstMove;
	private final int cachedHashCode;

	public Piece(final PlayerColor c, final int p, final PieceType t, boolean b) {
		this.pieceCoordinate = p;
		this.pieceColor = c;
		this.pieceType = t;
		this.isFirstMove = b;
		this.cachedHashCode = computeHashCode();
	}
	
	public abstract Collection<Move> getLegalMoves(final Board board);
	
	public PlayerColor getPieceColor() {
		return this.pieceColor;
	}
	
	public int getCoordinate() {
		return this.pieceCoordinate;
	}
	
	public PieceType getPieceType() {
		return this.pieceType;
	}
	
	public boolean isFirstMove() {
		return this.isFirstMove;
	}
	
	public abstract Piece movePiece(Move move);
	
	@Override
	public String toString() {
		return this.pieceColor == PlayerColor.WHITE ? pieceType.toString() : pieceType.toString().toLowerCase();
	}
	
	@Override
	public int hashCode() {
		return this.cachedHashCode;
	}
	
	@Override
	public int compareTo(Piece p) {
		if (this.getPieceType().getValue() < p.getPieceType().getValue()) {
			return -1;
		} else if (this.getPieceType().getValue() > p.getPieceType().getValue()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	private int computeHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isFirstMove ? 1231 : 1237);
		result = prime * result + ((pieceColor == null) ? 0 : pieceColor.hashCode());
		result = prime * result + pieceCoordinate;
		result = prime * result + ((pieceType == null) ? 0 : pieceType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Piece other = (Piece) obj;
		if (isFirstMove != other.isFirstMove)
			return false;
		if (pieceColor != other.pieceColor)
			return false;
		if (pieceCoordinate != other.pieceCoordinate)
			return false;
		if (pieceType != other.pieceType)
			return false;
		return true;
	}

}
