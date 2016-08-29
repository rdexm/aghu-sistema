package br.gov.mec.aghu.compras.solicitacaocompra.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAcoesPontoParadaDAO;
import br.gov.mec.aghu.model.ScoAcoesPontoParada;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ScoAcoesPontoParadaON extends BaseBusiness {
	
	@Inject 
	private ScoAcoesPontoParadaDAO scoAcoesPontoParadaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5922834917785524834L;
	
	private static final Log LOG = LogFactory.getLog(ScoAcoesPontoParadaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	

	public void removerScoAcoesPontoParada(Long seq) {
		ScoAcoesPontoParada acaoPp = this.getScoAcoesPontoParadaDAO().obterPorChavePrimaria(seq);
		if (acaoPp != null) {
			this.getScoAcoesPontoParadaDAO().remover(acaoPp);
		}
	}
	
	public void persistirScoAcoesPontoParada(ScoAcoesPontoParada acao) {
		if (acao.getSeq() == null) {
			this.getScoAcoesPontoParadaDAO().persistir(acao);
		} else {
			ScoAcoesPontoParada acaoPp = this.getScoAcoesPontoParadaDAO().obterPorChavePrimaria(acao.getSeq());
			if (acaoPp != null) {
				acaoPp.setAcao(acao.getAcao());
				this.getScoAcoesPontoParadaDAO().persistir(acaoPp);
			}
		}
	}
	
	private ScoAcoesPontoParadaDAO getScoAcoesPontoParadaDAO() {
		return scoAcoesPontoParadaDAO;
	}
}