package br.gov.mec.aghu.compras.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;

import br.gov.mec.aghu.compras.vo.PesquisarPlanjProgrEntregaItensAfFiltroVO;
import br.gov.mec.aghu.compras.vo.PesquisarPlanjProgrEntregaItensAfVO;
import br.gov.mec.aghu.dominio.DominioAfEmpenhada;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ProgrEntregaItensAfComPrevisaoQueryBulder extends
		QueryBuilder<DetachedCriteria> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7854348123684261723L;
	private PesquisarPlanjProgrEntregaItensAfFiltroVO filtro;
	private Integer fornecedorPadrao;
	private Boolean exists;
	private Boolean isOracle;
	private boolean isCount = false;
	
	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AFN");
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR", JoinType.INNER_JOIN);
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.LICITACAO.toString(), "LCT", JoinType.INNER_JOIN);
		criteria.createAlias("LCT." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MDL", JoinType.LEFT_OUTER_JOIN);

		return criteria;
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {

		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.IND_ENTREGA_PROGRAMADA.toString(), true));

		DetachedCriteria criteriaAFAssinada = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class, "AFN_JN");
		criteriaAFAssinada.add(Restrictions.eqProperty("AFN_JN." + ScoAutorizacaoFornJn.Fields.NUMERO.toString(), "AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString()));
		criteriaAFAssinada.add(Restrictions.isNotNull("AFN_JN." + ScoAutorizacaoFornJn.Fields.MATRICULA_ASSINA_COORD.toString()));
		criteriaAFAssinada.add(Restrictions.isNotNull("AFN_JN." + ScoAutorizacaoFornJn.Fields.DT_ASSINATURA_COORD.toString()));
		criteriaAFAssinada.setProjection(Projections.projectionList().add(Projections.property("AFN_JN." + ScoAutorizacaoFornJn.Fields.NUMERO.toString())));
		criteria.add(Subqueries.propertyIn("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), criteriaAFAssinada));

		DetachedCriteria subQureryA = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		subQureryA.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), false));
		subQureryA.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
		if(Boolean.TRUE.equals(isOracle)) {
			subQureryA.add(Restrictions.sqlRestriction("DT_PREV_ENTREGA -1 < ?", DateUtil.truncaData(filtro.getDataPrevisaoEntrega()),DateType.INSTANCE));
		}
		else {
			subQureryA.add(Restrictions.sqlRestriction("DT_PREV_ENTREGA - INTERVAL '1 DAY' < ?", DateUtil.truncaData(filtro.getDataPrevisaoEntrega()), DateType.INSTANCE));
		}
		
		subQureryA.setProjection(Projections.projectionList().add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString())));
		criteria.add(Subqueries.propertyIn("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subQureryA));

		this.restricoesFiltros1(criteria);
		this.restricoesFiltros2(criteria);
		this.restricoesFiltros3(criteria);
		this.restricoesFiltros4(criteria);
		
		if(!isCount){
			criteria.addOrder(Order.asc("PFR." + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString()));
			criteria.addOrder(Order.asc("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()));
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.NUMERO_AF.toString())
					.add(Projections.property("PFR." + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.NUMERO_LICITACAO.toString())
					.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.COMPLEMENTO.toString())
					.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.SITUACAO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.SITUACAO.toString())
					.add(Projections.property("MDL." + ScoModalidadeLicitacao.Fields.CODIGO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.MODALIDADE_COMPRA.toString())
					.add(Projections.property("MDL." + ScoModalidadeLicitacao.Fields.DESCRICAO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.DESCRICAO_MODALIDADE_COMPRA.toString())
					.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.MODALIDADE_EMPENHO.toString())
					.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.VENCIMENTO_CONTRATO.toString())
					.add(Projections.property("FRN." + ScoFornecedor.Fields.NUMERO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.NRO_FORNECEDOR.toString())
					.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.NOME_FORNECEDOR.toString())
					.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.PFR_LCT_NUMERO.toString())
					);
			criteria.setResultTransformer(Transformers.aliasToBean(PesquisarPlanjProgrEntregaItensAfVO.class));
		}

	}

	private void restricoesFiltros1(DetachedCriteria criteria) {
		if(filtro.getNumeroAF() == null) {
			DetachedCriteria subQureryLct = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
			subQureryLct.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.IND_ANALISE_PROGR_PLANEJ.toString(), true));
			subQureryLct.add(Restrictions.eqProperty("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString(), "AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString()));
			subQureryLct.setProjection(Projections.projectionList().add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString())));
			criteria.add(Subqueries.propertyIn("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subQureryLct));

			DetachedCriteria subQureryLctA = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
			subQureryLctA.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), false));
			subQureryLctA.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
			subQureryLctA.add(Restrictions.eqProperty("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), "AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString()));
			subQureryLctA.setProjection(Projections.projectionList().add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString())));
			criteria.add(Subqueries.propertyIn("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subQureryLctA));
			
			
		}
		else {
			DetachedCriteria criteriaLct = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AFN_LCT");
			criteriaLct.add(Restrictions.eq("AFN_LCT." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtro.getNumeroAF()));
			if(filtro.getComplemento() != null) {
				criteriaLct.add(Restrictions.eq("AFN_LCT." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getComplemento()));
			}
			criteriaLct.setProjection(Projections.projectionList().add(Projections.property("AFN_LCT." + ScoAutorizacaoForn.Fields.NUMERO.toString())));
			criteria.add(Subqueries.propertyIn("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), criteriaLct));
			
			if(exists) {
				DetachedCriteria subQureryLctA = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
				subQureryLctA.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), false));
				subQureryLctA.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
				subQureryLctA.add(Restrictions.eqProperty("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), "AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString()));
				subQureryLctA.setProjection(Projections.projectionList().add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString())));
				criteria.add(Subqueries.propertyIn("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subQureryLctA));
			}
		}
		
		if(filtro.getEfetivada() != null) {
			if(filtro.getEfetivada().isSim()) {
				criteria.add(Restrictions.in("AFN." + ScoAutorizacaoForn.Fields.SITUACAO.toString(), new DominioSituacaoAutorizacaoFornecimento[] {DominioSituacaoAutorizacaoFornecimento.EF, DominioSituacaoAutorizacaoFornecimento.EP}));
			}
			else {
				criteria.add(Restrictions.in("AFN." + ScoAutorizacaoForn.Fields.SITUACAO.toString(), new DominioSituacaoAutorizacaoFornecimento[] {DominioSituacaoAutorizacaoFornecimento.AE, DominioSituacaoAutorizacaoFornecimento.PA}));
			}
		}

		if(filtro.getEstocavel() != null || filtro.getGrupoMaterial() != null) {
			DetachedCriteria subQureryGrupoMaterial = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
			subQureryGrupoMaterial.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
			subQureryGrupoMaterial.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
			subQureryGrupoMaterial.add(Restrictions.eqProperty("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), "AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString()));
			
			if(filtro.getGrupoMaterial() != null) {
				subQureryGrupoMaterial.add(Restrictions.eq("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), filtro.getGrupoMaterial().getCodigo()));
			}
			if(filtro.getEstocavel() != null) {
				subQureryGrupoMaterial.add(Restrictions.eq("MAT." + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), filtro.getEstocavel().isSim()));
			}
			
			subQureryGrupoMaterial.setProjection(Projections.projectionList().add(Projections.property("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())));
			criteria.add(Subqueries.propertyIn("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subQureryGrupoMaterial));
		}
	}
	
	private void restricoesFiltros2(DetachedCriteria criteria) {
		if(filtro.getMaterial() != null) {
			DetachedCriteria subQureryMaterial = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
			subQureryMaterial.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
			subQureryMaterial.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
			subQureryMaterial.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), filtro.getMaterial().getCodigo()));
			subQureryMaterial.setProjection(Projections.projectionList().add(Projections.property("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())));
			criteria.add(Subqueries.propertyIn("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subQureryMaterial));
		}
		
		if(filtro.getCurvaABC() != null) {
			DetachedCriteria subQureryCurva = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
			subQureryCurva.createAlias("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF", JoinType.INNER_JOIN);
			subQureryCurva.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC", JoinType.INNER_JOIN);
			subQureryCurva.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
			subQureryCurva.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
			subQureryCurva.createAlias("MAT." + ScoMaterial.Fields.ESTOQUE_GERAL.toString(), "EGR", JoinType.INNER_JOIN);
			subQureryCurva.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.FRN_NUMERO.toString(), fornecedorPadrao));
			subQureryCurva.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), DateUtil.truncaData(DateUtil.obterDataInicioCompetencia(new Date()))));
			subQureryCurva.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString(), filtro.getCurvaABC()));
			subQureryCurva.setProjection(Projections.projectionList().add(Projections.property("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())));
			criteria.add(Subqueries.propertyIn("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subQureryCurva));
		}
		
		if(filtro.getServico() != null) {
			DetachedCriteria subQureryServico = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
			if(!filtro.getServico().isSim()) {
				subQureryServico.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.TIPO.toString(), DominioTipoFaseSolicitacao.C));
			}
			else {
				subQureryServico.add(Restrictions.ne("FSC." + ScoFaseSolicitacao.Fields.TIPO.toString(), DominioTipoFaseSolicitacao.C));
			}
			subQureryServico.setProjection(Projections.projectionList().add(Projections.property("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())));
			criteria.add(Subqueries.propertyIn("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subQureryServico));
		}

		if(filtro.getFornecedorAF() != null) {
			criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), filtro.getFornecedorAF().getNumeroFornecedor()));
		}
	}
	
	private void restricoesFiltros3(DetachedCriteria criteria) {
		if(filtro.getModalidadeCompra() != null) {
			criteria.add(Restrictions.eq("LCT." + ScoLicitacao.Fields.MODALIDADE_LICITACAO_CODIGO.toString(), filtro.getModalidadeCompra().getCodigo()));
		}

		if(filtro.getDataInicioPrevisaoEntrega() != null || filtro.getDataFimPrevisaoEntrega() != null) {
			DetachedCriteria subQureryPrevisaoEntrega = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
			if(filtro.getDataInicioPrevisaoEntrega() != null && filtro.getDataFimPrevisaoEntrega() == null) {
				subQureryPrevisaoEntrega.add(Restrictions.ge("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), DateUtil.truncaData(filtro.getDataInicioPrevisaoEntrega())));
			}
			else if(filtro.getDataInicioPrevisaoEntrega() == null && filtro.getDataFimPrevisaoEntrega() != null) {
				subQureryPrevisaoEntrega.add(Restrictions.le("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), DateUtil.truncaData(filtro.getDataFimPrevisaoEntrega())));
			}
			else {
				subQureryPrevisaoEntrega.add(Restrictions.between("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), DateUtil.truncaData(filtro.getDataInicioPrevisaoEntrega()), DateUtil.truncaData(DateUtil.adicionaDias(filtro.getDataFimPrevisaoEntrega(), 1))));
			}
			subQureryPrevisaoEntrega.setProjection(Projections.projectionList().add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString())));
			criteria.add(Subqueries.propertyIn("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subQureryPrevisaoEntrega));
		}
		
		if(filtro.getProgAutomatica() != null) {
			DetachedCriteria subQureryProgAutomatica = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF_I");
			subQureryProgAutomatica.add(Restrictions.eqProperty("IAF_I." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString(), "AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString()));
			subQureryProgAutomatica.add(Restrictions.eq("IAF_I." + ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString(), filtro.getProgAutomatica().isSim()));
			subQureryProgAutomatica.setProjection(Projections.projectionList().add(Projections.property("IAF_I." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString())));
			criteria.add(Subqueries.propertyIn("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subQureryProgAutomatica));

		}
		
	}
	
	private void restricoesFiltros4(DetachedCriteria criteria) {
		if(filtro.getAssinada() != null || filtro.getPlanejada() != null || filtro.getEmpenhada() != null) {
			DetachedCriteria subQureryPrevisaoEntrega = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA_1");
			if(filtro.getAssinada() != null) {
				subQureryPrevisaoEntrega.add(Restrictions.eq("PEA_1." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), filtro.getAssinada().isSim()));
			}
			if(filtro.getPlanejada() != null) {
				subQureryPrevisaoEntrega.add(Restrictions.eq("PEA_1." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), filtro.getPlanejada().isSim()));
			}
			if(filtro.getEmpenhada() != null) {
				subQureryPrevisaoEntrega.add(Restrictions.eq("PEA_1." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), filtro.getEmpenhada().isSim() ? DominioAfEmpenhada.S : DominioAfEmpenhada.N));
			}
			subQureryPrevisaoEntrega.setProjection(Projections.projectionList().add(Projections.property("PEA_1." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString())));
			criteria.add(Subqueries.propertyIn("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subQureryPrevisaoEntrega));
		}
		
		if(filtro.getModalidadeEmpenho() != null) {
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString(), filtro.getModalidadeEmpenho()));
		}

		if(filtro.getCentroCustoAplicacao() != null || filtro.getCentroCustoSolicitante() != null) {
			DetachedCriteria subQureryCentroCusto = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
			subQureryCentroCusto.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.LEFT_OUTER_JOIN);
			subQureryCentroCusto.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.LEFT_OUTER_JOIN);

			subQureryCentroCusto.add(Restrictions.isNotNull("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			subQureryCentroCusto.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
			if(filtro.getCentroCustoSolicitante() != null) {
				subQureryCentroCusto.add(Restrictions.or(Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.CCT_CODIGO.toString(), filtro.getCentroCustoSolicitante().getCodigo()), 
						Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.CCT_CODIGO.toString(), filtro.getCentroCustoSolicitante().getCodigo())));
			}
			if(filtro.getCentroCustoAplicacao() != null) {
				subQureryCentroCusto.add(Restrictions.or(Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.CCT_CODIGO_APLICADA.toString(), filtro.getCentroCustoAplicacao().getCodigo()), 
						Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.CC_APLICADA_CODIGO.toString(), filtro.getCentroCustoAplicacao().getCodigo())));
			}
			subQureryCentroCusto.setProjection(Projections.projectionList().add(Projections.property("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())));
			criteria.add(Subqueries.propertyIn("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subQureryCentroCusto));
		}
	}
	
	public DetachedCriteria build(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro, Integer fornecedorPadrao, Boolean exists, Boolean isOracle, boolean count) {
		
		this.filtro = filtro;
		this.fornecedorPadrao = fornecedorPadrao;
		this.exists = exists;
		this.isOracle = isOracle;
		this.isCount = count;
		return super.build();
	}

}
