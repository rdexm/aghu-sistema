package br.gov.mec.aghu.compras.pac.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAcoesPontoParadaDAO;
import br.gov.mec.aghu.compras.dao.ScoAndamentoProcessoCompraDAO;
import br.gov.mec.aghu.model.ScoAndamentoProcessoCompra;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Serviço de Andamento do PAC
 * 
 * Responsável pelas regras de negócio do andamento do PAC.
 * 
 * @author mlcruz
 */
@Stateless
public class ScoAndamentoProcessoCompraRN extends BaseBusiness {

	private static final long serialVersionUID = 7473022992282148283L;
	
	public enum ScoAndamentoProcessoCompraRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_EXCLUSAO_ANDAMENTO_NAO_PERMITIDA, MENSAGEM_PROCESSO_JA_SE_ENCONTRA_NO_LOCAL_INFORMADO;
	}	

	private static final Log LOG = LogFactory.getLog(ScoAndamentoProcessoCompraRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private ScoAndamentoProcessoCompraDAO scoAndamentoProcessoCompraDAO;
	
	@Inject 
	private ScoAcoesPontoParadaDAO scoAcoesPontoParadaDAO;

	/**
	 * Inclui andamento do PAC.
	 * 
	 * @param andamento
	 *            Item ao PAC
	 * @throws ApplicationBusinessException
	 */
	public void incluir(ScoAndamentoProcessoCompra andamento) throws ApplicationBusinessException {
		ScoAndamentoProcessoCompra andamentoSemDataSaida = scoAndamentoProcessoCompraDAO.obterAndamentoSemDataSaida(
				andamento.getLicitacao().getNumero());

		if (andamentoSemDataSaida != null) {
			// Estória do Usuário #5721 - RN02: Não permitir incluir local
			// Validar se o local onde o processo se encontra já é o mesmo que
			// esta
			// sendo informado.
			ScoLocalizacaoProcesso local = andamentoSemDataSaida.getLocalizacaoProcesso();

			if (local != null && local.equals(andamento.getLocalizacaoProcesso())) {
				throw new ApplicationBusinessException(ScoAndamentoProcessoCompraRNExceptionCode.MENSAGEM_PROCESSO_JA_SE_ENCONTRA_NO_LOCAL_INFORMADO);
			}

			andamentoSemDataSaida.setDtSaida(new Date());
			getScoAndamentoProcessoCompraDAO().persistir(andamentoSemDataSaida);
		}

		getScoAndamentoProcessoCompraDAO().persistir(andamento);
	}

	/**
	 * Remove andamento do PAC.
	 * 
	 * @param andamento
	 *            Item do PAC
	 * @throws ApplicationBusinessException 
	 */
	public void excluir(Integer seq) throws ApplicationBusinessException {
		ScoAndamentoProcessoCompra andamento = scoAndamentoProcessoCompraDAO.obterPorChavePrimaria(seq);
		if (scoAcoesPontoParadaDAO.verificarAcoesPontoParadaPac(andamento.getSeq())) {
			throw new ApplicationBusinessException(ScoAndamentoProcessoCompraRNExceptionCode.MENSAGEM_EXCLUSAO_ANDAMENTO_NAO_PERMITIDA);
		}
				
		getScoAndamentoProcessoCompraDAO().removerPorId(seq);

		ScoAndamentoProcessoCompra ultimo = getScoAndamentoProcessoCompraDAO().obterUltimoAndamento(
				andamento.getLicitacao().getNumero());

		if (ultimo != null) {
			ultimo.setDtSaida(null);
			getScoAndamentoProcessoCompraDAO().atualizar(ultimo);
		}
	}

	protected ScoAndamentoProcessoCompraDAO getScoAndamentoProcessoCompraDAO() {
		return scoAndamentoProcessoCompraDAO;
	}

	/*public enum ExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PROCESSO_JA_SE_ENCONTRA_NO_LOCAL_INFORMADO;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}*/
}
