import static java.lang.System.out;
import java.util.ArrayList;
import java.util.List;

import agents.ArtificialAgent;
import game.actions.EDirection;
import game.actions.compact.*;
import game.actions.oop.EActionType;
import game.board.compact.BoardCompact;
import search.HeuristicProblem;
import search.Solution;

public class MyAgent extends ArtificialAgent {
	protected int searchedNodes;
	protected boolean[][] deadSquares;
	
	@Override
	protected List<EDirection> think(BoardCompact board) {
		searchedNodes = 0;
		
		HeuristicProblem<BoardCompact, CAction> problem = new HeuristicProblem<BoardCompact, CAction>() {
			public double estimate(BoardCompact state) {
				return 0;
			}

			@Override
			public BoardCompact initialState() {
				return board.clone();
			}

			@Override
			public List<CAction> actions(BoardCompact state) {
				return getActions(state);
			}

			@Override
			public BoardCompact result(BoardCompact state, CAction action) {
				searchedNodes++;
				var new_state = state.clone();
				action.perform(new_state);

				// Reverse the action if box was pushed to a dead square
				if (action.getType() == EActionType.PUSH) {
					int boxX = new_state.playerX + action.getDirection().dX;
					int boxY = new_state.playerY + action.getDirection().dY;
					if (deadSquares[boxX][boxY]) {
						action.reverse(new_state);
					}
				}

				return new_state;
			}

			@Override
			public boolean isGoal(BoardCompact state) {
				return state.isVictory();
			}

			@Override
			public double cost(BoardCompact state, CAction action) {
				return 1;
			}
		};

		deadSquares = DeadSquareDetector.detect(board);

		long searchStartMillis = System.currentTimeMillis();
		Solution<BoardCompact, CAction> solution = AStar.search(problem);
		long searchTime = System.currentTimeMillis() - searchStartMillis;

		if (solution == null) {
			out.println("No solution found!");
			return null;
		}

		List<EDirection> result = new ArrayList<EDirection>();
		for (CAction action : solution.actions) {
			result.add(action.getDirection());
		}

        if (verbose) {
			out.println("Search time: " + searchTime + " ms");
            out.println("Nodes visited: " + searchedNodes);
            out.printf("Performance: %.1f nodes/sec\n",
                        ((double)searchedNodes / (double)searchTime * 1000));
        }
		
		return result;
	}


	private List<CAction> getActions(BoardCompact state) {
		List<CAction> actions = new ArrayList<CAction>(4);
		
		for (CMove move : CMove.getActions()) {
			if (move.isPossible(state)) {
				actions.add(move);
			}
		}
		for (CPush push : CPush.getActions()) {
			if (push.isPossible(state)) {
				actions.add(push);
			}
		}

		return actions;
	}
}
