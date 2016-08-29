package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSexo;

/**
 * #1000 Prescrição: Visualizar Dados da Solicitação de Consultoria
 * 
 * @author aghu
 *
 */
public class VisualizaDadosSolicitacaoConsultoriaVO {

	/*
	 * Dados do ID de MPM_SOLICITACAO_CONSULTORIAS
	 */
	private Integer idAtdSeq;
	private Integer idScnSeq;
	
	/*
	 * Dados da tela
	 */
	private Integer prontuario; // C1.PRONTUARIO
	private String nomePaciente; // C1.NOME_PACIENTE
	private Date dataNascimento; // C1.DT_NASCIMENTO
	private DominioSexo sexo; // C1.SEXO
	private String idade; // RF02 em MPMC_IDA_ANO_MES_DIA
	private String localizacao; // RF03 em MPMC_LOCAL_PAC
	private Date dataAtendimento; // RF04 em MPMC_VER_DT_INI_ATD
	private String clinica; // C1.CLINICA
	private String especialidadeAtendimento; // C1.ESPECIALIDADE_ATENDIMENTO
	private String equipe; // C1.EQUIPE
	private String siglaEspecialidade; // C1.ESP_SIGLA_SOLICITACAO
	private String especialidade; // C1.ESP_SIGLA_SOLICITACAO – C1.ESPECIALIDADE_SOLICITACAO”
	private Date dataSolicitacao; // C1.DTHR_SOLICITADA
	private Integer prescricao; // C1.PME_SEQ
	private String solicitante; // C1.SOLICITANTE
	private String conselho; // C1.SOLIC_CONSELHO
	private String numeroConselho; // C1.SOLIC_CONS_NUMERO
	private String descricao; // C1.MOTIVO
	
	/**
	 * Dados auxiliares utilizados nas funcitons e consultas externas (ON)
	 */
	private Integer internacao; // INT_SEQ para MPMC_VER_DT_INI_ATD
	private Integer atendimentoUrgencia; // ATU_SEQ para MPMC_VER_DT_INI_ATD
	
	private Integer vcsMatricula; // ATD2.SER_VIN_CODIGO
	private Short vcsVinculo; // ATD2.SER_VIN_CODIGO
	
	private Integer vcs2Matricula; // SCN.SER_MATRICULA_VALIDA
	private Short vcs2Vinculo; // SCN.SER_VIN_CODIGO_VALIDA

	public Integer getIdAtdSeq() {
		return idAtdSeq;
	}

	public void setIdAtdSeq(Integer idAtdSeq) {
		this.idAtdSeq = idAtdSeq;
	}

	public Integer getIdScnSeq() {
		return idScnSeq;
	}

	public void setIdScnSeq(Integer idScnSeq) {
		this.idScnSeq = idScnSeq;
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

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public DominioSexo getSexo() {
		return sexo;
	}

	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}

	public String getClinica() {
		return clinica;
	}

	public void setClinica(String clinica) {
		this.clinica = clinica;
	}

	public String getEspecialidadeAtendimento() {
		return especialidadeAtendimento;
	}

	public void setEspecialidadeAtendimento(String especialidadeAtendimento) {
		this.especialidadeAtendimento = especialidadeAtendimento;
	}

	public String getEquipe() {
		return equipe;
	}

	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}

	/**
	 * Alterado conforme a documentação de análise
	 * 
	 * @return
	 */
	public String getEspecialidade() {
		return this.siglaEspecialidade.concat(" - ").concat(this.especialidade);
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public Integer getPrescricao() {
		return prescricao;
	}

	public void setPrescricao(Integer prescricao) {
		this.prescricao = prescricao;
	}

	public String getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(String solicitante) {
		this.solicitante = solicitante;
	}

	public String getConselho() {
		return conselho;
	}

	public void setConselho(String conselho) {
		this.conselho = conselho;
	}

	public String getNumeroConselho() {
		return numeroConselho;
	}

	public void setNumeroConselho(String numeroConselho) {
		this.numeroConselho = numeroConselho;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getInternacao() {
		return internacao;
	}
	public void setInternacao(Integer internacao) {
		this.internacao = internacao;
	}
	
	public Integer getAtendimentoUrgencia() {
		return atendimentoUrgencia;
	}
	
	public void setAtendimentoUrgencia(Integer atendimentoUrgencia) {
		this.atendimentoUrgencia = atendimentoUrgencia;
	}
	

	public Integer getVcsMatricula() {
		return vcsMatricula;
	}

	public void setVcsMatricula(Integer vcsMatricula) {
		this.vcsMatricula = vcsMatricula;
	}

	public Short getVcsVinculo() {
		return vcsVinculo;
	}

	public void setVcsVinculo(Short vcsVinculo) {
		this.vcsVinculo = vcsVinculo;
	}

	public Integer getVcs2Matricula() {
		return vcs2Matricula;
	}

	public void setVcs2Matricula(Integer vcs2Matricula) {
		this.vcs2Matricula = vcs2Matricula;
	}

	public Short getVcs2Vinculo() {
		return vcs2Vinculo;
	}

	public void setVcs2Vinculo(Short vcs2Vinculo) {
		this.vcs2Vinculo = vcs2Vinculo;
	}

	public enum Fields {
		ID_ATD_SEQ("idAtdSeq"), 
		ID_SCN_SEQ("idScnSeq"), 
		PRONTUARIO("prontuario"), 
		NOME_PACIENTE("nomePaciente"), 
		DATA_NASCIMENTO("dataNascimento"), 
		SEXO("sexo"), 
		IDADE("idade"), 
		LOCALIZACAO("localizacao"), 
		DATA_ATENDIMENTO("dataAtendimento"), 
		CLINICA("clinica"), 
		ESPECIALIDADE_ATENDIMENTO("especialidadeAtendimento"), 
		EQUIPE("equipe"), 
		SIGLA_ESPECIALIDADE("siglaEspecialidade"),
		ESPECIALIDADE("especialidade"), 
		DATA_SOLICITACAO("dataSolicitacao"), 
		PRESCRICAO("prescricao"), 
		SOLICITANTE("solicitante"), 
		CONSELHO("conselho"), 
		NUMERO_CONSELHO("numeroConselho"), 
		DESCRICAO("descricao"),
		INTERNACAO("internacao"),
		ATENDIMENTO_URGENCIA("atendimentoUrgencia"),
		VCS_MATRICULA("vcsMatricula"),
		VCS_VINCULO("vcsVinculo"),
		VCS2_MATRICULA("vcs2Matricula"),
		VCS2_VINCULO("vcs2Vinculo");

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