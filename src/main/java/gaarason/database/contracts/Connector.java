package gaarason.database.contracts;


import gaarason.database.pojo.config.ConnectionDTO;

public interface Connector {

    void connect(ConnectionDTO config);

}
