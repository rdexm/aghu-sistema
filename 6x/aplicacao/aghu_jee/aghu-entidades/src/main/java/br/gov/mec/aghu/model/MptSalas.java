package br.gov.mec.aghu.model;

import java.util.Date;
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
import javax.persistence.Version;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptSalSq1", sequenceName="AGH.MPT_SAL_SQ1", allocationSize = 1)
@Table(name = "MPT_SALAS", schema = "AGH")
public class MptSalas extends BaseEntitySeq<Short> implements java.io.Serializable {
	
	private static final long serialVersionUID = 6314977826593612117L;
	
	private Short seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;
	private AghEspecialidades especialidade;
	private MptTipoSessao tipoSessao;
	private Integer version;
	private Short tpsSeq;
	
	private Set<MptLocalAtendimento> locaisAtendimento;
	
	public MptSalas() {
	}

	public MptSalas(Short seq, String descricao, DominioSituacao indSituacao, Date criadoEm, RapServidores servidor,
			AghEspecialidades especialidade, MptTipoSessao tipoSessao) {
		this.seq = seq;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.especialidade = especialidade;
		this.tipoSessao = tipoSessao;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptSalSq1")
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESP_SEQ", nullable = false)
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TPS_SEQ", nullable = false)
	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}

	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}

	public enum Fields {
		
		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo"),
		SERVIDOR("servidor"),
		ESPECIALIDADE("especialidade"),
		ESP_SEQ("especialidade.seq"),
		TIPO_SESSAO("tipoSessao"),
		TPS_SEQ("tipoSessao.seq"),
		TPSSEQ("tpsSeq"),
		LOCAIS_ATENDIMENTO("locaisAtendimento");
		
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
		MptSalas other = (MptSalas) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(mappedBy = "sala", fetch = FetchType.LAZY)
	public Set<MptLocalAtendimento> getLocaisAtendimento() {
		return locaisAtendimento;
	}

	public void setLocaisAtendimento(Set<MptLocalAtendimento> locaisAtendimento) {
		this.locaisAtendimento = locaisAtendimento;
	}

	@Column(name = "TPS_SEQ", insertable=false, updatable=false)
	public Short getTpsSeq() {
		return tpsSeq;
	}

	public void setTpsSeq(Short tpsSeq) {
		this.tpsSeq = tpsSeq;
	}
}
