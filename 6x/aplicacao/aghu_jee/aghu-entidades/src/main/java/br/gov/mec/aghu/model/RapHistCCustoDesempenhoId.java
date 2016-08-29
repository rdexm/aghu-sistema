package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable()
public class RapHistCCustoDesempenhoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8135824412077459793L;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer cctCodigo;
	private Date dtInicio;

	public RapHistCCustoDesempenhoId(){		
	}
		
	public RapHistCCustoDesempenhoId(Integer serMatricula, Short serVinCodigo,
			Integer cctCodigo, Date dtInicio) {
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.cctCodigo = cctCodigo;
		this.dtInicio = dtInicio;
	}

	@Column(name = "SER_MATRICULA", length = 7, nullable = false)	 
	public Integer getSerMatricula(){
		return this.serMatricula;
	}
	
	public void setSerMatricula(Integer serMatricula){
		this.serMatricula = serMatricula;
	}
		
	@Column(name = "SER_VIN_CODIGO", length = 3, nullable = false)	 
	public Short getSerVinCodigo(){
		return this.serVinCodigo;
	}
	
	public void setSerVinCodigo(Short serVinCodigo){
		this.serVinCodigo = serVinCodigo;
	}
		
	@Column(name = "CCT_CODIGO", length = 6, nullable = false)	 
	public Integer getCctCodigo(){
		return this.cctCodigo;
	}
	
	public void setCctCodigo(Integer cctCodigo){
		this.cctCodigo = cctCodigo;
	}
		
	@Column(name = "DT_INICIO", nullable = false)	 
	public Date getDtInicio(){
		return this.dtInicio;
	}
	
	public void setDtInicio(Date dtInicio){
		this.dtInicio = dtInicio;
	}
		
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("serMatricula",this.serMatricula)
			.append("serVinCodigo",this.serVinCodigo)
			.append("cctCodigo",this.cctCodigo)
			.append("dtInicio",this.dtInicio)
		.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof RapHistCCustoDesempenhoId)) {
			return false;
		}
		RapHistCCustoDesempenhoId castOther = (RapHistCCustoDesempenhoId) other;
		return new EqualsBuilder()
			.append(this.serMatricula, castOther.getSerMatricula())
			.append(this.serVinCodigo, castOther.getSerVinCodigo())
			.append(this.cctCodigo, castOther.getCctCodigo())
			.append(this.dtInicio, castOther.getDtInicio())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.serMatricula)
			.append(this.serVinCodigo)
			.append(this.cctCodigo)
			.append(this.dtInicio)
			.toHashCode();
	}
} 