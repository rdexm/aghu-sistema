package br.gov.mec.aghu.compras.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * Segundo DAO para {@link ScoProgEntregaItemAutorizacaoFornecimento}
 * 
 * @author luismoura
 * 
 */
public class ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO extends BaseDao<ScoProgEntregaItemAutorizacaoFornecimento> {
	private static final long serialVersionUID = 5981032861780181453L;

	/**
	 * Busca o total assinado
	 * 
	 * C4 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param numero
	 * @param nroComplemento
	 * @param lctNumero
	 * @return
	 */
	public Long buscarTotalScoItemAutorizacaoFornAssinado(Integer numero, Short nroComplemento, Integer lctNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");

		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), true));

		criteria.createAlias("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF");
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), numero));

		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), nroComplemento));

		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR");
		criteria.add(Restrictions.eq("PFR." + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString(), lctNumero));

		criteria.setProjection(Projections.sum("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()));

		return (Long) super.executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Quantidade em parcelas já assinadas, com saldo a receber e data prevista para entrega menor que a data da 1º parcela
	 * 
	 * C11 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param numero
	 * @param nroComplemento
	 * @param lctNumero
	 * @return
	 */
	public Object[] buscarParcelasAssinadasSaldoReceberAntesPrimeiraParcelas(Integer iafAfnNumero, Integer iafNumero, Date dataPrevistaPrimeiraEntrega) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class);

		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), iafNumero));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.lt(ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), dataPrevistaPrimeiraEntrega));

		criteria.add(Restrictions.ltProperty(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString(),
				ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()));

		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.sum(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()));
		projections.add(Projections.sum(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()));
		criteria.setProjection(projections);

		List<Object[]> result = executeCriteria(criteria, 0, 1, null, false);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}

		return null;
	}

	/**
	 * Busca as parcelas ainda não assinadas
	 * 
	 * @param iafAfnNumero
	 * @param iafNumero
	 * @return
	 */
	public List<ScoProgEntregaItemAutorizacaoFornecimento> pesquisarParcelasNaoAssinadas(Integer iafAfnNumero, Integer iafNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class);
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), iafNumero));
		criteria.add(Restrictions.ne(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString(), Integer.valueOf(0)));
		return super.executeCriteria(criteria);
	}

	/**
	 * Busca a próxima parcela
	 * 
	 * @param iafAfnNumero
	 * @param iafNumero
	 * @param seq
	 * @return
	 */
	public Integer obterProximaParcela(Integer iafAfnNumero, Integer iafNumero, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class);
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), iafNumero));
		//criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString(), seq));

		criteria.setProjection(Projections.max(ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()));

		Integer result = (Integer) super.executeCriteriaUniqueResult(criteria);

		if (result == null) {
			result = 0;
		}

		return ++result;
	}

	@Override
	protected void obterValorSequencialId(ScoProgEntregaItemAutorizacaoFornecimento elemento) {
		if (elemento == null || elemento.getId() == null || elemento.getId().getIafAfnNumero() == null || elemento.getId().getIafNumero() == null
				|| elemento.getId().getSeq() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		if (elemento.getId().getParcela() == null) {
			Integer parcela = this.obterProximaParcela(elemento.getId().getIafAfnNumero(), elemento.getId().getIafNumero(), elemento.getId().getSeq());
			elemento.getId().setParcela(parcela);
		}

	}
}