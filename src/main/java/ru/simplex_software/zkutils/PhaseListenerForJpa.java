package ru.simplex_software.zkutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Phase;
import org.zkoss.bind.PhaseListener;
import org.zkoss.zkplus.spring.SpringUtil;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;

public class PhaseListenerForJpa implements PhaseListener {
    private static final Logger LOG = LoggerFactory.getLogger(PhaseListenerForJpa.class);

    @Override
    public void prePhase(Phase phase, BindContext context) {
        LOG.debug("prePhase: {}", phase.name());

        if (phase != Phase.COMMAND && phase != Phase.GLOBAL_COMMAND && phase != Phase.INITIAL_BINDING) {
            return;
        }

        Object viewModel = context.getBinder().getViewModel();
        Field[] fields = viewModel.getClass().getDeclaredFields();

        for (Field field : fields) {
            DetachableModel annotation = field.getAnnotation(DetachableModel.class);
            if (annotation == null) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object fieldValue = field.get(viewModel);
                if (!(fieldValue instanceof AbstractPersistable)) {
                    continue;
                }

                EntityManager entityManager = getEntityManager();
                Object newValue = entityManager.find(fieldValue.getClass(), ((AbstractPersistable<?>) fieldValue).getId());
                field.set(viewModel, newValue);
            } catch (IllegalAccessException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void postPhase(Phase phase, BindContext ctx) {
        LOG.debug("postPhase");
    }

    private EntityManager getEntityManager() {
        ApplicationContext applicationContext = SpringUtil.getApplicationContext();
        return applicationContext.getBean(EntityManager.class);
    }
}
