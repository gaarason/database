package gaarason.database.contracts;

public interface ConnectionResolver {


    /**
     * Get a database connection instance.
     * @param name connection name
     * @return Connection
     */
    Connection connection(String name);

    /**
     * Get the default connection name.
     * @return string
     */
    String getDefaultConnection();

    /**
     * Set the default connection name.
     * @param name connection name
     */
    void setDefaultConnection(String name);

}
