package com.demo.osivdemo.util;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.demo.osivdemo.OsivDemoConfig.DEMO_EXECUTOR;

@Component
public class SessionUtils {

    public static final Logger logger = LoggerFactory.getLogger(SessionUtils.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DataSource dataSource;

    private final SessionFactory sessionFactory;

    private final ThreadPoolTaskExecutor asyncSessionTaskExecutor;

    public SessionUtils(EntityManagerFactory entityManagerFactory,
                        @Qualifier(DEMO_EXECUTOR) ThreadPoolTaskExecutor demoExecutor) {
        this.sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        this.asyncSessionTaskExecutor = demoExecutor;
    }

    public Session createNewSession() {
        return sessionFactory.openSession();
    }

    public int getActiveConnections() {
        return getHikariPoolMXBean().getActiveConnections();
    }

    public void logActiveConnections() {
        logger.debug("ActiveConnections: {}", getActiveConnections());
    }

    private HikariPoolMXBean getHikariPoolMXBean() {
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        return hikariDataSource.getHikariPoolMXBean();
    }



    /**
     * Executes a function in a separate thread to avoid attaching a DB Connection to this thread's
     * session.
     *
     * <p>Note that this async session will not be included in any existing transactions. Entities
     * returned from this async session will need to be merged into the request thread's session,
     * which will trigger a database call.
     *
     * <p>Prefer usage of createNewSession() unless your code requires interacting with JPA
     * Repositories.
     *
     * <p>Spring's default enabled Open-In-View filter causes a new Session to be tied to the
     * executing http-nio thread via ThreadLocal variables. The first JPA repository call made
     * servicing the request will permanently attach the database connection to the Session until the
     * request finishes processing. Using this method allows executing the JPA repository call on a
     * separate thread that will immediately release the connection, thus allowing us to avoid
     * prematurely attaching a database connection to the request's session. For basic Hibernate
     * Session related DB access, you should prefer to just open a new Hibernate Session via the
     * createNewSession() operation to avoid the async thread overhead.
     */
    public <T> T executeInAsyncSession(Supplier<T> supplier) {
        Callable<T> callable = supplier::get;
        return this.executeInAsyncSession(callable);
    }

    /**
     * Executes a function requiring a Session in a separate thread to avoid attaching a DB Connection
     * to this thread's session.
     *
     * <p>Note that this async session will not be included in any existing transactions. Entities
     * returned from this async session will need to be merged into the request thread's session,
     * which will trigger a database call.
     *
     * <p>Prefer usage of createNewSession() unless your code requires interacting with JPA
     * Repositories.
     *
     * <p>Spring's default enabled Open-In-View filter causes a new Session to be tied to the
     * executing http-nio thread via ThreadLocal variables. The first JPA repository call made
     * servicing the request will permanently attach the database connection to the Session until the
     * request finishes processing. Using this method allows executing the JPA repository call on a
     * separate thread that will immediately release the connection, thus allowing us to avoid
     * prematurely attaching a database connection to the request's session. For basic Hibernate
     * Session related DB access, you should prefer to just open a new Hibernate Session via the
     * createNewSession() operation to avoid the async thread overhead.
     */
    public <T> T executeInAsyncSession(Function<Session, T> function) {
        Callable<T> callable =
                () -> {
                    Session session = sessionFactory.openSession();
                    try {
                        session.beginTransaction();
                        T response = function.apply(session);
                        session.getTransaction().commit();
                        return response;
                    } catch (RuntimeException e) {
                        session.getTransaction().rollback();
                        throw e;
                    } finally {
                        session.close();
                    }
                };

        return this.executeInAsyncSession(callable);
    }

    private <T> T executeInAsyncSession(Callable<T> callable) {
        try {
            return asyncSessionTaskExecutor.submit(callable).get();
        } catch (InterruptedException e) {
            logger.error("Interrupted while processing callable in async session", e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Interrupted processing in async session", e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException) {
                logger.warn(
                        "Rethrowing runtime exception that occurred while processing async callable", e);
                throw (RuntimeException) e.getCause();
            }
            logger.error("Error processing async session callable", e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error processing in async session", e);
        }
    }
}
