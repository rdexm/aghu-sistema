package br.gov.mec.aghu.compras.cadastrosbasicos.business;


import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoPontoParadaServidorDAO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPontoServidor;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * 
 * @author aghu
 * 
 */
@Stateless
public class ScoPontoParadaServidorON extends BaseBusiness implements Serializable {

private static final Log LOG = LogFactory.getLog(ScoPontoParadaServidorON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoPontoParadaServidorDAO scoPontoParadaServidorDAO;

@EJB
private IServidorLogadoFacade servidorLogadoFacade;
	/**
	 * 
	 */
	private static final long serialVersionUID = 2672651430122798649L;
	
	public Boolean verificarPontoParadaPermitido(ScoPontoParadaSolicitacao pontoParada) {
		RapServidores servidor = this.getServidorLogadoFacade().obterServidorLogado();
		return getScoPontoParadaServidorDAO().verificarPontoParadaPermitido(pontoParada, servidor);
	}
	
	/**
	 * @author Flavio Rutkowski
	 * @param codigo	
	 * @param vinculo
	 * @param matricula
	 * @return
	 */
	public ScoPontoServidor obterPontoParadaServidorCodigoMatriculaVinculo(
			Short codigo, Short vinculo, Integer matricula) {
		List<ScoPontoServidor> listScoPontoServidor =  getScoPontoParadaServidorDAO()
				.pesquisarPontoParadaServidorCodigoMatriculaVinculo(0, 1, null,
						true, codigo, matricula, vinculo);
		
		return (listScoPontoServidor.get(0));
	}
	
	/**
	 * Retorna instÃ¢ncia de ScoPontoParadaServidorDAO
	 * 
	 * @return
	 */
	protected ScoPontoParadaServidorDAO getScoPontoParadaServidorDAO() {
		return scoPontoParadaServidorDAO;
	}
	
	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}
}