package chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import chess.engine.PlayerColor;
import chess.engine.board.Board;
import chess.engine.board.BoardUtil;
import chess.engine.board.Move;
import chess.engine.board.Tile;
import chess.engine.board.Move.NormalCapture;
import chess.engine.board.Move.NormalMove;

public class Rook extends Piece {
	
	final private static int CANDIDATE_VECTORS[] = {
			BoardUtil.positionOf( 1,  0),
			BoardUtil.positionOf( 0,  1),
			BoardUtil.positionOf(-1,  0),
			BoardUtil.positionOf( 0, -1),
	};

	public Rook(final PlayerColor c, final int p) {
		super(c, p, PieceType.ROOK, true);
	}
	
	public Rook(final PlayerColor c, final int p, final boolean b) {
		super(c, p, PieceType.ROOK, b);
	}

	@Override
	public Collection<Move> getLegalMoves(Board board) {
		final List<Move> legalMoves = new ArrayList<>();
		for (int offset : CANDIDATE_VECTORS) {
			int candidateCoordinate = this.pieceCoordinate;
			while(BoardUtil.isValidTileCoordinate(candidateCoordinate)) {				
				if (BoardUtil.testEdgeCases(1, candidateCoordinate, offset)) {
					break;
				}				
				candidateCoordinate += offset;
				if (!BoardUtil.isValidTileCoordinate(candidateCoordinate)) {
					break;
				}
				final Tile candidateTile = board.getTile(candidateCoordinate);
				if (!candidateTile.isOccupied()) {
					legalMoves.add(new NormalMove(board, this, candidateCoordinate));
				} else {
					final Piece pieceAtDestination = candidateTile.getPiece();
					if (this.pieceColor != pieceAtDestination.getPieceColor()) {
						legalMoves.add(new NormalCapture(board, this, candidateCoordinate, pieceAtDestination));
					}
					break;
				}
			}
		}
		return Collections.unmodifiableList(legalMoves);
	}

	@Override
	public Rook movePiece(Move move) {
		return new Rook(move.getMovedPiece().getPieceColor(), move.getDestinationCoordinate(), false);
	}
	
}



