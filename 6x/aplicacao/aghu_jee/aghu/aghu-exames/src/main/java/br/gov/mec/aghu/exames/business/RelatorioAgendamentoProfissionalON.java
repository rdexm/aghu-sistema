package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.vo.RelatorioAgendamentoProfissionalVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class RelatorioAgendamentoProfissionalON extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(RelatorioAgendamentoProfissionalON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6009646992215319889L;

	public List<RelatorioAgendamentoProfissionalVO> pesquisarRelatorioAgendamentoProfissional(Date dataReferenciaIni, Date dataReferenciaFim)    throws BaseException {
		return getAelSolicitacaoExameDAO().pesquisarRelatorioAgendamentoProf(dataReferenciaIni, dataReferenciaFim);
	
	}
	
	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}
	
}
