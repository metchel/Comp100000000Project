package Middleware;

/**
 * Interface that provides a contract for implementation of the Middleware
 *
 * TODO: define contracts for methods to properly handle transactions, crashes of ResourceManager servers ... and more that we don't know about yet
 */
public interface IMiddleware implements Runnable {

    /**
     * Initialize hosts addresses and ports for each of the ResourceManager servers
     *
     * @param flightHost
     * @param flightPort
     * @param carHost
     * @param carPort
     * @param roomHost
     * @param roomPort
     */
    void initMiddleware(String flightHost, int flightPort, String carHost, int carPort, String roomHost, int roomPort);
}