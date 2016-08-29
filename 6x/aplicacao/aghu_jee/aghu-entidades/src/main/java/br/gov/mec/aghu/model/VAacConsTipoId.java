package br.gov.mec.aghu.model;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacaoConsulta;
import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class VAacConsTipoId implements EntityCompositeId{

	/**
	 * 
	 */
	private static final long serialVersionUID = 828576285884582909L;
	private Short espSeq;
	private String espSigla;
	private String espNome;
	private Short vCaaSeq;
	private String caaDescricao;
	private Short vPgdSeq;
	private String pgdDescricao;
	private Short vTagSeq;
	private String tagDescricao;
	private DominioSituacaoConsulta visitIndSitCons;

	@Column(name = "V_ESP_SEQ", nullable = false)
	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	@Column(name = "ESP_SIGLA", nullable = false, length = 3)
	public String getEspSigla() {
		return espSigla;
	}

	public void setEspSigla(String espSigla) {
		this.espSigla = espSigla;
	}

	@Column(name = "ESP_NOME", nullable = false, length = 45)
	public String getEspNome() {
		return espNome;
	}

	public void setEspNome(String espNome) {
		this.espNome = espNome;
	}

	@Column(name = "V_CAA_SEQ", nullable = false)
	public Short getvCaaSeq() {
		return vCaaSeq;
	}

	public void setvCaaSeq(Short vCaaSeq) {
		this.vCaaSeq = vCaaSeq;
	}

	@Column(name = "CAA_DESCRICAO", nullable = false, length = 45)
	public String getCaaDescricao() {
		return caaDescricao;
	}

	public void setCaaDescricao(String caaDescricao) {
		this.caaDescricao = caaDescricao;
	}

	@Column(name = "V_PGD_SEQ", nullable = false)
	public Short getvPgdSeq() {
		return vPgdSeq;
	}

	public void setvPgdSeq(Short vPgdSeq) {
		this.vPgdSeq = vPgdSeq;
	}

	@Column(name = "PGD_DESCRICAO", nullable = false, length = 45)
	public String getPgdDescricao() {
		return pgdDescricao;
	}

	public void setPgdDescricao(String pgdDescricao) {
		this.pgdDescricao = pgdDescricao;
	}

	@Column(name = "V_TAG_SEQ", nullable = false)
	public Short getvTagSeq() {
		return vTagSeq;
	}

	public void setvTagSeq(Short vTagSeq) {
		this.vTagSeq = vTagSeq;
	}

	@Column(name = "TAG_DESCRICAO", nullable = false, length = 45)
	public String getTagDescricao() {
		return tagDescricao;
	}

	public void setTagDescricao(String tagDescricao) {
		this.tagDescricao = tagDescricao;
	}
	
	@Column(name = "VSIT_IND_SIT_CONS", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoConsulta getVisitIndSitCons() {
		return visitIndSitCons;
	}

	public void setVisitIndSitCons(DominioSituacaoConsulta visitIndSitCons) {
		this.visitIndSitCons = visitIndSitCons;
	}

	@Override
	public int hashCode() {
		
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getCaaDescricao());
		umHashCodeBuilder.append(this.getEspNome());
		umHashCodeBuilder.append(this.getEspSeq());
		umHashCodeBuilder.append(this.getEspSigla());
		umHashCodeBuilder.append(this.getPgdDescricao());
		umHashCodeBuilder.append(this.getTagDescricao());
		umHashCodeBuilder.append(this.getvCaaSeq());
		umHashCodeBuilder.append(this.getvPgdSeq());
		umHashCodeBuilder.append(this.getvTagSeq());
		umHashCodeBuilder.append(this.getVisitIndSitCons());
		
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

		if (!(obj instanceof VAacConsTipoId)) {
			return false;
		}

		VAacConsTipoId other = (VAacConsTipoId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getCaaDescricao(), other.getCaaDescricao());
		umEqualsBuilder.append(this.getEspNome(), other.getEspNome());
		umEqualsBuilder.append(this.getEspSeq(), other.getEspSeq()); 
		umEqualsBuilder.append(this.getEspSigla(), other.getEspSigla()); 
		umEqualsBuilder.append(this.getPgdDescricao(), other.getPgdDescricao()); 
		umEqualsBuilder.append(this.getTagDescricao(), other.getTagDescricao()); 
		umEqualsBuilder.append(this.getvCaaSeq(), other.getvCaaSeq()); 
		umEqualsBuilder.append(this.getvPgdSeq(), other.getvPgdSeq()); 
		umEqualsBuilder.append(this.getvTagSeq(), other.getvTagSeq()); 
		umEqualsBuilder.append(this.getVisitIndSitCons(), other.getVisitIndSitCons()); 

		return umEqualsBuilder.isEquals();
	}

}
