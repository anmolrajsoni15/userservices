package com.yarr.userservices.converters;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.yarr.userservices.entity.FormData;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class FormDataToUdtValueConverter implements Converter<FormData, UdtValue> {

  private final UserDefinedType userType;

  public FormDataToUdtValueConverter(UserDefinedType userType) {
    this.userType = userType;
  }

  @Override
  public UdtValue convert(FormData source) {
    return userType.newValue()
        .setString("id", source.getId())
        .setString("key", source.getKey())
        .setString("label", source.getLabel())
        .setString("type", source.getType())
        .setString("side", source.getSide())
        .setBoolean("show_label", source.getShow_label())
        .setString("style", source.getStyle())
        .setString("value", source.getValue())
        .setString("value_type", source.getValue_type());
  }
}
