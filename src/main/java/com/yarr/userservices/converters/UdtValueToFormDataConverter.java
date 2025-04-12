package com.yarr.userservices.converters;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.yarr.userservices.entity.FormData;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class UdtValueToFormDataConverter implements Converter<UdtValue, FormData> {

  @Override
  public FormData convert(UdtValue source) {
    FormData formData = new FormData();
    formData.setId(source.getString("id"));
    formData.setKey(source.getString("key"));
    formData.setLabel(source.getString("label"));
    formData.setType(source.getString("type"));
    formData.setSide(source.getString("side"));
    formData.setShow_label(source.getBoolean("show_label"));
    formData.setStyle(source.getString("style"));
    formData.setValue(source.getString("value"));
    formData.setValue_type(source.getString("value_type"));
    return formData;
  }
}
