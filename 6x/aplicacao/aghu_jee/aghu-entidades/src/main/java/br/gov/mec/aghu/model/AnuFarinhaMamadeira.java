package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
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
@Table(name = "ANU_FARINHA_MAMADEIRAS", schema = "AGH")
public class AnuFarinhaMamadeira extends BaseEntityId<AnuFarinhaMamadeiraId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4952903625915212599L;
	private AnuFarinhaMamadeiraId id;
	private Integer version;
	private AnuDietaMamadeira anuDietaMamadeira;
	private AnuTipoFarinha anuTipoFarinha;
	private RapServidores rapServidoresByAnuFmaSerFk1;
	private RapServidores rapServidoresByAnuFmaSerFk2;
	private RapServidores rapServidoresByAnuFmaSerFk3;
	private Date criadoEm;
	private Date dthrInicio;
	private Float percentual;
	private Integer qtdeGramas;
	private Date dthrFim;
	private String indSuspenso;
	private Date alteradoEm;

	public AnuFarinhaMamadeira() {
	}

	public AnuFarinhaMamadeira(AnuFarinhaMamadeiraId id, AnuDietaMamadeira anuDietaMamadeira, AnuTipoFarinha anuTipoFarinha,
			RapServidores rapServidoresByAnuFmaSerFk1, Date criadoEm, Date dthrInicio, String indSuspenso) {
		this.id = id;
		this.anuDietaMamadeira = anuDietaMamadeira;
		this.anuTipoFarinha = anuTipoFarinha;
		this.rapServidoresByAnuFmaSerFk1 = rapServidoresByAnuFmaSerFk1;
		this.criadoEm = criadoEm;
		this.dthrInicio = dthrInicio;
		this.indSuspenso = indSuspenso;
	}

	public AnuFarinhaMamadeira(AnuFarinhaMamadeiraId id, AnuDietaMamadeira anuDietaMamadeira, AnuTipoFarinha anuTipoFarinha,
			RapServidores rapServidoresByAnuFmaSerFk1, RapServidores rapServidoresByAnuFmaSerFk2,
			RapServidores rapServidoresByAnuFmaSerFk3, Date criadoEm, Date dthrInicio, Float percentual, Integer qtdeGramas,
			Date dthrFim, String indSuspenso, Date alteradoEm) {
		this.id = id;
		this.anuDietaMamadeira = anuDietaMamadeira;
		this.anuTipoFarinha = anuTipoFarinha;
		this.rapServidoresByAnuFmaSerFk1 = rapServidoresByAnuFmaSerFk1;
		this.rapServidoresByAnuFmaSerFk2 = rapServidoresByAnuFmaSerFk2;
		this.rapServidoresByAnuFmaSerFk3 = rapServidoresByAnuFmaSerFk3;
		this.criadoEm = criadoEm;
		this.dthrInicio = dthrInicio;
		this.percentual = percentual;
		this.qtdeGramas = qtdeGramas;
		this.dthrFim = dthrFim;
		this.indSuspenso = indSuspenso;
		this.alteradoEm = alteradoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "dmaAtdSeq", column = @Column(name = "DMA_ATD_SEQ", nullable = false)),
			@AttributeOverride(name = "dmaSeq", column = @Column(name = "DMA_SEQ", nullable = false)),
			@AttributeOverride(name = "tfaSeq", column = @Column(name = "TFA_SEQ", nullable = false)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false)) })
	public AnuFarinhaMamadeiraId getId() {
		return this.id;
	}

	public void setId(AnuFarinhaMamadeiraId id) {
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "DMA_ATD_SEQ", referencedColumnName = "ATD_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "DMA_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false) })
	public AnuDietaMamadeira getAnuDietaMamadeira() {
		return this.anuDietaMamadeira;
	}

	public void setAnuDietaMamadeira(AnuDietaMamadeira anuDietaMamadeira) {
		this.anuDietaMamadeira = anuDietaMamadeira;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TFA_SEQ", nullable = false, insertable = false, updatable = false)
	public AnuTipoFarinha getAnuTipoFarinha() {
		return this.anuTipoFarinha;
	}

	public void setAnuTipoFarinha(AnuTipoFarinha anuTipoFarinha) {
		this.anuTipoFarinha = anuTipoFarinha;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidoresByAnuFmaSerFk1() {
		return this.rapServidoresByAnuFmaSerFk1;
	}

	public void setRapServidoresByAnuFmaSerFk1(RapServidores rapServidoresByAnuFmaSerFk1) {
		this.rapServidoresByAnuFmaSerFk1 = rapServidoresByAnuFmaSerFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MOVIMENTADA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuFmaSerFk2() {
		return this.rapServidoresByAnuFmaSerFk2;
	}

	public void setRapServidoresByAnuFmaSerFk2(RapServidores rapServidoresByAnuFmaSerFk2) {
		this.rapServidoresByAnuFmaSerFk2 = rapServidoresByAnuFmaSerFk2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_SUSPENSA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_SUSPENSA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuFmaSerFk3() {
		return this.rapServidoresByAnuFmaSerFk3;
	}

	public void setRapServidoresByAnuFmaSerFk3(RapServidores rapServidoresByAnuFmaSerFk3) {
		this.rapServidoresByAnuFmaSerFk3 = rapServidoresByAnuFmaSerFk3;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO", nullable = false, length = 29)
	public Date getDthrInicio() {
		return this.dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	@Column(name = "PERCENTUAL", precision = 8, scale = 8)
	public Float getPercentual() {
		return this.percentual;
	}

	public void setPercentual(Float percentual) {
		this.percentual = percentual;
	}

	@Column(name = "QTDE_GRAMAS")
	public Integer getQtdeGramas() {
		return this.qtdeGramas;
	}

	public void setQtdeGramas(Integer qtdeGramas) {
		this.qtdeGramas = qtdeGramas;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM", length = 29)
	public Date getDthrFim() {
		return this.dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	@Column(name = "IND_SUSPENSO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSuspenso() {
		return this.indSuspenso;
	}

	public void setIndSuspenso(String indSuspenso) {
		this.indSuspenso = indSuspenso;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		ANU_DIETA_MAMADEIRAS("anuDietaMamadeira"),
		ANU_TIPO_FARINHAS("anuTipoFarinha"),
		RAP_SERVIDORES_BY_ANU_FMA_SER_FK1("rapServidoresByAnuFmaSerFk1"),
		RAP_SERVIDORES_BY_ANU_FMA_SER_FK2("rapServidoresByAnuFmaSerFk2"),
		RAP_SERVIDORES_BY_ANU_FMA_SER_FK3("rapServidoresByAnuFmaSerFk3"),
		CRIADO_EM("criadoEm"),
		DTHR_INICIO("dthrInicio"),
		PERCENTUAL("percentual"),
		QTDE_GRAMAS("qtdeGramas"),
		DTHR_FIM("dthrFim"),
		IND_SUSPENSO("indSuspenso"),
		ALTERADO_EM("alteradoEm");

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
		if (!(obj instanceof AnuFarinhaMamadeira)) {
			return false;
		}
		AnuFarinhaMamadeira other = (AnuFarinhaMamadeira) obj;
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
