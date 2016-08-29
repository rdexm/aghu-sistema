package br.gov.mec.aghu.core.persistence.dialect;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;

public class OrderBySql extends Order {

	/**
	 * 
	 */
	private static final long serialVersionUID = -77959223815739091L;
	
	
	private String sql;

	protected OrderBySql(String sql) {
		super(sql, true);
		this.sql = sql;
	}

	public String toString() {
		return sql;
	}

	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
		return sql;
	}

	public static Order sql(String sql) {
		return new OrderBySql(sql);
	}

}
