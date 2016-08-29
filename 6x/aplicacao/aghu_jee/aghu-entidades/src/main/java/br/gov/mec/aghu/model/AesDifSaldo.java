package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@Table(name = "AES_DIF_SALDOS", schema = "AGH")
public class AesDifSaldo extends BaseEntityId<AesDifSaldoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2865529919525509099L;
	private AesDifSaldoId id;
	private Integer version;
	private Date dtIni;
	private Date dtFim;
	private Short saldoGrav;
	private Short saldoCalc;
	private Date criadoEm;
	private Integer cctCodigo;
	private String tgtCodigo;
	private String treCodigo;
	private Short dfoPos;
	private Short dfoNeg;
	private Short ehePos;
	private Integer ultCctCodigo;

	public AesDifSaldo() {
	}

	public AesDifSaldo(AesDifSaldoId id) {
		this.id = id;
	}

	public AesDifSaldo(AesDifSaldoId id, Date dtIni, Date dtFim, Short saldoGrav, Short saldoCalc, Date criadoEm, Integer cctCodigo,
			String tgtCodigo, String treCodigo, Short dfoPos, Short dfoNeg, Short ehePos, Integer ultCctCodigo) {
		this.id = id;
		this.dtIni = dtIni;
		this.dtFim = dtFim;
		this.saldoGrav = saldoGrav;
		this.saldoCalc = saldoCalc;
		this.criadoEm = criadoEm;
		this.cctCodigo = cctCodigo;
		this.tgtCodigo = tgtCodigo;
		this.treCodigo = treCodigo;
		this.dfoPos = dfoPos;
		this.dfoNeg = dfoNeg;
		this.ehePos = ehePos;
		this.ultCctCodigo = ultCctCodigo;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "areCodigo", column = @Column(name = "ARE_CODIGO", nullable = false)),
			@AttributeOverride(name = "sequencia", column = @Column(name = "SEQUENCIA", nullable = false)),
			@AttributeOverride(name = "serMatricula", column = @Column(name = "SER_MATRICULA", nullable = false)),
			@AttributeOverride(name = "serVinCodigo", column = @Column(name = "SER_VIN_CODIGO", nullable = false)) })
	public AesDifSaldoId getId() {
		return this.id;
	}

	public void setId(AesDifSaldoId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_INI", length = 29)
	public Date getDtIni() {
		return this.dtIni;
	}

	public void setDtIni(Date dtIni) {
		this.dtIni = dtIni;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_FIM", length = 29)
	public Date getDtFim() {
		return this.dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	@Column(name = "SALDO_GRAV")
	public Short getSaldoGrav() {
		return this.saldoGrav;
	}

	public void setSaldoGrav(Short saldoGrav) {
		this.saldoGrav = saldoGrav;
	}

	@Column(name = "SALDO_CALC")
	public Short getSaldoCalc() {
		return this.saldoCalc;
	}

	public void setSaldoCalc(Short saldoCalc) {
		this.saldoCalc = saldoCalc;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "CCT_CODIGO")
	public Integer getCctCodigo() {
		return this.cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	@Column(name = "TGT_CODIGO", length = 5)
	@Length(max = 5)
	public String getTgtCodigo() {
		return this.tgtCodigo;
	}

	public void setTgtCodigo(String tgtCodigo) {
		this.tgtCodigo = tgtCodigo;
	}

	@Column(name = "TRE_CODIGO", length = 3)
	@Length(max = 3)
	public String getTreCodigo() {
		return this.treCodigo;
	}

	public void setTreCodigo(String treCodigo) {
		this.treCodigo = treCodigo;
	}

	@Column(name = "DFO_POS")
	public Short getDfoPos() {
		return this.dfoPos;
	}

	public void setDfoPos(Short dfoPos) {
		this.dfoPos = dfoPos;
	}

	@Column(name = "DFO_NEG")
	public Short getDfoNeg() {
		return this.dfoNeg;
	}

	public void setDfoNeg(Short dfoNeg) {
		this.dfoNeg = dfoNeg;
	}

	@Column(name = "EHE_POS")
	public Short getEhePos() {
		return this.ehePos;
	}

	public void setEhePos(Short ehePos) {
		this.ehePos = ehePos;
	}

	@Column(name = "ULT_CCT_CODIGO")
	public Integer getUltCctCodigo() {
		return this.ultCctCodigo;
	}

	public void setUltCctCodigo(Integer ultCctCodigo) {
		this.ultCctCodigo = ultCctCodigo;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		DT_INI("dtIni"),
		DT_FIM("dtFim"),
		SALDO_GRAV("saldoGrav"),
		SALDO_CALC("saldoCalc"),
		CRIADO_EM("criadoEm"),
		CCT_CODIGO("cctCodigo"),
		TGT_CODIGO("tgtCodigo"),
		TRE_CODIGO("treCodigo"),
		DFO_POS("dfoPos"),
		DFO_NEG("dfoNeg"),
		EHE_POS("ehePos"),
		ULT_CCT_CODIGO("ultCctCodigo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AesDifSaldo)) {
			return false;
		}
		AesDifSaldo other = (AesDifSaldo) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
