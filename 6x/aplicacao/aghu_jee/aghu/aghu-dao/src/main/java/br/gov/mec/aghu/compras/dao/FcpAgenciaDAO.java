package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FcpAgenciaBanco;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class FcpAgenciaDAO extends BaseDao<FcpAgenciaBanco> {

	private static final long serialVersionUID = 3184551550098041624L;

	/**
	 * Listagem de agências por código banco.
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param fcpAgencia
	 * @return
	 */
	public List pesquisarAgenciaPorBanco(FcpAgenciaBanco fcpAgencia) {
		// Tabela
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpAgenciaBanco.class);
		// Código do banco
		Short codigoBanco = fcpAgencia.getId().getBcoCodigo();
		// Where - Código de banco
		if (codigoBanco != null) {
			criteria.add(Restrictions.eq(FcpAgenciaBanco.Fields.ID.toString() + ".bcoCodigo", codigoBanco));
		}
		return executeCriteria(criteria);
	}
	
}