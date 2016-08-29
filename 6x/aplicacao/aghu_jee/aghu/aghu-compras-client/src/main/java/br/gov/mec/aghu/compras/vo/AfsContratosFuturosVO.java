package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;

public class AfsContratosFuturosVO implements Serializable {

	private static final long serialVersionUID = 6402629699255519990L;

	private Integer numeroAf;
	private Short nroComplemento;
	private Integer nroFornecedor;
	private String razaoSocial;
	private Integer nroConvFinanceiro;
	private String convenioFinanceiro;
	// /----------------------------------------------
	private ScoAutorizacaoForn af;
	private Integer pkAf;
	// /----------------------------------------------
	private Long qtdadeItemPropostaForn;
	private BigDecimal valorUnitItemPropostaForn;
	private Integer freqEntregaLicitacao;
	// /----------------------------------------------
	private Boolean bTemSiasg;
	private String toolTipSemSiasg;
	private String toolTipComSiasg;
	// ----------------------------------------------
	private List<ItemAutorizFornVO> itensAF;

	public AfsContratosFuturosVO() {
		super();
	}

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getConvenioFinanceiro() {
		return convenioFinanceiro;
	}

	public void setConvenioFinanceiro(String convenioFinanceiro) {
		this.convenioFinanceiro = convenioFinanceiro;
	}

	public ScoAutorizacaoForn getAf() {
		return af;
	}

	public void setAf(ScoAutorizacaoForn af) {
		this.af = af;
	}

	public Integer getPkAf() {
		return pkAf;
	}

	public void setPkAf(Integer pkAf) {
		this.pkAf = pkAf;
	}

	public Boolean getbTemSiasg() {
		return bTemSiasg;
	}

	public void setbTemSiasg(Boolean bTemSiasg) {
		this.bTemSiasg = bTemSiasg;
	}

	public String getToolTipSemSiasg() {
		return toolTipSemSiasg;
	}

	public void setToolTipSemSiasg(String toolTipSemSiasg) {
		this.toolTipSemSiasg = toolTipSemSiasg;
	}

	public String getToolTipComSiasg() {
		return toolTipComSiasg;
	}

	public void setToolTipComSiasg(String toolTipComSiasg) {
		this.toolTipComSiasg = toolTipComSiasg;
	}

	public Long getQtdadeItemPropostaForn() {
		return qtdadeItemPropostaForn;
	}

	public void setQtdadeItemPropostaForn(Long qtdadeItemPropostaForn) {
		this.qtdadeItemPropostaForn = qtdadeItemPropostaForn;
	}

	public BigDecimal getValorUnitItemPropostaForn() {
		return valorUnitItemPropostaForn;
	}

	public void setValorUnitItemPropostaForn(
			BigDecimal valorUnitItemPropostaForn) {
		this.valorUnitItemPropostaForn = valorUnitItemPropostaForn;
	}

	public Integer getFreqEntregaLicitacao() {
		return freqEntregaLicitacao;
	}

	public void setFreqEntregaLicitacao(Integer freqEntregaLicitacao) {
		this.freqEntregaLicitacao = freqEntregaLicitacao;
	}

	public Integer getNroFornecedor() {
		return nroFornecedor;
	}

	public void setNroFornecedor(Integer nroFornecedor) {
		this.nroFornecedor = nroFornecedor;
	}

	public Integer getNroConvFinanceiro() {
		return nroConvFinanceiro;
	}

	public void setNroConvFinanceiro(Integer nroConvFinanceiro) {
		this.nroConvFinanceiro = nroConvFinanceiro;
	}

	public BigDecimal getValorTotalItemCalculado() {
		Long qtdade = this.getQtdadeItemPropostaForn();
		BigDecimal valorUnit = this.getValorUnitItemPropostaForn();
		Integer freqEntreg = this.getFreqEntregaLicitacao();

		if (qtdade == null) {
			qtdade = 1L;
		}

		if (valorUnit == null) {
			valorUnit = BigDecimal.ZERO;
		}

		if (freqEntreg == null) {
			freqEntreg = 1;
		}

		return new BigDecimal(qtdade * valorUnit.doubleValue() * freqEntreg);
	}

	public String getNumeroAfComComplemento() {
		return (this.getNumeroAf().toString() + "/" + this.getNroComplemento()
				.toString());
	}

	public List<ItemAutorizFornVO> getItensAF() {
		List<ItemAutorizFornVO> res = new ArrayList<ItemAutorizFornVO>();
		for (ScoItemAutorizacaoForn iprop : this.af.getItensAutorizacaoForn()) {
			for (ScoFaseSolicitacao fas : iprop.getScoFaseSolicitacao()) {
				ItemAutorizFornVO vo = new ItemAutorizFornVO();
				vo.setFreq(this.af.getPropostaFornecedor().getLicitacao()
						.getFrequenciaEntrega());
				if (fas.getSolicitacaoDeCompra() != null) {
					vo.setMaterial(fas.getSolicitacaoDeCompra().getMaterial());
				}
				if (fas.getSolicitacaoServico() != null) {
					vo.setServico(fas.getSolicitacaoServico().getServico());
				}
				vo.setNumItem(iprop.getId().getNumero());
				vo.setQuant(iprop.getItemPropostaFornecedor().getQuantidade()
						.intValue());
				vo.setUnidade(iprop.getItemPropostaFornecedor()
						.getUnidadeMedida());
				vo.setValorUnit(iprop.getItemPropostaFornecedor()
						.getValorUnitario());
				res.add(vo);
			}
		}
		this.itensAF = res;
		return this.itensAF;
	}

	public void setItensAF(List<ItemAutorizFornVO> itensAF) {
		this.itensAF = itensAF;
	}

	public enum Fields {
		AF("af"), 
		PK_AF("pkAf"), 
		AF_NUMERO("numeroAf"), 
		NRO_COMPLEMENTO("nroComplemento"), 
		RAZAO_SOCIAL("razaoSocial"), 
		NRO_FORNECEDOR("nroFornecedor"), 
		VALOR_TOTAL("valorTotalItem"), 
		CONVENIO_FINANCEIRO("convenioFinanceiro"), 
		NRO_CONVENIO_FINANCEIRO("nroConvFinanceiro"), 
		TEMSIASG("bTemSiasg"), 
		TOOLTIPSEMSIASG("toolTipSemSiasg"), 
		TOOLTIPCOMSIASG("toolTipComSiasg"), 
		QUANTIDADE_ITEM("qtdadeItemPropostaForn"), 
		VALOR_UNIT_ITEM("valorUnitItemPropostaForn"), 
		FREQ_ENTREGA_LIC("freqEntregaLicitacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((af == null) ? 0 : af.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		AfsContratosFuturosVO other = (AfsContratosFuturosVO) obj;
		if (af == null) {
			if (other.af != null) {
				return false;
			}
		} else if (!af.equals(other.af)) {
			return false;
		}
		return true;
	}

}