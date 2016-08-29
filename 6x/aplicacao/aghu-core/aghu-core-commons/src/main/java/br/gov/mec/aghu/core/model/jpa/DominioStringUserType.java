package br.gov.mec.aghu.core.model.jpa;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;

import br.gov.mec.aghu.core.dominio.DominioString;



/**
 * Tipo de dado customizado para persistencia com Hibernate para Dominios. Todo
 * dominio deve ser uma classe enum que implementa a interface
 * <code>br.ufrgs.hcpa.dominio.Dominio</code>
 * 
 * Example Hibernate CaveatEmptor http://caveatemptor.hibernate.org/
 * 
 * @author Gavin King
 */
@SuppressWarnings({"rawtypes", "PMD.SuspiciousEqualsMethodName" })
public class DominioStringUserType implements EnhancedUserType, ParameterizedType {
	private static final Log LOG = LogFactory.getLog(DominioStringUserType.class);

	private Class<Enum> enumClass;

	@SuppressWarnings("unchecked")
	public void setParameterValues(Properties parameters) {
		String enumClassName = parameters.getProperty("enumClassName");
		try {
			enumClass = (Class<Enum>) Class.forName(enumClassName);
		} catch (ClassNotFoundException cnfe) {
			LOG.error("Não encontrou a classe de dominio", cnfe);
			throw new HibernateException("Enum class not found", cnfe);
		}
	}

	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (Enum) value;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		return x == y;
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public boolean isMutable() {
		return false;
	}

	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		
		String value = rs.getString(names[0]);

		for (Enum e : enumClass.getEnumConstants()) {
			if (((DominioString) e).getCodigo().equals(value)) {
				return e;
			}
		}

		return null;
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
			throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, Types.NUMERIC);
		} else {
			st.setString(index, ((DominioString) value).getCodigo().toString());	
			
		}
	}

	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	public Class returnedClass() {
		return enumClass;
	}

	public int[] sqlTypes() {
		return new int[] { Types.VARCHAR };
	}

	@SuppressWarnings("unchecked")
	public Object fromXMLString(String xmlValue) {
		return Enum.valueOf(enumClass, xmlValue);
	}

	public String objectToSQLString(Object value) {
		return '\'' + ((Enum) value).name() + '\'';
	}

	public String toXMLString(Object value) {
		return ((Enum) value).name();
	}

}
