package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoFormaPagamentoDAO;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoFormaPagamentoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ScoFormaPagamentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	@Inject
	private ScoFormaPagamentoDAO scoFormaPagamentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7130595457653656617L;

	public enum ScoFormaPagamentoONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_PARAM_OBRIG,  MENSAGEM_SIGLA_FORMA_PAGAMENTO_DUPLICADO, MENSAGEM_DESCRICAO_FORMA_PAGAMENTO_DUPLICADO;
	}

	public List<ScoFormaPagamento> pesquisarFormasPagamento(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final ScoFormaPagamento scoFormaPagamento) {

		return this.getScoFormaPagamentoDAO().pesquisarFormasPagamento(
				firstResult, maxResult, orderProperty, asc, scoFormaPagamento);
	}

	public Long pesquisarFormasPagamentoCount(
			final ScoFormaPagamento scoFormaPagamento) {

		return this.getScoFormaPagamentoDAO().pesquisarFormasPagamentoCount(
				scoFormaPagamento);
	}

	public ScoFormaPagamento obterFormaPagamento(Short codigo) {
		return this.getScoFormaPagamentoDAO().obterPorChavePrimaria(codigo);
	}

	public void inserirFormaPagamento(ScoFormaPagamento scoFormaPagamento)
			throws ApplicationBusinessException {

		if (scoFormaPagamento == null) {
			throw new ApplicationBusinessException(
					ScoFormaPagamentoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		this.validaFormaPagamentoSigla(scoFormaPagamento);
		this.validaFormaPagamentoDescricao(scoFormaPagamento);

		this.getScoFormaPagamentoDAO().persistir(scoFormaPagamento);
	}

	public void alterarFormaPagamento(ScoFormaPagamento scoFormaPagamento)
			throws ApplicationBusinessException {

		if (scoFormaPagamento == null) {
			throw new ApplicationBusinessException(
					ScoFormaPagamentoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		this.validaFormaPagamentoSigla(scoFormaPagamento);
		this.validaFormaPagamentoDescricao(scoFormaPagamento);

		this.getScoFormaPagamentoDAO().merge(scoFormaPagamento);
	}

	public void validaFormaPagamentoSigla(ScoFormaPagamento scoFormaPagamento)
			throws ApplicationBusinessException {

		ScoFormaPagamento searchFormaPagamento = this.getScoFormaPagamentoDAO()
				.pesquisarFormaPagamentoPorSiglaEDescricao(
						scoFormaPagamento.getCodigo(),
						scoFormaPagamento.getSigla(), null);

		if (searchFormaPagamento != null) {
			throw new ApplicationBusinessException(
					ScoFormaPagamentoONExceptionCode.MENSAGEM_SIGLA_FORMA_PAGAMENTO_DUPLICADO);
		}
	}

	public void validaFormaPagamentoDescricao(
			ScoFormaPagamento scoFormaPagamento) throws ApplicationBusinessException {

		ScoFormaPagamento searchFormaPagamento = this.getScoFormaPagamentoDAO()
				.pesquisarFormaPagamentoPorSiglaEDescricao(
						scoFormaPagamento.getCodigo(), null,
						scoFormaPagamento.getDescricao());

		if (searchFormaPagamento != null) {
			throw new ApplicationBusinessException(
					ScoFormaPagamentoONExceptionCode.MENSAGEM_DESCRICAO_FORMA_PAGAMENTO_DUPLICADO);
		}
	}

	private ScoFormaPagamentoDAO getScoFormaPagamentoDAO() {
		return scoFormaPagamentoDAO;
	}
}
