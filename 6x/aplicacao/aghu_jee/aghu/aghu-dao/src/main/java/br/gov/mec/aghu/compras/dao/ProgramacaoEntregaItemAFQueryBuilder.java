package br.gov.mec.aghu.compras.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaItemAFVO;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ProgramacaoEntregaItemAFQueryBuilder extends QueryBuilder<DetachedCriteria> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2925970509610565360L;
	private Integer nroAF;
	private Integer numeroFornecedorPadrao;
	
	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		return criteria;
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.NUMERO.toString(), nroAF));
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AF");
		criteria.createAlias("AF." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSC1");
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("MAT." + ScoMaterial.Fields.ESTOQUE_GERAL.toString(), "EGR");
		criteria.createAlias("MAT." + ScoMaterial.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAL");
		criteria.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GRU");
		
		criteria.add(Restrictions.isNull("FSC1." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
		criteria.add(Restrictions.eq("FSC1." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		
		
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), numeroFornecedorPadrao));
		criteria.add(Restrictions.ge("EGR." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), obterPrimeiroDiaMes()));
		criteria.add(Restrictions.le("EGR." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), obterPrimeiroDiaMes()));
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), "nroAF");
		projectionList.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), "cp");
		projectionList.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.NUMERO.toString()), "numero");
		projectionList.add(Projections.property("FSC1." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()), "item");
		projectionList.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_SOLICITADA.toString()), "qAF");
		projectionList.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_RECEBIDA.toString()), "qRecebida");
		projectionList.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), "codigoMaterial");
		projectionList.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), "nomeMaterial");
		projectionList.add(Projections.property("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString()), "grupo");
		projectionList.add(Projections.property("GRU." + ScoGrupoMaterial.Fields.DESCRICAO.toString()), "descricaoGrupo");
		projectionList.add(Projections.property("MAT." + ScoMaterial.Fields.DESCRICAO.toString()), "descricaoMaterial");
		projectionList.add(Projections.property("MAT." + ScoMaterial.Fields.IND_ESTOCAVEL.toString()), "est");
		projectionList.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.QTDE_PONTO_PEDIDO.toString()), "pPedido");
		projectionList.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.IND_PONTO_PEDIDO_CALC.toString()), "ppCal");
		projectionList.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.TEMPO_REPOSICAO.toString()), "tempRep");
		projectionList.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString()), "qDisp");
		projectionList.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.QTDE_BLOQUEADA.toString()), "qBloq");
		projectionList.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.IND_CONTROLE_VALIDADE.toString()), "contrVal");
		projectionList.add(Projections.property("EGR." + SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString()), "classificacaoABC");
		projectionList.add(Projections.property("EGR." + SceEstoqueGeral.Fields.SUBCLASSIFICACAO_ABC.toString()), "subClassificacaoABC");
		projectionList.add(Projections.property("PFR." + ScoPropostaFornecedor.Fields.PRAZO_ENTREGA.toString()), "prazoEntg");
		projectionList.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.TEMPO_REPOSICAO.toString()), "tempRepOriginal");
		projectionList.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.QTDE_PONTO_PEDIDO.toString()), "pPedidoOriginal");
		projectionList.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.IND_PONTO_PEDIDO_CALC.toString()), "ppCalOriginal");
		projectionList.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.IND_CONTROLE_VALIDADE.toString()), "contrValOriginal");
		
		criteria.setProjection(projectionList);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProgramacaoEntregaItemAFVO.class));
	}

	private Date obterPrimeiroDiaMes() {
		return DateUtil.obterDataInicioCompetencia(new Date());
	}
	
	public Integer getNroAF() {
		return nroAF;
	}

	public void setNroAF(Integer nroAF) {
		this.nroAF = nroAF;
	}

	public Integer getNumeroFornecedorPadrao() {
		return numeroFornecedorPadrao;
	}

	public void setNumeroFornecedorPadrao(Integer numeroFornecedorPadrao) {
		this.numeroFornecedorPadrao = numeroFornecedorPadrao;
	}
}