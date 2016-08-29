package br.gov.mec.aghu.core.persistence.dialect;

import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

public class PostgreSQLExtendedDialect extends PostgreSQLDialect {

	public PostgreSQLExtendedDialect() {
		registerFunction("add_days", new SQLFunctionTemplate(TimestampType.INSTANCE, "?1 + interval '?2 Days'"));
		registerFunction("add_months", new SQLFunctionTemplate(TimestampType.INSTANCE, "?1 + interval '?2 Months'"));
		registerFunction("lpad", new SQLFunctionTemplate(StringType.INSTANCE, "lpad(cast(?1 as text), ?2, ?3)"));
		registerFunction("trunc", new SQLFunctionTemplate(TimestampType.INSTANCE, "date_trunc('day', ?1)"));
	}
	
}
