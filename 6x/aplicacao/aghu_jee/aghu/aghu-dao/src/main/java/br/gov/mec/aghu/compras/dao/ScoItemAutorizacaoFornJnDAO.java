package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.autfornecimento.vo.ScoItemAutorizacaoFornJnVO;
import br.gov.mec.aghu.dominio.DominioAprovadaAutorizacaoForn;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;

/**
 * 
 * @modulo compras
 *
 */
public class ScoItemAutorizacaoFornJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoItemAutorizacaoFornJn> {

	private static final long serialVersionUID = -1708280136518439897L;

	/**
	 * Query foi migrada para hql pois nao existe relacionamento no banco entre as tabelas sco_iaf_jn e sco_af_jn  
	 */
	public ScoItemAutorizacaoFornJn obterValorAssinadoPorItemNotaPorItemAutorizacaoForn(ScoItemAutorizacaoForn scoItensAutorizacaoForn) {
		
		final StringBuilder hql = new StringBuilder(700);

		hql.append("select iafjn ")
		.append("from ScoLicitacao lct, ScoFaseSolicitacao fsc, ScoItemAutorizacaoForn iaf, ScoItemAutorizacaoFornJn iafjn, ScoAutorizacaoFornJn afnjn ")
		.append("where afnjn.")
		.append(ScoAutorizacaoFornJn.Fields.NUMERO.toString())
		.append(" = :iafAfnNumero ")
		.append("and iafjn.")
		.append(ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString())
		.append(" = ")
		.append("afnjn.")
		.append(ScoAutorizacaoFornJn.Fields.NUMERO.toString())
		.append(" and iafjn.")
		.append(ScoItemAutorizacaoFornJn.Fields.NUMERO.toString())
		.append(" = ")
		.append(" :iafNumero")
		.append(" and iafjn.")
		.append(ScoItemAutorizacaoFornJn.Fields.IND_SITUACAO.toString())
		.append(" <> ")
		.append(" :iafSituacao")
		.append(" and iaf.")
		.append(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString())
		.append(" = ")
		.append("iafjn.")
		.append(ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString())
		.append(" and iaf.")
		.append(ScoItemAutorizacaoForn.Fields.NUMERO.toString())
		.append(" = ")
		.append("iafjn.")
		.append(ScoItemAutorizacaoFornJn.Fields.NUMERO.toString())
		.append(" and fsc.")
		.append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())
		.append(" = ")
		.append(" iafjn.")
		.append(ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString())
		.append(" and fsc.")
		.append(ScoFaseSolicitacao.Fields.IAF_NUMERO.toString())
		.append(" = ")
		.append("iafjn.")
		.append(ScoItemAutorizacaoFornJn.Fields.NUMERO.toString())
		.append(" and lct.")
		.append(ScoLicitacao.Fields.NUMERO.toString())
		.append(" = ")
		.append(" afnjn.")
		.append(ScoAutorizacaoFornJn.Fields.PFR_LCT_NUMERO.toString())
		.append(" and afnjn.")
		.append(ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString())
		.append(" in ( ")
		
		.append(" select ")
		.append(" max(")
		.append(ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString())
		.append(')')
		.append(" from ScoAutorizacaoFornJn ")
		.append(" where ")
		.append(ScoAutorizacaoFornJn.Fields.NUMERO.toString())
		.append(" = afnjn.").append(ScoAutorizacaoFornJn.Fields.NUMERO.toString())
		.append(" and ").append(ScoAutorizacaoFornJn.Fields.IND_APROVADA)
		.append(" in(")
		.append(" :situacaoAprovadaA,")
		.append(" :situacaoAprovadaB")
		.append(" )")
		.append(" and servidorAssinaCoord is not null")
		.append(" and dtAssinaturaCoord is not null) ")
		.append(" and iafjn.").append(ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString())
		.append(" in (")
		.append(" select ")
		.append(" max(")
		.append(" iafj1.").append(ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString())
		.append(')')
		.append(" from ScoItemAutorizacaoFornJn iafj1 ")
		.append(" where ")
		.append(" iafj1.").append(ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString())
		.append(" = ")
		.append(" iafjn.").append(ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString())
		.append(" and ")
		.append(" iafj1.").append(ScoItemAutorizacaoFornJn.Fields.NUMERO.toString())
		.append(" = ")
		.append(" iafjn.").append(ScoItemAutorizacaoFornJn.Fields.NUMERO.toString())
		.append(" and iafj1.").append(ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString())
		.append(" <= ")
		.append(" afnjn.").append(ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString())
		.append(" )");
	
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("iafAfnNumero", scoItensAutorizacaoForn.getId().getAfnNumero());
		query.setParameter("iafNumero", scoItensAutorizacaoForn.getId().getNumero());
		query.setParameter("iafSituacao", DominioSituacaoAutorizacaoFornecedor.EX);
		query.setParameter("situacaoAprovadaA", DominioAprovadaAutorizacaoForn.A);
		query.setParameter("situacaoAprovadaB", DominioAprovadaAutorizacaoForn.E);

		return (ScoItemAutorizacaoFornJn) query.uniqueResult();

	}

	public Integer obterMaxSequenciaAlteracaoItemAfJn(Integer numeroAf, Integer numeroItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoFornJn.class,"IAFJN");		
		criteria.add(Restrictions.eq("IAFJN."+ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString(), numeroAf));
		criteria.add(Restrictions.eq("IAFJN."+ScoItemAutorizacaoFornJn.Fields.NUMERO.toString(), numeroItem));
		criteria.setProjection(Projections.max(ScoItemAutorizacaoForn.Fields.SEQUENCIA_ALTERACAO.toString()));
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	public ScoItemAutorizacaoFornJn obterItemAfJnPorSequenciaAlteracao(Integer numeroAf, Integer numeroItem, Integer sequenciaAlteracao) {
		ScoItemAutorizacaoFornJn itemAutJn = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoFornJn.class,"IAFJN");		
		criteria.add(Restrictions.eq("IAFJN."+ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString(), numeroAf));
		criteria.add(Restrictions.eq("IAFJN."+ScoItemAutorizacaoFornJn.Fields.NUMERO.toString(), numeroItem));
		if (sequenciaAlteracao != null) {
			criteria.add(Restrictions.eq("IAFJN."+ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), sequenciaAlteracao));
		} else {
			criteria.addOrder(Order.desc(ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString()));
		}
				
		List<ScoItemAutorizacaoFornJn> listaResult = executeCriteria(criteria);
		
		if (listaResult != null && !listaResult.isEmpty()) {
			itemAutJn = (ScoItemAutorizacaoFornJn) executeCriteria(criteria).get(0);	
		}		
		return itemAutJn;
	}
	
	public List<ScoItemAutorizacaoFornJn> obterScoItemAutorizacaoFornJnPorNumPacSeq(Integer seqAF, Integer sequenciaAlteracao){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoFornJn.class, "IAJF");
		criteria.add(Restrictions.eq(ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString(), seqAF));
		criteria.add(Restrictions.le(ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), sequenciaAlteracao));
		
		DetachedCriteria maxSequenciaAlteracao = DetachedCriteria.forClass(ScoItemAutorizacaoFornJn.class, "IAJF1");
		maxSequenciaAlteracao.setProjection(Projections.max("IAJF1."+ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO));
		maxSequenciaAlteracao.add(Restrictions.eqProperty("IAJF1."+ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString(), "IAJF."+ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString()));
		maxSequenciaAlteracao.add(Restrictions.eqProperty("IAJF1."+ScoItemAutorizacaoFornJn.Fields.NUMERO.toString(), "IAJF."+ScoItemAutorizacaoFornJn.Fields.NUMERO.toString()));
		maxSequenciaAlteracao.add(Restrictions.le("IAJF1."+ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), sequenciaAlteracao));
		
		criteria.add(Subqueries.propertyIn("IAJF."+ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), maxSequenciaAlteracao));
		
		return executeCriteria(criteria);
	}
	
	public List<ScoItemAutorizacaoFornJnVO> obterScoItemAutFornJnScSsPorNumPacSeq(Integer seqAF, Integer sequenciaAlteracao){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoFornJn.class, "IAJF");
		criteria.createAlias("IAJF." + ScoItemAutorizacaoFornJn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "IPO", Criteria.LEFT_JOIN);
		criteria.createAlias("IPO." + ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "ILI", Criteria.LEFT_JOIN);
		criteria.createAlias("ILI." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FAS", Criteria.LEFT_JOIN);
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("IAJF."+ScoItemAutorizacaoFornJn.Fields.ID.toString()), ScoItemAutorizacaoFornJnVO.Fields.ITEM_SEQ.toString())
				.add(Projections.property("FAS." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString()), ScoItemAutorizacaoFornJnVO.Fields.SCO_COMPRA.toString())
				.add(Projections.property("FAS." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString()), ScoItemAutorizacaoFornJnVO.Fields.SCO_SERVICO.toString());
				criteria.setProjection(projection);
		
		criteria.add(Restrictions.eq(ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString(), seqAF));
		criteria.add(Restrictions.le(ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), sequenciaAlteracao));
		
		DetachedCriteria maxSequenciaAlteracao = DetachedCriteria.forClass(ScoItemAutorizacaoFornJn.class, "IAJF1");
		maxSequenciaAlteracao.setProjection(Projections.max("IAJF1."+ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO));
		maxSequenciaAlteracao.add(Restrictions.eqProperty("IAJF1."+ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString(), "IAJF."+ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString()));
		maxSequenciaAlteracao.add(Restrictions.eqProperty("IAJF1."+ScoItemAutorizacaoFornJn.Fields.NUMERO.toString(), "IAJF."+ScoItemAutorizacaoFornJn.Fields.NUMERO.toString()));
		maxSequenciaAlteracao.add(Restrictions.le("IAJF1."+ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), sequenciaAlteracao));
		
		criteria.add(Subqueries.propertyIn("IAJF."+ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), maxSequenciaAlteracao));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ScoItemAutorizacaoFornJnVO.class));
		return executeCriteria(criteria);
	}
	
	
	
    
	public List<ScoItemAutorizacaoFornJn> pesquisarItemAutFornJnPorNumAfSeqAlteracao(Integer numeroAF, Short sequenciaAlteracao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoFornJn.class);
		criteria.add(Restrictions.eq(ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString(), numeroAF));
		criteria.add(Restrictions.eq(ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), Integer.valueOf(sequenciaAlteracao)));				
		
		return executeCriteria(criteria);
	}
	
	public List<ScoItemAutorizacaoFornJn> pesquisarItemAutFornJnPorItem(Integer numeroAF, Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoFornJn.class);
		criteria.add(Restrictions.eq(ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString(), numeroAF));
		criteria.add(Restrictions.eq(ScoItemAutorizacaoFornJn.Fields.NUMERO.toString(), numero));				
		
		return executeCriteria(criteria);
	}
	
	public Integer obterMaxSequenciaAlteracaoItemAfJn(Integer numeroAF, Integer numeroItem, Short sequenciaAlteracao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoFornJn.class,"IAFJN");		
		criteria.add(Restrictions.eq(ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString(), numeroAF));
		criteria.add(Restrictions.eq(ScoItemAutorizacaoFornJn.Fields.NUMERO.toString(), numeroItem));
		criteria.add(Restrictions.lt(ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), Integer.valueOf(sequenciaAlteracao)));				
		
		criteria.setProjection(Projections.max(ScoItemAutorizacaoForn.Fields.SEQUENCIA_ALTERACAO.toString()));
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	public ScoItemAutorizacaoFornJn pesquisarItemAFSeqAlteracao(Integer numeroAF, Integer numeroItem, Integer sequenciaAlteracao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoFornJn.class, "IAJF");
		criteria.add(Restrictions.eq(ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString(), numeroAF));
		criteria.add(Restrictions.eq(ScoItemAutorizacaoFornJn.Fields.NUMERO.toString(), numeroItem));
		
		DetachedCriteria maxSequenciaAlteracao = DetachedCriteria.forClass(ScoItemAutorizacaoFornJn.class, "IAJF1");
		maxSequenciaAlteracao.setProjection(Projections.max("IAJF1."+ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO));
		maxSequenciaAlteracao.add(Restrictions.eqProperty("IAJF1."+ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString(), "IAJF."+ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString()));
		maxSequenciaAlteracao.add(Restrictions.eqProperty("IAJF1."+ScoItemAutorizacaoFornJn.Fields.NUMERO.toString(), "IAJF."+ScoItemAutorizacaoFornJn.Fields.NUMERO.toString()));
		maxSequenciaAlteracao.add(Restrictions.lt("IAJF1."+ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), sequenciaAlteracao));
		
		criteria.add(Subqueries.propertyIn("IAJF."+ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), maxSequenciaAlteracao));
		
		
	
		return (ScoItemAutorizacaoFornJn) executeCriteriaUniqueResult(criteria);
	}
	
    public List<ScoItemAutorizacaoFornJn> pesquisarItemAF(Integer numeroAF) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoFornJn.class, "IAJF");
		criteria.add(Restrictions.eq(ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString(), numeroAF));
		
		return executeCriteria(criteria);
	}
	
	
}