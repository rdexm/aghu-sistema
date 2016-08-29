package br.gov.mec.aghu.blococirurgico.action;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaSalaPreparoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;

public class MonitorCirurgiaPesquisa1Controller extends ActionController {

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(MonitorCirurgiaPesquisa1Controller.class);
	private static final long serialVersionUID = -1702775865245377880L;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@Inject
	private MonitorCirurgiaPesquisa2Controller monitorCirurgiaPesquisa2Controller;
	
	// Parâmetro de conversação
	private Short unfSeq; // Parâmetro da unidade funcional executora de cirurgias

	private Integer totalRegistrosEncontrados; // Total de registros encontrados na consulta principal de cada tela do monitor
	private List<List<MonitorCirurgiaSalaPreparoVO>> listasPaginadas; // Resultado EM LISTAS da divisão do total de registros encontrados na consulta principal
	private Integer totalListasPaginadas; // Total de listas paginadas através do resultado da consulta principal
	private List<MonitorCirurgiaSalaPreparoVO> listaAtual; // Lista atual/selecionada nas listas paginadas
	private Integer indiceListaAtual; // Índice da lista atual/selecionada nas listas paginadas

	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {
		try {
			inicializarCampos();
			List<MonitorCirurgiaSalaPreparoVO> resultadoConsulta = this.blocoCirurgicoFacade.pesquisarMonitorCirurgiaSalaPreparo(this.getUnfSeq());
			setTotalRegistrosEncontrados(resultadoConsulta.size());
			
			setListasPaginadas(this.blocoCirurgicoFacade.obterListaResultadoPaginado(resultadoConsulta));
			setTotalListasPaginadas(this.listasPaginadas.size());
			
			if (getTotalRegistrosEncontrados() == 0) {
				this.setListaAtual(resultadoConsulta);
				setIndiceListaAtual(getIndiceListaAtual() + 1);
			} else {
				pesquisarAutomaticamente();
			}
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void inicializarCampos() {
		this.setIndiceListaAtual(0);
		this.setTotalRegistrosEncontrados(0);
		this.setTotalListasPaginadas(0);
		this.setListaAtual(new LinkedList<MonitorCirurgiaSalaPreparoVO>());
	}
	
	public String getMensagemTotais() {
		if (this.totalRegistrosEncontrados > 0) {
			return this.totalRegistrosEncontrados + " registros encontrados. Página " + this.indiceListaAtual + " de " + this.totalListasPaginadas + ".";
		}
		return "Nenhum resultado encontrado!";
	}
	
	public void pesquisarAutomaticamente() {
		if (getIndiceListaAtual() > (getTotalListasPaginadas() - 1)) {
			monitorCirurgiaPesquisa2Controller.setUnfSeq(getUnfSeq());
			monitorCirurgiaPesquisa2Controller.inicio();
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("monitorCirurgiaPesquisa2.xhtml?cid="+super.conversation.getId());
			} catch (IOException e) {
				LOG.error("A página monitorCirurgiaPesquisa2.xhtml não foi encontrada: ", e);
			}
		} else {
			setListaAtual(this.listasPaginadas.get(getIndiceListaAtual()));
			setIndiceListaAtual(getIndiceListaAtual() + 1);
		}
	}

	public Integer getTotalRegistrosEncontrados() {
		return totalRegistrosEncontrados;
	}

	public void setTotalRegistrosEncontrados(Integer totalRegistrosEncontrados) {
		this.totalRegistrosEncontrados = totalRegistrosEncontrados;
	}

	public Integer getTotalListasPaginadas() {
		return totalListasPaginadas;
	}

	public void setTotalListasPaginadas(Integer totalListasPaginadas) {
		this.totalListasPaginadas = totalListasPaginadas;
	}

	public Integer getIndiceListaAtual() {
		return indiceListaAtual;
	}

	public void setIndiceListaAtual(Integer indiceListaAtual) {
		this.indiceListaAtual = indiceListaAtual;
	}

	public List<MonitorCirurgiaSalaPreparoVO> getListaAtual() {
		return listaAtual;
	}

	public void setListaAtual(List<MonitorCirurgiaSalaPreparoVO> listaAtual) {
		this.listaAtual = listaAtual;
	}

	public List<List<MonitorCirurgiaSalaPreparoVO>> getListasPaginadas() {
		return listasPaginadas;
	}

	public void setListasPaginadas(
			List<List<MonitorCirurgiaSalaPreparoVO>> listasPaginadas) {
		this.listasPaginadas = listasPaginadas;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
}