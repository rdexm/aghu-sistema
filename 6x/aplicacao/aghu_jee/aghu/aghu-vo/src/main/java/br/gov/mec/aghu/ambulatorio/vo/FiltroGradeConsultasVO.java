package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;

public class FiltroGradeConsultasVO implements Serializable {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 5118942095129212390L;
	private Integer grade;
	private Short setorSalaSeq;
	private Short espSeq;
	private Integer eqpSeq;
	private Short vinculo;
	private Integer matricula;
	private Boolean indProcedimento = Boolean.FALSE;
	private Date apartirDe;
	private Integer dia;
	private Date horario;
	private Integer quantidade;
	private String situacao;
	
	public DominioSimNao getGradeProcedimento(){
		if(indProcedimento){
			return DominioSimNao.S;
		}
		else{
			return DominioSimNao.N;
		}
	}
	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public Boolean getIndProcedimento() {
		return indProcedimento;
	}

	public void setIndProcedimento(Boolean indProcedimento) {
		this.indProcedimento = indProcedimento;
	}

	public Integer getEqpSeq() {
		return eqpSeq;
	}

	public void setEqpSeq(Integer eqpSeq) {
		this.eqpSeq = eqpSeq;
	}

	public Date getApartirDe() {
		return apartirDe;
	}

	public void setApartirDe(Date apartirDe) {
		this.apartirDe = apartirDe;
	}

	public Integer getDia() {
		return dia;
	}

	public void setDia(Integer dia) {
		this.dia = dia;
	}

	public Date getHorario() {
		return horario;
	}

	public void setHorario(Date horario) {
		this.horario = horario;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public Short getSetorSalaSeq() {
		return setorSalaSeq;
	}

	public void setSetorSalaSeq(Short setorSalaSeq) {
		this.setorSalaSeq = setorSalaSeq;
	}
	public Short getVinculo() {
		return vinculo;
	}
	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}
	public Integer getMatricula() {
		return matricula;
	}
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
}