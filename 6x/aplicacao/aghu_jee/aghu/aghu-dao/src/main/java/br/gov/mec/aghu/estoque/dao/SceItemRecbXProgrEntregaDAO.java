package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceItemRecbXProgrEntrega;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.SceItemRecebProvisorioId;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;

public class SceItemRecbXProgrEntregaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceItemRecbXProgrEntrega> {

	private static final long serialVersionUID = -1471240016189461860L;
	
	/**
	 * Obtem a maior SEQ da pk da tabela SceItemRecbXProgrEntrega
	 * @return Long
	 */
	public Long obterMaxItemRecbXProgrEntrega() {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecbXProgrEntrega.class,"IPR");
		criteria.setProjection(Projections.max(SceItemRecbXProgrEntrega.Fields.SEQ.toString()));
		
		Long maxSeq = (Long) this.executeCriteriaUniqueResult(criteria);
		
		if (maxSeq == null){
			maxSeq = Long.valueOf(0);
		}
		
		return maxSeq;		
	}
	
	
	public List<SceItemRecbXProgrEntrega> pesquisarItemRecbXProgrEntregaPorItemRecebimentoProvisorio(SceItemRecebProvisorioId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecbXProgrEntrega.class,"IPP");
		criteria.createAlias("IPP."+SceItemRecbXProgrEntrega.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), "IRP");
		criteria.add(Restrictions.eq("IRP."+SceItemRecebProvisorio.Fields.NRP_SEQ.toString(),id.getNrpSeq()));
		criteria.add(Restrictions.eq("IRP."+SceItemRecebProvisorio.Fields.NRO_ITEM.toString(),id.getNroItem()));
		return executeCriteria(criteria);
	}
	
	public List<SceItemRecbXProgrEntrega> pesquisarItemRecbXProgrEntregaPorSeqNrpEItemAf(Integer seqNrp, Short itemNrp, Integer iafAfnNumero, Short iafNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecbXProgrEntrega.class,"IPP");
		
		criteria.add(Restrictions.eq("IPP."+SceItemRecbXProgrEntrega.Fields.IRP_NRP_SEQ.toString(), seqNrp));
		criteria.add(Restrictions.eq("IPP."+SceItemRecbXProgrEntrega.Fields.IRP_NRO_ITEM.toString(), Integer.valueOf(itemNrp)));
		criteria.add(Restrictions.eq("IPP."+SceItemRecbXProgrEntrega.Fields.PEA_IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq("IPP."+SceItemRecbXProgrEntrega.Fields.PEA_IAF_NUMERO.toString(), Integer.valueOf(iafNumero)));

		criteria.addOrder(Order.desc("IPP."+SceItemRecbXProgrEntrega.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Verifica se existe ligacao entre o item do recebimento provisorio e as 
	 * solicitacoes de compras/servico por parcela de entrega do item da af
	 * @param nrpSeq
	 * @param nroItem
	 * @return Boolean
	 */
	public Boolean verificarLigacaoSolicitacaoParcelaEntrega(Integer nrpSeq, Integer nroItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecbXProgrEntrega.class,"IPP");
		criteria.createAlias("IPP."+SceItemRecbXProgrEntrega.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), "IRP");
		
		criteria.add(Restrictions.eq("IRP."+SceItemRecebProvisorio.Fields.NRP_SEQ.toString(), nrpSeq));
		criteria.add(Restrictions.eq("IRP."+SceItemRecebProvisorio.Fields.NRO_ITEM.toString(), nroItem));
		criteria.add(Restrictions.isNotNull("IPP."+SceItemRecbXProgrEntrega.Fields.SPE_SEQ.toString()));
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	public Long obterSomaItemRecbXProgrEntregaPorItemAF(ScoProgEntregaItemAutorizacaoFornecimento scoProgEntregaItemAutorizacaoFornecimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecbXProgrEntrega.class,"IPP");
		
		criteria.createAlias("IPP." + SceItemRecbXProgrEntrega.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(),"IRP");
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), "NRP");		
		
		criteria.setProjection(Projections.sqlProjection("sum(coalesce({alias}.QTDE_ENTREGUE,0)) soma", 
				new String[]{"soma"}, new Type[] {LongType.INSTANCE}));
		
		criteria.add(Restrictions.eq("IPP."+SceItemRecbXProgrEntrega.Fields.SCO_PROG_ENTREGA_ITEM_AUTORIZACAO_FORNECIMENTO.toString(),scoProgEntregaItemAutorizacaoFornecimento));
		criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.IND_ESTORNO.toString(), false));
		criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.IND_CONFIRMADO.toString(), false));
		
		return (Long) executeCriteriaUniqueResult(criteria);
	}	
	
	public List<SceItemRecbXProgrEntrega> pesquisarItemRecbXProgrEntregaPorItemAutorizacaoFornecimento(ScoProgEntregaItemAutorizacaoFornecimento scoProgEntregaItemAutorizacaoFornecimento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecbXProgrEntrega.class,"IRPEA");
		//criteria.createAlias("IRPEA."+SceItemRecbXProgrEntrega.Fields.SCO_PROG_ENTREGA_ITEM_AUTORIZACAO_FORNECIMENTO.toString(), "IAF");
		criteria.createCriteria("IRPEA." + SceItemRecbXProgrEntrega.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), "IRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("IRP." + SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), "NRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("NRP." + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("DFE." + SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("IRPEA."+SceItemRecbXProgrEntrega.Fields.SCO_PROG_ENTREGA_ITEM_AUTORIZACAO_FORNECIMENTO.toString(),scoProgEntregaItemAutorizacaoFornecimento));
		
		return executeCriteria(criteria);
	}


	public Integer deletaRelacaoParcelaItemRecebXProgEntrega(Integer iafAfnNumero, Integer iafNumero, Integer numeroParcela, Integer seq) {
		String sql = " delete from " + SceItemRecbXProgrEntrega.class.getSimpleName() + " where pea_iaf_afn_numero= :iafAfnNumero and pea_iaf_numero= :iafNumero and pea_parcela= :numeroParcela and pea_seq= :seq";
		Query query = createQuery(sql);
		query.setParameter("iafAfnNumero", iafAfnNumero);
		query.setParameter("iafNumero", iafNumero);
		query.setParameter("numeroParcela", numeroParcela);
		query.setParameter("seq", seq);
		return query.executeUpdate();
	}
}