package br.gov.mec.aghu.model;

import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class VMedicoSolicitanteId  implements EntityCompositeId{

	/**
	 * 
	 */
	private static final long serialVersionUID = 434139885849910607L;

	private Integer matricula;
	
	private Integer vinCodigo;
	
	private String nome;
	
	private String nomeUsual;
	
	private String sigla;
	
	private String nroRegConselho;
	
	public enum Fields {
		MATRICULA("matricula"),
		VIN_CODIGO("vinCodigo"),
		NOME("nome"),
		NOME_USUAL("nomeUsual"),
		SIGLA("sigla"),
		NRO_REG_CONSELHO("nroRegConselho");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Integer getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Integer vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeUsual() {
		return nomeUsual;
	}

	public void setNomeUsual(String nomeUsual) {
		this.nomeUsual = nomeUsual;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getNroRegConselho() {
		return nroRegConselho;
	}

	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((matricula == null) ? 0 : matricula.hashCode());
		result = prime * result
				+ ((nroRegConselho == null) ? 0 : nroRegConselho.hashCode());
		result = prime * result
				+ ((vinCodigo == null) ? 0 : vinCodigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof VMedicoSolicitanteId)) {
			return false;
		}
		VMedicoSolicitanteId other = (VMedicoSolicitanteId) obj;
		if (matricula == null) {
			if (other.matricula != null) {
				return false;
			}
		} else if (!matricula.equals(other.matricula)) {
			return false;
		}
		if (nroRegConselho == null) {
			if (other.nroRegConselho != null) {
				return false;
			}
		} else if (!nroRegConselho.equals(other.nroRegConselho)) {
			return false;
		}
		if (vinCodigo == null) {
			if (other.vinCodigo != null) {
				return false;
			}
		} else if (!vinCodigo.equals(other.vinCodigo)) {
			return false;
		}
		return true;
	}
	
}
