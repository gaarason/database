package gaarason.database.contracts.builder;

public interface Transaction<T> {

    boolean begin();

    boolean commit();

    boolean rollBack();

    boolean inTransaction();

    boolean transaction(Runnable runnable);
}
