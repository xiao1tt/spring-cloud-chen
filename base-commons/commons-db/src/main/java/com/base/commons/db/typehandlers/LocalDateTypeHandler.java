package com.base.commons.db.typehandlers;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

@MappedJdbcTypes(JdbcType.TIMESTAMP)
@MappedTypes(LocalDate.class)
public class LocalDateTypeHandler implements TypeHandler<LocalDate> {

    private static final LocalDate DB_DEFAULT_DATE = LocalDate.of(1999, 11, 11);

    @Override
    public void setParameter(PreparedStatement ps, int i, LocalDate parameter, JdbcType jdbcType)
            throws SQLException {
        if (parameter == null) {
            ps.setObject(i, DB_DEFAULT_DATE);
        } else {
            ps.setObject(i, parameter);
        }
    }

    @Override
    public LocalDate getResult(ResultSet rs, String columnName) throws SQLException {
        LocalDate result = rs.getObject(columnName, LocalDate.class);
        if (DB_DEFAULT_DATE.equals(result)) {
            return null;
        }
        return result;
    }

    @Override
    public LocalDate getResult(ResultSet rs, int columnIndex) throws SQLException {
        LocalDate result = rs.getObject(columnIndex, LocalDate.class);
        if (DB_DEFAULT_DATE.equals(result)) {
            return null;
        }
        return result;
    }

    @Override
    public LocalDate getResult(CallableStatement cs, int columnIndex) throws SQLException {
        LocalDate result = cs.getObject(columnIndex, LocalDate.class);
        if (DB_DEFAULT_DATE.equals(result)) {
            return null;
        }
        return result;
    }
}