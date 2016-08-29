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
@Table(name = "ANU_ACUCAR_MAMADEIRAS", schema = "AGH")
public class AnuAcucarMamadeira extends BaseEntityId<AnuAcucarMamadeiraId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1927687685149889140L;
	private AnuAcucarMamadeiraId id;
	private Integer version;
	private AnuDietaMamadeira anuDietaMamadeira;
	private RapServidores rapServidoresByAnuAmaSerFk1;
	private AnuTipoAcucar anuTipoAcucar;
	private RapServidores rapServidoresByAnuAmaSerFk3;
	private RapServidores rapServidoresByAnuAmaSerFk2;
	private Date criadoEm;
	private Date dthrInicio;
	private Float percentual;
	private Integer qtdeGramas;
	private Date dthrFim;
	private String indSuspenso;
	private Date alteradoEm;

	public AnuAcucarMamadeira() {
	}

	public AnuAcucarMamadeira(AnuAcucarMamadeiraId id, AnuDietaMamadeira anuDietaMamadeira,
			RapServidores rapServidoresByAnuAmaSerFk1, AnuTipoAcucar anuTipoAcucar, Date criadoEm, Date dthrInicio,
			String indSuspenso) {
		this.id = id;
		this.anuDietaMamadeira = anuDietaMamadeira;
		this.rapServidoresByAnuAmaSerFk1 = rapServidoresByAnuAmaSerFk1;
		this.anuTipoAcucar = anuTipoAcucar;
		this.criadoEm = criadoEm;
		this.dthrInicio = dthrInicio;
		this.indSuspenso = indSuspenso;
	}

	public AnuAcucarMamadeira(AnuAcucarMamadeiraId id, AnuDietaMamadeira anuDietaMamadeira,
			RapServidores rapServidoresByAnuAmaSerFk1, AnuTipoAcucar anuTipoAcucar, RapServidores rapServidoresByAnuAmaSerFk3,
			RapServidores rapServidoresByAnuAmaSerFk2, Date criadoEm, Date dthrInicio, Float percentual, Integer qtdeGramas,
			Date dthrFim, String indSuspenso, Date alteradoEm) {
		this.id = id;
		this.anuDietaMamadeira = anuDietaMamadeira;
		this.rapServidoresByAnuAmaSerFk1 = rapServidoresByAnuAmaSerFk1;
		this.anuTipoAcucar = anuTipoAcucar;
		this.rapServidoresByAnuAmaSerFk3 = rapServidoresByAnuAmaSerFk3;
		this.rapServidoresByAnuAmaSerFk2 = rapServidoresByAnuAmaSerFk2;
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
			@AttributeOverride(name = "tiaSeq", column = @Column(name = "TIA_SEQ", nullable = false)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false)) })
	public AnuAcucarMamadeiraId getId() {
		return this.id;
	}

	public void setId(AnuAcucarMamadeiraId id) {
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
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidoresByAnuAmaSerFk1() {
		return this.rapServidoresByAnuAmaSerFk1;
	}

	public void setRapServidoresByAnuAmaSerFk1(RapServidores rapServidoresByAnuAmaSerFk1) {
		this.rapServidoresByAnuAmaSerFk1 = rapServidoresByAnuAmaSerFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TIA_SEQ", nullable = false, insertable = false, updatable = false)
	public AnuTipoAcucar getAnuTipoAcucar() {
		return this.anuTipoAcucar;
	}

	public void setAnuTipoAcucar(AnuTipoAcucar anuTipoAcucar) {
		this.anuTipoAcucar = anuTipoAcucar;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_SUSPENSO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_SUSPENSO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuAmaSerFk3() {
		return this.rapServidoresByAnuAmaSerFk3;
	}

	public void setRapServidoresByAnuAmaSerFk3(RapServidores rapServidoresByAnuAmaSerFk3) {
		this.rapServidoresByAnuAmaSerFk3 = rapServidoresByAnuAmaSerFk3;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuAmaSerFk2() {
		return this.rapServidoresByAnuAmaSerFk2;
	}

	public void setRapServidoresByAnuAmaSerFk2(RapServidores rapServidoresByAnuAmaSerFk2) {
		this.rapServidoresByAnuAmaSerFk2 = rapServidoresByAnuAmaSerFk2;
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
		RAP_SERVIDORES_BY_ANU_AMA_SER_FK1("rapServidoresByAnuAmaSerFk1"),
		ANU_TIPO_ACUCARES("anuTipoAcucar"),
		RAP_SERVIDORES_BY_ANU_AMA_SER_FK3("rapServidoresByAnuAmaSerFk3"),
		RAP_SERVIDORES_BY_ANU_AMA_SER_FK2("rapServidoresByAnuAmaSerFk2"),
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
		if (!(obj instanceof AnuAcucarMamadeira)) {
			return false;
		}
		AnuAcucarMamadeira other = (AnuAcucarMamadeira) obj;
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
