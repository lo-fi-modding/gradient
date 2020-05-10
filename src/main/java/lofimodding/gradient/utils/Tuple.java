package lofimodding.gradient.utils;

public class Tuple<A, B> {
  public final A a;
  public final B b;

  public Tuple(final A a, final B b) {
    this.a = a;
    this.b = b;
  }

  @Override
  public boolean equals(final Object other) {
    if(this == other) {
      return true;
    }

    if(!(other instanceof Tuple)) {
      return false;
    }

    return this.a.equals(((Tuple)other).a) && this.b.equals(((Tuple)other).b);
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + this.a.hashCode();
    result = 31 * result + this.b.hashCode();
    return result;
  }
}
