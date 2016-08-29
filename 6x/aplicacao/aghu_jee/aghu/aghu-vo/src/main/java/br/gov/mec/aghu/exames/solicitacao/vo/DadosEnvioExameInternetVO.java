package br.gov.mec.aghu.exames.solicitacao.vo;

import java.math.BigInteger;

import javax.xml.datatype.XMLGregorianCalendar;

public class DadosEnvioExameInternetVO {

	private String soeSeq;
	private BigInteger seqGrupo;
	private String descricaoGrupo;
	private String localizador;
	private BigInteger codigoPaciente;
	private XMLGregorianCalendar dataNascimentoPaciente;
	private String nomePaciente;
	private ConselhoProfissionalVO conselhoProfissionalMedico;
	private ConselhoProfissionalVO conselhoProfissionalResponsavel;
	private String convenio;
	private XMLGregorianCalendar dataLiberacaoExame;
	private XMLGregorianCalendar dataExame;
	private boolean notaAdicional;
	private String cnpjInstituicao;

	public String getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(String soeSeq) {
		this.soeSeq = soeSeq;
	}

	public BigInteger getSeqGrupo() {
		return seqGrupo;
	}

	public void setSeqGrupo(BigInteger seqGrupo) {
		this.seqGrupo = seqGrupo;
	}

	public String getLocalizador() {
		return localizador;
	}

	public void setLocalizador(String localizador) {
		this.localizador = localizador;
	}

	public BigInteger getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(BigInteger codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public XMLGregorianCalendar getDataNascimentoPaciente() {
		return dataNascimentoPaciente;
	}

	public void setDataNascimentoPaciente(
			XMLGregorianCalendar dataNascimentoPaciente) {
		this.dataNascimentoPaciente = dataNascimentoPaciente;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public ConselhoProfissionalVO getConselhoProfissionalMedico() {
		return conselhoProfissionalMedico;
	}

	public void setConselhoProfissionalMedico(
			ConselhoProfissionalVO conselhoProfissionalMedico) {
		this.conselhoProfissionalMedico = conselhoProfissionalMedico;
	}

	public ConselhoProfissionalVO getConselhoProfissionalResponsavel() {
		return conselhoProfissionalResponsavel;
	}

	public void setConselhoProfissionalResponsavel(
			ConselhoProfissionalVO conselhoProfissionalResponsavel) {
		this.conselhoProfissionalResponsavel = conselhoProfissionalResponsavel;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public XMLGregorianCalendar getDataLiberacaoExame() {
		return dataLiberacaoExame;
	}

	public void setDataLiberacaoExame(XMLGregorianCalendar dataLiberacaoExame) {
		this.dataLiberacaoExame = dataLiberacaoExame;
	}

	public XMLGregorianCalendar getDataExame() {
		return dataExame;
	}

	public void setDataExame(XMLGregorianCalendar dataExame) {
		this.dataExame = dataExame;
	}

	public boolean isNotaAdicional() {
		return notaAdicional;
	}

	public void setNotaAdicional(boolean notaAdicional) {
		this.notaAdicional = notaAdicional;
	}

	public String getCnpjInstituicao() {
		return cnpjInstituicao;
	}

	public void setCnpjInstituicao(String cnpjInstituicao) {
		this.cnpjInstituicao = cnpjInstituicao;
	}

	public String getDescricaoGrupo() {
		return descricaoGrupo;
	}

	public void setDescricaoGrupo(String descricaoGrupo) {
		this.descricaoGrupo = descricaoGrupo;
	}

}
