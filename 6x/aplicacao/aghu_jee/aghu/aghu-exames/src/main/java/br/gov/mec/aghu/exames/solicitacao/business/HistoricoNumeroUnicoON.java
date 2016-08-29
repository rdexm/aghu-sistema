package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.exames.dao.AelAmostrasJnDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasJnHistDAO;
import br.gov.mec.aghu.exames.solicitacao.vo.HistoricoNumeroUnicoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class HistoricoNumeroUnicoON extends BaseBusiness {


@Inject
private AelAmostrasJnHistDAO aelAmostrasJnHistDAO;

private static final Log LOG = LogFactory.getLog(HistoricoNumeroUnicoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private ICascaFacade cascaFacade;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@Inject
private AelAmostrasJnDAO aelAmostrasJnDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1740047913589181467L;

	public List<HistoricoNumeroUnicoVO> listarHistoricoNroUnico(Integer soeSeq, Short seqp, Boolean isHist) {
		List<HistoricoNumeroUnicoVO> lista = listarAmostrasJnPorSoeSeqESeqp(soeSeq, seqp, isHist);
		
		IRegistroColaboradorFacade ircf = getRegistroColaboradorFacade();
		String nomeUsuario;
		for (HistoricoNumeroUnicoVO vo : lista ) {
			nomeUsuario = null;
			try {
				nomeUsuario = ircf.obterServidorPorUsuario(vo.getJnUser()).getPessoaFisica().getNome();
				if (nomeUsuario == null) {
					nomeUsuario = this.getICascaFacade().recuperarUsuario(vo.getJnUser()).getNome();
				}
				vo.setResponsavel(nomeUsuario);
			} catch (ApplicationBusinessException e) {
				logError(e);
			}	
		}
		
		return lista;
	}
	
	protected List<HistoricoNumeroUnicoVO> listarAmostrasJnPorSoeSeqESeqp(Integer soeSeq, Short seqp, Boolean isHist) {
		if(isHist){
			return getAelAmostrasJnHistDAO().listarAmostrasJnPorSoeSeqESeqp(soeSeq, seqp);
		}else{
			return getAelAmostrasJnDAO().listarAmostrasJnPorSoeSeqESeqp(soeSeq, seqp);
		}
	}
	
	protected AelAmostrasJnDAO getAelAmostrasJnDAO() {
		return aelAmostrasJnDAO;
	}
	
	protected AelAmostrasJnHistDAO getAelAmostrasJnHistDAO() {
		return aelAmostrasJnHistDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}
	
}