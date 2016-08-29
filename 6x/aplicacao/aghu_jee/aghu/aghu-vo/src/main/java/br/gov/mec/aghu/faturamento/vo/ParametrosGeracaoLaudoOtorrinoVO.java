package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;


public class ParametrosGeracaoLaudoOtorrinoVO {
	
	private Integer codigoPaciente;
	private Date dthrRealizado;
	private Integer seqProcedimentoHospitalarInterno;
	private Integer seqEquipe;
	private Integer conNumero;
	private String candidato;
	private Integer fatCandidatoApacSeq;

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}
	
	public Date getDthrRealizado() {
		return dthrRealizado;
	}

	public void setDthrRealizado(Date dthrRealizado) {
		this.dthrRealizado = dthrRealizado;
	}

	public Integer getSeqProcedimentoHospitalarInterno() {
		return seqProcedimentoHospitalarInterno;
	}

	public void setSeqProcedimentoHospitalarInterno(
			Integer seqProcedimentoHospitalarInterno) {
		this.seqProcedimentoHospitalarInterno = seqProcedimentoHospitalarInterno;
	}

	public Integer getSeqEquipe() {
		return seqEquipe;
	}

	public void setSeqEquipe(Integer seqEquipe) {
		this.seqEquipe = seqEquipe;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public String getCandidato() {
		return candidato;
	}

	public void setCandidato(String candidato) {
		this.candidato = candidato;
	}

	public Integer getFatCandidatoApacSeq() {
		return fatCandidatoApacSeq;
	}

	public void setFatCandidatoApacSeq(Integer fatCandidatoApacSeq) {
		this.fatCandidatoApacSeq = fatCandidatoApacSeq;
	}

	public enum Fields {
		
		CODIGO_PACIENTE("codigoPaciente"),
		DTHR_REALIZADO("dthrRealizado"), 
		SEQ_PHI("seqProcedimentoHospitalarInterno"), 
		SEQ_EQUIPE("seqEquipe"), 
		CON_NUMERO("conNumero"),
		CANDIDATO("candidato"),
		FAT_CAND_APAC_SEQ("fatCandidatoApacSeq")
		;

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
