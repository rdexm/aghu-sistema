package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class ImpressaoTicketAgendamentoVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1771195071390435356L;
	
	//C1
	private Short cloCiclo;
	private Short hrsDia;
	private Date hrsDataInicio;
	private Date hrsDataFim;
	private Integer sesCloSeq;
	
	//C2
	private Date cloDataPresvista;
	private Integer cloSeq;
	
	//C3
	private String ptcDescricao;
	private String ptaTitulo; 
	
	//C5
	private String tpsDescricao;
	private String salDescricao;
	private Short pteCiclo;
	//private Integer sesCloSeq;
	private String tpsAviso;
	private Short espSeq;
	private String espNomeReduzido;
	private Short agsSeq;

	
	public enum Fields {
		//C1
		CLO_CICLO("cloCiclo"),
		HRS_DIA("hrsDia"),
		HRS_DATA_INICIO("hrsDataInicio"),
		HRS_DATA_FIM("hrsDataFim"),
		SES_CLO_SEQ("sesCloSeq"),
		
		//C2
		CLO_DATA_PRESVISTA("cloDataPresvista"),
		CLO_SEQ("cloSeq"),
		
		//C3
		PTC_DESCRICAO("ptcDescricao"),
		PTA_TITULO("ptaTitulo"),
		
		//C5
		TPS_DESCRICAO("tpsDescricao"),
		SAL_DESCRICAO("salDescricao"),
		PTE_CICLO("pteCiclo"),
		TPS_AVISO("tpsAviso"),
		ESP_SEQ("espSeq"),
		ESP_NOME_REDUZIDO("espNomeReduzido"),
		AGS_SEQ("agsSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	//C1
	public Short getCloCiclo() {
		return cloCiclo;
	}
	public void setCloCiclo(Short cloCiclo) {
		this.cloCiclo = cloCiclo;
	}
	public Short getHrsDia() {
		return hrsDia;
	}
	public void setHrsDia(Short hrsDia) {
		this.hrsDia = hrsDia;
	}
	public Date getHrsDataInicio() {
		return hrsDataInicio;
	}
	public void setHrsDataInicio(Date hrsDataInicio) {
		this.hrsDataInicio = hrsDataInicio;
	}
	public Date getHrsDataFim() {
		return hrsDataFim;
	}
	public void setHrsDataFim(Date hrsDataFim) {
		this.hrsDataFim = hrsDataFim;
	}
	public Integer getSesCloSeq() {
		return sesCloSeq;
	}
	public void setSesCloSeq(Integer sesCloSeq) {
		this.sesCloSeq = sesCloSeq;
	}
	public String getPtcDescricao() {
		return ptcDescricao;
	}
	public void setPtcDescricao(String ptcDescricao) {
		this.ptcDescricao = ptcDescricao;
	}
	
	
	//C2
	public Date getCloDataPresvista() {
		return cloDataPresvista;
	}
	public void setCloDataPresvista(Date cloDataPresvista) {
		this.cloDataPresvista = cloDataPresvista;
	}
	public Integer getCloSeq() {
		return cloSeq;
	}
	public void setCloSeq(Integer cloSeq) {
		this.cloSeq = cloSeq;
	}
	
	
	//C3
	public String getPtaTitulo() {
		return ptaTitulo;
	}
	public void setPtaTitulo(String ptaTitulo) {
		this.ptaTitulo = ptaTitulo;
	}
	
	
	//C5
	public String getTpsDescricao() {
		return tpsDescricao;
	}
	public void setTpsDescricao(String tpsDescricao) {
		this.tpsDescricao = tpsDescricao;
	}
	public String getSalDescricao() {
		return salDescricao;
	}
	public void setSalDescricao(String salDescricao) {
		this.salDescricao = salDescricao;
	}
	public Short getPteCiclo() {
		return pteCiclo;
	}
	public void setPteCiclo(Short pteCiclo) {
		this.pteCiclo = pteCiclo;
	}
	public String getTpsAviso() {
		return tpsAviso;
	}
	public void setTpsAviso(String tpsAviso) {
		this.tpsAviso = tpsAviso;
	}
	public Short getEspSeq() {
		return espSeq;
	}
	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
	public String getEspNomeReduzido() {
		return espNomeReduzido;
	}
	public void setEspNomeReduzido(String espNomeReduzido) {
		this.espNomeReduzido = espNomeReduzido;
	}
	public Short getAgsSeq() {
		return agsSeq;
	}
	public void setAgsSeq(Short agsSeq) {
		this.agsSeq = agsSeq;
	}
	public String retornaLocalAtendimento() {
		return (this.tpsDescricao != null ? this.tpsDescricao + " - "  : StringUtils.EMPTY) + (this.salDescricao != null ? this.salDescricao : StringUtils.EMPTY);
	}
}
