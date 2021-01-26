package com.base.commons.db.typehandlers;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

@MappedJdbcTypes(JdbcType.TIMESTAMP)
@MappedTypes(LocalDateTime.class)
public class LocalDateTimeTypeHandler implements TypeHandler<LocalDateTime> {

    private static final LocalDateTime DB_DEFAULT_DATETIME = LocalDateTime.of(1999, 11, 11, 11, 11, 11);

    @Override
    public void setParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType)
            throws SQLException {
        if (parameter == null) {
            ps.setObject(i, DB_DEFAULT_DATETIME);
        } else {
            ps.setObject(i, parameter);
        }
    }

    @Override
    public LocalDateTime getResult(ResultSet rs, String columnName) throws SQLException {
        LocalDateTime result = rs.getObject(columnName, LocalDateTime.class);
        if (DB_DEFAULT_DATETIME.equals(result)) {
            return null;
        }
        return result;
    }

    @Override
    public LocalDateTime getResult(ResultSet rs, int columnIndex) throws SQLException {
        LocalDateTime result = rs.getObject(columnIndex, LocalDateTime.class);
        if (DB_DEFAULT_DATETIME.equals(result)) {
            return null;
        }
        return result;
    }

    @Override
    public LocalDateTime getResult(CallableStatement cs, int columnIndex) throws SQLException {
        LocalDateTime result = cs.getObject(columnIndex, LocalDateTime.class);
        if (DB_DEFAULT_DATETIME.equals(result)) {
            return null;
        }
        return result;
    }
}