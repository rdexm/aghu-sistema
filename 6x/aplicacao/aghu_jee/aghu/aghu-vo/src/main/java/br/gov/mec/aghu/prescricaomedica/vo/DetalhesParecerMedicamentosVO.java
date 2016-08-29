package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;

public class DetalhesParecerMedicamentosVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5381594494825257714L;

	private String complemento;
	private String parecer;
	private String medicamento;
	private String dose;
	private String aprazamento;
	private Short duracaoAprovada;
	private Short duracaoSolicitada;
	private String via;
	private String avaliador;
	private String sigla;
	private Date dataInicioTratamento;
	private String numero;
	private Integer pesCod;

	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public String getParecer() {
		return parecer;
	}
	public void setParecer(String parecer) {
		this.parecer = parecer;
	}
	public String getMedicamento() {
		return medicamento;
	}
	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}
	public String getDose() {
		return dose;
	}
	public void setDose(String dose) {
		this.dose = dose;
	}
	public String getAprazamento() {
		return aprazamento;
	}
	public void setAprazamento(String aprazamento) {
		this.aprazamento = aprazamento;
	}
	public Short getDuracaoAprovada() {
		return duracaoAprovada;
	}
	public void setDuracaoAprovada(Short duracaoAprovada) {
		this.duracaoAprovada = duracaoAprovada;
	}
	public Short getDuracaoSolicitada() {
		return duracaoSolicitada;
	}
	public void setDuracaoSolicitada(Short duracaoSolicitada) {
		this.duracaoSolicitada = duracaoSolicitada;
	}
	public String getVia() {
		return via;
	}
	public void setVia(String via) {
		this.via = via;
	}
	public String getAvaliador() {
		return avaliador;
	}
	public void setAvaliador(String avaliador) {
		this.avaliador = avaliador;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public Date getDataInicioTratamento() {
		return dataInicioTratamento;
	}
	public void setDataInicioTratamento(Date dataInicioTratamento) {
		this.dataInicioTratamento = dataInicioTratamento;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}


	public Integer getPesCod() {
		return pesCod;
	}

	public void setPesCod(Integer pesCod) {
		this.pesCod = pesCod;
	}


	public enum Fields{
		
		COMPLEMENTO("complemento"),
		PARECER("parecer"),
		MEDICAMENTO("medicamento"),
		DOSE("dose"),
		APRAZAMENTO("aprazamento"),
		DATA_INICIO_TRATAMENTO("dataInicioTratamento"),
		DURACAO_SOLICITADA("duracaoSolicitada"),
		DURACAO_APROVADA("duracaoAprovada"),
		VIA_ADM("via"),
		NRO_REG("numero"),
		AVALIADOR("avaliador"),
		PES_CODIGO("pesCod"),
		SIGLA("sigla");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}
