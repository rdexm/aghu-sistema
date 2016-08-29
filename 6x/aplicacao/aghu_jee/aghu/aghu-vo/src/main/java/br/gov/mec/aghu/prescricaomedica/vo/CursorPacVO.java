package br.gov.mec.aghu.prescricaomedica.vo;

import br.gov.mec.aghu.dominio.DominioEstadoCivil;
import br.gov.mec.aghu.dominio.DominioSexo;

public class CursorPacVO {

	
	Integer pacCodigo;
	String nome;
	Integer prontuario;
	Integer clcCodigo;
	Boolean indEspPediatrica;
	
	private DominioEstadoCivil estadoCivil;
	private DominioSexo sexo;
	
	public DominioEstadoCivil getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(DominioEstadoCivil estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public DominioSexo getSexo() {
		return sexo;
	}

	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}

	public enum Fields {
		PAC_CODIGO("pacCodigo"),
		NOME("nome"),
		PRONTUARIO("prontuario"),
		CLC_CODIGO("clcCodigo"),
		IND_ESP_PEDIATRICA("indEspPediatrica"),
		ESTADO_CIVIL("estadoCivil"),
		SEXO("sexo"),
		;
		  
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		@Override
		public String toString() {
			return this.field;
		}
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getClcCodigo() {
		return clcCodigo;
	}

	public void setClcCodigo(Integer clcCodigo) {
		this.clcCodigo = clcCodigo;
	}

	public Boolean getIndEspPediatrica() {
		return indEspPediatrica;
	}

	public void setIndEspPediatrica(Boolean indEspPediatrica) {
		this.indEspPediatrica = indEspPediatrica;
	}
}
