package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * Por não fazer parte de nenhum módulo no AGH, iremos considerar que este POJO
 * é do módulo de Internação, pois é o módulo que ele está sendo utilizado no
 * sistema.
 */
@Entity
@Table(name = "PAC_INTD_CONV", schema = "CONV")
public class PacIntdConv extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6852406418591327224L;
	private Integer seq;
	private Integer codPrnt;
	private Date dataInt;
	private Byte codAcomConv;
	private String nomePac;
	private String indDifcClas;
	private Byte codClin;
	private String qrtoLto;
	private Date horaAdmsPac;
	private Date horaAltaMdca;
	private Date dataProcCirc;
	private Date dataAltaMdca;
	private Date dataSaidPac;
	private Short codConv;
	private String sglaEsp;
	private String tipoAtd;
	private Integer atdSeq;
	private String indicacaoClinica;
	private Date dthrSolicitacao;
	private Short qtdeSolicitada;
	private Short qtdeAutorizada;
	private Set<CntaConv> cntaConvs = new HashSet<CntaConv>(0);

	public PacIntdConv() {
	}

	public PacIntdConv(Integer seq, Integer codPrnt, Date dataInt, Byte codAcomConv,
			String nomePac, String indDifcClas, Short codConv) {
		this.seq = seq;
		this.codPrnt = codPrnt;
		this.dataInt = dataInt;
		this.codAcomConv = codAcomConv;
		this.nomePac = nomePac;
		this.indDifcClas = indDifcClas;
		this.codConv = codConv;
	}

	public PacIntdConv(Integer seq, Integer codPrnt, Date dataInt, Byte codAcomConv,
			String nomePac, String indDifcClas, Byte codClin,
			String qrtoLto, Date horaAdmsPac, Date horaAltaMdca,
			Date dataProcCirc, Date dataAltaMdca, Date dataSaidPac,
			Short codConv, String sglaEsp, String tipoAtd, Integer atdSeq,
			String indicacaoClinica, Date dthrSolicitacao,
			Short qtdeSolicitada, Short qtdeAutorizada, Set<CntaConv> cntaConvs) {
		this.seq = seq;
		this.codPrnt = codPrnt;
		this.dataInt = dataInt;
		this.codAcomConv = codAcomConv;
		this.nomePac = nomePac;
		this.indDifcClas = indDifcClas;
		this.codClin = codClin;
		this.qrtoLto = qrtoLto;
		this.horaAdmsPac = horaAdmsPac;
		this.horaAltaMdca = horaAltaMdca;
		this.dataProcCirc = dataProcCirc;
		this.dataAltaMdca = dataAltaMdca;
		this.dataSaidPac = dataSaidPac;
		this.codConv = codConv;
		this.sglaEsp = sglaEsp;
		this.tipoAtd = tipoAtd;
		this.atdSeq = atdSeq;
		this.indicacaoClinica = indicacaoClinica;
		this.dthrSolicitacao = dthrSolicitacao;
		this.qtdeSolicitada = qtdeSolicitada;
		this.qtdeAutorizada = qtdeAutorizada;
		this.cntaConvs = cntaConvs;
	}

	@Id
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "COD_PRNT", nullable = false, precision = 9, scale = 0)
	public Integer getCodPrnt() {
		return this.codPrnt;
	}

	public void setCodPrnt(Integer codPrnt) {
		this.codPrnt = codPrnt;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_INT", nullable = false, length = 7)
	public Date getDataInt() {
		return this.dataInt;
	}

	public void setDataInt(Date dataInt) {
		this.dataInt = dataInt;
	}

	@Column(name = "COD_ACOM_CONV", nullable = false, precision = 2, scale = 0)
	public Byte getCodAcomConv() {
		return this.codAcomConv;
	}

	public void setCodAcomConv(Byte codAcomConv) {
		this.codAcomConv = codAcomConv;
	}

	@Column(name = "NOME_PAC", nullable = false, length = 60)
	@Length(max = 60)
	public String getNomePac() {
		return this.nomePac;
	}

	public void setNomePac(String nomePac) {
		this.nomePac = nomePac;
	}

	@Column(name = "IND_DIFC_CLAS", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndDifcClas() {
		return this.indDifcClas;
	}

	public void setIndDifcClas(String indDifcClas) {
		this.indDifcClas = indDifcClas;
	}

	@Column(name = "COD_CLIN", precision = 1, scale = 0)
	public Byte getCodClin() {
		return this.codClin;
	}

	public void setCodClin(Byte codClin) {
		this.codClin = codClin;
	}

	@Column(name = "QRTO_LTO", length = 5)
	@Length(max = 5)
	public String getQrtoLto() {
		return this.qrtoLto;
	}

	public void setQrtoLto(String qrtoLto) {
		this.qrtoLto = qrtoLto;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "HORA_ADMS_PAC", length = 7)
	public Date getHoraAdmsPac() {
		return this.horaAdmsPac;
	}

	public void setHoraAdmsPac(Date horaAdmsPac) {
		this.horaAdmsPac = horaAdmsPac;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "HORA_ALTA_MDCA", length = 7)
	public Date getHoraAltaMdca() {
		return this.horaAltaMdca;
	}

	public void setHoraAltaMdca(Date horaAltaMdca) {
		this.horaAltaMdca = horaAltaMdca;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_PROC_CIRC", length = 7)
	public Date getDataProcCirc() {
		return this.dataProcCirc;
	}

	public void setDataProcCirc(Date dataProcCirc) {
		this.dataProcCirc = dataProcCirc;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_ALTA_MDCA", length = 7)
	public Date getDataAltaMdca() {
		return this.dataAltaMdca;
	}

	public void setDataAltaMdca(Date dataAltaMdca) {
		this.dataAltaMdca = dataAltaMdca;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_SAID_PAC", length = 7)
	public Date getDataSaidPac() {
		return this.dataSaidPac;
	}

	public void setDataSaidPac(Date dataSaidPac) {
		this.dataSaidPac = dataSaidPac;
	}

	@Column(name = "COD_CONV", nullable = false, precision = 3, scale = 0)
	public Short getCodConv() {
		return this.codConv;
	}

	public void setCodConv(Short codConv) {
		this.codConv = codConv;
	}

	@Column(name = "SGLA_ESP", length = 3)
	@Length(max = 3)
	public String getSglaEsp() {
		return this.sglaEsp;
	}

	public void setSglaEsp(String sglaEsp) {
		this.sglaEsp = sglaEsp;
	}

	@Column(name = "TIPO_ATD", length = 3)
	@Length(max = 3)
	public String getTipoAtd() {
		return this.tipoAtd;
	}

	public void setTipoAtd(String tipoAtd) {
		this.tipoAtd = tipoAtd;
	}

	@Column(name = "ATD_SEQ", precision = 7, scale = 0)
	public Integer getAtdSeq() {
		return this.atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	@Column(name = "INDICACAO_CLINICA", length = 200)
	@Length(max = 200)
	public String getIndicacaoClinica() {
		return this.indicacaoClinica;
	}

	public void setIndicacaoClinica(String indicacaoClinica) {
		this.indicacaoClinica = indicacaoClinica;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DTHR_SOLICITACAO", length = 7)
	public Date getDthrSolicitacao() {
		return this.dthrSolicitacao;
	}

	public void setDthrSolicitacao(Date dthrSolicitacao) {
		this.dthrSolicitacao = dthrSolicitacao;
	}

	@Column(name = "QTDE_SOLICITADA", precision = 3, scale = 0)
	public Short getQtdeSolicitada() {
		return this.qtdeSolicitada;
	}

	public void setQtdeSolicitada(Short qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}

	@Column(name = "QTDE_AUTORIZADA", precision = 3, scale = 0)
	public Short getQtdeAutorizada() {
		return this.qtdeAutorizada;
	}

	public void setQtdeAutorizada(Short qtdeAutorizada) {
		this.qtdeAutorizada = qtdeAutorizada;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pacIntdConv")
	public Set<CntaConv> getCntaConvs() {
		return this.cntaConvs;
	}

	public void setCntaConvs(Set<CntaConv> cntaConvs) {
		this.cntaConvs = cntaConvs;
	}

	public enum Fields {
		SEQ("seq"), COD_PRNT("codPrnt"), DATA_INT("dataInt"), ATD_SEQ("atdSeq"), CNTA_CONVS("cntaConvs");

		private String fields;

		private Fields(String fields) {
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
		if (!(obj instanceof PacIntdConv)) {
			return false;
		}
		PacIntdConv other = (PacIntdConv) obj;
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
