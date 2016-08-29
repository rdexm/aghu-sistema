package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.model.BaseJournal;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoEvolucao;


@Entity
@Table(name = "MPM_EVOLUCOES_JN", schema = "AGH")
@SequenceGenerator(name = "mpmEvoJnSeq", sequenceName = "AGH.mpm_evo_jn_sq1", allocationSize = 1)
@Immutable
public class MpmEvolucoesJn extends BaseJournal implements java.io.Serializable {

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
	private Integer tipoItemEvolucao;
	private String descricao;
	private Integer servidorMatricula;
	private Short servidorVinCodigo;
	private Long anamnese;
	private DominioSituacaoEvolucao situacao;

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpmEvoJnSeq")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ")
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

	@Column(name = "IND_PENDENTE")
	@Enumerated(EnumType.STRING)
	public DominioIndPendenteAmbulatorio getPendente() {
		return this.pendente;
	}

	public void setPendente(DominioIndPendenteAmbulatorio pendente) {
		this.pendente = pendente;
	}

	@Column(name = "IND_USO")
	@Enumerated(EnumType.STRING)
	public DominioSituacaoEvolucao getSituacao() {
		return this.situacao;
	}

	@Column(name = "DESCRICAO")
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setSituacao(DominioSituacaoEvolucao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "SER_MATRICULA")
	public Integer getServidorMatricula() {
		return this.servidorMatricula;
	}

	public void setServidorMatricula(Integer servidorMatricula) {
		this.servidorMatricula = servidorMatricula;
	}

	@Column(name = "SER_VIN_CODIGO")
	public Short getServidorVinCodigo() {
		return this.servidorVinCodigo;
	}

	public void setServidorVinCodigo(Short servidorVinCodigo) {
		this.servidorVinCodigo = servidorVinCodigo;
	}

	@Column(name = "ANA_SEQ")
	public Long getAnamnese() {
		return anamnese;
	}

	public void setAnamnese(Long anamnese) {
		this.anamnese = anamnese;
	}

	@Column(name = "TIE_SEQ")
	public Integer getTipoItemEvolucao() {
		return tipoItemEvolucao;
	}

	public void setTipoItemEvolucao(Integer tipoItemEvolucao) {
		this.tipoItemEvolucao = tipoItemEvolucao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM", length = 7, nullable = false)
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
		IND_PENDENTE("pendente"),
		DTHR_CRIACAO("dthrCriacao"),
		PENDENTE("pendente"),
		DTHR_MOVIMENTO("dthrMvto"),
		SEQ("seq"),
		ITEM_TIPO_EVOLUCAO("tipoItemEvolucao"),
		DTHR_VALIDA("dthrValida"),
		ANAMNESE("anamnese"),
		DTHR_FIM("dthrFim"),
		DTHR_ALTERACAO("dthrAlteracao"),
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

}