package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpaProtocoloAssistencial;
import br.gov.mec.aghu.protocolos.dao.MpaProtocoloAssistencialDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MpaProtocoloAssistencialRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8329460621046534629L;

	private static final Log LOG = LogFactory.getLog(MpaProtocoloAssistencialRN.class);
	
	@Inject
	private MpaProtocoloAssistencialDAO protocoloAssistencialDAO;
	
	public enum ListaTrabalhoSessaoTerapeuticaRNNExceptionCode implements BusinessExceptionCode {
	}
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	public List<MpaProtocoloAssistencial> buscarProtocolo(Object parametro, Short codigoTipoSessao) {
		
		return protocoloAssistencialDAO.buscarProtocolo(parametro, codigoTipoSessao);
	}

	
	public Long buscarProtocoloCount(Object parametro, Short codigoSala) {
		
		return protocoloAssistencialDAO.buscarProtocoloCount(parametro, codigoSala);
	}
}
