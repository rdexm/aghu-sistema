package br.gov.mec.aghu.sicon.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoCatalogoSicon;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialSicon;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ManterTipoContratoON.ManterTipoContratoONExceptionCode;
import br.gov.mec.aghu.sicon.dao.ScoCatalogoSiconDAO;
import br.gov.mec.aghu.sicon.dao.ScoMaterialSiconDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterMaterialSiconON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterMaterialSiconON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private ScoCatalogoSiconDAO scoCatalogoSiconDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private ScoMaterialSiconDAO scoMaterialSiconDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1066215596800961383L;

	public enum ManterMaterialSiconONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_PARAM_OBRIG, MENSAGEM_ERRO_HIBERNATE_VALIDATION, MENSAGEM_ERRO_PERSISTIR_DADOS, MENSAGEM_RELACIONAMENTO_DUPLICADO, 
		MENSAGEM_NRO_MINIMO_CARACTERES, MENSAGEM_NRO_MINIMO_CARACTERES_COD_SICON;
	}

	/**
	 * Altera um registro de {@code ScoMaterialSicon}.
	 * 
	 * @param _materialSicon
	 *            Registro a ser alterado.
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void alterar(ScoMaterialSicon _materialSicon)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (_materialSicon == null) {
			throw new ApplicationBusinessException(
					ManterMaterialSiconONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		// if (this.verificarRegistroIgual(_materialSicon)) {
		// throw new ApplicationBusinessException(
		// ManterMaterialSiconONExceptionCode.MENSAGEM_RELACIONAMENTO_DUPLICADO);
		// }

		if (_materialSicon.getSituacao() == DominioSituacao.A) {
			// RN02 - garante que o material do relacionamento não possui nenhum
			// outro relacionamento ativo.
			if (validaMaterial(_materialSicon.getMaterial()) == false) {
				throw new ApplicationBusinessException(
						ManterMaterialSiconONExceptionCode.MENSAGEM_RELACIONAMENTO_DUPLICADO);
			}
		}

		_materialSicon.setAlteradoEm(new Date());
		_materialSicon.setServidor(servidorLogado);
		
		try {
			ScoMaterialSiconDAO scoMaterialSiconDAO = this.getScoMaterialSiconDAO();
			scoMaterialSiconDAO.merge(_materialSicon);
		} catch (ConstraintViolationException ise) {
			String mensagem = " Valor inválido para o campo ";
			throw new ApplicationBusinessException(
					ManterMaterialSiconONExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION,
					mensagem);

		}
	}

	/**
	 * Insere novo registro em {@code ScoMaterialSicon}.
	 * 
	 * @param _materialSicon
	 *            Registro a ser inserido.
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void inserir(ScoMaterialSicon _materialSicon)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (_materialSicon == null) {
			throw new ApplicationBusinessException(
					ManterMaterialSiconONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		// RN02 - garante que o material do relacionamento não possui nenhum
		// outro relacionamento ativo.
		if (validaMaterial(_materialSicon.getMaterial()) == false) {
			throw new ApplicationBusinessException(
					ManterMaterialSiconONExceptionCode.MENSAGEM_RELACIONAMENTO_DUPLICADO);
		}

		// RN03 - auditoria
		_materialSicon.setCriadoEm(new Date());
		_materialSicon.setServidor(servidorLogado);

		try {
			ScoMaterialSiconDAO scoMaterialSiconDAO = this.getScoMaterialSiconDAO();
			scoMaterialSiconDAO.persistir(_materialSicon);
			scoMaterialSiconDAO.flush();
		} catch (ConstraintViolationException ise) {
			String mensagem = " Valor inválido para o campo ";
			throw new ApplicationBusinessException(
					ManterTipoContratoONExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION,
					mensagem);

		} catch (Exception e) {
			throw new ApplicationBusinessException(
					ManterTipoContratoONExceptionCode.MENSAGEM_ERRO_PERSISTIR_DADOS,
					e.getMessage());
		}

	}

	// regras de negócio.

	/**
	 * Verifica o número mínimo de caracteres na pesquisa do nome do material.
	 * 
	 */
	public List<ScoMaterial> pesquisarMateriaisPorFiltro(Object _input)
			throws ApplicationBusinessException {

		String nome = (String) _input;
		Integer codigo = null;

		if(CoreUtil.isNumeroInteger(nome)){
			codigo = Integer.valueOf(nome);
			nome = null;
		}

		if (codigo == null) {
			if (nome.length() < 3) {
				throw new ApplicationBusinessException(
						ManterMaterialSiconONExceptionCode.MENSAGEM_NRO_MINIMO_CARACTERES);
			}
		}

		return this.getComprasFacade().pesquisarMaterialPorFiltro(_input);
	}


	/**
	 * Verifica o número mínimo de caracteres na pesquisa do código SICON.
	 * 
	 */
	public List<ScoCatalogoSicon> listarCodigoSiconServicoAtivo(Object pesquisa)
			throws ApplicationBusinessException {

		String nome = (String) pesquisa;
		Integer codigo = null;

		if(CoreUtil.isNumeroInteger(nome)){
			codigo = Integer.valueOf(nome);
			nome = null;
		}

		if ((codigo == null) && (nome.length() < 3)) {
				throw new ApplicationBusinessException(
						ManterMaterialSiconONExceptionCode.MENSAGEM_NRO_MINIMO_CARACTERES_COD_SICON);
		}
		return scoCatalogoSiconDAO.listarCatalogoSiconMaterialAtivo(pesquisa);
	}
	
	public Long listarCodigoSiconServicoAtivoCount(Object pesquisa)
			throws ApplicationBusinessException {

		String nome = (String) pesquisa;
		Integer codigo = null;

		if(CoreUtil.isNumeroInteger(nome)){
			codigo = Integer.valueOf(nome);
			nome = null;
		}

		if ((codigo == null) && (nome.length() < 3)) {
				throw new ApplicationBusinessException(
						ManterMaterialSiconONExceptionCode.MENSAGEM_NRO_MINIMO_CARACTERES_COD_SICON);
		}
		return scoCatalogoSiconDAO.listarCatalogoSiconMaterialAtivoCount(pesquisa);
	}

	/**
	 * RN01 - verifica se o código sicon é único dentro de
	 * {@code ScoMaterialSicon}.
	 * 
	 * @param _codigoSicon
	 *            Código a ser analisado.
	 * @return {@code true} para código único e {@code false} para caso
	 *         contrário.
	 */
	public boolean verificaCodigoUnico(Integer _codigoSicon) {

		List<ScoMaterialSicon> listaContrato = this.getScoMaterialSiconDAO()
				.listarMateriaisSicon(_codigoSicon, null, DominioSituacao.A,
						null);

		if (listaContrato.size() > 0) {
			return false;
		}

		return true;
	}

	/**
	 * RN02- Verifica se o material do relacionamento é válido ou não. Para ser
	 * válido, o material não pode estar presente em outro relacionamento SICON.
	 * 
	 * @param _material
	 *            Material a ser analisado.
	 * @return {@code true} para material válido ou {@code false} para caso
	 *         contrário.
	 */
	@SuppressWarnings("ucd")
	public boolean validaMaterial(ScoMaterial _material) {

		List<ScoMaterialSicon> listaMaterialSicon = this
				.getScoMaterialSiconDAO().listarMateriaisSicon(null, _material,
						DominioSituacao.A, null);

		if (listaMaterialSicon.size() > 0) {
			return false;
		}

		return true;
	}


	// instâncias

	protected ScoCatalogoSiconDAO getScoCatalogoSiconDAO(){
		return scoCatalogoSiconDAO;
	}
	
	protected ScoMaterialSiconDAO getScoMaterialSiconDAO() {
		return scoMaterialSiconDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IComprasFacade getComprasFacade(){
		return comprasFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
