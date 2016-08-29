package br.gov.mec.aghu.model;

// Generated 26/02/2010 17:37:25 by Hibernate Tools 3.2.5.Beta

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioFormaRupturaBolsaRota;
import br.gov.mec.aghu.dominio.DominioLiquidoAmniotico;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
@Table(name = "MCO_BOLSA_ROTAS", schema = "AGH")
public class McoBolsaRotas extends BaseEntityId<McoGestacoesId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1919584594517081983L;
	private McoGestacoesId id;
	private McoGestacoes mcoGestacoes;
	private Date dthrRompimento;
	private String formaRuptura;
	private DominioFormaRupturaBolsaRota dominioFormaRuptura;
	private Boolean indAmnioscopia;
	private Boolean indDataHoraIgnorada;
	private DominioLiquidoAmniotico liquidoAmniotico;
	private Boolean indOdorFetido;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer version;

	public McoBolsaRotas() {
	}

	public McoBolsaRotas(McoGestacoes mcoGestacoes, Date criadoEm,
			Integer serMatricula, Short serVinCodigo) {
		this.mcoGestacoes = mcoGestacoes;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	public McoBolsaRotas(McoGestacoes mcoGestacoes, Date dthrRompimento,
			String formaRuptura, Boolean indAmnioscopia,
			DominioLiquidoAmniotico liquidoAmniotico, Boolean indOdorFetido, Date criadoEm,
			Integer serMatricula, Short serVinCodigo) {
		this.mcoGestacoes = mcoGestacoes;
		this.dthrRompimento = dthrRompimento;
		this.formaRuptura = formaRuptura;
		this.indAmnioscopia = indAmnioscopia;
		this.liquidoAmniotico = liquidoAmniotico;
		this.indOdorFetido = indOdorFetido;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	public McoBolsaRotas(McoGestacoesId id) {
		this.setId(id);
	}
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "pacCodigo", column = @Column(name = "GSO_PAC_CODIGO", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "GSO_SEQP", nullable = false, precision = 3, scale = 0)) })
	public McoGestacoesId getId() {
		return this.id;
	}

	public void setId(McoGestacoesId id) {
		this.id = id;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	//@PrimaryKeyJoinColumns({@PrimaryKeyJoinColumn(name="GSO_PAC_CODIGO",referencedColumnName="PAC_CODIGO"),@PrimaryKeyJoinColumn(name="GSO_SEQP",referencedColumnName="SEQP")})
	// @JoinColumns({@JoinColumn(name="GSO_PAC_CODIGO",referencedColumnName="PAC_CODIGO"),@JoinColumn(name="GSO_SEQP",referencedColumnName="SEQP")})
	public McoGestacoes getMcoGestacoes() {
		return this.mcoGestacoes;
	}

	public void setMcoGestacoes(McoGestacoes mcoGestacoes) {
		this.mcoGestacoes = mcoGestacoes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ROMPIMENTO")
	public Date getDthrRompimento() {
		return this.dthrRompimento;
	}

	public void setDthrRompimento(Date dthrRompimento) {
		this.dthrRompimento = dthrRompimento;
	}

	@Column(name = "FORMA_RUPTURA", length = 15, insertable=false, updatable=false)
	@Length(max = 15)
	public String getFormaRuptura() {
		return this.formaRuptura;
	}

	public void setFormaRuptura(String formaRuptura) {
		this.formaRuptura = formaRuptura;
	}

	@Column(name = "IND_AMNIOSCOPIA")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAmnioscopia() {
		return this.indAmnioscopia;
	}

	public void setIndAmnioscopia(Boolean indAmnioscopia) {
		this.indAmnioscopia = indAmnioscopia;
	}

	@Column(name = "IND_DTHR_IGNORADA")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndDataHoraIgnorada() {
		return indDataHoraIgnorada;
	}

	public void setIndDataHoraIgnorada(Boolean indDataHoraIgnorada) {
		this.indDataHoraIgnorada = indDataHoraIgnorada;
	}
	
	@Column(name = "LIQUIDO_AMNIOTICO")
	@Enumerated(EnumType.STRING)
	public DominioLiquidoAmniotico getLiquidoAmniotico() {
		return this.liquidoAmniotico;
	}

	public void setLiquidoAmniotico(DominioLiquidoAmniotico liquidoAmniotico) {
		this.liquidoAmniotico = liquidoAmniotico;
	}

	@Column(name = "IND_ODOR_FETIDO")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndOdorFetido() {
		return this.indOdorFetido;
	}

	public void setIndOdorFetido(Boolean indOdorFetido) {
		this.indOdorFetido = indOdorFetido;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	// FIXME Tocchetto Validar com o JH a problema da PK e corrigir a enum FIELDS de acordo com esse ajuste.
//	public enum Fields {
//		CODIGO_PACIENTE("id.pacCodigo"), 
//		SEQUENCE("id.seqp");       
//
//		private String fields;
//
//		private Fields(String fields) {
//			this.fields = fields;
//		}
//
//		@Override
//		public String toString() {
//			return this.fields;
//		}
//	}	

	public enum Fields {

		ID("id"),
		ID_CODIGO_PACIENTE("id.pacCodigo"), 
		ID_SEQUENCE("id.seqp"),
		MCO_GESTACOES("mcoGestacoes"),
		DTHR_ROMPIMENTO("dthrRompimento"),
		FORMA_RUPTURA("formaRuptura"),
		DOMINIO_FORMA_RUPTURA("dominioFormaRuptura"),
		IND_AMNIOSCOPIA("indAmnioscopia"),
		LIQUIDO_AMNIOTICO("liquidoAmniotico"),
		IND_ODOR_FETIDO("indOdorFetido"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

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
		if (!(obj instanceof McoBolsaRotas)) {
			return false;
		}
		McoBolsaRotas other = (McoBolsaRotas) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	@Column(name = "FORMA_RUPTURA")
	@Enumerated(EnumType.STRING)
	public DominioFormaRupturaBolsaRota getDominioFormaRuptura() {
		return dominioFormaRuptura;
	}

	public void setDominioFormaRuptura(
			DominioFormaRupturaBolsaRota dominioFormaRuptura) {
		this.dominioFormaRuptura = dominioFormaRuptura;
	}
}
