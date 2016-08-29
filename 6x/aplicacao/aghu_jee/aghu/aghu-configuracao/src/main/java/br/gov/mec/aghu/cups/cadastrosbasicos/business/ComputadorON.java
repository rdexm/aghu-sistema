package br.gov.mec.aghu.cups.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.ImpComputadorDAO;
import br.gov.mec.aghu.configuracao.dao.ImpComputadorImpressoraDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.model.cups.ImpComputador;
import br.gov.mec.aghu.model.cups.ImpComputadorImpressora;

/**
 * Classe provedora dos métodos de negócio para o cadastro de computadores
 * 
 * @author Lilian
 */
@Stateless
public class ComputadorON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ComputadorON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ImpComputadorImpressoraDAO impComputadorImpressoraDAO;

@Inject
private ImpComputadorDAO impComputadorDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2007465760496131271L;

	/**
	 * Enumeracao com os codigos de mensagens de exceções negociais do cadastro
	 * de computador
	 */
	private enum ComputadorONExceptionCode implements BusinessExceptionCode {
		PARAMETRO_NAO_INFORMADO, REGISTRO_NULO_EXCLUSAO, VIOLACAO_FK_COMPUTADOR, COMPUTADOR_EXISTENTE, COMPUTADOR_INEXISTENTE, COMPUTADOR_NAO_ALTERADO, IP_VALIDATOR, ERRO_REMOVER_COMPUTADOR
	};

	protected ImpComputadorDAO getImpComputadorDAO() {
		return impComputadorDAO;
	}
	
	protected ImpComputadorImpressoraDAO getImpComputadorImpressoraDAO() {
		return impComputadorImpressoraDAO;
	}

	/**
	 * Método definido para retornar uma lista de computadores
	 * 
	 * @param ip
	 *            e/ou nome e/ou descricao
	 * @return List<ImpComputador>
	 */
	public List<ImpComputador> pesquisarComputador(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, String ip, String nome, String descricao) {
		return getImpComputadorDAO().pesquisarComputador(firstResult, maxResult, orderProperty,
				asc, ip, nome, descricao);
	}

	/**
	 * Método definido para retornar a quantidade de computadores lidos
	 * 
	 * @param ip
	 *            e/ou nome e/ou descricao
	 * @return Integer
	 */
	public Long pesquisarComputadorCount(String ip, String nome, String descricao) {

		return getImpComputadorDAO().pesquisarComputadorCount(ip, nome, descricao);
	}

	/**
	 * Método definido para incluir ou alterar um computador
	 * 
	 * @param impComputador
	 */
	public void gravarComputador(ImpComputador impComputador) throws ApplicationBusinessException {

		if (impComputador == null) {
			throw new ApplicationBusinessException(ComputadorONExceptionCode.COMPUTADOR_INEXISTENTE);
		}

		if (impComputador.getSeq() == null) { // Inclusao

			if (getImpComputadorDAO().isComputadorExistente(null, impComputador.getIpComputador(),
					impComputador.getNomeComputador())) {
				throw new ApplicationBusinessException(ComputadorONExceptionCode.COMPUTADOR_EXISTENTE);
			}

			getImpComputadorDAO().persistir(impComputador);
			getImpComputadorDAO().flush();

		} else { // Alteracao

			if (getImpComputadorDAO().isComputadorExistente(impComputador.getSeq(),
					impComputador.getIpComputador(), impComputador.getNomeComputador())) {
				throw new ApplicationBusinessException(ComputadorONExceptionCode.COMPUTADOR_EXISTENTE);
			}

			getImpComputadorDAO().merge(impComputador);
			getImpComputadorDAO().flush();
		}
	}

	/**
	 * Método definido para excluir um computador
	 * 
	 * @param idComputador
	 */
	public void excluirComputador(int idComputador) throws ApplicationBusinessException {
			
		ImpComputador impComputador = null;
		
		try {
			ImpComputadorImpressora impComputadorImpressora = getImpComputadorImpressoraDAO().buscarImpressoraPorIdComputador(idComputador);
			if(impComputadorImpressora != null){
				impComputador = obterComputador(idComputador);
				throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OFG_00005,
						impComputador.getDescricao(), "Impressoras por Computador");
			}
			getImpComputadorDAO().removerPorId(idComputador);
			getImpComputadorDAO().flush();
		}catch (PersistenceException e) {
			throw new ApplicationBusinessException(ComputadorONExceptionCode.ERRO_REMOVER_COMPUTADOR, e.getMessage());
		}
	}

	/**
	 * Método definido para obter um computador
	 * 
	 * @param idComputador
	 * @return impComputador
	 */
	public ImpComputador obterComputador(Integer idComputador) throws ApplicationBusinessException {
		if (idComputador == null) {
			throw new ApplicationBusinessException(ComputadorONExceptionCode.PARAMETRO_NAO_INFORMADO);
		}

		ImpComputador impComputador = getImpComputadorDAO().obterPorChavePrimaria(idComputador);

		if (impComputador == null) {
			throw new ApplicationBusinessException(ComputadorONExceptionCode.COMPUTADOR_INEXISTENTE);
		}

		return impComputador;
	}

	/**
	 * Pesquisar Computadores.
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	public List<ImpComputador> pesquisarComputador(Object paramPesquisa) {
		return getImpComputadorDAO().pesquisarComputador(paramPesquisa);
	}

	/**
	 * Metodo usado para validar IP.
	 * 
	 * @param ip
	 * @throws ApplicationBusinessException
	 */
	public void validarIp(String ip) throws ApplicationBusinessException {
		String[] split = ip.split("\\.");

		if (split.length < 4 || split.length > 4) {
			throw new ApplicationBusinessException(ComputadorONExceptionCode.IP_VALIDATOR);
		}
	}

}
