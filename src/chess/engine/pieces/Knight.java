package chess.engine.pieces;

import static chess.engine.board.Move.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import chess.engine.PlayerColor;
import chess.engine.board.Board;
import static chess.engine.board.BoardUtil.*;
import chess.engine.board.Move;
import chess.engine.board.Tile;

public class Knight extends Piece {
	
	final private static int CANDIDATE_MOVES[] = {
			positionOf( 2,  1),
			positionOf( 1,  2),
			positionOf(-2,  1),
			positionOf(-1,  2),
			positionOf( 2, -1),
			positionOf( 1, -2),
			positionOf(-2, -1),
			positionOf(-1, -2),
	};
	
	public Knight(final PlayerColor c, final int p) {
		super(c, p, PieceType.KNIGHT, true);
	}
	
	public Knight(final PlayerColor c, final int p, final boolean b) {
		super(c, p, PieceType.KNIGHT, b);
	}

	@Override
	public Collection<Move> getLegalMoves(final Board board) {
		final List<Move> legalMoves = new ArrayList<>();
		for (final int offset : CANDIDATE_MOVES) {
			int candidateCoordinate = this.pieceCoordinate + offset;
			if (isValidTileCoordinate(candidateCoordinate)) {
				if (testEdgeCases(2, this.pieceCoordinate, offset)) {
					continue;
				}
				final Tile candidateTile = board.getTile(candidateCoordinate);
				if (!candidateTile.isOccupied()) {
					legalMoves.add(new NormalMove(board, this, candidateCoordinate));
				} else {
					final Piece pieceAtDestination = candidateTile.getPiece();
					if (this.pieceColor != pieceAtDestination.getPieceColor()) {
						legalMoves.add(new NormalCapture(board, this, candidateCoordinate, pieceAtDestination));
					}
				}
			}
		}
		return Collections.unmodifiableList(legalMoves);
	}

	@Override
	public Knight movePiece(Move move) {
		return new Knight(move.getMovedPiece().getPieceColor(), move.getDestinationCoordinate(), false);
	}
	
}
