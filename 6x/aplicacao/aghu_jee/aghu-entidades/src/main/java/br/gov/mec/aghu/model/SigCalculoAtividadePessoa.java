package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigCapSq1", sequenceName = "SIG_CAP_SQ1", allocationSize = 1)
@Table(name = "sig_calculo_atividade_pessoas", schema = "agh")
public class SigCalculoAtividadePessoa extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -4423351486062929177L;

	private Integer seq;
	private Date criadoEm;
	private RapServidores rapServidores;
	private SigCalculoComponente sigCalculoComponentes;
	private SigGrupoOcupacoes sigGrupoOcupacoes;
	private Double valorGrupoOcupacao;
	private Double qtdePrevista;
	private Double qtdeRealizada;
	private Integer version;
	private SigAtividadePessoas sigAtividadePessoas;
	private SigAtividades sigAtividades;
	private SigDirecionadores sigDirecionadores;
	private BigDecimal peso;

	public SigCalculoAtividadePessoa() {
	}
	
	public SigCalculoAtividadePessoa(Integer seq) {
		this.seq = seq;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCapSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cmt_seq", nullable = false, referencedColumnName = "seq")
	public SigCalculoComponente getSigCalculoComponentes() {
		return this.sigCalculoComponentes;
	}

	public void setSigCalculoComponentes(SigCalculoComponente sigCalculoComponentes) {
		this.sigCalculoComponentes = sigCalculoComponentes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "criado_em", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
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

	@Column(name = "qtde_prevista", precision = 12, scale = 4)
	public Double getQtdePrevista() {
		return this.qtdePrevista;
	}

	public void setQtdePrevista(Double qtdePrevista) {
		this.qtdePrevista = qtdePrevista;
	}

	@Column(name = "qtde_realizada", precision = 12, scale = 4)
	public Double getQtdeRealizada() {
		return this.qtdeRealizada;
	}

	public void setQtdeRealizada(Double qtdeRealizada) {
		this.qtdeRealizada = qtdeRealizada;
	}

	@Column(name = "vlr_grp_ocupacao", precision = 18, scale = 4)
	public Double getValorGrupoOcupacao() {
		return valorGrupoOcupacao;
	}

	public void setValorGrupoOcupacao(Double valorGrupoOcupacao) {
		this.valorGrupoOcupacao = valorGrupoOcupacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GOC_SEQ", referencedColumnName = "SEQ")
	public SigGrupoOcupacoes getSigGrupoOcupacoes() {
		return sigGrupoOcupacoes;
	}

	public void setSigGrupoOcupacoes(SigGrupoOcupacoes sigGrupoOcupacoes) {
		this.sigGrupoOcupacoes = sigGrupoOcupacoes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tvd_seq", referencedColumnName = "seq")
	public SigAtividades getSigAtividades() {
		return this.sigAtividades;
	}

	public void setSigAtividades(SigAtividades sigAtividades) {
		this.sigAtividades = sigAtividades;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "avp_seq", referencedColumnName = "seq")
	public SigAtividadePessoas getSigAtividadePessoas() {
		return this.sigAtividadePessoas;
	}

	public void setSigAtividadePessoas(SigAtividadePessoas sigAtividadePessoas) {
		this.sigAtividadePessoas = sigAtividadePessoas;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DIR_SEQ", referencedColumnName = "SEQ")
	public SigDirecionadores getSigDirecionadores() {
		return sigDirecionadores;
	}

	public void setSigDirecionadores(SigDirecionadores sigDirecionadores) {
		this.sigDirecionadores = sigDirecionadores;
	}

	@Column(name = "peso", nullable = true, precision = 14, scale = 5)
	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	public enum Fields {

		SEQ("seq"),
		CALCULO_COMPONENTE("sigCalculoComponentes"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		VALOR_GRUPO_OCUPACAO("valorGrupoOcupacao"),
		QUANTIDADE_PREVISTA("qtdePrevista"),
		QUANTIDADE_REALIZADA("qtdeRealizada"),
		GRUPO_OCUPACOES("sigGrupoOcupacoes"),
		ATIVIDADE_PESSOA("sigAtividadePessoas"),
		ATIVIDADES("sigAtividades"),
		DIRECIONADOR("sigDirecionadores"),
		PESO("peso");

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
		return new HashCodeBuilder().append(seq).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SigCalculoAtividadePessoa)) {
			return false;
		}
		SigCalculoAtividadePessoa other = (SigCalculoAtividadePessoa) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();

	}
}
