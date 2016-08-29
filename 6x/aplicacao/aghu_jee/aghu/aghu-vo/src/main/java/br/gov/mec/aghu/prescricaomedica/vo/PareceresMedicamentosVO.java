/**
 * 
 */
package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author rcorvalao
 * 
 */
public class PareceresMedicamentosVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 102775340530070016L;



	private Integer medCodigo;
	
	
	private BigDecimal parecerSeq;
	
	private BigDecimal jumSeq;

	private Date dthrParecer;
	
	private String descricaoMedicamentos;
	
	private String mpmTipoParecerUsoMdtosDescricao; 

	
	
	
	public enum Fields {
		MAT_CODIGO("medCodigo"),
		DTHR_PARECER("dthrParecer"),
		DESCRICAO_MEDICAMENTO("descricaoMedicamentos"),
		MPM_TIPO_PARECER_USO_MDTOS_DESCRICAO("mpmTipoParecerUsoMdtosDescricao"),
		PARECER_SEQ("parecerSeq"),
		JUN_SEQ("jumSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	public PareceresMedicamentosVO() {
		super();
	}
	
	

	public Date getDthrParecer() {
		return dthrParecer;
	}

	public void setDthrParecer(Date dthrParecer) {
		this.dthrParecer = dthrParecer;
	}

	public String getDescricaoMedicamentos() {
		return descricaoMedicamentos;
	}

	public void setDescricaoMedicamentos(String descricaoMedicamentos) {
		this.descricaoMedicamentos = descricaoMedicamentos;
	}

	public String getMpmTipoParecerUsoMdtosDescricao() {
		return mpmTipoParecerUsoMdtosDescricao;
	}

	public void setMpmTipoParecerUsoMdtosDescricao(
			String mpmTipoParecerUsoMdtosDescricao) {
		this.mpmTipoParecerUsoMdtosDescricao = mpmTipoParecerUsoMdtosDescricao;
	}



	public BigDecimal getJumSeq() {
		return jumSeq;
	}



	public void setJumSeq(BigDecimal jumSeq) {
		this.jumSeq = jumSeq;
	}



	public BigDecimal getParecerSeq() {
		return parecerSeq;
	}



	public void setParecerSeq(BigDecimal parecerSeq) {
		this.parecerSeq = parecerSeq;
	}



	public Integer getMedCodigo() {
		return medCodigo;
	}



	public void setMedCodigo(Integer medCodigo) {
		this.medCodigo = medCodigo;
	}
	
	
}
