package br.gov.mec.aghu.ambulatorio.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacMotivosDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacMotivos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de motivo consultas.
 */
@Stateless
public class MotivoConsultaCRUD extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MotivoConsultaCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@Inject
	private AacMotivosDAO aacMotivosDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4556515279445041641L;

	public List<AacMotivos> pesquisa(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Short codigo,
			String descricao, DominioSituacao situacao) {
		return getAacMotivosDAO().pesquisa(firstResult, maxResults, orderProperty, asc, codigo, descricao, situacao);
	}

	public Long pesquisaCount(Short codigo, String descricao,
			DominioSituacao situacao) {
		return getAacMotivosDAO().pesquisaCount(codigo, descricao, situacao);
	}

	/**
	 * Método responsável pela persistência de motivo consulta.
	 * 
	 * @param motivoConsulta
	 * @throws ApplicationBusinessException
	 */
	
	public void persistirMotivoConsulta(AacMotivos motivo)
			throws ApplicationBusinessException {
		getAacMotivosDAO().persistirMotivoConsulta(motivo);
	}

	
	public AacMotivos obterMotivoConsulta(Short accMotivoConsultaCodigo) {
		return getAacMotivosDAO().obterMotivoConsulta(accMotivoConsultaCodigo);
	}

	private AacMotivosDAO getAacMotivosDAO(){
		return aacMotivosDAO;
	}
}