package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

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

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mbcNciSq1", sequenceName="AGH.MBC_NCI_SQ1", allocationSize = 1)
@Table(name = "MBC_NECESSIDADE_CIRURGICAS", schema = "AGH")
public class MbcNecessidadeCirurgica extends BaseEntitySeq<Short> implements java.io.Serializable {

	private static final long serialVersionUID = -5901915174092881685L;
	private Short seq;
	private Integer version;
	private AghUnidadesFuncionais aghUnidadesFuncionais;
	private String descricao;
	private DominioSituacao situacao;
	private Date criadoEm;
	private Boolean indExigeDescSolic;
	
	// Este atributo não está mapeado na definição da tabela (com CONSTRAINT FOREING KEY) 
	// e provavelmente precisa ser corrigido lá...
	private RapServidores servidor;

	private Set<MbcAgendaSolicEspecial> mbcAgendaSolicEspeciales = new HashSet<MbcAgendaSolicEspecial>(0);
	private Set<MbcSolicitacaoEspExecCirg> mbcSolicitacaoEspExecCirges = new HashSet<MbcSolicitacaoEspExecCirg>(0);

	public MbcNecessidadeCirurgica() {
	}

	public MbcNecessidadeCirurgica(Short seq, String descricao, DominioSituacao situacao, Date criadoEm, Boolean indExigeDescSolic) {
		this.seq = seq;
		this.descricao = descricao;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.indExigeDescSolic = indExigeDescSolic;
	}

	public MbcNecessidadeCirurgica(Short seq, AghUnidadesFuncionais aghUnidadesFuncionais, String descricao, DominioSituacao situacao,
			Date criadoEm, Boolean indExigeDescSolic, RapServidores servidor,
			Set<MbcAgendaSolicEspecial> mbcAgendaSolicEspeciales, Set<MbcSolicitacaoEspExecCirg> mbcSolicitacaoEspExecCirges) {
		this.seq = seq;
		this.aghUnidadesFuncionais = aghUnidadesFuncionais;
		this.descricao = descricao;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.indExigeDescSolic = indExigeDescSolic;
		this.servidor = servidor;
		this.mbcAgendaSolicEspeciales = mbcAgendaSolicEspeciales;
		this.mbcSolicitacaoEspExecCirges = mbcSolicitacaoEspExecCirges;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcNciSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
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
	@JoinColumn(name = "UNF_SEQ")
	public AghUnidadesFuncionais getAghUnidadesFuncionais() {
		return this.aghUnidadesFuncionais;
	}

	public void setAghUnidadesFuncionais(AghUnidadesFuncionais aghUnidadesFuncionais) {
		this.aghUnidadesFuncionais = aghUnidadesFuncionais;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 120)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcNecessidadeCirurgica")
	public Set<MbcAgendaSolicEspecial> getMbcAgendaSolicEspeciales() {
		return this.mbcAgendaSolicEspeciales;
	}

	public void setMbcAgendaSolicEspeciales(Set<MbcAgendaSolicEspecial> mbcAgendaSolicEspeciales) {
		this.mbcAgendaSolicEspeciales = mbcAgendaSolicEspeciales;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcNecessidadeCirurgica")
	public Set<MbcSolicitacaoEspExecCirg> getMbcSolicitacaoEspExecCirges() {
		return this.mbcSolicitacaoEspExecCirges;
	}

	public void setMbcSolicitacaoEspExecCirges(Set<MbcSolicitacaoEspExecCirg> mbcSolicitacaoEspExecCirges) {
		this.mbcSolicitacaoEspExecCirges = mbcSolicitacaoEspExecCirges;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		AGH_UNIDADES_FUNCIONAIS("aghUnidadesFuncionais"),
		DESCRICAO("descricao"),
		SITUACAO("situacao"),
		CRIADO_EM("criadoEm"),
		IND_EXIGE_DESC_SOLIC("indExigeDescSolic"),
		SER_MATRICULA("servidor.id.matricula"),
		SER_VIN_CODIGO("servidor.id.vinCodigo"),
		MBC_AGENDA_SOLIC_ESPECIALES("mbcAgendaSolicEspeciales"),
		MBC_SOLICITACAO_ESP_EXEC_CIRGES("mbcSolicitacaoEspExecCirges");

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
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof MbcNecessidadeCirurgica)) {
			return false;
		}
		MbcNecessidadeCirurgica other = (MbcNecessidadeCirurgica) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "IND_EXIGE_DESC_SOLIC", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndExigeDescSolic() {
		return indExigeDescSolic;
	}

	public void setIndExigeDescSolic(Boolean indExigeDescSolic) {
		this.indExigeDescSolic = indExigeDescSolic;
	}
	
}
