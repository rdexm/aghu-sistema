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
import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;

@Entity
@SequenceGenerator(name = "mpmNaeSeq", sequenceName = "AGH.mpm_nae_sq1", allocationSize = 1)
@Table(name = "MPM_NOTA_ADICIONAL_EVOLUCOES", schema = "AGH")
public class MpmNotaAdicionalEvolucoes extends BaseEntitySeq<Integer> implements
		java.io.Serializable {

	private static final long serialVersionUID = 84162638996927628L;
	private Integer seq;
	private Date dthrCriacao;
	private Date dthrAlteracao;
	private String descricao;
	private DominioIndPendenteAmbulatorio pendente;
	private MpmEvolucoes mpmEvolucoes;
	private RapServidores servidor;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpmNaeSeq")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 14, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_CRIACAO", nullable = false, length = 7)
	public Date getDthrCriacao() {
		return this.dthrCriacao;
	}

	public void setDthrCriacao(Date dthrCriacao) {
		this.dthrCriacao = dthrCriacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ALTERACAO", length = 7)
	public Date getDthrAlteracao() {
		return this.dthrAlteracao;
	}

	public void setDthrAlteracao(Date dthrAlteracao) {
		this.dthrAlteracao = dthrAlteracao;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 4000)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_PENDENTE", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioIndPendenteAmbulatorio getPendente() {
		return this.pendente;
	}

	public void setPendente(DominioIndPendenteAmbulatorio pendente) {
		this.pendente = pendente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EVO_SEQ")
	public MpmEvolucoes getMpmEvolucoes() {
		return mpmEvolucoes;
	}

	public void setMpmEvolucoes(MpmEvolucoes mpmEvolucoes) {
		this.mpmEvolucoes = mpmEvolucoes;
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

	public enum Fields {
		DTHR_CRIACAO("dthrCriacao"),
		PENDENTE("pendente"),
		SEQ("seq"),
		EVOLUCOES_SEQ("mpmEvolucoes.seq");
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
		if (!(obj instanceof MpmNotaAdicionalEvolucoes)) {
			return false;
		}
		MpmNotaAdicionalEvolucoes other = (MpmNotaAdicionalEvolucoes) obj;
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