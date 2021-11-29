import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import search.*;

// A* search

public class AStar<S, A> {
  public static <S, A> Solution<S, A> search(HeuristicProblem<S, A> prob) {
    S init_state = prob.initialState();
    AStarNode<S, A> init_node = new AStarNode<S,A>(init_state, null, null, 0.0, prob.estimate(init_state));

    HashSet<S> explored = new HashSet<>();

    // Estimates of the shortest distances from the start to some discovered state S
    HashMap<S, Double> dist = new HashMap<>();
    dist.put(init_state, 0.0);

    // Memoized h-costs of the states
    // Computing the heuristic function is costly, so compute it only once
    HashMap<S, Double> h_costs = new HashMap<>(); 
    h_costs.put(init_state, prob.estimate(init_state));

    PriorityQueue<AStarNode<S, A>> frontier = new PriorityQueue<>();
    frontier.add(init_node);

    AStarNode<S, A> current;
    AStarNode<S, A> goal = null;

    while (!frontier.isEmpty()) {
      current = frontier.poll();

      if (explored.contains(current.s)) {
        continue;
      }

      // In the Grid.java test, the duplicity ratio is somewhere around 1.28
      // suggesting that there aren't many duplicit nodes in the frontier at any given time
      // System.out.println(getDuplicityRatio(frontier));

      if (prob.isGoal(current.s)) {
          // we are closing the goal state (or at-least its first copy in the queue)
          goal = current;
          break;
      }

      // Warning! If heuristic is not admissible, this may lead to an infinite loop
      explored.add(current.s);

      for (A action : prob.actions(current.s)) {
        S next_state = prob.result(current.s, action);
        Double next_cost = current.g_cost + prob.cost(current.s, action);

        if (!dist.containsKey(next_state) || next_cost < dist.get(next_state)) {
          dist.put(next_state, next_cost);

          // We don't decrease key here, because java's PriorityQueue doesn't support it
          // We simply insert a copy of the node
          AStarNode<S, A> next_node = new AStarNode<S, A>(next_state, current, action, next_cost, getHCost(next_state, h_costs, prob));
          frontier.add(next_node);
        }
      }
    }

    if (goal == null) {
      return null;
    }

    return generateSolution(goal);

  }


  // memoized heuristic costs
  private static <S, A> Double getHCost(S s, HashMap<S, Double> h_costs, HeuristicProblem<S, A> prob) {
    if (!h_costs.containsKey(s)) {
      h_costs.put(s, prob.estimate(s));
    }
    return h_costs.get(s);
  }


  private static <S, A> Solution<S, A> generateSolution(AStarNode<S, A> goal) {

    AStarNode<S, A> current = goal;
    ArrayList<A> actions = new ArrayList<A>();

    while(true) {
      if (current.comeFrom == null) {
        break;
      }

      actions.add(current.comeFromAction);
      current = current.comeFrom;
    }


    Collections.reverse(actions);

    return new Solution<S,A>(actions, goal.s, goal.g_cost);
  }


  private static <S, A> double getDuplicityRatio(PriorityQueue<AStarNode<S, A>> frontier) {
    if (frontier.size() == 0)
      return 1;
    HashSet<S> unique = new HashSet<S>();

    for (var node : frontier) {
      unique.add(node.s);
    }

    System.out.println("frontier: " + frontier.size() + ", unique: " + unique.size());

    return (double)frontier.size() / unique.size();
  } 
}
