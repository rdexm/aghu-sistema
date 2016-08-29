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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigCtcSq1", sequenceName = "SIG_CTC_SQ1", allocationSize = 1)
@Table(name = "SIG_CATEGORIA_CONSUMOS", schema = "AGH")
public class SigCategoriaConsumos extends BaseEntitySeq<Integer> implements Serializable {
	
	private static final long serialVersionUID = 3022914659841204L;
	
	private Integer seq;
	private String descricao;
	private DominioIndContagem indContagem;
	private Short ordemVisualizacao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;
	private String agrupador;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCtcSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}
	
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Column(name = "DESCRICAO", nullable = false, length = 60)
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name = "IND_CONTAGEM", nullable = false, length = 2)
	@Enumerated(EnumType.STRING)
	public DominioIndContagem getIndContagem() {
		return indContagem;
	}
	
	public void setIndContagem(DominioIndContagem indContagem) {
		this.indContagem = indContagem;
	}
	
	@Column(name = "ORDEM_VISUALIZACAO", nullable = false, length = 60)
	public Short getOrdemVisualizacao() {
		return ordemVisualizacao;
	}
	
	public void setOrdemVisualizacao(Short ordemVisualizacao) {
		this.ordemVisualizacao = ordemVisualizacao;
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
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}
	
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Column(name = "VERSION")
	@Version
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name = "AGRUPADOR", nullable = true, length = 60)
	public String getAgrupador() {
		return agrupador;
	}

	public void setAgrupador(String agrupador) {
		this.agrupador = agrupador;
	}

	public enum Fields {
		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_CONTAGEM("indContagem"),
		ORDEM_VISUALIZACAO("ordemVisualizacao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		VERSION("version"),
		AGRUPADOR("agrupador");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

}
