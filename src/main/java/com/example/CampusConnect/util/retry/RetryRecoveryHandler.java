
package com.example.CampusConnect.util.retry;

import jakarta.persistence.OptimisticLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Component;

@Component
public class RetryRecoveryHandler {

    @Recover
    public RuntimeException recover(ObjectOptimisticLockingFailureException ex) {
        return new RuntimeException(
                "Concurrent update detected. Please retry the request.",
                ex
        );
    }

    @Recover
    public RuntimeException recover(OptimisticLockException ex) {
        return new RuntimeException(
                "Concurrent update detected. Please retry the request.",
                ex
        );
    }
}
