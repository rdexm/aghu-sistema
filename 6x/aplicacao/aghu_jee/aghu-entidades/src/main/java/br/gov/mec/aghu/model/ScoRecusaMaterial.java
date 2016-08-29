package br.gov.mec.aghu.model;

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
@SequenceGenerator(name="scoRmaSq1", sequenceName="SCO_RMA_SQ1", allocationSize = 1)
@Table(name="SCO_RECUSA_MATERIAL" , schema = "AGH")
public class ScoRecusaMaterial extends BaseEntitySeq<Long> {

	private static final long serialVersionUID = -4597201761706689755L;

	private Long seq;
	
	private Date dtInclusao;
	
	private String indEnviaEmail;
	
	private Short jusCodigo;
	
	private String descricao;
	
	private RapServidores servidor;
	
	private Long dfeNumero;
	
	private String dfeSerie;
	
	private Integer frnNumero;
	
	private String frnNome;
	
	private Long ctNumero;
	
	private String ctSerie;
	
	private Integer traNumero;
	
	private String traNome;
	
	private ScoAutorizacaoForn autorizacaoFornecedor;
	
	private ScoItemAutorizacaoForn itemAutorizacaoFornecedor;
	
	private Integer afnNumero;
	
	private Integer iafNumero;
	
	private Short parcela;
	
	private SceEntrSaidSemLicitacao sceEsl;
	
	private Integer eslSeq;
	
	private Integer version;
	
	private Integer serMatricula;
	
	private Short serVinCodigo;
	
	private ScoFornecedor fornecedor;
	
	private ScoFornecedor transportadora;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoRmaSq1")
	@Column(name = "SEQ", unique = true, nullable = false, scale = 0)
	public Long getSeq() {
		return seq;
	}

	@Override
	public void setSeq(Long seq) {
		this.seq = seq;
		
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_INCLUSAO", nullable = false)
	public Date getDtInclusao() {
		return dtInclusao;
	}

	public void setDtInclusao(Date dtInclusao) {
		this.dtInclusao = dtInclusao;
	}

	@Column(name = "IND_ENVIA_EMAIL")
	public String getIndEnviaEmail() {
		return indEnviaEmail;
	}

	public void setIndEnviaEmail(String indEnviaEmail) {
		this.indEnviaEmail = indEnviaEmail;
	}

	@Column(name = "JUS_CODIGO")
	public Short getJusCodigo() {
		return jusCodigo;
	}

	public void setJusCodigo(Short jusCodigo) {
		this.jusCodigo = jusCodigo;
	}

	@Column(name = "DESCRICAO")
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "DFE_NUMERO", nullable = false)
	public Long getDfeNumero() {
		return dfeNumero;
	}

	public void setDfeNumero(Long dfeNumero) {
		this.dfeNumero = dfeNumero;
	}

	@Column(name = "DFE_SERIE")
	public String getDfeSerie() {
		return dfeSerie;
	}

	public void setDfeSerie(String dfeSerie) {
		this.dfeSerie = dfeSerie;
	}

	@Column(name = "FRN_NUMERO", nullable = false)
	public Integer getFrnNumero() {
		return frnNumero;
	}

	public void setFrnNumero(Integer frnNumero) {
		this.frnNumero = frnNumero;
	}

	@Column(name = "FRN_NOME")
	public String getFrnNome() {
		return frnNome;
	}

	public void setFrnNome(String frnNome) {
		this.frnNome = frnNome;
	}

	@Column(name = "CT_NUMERO")
	public Long getCtNumero() {
		return ctNumero;
	}

	public void setCtNumero(Long ctNumero) {
		this.ctNumero = ctNumero;
	}

	@Column(name = "CT_SERIE")
	public String getCtSerie() {
		return ctSerie;
	}

	public void setCtSerie(String ctSerie) {
		this.ctSerie = ctSerie;
	}

	@Column(name = "TRA_NUMERO")
	public Integer getTraNumero() {
		return traNumero;
	}

	public void setTraNumero(Integer traNumero) {
		this.traNumero = traNumero;
	}

	@Column(name = "TRA_NOME")
	public String getTraNome() {
		return traNome;
	}

	public void setTraNome(String traNome) {
		this.traNome = traNome;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AFN_NUMERO", referencedColumnName = "NUMERO", insertable = false, updatable = false)
	public ScoAutorizacaoForn getAutorizacaoFornecedor() {
		return autorizacaoFornecedor;
	}

	public void setAutorizacaoFornecedor(ScoAutorizacaoForn autorizacaoFornecedor) {
		this.autorizacaoFornecedor = autorizacaoFornecedor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "AFN_NUMERO", referencedColumnName = "AFN_NUMERO", insertable = false, updatable = false),
		@JoinColumn(name = "IAF_NUMERO", referencedColumnName = "NUMERO", insertable = false, updatable = false) })
	public ScoItemAutorizacaoForn getItemAutorizacaoFornecedor() {
		return itemAutorizacaoFornecedor;
	}

	public void setItemAutorizacaoFornecedor(
			ScoItemAutorizacaoForn itemAutorizacaoFornecedor) {
		this.itemAutorizacaoFornecedor = itemAutorizacaoFornecedor;
	}
		
	@Column(name = "AFN_NUMERO")
	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	@Column(name = "IAF_NUMERO")
	public Integer getIafNumero() {
		return iafNumero;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	@Column(name = "PARCELA")
	public Short getParcela() {
		return parcela;
	}

	public void setParcela(Short parcela) {
		this.parcela = parcela;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESL_SEQ", referencedColumnName = "SEQ")
	public SceEntrSaidSemLicitacao getSceEsl() {
		return sceEsl;
	}

	public void setSceEsl(SceEntrSaidSemLicitacao sceEsl) {
		this.sceEsl = sceEsl;
	}

	@Column(name = "ESL_SEQ", insertable = false, updatable = false)
	public Integer getEslSeq() {
		return eslSeq;
	}

	public void setEslSeq(Integer eslSeq) {
		this.eslSeq = eslSeq;
	}

	@Version
	@Column(name = "VERSION")
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name = "SER_MATRICULA", insertable = false, updatable = false)
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", insertable = false, updatable = false)
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public enum Fields {

		SEQ("seq"),
		DFE_NUMERO("dfeNumero"),
		DFE_SERIE("dfeSerie"),
		FRN_NUMERO("frnNumero"),
		CT_NUMERO("ctNumero"),
		CT_SERIE("ctSerie"),
		TRA_NUMERO("traNumero"),
		ESL_SEQ("eslSeq"),
		AFN_NUMERO("afnNumero"),
		IAF_NUMERO("iafNumero"),
		PARCELA("parcela"),
		DT_INCLUSAO("dtInclusao"),
		FRN_NOME("frnNome"),
		TRA_NOME("traNome"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		DESCRICAO("descricao"),
		JUS_CODIGO("jusCodigo"),
		IND_ENVIA_EMAIL("indEnviaEmail"),
		FORNECEDOR("fornecedor"),
		TRANSPORTADORA("transportadora"),
		AUTORIZACAO_FORNECEDOR("autorizacaoFornecedor");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		hashCodeBuilder.append(this.getSeq());
		return hashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ScoRecusaMaterial)) {
			return false;
		}
		ScoRecusaMaterial other = (ScoRecusaMaterial) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(this.getSeq(), other.getSeq());	
		return equalsBuilder.isEquals();
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FRN_NUMERO", referencedColumnName = "NUMERO", insertable = false, updatable = false)
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRA_NUMERO", referencedColumnName = "NUMERO", insertable = false, updatable = false)
	public ScoFornecedor getTransportadora() {
		return transportadora;
	}

	public void setTransportadora(ScoFornecedor transportadora) {
		this.transportadora = transportadora;
	}
	
}
