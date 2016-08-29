package br.gov.mec.aghu.exames.agendamento.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoTransporte;

public class RelatorioAgendaPorUnidadeVO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2349952462196429602L;
	private Date hora;
	private String sala;
	private Integer prontuario;
	private String nomePaciente;
	private String localizacao;
	private Integer solicitacao;
	private String descricaoExame;
	private Boolean o2;
	private DominioTipoTransporte tipoTransporte;
	private String transporte;
	
	public RelatorioAgendaPorUnidadeVO(){
	}
	
	public RelatorioAgendaPorUnidadeVO(
			Date hora, String sala, Integer prontuario, String nomePaciente,
			Integer solicitacao, String descricaoExame, Boolean o2, DominioTipoTransporte tipoTransporte
			){
		super();
		this.hora = hora;
		this.sala = sala;
		this.prontuario = prontuario;
		this.nomePaciente = nomePaciente;
		this.solicitacao = solicitacao;
		this.descricaoExame = descricaoExame;
		this.o2 = o2;
		this.tipoTransporte = tipoTransporte;
		if(tipoTransporte!=null) {
			this.transporte = tipoTransporte.getDescricao();
		}
	}
	
	public String getSala() {
		return sala;
	}
	
	public void setSala(String sala) {
		this.sala = sala;
	}

	public Date getHora() {
		return hora;
	}

	public void setHora(Date hora) {
		this.hora = hora;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public Integer getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}

	public String getDescricaoExame() {
		return descricaoExame;
	}

	public void setDescricaoExame(String descricaoExame) {
		this.descricaoExame = descricaoExame;
	}

	public Boolean getO2() {
		return o2;
	}

	public void setO2(Boolean o2) {
		this.o2 = o2;
	}

	public String getTransporte() {
		return transporte;
	}

	public void setTransporte(String transporte) {
		this.transporte = transporte;
	}

	public void setTipoTransporte(DominioTipoTransporte tipoTransporte) {
		this.tipoTransporte = tipoTransporte;
	}

	public DominioTipoTransporte getTipoTransporte() {
		return tipoTransporte;
	}
	
}