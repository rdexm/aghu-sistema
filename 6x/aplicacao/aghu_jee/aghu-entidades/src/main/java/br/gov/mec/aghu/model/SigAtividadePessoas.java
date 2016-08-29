package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cascade;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;
import br.gov.mec.aghu.dominio.DominioSituacao;

@Entity
@SequenceGenerator(name = "sigAvpSq1", sequenceName = "SIG_AVP_SQ1", allocationSize = 1)
@Table(name = "SIG_ATIVIDADE_PESSOAS", schema = "AGH")
public class SigAtividadePessoas extends BaseEntitySeq<Integer> implements Serializable, Cloneable {

	private static final long serialVersionUID = -2610444451017090182L;

	private Integer seq;
	private SigAtividades sigAtividades;
	private SigGrupoOcupacoes sigGrupoOcupacoes;
	private Integer quantidade;
	private Integer tempo;
	private SigDirecionadores sigDirecionadores;
	private Date criadoEm;
	private RapServidores rapServidores;
	private DominioSituacao indSituacao;
	//transient
	private Boolean emEdicao = Boolean.FALSE;
	private Integer version;

	private List<SigAtividadePessoaRestricoes> listAtividadePessoaRestricoes = new ArrayList<SigAtividadePessoaRestricoes>(0);
	
	public SigAtividadePessoas() {
	}

	public SigAtividadePessoas(Integer seq) {
		this.seq = seq;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigAvpSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TVD_SEQ", referencedColumnName = "SEQ")
	public SigAtividades getSigAtividades() {
		return sigAtividades;
	}

	public void setSigAtividades(SigAtividades sigAtividades) {
		this.sigAtividades = sigAtividades;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GOC_SEQ", referencedColumnName = "SEQ")
	public SigGrupoOcupacoes getSigGrupoOcupacoes() {
		return sigGrupoOcupacoes;
	}

	public void setSigGrupoOcupacoes(SigGrupoOcupacoes sigGrupoOcupacoes) {
		this.sigGrupoOcupacoes = sigGrupoOcupacoes;
	}

	@Column(name = "QUANTIDADE", precision = 5, scale = 0)
	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	@Column(name = "TEMPO", precision = 5, scale = 0)
	public Integer getTempo() {
		return tempo;
	}

	public void setTempo(Integer tempo) {
		this.tempo = tempo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DIR_SEQ", referencedColumnName = "SEQ")
	public SigDirecionadores getSigDirecionadores() {
		return sigDirecionadores;
	}

	public void setSigDirecionadores(SigDirecionadores sigDirecionadores) {
		this.sigDirecionadores = sigDirecionadores;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Transient
	public String getTempoMedio() {
		if (this.getTempo() != null && this.getSigDirecionadores() != null) {
			return this.getTempo().toString() + " " + this.getSigDirecionadores().getNome();
		} else {
			return "";
		}
	}

	@Transient
	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigAtividadePessoas")
	@Cascade({ org.hibernate.annotations.CascadeType.DELETE })
	public List<SigAtividadePessoaRestricoes> getListAtividadePessoaRestricoes() {
		return listAtividadePessoaRestricoes;
	}

	public void setListAtividadePessoaRestricoes(List<SigAtividadePessoaRestricoes> listAtividadePessoaRestricoes) {
		this.listAtividadePessoaRestricoes = listAtividadePessoaRestricoes;
	}

	public enum Fields {

		SEQ("seq"),
		ATIVIDADE("sigAtividades"),
		GRUPO_OCUPACAO("sigGrupoOcupacoes"),
		QUANTIDADE("quantidade"),
		TEMPO("tempo"),
		DIRECIONADOR("sigDirecionadores"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		IND_SITUACAO("indSituacao"),
		ATIVIDADE_SEQ("sigAtividades.seq"),
		GRUPO_OCUPACAO_SEQ("sigGrupoOcupacoes.seq"),
		RESTRICOES("listAtividadePessoaRestricoes");

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
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(seq).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SigAtividadePessoas)) {
			return false;
		}
		SigAtividadePessoas other = (SigAtividadePessoas) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();

	}

}
