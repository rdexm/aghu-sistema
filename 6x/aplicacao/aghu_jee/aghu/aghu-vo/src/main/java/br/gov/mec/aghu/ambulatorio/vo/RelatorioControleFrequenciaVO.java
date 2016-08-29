package br.gov.mec.aghu.ambulatorio.vo;

import java.util.Date;

import br.gov.mec.aghu.core.persistence.BaseEntity;

public class RelatorioControleFrequenciaVO implements BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3743795150373550278L;
	
	private String tipoRelatorio;
	
	//Dados da UNIDADE
	private String nomeUnidade;
	private Integer numeroCgc;
	private Integer codigoUnidade;
	private String nomeMunicipioUnidade;
	private String siglaEstadoUnidade;
	private String telefoneUnidade;
	private String mesReferencia;
	
	//Dados do PACIENTE
	private String nomePaciente;
	private String cpfPaciente;
	private String nomeMaePaciente;
	private String enderecoPaciente;
	private String cepPaciente;
	private String telefonePaciente;
	private Date dataNascimento;
	private Character sexoPaciente;
	private Integer numeroCartaoSaude;

	//Dados da DECLARAÇÃO
	private Date dataProcedimento;
	private Integer codigoProcedimentoPrincipal;
	private String nomeProcedimentoPrincipal;
	
	//Dados da COMPROVAÇÃO
	private Integer numeroProntuario;

	public String getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(String tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public String getNomeUnidade() {
		return nomeUnidade;
	}

	public void setNomeUnidade(String nomeUnidade) {
		this.nomeUnidade = nomeUnidade;
	}

	public Integer getNumeroCgc() {
		return numeroCgc;
	}

	public void setNumeroCgc(Integer numeroCgc) {
		this.numeroCgc = numeroCgc;
	}

	public Integer getCodigoUnidade() {
		return codigoUnidade;
	}

	public void setCodigoUnidade(Integer codigoUnidade) {
		this.codigoUnidade = codigoUnidade;
	}

	public String getNomeMunicipioUnidade() {
		return nomeMunicipioUnidade;
	}

	public void setNomeMunicipioUnidade(String nomeMunicipioUnidade) {
		this.nomeMunicipioUnidade = nomeMunicipioUnidade;
	}

	public String getSiglaEstadoUnidade() {
		return siglaEstadoUnidade;
	}

	public void setSiglaEstadoUnidade(String siglaEstadoUnidade) {
		this.siglaEstadoUnidade = siglaEstadoUnidade;
	}

	public String getTelefoneUnidade() {
		return telefoneUnidade;
	}

	public void setTelefoneUnidade(String telefoneUnidade) {
		this.telefoneUnidade = telefoneUnidade;
	}

	public String getMesReferencia() {
		return mesReferencia;
	}

	public void setMesReferencia(String mesReferencia) {
		this.mesReferencia = mesReferencia;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getCpfPaciente() {
		return cpfPaciente;
	}

	public void setCpfPaciente(String cpfPaciente) {
		this.cpfPaciente = cpfPaciente;
	}

	public String getNomeMaePaciente() {
		return nomeMaePaciente;
	}

	public void setNomeMaePaciente(String nomeMaePaciente) {
		this.nomeMaePaciente = nomeMaePaciente;
	}

	public String getEnderecoPaciente() {
		return enderecoPaciente;
	}

	public void setEnderecoPaciente(String enderecoPaciente) {
		this.enderecoPaciente = enderecoPaciente;
	}

	public String getCepPaciente() {
		return cepPaciente;
	}

	public void setCepPaciente(String cepPaciente) {
		this.cepPaciente = cepPaciente;
	}

	public String getTelefonePaciente() {
		return telefonePaciente;
	}

	public void setTelefonePaciente(String telefonePaciente) {
		this.telefonePaciente = telefonePaciente;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Character getSexoPaciente() {
		return sexoPaciente;
	}

	public void setSexoPaciente(Character sexoPaciente) {
		this.sexoPaciente = sexoPaciente;
	}

	public Integer getNumeroCartaoSaude() {
		return numeroCartaoSaude;
	}

	public void setNumeroCartaoSaude(Integer numeroCartaoSaude) {
		this.numeroCartaoSaude = numeroCartaoSaude;
	}

	public Date getDataProcedimento() {
		return dataProcedimento;
	}

	public void setDataProcedimento(Date dataProcedimento) {
		this.dataProcedimento = dataProcedimento;
	}

	public Integer getCodigoProcedimentoPrincipal() {
		return codigoProcedimentoPrincipal;
	}

	public void setCodigoProcedimentoPrincipal(Integer codigoProcedimentoPrincipal) {
		this.codigoProcedimentoPrincipal = codigoProcedimentoPrincipal;
	}

	public String getNomeProcedimentoPrincipal() {
		return nomeProcedimentoPrincipal;
	}

	public void setNomeProcedimentoPrincipal(String nomeProcedimentoPrincipal) {
		this.nomeProcedimentoPrincipal = nomeProcedimentoPrincipal;
	}

	public Integer getNumeroProntuario() {
		return numeroProntuario;
	}

	public void setNumeroProntuario(Integer numeroProntuario) {
		this.numeroProntuario = numeroProntuario;
	}
}
