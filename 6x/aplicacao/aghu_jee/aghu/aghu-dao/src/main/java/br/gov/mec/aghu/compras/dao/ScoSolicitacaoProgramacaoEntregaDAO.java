package br.gov.mec.aghu.compras.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;

public class ScoSolicitacaoProgramacaoEntregaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoSolicitacaoProgramacaoEntrega> {

	private static final long serialVersionUID = -7041050946417696547L;
	
	/**
	 * Retorna uma lista de solicitações associadas às programações de entrega do item da AF
	 * @param afnNumero
	 * @param numero
	 * @return List
	 */
	public List<ScoSolicitacaoProgramacaoEntrega> pesquisarSolicitacaoProgEntregaPorItemAf(Integer afnNumero, Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoProgramacaoEntrega.class,"PEA");
		criteria.createAlias(ScoSolicitacaoProgramacaoEntrega.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF");
		
		criteria.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.NUMERO.toString(), numero));
				
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna uma lista de solicitações associadas à programação de entrega do item da AF ordenado por prioridade
	 * @param iafAfnNumero
	 * @param iafNumero
	 * @param seq
	 * @param parcela
	 * @return List
	 */
	public List<ScoSolicitacaoProgramacaoEntrega> pesquisarSolicitacaoProgEntregaPorItemProgEntrega(Integer iafAfnNumero, Integer iafNumero, Integer seq, Integer parcela) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoProgramacaoEntrega.class,"SPE");
		criteria.createAlias("SPE."+ScoSolicitacaoProgramacaoEntrega.Fields.PROG_ENTREGA_ITEM_AF.toString(), "PEA");
		
		criteria.add(Restrictions.eq("SPE."+ScoSolicitacaoProgramacaoEntrega.Fields.PEA_IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq("SPE."+ScoSolicitacaoProgramacaoEntrega.Fields.PEA_IAF_NUMERO.toString(), iafNumero));
		criteria.add(Restrictions.eq("SPE."+ScoSolicitacaoProgramacaoEntrega.Fields.PEA_SEQ.toString(), seq));
		criteria.add(Restrictions.eq("SPE."+ScoSolicitacaoProgramacaoEntrega.Fields.PEA_PARCELA.toString(), parcela));
		
		criteria.addOrder(Order.asc("SPE."+ScoSolicitacaoProgramacaoEntrega.Fields.IND_PRIORIDADE.toString()));
		
		return executeCriteria(criteria);
	}

	/**
	 * Obtem a proxima solicitação de programação de entrega por item de AF com saldo
	 * para ser analisada pela regra de priorização/rateio. A ordem das parcelas para rateio é
	 * 2 - IND_ENTREGA_IMEDIATA = ‘S’,  3 - IND_TRAMITE_INTERNO = ‘S’ e 4 - menor DT_PREV_ENTREGA
	 * @param iafAfnNumero
	 * @param iafNumero
	 * @param seq
	 * @param parcelaAtual
	 * @return ScoSolicitacaoProgramacaoEntrega
	 */
	public ScoSolicitacaoProgramacaoEntrega obterProximaSolicitacaoProgEntregaPorItemProgEntrega(Integer iafAfnNumero, Integer iafNumero, Integer seq, List<Integer> parcelaProcessada) {
		ScoSolicitacaoProgramacaoEntrega solicitacaoProgramacaoEntrega;

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoProgramacaoEntrega.class,"SPE");
		criteria.createAlias("SPE."+ScoSolicitacaoProgramacaoEntrega.Fields.PROG_ENTREGA_ITEM_AF.toString(), "PEA");
		//criteria.getExecutableCriteria(getSession()).setMaxResults(1);

		criteria.add(Restrictions.eq("SPE."+ScoSolicitacaoProgramacaoEntrega.Fields.PEA_IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq("SPE."+ScoSolicitacaoProgramacaoEntrega.Fields.PEA_IAF_NUMERO.toString(), iafNumero));
		criteria.add(Restrictions.eq("SPE."+ScoSolicitacaoProgramacaoEntrega.Fields.PEA_SEQ.toString(), seq));
		criteria.add(Restrictions.not(Restrictions.in("SPE."+ScoSolicitacaoProgramacaoEntrega.Fields.PEA_PARCELA.toString(), parcelaProcessada)));

		criteria.add(Restrictions.or(
				Restrictions.and(
						Restrictions.isNotNull(ScoSolicitacaoProgramacaoEntrega.Fields.SOLICITACAO_COMPRA.toString()),
						Restrictions.sqlRestriction("(COALESCE({alias}."+ScoSolicitacaoProgramacaoEntrega.Fields.QTDE.name()+",0) - COALESCE({alias}."+ScoSolicitacaoProgramacaoEntrega.Fields.QTDE_ENTREGUE.name() + ",0)) > 0")),
				Restrictions.and(
						Restrictions.isNotNull(ScoSolicitacaoProgramacaoEntrega.Fields.SOLICITACAO_SERVICO.toString()),
						Restrictions.sqlRestriction("(COALESCE({alias}."+ScoSolicitacaoProgramacaoEntrega.Fields.VALOR.name()+",0) - COALESCE({alias}."+ScoSolicitacaoProgramacaoEntrega.Fields.VALOR_EFETIVADO.name() + ",0)) > 0"))));

		criteria.addOrder(Order.desc("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENTREGA_IMEDIATA.toString()));
		criteria.addOrder(Order.desc("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_TRAMITE_INTERNO.toString()));
		criteria.addOrder(Order.asc("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString()));
		criteria.addOrder(Order.asc("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()));

		List<ScoSolicitacaoProgramacaoEntrega> listaResultado = executeCriteria(criteria, 0, 1 , null);
		if (listaResultado != null && !listaResultado.isEmpty()) {
			solicitacaoProgramacaoEntrega = listaResultado.get(0);
		} else {
			solicitacaoProgramacaoEntrega = null;
		}

		return solicitacaoProgramacaoEntrega;
	}

	/**
	 * #25484 C3 Consultar quantidade das parcelas associadas a uma solicitação de compra
	 * @param numeroSlc
	 * @return
	 */
	public Integer obterQtdeSolicPorNumeroSolicitacao(Integer numeroSlc){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoProgramacaoEntrega.class, "SPE");
		criteria.add(Restrictions.eq("SPE." + ScoSolicitacaoProgramacaoEntrega.Fields.NUMERO_SOLICITACAO_COMPRA.toString(), numeroSlc));
		criteria.setProjection(Projections.sum(ScoSolicitacaoProgramacaoEntrega.Fields.QTDE.toString()));
		return (Integer)this.executeCriteriaUniqueResult(criteria);
	}

	public List<ScoSolicitacaoProgramacaoEntrega> listarSolicitacaoByItemAFId(Integer iafAfnNumero, Integer iafNumero, Integer seq, Integer parcela){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoProgramacaoEntrega.class, "SPE");

		criteria.add(Restrictions.eq("SPE." + ScoSolicitacaoProgramacaoEntrega.Fields.PEA_IAF_AFN_NUMERO.toString(), iafAfnNumero));

		if (iafNumero != null) {
			criteria.add(Restrictions.eq("SPE." + ScoSolicitacaoProgramacaoEntrega.Fields.PEA_IAF_NUMERO.toString(), iafNumero));
		}

		if (seq != null) {
			criteria.add(Restrictions.eq("SPE." + ScoSolicitacaoProgramacaoEntrega.Fields.PEA_SEQ.toString(), seq));
		}

		if (parcela != null) {
			criteria.add(Restrictions.eq("SPE." + ScoSolicitacaoProgramacaoEntrega.Fields.PEA_PARCELA.toString(), parcela));
		}

		return executeCriteria(criteria);
	}

	public List<ScoSolicitacaoProgramacaoEntrega> listarSolicitacaoByItemAFId(ScoProgEntregaItemAutorizacaoFornecimentoId item){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoProgramacaoEntrega.class, "SPE");

		criteria.add(Restrictions.eq("SPE." + ScoSolicitacaoProgramacaoEntrega.Fields.PEA_IAF_AFN_NUMERO.toString(), item.getIafAfnNumero()));
		criteria.add(Restrictions.eq("SPE." + ScoSolicitacaoProgramacaoEntrega.Fields.PEA_IAF_NUMERO.toString(), item.getIafNumero()));
		criteria.add(Restrictions.eq("SPE." + ScoSolicitacaoProgramacaoEntrega.Fields.PEA_SEQ.toString(), item.getSeq()));
		criteria.add(Restrictions.eq("SPE." + ScoSolicitacaoProgramacaoEntrega.Fields.PEA_PARCELA.toString(), item.getParcela()));

		return executeCriteria(criteria);
	}

	public Integer deletaRelacaoParcelaSolicProgramacaoEntrega(
			Integer iafAfnNumero, Integer iafNumero, Integer numeroParcela, Integer seq) {
		String sql = " delete from " + ScoSolicitacaoProgramacaoEntrega.class.getSimpleName() + " where pea_iaf_afn_numero= :iafAfnNumero and pea_iaf_numero= :iafNumero and pea_parcela= :numeroParcela and pea_seq= :seq";
		Query query = createQuery(sql);
		query.setParameter("iafAfnNumero", iafAfnNumero);
		query.setParameter("iafNumero", iafNumero);
		query.setParameter("numeroParcela", numeroParcela);
		query.setParameter("seq", seq);
		return query.executeUpdate();
	}
}

