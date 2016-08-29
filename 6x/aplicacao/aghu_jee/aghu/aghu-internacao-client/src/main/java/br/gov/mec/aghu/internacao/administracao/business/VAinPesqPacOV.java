package br.gov.mec.aghu.internacao.administracao.business;

import java.util.Date;

/**
 * Os dados armazenados nesse objeto representam os parametros de detalhes de
 * internação
 * 
 * @author Stanley Araujo
 */

public class VAinPesqPacOV {

	private int seq;
	private int pacCodigo;
	private String nome;
	private String sexo;
	private String prontuario;
	private String nomeMae;
	private Date dataNascimento;
	private String dataHoraInternacao;
	private String ltoLtoId;
	private Short quartoNumero;
	private String descricaoQuarto;
	private Short unfSeq;
	private String unfDescricao;
	private String andarAlaDescricao;
	private Integer idade;
	private Date dataPrevisaoAlta;
	private Short codigoConvenio;
	private String descricaoConvenio;
	private String siglaEspecialidade;
	private String nomeEspecialidade;
	private String nomeProfessor;
	private String numeroRegistroConselho;
	private Short vinculoServidorCodigoGerado;
	private Integer matriculaServidorGerado;
	private String nomeServidorGerado;
	private String descricaoOrigemEventos;
	private String dataHoraAntendimentoUrgencia;
	private String dataHoraAltaAntendimentoUrgencia;
	private Integer seqAntendimentoUrgencia;
	

	public String getDescricaoOrigemEventos() {
		return descricaoOrigemEventos;
	}

	public void setDescricaoOrigemEventos(String descricaoOrigemEventos) {
		this.descricaoOrigemEventos = descricaoOrigemEventos;
	}

	public String getDataHoraAntendimentoUrgencia() {
		return dataHoraAntendimentoUrgencia;
	}

	public void setDataHoraAntendimentoUrgencia(String dataHoraAntendimentoUrgencia) {
		this.dataHoraAntendimentoUrgencia = dataHoraAntendimentoUrgencia;
	}

	public String getDataHoraAltaAntendimentoUrgencia() {
		return dataHoraAltaAntendimentoUrgencia;
	}

	public void setDataHoraAltaAntendimentoUrgencia(
			String dataHoraAltaAntendimentoUrgencia) {
		this.dataHoraAltaAntendimentoUrgencia = dataHoraAltaAntendimentoUrgencia;
	}

	public Short getVinculoServidorCodigoGerado() {
		return vinculoServidorCodigoGerado;
	}

	public void setVinculoServidorCodigoGerado(Short vinculoServidorCodigoGerado) {
		this.vinculoServidorCodigoGerado = vinculoServidorCodigoGerado;
	}

	public Integer getMatriculaServidorGerado() {
		return matriculaServidorGerado;
	}

	public void setMatriculaServidorGerado(Integer matriculaServidorGerado) {
		this.matriculaServidorGerado = matriculaServidorGerado;
	}

	public String getNomeServidorGerado() {
		return nomeServidorGerado;
	}

	public void setNomeServidorGerado(String nomeServidorGerado) {
		this.nomeServidorGerado = nomeServidorGerado;
	}

	public String getNomeProfessor() {
		return nomeProfessor;
	}

	public void setNomeProfessor(String nomeProfessor) {
		this.nomeProfessor = nomeProfessor;
	}

	public String getNumeroRegistroConselho() {
		return numeroRegistroConselho;
	}

	public void setNumeroRegistroConselho(String numeroRegistroConselho) {
		this.numeroRegistroConselho = numeroRegistroConselho;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public String getUnfDescricao() {
		return unfDescricao;
	}

	public void setUnfDescricao(String unfDescricao) {
		this.unfDescricao = unfDescricao;
	}

	public Short getCodigoConvenio() {
		return codigoConvenio;
	}

	public void setCodigoConvenio(Short codigoConvenio) {
		this.codigoConvenio = codigoConvenio;
	}

	public String getDescricaoConvenio() {
		return descricaoConvenio;
	}

	public void setDescricaoConvenio(String descricaoConvenio) {
		this.descricaoConvenio = descricaoConvenio;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(int pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getDataHoraInternacao() {
		return dataHoraInternacao;
	}

	public void setDataHoraInternacao(String datahoraInternacao) {
		this.dataHoraInternacao = datahoraInternacao;
	}

	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	public Short getQuartoNumero() {
		return quartoNumero;
	}

	public void setQuartoNumero(Short quartoNumero) {
		this.quartoNumero = quartoNumero;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public String getAndarAlaDescricao() {
		return andarAlaDescricao;
	}

	public void setAndarAlaDescricao(String andarAlaDescricao) {
		this.andarAlaDescricao = andarAlaDescricao;
	}

	public Integer getIdade() {
		return idade;
	}

	public void setIdade(Integer idade) {
		this.idade = idade;
	}

	public Date getDataPrevisaoAlta() {
		return dataPrevisaoAlta;
	}

	public void setDataPrevisaoAlta(Date dataPrevisaoAlta) {
		this.dataPrevisaoAlta = dataPrevisaoAlta;
	}

	public void setSeqAntendimentoUrgencia(Integer seqAntendimentoUrgencia) {
		this.seqAntendimentoUrgencia = seqAntendimentoUrgencia;
	}

	public Integer getSeqAntendimentoUrgencia() {
		return seqAntendimentoUrgencia;
	}

	public String getDescricaoQuarto() {
		return descricaoQuarto;
	}

	public void setDescricaoQuarto(String descricaoQuarto) {
		this.descricaoQuarto = descricaoQuarto;
	}

	

}
