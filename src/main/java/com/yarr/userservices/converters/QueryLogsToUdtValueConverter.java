package com.yarr.userservices.converters;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.yarr.userservices.entity.QueryLogs;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class QueryLogsToUdtValueConverter implements Converter<QueryLogs, UdtValue> {

    private final UserDefinedType userType;

    public QueryLogsToUdtValueConverter(UserDefinedType userType) {
        this.userType = userType;
    }

    @Override
    public UdtValue convert(QueryLogs source) {
        return userType.newValue()
                .setString("from_role", source.getFrom_role())
                .setString("from_email", source.getFrom_email()) // Note: This field appears to be misnamed or mistyped
                .setString("to_role", source.getTo_role())
                .setString("to_email", source.getTo_email())
                .setString("query", source.getQuery())
                .setString("type", source.getType())
                .setInstant("date_time", source.getDate_time());
    }
}
