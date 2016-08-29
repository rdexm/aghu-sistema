package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTmo;

public class RelatorioTransplantesRealizTMOOutrosVO {
	
	private Integer prontuario;
	private String nome;
	private Date dtNascimento;
	private Date dtTransplante;
	private DominioTmo indTmo;
	private String procedimento;
	private String equipe;

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

	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public Date getDtTransplante() {
		return dtTransplante;
	}

	public void setDtTransplante(Date dtTransplante) {
		this.dtTransplante = dtTransplante;
	}

	public DominioTmo getIndTmo() {
		return indTmo;
	}

	public void setIndTmo(DominioTmo indTmo) {
		this.indTmo = indTmo;
	}

	public String getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(String procedimento) {
		this.procedimento = procedimento;
	}

	public String getEquipe() {
		return equipe;
	}

	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	
	public enum Fields {
		PRONTUARIO("prontuario"),
		NOME("nome"),
		DT_NASCIMENTO("dtNascimento"),
		DT_TRANSPLANTE("dtTransplante"),
		IND_TMO("indTmo"),
		PROCEDIMENTO("procedimento"),
		EQUIPE("equipe");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}	
}
