package br.gov.mec.aghu.model;

// Generated 14/02/2012 11:33:00 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

/**
 * AelEquipamentosJn generated by hbm2java
 */
@Entity
@Table(name = "AEL_EQUIPAMENTOS_JN", schema = "AGH")
@SequenceGenerator(name = "aelEqujJnSeq", sequenceName = "AGH.AEL_EQU_jn_seq", allocationSize = 1)
@Immutable
public class AelEquipamentosJn extends BaseJournal{

	/**
	 * 
	 */
	private static final long serialVersionUID = 538259774399653445L;
	private Short seq;
	private String descricao;
	private AghUnidadesFuncionais unidadeFuncional;
	private DominioSituacao situacao;
	private Boolean possuiInterface;
	private Date criadoEm;
	private RapServidores servidor;
	private String driverId;
	private Boolean cargaAutomatica;

	public AelEquipamentosJn() {
		super();
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aelEqujJnSeq")
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ", nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 45)
	@Length(max = 45)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="UNF_SEQ", insertable=false, updatable=false)
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "IND_POSSUI_INTERFACE", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getPossuiInterface() {
		return this.possuiInterface;
	}

	public void setPossuiInterface(Boolean possuiInterface) {
		this.possuiInterface = possuiInterface;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "DRIVER_ID", length = 5)
	@Length(max = 5)
	public String getDriverId() {
		return this.driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	@Column(name = "IND_CARGA_AUTOMATICA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getCargaAutomatica() {
		return this.cargaAutomatica;
	}

	public void setCargaAutomatica(Boolean cargaAutomatica) {
		this.cargaAutomatica = cargaAutomatica;
	}

	public enum Fields {

		SEQ("seq"),
		DESCRICAO("descricao"),
		UNIDADE_FUNCIONAL("unidadeFuncional"),
		SITUACAO("situacao"),
		POSSUI_INTERFACE("possuiInterface"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		DRIVER_ID("driverId"),
		CARGA_AUTOMATICA("cargaAutomatica");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}
