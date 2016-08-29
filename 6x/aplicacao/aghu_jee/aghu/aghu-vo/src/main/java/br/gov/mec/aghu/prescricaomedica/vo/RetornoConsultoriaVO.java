package br.gov.mec.aghu.prescricaomedica.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class RetornoConsultoriaVO implements BaseBean {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5639376812692208794L;
	private String titulo;
	private String especialidadeDescricao;	//1
	private String siglaEspecialidade;		//2
	private String nomeSolicitante;			//3
	private String dataHoraSolicitacao;		//5
	private String prescricao;				//6
	//paciente
	private String prontuario;				//7
	private String nomePaciente;			//8
	private String dataNascimento;			//9
	private String idade;					//10
	private String sexo;					//11
	private String localizacao;				//12
		
	private String dataUltimaInternacao;	//13
	private String clinicaDescricao;		//14
	private String diagnostico;				//15
	private String motivo;					//16
	private String dataHoraConhecimento;	//17
	
	//campos 20 a 26 - footer da pagina
	private String resposta;				//18
	private String assinaturaResponsavel;	//19
	private String nomePacienteFormatado;	//21
	private String localizacaoFormatado;	//22
	private String prontuarioFormatado;		//23
	private String nomeMae;					//24
	private String prontuarioMae;			//25
	private String dataHoraEmissao;			//26	
	
	public String getEspecialidadeDescricao() {
		return especialidadeDescricao;
	}
	public void setEspecialidadeDescricao(String especialidadeDescricao) {
		this.especialidadeDescricao = especialidadeDescricao;
	}
	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}
	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}
	public String getNomeSolicitante() {
		return nomeSolicitante;
	}
	public void setNomeSolicitante(String nomeSolicitante) {
		this.nomeSolicitante = nomeSolicitante;
	}
	public String getDataHoraSolicitacao() {
		return dataHoraSolicitacao;
	}
	public void setDataHoraSolicitacao(String dataHoraSolicitacao) {
		this.dataHoraSolicitacao = dataHoraSolicitacao;
	}
	public String getPrescricao() {
		return prescricao;
	}
	public void setPrescricao(String prescricao) {
		this.prescricao = prescricao;
	}
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
	public String getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public String getIdade() {
		return idade;
	}
	public void setIdade(String idade) {
		this.idade = idade;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getLocalizacao() {
		return localizacao;
	}
	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}
	public String getDataUltimaInternacao() {
		return dataUltimaInternacao;
	}
	public void setDataUltimaInternacao(String dataUltimaInternacao) {
		this.dataUltimaInternacao = dataUltimaInternacao;
	}
	public String getClinicaDescricao() {
		return clinicaDescricao;
	}
	public void setClinicaDescricao(String clinicaDescricao) {
		this.clinicaDescricao = clinicaDescricao;
	}
	public String getDiagnostico() {
		return diagnostico;
	}
	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}
	public String getMotivo() {
		return motivo;
	}
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	public String getDataHoraConhecimento() {
		return dataHoraConhecimento;
	}
	public void setDataHoraConhecimento(String dataHoraConhecimento) {
		this.dataHoraConhecimento = dataHoraConhecimento;
	}
	public String getResposta() {
		return resposta;
	}
	public void setResposta(String resposta) {
		this.resposta = resposta;
	}
	public String getAssinaturaResponsavel() {
		return assinaturaResponsavel;
	}
	public void setAssinaturaResponsavel(String assinaturaResponsavel) {
		this.assinaturaResponsavel = assinaturaResponsavel;
	}
	public String getNomePacienteFormatado() {
		return nomePacienteFormatado;
	}
	public void setNomePacienteFormatado(String nomePacienteFormatado) {
		this.nomePacienteFormatado = nomePacienteFormatado;
	}
	public String getLocalizacaoFormatado() {
		return localizacaoFormatado;
	}
	public void setLocalizacaoFormatado(String localizacaoFormatado) {
		this.localizacaoFormatado = localizacaoFormatado;
	}
	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}
	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}
	public String getNomeMae() {
		return nomeMae;
	}
	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}
	public String getProntuarioMae() {
		return prontuarioMae;
	}
	public void setProntuarioMae(String prontuarioMae) {
		this.prontuarioMae = prontuarioMae;
	}
	public String getDataHoraEmissao() {
		return dataHoraEmissao;
	}
	public void setDataHoraEmissao(String dataHoraEmissao) {
		this.dataHoraEmissao = dataHoraEmissao;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
}