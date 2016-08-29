package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

import br.gov.mec.aghu.compras.vo.ParcelasAFVO;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;

public class ImprimirPreviaProgramacaoParcelaAFMaterialBuilder extends ImprimirPreviaProgramacaoParcelaBuilder {

	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = super.createProduct();
		criteria.createAlias("fsc1." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "slc");
		criteria.createAlias("slc." + ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "fsc2");
		criteria.createAlias("fsc2." + ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "itl");
		criteria.createAlias("slc." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "mat");
		return criteria;
	}


	@Override
	protected ProjectionList listagemBasicaParcelas() {
		ProjectionList projectionList = super.listagemBasicaParcelas();
		projectionList.add(Projections.property("pea." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()), ParcelasAFVO.Fields.PEA_QTDE.toString());
		projectionList.add(Projections.property("mat." + ScoMaterial.Fields.CODIGO.toString()), ParcelasAFVO.Fields.CODIGO.toString());
		projectionList.add(Projections.property("mat." + ScoMaterial.Fields.NOME.toString()), ParcelasAFVO.Fields.NOME.toString());
		return projectionList;
	}
	
}
