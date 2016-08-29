package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class BuscaParcelasNaoAssinadaNaoCanceladaQueryBuilder extends QueryBuilder<DetachedCriteria> {
	
	/**
     * 
     */
    private static final long serialVersionUID = 6937700827800037391L;
	private Integer afnNumero; 
	private Integer numeroItem;
	private Integer iafAfnNumero;
	private Integer iafNumero;
	private Boolean verificaParcelas;
	private Boolean listarParcelasAssociadas;

	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class,"PEA");
		return criteria;	
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		
		if(this.verificaParcelas){
			criteria=verificaParcelas(criteria);
		}
		
		if(listarParcelasAssociadas){
			criteria=listarParcelasAssociadas(criteria);
		}
		
		if(!listarParcelasAssociadas && !this.verificaParcelas){
			if (numeroItem != null) {
				criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), numeroItem));
			}
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), afnNumero));
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), false));
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENVIO_FORNECEDOR.toString(), false));
		}
		
		
	
	}
	
	public DetachedCriteria build(final Integer afnNumero, final Integer numeroItem){
		
		this.afnNumero = afnNumero;
		this.numeroItem = numeroItem;
		this.verificaParcelas = Boolean.FALSE;
		this.listarParcelasAssociadas = Boolean.FALSE;
		return super.build();
	}

	public DetachedCriteria buildVerificaParcelas(Integer iafAfnNumero, Integer iafNumero){
		
		this.iafAfnNumero = iafAfnNumero;
		this.iafNumero = iafNumero;
		this.verificaParcelas = Boolean.TRUE;
		this.listarParcelasAssociadas = Boolean.FALSE;
		return super.build();
	}

	public DetachedCriteria buildListarParcelasAssociadas(Integer iafAfnNumero, Integer iafNumero){
		
		this.iafAfnNumero = iafAfnNumero;
		this.iafNumero = iafNumero;
		this.listarParcelasAssociadas = Boolean.TRUE;
		this.verificaParcelas = Boolean.FALSE;
		return super.build();
	}
	
	
	public DetachedCriteria  verificaParcelas(DetachedCriteria criteria){
		criteria.add(Restrictions.eq("PEA." +  ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq("PEA." +  ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), iafNumero));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), true));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
		criteria.add(Restrictions.isNotNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString()));
		criteria.add(Restrictions.gt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString(), 0));
		criteria.add(Restrictions.ltProperty("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString(), "PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()));
		return criteria;
	}
	
	public DetachedCriteria listarParcelasAssociadas(DetachedCriteria criteria){
		criteria.add(Restrictions.eq("PEA." +  ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq("PEA." +  ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), iafNumero));
		criteria.add(Restrictions.ne("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), true));
		criteria.add(Restrictions.or(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString(),0), 
				Restrictions.isNull("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString())));
		return criteria;
	}
	
	public Integer getIafAfnNumero() {
		return iafAfnNumero;
	}

	public void setIafAfnNumero(Integer iafAfnNumero) {
		this.iafAfnNumero = iafAfnNumero;
	}

	public Integer getIafNumero() {
		return iafNumero;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	public Boolean getVerificaParcelas() {
		return verificaParcelas;
	}

	public void setVerificaParcelas(Boolean verificaParcelas) {
		this.verificaParcelas = verificaParcelas;
	}

	public Boolean getListarParcelasAssociadas() {
		return listarParcelasAssociadas;
	}

	public void setListarParcelasAssociadas(Boolean listarParcelasAssociadas) {
		this.listarParcelasAssociadas = listarParcelasAssociadas;
	}
	
}
