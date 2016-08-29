package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class PacientesEmSalaRecuperacaoVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6299726061528935506L;

	
	
	private Integer crgSeq; // C3 - MbcCirurgia.crgSeq
	private Integer prontuario; // C3 - MbcCirurgia.AipPacientes.prontuario
	private Integer pacCodigo; // C3 - MbcCirurgia.AipPacientes.codigo
	private String nomePaciente; // C3 - MbcCirurgia.AipPacientes.nome
	private Date dataEntradaSr; // c3 - MbcCirurgia.dataEntradaSr
	private Date dataSaidaSr; // c3 - MbcCirurgia.dataSaidaSr
	private String localizacao; // MBCC_LOCAL_AIP_PAC
	private String nomeCirurgiao; // C4 - MbcProfCirurgias.MbcProfAtuaUnidCirgs.RapServidores. RapPessoasFisicas.nome
	
	public enum Fields {

		CRG_SEQ("crgSeq"), 
		PRONTUARIO("prontuario"), 
		PAC_CODIGO("pacCodigo"), 
		NOME_PACIENTE("nomePaciente"), 
		DATA_ENTRADA_SR("dataEntradaSr"), 
		DTHR_SAIDA_SR("dataSaidaSr"),
		LOCALIZACAO("localizacao"),
		NOME_CIRURGIAO("nomeCirurgiao")
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}	
	
	public Integer getCrgSeq() {
		return crgSeq;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public String getLocalizacao() {
		return localizacao;
	}
	public Date getDataEntradaSr() {
		return dataEntradaSr;
	}
	public Date getDataSaidaSr() {
		return dataSaidaSr;
	}
	public String getNomeCirurgiao() {
		return nomeCirurgiao;
	}
	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}
	public void setDataEntradaSr(Date dataEntradaSr) {
		this.dataEntradaSr = dataEntradaSr;
	}
	public void setDataSaidaSr(Date dataSaidaSr) {
		this.dataSaidaSr = dataSaidaSr;
	}
	public void setNomeCirurgiao(String nomeCirurgiao) {
		this.nomeCirurgiao = nomeCirurgiao;
	}
	
}
