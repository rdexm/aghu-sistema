package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

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
@Table(name = "ECE_AUXILIAR_PREFERENCIAS", schema = "AGH")
public class EceAuxiliarPreferencia extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8610384821844478274L;
	private Integer seq;
	private Integer version;
	private RapServidores rapServidoresByEceAxpSerFk1;
	private RapServidores rapServidoresByEceAxpSerFk2;
	private EcePreferenciaServidor ecePreferenciaServidor;
	private Date criadoEm;

	public EceAuxiliarPreferencia() {
	}

	public EceAuxiliarPreferencia(Integer seq, RapServidores rapServidoresByEceAxpSerFk1, RapServidores rapServidoresByEceAxpSerFk2,
			EcePreferenciaServidor ecePreferenciaServidor, Date criadoEm) {
		this.seq = seq;
		this.rapServidoresByEceAxpSerFk1 = rapServidoresByEceAxpSerFk1;
		this.rapServidoresByEceAxpSerFk2 = rapServidoresByEceAxpSerFk2;
		this.ecePreferenciaServidor = ecePreferenciaServidor;
		this.criadoEm = criadoEm;
	}

	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidoresByEceAxpSerFk1() {
		return this.rapServidoresByEceAxpSerFk1;
	}

	public void setRapServidoresByEceAxpSerFk1(RapServidores rapServidoresByEceAxpSerFk1) {
		this.rapServidoresByEceAxpSerFk1 = rapServidoresByEceAxpSerFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_PREFERENCIA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_PREFERENCIA", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidoresByEceAxpSerFk2() {
		return this.rapServidoresByEceAxpSerFk2;
	}

	public void setRapServidoresByEceAxpSerFk2(RapServidores rapServidoresByEceAxpSerFk2) {
		this.rapServidoresByEceAxpSerFk2 = rapServidoresByEceAxpSerFk2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRX_SEQ", nullable = false)
	public EcePreferenciaServidor getEcePreferenciaServidor() {
		return this.ecePreferenciaServidor;
	}

	public void setEcePreferenciaServidor(EcePreferenciaServidor ecePreferenciaServidor) {
		this.ecePreferenciaServidor = ecePreferenciaServidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES_BY_ECE_AXP_SER_FK1("rapServidoresByEceAxpSerFk1"),
		RAP_SERVIDORES_BY_ECE_AXP_SER_FK2("rapServidoresByEceAxpSerFk2"),
		ECE_PREFERENCIA_SERVIDORES("ecePreferenciaServidor"),
		CRIADO_EM("criadoEm");

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
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof EceAuxiliarPreferencia)) {
			return false;
		}
		EceAuxiliarPreferencia other = (EceAuxiliarPreferencia) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
