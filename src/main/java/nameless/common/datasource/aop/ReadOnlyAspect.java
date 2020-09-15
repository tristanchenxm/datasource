package nameless.common.datasource.aop;

import nameless.common.datasource.MasterSlaveDualConnectionDataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ReadOnlyAspect {
    private final Logger logger = LoggerFactory.getLogger(ReadOnlyAspect.class);
    @Around("@annotation(nameless.common.datasource.aop.ReadOnly) || @within(nameless.common.datasource.aop.ReadOnly)")
    public Object readOnlyAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("In readonly context of joint point: " + joinPoint.getSignature().toString());
            }
            MasterSlaveDualConnectionDataSource.readOnly();
            return joinPoint.proceed();
        } finally {
            MasterSlaveDualConnectionDataSource.clearContext();
        }
    }
}
