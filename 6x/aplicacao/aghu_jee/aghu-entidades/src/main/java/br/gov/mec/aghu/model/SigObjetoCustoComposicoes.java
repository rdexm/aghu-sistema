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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigCbtSq1", sequenceName = "SIG_CBT_SQ1", allocationSize = 1)
@Table(name = "SIG_OBJETO_CUSTO_COMPOSICOES", schema = "AGH")
public class SigObjetoCustoComposicoes extends BaseEntitySeq<Integer> implements Serializable, Cloneable {

	private static final long serialVersionUID = 6559569270256864944L;
	
	private Integer seq;
	private SigObjetoCustoVersoes sigObjetoCustoVersoes;
	private SigAtividades sigAtividades;
	private SigObjetoCustoVersoes sigObjetoCustoVersoesCompoe;
	private SigDirecionadores sigDirecionadores;
	private String identificadorPop;
	private Date criadoEm;
	private RapServidores rapServidores;
	private DominioSituacao indSituacao;
	private Integer nroExecucoes;
	//transient
	private Boolean emEdicao = Boolean.FALSE;
	private Integer version;

	private Set<SigCalculoComponente> listSigCalculoComponente = new HashSet<SigCalculoComponente>(0);

	public SigObjetoCustoComposicoes(Integer seq) {
		this.seq = seq;
	}

	public SigObjetoCustoComposicoes(){}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCbtSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OCV_SEQ", referencedColumnName = "SEQ")
	public SigObjetoCustoVersoes getSigObjetoCustoVersoes() {
		return sigObjetoCustoVersoes;
	}

	public void setSigObjetoCustoVersoes(SigObjetoCustoVersoes sigObjetoCustoVersoes) {
		this.sigObjetoCustoVersoes = sigObjetoCustoVersoes;
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
	@JoinColumn(name = "OCV_SEQ_COMPOE", referencedColumnName = "SEQ")
	public SigObjetoCustoVersoes getSigObjetoCustoVersoesCompoe() {
		return sigObjetoCustoVersoesCompoe;
	}

	public void setSigObjetoCustoVersoesCompoe(SigObjetoCustoVersoes sigObjetoCustoVersoesCompoe) {
		this.sigObjetoCustoVersoesCompoe = sigObjetoCustoVersoesCompoe;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DIR_SEQ", referencedColumnName = "SEQ")
	public SigDirecionadores getSigDirecionadores() {
		return sigDirecionadores;
	}

	public void setSigDirecionadores(SigDirecionadores sigDirecionadores) {
		this.sigDirecionadores = sigDirecionadores;
	}

	@Column(name = "IDENTIFICADOR_POP", nullable = true, length = 60)
	public String getIdentificadorPop() {
		return identificadorPop;
	}

	public void setIdentificadorPop(String identificadorPop) {
		this.identificadorPop = identificadorPop;
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

	@Column(name = "NRO_EXECUCOES")
	public Integer getNroExecucoes() {
		return nroExecucoes;
	}

	public void setNroExecucoes(Integer nroExecucoes) {
		this.nroExecucoes = nroExecucoes;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigObjetoCustoComposicoes")
	public Set<SigCalculoComponente> getListSigCalculoComponente() {
		return listSigCalculoComponente;
	}

	public void setListSigCalculoComponente(Set<SigCalculoComponente> listSigCalculoComponente) {
		this.listSigCalculoComponente = listSigCalculoComponente;
	}

	public enum Fields {

		SEQ("seq"),
		OBJETO_CUSTO_VERSOES_OBJ("sigObjetoCustoVersoes"),
		OBJETO_CUSTO_VERSOES("sigObjetoCustoVersoes.seq"),
		ATIVIDADE("sigAtividades"),
		ATIVIDADE_SEQ("sigAtividades.seq"),
		OBJETO_CUSTO_VERSOES_COMPOE("sigObjetoCustoVersoesCompoe"),
		OBJETO_CUSTO_VERSOES_COMPOE_SEQ("sigObjetoCustoVersoesCompoe.seq"),
		DIRECIONADORES("sigDirecionadores"),
		IDENTIFICADOR_POP("identificadorPop"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		IND_SITUACAO("indSituacao"),
		NRO_EXECUCAO("nroExecucoes");
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
	public boolean equals(Object other) {
		if (!(other instanceof SigObjetoCustoComposicoes)) {
			return false;
		}
		SigObjetoCustoComposicoes castOther = (SigObjetoCustoComposicoes) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
