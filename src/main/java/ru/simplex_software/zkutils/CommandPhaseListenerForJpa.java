package ru.simplex_software.zkutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Phase;
import org.zkoss.bind.PhaseListener;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.ToClientCommand;
import org.zkoss.bind.impl.BindContextImpl;
import org.zkoss.zkplus.spring.SpringUtil;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * Слушатель команд.
 * Перезагружает поля ViewModel и аргументы команды, помеченные аннотацией Reload.
 */
public class CommandPhaseListenerForJpa implements PhaseListener {
    private static final Logger LOG = LoggerFactory.getLogger(CommandPhaseListenerForJpa.class);

    @Override
    public void prePhase(Phase phase, BindContext context) {
        LOG.debug("prePhase: {}", phase.name());

        if (phase != Phase.COMMAND) {
            return;
        }

        Object viewModel = context.getBinder().getViewModel();
        BindContextImpl bindContext = (BindContextImpl) context;

        // Если это команда для клиента, то не обрабатываем ее.
        if (isToClientCommand(viewModel.getClass(), bindContext.getCommandName())) {
            return;
        }

        try {
            // Аннотированный метод.
            Method command = getCommandMethod(viewModel.getClass(), bindContext.getCommandName());

            // Перезагрузка полей.
            if (command.isAnnotationPresent(Reload.class)) {
                String[] fieldNames = command.getAnnotation(Reload.class).value();
                for (String fieldName : fieldNames) {
                    Field field = viewModel.getClass().getDeclaredField(fieldName);

                    field.setAccessible(true);
                    Object fieldValue = field.get(viewModel);
                    if (!(fieldValue instanceof AbstractPersistable)) {
                        continue;
                    }

                    // Перезагрузка поля.
                    EntityManager entityManager = getEntityManager();
                    Object newValue = entityManager.find(fieldValue.getClass(), ((AbstractPersistable<?>) fieldValue).getId());
                    field.set(viewModel, newValue);
                }
            }

            // Перезагрузка параметров команды.
            for (Parameter parameter : command.getParameters()) {
                if (parameter.isAnnotationPresent(Reload.class) && parameter.isAnnotationPresent(BindingParam.class)) {
                    String paramName = parameter.getAnnotation(BindingParam.class).value();
                    Object paramValue = bindContext.getCommandArg(paramName);

                    if (!(paramValue instanceof AbstractPersistable)) {
                        continue;
                    }

                    // Перезагрузка параметра.
                    EntityManager entityManager = getEntityManager();
                    Object newValue = entityManager.find(paramValue.getClass(), ((AbstractPersistable<?>) paramValue).getId());
                    bindContext.getCommandArgs().put(paramName, newValue);
                }
            }
        } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void postPhase(Phase phase, BindContext ctx) {
        LOG.debug("postPhase: {}", phase.name());
    }

    private EntityManager getEntityManager() {
        ApplicationContext applicationContext = SpringUtil.getApplicationContext();
        return applicationContext.getBean(EntityManager.class);
    }

    /**
     * Получение метода команды.
     *
     * @param viewModelClass класс ViewModel.
     * @param commandName    имя команды.
     * @return метод команды.
     * @throws NoSuchMethodException если метод не найден.
     */
    private Method getCommandMethod(Class<?> viewModelClass, String commandName) throws NoSuchMethodException {
        return Arrays.stream(viewModelClass.getDeclaredMethods())
                .filter(method -> method.getName().equals(commandName))
                .findFirst()
                .orElseThrow(() -> new NoSuchMethodException("Command " + commandName + " not found in " + viewModelClass));
    }

    /**
     * Являтся ли команда командой для клиента.
     *
     * @param viewModelClass класс ViewModel с командой.
     * @param commandName    имя команды.
     * @return являтся ли команда командой для клиента.
     */
    private boolean isToClientCommand(Class<?> viewModelClass, String commandName) {
        ToClientCommand toClientCommand = viewModelClass.getAnnotation(ToClientCommand.class);
        if (toClientCommand != null) {
            return Arrays.asList(toClientCommand.value()).contains(commandName);
        }
        return false;
    }
}
