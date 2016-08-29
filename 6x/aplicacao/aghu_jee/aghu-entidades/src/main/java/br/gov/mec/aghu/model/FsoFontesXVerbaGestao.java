package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Parameter;

import br.gov.mec.aghu.dominio.DominioPrioridadeFonteRecurso;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name="FSO_FONTES_X_VERBA_GESTAO", schema="AGH", uniqueConstraints = @UniqueConstraint(columnNames = "SEQ"))
@SequenceGenerator(name = "fsoFvbSq1", sequenceName = "AGH.FSO_FVB_SQ1", allocationSize = 1)
public class FsoFontesXVerbaGestao extends BaseEntitySeq<Integer> implements Serializable{

	private static final long serialVersionUID = -8721794136155769470L;
	
	private Integer seq;
	private FsoVerbaGestao verbaGestao;  
	private FsoFontesRecursoFinanc fonteRecursoFinanceiro;
	private DominioPrioridadeFonteRecurso indPrioridade;         
	private Date dtVigIni;
	private Date dtVigFim;
	private Integer version;
	//transient
	private Boolean emEdicao = Boolean.FALSE;
	
	public FsoFontesXVerbaGestao(){	
	}
	
	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fsoFvbSq1")
	public Integer getSeq() {
		return seq;
	}
	
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@ManyToOne
	@JoinColumn(name = "VBG_SEQ", referencedColumnName = "SEQ")
	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}
	
	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}
	
	
	@ManyToOne
	@JoinColumn(name = "FRF_CODIGO", referencedColumnName = "CODIGO")
	public FsoFontesRecursoFinanc getFonteRecursoFinanceiro() {
		return fonteRecursoFinanceiro;
	}
	
	public void setFonteRecursoFinanceiro(
			FsoFontesRecursoFinanc fonteRecursoFinanceiro) {
		this.fonteRecursoFinanceiro = fonteRecursoFinanceiro;
	}
	
	
	@Column(name = "IND_PRIORIDADE", precision = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioPrioridadeFonteRecurso") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioPrioridadeFonteRecurso getIndPrioridade() {
		return indPrioridade;
	}
	
	public void setIndPrioridade(DominioPrioridadeFonteRecurso indPrioridade) {
		this.indPrioridade = indPrioridade;
	}
	
	
	@Column(name = "DT_VIG_INI", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)		
	public Date getDtVigIni() {
		return dtVigIni;
	}
	
	public void setDtVigIni(Date dtVigIni) {
		this.dtVigIni = dtVigIni;
	}
	

	@Column(name = "DT_VIG_FIM")
	@Temporal(TemporalType.TIMESTAMP)		
	public Date getDtVigFim() {
		return dtVigFim;
	}
	
	public void setDtVigFim(Date dtVigFim) {
		this.dtVigFim = dtVigFim;
	}
	
	
	@Column(name = "VERSION", length = 7)
	@Version
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}

	@Transient
	public Boolean getEmEdicao() {
		return emEdicao;
	}
	
	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof FsoFontesXVerbaGestao)) {
			return false;
		}
		FsoFontesXVerbaGestao castOther = (FsoFontesXVerbaGestao) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {

		SEQ("seq"),
		VERBA("verbaGestao"),
		RECURSO("fonteRecursoFinanceiro"),
		IND_PRIORIDADE("indPrioridade"),
		DT_VIG_INI("dtVigIni"),
		DT_VIG_FIM("dtVigFim"),
		VERSION("version");
	    
	    private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	
	
}