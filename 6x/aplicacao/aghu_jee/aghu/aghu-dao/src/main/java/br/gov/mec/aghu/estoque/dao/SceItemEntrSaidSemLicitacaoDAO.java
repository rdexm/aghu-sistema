package br.gov.mec.aghu.estoque.dao;

import br.gov.mec.aghu.estoque.vo.QuantidadesVO;
import br.gov.mec.aghu.model.SceEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceItemEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import org.hibernate.criterion.*;
import org.hibernate.transform.Transformers;

import java.util.List;

/**
 * DAO da entidade {@link SceItemEntrSaidSemLicitacao}
 * 
 * @author luismoura
 * 
 */
public class SceItemEntrSaidSemLicitacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceItemEntrSaidSemLicitacao> {
	private static final long serialVersionUID = 4024309495630015262L;

	/**
	 * Lista itens de uma ESL
	 * C19 de #29051
	 * @param seq
	 * @return
	 */
	public List<SceItemEntrSaidSemLicitacao> listaItemEntrSaidSemLicitacao(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemEntrSaidSemLicitacao.class, "IESSL");
		criteria.createAlias("IESSL."+ SceItemEntrSaidSemLicitacao.Fields.SCE_ENTR_SAID_SEM_LICITACAO.toString(), "ESL");
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, "IRP");
		subCriteria.setProjection(Projections.property("IRP." + SceItemRecebProvisorio.Fields.ITEM_ENTR_SAID_SEM_LICITACAO.toString()));
		subCriteria.add(Restrictions.eqProperty("IRP."+ SceItemRecebProvisorio.Fields.ITEM_ENTR_SAID_SEM_LICITACAO_SEQ, "IESSL." + SceItemEntrSaidSemLicitacao.Fields.SEQ ));
		subCriteria.add(Restrictions.eq("IRP."+SceItemRecebProvisorio.Fields.NRP_SEQ, seq));
		criteria.add(Subqueries.exists(subCriteria));
		criteria.addOrder(Order.desc("ESL."+ SceEntrSaidSemLicitacao.Fields.SEQ));

		return executeCriteria(criteria);
	}
	/**
	 * Busca quantidade e quantidade devolvida
	 * 
	 * C5 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param afnNumero
	 * @param numero
	 * @return
	 */
	public QuantidadesVO obterQuantidadeQuantidadeDevolvida(Integer afnNumero, Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemEntrSaidSemLicitacao.class, "ISL");
		criteria.add(Restrictions.ne("ISL." + SceItemEntrSaidSemLicitacao.Fields.IND_ESTORNO.toString(), Boolean.TRUE));

		criteria.createAlias("ISL." + SceItemEntrSaidSemLicitacao.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF");
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), numero));

		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.sum("ISL." + SceItemEntrSaidSemLicitacao.Fields.QUANTIDADE.toString()), QuantidadesVO.Fields.QUANTIDADE_1.toString());
		projections.add(Projections.sum("ISL." + SceItemEntrSaidSemLicitacao.Fields.QTDE_DEVOLVIDA.toString()), QuantidadesVO.Fields.QUANTIDADE_2.toString());
		criteria.setProjection(projections);

		criteria.setResultTransformer(Transformers.aliasToBean(QuantidadesVO.class));

		return (QuantidadesVO) super.executeCriteriaUniqueResult(criteria);
	}
}