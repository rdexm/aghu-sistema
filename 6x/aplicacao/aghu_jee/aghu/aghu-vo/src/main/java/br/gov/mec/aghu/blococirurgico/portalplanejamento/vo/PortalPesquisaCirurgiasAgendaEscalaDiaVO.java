package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PortalPesquisaCirurgiasAgendaEscalaDiaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8006983284365767482L;
	
	private String dataAgenda[] = new String[3];
	private Date dataAgendaDate[] = new Date[3];
	private List<PortalPesquisaCirurgiasAgendaEscalaVO> primeiroDia = new ArrayList<PortalPesquisaCirurgiasAgendaEscalaVO>();
	private List<PortalPesquisaCirurgiasAgendaEscalaVO> segundoDia = new ArrayList<PortalPesquisaCirurgiasAgendaEscalaVO>();
	private List<PortalPesquisaCirurgiasAgendaEscalaVO> terceiroDia = new ArrayList<PortalPesquisaCirurgiasAgendaEscalaVO>();
	
	public String[] getDataAgenda() {
		return dataAgenda;
	}
	public void setDataAgenda(String[] dataAgenda) {
		this.dataAgenda = dataAgenda;
	}
	public Date[] getDataAgendaDate() {
		return dataAgendaDate;
	}
	public void setDataAgendaDate(Date[] dataAgendaDate) {
		this.dataAgendaDate = dataAgendaDate;
	}
	public List<PortalPesquisaCirurgiasAgendaEscalaVO> getPrimeiroDia() {
		return primeiroDia;
	}
	public void setPrimeiroDia(
			List<PortalPesquisaCirurgiasAgendaEscalaVO> primeiroDia) {
		this.primeiroDia = primeiroDia;
	}
	public List<PortalPesquisaCirurgiasAgendaEscalaVO> getSegundoDia() {
		return segundoDia;
	}
	public void setSegundoDia(List<PortalPesquisaCirurgiasAgendaEscalaVO> segundoDia) {
		this.segundoDia = segundoDia;
	}
	public List<PortalPesquisaCirurgiasAgendaEscalaVO> getTerceiroDia() {
		return terceiroDia;
	}
	public void setTerceiroDia(
			List<PortalPesquisaCirurgiasAgendaEscalaVO> terceiroDia) {
		this.terceiroDia = terceiroDia;
	}
}
