package com.brpratt.diceroll;

import org.springframework.stereotype.Component;

import io.kubernetes.client.extended.controller.reconciler.Reconciler;
import io.kubernetes.client.extended.controller.reconciler.Request;
import io.kubernetes.client.extended.controller.reconciler.Result;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformer;
import io.kubernetes.client.informer.cache.Lister;

@Component
public class DiceRollReconciler implements Reconciler {

    @SuppressWarnings("unused")
    private SharedInformer<DiceRoll> diceRollInformer;

    private Lister<DiceRoll> diceRollLister;

    public DiceRollReconciler(SharedIndexInformer<DiceRoll> diceRollInformer) {
        this.diceRollInformer = diceRollInformer;
        this.diceRollLister = new Lister<>(diceRollInformer.getIndexer());
    }

    @Override
    public Result reconcile(Request request) {
        System.out.println("Reconciling " + request.toString());

        var diceRoll = diceRollLister
            .namespace(request.getNamespace())
            .get(request.getName());

        diceRoll.getSpec().getDice().forEach(System.out::println);

        if (needsToBeRolled(diceRoll)) {
            System.out.println("needs to be rolled");
        }

        return new Result(false);
    }

    
    private boolean needsToBeRolled(DiceRoll diceRoll) {      
        if (diceRoll.getStatus() == null) {
            return true;
        }

        var dice = diceRoll.getSpec().getDice();
        var results = diceRoll.getStatus().getResults();

        if (dice.size() != results.size()) {
            return true;
        }

        for (int i = 0; i < dice.size(); i++) {
            if (!dice.get(i).equals(results.get(i).getDie())) {
                return true;
            }
        }

        return false;
    }
}
