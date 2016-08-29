package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;
import java.util.Date;


public class CodAtendimentoInformacaoPacienteVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8215481143774093305L;

	/**
	 * #5799
	 * 02/02/2014
	 */
	
	private Integer seqIfp; 
	
	private Integer atdSeqPme; 
	private Integer seqPme; 
	
	private Short seqUnf; 
	private Integer seqAtd; 
	
	private String descricaoIfp; 
	
	private Integer codigoPac;
	private String nomePac;
	private Integer prontuario;
	
	private Integer matriculaServidor; 
	private Short vinCodigoServidor; 
	
	private Integer matriculaServidorVerificado; 
	private Short vinCodigoServidorVerificado; 
	
	private Boolean indInfVerificada; 
	private Date dthrInfVerificada;
	
	private String prontuarioS;
	
	
	public Integer getSeqIfp() {
		return seqIfp;
	}
	public void setSeqIfp(Integer seqIfp) {
		this.seqIfp = seqIfp;
	}
	public Integer getAtdSeqPme() {
		return atdSeqPme;
	}
	public void setAtdSeqPme(Integer atdSeqPme) {
		this.atdSeqPme = atdSeqPme;
	}
	public Integer getSeqPme() {
		return seqPme;
	}
	public void setSeqPme(Integer seqPme) {
		this.seqPme = seqPme;
	}
	public Short getSeqUnf() {
		return seqUnf;
	}
	public void setSeqUnf(Short seqUnf) {
		this.seqUnf = seqUnf;
	}
	public Integer getSeqAtd() {
		return seqAtd;
	}
	public void setSeqAtd(Integer seqAtd) {
		this.seqAtd = seqAtd;
	}
	public String getDescricaoIfp() {
		return descricaoIfp;
	}
	public void setDescricaoIfp(String descricaoIfp) {
		this.descricaoIfp = descricaoIfp;
	}
	public Integer getCodigoPac() {
		return codigoPac;
	}
	public void setCodigoPac(Integer codigoPac) {
		this.codigoPac = codigoPac;
	}
	public String getNomePac() {
		return nomePac;
	}
	public void setNomePac(String nomePac) {
		this.nomePac = nomePac;
	}
	public Integer getMatriculaServidor() {
		return matriculaServidor;
	}
	public void setMatriculaServidor(Integer matriculaServidor) {
		this.matriculaServidor = matriculaServidor;
	}
	public Short getVinCodigoServidor() {
		return vinCodigoServidor;
	}
	public void setVinCodigoServidor(Short vinCodigoServidor) {
		this.vinCodigoServidor = vinCodigoServidor;
	}
	public Integer getMatriculaServidorVerificado() {
		return matriculaServidorVerificado;
	}
	public void setMatriculaServidorVerificado(Integer matriculaServidorVerificado) {
		this.matriculaServidorVerificado = matriculaServidorVerificado;
	}
	public Short getVinCodigoServidorVerificado() {
		return vinCodigoServidorVerificado;
	}
	public void setVinCodigoServidorVerificado(Short vinCodigoServidorVerificado) {
		this.vinCodigoServidorVerificado = vinCodigoServidorVerificado;
	}
	public Boolean getIndInfVerificada() {
		return indInfVerificada;
	}
	public void setIndInfVerificada(Boolean indInfVerificada) {
		this.indInfVerificada = indInfVerificada;
	}
	public Date getDthrInfVerificada() {
		return dthrInfVerificada;
	}
	public void setDthrInfVerificada(Date dthrInfVerificada) {
		this.dthrInfVerificada = dthrInfVerificada;
	}
	
	public enum Fields {
		SEQ_IFP("seqIfp"),
		DESCRICAO_IFP("descricaoIfp"),
		IND_INF_VERIFICADA("indInfVerificada"),
		DTHR_INF_VERIFICADA("dthrInfVerificada"),
		PME_ATD_SEQ("atdSeqPme"),
		PME_SEQ("seqPme"),
		UNF_SEQ("seqUnf"),
		ATD_SEQ("seqAtd"),
		SER_MATRICULA("matriculaServidor"),
		SER_VIN_CODIGO("vinCodigoServidor"),
		SER_MATRICULA_VERIF("matriculaServidorVerificado"),
		SER_VIN_CODIGO_VERIF("vinCodigoServidorVerificado"),
		CODIGO_PAC("codigoPac"),
		NOME_PAC("nomePac"),
		PRONTUARIO("prontuario");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getProntuarioS() {
		String primeiraParte = this.prontuario.toString().substring(0, this.prontuario.toString().length()-1);
		String segundaParte = this.prontuario.toString().substring(this.prontuario.toString().length()-1);
		prontuarioS = primeiraParte.concat("/").concat(segundaParte);		
		return prontuarioS;
	}
	public void setProntuarioS(String prontuarioS) {
		this.prontuarioS = prontuarioS;
	}
	
}