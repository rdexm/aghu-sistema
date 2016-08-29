package br.gov.mec.aghu.core.persistence.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

public class BooleanUserType implements UserType, Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -822281895299394125L;

	@Override
	public Object assemble(Serializable arg0, Object arg1)
			throws HibernateException {
		return arg0;
	}

	@Override
	public Object deepCopy(Object arg0) throws HibernateException {
		return arg0;
	}

	@Override
	public Serializable disassemble(Object arg0) throws HibernateException {
		{
			return (Serializable) arg0;
		}
	}
	
	@SuppressWarnings("PMD.SuspiciousEqualsMethodName")
	@Override
	public boolean equals(Object arg0, Object arg1) throws HibernateException {
		if (arg0 == arg1) {
			return true;
		}
		if (arg0 == null || arg1 == null) {
			return false;
		}
		return arg0.equals(arg1);
	}

	@Override
	public int hashCode(Object arg0) throws HibernateException {
		return arg0.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public Object replace(Object arg0, Object arg1, Object arg2)
			throws HibernateException {
		return arg0;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class returnedClass() {
		return Boolean.class;
	}

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.VARCHAR };
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
		String value = rs.getString(names[0]);

		if (value == null) {
			return null;
		} else if ("S".equals(value)) {
			//por hora, fixo em S - true e N - false
			return true;
		} else if ("N".equals(value)) {
			return false;
		} else {
			throw new HibernateException("Valor " + value
					+ "Não pode ser convertido para Boolean");
		}
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, Types.VARCHAR);
		} else {
			Boolean valorBoolean = (Boolean) value;
			
			//por hora, fixo em S - true e N - false
			if (valorBoolean){
				st.setString(index, "S");
			}else{
				st.setString(index, "N");
			}		
		}		
	}



}
