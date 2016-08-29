package br.gov.mec.aghu.model;

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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoEvolucao;

@Entity
@SequenceGenerator(name = "mpmEvoSeq", sequenceName = "AGH.mpm_evo_sq1", allocationSize = 1)
@Table(name = "MPM_EVOLUCOES", schema = "AGH")
public class MpmEvolucoes extends BaseEntitySeq<Long> implements
		java.io.Serializable {
	/**

     * 

     */

	private static final long serialVersionUID = -3638660513556361007L;
	private Long seq;
	private Date dthrCriacao;
	private Date dthrAlteracao;
	private Date dthrReferencia;
	private Date dthrFim;
	private Date dthrPendente;
	private DominioIndPendenteAmbulatorio pendente;
	private MamTipoItemEvolucao tipoItemEvolucao;
	private String descricao;
	private RapServidores servidor;
	private MpmAnamneses anamnese;
	private DominioSituacaoEvolucao situacao;
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpmEvoSeq")
	@Column(name = "SEQ", nullable = false, precision = 14, scale = 0)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_CRIACAO", nullable = false, length = 7)
	@NotNull
	public Date getDthrCriacao() {
		return this.dthrCriacao;
	}

	public void setDthrCriacao(Date dthrCriacao) {
		this.dthrCriacao = dthrCriacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ALTERACAO", length = 7)
	public Date getDthrAlteracao() {
		return dthrAlteracao;
	}

	public void setDthrAlteracao(Date dthrAlteracao) {
		this.dthrAlteracao = dthrAlteracao;
	}

	@Column(name = "IND_PENDENTE", nullable = false, length = 1)
	@NotNull
	@Enumerated(EnumType.STRING)
	public DominioIndPendenteAmbulatorio getPendente() {
		return this.pendente;
	}

	public void setPendente(DominioIndPendenteAmbulatorio pendente) {
		this.pendente = pendente;
	}

	@Column(name = "IND_USO", nullable = false, length = 1)
	@NotNull
	@Enumerated(EnumType.STRING)
	public DominioSituacaoEvolucao getSituacao() {
		return this.situacao;
	}

	@Column(name = "DESCRICAO", length = 12000)
	@Length(max = 12000)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setSituacao(DominioSituacaoEvolucao situacao) {
		this.situacao = situacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@NotNull
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ANA_SEQ", nullable = false)
	public MpmAnamneses getAnamnese() {
		return anamnese;
	}

	public void setAnamnese(MpmAnamneses anamnese) {
		this.anamnese = anamnese;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TIE_SEQ")
	public MamTipoItemEvolucao getTipoItemEvolucao() {
		return tipoItemEvolucao;
	}

	public void setTipoItemEvolucao(MamTipoItemEvolucao tipoItemEvolucao) {
		this.tipoItemEvolucao = tipoItemEvolucao;
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
	@Column(name = "DTHR_FIM", length = 7, nullable = false)
	@NotNull
	public Date getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_REFERENCIA", length = 7, nullable = false)
	@NotNull
	public Date getDthrReferencia() {
		return dthrReferencia;
	}

	public void setDthrReferencia(Date dthrReferencia) {
		this.dthrReferencia = dthrReferencia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_PENDENTE", length = 7)
	public Date getDthrPendente() {
		return dthrPendente;
	}

	public void setDthrPendente(Date dthrPendente) {
		this.dthrPendente = dthrPendente;
	}

	public enum Fields {
		IND_PENDENTE("pendente"), DTHR_CRIACAO("dthrCriacao"), PENDENTE("pendente"),
		DTHR_MOVIMENTO("dthrMvto"), SEQ("seq"), DTHR_REFERENCIA("dthrReferencia"),
		ITEM_TIPO_EVOLUCAO("tipoItemEvolucao"), DTHR_VALIDA("dthrValida"),
		ANAMNESE("anamnese"), ANA_SEQ("anamnese.seq"), SERVIDOR("servidor"),
		DTHR_FIM("dthrFim"), DTHR_ALTERACAO("dthrAlteracao"),
		DTHR_PENDENTE("dthrPendente");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof MpmEvolucoes)) {
			return false;
		}
		MpmEvolucoes other = (MpmEvolucoes) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}

}