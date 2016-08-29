package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.core.commons.BaseBean;

public class DetalhamentoCustosGeralVO implements BaseBean, CalculoObjetosCentrosCustosInterface {

	private static final long serialVersionUID = 3844840805812033432L;

	private String tipoCusto;
	private String despesa;
	private BigDecimal alocadoAtividade = BigDecimal.ZERO;
	private BigDecimal rateado = BigDecimal.ZERO;
	private BigDecimal naoRateado = BigDecimal.ZERO;
	private BigDecimal total = BigDecimal.ZERO;
	private Double percentual = 0.0D;

	public DetalhamentoCustosGeralVO() {
	}

	public static DetalhamentoCustosGeralVO create(Object[] mvto) {
		DetalhamentoCustosGeralVO vo = new DetalhamentoCustosGeralVO();
		if (mvto[0] != null) {
			vo.setAlocadoAtividade(new BigDecimal(mvto[0].toString()));
		}

		if (mvto[1] != null) {
			vo.setRateado(new BigDecimal(mvto[1].toString()));
		}

		if (mvto[2] != null) {
			vo.setNaoRateado(new BigDecimal(mvto[2].toString()));
		}

		if (mvto[3] != null) {
			vo.setTotal(new BigDecimal(mvto[3].toString()));
		}
		return vo;
	}

	public String getTipoCusto() {
		return tipoCusto;
	}

	public void setTipoCusto(String tipoCusto) {
		this.tipoCusto = tipoCusto;
	}

	public String getDespesa() {
		return despesa;
	}

	public void setDespesa(String despesa) {
		this.despesa = despesa;
	}

	public BigDecimal getAlocadoAtividade() {
		return alocadoAtividade;
	}

	public void setAlocadoAtividade(BigDecimal alocadoAtividade) {
		this.alocadoAtividade = alocadoAtividade != null ? alocadoAtividade.abs() : null;
	}

	public BigDecimal getRateado() {
		return rateado;
	}

	public void setRateado(BigDecimal rateado) {
		this.rateado = rateado != null ? rateado.abs() : null;
	}

	@Override
	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total != null ? total.abs() : null;
	}

	public Double getPercentual() {
		return percentual;
	}

	@Override
	public void setPercentual(Double percentual) {
		this.percentual = percentual;
	}

	public BigDecimal getNaoRateado() {
		return naoRateado;
	}

	public void setNaoRateado(BigDecimal naoRateado) {
		this.naoRateado = naoRateado != null ? naoRateado.abs() : null;
	}
}