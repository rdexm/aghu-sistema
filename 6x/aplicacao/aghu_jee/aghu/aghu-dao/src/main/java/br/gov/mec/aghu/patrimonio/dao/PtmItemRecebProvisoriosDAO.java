package br.gov.mec.aghu.patrimonio.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioIndResponsavel;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioStatusAceiteTecnico;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmServAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmTecnicoItemRecebimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceEntradaSaidaSemLicitacao;
import br.gov.mec.aghu.model.SceItemEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.patrimonio.vo.AnaliseTecnicaBemPermanenteVO;
import br.gov.mec.aghu.patrimonio.vo.FiltroAceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.patrimonio.vo.ItemRecebimentoVO;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class PtmItemRecebProvisoriosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PtmItemRecebProvisorios> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2321365942235655918L;
	
	private static final String IRP = "IRP", NRP = "NRP", IAF = "IAF", FSC = "FSC", SLC = "SLC", DFE = "DFE", PIRP= "PIRP", AFN = "AFN", MAT = "MAT", FRN = "FRN", ISL = "ISL", ESL = "ESL";
	private static final String ATA = "ATA";
	private static final String PIRP_PONTO = "PIRP.";
	private static final String ATA_PONTO = "ATA.";

	/**
	 * Obtem lista de objetos com dados da consulta e converte para o VO.
	 */
		
	public List<AceiteTecnicoParaSerRealizadoVO> obterListaAceitesTecnicos(FiltroAceiteTecnicoParaSerRealizadoVO filtro) {
		List<AceiteTecnicoParaSerRealizadoVO> listaRetorno = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
		
		List<Object[]> listaObjeto = consultarListaRecebimentos(filtro);
		
		AceiteTecnicoParaSerRealizadoVO vo = null;
		
		if (listaObjeto != null && !listaObjeto.isEmpty()) {
			for (Object[] objeto : listaObjeto) {
				vo = obterAceiteTecnicoParaSerRealizado(objeto);
				listaRetorno.add(vo);
			}
		}
		
		return listaRetorno;
	}
	
	/**
	 * Obtem lista de objetos com dados do union da consulta e converte para o VO.
	 */
	public List<AceiteTecnicoParaSerRealizadoVO> obterListaAceitesTecnicosUnion(FiltroAceiteTecnicoParaSerRealizadoVO filtro) {
		
		List<AceiteTecnicoParaSerRealizadoVO> listaRetorno = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
		
		List<Object[]> listaObjeto = consultarListaRecebimentosUnion(filtro);
		
		AceiteTecnicoParaSerRealizadoVO vo = null;
		
		if (listaObjeto != null && !listaObjeto.isEmpty()) {
			for (Object[] objeto : listaObjeto) {
				vo = obterAceiteTecnicoParaSerRealizadoUnion(objeto);
				listaRetorno.add(vo);
			}
		}
		
		return listaRetorno;
	}
	
	/**
	 * Recebe array de objetos e retorna VO.
	 */
	private AceiteTecnicoParaSerRealizadoVO obterAceiteTecnicoParaSerRealizado(Object[] objeto) {
		
		AceiteTecnicoParaSerRealizadoVO retorno = new AceiteTecnicoParaSerRealizadoVO();
		
		retorno.setRecebimento(this.converterObjetoParaInteger(objeto[0]));
		retorno.setItemRecebimento(this.converterObjetoParaInteger(objeto[1]));
		retorno.setAf(this.converterObjetoParaInteger(objeto[2]));
		retorno.setComplemento(this.converterObjetoParaShort(objeto[3]));
		retorno.setNroSolicCompras(this.converterObjetoParaInteger(objeto[4]));
		retorno.setCodigo(this.converterObjetoParaInteger(objeto[5]));
		retorno.setMaterial(this.converterObjetoParaString(objeto[6]));
		retorno.setAreaTecAvaliacao(this.converterObjetoParaInteger(objeto[7]));
		retorno.setCentroCustoAutTec(this.converterObjetoParaInteger(objeto[8]));
		retorno.setTecnicoResponsavel(this.converterObjetoParaInteger(objeto[9]));
		retorno.setCodigoTecnicoResponsavel(this.converterObjetoParaShort(objeto[10]));
		retorno.setStatus(this.converterObjetoParaInteger(objeto[11]));
		retorno.setNotaFiscal(this.converterObjetoParaLong(objeto[12]));
		retorno.setFornecedor(this.converterObjetoParaString(objeto[13]));
		retorno.setQuantidade(this.converterObjetoParaInteger(objeto[14]));
		retorno.setQuantidadeDisponivel(this.converterObjetoParaLong(objeto[15]));
		retorno.setSeqItemPatrimonio(this.converterObjetoParaLong(objeto[16]));
		retorno.setCentroCusto(this.converterObjetoParaInteger(objeto[17]));
		retorno.setTecnicoResponsavelItem(this.converterObjetoParaInteger(objeto[18]));
		retorno.setCodigoTecnicoResponsavelItem(this.converterObjetoParaShort(objeto[19]));		

		return retorno;
	}
	
	/**
	 * Recebe array de objetos e retorna VO.
	 */
	private AceiteTecnicoParaSerRealizadoVO obterAceiteTecnicoParaSerRealizadoUnion(Object[] objeto) {
		
		AceiteTecnicoParaSerRealizadoVO retorno = new AceiteTecnicoParaSerRealizadoVO();
		
		retorno.setRecebimento(this.converterObjetoParaInteger(objeto[0]));
		retorno.setEsl(this.converterObjetoParaInteger(objeto[1]));
		retorno.setItemRecebimento(this.converterObjetoParaInteger(objeto[2]));
		retorno.setCodigo(this.converterObjetoParaInteger(objeto[3]));
		retorno.setMaterial(this.converterObjetoParaString(objeto[4]));
		retorno.setAreaTecAvaliacao(this.converterObjetoParaInteger(objeto[5]));
		retorno.setTecnicoResponsavel(this.converterObjetoParaInteger(objeto[6]));
		retorno.setCodigoTecnicoResponsavel(this.converterObjetoParaShort(objeto[7]));
		retorno.setStatus(this.converterObjetoParaInteger(objeto[8]));
		retorno.setNotaFiscal(this.converterObjetoParaLong(objeto[9]));
		retorno.setFornecedor(this.converterObjetoParaString(objeto[10]));
		retorno.setQuantidade(this.converterObjetoParaInteger(objeto[11]));
		retorno.setQuantidadeDisponivel(this.converterObjetoParaLong(objeto[12]));
		retorno.setSeqItemPatrimonio(this.converterObjetoParaLong(objeto[13]));
		retorno.setCentroCusto(this.converterObjetoParaInteger(objeto[14]));
		retorno.setTecnicoResponsavelItem(this.converterObjetoParaInteger(objeto[15]));
		retorno.setCodigoTecnicoResponsavelItem(this.converterObjetoParaShort(objeto[16]));

		
		return retorno;
	}

	/**
	 * Consulta principal da estoria #43464.
	 */
	private List<Object[]> consultarListaRecebimentos(FiltroAceiteTecnicoParaSerRealizadoVO filtro) {
		
		DetachedCriteria criteria = obterCriteriaListaRecebimento(filtro);
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Union da consulta principal da estoria #43464.
	 */
	private List<Object[]> consultarListaRecebimentosUnion(FiltroAceiteTecnicoParaSerRealizadoVO filtro) {
		
		DetachedCriteria criteria = obterCriteriaListaRecebimentoUnion(filtro);
		
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaListaRecebimento(FiltroAceiteTecnicoParaSerRealizadoVO filtro) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, "IRP");
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), "NRP");
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.PTM_ITENS_RECEB_PROVISORIOS.toString(), "PIRP");
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.SCO_ITEM_AUTORIZACAO_FORN.toString(), "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.SCO_FASE_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("NRP." + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE");
		criteria.createAlias("DFE." + SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN");
		criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.TECNICO_ITEM_RECEBIMENTO.toString(), "PTIR", JoinType.LEFT_OUTER_JOIN, Restrictions.eq("PTIR." + PtmTecnicoItemRecebimento.Fields.IND_RESPONSAVEL.toString(), DominioIndResponsavel.R));
		criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), ATA, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ATA_PONTO + PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), "FCC", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projection = Projections.projectionList();
		
		projection.add(Projections.property("NRP." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.RECEBIMENTO.toString());
		projection.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.NRO_ITEM.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.ITEM_RECEBIMENTO.toString());
		projection.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.AF.toString());
		projection.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.COMPLEMENTO.toString());
		projection.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.NRO_SOLIC_COMPRAS.toString());
		projection.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.CODIGO.toString());
		projection.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.MATERIAL.toString());
		projection.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.ATA_SEQ.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.AREA_TEC_AVALIACAO.toString());
		projection.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.CCT_CODIGO_AUTZ_TECNICA.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.CENTRO_CUSTO_AUT_TEC.toString());
		projection.add(Projections.property("PTIR." + PtmTecnicoItemRecebimento.Fields.SER_MATRICULA_TEC_PADRAO.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.TECNICO_RESPONSAVEL.toString());
		projection.add(Projections.property("PTIR." + PtmTecnicoItemRecebimento.Fields.SER_VIN_CODIGO_TEC_PADRAO.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.CODIGO_TECNICO_RESPONSAVEL.toString());
		projection.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.STATUS.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.STATUS.toString());
		projection.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.NOTA_FISCAL.toString());
		projection.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.FORNECEDOR.toString());
		projection.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.QUANTIDADE.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.QUANTIDADE.toString());
		projection.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.QUANTIDADE_DISP.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.QUANTIDADE_DISPONIVEL.toString());
		projection.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SEQ.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.SEQ_ITEM_PATRIMONIO.toString());
		projection.add(Projections.property("FCC." + FccCentroCustos.Fields.CODIGO.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.CENTRO_CUSTO.toString());
		projection.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SER_MATRICULA_TEC_PADRAO.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.TECNICO_RESPONSAVEL_ITEM.toString());
        projection.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SER_VIN_CODIGO_TEC_PADRAO.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.CODIGO_TECNICO_RESPONSAVEL_ITEM.toString());
		
		criteria.setProjection(Projections.distinct(projection));
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));
		
		filtrarPeloCampoRecebimento(filtro, criteria);
		filtrarPeloCampoNumeroAf(filtro, criteria);
		filtrarPeloCampoComplementoAf(filtro, criteria);
		filtrarPeloCampoMaterial(filtro, criteria);
		filtrarPeloCampoResponsavelTecnico(filtro, criteria);
		filtrarPeloCampoAreaTecAvaliacao(filtro, criteria);
		filtrarPeloCampoStatus(filtro, criteria);
		filtrarPorNotaFiscal(filtro, criteria);
		filtrarPeloFornecedor(filtro, criteria);
		filtrarPorSolicitacaoCompras(filtro, criteria);
		
		return criteria;
	}
	
	private DetachedCriteria obterCriteriaListaRecebimentoUnion(FiltroAceiteTecnicoParaSerRealizadoVO filtro) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, "IRP");
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), "NRP");
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.PTM_ITENS_RECEB_PROVISORIOS.toString(), "PIRP");
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.ENTRADA_SAIDA_SEM_LICITACAO.toString(), "ESL");
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.ITEM_ENTR_SAID_SEM_LICITACAO.toString(), "ISL");
		criteria.createAlias("ISL." + SceItemEntrSaidSemLicitacao.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("NRP." + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE");
		criteria.createAlias("DFE." + SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN");
		criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.TECNICO_ITEM_RECEBIMENTO.toString(), "PTIR", JoinType.LEFT_OUTER_JOIN, Restrictions.eq("PTIR." + PtmTecnicoItemRecebimento.Fields.IND_RESPONSAVEL.toString(), DominioIndResponsavel.R));
		criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), ATA, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ATA_PONTO + PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), "FCC", JoinType.LEFT_OUTER_JOIN);
			
				
		ProjectionList projection = Projections.projectionList();

		projection.add(Projections.property("NRP." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.RECEBIMENTO.toString());
		projection.add(Projections.property("ESL." + SceItemEntrSaidSemLicitacao.Fields.SEQ.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.ESL.toString());
		projection.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.NRO_ITEM.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.ITEM_RECEBIMENTO.toString());
		projection.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.CODIGO.toString());
		projection.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.MATERIAL.toString());
		projection.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.ATA_SEQ.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.AREA_TEC_AVALIACAO.toString());
		projection.add(Projections.property("PTIR." + PtmTecnicoItemRecebimento.Fields.SER_MATRICULA_TEC_PADRAO.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.TECNICO_RESPONSAVEL.toString());
		projection.add(Projections.property("PTIR." + PtmTecnicoItemRecebimento.Fields.SER_VIN_CODIGO_TEC_PADRAO.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.CODIGO_TECNICO_RESPONSAVEL.toString());
		projection.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.STATUS.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.STATUS.toString());
		projection.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.NOTA_FISCAL.toString());
		projection.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.FORNECEDOR.toString());
		projection.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.QUANTIDADE.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.QUANTIDADE.toString());
		projection.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.QUANTIDADE_DISP.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.QUANTIDADE_DISPONIVEL.toString());
		projection.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SEQ.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.SEQ_ITEM_PATRIMONIO.toString());
		projection.add(Projections.property("FCC." + FccCentroCustos.Fields.CODIGO.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.CENTRO_CUSTO.toString());
		projection.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SER_MATRICULA_TEC_PADRAO.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.TECNICO_RESPONSAVEL_ITEM.toString());
        projection.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SER_VIN_CODIGO_TEC_PADRAO.toString()), AceiteTecnicoParaSerRealizadoVO.Fields.CODIGO_TECNICO_RESPONSAVEL_ITEM.toString());
		
		criteria.setProjection(projection);
		
		filtrarPeloCampoRecebimento(filtro, criteria);
		filtrarPeloCampoMaterial(filtro, criteria);
		filtrarPeloCampoResponsavelTecnico(filtro, criteria);
		filtrarPeloCampoAreaTecAvaliacao(filtro, criteria);
		filtrarPeloCampoStatus(filtro, criteria);
		filtrarPorNotaFiscal(filtro, criteria);
		filtrarPeloFornecedor(filtro, criteria);
		
		return criteria;
	}
	
	private void filtrarPeloCampoAreaTecAvaliacao(FiltroAceiteTecnicoParaSerRealizadoVO filtro, DetachedCriteria criteria) {
		if (filtro != null && filtro.getAreaTecnicaAvaliacao() != null) {
			criteria.add(Restrictions.eq(PIRP_PONTO + PtmItemRecebProvisorios.Fields.ATA_SEQ.toString(), filtro.getAreaTecnicaAvaliacao().getSeq()));
		}
	}

	private void filtrarPeloCampoResponsavelTecnico(FiltroAceiteTecnicoParaSerRealizadoVO filtro, DetachedCriteria criteria) {
		if (filtro != null && filtro.getResponsavelTecnico() != null && filtro.getResponsavelTecnico().getId() != null) {
			criteria.add(Restrictions.eq("PTIR." + PtmTecnicoItemRecebimento.Fields.SER_MATRICULA_TEC_PADRAO.toString(), filtro.getResponsavelTecnico().getId().getMatricula()));
			criteria.add(Restrictions.eq("PTIR." + PtmTecnicoItemRecebimento.Fields.SER_VIN_CODIGO_TEC_PADRAO.toString(), filtro.getResponsavelTecnico().getId().getVinCodigo()));
		}
	}

	private void filtrarPeloCampoMaterial(FiltroAceiteTecnicoParaSerRealizadoVO filtro, DetachedCriteria criteria) {
		if (filtro != null && filtro.getMaterial() != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), filtro.getMaterial().getCodigo()));
		}
	}

	private void filtrarPeloCampoComplementoAf(FiltroAceiteTecnicoParaSerRealizadoVO filtro, DetachedCriteria criteria) {
		if (filtro != null && filtro.getComplementoAf() != null) {
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getComplementoAf()));
		}
	}

	private void filtrarPeloCampoNumeroAf(FiltroAceiteTecnicoParaSerRealizadoVO filtro, DetachedCriteria criteria) {
		if (filtro != null && filtro.getNumeroAf() != null) {
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtro.getNumeroAf()));
		}
	}

	private void filtrarPeloCampoRecebimento(FiltroAceiteTecnicoParaSerRealizadoVO filtro, DetachedCriteria criteria) {
		if (filtro != null && filtro.getNumeroRecebimento() != null) {
			criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString(), filtro.getNumeroRecebimento()));
		}
	}

	private void filtrarPeloCampoStatus(FiltroAceiteTecnicoParaSerRealizadoVO filtro, DetachedCriteria criteria) {
		if (filtro != null && filtro.getStatus() != null) {
			criteria.add(Restrictions.eq(PIRP_PONTO + PtmItemRecebProvisorios.Fields.STATUS.toString(), filtro.getStatus().getCodigo()));
		}
	}
	
	private void filtrarPorNotaFiscal(FiltroAceiteTecnicoParaSerRealizadoVO filtro, DetachedCriteria criteria) {
		if (filtro != null && filtro.getNotaFiscal() != null) {
			criteria.add(Restrictions.eq("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString(), filtro.getNotaFiscal()));
		}
	}
	
	private void filtrarPeloFornecedor(FiltroAceiteTecnicoParaSerRealizadoVO filtro, DetachedCriteria criteria) {
		if (filtro != null && filtro.getFornecedor() != null) {
			criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), filtro.getFornecedor().getNumero()));
		}
	}
	
	private void filtrarPorSolicitacaoCompras(FiltroAceiteTecnicoParaSerRealizadoVO filtro, DetachedCriteria criteria) {
		if (filtro != null && filtro.getNroSolicitacaoCompra() != null) {
			criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), filtro.getNroSolicitacaoCompra()));
		}
	}

	/**
	 * Converte Objeto para String
	 */
	private String converterObjetoParaString(Object object) {
		if (object != null) {
			return object.toString();
		}
		return null;
	}
	
	/**
	 * Converte Objeto para Integer
	 */
	private Integer converterObjetoParaInteger(Object object) {
		if (object != null) {
			return Integer.parseInt(object.toString());
		}
		return null;
	}
	
	/**
	 * Converte Objeto para Long
	 */
	private Long converterObjetoParaLong(Object object) {
		if (object != null) {
			return Long.parseLong(object.toString());
		}
		return null;
	}
	
	/**
	 * Converte Objeto para Short
	 */
	private Short converterObjetoParaShort(Object object) {
		if (object != null) { 
			return Short.parseShort(object.toString());
		}
		return null;
	}

	/**
	 * Realiza a busca por itens de recebimento com AF, relacionados ao Recebimento informado.
	 * 
	 * @param numeroRecebimento - Número de Recebimento
	 * @return Lista de itens de recebimento
	 */
	public List<AnaliseTecnicaBemPermanenteVO> consultarItensRecebimentoComAF(Integer numeroRecebimento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, "IRP");

		ProjectionList projections = Projections.projectionList();
		
		projections.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString()),
				AnaliseTecnicaBemPermanenteVO.Fields.RECEBIMENTO.toString());
		projections.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.NRO_ITEM.toString()),
				AnaliseTecnicaBemPermanenteVO.Fields.ITEM_RECEBIMENTO.toString());
		projections.add(Projections.property("SIA." + ScoItemAutorizacaoForn.Fields.IPF_PFR_LCT_NUMERO.toString()),
				AnaliseTecnicaBemPermanenteVO.Fields.AF.toString());
		projections.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()),
				AnaliseTecnicaBemPermanenteVO.Fields.COMPLEMENTO.toString());
		projections.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()),
				AnaliseTecnicaBemPermanenteVO.Fields.NRO_SOLIC_COMPRAS.toString());
		projections.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), AnaliseTecnicaBemPermanenteVO.Fields.NRO_MATERIAL.toString());
		projections.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), AnaliseTecnicaBemPermanenteVO.Fields.MATERIAL.toString());
		projections.add(Projections.property("CCT." + FccCentroCustos.Fields.CODIGO.toString()),
				AnaliseTecnicaBemPermanenteVO.Fields.CENTRO_CUSTO_AUT_TEC.toString());
		projections.add(Projections.property(ATA_PONTO + PtmAreaTecAvaliacao.Fields.SEQ.toString()), AnaliseTecnicaBemPermanenteVO.Fields.SEQ_AREA.toString());
		projections.add(Projections.property(ATA_PONTO + PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString()),
				AnaliseTecnicaBemPermanenteVO.Fields.NOME_AREA.toString());
		projections.add(Projections.property("SER." + RapServidores.Fields.MATRICULA.toString()),
				AnaliseTecnicaBemPermanenteVO.Fields.MATRICULA_SERVIDOR.toString());
		projections.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString()), AnaliseTecnicaBemPermanenteVO.Fields.AFN_NUMERO.toString());
		
		criteria.setProjection(Projections.distinct(projections));
		
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.SCO_ITEM_AUTORIZACAO_FORN.toString(), "SIA2");
		criteria.createAlias("SIA2." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString(), "SIA");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.CCT_AUTZ_TECNICA.toString(), "CCT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.PTM_ITENS_RECEB_PROVISORIOS.toString(), "PIRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), ATA, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SERVIDOR_TEC_PADRAO.toString(), "SER", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("IRP." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString(), numeroRecebimento));
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));
		
		criteria.addOrder(Order.asc("IRP." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString()));
		criteria.addOrder(Order.asc("IRP." + SceItemRecebProvisorio.Fields.NRO_ITEM.toString()));
		criteria.addOrder(Order.asc("SIA." + ScoItemAutorizacaoForn.Fields.IPF_PFR_LCT_NUMERO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(AnaliseTecnicaBemPermanenteVO.class));

		return executeCriteria(criteria);
	}

	/**
	 * Realiza a busca por itens de recebimento sem AF, relacionados ao Recebimento informado.
	 * 
	 * @param numeroRecebimento - Número de Recebimento
	 * @return Lista de itens de recebimento
	 */
	public List<AnaliseTecnicaBemPermanenteVO> consultarItensRecebimentoSemAF(Integer numeroRecebimento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, "IRP");

		ProjectionList projections = Projections.projectionList();
		
		projections.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString()),
				AnaliseTecnicaBemPermanenteVO.Fields.RECEBIMENTO.toString());
		projections.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.NRO_ITEM.toString()),
				AnaliseTecnicaBemPermanenteVO.Fields.ITEM_RECEBIMENTO.toString());
		projections.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), AnaliseTecnicaBemPermanenteVO.Fields.NRO_MATERIAL.toString());
		projections.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), AnaliseTecnicaBemPermanenteVO.Fields.MATERIAL.toString());
		projections.add(Projections.property("ESL." + SceEntradaSaidaSemLicitacao.Fields.SEQ.toString()), AnaliseTecnicaBemPermanenteVO.Fields.ESL.toString());
		projections.add(Projections.property(ATA_PONTO + PtmAreaTecAvaliacao.Fields.SEQ.toString()), AnaliseTecnicaBemPermanenteVO.Fields.SEQ_AREA.toString());
		projections.add(Projections.property(ATA_PONTO + PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString()),
				AnaliseTecnicaBemPermanenteVO.Fields.NOME_AREA.toString());
		projections.add(Projections.property("SER." + RapServidores.Fields.MATRICULA.toString()),
				AnaliseTecnicaBemPermanenteVO.Fields.MATRICULA_SERVIDOR.toString());
		
		criteria.setProjection(Projections.distinct(projections));
		
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.ENTRADA_SAIDA_SEM_LICITACAO.toString(), "ESL");
		criteria.createAlias("ESL." + SceEntradaSaidaSemLicitacao.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.PTM_ITENS_RECEB_PROVISORIOS.toString(), "PIRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), ATA, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SERVIDOR_TEC_PADRAO.toString(), "SER", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("IRP." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString(), numeroRecebimento));
		
		criteria.addOrder(Order.asc("IRP." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString()));
		criteria.addOrder(Order.asc("IRP." + SceItemRecebProvisorio.Fields.NRO_ITEM.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(AnaliseTecnicaBemPermanenteVO.class));

		return executeCriteria(criteria);
	}

	/**
	 * Obtém item de recebimento através do id do Item em Estoque.
	 * 
	 * @param nrpSeq - Seq do Item 
	 * @param nroItem - Número do Item
	 * @return Item de recebimento em Patrimônio
	 */
	public PtmItemRecebProvisorios obterPorIdItensEstoque(Integer nrpSeq, Integer nroItem) {

		DetachedCriteria criteria = DetachedCriteria.forClass(PtmItemRecebProvisorios.class, "PIR");

		criteria.createAlias("PIR." + PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), "IRP");
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.PROG_ENTREGA_IAF.toString(), "PEI");
		
		criteria.add(Restrictions.eq("IRP." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString(), nrpSeq));
		criteria.add(Restrictions.eq("IRP." + SceItemRecebProvisorio.Fields.NRO_ITEM.toString(), nroItem));
		
		return (PtmItemRecebProvisorios) executeCriteriaUniqueResult(criteria);
	}
	
	public List<PtmItemRecebProvisorios> pesquisarItemRecebProvisorios(Integer recebimento, Integer itemRecebimento, RapServidores servidor){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmItemRecebProvisorios.class);
		criteria.add(Restrictions.eq(PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), recebimento));
		criteria.add(Restrictions.eq(PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), itemRecebimento));
		if (servidor != null) {
			criteria.add(Restrictions.eq(PtmItemRecebProvisorios.Fields.SERVIDOR.toString(), servidor));
		}
		
		return executeCriteria(criteria);
	}

	/**
	 * #43446 C8 Verifica se item de recebimento tem pagamento parcial assinalado.
	 * @param recebimento {@link Integer}
	 * @param itemRecebimento {@link Integer}
	 * @return valor do Pagamento Parcial
	 */
	public Integer obterPagamentoParcialItemRecebimento(Integer recebimento, Integer itemRecebimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmItemRecebProvisorios.class);
		criteria.setProjection(Projections.projectionList().add(Projections.property(PtmItemRecebProvisorios.Fields.PAG_PARCIAL.toString()),
				PtmItemRecebProvisorios.Fields.PAG_PARCIAL.toString()));
		if (recebimento != null) {
			criteria.add(Restrictions.eq(PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), recebimento));
		}
		if (itemRecebimento != null) {
			criteria.add(Restrictions.eq(PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), itemRecebimento));
		}
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	public PtmItemRecebProvisorios pesquisarIrpSeq(Integer recebimento, Integer itemRecebimento, RapServidores servidor){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmItemRecebProvisorios.class);
		criteria.add(Restrictions.eq(PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), recebimento));
		criteria.add(Restrictions.eq(PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), itemRecebimento));
//		if (servidor != null) {
//			criteria.add(Restrictions.eq(PtmItemRecebProvisorios.Fields.SERVIDOR.toString(), servidor));
//		}
		
		return (PtmItemRecebProvisorios) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #44713 - Suggestion para Consulta de recebimentos
				filtrando sce_nrp_seq  e nro_item ordenado por nrpseq
	 * @param objPesquisa
	 * @param colunaOrdenacao
	 * @return
	 */
	public List<PtmItemRecebProvisorios> listarConsultaRecebimentosSugestion(Object param) {
		DetachedCriteria criteria = listarConsultaRecebimentosSugestionCriteria(param);
		criteria.addOrder(Order.asc("IRP."+SceItemRecebProvisorio.Fields.NRP_SEQ.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}

	private DetachedCriteria listarConsultaRecebimentosSugestionCriteria(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmItemRecebProvisorios.class,"PIRP");
		criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), "IRP", JoinType.LEFT_OUTER_JOIN);
		if (param != null &&  StringUtils.isNotBlank(param.toString())) {
			criteria.add(Restrictions.or(Restrictions.eq("IRP."+SceItemRecebProvisorio.Fields.NRP_SEQ.toString(), Integer.parseInt(param.toString())), Restrictions.eq("IRP."+SceItemRecebProvisorio.Fields.NRO_ITEM.toString(), Integer.parseInt(param.toString()))));
		}
		return criteria;
	}
	
	public PtmItemRecebProvisorios listarConsultaRecebimentosSugestionGrid(PtmItemRecebProvisorios ptmItemRecebProvisorios) {
		DetachedCriteria criteria = listarConsultaRecebimentosSugestionGridCriteria(ptmItemRecebProvisorios);
		return (PtmItemRecebProvisorios) executeCriteriaUniqueResult(criteria);
	}
	
	private DetachedCriteria listarConsultaRecebimentosSugestionGridCriteria(PtmItemRecebProvisorios ptmItemRecebProvisorios) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmItemRecebProvisorios.class,"PIRP");
		 
		if (ptmItemRecebProvisorios != null && ptmItemRecebProvisorios.getNroItem() != null && ptmItemRecebProvisorios.getNrpSeq() != null) {
			criteria.add(Restrictions.eq("PIRP."+PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), ptmItemRecebProvisorios.getNrpSeq())); 
			criteria.add(Restrictions.eq("PIRP."+PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), ptmItemRecebProvisorios.getNroItem()));			
		}
		return criteria;
	}
	
	public Long listarConsultaRecebimentosSugestionCount(Object param){
		DetachedCriteria criteria = listarConsultaRecebimentosSugestionCriteria(param);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * #48782 C2 SB Seleção de recebimento
	 * @param param
	 * @param ccCodigo
	 * @return
	 */
	public List<PtmItemRecebProvisorios> pesquisarItemRecebProvisorioPorCentroCusto(String param, RapServidores servidor){
		DetachedCriteria criteria = montarCriteriaSBItemRecebProvisorio(param, servidor);
		criteria.addOrder(Order.asc(PIRP_PONTO+PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString()));
		criteria.addOrder(Order.asc(PIRP_PONTO+PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	//#48782 Count C2 SB Seleção de recebimento
	public Long pesquisarItemRecebProvisorioPorCentroCustoCount(String param, RapServidores servidor){
		List<PtmItemRecebProvisorios> lista = executeCriteria(montarCriteriaSBItemRecebProvisorio(param, servidor));
		return Long.valueOf(lista.size());
	}
	private DetachedCriteria montarCriteriaSBItemRecebProvisorio(String param, RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmItemRecebProvisorios.class, "PIRP");
		criteria.createAlias(PIRP_PONTO+PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), "IRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PIRP_PONTO+PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), ATA, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATA."+PtmAreaTecAvaliacao.Fields.PTM_SERV_AREA_TEC_AVALIACAO.toString(), "SATA", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.ne(PIRP_PONTO + PtmItemRecebProvisorios.Fields.STATUS.toString(), DominioStatusAceiteTecnico.CONCLUIDA.getCodigo()));
		criteria.add(Restrictions.ne(PIRP_PONTO + PtmItemRecebProvisorios.Fields.STATUS.toString(), DominioStatusAceiteTecnico.DEVOLVIDO.getCodigo()));
		
		if (servidor != null && servidor.getId() != null) {
			criteria.add(Restrictions.or(Subqueries.propertyIn(ATA_PONTO+PtmAreaTecAvaliacao.Fields.COD_CENTRO_CUSTOS.toString(), obterSubCriteira1(servidor)),
					Subqueries.propertyIn(ATA_PONTO+PtmAreaTecAvaliacao.Fields.SEQ.toString(), obterSubCriteira2(servidor))));
		}
		
		if (StringUtils.isNotBlank((String) param)) {
			if (CoreUtil.isNumeroInteger(param)) {
				criteria.add(Restrictions.or(
						Restrictions.eq(PIRP_PONTO+PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), Integer.valueOf(param)),
						Restrictions.eq(PIRP_PONTO+PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), Integer.valueOf(param)),
						Restrictions.eq(ATA_PONTO+PtmAreaTecAvaliacao.Fields.COD_CENTRO_CUSTOS.toString(), Integer.valueOf(param))
						));
			}
		} 
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(PIRP_PONTO+PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString()),
						PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString())
				.add(Projections.property(PIRP_PONTO+PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString()),
						PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString())
				.add(Projections.property(PIRP_PONTO+PtmItemRecebProvisorios.Fields.SEQ.toString()),
						PtmItemRecebProvisorios.Fields.SEQ.toString()))
				);
		
		criteria.setResultTransformer(Transformers.aliasToBean(PtmItemRecebProvisorios.class));
		return criteria;
	}
	private DetachedCriteria obterSubCriteira1(RapServidores servidor) {
		DetachedCriteria subcriteria1 = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class, ATA);
		
		subcriteria1.setProjection(Projections.projectionList()
				.add(Projections.property(ATA_PONTO+PtmAreaTecAvaliacao.Fields.COD_CENTRO_CUSTOS.toString()),
						PtmAreaTecAvaliacao.Fields.COD_CENTRO_CUSTOS.toString())
				);
			
		subcriteria1.add(Restrictions.eq(ATA_PONTO+PtmAreaTecAvaliacao.Fields.SERVIDOR_CC_MATRICULA.toString(), servidor.getId().getMatricula()));
		subcriteria1.add(Restrictions.eq(ATA_PONTO+PtmAreaTecAvaliacao.Fields.SERVIDOR_CC_VIN_CODIGO.toString(), servidor.getId().getVinCodigo()));
			
		subcriteria1.setResultTransformer(Transformers.aliasToBean(PtmAreaTecAvaliacao.class));
		return subcriteria1;
	}
	private DetachedCriteria obterSubCriteira2(RapServidores servidor) {
		DetachedCriteria subcriteria2 = DetachedCriteria.forClass(PtmServAreaTecAvaliacao.class, ATA);
		
		subcriteria2.setProjection(Projections.projectionList()
				.add(Projections.property(ATA_PONTO+PtmServAreaTecAvaliacao.Fields.SEQ_AREA_TEC_AVALIACAO.toString()),
						PtmServAreaTecAvaliacao.Fields.SEQ_AREA_TEC_AVALIACAO.toString())
				);
		
		subcriteria2.add(Restrictions.eq(ATA_PONTO+PtmServAreaTecAvaliacao.Fields.MAT_RAP_TECNICO.toString(), servidor.getId().getMatricula()));
		subcriteria2.add(Restrictions.eq(ATA_PONTO+PtmServAreaTecAvaliacao.Fields.SER_VIN_CODIGO_TECNICO.toString(), servidor.getId().getVinCodigo()));
		
		subcriteria2.setResultTransformer(Transformers.aliasToBean(PtmServAreaTecAvaliacao.class));
		return subcriteria2;
	}	
	//#48782 C19
	public List<PtmItemRecebProvisorios> verificarSeq(Integer sceNrSeq, Integer sceNrItem){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmItemRecebProvisorios.class, "PIRP");
		criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), ATA, JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
					.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SEQ.toString()), 
							PtmItemRecebProvisorios.Fields.SEQ.toString())
				);
		
		criteria.add(Restrictions.ne(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), sceNrSeq));
		criteria.add(Restrictions.ne(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), sceNrItem));
		
		criteria.addOrder(Order.asc(PIRP_PONTO+PtmItemRecebProvisorios.Fields.SEQ.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(PtmItemRecebProvisorios.class));
		return executeCriteria(criteria);
	}
	//#48782 - C21
	public boolean verificarUsuarioLogadoResponsavelPorAceite(Long seqItemReceb, RapServidores servidor){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmItemRecebProvisorios.class, "PIRP");
		criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.TECNICO_ITEM_RECEBIMENTO.toString(), "TIR", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("PIRP." + PtmItemRecebProvisorios.Fields.SEQ.toString(), seqItemReceb));
		criteria.add(Restrictions.eq("TIR." + PtmTecnicoItemRecebimento.Fields.SER_MATRICULA_TEC_PADRAO.toString(), servidor.getId().getMatricula()));
		criteria.add(Restrictions.eq("TIR." + PtmTecnicoItemRecebimento.Fields.SER_VIN_CODIGO_TEC_PADRAO.toString(), servidor.getId().getVinCodigo()));
		criteria.add(Restrictions.eq("TIR." + PtmTecnicoItemRecebimento.Fields.IND_RESPONSAVEL.toString(), DominioIndResponsavel.R));
		return executeCriteriaExists(criteria);
	}
	public PtmItemRecebProvisorios pesquisarItemRecebSeq(Integer recebimento, Integer itemRecebimento){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmItemRecebProvisorios.class,"PIRP");
		criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), "SITP", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO.toString()), 
						PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO.toString())
				.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString()), 
						PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString())
				.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString()), 
						PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString())
				.add(Projections.property(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SEQ.toString()), 
						PtmItemRecebProvisorios.Fields.SEQ.toString())
			);
		
		criteria.add(Restrictions.eq(PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), recebimento));
		criteria.add(Restrictions.eq(PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), itemRecebimento));
		criteria.setResultTransformer(Transformers.aliasToBean(PtmItemRecebProvisorios.class));
		return (PtmItemRecebProvisorios) executeCriteriaUniqueResult(criteria);
	}
	
	public List<PtmItemRecebProvisorios> listarItemRecebPorSeqItemReceb(Integer recebimento, Integer itemRecebimento){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmItemRecebProvisorios.class,"PIRP");
		criteria.createAlias(PIRP_PONTO + PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), "SITP", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), recebimento));
		criteria.add(Restrictions.eq(PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), itemRecebimento));
		return executeCriteria(criteria);
	}


	/**
	 * Consulta para preencher fieldset “Item de Recebimento”
	 * @param numRecebimento
	 * @param itemRecebimento
	 * @return ItemRecebimentoVO
	 */
	public List<ItemRecebimentoVO> carregarItemRecebimentoPrimeiraParte(Integer numRecebimento, Integer itemRecebimento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, "IRP");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(PIRP + "." + PtmItemRecebProvisorios.Fields.SEQ.toString()),	ItemRecebimentoVO.Fields.IRPSEQ.toString())
				.add(Projections.property(IRP + "." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString()),ItemRecebimentoVO.Fields.RECEBIMENTO.toString())
				.add(Projections.property(IRP + "." + SceItemRecebProvisorio.Fields.NRO_ITEM.toString()),ItemRecebimentoVO.Fields.ITEM_RECEBIMENTO.toString())
				.add(Projections.property(AFN + "." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()),ItemRecebimentoVO.Fields.AF.toString())
				.add(Projections.property(AFN + "." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()),ItemRecebimentoVO.Fields.COMPLEMENTO.toString())
				.add(Projections.property(SLC + "." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()),ItemRecebimentoVO.Fields.NRO_SOLIC_COMPRAS.toString())
				.add(Projections.property(MAT + "." + ScoMaterial.Fields.CODIGO.toString()),ItemRecebimentoVO.Fields.CODIGO.toString())
				.add(Projections.property(MAT + "." + ScoMaterial.Fields.NOME.toString()),ItemRecebimentoVO.Fields.MATERIAL.toString())
				.add(Projections.property(PIRP + "." + PtmItemRecebProvisorios.Fields.ATA_SEQ.toString()),	ItemRecebimentoVO.Fields.AREA_TEC_AVALIACAO.toString())
				.add(Projections.property(ATA + "." + PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString()),	ItemRecebimentoVO.Fields.NOME_AREA_TEC_AVALIACAO.toString())
				.add(Projections.property(PIRP + "." + PtmItemRecebProvisorios.Fields.STATUS.toString()),	ItemRecebimentoVO.Fields.STATUS.toString())
				.add(Projections.property(DFE + "." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()),	ItemRecebimentoVO.Fields.NOTA_FISCAL.toString())
				.add(Projections.property(FRN + "." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()),	ItemRecebimentoVO.Fields.RAZAO_SOCIAL.toString())
				.add(Projections.property(FRN + "." + ScoFornecedor.Fields.CGC.toString()),	ItemRecebimentoVO.Fields.CNPJ.toString())
				.add(Projections.property(FRN + "." + ScoFornecedor.Fields.CPF.toString()),	ItemRecebimentoVO.Fields.CPF.toString()));

				
		criteria.createAlias(IRP + "." + SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), "NRP", JoinType.INNER_JOIN);
		criteria.createAlias(IRP + "." + SceItemRecebProvisorio.Fields.PTM_ITENS_RECEB_PROVISORIOS.toString(), "PIRP", JoinType.INNER_JOIN);
		criteria.createAlias(IRP + "." + SceItemRecebProvisorio.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF", JoinType.INNER_JOIN);
		criteria.createAlias(IAF + "." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.INNER_JOIN);
		criteria.createAlias(IRP + "." + SceItemRecebProvisorio.Fields.SCO_FASE_SOLICITACAO.toString(), "FSC", JoinType.INNER_JOIN);
		criteria.createAlias(FSC + "." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
		criteria.createAlias(SLC + "." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias(NRP + "." + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.INNER_JOIN);
		criteria.createAlias(DFE + "." + SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		criteria.createAlias(PIRP + "." + PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), "ATA", JoinType.INNER_JOIN);		
		criteria.add(Restrictions.eq(FSC + "." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));
		
		criteria.add(Restrictions.eq(IRP + "." +SceItemRecebProvisorio.Fields.NRP_SEQ.toString(), numRecebimento));
		criteria.add(Restrictions.eq(IRP + "." +SceItemRecebProvisorio.Fields.NRO_ITEM.toString(), itemRecebimento));
	
		criteria.setResultTransformer(Transformers.aliasToBean(ItemRecebimentoVO.class));		
		return executeCriteria(criteria);
	}	
	
	/**
	 * Consulta para preencher fieldset “Item de Recebimento”
	 * @param numRecebimento
	 * @param itemRecebimento
	 * @return ItemRecebimentoVO
	 */
	public List<ItemRecebimentoVO> carregarItemRecebimentoSegundaParte(Integer numRecebimento, Integer itemRecebimento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, "IRP");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(PIRP + "." + PtmItemRecebProvisorios.Fields.SEQ.toString()),	ItemRecebimentoVO.Fields.IRPSEQ.toString())
				.add(Projections.property(IRP + "." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString()),ItemRecebimentoVO.Fields.RECEBIMENTO.toString())
				.add(Projections.property(IRP + "." + SceItemRecebProvisorio.Fields.NRO_ITEM.toString()),ItemRecebimentoVO.Fields.ITEM_RECEBIMENTO.toString())
				.add(Projections.property(ESL + "." + SceEntrSaidSemLicitacao.Fields.SEQ.toString()),ItemRecebimentoVO.Fields.ESL.toString())
				.add(Projections.property(MAT + "." + ScoMaterial.Fields.CODIGO.toString()),ItemRecebimentoVO.Fields.CODIGO.toString())
				.add(Projections.property(MAT + "." + ScoMaterial.Fields.NOME.toString()),ItemRecebimentoVO.Fields.MATERIAL.toString())
				.add(Projections.property(PIRP + "." + PtmItemRecebProvisorios.Fields.ATA_SEQ.toString()),	ItemRecebimentoVO.Fields.AREA_TEC_AVALIACAO.toString())
				.add(Projections.property(ATA + "." + PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString()),	ItemRecebimentoVO.Fields.NOME_AREA_TEC_AVALIACAO.toString())
				.add(Projections.property(PIRP + "." + PtmItemRecebProvisorios.Fields.STATUS.toString()),	ItemRecebimentoVO.Fields.STATUS.toString())
				.add(Projections.property(DFE + "." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()),	ItemRecebimentoVO.Fields.NOTA_FISCAL.toString())
				.add(Projections.property(FRN + "." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()),	ItemRecebimentoVO.Fields.RAZAO_SOCIAL.toString())
				.add(Projections.property(FRN + "." + ScoFornecedor.Fields.CGC.toString()),	ItemRecebimentoVO.Fields.CNPJ.toString())
				.add(Projections.property(FRN + "." + ScoFornecedor.Fields.CPF.toString()),	ItemRecebimentoVO.Fields.CPF.toString()));
				
		criteria.createAlias(IRP + "." + SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), "NRP", JoinType.INNER_JOIN);
		criteria.createAlias(IRP + "." + SceItemRecebProvisorio.Fields.PTM_ITENS_RECEB_PROVISORIOS.toString(), "PIRP", JoinType.INNER_JOIN);		
		criteria.createAlias(IRP + "." + SceItemRecebProvisorio.Fields.ITEM_ENTR_SAID_SEM_LICITACAO.toString(), "ISL", JoinType.INNER_JOIN );
		criteria.add(Restrictions.eqProperty(IRP + "." + SceItemRecebProvisorio.Fields.ESL_SEQ.toString(), ISL + "." + SceItemEntrSaidSemLicitacao.Fields.ESL_SEQ.toString()));
		criteria.createAlias(ISL + "." + SceItemEntrSaidSemLicitacao.Fields.SCE_ENTR_SAID_SEM_LICITACAO.toString(), "ESL", JoinType.INNER_JOIN);
		criteria.createAlias(ISL + "." + SceItemEntrSaidSemLicitacao.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias(NRP + "." + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.INNER_JOIN);
		criteria.createAlias(ESL + "." + SceEntrSaidSemLicitacao.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		criteria.createAlias(PIRP + "." + PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), "ATA", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eqProperty(ESL + "." + SceEntrSaidSemLicitacao.Fields.DFE_SEQ.toString(), DFE + "." + SceDocumentoFiscalEntrada.Fields.SEQ.toString()));
		
		criteria.add(Restrictions.eq(IRP + "." +SceItemRecebProvisorio.Fields.NRP_SEQ.toString(), numRecebimento));
		criteria.add(Restrictions.eq(IRP + "." +SceItemRecebProvisorio.Fields.NRO_ITEM.toString(), itemRecebimento));
	
		criteria.setResultTransformer(Transformers.aliasToBean(ItemRecebimentoVO.class));		
		return executeCriteria(criteria);
	}
}

