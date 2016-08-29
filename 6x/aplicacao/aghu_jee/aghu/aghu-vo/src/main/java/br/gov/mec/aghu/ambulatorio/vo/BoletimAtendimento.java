package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class BoletimAtendimento implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4188012323557082071L;

	private Integer numeroConsulta;
	private String dataConsulta;
	private String horaConsulta;
	private String diaConsulta;
	private Short serVinCodigoConsultado;
	private Integer serMatriculaConsultado;
	private String descricaoCentroCusto;
	private String informacoesConsulta;
	private String nomeReduzidoEspecialidade;
	private String nomeEquipe;
	private String siglaSala;
	private String descricaoSala;
	private String sala;
	private String descricaoEspecialidade;
	private Integer codigoPaciente;
	private Integer prontuario;
	private String nomePaciente;
	private Integer seqGrade;
	private String nomeUsuario;
	private String infoExcedeProgramacao;
	private String profissional;
	
	public BoletimAtendimento() {
		super();
	}

	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	public String getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(String dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public String getHoraConsulta() {
		return horaConsulta;
	}

	public void setHoraConsulta(String horaConsulta) {
		this.horaConsulta = horaConsulta;
	}

	public String getDiaConsulta() {
		return diaConsulta;
	}

	public void setDiaConsulta(String diaConsulta) {
		this.diaConsulta = diaConsulta;
	}

	public Short getSerVinCodigoConsultado() {
		return serVinCodigoConsultado;
	}

	public void setSerVinCodigoConsultado(Short serVinCodigoConsultado) {
		this.serVinCodigoConsultado = serVinCodigoConsultado;
	}

	public Integer getSerMatriculaConsultado() {
		return serMatriculaConsultado;
	}

	public void setSerMatriculaConsultado(Integer serMatriculaConsultado) {
		this.serMatriculaConsultado = serMatriculaConsultado;
	}

	public String getDescricaoCentroCusto() {
		return descricaoCentroCusto;
	}

	public void setDescricaoCentroCusto(String descricaoCentroCusto) {
		this.descricaoCentroCusto = descricaoCentroCusto;
	}

	public String getInformacoesConsulta() {
		return informacoesConsulta;
	}

	public void setInformacoesConsulta(String informacoesConsulta) {
		this.informacoesConsulta = informacoesConsulta;
	}

	public String getNomeReduzidoEspecialidade() {
		return nomeReduzidoEspecialidade;
	}

	public void setNomeReduzidoEspecialidade(String nomeReduzidoEspecialidade) {
		this.nomeReduzidoEspecialidade = nomeReduzidoEspecialidade;
	}

	public String getNomeEquipe() {
		return nomeEquipe;
	}

	public void setNomeEquipe(String nomeEquipe) {
		this.nomeEquipe = nomeEquipe;
	}

	public String getSiglaSala() {
		return siglaSala;
	}

	public void setSiglaSala(String siglaSala) {
		this.siglaSala = siglaSala;
	}

	public String getDescricaoSala() {
		return descricaoSala;
	}

	public void setDescricaoSala(String descricaoSala) {
		this.descricaoSala = descricaoSala;
	}

	public String getSala() {
		return sala;
	}

	public void setSala(String sala) {
		this.sala = sala;
	}

	public String getDescricaoEspecialidade() {
		return descricaoEspecialidade;
	}

	public void setDescricaoEspecialidade(String descricaoEspecialidade) {
		this.descricaoEspecialidade = descricaoEspecialidade;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
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

	public Integer getSeqGrade() {
		return seqGrade;
	}

	public void setSeqGrade(Integer seqGrade) {
		this.seqGrade = seqGrade;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public String getInfoExcedeProgramacao() {
		return infoExcedeProgramacao;
	}

	public void setInfoExcedeProgramacao(String infoExcedeProgramacao) {
		this.infoExcedeProgramacao = infoExcedeProgramacao;
	}

	public String getProfissional() {
		return profissional;
	}

	public void setProfissional(String profissional) {
		this.profissional = profissional;
	}
}
