package br.gov.mec.aghu.prescricaomedica.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioIdentificacaoComponenteNPT;
import br.gov.mec.aghu.dominio.DominioTipoVolume;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * #990 VO de Componentes - C5 - VO neto
 * 
 * @author paulo
 *
 */
public class MpmItemPrescricaoNptVO implements BaseBean {

	private static final long serialVersionUID = 65271128968390795L;

	private Integer cptPnpAtdSeq;
	private Integer cptPnpSeq;
	private Short cptSeqp;
	private Short seqp;
	private Integer cnpMedMatCodigo;
	private String descricaoComponente;
	private Integer fdsSeq;
	private String unidadeComponente;
	private BigDecimal qtdePrescrita;
	private BigDecimal qtdeBaseCalculo;
	private BigDecimal qtdeCalculada;
	private DominioTipoVolume tipoParamCalculo;
	private BigDecimal totParamCalculo;
	private Integer ummSeq;
	private Integer pcnCnpMedMatCodigo;
	private Short pcnSeqp;
	private BigDecimal percParamCalculo;
	private DominioIdentificacaoComponenteNPT identifComponente;
	
	public enum Fields {
		CPT_PNP_ATD_SEQ("cptPnpAtdSeq"),
		CPT_PNP_SEQ("cptPnpSeq"),		
		CPT_SEQP("cptSeqp"),
		SEQP("seqp"),
		CNP_MED_MAT_CODIGO("cnpMedMatCodigo"),
		DESCRICAO_COMPONENTE("descricaoComponente"),
		FDS_SEQ("fdsSeq"),
		UNIDADE_COMPONENTE("unidadeComponente"),
		QTDE_PRESCRITA("qtdePrescrita"),
		QTDE_BASE_CALCULO("qtdeBaseCalculo"),
		QTDE_CALCULADA("qtdeCalculada"),
		TIPO_PARAM_CALCULO("tipoParamCalculo"),
		TOT_PARAM_CALCULO("totParamCalculo"),
		UMM_SEQ("ummSeq"),
		PCN_CNP_MED_MAT_CODIGO("pcnCnpMedMatCodigo"),
		PCN_SEQP("pcnSeqp"),
		PERC_PARAM_CALCULO("percParamCalculo"),
		IDENTIF_COMPONENTE("identifComponente");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getCptPnpAtdSeq() {
		return cptPnpAtdSeq;
	}

	public void setCptPnpAtdSeq(Integer cptPnpAtdSeq) {
		this.cptPnpAtdSeq = cptPnpAtdSeq;
	}

	public Integer getCptPnpSeq() {
		return cptPnpSeq;
	}

	public void setCptPnpSeq(Integer cptPnpSeq) {
		this.cptPnpSeq = cptPnpSeq;
	}

	public Short getCptSeqp() {
		return cptSeqp;
	}

	public void setCptSeqp(Short cptSeqp) {
		this.cptSeqp = cptSeqp;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Integer getCnpMedMatCodigo() {
		return cnpMedMatCodigo;
	}

	public void setCnpMedMatCodigo(Integer cnpMedMatCodigo) {
		this.cnpMedMatCodigo = cnpMedMatCodigo;
	}

	public String getDescricaoComponente() {
		return descricaoComponente;
	}

	public void setDescricaoComponente(String descricaoComponente) {
		this.descricaoComponente = descricaoComponente;
	}

	public Integer getFdsSeq() {
		return fdsSeq;
	}

	public void setFdsSeq(Integer fdsSeq) {
		this.fdsSeq = fdsSeq;
	}

	public String getUnidadeComponente() {
		return unidadeComponente;
	}

	public void setUnidadeComponente(String unidadeComponente) {
		this.unidadeComponente = unidadeComponente;
	}

	public BigDecimal getQtdePrescrita() {
		return qtdePrescrita;
	}

	public void setQtdePrescrita(BigDecimal qtdePrescrita) {
		this.qtdePrescrita = qtdePrescrita;
	}

	public BigDecimal getQtdeBaseCalculo() {
		return qtdeBaseCalculo;
	}

	public void setQtdeBaseCalculo(BigDecimal qtdeBaseCalculo) {
		this.qtdeBaseCalculo = qtdeBaseCalculo;
	}

	public BigDecimal getQtdeCalculada() {
		return qtdeCalculada;
	}

	public void setQtdeCalculada(BigDecimal qtdeCalculada) {
		this.qtdeCalculada = qtdeCalculada;
	}

	public DominioTipoVolume getTipoParamCalculo() {
		return tipoParamCalculo;
	}

	public void setTipoParamCalculo(DominioTipoVolume tipoParamCalculo) {
		this.tipoParamCalculo = tipoParamCalculo;
	}

	public BigDecimal getTotParamCalculo() {
		return totParamCalculo;
	}

	public void setTotParamCalculo(BigDecimal totParamCalculo) {
		this.totParamCalculo = totParamCalculo;
	}

	public Integer getUmmSeq() {
		return ummSeq;
	}

	public void setUmmSeq(Integer ummSeq) {
		this.ummSeq = ummSeq;
	}

	public Integer getPcnCnpMedMatCodigo() {
		return pcnCnpMedMatCodigo;
	}

	public void setPcnCnpMedMatCodigo(Integer pcnCnpMedMatCodigo) {
		this.pcnCnpMedMatCodigo = pcnCnpMedMatCodigo;
	}

	public Short getPcnSeqp() {
		return pcnSeqp;
	}

	public void setPcnSeqp(Short pcnSeqp) {
		this.pcnSeqp = pcnSeqp;
	}

	public BigDecimal getPercParamCalculo() {
		return percParamCalculo;
	}

	public void setPercParamCalculo(BigDecimal percParamCalculo) {
		this.percParamCalculo = percParamCalculo;
	}

	public DominioIdentificacaoComponenteNPT getIdentifComponente() {
		return identifComponente;
	}

	public void setIdentifComponente(
			DominioIdentificacaoComponenteNPT identifComponente) {
		this.identifComponente = identifComponente;
	}

}
