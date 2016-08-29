package br.gov.mec.aghu.model;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "AEL_MARCADORES", schema = "AGH")
@SequenceGenerator(name = "AelMarcadorSequence", sequenceName = "AGH.AEL_MRC_SQ1")
public class AelMarcador extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -989687249187242721L;

	private Integer seq;
	private String marcadorPedido;
	private String marcadorLaudo;
	private ScoMarcaComercial fabricante;
	private String cloneMarcador;
	private Date criadoEm;
	private Date alteradoEm;
	private RapServidores servidorInclusao;
	private RapServidores servidorAlteracao;
	private Integer version;
	private DominioSituacao indSituacao;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AelMarcadorSequence")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "MARCADOR_PEDIDO", nullable = false, length = 60)
	@NotNull
	public String getMarcadorPedido() {
		return marcadorPedido;
	}
	
	public void setMarcadorPedido(String marcadorPedido) {
		this.marcadorPedido = marcadorPedido;
	}
	
	@Column(name = "MARCADOR_LAUDO", nullable = false, length = 60)
	@NotNull
	public String getMarcadorLaudo() {
		return this.marcadorLaudo;
	}
	
	public void setMarcadorLaudo(String marcadorLaudo) {
		this.marcadorLaudo = marcadorLaudo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn (name = "MCM_MRC_SEQ", referencedColumnName = "CODIGO", nullable = false)
	public ScoMarcaComercial getFabricante() {
		return fabricante;
	}

	public void setFabricante(ScoMarcaComercial fabricante) {
		this.fabricante = fabricante;
	}

	@Column(name = "CLONE_MARCADOR", nullable = false)
	@NotNull
	public String getCloneMarcador() {
		return cloneMarcador;
	}

	public void setCloneMarcador(String cloneMarcador) {
		this.cloneMarcador = cloneMarcador;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	@NotNull
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", nullable = true)
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_INCLUSAO", referencedColumnName = "MATRICULA", nullable=false),
			@JoinColumn(name = "SER_VIN_CODIGO_INCLUSAO", referencedColumnName = "VIN_CODIGO", nullable=false) })
	@NotNull
	public RapServidores getServidorInclusao() {
		return servidorInclusao;
	}

	public void setServidorInclusao(RapServidores servidorInclusao) {
		this.servidorInclusao = servidorInclusao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_ALTERACAO", referencedColumnName = "MATRICULA", nullable=false),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERACAO", referencedColumnName = "VIN_CODIGO", nullable=false) })
	@NotNull
	public RapServidores getServidorAlteracao() {
		return servidorAlteracao;
	}

	public void setServidorAlteracao(RapServidores servidorAlteracao) {
		this.servidorAlteracao = servidorAlteracao;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	@NotNull
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	@NotNull
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@Transient
	public String getDescricao() {
		return marcadorLaudo + " - " + fabricante.getDescricao() + " - " + cloneMarcador;
	}

	public enum Fields {
		SEQ("seq"),
		MARCADOR_PEDIDO("marcadorPedido"),
		MARCADOR_LAUDO("marcadorLaudo"),
		FABRICANTE("fabricante"),
		CLONE_MARCADOR("cloneMarcador"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		SERVIDOR_INCLUSAO("servidorInclusao"),
		SERVIDOR_ALTERACAO("servidorAlteracao"),
		VERSION("version"),
		IND_SITUACAO("indSituacao")
		;

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
		if (!(obj instanceof AelMarcador)) {
			return false;
		}
		AelMarcador other = (AelMarcador) obj;
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

}