package gaarason.database.contracts;

import gaarason.database.query.Builder;
import gaarason.database.query.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Connection {


    /**
     * Begin a fluent query against a database table.
     * @param table
     * @return \Illuminate\Database\Query\Builder
     */
    Builder table(String table);

    /**
     * Get a new raw query expression.
     * @param value
     * @return \Illuminate\Database\Query\Expression
     */
    Expression raw(String value);

    Expression raw(ArrayList value);

    /**
     * Run a select statement and return a single result.
     * @param query
     * @param bindings
     * @return mixed
     */
    Map selectOne(String query, List bindings);

    /**
     * Run a select statement against the database.
     * @param query
     * @param bindings
     * @return array
     */
    Map select(String query, List bindings);

    /**
     * Run an insert statement against the database.
     * @param query
     * @param bindings
     * @return bool
     */
    boolean insert(String query, List bindings);

    /**
     * Run an update statement against the database.
     * @param query
     * @param bindings
     * @return int
     */
    int update(String query, List bindings);

    /**
     * Run a delete statement against the database.
     * @param query
     * @param bindings
     * @return int
     */
    int delete(String query, List bindings);

    /**
     * Execute an SQL statement and return the boolean result.
     * @param query
     * @param bindings
     * @return bool
     */
    boolean statement(String query, List bindings);

    /**
     * Run an SQL statement and get the number of rows affected.
     * @param query
     * @param bindings
     * @return int
     */
    int affectingStatement(String query, List bindings);

    /**
     * Run a raw, unprepared query against the PDO connection.
     * @param query
     * @return bool
     */
    boolean unprepared(String query);

    /**
     * Prepare the query bindings for execution.
     * @param bindings
     * @return array
     */
    Map prepareBindings(List bindings);

    /**
     * Execute a Closure within a transaction.
     * @param callback
     * @param attempts 重试次数
     * @return mixed
     */
    Map transaction(Closure callback, Byte attempts) throws Throwable;

    /**
     * Start a new database transaction.
     */
    void beginTransaction();

    /**
     * Commit the active database transaction.
     */
    void commit();

    /**
     * Rollback the active database transaction.
     */
    void rollBack();

    /**
     * Get the number of active transactions.
     * @return int
     */
    Byte transactionLevel();

    /**
     * Execute the given callback in "dry run" mode.
     * @param \Closure $callback
     * @return array
     */
    Map pretend(Closure callback);

}
