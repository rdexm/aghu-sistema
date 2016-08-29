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

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MBC_SOLIC_HEMO_CIRG_AGENDADAS", schema = "AGH")
public class MbcSolicHemoCirgAgendada extends BaseEntityId<MbcSolicHemoCirgAgendadaId> implements java.io.Serializable {

	private static final long serialVersionUID = 1480005933327756330L;
	private MbcSolicHemoCirgAgendadaId id;
	private Integer version;
	private MbcCirurgias mbcCirurgias;
	private AbsComponenteSanguineo absComponenteSanguineo;
	private Date criadoEm;
	private Short quantidade;
	private Boolean indLavado;
	private Short qtdeMl;
	private Boolean indImprLaudo;
	private Boolean indFiltrado;
	private Boolean indIrradiado;
	private Boolean indAutoTransfusao;
	private RapServidores servidor;

	public MbcSolicHemoCirgAgendada() {
	}

	public MbcSolicHemoCirgAgendada(MbcSolicHemoCirgAgendadaId id, MbcCirurgias mbcCirurgias,
			AbsComponenteSanguineo absComponenteSanguineo, Date criadoEm, Boolean indLavado, Boolean indImprLaudo,
			Boolean indFiltrado, Boolean indIrradiado, Boolean indAutoTransfusao) {
		this.id = id;
		this.mbcCirurgias = mbcCirurgias;
		this.absComponenteSanguineo = absComponenteSanguineo;
		this.criadoEm = criadoEm;
		this.indLavado = indLavado;
		this.indImprLaudo = indImprLaudo;
		this.indFiltrado = indFiltrado;
		this.indIrradiado = indIrradiado;
		this.indAutoTransfusao = indAutoTransfusao;
	}

	public MbcSolicHemoCirgAgendada(MbcSolicHemoCirgAgendadaId id, MbcCirurgias mbcCirurgias,
			AbsComponenteSanguineo absComponenteSanguineo, Date criadoEm, Short quantidade, Boolean indLavado, Short qtdeMl,
			Boolean indImprLaudo, Boolean indFiltrado, Boolean indIrradiado, Boolean indAutoTransfusao, RapServidores servidor) {
		this.id = id;
		this.mbcCirurgias = mbcCirurgias;
		this.absComponenteSanguineo = absComponenteSanguineo;
		this.criadoEm = criadoEm;
		this.quantidade = quantidade;
		this.indLavado = indLavado;
		this.qtdeMl = qtdeMl;
		this.indImprLaudo = indImprLaudo;
		this.indFiltrado = indFiltrado;
		this.indIrradiado = indIrradiado;
		this.indAutoTransfusao = indAutoTransfusao;
		this.servidor = servidor;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "crgSeq", column = @Column(name = "CRG_SEQ", nullable = false)),
			@AttributeOverride(name = "csaCodigo", column = @Column(name = "CSA_CODIGO", nullable = false, length = 2)) })
	public MbcSolicHemoCirgAgendadaId getId() {
		return this.id;
	}

	public void setId(MbcSolicHemoCirgAgendadaId id) {
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
	@JoinColumn(name = "CRG_SEQ", nullable = false, insertable = false, updatable = false)
	public MbcCirurgias getMbcCirurgias() {
		return this.mbcCirurgias;
	}

	public void setMbcCirurgias(MbcCirurgias mbcCirurgias) {
		this.mbcCirurgias = mbcCirurgias;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CSA_CODIGO", nullable = false, insertable = false, updatable = false)
	public AbsComponenteSanguineo getAbsComponenteSanguineo() {
		return this.absComponenteSanguineo;
	}

	public void setAbsComponenteSanguineo(AbsComponenteSanguineo absComponenteSanguineo) {
		this.absComponenteSanguineo = absComponenteSanguineo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "QUANTIDADE")
	public Short getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}

	@Column(name = "QTDE_ML")
	public Short getQtdeMl() {
		return this.qtdeMl;
	}

	public void setQtdeMl(Short qtdeMl) {
		this.qtdeMl = qtdeMl;
	}

	public enum Fields {

		ID("id"),
		ID_CRG_SEQ("id.crgSeq"),
		ID_CSA_CODIGO("id.csaCodigo"),
		VERSION("version"),
		MBC_CIRURGIAS("mbcCirurgias"),
		ABS_COMPONENTE_SANGUINEO("absComponenteSanguineo"),
		ABS_COMPONENTE_SANGUINEO_CODIGO("absComponenteSanguineo.codigo"),
		CRIADO_EM("criadoEm"),
		QUANTIDADE("quantidade"),
		IND_LAVADO("indLavado"),
		QTDE_ML("qtdeMl"),
		IND_IMPR_LAUDO("indImprLaudo"),
		IND_FILTRADO("indFiltrado"),
		IND_IRRADIADO("indIrradiado"),
		IND_AUTO_TRANSFUSAO("indAutoTransfusao"),
		SER_MATRICULA("servidor.id.matricula"),
		SER_VIN_CODIGO("servidor.id.vinCodigo");

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
		if (!(obj instanceof MbcSolicHemoCirgAgendada)) {
			return false;
		}
		MbcSolicHemoCirgAgendada other = (MbcSolicHemoCirgAgendada) obj;
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

	
	@Column(name = "IND_LAVADO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndLavado() {
		return indLavado;
	}

	public void setIndLavado(Boolean indLavado) {
		this.indLavado = indLavado;
	}

	@Column(name = "IND_IMPR_LAUDO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndImprLaudo() {
		return indImprLaudo;
	}

	public void setIndImprLaudo(Boolean indImprLaudo) {
		this.indImprLaudo = indImprLaudo;
	}

	@Column(name = "IND_FILTRADO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndFiltrado() {
		return indFiltrado;
	}

	public void setIndFiltrado(Boolean indFiltrado) {
		this.indFiltrado = indFiltrado;
	}

	@Column(name = "IND_IRRADIADO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndIrradiado() {
		return indIrradiado;
	}

	public void setIndIrradiado(Boolean indIrradiado) {
		this.indIrradiado = indIrradiado;
	}

	@Column(name = "IND_AUTO_TRANSFUSAO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAutoTransfusao() {
		return indAutoTransfusao;
	}

	public void setIndAutoTransfusao(Boolean indAutoTransfusao) {
		this.indAutoTransfusao = indAutoTransfusao;
	}

	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO")})
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

}
