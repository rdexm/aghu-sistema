package br.gov.mec.aghu.compras.cadastrosapoio.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.compras.dao.FcpAgenciaDAO;
import br.gov.mec.aghu.compras.dao.FcpBancoDAO;
import br.gov.mec.aghu.model.FcpAgenciaBanco;
import br.gov.mec.aghu.model.FcpAgenciaBancoId;
import br.gov.mec.aghu.model.FcpBanco;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class FcpBancoRN extends BaseBusiness {

	private static final long serialVersionUID = -5216359093986655205L;
	
	@Inject
	private FcpBancoDAO fcpBancoDAO;
	
	@Inject
	private FcpAgenciaDAO fcpAgenciaDAO;

	@Override
	protected Log getLogger() {
		return null;
	}
	
	/**
	 * Obter lista banco com paginação.
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param fcpBanco
	 * @return
	 * @throws BaseException
	 */
	public List pesquisarListaBanco(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FcpBanco fcpBanco) throws BaseException {
		return this.getFcpBancoDAO().pesquisar(firstResult, maxResult, orderProperty, asc, fcpBanco);
	}

	/**
	 * Obter count lista de bancos.
	 * @param fcpBanco
	 * @return
	 * @throws BaseException
	 */
	public Long pesquisarCountListaBanco(FcpBanco fcpBanco) throws BaseException {
		return this.getFcpBancoDAO().pesquisar(fcpBanco);
	}
	
	/**
	 * Pesquisar banco.
	 * @param fcpBanco
	 * @return
	 * @throws BaseException
	 */
	public FcpBanco pesquisarBanco(Short codigoBanco) throws BaseException {
		return this.getFcpBancoDAO().pesquisarCodigo(codigoBanco);
	}

	/**
	 * Inserir banco.
	 * @param fcpBanco
	 * @throws BaseException
	 */
	public boolean inserirBanco(FcpBanco fcpBanco) throws BaseException {
		boolean retorno = false;
		// Verificar se banco já existe cadastrado
		FcpBanco fcpBancoPesquisa = new FcpBanco();
		fcpBancoPesquisa.setCodigo(fcpBanco.getCodigo());
		Long countBanco = this.getFcpBancoDAO().pesquisar(fcpBancoPesquisa);
		if(countBanco == null || countBanco == 0) {
			// Incluir, registro não cadastrado
			this.getFcpBancoDAO().incluir(fcpBanco);
			retorno = true;
		}
		return retorno;
	}

	/**
	 * Atualizar banco.
	 * @param fcpBanco
	 * @throws BaseException
	 */
	public void atualizarBanco(FcpBanco fcpBanco) throws BaseException {
		this.getFcpBancoDAO().alterar(fcpBanco);
	}

	/**
	 * Excluir banco.
	 * @param fcpBanco
	 * @throws BaseException
	 */
	public boolean excluirBanco(Short codigoBanco) throws BaseException {
		boolean retorno = false;
		// Verificar se existe agência cadastrada com o banco a ser excluído
		FcpAgenciaBanco fcpAgenciaBanco = new FcpAgenciaBanco();
		FcpAgenciaBancoId fcpAgenciaBancoId = new FcpAgenciaBancoId();	
		fcpAgenciaBancoId.setBcoCodigo(codigoBanco);
		fcpAgenciaBanco.setId(fcpAgenciaBancoId);
		List listaAgencias = this.getFcpAgenciaDAO().pesquisarAgenciaPorBanco(fcpAgenciaBanco);
		if (listaAgencias == null || listaAgencias.size() == 0) {
			// Excluir, registro não associado
			this.getFcpBancoDAO().excluir(codigoBanco);
			retorno = true;
		}
		return retorno;
	}
	
	/**
	 * @return the fcpBancoDAO
	 */
	public FcpBancoDAO getFcpBancoDAO() {
		return fcpBancoDAO;
	}

	/**
	 * @param fcpBancoDAO the fcpBancoDAO to set
	 */
	public void setFcpBancoDAO(FcpBancoDAO fcpBancoDAO) {
		this.fcpBancoDAO = fcpBancoDAO;
	}

	/**
	 * @return the fcpAgenciaDAO
	 */
	public FcpAgenciaDAO getFcpAgenciaDAO() {
		return fcpAgenciaDAO;
	}

	/**
	 * @param fcpAgenciaDAO the fcpAgenciaDAO to set
	 */
	public void setFcpAgenciaDAO(FcpAgenciaDAO fcpAgenciaDAO) {
		this.fcpAgenciaDAO = fcpAgenciaDAO;
	}

}