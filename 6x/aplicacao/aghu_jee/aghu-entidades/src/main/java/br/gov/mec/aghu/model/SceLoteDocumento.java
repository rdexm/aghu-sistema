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
 * The persistent class for the sce_lote_x_documentos database table.
 * 
 */
@Entity
@Table(name="SCE_LOTE_X_DOCUMENTOS")
@SequenceGenerator(name = "sceLdcSql1", sequenceName = "AGH.SCE_LDC_SQ1", allocationSize = 1)
public class SceLoteDocumento extends BaseEntitySeq<Integer> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = 7866229246579168325L;
	private Integer seq;
	private Date dtValidade;
	private SceEntradaSaidaSemLicitacao entradaSaidaSemLicitacao;
	
	private Integer idaDalSeq;
	private Integer idaEalSeq;
	private Integer inrEalSeq;
	
	private Integer inrIafAfnNumero;
	private Integer inrIafNumero;
	private Integer inrNrsSeq;

	private SceItemNotaRecebimento itemNotaRecebimento;
	private Integer irmEalSeq;
	private Integer irmRmsSeq;
	private Integer itrEalSeq;
	private Integer itrTrfSeq;
	private String lotCodigo;
	private Integer lotMatCodigo;
	private Integer lotMcmCodigo;
	private ScoFornecedor fornecedor;
	
	private Integer quantidade;
	private String serie;
	private String tamanho;
	
	private SceTipoMovimento tipoMovimento;
	private Integer version;

    public SceLoteDocumento() {
    }
    
    public enum Fields {
		SEQUENCIAL("seq"),

		LOT_CODIGO("lotCodigo"),
		LOT_MAT_CODIGO("lotMatCodigo"),
		LOT_MCM_CODIGO("lotMcmCodigo"),
		
		INR_NRS_SEQ("inrNrsSeq"),
		INR_IAF_AFN_NUMERO("inrIafAfnNumero"),
		INR_IAF_NUMERO("inrIafNumero"),
		IRM_RMS_SEQ("irmRmsSeq"),
		IRM_EAL_SEQ("irmEalSeq"),
		
		ITEM_NOTA_RECEBIMENTO("itemNotaRecebimento"),
		DATA_VALIDADE("dtValidade"),
		FORNECEDOR("fornecedor"),
		IDA_DAL_SEQ("idaDalSeq"),
		IDA_EAL_SEQ("idaEalSeq"),
		INR_EAL_SEQ("inrEalSeq"),
		ENTRADA_SAIDA_SEM_LICITACAO("entradaSaidaSemLicitacao");
		
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sceLdcSql1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	@Column(name="DT_VALIDADE")
	@Temporal(TemporalType.DATE)
	public Date getDtValidade() {
		return this.dtValidade;
	}

	public void setDtValidade(Date dtValidade) {
		this.dtValidade = dtValidade;
	}
	
	/*
	@Column(name="ESL_SEQ")
	public Integer getEslSeq() {
		return this.eslSeq;
	}

	public void setEslSeq(Integer eslSeq) {
		this.eslSeq = eslSeq;
	}
	*/

	@Column(name="IDA_DAL_SEQ")
	public Integer getIdaDalSeq() {
		return this.idaDalSeq;
	}

	public void setIdaDalSeq(Integer idaDalSeq) {
		this.idaDalSeq = idaDalSeq;
	}

	@Column(name="IDA_EAL_SEQ")
	public Integer getIdaEalSeq() {
		return this.idaEalSeq;
	}

	public void setIdaEalSeq(Integer idaEalSeq) {
		this.idaEalSeq = idaEalSeq;
	}

	@Column(name="INR_EAL_SEQ")
	public Integer getInrEalSeq() {
		return this.inrEalSeq;
	}

	public void setInrEalSeq(Integer inrEalSeq) {
		this.inrEalSeq = inrEalSeq;
	}

	@Column(name="INR_IAF_AFN_NUMERO", insertable=false, updatable=false)
	public Integer getInrIafAfnNumero() {
		return this.inrIafAfnNumero;
	}

	public void setInrIafAfnNumero(Integer inrIafAfnNumero) {
		this.inrIafAfnNumero = inrIafAfnNumero;
	}
	
	@Column(name="INR_IAF_NUMERO", insertable=false, updatable=false)
	public Integer getInrIafNumero() {
		return this.inrIafNumero;
	}

	public void setInrIafNumero(Integer inrIafNumero) {
		this.inrIafNumero = inrIafNumero;
	}

	@Column(name="INR_NRS_SEQ", insertable=false, updatable=false)
	public Integer getInrNrsSeq() {
		return this.inrNrsSeq;
	}

	public void setInrNrsSeq(Integer inrNrsSeq) {
		this.inrNrsSeq = inrNrsSeq;
	}
	


	@Column(name="IRM_EAL_SEQ")
	public Integer getIrmEalSeq() {
		return this.irmEalSeq;
	}

	public void setIrmEalSeq(Integer irmEalSeq) {
		this.irmEalSeq = irmEalSeq;
	}


	@Column(name="IRM_RMS_SEQ")
	public Integer getIrmRmsSeq() {
		return this.irmRmsSeq;
	}

	public void setIrmRmsSeq(Integer irmRmsSeq) {
		this.irmRmsSeq = irmRmsSeq;
	}


	@Column(name="ITR_EAL_SEQ")
	public Integer getItrEalSeq() {
		return this.itrEalSeq;
	}

	public void setItrEalSeq(Integer itrEalSeq) {
		this.itrEalSeq = itrEalSeq;
	}


	@Column(name="ITR_TRF_SEQ")
	public Integer getItrTrfSeq() {
		return this.itrTrfSeq;
	}

	public void setItrTrfSeq(Integer itrTrfSeq) {
		this.itrTrfSeq = itrTrfSeq;
	}

	@Column(name="QUANTIDADE")
	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}


	public String getSerie() {
		return this.serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}


	public String getTamanho() {
		return this.tamanho;
	}

	public void setTamanho(String tamanho) {
		this.tamanho = tamanho;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "TMV_SEQ", referencedColumnName = "SEQ"),
		@JoinColumn(name = "TMV_COMPLEMENTO", referencedColumnName = "COMPLEMENTO") })
	public SceTipoMovimento getTipoMovimento() {
		return this.tipoMovimento;
	}

	public void setTipoMovimento(SceTipoMovimento tipoMovimento) {
		this.tipoMovimento = tipoMovimento;
	}

	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name="LOT_CODIGO")
	public String getLotCodigo() {
		return this.lotCodigo;
	}

	public void setLotCodigo(String lotCodigo) {
		this.lotCodigo = lotCodigo;
	}


	@Column(name="LOT_MAT_CODIGO")
	public Integer getLotMatCodigo() {
		return this.lotMatCodigo;
	}

	public void setLotMatCodigo(Integer lotMatCodigo) {
		this.lotMatCodigo = lotMatCodigo;
	}


	@Column(name="LOT_MCM_CODIGO")
	public Integer getLotMcmCodigo() {
		return this.lotMcmCodigo;
	}

	public void setLotMcmCodigo(Integer lotMcmCodigo) {
		this.lotMcmCodigo = lotMcmCodigo;
	}
	
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="INR_IAF_AFN_NUMERO", referencedColumnName="IAF_AFN_NUMERO", nullable=true),
		@JoinColumn(name="INR_IAF_NUMERO", referencedColumnName="IAF_NUMERO", nullable=true),
		@JoinColumn(name="INR_NRS_SEQ", referencedColumnName="NRS_SEQ", nullable=true)
		})
	public SceItemNotaRecebimento getItemNotaRecebimento() {
		return this.itemNotaRecebimento;
	}

	public void setItemNotaRecebimento(SceItemNotaRecebimento itemNotaRecebimento) {
		this.itemNotaRecebimento = itemNotaRecebimento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESL_SEQ", referencedColumnName = "SEQ", nullable = true)
	public SceEntradaSaidaSemLicitacao getEntradaSaidaSemLicitacao() {
		return this.entradaSaidaSemLicitacao;
	}

	public void setEntradaSaidaSemLicitacao(SceEntradaSaidaSemLicitacao entradaSaidaSemLicitacao) {
		this.entradaSaidaSemLicitacao = entradaSaidaSemLicitacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FRN_NUMERO", referencedColumnName = "NUMERO", nullable = true)
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
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
		if (!(obj instanceof SceLoteDocumento)) {
			return false;
		}
		SceLoteDocumento other = (SceLoteDocumento) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return super.equals(obj);
	}
	// ##### GeradorEqualsHashCodeMain #####

}