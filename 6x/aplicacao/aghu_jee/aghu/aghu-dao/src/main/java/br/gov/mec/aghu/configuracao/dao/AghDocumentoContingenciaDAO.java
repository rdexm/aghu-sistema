package br.gov.mec.aghu.configuracao.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghDocumentoContingencia;
import br.gov.mec.aghu.core.dominio.DominioMimeType;

public class AghDocumentoContingenciaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghDocumentoContingencia> {

	private static final long serialVersionUID = -404348113431359175L;

	/**
	 * Método que executa delete documentos armazenados como contingência. A
	 * deleção em feita em batch (diretamente - sem carga em memória) por
	 * questão de performance.
	 * 
	 * @param data
	 */
	public void deletarDocumentosContingenciaPorData(Date data) {

		Query q = createHibernateQuery(
				"delete AghDocumentoContingencia dc where dc."
						+ AghDocumentoContingencia.Fields.DATA_CRIACAO
								.toString() + " < :dataLimite");
		q.setDate("dataLimite", data);
		q.executeUpdate();

	}

	public List<AghDocumentoContingencia> pesquisarDocumentoContingenciaPorUsuarioNomeTipo(
			String login, String nomeDocumento, DominioMimeType tipo, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		
		DetachedCriteria criteria = obterCriteriaPesquisarDocumentosPorUsuarioNomeTipo(login, nomeDocumento,tipo);

		criteria.addOrder(Order.desc(AghDocumentoContingencia.Fields.DATA_CRIACAO.toString()));
		return this.executeCriteria(criteria, firstResult, maxResult,orderProperty, asc);
	}

	private DetachedCriteria obterCriteriaPesquisarDocumentosPorUsuarioNomeTipo(
			String login, String nomeDocumento,DominioMimeType tipo) {
		if (nomeDocumento == null) {
			nomeDocumento = "";
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(AghDocumentoContingencia.class);
		criteria.add(Restrictions.eq(AghDocumentoContingencia.Fields.USUARIO.toString(), login.toUpperCase()));
		
		criteria.add(Restrictions.ilike(AghDocumentoContingencia.Fields.NOME.toString(), nomeDocumento,MatchMode.ANYWHERE));
		
		if (tipo != null) {
			criteria.add(Restrictions.eq(AghDocumentoContingencia.Fields.FORMATO.toString(), tipo));
		}
		
		return criteria;
	}

	public Long obterCountDocumentoContingenciaPorUsuarioNomeTipo(String login, String nomeDocumento,DominioMimeType tipo) {
		DetachedCriteria criteria = obterCriteriaPesquisarDocumentosPorUsuarioNomeTipo(login, nomeDocumento, tipo);
		return this.executeCriteriaCount(criteria);
	}
	
}
