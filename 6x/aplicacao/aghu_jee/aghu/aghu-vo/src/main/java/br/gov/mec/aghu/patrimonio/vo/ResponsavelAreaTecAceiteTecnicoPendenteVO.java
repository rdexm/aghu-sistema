package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;

public class ResponsavelAreaTecAceiteTecnicoPendenteVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5617547499401679912L;
	private Integer matriculaResponsavel;
	private Short codVinculoResponsavel;
	private String usuarioResponsavel;
	private Integer codigCentroCusto;
	private Integer seqAreatec;
	
	public enum Fields {
		
		MATRICULA_RESPONSAVEL("matriculaResponsavel"),
		VINCULO_RESPONSAVEL("codVinculoResponsavel"),
		USUARIO_RESPONSAVEL("usuarioResponsavel"),
		SEQ_AREA_TEC("seqAreatec"),
		COD_CENTRO_CUSTO("codigCentroCusto");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	public Integer getMatriculaResponsavel() {
		return matriculaResponsavel;
	}

	public void setMatriculaResponsavel(Integer matriculaResponsavel) {
		this.matriculaResponsavel = matriculaResponsavel;
	}

	public Short getCodVinculoResponsavel() {
		return codVinculoResponsavel;
	}

	public void setCodVinculoResponsavel(Short codVinculoResponsavel) {
		this.codVinculoResponsavel = codVinculoResponsavel;
	}

	public String getUsuarioResponsavel() {
		return usuarioResponsavel;
	}

	public void setUsuarioResponsavel(String usuarioResponsavel) {
		this.usuarioResponsavel = usuarioResponsavel;
	}

	public Integer getCodigCentroCusto() {
		return codigCentroCusto;
	}

	public void setCodigCentroCusto(Integer codigCentroCusto) {
		this.codigCentroCusto = codigCentroCusto;
	}

	public Integer getSeqAreatec() {
		return seqAreatec;
	}

	public void setSeqAreatec(Integer seqAreatec) {
		this.seqAreatec = seqAreatec;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((codVinculoResponsavel == null) ? 0 : codVinculoResponsavel
						.hashCode());
		result = prime
				* result
				+ ((matriculaResponsavel == null) ? 0 : matriculaResponsavel
						.hashCode());
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
		ResponsavelAreaTecAceiteTecnicoPendenteVO other = (ResponsavelAreaTecAceiteTecnicoPendenteVO) obj;
		if (codVinculoResponsavel == null) {
			if (other.codVinculoResponsavel != null){
				return false;
			}
		} else if (!codVinculoResponsavel.equals(other.codVinculoResponsavel)){
			return false;
		}
		if (matriculaResponsavel == null) {
			if (other.matriculaResponsavel != null){
				return false;
			}
		} else if (!matriculaResponsavel.equals(other.matriculaResponsavel)){
			return false;
		}
		return true;
	}

}
