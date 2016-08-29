package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

@Entity
@Table(name = "MBC_PADRAO_FARMACO_FICHAS", schema = "AGH")
public class MbcPadraoFarmacoFicha extends BaseEntityCodigo<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = 5777115480168579476L;
	private Integer medMatCodigo;
	private Integer version;
	private RapServidores rapServidoresByMbcPffSerFk1;
	private AfaMedicamento afaMedicamento;
	private RapServidores rapServidoresByMbcPffSerFk2;
	private MpmUnidadeMedidaMedica mpmUnidadeMedidaMedica;
	private AfaViaAdministracao afaViaAdministracao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private Date alteradoEm;

	public MbcPadraoFarmacoFicha() {
	}

	public MbcPadraoFarmacoFicha(RapServidores rapServidoresByMbcPffSerFk1, AfaMedicamento afaMedicamento,
			MpmUnidadeMedidaMedica mpmUnidadeMedidaMedica, DominioSituacao indSituacao, Date criadoEm) {
		this.rapServidoresByMbcPffSerFk1 = rapServidoresByMbcPffSerFk1;
		this.afaMedicamento = afaMedicamento;
		this.mpmUnidadeMedidaMedica = mpmUnidadeMedidaMedica;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	public MbcPadraoFarmacoFicha(RapServidores rapServidoresByMbcPffSerFk1, AfaMedicamento afaMedicamento,
			RapServidores rapServidoresByMbcPffSerFk2, MpmUnidadeMedidaMedica mpmUnidadeMedidaMedica,
			AfaViaAdministracao afaViaAdministracao, DominioSituacao indSituacao, Date criadoEm, Date alteradoEm) {
		this.rapServidoresByMbcPffSerFk1 = rapServidoresByMbcPffSerFk1;
		this.afaMedicamento = afaMedicamento;
		this.rapServidoresByMbcPffSerFk2 = rapServidoresByMbcPffSerFk2;
		this.mpmUnidadeMedidaMedica = mpmUnidadeMedidaMedica;
		this.afaViaAdministracao = afaViaAdministracao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
	}

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "afaMedicamento"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "MED_MAT_CODIGO", unique = true, nullable = false)
	public Integer getMedMatCodigo() {
		return this.medMatCodigo;
	}

	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
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
	public RapServidores getRapServidoresByMbcPffSerFk1() {
		return this.rapServidoresByMbcPffSerFk1;
	}

	public void setRapServidoresByMbcPffSerFk1(RapServidores rapServidoresByMbcPffSerFk1) {
		this.rapServidoresByMbcPffSerFk1 = rapServidoresByMbcPffSerFk1;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public AfaMedicamento getAfaMedicamento() {
		return this.afaMedicamento;
	}

	public void setAfaMedicamento(AfaMedicamento afaMedicamento) {
		this.afaMedicamento = afaMedicamento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_ALTERA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByMbcPffSerFk2() {
		return this.rapServidoresByMbcPffSerFk2;
	}

	public void setRapServidoresByMbcPffSerFk2(RapServidores rapServidoresByMbcPffSerFk2) {
		this.rapServidoresByMbcPffSerFk2 = rapServidoresByMbcPffSerFk2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMM_SEQ", nullable = false)
	public MpmUnidadeMedidaMedica getMpmUnidadeMedidaMedica() {
		return this.mpmUnidadeMedidaMedica;
	}

	public void setMpmUnidadeMedidaMedica(MpmUnidadeMedidaMedica mpmUnidadeMedidaMedica) {
		this.mpmUnidadeMedidaMedica = mpmUnidadeMedidaMedica;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VAD_SIGLA")
	public AfaViaAdministracao getAfaViaAdministracao() {
		return this.afaViaAdministracao;
	}

	public void setAfaViaAdministracao(AfaViaAdministracao afaViaAdministracao) {
		this.afaViaAdministracao = afaViaAdministracao;
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
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	public enum Fields {

		MED_MAT_CODIGO("medMatCodigo"),
		VERSION("version"),
		RAP_SERVIDORES_BY_MBC_PFF_SER_FK1("rapServidoresByMbcPffSerFk1"),
		AFA_MEDICAMENTO("afaMedicamento"),
		RAP_SERVIDORES_BY_MBC_PFF_SER_FK2("rapServidoresByMbcPffSerFk2"),
		MPM_UNIDADE_MEDIDA_MEDICA("mpmUnidadeMedidaMedica"),
		AFA_VIA_ADMINISTRACAO("afaViaAdministracao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
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
		result = prime * result + ((getMedMatCodigo() == null) ? 0 : getMedMatCodigo().hashCode());
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
		if (!(obj instanceof MbcPadraoFarmacoFicha)) {
			return false;
		}
		MbcPadraoFarmacoFicha other = (MbcPadraoFarmacoFicha) obj;
		if (getMedMatCodigo() == null) {
			if (other.getMedMatCodigo() != null) {
				return false;
			}
		} else if (!getMedMatCodigo().equals(other.getMedMatCodigo())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
 
 @Transient public Integer getCodigo(){ return this.getMedMatCodigo();} 
 public void setCodigo(Integer codigo){ this.setMedMatCodigo(codigo);}
}
