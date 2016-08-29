package br.gov.mec.aghu.compras.dao;

import br.gov.mec.aghu.compras.vo.ProgramacaoParcelaItemVO;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ScoProgEntregaItemAFParcelasDAO extends BaseDao<ScoProgEntregaItemAutorizacaoFornecimento> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3862442687914139614L;

	private List<ScoAutorizacaoForn> listarAutFornecedores(Integer numeroFornecedor){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class);
		criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString(), numeroFornecedor));
		return executeCriteria(criteria);
	}
	
	// #25391 - C1
	public List<ScoProgEntregaItemAutorizacaoFornecimento> pesquisarProgEntregaItensAFParcelas(Integer iafAfnNumero, Integer iafNumero, Integer numero, Date dataInicial, Date dataFinal){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class);
		
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
		
		if(dataInicial != null || dataFinal != null){
			criteria.add(Restrictions.between(ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString().toString(), dataInicial, dataFinal));
		}
		
		if(numero != null){
			List<ScoAutorizacaoForn> listaNumeroForn = this.listarAutFornecedores(numero);
			List<Integer> numeroForn = new ArrayList<Integer>();

			for (ScoAutorizacaoForn fornecedor : listaNumeroForn) {
					 numeroForn.add(fornecedor.getNumero());
				}
			
			criteria.add(Restrictions.in(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), numeroForn));
		}
		
		if(iafAfnNumero != null){
			criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), iafAfnNumero));
		}
		
		if(iafNumero != null){
			criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), iafNumero));
		}
		
		return executeCriteria(criteria);
	}
	
	// #25391 - C2
	@SuppressWarnings("unchecked")
	public ProgramacaoParcelaItemVO pesquisarProgParcelaItem(Integer iafAfnNumero, Integer iafNumero){
	
		javax.persistence.Query query = this.queryPesquisarProgParcelaItem(iafAfnNumero, iafNumero);
		
		List<Object[]> valores = query.getResultList();
		
		ProgramacaoParcelaItemVO parcelaItem = new ProgramacaoParcelaItemVO();
		
		if(valores != null && valores.size() > 0){
			
			for (Object[] objects : valores) {
				
				parcelaItem.setNumeroAf(Integer.valueOf(objects[0].toString()));
				parcelaItem.setNroComplemento(Short.valueOf(objects[1].toString()));
				parcelaItem.setNumeroItem(Short.valueOf(objects[2].toString()));
				parcelaItem.setUnidEstoque(String.valueOf(objects[3]));
				parcelaItem.setFatorConversao(Integer.valueOf(objects[4].toString()));
				parcelaItem.setUnidFornecimento(String.valueOf(objects[5]));
				parcelaItem.setCodigoMaterial(Integer.valueOf(objects[6].toString()));
				parcelaItem.setNumeroFornecedor(Integer.valueOf(objects[7].toString()));
				
				if(objects[8] != null){
					parcelaItem.setDtVencContrato((Date) objects[8]);
				}
				
			}
		}
		
		return parcelaItem;
		
	}
	
	// #25391 - C2 Query
	private javax.persistence.Query queryPesquisarProgParcelaItem(Integer iafAfnNumero, Integer iafNumero){
		
		StringBuilder hql = new StringBuilder(1200);
		hql.append("SELECT afn.propostaFornecedor.id.lctNumero as numeroAf, "
						).append( "afn.nroComplemento as nroComplemento, "
						).append( "fsc2.itemLicitacao.id.numero as numeroItem, "
						).append( "iaf.unidadeMedida.codigo as unidFornecimento, "
						).append( "iaf.fatorConversao as fatorConversao, "
						).append( "mat.unidadeMedida.codigo as unidEstoque, "
						).append( "mat.codigo as codigoMaterial, "
						).append( "frn.numero as numeroFornecedor,"
						).append( "afn.dtVenctoContrato as dtVencContrato ");
		hql.append("FROM ScoFornecedor frn, "
						).append( "ScoMaterial mat, "
						).append( "ScoSolicitacaoDeCompra slc, "
						).append( "ScoAutorizacaoForn afn, "
						).append( "ScoItemAutorizacaoForn iaf, "
						).append( "ScoFaseSolicitacao fsc2, "
						).append( "ScoFaseSolicitacao fsc1 ");
		hql.append("WHERE fsc1.itemAutorizacaoForn.id.afnNumero = :iafAfnNumero"
				     ).append( " and fsc1.itemAutorizacaoForn.id.numero = :iafNumero"
				     ).append( " and fsc1.exclusao = 'N'"
				     ).append( " and fsc2.solicitacaoDeCompra.numero = fsc1.solicitacaoDeCompra.numero"
				     ).append( " and fsc2.itemLicitacao.id.lctNumero is not null"
				     ).append( " and fsc2.exclusao = 'N'"
				     ).append( " and iaf.autorizacoesForn.numero = fsc1.itemAutorizacaoForn.id.afnNumero"
				     ).append( " and iaf.id.numero = fsc1.itemAutorizacaoForn.id.numero"
				     ).append( " and afn.numero = iaf.id.afnNumero"
				     ).append( " and slc.numero = fsc1.solicitacaoDeCompra.numero"
				     ).append( " and mat.codigo = slc.material.codigo"  
				     ).append( " and frn.numero = afn.propostaFornecedor.id.frnNumero");
		
		javax.persistence.Query query = this.createQuery(hql.toString());
		query.setParameter("iafAfnNumero", iafAfnNumero);
		query.setParameter("iafNumero", iafNumero);
		
		return query;
		
	}
	
	//#5561 - D1
	public Integer deletarParcelasItemAF(Integer iafAfnNumero, Integer iafNumero) throws ApplicationBusinessException {
		StringBuilder ss = new StringBuilder(300);
		ss.append(" ( select max(");
		ss.append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString());
		ss.append(") from " ).append(  ScoProgEntregaItemAutorizacaoFornecimento.class.getSimpleName());
		ss.append("    where iaf_afn_numero = :iafAfnNumero ");
		ss.append("    and ind_assinatura = 'N' ");
		ss.append("    and ind_cancelada = 'N' ");
		ss.append("    and iaf_numero= :iafNumero )");
		StringBuilder sql = new StringBuilder(300);
		sql.append(" delete from ").append(ScoProgEntregaItemAutorizacaoFornecimento.class.getSimpleName());
		sql.append("  where iaf_afn_numero = :iafAfnNumero ");
		sql.append("    and iaf_numero= :iafNumero ");
		sql.append("    and parcela in " ).append( ss.toString());

		final org.hibernate.Query query = createHibernateQuery(sql.toString());

		query.setParameter("iafAfnNumero", iafAfnNumero);
		query.setParameter("iafNumero", iafNumero);
		return query.executeUpdate();
	}
	
	public List<ScoProgEntregaItemAutorizacaoFornecimento> listarProgEntregaItemAf(Integer iafAfnNumero, Integer iafNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class);
		
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), iafNumero));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString(), 0));
		
		return executeCriteria(criteria);
	}
	
}
