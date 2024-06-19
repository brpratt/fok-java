package com.brpratt.diceroll;

import io.kubernetes.client.common.KubernetesListObject;
import io.kubernetes.client.openapi.models.V1ListMeta;
import java.util.List;

public class DiceRollList implements KubernetesListObject {
  private String apiVersion;
  private String kind;
  private V1ListMeta metadata;
  private List<DiceRoll> items;

  public String getApiVersion() {
    return apiVersion;
  }

  public String getKind() {
    return kind;
  }

  public V1ListMeta getMetadata() {
    return metadata;
  }

  public List<DiceRoll> getItems() {
    return items;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public void setMetadata(V1ListMeta metadata) {
    this.metadata = metadata;
  }

  public void setItems(List<DiceRoll> items) {
    this.items = items;
  }
}
