package chess.engine.player.ai;

import static chess.engine.board.BoardUtil.*;
import chess.engine.PlayerColor;
import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.pieces.Pawn;
import chess.engine.pieces.Piece;
import chess.engine.pieces.PieceType;
import chess.engine.pieces.Queen;
import chess.engine.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator {
	
	public final static double MOBILITY_BONUS = 0.12;
	public final static double CHECK_BONUS = 0.20;
	public final static double CHECKMATE_BONUS = 255.0;
	public final static double PAWN_SPACE_BONUS = 0.07;
	public final static double DEVELOPMENT_BONUS = 0.17;
	public final static double PAWN_STRUCTURE_BONUS = 0.09;
	
	public StandardBoardEvaluator() {}
	
	@Override
	public double evaluate(final Board board, final int depth) {
		if (stalemate(board)) {
			return 0;
		}
		return scorePlayer(board, board.getWhitePlayer(), depth) - scorePlayer(board, board.getBlackPlayer(), depth);
	}

	private double scorePlayer(Board board, Player player, int depth) {
		return pieceValue(player) + mobility(player) + pawnSpace(player) + development(player) + pawnStructure(player) + check(player) + checkmate(player, depth);
	}
	
	private static double pieceValue(final Player player) {
		double pieceValueScore = 0;
		for (final Piece piece : player.getPieces()) {
			pieceValueScore += piece.getPieceType().getValue();
		}
		return pieceValueScore;
	}
	
	private static double pawnSpace (final Player player) {
		double result = 0.0;
		if (player.getColor() == PlayerColor.WHITE) {
			for (final Piece piece : player.getPieces()) {
				if (piece.getPieceType() == PieceType.PAWN) {
					result += (BOARD_RANKS - 1 - rankOf(piece.getCoordinate())) * PAWN_SPACE_BONUS;
				}
			}
		} else {
			for (final Piece piece : player.getPieces()) {
				if (piece.getPieceType() == PieceType.PAWN) {
					result += (rankOf(piece.getCoordinate())) * PAWN_SPACE_BONUS;
				}
			}
		}
		return result;
	}
	
	private static double mobility(final Player player) {
		double total = 0;
		for (final Move move : player.getLegalMoves()) {
			if (!(move.getMovedPiece().getPieceType() == PieceType.QUEEN)) {
				total += 1.0 / (double) move.getMovedPiece().getPieceType().getValue();
			}
		}
		return total * MOBILITY_BONUS;
	}
	
	private static double development(Player player) {
		double total = 0;
		for (final Piece piece : player.getPieces()) {
			if ((piece.getPieceType() == PieceType.KNIGHT || piece.getPieceType() == PieceType.BISHOP)) {
				if (rankOf(piece.getCoordinate()) > 0 && rankOf(piece.getCoordinate()) < BOARD_RANKS - 1) {
					total += DEVELOPMENT_BONUS;
				}
				if (rankOf(piece.getCoordinate()) > 1 && rankOf(piece.getCoordinate()) < BOARD_RANKS - 2) {
					total += DEVELOPMENT_BONUS;
				}
				if (fileOf(piece.getCoordinate()) > 0 && fileOf(piece.getCoordinate()) < BOARD_RANKS - 1) {
					total += DEVELOPMENT_BONUS;
				}
			}
		}
		return total;
	}
	
	private static double pawnStructure(final Player player) {
		byte pawnScores[] = new byte[BOARD_FILES];
		for (final Piece piece : player.getPieces()) {
			if (piece.getPieceType() == PieceType.PAWN) {
				pawnScores[fileOf(piece.getCoordinate())] += 1;
			}
		}
		double total = 0.0;
			for (int i = 0; i < BOARD_FILES - 1; i++) {
				total += pawnScores[i] * pawnScores[i+1];
			}
		return total * PAWN_STRUCTURE_BONUS;
		
	}
	
	private static double check(final Player player) {
		return player.getOpponent().isInCheck() ? CHECK_BONUS : 0.0;
	}
	
	private static double checkmate(final Player player, final int depth) {
		return player.getOpponent().isInCheckmate() ? CHECKMATE_BONUS * (depth + 1) : 0.0;
	}
	
	private static boolean stalemate(final Board board) {
		return board.getCurrentPlayer().isInStalemate();
	}

}
