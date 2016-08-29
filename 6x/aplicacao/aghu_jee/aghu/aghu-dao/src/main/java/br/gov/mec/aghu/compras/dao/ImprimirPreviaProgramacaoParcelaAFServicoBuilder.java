package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

import br.gov.mec.aghu.compras.vo.ParcelasAFVO;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;

public class ImprimirPreviaProgramacaoParcelaAFServicoBuilder extends ImprimirPreviaProgramacaoParcelaBuilder {

	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = super.createProduct();
		criteria.createAlias("fsc1." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "sls");
		criteria.createAlias("sls." + ScoSolicitacaoServico.Fields.FASES_SOLICITACAO.toString(), "fsc2");
		criteria.createAlias("fsc2." + ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "itl");
		criteria.createAlias("sls." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "srv");
		return criteria;
	}


	@Override
	protected ProjectionList listagemBasicaParcelas() {
		ProjectionList projectionList = super.listagemBasicaParcelas();
		projectionList.add(Projections.property("srv." + ScoServico.Fields.CODIGO.toString()), ParcelasAFVO.Fields.CODIGO.toString());
		projectionList.add(Projections.property("srv." + ScoServico.Fields.NOME.toString()), ParcelasAFVO.Fields.NOME.toString());
		return projectionList;
	}
	
}
