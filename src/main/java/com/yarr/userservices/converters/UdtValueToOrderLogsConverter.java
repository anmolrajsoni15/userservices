package com.yarr.userservices.converters;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.yarr.userservices.entity.OrderLogs;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class UdtValueToOrderLogsConverter implements Converter<UdtValue, OrderLogs> {

    @Override
    public OrderLogs convert(UdtValue source) {
        OrderLogs orderLogs = new OrderLogs();
        orderLogs.setStatus(source.getString("status"));
        orderLogs.setDate_time(source.getInstant("date_time"));
        orderLogs.setUsermail(source.getString("usermail"));
        orderLogs.setUserrole(source.getList("userrole", String.class));
        return orderLogs;
    }
}
