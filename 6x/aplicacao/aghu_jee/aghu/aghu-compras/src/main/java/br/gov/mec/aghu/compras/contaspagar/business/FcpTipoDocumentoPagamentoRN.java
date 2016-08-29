package br.gov.mec.aghu.compras.contaspagar.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.compras.dao.FcpTipoDocumentoPagamentoDAO;
import br.gov.mec.aghu.model.FcpTipoDocumentoPagamento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável pelos métodos e regras de negócio envolvendo a entidade
 * {@link FcpTipoDocumentoPagamento}.
 *
 */
@Stateless
public class FcpTipoDocumentoPagamentoRN extends BaseBusiness {

	/** Identificador Único */
	private static final long serialVersionUID = -9153094651873525069L;

	@Override
	protected Log getLogger() {
		return null;
	}

	public enum FcpTipoDocumentoPagamentoExceptionCode implements
			BusinessExceptionCode {
		TIPO_DOC_PGTO_DESCRICAO_INVALIDA;
	}

	@Inject
	private FcpTipoDocumentoPagamentoDAO tipoDocumentoPagamentoDAO;

	/**
	 * Método de pesquisa dos {@link FcpTipoDocumentoPagamento}.
	 * 
	 * @param firstResult
	 *            Indice inicial da pesquisa.
	 * @param maxResult
	 *            Indice final da pesquisa.
	 * @param orderProperty
	 *            Coluna de ordenação da pesquisa.
	 * @param asc
	 *            Indica se os resultados viram em ordem Ascendente(true) ou
	 *            descendente(false).
	 * @param fcpTipoDocumentoPagamento
	 *            Objeto com a ser populado.
	 * @return {@link List} de {@link FcpTipoDocumentoPagamento}.
	 */
	public List<FcpTipoDocumentoPagamento> pesquisarTiposDocumentoPagamento(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) {
		return this.getTipoDocumentoPagamentoDAO()
				.pesquisarTipoDocumentoPagamento(firstResult, maxResult,
						orderProperty, asc, fcpTipoDocumentoPagamento);
	}

	/**
	 * Consulta Tipo Documento Pagamento NR count
	 * 
	 * @return	{@link Long} que representa o número de resultados para o paginador.
	 */
	public Long pesquisarCountTipoDocumentoPagamento(
			FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) {
		return this
				.getTipoDocumentoPagamentoDAO()
				.pesquisarCountTipoDocumentoPagamento(fcpTipoDocumentoPagamento);
	}
	
	/**
	 * Método de atualização do {@link FcpTipoDocumentoPagamento}, observando a
	 * regra que dita que não pode haver dois {@link FcpTipoDocumentoPagamento}
	 * com a mesma descrição ativo.
	 * @param fcpTipoDocumentoPagamento	Objeto a ser atualizado.
	 * @throws ApplicationBusinessException
	 */
	public void atualizarTipoDocumentoPagamento(FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) throws ApplicationBusinessException {
		
		FcpTipoDocumentoPagamento fcpTipoDocumentoPagamentoVerificar = this.getTipoDocumentoPagamentoDAO().verificarTipoDocumentoPagamento(fcpTipoDocumentoPagamento);
		
		if (fcpTipoDocumentoPagamentoVerificar != null) {
			
			throw new ApplicationBusinessException(FcpTipoDocumentoPagamentoExceptionCode.TIPO_DOC_PGTO_DESCRICAO_INVALIDA, Severity.ERROR, fcpTipoDocumentoPagamentoVerificar.getSeq());
		
		} else {
			
			this.getTipoDocumentoPagamentoDAO().atualizarFcpTipDocumentoPagamento(fcpTipoDocumentoPagamento);
		}
	}

	/**
	 * Método de atualização do {@link FcpTipoDocumentoPagamento}, observando a
	 * regra que dita que não pode haver dois {@link FcpTipoDocumentoPagamento}
	 * com a mesma descrição ativo.
	 * @param fcpTipoDocumentoPagamento	Objeto a ser inserido.
	 * @throws ApplicationBusinessException
	 */
	public void inserirTipoDocumentoPagamento( FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) throws ApplicationBusinessException {
		
		FcpTipoDocumentoPagamento fcpTipoDocumentoPagamentoVerificar = this.getTipoDocumentoPagamentoDAO().verificarTipoDocumentoPagamento(fcpTipoDocumentoPagamento);
		
		if (fcpTipoDocumentoPagamentoVerificar != null) {
			
			throw new ApplicationBusinessException(FcpTipoDocumentoPagamentoExceptionCode.TIPO_DOC_PGTO_DESCRICAO_INVALIDA, Severity.ERROR, fcpTipoDocumentoPagamentoVerificar.getSeq());
		
		} else {
			
			this.getTipoDocumentoPagamentoDAO().inserirFcpTipoDocumentoPagamento(fcpTipoDocumentoPagamento);
		}
	}

	public FcpTipoDocumentoPagamentoDAO getTipoDocumentoPagamentoDAO() {
		return tipoDocumentoPagamentoDAO;
	}

	public void setTipoDocumentoPagamentoDAO(
			FcpTipoDocumentoPagamentoDAO tipoDocumentoPagamentoDAO) {
		this.tipoDocumentoPagamentoDAO = tipoDocumentoPagamentoDAO;
	}

}
