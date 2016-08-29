package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MBC_CONTROLE_ESCALA_CIRURGICAS", schema = "AGH")
public class MbcControleEscalaCirurgica extends BaseEntityId<MbcControleEscalaCirurgicaId> implements java.io.Serializable {

	private static final long serialVersionUID = 181483593195134275L;
	private MbcControleEscalaCirurgicaId id;
	private Integer version;
	private RapServidores rapServidores;
	private AghUnidadesFuncionais aghUnidadesFuncionais;
	private Date dthrGeracaoEscala;
	private DominioTipoEscala tipoEscala;

	public MbcControleEscalaCirurgica() {
	}

	public MbcControleEscalaCirurgica(MbcControleEscalaCirurgicaId id, RapServidores rapServidores,
			AghUnidadesFuncionais aghUnidadesFuncionais, Date dthrGeracaoEscala, DominioTipoEscala tipoEscala) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.aghUnidadesFuncionais = aghUnidadesFuncionais;
		this.dthrGeracaoEscala = dthrGeracaoEscala;
		this.tipoEscala = tipoEscala;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "unfSeq", column = @Column(name = "UNF_SEQ", nullable = false)),
			@AttributeOverride(name = "dtEscala", column = @Column(name = "DT_ESCALA", nullable = false, length = 29)) })
	public MbcControleEscalaCirurgicaId getId() {
		return this.id;
	}

	public void setId(MbcControleEscalaCirurgicaId id) {
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
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNF_SEQ", nullable = false, insertable = false, updatable = false)
	public AghUnidadesFuncionais getAghUnidadesFuncionais() {
		return this.aghUnidadesFuncionais;
	}

	public void setAghUnidadesFuncionais(AghUnidadesFuncionais aghUnidadesFuncionais) {
		this.aghUnidadesFuncionais = aghUnidadesFuncionais;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_GERACAO_ESCALA", nullable = false, length = 29)
	public Date getDthrGeracaoEscala() {
		return this.dthrGeracaoEscala;
	}

	public void setDthrGeracaoEscala(Date dthrGeracaoEscala) {
		this.dthrGeracaoEscala = dthrGeracaoEscala;
	}

	public enum Fields {

		ID("id"),
		UNF_SEQ("id.unfSeq"),
		DT_ESCALA("id.dtEscala"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		AGH_UNIDADES_FUNCIONAIS("aghUnidadesFuncionais"),
		DTHR_GERACAO_ESCALA("dthrGeracaoEscala"),
		TIPO_ESCALA("tipoEscala");

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
		if (!(obj instanceof MbcControleEscalaCirurgica)) {
			return false;
		}
		MbcControleEscalaCirurgica other = (MbcControleEscalaCirurgica) obj;
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

	@Column(name = "TIPO_ESCALA", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoEscala getTipoEscala() {
		return tipoEscala;
	}

	public void setTipoEscala(DominioTipoEscala tipoEscala) {
		this.tipoEscala = tipoEscala;
	}

}
