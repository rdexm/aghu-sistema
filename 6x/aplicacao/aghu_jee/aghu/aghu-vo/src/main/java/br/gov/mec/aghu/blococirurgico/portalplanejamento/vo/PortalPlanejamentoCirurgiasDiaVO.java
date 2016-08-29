package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PortalPlanejamentoCirurgiasDiaVO implements Serializable {
	
	
	private static final long serialVersionUID = -3884231963862748532L;
	
	private String[] datasAgenda;
	private Date[] datasAgendaDate;
	private List<PortalPlanejamentoCirurgiasSalaVO> listaSalas;
	
	//Atributos da tela de detalhe
	private List<Date> horarios = new ArrayList<Date>();
	
	
	public String[] getDatasAgenda() {
		return datasAgenda;
	}
	public void setDatasAgenda(String[] datasAgenda) {
		this.datasAgenda = datasAgenda;
	}
	public List<PortalPlanejamentoCirurgiasSalaVO> getListaSalas() {
		return listaSalas;
	}
	public void setListaSalas(List<PortalPlanejamentoCirurgiasSalaVO> listaSalas) {
		this.listaSalas = listaSalas;
	}
	public Date[] getDatasAgendaDate() {
		return datasAgendaDate;
	}
	public void setDatasAgendaDate(Date[] datasAgendaDate) {
		this.datasAgendaDate = datasAgendaDate;
	}
	public List<Date> getHorarios() {
		return horarios;
	}
	public void setHorarios(List<Date> horarios) {
		this.horarios = horarios;
	}
}
