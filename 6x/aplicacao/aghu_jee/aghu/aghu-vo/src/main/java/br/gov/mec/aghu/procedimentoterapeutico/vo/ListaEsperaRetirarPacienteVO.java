package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ListaEsperaRetirarPacienteVO implements BaseBean {		
		
	private static final long serialVersionUID = -5565224614384825391L;
	
	private Date dtSugerida;
	private Short ciclo;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer serMatriculaValida;
	private Short serVinCodigoValida;	
	private String responsavel1;
	private String responsavel2;
	private Integer cloSeq;	
	private Integer lote;
	private Date dtPrescricao;	
	private Integer pacCodigo;
	private Integer prontuario;
	private String nomePaciente;
	private String nomeEspecialidade;

	public Short getCiclo() {
		return ciclo;
	}

	public void setCiclo(Short ciclo) {
		this.ciclo = ciclo;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public String getResponsavel1() {
		return responsavel1;
	}

	public void setResponsavel1(String responsavel1) {
		this.responsavel1 = responsavel1;
	}

	public String getResponsavel2() {
		return responsavel2;
	}

	public void setResponsavel2(String responsavel2) {
		this.responsavel2 = responsavel2;
	}

	public Integer getSerMatriculaValida() {
		return serMatriculaValida;
	}

	public void setSerMatriculaValida(Integer serMatriculaValida) {
		this.serMatriculaValida = serMatriculaValida;
	}

	public Short getSerVinCodigoValida() {
		return serVinCodigoValida;
	}

	public void setSerVinCodigoValida(Short serVinCodigoValida) {
		this.serVinCodigoValida = serVinCodigoValida;
	}

	public Integer getCloSeq() {
		return cloSeq;
	}

	public void setCloSeq(Integer cloSeq) {
		this.cloSeq = cloSeq;
	}

	public Integer getLote() {
		return lote;
	}

	public void setLote(Integer lote) {
		this.lote = lote;
	}

	public Date getDtPrescricao() {
		return dtPrescricao;
	}

	public void setDtPrescricao(Date dtPrescricao) {
		this.dtPrescricao = dtPrescricao;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
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

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public Date getDtSugerida() {
		return dtSugerida;
	}

	public void setDtSugerida(Date dtSugerida) {
		this.dtSugerida = dtSugerida;
	}	
}
