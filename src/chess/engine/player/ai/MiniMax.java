package chess.engine.player.ai;

import chess.engine.PlayerColor;
import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.player.MoveTransition;

public class MiniMax implements MoveStrategy {
	
	private final BoardEvaluator evaluator;
	
	public MiniMax() {
		this.evaluator = new StandardBoardEvaluator();
	}
	
	@Override
	public String toString() {
		return "MiniMax";
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
							   min(transition.getTransitionBoard(), depth - 1) :
							   max(transition.getTransitionBoard(), depth - 1);
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
	
	public double min(final Board board, final int depth) {
		if (depth <= 0 || isEndGameScenario(board)) {
			return this.evaluator.evaluate(board, depth);
		}
		double lowestSeen = Double.MAX_VALUE;		
		for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
			final MoveTransition transition = board.getCurrentPlayer().makeMove(move);
			if (transition.getMoveStatus().isDone()) {
				final double currentValue = max(transition.getTransitionBoard(), depth - 1);
				if (currentValue < lowestSeen) {
					lowestSeen = currentValue;
				}
			}
		}
		return lowestSeen;
	}
	
	public double max(final Board board, final int depth) {
		if (depth <= 0 || isEndGameScenario(board)) {
			return this.evaluator.evaluate(board, depth);
		}
		double highestSeen = -Double.MAX_VALUE;
		for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
			final MoveTransition transition = board.getCurrentPlayer().makeMove(move);
			if (transition.getMoveStatus().isDone()) {
				final double currentValue = min(transition.getTransitionBoard(), depth - 1);
				if (currentValue > highestSeen) {
					highestSeen = currentValue;
				}
			}
		}
		return highestSeen;
	}
	
}
