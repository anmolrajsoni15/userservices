package com.yarr.userservices.converters;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.yarr.userservices.entity.OrderLogs;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class OrderLogsToUdtValueConverter implements Converter<OrderLogs, UdtValue> {

    private final UserDefinedType userType;

    public OrderLogsToUdtValueConverter(UserDefinedType userType) {
        this.userType = userType;
    }

    @Override
    public UdtValue convert(OrderLogs source) {
        UdtValue udtValue = userType.newValue()
                .setString("status", source.getStatus())
                .setInstant("date_time", source.getDate_time())
                .setString("usermail", source.getUsermail());
                
        // Handle the list separately
        if (source.getUserrole() != null) {
            udtValue.setList("userrole", source.getUserrole(), String.class);
        }
        
        return udtValue;
    }
}
