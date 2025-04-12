package com.yarr.userservices.converters;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.yarr.userservices.entity.UserFormRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class UserFormRequestToUdtValueConverter implements Converter<UserFormRequest, UdtValue> {

  private final UserDefinedType userType;

  public UserFormRequestToUdtValueConverter(UserDefinedType userType) {
    this.userType = userType;
  }

  @Override
  public UdtValue convert(UserFormRequest source) {
    return userType.newValue()
        .setString("label", source.getLabel())
        .setString("description", source.getDescription())
        .setString("type", source.getType())
        .setString("side", source.getSide())
        .setInstant("modification_date", source.getModification_date());
  }
}
