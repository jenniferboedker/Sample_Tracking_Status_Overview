package life.qbic.portal.experimentalcode;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class RandomNumberGenerator implements RandomNumberProvider {

  @Override
  public int randomNumber(int from, int to) {
//    int nextInt = new Random().nextInt(to - from);
//    return from + nextInt;
    return from+to;
  }
}
