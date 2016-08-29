package br.gov.mec.aghu.model;

import java.io.Serializable;
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


import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the sce_hist_problema_materiais database table.
 * 
 */
@Entity
@Table(name="SCE_HIST_PROBLEMA_MATERIAIS")
@SequenceGenerator(name = "sceHpmSql1", sequenceName = "AGH.SCE_HPM_SQ1", allocationSize = 1)
public class SceHistoricoProblemaMaterial extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -7661846698318072433L;
	private Integer seq;
	private Date dtGeracao;
	private SceEntradaSaidaSemLicitacao sceEntradaSaidaSemLicitacao;
	private ScoFornecedor fornecedor;
	private Boolean indEfetivado;
	private SceMotivoProblema motivoProblema;
	private Integer nrsSeq;
	private Integer qtdeDesbloqueada;
	private Integer qtdeDf;
	private Integer qtdeProblema;
	private RapServidores servidor;
	private Integer version;
	private SceEstoqueAlmoxarifado sceEstqAlmox;

    public SceHistoricoProblemaMaterial() {
    }

	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sceHpmSql1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_GERACAO", nullable = false, length = 7)
	public Date getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESL_SEQ", referencedColumnName = "SEQ")
	public SceEntradaSaidaSemLicitacao getSceEntradaSaidaSemLicitacao() {
		return this.sceEntradaSaidaSemLicitacao;
	}

	public void setSceEntradaSaidaSemLicitacao(SceEntradaSaidaSemLicitacao sceEntradaSaidaSemLicitacao) {
		this.sceEntradaSaidaSemLicitacao = sceEntradaSaidaSemLicitacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="FRN_NUMERO")
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	@Column(name="IND_EFETIVADO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEfetivado() {
		return this.indEfetivado;
	}

	public void setIndEfetivado(Boolean indEfetivado) {
		this.indEfetivado = indEfetivado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MPR_SEQ")
	public SceMotivoProblema getMotivoProblema() {
		return motivoProblema;
	}

	public void setMotivoProblema(SceMotivoProblema motivoProblema) {
		this.motivoProblema = motivoProblema;
	}

	@Column(name="NRS_SEQ")
	public Integer getNrsSeq() {
		return this.nrsSeq;
	}

	public void setNrsSeq(Integer nrsSeq) {
		this.nrsSeq = nrsSeq;
	}

	@Column(name="QTDE_DESBLOQUEADA")
	public Integer getQtdeDesbloqueada() {
		return this.qtdeDesbloqueada;
	}

	public void setQtdeDesbloqueada(Integer qtdeDesbloqueada) {
		this.qtdeDesbloqueada = qtdeDesbloqueada;
	}


	@Column(name="QTDE_DF")
	public Integer getQtdeDf() {
		return this.qtdeDf;
	}

	public void setQtdeDf(Integer qtdeDf) {
		this.qtdeDf = qtdeDf;
	}


	@Column(name="QTDE_PROBLEMA")
	public Integer getQtdeProblema() {
		return this.qtdeProblema;
	}

	public void setQtdeProblema(Integer qtdeProblema) {
		this.qtdeProblema = qtdeProblema;
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

	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EAL_SEQ",referencedColumnName = "SEQ")
	public SceEstoqueAlmoxarifado getSceEstqAlmox() {
		return this.sceEstqAlmox;
	}

	public void setSceEstqAlmox(SceEstoqueAlmoxarifado sceEstqAlmox) {
		this.sceEstqAlmox = sceEstqAlmox;
	}
	
	public enum Fields {
		SEQ("seq"),
		QTDE_PROBLEMA("qtdeProblema"),
		QTDE_DF("qtdeDf"),
		DT_GERACAO("dtGeracao"),
		FORNECEDOR_ENTREGA("fornecedor"),
		FORNECEDOR_NUMERO("fornecedor.numero"),
		QTDE_DESBLOQUEADA("qtdeDesbloqueada"),
		ENTRADA_SAIDA_SEM_LICIACAO("sceEntradaSaidaSemLicitacao"),
		ESTOQUE_ALMOXARIFADO("sceEstqAlmox"),
		MOTIVO_PROBLEMA("motivoProblema"),		
		ESTOQUE_ALMOXARIFADO_SEQ("sceEstqAlmox.seq"),
		IND_EFETIVADO("indEfetivado"),
		SERVIDOR("servidor");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
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
		if (!(obj instanceof SceHistoricoProblemaMaterial)) {
			return false;
		}
		SceHistoricoProblemaMaterial other = (SceHistoricoProblemaMaterial) obj;
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