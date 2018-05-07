package com.tantan.l2.services.abtest;

import com.tantan.l2.enums.ModelTarget;
import org.springframework.stereotype.Service;

/**
 * Model Target: the top layer classification of models
 */
@Service
public class ModelTargetService {

  public ModelTarget getModelTarget(boolean female, int mlc) {
    if (female) {
      switch (mlc) {
        case 0: return ModelTarget.FEMALE_MLC0;
        case 1: return ModelTarget.FEMALE_MLC1;
        case 2: case 3: return ModelTarget.FEMALE_MLC2;
        case 4: return ModelTarget.FEMALE_MLC4;
        default: return ModelTarget.FEMALE_MLC0; // ? TODO
      }
    } else {
      switch (mlc) {
        case 0: return ModelTarget.MALE_MLC0;
        case 1: return ModelTarget.MALE_MLC1;
        case 2: case 3: return ModelTarget.MALE_MLC2;
        case 4: return ModelTarget.MALE_MLC4;
        default: return ModelTarget.MALE_MLC0; // ? TODO
      }
    }
  }

}
