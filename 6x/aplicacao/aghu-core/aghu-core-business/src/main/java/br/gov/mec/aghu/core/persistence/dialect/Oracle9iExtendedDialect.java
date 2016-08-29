package br.gov.mec.aghu.core.persistence.dialect;

import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.DateType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

public class Oracle9iExtendedDialect extends Oracle9iDialect {
	
	public Oracle9iExtendedDialect() {
		registerFunction("add_days", new SQLFunctionTemplate(DateType.INSTANCE, "?1 + ?2"));
		registerFunction("lpad", new SQLFunctionTemplate(StringType.INSTANCE, "lpad(?1, ?2, ?3)"));
		registerFunction("trunc", new SQLFunctionTemplate(TimestampType.INSTANCE, "trunc(?1)"));
	}

}
