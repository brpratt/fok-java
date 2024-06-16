package com.brpratt.diceroll;

import java.util.Random;

import org.springframework.stereotype.Component;

import io.kubernetes.client.extended.controller.reconciler.Reconciler;
import io.kubernetes.client.extended.controller.reconciler.Request;
import io.kubernetes.client.extended.controller.reconciler.Result;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.cache.Lister;
import io.kubernetes.client.util.generic.GenericKubernetesApi;

@Component
public class DiceRollReconciler implements Reconciler {

    private GenericKubernetesApi<DiceRoll, DiceRollList> genericApi;

    private Lister<DiceRoll> diceRollLister;

    private Random random;

    public DiceRollReconciler(GenericKubernetesApi<DiceRoll, DiceRollList> genericApi, SharedIndexInformer<DiceRoll> diceRollInformer) {
        this.genericApi = genericApi;
        this.diceRollLister = new Lister<>(diceRollInformer.getIndexer());
        this.random = new Random();
    }

    @Override
    public Result reconcile(Request request) {
        var diceRoll = diceRollLister
            .namespace(request.getNamespace())
            .get(request.getName());

        if (diceRoll == null || diceRoll.getMetadata().getDeletionTimestamp() != null) {
            return new Result(false);
        }

        if (needsToBeRolled(diceRoll)) {
            roll(diceRoll);
            genericApi.updateStatus(diceRoll, roll -> roll.getStatus());
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

    private void roll(DiceRoll roll) {
        var dice = roll.getSpec().getDice();
        var results = dice.stream()
            .map(die -> roll.new Result(die, roll(die)))
            .toList();
        var total = results.stream()
            .mapToInt(r -> r.getValue())
            .sum();

        if (roll.getStatus() == null) {
            roll.setStatus(roll.new Status());
        }

        roll.getStatus().setTotal(total);
        roll.getStatus().setResults(results);
    }

    private int roll(String die) {
        return switch (die) {
            case "D4" -> random.nextInt(1, 5);
            case "D6" -> random.nextInt(1, 7);
            case "D8" -> random.nextInt(1, 9);
            case "D10" -> random.nextInt(1, 11);
            case "D12" -> random.nextInt(1, 13);
            case "D20" -> random.nextInt(1, 21);
            case "D100" -> random.nextInt(1, 101);
            default -> 1;
        };
    }
}
