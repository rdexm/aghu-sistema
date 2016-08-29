package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;


public class MedicoSolicitanteVO implements Serializable{

	private static final long serialVersionUID = 3980815498181529888L;

	private String nome;
	
	private String vinDesc;
	
	private Integer matricula;
	
	private Short vinCodigo;
	
	public MedicoSolicitanteVO() {}
	
	public MedicoSolicitanteVO(String nome, String vinDesc, Integer matricula,
			Short vinCodigo) {
		super();
		this.nome = nome;
		this.vinDesc = vinDesc;
		this.matricula = matricula;
		this.vinCodigo = vinCodigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getVinDesc() {
		return vinDesc;
	}

	public void setVinDesc(String vinDesc) {
		this.vinDesc = vinDesc;
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
	
	public enum Fields {
		NOME("nome"),
		VIN_DESC("vinDesc"),
		MATRICULA("matricula"),
		VIN_CODIGO("vinCodigo");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((matricula == null) ? 0 : matricula.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result
				+ ((vinCodigo == null) ? 0 : vinCodigo.hashCode());
		result = prime * result + ((vinDesc == null) ? 0 : vinDesc.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MedicoSolicitanteVO)) {
			return false;
		}
		MedicoSolicitanteVO other = (MedicoSolicitanteVO) obj;
		if (matricula == null) {
			if (other.matricula != null) {
				return false;
			}
		} else if (!matricula.equals(other.matricula)) {
			return false;
		}
		if (nome == null) {
			if (other.nome != null) {
				return false;
			}
		} else if (!nome.equals(other.nome)) {
			return false;
		}
		if (vinCodigo == null) {
			if (other.vinCodigo != null) {
				return false;
			}
		} else if (!vinCodigo.equals(other.vinCodigo)) {
			return false;
		}
		if (vinDesc == null) {
			if (other.vinDesc != null) {
				return false;
			}
		} else if (!vinDesc.equals(other.vinDesc)) {
			return false;
		}
		return true;
	}
}