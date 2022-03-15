package chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import chess.engine.PlayerColor;
import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.board.Tile;
import chess.engine.board.Move.*;
import chess.engine.pieces.*;

public abstract class Player {
	
	protected final Board board;
	protected final King king;
	protected final Collection<Move> legalMoves;
	private final boolean isInCheck;
	
	public Player(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves) {
		this.board = board;
		this.king = establishKing();
		this.isInCheck = !Player.getAttacksOnTile(this.king.getCoordinate(), opponentMoves).isEmpty();
		legalMoves.addAll(getCastlingMoves(legalMoves, opponentMoves));
		this.legalMoves = Collections.unmodifiableCollection(legalMoves);
	}

	protected static Collection<Move> getAttacksOnTile(final int coordinate, final Collection<Move> moves) {
		final List<Move> attackMoves = new ArrayList<>();
		for (final Move move : moves) {
			if (coordinate == move.getDestinationCoordinate()) {
				attackMoves.add(move);
			}
		}
		return Collections.unmodifiableList(attackMoves);
	}

	protected King establishKing() {
		for (final Piece piece : getPieces()) {
			if (piece.getPieceType() == PieceType.KING) {
				return (King) piece;
			}
		}
		throw new RuntimeException("Board does not have king");
	}
	
	public King getKing() {
		return this.king;
	}
	
	public Collection<Move> getLegalMoves() {
		return this.legalMoves;
	}
	
	public Collection<Move> getLegalMovesNoCheck() {
		final List<Move> moves = new ArrayList<>();
		for (Move move : this.legalMoves) {
			final MoveTransition transition = this.makeMove(move);
			if (transition.getMoveStatus().isDone()) {
				moves.add(move);
			}
		}
		return Collections.unmodifiableList(moves);
	}
	
	public boolean isMoveLegal(final Move move) {
		return this.legalMoves.contains(move);
	}
	
	public boolean isInCheck() {
		return this.isInCheck;
	}
	
	public boolean isInCheckmate() {
		return  this.isInCheck && !this.hasEscapeMoves();
	}
	
	public boolean isInStalemate() {
		return !this.isInCheck && !this.hasEscapeMoves();
	}
	
	protected boolean hasEscapeMoves() {
		for (final Move move : legalMoves) {
			final MoveTransition transition = makeMove(move);
			if (transition.getMoveStatus().isDone()) {
				return true;
			}
		}
		return false;
	}
	
	public MoveTransition makeMove(final Move move) {
		if (!isMoveLegal(move)) {
			return new MoveTransition(this.board, move, MoveStatus.ILLEGAL);
		}
		final Board transitionBoard = move.execute();
		final Collection<Move> kingAttacks = Player.getAttacksOnTile(transitionBoard.getCurrentPlayer().getOpponent().getKing().getCoordinate(), 
																	 transitionBoard.getCurrentPlayer().getLegalMoves());
		if (!kingAttacks.isEmpty()) {
			return new MoveTransition(this.board, move, MoveStatus.LEAVES_IN_CHECK);
		}
		return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
	}
	
	public abstract Collection<Piece> getPieces();
	public abstract PlayerColor getColor();
	public abstract Player getOpponent();
	protected abstract Collection<Move> getCastlingMoves(Collection<Move> legalMoves, Collection<Move> opponentMoves);
	
	public static class WhitePlayer extends Player {

		public WhitePlayer(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves) {
			super(board, legalMoves, opponentMoves);
		}

		@Override
		public Collection<Piece> getPieces() {
			return this.board.getWhitePieces();
		}

		@Override
		public PlayerColor getColor() {
			return PlayerColor.WHITE;
		}

		@Override
		public Player getOpponent() {
			return this.board.getBlackPlayer();
		}

		@Override
		protected Collection<Move> getCastlingMoves(final Collection<Move> legalMoves, final Collection<Move> opponentMoves) {
			final List<Move> castlingMoves = new ArrayList<>();
			if (this.king.isFirstMove() && !this.isInCheck()) {
				if (!this.board.getTile(61).isOccupied() && !this.board.getTile(62).isOccupied()) {
					final Tile rookTile = this.board.getTile(63);
					if (rookTile.isOccupied() && 
							rookTile.getPiece().isFirstMove() && 
							rookTile.getPiece().getPieceType() == PieceType.ROOK && 
							Player.getAttacksOnTile(61, opponentMoves).isEmpty()) {
						castlingMoves.add(new KingsideCastlingMove(this.board, this.king, 62, (Rook) rookTile.getPiece(), 61));
					}
				}
				if (!this.board.getTile(57).isOccupied() && !this.board.getTile(58).isOccupied() && !this.board.getTile(59).isOccupied()) {
					final Tile rookTile = this.board.getTile(56);
					if (rookTile.isOccupied() && 
							rookTile.getPiece().isFirstMove() && 
							rookTile.getPiece().getPieceType() == PieceType.ROOK && 
							Player.getAttacksOnTile(59, opponentMoves).isEmpty()) {
						castlingMoves.add(new QueensideCastlingMove(this.board, this.king, 58, (Rook) rookTile.getPiece(), 59));
					}
				}
			}
			return Collections.unmodifiableList(castlingMoves);
		}
		
		@Override
		public String toString() {
			return "White player";
		}

	}
	
	public static class BlackPlayer extends Player {

		public BlackPlayer(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves) {
			super(board, legalMoves, opponentMoves);
		}

		@Override
		public Collection<Piece> getPieces() {
			return this.board.getBlackPieces();
		}
		
		@Override
		public PlayerColor getColor() {
			return PlayerColor.BLACK;
		}

		@Override
		public Player getOpponent() {
			return this.board.getWhitePlayer();
		}

		@Override
		protected Collection<Move> getCastlingMoves(final Collection<Move> legalMoves, final Collection<Move> opponentMoves) {
			final List<Move> castlingMoves = new ArrayList<>();
			if (this.king.isFirstMove() && !this.isInCheck()) {
				if (!this.board.getTile(5).isOccupied() && !this.board.getTile(6).isOccupied()) {
					final Tile rookTile = this.board.getTile(7);
					if (rookTile.isOccupied() && 
							rookTile.getPiece().isFirstMove() && 
							rookTile.getPiece().getPieceType() == PieceType.ROOK && 
							Player.getAttacksOnTile(5, opponentMoves).isEmpty()) {
						castlingMoves.add(new KingsideCastlingMove(this.board, this.king, 6, (Rook) rookTile.getPiece(), 5));
					}
				}
				if (!this.board.getTile(1).isOccupied() && !this.board.getTile(2).isOccupied() && !this.board.getTile(3).isOccupied()) {
					final Tile rookTile = this.board.getTile(0);
					if (rookTile.isOccupied() && 
							rookTile.getPiece().isFirstMove() && 
							rookTile.getPiece().getPieceType() == PieceType.ROOK && 
							Player.getAttacksOnTile(3, opponentMoves).isEmpty()) {
						castlingMoves.add(new QueensideCastlingMove(this.board, this.king, 2, (Rook) rookTile.getPiece(), 3));
					}
				}
			}
			return Collections.unmodifiableList(castlingMoves);
		}
		
		@Override
		public String toString() {
			return "Black player";
		}
		
	}
	
}
