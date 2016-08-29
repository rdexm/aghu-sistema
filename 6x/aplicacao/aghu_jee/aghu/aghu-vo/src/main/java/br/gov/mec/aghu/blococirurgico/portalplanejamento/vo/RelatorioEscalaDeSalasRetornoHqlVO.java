package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;

public class RelatorioEscalaDeSalasRetornoHqlVO implements Serializable {
	
	private static final long serialVersionUID = -5820218448799518417L;

	private String sigla;
	private Integer matricula;
	private Short vinCodigo;
	private Boolean cirurgiaParticular;
	private Boolean indUrgencia;
	private String nome;
	
	public enum Fields {
		SIGLA("sigla"),
		MATRICULA("matricula"),
		VIN_CODIGO("vinCodigo"),
		CIRURGIA_PARTICULAR("cirurgiaParticular"),
		IND_URGENCIA("indUrgencia"),
		NOME("nome")
		;
		
		private String fields;
		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public Integer getMatricula() {
		return matricula;
	}
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	public Short getVinCodigo() {
		return vinCodigo;
	}
	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	public Boolean getCirurgiaParticular() {
		return cirurgiaParticular;
	}
	public void setCirurgiaParticular(Boolean cirurgiaParticular) {
		this.cirurgiaParticular = cirurgiaParticular;
	}
	public Boolean getIndUrgencia() {
		return indUrgencia;
	}
	public void setIndUrgencia(Boolean indUrgencia) {
		this.indUrgencia = indUrgencia;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

}