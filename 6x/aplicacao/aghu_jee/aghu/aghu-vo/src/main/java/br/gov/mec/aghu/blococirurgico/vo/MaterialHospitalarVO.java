package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

/**
 * #37999
 * Criada para a suggestion de materiais 
 * 
 * 
 * @author gelizeire@hcpa.com.br
 *
 */
public class MaterialHospitalarVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1465549423871016161L;
	/**
	 * 
	 */

	private Integer matCodigo;
	private String matNome;
	private String umdCodigo;
	
	public enum Fields {

		MAT_CODIGO("matCodigo"), 
		MAT_NOME("matNome"), 
		UMD_CODIGO("umdCodigo");
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	
	public MaterialHospitalarVO() {
		super();
	}
	public MaterialHospitalarVO(Integer matCodigo, String matNome,
			String umdCodigo) {
		super();
		this.matCodigo = matCodigo;
		this.matNome = matNome;
		this.umdCodigo = umdCodigo;
	}
	public Integer getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	public String getMatNome() {
		return matNome;
	}
	public void setMatNome(String matNome) {
		this.matNome = matNome;
	}
	public String getUmdCodigo() {
		return umdCodigo;
	}
	public void setUmdCodigo(String umdCodigo) {
		this.umdCodigo = umdCodigo;
	}
	
	
	
}
