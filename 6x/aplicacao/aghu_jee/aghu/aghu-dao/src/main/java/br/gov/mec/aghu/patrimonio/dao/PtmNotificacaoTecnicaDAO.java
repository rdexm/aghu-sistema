package br.gov.mec.aghu.patrimonio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmNotificacaoTecnica;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceItemEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.patrimonio.vo.NotificacaoTecnicaItemRecebimentoProvisorioVO;
import br.gov.mec.aghu.patrimonio.vo.RelatorioRecebimentoProvisorioVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dialect.OrderBySql;

public class PtmNotificacaoTecnicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PtmNotificacaoTecnica> {
	
	private static final long serialVersionUID = -318544108891213608L;
	
	private final String ALIAS_AFN_PONTO = "AFN.";
	private final String ALIAS_ATA_PONTO = "ATA.";
	private final String ALIAS_CCT_PONTO = "CCT.";
	private final String ALIAS_DFE_PONTO = "DFE.";
	private final String ALIAS_ESL_PONTO = "ESL.";
	private final String ALIAS_FRN_PONTO = "FRN.";
	private final String ALIAS_FSC_PONTO = "FSC.";
	private final String ALIAS_IRP_PONTO = "IRP.";
	private final String ALIAS_ISL_PONTO = "ISL.";
	private final String ALIAS_MAT_PONTO = "MAT.";
	private final String ALIAS_NRP_PONTO = "NRP.";
	private final String ALIAS_PES_PONTO = "PES.";
	private final String ALIAS_PIRP_PONTO = "PIRP.";
	private final String ALIAS_PTN_PONTO = "PTN.";
	private final String ALIAS_SER_PONTO = "SER.";
	private final String ALIAS_SLC_PONTO = "SLC.";

	public List<PtmNotificacaoTecnica> pesquisarNotificacoesTecnica(Long irpSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmNotificacaoTecnica.class);
		List<PtmNotificacaoTecnica> lista = null;
		criteria.add(Restrictions.eq(PtmNotificacaoTecnica.Fields.IRP_SEQ.toString(), irpSeq));
		criteria.addOrder(Order.asc(PtmNotificacaoTecnica.Fields.DATA.toString()));
		lista = executeCriteria(criteria);
		return lista;	
	}

	public NotificacaoTecnicaItemRecebimentoProvisorioVO obterNotificacaoTecnicaItemRecebProvisorio(Long pntSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmNotificacaoTecnica.class,"PNT");
		criteria.createAlias("PNT." + PtmNotificacaoTecnica.Fields.PTM_ITEM_RECEBIMENTO_PROVISORIO.toString(), "PRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PRP." + PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), "IRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.SCO_FASE_SOLICITACAO.toString(), "FSC", JoinType.LEFT_OUTER_JOIN, 
				Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "SIA", JoinType.LEFT_OUTER_JOIN, 
				Restrictions.eqProperty("IRP." + SceItemRecebProvisorio.Fields.PEA_IAF_AFN_NUMERO.toString(), 
										"SIA." + ScoItemAutorizacaoForn.Fields.AFN_NUMERO.toString()));
		criteria.createAlias("SIA." + ScoItemAutorizacaoForn.Fields.SCO_FORNECEDOR.toString(), "FOR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SIA." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PRP." + PtmItemRecebProvisorios.Fields.SCE_NOTA_RECEB_PROVISORIOS.toString(), "NRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("NRP." + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PNT." + PtmNotificacaoTecnica.Fields.RAP_SERVIDORES.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "RPF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PRP." + PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), "ATA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATA." + PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), "FCC", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("PNT." + PtmNotificacaoTecnica.Fields.SEQ.toString(), pntSeq));
		criteria.addOrder(Order.asc("PNT." + PtmNotificacaoTecnica.Fields.SEQ.toString()));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.RECEBIMENTO.toString())
				.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.NRO_ITEM.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.ITEM_RECEBIMENTO.toString())
				.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.ESL_SEQ.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.ESL.toString())
				.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.QUANTIDADE.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.QUANTIDADE.toString())
				.add(Projections.property("SIA." + ScoItemAutorizacaoForn.Fields.IPF_PFR_LCT_NUMERO.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.AF.toString())
				.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.COMPLEMENTO.toString())
				.add(Projections.property("FOR." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.FORNECEDOR.toString())
				.add(Projections.property("FOR." + ScoFornecedor.Fields.CPF.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.CPF.toString())
				.add(Projections.property("FOR." + ScoFornecedor.Fields.CGC.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.CGC.toString())
				.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.NOTA_FISCAL.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.CODIGO_MATERIAL.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.DESCRICAO_MATERIAL.toString())
				.add(Projections.property("PNT." + PtmNotificacaoTecnica.Fields.DATA.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.DT_NOTIFICACAO.toString())
				.add(Projections.property("PNT." + PtmNotificacaoTecnica.Fields.STATUS.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.STATUS.toString())
				.add(Projections.property("PNT." + PtmNotificacaoTecnica.Fields.SER_MATRICULA.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.NATRICULA_TEC.toString())
				.add(Projections.property("ATA." + PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.OFICINA.toString())
				.add(Projections.property("FCC." + FccCentroCustos.Fields.DESCRICAO.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.NOME_CENTRO_CUSTO.toString())
				.add(Projections.property("FCC." + FccCentroCustos.Fields.CODIGO.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.CODIGO_CENTRO_CUSTO.toString())						
				.add(Projections.property("PRP." + PtmItemRecebProvisorios.Fields.QUANTIDADE_DISP.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.QUANTIDADE_DISPONIVEL.toString())
				.add(Projections.property("PNT." + PtmNotificacaoTecnica.Fields.DESCRICAO.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.DESCRICAO_NOTIFICACAO.toString())
				.add(Projections.property("RPF." + RapPessoasFisicas.Fields.NOME.toString()), 
						NotificacaoTecnicaItemRecebimentoProvisorioVO.Fields.TECNICO_RESPONSAVEL.toString()));		
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacaoTecnicaItemRecebimentoProvisorioVO.class));		
		return (NotificacaoTecnicaItemRecebimentoProvisorioVO) executeCriteriaUniqueResult(criteria);		
	}
	
	
	/**
	 * SB2 44713 Suggestion para o campo Notificação Técnica.
	 * @param param - SEQ / Descrição
	 * @return Lista de PtmNotificacaoTecnica
	 */
	public List<PtmNotificacaoTecnica> listarSugestionNotificacaoTecnica(Object param) {
		DetachedCriteria criteria = listarSugestionNotificacaoTecnicaCriteria(param);
		if(isOracle()){
		criteria.addOrder(OrderBySql.sql(" dbms_lob.substr(this_.DESCRICAO,100,1)"));
		}
		return executeCriteria(criteria, 0, 100, null, true);
	}

	private DetachedCriteria listarSugestionNotificacaoTecnicaCriteria(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmNotificacaoTecnica.class);
		if (param != null && StringUtils.isNotBlank(param.toString())) {
			if(CoreUtil.isNumeroInteger(param.toString())) {
				criteria.add(Restrictions.eq(PtmNotificacaoTecnica.Fields.SEQ.toString(),Long.valueOf(param.toString())));
				if(!executeCriteriaExists(criteria)){
					criteria = DetachedCriteria.forClass(PtmNotificacaoTecnica.class);
					criteria.add(Restrictions.ilike(PtmNotificacaoTecnica.Fields.DESCRICAO.toString(),StringUtils.lowerCase(param.toString()),MatchMode.ANYWHERE));
				}
			} else {
				criteria.add(Restrictions.ilike(PtmNotificacaoTecnica.Fields.DESCRICAO.toString(),StringUtils.lowerCase(param.toString()),MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
	public Long listarSugestionNotificacaoTecnicaCount(Object param){
		DetachedCriteria criteria = listarSugestionNotificacaoTecnicaCriteria(param);
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Consulta irp_seq de uma notificação técnica
	 * C11 - Consulta irp_seq de uma notificação técnica
	 */
	
	public Long obterIrpSeqNotificacoesTecnica(Long seqNotTecnica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmNotificacaoTecnica.class, "NT");
		
		criteria.createAlias("NT." + PtmNotificacaoTecnica.Fields.PTM_ITEM_RECEBIMENTO_PROVISORIO.toString(), "PIRP", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList p = Projections.projectionList();
			p.add(Projections.property("PIRP."+ PtmItemRecebProvisorios.Fields.SEQ.toString()).as(PtmItemRecebProvisorios.Fields.SEQ.toString()));
		criteria.setProjection(p);
		
		criteria.add(Restrictions.eq("NT."+ PtmNotificacaoTecnica.Fields.SEQ.toString(), seqNotTecnica));
		
		return  (Long) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * 48204 - C1 - Recebimento com AF
	 * @param recebimento
	 * @param itemRecebimento
	 * @return List<RelatorioRecebimentoProvisorioVO>
	 */
	public List<RelatorioRecebimentoProvisorioVO> obterRecebimentosComAF(Integer recebimento, Integer itemRecebimento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmNotificacaoTecnica.class, "PTN");
		
		criteria.createAlias(ALIAS_PTN_PONTO + PtmNotificacaoTecnica.Fields.PTM_ITEM_RECEBIMENTO_PROVISORIO.toString(), "PIRP", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), "IRP", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_IRP_PONTO + SceItemRecebProvisorio.Fields.SCO_FASE_SOLICITACAO.toString(), "FSC", JoinType.INNER_JOIN, Restrictions.eq(ALIAS_FSC_PONTO + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.createAlias(ALIAS_PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NOTA_RECEB_PROVISORIOS.toString(), "NRP", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_IRP_PONTO + SceItemRecebProvisorio.Fields.SCO_AUTORIZACAO_FORN.toString(), "AFN", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_AFN_PONTO + ScoAutorizacaoForn.Fields.SCO_FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_NRP_PONTO + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_FSC_PONTO + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_SLC_PONTO + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_PTN_PONTO + PtmNotificacaoTecnica.Fields.RAP_SERVIDORES.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_SER_PONTO + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_PIRP_PONTO + PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), "ATA", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_ATA_PONTO + PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), "CCT", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(ALIAS_PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), recebimento));
		criteria.add(Restrictions.eq(ALIAS_PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), itemRecebimento));
		
		criteria.addOrder(Order.asc(ALIAS_PTN_PONTO + PtmNotificacaoTecnica.Fields.SEQ.toString()));
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property(ALIAS_IRP_PONTO + SceItemRecebProvisorio.Fields.NRP_SEQ.toString()), RelatorioRecebimentoProvisorioVO.Fields.NRP_SEQ.toString());
		projectionList.add(Projections.property(ALIAS_IRP_PONTO + SceItemRecebProvisorio.Fields.NRO_ITEM.toString()), RelatorioRecebimentoProvisorioVO.Fields.NRO_ITEM.toString());
		projectionList.add(Projections.property(ALIAS_IRP_PONTO + SceItemRecebProvisorio.Fields.ESL_SEQ.toString()), RelatorioRecebimentoProvisorioVO.Fields.ESL_SEQ.toString());
		projectionList.add(Projections.property(ALIAS_AFN_PONTO + ScoAutorizacaoForn.Fields.PFRLCFNUMERO.toString()), RelatorioRecebimentoProvisorioVO.Fields.PFR_LCT_NUMERO.toString());
		projectionList.add(Projections.property(ALIAS_AFN_PONTO + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), RelatorioRecebimentoProvisorioVO.Fields.NRO_COMPLEMENTO.toString());
		projectionList.add(Projections.property(ALIAS_IRP_PONTO + SceItemRecebProvisorio.Fields.QUANTIDADE.toString()), RelatorioRecebimentoProvisorioVO.Fields.IRP_QUANTIDADE.toString());
		projectionList.add(Projections.property(ALIAS_PIRP_PONTO + PtmItemRecebProvisorios.Fields.QUANTIDADE_DISP.toString()), RelatorioRecebimentoProvisorioVO.Fields.PIRP_QUANTIDADE_DISP.toString());
		projectionList.add(Projections.property(ALIAS_FRN_PONTO + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), RelatorioRecebimentoProvisorioVO.Fields.RAZAO_SOCIAL.toString());
		projectionList.add(Projections.property(ALIAS_FRN_PONTO + ScoFornecedor.Fields.CGC.toString()), RelatorioRecebimentoProvisorioVO.Fields.CGC.toString());
		projectionList.add(Projections.property(ALIAS_FRN_PONTO + ScoFornecedor.Fields.CPF.toString()), RelatorioRecebimentoProvisorioVO.Fields.CPF.toString());
		projectionList.add(Projections.property(ALIAS_DFE_PONTO + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()), RelatorioRecebimentoProvisorioVO.Fields.DFE_NUMERO.toString());
		projectionList.add(Projections.property(ALIAS_MAT_PONTO + ScoMaterial.Fields.CODIGO.toString()), RelatorioRecebimentoProvisorioVO.Fields.MAT_CODIGO.toString());
		projectionList.add(Projections.property(ALIAS_MAT_PONTO + ScoMaterial.Fields.NOME.toString()), RelatorioRecebimentoProvisorioVO.Fields.MAT_NOME.toString());
		projectionList.add(Projections.property(ALIAS_ATA_PONTO + PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString()), RelatorioRecebimentoProvisorioVO.Fields.NOME_AREA_TEC_AVALIACAO.toString());
		projectionList.add(Projections.property(ALIAS_CCT_PONTO + FccCentroCustos.Fields.DESCRICAO.toString()), RelatorioRecebimentoProvisorioVO.Fields.CCT_DESCRICAO.toString());
		projectionList.add(Projections.property(ALIAS_ATA_PONTO + PtmAreaTecAvaliacao.Fields.COD_CENTRO_CUSTOS.toString()), RelatorioRecebimentoProvisorioVO.Fields.COD_CENTRO_CUSTO.toString());
		projectionList.add(Projections.property(ALIAS_PTN_PONTO + PtmNotificacaoTecnica.Fields.DATA.toString()), RelatorioRecebimentoProvisorioVO.Fields.PTN_DATA.toString());
		projectionList.add(Projections.property(ALIAS_PTN_PONTO + PtmNotificacaoTecnica.Fields.STATUS.toString()), RelatorioRecebimentoProvisorioVO.Fields.PTN_STATUS.toString());		
		projectionList.add(Projections.property(ALIAS_PES_PONTO + RapPessoasFisicas.Fields.NOME.toString()), RelatorioRecebimentoProvisorioVO.Fields.PES_NOME.toString());
		projectionList.add(Projections.property(ALIAS_PTN_PONTO + PtmNotificacaoTecnica.Fields.SER_MATRICULA.toString()), RelatorioRecebimentoProvisorioVO.Fields.PTN_SER_MATRICULA.toString());
		projectionList.add(Projections.property(ALIAS_PTN_PONTO + PtmNotificacaoTecnica.Fields.DESCRICAO.toString()), RelatorioRecebimentoProvisorioVO.Fields.PTN_DESCRICAO.toString());
		criteria.setProjection(projectionList);
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioRecebimentoProvisorioVO.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * 48204 - C1 - Recebimento ESL
	 * @param recebimento
	 * @param itemRecebimento
	 * @return List<RelatorioRecebimentoProvisorioVO>
	 */
	public List<RelatorioRecebimentoProvisorioVO> obterRecebimentosESL(Integer recebimento, Integer itemRecebimento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmNotificacaoTecnica.class, "PTN");
		
		criteria.createAlias(ALIAS_PTN_PONTO + PtmNotificacaoTecnica.Fields.PTM_ITEM_RECEBIMENTO_PROVISORIO.toString(), "PIRP", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), "IRP", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NOTA_RECEB_PROVISORIOS.toString(), "NRP", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_IRP_PONTO + SceItemRecebProvisorio.Fields.SCE_ENTR_SAID_SEM_LICITACAO.toString(), "ESL", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_IRP_PONTO + SceItemRecebProvisorio.Fields.ITEM_ENTR_SAID_SEM_LICITACAO.toString(), "ISL", JoinType.INNER_JOIN, Restrictions.eqProperty(ALIAS_ISL_PONTO + SceItemEntrSaidSemLicitacao.Fields.ESL_SEQ.toString(), ALIAS_IRP_PONTO + SceItemRecebProvisorio.Fields.ESL_SEQ.toString()));
		criteria.createAlias(ALIAS_NRP_PONTO + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_ESL_PONTO + SceEntrSaidSemLicitacao.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN); 
		criteria.createAlias(ALIAS_ISL_PONTO + SceItemEntrSaidSemLicitacao.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_PTN_PONTO + PtmNotificacaoTecnica.Fields.RAP_SERVIDORES.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_SER_PONTO + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_PIRP_PONTO + PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), "ATA", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_ATA_PONTO + PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), "CCT", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(ALIAS_PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), recebimento));
		criteria.add(Restrictions.eq(ALIAS_PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), itemRecebimento));
		
		criteria.addOrder(Order.asc(ALIAS_PTN_PONTO + PtmNotificacaoTecnica.Fields.SEQ.toString()));
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property(ALIAS_IRP_PONTO + SceItemRecebProvisorio.Fields.NRP_SEQ.toString()), RelatorioRecebimentoProvisorioVO.Fields.NRP_SEQ.toString());
		projectionList.add(Projections.property(ALIAS_IRP_PONTO + SceItemRecebProvisorio.Fields.NRO_ITEM.toString()), RelatorioRecebimentoProvisorioVO.Fields.NRO_ITEM.toString());
		projectionList.add(Projections.property(ALIAS_IRP_PONTO + SceItemRecebProvisorio.Fields.ESL_SEQ.toString()), RelatorioRecebimentoProvisorioVO.Fields.ESL_SEQ.toString());
		projectionList.add(Projections.property(ALIAS_IRP_PONTO + SceItemRecebProvisorio.Fields.QUANTIDADE.toString()), RelatorioRecebimentoProvisorioVO.Fields.IRP_QUANTIDADE.toString());
		projectionList.add(Projections.property(ALIAS_PIRP_PONTO + PtmItemRecebProvisorios.Fields.QUANTIDADE_DISP.toString()), RelatorioRecebimentoProvisorioVO.Fields.PIRP_QUANTIDADE_DISP.toString());
		projectionList.add(Projections.property(ALIAS_FRN_PONTO + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), RelatorioRecebimentoProvisorioVO.Fields.RAZAO_SOCIAL.toString());
		projectionList.add(Projections.property(ALIAS_FRN_PONTO + ScoFornecedor.Fields.CGC.toString()), RelatorioRecebimentoProvisorioVO.Fields.CGC.toString());
		projectionList.add(Projections.property(ALIAS_FRN_PONTO + ScoFornecedor.Fields.CPF.toString()), RelatorioRecebimentoProvisorioVO.Fields.CPF.toString());
		projectionList.add(Projections.property(ALIAS_DFE_PONTO + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()), RelatorioRecebimentoProvisorioVO.Fields.DFE_NUMERO.toString());
		projectionList.add(Projections.property(ALIAS_MAT_PONTO + ScoMaterial.Fields.CODIGO.toString()), RelatorioRecebimentoProvisorioVO.Fields.MAT_CODIGO.toString());
		projectionList.add(Projections.property(ALIAS_MAT_PONTO + ScoMaterial.Fields.NOME.toString()), RelatorioRecebimentoProvisorioVO.Fields.MAT_NOME.toString());
		projectionList.add(Projections.property(ALIAS_ATA_PONTO + PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString()), RelatorioRecebimentoProvisorioVO.Fields.NOME_AREA_TEC_AVALIACAO.toString());
		projectionList.add(Projections.property(ALIAS_CCT_PONTO + FccCentroCustos.Fields.DESCRICAO.toString()), RelatorioRecebimentoProvisorioVO.Fields.CCT_DESCRICAO.toString());
		projectionList.add(Projections.property(ALIAS_ATA_PONTO + PtmAreaTecAvaliacao.Fields.COD_CENTRO_CUSTOS.toString()), RelatorioRecebimentoProvisorioVO.Fields.COD_CENTRO_CUSTO.toString());
		projectionList.add(Projections.property(ALIAS_PTN_PONTO + PtmNotificacaoTecnica.Fields.DATA.toString()), RelatorioRecebimentoProvisorioVO.Fields.PTN_DATA.toString());
		projectionList.add(Projections.property(ALIAS_PTN_PONTO + PtmNotificacaoTecnica.Fields.STATUS.toString()), RelatorioRecebimentoProvisorioVO.Fields.PTN_STATUS.toString());
		projectionList.add(Projections.property(ALIAS_PES_PONTO + RapPessoasFisicas.Fields.NOME.toString()), RelatorioRecebimentoProvisorioVO.Fields.PES_NOME.toString());
		projectionList.add(Projections.property(ALIAS_PTN_PONTO + PtmNotificacaoTecnica.Fields.SER_MATRICULA.toString()), RelatorioRecebimentoProvisorioVO.Fields.PTN_SER_MATRICULA.toString());
		projectionList.add(Projections.property(ALIAS_PTN_PONTO + PtmNotificacaoTecnica.Fields.DESCRICAO.toString()), RelatorioRecebimentoProvisorioVO.Fields.PTN_DESCRICAO.toString());
		criteria.setProjection(projectionList);
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioRecebimentoProvisorioVO.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Consulta para preencher fieldset “Notificações Técnicas”
	 * @param irpSeq
	 * @return List<PtmNotificacaoTecnica>
	 */
	public List<PtmNotificacaoTecnica> obterqNotificacoesTecnica(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Long irpSeq) {
		DetachedCriteria criteria = montarqNotificacoesTecnica(irpSeq);
		
		if (StringUtils.isEmpty(orderProperty)) {
			criteria.addOrder(Order.desc("NT." + PtmNotificacaoTecnica.Fields.DATA.toString()));
		}
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Count da consulta para preencher fieldset “Notificações Técnicas”
	 * @param irpSeq
	 * @return Long
	 */
	public Long obterqNotificacoesTecnicaCount(Long irpSeq) {
		DetachedCriteria criteria = montarqNotificacoesTecnica(irpSeq);
		
		return executeCriteriaCount(criteria);
	}
	
	
	/**
	 * Consulta para preencher fieldset “Notificações Técnicas”
	 * @param irpSeq
	 * @return
	 */
	public DetachedCriteria montarqNotificacoesTecnica(Long irpSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmNotificacaoTecnica.class, "NT");
		
		criteria.add(Restrictions.eq("NT."+ PtmNotificacaoTecnica.Fields.IRP_SEQ.toString(), irpSeq));
		return criteria;
	}	
}
