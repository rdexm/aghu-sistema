package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class PesquisaAFComplementoFornecedorQueryBuilder extends QueryBuilder<DetachedCriteria> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1045204818116089548L;
	private Integer numeroAf; 
	private Short numComplementoAf;
	private Object numFornecedor;
	private String tipo;
//	private String razaoSocialFornecedor;
	
	private static final String FORNECEDOR = "FRN";
//	private static final String PONTO = ".";

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(ScoAutorizacaoForn.class);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		//trecho alterado pela estória 11033
		criteria.createAlias(ScoAutorizacaoForn.Fields.SCO_FORNECEDOR.toString(), FORNECEDOR, JoinType.INNER_JOIN);
		
		
		if (numeroAf != null) {
			criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), numeroAf));
		}
		
		if (numComplementoAf != null) {
			criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), numComplementoAf));
		}
		
		if (tipo.equals("AF")) {
			criteria.addOrder(Order.asc(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()));
		} else if (tipo.equals("CP")) {
			criteria.addOrder(Order.asc(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()));
		}
		
		if (numFornecedor != null) {
			String strParametro = numFornecedor.toString();
			
			criteria.createAlias(ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PROPOSTA");		
			criteria.createAlias("PROPOSTA." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FORNECEDOR");		
	
			if (CoreUtil.isNumeroInteger(strParametro)) {
				criteria.add(Restrictions.eq("FORNECEDOR." + ScoFornecedor.Fields.NUMERO.toString(), Integer.valueOf(strParametro)));
			} else if(CoreUtil.isNumeroLong(strParametro)) {
				criteria.add(Restrictions.or(Restrictions.eq(
						"FORNECEDOR." + ScoFornecedor.Fields.CGC.toString(), Long.valueOf(strParametro)),
						Restrictions.eq("FORNECEDOR." + ScoFornecedor.Fields.CPF.toString(),
								Long.valueOf(strParametro))));
			} else {
				criteria.add(Restrictions.ilike("FORNECEDOR." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString(), strParametro, MatchMode.ANYWHERE));
			}
			
			criteria.addOrder(Order.asc("FORNECEDOR." + ScoFornecedor.Fields.NUMERO.toString()));
		}
		
		criteria.add(Restrictions.in(
				ScoAutorizacaoForn.Fields.SITUACAO.toString(),
				new DominioSituacaoAutorizacaoFornecimento[] {
						DominioSituacaoAutorizacaoFornecimento.AE,
						DominioSituacaoAutorizacaoFornecimento.PA }));
		
	}
	
	
	public DetachedCriteria build(Integer numeroAf, Short numComplementoAf, Object numFornecedor, String tipo) {
		
		this.numeroAf = numeroAf;
		this.numComplementoAf = numComplementoAf;
		this.numFornecedor = numFornecedor;
		this.tipo = tipo;
		
		return super.build();
	}

}
