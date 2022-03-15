package chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import chess.engine.PlayerColor;
import chess.engine.board.Board;
import static chess.engine.board.BoardUtil.*;
import chess.engine.board.Move;
import chess.engine.board.Tile;
import chess.engine.board.Move.*;

public class Pawn extends Piece {
	
	private final static int CANDIDATE_MOVES[] = {
			positionOf(1,  0),
			positionOf(1,  1),
			positionOf(1, -1),
			positionOf(2,  0),
	};
	
	public Pawn(final PlayerColor c, final int p) {
		super(c, p, PieceType.PAWN, true);
	}
	
	public Pawn(final PlayerColor c, final int p, final boolean b) {
		super(c, p, PieceType.PAWN, b);
	}

	@Override
	public Collection<Move> getLegalMoves(Board board) {
		final List<Move> legalMoves = new ArrayList<>();
		for (final int offset : CANDIDATE_MOVES) {
			int candidateCoordinate = this.pieceCoordinate + this.pieceColor.getDirection() * offset;
			if (isValidTileCoordinate(candidateCoordinate)) {
				if (testEdgeCases(1, this.pieceCoordinate, this.pieceColor.getDirection() * offset)) {
					continue;
				}
				final Tile candidateTile = board.getTile(candidateCoordinate);
				if (offset == positionOf(1,  0) && !candidateTile.isOccupied()) {
					Move move = new PawnMove(board, this, candidateCoordinate);
					if (this.pieceColor.isPromotionAllowed(candidateCoordinate)) {
						legalMoves.add(new PawnPromotion(move, new Queen (this.pieceColor, candidateCoordinate, false)));
						legalMoves.add(new PawnPromotion(move, new Rook  (this.pieceColor, candidateCoordinate, false)));
						legalMoves.add(new PawnPromotion(move, new Bishop(this.pieceColor, candidateCoordinate, false)));
						legalMoves.add(new PawnPromotion(move, new Knight(this.pieceColor, candidateCoordinate, false)));
					} else {
						legalMoves.add(move);
					}
				}
				if ((offset == positionOf(1,  1) || offset == positionOf(1, -1))) {
					if (candidateTile.isOccupied() && candidateTile.getPiece().getPieceColor() != this.pieceColor) {
						Move move = new PawnCapture(board, this, candidateCoordinate, candidateTile.getPiece());
						if (this.pieceColor.isPromotionAllowed(candidateCoordinate)) {
							legalMoves.add(new PawnPromotion(move, new Queen (this.pieceColor, candidateCoordinate, false)));
							legalMoves.add(new PawnPromotion(move, new Rook  (this.pieceColor, candidateCoordinate, false)));
							legalMoves.add(new PawnPromotion(move, new Bishop(this.pieceColor, candidateCoordinate, false)));
							legalMoves.add(new PawnPromotion(move, new Knight(this.pieceColor, candidateCoordinate, false)));
						} else {
							legalMoves.add(move);
						}
					} else if (board.getEnPassantPawn() != null){
						int enPassantCoordinate = board.getEnPassantPawn().getCoordinate();
						for (int i = 1; i <= board.getEnPassantRange(); i++) {
							enPassantCoordinate += BOARD_FILES * this.pieceColor.getDirection();
							if (enPassantCoordinate == candidateCoordinate) {
								legalMoves.add(new EnPassantCapture(board, this, candidateCoordinate, board.getEnPassantPawn()));
							}
						}
					}
				}
				if (offset == positionOf(2,  0) && this.canMoveTwice() && !candidateTile.isOccupied() && !board.getTile(this.pieceCoordinate + this.pieceColor.getDirection() * BOARD_FILES).isOccupied()) {
					legalMoves.add(new DoublePawnMove(board, this, candidateCoordinate));
				}
			}
		}
		return Collections.unmodifiableList(legalMoves);
	}
	
	private boolean canMoveTwice() {
		return rankOf(this.pieceCoordinate) == (this.getPieceColor() == PlayerColor.WHITE ? BOARD_RANKS - 2 : 1) && this.isFirstMove;
	}
	
	@SuppressWarnings("unused")
	private boolean canContinue(final int position, final PlayerColor color) {
		if (color == PlayerColor.WHITE) {
			return BOARD_RANKS - rankOf(position) < BOARD_RANKS / 2;
		} else {
			return rankOf(position) + 1 < BOARD_RANKS / 2;
		}
	}
	
	@Override
	public Pawn movePiece(Move move) {
		return new Pawn(move.getMovedPiece().getPieceColor(), move.getDestinationCoordinate(), false);
	}
	
}