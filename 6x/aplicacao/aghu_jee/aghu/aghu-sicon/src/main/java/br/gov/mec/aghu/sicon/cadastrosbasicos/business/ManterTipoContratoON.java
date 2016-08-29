package br.gov.mec.aghu.sicon.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoTipoContratoSicon;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.dao.ScoTipoContratoSiconDAO;
import br.gov.mec.aghu.sicon.util.SiconUtil;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterTipoContratoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterTipoContratoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private ScoTipoContratoSiconDAO scoTipoContratoSiconDAO;
	
	@EJB
	private ISiconFacade siconFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -440286732468557537L;

	public enum ManterTipoContratoONExceptionCode implements
			BusinessExceptionCode {
		    MENSAGEM_PARAM_OBRIG, 
			MENSAGEM_CODIGO_DUPLICADO,
			MENSAGEM_DESCRICAO_DUPLICADA,
			MENSAGEM_EXCEDE_TAMANHO_CODIGO,
			MENSAGEM_ERRO_HIBERNATE_VALIDATION,
			MENSAGEM_ERRO_PERSISTIR_DADOS,
			MENSAGEM_VINCULO_CONTRATO,
	}

	/**
	 * Pesquisa por registro de tipos de contrato.
	 * 
	 * @param _firstResult
	 *            Índice da primeira posição na pesquisa.
	 * @param _maxResult
	 *            Número máximo de resultados esperados.
	 * @param _orderProperty
	 *            Campo de ordenação.
	 * @param _asc
	 *            {@code true} para acionar a ordenação ascendente e
	 *            {@code false} para caso contrário.
	 * @param _seq
	 *            Filtro para sequence da tabela.
	 * @param _descricao
	 *            Filtro para descrição.
	 * @param _situacao
	 *            Filtro para situação.
	 * @return Listagem de tipos de contrato encontrados.
	 */
	public List<ScoTipoContratoSicon> pesquisarTiposContrato(
			Integer _firstResult, Integer _maxResult, String _orderProperty,
			boolean _asc, Integer _seq, String _descricao,
			DominioSituacao _situacao) {

		return this.getScoTipoContratoSiconDAO().pesquisarTiposContrato(
				_firstResult, _maxResult, _orderProperty, _asc, _seq,
				_descricao, _situacao);
	}

	/**
	 * Insere um novo registro de tipo de contrato.
	 * 
	 * @param _tipoContrato
	 *            Tipo de Contrato a ser inserido.
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void inserir(ScoTipoContratoSicon _tipoContrato)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (_tipoContrato == null) {
			throw new ApplicationBusinessException(
					ManterTipoContratoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		// RN03 - garante código único para tipo de contrato.
		if (verificaCodigoUnico(_tipoContrato.getCodigoSicon()) == false) {
			throw new ApplicationBusinessException(
					ManterTipoContratoONExceptionCode.MENSAGEM_CODIGO_DUPLICADO);
		}

		// RN04 - garante que a descrição do tipo de contrato seja única.
		if (verificaDescricaoUnica(_tipoContrato.getDescricao()) == false) {
			throw new ApplicationBusinessException(
					ManterTipoContratoONExceptionCode.MENSAGEM_DESCRICAO_DUPLICADA);
		}

		if (verificaTamanhoCodigo(_tipoContrato.getCodigoSicon()) == false){
			throw new ApplicationBusinessException(
					ManterTipoContratoONExceptionCode.MENSAGEM_EXCEDE_TAMANHO_CODIGO);
		}
		
		_tipoContrato.setDescricao(SiconUtil.retiraEspacosConsecutivos(_tipoContrato.getDescricao()));		
		
		// RN05 - auditoria
		_tipoContrato.setCriadoEm(new Date());
		_tipoContrato.setServidor(servidorLogado);

		try {
//			this.getScoTipoContratoSiconDAO().inserir(_tipoContrato, true);
			this.getScoTipoContratoSiconDAO().persistir(_tipoContrato);
			this.getScoTipoContratoSiconDAO().flush();
			
		} catch (ConstraintViolationException ise) {
			String mensagem = " Valor inválido para o campo ";
			throw new ApplicationBusinessException(
					ManterTipoContratoONExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION,
					mensagem);	

		} catch (Exception e) {
			throw new ApplicationBusinessException(
					ManterTipoContratoONExceptionCode.MENSAGEM_CODIGO_DUPLICADO,
					e.getMessage());
		}

	}

	/**
	 * Atualiza um registro de tipo de contrato.
	 * 
	 * @param _tipoContrato
	 *            Tipo de Contrato a ser atualizado.
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void alterar(ScoTipoContratoSicon _tipoContrato)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoTipoContratoSiconDAO scoTipoContratoSiconDAO = this.getScoTipoContratoSiconDAO();
		
		if (_tipoContrato == null) {
			throw new ApplicationBusinessException(
					ManterTipoContratoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		// RN03 - garante código único para tipo de contrato.
		
		ScoTipoContratoSicon orig  = scoTipoContratoSiconDAO.obterPorCodigoSicon(_tipoContrato.getCodigoSicon());
		if(orig!=null && !orig.getSeq().equals(_tipoContrato.getSeq())){
			if (verificaCodigoUnico(_tipoContrato.getCodigoSicon()) == false) {
				throw new ApplicationBusinessException(
						ManterTipoContratoONExceptionCode.MENSAGEM_CODIGO_DUPLICADO);
			}
		}

		List<ScoTipoContratoSicon> listaTipoContrato = scoTipoContratoSiconDAO.listarTiposContrato(null,_tipoContrato.getDescricao().trim(), null);		
		if(listaTipoContrato.size()>0){
			orig = listaTipoContrato.get(0);
			if(!orig.getSeq().equals(_tipoContrato.getSeq())) {
				throw new ApplicationBusinessException(ManterTipoContratoONExceptionCode.MENSAGEM_DESCRICAO_DUPLICADA);
			}
		}	
		
		if (verificaTamanhoCodigo(_tipoContrato.getCodigoSicon()) == false){
			throw new ApplicationBusinessException(
					ManterTipoContratoONExceptionCode.MENSAGEM_EXCEDE_TAMANHO_CODIGO);
		}

		_tipoContrato.setDescricao(SiconUtil.retiraEspacosConsecutivos(_tipoContrato.getDescricao()));
		_tipoContrato.setAlteradoEm(new Date());
		_tipoContrato.setServidor(servidorLogado);

		try {
			scoTipoContratoSiconDAO.merge(_tipoContrato);
		} catch (ConstraintViolationException ise) {
			String mensagem = " Valor inválido para o campo ";
			throw new ApplicationBusinessException(
					ManterTipoContratoONExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION,
					mensagem);			

		} 
	}

	/**
	 * Remove um registro de tipo de contrato.
	 * 
	 * @param _tipoContrato
	 *            Tipo de Contrato a ser removido.
	 * @throws ApplicationBusinessException
	 */
	public void remover(Integer seq) throws ApplicationBusinessException {

		ScoTipoContratoSicon _tipoContrato = scoTipoContratoSiconDAO.obterPorChavePrimaria(seq);

		if (_tipoContrato == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}

		if (verificarAssociacaoContrato(_tipoContrato)) {
			throw new ApplicationBusinessException(ManterTipoContratoONExceptionCode.MENSAGEM_VINCULO_CONTRATO);
		}
		scoTipoContratoSiconDAO.removerPorId(seq);
	}

	// regras de negócio

	/**
	 * Verifica se o código do tipo de contrato é um valor único na tabela.
	 * Valida apenas para tipos de contrato com situação {@code Ativo}.
	 * 
	 * @param _codigoSicon
	 *            Código a ser analisado.
	 * @return {@code true} para valor único e {@code false} para o caso
	 *         contrário.
	 */
	public boolean verificaCodigoUnico(Integer _codigoSicon) {

		List<ScoTipoContratoSicon> listaContrato = this
				.getScoTipoContratoSiconDAO().listarTiposContrato(_codigoSicon,
						null, null);

		if (listaContrato.size() > 0) {
			return false;
		}

		return true;
	}

	/**
	 * Verifica se a descrição do tipo de contrato é única dentro da tabela.
	 * Valida apenas para tipos de contrato com situação {@code Ativo}.
	 * 
	 * @param _descricao
	 *            Descrição a ser analisada.
	 * @return {@code true} para descrição única e {@code false} para caso
	 *         contrário.
	 */
	public boolean verificaDescricaoUnica(String _descricao) {

		List<ScoTipoContratoSicon> listaTipoContrato = this
				.getScoTipoContratoSiconDAO().listarTiposContrato(null,
						_descricao, DominioSituacao.A);

		if (listaTipoContrato.size() > 0) {
			return false;
		}

		return true;
	}

	/**
	 * Verifica se um tipo de contrato está associado a um contrato ou aditivo
	 * de contrato.
	 * 
	 * @param _tipoContrato
	 *            Tipo de Contrato a ser analisado.
	 * @return {@code true} para associação existente ou {@code false} para caso
	 *         contrário.
	 */
	public boolean verificarAssociacaoContrato(
			ScoTipoContratoSicon _tipoContrato) {

		List<ScoContrato> listaContrato = this.getSiconFacade()
				.obterListaContratoAssociado(_tipoContrato);
		List<ScoAditContrato> listaAditivoContrato = this
				.getSiconFacade().obterListaAditContratoAssociado(
						_tipoContrato);

		if ((listaContrato.size() > 0) || (listaAditivoContrato.size() > 0)) {
			return true;
		}

		return false;
	}

	/**
	 * Verifica se excede o número de dígitos, 7, do código Sicon
	 * 
	 * @param _codigoSicon
	 *             
	 * @return {@code true} código que não excede o numero máximo de dígitos ou {@code false} para caso
	 *         contrário.
	 */
	
	public boolean verificaTamanhoCodigo(Integer _codigoSicon) {

		if (_codigoSicon.toString().length() > 7){
			return false;			
		}
		
		
		return true;
	}
	
	
	
	/**
	 * instancia objeto da classe {@code ScoTipoContratoSiconDAO}.
	 * 
	 * @return Nova instância para classe {@code ScoTipoContratoSiconDAO}.
	 */
	protected ScoTipoContratoSiconDAO getScoTipoContratoSiconDAO() {
		return scoTipoContratoSiconDAO;
	}

	// Facades
	protected ISiconFacade getSiconFacade(){
		return this.siconFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
