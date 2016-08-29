package br.gov.mec.aghu.exames.patologia.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelExameApItemSolicDAO;
import br.gov.mec.aghu.exames.dao.AelNotaAdicionalDAO;
import br.gov.mec.aghu.model.AelExameApItemSolic;
import br.gov.mec.aghu.model.AelNotaAdicional;
import br.gov.mec.aghu.model.AelNotaAdicionalId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class AelNotaAdicionalApON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AelNotaAdicionalApON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelExameApItemSolicDAO aelExameApItemSolicDAO;

@Inject
private AelNotaAdicionalDAO aelNotaAdicionalDAO;

@EJB
private IExamesFacade examesFacade;

	private static final long serialVersionUID = 487937854886729176L;

	
	
	// Carrega notas adicionais na tabela do AEL
	public void gravarNotasAdicionais(String notas, Long luxSeq) throws BaseException {
		List<AelExameApItemSolic> lista = getAelExameApItemSolicDAO().listarAelExameApItemSolicPorLuxSeq(luxSeq);
		for(AelExameApItemSolic exame : lista) {
			AelNotaAdicional nota = new AelNotaAdicional();
			nota.setId(new AelNotaAdicionalId(exame.getId().getIseSoeSeq(), exame.getId().getIseSeqp(), null));
			nota.setItemSolicitacaoExame(exame.getItemSolicitacaoExames());
			getAelNotaAdicionalDAO().obterValorSequencialId(nota);
			nota.setNotasAdicionais(notas);
			getExamesFacade().inserirAelNotaAdicional(nota);
		}
	}
	
	private AelExameApItemSolicDAO getAelExameApItemSolicDAO() {
		return aelExameApItemSolicDAO;
	}
	
	private AelNotaAdicionalDAO getAelNotaAdicionalDAO() {
		return aelNotaAdicionalDAO;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
}
