package br.gov.mec.aghu.compras.cadastrosapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.model.FcpBanco;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class FcpBancoON extends BaseBusiness {

	private static final long serialVersionUID = 2468736352491628082L;
	
	@EJB
	private FcpBancoRN fcpBancoRN;
	
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
		return getFcpBancoRN().pesquisarListaBanco(firstResult, maxResult, orderProperty, asc, fcpBanco);
	}

	/**
	 * Obter count lista de bancos.
	 * @param fcpBanco
	 * @return
	 * @throws BaseException
	 */
	public Long pesquisarCountListaBanco(FcpBanco fcpBanco) throws BaseException {
		return getFcpBancoRN().pesquisarCountListaBanco(fcpBanco);
	}
	
	/**
	 * Pessquisar banco.
	 * @param fcpBanco
	 * @return
	 * @throws BaseException
	 */
	public FcpBanco pesquisarBanco(Short codigoBanco) throws BaseException {
		return getFcpBancoRN().pesquisarBanco(codigoBanco);
	}

	/**
	 * Inserir banco.
	 * @param fcpBanco
	 * @throws BaseException
	 */
	public boolean inserirBanco(FcpBanco fcpBanco) throws BaseException {
		return getFcpBancoRN().inserirBanco(fcpBanco);
	}

	/**
	 * Atualizar banco.
	 * @param fcpBanco
	 * @throws BaseException
	 */
	public void atualizarBanco(FcpBanco fcpBanco) throws BaseException {
		getFcpBancoRN().atualizarBanco(fcpBanco);
	}

	/**
	 * Excluir banco.
	 * @param fcpBanco
	 * @throws BaseException
	 */
	public boolean excluirBanco(FcpBanco fcpBanco) throws BaseException {
		Short codigoBanco = fcpBanco.getCodigo();
		return getFcpBancoRN().excluirBanco(codigoBanco);
	}

	/**
	 * @return the fcpBancoRN
	 */
	public FcpBancoRN getFcpBancoRN() {
		return fcpBancoRN;
	}

	/**
	 * @param fcpBancoRN the fcpBancoRN to set
	 */
	public void setFcpBancoRN(FcpBancoRN fcpBancoRN) {
		this.fcpBancoRN = fcpBancoRN;
	}
	
}
