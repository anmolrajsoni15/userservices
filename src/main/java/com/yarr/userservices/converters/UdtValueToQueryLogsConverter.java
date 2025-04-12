package com.yarr.userservices.converters;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.yarr.userservices.entity.QueryLogs;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class UdtValueToQueryLogsConverter implements Converter<UdtValue, QueryLogs> {

    @Override
    public QueryLogs convert(UdtValue source) {
        QueryLogs queryLogs = new QueryLogs();
        queryLogs.setFrom_role(source.getString("from_role"));
        queryLogs.setFrom_email(source.getString("from_email"));
        queryLogs.setTo_role(source.getString("to_role"));
        queryLogs.setTo_email(source.getString("to_email"));
        queryLogs.setQuery(source.getString("query"));
        queryLogs.setType(source.getString("type"));
        queryLogs.setDate_time(source.getInstant("date_time"));
        return queryLogs;
    }
}
