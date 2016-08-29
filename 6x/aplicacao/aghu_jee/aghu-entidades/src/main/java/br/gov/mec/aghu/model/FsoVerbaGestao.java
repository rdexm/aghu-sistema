package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name="FSO_VERBAS_GESTAO", schema="AGH", uniqueConstraints = @UniqueConstraint(columnNames = "SEQ"))
@SequenceGenerator(name = "fsoVbgSq1", sequenceName = "AGH.FSO_VBG_SQ1", allocationSize = 1)
public class FsoVerbaGestao extends BaseEntitySeq<Integer> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6271716260343314281L;
	
	private Integer seq;
	private String descricao;
	private Boolean indConvEspecial;
	private DominioSituacao situacao;
	private String nroInterno;
	private BigInteger nroConvSiafi;
	private Date dtIniConv;
	private Date dtFimConv;
	private Short anoExercicio;
	private String indDetPi;
	private Integer version;
	private List<FsoFontesXVerbaGestao> verbaGestao;
	
	
	public FsoVerbaGestao(){
	}

	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fsoVbgSq1")
	public Integer getSeq() {
		return this.seq;
	}
	
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	

	@Column(name = "DESCRICAO", length = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	
	@Column(name = "IND_CONV_ESPECIAL", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndConvEspecial() {
		return indConvEspecial;
	}

	public void setIndConvEspecial(Boolean indConvEspecial) {
		this.indConvEspecial = indConvEspecial;
	}
	

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	
	@Column(name = "NRO_INTERNO", length= 10)
	public String getNroInterno() {
		return nroInterno;
	}

	public void setNroInterno(String nroInterno) {
		this.nroInterno = nroInterno;
	}

	
	@Column(name = "NRO_CONV_SIAFI", length= 7)
	public BigInteger getNroConvSiafi() {
		return nroConvSiafi;
	}

	public void setNroConvSiafi(BigInteger nroConvSiafi) {
		this.nroConvSiafi = nroConvSiafi;
	}
	

	@Column(name = "DT_INI_CONV")
	@Temporal(TemporalType.TIMESTAMP)	
	public Date getDtIniConv() {
		return dtIniConv;
	}

	public void setDtIniConv(Date dtIniConv) {
		this.dtIniConv = dtIniConv;
	}

	
	@Column(name = "DT_FIM_CONV")
	@Temporal(TemporalType.TIMESTAMP)		
	public Date getDtFimConv() {
		return dtFimConv;
	}

	public void setDtFimConv(Date dtFimConv) {
		this.dtFimConv = dtFimConv;
	}

	
	@Column(name = "ANO_EXERCICIO", precision = 4)
	public Short getAnoExercicio() {
		return anoExercicio;
	}

	public void setAnoExercicio(Short anoExercicio) {
		this.anoExercicio = anoExercicio;
	}
	

	@Column(name = "IND_DET_PI", length= 12)
	public String getIndDetPi() {
		return indDetPi;
	}

	public void setIndDetPi(String indDetPi) {
		this.indDetPi = indDetPi;
	}

	@Column(name = "VERSION", length = 7)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@OneToMany(mappedBy="verbaGestao", fetch=FetchType.LAZY)
	public List<FsoFontesXVerbaGestao> getVerbaGestao(){
		return verbaGestao;
	}
	
	public void setVerbaGestao(List<FsoFontesXVerbaGestao> verbaGestao){
		this.verbaGestao = verbaGestao;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof FsoVerbaGestao)) {
			return false;
		}
		FsoVerbaGestao castOther = (FsoVerbaGestao) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {

		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_CONV_ESPECIAL("indConvEspecial"),
		SITUACAO("situacao"),
		NRO_INTERNO("nroInterno"),
		NRO_CONV_SIAFI("nroConvSiafi"),
		DT_INI_CONV("dtIniConv"),
		DT_FIM_CONV("dtFimConv"),
		ANO_EXERCICIO("anoExercicio"),
		IND_DET_PI("indDetPi"),
		VERSION("version"),
		VERBA("verbaGestao");
	    
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