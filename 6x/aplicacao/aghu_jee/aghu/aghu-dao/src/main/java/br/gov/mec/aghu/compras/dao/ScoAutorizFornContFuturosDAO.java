package br.gov.mec.aghu.compras.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.AfFiltroVO;
import br.gov.mec.aghu.compras.vo.AfsContratosFuturosVO;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioPossuiSIASG;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoItemContrato;
import br.gov.mec.aghu.model.FsoConveniosFinanceiro;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialSicon;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoServicoSicon;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;


public class ScoAutorizFornContFuturosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoAutorizacaoForn> {
	
	private static final long serialVersionUID = 7560943552026609157L;

	/**
	 * 
	 * @param filtro
	 * @param param
	 * @return
	 */
	public List<AfsContratosFuturosVO> montarListaItemAutorizacaoFornVO(AfFiltroVO filtro, DominioModalidadeEmpenho param) {
        DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AFN");
        criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR");
        criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString(), "IAFN", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.CVF_CODIGO.toString(), "CVF");
        criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.ITEM.toString(), "IPF", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN");  
        criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.LICITACAO.toString(), "LCT"); 
        criteria.createAlias("IAFN." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC");
        criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
        DominioSituacaoAutorizacaoFornecimento[] situacoesInvalidas = {DominioSituacaoAutorizacaoFornecimento.EX, DominioSituacaoAutorizacaoFornecimento.EF};
        Criterion in = Restrictions.in(ScoAutorizacaoForn.Fields.SITUACAO.toString(), situacoesInvalidas); 
        criteria.add(Restrictions.not(in));
        criteria.add(Restrictions.or(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString(), DominioModalidadeEmpenho.CONTRATO),
        Restrictions.eq("LCT." + ScoLicitacao.Fields.MODALIDADE_EMPENHO.toString(), DominioModalidadeEmpenho.CONTRATO)));
        criteria.add(Restrictions.eq("IPF." + ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(), Boolean.TRUE));
        criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		final DetachedCriteria subQuerieAFContrato = DetachedCriteria.forClass(ScoAfContrato.class, "AFC");
		subQuerieAFContrato.setProjection(Projections.property("AFC." + ScoAfContrato.Fields.SEQ.toString()));
		subQuerieAFContrato.add(Property.forName("AFC." + ScoAfContrato.Fields.AUT_FORN.toString()).eqProperty("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString()));
		criteria.add(Subqueries.notExists(subQuerieAFContrato));
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------
		montarFiltros1(criteria,filtro);
		montarFiltros2(criteria,filtro);
		//--------------------------------------------------------------------------------------------------------------------------------------------------------------
		ProjectionList p = Projections.projectionList();
    	p.add(Projections.groupProperty("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), AfsContratosFuturosVO.Fields.AF_NUMERO.toString())
    	 .add(Projections.groupProperty("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString()), AfsContratosFuturosVO.Fields.PK_AF.toString())
		 .add(Projections.groupProperty("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), AfsContratosFuturosVO.Fields.NRO_COMPLEMENTO.toString())
		 .add(Projections.groupProperty("FRN." + ScoFornecedor.Fields.NUMERO.toString()), AfsContratosFuturosVO.Fields.NRO_FORNECEDOR.toString())
		 .add(Projections.groupProperty("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), AfsContratosFuturosVO.Fields.RAZAO_SOCIAL.toString())
		 .add(Projections.groupProperty("CVF." + FsoConveniosFinanceiro.Fields.CODIGO.toString()), AfsContratosFuturosVO.Fields.NRO_CONVENIO_FINANCEIRO.toString())
		 .add(Projections.max("CVF." + FsoConveniosFinanceiro.Fields.DESC.toString()), AfsContratosFuturosVO.Fields.CONVENIO_FINANCEIRO.toString())
		 .add(Projections.max("IPF." + ScoItemPropostaFornecedor.Fields.QUANTIDADE.toString()), AfsContratosFuturosVO.Fields.QUANTIDADE_ITEM.toString())
		 .add(Projections.max("IPF." + ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString()), AfsContratosFuturosVO.Fields.VALOR_UNIT_ITEM.toString())
		 .add(Projections.max("LCT." + ScoLicitacao.Fields.FREQUENCIA_ENTREGA.toString()), AfsContratosFuturosVO.Fields.FREQ_ENTREGA_LIC.toString())
		 .add(Projections.max("IAFN." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString()), AfsContratosFuturosVO.Fields.AF.toString());
		criteria.setProjection(p);
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------- 
		criteria.setResultTransformer(Transformers.aliasToBean(AfsContratosFuturosVO.class));
		
		List<AfsContratosFuturosVO> lista = executeCriteria(criteria); 
		List<AfsContratosFuturosVO> listaInicializada = new ArrayList<AfsContratosFuturosVO>(); 
		
		for (AfsContratosFuturosVO vo : lista) {
			vo.getAf().setNumero(vo.getPkAf());
			Hibernate.initialize(vo.getAf().getNumero());
			listaInicializada.add(vo);
		}
		
        return listaInicializada;
	}
	
	private void montarFiltros1(DetachedCriteria criteria, AfFiltroVO filtro)	{
			//Licitação
			if ((filtro.getLicitacao() != null) && (filtro.getLicitacao().getNumero() != null)) {	
				criteria.add(Restrictions.eq("LCT." + ScoLicitacao.Fields.NUMERO.toString(),filtro.getLicitacao().getNumero()));
			}
			//Filtro de AF 
			if ((filtro.getNumeroAf() != null) && (filtro.getNroComplementoAf() != null)) {	
				criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(),filtro.getNumeroAf()));					
				criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(),filtro.getNroComplementoAf().shortValue()));
			}	
			//Filtro de Fornecedor 
			if ((filtro.getFornecedor() != null) && (filtro.getFornecedor().getNumero()!= null)) {	
				criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(),filtro.getFornecedor().getNumero()));					
			}	
			//Filtro de Tipo de Serviço: serviço
			if (DominioTipoItemContrato.S.equals(filtro.getTipoItens())) {
				final DetachedCriteria subQuerieTpServico = DetachedCriteria.forClass(ScoSolicitacaoServico.class, "SLS");
				subQuerieTpServico.setProjection(Projections.property("SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString()));
				subQuerieTpServico.add(Property.forName("SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString()).eqProperty("FSC." + ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()));
				criteria.add(Subqueries.exists(subQuerieTpServico));	
			}
			//Filtro de Tipo de Serviço: material
			if (DominioTipoItemContrato.M.equals(filtro.getTipoItens())) {
				final DetachedCriteria subQuerieTpMaterial = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
				subQuerieTpMaterial.setProjection(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
				subQuerieTpMaterial.add(Property.forName("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).eqProperty("FSC." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
				criteria.add(Subqueries.exists(subQuerieTpMaterial));	
			}
			//Filtro de De Para Siasg: pendente/existente
			if (DominioPossuiSIASG.P.equals(filtro.getCodSiasg()) || DominioPossuiSIASG.E.equals(filtro.getCodSiasg())) {
				final DetachedCriteria subQueriePendenteSiasg = DetachedCriteria.forClass(ScoSolicitacaoServico.class, "SLS");
				subQueriePendenteSiasg.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV");	
				subQueriePendenteSiasg.createAlias("SRV." + ScoServico.Fields.SERVICO_SICON.toString(), "SRVS");
				subQueriePendenteSiasg.setProjection(Projections.property("SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString()));
				
				subQueriePendenteSiasg.add(Property.forName("SRV." + ScoServico.Fields.CODIGO.toString()).eqProperty("SLS." + ScoSolicitacaoServico.Fields.SERVICO_CODIGO.toString()));
				subQueriePendenteSiasg.add(Property.forName("SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString()).eqProperty("FSC." + ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()));
				subQueriePendenteSiasg.add(Property.forName("SRVS." + ScoServicoSicon.Fields.SERVICO.toString()).eqProperty("SRV." + ScoServico.Fields.CODIGO.toString()));
				subQueriePendenteSiasg.add(Property.forName("SRVS." + ScoServicoSicon.Fields.SITUACAO.toString()).eq("="+DominioSituacao.A.toString() +")"));
	
				final DetachedCriteria subQuerieExistenteSiasg = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
				subQuerieExistenteSiasg.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
				subQuerieExistenteSiasg.createAlias("MAT." + ScoMaterial.Fields.MATERIAL_SICON.toString(), "MATS");
				subQuerieExistenteSiasg.setProjection(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
				
				subQuerieExistenteSiasg.add(Property.forName("MAT." + ScoMaterial.Fields.CODIGO.toString()).eqProperty("SLC." + ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()));
				subQuerieExistenteSiasg.add(Property.forName("SLC." +  ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).eqProperty("FSC." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
				subQuerieExistenteSiasg.add(Property.forName("MATS." + ScoMaterialSicon.Fields.MATERIAL.toString()).eqProperty("MAT." + ScoMaterial.Fields.CODIGO.toString()));
				subQuerieExistenteSiasg.add(Property.forName("MATS." + ScoMaterialSicon.Fields.SITUACAO.toString()).eq("="+DominioSituacao.A.toString() +")"));
				
			//Filtro de De Para Siasg: pendente
			if (DominioPossuiSIASG.P.equals(filtro.getCodSiasg())) {
				criteria.add(Restrictions.or(Subqueries.notExists(subQueriePendenteSiasg),Subqueries.notExists(subQuerieExistenteSiasg)));
			}
			//Filtro de De Para Siasg: existente
			if (DominioPossuiSIASG.E.equals(filtro.getCodSiasg())) {
				criteria.add(Restrictions.or(Subqueries.exists(subQueriePendenteSiasg),Subqueries.exists(subQuerieExistenteSiasg)));
			}
		}
	}
	private void montarFiltros2(DetachedCriteria criteria,AfFiltroVO filtro)	{
		//Filtro de Grupo de Serviço
		if (filtro.getGrupoServico() != null) {
			final DetachedCriteria subQuerieGrpServico = DetachedCriteria.forClass(ScoSolicitacaoServico.class, "SLS");
			subQuerieGrpServico.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV");
			subQuerieGrpServico.setProjection(Projections.property("SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString()));
			subQuerieGrpServico.add(Property.forName("SRV." + ScoServico.Fields.GRUPO_SERVICO_CODIGO.toString()).eq(filtro.getGrupoServico().getCodigo()));
			subQuerieGrpServico.add(Property.forName("SRV." + ScoServico.Fields.CODIGO.toString()).eqProperty("SLS." + ScoSolicitacaoServico.Fields.SERVICO_CODIGO.toString()));
			subQuerieGrpServico.add(Property.forName("SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString()).eqProperty("FSC." + ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()));
			criteria.add(Subqueries.exists(subQuerieGrpServico));	
		}
		//Filtro de Serviço
		if (filtro.getServico() != null) {
			final DetachedCriteria subQuerieServico = DetachedCriteria.forClass(ScoSolicitacaoServico.class, "SLS");
			subQuerieServico.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV");
			subQuerieServico.setProjection(Projections.property("SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString()));
			subQuerieServico.add(Property.forName("SRV." + ScoServico.Fields.CODIGO.toString()).eq(filtro.getServico().getCodigo()));
			subQuerieServico.add(Property.forName("SRV." + ScoServico.Fields.CODIGO.toString()).eqProperty("SLS." + ScoSolicitacaoServico.Fields.SERVICO_CODIGO.toString()));
			subQuerieServico.add(Property.forName("SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString()).eqProperty("FSC." + ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()));
			criteria.add(Subqueries.exists(subQuerieServico));
		}
		//Filtro de Grupo de Material
		if (filtro.getGrupoMaterial() != null) {
			final DetachedCriteria subQuerieGrpMaterial = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
			subQuerieGrpMaterial.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
			subQuerieGrpMaterial.setProjection(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
			subQuerieGrpMaterial.setProjection(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()));
			subQuerieGrpMaterial.add(Property.forName("MAT." +  ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString()).eq(filtro.getGrupoMaterial().getCodigo()));
			subQuerieGrpMaterial.add(Property.forName("MAT." + ScoMaterial.Fields.CODIGO.toString()).eqProperty("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL_CODIGO.toString()));
			subQuerieGrpMaterial.add(Property.forName("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).eqProperty("FSC." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
			criteria.add(Subqueries.exists(subQuerieGrpMaterial));
		}
		//Filtro de Material
		if (filtro.getMaterial() != null) {
			final DetachedCriteria subQuerieMaterial = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
			subQuerieMaterial.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
			subQuerieMaterial.setProjection(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
			subQuerieMaterial.add(Property.forName("MAT." +  ScoMaterial.Fields.CODIGO.toString()).eq(filtro.getMaterial().getCodigo()));
			subQuerieMaterial.add(Property.forName("MAT." + ScoMaterial.Fields.CODIGO.toString()).eqProperty("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL_CODIGO.toString()));
			subQuerieMaterial.add(Property.forName("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).eqProperty("FSC." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
			criteria.add(Subqueries.exists(subQuerieMaterial));
		}
	}
}
