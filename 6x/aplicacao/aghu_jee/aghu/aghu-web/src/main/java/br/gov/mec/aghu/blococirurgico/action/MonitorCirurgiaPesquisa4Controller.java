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
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaConcluidaHojeVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;

public class MonitorCirurgiaPesquisa4Controller extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(MonitorCirurgiaPesquisa4Controller.class);
	
	private static final long serialVersionUID = -2526895431549990627L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@Inject
	private MonitorCirurgiaPesquisa1Controller monitorCirurgiaPesquisa1Controller;
	
	private Short unfSeq; // Parâmetro da unidade funcional executora de cirurgias

	private Integer totalRegistrosEncontrados; // Total de registros encontrados na consulta principal de cada tela do monitor
	private List<List<MonitorCirurgiaConcluidaHojeVO>> listasPaginadas; // Resultado EM LISTAS da divisão do total de registros encontrados na consulta principal
	private Integer totalListasPaginadas; // Total de listas paginadas através do resultado da consulta principal
	private List<MonitorCirurgiaConcluidaHojeVO> listaAtual; // Lista atual/selecionada nas listas paginadas
	private Integer indiceListaAtual; // Índice da lista atual/selecionada nas listas paginadas

	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {
		try {
			inicializarCampos();

			List<MonitorCirurgiaConcluidaHojeVO> resultadoConsulta = this.blocoCirurgicoFacade.pesquisarMonitorCirurgiaConcluidaHoje(this.getUnfSeq());
			setTotalRegistrosEncontrados(resultadoConsulta.size());

			setListasPaginadas(this.blocoCirurgicoFacade.obterListaResultadoPaginado(resultadoConsulta));
			setTotalListasPaginadas(this.listasPaginadas.size());
			
			if (getTotalRegistrosEncontrados() == 0) {
				this.setListaAtual(resultadoConsulta);
				setIndiceListaAtual(getIndiceListaAtual() + 1);
			} else {
				this.pesquisarAutomaticamente();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String getMensagemTotaisMonitor4() {
		if (this.totalRegistrosEncontrados > 0) {
			return this.totalRegistrosEncontrados + " registros encontrados. Página " + this.indiceListaAtual + " de " + this.totalListasPaginadas + ".";
		}
		return "Não há cirurgias finalizadas até o momento.";
	}

	public void inicializarCampos() {
		this.setIndiceListaAtual(0);
		this.setTotalRegistrosEncontrados(0);
		this.setTotalListasPaginadas(0);
		this.setListaAtual(new LinkedList<MonitorCirurgiaConcluidaHojeVO>());
	}

	public void pesquisarAutomaticamente() {
		if (getIndiceListaAtual() > (getTotalListasPaginadas() - 1)) {
			monitorCirurgiaPesquisa1Controller.inicio();
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("monitorCirurgiaPesquisa1.xhtml?cid="+super.conversation.getId());
			} catch (IOException e) {
				LOG.error("A página monitorCirurgiaPesquisa1.xhtml não foi encontrada: ", e);
			}
		} else {
			setListaAtual(this.listasPaginadas.get(getIndiceListaAtual()));
			setIndiceListaAtual(getIndiceListaAtual() + 1);
		}
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
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

	public List<MonitorCirurgiaConcluidaHojeVO> getListaAtual() {
		return listaAtual;
	}

	public void setListaAtual(List<MonitorCirurgiaConcluidaHojeVO> listaAtual) {
		this.listaAtual = listaAtual;
	}

	public Integer getIndiceListaAtual() {
		return indiceListaAtual;
	}

	public void setIndiceListaAtual(Integer indiceListaAtual) {
		this.indiceListaAtual = indiceListaAtual;
	}

	public List<List<MonitorCirurgiaConcluidaHojeVO>> getListasPaginadas() {
		return listasPaginadas;
	}

	public void setListasPaginadas(
			List<List<MonitorCirurgiaConcluidaHojeVO>> listasPaginadas) {
		this.listasPaginadas = listasPaginadas;
	}

}