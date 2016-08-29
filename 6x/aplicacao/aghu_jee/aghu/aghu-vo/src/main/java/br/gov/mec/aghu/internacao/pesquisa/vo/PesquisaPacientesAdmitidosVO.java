package br.gov.mec.aghu.internacao.pesquisa.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class PesquisaPacientesAdmitidosVO implements BaseBean {

	private String prontuario;
	private String nomePaciente;
	private String nomeProfessor;
	private String local;
	private String clinica;
	private String nomeEspecialidade;
	private String nomeEspecialidadeReduzido;
	private String convenioPlano;
	private String dataInternacao;
	private String dataAlta;
	private String origemPaciente;
	private String nomeInstituicaoHospital;
	private String codigoTipoAltaMedica;
	private String descricaoTipoAltaMedica;
	private String nomeInstituicaoHospitalar;
	private Integer codigoPaciente;
	private Integer seqInternacao;
	
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public String getNomeProfessor() {
		return nomeProfessor;
	}
	public void setNomeProfessor(String nomeProfessor) {
		this.nomeProfessor = nomeProfessor;
	}
	public String getClinica() {
		return clinica;
	}
	public void setClinica(String clinica) {
		this.clinica = clinica;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}
	
	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}	
	public String getNomeEspecialidadeReduzido() {
		return nomeEspecialidadeReduzido;
	}
	public void setNomeEspecialidadeReduzido(String nomeEspecialidadeReduzido) {
		this.nomeEspecialidadeReduzido = nomeEspecialidadeReduzido;
	}
	public String getConvenioPlano() {
		return convenioPlano;
	}
	public void setConvenioPlano(String convenioPlano) {
		this.convenioPlano = convenioPlano;
	}
	public String getDataInternacao() {
		return dataInternacao;
	}
	public void setDataInternacao(String dataInternacao) {
		this.dataInternacao = dataInternacao;
	}
	public String getDataAlta() {
		return dataAlta;
	}
	public void setDataAlta(String dataAlta) {
		this.dataAlta = dataAlta;
	}
	public String getOrigemPaciente() {
		return origemPaciente;
	}
	public void setOrigemPaciente(String origemPaciente) {
		this.origemPaciente = origemPaciente;
	}
	public String getNomeInstituicaoHospital() {
		return nomeInstituicaoHospital;
	}
	public void setNomeInstituicaoHospital(String nomeInstituicaoHospital) {
		this.nomeInstituicaoHospital = nomeInstituicaoHospital;
	}
	public String getCodigoTipoAltaMedica() {
		return codigoTipoAltaMedica;
	}
	public void setCodigoTipoAltaMedica(String codigoTipoAltaMedica) {
		this.codigoTipoAltaMedica = codigoTipoAltaMedica;
	}
	public String getDescricaoTipoAltaMedica() {
		return descricaoTipoAltaMedica;
	}
	public void setDescricaoTipoAltaMedica(String descricaoTipoAltaMedica) {
		this.descricaoTipoAltaMedica = descricaoTipoAltaMedica;
	}
	public String getNomeInstituicaoHospitalar() {
		return nomeInstituicaoHospitalar;
	}
	public void setNomeInstituicaoHospitalar(String nomeInstituicaoHospitalar) {
		this.nomeInstituicaoHospitalar = nomeInstituicaoHospitalar;
	}
	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}
	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public Integer getSeqInternacao() {
		return seqInternacao;
	}
	public void setSeqInternacao(Integer seqInternacao) {
		this.seqInternacao = seqInternacao;
	}
		
}
