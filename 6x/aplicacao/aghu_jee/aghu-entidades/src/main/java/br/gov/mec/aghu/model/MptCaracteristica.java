package br.gov.mec.aghu.model;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptCarSq1", sequenceName="AGH.MPT_CAR_SQ1", allocationSize = 1)
@Table(name = "MPT_CARACTERISTICA", schema = "AGH")

public class MptCaracteristica extends BaseEntitySeq<Short> implements java.io.Serializable {
	
	private static final long serialVersionUID = 5528459924325186402L;
	
	private Short seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private String sigla;
	private Date criadoEm;
	private RapServidores servidor;
	
	private Set<MptCaracteristicaTipoSessao> listMptCaracteristicaTipoSessao = new HashSet<MptCaracteristicaTipoSessao>();
	
	public MptCaracteristica() {
		
	}

	public MptCaracteristica(Short seq, String descricao, DominioSituacao indSituacao,
			String sigla, Date criadoEm, RapServidores servidor) {
		this.seq = seq;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptCarSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}
	
	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "SIGLA", nullable = false, length = 4)
	@Length(max = 4)
	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
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

	public void setServidor(
			RapServidores servidor) {
		this.servidor = servidor;
	}

	@OneToMany(mappedBy = "mptCaracteristica", fetch = FetchType.LAZY)
	public Set<MptCaracteristicaTipoSessao> getListMptCaracteristicaTipoSessao() {
		return listMptCaracteristicaTipoSessao;
	}

	public void setListMptCaracteristicaTipoSessao(
			Set<MptCaracteristicaTipoSessao> listMptCaracteristicaTipoSessao) {
		this.listMptCaracteristicaTipoSessao = listMptCaracteristicaTipoSessao;
	}
	
	public enum Fields {
		
		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		SIGLA("sigla"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo"),
		SERVIDOR("servidor"),
		LIST_CARACTERISTICA_TIPOS_SESSAO("listMptCaracteristicaTipoSessao");
		
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
		MptCaracteristica other = (MptCaracteristica) obj;
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
