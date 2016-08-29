package br.gov.mec.aghu.model;

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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@SuppressWarnings("serial")
@Entity
@Table(name = "SCE_E660INC_FORN")
@SequenceGenerator(allocationSize = 1, name = "sequence", sequenceName = "SCE_E660INC_SQ1")
public class SceE660IncForn extends BaseEntitySeq<Long> implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 436178972682441842L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
	@Column(name = "SEQ_INC", length = 10, nullable = false)
	private Long seq;

	@Column(name = "CODEMP", length = 4, nullable = false)
	private Short codemp;

	@Column(name = "CODFIL", length = 4, nullable = false)
	private Short codfil;

	@Column(name = "NUMNFI", length = 9, nullable = false)
	private Integer numnfi;

	@Column(name = "NUMNFF", length = 9, nullable = false)
	private Integer numnff;

	@Column(name = "CODSNF", length = 3, nullable = false)
	private String codsnf;

	@Column(name = "CODTNS", length = 5, nullable = false)
	private String codtns;

	@Column(name = "SEQINC", length = 4, nullable = false)
	private Short seqinc;

	@Column(name = "CODPRO", length = 20)
	private String codpro;

	@Column(name = "CODDER", length = 7)
	private String codder;

	@Column(name = "CPLPRO", length = 250)
	private String cplpro;

	@Column(name = "CODCLF", length = 3)
	private String codclf;

	@Column(name = "CLAFIS", length = 10)
	private String clafis;

	@Column(name = "QTDENT", length = 14)
	private BigDecimal qtdent;

	@Column(name = "UNIMED", length = 3)
	private String unimed;

	@Column(name = "VLRBIP", length = 15)
	private BigDecimal vlrbip;

	@Column(name = "PERIPI", length = 5)
	private BigDecimal peripi;

	@Column(name = "VLRIPI", length = 15)
	private BigDecimal vlripi;

	@Column(name = "VLRIIP", length = 15)
	private BigDecimal vlriip;

	@Column(name = "VLROIP", length = 15)
	private BigDecimal vlroip;

	@Column(name = "VLRBID", length = 15)
	private BigDecimal vlrbid;

	@Column(name = "VLRIPD", length = 15)
	private BigDecimal vlripd;

	@Column(name = "VLRBIC", length = 15)
	private BigDecimal vlrbic;

	@Column(name = "VLRBSI", length = 15)
	private BigDecimal vlrbsi;

	@Column(name = "VLRCIP", length = 15)
	private BigDecimal vlrcip;

	@Column(name = "SEQIPC", length = 3)
	private Short seqipc;

	@Column(name = "VLRDSC", length = 15)
	private BigDecimal vlrdsc;

	@Column(name = "CODSTR", length = 3)
	private String codstr;

	@Column(name = "CODTRD", length = 3)
	private String codtrd;

	@Column(name = "CODSER", length = 14)
	private String codser;

	@Column(name = "VLRCTB", length = 15)
	private BigDecimal vlrctb;

	@Column(name = "VLRDAC", length = 15)
	private BigDecimal vlrdac;

	@Column(name = "PERIIM", length = 5)
	private BigDecimal periim;

	@Column(name = "VLRBII", length = 15)
	private BigDecimal vlrbii;

	@Column(name = "VLRIIM", length = 15)
	private BigDecimal vlriim;

	@Column(name = "VLRRIS", length = 15)
	private BigDecimal vlrris;

	@Column(name = "PERICM", length = 5)
	private BigDecimal pericm;

	@Column(name = "VLRICM", length = 15)
	private BigDecimal vlricm;

	@Column(name = "VLROIC", length = 15)
	private BigDecimal vlroic;

	@Column(name = "VLRIIC", length = 15)
	private BigDecimal vlriic;

	@Column(name = "VLRDAI", length = 15)
	private BigDecimal vlrdai;

	@Column(name = "VLRSIC", length = 15)
	private BigDecimal vlrsic;

	@Column(name = "VLRBSD", length = 15)
	private BigDecimal vlrbsd;

	@Column(name = "VLRISD", length = 15)
	private BigDecimal vlrisd;

	@Column(name = "VLRBIS", length = 15)
	private BigDecimal vlrbis;

	@Column(name = "PERISS", length = 4)
	private BigDecimal periss;

	@Column(name = "VLRISS", length = 15)
	private BigDecimal vlriss;

	@Column(name = "VLRBIR", length = 15)
	private BigDecimal vlrbir;

	@Column(name = "PERIRF", length = 4)
	private BigDecimal perirf;

	@Column(name = "VLRIRF", length = 15)
	private BigDecimal vlrirf;

	@Column(name = "VLRBIN", length = 15)
	private BigDecimal vlrbin;

	@Column(name = "PERINS", length = 4)
	private BigDecimal perins;

	@Column(name = "VLRINS", length = 15)
	private BigDecimal vlrins;

	@Column(name = "VLRSEG", length = 15)
	private BigDecimal vlrseg;

	@Column(name = "VLRFRE", length = 15)
	private BigDecimal vlrfre;

	@Column(name = "PERCRT", length = 4)
	private BigDecimal percrt;

	@Column(name = "VLRBCT", length = 15)
	private BigDecimal vlrbct;

	@Column(name = "VLRCRT", length = 15)
	private BigDecimal vlrcrt;

	@Column(name = "PERPIT", length = 4)
	private BigDecimal perpit;

	@Column(name = "VLRBPT", length = 15)
	private BigDecimal vlrbpt;

	@Column(name = "VLRPIT", length = 15)
	private BigDecimal vlrpit;

	@Column(name = "PERCSL", length = 4)
	private BigDecimal percsl;

	@Column(name = "VLRBCL", length = 15)
	private BigDecimal vlrbcl;

	@Column(name = "VLRCSL", length = 15)
	private BigDecimal vlrcsl;

	@Column(name = "PEROUR", length = 4)
	private BigDecimal perour;

	@Column(name = "VLRBOR", length = 15)
	private BigDecimal vlrbor;

	@Column(name = "VLROUR", length = 15)
	private BigDecimal vlrour;

	@Column(name = "USUGER", length = 10)
	private Long usuger;

	@Column(name = "DATGER")
	private Date datger;

	@Column(name = "HORGER", length = 5)
	private Integer horger;

	@Column(name = "USUATU", length = 10)
	private Long usuatu;

	@Column(name = "DATATU")
	private Date datatu;

	@Column(name = "HORATU", length = 5)
	private Integer horatu;
	
	@Column(name = "CODEAN", length = 14)
	private String codean;
	
	@Column(name = "CODORI", length = 2)
	private String codori;
	
	@Column(name = "INFADI", length = 500)
	private String infadi;

	@ManyToOne
	@JoinColumn(name = "CODFOR", referencedColumnName = "NUMERO")
	private ScoFornecedor fornecedor;

	@Column(name="CODFOR", insertable=false, updatable=false)
	private Integer codigoFornecedor;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "CODPRO", referencedColumnName = "COD_MAT_FORN", nullable = false, insertable = false, updatable = false),
		@JoinColumn(name = "CODFOR", referencedColumnName = "FRN_NUMERO", nullable = false, insertable = false, updatable = false)
	})
	private SceRelacionamentoMaterialFornecedor relMatForn;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "NUMNFI", referencedColumnName = "NUMERO", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "CODFOR", referencedColumnName = "FRN_NUMERO", nullable = false, insertable = false, updatable = false) })
	private SceDocumentoFiscalEntrada sceDocumentoFiscalEntrada;
	
	// getters & setters

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public Short getCodemp() {
		return codemp;
	}

	public void setCodemp(Short codemp) {
		this.codemp = codemp;
	}

	public Short getCodfil() {
		return codfil;
	}

	public void setCodfil(Short codfil) {
		this.codfil = codfil;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Integer getNumnfi() {
		return numnfi;
	}

	public void setNumnfi(Integer numnfi) {
		this.numnfi = numnfi;
	}

	public Integer getNumnff() {
		return numnff;
	}

	public void setNumnff(Integer numnff) {
		this.numnff = numnff;
	}

	public String getCodsnf() {
		return codsnf;
	}

	public void setCodsnf(String codsnf) {
		this.codsnf = codsnf;
	}

	public String getCodtns() {
		return codtns;
	}

	public void setCodtns(String codtns) {
		this.codtns = codtns;
	}

	public Short getSeqinc() {
		return seqinc;
	}

	public void setSeqinc(Short seqinc) {
		this.seqinc = seqinc;
	}

	/**
	 * Código do produto
	 * 
	 * @return
	 */
	public String getCodpro() {
		return codpro;
	}

	/**
	 * Código do produto
	 * 
	 * @param codpro
	 */
	public void setCodpro(String codpro) {
		this.codpro = codpro;
	}

	public String getCodder() {
		return codder;
	}

	public void setCodder(String codder) {
		this.codder = codder;
	}

	public String getCplpro() {
		return cplpro;
	}

	public void setCplpro(String cplpro) {
		this.cplpro = cplpro;
	}

	public String getCodclf() {
		return codclf;
	}

	public void setCodclf(String codclf) {
		this.codclf = codclf;
	}

	public String getClafis() {
		return clafis;
	}

	public void setClafis(String clafis) {
		this.clafis = clafis;
	}

	public BigDecimal getQtdent() {
		return qtdent;
	}

	public void setQtdent(BigDecimal qtdent) {
		this.qtdent = qtdent;
	}

	public String getUnimed() {
		return unimed;
	}

	public void setUnimed(String unimed) {
		this.unimed = unimed;
	}

	public BigDecimal getVlrbip() {
		return vlrbip;
	}

	public void setVlrbip(BigDecimal vlrbip) {
		this.vlrbip = vlrbip;
	}

	public BigDecimal getPeripi() {
		return peripi;
	}

	public void setPeripi(BigDecimal peripi) {
		this.peripi = peripi;
	}

	public BigDecimal getVlripi() {
		return vlripi;
	}

	public void setVlripi(BigDecimal vlripi) {
		this.vlripi = vlripi;
	}

	public BigDecimal getVlriip() {
		return vlriip;
	}

	public void setVlriip(BigDecimal vlriip) {
		this.vlriip = vlriip;
	}

	public BigDecimal getVlroip() {
		return vlroip;
	}

	public void setVlroip(BigDecimal vlroip) {
		this.vlroip = vlroip;
	}

	public BigDecimal getVlrbid() {
		return vlrbid;
	}

	public void setVlrbid(BigDecimal vlrbid) {
		this.vlrbid = vlrbid;
	}

	public BigDecimal getVlripd() {
		return vlripd;
	}

	public void setVlripd(BigDecimal vlripd) {
		this.vlripd = vlripd;
	}

	public BigDecimal getVlrbic() {
		return vlrbic;
	}

	public void setVlrbic(BigDecimal vlrbic) {
		this.vlrbic = vlrbic;
	}

	public BigDecimal getVlrbsi() {
		return vlrbsi;
	}

	public void setVlrbsi(BigDecimal vlrbsi) {
		this.vlrbsi = vlrbsi;
	}

	public BigDecimal getVlrcip() {
		return vlrcip;
	}

	public void setVlrcip(BigDecimal vlrcip) {
		this.vlrcip = vlrcip;
	}

	public Short getSeqipc() {
		return seqipc;
	}

	public void setSeqipc(Short seqipc) {
		this.seqipc = seqipc;
	}

	public BigDecimal getVlrdsc() {
		return vlrdsc;
	}

	public void setVlrdsc(BigDecimal vlrdsc) {
		this.vlrdsc = vlrdsc;
	}

	public String getCodstr() {
		return codstr;
	}

	public void setCodstr(String codstr) {
		this.codstr = codstr;
	}

	public String getCodtrd() {
		return codtrd;
	}

	public void setCodtrd(String codtrd) {
		this.codtrd = codtrd;
	}

	public String getCodser() {
		return codser;
	}

	public void setCodser(String codser) {
		this.codser = codser;
	}

	public BigDecimal getVlrctb() {
		return vlrctb;
	}

	public void setVlrctb(BigDecimal vlrctb) {
		this.vlrctb = vlrctb;
	}

	public BigDecimal getVlrdac() {
		return vlrdac;
	}

	public void setVlrdac(BigDecimal vlrdac) {
		this.vlrdac = vlrdac;
	}

	public BigDecimal getPeriim() {
		return periim;
	}

	public void setPeriim(BigDecimal periim) {
		this.periim = periim;
	}

	public BigDecimal getVlrbii() {
		return vlrbii;
	}

	public void setVlrbii(BigDecimal vlrbii) {
		this.vlrbii = vlrbii;
	}

	public BigDecimal getVlriim() {
		return vlriim;
	}

	public void setVlriim(BigDecimal vlriim) {
		this.vlriim = vlriim;
	}

	public BigDecimal getVlrris() {
		return vlrris;
	}

	public void setVlrris(BigDecimal vlrris) {
		this.vlrris = vlrris;
	}

	public BigDecimal getPericm() {
		return pericm;
	}

	public void setPericm(BigDecimal pericm) {
		this.pericm = pericm;
	}

	public BigDecimal getVlricm() {
		return vlricm;
	}

	public void setVlricm(BigDecimal vlricm) {
		this.vlricm = vlricm;
	}

	public BigDecimal getVlroic() {
		return vlroic;
	}

	public void setVlroic(BigDecimal vlroic) {
		this.vlroic = vlroic;
	}

	public BigDecimal getVlriic() {
		return vlriic;
	}

	public void setVlriic(BigDecimal vlriic) {
		this.vlriic = vlriic;
	}

	public BigDecimal getVlrdai() {
		return vlrdai;
	}

	public void setVlrdai(BigDecimal vlrdai) {
		this.vlrdai = vlrdai;
	}

	public BigDecimal getVlrsic() {
		return vlrsic;
	}

	public void setVlrsic(BigDecimal vlrsic) {
		this.vlrsic = vlrsic;
	}

	public BigDecimal getVlrbsd() {
		return vlrbsd;
	}

	public void setVlrbsd(BigDecimal vlrbsd) {
		this.vlrbsd = vlrbsd;
	}

	public BigDecimal getVlrisd() {
		return vlrisd;
	}

	public void setVlrisd(BigDecimal vlrisd) {
		this.vlrisd = vlrisd;
	}

	public BigDecimal getVlrbis() {
		return vlrbis;
	}

	public void setVlrbis(BigDecimal vlrbis) {
		this.vlrbis = vlrbis;
	}

	public BigDecimal getPeriss() {
		return periss;
	}

	public void setPeriss(BigDecimal periss) {
		this.periss = periss;
	}

	public BigDecimal getVlriss() {
		return vlriss;
	}

	public void setVlriss(BigDecimal vlriss) {
		this.vlriss = vlriss;
	}

	public BigDecimal getVlrbir() {
		return vlrbir;
	}

	public void setVlrbir(BigDecimal vlrbir) {
		this.vlrbir = vlrbir;
	}

	public BigDecimal getPerirf() {
		return perirf;
	}

	public void setPerirf(BigDecimal perirf) {
		this.perirf = perirf;
	}

	public BigDecimal getVlrirf() {
		return vlrirf;
	}

	public void setVlrirf(BigDecimal vlrirf) {
		this.vlrirf = vlrirf;
	}

	public BigDecimal getVlrbin() {
		return vlrbin;
	}

	public void setVlrbin(BigDecimal vlrbin) {
		this.vlrbin = vlrbin;
	}

	public BigDecimal getPerins() {
		return perins;
	}

	public void setPerins(BigDecimal perins) {
		this.perins = perins;
	}

	public BigDecimal getVlrins() {
		return vlrins;
	}

	public void setVlrins(BigDecimal vlrins) {
		this.vlrins = vlrins;
	}

	/**
	 * Valor do seguro
	 * 
	 * @return
	 */
	public BigDecimal getVlrseg() {
		return vlrseg;
	}

	/**
	 * Valor do seguro
	 * 
	 * @param vlrseg
	 */
	public void setVlrseg(BigDecimal vlrseg) {
		this.vlrseg = vlrseg;
	}

	/**
	 * Valor do frete
	 * 
	 * @return
	 */
	public BigDecimal getVlrfre() {
		return vlrfre;
	}

	/**
	 * Valor do frete
	 * 
	 * @param vlrfre
	 */
	public void setVlrfre(BigDecimal vlrfre) {
		this.vlrfre = vlrfre;
	}

	public BigDecimal getPercrt() {
		return percrt;
	}

	public void setPercrt(BigDecimal percrt) {
		this.percrt = percrt;
	}

	public BigDecimal getVlrbct() {
		return vlrbct;
	}

	public void setVlrbct(BigDecimal vlrbct) {
		this.vlrbct = vlrbct;
	}

	public BigDecimal getVlrcrt() {
		return vlrcrt;
	}

	public void setVlrcrt(BigDecimal vlrcrt) {
		this.vlrcrt = vlrcrt;
	}

	public BigDecimal getPerpit() {
		return perpit;
	}

	public void setPerpit(BigDecimal perpit) {
		this.perpit = perpit;
	}

	public BigDecimal getVlrbpt() {
		return vlrbpt;
	}

	public void setVlrbpt(BigDecimal vlrbpt) {
		this.vlrbpt = vlrbpt;
	}

	public BigDecimal getVlrpit() {
		return vlrpit;
	}

	public void setVlrpit(BigDecimal vlrpit) {
		this.vlrpit = vlrpit;
	}

	public BigDecimal getPercsl() {
		return percsl;
	}

	public void setPercsl(BigDecimal percsl) {
		this.percsl = percsl;
	}

	public BigDecimal getVlrbcl() {
		return vlrbcl;
	}

	public void setVlrbcl(BigDecimal vlrbcl) {
		this.vlrbcl = vlrbcl;
	}

	public BigDecimal getVlrcsl() {
		return vlrcsl;
	}

	public void setVlrcsl(BigDecimal vlrcsl) {
		this.vlrcsl = vlrcsl;
	}

	public BigDecimal getPerour() {
		return perour;
	}

	public void setPerour(BigDecimal perour) {
		this.perour = perour;
	}

	public BigDecimal getVlrbor() {
		return vlrbor;
	}

	public void setVlrbor(BigDecimal vlrbor) {
		this.vlrbor = vlrbor;
	}

	public BigDecimal getVlrour() {
		return vlrour;
	}

	public void setVlrour(BigDecimal vlrour) {
		this.vlrour = vlrour;
	}

	public Long getUsuger() {
		return usuger;
	}

	public void setUsuger(Long usuger) {
		this.usuger = usuger;
	}

	public Date getDatger() {
		return datger;
	}

	public void setDatger(Date datger) {
		this.datger = datger;
	}

	public Integer getHorger() {
		return horger;
	}

	public void setHorger(Integer horger) {
		this.horger = horger;
	}

	public Long getUsuatu() {
		return usuatu;
	}

	public void setUsuatu(Long usuatu) {
		this.usuatu = usuatu;
	}

	public Date getDatatu() {
		return datatu;
	}

	public void setDatatu(Date datatu) {
		this.datatu = datatu;
	}

	public Integer getHoratu() {
		return horatu;
	}

	public void setHoratu(Integer horatu) {
		this.horatu = horatu;
	}

	public String getCodean() {
		return codean;
	}

	public void setCodean(String codean) {
		this.codean = codean;
	}

	public String getCodori() {
		return codori;
	}

	public void setCodori(String codori) {
		this.codori = codori;
	}

	public String getInfadi() {
		return infadi;
	}

	public void setInfadi(String infadi) {
		this.infadi = infadi;
	}
	
	public Integer getCodigoFornecedor() {
		return codigoFornecedor;
	}

	public void setCodigoFornecedor(Integer codigoFornecedor) {
		this.codigoFornecedor = codigoFornecedor;
	}

	public SceRelacionamentoMaterialFornecedor getRelMatForn() {
		return relMatForn;
	}
	
	public void setRelMatForn(SceRelacionamentoMaterialFornecedor relMatForn) {
		this.relMatForn = relMatForn;
	}
	
	public SceDocumentoFiscalEntrada getSceDocumentoFiscalEntrada() {
		return sceDocumentoFiscalEntrada;
	}

	public void setSceDocumentoFiscalEntrada(
			SceDocumentoFiscalEntrada sceDocumentoFiscalEntrada) {
		this.sceDocumentoFiscalEntrada = sceDocumentoFiscalEntrada;
	}
	
	// outros	

	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	public boolean equals(Object other) {	
		if (!(other instanceof SceE660IncForn)) {
			return false;
		}
		SceE660IncForn castOther = (SceE660IncForn) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}
	
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}
	
	public enum Fields {
		SEQINC("seqinc"),
		SEQ_INC("seq"),
		CODIGOFORNECEDOR("codigoFornecedor"),
		FORNECEDOR("fornecedor"),
		NUMNFI("numnfi"),
		CODPRO("codpro"),
		CPLPRO("cplpro"),
		QTDENT("qtdent"),
		VLRCTB("vlrctb"),
		VLRBIC("vlrbic"),
		CODCLF("codclf"),
		CLAFIS("clafis"),
		INFADI("infadi"),
		UNIMED("unimed"),
		CODEAN("codean"),
		CODSTR("codstr"),
		VLRICM("vlricm"),
		VLRBSI("vlrbsi"),
		VLRIPI("vlripi"),
		PERICM("pericm"),
		PERIPI("peripi"),
		CODORI("codori"),
		REL_MAT_FORN("relMatForn"),
		DFE("sceDocumentoFiscalEntrada")
		;
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

}