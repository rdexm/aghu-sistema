package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelMatrizSituacao;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;

public class AelMatrizSituacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelMatrizSituacao> {
	
	private static final long serialVersionUID = -1494745255582972606L;

	/**
	 * Busca uma lista contendo as SituacaoPara onde pode ser transitada a Situacao atual.<br> 
	 * 
	 * @param sitCodigoOriginal
	 * @return
	 */
	public List<AelMatrizSituacao> pesquisarMatrizSituacaoPorSituacaoOrigem(AelSitItemSolicitacoes sitCodigoOriginal) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMatrizSituacao.class);
		
		if (sitCodigoOriginal != null) {
			criteria.add(Restrictions.eq(AelMatrizSituacao.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), sitCodigoOriginal));
		} else {
			criteria.add(Restrictions.isNull(AelMatrizSituacao.Fields.SITUACAO_ITEM_SOLICITACAO.toString()));			
		}
		
		return executeCriteria(criteria);
	}
	
	public List<AelMatrizSituacao> listarPorSituacaoItemSolicitacaoPara(AelSitItemSolicitacoes situacaoItemSolicitacaoPara) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMatrizSituacao.class);
	
		criteria.add(Restrictions.eq(AelMatrizSituacao.Fields.SITUACAO_ITEM_SOLICITACAO_PARA.toString(), situacaoItemSolicitacaoPara));
		
		return executeCriteria(criteria);
	}
	
	public List<AelMatrizSituacao> listarPorSituacaoItemSolicitacaoParaCodigo(String situacaoItemSolicitacaoPara) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMatrizSituacao.class);
	
		criteria.createAlias(AelMatrizSituacao.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), AelMatrizSituacao.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(AelMatrizSituacao.Fields.SITUACAO_ITEM_SOLICITACAO_PARA_CODIGO.toString(), situacaoItemSolicitacaoPara));
		
		return executeCriteria(criteria);
	}

	/*public AelMatrizSituacao obterOriginal(Short seq) {
		StringBuilder hql = new StringBuilder();
		hql.append("select o.").append(AelMatrizSituacao.Fields.EXIGE_MOTIVO_CANC.toString());
		hql.append(", o.").append(AelMatrizSituacao.Fields.SITUACAO_ITEM_SOLICITACAO.toString());
		hql.append(", o.").append(AelMatrizSituacao.Fields.SITUACAO_ITEM_SOLICITACAO_PARA.toString());
		hql.append(" from ").append(AelMatrizSituacao.class.getSimpleName()).append(" o ");
		hql.append(" left outer join o.").append(AelMatrizSituacao.Fields.SITUACAO_ITEM_SOLICITACAO.toString());
		hql.append(" left outer join o.").append(AelMatrizSituacao.Fields.SITUACAO_ITEM_SOLICITACAO_PARA.toString());
		hql.append(" where o.").append(AelMatrizSituacao.Fields.SEQ.toString()).append(" = :entityId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", seq);
		
		
		AelMatrizSituacao retorno = null;
		Object[] campos = (Object[]) query.getSingleResult();
		 
		if(campos != null) {
			retorno = new AelMatrizSituacao();

			retorno.setSeq(seq);
			retorno.setIndExigeMotivoCanc((Boolean) campos[0]);
			retorno.setSituacaoItemSolicitacao((AelSitItemSolicitacoes) campos[1]);
			retorno.setSituacaoItemSolicitacaoPara((AelSitItemSolicitacoes) campos[2]);
		}		
		
		return retorno;
	}*/

	public AelMatrizSituacao obterPorId(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMatrizSituacao.class);
		
		criteria.add(Restrictions.eq(AelMatrizSituacao.Fields.SEQ.toString(), seq));
		
		return (AelMatrizSituacao)executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Busca uma AelMatrizSituacao que represente a transicao da situacaoDe para situacaoPara.<br>
	 * Deve encontrar 0 ou 1 Matriz para a transicao informada.<br>
	 * Todos os parametros sao obrigatorios.<br>
	 * 
	 * @param situacaoDe
	 * @param situacaoPara
	 * @return <code>AelMatrizSituacao</code>
	 * 
	 * @throws IllegalArgumentException
	 */
	public AelMatrizSituacao obterPorTransicao(String situacaoDe, String situacaoPara) {
		if (StringUtils.isBlank(situacaoDe) || StringUtils.isBlank(situacaoPara)) {
			throw new IllegalArgumentException("obterPorTransicao: Parametro obrigatorio nao informado!");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMatrizSituacao.class);
		
		criteria.add(Restrictions.eq(AelMatrizSituacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), situacaoDe));
		criteria.add(Restrictions.eq(AelMatrizSituacao.Fields.SITUACAO_ITEM_SOLICITACAO_PARA_CODIGO.toString(), situacaoPara));
		
		List<AelMatrizSituacao> result = executeCriteria(criteria);
		AelMatrizSituacao matrizSituacao = null;
		
		if(result != null && !result.isEmpty()) {
			matrizSituacao = result.get(0);
		} 
		
		return matrizSituacao;
	}
	
}
