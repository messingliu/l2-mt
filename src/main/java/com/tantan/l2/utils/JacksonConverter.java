package com.tantan.l2.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;


public class JacksonConverter extends MappingJackson2HttpMessageConverter {
  public JacksonConverter() {
    List<MediaType> mediaTypes = new ArrayList<>();
    mediaTypes.add(MediaType.TEXT_PLAIN);
    setSupportedMediaTypes(mediaTypes);
  }
}