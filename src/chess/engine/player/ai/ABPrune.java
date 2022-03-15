package chess.engine.player.ai;

import chess.engine.PlayerColor;
import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.player.MoveTransition;

public class ABPrune implements MoveStrategy {

	private final BoardEvaluator evaluator;
	
	public ABPrune() {
		this.evaluator = new StandardBoardEvaluator();
	}
	
	@Override
	public String toString() {
		return "Alpha Beta Pruning";
	}
	
	private static boolean isEndGameScenario(final Board board) {
		return board.getCurrentPlayer().isInCheckmate() || board.getCurrentPlayer().isInStalemate();
	}
	
	@Override
	public Move execute(Board board, int depth) {
		final long startTime = System.currentTimeMillis();
		Move bestMove = null;
		double highestSeen = -Double.MAX_VALUE;
		double lowestSeen = Double.MAX_VALUE;
		double currentValue;
		System.out.println(board.getCurrentPlayer() + " thinking with depth " + depth);
		int numMoves = board.getCurrentPlayer().getLegalMoves().size();
		for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
			final MoveTransition transition = board.getCurrentPlayer().makeMove(move);
			if (transition.getMoveStatus().isDone()) {
				currentValue = board.getCurrentPlayer().getColor() == PlayerColor.WHITE ?
							   alphabeta(transition.getTransitionBoard(), depth - 1, -highestSeen, lowestSeen, false) :
				   			   alphabeta(transition.getTransitionBoard(), depth - 1, -highestSeen, lowestSeen, true);
				if (board.getCurrentPlayer().getColor() == PlayerColor.WHITE && currentValue > highestSeen) {
					highestSeen = currentValue;
					bestMove = move;
				} else if (board.getCurrentPlayer().getColor() == PlayerColor.BLACK && currentValue < lowestSeen) {
					lowestSeen = currentValue;
					bestMove = move;
				}
			}
		}
		final long executionTime = System.currentTimeMillis() - startTime;
		return bestMove;
	}
	
	private double alphabeta(final Board board, final int depth, double a, double b, final boolean isMaximizingPlayer) {
		if (depth <= 0 || isEndGameScenario(board)) {
			return this.evaluator.evaluate(board, depth);
		}
		if (isMaximizingPlayer) {
			double highestSeen = -Double.MAX_VALUE;
			for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
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
			for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
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
