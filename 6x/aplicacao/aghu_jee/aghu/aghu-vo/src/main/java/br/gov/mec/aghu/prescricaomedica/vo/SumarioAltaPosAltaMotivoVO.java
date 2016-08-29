package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.MpmAltaMotivo;
import br.gov.mec.aghu.model.MpmAltaPlano;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.model.MpmPlanoPosAlta;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * Responsabilidade de representar as lista de Motivo Alta e Plano Pos-Alta<br>
 * Na tela de Sumario de Alta na aba Pos-Alta.
 * 
 * @author rcorvalao
 */
public class SumarioAltaPosAltaMotivoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5599118758561739256L;
	
	/**
	 * Este id representa o id do objeto motivo
	 * ou plano pos alta.
	 */
	private MpmAltaSumarioId idItem;
	private Short seq;
	private MpmPlanoPosAlta planoPosAlta;
	private MpmMotivoAltaMedica motivoAltaMedica;
	private String descricao;
	private Boolean exigeComplemento = Boolean.FALSE;
	private String complemento;
	private MpmAltaSumario altaSumario;
	
	
	public MpmAltaMotivo getModelMotivoAlta() {
		MpmAltaMotivo pojo = new MpmAltaMotivo();
		
		pojo.setId(this.idItem);
		pojo.setAltaSumario(this.altaSumario);
		pojo.setMotivoAltaMedicas(this.motivoAltaMedica);
		pojo.setComplMotivo(this.complemento);
		pojo.setDescMotivo(this.motivoAltaMedica.getDescricao());
		
		return pojo;
	}
	
	public MpmAltaPlano getModelPlanoPosAlta() {
		MpmAltaPlano pojo = new MpmAltaPlano();
		
		pojo.setId(this.idItem);
		pojo.setAltaSumario(this.altaSumario);
		pojo.setMpmPlanoPosAltas(this.planoPosAlta);
		pojo.setComplPlanoPosAlta(StringUtil.trim(this.complemento));
		pojo.setDescPlanoPosAlta(this.planoPosAlta.getDescricao());
		
		return pojo;
	}
	
	
	public void setPlanoPosAlta(MpmPlanoPosAlta p) {
		this.planoPosAlta = p;
		if (p != null) {
			this.setSeq(p.getSeq());
		}
	}
	public MpmPlanoPosAlta getPlanoPosAlta() {
		return planoPosAlta;
	}
	
	public MpmMotivoAltaMedica getMotivoAltaMedica() {
		return motivoAltaMedica;
	}
	public void setMotivoAltaMedica(MpmMotivoAltaMedica p) {
		this.motivoAltaMedica = p;
		if (p != null) {
			this.setSeq(p.getSeq());
		}
	}
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Boolean getExigeComplemento() {
		return exigeComplemento;
	}
	public void setExigeComplemento(Boolean exigeComplemento) {
		this.exigeComplemento = exigeComplemento;
	}
	
	public void setAltaSumario(MpmAltaSumario altaSumario) {
		this.altaSumario = altaSumario;
	}
	public MpmAltaSumario getAltaSumario() {
		return altaSumario;
	}
	
	public void setSeq(Short seq) {
		this.seq = seq;
	}
	public Short getSeq() {
		return seq;
	}
	
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	
	public MpmAltaSumarioId getIdItem() {
		return idItem;
	}
	public void setIdItem(MpmAltaSumarioId idItem) {
		this.idItem = idItem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		SumarioAltaPosAltaMotivoVO other = (SumarioAltaPosAltaMotivoVO) obj;
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
		} else if (!seq.equals(other.seq)){
			return false;
		}
		return true;
	}

	

}
