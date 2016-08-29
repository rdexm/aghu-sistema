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
import javax.persistence.Version;
import javax.validation.constraints.Max;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAgenda;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptTpsSq1", sequenceName="AGH.MPT_TPS_SQ1", allocationSize = 1)
@Table(name = "MPT_TIPO_SESSAO", schema = "AGH")
public class MptTipoSessao extends BaseEntitySeq<Short> implements java.io.Serializable {
	
	private static final long serialVersionUID = 7136877534967025517L;
	
	private Short seq;
	private String descricao;
	private RapServidores servidor;
	private AghUnidadesFuncionais unidadeFuncional;
	private DominioTipoAgenda tipoAgenda;
	private Date tempoFixo;
	private Integer tempoDisponivel;
	private Boolean indApac;
	private Boolean indConsentimento;
	private Boolean indFrequencia;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private Integer version; 
	private Set<MptSalas> salas;
	private Set<MptTurnoTipoSessao> listMptTurnoTipoSessao = new HashSet<MptTurnoTipoSessao>();
	private String aviso;
	
	public MptTipoSessao() {
	}

	public MptTipoSessao(Short seq, String descricao, RapServidores servidor, AghUnidadesFuncionais unidadeFuncional,
			DominioTipoAgenda tipoAgenda, Date tempoFixo, Integer tempoDisponivel, Boolean indApac, Boolean indConsentimento,
			Boolean indFrequencia, Date criadoEm, DominioSituacao indSituacao) {
		this.seq = seq;
		this.descricao = descricao;
		this.servidor = servidor;
		this.unidadeFuncional = unidadeFuncional;
		this.tipoAgenda = tipoAgenda;
		this.tempoFixo = tempoFixo;
		this.tempoDisponivel = tempoDisponivel;
		this.indApac = indApac;
		this.indConsentimento = indConsentimento;
		this.indFrequencia = indFrequencia;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptTpsSq1")
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
	@JoinColumn(name = "UNF_SEQ", nullable = false)
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return this.unidadeFuncional;
	}

	public void setUnidadeFuncional(
			AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	@Column(name = "TIPO_AGENDA", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoAgenda getTipoAgenda() {
		return tipoAgenda;
	}

	public void setTipoAgenda(DominioTipoAgenda tipoAgenda) {
		this.tipoAgenda = tipoAgenda;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TEMPO_FIXO", length = 7)
	public Date getTempoFixo() {
		return tempoFixo;
	}

	public void setTempoFixo(Date tempoFixo) {
		this.tempoFixo = tempoFixo;
	}

	
	@Column(name = "TEMPO_DISPONIVEL", length = 3)
	@Max(value = 365, message = "Valor máximo permitido para o campo Disponibilidade: 365")
	public Integer getTempoDisponivel() {
		return tempoDisponivel;
	}

	public void setTempoDisponivel(Integer tempoDisponivel) {
		this.tempoDisponivel = tempoDisponivel;
	}

	@Column(name = "IND_APAC", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndApac() {
		return indApac;
	}

	public void setIndApac(Boolean indApac) {
		this.indApac = indApac;
	}

	@Column(name = "IND_CONSENTIMENTO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndConsentimento() {
		return indConsentimento;
	}

	public void setIndConsentimento(Boolean indConsentimento) {
		this.indConsentimento = indConsentimento;
	}

	@Column(name = "IND_FREQUENCIA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndFrequencia() {
		return indFrequencia;
	}

	public void setIndFrequencia(Boolean indFrequencia) {
		this.indFrequencia = indFrequencia;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoSessao")
	public Set<MptTurnoTipoSessao> getListMptTurnoTipoSessao() {
		return listMptTurnoTipoSessao;
	}

	public void setListMptTurnoTipoSessao(
			Set<MptTurnoTipoSessao> listMptTurnoTipoSessao) {
		this.listMptTurnoTipoSessao = listMptTurnoTipoSessao;
	}
	

	public enum Fields {
		
		SEQ("seq"),
		UNIDADE_FUNCIONAL("unidadeFuncional"),
		UNF_SEQ("unidadeFuncional.seq"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo"),
		TIPO_AGENDA("tipoAgenda"),
		TEMPO_FIXO("tempoFixo"),
		TEMPO_DISPONIVEL("tempoDisponivel"),
		IND_APAC("indApac"),
		IND_CONSENTIMENTO("indConsentimento"),
		IND_FREQUENCIA("indFrequencia"),
		IND_SITUACAO("indSituacao"),
		RNI_SEQP("rniSeqp"),
		SALAS("salas"),
		TURNO_TIPO_SESSAO("listMptTurnoTipoSessao"),
		AVISO("aviso")
		;
		
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
		MptTipoSessao other = (MptTipoSessao) obj;
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

	@OneToMany(mappedBy = "tipoSessao", fetch = FetchType.LAZY)
	public Set<MptSalas> getSalas() {
		return salas;
}

	public void setSalas(Set<MptSalas> salas) {
		this.salas = salas;
	}

	@Column(name = "AVISO", length = 200)
	@Length(max = 200)
	public String getAviso() {
		return aviso;
	}

	public void setAviso(String aviso) {
		this.aviso = aviso;
	}
}
