package com.yarr.userservices.converters;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.yarr.userservices.entity.UserFormRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class UdtValueToUserFormRequestConverter implements Converter<UdtValue, UserFormRequest> {

  @Override
  public UserFormRequest convert(UdtValue source) {
    UserFormRequest userFormRequest = new UserFormRequest();
    userFormRequest.setLabel(source.getString("label"));
    userFormRequest.setDescription(source.getString("description"));
    userFormRequest.setType(source.getString("type"));
    userFormRequest.setSide(source.getString("side"));
    userFormRequest.setModification_date(source.getInstant("modification_date"));
    return userFormRequest;
  }
}
