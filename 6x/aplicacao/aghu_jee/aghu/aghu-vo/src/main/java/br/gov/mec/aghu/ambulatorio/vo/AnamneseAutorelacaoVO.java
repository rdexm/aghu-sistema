package br.gov.mec.aghu.ambulatorio.vo;

import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.core.commons.BaseBean;

public class AnamneseAutorelacaoVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3051472029439484128L;
	
	private DominioIndPendenteAmbulatorio pendente;	
	private Integer serMatriculaValida;
	private Short serVinCodigoValida;
	private Integer serMatriculaValidaRelacao;
	private Short serVinCodigoValidaRelacao;

	public enum Fields {
		PENDENTE("pendente"), 
		SERVIDOR_MATRICULA_VALIDA("serMatriculaValida"),
		SERVIDOR_VIN_CODIGO_VALIDA("serVinCodigoValida"), 
		SERVIDOR_MATRICULA_VALIDA_RELACAO("serMatriculaValidaRelacao"), 
		SERVIDOR_VIN_CODIGO_VALIDA_RELACAO("serVinCodigoValidaRelacao");		

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public DominioIndPendenteAmbulatorio getPendente() {
		return pendente;
	}

	public void setPendente(DominioIndPendenteAmbulatorio pendente) {
		this.pendente = pendente;
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

	public Integer getSerMatriculaValidaRelacao() {
		return serMatriculaValidaRelacao;
	}

	public void setSerMatriculaValidaRelacao(Integer serMatriculaValidaRelacao) {
		this.serMatriculaValidaRelacao = serMatriculaValidaRelacao;
	}

	public Short getSerVinCodigoValidaRelacao() {
		return serVinCodigoValidaRelacao;
	}

	public void setSerVinCodigoValidaRelacao(Short serVinCodigoValidaRelacao) {
		this.serVinCodigoValidaRelacao = serVinCodigoValidaRelacao;
	}
}
