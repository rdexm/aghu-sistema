package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

public class RelacaoDeOrtesesProtesesVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7180024389780125585L;

	private String pacnome;
	
	private String pacNomeLinhaAnterior;
	
	private Integer srmseq;
	
	private String datautl;
	
	private Date datautlDate;
	
	private String fatBuscaCumLinhaAnterior;
	
	private String fatBuscaCum;
	
	private Long codropm;
	
	private Long codproc;
	
	private String dcih;
	
	private Long aih;
	
	private String leito;
	
	private String enfermaria;
	
	private String numeroLeito;
	
	private Integer prontuario;
	
	private String prontuarioformatado;
	
	private Long qtde;
	
	private Short quantidade;
	
	private Long cgcfornecedor;
	
	private String cgcformatado;
	
//	private BigDecimal valorAnestesista;
//	
//	private BigDecimal valorProcedimento;
//	
//	private BigDecimal valorSadt;
//	
//	private BigDecimal valorServHosp;
//	
//	private BigDecimal valorServProf;
	
//	private Short quantidade;
	
	private BigDecimal valorunit;
	
	private BigDecimal valorapres;
	
	private String valorapresformatado;
	
	private Timestamp ordem;
	
	private Short cpgCphCspCnvCodigo;
	
	private Byte cpgCphCspSeq;
	
	private Integer iphSeq;
	
	private Short iphPhoSeq;
	
	private Integer cthSeq;
	
	DecimalFormat df = new DecimalFormat("#,##0.00");

	public String getPacnome() {
		return pacnome;
	}

	public void setPacnome(String pacnome) {
		this.pacnome = pacnome;
	}

	public Integer getSrmseq() {
		return srmseq;
	}

	public void setSrmseq(Integer srmseq) {
		this.srmseq = srmseq;
	}

	public String getDatautl() {
		if (getDatautlDate() == null) {
			datautl = " ";
		} else {
			datautl = DateUtil.obterDataFormatada(getDatautlDate(), DateConstants.DATE_PATTERN_DDMMYYYY);
		}
		return datautl;
	}

	public void setDatautl(String datautl) {
		this.datautl = datautl;
	}

	public Date getDatautlDate() {
		return datautlDate;
	}

	public void setDatautlDate(Date datautlDate) {
		this.datautlDate = datautlDate;
	}

	public Long getCodropm() {
		return codropm;
	}

	public void setCodropm(Long codropm) {
		this.codropm = codropm;
	}

	public Long getCodproc() {
		return codproc;
	}

	public void setCodproc(Long codproc) {
		this.codproc = codproc;
	}

	public String getDcih() {
		return dcih;
	}

	public void setDcih(String dcih) {
		this.dcih = dcih;
	}

	public Long getAih() {
		return aih;
	}

	public void setAih(Long aih) {
		this.aih = aih;
	}

	public String getLeito() {
		leito = getEnfermaria() + getNumeroLeito();
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Long getQtde() {
		return qtde;
	}

	public void setQtde(Long qtde) {
		this.qtde = qtde;
	}

	public Long getCgcfornecedor() {
		return cgcfornecedor;
	}

	public void setCgcfornecedor(Long cgcfornecedor) {
		this.cgcfornecedor = cgcfornecedor;
	}

	public BigDecimal getValorunit() {
		valorunit = getValorapres().divide(getQtde() == null ? BigDecimal.ONE : new BigDecimal(getQtde())); 
		return valorunit;
	}

	public void setValorunit(BigDecimal valorunit) {
		this.valorunit = valorunit;
	}

	public BigDecimal getValorapres() {
//		valorapres = getValorAnestesista().
//				add(getValorProcedimento()).
//				add(getValorSadt()).
//				add(getValorServHosp()).
//				add(getValorServProf()); 
		return valorapres;
	}

	public void setValorapres(BigDecimal valorapres) {
		this.valorapres = valorapres;
	}

	public Timestamp getOrdem() {
		return ordem;
	}

	public void setOrdem(Timestamp ordem) {
		this.ordem = ordem;
	}

	public String getCgcformatado() {
		if (getCgcfornecedor() != null) {
			cgcformatado = CoreUtil.formatarCNPJ(getCgcfornecedor());
		} else {
			cgcformatado = " ";
		}
		return cgcformatado;
	}

	public void setCgcformatado(String cgcFormatado) {
		this.cgcformatado = cgcFormatado;
	}

	public String getProntuarioformatado() {
		if(getProntuario() != null){
			prontuarioformatado = CoreUtil.formataProntuario(getProntuario());
		}else{
			prontuarioformatado = " ";
		}
		
		return prontuarioformatado;
	}

	public void setProntuarioformatado(String prontuarioformatado) {
		this.prontuarioformatado = prontuarioformatado;
	}

	public String getValorapresformatado() {
		if(getValorapres() != null){
			valorapresformatado = df.format(getValorapres());
		}	
		return valorapresformatado;
	}

	public void setValorapresformatado(String valorapresformatado) {
		this.valorapresformatado = valorapresformatado;
	}
	
	public String getEnfermaria() {
		if (enfermaria == null) {
			enfermaria = "";
		}
		return enfermaria;
	}

	public void setEnfermaria(String enfermaria) {
		this.enfermaria = enfermaria;
	}

	public String getNumeroLeito() {
		if (numeroLeito == null) {
			numeroLeito = "";
		}
		return numeroLeito;
	}

	public void setNumeroLeito(String numeroLeito) {
		this.numeroLeito = numeroLeito;
	}
	
	public Short getCpgCphCspCnvCodigo() {
		return cpgCphCspCnvCodigo;
	}

	public void setCpgCphCspCnvCodigo(Short cpgCphCspCnvCodigo) {
		this.cpgCphCspCnvCodigo = cpgCphCspCnvCodigo;
	}

	public Byte getCpgCphCspSeq() {
		return cpgCphCspSeq;
	}

	public void setCpgCphCspSeq(Byte cpgCphCspSeq) {
		this.cpgCphCspSeq = cpgCphCspSeq;
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}
	
	public Short getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}
	
	public String getPacNomeLinhaAnterior() {
		if (pacNomeLinhaAnterior == null) {
			pacNomeLinhaAnterior = "";
		}
		return pacNomeLinhaAnterior;
	}

	public void setPacNomeLinhaAnterior(String pacNomeLinhaAnterior) {
		this.pacNomeLinhaAnterior = pacNomeLinhaAnterior;
	}

	public String getFatBuscaCumLinhaAnterior() {
		if (fatBuscaCumLinhaAnterior == null) {
			fatBuscaCumLinhaAnterior = "";
		}
		return fatBuscaCumLinhaAnterior;
	}

	public void setFatBuscaCumLinhaAnterior(String fatBuscaCumLinhaAnterior) {
		this.fatBuscaCumLinhaAnterior = fatBuscaCumLinhaAnterior;
	}

	public String getFatBuscaCum() {
		String srmSeq = this.getSrmseq() == null ? "" : this.getSrmseq().toString();
		String cgc = this.getCgcfornecedor() == null ? "" : this.getCgcfornecedor().toString();
		fatBuscaCum = (srmSeq + this.getDatautl()+cgc);
		return fatBuscaCum;
	}

	public void setFatBuscaCum(String fatBuscaCum) {
		this.fatBuscaCum = fatBuscaCum;
	}

	public enum Fields{
		PAC_NOME("pacnome"),
		SRM_SEQ("srmseq"),
		DATA_UTL("datautl"),
		DATA_UTL_DT("datautlDate"),
		COD_ROPM("codropm"),
		COD_PROC("codproc"),
		DCIH("dcih"),
		AIH("aih"),
		LEITO("leito"),
		ENFERMARIA("enfermaria"),
		NUMERO_LEITO("numeroLeito"),
		PRONTUARIO("prontuario"),
		PRONTUARIO_FORMATADO("prontuarioformatado"),
		QTDE("qtde"),
		QUANTIDADE("quantidade"),
		CGC_FORNECEDOR("cgcfornecedor"),
		CGC_FORMATADO("cgcformatado"),
		VALOR_UNIT("valorunit"),
		VALOR_APRES("valorapres"),
		CPG_CPH_CSP_CNV_CODIGO("cpgCphCspCnvCodigo"),
		CPG_CPH_CSP_SEQ("cpgCphCspSeq"),
		IPH_SEQ("iphSeq"),
		IPH_PHO_SEQ("iphPhoSeq"),
		CTH_SEQ("cthSeq"),
		ORDEM("ordem");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RelacaoDeOrtesesProtesesVO)) {
			return false;
		}
		RelacaoDeOrtesesProtesesVO castOther = (RelacaoDeOrtesesProtesesVO) obj;
		return new EqualsBuilder().
				append(this.getPacnome(), castOther.getPacnome()).
				append(this.getSrmseq(), castOther.getSrmseq()).
				append(this.getDatautl(), castOther.getDatautl()).
				append(this.getCgcfornecedor(), castOther.getCgcfornecedor()).
				append(this.getCodropm(), castOther.getCodropm()).
				append(this.getDcih(), castOther.getDcih()).
				append(this.getAih(), castOther.getAih()).
				append(this.getLeito(), castOther.getLeito()).
				append(this.getProntuario(), castOther.getProntuario()).
				append(this.getQtde(), castOther.getQtde()).
				append(this.getQuantidade(), castOther.getQuantidade()).
				append(this.getValorunit(), castOther.getValorunit()).
				append(this.getValorapres(), castOther.getValorapres()).
				isEquals();
	}
	@Override
	public int hashCode() {
		return new HashCodeBuilder().
				append(this.getPacnome()).
				append(this.getSrmseq()).
				append(this.getDatautl()).
				append(this.getCgcfornecedor()).
				append(this.getCodropm()).
				append(this.getDcih()).
				append(this.getAih()).
				append(this.getLeito()).
				append(this.getProntuario()).
				append(this.getQtde()).
				append(this.getQuantidade()).
				append(this.getValorunit()).
				append(this.getValorapres()).
				toHashCode();
	}
}