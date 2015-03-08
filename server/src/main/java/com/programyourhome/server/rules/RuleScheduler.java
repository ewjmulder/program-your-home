package com.programyourhome.server.rules;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.programyourhome.server.rules.model.PyhRule;

@Component
public class RuleScheduler implements ApplicationContextAware {

    // TODO: make configurable (1 minute as default?
    private final int ruleCheckInterval = 5000;

    private final ScheduledExecutorService checkRulesService;

    private final Set<PyhRule> rules;

    public RuleScheduler() {
        this.rules = new HashSet<>();
        this.checkRulesService = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.rules.addAll(applicationContext.getBeansOfType(PyhRule.class).values());
        // TODO: exception handling, see also InfraRedImpl
        this.checkRulesService.scheduleAtFixedRate(this::checkRules, this.ruleCheckInterval, this.ruleCheckInterval,
                TimeUnit.MILLISECONDS);
    }

    private void checkRules() {
        for (final PyhRule rule : this.rules) {
            if (rule.isTriggered()) {
                rule.executeAction();
            }
        }
    }

}
