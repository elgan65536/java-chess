package chess.engine.player.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import chess.engine.PlayerColor;
import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.player.MoveTransition;

public class ModifiedABPrune implements MoveStrategy {

	private final BoardEvaluator evaluator = new StandardBoardEvaluator();
	
	private final Comparator<Move> simpleComparator = new Comparator<Move>() {
		@Override
		public int compare(Move move1, Move move2) {
			return (move1.isCapture() ? -1 : 1) + 
				   (move2.isCapture() ? 1 : -1);
		}
	};
	private final Comparator<Move> complexComparator = new Comparator<Move>() {
		@Override
		public int compare(Move move1, Move move2) {
			final MoveTransition transition1 = move1.getBoard().getCurrentPlayer().makeMove(move1);
			final MoveTransition transition2 = move1.getBoard().getCurrentPlayer().makeMove(move2);
			if (!(transition1.getMoveStatus().isDone() && transition2.getMoveStatus().isDone())) {
				return (transition1.getMoveStatus().isDone() ? -1 : 1) + 
					   (transition2.getMoveStatus().isDone() ? 1 : -1);
			}
			return (move1.isPromotion() ? -4 : 4) +
				   (move2.isPromotion() ? 4 : -4) +
				   (move1.isCheck() ? -2 : 2) + 
				   (move2.isCheck() ? 2 : -2) +
				   (move1.isCapture() ? -1 : 1) +
				   (move2.isCapture() ? 1 : -1) +
				   (move1.getBoard().getCurrentPlayer().getColor() == PlayerColor.WHITE ? 
				   (int) Math.signum(evaluator.evaluate(move2.execute(), 0) - evaluator.evaluate(move1.execute(), 0)) :
				   (int) Math.signum(evaluator.evaluate(move1.execute(), 0) - evaluator.evaluate(move2.execute(), 0)));
		}
	};
		
	@Override
	public String toString() {
		return "Modified Alpha Beta Pruning";
	}
	
	private static boolean isEndGameScenario(final Board board) {
		return board.getCurrentPlayer().isInCheckmate() || board.getCurrentPlayer().isInStalemate();
	}
	
	private Collection<Move> sortMoves(Collection<Move> moves, boolean isComplex) {
		List<Move> movesList = new ArrayList<>(moves);
		Collections.sort(movesList, isComplex ? complexComparator : simpleComparator);
		return movesList;
	}
	
	@Override
	public Move execute(Board board, int depth) {
		Move bestMove = null;
		double highestSeen = -Double.MAX_VALUE;
		double lowestSeen = Double.MAX_VALUE;
		double currentValue;
		System.out.println(board.getCurrentPlayer() + " thinking with depth " + depth);
		for (final Move move : sortMoves(board.getCurrentPlayer().getLegalMoves(), true)) {
			final MoveTransition transition = board.getCurrentPlayer().makeMove(move);
			if (transition.getMoveStatus().isDone()) {
				currentValue = board.getCurrentPlayer().getColor() == PlayerColor.WHITE ?
							   alphabeta(transition.getTransitionBoard(), depth - 1, highestSeen, lowestSeen, false) :
				   			   alphabeta(transition.getTransitionBoard(), depth - 1, highestSeen, lowestSeen, true);
				if (board.getCurrentPlayer().getColor() == PlayerColor.WHITE && currentValue > highestSeen) {
					highestSeen = currentValue;
					bestMove = move;
				} else if (board.getCurrentPlayer().getColor() == PlayerColor.BLACK && currentValue < lowestSeen) {
					lowestSeen = currentValue;
					bestMove = move;
				}
			}
		}
//		System.out.println(board.getCurrentPlayer().getColor() == PlayerColor.WHITE ? highestSeen : lowestSeen);
		return bestMove;
	}
	
	private double alphabeta(final Board board, final int depth, double a, double b, final boolean isMaximizingPlayer) {
		if (depth <= 0 || isEndGameScenario(board)) {
			return this.evaluator.evaluate(board, depth);
		} 
		if (isMaximizingPlayer) {
			double highestSeen = -Double.MAX_VALUE;
			for (final Move move : sortMoves(board.getCurrentPlayer().getLegalMoves(), false)) {
				final MoveTransition transition = board.getCurrentPlayer().makeMove(move);
				if (transition.getMoveStatus().isDone()) {
					double value = alphabeta(transition.getTransitionBoard(), depth - 1, a, b, false);
					if (value > highestSeen) {
						highestSeen = value;
					}
					if (highestSeen >= b) {
						break;
					}
					if (highestSeen > a) {
						a = highestSeen;
					}
				}
			}
			return highestSeen;
		} else {
			double lowestSeen = Double.MAX_VALUE;
			for (final Move move : sortMoves(board.getCurrentPlayer().getLegalMoves(), false)) {
				final MoveTransition transition = board.getCurrentPlayer().makeMove(move);
				if (transition.getMoveStatus().isDone()) {
					double value = alphabeta(transition.getTransitionBoard(), depth - 1, a, b, true);
					if (value < lowestSeen) {
						lowestSeen = value;
					}
					if (lowestSeen <= a) {
						break;
					}
					if (lowestSeen < b) {
						b = lowestSeen;
					}
				}
			}
			return lowestSeen;
		}
	}
	
}
