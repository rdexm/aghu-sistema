package br.gov.mec.aghu.paciente.vo;

import java.util.Date;

/**
 * Vo resultado do cursor c_paciente
 * 
 * @author riccosta
 * 
 */
public class PacienteZplVo {

	/**
	 * v_t_zn_sl
	 */
	String desc;

	/**
	 * turno.
	 */
	String turno;

	/**
	 * Zona.
	 */
	String sigla;

	/**
	 * Sala.
	 */
	String sala;

	/**
	 * dtConsulta
	 */
	Date dtConsulta;

	/**
	 * prontuário
	 */
	Integer prontuario;

	/**
	 * Nome
	 */
	String nome;

	/**
	 * Código
	 */
	Integer codigo;

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getTurno() {
		return turno;
	}

	public void setTurno(String turno) {
		this.turno = turno;
	}

	public Date getDtConsulta() {
		return dtConsulta;
	}

	public void setDtConsulta(Date dtConsulta) {
		this.dtConsulta = dtConsulta;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getSala() {
		return sala;
	}

	public void setSala(String sala) {
		this.sala = sala;
	}

}