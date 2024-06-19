package com.brpratt.diceroll;

import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import java.util.List;

public class DiceRoll implements KubernetesObject {
  private String apiVersion;
  private String kind;
  private V1ObjectMeta metadata;
  private Spec spec;
  private Status status;

  public String getApiVersion() {
    return apiVersion;
  }

  public String getKind() {
    return kind;
  }

  public V1ObjectMeta getMetadata() {
    return metadata;
  }

  public Spec getSpec() {
    return spec;
  }

  public Status getStatus() {
    return status;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public void setMetadata(V1ObjectMeta metadata) {
    this.metadata = metadata;
  }

  public void setSpec(Spec spec) {
    this.spec = spec;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public class Spec {
    private List<String> dice;

    public List<String> getDice() {
      return dice;
    }

    public void setDice(List<String> dice) {
      this.dice = dice;
    }
  }

  public class Status {
    private int total;
    private List<Result> results;

    public int getTotal() {
      return total;
    }

    public List<Result> getResults() {
      return results;
    }

    public void setTotal(int total) {
      this.total = total;
    }

    public void setResults(List<Result> rolls) {
      this.results = rolls;
    }
  }

  public class Result {
    private String die;
    private int value;

    public Result(String die, int value) {
      this.die = die;
      this.value = value;
    }

    public String getDie() {
      return die;
    }

    public int getValue() {
      return value;
    }

    public void setDie(String die) {
      this.die = die;
    }

    public void setValue(int value) {
      this.value = value;
    }
  }
}
