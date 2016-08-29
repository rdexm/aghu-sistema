package br.gov.mec.aghu.model;

import java.io.Serializable;
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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoEventoComunicacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigCevSq1", sequenceName = "SIG_CEV_SQ1", allocationSize = 1)
@Table(name = "SIG_COMUNICACAO_EVENTOS", schema = "AGH")
public class SigComunicacaoEventos extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 8973458903490343405L;

	private Integer seq;
	private DominioTipoEventoComunicacao tipoEvento;
	private Date criadoEm;
	private RapServidores servidor;
	private FccCentroCustos fccCentroCustos;
	private RapServidores servidorCadastro;
	private DominioSituacao situacao;
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCevSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "CCT_CODIGO", referencedColumnName = "CODIGO") })
	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	@Column(name = "IND_TIPO_EVENTO", nullable = false, length = 2)
	@Enumerated(EnumType.STRING)
	public DominioTipoEventoComunicacao getTipoEvento() {
		return tipoEvento;
	}

	public void setTipoEvento(DominioTipoEventoComunicacao tipoEvento) {
		this.tipoEvento = tipoEvento;
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
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_CADASTRO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_CADASTRO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorCadastro() {
		return servidorCadastro;
	}

	public void setServidorCadastro(RapServidores servidorCadastro) {
		this.servidorCadastro = servidorCadastro;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {
		SEQ("seq"),
		TIPO_EVENTO("tipoEvento"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		CENTRO_CUSTO("fccCentroCustos"),
		SERVIDOR_CADASTRO("servidorCadastro"),
		SITUACAO("situacao");

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
		return new HashCodeBuilder().append(seq).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SigComunicacaoEventos)) {
			return false;
		}
		SigComunicacaoEventos other = (SigComunicacaoEventos) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();
	}
}
