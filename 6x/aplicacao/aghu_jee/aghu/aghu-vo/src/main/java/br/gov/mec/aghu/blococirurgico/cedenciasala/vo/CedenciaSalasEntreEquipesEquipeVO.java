package br.gov.mec.aghu.blococirurgico.cedenciasala.vo;

import java.util.Date;

import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;

public class CedenciaSalasEntreEquipesEquipeVO {

	private Date data;
	private LinhaReportVO equipeSubstituta;
	private Boolean recorrencia;
	private Date dataFim;
	private Integer intervalo;
	private String usuarioLogado;
	
	public CedenciaSalasEntreEquipesEquipeVO() {
		super();
	}
	
	//Getters and Setters

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public LinhaReportVO getEquipeSubstituta() {
		return equipeSubstituta;
	}

	public void setEquipeSubstituta(LinhaReportVO equipeSubstituta) {
		this.equipeSubstituta = equipeSubstituta;
	}

	public Boolean getRecorrencia() {
		return recorrencia;
	}

	public void setRecorrencia(Boolean recorrencia) {
		this.recorrencia = recorrencia;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Integer getIntervalo() {
		return intervalo;
	}

	public void setIntervalo(Integer intervalo) {
		this.intervalo = intervalo;
	}

	public String getUsuarioLogado() {
		return usuarioLogado;
	}

	public void setUsuarioLogado(String usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}
}
