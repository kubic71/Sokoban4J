
public class AStarNode<S, A> implements Comparable<AStarNode<S, A>> {


  public A comeFromAction;
  public S s;
  public AStarNode<S, A> comeFrom;
  public Double g_cost;
  public Double f_cost;

  @Override
  public int compareTo(AStarNode<S, A> other) {
    return f_cost.compareTo(other.f_cost);
  }

  public AStarNode(S s, AStarNode<S, A> comeFrom, A comeFromAction, Double g_cost, Double h_cost) {
    this.s = s;
    this.comeFrom = comeFrom;
    this.comeFromAction = comeFromAction;
    this.g_cost = g_cost;
    this.f_cost = g_cost + h_cost;
  }


  


}