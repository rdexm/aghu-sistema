package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelDescricoesResulPadraoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCaracteristicaDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCodificadoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoPadraoCampoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadosPadraoDAO;
import br.gov.mec.aghu.model.AelDescricoesResulPadrao;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoPadraoCampo;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ResultadoPadraoON extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ResultadoPadraoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelResultadoPadraoCampoDAO aelResultadoPadraoCampoDAO;

@Inject
private AelDescricoesResulPadraoDAO aelDescricoesResulPadraoDAO;

@Inject
private AelResultadosPadraoDAO aelResultadosPadraoDAO;

@Inject
private AelResultadoCodificadoDAO aelResultadoCodificadoDAO;

@Inject
private AelResultadoCaracteristicaDAO aelResultadoCaracteristicaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2675525562187055624L;

	public List<AelResultadosPadrao> listarResultadosPadraoPorSeqOuDescricao(String param) {
		return this.getAelResultadosPadraoDAO().listarResultadosPadraoPorSeqOuDescricao(param);
	}
	
	
	public List<AelResultadoCodificado> sbListarResultadoCodificado(String param, Integer calSeq) {
		return this.getAelResultadoCodificadoDAO().listarResultadoCodificado(param, calSeq);
	}
	
	public List<AelResultadoCaracteristica> listarResultadoCaracteristica(String param, Integer calSeq) {
		return this.getAelResultadoCaracteristicaDAO().listarResultadoCaracteristica(param, calSeq);
	}
	
	public AelResultadoPadraoCampo obterResultadoPadraoCampoPorParametro(Integer calSeq, Integer seqP, Integer rpaSeq) {
		return this.getAelResultadoPadraoCampoDAO().obterResultadoPadraoCampoPorParametro(calSeq, seqP, rpaSeq);
			
	}
	
	public AelDescricoesResulPadrao obterAelDescricoesResulPadrao(Integer rpcRpaSeq, Integer seqP) {
		return this.getAelDescricoesResulPadraoDAO().obterAelDescricoesResulPadraoPorID(rpcRpaSeq, seqP);
	}
	
	public List<AelResultadoCodificado> buscarAelResultadoCodificadoPorCampoLaudo(final String filtro, final Integer calSeq) {
		return this.getAelResultadoCodificadoDAO().buscarAelResultadoCodificadoPorCampoLaudo(filtro, calSeq);
	}
	
	/** GETS **/
	protected AelResultadosPadraoDAO getAelResultadosPadraoDAO() {
		return aelResultadosPadraoDAO;
	}
	
	protected AelResultadoCodificadoDAO getAelResultadoCodificadoDAO() {
		return aelResultadoCodificadoDAO;
	}
	
	protected AelResultadoCaracteristicaDAO getAelResultadoCaracteristicaDAO() {
		return aelResultadoCaracteristicaDAO;
	}
	
	protected AelResultadoPadraoCampoDAO getAelResultadoPadraoCampoDAO() {
		return aelResultadoPadraoCampoDAO;
	}
	
	protected AelDescricoesResulPadraoDAO getAelDescricoesResulPadraoDAO() {
		return aelDescricoesResulPadraoDAO;
	}


}
