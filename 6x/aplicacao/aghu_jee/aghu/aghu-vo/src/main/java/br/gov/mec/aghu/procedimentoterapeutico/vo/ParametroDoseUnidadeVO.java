package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioCalculoDose;
import br.gov.mec.aghu.dominio.DominioUnidadeBaseParametroCalculo;
import br.gov.mec.aghu.dominio.DominioUnidadeIdade;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.MptProtocoloItemMedicamentos;
import br.gov.mec.aghu.view.VMpmDosagem;
import br.gov.mec.aghu.core.commons.BaseBean;


public class ParametroDoseUnidadeVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4432244978425807539L;
	
	
	private Integer seq;
	private BigDecimal dose;
	private DominioUnidadeBaseParametroCalculo unidBaseCalculo;
	private AfaFormaDosagem  afaFormaDosagem;
	private Integer fdsSeq;
	private Integer medMatCodigo;
	private DominioCalculoDose tipoCalculo;
	private Short idadeMinima;
	private Short idadeMaxima;
	private DominioUnidadeIdade unidIdade;
	private BigDecimal pesoMinimo;
	private BigDecimal pesoMaximo;
	private BigDecimal doseMaximaUnitaria;
	private MptProtocoloItemMedicamentos mptProtocoloItemMedicamentos;
	private Long pmiSeq;
	private String alertaCalculoDose;
	private String descricao;
	private VMpmDosagem comboUnidade;
	private Boolean isEdicao = Boolean.FALSE;
	
	private AfaFormaDosagemVO afaFormaDosagemVO;
	
	public enum Fields {

		SEQ("seq"), 
		DOSE("dose"), 
		UNID_BASE_CALCULO("unidBaseCalculo"), 
		AFA_FORMA_DOSAGEM("afaFormaDosagem"), 
		FDS_SEQ("fdsSeq"), 
		TIPO_CALCULO("tipoCalculo"), 
		IDADE_MINIMA("idadeMinima"), 
		IDADE_MAXIMA("idadeMaxima"), 
		PESO_MINIMO("pesoMinimo"), 
		UNID_IDADE("unidIdade"), 
		PESO_MAXIMO("pesoMaximo"), 
		DOSE_MAXIMA_UNITARIA("doseMaximaUnitaria"), 
		MPT_PROTOCOLO_ITEM_MEDICAMENTOS("mptProtocoloItemMedicamentos"), 
		PMI_SEQ("pmiSeq"), 
		ALERTA_CALCULO_DOSE("alertaCalculoDose"), 
		COMBO_UNIDADE("comboUnidade"), 
		DESCRICAO("descricao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public BigDecimal getDose() {
		return dose;
	}
	public void setDose(BigDecimal dose) {
		this.dose = dose;
	}
	public DominioUnidadeBaseParametroCalculo getUnidBaseCalculo() {
		return unidBaseCalculo;
	}
	public void setUnidBaseCalculo(DominioUnidadeBaseParametroCalculo unidBaseCalculo) {
		this.unidBaseCalculo = unidBaseCalculo;
	}
	public AfaFormaDosagem getAfaFormaDosagem() {
		return afaFormaDosagem;
	}
	public void setAfaFormaDosagem(AfaFormaDosagem afaFormaDosagem) {
		this.afaFormaDosagem = afaFormaDosagem;
	}
	public Integer getFdsSeq() {
		return fdsSeq;
	}
	public void setFdsSeq(Integer fdsSeq) {
		this.fdsSeq = fdsSeq;
	}
	public DominioCalculoDose getTipoCalculo() {
		return tipoCalculo;
	}
	public void setTipoCalculo(DominioCalculoDose tipoCalculo) {
		this.tipoCalculo = tipoCalculo;
	}
	public Short getIdadeMinima() {
		return idadeMinima;
	}
	public void setIdadeMinima(Short idadeMinima) {
		this.idadeMinima = idadeMinima;
	}
	public Short getIdadeMaxima() {
		return idadeMaxima;
	}
	public void setIdadeMaxima(Short idadeMaxima) {
		this.idadeMaxima = idadeMaxima;
	}
	public BigDecimal getPesoMinimo() {
		return pesoMinimo;
	}
	public void setPesoMinimo(BigDecimal pesoMinimo) {
		this.pesoMinimo = pesoMinimo;
	}
	public DominioUnidadeIdade getUnidIdade() {
		return unidIdade;
	}
	public void setUnidIdade(DominioUnidadeIdade unidIdade) {
		this.unidIdade = unidIdade;
	}
	public BigDecimal getPesoMaximo() {
		return pesoMaximo;
	}
	public void setPesoMaximo(BigDecimal pesoMaximo) {
		this.pesoMaximo = pesoMaximo;
	}
	public BigDecimal getDoseMaximaUnitaria() {
		return doseMaximaUnitaria;
	}
	public void setDoseMaximaUnitaria(BigDecimal doseMaximaUnitaria) {
		this.doseMaximaUnitaria = doseMaximaUnitaria;
	}
	public MptProtocoloItemMedicamentos getMptProtocoloItemMedicamentos() {
		return mptProtocoloItemMedicamentos;
	}
	public void setMptProtocoloItemMedicamentos(MptProtocoloItemMedicamentos mptProtocoloItemMedicamentos) {
		this.mptProtocoloItemMedicamentos = mptProtocoloItemMedicamentos;
	}
	public Long getPmiSeq() {
		return pmiSeq;
	}
	public void setPmiSeq(Long pmiSeq) {
		this.pmiSeq = pmiSeq;
	}
	public String getAlertaCalculoDose() {
		return alertaCalculoDose;
	}
	public void setAlertaCalculoDose(String alertaCalculoDose) {
		this.alertaCalculoDose = alertaCalculoDose;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public VMpmDosagem getComboUnidade() {
		return comboUnidade;
	}
	public void setComboUnidade(VMpmDosagem comboUnidade) {
		this.comboUnidade = comboUnidade;
	}
	public Integer getMedMatCodigo() {
		return medMatCodigo;
	}
	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}
	public AfaFormaDosagemVO getAfaFormaDosagemVO() {
		return afaFormaDosagemVO;
	}
	public void setAfaFormaDosagemVO(AfaFormaDosagemVO afaFormaDosagemVO) {
		this.afaFormaDosagemVO = afaFormaDosagemVO;
	}
	public Boolean getIsEdicao() {
		return isEdicao;
	}
	public void setIsEdicao(Boolean isEdicao) {
		this.isEdicao = isEdicao;
	}

}
