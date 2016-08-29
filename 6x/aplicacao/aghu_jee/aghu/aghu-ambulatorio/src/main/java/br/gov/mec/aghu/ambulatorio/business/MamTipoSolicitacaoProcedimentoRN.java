package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamTipoSolicitacaoProcedimentoDAO;
import br.gov.mec.aghu.model.MamTipoSolProcedimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MamTipoSolicitacaoProcedimentoRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(MamTipoSolicitacaoProcedimentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private MamTipoSolicitacaoProcedimentoDAO mamTipoSolicitacaoProcedimentoDAO;	
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9096730486475524075L;
	
	public void excluirTipoSolicitacaoProcedimentos(Short seq) throws ApplicationBusinessException {
		MamTipoSolProcedimento mamTipoSolProcedimento = getMamTipoSolProcedimentoDAO().obterPorChavePrimaria(seq);
		
		this.getMamTipoSolProcedimentoDAO().remover(mamTipoSolProcedimento);
		this.getMamTipoSolProcedimentoDAO().flush();
	}
	
	public void persistirTipoSolicitacaoProcedimentos(MamTipoSolProcedimento tipoSolicitacaoProcedimento)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		if (tipoSolicitacaoProcedimento.getSeq() != null) {
			this.getMamTipoSolProcedimentoDAO().atualizar(tipoSolicitacaoProcedimento);
			this.getMamTipoSolProcedimentoDAO().flush();
		} else {
			tipoSolicitacaoProcedimento.setRapServidores(servidorLogado);
			tipoSolicitacaoProcedimento.setCriadoEm(new Date());
			
			this.getMamTipoSolProcedimentoDAO().persistir(tipoSolicitacaoProcedimento);
			this.getMamTipoSolProcedimentoDAO().flush();
		}
	}
	
	public List<MamTipoSolProcedimento> pesquisarTipoSolicitacaoProcedimentosPaginado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MamTipoSolProcedimento tipoSolicitacaoProcedimento) {
		return this.getMamTipoSolProcedimentoDAO()
				.pesquisarTipoSolicitacaoProcedimentosPaginado(firstResult, maxResult, orderProperty, asc, tipoSolicitacaoProcedimento);
	}
	
	public Long countTipoSolicitacaoProcedimentosPaginado(MamTipoSolProcedimento tipoSolicitacaoProcedimento) {
		return this.getMamTipoSolProcedimentoDAO().countTipoSolicitacaoProcedimentosPaginado(tipoSolicitacaoProcedimento);
	}
	
	public MamTipoSolProcedimento obterTipoSolicitacaoProcedimentoPorChavePrimaria(Short codigoTipoSolicitacaoProcedimento){
		return getMamTipoSolProcedimentoDAO().obterPorChavePrimaria(codigoTipoSolicitacaoProcedimento);
	}
	
	protected MamTipoSolicitacaoProcedimentoDAO getMamTipoSolProcedimentoDAO() {
		return mamTipoSolicitacaoProcedimentoDAO;
	}

	
}
