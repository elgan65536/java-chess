package chess.engine.board;

import chess.engine.board.Board.Builder;
import chess.engine.pieces.*;
import static chess.engine.board.BoardUtil.*;

public abstract class Move {
	
	protected final Board board;
	protected final Piece movedPiece;
	protected final int destination;
	protected final boolean isFirstMove;
	
	public final static Move NULL_MOVE = new NullMove();
	
	private Move(final Board b, final Piece p, final int d) {
		this.board = b;
		this.movedPiece = p;
		this.destination = d;
		this.isFirstMove = movedPiece.isFirstMove();
	}
	
	private Move(final Board b, final int d) {
		this.board = b;
		this.movedPiece = null;
		this.destination = d;
		this.isFirstMove = false;
	}
	
	public Board getBoard() {
		return this.board;
	}
	
	public int getDestinationCoordinate() {
		return this.destination;
	}
	
	public Piece getMovedPiece() {
		return this.movedPiece;
	}
	
	public int getCurrentCoordinate() {
		return this.movedPiece.getCoordinate();
	}
		
	public Board execute() {
		final Builder builder = new Builder();
		for (final Piece piece : this.board.getCurrentPlayer().getPieces()) {
			if(!this.movedPiece.equals(piece)) {
				builder.setPiece(piece);
			}
		}
		for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getPieces()) {
			builder.setPiece(piece);
		}
		builder.setPiece(this.movedPiece.movePiece(this));
		builder.setToMove(this.board.getCurrentPlayer().getOpponent().getColor());
		return builder.build();
	}
	
	public boolean isCapture() {
		return false;
	}
	
	public boolean isPawnMove() {
		return false;
	}
	
	public boolean isCheck() {
		return board.getCurrentPlayer().makeMove(this).getTransitionBoard().getCurrentPlayer().isInCheck();
	}
	
	public boolean isCastlingMove() {
		return false;
	}
	
	public boolean isPromotion() {
		return false;
	}
	
	public Piece getCapturedPiece() {
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + destination;
		result = prime * result + (isFirstMove ? 1231 : 1237);
		result = prime * result + ((movedPiece == null) ? 0 : movedPiece.hashCode());
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
		Move other = (Move) obj;
		if (destination != other.destination)
			return false;
		if (isFirstMove != other.isFirstMove)
			return false;
		if (movedPiece == null) {
			if (other.movedPiece != null)
				return false;
		} else if (!movedPiece.equals(other.movedPiece))
			return false;
		return true;
	}

	public static final class NormalMove extends Move {

		public NormalMove(final Board b, final Piece p, final int d) {
			super(b, p, d);
		}
		
		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			return true;
		}

		@Override
		public String toString() {
			return this.movedPiece.getPieceType().toString() + getAlgebraicNotation(this.movedPiece.getCoordinate()) + getAlgebraicNotation(this.destination);
		}
	}
	
	public static class Capture extends Move {
		
		final Piece capturedPiece;
		
		public Capture(final Board b, final Piece p, final int d, final Piece c) {
			super(b, p, d);
			this.capturedPiece = c;
		}
		
		@Override
		public boolean isCapture() {
			return true;
		}
		
		@Override
		public Piece getCapturedPiece() {
			return this.capturedPiece;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((capturedPiece == null) ? 0 : capturedPiece.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			Capture other = (Capture) obj;
			if (capturedPiece == null) {
				if (other.capturedPiece != null)
					return false;
			} else if (!capturedPiece.equals(other.capturedPiece))
				return false;
			return true;
		}

	}
	
	public static final class NormalCapture extends Capture {
		
		public NormalCapture(final Board b, final Piece p, final int d, final Piece c) {
			super(b, p, d, c);
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return this.movedPiece.getPieceType().toString() + getAlgebraicNotation(this.movedPiece.getCoordinate()) + "x" + getAlgebraicNotation(this.destination);
		}
		
	}
	
	public static class PawnMove extends Move {
		
		public PawnMove(final Board b, final Piece p, final int d) {
			super(b, p, d);
		}
		
		@Override
		public boolean isPawnMove() {
			return true;
		}
		
		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			return true;
		}

		@Override
		public String toString() {
			return getAlgebraicNotation(this.destination);
		}
		
	}
	
	public static class PawnCapture extends Capture {
		
		public PawnCapture(final Board b, final Piece p, final int d, final Piece c) {
			super(b, p, d, c);
		}
		
		@Override
		public boolean isPawnMove() {
			return true;
		}
		
		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return FILE_NAMES[fileOf(this.movedPiece.getCoordinate())] + "x" + getAlgebraicNotation(this.destination);
		}
		
	}
	
	public static final class EnPassantCapture extends PawnCapture {
		
		public EnPassantCapture(final Board b, final Piece p, final int d, final Piece c) {
			super(b, p, d, c);
		}
		
		@Override
		public Board execute() {
			final Builder builder = new Builder();
			for (final Piece piece : this.board.getCurrentPlayer().getPieces()) {
				if(!this.movedPiece.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getPieces()) {
				if (!this.capturedPiece.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			builder.setPiece(this.movedPiece.movePiece(this));
			builder.setToMove(this.board.getCurrentPlayer().getOpponent().getColor());
			return builder.build();
		}
		
		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			return true;
		}

		@Override
		public String toString() {
			return FILE_NAMES[fileOf(this.movedPiece.getCoordinate())] + "x" + getAlgebraicNotation(this.destination) + " e.p.";
		}

	}
	
	public static final class DoublePawnMove extends PawnMove {
		
		public DoublePawnMove(final Board b, final Piece p, final int d) {
			super(b, p, d);
		}
		
		@Override
		public Board execute() {
			final Builder builder = new Builder();
			for (final Piece piece : this.board.getCurrentPlayer().getPieces()) {
				if(!this.movedPiece.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getPieces()) {
				builder.setPiece(piece);
			}
			final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
			builder.setPiece(movedPawn);
			builder.setEnPassantPawn(movedPawn);
			builder.setEnPassantRange(1);
			builder.setToMove(this.board.getCurrentPlayer().getOpponent().getColor());
			return builder.build();
		
		}
		
		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			return true;
		}

		@Override
		public String toString() {
			return getAlgebraicNotation(this.destination);
		}
		
	}
	
	public static class PawnPromotion extends PawnMove {
		
		final Move pawnMove;
		final Pawn promotedPawn;
		final Piece promotionPiece;
		
		public PawnPromotion(final Move pm, final Piece pp) {
			super(pm.getBoard(), pm.getMovedPiece(), pm.getDestinationCoordinate());
			this.pawnMove = pm;
			this.promotedPawn = (Pawn) pm.getMovedPiece();
			this.promotionPiece = pp;
		}
		
		public Move getPawnMove() {
			return this.pawnMove;
		}
		
		@Override
		public Board execute() {
			final Board pawnMoveBoard = this.pawnMove.execute();
			final Builder builder = new Builder();
			for (final Piece piece : pawnMoveBoard.getCurrentPlayer().getOpponent().getPieces()) {
				if(!this.promotedPawn.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for (final Piece piece : pawnMoveBoard.getCurrentPlayer().getPieces()) {
				builder.setPiece(piece);
			}
			builder.setPiece(this.promotionPiece.movePiece(this));
			builder.setToMove(pawnMoveBoard.getCurrentPlayer().getColor());
			return builder.build();
		}
		
		@Override
		public boolean isCapture() {
			return pawnMove.isCapture();
		}
		
		@Override
		public boolean isPromotion() {
			return true;
		}
		
		@Override
		public Piece getCapturedPiece() {
			return this.pawnMove.getCapturedPiece();
		}
		
		public Piece getPromotionPiece() {
			return this.promotionPiece;
		}

		@Override
		public String toString() {
			return pawnMove.toString() + "=" + promotionPiece.getPieceType().toString();
		}
		
	}
	
	public abstract static class CastlingMove extends Move {
		
		protected final Rook movedRook;
		protected final int rookDestination;
		
		public CastlingMove(final Board b, final Piece p, final int d, final Rook r, final int rd) {
			super(b, p, d);
			this.movedRook = r;
			this.rookDestination = rd;
		}
		
		public Rook getMovedRook() {
			return this.movedRook;
		}
		
		public int getRookCurrentPosition() {
			return this.movedRook.getCoordinate();
		}
		
		public int getRookDestination() {
			return this.rookDestination;
		}
		
		@Override
		public boolean isCastlingMove() {
			return true;
		}
		
		@Override
		public Board execute() {
			final Builder builder = new Builder();
			for (final Piece piece : this.board.getCurrentPlayer().getPieces()) {
				if(!this.movedPiece.equals(piece) && !this.movedRook.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getPieces()) {
				builder.setPiece(piece);
			}
			builder.setPiece(this.movedPiece.movePiece(this));
			builder.setPiece(new Rook(this.movedRook.getPieceColor(), rookDestination));
			builder.setToMove(this.board.getCurrentPlayer().getOpponent().getColor());
			return builder.build();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((movedRook == null) ? 0 : movedRook.hashCode());
			result = prime * result + rookDestination;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			CastlingMove other = (CastlingMove) obj;
			if (movedRook == null) {
				if (other.movedRook != null)
					return false;
			} else if (!movedRook.equals(other.movedRook))
				return false;
			if (rookDestination != other.rookDestination)
				return false;
			return true;
		}
		
	}
	
	public static final class KingsideCastlingMove extends CastlingMove {
		
		public KingsideCastlingMove(final Board b, final Piece p, final int d, final Rook r, final int rd) {
			super(b, p, d, r, rd);
		}
		
		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "O-O";
		}
		
	}
	
	public static final class QueensideCastlingMove extends CastlingMove {
		
		public QueensideCastlingMove(final Board b, final Piece p, final int d, final Rook r, final int rd) {
			super(b, p, d, r, rd);
		}
		
		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "O-O-O";
		}
		
	}
	
	public static final class NullMove extends Move {
		
		public NullMove() {
			super(null, -1);
		}
		
		@Override
		public Board execute() {
			throw new RuntimeException("Null Move");
		}
	}
	
	public static class MoveFactory {
		
		private MoveFactory() {
			throw new RuntimeException("do not instantiate the MoveFactory class");
		}
		
		public static Move createMove (final Board board, final int currentCoordinate, final int destinationCoordinate) {
			for (final Move move : board.getAllLegalMoves()) {
				if (move.getCurrentCoordinate() == currentCoordinate && move.getDestinationCoordinate() == destinationCoordinate) {
					return move;
				}					
			}
			return NULL_MOVE;
		}
	}
	
}
