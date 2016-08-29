package br.gov.mec.aghu.exames.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

/**
 * @author capgemini - Serviço para verificar se o atendimento do Recém Nascido
 *         possui alguma solicitação de exame. Serviço #42021
 */
public class SolicitacaoExamesPorSeqAtdSituacoesQueryBuilder extends
		QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = 6985872310663085785L;
	private static final String ALIAS_SOLICITACAO_EXAMES = "SOE";
	private static final String ALIAS_ITEM_SOLICITACAO_EXAMES = "ISE";
	private static final String PONTO = ".";

	private DetachedCriteria criteria;
	private Integer atdSeq;
	private String[] situacoes;

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(AelSolicitacaoExames.class, ALIAS_SOLICITACAO_EXAMES);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setJoin();
		setFiltro();
	}

	private void setJoin() {
		criteria.createAlias(AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(), ALIAS_ITEM_SOLICITACAO_EXAMES);
	}

	private void setFiltro() {
		criteria.add(Restrictions.eq(ALIAS_SOLICITACAO_EXAMES + PONTO + AelSolicitacaoExames.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Subqueries.exists(getSubQuery()));
	}

	private DetachedCriteria getSubQuery() {
		final DetachedCriteria subQuery = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, ALIAS_ITEM_SOLICITACAO_EXAMES);

		subQuery.setProjection(Projections
				.property(ALIAS_ITEM_SOLICITACAO_EXAMES + PONTO + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		
		subQuery.add(
				Restrictions.eqProperty(
						ALIAS_ITEM_SOLICITACAO_EXAMES + PONTO + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(),
						ALIAS_SOLICITACAO_EXAMES + PONTO + AelSolicitacaoExames.Fields.SEQ.toString()));

		subQuery.add(Restrictions.in(ALIAS_ITEM_SOLICITACAO_EXAMES + PONTO
				+ AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), situacoes));

		return subQuery;
	}

	public DetachedCriteria build(Integer atdSeq, String... situacoes) {
		this.atdSeq = atdSeq;
		this.situacoes = situacoes;

		return super.build();
	}

}
