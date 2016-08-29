package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class RelatorioEscalaDeSalasLinhaVO implements Serializable {
	
	private static final long serialVersionUID = -6910558555870394675L;
	private String seg;
	private String ter;
	private String qua;
	private String qui;
	private String sex;
	private String sab;
	private String dom;
	private String turno;
	
	public String getSeg() {
		return seg;
	}
	public void setSeg(String seg) {
		this.seg = seg;
	}
	public String getTer() {
		return ter;
	}
	public void setTer(String ter) {
		this.ter = ter;
	}
	public String getQua() {
		return qua;
	}
	public void setQua(String qua) {
		this.qua = qua;
	}
	public String getQui() {
		return qui;
	}
	public void setQui(String qui) {
		this.qui = qui;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getSab() {
		return sab;
	}
	public void setSab(String sab) {
		this.sab = sab;
	}
	public String getDom() {
		return dom;
	}
	public void setDom(String dom) {
		this.dom = dom;
	}
	public void setTurno(String turno) {
		this.turno = turno;
	}
	public String getTurno() {
		return turno;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getDom());
		umHashCodeBuilder.append(this.getQua());
		umHashCodeBuilder.append(this.getQui());
		umHashCodeBuilder.append(this.getSab());
		umHashCodeBuilder.append(this.getSeg());
		umHashCodeBuilder.append(this.getTer());
		umHashCodeBuilder.append(this.getSex());
		umHashCodeBuilder.append(this.getTurno());
		return umHashCodeBuilder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RelatorioEscalaDeSalasLinhaVO)) {
			return false;
		}
		RelatorioEscalaDeSalasLinhaVO other = (RelatorioEscalaDeSalasLinhaVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getDom(), other.getDom());
		umEqualsBuilder.append(this.getQua(), other.getQua());
		umEqualsBuilder.append(this.getQui(), other.getQui());
		umEqualsBuilder.append(this.getSab(), other.getSab());
		umEqualsBuilder.append(this.getSeg(), other.getSeg());
		umEqualsBuilder.append(this.getTer(), other.getTer());
		umEqualsBuilder.append(this.getSex(), other.getSex());
		umEqualsBuilder.append(this.getTurno(), other.getTurno());
		return umEqualsBuilder.isEquals();
	}
}