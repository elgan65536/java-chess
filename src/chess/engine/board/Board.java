package chess.engine.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chess.engine.PlayerColor;
import chess.engine.pieces.*;
import chess.engine.player.Player;
import chess.engine.player.Player.*;
import static chess.engine.board.BoardUtil.*;

public class Board {
	
	private final List<Tile> gameBoard;
	private final Collection<Piece> whitePieces;
	private final Collection<Piece> blackPieces;
	private final Collection<Move> whiteLegalMoves;
	private final Collection<Move> blackLegalMoves;
	private final WhitePlayer whitePlayer;
	private final BlackPlayer blackPlayer;
	private final Player currentPlayer;
	private final Pawn enPassantPawn;
	private final int enPassantRange;
	
	private Board(final Builder b) {
		this.gameBoard = createBoard(b);
		this.whitePieces = getColoredPieces(PlayerColor.WHITE);
		this.blackPieces = getColoredPieces(PlayerColor.BLACK);
		this.enPassantPawn = b.enPassantPawn;
		this.enPassantRange = b.enPassantRange;
		this.whiteLegalMoves = getLegalMoves(this.whitePieces);
		this.blackLegalMoves = getLegalMoves(this.blackPieces);
		this.whitePlayer = new WhitePlayer(this, whiteLegalMoves, blackLegalMoves);
		this.blackPlayer = new BlackPlayer(this, blackLegalMoves, whiteLegalMoves);
		this.currentPlayer = b.toMove == PlayerColor.WHITE ? this.whitePlayer : this.blackPlayer;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < BoardUtil.BOARD_SQUARES; i++) {
			sb.append(gameBoard.get(i).toString());
			if ((i + 1) % BoardUtil.BOARD_FILES == 0) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	
	public Collection<Piece> getBlackPieces() {
		return blackPieces;
	}

	public Collection<Piece> getWhitePieces() {
		return whitePieces;
	}
	
	public Player getWhitePlayer() {
		return this.whitePlayer;
	}
	
	public Player getBlackPlayer() {
		return this.blackPlayer;
	}
	
	public Player getCurrentPlayer() {
		return this.currentPlayer;
	}

	public Tile getTile(int coordinate) {
		return gameBoard.get(coordinate);
	}
	
	public Pawn getEnPassantPawn() {
		return enPassantPawn;
	}

	public int getEnPassantRange() {
		return enPassantRange;
	}

	private static List<Tile> createBoard(final Builder b) {
		final Tile[] tiles = new Tile[BOARD_SQUARES];
		for (int i = 0; i < BOARD_SQUARES; i++) {
			tiles[i] = Tile.createTile(i, b.boardConfig.get(i));	
		}
		return Collections.unmodifiableList(Arrays.asList(tiles));
	}
	
	private Collection<Piece> getColoredPieces(PlayerColor c) {
		final List<Piece> coloredPieces = new ArrayList<>();
		for (final Tile tile : gameBoard) {
			if (tile.isOccupied()) {
				if (tile.getPiece().getPieceColor() == c) {
					coloredPieces.add(tile.getPiece());
				}
			}
		}
		return Collections.unmodifiableList(coloredPieces);
	}
	
	private Collection<Move> getLegalMoves(Collection<Piece> p) {
		List<Move> legalMoves = new ArrayList<>();
		for (final Piece piece : p) {
			legalMoves.addAll(piece.getLegalMoves(this));
		}
		return legalMoves;
	}
	
	public Collection<Move> getAllLegalMoves() {
		List<Move> moves = new ArrayList<>();
		moves.addAll(whiteLegalMoves);
		moves.addAll(blackLegalMoves);
		return Collections.unmodifiableList(moves);
	}
	
	public static Board createStartingPosition() {
		final Builder boardBuilder = new Builder();
		boardBuilder.setPiece(new Rook  (PlayerColor.BLACK, 0));
		boardBuilder.setPiece(new Knight(PlayerColor.BLACK, 1));
		boardBuilder.setPiece(new Bishop(PlayerColor.BLACK, 2));
		boardBuilder.setPiece(new Queen (PlayerColor.BLACK, 3));
		boardBuilder.setPiece(new King  (PlayerColor.BLACK, 4));
		boardBuilder.setPiece(new Bishop(PlayerColor.BLACK, 5));
		boardBuilder.setPiece(new Knight(PlayerColor.BLACK, 6));
		boardBuilder.setPiece(new Rook  (PlayerColor.BLACK, 7));
		for (int i = 8; i < 16; i++) {
			boardBuilder.setPiece(new Pawn(PlayerColor.BLACK, i));
			boardBuilder.setPiece(new Pawn(PlayerColor.WHITE, i + 40));
		}
		boardBuilder.setPiece(new Rook  (PlayerColor.WHITE, 56));
		boardBuilder.setPiece(new Knight(PlayerColor.WHITE, 57));
		boardBuilder.setPiece(new Bishop(PlayerColor.WHITE, 58));
		boardBuilder.setPiece(new Queen (PlayerColor.WHITE, 59));
		boardBuilder.setPiece(new King  (PlayerColor.WHITE, 60));
		boardBuilder.setPiece(new Bishop(PlayerColor.WHITE, 61));
		boardBuilder.setPiece(new Knight(PlayerColor.WHITE, 62));
		boardBuilder.setPiece(new Rook  (PlayerColor.WHITE, 63));
		boardBuilder.setToMove(PlayerColor.WHITE);		
		return boardBuilder.build();
	}
	
	public static class Builder {
		
		Map<Integer, Piece> boardConfig = new HashMap<>();
		PlayerColor toMove;
		Pawn enPassantPawn;
		int enPassantRange;
		
		public Builder() {}
		
		public Builder setPiece(final Piece piece) {
			this.boardConfig.put(piece.getCoordinate(), piece);
			return this;
		}
		
		public Builder setEnPassantPawn(final Pawn pawn) {
			this.enPassantPawn = pawn;
			return this;
		}
		
		public Builder setEnPassantRange(final int range) {
			this.enPassantRange = range;
			return this;
		}
		
		public Builder setToMove(final PlayerColor p) {
			this.toMove = p;
			return this;
		}
		
		public Board build() {
			return new Board(this);
		}
		
	}

}
