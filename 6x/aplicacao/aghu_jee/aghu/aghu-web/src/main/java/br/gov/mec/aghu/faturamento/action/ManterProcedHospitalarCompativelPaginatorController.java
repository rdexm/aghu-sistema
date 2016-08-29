package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedimentosHospitalares;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


public class ManterProcedHospitalarCompativelPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7959919110552326081L;
	
	private static final Log LOG = LogFactory.getLog(ManterProcedHospitalarCompativelPaginatorController.class);
	
	private static final String REDIRECIONA_MANTER_PROC_HOSP_COMPAT = "manterProcedHospitalarCompativel";
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private ManterProcedHospitalarCompativelController manterProcedHospitalarCompativelController;
	
	// FILTROS
	private FatProcedimentosHospitalares codigoTabela;
	private Long codigoSus;
	private Integer iph;
	private String descricao;
	private Boolean ordenar = true;
	
	//DISPLAY
	private String descricaoTabela;
	
	@Inject @Paginator
	private DynamicDataModel<FatItensProcedHospitalar> dataModel;

	private FatItensProcedHospitalar procedimentoSelecionado;
	
	public enum ManterProcedHospitalarCompativelPaginatorControllerExceptionCode implements
	BusinessExceptionCode {
		LABEL_INFORME_UM_CAMPO_PARA_PESQUISAR
	}	

	@PostConstruct
	public void inicio() {
		begin(conversation);
		
		try {
			codigoTabela = faturamentoFacade.obterProcedimentoHospitalar(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO).getVlrNumerico().shortValue());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
			
	}	
	
	public void pesquisar() {
		
		if (codigoTabela!= null || codigoSus != null || 
		   iph != null || !descricao.trim().equals("")) {
			
			dataModel.reiniciarPaginator();
		}
		else {
			apresentarMsgNegocio(ManterProcedHospitalarCompativelPaginatorControllerExceptionCode.LABEL_INFORME_UM_CAMPO_PARA_PESQUISAR.toString());
		}

	}
	
	public String editar() {
		manterProcedHospitalarCompativelController.inicio(procedimentoSelecionado);
		return REDIRECIONA_MANTER_PROC_HOSP_COMPAT;
	}

	public void limparPesquisa() {
		inicio();
		codigoSus = null;
		iph = null;
		descricao = null;
		descricaoTabela = null;
		dataModel.setPesquisaAtiva(false);
	}
	
	@Override
	public Long recuperarCount() {		
		return this.faturamentoFacade.listarFatItensProcedHospitalarCount(descricao, iph, codigoSus, codigoTabela.getSeq());
	}

	@Override
	public List<FatItensProcedHospitalar> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		List<FatItensProcedHospitalar> lista = new ArrayList<FatItensProcedHospitalar>();

		if(ordenar) {
			orderProperty = FatItensProcedHospitalar.Fields.SEQ_ORDER.toString();
			asc = true;
			ordenar = false;
		}
		
		try {
			lista = this.faturamentoFacade.listarFatItensProcedHospitalar(firstResult, maxResult, orderProperty, asc, descricao, iph, codigoSus, codigoTabela.getSeq());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return lista;
	}
	
	/**
	 * Método para pesquisar FAT_PROCEDIMENTOS_HOSPITALARES na suggestion da tela
	 * 
	 * @return
	 */
	public List<FatProcedimentosHospitalares> pesquisarFatProcedimentosHospitalares(String param) {
		return this.returnSGWithCount(faturamentoFacade.listarProcedimentosHospitalaresPorSeqEDescricaoOrdenado(param, FatProcedimentosHospitalares.Fields.SEQ.toString()),pesquisarFatProcedimentosHospitalaresCount(param));
	}
	
	public Long pesquisarFatProcedimentosHospitalaresCount(String param) {
		return faturamentoFacade.listarProcedimentosHospitalaresPorSeqEDescricaoCount(param);
	}	
	
	
	/************* GETTERS AND SETTERS ******************/
	public Long getCodigoSus() {
		return codigoSus;
	}

	public void setCodigoSus(Long codigoSus) {
		this.codigoSus = codigoSus;
	}

	public Integer getIph() {
		return iph;
	}

	public void setIph(Integer iph) {
		this.iph = iph;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricaoTabela() {
		return descricaoTabela;
	}

	public void setDescricaoTabela(String descricaoTabela) {
		this.descricaoTabela = descricaoTabela;
	}

	public FatProcedimentosHospitalares getCodigoTabela() {
		return codigoTabela;
	}

	public void setCodigoTabela(FatProcedimentosHospitalares codigoTabela) {
		this.codigoTabela = codigoTabela;
	}

	public DynamicDataModel<FatItensProcedHospitalar> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatItensProcedHospitalar> dataModel) {
		this.dataModel = dataModel;
	}

	public FatItensProcedHospitalar getProcedimentoSelecionado() {
		return procedimentoSelecionado;
	}

	public void setProcedimentoSelecionado(
			FatItensProcedHospitalar procedimentoSelecionado) {
		this.procedimentoSelecionado = procedimentoSelecionado;
	}
	
	
}
