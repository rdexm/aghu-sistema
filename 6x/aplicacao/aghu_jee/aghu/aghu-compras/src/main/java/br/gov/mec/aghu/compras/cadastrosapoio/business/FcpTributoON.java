package br.gov.mec.aghu.compras.cadastrosapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.model.FcpRetencaoAliquota;
import br.gov.mec.aghu.model.FcpRetencaoTributo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class FcpTributoON extends BaseBusiness {

	private static final long serialVersionUID = 2743726191279600477L;

	@EJB
	private FcpTributoRN fcpTributoRN;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	 public List<FcpRetencaoAliquota> obterListaTributo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FcpRetencaoTributo fcpRetencaoTributo) throws BaseException {
	  List retorno = this.fcpTributoRN.obterListaTributo(firstResult, maxResult, orderProperty, asc, fcpRetencaoTributo);
	  return retorno;
	 }

	 public Long obterCountTributo(FcpRetencaoTributo fcpRetencaoTributo) throws BaseException {
	  Long retorno = this.fcpTributoRN.obterCountTributo(fcpRetencaoTributo);
	  return retorno;
	 }
	
	public void inserirRetencaoAliquota(FcpRetencaoAliquota fcpRetencaoAliquota) throws BaseException {
		this.fcpTributoRN.inserirRetencaoAliquota(fcpRetencaoAliquota);
	}

	public void atualizarRetencaoAliquota(FcpRetencaoAliquota fcpRetencaoAliquota) throws BaseException {
		this.fcpTributoRN.atualizarRetencaoAliquota(fcpRetencaoAliquota);
	}

	public void excluirRetencaoAliquota(FcpRetencaoAliquota fcpRetencaoAliquota) throws ApplicationBusinessException {
		
		this.fcpTributoRN.excluirRetencaoAliquota(fcpRetencaoAliquota);
	}
	
	public RapServidores obterRapServidor(Usuario usuario) throws BaseException {
		
		return fcpTributoRN.obterRapServidor(usuario);
	}
	
	/**
	 * @return the fcpTributoRN
	 */
	public FcpTributoRN getFcpTributoRN() {
		return fcpTributoRN;
	}

	/**
	 * @param fcpTributoRN the fcpTributoRN to set
	 */
	public void setFcpTributoRN(FcpTributoRN fcpTributoRN) {
		this.fcpTributoRN = fcpTributoRN;
	}

}
