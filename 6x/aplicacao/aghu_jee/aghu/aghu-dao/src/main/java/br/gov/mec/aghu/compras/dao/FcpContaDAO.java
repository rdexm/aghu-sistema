package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FcpAgenciaBanco;
import br.gov.mec.aghu.model.FcpContaCorrenteEncargo;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class FcpContaDAO extends BaseDao<FcpContaCorrenteEncargo> {

	private static final long serialVersionUID = 5386043856979758403L;

	/**
	 * Restrições da listagem de contas por agência.
	 * @param fcpAgencia
	 * @return
	 */
	private DetachedCriteria createPesquisarCriteriaAgencia(FcpAgenciaBanco fcpAgencia) {
		// Tabela
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpContaCorrenteEncargo.class);
		// Código do banco
		Short codigoBanco = fcpAgencia.getId().getBcoCodigo();
		// Código da agência
		Integer codigoAgencia = fcpAgencia.getId().getCodigo();
		// Where - Código de banco
		if(codigoBanco != null) {
			criteria.add(Restrictions.eq(FcpContaCorrenteEncargo.Fields.ID.toString() + ".agbBcoCodigo", codigoBanco));
		}
		// Where - Código da agência
		if(codigoAgencia != null) {
			criteria.add(Restrictions.eq(FcpContaCorrenteEncargo.Fields.ID.toString() + ".agbCodigo", codigoAgencia));
		}
		return criteria;
	}
	
	/**
	 * Listagem de contas por agência.
	 * @param fcpBanco
	 * @return
	 */
	public List pesquisarPorAgencia(FcpAgenciaBanco fcpAgencia) {
		return executeCriteria(createPesquisarCriteriaAgencia(fcpAgencia));
	}

}