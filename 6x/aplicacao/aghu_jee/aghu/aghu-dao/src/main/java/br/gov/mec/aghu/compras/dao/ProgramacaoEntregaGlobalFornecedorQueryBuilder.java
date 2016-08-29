package br.gov.mec.aghu.compras.dao;


import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.constantes.EntregasGlobaisAcesso;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaGlobalFornecedoresVO;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class ProgramacaoEntregaGlobalFornecedorQueryBuilder extends QueryBuilder<DetachedCriteria> {

	//TODO: talvez substituir por um VO
	private Integer codigoGrupoMaterial;
	private Date dataInicial;
	private Date dataFinal;
	private EntregasGlobaisAcesso tipoValorEnum;
	private Integer nrFornecedor;
	
	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		return criteria;
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.PROG_ENTREGAS.toString(), "PEA");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR");
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");		
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
		
		//TODO: definicao das datas ficara no ON
		if(dataInicial != null && dataFinal != null) {
			criteria.add(Restrictions.between("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), 
					dataInicial, dataFinal));
		} 	
		
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), 
				Boolean.FALSE));
		criteria.add(Restrictions.sqlRestriction(" pea1_.valor_total - coalesce(pea1_.valor_efetivado,0) > 0 " ));
		
//		criteria.add(Restrictions.eqProperty("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), 
//				"PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString()));
//		criteria.add(Restrictions.eqProperty("FSC." + ScoFaseSolicitacao.Fields.IAF_NUMERO.toString(), 
//				"PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString()));
//		criteria.add(Restrictions.eqProperty("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), 
//				"FSC." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
//		criteria.add(Restrictions.eqProperty("MAT." + ScoMaterial.Fields.CODIGO.toString(), 
//				"SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL_CODIGO.toString()));
		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), codigoGrupoMaterial));
//		criteria.add(Restrictions.eqProperty("IAF." + ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString(), 
//				"FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
//		criteria.add(Restrictions.eqProperty("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), 
//				"FSC." + ScoFaseSolicitacao.Fields.IAF_NUMERO.toString()));
//		criteria.add(Restrictions.eqProperty("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), 
//				"IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()));
		if(nrFornecedor != null){
			criteria.add(Restrictions.eqProperty("AFN." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString(), "FRN." + ScoAutorizacaoForn.Fields.NUMERO.toString()));
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString(), nrFornecedor));
		}
		
		
		ProjectionList projectionList = Projections.projectionList();
		
		projectionList.add(Projections.distinct(Projections.property("FRN." + ScoFornecedor.Fields.NUMERO.toString())), "numeroFornecedor")
					  .add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), "razaoSocialFornecedor");
		
		criteria.setProjection(projectionList);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProgramacaoEntregaGlobalFornecedoresVO.class));
		
	}

	public Integer getCodigoGrupoMaterial() {
		return codigoGrupoMaterial;
	}

	public void setCodigoGrupoMaterial(Integer codigoGrupoMaterial) {
		this.codigoGrupoMaterial = codigoGrupoMaterial;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	
	public EntregasGlobaisAcesso getTipoValorEnum() {
		return tipoValorEnum;
	}

	public void setTipoValorEnum(EntregasGlobaisAcesso tipoValorEnum) {
		this.tipoValorEnum = tipoValorEnum;
	}

	public Integer getNrFornecedor() {
		return nrFornecedor;
	}

	public void setNrFornecedor(Integer nrFornecedor) {
		this.nrFornecedor = nrFornecedor;
	}
	
}