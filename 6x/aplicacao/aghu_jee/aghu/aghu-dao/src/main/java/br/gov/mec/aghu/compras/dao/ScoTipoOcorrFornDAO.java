package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.compras.vo.CadastroTipoOcorrenciaFornecedorVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoTipoOcorrForn;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class ScoTipoOcorrFornDAO extends BaseDao<ScoTipoOcorrForn> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2606138722938832309L;

	private DetachedCriteria obterCriteriaPesquisaPorCodigoOuDescricao(Object pesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoTipoOcorrForn.class);
		
		Short codigo = null;
		String strParametro = pesquisa.toString(); 
		
		if(CoreUtil.isNumeroShort(strParametro)){
			codigo = Short.valueOf(strParametro);
		}
		
		if (codigo != null){
			criteria.add(Restrictions.eq(ScoTipoOcorrForn.Fields.CODIGO.toString(), codigo));
		}else {
			criteria.add(Restrictions.ilike(ScoTipoOcorrForn.Fields.DESCRICAO.toString(),strParametro, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(ScoTipoOcorrForn.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return criteria;
	}
	
	public List<ScoTipoOcorrForn> pesquisarTipoOcorrenciaPorCodigoOuDescricao(Object pesquisa) {
		final DetachedCriteria criteria = obterCriteriaPesquisaPorCodigoOuDescricao(pesquisa);
		criteria.addOrder(Order.asc(ScoTipoOcorrForn.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}

	public Long pesquisarTipoOcorrenciaPorCodigoOuDescricaoCount(Object pesquisa) {
		final DetachedCriteria criteria = obterCriteriaPesquisaPorCodigoOuDescricao(pesquisa);
		
		return executeCriteriaCount(criteria);
	}

	public List<ScoTipoOcorrForn> buscarTipoOcorrenciaFornecedor(Integer firstResult, Integer maxResults, String orderProperty,	boolean asc, CadastroTipoOcorrenciaFornecedorVO filtro) {
		return executeCriteria(getTipoOcorrenciaFornecedorCriteria(filtro), firstResult, maxResults, orderProperty, asc);
	}
	
	public Long buscarTipoOcorrenciaFornecedorCount(CadastroTipoOcorrenciaFornecedorVO filtro) {
		return executeCriteriaCount(getTipoOcorrenciaFornecedorCriteria(filtro));
	}

	private DetachedCriteria getTipoOcorrenciaFornecedorCriteria(CadastroTipoOcorrenciaFornecedorVO filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoTipoOcorrForn.class);
		if(filtro.getCodigo() != null){
			criteria.add(Restrictions.eq(ScoTipoOcorrForn.Fields.CODIGO.toString(), filtro.getCodigo()));
		}
		if(filtro.getDescricao() != null && !filtro.getDescricao().isEmpty()){
			criteria.add(Restrictions.eq(ScoTipoOcorrForn.Fields.DESCRICAO.toString(), filtro.getDescricao()));
		}
		if(filtro.getSituacao() != null){
			criteria.add(Restrictions.eq(ScoTipoOcorrForn.Fields.IND_SITUACAO.toString(), filtro.getSituacao()));
		}
		return criteria;
	}

}
