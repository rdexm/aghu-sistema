package br.gov.mec.aghu.patrimonio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioIndResponsavel;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmAvaliacaoTecnica;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmTecnicoItemRecebimento;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.patrimonio.vo.AvaliacaoTecnicaVO;
import br.gov.mec.aghu.patrimonio.vo.DevolucaoBemPermanenteVO;
import br.gov.mec.aghu.patrimonio.vo.ImprimirFichaAceiteTecnicoBemPermanenteVO;
import br.gov.mec.aghu.patrimonio.vo.ItemRecebimentoVO;
import br.gov.mec.aghu.patrimonio.vo.NumeroPatrimonioAvaliacaoTecnicaVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class PtmAvaliacaoTecnicaDAO extends BaseDao<PtmAvaliacaoTecnica> {

	private static final String MOM_PONTO = "MOM.";

	private static final String MCM_PONTO = "MCM.";

	private static final String RPF_PONTO = "RPF.";

	private static final String CC_PONTO = "CC.";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2441541670635068927L;
	
	private static final String PIRP = "PIRP";
	private static final String PIRP_PONTO = "PIRP.";
	private static final String AVT_PONTO = "AVT.";
	private static final String ALIAS_AVT = "AVT";
	private static final String BPE_PONTO = "BPE.";
	private static final String RAP_PONTO = "RAP.";
	private static final String IRP_PONTO = "IRP.";
	private static final String FSC_PONTO = "FSC.";
	private static final String SIA_PONTO = "SIA.";
	private static final String NRP_PONTO = "NRP.";
	private static final String DFE_PONTO = "DFE.";
	private static final String FORN_PONTO = "FORN.";
	private static final String MAT_PONTO = "MAT.";
	private static final String RS_PONTO = "RS.";
	private static final String ATA_PONTO = "ATA.";
	private static final String PAT_PONTO = "PAT.";
	private static final String TIC_PONTO = "TIC.";
	private static final String BP_PONTO = "BP.";
	
	public List<AvaliacaoTecnicaVO> recuperarListaPaginadaAceiteTecnico(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, AvaliacaoTecnicaVO filtro, boolean vinculoCentroCusto, RapServidores servidor){
		RegistrarAceiteTecnicoQueryBuilder queryBuilder = new RegistrarAceiteTecnicoQueryBuilder();
		DetachedCriteria criteria = queryBuilder.carregarGridPrincipal(filtro, true, vinculoCentroCusto, servidor);
		criteria.addOrder(Order.asc(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(AvaliacaoTecnicaVO.class));
		return executeCriteria(criteria, firstResult, maxResults, null, asc);
	}
	
	public Long recuperarListaPaginadaAceiteTecnicoCount(AvaliacaoTecnicaVO filtro, boolean vinculoCentroCusto, RapServidores servidor){
		RegistrarAceiteTecnicoQueryBuilder queryBuilder = new RegistrarAceiteTecnicoQueryBuilder();
		DetachedCriteria criteria = queryBuilder.carregarGridPrincipal(filtro, false, vinculoCentroCusto, servidor);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * #43468 C1
	 * @param avtSeq
	 * @return
	 */
	public ImprimirFichaAceiteTecnicoBemPermanenteVO recuperarCamposFixosRelatorio(Integer avtSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAvaliacaoTecnica.class, "AVT");
		criteria.createAlias(AVT_PONTO+PtmAvaliacaoTecnica.Fields.ITEM_RECEB_PROVISORIO.toString(), "PIRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PIRP_PONTO+PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), "IRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(IRP_PONTO+SceItemRecebProvisorio.Fields.SCO_FASE_SOLICITACAO.toString(), "FSC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(IRP_PONTO+SceItemRecebProvisorio.Fields.SCO_ITEM_AUTORIZACAO_FORN.toString(), "SIA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PIRP_PONTO+PtmItemRecebProvisorios.Fields.SCE_NOTA_RECEB_PROVISORIOS.toString(), "NRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(NRP_PONTO+SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FSC_PONTO+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SIA_PONTO+ScoItemAutorizacaoForn.Fields.SCO_FORNECEDOR.toString(), "FORN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AVT_PONTO+PtmAvaliacaoTecnica.Fields.MARCA_COMERCIAL.toString(), "MCM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AVT_PONTO+PtmAvaliacaoTecnica.Fields.MARCA_MODELO.toString(), "MOM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AVT_PONTO+PtmAvaliacaoTecnica.Fields.SERVIDOR.toString(), "RAP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(RAP_PONTO+RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(RAP_PONTO+RapServidores.Fields.OCUPACAO_CARGO.toString(), "OCA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PIRP_PONTO+PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), "ATA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATA."+PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), "FCC", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(FSC_PONTO+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq(AVT_PONTO+PtmAvaliacaoTecnica.Fields.SEQ.toString(), avtSeq));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(PIRP_PONTO+PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.RECEB.toString())
				.add(Projections.property(PIRP_PONTO+PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.ITEM.toString())
				.add(Projections.property(IRP_PONTO+SceItemRecebProvisorio.Fields.ESL_SEQ.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.ESL.toString())
				.add(Projections.property(SIA_PONTO+ScoItemAutorizacaoForn.Fields.IPF_PFR_LCT_NUMERO.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.NRO_AF.toString())
				.add(Projections.property(SIA_PONTO+ScoItemAutorizacaoForn.Fields.NUMERO.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.COMPLEMENTO_AF.toString())
				.add(Projections.property(DFE_PONTO+SceDocumentoFiscalEntrada.Fields.NUMERO.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.NOTA_FISCAL.toString())
				.add(Projections.property(FORN_PONTO+ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.RAZAO_SOCIAL.toString())
				.add(Projections.property(FORN_PONTO+ScoFornecedor.Fields.CPF.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.CPF.toString())
				.add(Projections.property(FORN_PONTO+ScoFornecedor.Fields.CGC.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.CGC.toString())
				.add(Projections.property(MAT_PONTO+ScoMaterial.Fields.CODIGO.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.MAT_CODIGO.toString())
				.add(Projections.property(MAT_PONTO+ScoMaterial.Fields.DESCRICAO.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.MAT_DESCRICAO.toString())
				.add(Projections.property(MCM_PONTO+ScoMarcaComercial.Fields.DESCRICAO.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.MARCA.toString())
				.add(Projections.property(MOM_PONTO+ScoMarcaModelo.Fields.DESCRICAO.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.MODELO.toString())
				.add(Projections.property("PES."+RapPessoasFisicas.Fields.NOME.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.NOME_TECNICO.toString())
				.add(Projections.property(AVT_PONTO+PtmAvaliacaoTecnica.Fields.MATRICULA_SERVIDOR.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.MATRICULA.toString())
				.add(Projections.property("OCA."+RapOcupacaoCargo.Fields.DESCRICAO.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.CARGO.toString())
				.add(Projections.property("ATA."+PtmAreaTecAvaliacao.Fields.COD_CENTRO_CUSTOS.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.CC_AREA_TEC.toString())
				.add(Projections.property("FCC."+FccCentroCustos.Fields.DESCRICAO.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.NOME_CC_AREA_TEC.toString())
				.add(Projections.property(AVT_PONTO+PtmAvaliacaoTecnica.Fields.IND_STATUS.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.IND_STATUS.toString())
				.add(Projections.property(AVT_PONTO+PtmAvaliacaoTecnica.Fields.JUSTIFICATIVA.toString()), ImprimirFichaAceiteTecnicoBemPermanenteVO.Fields.JUSTIFICATIVA.toString())
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ImprimirFichaAceiteTecnicoBemPermanenteVO.class));
		
		return (ImprimirFichaAceiteTecnicoBemPermanenteVO) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #43468 C2
	 * @param avtSeq
	 * @return
	 */
	public List <PtmBemPermanentes> recuperarCamposVariaveisRelatorio(Integer avtSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAvaliacaoTecnica.class, "AVT");
		criteria.createAlias(AVT_PONTO+PtmAvaliacaoTecnica.Fields.BEM_PERMANENTES.toString(), "BPE", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AVT_PONTO+PtmAvaliacaoTecnica.Fields.SEQ.toString(), avtSeq));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(BPE_PONTO+PtmBemPermanentes.Fields.NR_BEM.toString()), PtmBemPermanentes.Fields.NR_BEM.toString())
				.add(Projections.property(BPE_PONTO+PtmBemPermanentes.Fields.NR_SERIE.toString()), PtmBemPermanentes.Fields.NR_SERIE.toString())
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(PtmBemPermanentes.class));
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Consulta para preencher fieldset "Análises Técnicas"
	 * @param itemRecebimento
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return List<AvaliacaoTecnicaVO>
	 */
	public List<AvaliacaoTecnicaVO> carregarAnaliseTecnico(ItemRecebimentoVO itemRecebimento, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = montarAnaliseTecnico(itemRecebimento);
		
		if (StringUtils.isEmpty(orderProperty)) {
			criteria.addOrder(Order.desc(PAT_PONTO + PtmAvaliacaoTecnica.Fields.DATA_CRIACAO.toString()));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(AvaliacaoTecnicaVO.class));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * 
	 * @param itemRecebimento
	 * @return Long
	 */
	public Long carregarAnaliseTecnicoCount(ItemRecebimentoVO itemRecebimento) {
		DetachedCriteria criteria = montarAnaliseTecnico(itemRecebimento);
		
		return executeCriteriaCount(criteria);
	}
	
	
	private DetachedCriteria montarAnaliseTecnico(ItemRecebimentoVO itemRecebimento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAvaliacaoTecnica.class, "PAT");
		
		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.SEQ.toString()), AvaliacaoTecnicaVO.Fields.SEQ_AVALIACAO_TEC.toString());
		pList.add(Projections.property(BP_PONTO + PtmBemPermanentes.Fields.AVT_SEQ.toString()), AvaliacaoTecnicaVO.Fields.AVT_SEQ.toString());
		pList.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString()), AvaliacaoTecnicaVO.Fields.SCE_NRP_SEQ.toString());
		pList.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString()), AvaliacaoTecnicaVO.Fields.SCE_NRP_ITEM.toString());		
		pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.MCM_COD.toString()), AvaliacaoTecnicaVO.Fields.MCM_COD_AVALIACAO_TEC.toString());
		pList.add(Projections.property(MCM_PONTO + ScoMarcaComercial.Fields.DESCRICAO.toString()), AvaliacaoTecnicaVO.Fields.MARCA_COMERCIAL_DESCRICAO.toString());
		pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.MOM_COD.toString()), AvaliacaoTecnicaVO.Fields.MOM_COD_AVALIACAO_TEC.toString());
		pList.add(Projections.property(MOM_PONTO + ScoMarcaModelo.Fields.DESCRICAO.toString()), AvaliacaoTecnicaVO.Fields.MARCA_MODELO_DESCRICAO.toString());
		pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.JUSTIFICATIVA.toString()), AvaliacaoTecnicaVO.Fields.JUSTIFICATIVA_AVALIACAO_TEC.toString());
		pList.add(Projections.property(TIC_PONTO + PtmTecnicoItemRecebimento.Fields.SER_MATRICULA.toString()), AvaliacaoTecnicaVO.Fields.MATRICULA_SERVIDOR.toString());
		pList.add(Projections.property(TIC_PONTO + PtmTecnicoItemRecebimento.Fields.SER_VIN_CODIGO.toString()), AvaliacaoTecnicaVO.Fields.VIN_CODIGO_SERVIDOR.toString());
		pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.DATA_INICIO_GARANTIA.toString()), AvaliacaoTecnicaVO.Fields.DATA_INICIO_AVALIACAO_TEC.toString());
		pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.IND_STATUS.toString()), AvaliacaoTecnicaVO.Fields.IND_STATUS_AVALIACAO_TEC.toString());
		pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.IND_SITUACAO.toString()), AvaliacaoTecnicaVO.Fields.IND_SITUACAO_AVALIACAO_TEC.toString());
		pList.add(Projections.property(ATA_PONTO + PtmAreaTecAvaliacao.Fields.COD_CENTRO_CUSTOS.toString()), AvaliacaoTecnicaVO.Fields.CODIGO_CENTRO_CUSTO.toString());
		pList.add(Projections.property(CC_PONTO + FccCentroCustos.Fields.DESCRICAO.toString()), AvaliacaoTecnicaVO.Fields.DESCRICAO_CENTRO_CUSTO.toString());
		pList.add(Projections.property(RPF_PONTO + RapPessoasFisicas.Fields.NOME.toString()), AvaliacaoTecnicaVO.Fields.NOME_PESSOA_FISICA.toString());
		pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.VID_AUTIL.toString()), AvaliacaoTecnicaVO.Fields.VIDA_UTIL.toString());
		pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.TEMPO_GARANTIA.toString()), AvaliacaoTecnicaVO.Fields.TEMPO_GARANTIA.toString());
		pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.DATA_CRIACAO.toString()), AvaliacaoTecnicaVO.Fields.DATA_CRIACAO_AVALIACAO_TEC.toString());
		pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.DESCRICAO_MATERIAL.toString()), AvaliacaoTecnicaVO.Fields.DESCRICAO_MATERIAL.toString());		
		criteria.setProjection(Projections.distinct(pList));
		
		criteria.createAlias(PAT_PONTO + PtmAvaliacaoTecnica.Fields.BEM_PERMANENTES.toString(), "BP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PAT_PONTO + PtmAvaliacaoTecnica.Fields.ITEM_RECEB_PROVISORIO.toString(), PIRP, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.TECNICO_ITEM_RECEBIMENTO.toString(), "TIC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), "ATA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ATA_PONTO + PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), "CC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(TIC_PONTO + PtmTecnicoItemRecebimento.Fields.SERVIDOR_TECNICO.toString(), "RS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(RS_PONTO + RapServidores.Fields.PESSOA_FISICA.toString(), "RPF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PAT_PONTO + PtmAvaliacaoTecnica.Fields.MARCA_COMERCIAL.toString(), "MCM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PAT_PONTO + PtmAvaliacaoTecnica.Fields.MARCA_MODELO.toString(), "MOM", JoinType.LEFT_OUTER_JOIN);
	
		criteria.add(Restrictions.eq(TIC_PONTO + PtmTecnicoItemRecebimento.Fields.IND_RESPONSAVEL.toString(), DominioIndResponsavel.R));
		criteria.add(Restrictions.eq(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SEQ.toString(), itemRecebimento.getIrpSeq()));
			
		return criteria;
	}
	
	
	
	public List<Long> numeroPatrimonioAvaliacaoTecnica(ItemRecebimentoVO itemRecebimento, AvaliacaoTecnicaVO avaliacaoTecnica) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAvaliacaoTecnica.class, "PAT");
		
		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.property(BP_PONTO + PtmBemPermanentes.Fields.NR_BEM.toString()), NumeroPatrimonioAvaliacaoTecnicaVO.Fields.NUMERO_BEM.toString());				
		criteria.setProjection(Projections.distinct(pList));		
		
		criteria.createAlias(PAT_PONTO + PtmAvaliacaoTecnica.Fields.ITEM_RECEB_PROVISORIO.toString(), PIRP, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PAT_PONTO + PtmAvaliacaoTecnica.Fields.BEM_PERMANENTES.toString(), "BP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.TECNICO_ITEM_RECEBIMENTO.toString(), "TIC", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO_NRP_SEQ.toString(), itemRecebimento.getRecebimento()));
		criteria.add(Restrictions.eq(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO_NRO_ITEM.toString(), itemRecebimento.getItemRecebimento()));
		criteria.add(Restrictions.eq(BP_PONTO + PtmBemPermanentes.Fields.AVT_SEQ.toString(), avaliacaoTecnica.getAvtSeq()));		
		criteria.add(Restrictions.isNotNull(BP_PONTO + PtmBemPermanentes.Fields.NR_BEM.toString()));
			
		return executeCriteria(criteria);
	}
	
	public List<DevolucaoBemPermanenteVO> obterPatrimonioPorAvaliacao(Integer recebimento, Integer itemRecebimento, Integer avtSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAvaliacaoTecnica.class, ALIAS_AVT);		
		criteria.createAlias(AVT_PONTO+PtmAvaliacaoTecnica.Fields.BEM_PERMANENTES.toString(), "BP", JoinType.LEFT_OUTER_JOIN);
		//criteria.createAlias(AVT_PONTO+PtmAvaliacaoTecnica.Fields.ITEM_RECEB_PROVISORIO.toString(), "PIRP", JoinType.LEFT_OUTER_JOIN);
		//criteria.createAlias(PIRP_PONTO+PtmItemRecebProvisorios.Fields.TECNICO_ITEM_RECEBIMENTO.toString(), "TIR", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(BP_PONTO+PtmBemPermanentes.Fields.NR_BEM.toString()),
						DevolucaoBemPermanenteVO.Fields.PBP_NUMERO_BEM.toString())
				);
		
		//criteria.add(Restrictions.eq(PIRP_PONTO+PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), recebimento));
		//criteria.add(Restrictions.eq(PIRP_PONTO+PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), itemRecebimento));
		criteria.add(Restrictions.eq(BP_PONTO+PtmBemPermanentes.Fields.AVT_SEQ.toString(), avtSeq));
		criteria.add(Restrictions.isNotNull(BP_PONTO+PtmBemPermanentes.Fields.NR_BEM.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(DevolucaoBemPermanenteVO.class));
		return executeCriteria(criteria);
	}
}