package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioIndRespAvaliacao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.exception.BaseRuntimeExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * Entidade que representa a tabela AFA_GRUPO_USO_MDTOS
 * 
 * @author lcmoura
 * 
 */
@Entity
@SequenceGenerator(name="afaGupSq1", sequenceName="AGH.AFA_GUP_SQ1", allocationSize = 1)
@Table(name = "AFA_GRUPO_USO_MDTOS", schema = "AGH")
public class AfaGrupoUsoMedicamento extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4335446565479995687L;
	private Integer seq;
	private RapServidores servidor;
	private String descricao;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private DominioIndRespAvaliacao indResponsavelAvaliacao;

	private Set<AfaTipoUsoMdto> tiposDeUsoMedicamento = new HashSet<AfaTipoUsoMdto>(
			0);

	public AfaGrupoUsoMedicamento() {

	}

	public AfaGrupoUsoMedicamento(Integer seq) {
		this.seq = seq;
	}

	public AfaGrupoUsoMedicamento(Integer seq, String descricao,
			DominioIndRespAvaliacao indResponsavelAvaliacao) {
		this.seq = seq;
		this.descricao = descricao;
		this.indResponsavelAvaliacao = indResponsavelAvaliacao;
	}

	public AfaGrupoUsoMedicamento(Integer seq, String descricao,
			DominioSituacao indSituacao) {
		this.seq = seq;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
	}

	public AfaGrupoUsoMedicamento(Integer seq, String descricao,
			DominioSituacao indSituacao,
			DominioIndRespAvaliacao indResponsavelAvaliacao) {
		this.seq = seq;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.indResponsavelAvaliacao = indResponsavelAvaliacao;
	}

	public AfaGrupoUsoMedicamento(Integer seq, RapServidores servidor,
			String descricao, Date criadoEm, DominioSituacao indSituacao,
			DominioIndRespAvaliacao indResponsavelAvaliacao) {
		this.seq = seq;
		this.servidor = servidor;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.indResponsavelAvaliacao = indResponsavelAvaliacao;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "afaGupSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 4, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return descricao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	@Column(name = "IND_RESP_AVALIACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioIndRespAvaliacao getIndResponsavelAvaliacao() {
		return indResponsavelAvaliacao;
	}

	// SETS
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public void setIndResponsavelAvaliacao(
			DominioIndRespAvaliacao indResponsavelAvaliacao) {
		this.indResponsavelAvaliacao = indResponsavelAvaliacao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "grupoUsoMedicamento")
	public Set<AfaTipoUsoMdto> getTiposDeUsoMedicamento() {
		return tiposDeUsoMedicamento;
	}

	public void setTiposDeUsoMedicamento(
			Set<AfaTipoUsoMdto> tiposDeUsoMedicamento) {
		this.tiposDeUsoMedicamento = tiposDeUsoMedicamento;
	}

	// FIELDS
	public enum Fields {
		SEQ("seq"), SERVIDOR("servidor"), DESCRICAO("descricao"), CRIADOEM(
				"criadoEm"), SITUACAO("indSituacao"), RESPONSAVEL_AVALIACAO(
				"indResponsavelAvaliacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@SuppressWarnings("unused")
	@PreRemove
	private void validaRemove() {
		if (tiposDeUsoMedicamento != null && tiposDeUsoMedicamento.size() > 0) {
			throw new BaseRuntimeException(BaseRuntimeExceptionCode.OFG_00005);
		}
	}

	// EQUALS e HASHCODE
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		AfaGrupoUsoMedicamento other = (AfaGrupoUsoMedicamento) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
}
