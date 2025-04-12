package com.yarr.userservices.entity;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.time.Instant;

import lombok.Data;

@UserDefinedType("query_logs")
@Data
public class QueryLogs {
    private String from_role;
    private String from_email;
    private String to_role;
    private String to_email;
    private String query;
    private String type;
    private Instant date_time;

    public QueryLogs() {
    }

    public QueryLogs(String from_role, String from_email, String to_role, String to_email, String query, String type) {
        this.from_role = from_role;
        this.from_email = from_email;
        this.to_role = to_role;
        this.to_email = to_email;
        this.query = query;
        this.type = type;
        this.date_time = Instant.now();
    }
}
