package br.gov.mec.aghu.compras.pac.business;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.ScoCondicaoPgtoLicitacao;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoCondicaoPgtoLicitacaoValidacaoON extends BaseBusiness {

@EJB
private DadosItemLicitacaoON dadosItemLicitacaoON;

private static final Log LOG = LogFactory.getLog(ScoCondicaoPgtoLicitacaoValidacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1878569038050846259L;

	public enum CondicaoPagamentoEParcelasONExceptionCode implements
			BusinessExceptionCode {
		INFORMADO_PERCENTUAL_E_VALOR, 
		VALOR_PERCENTUAL_PARCELAS, 
		VALOR_TOTAL_LICITACAO, 
		VALOR_PERCENTUAL_TODAS_PARCELAS, 
		VALOR_TODAS_PARCELAS, 
		PRAZO_INFERIOR_AO_PRAZO_ANTERIOR, 
		DESCONTO_INVALIDO,
		ACRESCIMO_INVALIDO;
	}

	public void regrasNegocioParcelasDaCondicaoPagamento(
			List<ScoParcelasPagamento> listaParcelas,
			ScoCondicaoPgtoLicitacao condicaoPagamento)
			throws ApplicationBusinessException {

		percentualEValor(listaParcelas);

		somatorioPercentualParcelas(listaParcelas);

		somatorioValorParcelas(listaParcelas, condicaoPagamento);

		percentualInformadoTodasParcelas(listaParcelas);

		valorInformadoTodasParcelas(listaParcelas);

		validaSequenciaPrazo(listaParcelas);
	}

	public void validaCondicaoPgto(ScoCondicaoPgtoLicitacao condicaoPagamento)
			throws ApplicationBusinessException {
		// Validação do percentual do Desconto
		if (condicaoPagamento.getPercDesconto() != null && condicaoPagamento.getPercDesconto().doubleValue() > 100) {
			throw new ApplicationBusinessException(
					CondicaoPagamentoEParcelasONExceptionCode.DESCONTO_INVALIDO);
		}

		// Validação do valor de Acréscimo
		if (condicaoPagamento.getPercAcrescimo() != null && condicaoPagamento.getPercAcrescimo().doubleValue() < 0) {
			throw new ApplicationBusinessException(
					CondicaoPagamentoEParcelasONExceptionCode.ACRESCIMO_INVALIDO);
		}
	}

	private void validaSequenciaPrazo(List<ScoParcelasPagamento> listaParcelas)
			throws ApplicationBusinessException {
		// Validação da sequencia do prazo das parcelas
		Short prazoAnterior = 0;
		for (ScoParcelasPagamento parcela : listaParcelas) {
			if (prazoAnterior == 0) {
				prazoAnterior = parcela.getPrazo();
			}
			if (prazoAnterior > parcela.getPrazo()) {
				throw new ApplicationBusinessException(
						CondicaoPagamentoEParcelasONExceptionCode.PRAZO_INFERIOR_AO_PRAZO_ANTERIOR);
			}
			
			prazoAnterior = parcela.getPrazo();
		}
	}

	private void valorInformadoTodasParcelas(
			List<ScoParcelasPagamento> listaParcelas)
			throws ApplicationBusinessException {
		// Validação de valor informado em todas as parcelas
		boolean existeSemValor = false;
		boolean existeValor = false;
		
		for (ScoParcelasPagamento parcela : listaParcelas) {
			if (parcela.getValorPagamento() == null) {
				existeSemValor = true;
			}

			if (parcela.getValorPagamento() != null
					&& parcela.getValorPagamento().longValue() > 0) {
				existeValor = true;
			}
		}
		
		if (existeValor && existeSemValor) {
			throw new ApplicationBusinessException(
					CondicaoPagamentoEParcelasONExceptionCode.VALOR_TODAS_PARCELAS);
		}
	}

	private void percentualInformadoTodasParcelas(
			List<ScoParcelasPagamento> listaParcelas)
			throws ApplicationBusinessException {
		// Validação de valor percentual informado em todas as parcelas
		boolean existeSemPercentual = false;
		boolean existePercentual = false;
		
		for (ScoParcelasPagamento parcela : listaParcelas) {
			if (parcela.getPercPagamento() == null) {
				existeSemPercentual = true;
			}

			if (parcela.getPercPagamento() != null
					&& parcela.getPercPagamento().longValue() > 0) {
				existePercentual = true;
			}
		}
		
		if (existePercentual && existeSemPercentual) {
			throw new ApplicationBusinessException(
					CondicaoPagamentoEParcelasONExceptionCode.VALOR_PERCENTUAL_TODAS_PARCELAS);
		}
	}

	private void somatorioValorParcelas(
			List<ScoParcelasPagamento> listaParcelas,
			ScoCondicaoPgtoLicitacao condicaoPagamento)
			throws ApplicationBusinessException {
		BigDecimal valorTotal;
		
		if (condicaoPagamento.getLicitacao() != null) {
			valorTotal = getDadosItemLicitacaoON().obterValorTotalPorNumeroLicitacao(condicaoPagamento.getLicitacao().getNumero());
		} else {
			valorTotal = getDadosItemLicitacaoON().obterValorTotalItemPac(condicaoPagamento.getItemLicitacao().getId().getLctNumero(),
							condicaoPagamento.getItemLicitacao().getId().getNumero());
		}

		// Valida soma das parcelas
		BigDecimal somaParcelas = BigDecimal.ZERO;
		boolean existeValor = false;
		for (ScoParcelasPagamento parcela : listaParcelas) {
			if (parcela.getValorPagamento() != null) {
				existeValor = true;
				somaParcelas = somaParcelas.add(parcela.getValorPagamento());
			}
		}
		
		if (existeValor && somaParcelas.setScale(2, BigDecimal.ROUND_DOWN).compareTo(valorTotal) != 0) {
			throw new ApplicationBusinessException(
					CondicaoPagamentoEParcelasONExceptionCode.VALOR_TOTAL_LICITACAO, valorTotal);
		}

	}

	private void somatorioPercentualParcelas(
			List<ScoParcelasPagamento> listaParcelas)
			throws ApplicationBusinessException {
		// Valida soma do percentual de cada parcela
		BigDecimal percPagamento = BigDecimal.ZERO;
		boolean existePercentual = false;
		for (ScoParcelasPagamento parcela : listaParcelas) {
			if (parcela.getPercPagamento() != null) {
				existePercentual = true;
				percPagamento = percPagamento.add(parcela.getPercPagamento());
			}
		}

		if (existePercentual
				&& percPagamento.compareTo(new BigDecimal(100)) != 0) {
			throw new ApplicationBusinessException(
					CondicaoPagamentoEParcelasONExceptionCode.VALOR_PERCENTUAL_PARCELAS);
		}
	}

	private void percentualEValor(List<ScoParcelasPagamento> listaParcelas)
			throws ApplicationBusinessException {
		boolean existePercentual = false;
		boolean existeValor = false;

		for (ScoParcelasPagamento parcela : listaParcelas) {
			if (parcela.getPercPagamento() != null) {
				existePercentual = true;
			}

			if (parcela.getValorPagamento() != null) {
				existeValor = true;
			}
		}

		if (existePercentual && existeValor) {
			throw new ApplicationBusinessException(
					CondicaoPagamentoEParcelasONExceptionCode.INFORMADO_PERCENTUAL_E_VALOR);
		}
	}
	
	protected DadosItemLicitacaoON getDadosItemLicitacaoON(){
		return dadosItemLicitacaoON;
	}

}
