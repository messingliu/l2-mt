package com.tantan.l2.relevance.feature;

import java.util.Map;
import java.util.Vector;

public class FeatureVector<T> extends Vector<T>{
  private Map<String, Integer> _indexMap = null;
  public FeatureVector(T[] values, Map<String, Integer> indexMap) {
    elementData = values;
    _indexMap = indexMap;
  }

  public Map<String, Integer> getIndexMap() {
    return _indexMap;
  }

  public Object[] getElementData() {
    return elementData;
  }
}
