package br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controlepaciente.dao.EcpGrupoControleDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoGrupoControle;
import br.gov.mec.aghu.model.EcpGrupoControle;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Conjunto de regras de negócio para estória
 * "Cadastro de Grupo de Controle - #6462"
 * 
 * @author ptneto
 * 
 */

@Stateless
public class ManterGrupoControleON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterGrupoControleON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private EcpGrupoControleDAO ecpGrupoControleDAO;
	
	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -245287256598077620L;

	/**
	 * Enumeração para mensagens de sistema.
	 * 
	 * @author ptneto
	 * 
	 */
	public enum ManterGrupoControleONExceptionCode implements
			BusinessExceptionCode {
		ERRO_EXCLUIR_GRUPO_CONTROLE, ERRO_PERSISTIR_GRUPO_CONTROLE, 
		MENSAGEM_DESCRICAO_GRUPO_CONTROLE_JA_EXISTENTE, MENSAGEM_ORDEM_GRUPO_CONTROLE_JA_EXISTENTE;
	}

	public List<EcpGrupoControle> pesquisarGruposControle(Integer firstResult,Integer maxResult, String orderProperty, boolean asc, Integer seq, String descricao, Short ordem, DominioSituacao situacao, DominioTipoGrupoControle tipo) {

		return this.getEcpGrupoControleDAO().pesquisarGruposControle(firstResult, maxResult, orderProperty, asc, seq, descricao,ordem, situacao,  tipo);
	}

	/**
	 * Insere um novo registro de Grupo de Controle.
	 */
	public void inserir(EcpGrupoControle _grupoControle) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (_grupoControle == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		validaGrupoControle(_grupoControle, true);

		_grupoControle.setCriadoEm(new Date());
		_grupoControle.setServidor(servidorLogado);


		try {
			ecpGrupoControleDAO.persistir(_grupoControle);
		} catch (PersistenceException e) {
			throw new ApplicationBusinessException( ManterGrupoControleONExceptionCode.ERRO_PERSISTIR_GRUPO_CONTROLE,e.getCause().getMessage());
		}
	}

	public void atualizar(EcpGrupoControle _grupoControle) throws ApplicationBusinessException {
		if (_grupoControle == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		validaGrupoControle(_grupoControle, false);

		try {
			ecpGrupoControleDAO.merge(_grupoControle);
		} catch (PersistenceException e) {
			throw new ApplicationBusinessException(ManterGrupoControleONExceptionCode.ERRO_PERSISTIR_GRUPO_CONTROLE,e.getCause().getMessage());
		}
	}

	private void validaGrupoControle(EcpGrupoControle _grupoControle,boolean novoGrupo) throws ApplicationBusinessException {

		// Valida se existe um grupo com a mesma descrição
		EcpGrupoControle grupoAux = ecpGrupoControleDAO.obterDescricao(_grupoControle.getDescricao());
		
		if ((grupoAux != null && novoGrupo) || (!novoGrupo && grupoAux != null && !grupoAux.getSeq().equals(_grupoControle.getSeq()))) {
			throw new ApplicationBusinessException(ManterGrupoControleONExceptionCode.MENSAGEM_DESCRICAO_GRUPO_CONTROLE_JA_EXISTENTE);
		}

		// Valida se existe um grupo com a mesma ordem
		grupoAux = ecpGrupoControleDAO.obterOrdem(_grupoControle.getOrdem());
		
		if ((grupoAux != null && novoGrupo) || (!novoGrupo && grupoAux != null && !grupoAux.getSeq().equals(_grupoControle.getSeq()))) {
			throw new ApplicationBusinessException(ManterGrupoControleONExceptionCode.MENSAGEM_ORDEM_GRUPO_CONTROLE_JA_EXISTENTE);
		}
	}
	
	/**
	 * Exclui um registro de Grupo de Controle.
	 * 
	 * @param _grupoControle Código do grupo a ser incluído
	 * @throws ApplicationBusinessException
	 */
	public void excluir(Integer seq) throws ApplicationBusinessException {

		if (seq == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
		
		EcpGrupoControle _grupoControle = ecpGrupoControleDAO.obterPorChavePrimaria(seq);

		List<EcpItemControle> listaItens = verificaReferenciaItemControle(_grupoControle);

		if (listaItens.size() > 0) {
			throw new ApplicationBusinessException(ManterGrupoControleONExceptionCode.ERRO_EXCLUIR_GRUPO_CONTROLE);
		}

		// exclui o grupo
		ecpGrupoControleDAO.remover(_grupoControle);
	}

	/**
	 * Verifica se um grupo de controle possui itens que o referenciam.
	 * 
	 * @param _grupoControle  Grupo a ser pesquisado.
	 * @return lista de itens do grupo de controle.
	 */
	public List<EcpItemControle> verificaReferenciaItemControle(EcpGrupoControle _grupoControle) {

		if (_grupoControle == null) {
			return null;
		}

		List<EcpItemControle> listaItens = ecpGrupoControleDAO.listarItensGrupoControle(_grupoControle);

		return listaItens;
	}

	protected EcpGrupoControleDAO getEcpGrupoControleDAO() {
		return ecpGrupoControleDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}