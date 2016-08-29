package br.gov.mec.aghu.compras.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.compras.vo.BaseCalculoSaldoAfVO;
import br.gov.mec.aghu.compras.vo.FiltroConsSCSSVO;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.estoque.vo.GeracaoMovimentoEstoqueVO;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.suprimentos.vo.ScoFaseSolicitacaoVO;
import br.gov.mec.aghu.core.commons.CoreUtil;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class ScoFaseSolicitacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoFaseSolicitacao>{
	// Aliases
	private static final String IAF = "iaf", FSC = "fsc", SLC = "slc";

	
	private static final long serialVersionUID = -6509785179412999611L;

	public Long obterQuantidadeFaseSolicitacaoPeloItemLicitacaoPeloTipoEIndExclusao(ScoItemLicitacao itemLicitacao, DominioTipoFaseSolicitacao tipo, Boolean exclusao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class);
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), itemLicitacao));
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.TIPO.toString(), tipo));
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), exclusao));
		return executeCriteriaCount(criteria);
	}	
	public List<ScoFaseSolicitacao> pesquisarAutorizacaoFormAntigaNaoEncerrada(Integer codigoMaterial, String codigoUnidadeMedida) {
		
		if (codigoMaterial == null) {
			throw new IllegalArgumentException("Parâmetro codigoMaterial não foi informado.");
		}		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", Criteria.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", Criteria.INNER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		DominioSituacaoAutorizacaoFornecedor[] situacoesAutorizacaoFornecedor = {DominioSituacaoAutorizacaoFornecedor.AE, DominioSituacaoAutorizacaoFornecedor.PA};
		criteria.add(Restrictions.in("IAF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), situacoesAutorizacaoFornecedor));
		if (codigoUnidadeMedida != null) {
			criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.UNIDADE_CODIGO.toString(), codigoUnidadeMedida));
		}
		criteria.add(Property.forName("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString()).eqProperty("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<ScoFaseSolicitacao> pesquisarFasePorIafAfnNumeroIafNumero(Integer iafAfnNumero, Integer iafNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IAF_NUMERO.toString(), iafNumero));
		return executeCriteria(criteria);
	}
	
	public List<ScoServico> pesquisaCodigoSolicitacaoServico(ScoItemAutorizacaoFornId itemAutorizacaoFornId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		criteria.setProjection(Projections.property("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString()));
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", Criteria.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", Criteria.INNER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", Criteria.INNER_JOIN);
		criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SER", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), itemAutorizacaoFornId.getAfnNumero()));
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), itemAutorizacaoFornId.getNumero()));
		return this.executeCriteria(criteria);
	}

	public Boolean pesquisaTipoFaseSolicitacao(ScoItemAutorizacaoFornId itemAutorizacaoFornId) {
		Boolean servico = false;
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		criteria.setProjection(Projections.property("FSC." + ScoFaseSolicitacao.Fields.TIPO.toString()));
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", Criteria.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), itemAutorizacaoFornId.getAfnNumero()));
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), itemAutorizacaoFornId.getNumero()));
		List<DominioTipoFaseSolicitacao> list = this.executeCriteria(criteria);
		if(list != null && !list.isEmpty()){
			for(DominioTipoFaseSolicitacao tipo: list){
				if(tipo.equals(DominioTipoFaseSolicitacao.S)){
					servico = true;
				}
			}
		}
		return servico;
	}

	public List<ScoMaterial> pesquisaMaterialSolicitacaoCompras(ScoItemAutorizacaoFornId itemAutorizacaoFornId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		criteria.setProjection(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString()));
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", Criteria.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", Criteria.INNER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", Criteria.INNER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), itemAutorizacaoFornId.getAfnNumero()));
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), itemAutorizacaoFornId.getNumero()));
		return this.executeCriteria(criteria);
	}
	
	public Integer obterNumeroSolicitacaoCompraOriginal(ScoItemAutorizacaoFornId itemAutorizacaoFornId, boolean isSC){
		Integer numSolicitacaoOriginal = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		ProjectionList p = Projections.projectionList();
		if (isSC){
			p.add(Projections.min("FSC." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
		} else {
			p.add(Projections.min("FSC." + ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()));
		}
		criteria.setProjection(p);
		
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), itemAutorizacaoFornId.getAfnNumero()));
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IAF_NUMERO.toString(), itemAutorizacaoFornId.getNumero()));
		
		List<Object[]> resultList = executeCriteria(criteria);
		
		if(resultList != null && resultList.size() > 0) {
			Object objects = resultList.get(0);
			numSolicitacaoOriginal = (Integer)objects;		
		}			
		
		return numSolicitacaoOriginal;		
	}
	

	public ScoSolicitacaoDeCompra pesquisaSolicitacaoCompras(ScoItemAutorizacaoFornId itemAutorizacaoFornId) {
		
		ScoSolicitacaoDeCompra retorno = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");

		ProjectionList p = Projections.projectionList();

		p.add(Projections.groupProperty("SLC." + ScoSolicitacaoDeCompra.Fields.UNIDADE_MEDIDA.toString()));
		p.add(Projections.min("SLC." + ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString()));
		
		criteria.setProjection(p);

		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", Criteria.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", Criteria.INNER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", Criteria.INNER_JOIN);

		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));

		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), itemAutorizacaoFornId.getAfnNumero()));
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), itemAutorizacaoFornId.getNumero()));
		
		
		criteria.addOrder(Order.asc("SLC."+ScoSolicitacaoDeCompra.Fields.UMD_CODIGO.toString()));
		
		List<Object[]> resultList = executeCriteria(criteria);
		
		if(resultList != null && resultList.size()>0) {
			
			Object[] objects = resultList.get(0);
			retorno = new ScoSolicitacaoDeCompra();
			
			retorno.setUnidadeMedida((ScoUnidadeMedida)objects[0]);
			retorno.setDtSolicitacao((Date)objects[1]);			
		}			
		return retorno;
	}
	
	public List<ScoFaseSolicitacao> pesquisaFasePorItensAutForn(ScoItemAutorizacaoFornId itemAutorizacaoFornId) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class);
		
		criteria.createAlias(ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "IPF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createCriteria(ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "fs_comp", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("fs_comp." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "comp_mat", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("comp_mat." + ScoMaterial.Fields.MATERIAL_SICON.toString(), "comp_matsicon", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createCriteria(ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "fs_serv", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("fs_serv." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "serv", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("serv." + ScoServico.Fields.SERVICO_SICON.toString(), "serv_sicon", JoinType.LEFT_OUTER_JOIN);		
		
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		
		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), itemAutorizacaoFornId.getAfnNumero()));
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), itemAutorizacaoFornId.getNumero()));
		
		List<ScoFaseSolicitacao> resultList = executeCriteria(criteria);
		
		return resultList;
		
	}

	public List<ScoFaseSolicitacao> pesquisaItensAutPropostaFornecedor(ScoItemAutorizacaoFornId itemAutorizacaoFornId) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");

		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", Criteria.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", Criteria.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "IPF", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		
		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), itemAutorizacaoFornId.getAfnNumero()));
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), itemAutorizacaoFornId.getNumero()));
		
		List<ScoFaseSolicitacao> resultList = executeCriteria(criteria);
		
		if(resultList != null && resultList.size()>0) {
			return resultList;
		}			
		return null;
	}
	
	public Boolean pesquisaIndConsignado(ScoItemAutorizacaoFornId itemAutorizacaoFornId) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");

		criteria.setProjection(Projections.groupProperty("EAL." + SceEstoqueAlmoxarifado.Fields.IND_CONSIGNADO.toString()));
	
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", Criteria.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", Criteria.INNER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", Criteria.INNER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", Criteria.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAL", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eqProperty("EAL." + SceEstoqueAlmoxarifado.Fields.NRO_SOLICITACAO_COMPRA.toString(), "SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), itemAutorizacaoFornId.getAfnNumero()));
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IAF_NUMERO.toString(), itemAutorizacaoFornId.getNumero()));
			
		return (Boolean) executeCriteriaUniqueResult(criteria);
	}
	public DominioTipoFaseSolicitacao pesquisaTipoFaseSolic(ScoItemAutorizacaoFornId itemAutorizacaoFornId) {

		DominioTipoFaseSolicitacao retorno =null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		criteria.setProjection(Projections.property("FSC." + ScoFaseSolicitacao.Fields.TIPO.toString()));

		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", Criteria.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), itemAutorizacaoFornId.getAfnNumero()));
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), itemAutorizacaoFornId.getNumero()));

		List<DominioTipoFaseSolicitacao> list = this.executeCriteria(criteria);

		if(list != null && !list.isEmpty()){
			return list.get(0);	
		}

		return retorno;
	}
	
	public List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoPorNumeroSolCompraComItemLict(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class);

		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString() + "." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), numero));
		criteria.add(Restrictions.isNotNull(ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString()));
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		
		return executeCriteria(criteria);
	}

	public List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoComprasPorAutorizacaoFornecimento(Integer afnNumero, Integer numero, Boolean indExclusao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", JoinType.INNER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		
		if (indExclusao != null){
		   criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), indExclusao));
		}
		
		criteria.add(Restrictions.eq("FSC."  + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq("FSC."  + ScoFaseSolicitacao.Fields.IAF_NUMERO.toString(), numero));
		
		return executeCriteria(criteria);
	}

	public List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoServicoPorAutorizacaoFornecimento(Integer afnNumero, Integer numero, Boolean indExclusao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.INNER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", JoinType.INNER_JOIN);
		criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV", JoinType.INNER_JOIN);
		
		if (indExclusao != null){
		   criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), indExclusao));
		}
		
		criteria.add(Restrictions.eq("FSC."  + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq("FSC."  + ScoFaseSolicitacao.Fields.IAF_NUMERO.toString(), numero));
		
		return executeCriteria(criteria);
	}

	public List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoPorNumeroSolCompra(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class);
		
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString() + "." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), numero));
		
		return executeCriteria(criteria);
	}
	
	public List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoPorNumeroSolCompraIndExcAfnSit(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", Criteria.INNER_JOIN);
		criteria.createAlias("FSC." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("FSC."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.isNotNull("FSC."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
		criteria.add(Restrictions.in("AFN."+ScoAutorizacaoForn.Fields.SITUACAO.toString(), new DominioSituacaoAutorizacaoFornecimento[]{DominioSituacaoAutorizacaoFornecimento.AE, DominioSituacaoAutorizacaoFornecimento.PA}));
		
		return executeCriteria(criteria);
	}

	public List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoPorNumeroSolCompraComAutForn(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", Criteria.INNER_JOIN);
				
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), numero));
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Busca qtde saldo em AF para o material, em AF que não esta em contrato.
	 * 
	 * @param scId ID da SC
	 * @param materialId ID do Material
	 * @return Saldo (base para cálculo)
	 */
	public List<BaseCalculoSaldoAfVO> buscarSaldoEmAfSemContrato(Integer scId, Integer materialId) {
		assert scId != null : "ID da SC não definido";
		final String AF = "af";
		DetachedCriteria criteria = getSaldoAfCriteria(materialId);
		projectSaldoByIaf(criteria);
		criteria.createAlias(IAF + '.' + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), AF);
		criteria.add(Restrictions.ne(SLC + '.' + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), scId));
		criteria.add(Restrictions.eq(AF + '.' + ScoAutorizacaoForn.Fields.IND_ENTREGA_PROGRAMADA.toString(), false));
		criteria.add(Restrictions.eq(SLC + '.' + ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), materialId));
		return executeCriteria(criteria);
	}
	
	public List<ScoFaseSolicitacao> obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(Integer numeroLCT, Short numeroItem) {
		DetachedCriteria criteria = getCriteriaobterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(numeroLCT, numeroItem);
		criteria.addOrder(Order.asc(ScoFaseSolicitacao.Fields.NUMERO.toString()));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria getCriteriaobterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(Integer numeroLCT, Short numeroItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class);
		criteria.createAlias(ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ITL");
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.LCT_NUMERO.toString(), numeroLCT));
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString(), numeroItem));
		return criteria;
	}

	public Long pesquisarQuantidadeSCOutraLicitacaoPorNumeroLCTENumeroItem(Integer numeroLCT, Short numeroItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class,"FSO");
		
		criteria.add(Restrictions.eq("FSO."+ScoFaseSolicitacao.Fields.LCT_NUMERO.toString(), numeroLCT));
		criteria.add(Restrictions.eq("FSO."+ScoFaseSolicitacao.Fields.ITL_NUMERO.toString(), numeroItem));
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(ScoFaseSolicitacao.class);
		
		subQuery.setProjection(Projections.projectionList()
		.add(Projections.property(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())));
		
		subQuery.add((Restrictions.eqProperty(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(),"FSO."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString() )));
		subQuery.add(Restrictions.ne(ScoFaseSolicitacao.Fields.LCT_NUMERO.toString(), numeroLCT));
		subQuery.add(Restrictions.eq(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
	
		criteria.add(Subqueries.propertyIn("FSO."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), subQuery));
		return executeCriteriaCount(criteria);		
		
	}
	
	public Long pesquisarQuantidadeSSOutraLicitacaoPorNumeroLCTENumeroItem(Integer numeroLCT, Short numeroItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class,"FSO");
		
		criteria.add(Restrictions.eq("FSO."+ScoFaseSolicitacao.Fields.LCT_NUMERO.toString(), numeroLCT));
		criteria.add(Restrictions.eq("FSO."+ScoFaseSolicitacao.Fields.ITL_NUMERO.toString(), numeroItem));
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(ScoFaseSolicitacao.class);
		
		subQuery.setProjection(Projections.projectionList()
		.add(Projections.property(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString())));
		
		subQuery.add((Restrictions.eqProperty(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString(),"FSO."+ScoFaseSolicitacao.Fields.SLS_NUMERO.toString() )));
		subQuery.add(Restrictions.ne(ScoFaseSolicitacao.Fields.LCT_NUMERO.toString(), numeroLCT));
		subQuery.add(Restrictions.eq(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
	
		criteria.add(Subqueries.propertyIn("FSO."+ScoFaseSolicitacao.Fields.SLS_NUMERO.toString(), subQuery));
		return executeCriteriaCount(criteria);		
	}
	
	public List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoPorNumeroSolServicoComItemLict(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class);
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString() + "." + ScoSolicitacaoServico.Fields.NUMERO.toString(), numero));
		criteria.add(Restrictions.isNotNull(ScoFaseSolicitacao.Fields.LCT_NUMERO.toString()));
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		return executeCriteria(criteria);
	}
	
	public List<Object[]> createCalculoValorTotalAFHql (Integer afnNumero, Integer seqAlteracao){
		StringBuffer hql = new StringBuffer(720);
		hql.append(" select iaf.qtdeSolicitada, ");
		hql.append(" 		iaf.valorUnitario, ");
		hql.append(" 		iaf.percIpi, ");
		hql.append(" 		iaf.percAcrescimoItem, ");
		hql.append(" 		iaf.percDesconto, ");
		hql.append(" 		iaf.percAcrescimo, ");
		hql.append(" 		iaf.percDescontoItem, ");
		hql.append(" 		iaf.valorEfetivado, ");
		hql.append(" 		fsc.tipo, ");
		hql.append(" 		iaf.qtdeRecebida ");
		hql.append(" from  ").append(ScoFaseSolicitacao.class.getName()).append(" fsc, ");
		hql.append(" 	   ").append(ScoItemAutorizacaoFornJn.class.getName()).append(" iaf");
		hql.append(" where iaf.afnNumero = :afnNumero");
		hql.append(" 	and iaf.sequenciaAlteracao <= :seqAlteracao");
		hql.append(" 	and iaf.indSituacao != 'EX'");
		hql.append(" 	and iaf.sequenciaAlteracao IN");
		hql.append(" 			(select max(iaf2.sequenciaAlteracao) ");
		hql.append(" 				from ").append(ScoItemAutorizacaoFornJn.class.getName()).append(" iaf2");
		hql.append(" 				where iaf2.afnNumero = iaf.afnNumero");
		hql.append(" 					and iaf2.numero = iaf.numero");
		hql.append(" 					and iaf2.sequenciaAlteracao <= :seqAlteracao)");
		hql.append(" 	and fsc.itemAutorizacaoForn.id.afnNumero = iaf.afnNumero");
		hql.append(" 	and fsc.itemAutorizacaoForn.id.numero = iaf.numero");
		hql.append(" 	order by iaf.numero");
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("afnNumero",  afnNumero);
		query.setParameter("seqAlteracao", seqAlteracao);
		return query.list();
	}
		
	/**
	 * #5521 
	 * @author lucasbuzzo
	 * @param Integer pLctNumero, Short pItlNumero, DominioTipoFaseSolicitacao pTipoSol
	 * @return ScoFaseSolicitacao 
	 */
	public ScoFaseSolicitacao scocBuscaSolLicit (Integer pLctNumero, Short pItlNumero, DominioTipoFaseSolicitacao pTipoSol){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class);
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.LCT_NUMERO.toString(), pLctNumero));
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString(), pItlNumero));
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE ));
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.TIPO.toString(), pTipoSol ));
		return (ScoFaseSolicitacao) executeCriteriaUniqueResult(criteria);
	}
	
	/*****
	 * Consulta C16 estoria 21628 - verifica se a sc esta ativa em licitação
	 * autor: Flavio Rutkowski
	 */
	public ScoFaseSolicitacao obterSCAtivoLicitacao (ScoSolicitacaoDeCompra scoSolicitacaoDeCompra){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ITEM_LIC", Criteria.INNER_JOIN);
		if (scoSolicitacaoDeCompra != null){
			criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), scoSolicitacaoDeCompra));
			criteria.add(Restrictions.eq("ITEM_LIC." + ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		}
		return (ScoFaseSolicitacao) executeCriteriaUniqueResult(criteria);		
	}
	
	/*****
	 * Consulta C17 estoria 21628 - verifica se a sc está ativa em fases de licitação
	 * autor: Flavio Rutkowski
	 */
	public ScoFaseSolicitacao obterSCAtivoFasesLicitacao (ScoSolicitacaoDeCompra scoSolicitacaoDeCompra){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");		
		if (scoSolicitacaoDeCompra != null){
			criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), scoSolicitacaoDeCompra));
			criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			criteria.add(Restrictions.isNotNull("FSC." + ScoFaseSolicitacao.Fields.LCT_NUMERO.toString()));
		}
		return (ScoFaseSolicitacao) executeCriteriaUniqueResult(criteria);
	}
	
	public ScoFaseSolicitacao obterSCAtivoFasesAF (ScoSolicitacaoDeCompra scoSolicitacaoDeCompra){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		if (scoSolicitacaoDeCompra != null){
			criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), scoSolicitacaoDeCompra));			
		}
		final DetachedCriteria subQuerie = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AUT_FORN");
		subQuerie.setProjection(Projections.property("AUT_FORN." + ScoAutorizacaoForn.Fields.NUMERO.toString()));
		subQuerie.add(Property.forName("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()).eqProperty("AUT_FORN." + ScoAutorizacaoForn.Fields.NUMERO.toString()));
		criteria.add(Subqueries.exists(subQuerie));	  
		return (ScoFaseSolicitacao) executeCriteriaUniqueResult(criteria);
	}
	
	public ScoFaseSolicitacao obterSCAtivaAF (ScoSolicitacaoDeCompra scoSolicitacaoDeCompra){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "ITEM_AF", Criteria.INNER_JOIN);
		criteria.createAlias("ITEM_AF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AF", Criteria.INNER_JOIN);
		
		if (scoSolicitacaoDeCompra != null){
			criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), scoSolicitacaoDeCompra));
			criteria.add(Restrictions.ne("ITEM_AF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecedor.EX));
		}
		return (ScoFaseSolicitacao) executeCriteriaUniqueResult(criteria);
	}
	
	public List<ScoFaseSolicitacao> listarDadosLicitacao(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class);
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), numero));
		criteria.add(Restrictions.isNull(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()));
		criteria.add(Restrictions.isNull(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
		criteria.add(Restrictions.isNull(ScoFaseSolicitacao.Fields.IAF_NUMERO.toString()));
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.TIPO.toString(), DominioTipoFaseSolicitacao.C));
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.isNotNull(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));		
		criteria.addOrder(Order.asc(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));
		return executeCriteria(criteria);		
	}

	public List<ScoFaseSolicitacao> listarDadosLicitacaoSs(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FS");
		criteria.createAlias("FS."+ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ITL", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("FS."+ScoFaseSolicitacao.Fields.SLS_NUMERO.toString(), numero));
		criteria.add(Restrictions.isNull("FS."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
		criteria.add(Restrictions.isNull("FS."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
		criteria.add(Restrictions.isNull("FS."+ScoFaseSolicitacao.Fields.IAF_NUMERO.toString()));
		criteria.add(Restrictions.eq("FS."+ScoFaseSolicitacao.Fields.TIPO.toString(), DominioTipoFaseSolicitacao.S));
		criteria.add(Restrictions.eq("FS."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.isNotNull("FS."+ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));		
		criteria.addOrder(Order.asc("FS."+ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));
		return executeCriteria(criteria);		
	}
	
	public ScoFaseSolicitacao obterDadosAutorizacaoFornecimento(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class);
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), numero));
		criteria.add(Restrictions.isNull(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()));
     	criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.TIPO.toString(), DominioTipoFaseSolicitacao.C));		
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.isNotNull(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
		return (ScoFaseSolicitacao) executeCriteriaUniqueResult(criteria);		
	}
	
	public ScoFaseSolicitacao obterDadosAutorizacaoFornecimentoSs(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class);
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString(), numero));
		criteria.add(Restrictions.isNull(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
     	criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.TIPO.toString(), DominioTipoFaseSolicitacao.S));		
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.isNotNull(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
		return (ScoFaseSolicitacao) executeCriteriaUniqueResult(criteria);		
	}
	
	public List<ScoFaseSolicitacao> obterFaseSolicitacao(Integer numero, boolean isNull, DominioTipoSolicitacao tipoSolicitacao){
		DetachedCriteria criteriaFases = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FASES");		
		if (DominioTipoSolicitacao.SC.equals(tipoSolicitacao)){
		   criteriaFases.add(Restrictions.eq("FASES." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), numero));
		}
		else if (DominioTipoSolicitacao.SS.equals(tipoSolicitacao)){
			criteriaFases.add(Restrictions.eq("FASES." + ScoFaseSolicitacao.Fields.SLS_NUMERO.toString(), numero));	
		}
		criteriaFases.add(Restrictions.eq("FASES." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(),false));
		if (isNull == true){
			criteriaFases.add(Restrictions.isNull("FASES." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));	
		}
		else {
			criteriaFases.add(Restrictions.isNotNull("FASES." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));	
		}		
		return this.executeCriteria(criteriaFases);		
	}
	
	public ScoFaseSolicitacao obterSsAtivoFasesLicitacao (ScoSolicitacaoServico solicitacaoServico){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");	
		if (solicitacaoServico != null){
			criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), solicitacaoServico));
			criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			criteria.add(Restrictions.isNotNull("FSC." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));
		}		
		return (ScoFaseSolicitacao) executeCriteriaUniqueResult(criteria);
	}
	
	public void obterCriteriaFaseSolicitacaoLicitacaoFiltro(DetachedCriteria criteriaFases, FiltroConsSCSSVO filtro){		
		if (filtro.getNumeroPAC() != null) {			
		    criteriaFases.add(Restrictions.eq("LIC." + ScoLicitacao.Fields.NUMERO.toString(), filtro.getNumeroPAC()));
		}				
		if (StringUtils.isNotBlank(filtro.getDescricaoPAC())) {	
			criteriaFases.add(Restrictions.ilike("LIC." + ScoLicitacao.Fields.DESCRICAO.toString(), filtro.getDescricaoPAC(), MatchMode.ANYWHERE));
		}
		if (filtro.getDataInicioAberturaPropostaPAC() != null){			
			criteriaFases.add(Restrictions.ge("LIC." + ScoLicitacao.Fields.DT_ABERTURA_PROPOSTA.toString(),filtro.getDataInicioAberturaPropostaPAC()));
		}		
        if (filtro.getDataFimAberturaPropostaPAC() != null){        	
        	criteriaFases.add(Restrictions.le("LIC." + ScoLicitacao.Fields.DT_ABERTURA_PROPOSTA.toString(),filtro.getDataFimAberturaPropostaPAC()));
		}		
        if (filtro.getExclusaoPAC() != null){        	
        	criteriaFases.add(Restrictions.eq("LIC." + ScoLicitacao.Fields.EXCLUSAO.toString(), filtro.getIndExclusaoPAC()));
        }        
        if (filtro.getNumDocPAC() != null){        	
        	criteriaFases.add(Restrictions.eq("LIC." + ScoLicitacao.Fields.NUM_DOC_LICIT.toString(), filtro.getNumDocPAC()));
        }   
	}
	public DetachedCriteria obterCriteriaFaseSolicitacaoLicitacao(FiltroConsSCSSVO filtro){
		DetachedCriteria criteriaFases = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FASES");	
		criteriaFases.createAlias(ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ITEM_LIC", Criteria.INNER_JOIN);
		criteriaFases.createAlias("ITEM_LIC." + ScoItemLicitacao.Fields.LICITACAO.toString(), "LIC", Criteria.INNER_JOIN);
		criteriaFases.setProjection(Projections.projectionList().add(Projections.property("FASES." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString())));		
		criteriaFases.add(Restrictions.eq("FASES." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(),false));
		criteriaFases.add(Restrictions.isNotNull("FASES." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));	
		this.obterCriteriaFaseSolicitacaoLicitacaoFiltro(criteriaFases, filtro);
		if (filtro.getNumEditalPAC() != null){
	       	criteriaFases.add(Restrictions.eq("LIC." + ScoLicitacao.Fields.NUM_EDITAL.toString(), filtro.getNumEditalPAC()));
	    }
	    if (filtro.getAnoComplementoPAC() != null){
	       	criteriaFases.add(Restrictions.eq("LIC." + ScoLicitacao.Fields.ANO_COMPLEMENTO.toString(), filtro.getAnoComplementoPAC()));
	    }
	    if (filtro.getModalidadeLicitacaoPAC() != null){
	      	criteriaFases.add(Restrictions.eq("LIC." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), filtro.getModalidadeLicitacaoPAC()));
	    }
	    if (filtro.getTipoPregaoPAC() != null){
	      	criteriaFases.add(Restrictions.eq("LIC." + ScoLicitacao.Fields.IND_TIPO_PREGAO.toString(), filtro.getTipoPregaoPAC()));
	    }
	    if (filtro.getArtigoPAC() != null){
	      	criteriaFases.add(Restrictions.eq("LIC." + ScoLicitacao.Fields.ARTIGO_LICITACAO.toString(), filtro.getArtigoPAC()));
	    }
	    if (filtro.getIncisoArtigoPAC() != null){
	      	criteriaFases.add(Restrictions.eq("LIC." + ScoLicitacao.Fields.INCISO_ARTIGO_LICITACAO.toString(), filtro.getIncisoArtigoPAC()));
	    }
	    if (filtro.getTipoPAC() != null){
	       	criteriaFases.add(Restrictions.eq("LIC." + ScoLicitacao.Fields.TIPO.toString(), filtro.getTipoPAC()));
	    }
	    if (filtro.getServidorGestorPAC() != null){
	      	criteriaFases.add(Restrictions.eq("LIC." + ScoLicitacao.Fields.SERVIDOR_GESTOR.toString(), filtro.getServidorGestorPAC()));
	    }       	
		return criteriaFases;
	}
	
	public void obterCriteriaFaseSolicitacaoAFFiltro(DetachedCriteria criteriaFases, FiltroConsSCSSVO filtro){		
		if (filtro.getNumeroAF() != null) {			
		    criteriaFases.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtro.getNumeroAF()));
		}		
		if (filtro.getNroComplementoAF() != null) {			
		    criteriaFases.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getNroComplementoAF()));
		}		
		if (filtro.getDataInicioGeracaoAF() != null){			
			criteriaFases.add(Restrictions.ge("AF." + ScoAutorizacaoForn.Fields.DT_GERACAO.toString(),filtro.getDataInicioGeracaoAF()));
		}		
        if (filtro.getDataFimGeracaoAF() != null){        	
        	criteriaFases.add(Restrictions.le("AF." + ScoAutorizacaoForn.Fields.DT_GERACAO.toString(),filtro.getDataFimGeracaoAF()));
		}
        if (filtro.getDataInicioPrevEntregaAF() != null){			
			criteriaFases.add(Restrictions.ge("AF." + ScoAutorizacaoForn.Fields.DT_PREV_ENTREGA.toString(),filtro.getDataInicioPrevEntregaAF()));
		}		
        if (filtro.getDataFimPrevEntregaAF() != null){        	
        	criteriaFases.add(Restrictions.le("AF." + ScoAutorizacaoForn.Fields.DT_PREV_ENTREGA.toString(),filtro.getDataFimPrevEntregaAF()));
		}	
        if (filtro.getExclusaoAF() != null){        	
        	criteriaFases.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.IND_EXCLUSAO.toString(), filtro.getIndExclusaoAF()));
        }  
        if (filtro.getFornecedorAF() != null){         	
        	criteriaFases.add(Restrictions.eq("PROP_FORNEC." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), filtro.getFornecedorAF()));
        }  		
	}
    public DetachedCriteria obterCriteriaFaseSolicitacaoAF(FiltroConsSCSSVO filtro){
		DetachedCriteria criteriaFases = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FASES");		
		criteriaFases.createAlias(ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "ITEM_AF", JoinType.INNER_JOIN);
		criteriaFases.createAlias("ITEM_AF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AF", JoinType.INNER_JOIN);
		criteriaFases.createAlias("AF." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PROP_FORNEC", JoinType.INNER_JOIN);		
		criteriaFases.setProjection(Projections.projectionList().add(Projections.property("FASES." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString())));				
		criteriaFases.add(Restrictions.eq("FASES." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(),false));
		criteriaFases.add(Restrictions.isNull("FASES." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));			
		this.obterCriteriaFaseSolicitacaoAFFiltro(criteriaFases, filtro);
        if (StringUtils.isNotBlank(filtro.getNroEmpenhoAF())) {			
		    criteriaFases.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.NRO_EMPENHO.toString(), filtro.getNroEmpenhoAF()));
		}	
        if (filtro.getNroContratoAF() != null){        	
        	criteriaFases.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString(), filtro.getNroContratoAF()));
        }  
        if (filtro.getServidorGestorAF() != null){        	
        	criteriaFases.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.SERVIDOR_GESTOR.toString(), filtro.getServidorGestorAF()));
        }               	
		return criteriaFases;
	}
    
		
    /**
     * Retorna uma lista para suggestion de fases da tela de cadastro de item de proposta
     * @param param
     * @param numeroPac
     * @return List
     */
    public List<ScoFaseSolicitacaoVO> pesquisarItemLicitacao(Object param, Integer numeroPac) {
		String strPesquisa = (String) param;
		
    	DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FS");
		criteria.createAlias(ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ITL", JoinType.INNER_JOIN);
    	criteria.createAlias("ITL."+ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FASES_ITEM", JoinType.LEFT_OUTER_JOIN);    	
		criteria.createAlias(ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.LEFT_OUTER_JOIN);		
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.UNIDADE_MEDIDA.toString(), "UND", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV", JoinType.LEFT_OUTER_JOIN);	
		criteria.add(Restrictions.eq("ITL."+ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(),  numeroPac));
		criteria.add(Restrictions.eq("FS."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));		
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq("ITL."+ScoItemLicitacao.Fields.NUMERO.toString(), Short.valueOf(strPesquisa)));
		} else if (StringUtils.isNotBlank(strPesquisa)) {
				criteria.add(
						Restrictions.or(Restrictions.ilike("MAT."+ScoMaterial.Fields.NOME.toString(),strPesquisa, MatchMode.ANYWHERE), 
										Restrictions.ilike("SRV."+ScoServico.Fields.NOME.toString(),strPesquisa, MatchMode.ANYWHERE))
						);
		}
		List<ScoFaseSolicitacao> listaFases = executeCriteria(criteria);
		List<ScoFaseSolicitacaoVO> listaRetorno = new ArrayList<ScoFaseSolicitacaoVO>();		
		for (ScoFaseSolicitacao fase : listaFases) {
			listaRetorno.add(montarFaseVO(fase));
		}		
		return listaRetorno;
    }
 
    private ScoFaseSolicitacaoVO montarFaseVO(ScoFaseSolicitacao fase) {
		ScoFaseSolicitacaoVO obj = new ScoFaseSolicitacaoVO();		
		if (fase.getItemLicitacao() != null) {
			obj.setNumeroPac(fase.getItemLicitacao().getId().getLctNumero());
			obj.setNumeroItemPac(fase.getItemLicitacao().getId().getNumero());
			obj.setItemLicitacao(fase.getItemLicitacao());
			obj.setDescricaoSolicitacao(obterNomeMaterialServico(fase.getItemLicitacao(),false));
			if (fase.getSolicitacaoDeCompra() != null) {
				obj.setSolicitacaoCompra(fase.getSolicitacaoDeCompra());
			}
			if (fase.getSolicitacaoServico() != null) {
				obj.setSolicitacaoServico(fase.getSolicitacaoServico());
			}
		} else {
			if (fase.getSolicitacaoDeCompra() != null && fase.getSolicitacaoDeCompra().getMaterial().getCodigo() != null){
				obj.setDescricaoSolicitacao(fase.getSolicitacaoDeCompra().getMaterial().getNome());
				obj.setSolicitacaoCompra(fase.getSolicitacaoDeCompra());
			}
			else {
				obj.setDescricaoSolicitacao(fase.getSolicitacaoServico().getServico().getNome());
				obj.setSolicitacaoServico(fase.getSolicitacaoServico());
			}
		}
		return obj;
    }
 
	/**
	 * Monta o VO para suggestion de fases pela PK do item da licitacao
	 * @param numeroLCT
	 * @param numeroItem
	 * @return
	 */
	public ScoFaseSolicitacaoVO obterFasesVOPorNumeroLicitacaoENumeroItemLicitacao(Integer numeroLCT, Short numeroItem) {
		List<ScoFaseSolicitacao> listaFases = this.obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(numeroLCT, numeroItem);
		ScoFaseSolicitacaoVO faseVO = null;		
		if (listaFases != null && !listaFases.isEmpty()) {
			faseVO = montarFaseVO(listaFases.get(0)); 
		}		
		return faseVO;
	}

    /**
     * Retorna o nome do material ou servico de um item de licitacao, concatenando o código caso necessário
     * @param item
     * @param concatenarCodigo
     * @return String
     */
    public String obterNomeMaterialServico(ScoItemLicitacao item, Boolean concatenarCodigo) {
    	final String FSL = "FSL.", ITL = "ITL.", SLC = "SLC.", SLS = "SLS.";
    	
    	DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSL");
    	criteria.createAlias(FSL+ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ITL", JoinType.INNER_JOIN);
    	criteria.createAlias(FSL+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.LEFT_OUTER_JOIN);
    	criteria.createAlias(FSL+ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.LEFT_OUTER_JOIN);
    	criteria.createAlias(SLC+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.LEFT_OUTER_JOIN);
    	criteria.createAlias(SLS+ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV", JoinType.LEFT_OUTER_JOIN);
    	criteria.add(Restrictions.eq(ITL+ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), item.getId().getLctNumero()));
    	criteria.add(Restrictions.eq(ITL+ScoItemLicitacao.Fields.NUMERO.toString(), item.getId().getNumero()));
//    	criteria.add(Restrictions.eq(ITL+ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), false));
//    	criteria.add(Restrictions.eq(FSL+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
    	if (!concatenarCodigo) {
    		criteria.setProjection(Projections.sqlProjection("COALESCE(MAT4_.nome, SRV5_.nome) AS nome", new String[] {"nome"}, new Type[]{StringType.INSTANCE}));
    	} else {
    		criteria.setProjection(Projections.sqlProjection("(CASE WHEN MAT4_.codigo IS NULL THEN SRV5_.codigo || '-' || SRV5_.nome ELSE MAT4_.codigo || '-' || MAT4_.nome END) AS nome", new String[] {"nome"}, new Type[]{StringType.INSTANCE}));
    	}
    	
    	String nomeMaterialServico = (String) executeCriteriaUniqueResult(criteria);
    	
    	return nomeMaterialServico;
	}
    
	public DominioTipoFaseSolicitacao obterTipoFaseSolicitacaoPorNumeroAF(final Integer numeroAf) {
		final DetachedCriteria criteria = this.criarDetachedCriteria();
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), numeroAf));
		criteria.setProjection(Projections.groupProperty(ScoFaseSolicitacao.Fields.TIPO.toString()));
		final List<DominioTipoFaseSolicitacao> result = executeCriteria(criteria, 0, 1, null, false);
		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	public BigDecimal obterValorReforco(final Integer numeroAf, final Short seqAlteracao) {
		final StringBuffer sql = new StringBuffer(1341);
		sql.append(" select SUM( case when FSC.TIPO = :tipoFase then (  COALESCE(IAFJ.QTDE_SOLICITADA, 0) -  (case when AFJN.SEQUENCIA_ALTERACAO = 0 then 0 else COALESCE(IAFJA.QTDE_SOLICITADA , 0) end) ) * COALESCE(IAFJ.VALOR_UNITARIO ,0) else (COALESCE(IAFJ.VALOR_UNITARIO, 0) - (case when AFJN.SEQUENCIA_ALTERACAO = 0 then 0 else COALESCE(IAFJA.VALOR_UNITARIO, 0) end) ) end ) as soma ");
		sql.append(" FROM agh.").append(ScoFaseSolicitacao.class.getAnnotation(Table.class).name()).append(" FSC LEFT JOIN agh.")
				.append(ScoItemAutorizacaoFornJn.class.getAnnotation(Table.class).name())
				.append(" IAFJ ON (FSC.IAF_AFN_NUMERO = IAFJ.AFN_NUMERO AND FSC.IAF_NUMERO = IAFJ.NUMERO) LEFT JOIN agh.")
				.append(ScoAutorizacaoFornJn.class.getAnnotation(Table.class).name()).append(" AFJN ON (IAFJ.AFN_NUMERO = AFJN.NUMERO) INNER JOIN agh.")
				.append(ScoItemAutorizacaoFornJn.class.getAnnotation(Table.class).name())
				.append(" IAFJA ON (IAFJA.AFN_NUMERO = AFJN.NUMERO AND IAFJA.NUMERO = IAFJ.NUMERO ) ");
		sql.append(
				" WHERE AFJN.NUMERO = :numeroAf AND AFJN.SEQUENCIA_ALTERACAO = :seqAlteracao AND IAFJ.IND_EXCLUSAO = :indExclusao AND IAFJ.SEQUENCIA_ALTERACAO = AFJN.SEQUENCIA_ALTERACAO AND IAFJA.SEQUENCIA_ALTERACAO <= AFJN.SEQUENCIA_ALTERACAO AND IAFJA.SEQUENCIA_ALTERACAO IN (SELECT MAX(SEQUENCIA_ALTERACAO) FROM agh.")
				.append(ScoItemAutorizacaoFornJn.class.getAnnotation(Table.class).name())
				.append(" WHERE AFN_NUMERO = IAFJ.AFN_NUMERO AND NUMERO = IAFJ.NUMERO AND ((SEQUENCIA_ALTERACAO < AFJN.SEQUENCIA_ALTERACAO) OR (SEQUENCIA_ALTERACAO = AFJN.SEQUENCIA_ALTERACAO AND AFJN.SEQUENCIA_ALTERACAO = 0)))");
		final SQLQuery query = createSQLQuery(sql.toString());
		query.setString("tipoFase", DominioTipoFaseSolicitacao.C.toString());
		query.setInteger("numeroAf", numeroAf);
		query.setShort("seqAlteracao", seqAlteracao);
		query.setString("indExclusao", "N");

		final Object lista = query.addScalar("soma", BigDecimalType.INSTANCE).uniqueResult();
		if (lista == null) {
			return BigDecimal.ZERO;
		}
		return (BigDecimal) lista;
	}

	public BigDecimal obterValorAF(final Integer numeroAf, final Short seqAlteracao) {
		final StringBuffer sql = new StringBuffer(1341);
		sql.append(" SELECT SUM(( case when FSC.TIPO = :tipoFase then ((IAFJN.QTDE_SOLICITADA - COALESCE(IAFJN.QTDE_RECEBIDA,0)) * IAFJN.VALOR_UNITARIO) + COALESCE(IAFJN.VALOR_EFETIVADO,0) else IAFJN.VALOR_UNITARIO end)) as soma ");
		sql.append(" FROM agh.").append(ScoFaseSolicitacao.class.getAnnotation(Table.class).name()).append(" FSC, agh.")
				.append(ScoItemAutorizacaoFornJn.class.getAnnotation(Table.class).name()).append(" IAFJN ");
		sql.append("  WHERE IAFJN.AFN_NUMERO = :numeroAf AND IAFJN.IND_SITUACAO <> :sitExclusao AND IAFJN.SEQUENCIA_ALTERACAO IN (SELECT MAX(SEQUENCIA_ALTERACAO) FROM agh.SCO_IAF_JN WHERE AFN_NUMERO = IAFJN.AFN_NUMERO AND NUMERO = IAFJN.NUMERO AND SEQUENCIA_ALTERACAO <= :seqAlteracao ) AND FSC.IAF_AFN_NUMERO = IAFJN.AFN_NUMERO AND FSC.IAF_NUMERO = IAFJN.NUMERO");
		final SQLQuery query = createSQLQuery(sql.toString());
		query.setString("tipoFase", DominioTipoFaseSolicitacao.C.toString());
		query.setInteger("numeroAf", numeroAf);
		query.setShort("seqAlteracao", seqAlteracao);
		query.setString("sitExclusao", DominioSituacaoAutorizacaoFornecedor.EX.toString());
		final Object lista = query.addScalar("soma", BigDecimalType.INSTANCE).uniqueResult();
		if (lista == null) {
			return BigDecimal.ZERO;
		}
		return (BigDecimal) lista;
	}
	
	public BigDecimal obterValorReforcoPorNumeroLicOuItemAF(Integer NumeroLicitacao, Integer numeroAF, Integer numeroItem) {
		StringBuilder hql = new StringBuilder(400);
		
		hql.append(" SELECT COALESCE(SUM(CASE WHEN FSC." + ScoFaseSolicitacao.Fields.TIPO.toString() + "= :TIPO_FASE THEN " +
		           "(((COALESCE(IAF." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_SOLICITADA.toString()  + ", 0) - " +
			       "   COALESCE(IAF." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_RECEBIDA.toString() + ", 0)) * " +
				   "   COALESCE(IAF." + ScoItemAutorizacaoForn.Fields.VALOR_UNITARIO.toString() + ",0)) + " +
			       "   COALESCE(IAF." + ScoItemAutorizacaoForn.Fields.VALOR_EFETIVADO.toString() + ",0)) ELSE " +
				   "   COALESCE(IAF." + ScoItemAutorizacaoForn.Fields.VALOR_UNITARIO.toString() + ", 0) END),0)" +
		           " FROM " + ScoFaseSolicitacao.class.getSimpleName() + " FSC, " + 
				              ScoItemAutorizacaoForn.class.getSimpleName() + " IAF, " +
							  ScoAutorizacaoForn.class.getSimpleName() + " AF " +
				   " WHERE FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString() + "= IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString() +
				   " AND  FSC." + ScoFaseSolicitacao.Fields.IAF_NUMERO.toString() + "= IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString() +
				   " AND  IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString() + " = AF." + ScoAutorizacaoForn.Fields.NUMERO.toString() + 
				   " AND  FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString() + " = :EXCLUSAO " +
				   " AND  IAF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString() + " <> :IND_SITUACAO");
		
		if (NumeroLicitacao != null) {
			hql.append( " AND  AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() + " = :NUMERO_LICITACAO");
		}
		
		if (numeroAF != null && numeroItem != null) {
			hql.append( " AND IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString() + " = :NUMERO_AF");
			hql.append( " AND IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString() + " = :NUMERO_ITEM_AF");
		}		
				
		javax.persistence.Query query = this.createQuery(hql.toString());		
		
		query.setParameter("TIPO_FASE", DominioTipoFaseSolicitacao.C);
		query.setParameter("EXCLUSAO",  false);
		query.setParameter("IND_SITUACAO", DominioSituacaoAutorizacaoFornecedor.EX);
		
		if (NumeroLicitacao != null) {
			query.setParameter("NUMERO_LICITACAO", NumeroLicitacao);
		}
		if (numeroAF != null && numeroItem != null) {
			query.setParameter("NUMERO_AF", numeroAF);
			query.setParameter("NUMERO_ITEM_AF", numeroItem);
		}			
		return new BigDecimal((Double) query.getSingleResult());
		
	}
	

	public GeracaoMovimentoEstoqueVO obterGeracaoMovimentoEstoque(Integer afnNumero, Integer numero, Integer matCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class);
		criteria.createAlias(ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
		criteria.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("MAT."+ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT");
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), afnNumero));		
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.IAF_NUMERO.toString(), numero));
		criteria.add(Restrictions.eq("MAT."+ScoMaterial.Fields.CODIGO.toString(), matCodigo));
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("GMT."+ScoGrupoMaterial.Fields.IND_GERA_MOV_ESTOQUE.toString()),GeracaoMovimentoEstoqueVO.Fields.IND_GERA_MOVIMENTO_ESTOQUE.toString())
				.add(Projections.property("MAT."+ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString()),GeracaoMovimentoEstoqueVO.Fields.UMD_CODIGO.toString())
				.add(Projections.property("MAT."+ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString()),GeracaoMovimentoEstoqueVO.Fields.ALM_SEQ.toString())
				.add(Projections.property("SLC."+ScoSolicitacaoDeCompra.Fields.UMD_CODIGO.toString()),GeracaoMovimentoEstoqueVO.Fields.UMD_SLC_CODIGO.toString())
				.add(Projections.property("SLC."+ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString()),GeracaoMovimentoEstoqueVO.Fields.DT_SOLICITACAO_COMPRA.toString());
		criteria.setProjection(projection);	
		criteria.setResultTransformer(Transformers.aliasToBean(GeracaoMovimentoEstoqueVO.class));
		return (GeracaoMovimentoEstoqueVO) executeCriteriaUniqueResult(criteria);		
	}

	/**
	 * Busca itens de SC em AF.
	 * 
	 * @param mat Material
	 * @return Saldo (base para cálculo)
	 */
	public List<BaseCalculoSaldoAfVO> buscarItensScEmAf(ScoMaterial mat) {
		assert mat != null : "Material não definido";
		return buscarItensScEmAf(mat.getCodigo());
	}

	/**
	 * Busca qtde saldo em SC que está em AF.
	 * 
	 * @param materialId ID do material
	 * @return Saldo (base para cálculo)
	 */
	public List<BaseCalculoSaldoAfVO> buscarItensScEmAf(Integer materialId) {
		DetachedCriteria criteria = getSaldoAfCriteria(materialId);
		criteria.add(Restrictions.eq(SLC + '.' + ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), materialId));
		projectSaldoByIaf(criteria);
		return executeCriteria(criteria);
	}
	
	/**
	 * Busca qtde saldo em Parcela de AF em contrato sem parcelas de entrega.
	 * 
	 * @param sc Solicitação de Compras
	 * @return Saldo (base para cálculo)
	 */
	public List<BaseCalculoSaldoAfVO> buscarSaldoEmAfSemParcelas(
			ScoSolicitacaoDeCompra sc) {
		assert sc != null : "SC não definida";
		return buscarSaldoEmAfSemParcelas(sc.getNumero());
	}
	
	/**
	 * Busca qtde saldo em Parcela de AF em contrato sem parcelas de entrega.
	 * 
	 * @param scId ID da SC
	 * @return Saldo (base para cálculo)
	 */
	public List<BaseCalculoSaldoAfVO> buscarSaldoEmAfSemParcelas(Integer scId) {
		assert scId != null : "ID da SC não definido";
		final String PEA = "pea";
		DetachedCriteria criteria = getSaldoAfCriteria(scId);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(PEA + '.' + 
						ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()), 
						BaseCalculoSaldoAfVO.Field.QTDE_SOLICITADA.toString())
				.add(Projections.property(PEA + '.' + 
						ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()), 
						BaseCalculoSaldoAfVO.Field.QTDE_RECEBIDA.toString())
				.add(Projections.property(IAF + '.' + 
						ScoItemAutorizacaoForn.Fields.FATOR_CONVERSAO.toString()), 
						BaseCalculoSaldoAfVO.Field.FATOR_CONVERSAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(BaseCalculoSaldoAfVO.class));
		criteria.createAlias(IAF + '.' + ScoItemAutorizacaoForn.Fields.PROG_ENTREGAS.toString(), PEA);		
		criteria.add(Restrictions.eq(SLC + '.' + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), scId));
		criteria.add(Restrictions.eq(PEA + '.' + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
		criteria.add(Restrictions.eq(PEA + '.' + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), true));
		return executeCriteria(criteria);
	}
	
	/**
	 * Obtem criteria para consulta de saldo com base nos itens de AF.
	 * 
	 * @param materialId ID do Material
	 * @return Criteria
	 */
	private DetachedCriteria getSaldoAfCriteria(Integer materialId) {		
		assert materialId != null : "ID do material não definido";
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, FSC);
		
		criteria.createAlias(FSC + '.' + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), SLC);
		criteria.createAlias(FSC + '.' + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), IAF);
		criteria.add(Restrictions.eq(SLC + '.' + ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), false));
		criteria.add(Restrictions.eq(FSC + '.' + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		
		criteria.add(Restrictions.in(IAF + '.'
				+ ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(),
				new DominioSituacaoAutorizacaoFornecedor[] {
						DominioSituacaoAutorizacaoFornecedor.AE,
						DominioSituacaoAutorizacaoFornecedor.PA }));
		
		return criteria;
	}
	
	/**
	 * Define projeção do saldo com base no item de AF.
	 * 
	 * @param criteria Criteria
	 */
	private void projectSaldoByIaf(DetachedCriteria criteria) {
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(IAF + '.' + 
						ScoItemAutorizacaoForn.Fields.QUANTIDADE_SOLICITADA.toString()), 
						BaseCalculoSaldoAfVO.Field.QTDE_SOLICITADA.toString())
				.add(Projections.property(IAF + '.' + 
						ScoItemAutorizacaoForn.Fields.QUANTIDADE_RECEBIDA.toString()), 
						BaseCalculoSaldoAfVO.Field.QTDE_RECEBIDA.toString())
				.add(Projections.property(IAF + '.' + 
						ScoItemAutorizacaoForn.Fields.FATOR_CONVERSAO.toString()), 
						BaseCalculoSaldoAfVO.Field.FATOR_CONVERSAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(BaseCalculoSaldoAfVO.class));
	}	
	
	public List<ScoFaseSolicitacao> pesquisarFasePorLicitacaoNumero(Integer lctNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.LCT_NUMERO.toString(), lctNumero));
		return executeCriteria(criteria);
	}
	
	public Boolean verificarSolicOutroPAC(Integer solicNumero, Integer lctNumero, DominioTipoSolicitacao tipoSolicitacao){
		Boolean emOutroPAC = false;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class);
		criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		criteria.add(Restrictions.ne(ScoFaseSolicitacao.Fields.LCT_NUMERO.toString(), lctNumero));
		if (DominioTipoSolicitacao.SC.equals(tipoSolicitacao)){
			criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), solicNumero));
		} else {
			criteria.add(Restrictions.eq(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString(), solicNumero));
		}
		
		emOutroPAC = executeCriteriaExists(criteria);
		
		return emOutroPAC;
	}
	
	public Boolean getScEmFases(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), numero));
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	public Boolean getSsEmFases(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.SLS_NUMERO.toString(), numero));
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	public List<ScoFaseSolicitacao> pesquisarFasePorItemLicitacao(Integer lctNumero, Short numeroItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SC");
		criteria.createAlias("SC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MT");
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.LCT_NUMERO.toString(), lctNumero));
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString(), numeroItem));		
		return executeCriteria(criteria);
	}
	
	public ScoFaseSolicitacao pesquisarFaseESolicitacaoPorItemLicitacao(Integer lctNumero, Short numeroItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SS", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.LCT_NUMERO.toString(), lctNumero));
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString(), numeroItem));		
		return (ScoFaseSolicitacao) executeCriteriaUniqueResult(criteria);
	}
	
}