package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioAfpPublicado;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sco_af_pedidos database table.
 * 
 */
@Entity
@Table(name="SCO_AF_PEDIDOS")
public class ScoAutorizacaoFornecedorPedido extends BaseEntityId<ScoAutorizacaoFornecedorPedidoId> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = 745160760050788090L;
	private ScoAutorizacaoFornecedorPedidoId id;
	private Date dtEntrada;
	private Date dtEnvioFornecedor;
	private Date dtGeracao;
	private Date dtPublicacao;
	private Date dtVisualizacao;
	private Integer eslSeqFatura;
	private String indEfetivado;
	private String indTipoAcessoForn;
	private DominioAfpPublicado indPublicado;
	private Integer serMatriculaEnvioForn;
	private Integer serVinCodigoEnvioForn;
	private Integer version;
	private RapServidores rapServidor;
	private ScoAutorizacaoForn scoAutorizacaoForn;
	private List<ScoProgEntregaItemAutorizacaoFornecimento> scoProgrEntregaItensAfs;
	private List<SceNotaRecebProvisorio> notaRecebProvisorios;

    public ScoAutorizacaoFornecedorPedido() {
    }


    @EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "numero", column = @Column(name = "NUMERO", nullable = false)),
			@AttributeOverride(name = "afnNumero", column = @Column(name = "AFN_NUMERO", nullable = false, length = 1)) })
	public ScoAutorizacaoFornecedorPedidoId getId() {
		return this.id;
	}

	public void setId(ScoAutorizacaoFornecedorPedidoId id) {
		this.id = id;
	}
	

	@Column(name="DT_ENTRADA")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtEntrada() {
		return this.dtEntrada;
	}

	public void setDtEntrada(Date dtEntrada) {
		this.dtEntrada = dtEntrada;
	}


	@Column(name="DT_ENVIO_FORNECEDOR")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtEnvioFornecedor() {
		return this.dtEnvioFornecedor;
	}

	public void setDtEnvioFornecedor(Date dtEnvioFornecedor) {
		this.dtEnvioFornecedor = dtEnvioFornecedor;
	}


	@Column(name="DT_GERACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}


	@Column(name="DT_PUBLICACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtPublicacao() {
		return this.dtPublicacao;
	}

	public void setDtPublicacao(Date dtPublicacao) {
		this.dtPublicacao = dtPublicacao;
	}


	@Column(name="DT_VISUALIZACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtVisualizacao() {
		return this.dtVisualizacao;
	}

	public void setDtVisualizacao(Date dtVisualizacao) {
		this.dtVisualizacao = dtVisualizacao;
	}


	@Column(name="ESL_SEQ_FATURA")
	public Integer getEslSeqFatura() {
		return this.eslSeqFatura;
	}

	public void setEslSeqFatura(Integer eslSeqFatura) {
		this.eslSeqFatura = eslSeqFatura;
	}


	@Column(name="IND_EFETIVADO")
	public String getIndEfetivado() {
		return this.indEfetivado;
	}

	public void setIndEfetivado(String indEfetivado) {
		this.indEfetivado = indEfetivado;
	}


	@Column(name="IND_PUBLICADO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioAfpPublicado getIndPublicado() {
		return this.indPublicado;
	}

	public void setIndPublicado(DominioAfpPublicado indPublicado) {
		this.indPublicado = indPublicado;
	}
	
	@Column(name="IND_TIPO_ACESSO_FORN")
	public String getIndTipoAcessoForn() {
		return this.indTipoAcessoForn;
	}

	public void setIndTipoAcessoForn(String indTipoAcessoForn) {
		this.indTipoAcessoForn = indTipoAcessoForn;
	}


	@Column(name="SER_MATRICULA_ENVIO_FORN")
	public Integer getSerMatriculaEnvioForn() {
		return this.serMatriculaEnvioForn;
	}

	public void setSerMatriculaEnvioForn(Integer serMatriculaEnvioForn) {
		this.serMatriculaEnvioForn = serMatriculaEnvioForn;
	}


	@Column(name="SER_VIN_CODIGO_ENVIO_FORN")
	public Integer getSerVinCodigoEnvioForn() {
		return this.serVinCodigoEnvioForn;
	}

	public void setSerVinCodigoEnvioForn(Integer serVinCodigoEnvioForn) {
		this.serVinCodigoEnvioForn = serVinCodigoEnvioForn;
	}


	@Version
	@Column(name = "version", nullable = false)	
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	//bi-directional many-to-one association to RapServidore
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getRapServidor() {
		return this.rapServidor;
	}

	public void setRapServidor(RapServidores rapServidore) {
		this.rapServidor = rapServidore;
	}
	

	//bi-directional many-to-one association to ScoAutorizacoesForn
    @ManyToOne
	@JoinColumn(name="AFN_NUMERO", insertable=false, updatable=false)
	public ScoAutorizacaoForn getScoAutorizacaoForn() {
		return this.scoAutorizacaoForn;
	}

	public void setScoAutorizacaoForn(ScoAutorizacaoForn scoAutorizacoesForn) {
		this.scoAutorizacaoForn = scoAutorizacoesForn;
	}
	

	//bi-directional many-to-one association to ScoProgEntregaItemAutorizacaoFornecimento
	@OneToMany(mappedBy="scoAfPedido")
	@OrderBy("indEntregaImediata DESC, dtPrevEntrega ASC")
	public List<ScoProgEntregaItemAutorizacaoFornecimento> getScoProgrEntregaItensAfs() {
		return this.scoProgrEntregaItensAfs;
	}

	public void setScoProgrEntregaItensAfs(List<ScoProgEntregaItemAutorizacaoFornecimento> scoProgrEntregaItensAfs) {
		this.scoProgrEntregaItensAfs = scoProgrEntregaItensAfs;
	}
	
	@OneToMany(mappedBy = "scoAfPedido")
	public List<SceNotaRecebProvisorio> getNotaRecebProvisorios() {
		return this.notaRecebProvisorios;
	}

	public void setNotaRecebProvisorios(List<SceNotaRecebProvisorio> notaRecebProvisorios) {
		this.notaRecebProvisorios = notaRecebProvisorios;
	}

	public enum Fields {

		ID("id"),
		ID_NUM("id.numero"),
		ID_AFN_NUMERO("id.afnNumero"),
		DT_ENTRADA("dtEntrada"),
		DT_ENVIO_FORNECEDOR("dtEnvioFornecedor"),
		DT_GERACAO("dtGeracao"),
		DT_PUBLICACAO("dtPublicacao"),
		DT_VISUALIZACAO("dtVisualizacao"),
		ESL_SEQ_FATURA("eslSeqFatura"),
		IND_EFETIVADO("indEfetivado"),
		IND_PUBLICADO("indPublicado"),
		IND_TIPO_ACESSO_FORN("indTipoAcessoForn"),
		SER_MATRICULA_ENVIO_FORN("serMatriculaEnvioForn"),
		SER_VIN_CODIGO_ENVIO_FORN("serVinCodigoEnvioForn"),
		RAP_SERVIDOR("rapServidor"),
		SCO_AUTORIZACAO_FORN("scoAutorizacaoForn"),
		SCO_AUTORIZACAO_FORN_NUM("scoAutorizacaoForn.numero"),
		SCO_PROGR_ENTREGA_ITENS_AFS("scoProgrEntregaItensAfs"); 
		

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
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof ScoAutorizacaoFornecedorPedido)) {
			return false;
		}
		ScoAutorizacaoFornecedorPedido other = (ScoAutorizacaoFornecedorPedido) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}