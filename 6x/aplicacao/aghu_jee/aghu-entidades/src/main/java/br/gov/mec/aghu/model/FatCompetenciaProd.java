package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacaoCompProd;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="fatProdSq1", sequenceName="AGH.FAT_PROD_SQ1", allocationSize = 1)
@Table(name = "FAT_COMPETENCIAS_PROD", schema = "AGH")
public class FatCompetenciaProd extends BaseEntitySeq<Short> implements Serializable {

	private static final long serialVersionUID = 4025727504185450404L;
	
	private Short seq;

	private Byte mes;
	private Short ano;
	private Date dthrInicioProd;
	private Date dthrFimProd;
	private String criadoPor;
	private String alteradoPor;
	private Date criadoEm;
	private Date alteradoEm;
	private DominioSituacaoCompProd indSituacao;
	private Integer version;

	// construtores

	public FatCompetenciaProd() {
	}

	public FatCompetenciaProd(Short seq, Byte mes, Short ano,
			Date dthrInicioProd, Date dthrFimProd, String criadoPor,
			String alteradoPor, Date criadoEm, Date alteradoEm,
			DominioSituacaoCompProd indSituacao) {

		this.seq = seq;
		this.mes = mes;
		this.ano = ano;
		this.dthrInicioProd = dthrInicioProd;
		this.dthrFimProd = dthrFimProd;
		this.criadoPor = criadoPor;
		this.alteradoPor = alteradoPor;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
		this.indSituacao = indSituacao;
	}

	// getters & setters
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fatProdSq1")
	@Column(name = "SEQ", length = 4, nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "MES", length = 2, nullable = false)
	public Byte getMes() {
		return this.mes;
	}

	public void setMes(Byte mes) {
		this.mes = mes;
	}

	@Column(name = "ANO", length = 4, nullable = false)
	public Short getAno() {
		return this.ano;
	}

	public void setAno(Short ano) {
		this.ano = ano;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO_PROD", nullable = false)
	public Date getDthrInicioProd() {
		return this.dthrInicioProd;
	}

	public void setDthrInicioProd(Date dthrInicioProd) {
		this.dthrInicioProd = dthrInicioProd;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM_PROD")
	public Date getDthrFimProd() {
		return this.dthrFimProd;
	}

	public void setDthrFimProd(Date dthrFimProd) {
		this.dthrFimProd = dthrFimProd;
	}

	@Column(name = "CRIADO_POR", length = 30, nullable = false)
	@Length(max = 30)
	public String getCriadoPor() {
		return this.criadoPor;
	}

	public void setCriadoPor(String criadoPor) {
		this.criadoPor = criadoPor;
	}

	@Column(name = "ALTERADO_POR", length = 30, nullable = false)
	@Length(max = 30)
	public String getAlteradoPor() {
		return this.alteradoPor;
	}

	public void setAlteradoPor(String alteradoPor) {
		this.alteradoPor = alteradoPor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM")
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoCompProd getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacaoCompProd indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Version
	@Column(name = "VERSION", length = 9, nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	// outros

	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	public boolean equals(Object other) {
		if (!(other instanceof FatCompetenciaProd)) {
			return false;
		}
		FatCompetenciaProd castOther = (FatCompetenciaProd) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), MES("mes"), ANO("ano"), DTHR_INICIO_PROD("dthrInicioProd"), DTHR_FIM_PROD(
				"dthrFimProd"), CRIADO_POR("criadoPor"), ALTERADO_POR(
				"alteradoPor"), CRIADO_EM("criadoEm"), ALTERADO_EM("alteradoEm"), IND_SITUACAO(
				"indSituacao"), VERSION("version");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		public String toString() {
			return this.field;
		}
	}

}