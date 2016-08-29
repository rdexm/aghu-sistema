/**
 * 
 */
package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FcpTipoDocumentoPagamento;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * Classe responsável por recuperar dados da tabela da entidade
 * FcpTipoDocumentoPagamento
 * 
 * @author julianosena
 *
 */
public class FcpTipoDocumentoPagamentoDAO extends BaseDao<FcpTipoDocumentoPagamento> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5266964997238334311L;

	/**
	 * Recupera todos os tipos de documentos de pagamento
	 * por código, descrição e indSituacao
	 * 
	 * @param codigo
	 * @param descricao
	 * @param indSituacao
	 * @return
	 */
	public List<FcpTipoDocumentoPagamento> pesquisarTipoDocumentoPagamento(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) {
		
		DetachedCriteria criteria = getDetachedCriteria(fcpTipoDocumentoPagamento);
		List<FcpTipoDocumentoPagamento> results = executeCriteria(criteria, firstResult, maxResult, orderProperty,asc);
		return results;
	}

	/**
	 * Verifica se o tipo de documento pagamento existe por descrição e situacao
	 * 
	 * @param descricao
	 * @param indSituacao
	 * @return
	 */
	public FcpTipoDocumentoPagamento verificarTipoDocumentoPagamento(FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTipoDocumentoPagamento.class);
		
		criteria.add(Restrictions.eq(FcpTipoDocumentoPagamento.Fields.DESCRICAO.toString(), fcpTipoDocumentoPagamento.getDescricao()));
		criteria.add(Restrictions.eq(FcpTipoDocumentoPagamento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		List<FcpTipoDocumentoPagamento> listafcpTipoDocumentoPagamento = executeCriteria(criteria);
		
		for (FcpTipoDocumentoPagamento fcpTipoDocumentoPagamentoVerificar : listafcpTipoDocumentoPagamento) {
			
			if (fcpTipoDocumentoPagamentoVerificar != null && fcpTipoDocumentoPagamentoVerificar.getIndSituacao().isAtivo() && fcpTipoDocumentoPagamento.getIndSituacao().isAtivo()) {
				
				return fcpTipoDocumentoPagamentoVerificar;
			}
		}
		
		return null;
	}

	/**
	 * Recupera DetachedCriteria por descrição e situacao de
	 * FcpTipoDocumentoPagamento
	 * 
	 * @param descricao
	 * @param indSituacao
	 * @return DetachedCriteria
	 */
	private DetachedCriteria getDetachedCriteria(FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTipoDocumentoPagamento.class);
		
		//Se a seq (codigo) for informado
		if (fcpTipoDocumentoPagamento.getSeq() != null) {
			Criterion criterion = Restrictions.eq(FcpTipoDocumentoPagamento.Fields.SEQ.toString(), fcpTipoDocumentoPagamento.getSeq());
			criteria.add(criterion);
		}
		
		//Se a descrição for informada
		if (fcpTipoDocumentoPagamento.getDescricao() != null && !fcpTipoDocumentoPagamento.getDescricao().isEmpty()) {
			criteria.add(Restrictions.like(FcpTipoDocumentoPagamento.Fields.DESCRICAO.toString(), fcpTipoDocumentoPagamento.getDescricao(), MatchMode.ANYWHERE));
		}

		//Se a situação for informada
		if (fcpTipoDocumentoPagamento.getIndSituacao() != null) {
			Criterion criterion = Restrictions.eq(FcpTipoDocumentoPagamento.Fields.IND_SITUACAO.toString(), fcpTipoDocumentoPagamento.getIndSituacao());
			criteria.add(criterion);
		}
		return criteria;
	}

	/**
	 * Insere um registro na tabela da entidade FcpTipoDocumentoPagamento
	 * Retorna a entidade que inseriu já gerenciada pelo entity manager
	 * 
	 * @param FcpTipoDocumentoPagamento
	 * @return
	 */
	public void inserirFcpTipoDocumentoPagamento(FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento){
		
		this.persistir(fcpTipoDocumentoPagamento);
	}

	/**
	 * Realiza o update da entidade FcpTipoDocumentoPagamento
	 * 
	 * @param codigo
	 * @param descricao
	 * @param indSituacao
	 * @return
	 */
	public void atualizarFcpTipDocumentoPagamento(FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento){
		this.merge(fcpTipoDocumentoPagamento);
	}
	
	/**
	 * Metodo utilizado para realizar o count do TipoDocumentoPagamento
	 * 
	 * @param fcpTipoDocumentoPagamento
	 * @return
	 */
	public Long pesquisarCountTipoDocumentoPagamento(FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) {
		DetachedCriteria criteria = getDetachedCriteria(fcpTipoDocumentoPagamento);
		return executeCriteriaCount(criteria);
		
	}
	
	
}
