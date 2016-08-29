package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;

public class ResponsavelAceiteTecnicoPendenteVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1463444901285886393L;
	private Integer seqMatricula;
	private Short codVinculo;
	private String nomeResponsavel;
	
	public enum Fields {
		
		MATRICULA("seqMatricula"),
		VINCULO("codVinculo"),
		NOME_RESPONSAVEL("nomeResponsavel");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	public Integer getSeqMatricula() {
		return seqMatricula;
	}

	public void setSeqMatricula(Integer seqMatricula) {
		this.seqMatricula = seqMatricula;
	}

	public Short getCodVinculo() {
		return codVinculo;
	}

	public void setCodVinculo(Short codVinculo) {
		this.codVinculo = codVinculo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((codVinculo == null) ? 0 : codVinculo.hashCode());
		result = prime * result
				+ ((nomeResponsavel == null) ? 0 : nomeResponsavel.hashCode());
		result = prime * result
				+ ((seqMatricula == null) ? 0 : seqMatricula.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ResponsavelAceiteTecnicoPendenteVO other = (ResponsavelAceiteTecnicoPendenteVO) obj;
		if (codVinculo == null) {
			if (other.codVinculo != null){
				return false;
			}
		} else if (!codVinculo.equals(other.codVinculo)){
			return false;
		}
		if (nomeResponsavel == null) {
			if (other.nomeResponsavel != null){
				return false;
			}
		} else if (!nomeResponsavel.equals(other.nomeResponsavel)){
			return false;
		}
		if (seqMatricula == null) {
			if (other.seqMatricula != null){
				return false;
			}
		} else if (!seqMatricula.equals(other.seqMatricula)){
			return false;
		}
		return true;
	}

}
