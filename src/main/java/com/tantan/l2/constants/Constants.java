package com.tantan.l2.constants;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

public class Constants {
  public static final Splitter SPLITTER_COMMA = Splitter.on(",").omitEmptyStrings().trimResults();
  public static final Joiner JOINER_COMMA = Joiner.on(",").skipNulls();
}
