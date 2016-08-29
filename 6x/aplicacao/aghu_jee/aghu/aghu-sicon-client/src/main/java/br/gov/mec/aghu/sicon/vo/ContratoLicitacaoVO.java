package br.gov.mec.aghu.sicon.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;

public class ContratoLicitacaoVO implements Serializable {

	private static final long serialVersionUID = 2361488277261078834L;
	private ScoAutorizacaoForn af;
	private DominioSimNao vincularAoContrato;
	private BigDecimal valorProposta;
	private List<ItemAutorizacaoFornVO> itensAF;

	public ContratoLicitacaoVO(ScoAutorizacaoForn af,
			DominioSimNao vincularAoContrato) {
		super();
		this.af = af;
		this.vincularAoContrato = vincularAoContrato;
	}

	public DominioSimNao getVincularAoContrato() {
		return vincularAoContrato;
	}

	public void setVincularAoContrato(DominioSimNao vincularAoContrato) {
		this.vincularAoContrato = vincularAoContrato;
	}

	public ScoAutorizacaoForn getAf() {
		return af;
	}

	public void setAf(ScoAutorizacaoForn af) {
		this.af = af;
	}

	public BigDecimal getValorProposta() {
		List<ScoItemAutorizacaoForn> itensProp = this.af
				.getItensAutorizacaoForn();
		valorProposta = BigDecimal.ZERO;
		BigDecimal valorAuxiliar = BigDecimal.ZERO;

		for (ScoItemAutorizacaoForn iprop : itensProp) {
			if (af.getPropostaFornecedor().getLicitacao()
					.getFrequenciaEntrega() != null) {

				valorAuxiliar = BigDecimal
						.valueOf(
								iprop.getItemPropostaFornecedor()
										.getQuantidade())
						.multiply(
								BigDecimal
										.valueOf(
												af.getPropostaFornecedor()
														.getLicitacao()
														.getFrequenciaEntrega())
										.multiply(
												iprop.getItemPropostaFornecedor()
														.getValorUnitario()
														.setScale(2,BigDecimal.ROUND_HALF_EVEN)));

				valorProposta = valorProposta.add(valorAuxiliar);

			} else {
				valorProposta = valorProposta.add(BigDecimal.valueOf(
						iprop.getItemPropostaFornecedor().getQuantidade())
						.multiply(
								iprop.getItemPropostaFornecedor()
										.getValorUnitario()));
			}
		}
		return valorProposta;
	}

	public void setValorProposta(BigDecimal valorProposta) {
		this.valorProposta = valorProposta;
	}

	public List<ItemAutorizacaoFornVO> getItensAF() {
		List<ItemAutorizacaoFornVO> res = new ArrayList<ItemAutorizacaoFornVO>();
		for (ScoItemAutorizacaoForn iprop : this.af.getItensAutorizacaoForn()) {
			for (ScoFaseSolicitacao fas : iprop.getScoFaseSolicitacao()) {
				ItemAutorizacaoFornVO vo = new ItemAutorizacaoFornVO();
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

	public void setItensAF(List<ItemAutorizacaoFornVO> itensAF) {
		this.itensAF = itensAF;
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
		ContratoLicitacaoVO other = (ContratoLicitacaoVO) obj;
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
