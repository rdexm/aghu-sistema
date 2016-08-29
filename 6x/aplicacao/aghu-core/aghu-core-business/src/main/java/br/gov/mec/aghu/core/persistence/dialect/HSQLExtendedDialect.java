package br.gov.mec.aghu.core.persistence.dialect;

import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

public class HSQLExtendedDialect extends HSQLDialect {
	
	public HSQLExtendedDialect(){
		registerFunction("add_days", new SQLFunctionTemplate(TimestampType.INSTANCE, "TIMESTAMPADD('SQL_TSI_DAY', ?2, ?1)"));
		registerFunction("add_months", new SQLFunctionTemplate(TimestampType.INSTANCE, "TIMESTAMPADD('SQL_TSI_MONTH', ?2, ?1)"));
		registerFunction("lpad", new SQLFunctionTemplate(StringType.INSTANCE, "lpad(?1, ?2, ?3)"));
		registerFunction("trunc", new SQLFunctionTemplate(TimestampType.INSTANCE, "trunc(?1)"));		
	}

}
