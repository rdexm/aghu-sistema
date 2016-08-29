package br.gov.mec.aghu.patrimonio.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioIndResponsavel;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmAvaliacaoTecnica;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmTecnicoItemRecebimento;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceEntradaSaidaSemLicitacao;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.patrimonio.vo.AvaliacaoTecnicaVO;

public class RegistrarAceiteTecnicoQueryBuilder implements Serializable  {

	private static final String PIRP = "PIRP";
	private static final String NULL_AS_PROJECTION = "null as ";
	private static final String RS_PONTO = "RS.";
	private static final String ESL_PONTO = "ESL.";
	private static final String AFN_PONTO = "AFN.";
	private static final String SIA_PONTO = "SIA.";
	private static final String FSC_PONTO = "FSC.";
	private static final String IAF_PONTO = "IAF.";
	private static final String MAT_PONTO = "MAT.";
	private static final String IRP_PONTO = "IRP.";
	private static final String IRP = "IRP";
	private static final String BP_PONTO = "BP.";
	private static final String ATA_PONTO = "ATA.";
	private static final String PIRP_PONTO = "PIRP.";
	private static final String PAT_PONTO = "PAT.";
	private static final String TIC_PONTO = "TIC.";
	
	private static final long serialVersionUID = -5716419877427345616L;
	
//	@Inject
//	private FccCentroCustosDAO fccCentroCustosDAO;
	
	public DetachedCriteria carregarGridPrincipal(AvaliacaoTecnicaVO filtro, boolean projecao, boolean vinculo, RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAvaliacaoTecnica.class, "PAT");
		
			if(projecao){
				criteria.setProjection(Projections.distinct(projecaoGridPrincipal()));
			}
		
			criteria.createAlias(PAT_PONTO+ PtmAvaliacaoTecnica.Fields.BEM_PERMANENTES.toString(), "BP", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(PAT_PONTO+ PtmAvaliacaoTecnica.Fields.ITEM_RECEB_PROVISORIO.toString(), PIRP, JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(PIRP_PONTO+ PtmItemRecebProvisorios.Fields.TECNICO_ITEM_RECEBIMENTO.toString(), "TIC", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(PIRP_PONTO+ PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), "ATA", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(ATA_PONTO+ PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), "CC", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(PAT_PONTO + PtmAvaliacaoTecnica.Fields.MARCA_COMERCIAL.toString(), "MCM", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(PAT_PONTO + PtmAvaliacaoTecnica.Fields.MARCA_MODELO.toString(), "MOM", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), IRP, JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(TIC_PONTO + PtmTecnicoItemRecebimento.Fields.SERVIDOR.toString(), "RS", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(RS_PONTO+ RapServidores.Fields.PESSOA_FISICA.toString(), "RPF", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.eq(TIC_PONTO + PtmTecnicoItemRecebimento.Fields.IND_RESPONSAVEL.toString(), DominioIndResponsavel.R));
			criteria.add(Restrictions.eq(PIRP_PONTO + PtmItemRecebProvisorios.Fields.STATUS.toString(), 3));
			
			if (filtro != null) {
				
				if(vinculo){
//					restricaoCentroCusto(filtro, criteria, servidor);
					if (filtro.getCentroCusto() != null && filtro.getCentroCusto().getCodigo() != null){
						criteria.add(Restrictions.eq(ATA_PONTO + PtmAreaTecAvaliacao.Fields.COD_CENTRO_CUSTOS.toString(), filtro.getCentroCusto().getCodigo()));
					}
				}else if (filtro.getCentroCusto() != null && filtro.getCentroCusto().getCodigo() != null) {
					criteria.add(Restrictions.eq(ATA_PONTO + PtmAreaTecAvaliacao.Fields.COD_CENTRO_CUSTOS.toString(), filtro.getCentroCusto().getCodigo()));
				}
				
				if (filtro.getItemRecebimento() != null) {
					if (filtro.getItemRecebimento().getNrpSeq() != null) {
						criteria.add(Restrictions.eq(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), 
								filtro.getItemRecebimento().getNrpSeq()));
					}
					if (filtro.getItemRecebimento().getNroItem() != null) {
						criteria.add(Restrictions.eq(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), 
								filtro.getItemRecebimento().getNroItem()));
					}
				}
				
				if (filtro.getPatrimonio() != null && filtro.getPatrimonio().getNumeroBem() != null) {
					criteria.add(Restrictions.eq(BP_PONTO + PtmBemPermanentes.Fields.NR_BEM.toString(), filtro.getPatrimonio().getNumeroBem()));
				}
				 
				if (filtro.getMarcaComercial() != null && filtro.getMarcaComercial().getCodigo() != null) {
					criteria.add(Restrictions.eq(PAT_PONTO + PtmAvaliacaoTecnica.Fields.MCM_COD.toString(), filtro.getMarcaComercial().getCodigo()));
				}
				
				restricoesGridPrincipal(filtro, criteria);
			}
			
		return criteria;
	}		
	
	private ProjectionList projecaoGridPrincipal() {
		ProjectionList pList = Projections.projectionList();
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.SEQ.toString()), AvaliacaoTecnicaVO.Fields.SEQ_AVALIACAO_TEC.toString());
			pList.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString()), AvaliacaoTecnicaVO.Fields.SCE_NRP_SEQ.toString());
			pList.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString()), AvaliacaoTecnicaVO.Fields.SCE_NRP_ITEM.toString());
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.MCM_COD.toString()), AvaliacaoTecnicaVO.Fields.MCM_COD_AVALIACAO_TEC.toString());
			pList.add(Projections.property("MCM." + ScoMarcaComercial.Fields.DESCRICAO.toString()), AvaliacaoTecnicaVO.Fields.MARCA_COMERCIAL_DESCRICAO.toString());
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.MOM_COD.toString()), AvaliacaoTecnicaVO.Fields.MOM_COD_AVALIACAO_TEC.toString());
			pList.add(Projections.property("MOM." + ScoMarcaModelo.Fields.DESCRICAO.toString()), AvaliacaoTecnicaVO.Fields.MARCA_MODELO_DESCRICAO.toString());
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.JUSTIFICATIVA.toString()), AvaliacaoTecnicaVO.Fields.JUSTIFICATIVA_AVALIACAO_TEC.toString());
			pList.add(Projections.property(TIC_PONTO + PtmTecnicoItemRecebimento.Fields.SER_MATRICULA.toString()), AvaliacaoTecnicaVO.Fields.MATRICULA_SERVIDOR.toString());
			pList.add(Projections.property(TIC_PONTO + PtmTecnicoItemRecebimento.Fields.SER_VIN_CODIGO.toString()), AvaliacaoTecnicaVO.Fields.VIN_CODIGO_SERVIDOR.toString());
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.DATA_INICIO_GARANTIA.toString()), AvaliacaoTecnicaVO.Fields.DATA_INICIO_AVALIACAO_TEC.toString());
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.IND_STATUS.toString()), AvaliacaoTecnicaVO.Fields.IND_STATUS_AVALIACAO_TEC.toString());
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.IND_SITUACAO.toString()), AvaliacaoTecnicaVO.Fields.IND_SITUACAO_AVALIACAO_TEC.toString());
			pList.add(Projections.property(ATA_PONTO + PtmAreaTecAvaliacao.Fields.COD_CENTRO_CUSTOS.toString()), AvaliacaoTecnicaVO.Fields.CODIGO_CENTRO_CUSTO.toString());
			pList.add(Projections.property( "CC." + FccCentroCustos.Fields.DESCRICAO.toString()), AvaliacaoTecnicaVO.Fields.DESCRICAO_CENTRO_CUSTO.toString());
			pList.add(Projections.property("RPF." + RapPessoasFisicas.Fields.NOME.toString()), AvaliacaoTecnicaVO.Fields.NOME_PESSOA_FISICA.toString());
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.VID_AUTIL.toString()), AvaliacaoTecnicaVO.Fields.VIDA_UTIL.toString());
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.TEMPO_GARANTIA.toString()), AvaliacaoTecnicaVO.Fields.TEMPO_GARANTIA.toString());
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.DATA_CRIACAO.toString()), AvaliacaoTecnicaVO.Fields.DATA_CRIACAO_AVALIACAO_TEC.toString());
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.DESCRICAO_MATERIAL.toString()), AvaliacaoTecnicaVO.Fields.DESCRICAO_MATERIAL.toString());
		return pList;
	}

//	private void restricaoCentroCusto(AvaliacaoTecnicaVO filtro, DetachedCriteria criteria, RapServidores servidor){
//		List<FccCentroCustos> listaCentroCusto = new ArrayList<FccCentroCustos>();
//		List<FccCentroCustos> listaCentroCustoRetorno = new ArrayList<FccCentroCustos>();
//		List<FccCentroCustos> listaCentroCustoTemp = new ArrayList<FccCentroCustos>();
//			listaCentroCusto = this.fccCentroCustosDAO.centroCustoPorServidorLogado(servidor);
//			
//			listaCentroCustoRetorno.addAll(listaCentroCusto);
//			do {
//				listaCentroCustoTemp = this.fccCentroCustosDAO.obterCentroCustoPorCentroCustoInferior(listaCentroCusto);
//				
//				if(listaCentroCustoTemp != null){
//					listaCentroCusto.clear();
//					listaCentroCusto.addAll(listaCentroCustoTemp);
//					listaCentroCustoTemp = null;
//				}
//				if(listaCentroCusto != null && !listaCentroCusto.isEmpty()){
//					listaCentroCustoRetorno.addAll(listaCentroCusto);
//				}
//			} while (listaCentroCustoTemp == null);
//			criteria.add(Restrictions.in(ATA_PONTO + PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), listaCentroCustoRetorno));
//	}
	
	private void restricoesGridPrincipal(AvaliacaoTecnicaVO filtro,
			DetachedCriteria criteria) {
		
		if (filtro.getMarcaModelo() != null && filtro.getMarcaModelo().getId() != null && filtro.getMarcaModelo().getId().getSeqp() != null) {
			criteria.add(Restrictions.eq(PAT_PONTO + PtmAvaliacaoTecnica.Fields.MOM_COD.toString(), filtro.getMarcaModelo().getId().getSeqp()));
		}
		
		if (filtro.getResponsavelTecnico() != null && filtro.getResponsavelTecnico().getId() != null) {
			if (filtro.getResponsavelTecnico().getId().getMatricula() != null) {
				criteria.add(Restrictions.eq(TIC_PONTO + PtmTecnicoItemRecebimento.Fields.SER_MATRICULA.toString(), 
						filtro.getResponsavelTecnico().getId().getMatricula()));
			}
			if (filtro.getResponsavelTecnico().getId().getVinCodigo() != null) {
				criteria.add(Restrictions.eq(TIC_PONTO + PtmTecnicoItemRecebimento.Fields.SER_VIN_CODIGO.toString(), 
						filtro.getResponsavelTecnico().getId().getVinCodigo()));
			}
		}
		
		if (filtro.getStatus() != null) {
			criteria.add(Restrictions.eq(PAT_PONTO + PtmAvaliacaoTecnica.Fields.IND_STATUS.toString(), filtro.getStatus()));
		}
		
		if (filtro.getSituacao() != null) {
			criteria.add(Restrictions.eq(PAT_PONTO + PtmAvaliacaoTecnica.Fields.IND_SITUACAO.toString(), filtro.getSituacao()));
		}
		
		if (filtro.getDtInicio() != null && filtro.getDtFim() != null) {
			criteria.add(Restrictions.between(PAT_PONTO + PtmAvaliacaoTecnica.Fields.DATA_CRIACAO.toString(), filtro.getDtInicio(), filtro.getDtFim()));
		}else if(filtro.getDtInicio() != null && filtro.getDtFim() == null) {
			criteria.add(Restrictions.between(PAT_PONTO + PtmAvaliacaoTecnica.Fields.DATA_CRIACAO.toString(), filtro.getDtInicio(), new Date()));
		}else if(filtro.getDtInicio() == null && filtro.getDtFim() != null) {
			criteria.add(Restrictions.le(PAT_PONTO + PtmAvaliacaoTecnica.Fields.DATA_CRIACAO.toString(), filtro.getDtFim()));
		}
	}
	
	public DetachedCriteria carregarCampoDaTelaParte1(PtmItemRecebProvisorios ptmItemRecebProvisorios) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, "IRP");
			
		ProjectionList pList = Projections.projectionList();
			pList.add(Projections.property(IRP_PONTO + SceItemRecebProvisorio.Fields.NRP_SEQ.toString()), AvaliacaoTecnicaVO.Fields.IRP_NRP_SEQ.toString());
			pList.add(Projections.property(IRP_PONTO + SceItemRecebProvisorio.Fields.ESL_SEQ.toString()), AvaliacaoTecnicaVO.Fields.IRP_ESL_SEQ.toString());
			pList.add(Projections.property(IRP_PONTO + SceItemRecebProvisorio.Fields.VALOR.toString()), AvaliacaoTecnicaVO.Fields.IRP_VALOR.toString());
			pList.add(Projections.property(IRP_PONTO + SceItemRecebProvisorio.Fields.NRO_ITEM.toString()), AvaliacaoTecnicaVO.Fields.IRP_NRO_ITEM.toString());
			pList.add(Projections.property(MAT_PONTO + ScoMaterial.Fields.DESCRICAO.toString()), AvaliacaoTecnicaVO.Fields.MAT_DESCRICAO.toString());
			pList.add(Projections.property(IAF_PONTO + ScoItemAutorizacaoForn.Fields.MARCA_COMERCIAL_CODIGO.toString()), AvaliacaoTecnicaVO.Fields.IAF_MCM_CODIGO.toString());
			pList.add(Projections.property(IAF_PONTO + ScoItemAutorizacaoForn.Fields.MOM_SEQP.toString()), AvaliacaoTecnicaVO.Fields.IAF_MOM_SEQP.toString());
			pList.add(Projections.property(IAF_PONTO + ScoItemAutorizacaoForn.Fields.MOM_MCM_CODIGO.toString()), AvaliacaoTecnicaVO.Fields.IAF_MOM_MCM_CODIGO.toString());
			
			criteria.setProjection(pList);
			criteria.setResultTransformer(Transformers.aliasToBean(AvaliacaoTecnicaVO.class));
		
			criteria.createAlias(IRP_PONTO+SceItemRecebProvisorio.Fields.SCO_FASE_SOLICITACAO.toString(), "FSC", JoinType.INNER_JOIN);
			criteria.createAlias(FSC_PONTO+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
			criteria.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
			criteria.createAlias(IRP_PONTO+SceItemRecebProvisorio.Fields.SCO_ITEM_AUTORIZACAO_FORN.toString(), "SIA", JoinType.INNER_JOIN);
			criteria.createAlias(SIA_PONTO+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.INNER_JOIN);
			criteria.createAlias(IRP_PONTO+SceItemRecebProvisorio.Fields.PTM_ITENS_RECEB_PROVISORIOS.toString(), PIRP, JoinType.INNER_JOIN);
			criteria.createAlias(IRP_PONTO+SceItemRecebProvisorio.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF", JoinType.INNER_JOIN);
			criteria.createAlias(IAF_PONTO+ScoItemAutorizacaoForn.Fields.MARCA_COMERCIAL.toString(), "MCM", JoinType.INNER_JOIN);
			
			criteria.add(Restrictions.eq(FSC_PONTO + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			criteria.add(Restrictions.eqProperty(SIA_PONTO + ScoItemAutorizacaoForn.Fields.AFN_NUMERO.toString(), AFN_PONTO + ScoAutorizacaoForn.Fields.NUMERO.toString()));
			criteria.add(Restrictions.eqProperty(IAF_PONTO + ScoItemAutorizacaoForn.Fields.AFN_NUMERO.toString(), AFN_PONTO + ScoAutorizacaoForn.Fields.NUMERO.toString()));
			
			if (ptmItemRecebProvisorios != null) {
				
				if (ptmItemRecebProvisorios.getNrpSeq() != null) {
					criteria.add(Restrictions.eq(IRP_PONTO + SceItemRecebProvisorio.Fields.NRP_SEQ.toString(), 
							ptmItemRecebProvisorios.getNrpSeq()));
				}
				if (ptmItemRecebProvisorios.getNroItem() != null) {
					criteria.add(Restrictions.eq(IRP_PONTO + SceItemRecebProvisorio.Fields.NRO_ITEM.toString(), 
							ptmItemRecebProvisorios.getNroItem()));
				}
			}
		return criteria;
	}
	
	public DetachedCriteria carregarCampoDaTelaParte2(PtmItemRecebProvisorios ptmItemRecebProvisorios) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, IRP);
			criteria.createAlias(IRP_PONTO+SceItemRecebProvisorio.Fields.ENTRADA_SAIDA_SEM_LICITACAO.toString(), "ESL", JoinType.INNER_JOIN);
			criteria.createAlias(ESL_PONTO+SceEntradaSaidaSemLicitacao.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		
		ProjectionList pList = Projections.projectionList();
			pList.add(Projections.property(IRP_PONTO + SceItemRecebProvisorio.Fields.NRP_SEQ.toString()), AvaliacaoTecnicaVO.Fields.IRP_NRP_SEQ.toString());
			pList.add(Projections.property(ESL_PONTO + SceItemRecebProvisorio.Fields.ESL_SEQ.toString()), AvaliacaoTecnicaVO.Fields.IRP_ESL_SEQ.toString());
			pList.add(Projections.sqlProjection(NULL_AS_PROJECTION + SceItemRecebProvisorio.Fields.VALOR.toString(), 
				new String[]{SceItemRecebProvisorio.Fields.VALOR.toString()}, new Type[]{DoubleType.INSTANCE}), AvaliacaoTecnicaVO.Fields.IRP_VALOR.toString());
			pList.add(Projections.property(IRP_PONTO + SceItemRecebProvisorio.Fields.NRO_ITEM.toString()), AvaliacaoTecnicaVO.Fields.IRP_NRO_ITEM.toString());
			pList.add(Projections.property(MAT_PONTO + ScoMaterial.Fields.DESCRICAO.toString()), AvaliacaoTecnicaVO.Fields.MAT_DESCRICAO.toString());
			
			pList.add(Projections.sqlProjection(NULL_AS_PROJECTION + AvaliacaoTecnicaVO.Fields.IAF_MCM_CODIGO.toString(), 
				new String[]{AvaliacaoTecnicaVO.Fields.IAF_MCM_CODIGO.toString()}, new Type[]{IntegerType.INSTANCE}), AvaliacaoTecnicaVO.Fields.IAF_MCM_CODIGO.toString());
			
			pList.add(Projections.sqlProjection(NULL_AS_PROJECTION + AvaliacaoTecnicaVO.Fields.IAF_MOM_SEQP.toString(), 
				new String[]{AvaliacaoTecnicaVO.Fields.IAF_MOM_SEQP.toString()}, new Type[]{IntegerType.INSTANCE}), AvaliacaoTecnicaVO.Fields.IAF_MOM_SEQP.toString());
			
			pList.add(Projections.sqlProjection(NULL_AS_PROJECTION + AvaliacaoTecnicaVO.Fields.IAF_MOM_MCM_CODIGO.toString(), 
				new String[]{AvaliacaoTecnicaVO.Fields.IAF_MOM_MCM_CODIGO.toString()}, new Type[]{IntegerType.INSTANCE}), AvaliacaoTecnicaVO.Fields.IAF_MOM_MCM_CODIGO.toString());
			
			criteria.setProjection(pList);
			criteria.setResultTransformer(Transformers.aliasToBean(AvaliacaoTecnicaVO.class));
			
		if (ptmItemRecebProvisorios != null) {
			
			if (ptmItemRecebProvisorios.getNrpSeq() != null) {
				criteria.add(Restrictions.eq(IRP_PONTO + SceItemRecebProvisorio.Fields.NRP_SEQ.toString(), 
						ptmItemRecebProvisorios.getNrpSeq()));
			}
			if (ptmItemRecebProvisorios.getNroItem() != null) {
				criteria.add(Restrictions.eq(IRP_PONTO + SceItemRecebProvisorio.Fields.NRO_ITEM.toString(), 
						ptmItemRecebProvisorios.getNroItem()));
			}
		}
		
		return criteria;
	}
	
	/**Faz o Union e ordenar a lista  */
	public AvaliacaoTecnicaVO ordenarConsultaCampoDaTela(List<AvaliacaoTecnicaVO> listParte1, List<AvaliacaoTecnicaVO> listParte2){
		if(listParte1 == null){
			listParte1 = new ArrayList<AvaliacaoTecnicaVO>();
		}
		if(listParte2 == null){
			listParte2 = new ArrayList<AvaliacaoTecnicaVO>();
		}
		
		listParte1.addAll(listParte2);//Union
		Set<AvaliacaoTecnicaVO> setListVo = new HashSet<AvaliacaoTecnicaVO>(listParte1);//Distinct
		listParte1 = new ArrayList<AvaliacaoTecnicaVO>();//Limpando lista1
		listParte1.addAll(setListVo);
		
		if(listParte1 != null && !listParte1.isEmpty()){
			Comparator<AvaliacaoTecnicaVO> comparator;
			comparator = new ordenarConsultaCampoDaTelaCampoUM();
			Collections.sort(listParte1, comparator);
			
			return listParte1.get(0);
		}
		return null;
	}
	
	private static class ordenarConsultaCampoDaTelaCampoUM implements Comparator<AvaliacaoTecnicaVO> {
		@Override
		public int compare(AvaliacaoTecnicaVO o1, AvaliacaoTecnicaVO o2) {
			Integer nota1=null;Integer nota2=null;
			if(o1.getIrpNrpseq()!= null){
				nota1 = (o1.getIrpNrpseq());
			}
			if(o2.getIrpNrpseq()!=null){
				nota2 = (o2.getIrpNrpseq());
			}
			return nota1 - nota2;
		}
	}
	
	/**
	 * #43467 C14 Consulta geral para edição de aceite técnico
	 * @return
	 */
	public DetachedCriteria consultaGeralParaEdicaoAceiteTecnico() {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmAvaliacaoTecnica.class, "PAT");
			
		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.IND_STATUS.toString()), AvaliacaoTecnicaVO.Fields.IND_STATUS_AVALIACAO_TEC.toString());
			pList.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString()), AvaliacaoTecnicaVO.Fields.SCE_NRP_SEQ.toString());
			pList.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString()), AvaliacaoTecnicaVO.Fields.SCE_NRP_ITEM.toString());
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.MCM_COD.toString()), AvaliacaoTecnicaVO.Fields.MCM_COD_AVALIACAO_TEC.toString());
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.MOM_COD.toString()), AvaliacaoTecnicaVO.Fields.MOM_COD_AVALIACAO_TEC.toString());
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.VID_AUTIL.toString()), AvaliacaoTecnicaVO.Fields.VIDA_UTIL.toString());
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.DATA_INICIO_GARANTIA.toString()), AvaliacaoTecnicaVO.Fields.DATA_INICIO_AVALIACAO_TEC.toString());
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.TEMPO_GARANTIA.toString()), AvaliacaoTecnicaVO.Fields.TEMPO_GARANTIA.toString());
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.DESCRICAO_MATERIAL.toString()), AvaliacaoTecnicaVO.Fields.DESCRICAO_MATERIAL.toString());
			pList.add(Projections.property(PAT_PONTO + PtmAvaliacaoTecnica.Fields.JUSTIFICATIVA.toString()), AvaliacaoTecnicaVO.Fields.JUSTIFICATIVA_AVALIACAO_TEC.toString());
			pList.add(Projections.property(IRP_PONTO + SceItemRecebProvisorio.Fields.VALOR.toString()), AvaliacaoTecnicaVO.Fields.VALOR.toString());
			
			criteria.setProjection(pList);
			criteria.setResultTransformer(Transformers.aliasToBean(AvaliacaoTecnicaVO.class));
		
			criteria.createAlias(PAT_PONTO + PtmAvaliacaoTecnica.Fields.BEM_PERMANENTES.toString(), "BP", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(PAT_PONTO + PtmAvaliacaoTecnica.Fields.ITEM_RECEB_PROVISORIO.toString(), PIRP, JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), "ATA", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(ATA_PONTO+ PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), "CC", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.TECNICO_ITEM_RECEBIMENTO.toString(), "TIC", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(TIC_PONTO + PtmTecnicoItemRecebimento.Fields.SERVIDOR.toString(), "RS", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(RS_PONTO+ RapServidores.Fields.PESSOA_FISICA.toString(), "RPF", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), IRP, JoinType.LEFT_OUTER_JOIN);
			criteria.addOrder(Order.asc(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString()));
		return criteria;
	}	
}