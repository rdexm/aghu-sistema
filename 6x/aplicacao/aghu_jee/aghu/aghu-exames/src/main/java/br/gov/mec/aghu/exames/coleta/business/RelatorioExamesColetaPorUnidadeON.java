package br.gov.mec.aghu.exames.coleta.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.coleta.vo.RelatorioExameColetaPorUnidadeVO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class RelatorioExamesColetaPorUnidadeON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioExamesColetaPorUnidadeON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;

@Inject
private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;

@EJB
private IParametroFacade parametroFacade;


	/**
	 * 
	 */
	private static final long serialVersionUID = -1298887998146467268L;

	/**
	 * Retorna Exames em Coleta Por Unidade
	 * 
	 * @param unidadeExecutora
	 * @return List<RelatorioExameColetaPorUnidadeVO>
	 *  
	 */
	public List<RelatorioExameColetaPorUnidadeVO> obterAgendasPorUnidade(AghUnidadesFuncionais unidadeExecutora) throws ApplicationBusinessException {
		AghParametros paramSituacaoEmColeta = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_EM_COLETA);
		AelSitItemSolicitacoes situacao = getAelSitItemSolicitacoesDAO().obterPeloId(paramSituacaoEmColeta.getVlrTexto());
		List<RelatorioExameColetaPorUnidadeVO> listaRetorno = getAelExtratoItemSolicitacaoDAO().obterAgendaPorUnidade(unidadeExecutora, situacao);
		for(RelatorioExameColetaPorUnidadeVO item : listaRetorno) {
			item.setSubRelatorio(getAelExtratoItemSolicitacaoDAO().obterSubAgendaPorUnidade(unidadeExecutora, situacao, item.getProntuario()));
		}
		return listaRetorno;
	}	
	
	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}
	
	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO() {
		return aelSitItemSolicitacoesDAO;
	}
	
	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO() {
		return aelExtratoItemSolicitacaoDAO;
	}

}
