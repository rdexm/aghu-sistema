package br.gov.mec.aghu.exames.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.exames.vo.DetalhesExamesPacienteVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class DetalhesExamesPacientesON extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(DetalhesExamesPacientesON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelExamesDAO aelExamesDAO;

	private static final long serialVersionUID = -8718422487674206246L;
	
	public DetalhesExamesPacienteVO buscaResultadosExames(Integer pSoeSeq,
			Integer pIseSoeSeq, Integer pNroSessao) {

		return this.getAelExamesDAO().buscaResultadosExames(pSoeSeq,
				pIseSoeSeq, pNroSessao);
	}
	
	private AelExamesDAO getAelExamesDAO(){
		return aelExamesDAO;
	}
	
}
