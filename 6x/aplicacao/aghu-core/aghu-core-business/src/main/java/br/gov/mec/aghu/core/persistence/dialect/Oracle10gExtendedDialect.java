package br.gov.mec.aghu.core.persistence.dialect;

import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.DateType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

public class Oracle10gExtendedDialect extends Oracle10gDialect {

	public Oracle10gExtendedDialect() {
		registerFunction("add_days", new SQLFunctionTemplate(DateType.INSTANCE, "?1 + ?2"));
		registerFunction("lpad", new SQLFunctionTemplate(StringType.INSTANCE, "lpad(?1, ?2, ?3)"));
		registerFunction("trunc", new SQLFunctionTemplate(TimestampType.INSTANCE, "trunc(?1)"));
	}
	
}
