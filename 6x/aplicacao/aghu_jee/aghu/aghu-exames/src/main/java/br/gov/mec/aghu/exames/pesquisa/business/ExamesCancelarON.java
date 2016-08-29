package br.gov.mec.aghu.exames.pesquisa.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ExamesCancelarON extends BaseBusiness {


@EJB
private ExamesCancelarRN examesCancelarRN;

private static final Log LOG = LogFactory.getLog(ExamesCancelarON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;

@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;

@EJB
private ISolicitacaoExameFacade solicitacaoExameFacade;

@Inject
private AelAmostrasDAO aelAmostrasDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IExamesFacade examesFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6353137653577690002L;

	public void cancelarExames(AelItemSolicitacaoExames aelItemSolicitacaoExames, final AelMotivoCancelaExames motivoCancelar, String nomeMicrocomputador) throws BaseException{
		
		aelItemSolicitacaoExames = getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(aelItemSolicitacaoExames.getId());
		aelItemSolicitacaoExames.setAelMotivoCancelaExames(motivoCancelar); // Seta motivo do cancelamento
		
		getExamesCancelarRN().cancelarExames(aelItemSolicitacaoExames, nomeMicrocomputador);
		atuSituacaoItem(aelItemSolicitacaoExames, nomeMicrocomputador);
	
	}

	/**
	 * ORADB AELP_ATU_SITUACAO_ITEM
	 * @param aelItemSolicitacaoExames
	 * @throws BaseException 
	 */
	private void atuSituacaoItem(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador) throws BaseException {
		
		if (aelItemSolicitacaoExames.getAelMotivoCancelaExames().getIndRetornaAExecutar().equals(DominioSimNao.S)) {
			
			if (aelItemSolicitacaoExames.getAelAmostraItemExames().isEmpty()) {								
			
				atualizarItemSolicitacaoExameComSituacao(aelItemSolicitacaoExames, AghuParametrosEnum.P_SITUACAO_CANCELADO, nomeMicrocomputador);
				atualizarItemSolicitacaoExameComSituacao(aelItemSolicitacaoExames, AghuParametrosEnum.P_SITUACAO_A_EXECUTAR, nomeMicrocomputador);
			
			} else {
				
				atualizarItemSolicitacaoExameComSituacao(aelItemSolicitacaoExames, AghuParametrosEnum.P_SITUACAO_CANCELADO, nomeMicrocomputador);
				atualizarItemSolicitacaoExameComSituacao(aelItemSolicitacaoExames, AghuParametrosEnum.P_SITUACAO_A_COLETAR, nomeMicrocomputador);
				//List<AelAmostraItemExames> itensExames = aelItemSolicitacaoExames.getAelAmostraItemExames();
				
				List<AelAmostraItemExames> itensExames = this.getAelAmostraItemExamesDAO().buscarAelAmostraItemExamesPorItemSolicitacaoExame(aelItemSolicitacaoExames);

				if(itensExames != null && !itensExames.isEmpty()){
					
					AelAmostraItemExames item = itensExames.get(0);
					
					//AelAmostras amostra = itemAmostra.getAelAmostras();
					AelAmostras amostra = this.getAelAmostrasDAO().obterOriginal(item.getAelAmostras().getId());
					amostra.setNroInterno(null);
					getExamesFacade().persistirAelAmostra(amostra, true);

					for (AelAmostraItemExames itemAmostra: itensExames) {
						// Limpa NroMapa e OrigemMapa da AelAmostraItemExames
						itemAmostra.setNroMapa(null);
						itemAmostra.setOrigemMapa(null);
						getExamesFacade().persistirAelAmostraItemExames(itemAmostra, true, nomeMicrocomputador);
					}

				}
				
				cancelaDepOrb(aelItemSolicitacaoExames, nomeMicrocomputador);
				
				Boolean retorno = getExamesCancelarRN().verGeraCarta(aelItemSolicitacaoExames);
				
				if (retorno) {
				
					getExamesCancelarRN().geraCartaCanc(aelItemSolicitacaoExames);
				
				}
			}
		
		} else {
		
			AghParametros parametroColeta = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO);
			
			if (!aelItemSolicitacaoExames.getSituacaoItemSolicitacao().getCodigo().equals(parametroColeta.getVlrTexto())) {
				
				AelSitItemSolicitacoes vIseSituacao = getAelSitItemSolicitacoesDAO().obterPeloId(parametroColeta.getVlrTexto());
				aelItemSolicitacaoExames.setSituacaoItemSolicitacao(vIseSituacao);
				//moc_seq = ISE.moc_seq ???
				getSolicitacaoExameFacade().atualizar(aelItemSolicitacaoExames, nomeMicrocomputador);
			
			}
			
		}
		
	}

	private void atualizarItemSolicitacaoExameComSituacao(
			AelItemSolicitacaoExames aelItemSolicitacaoExames,
			AghuParametrosEnum aghuParametrosEnum, String nomeMicrocomputador) throws ApplicationBusinessException,
			BaseException {
		AghParametros parametroColeta = getParametroFacade().buscarAghParametro(aghuParametrosEnum);
		AelSitItemSolicitacoes vIseSituacao = getAelSitItemSolicitacoesDAO().obterPeloId(parametroColeta.getVlrTexto());
		aelItemSolicitacaoExames.setSituacaoItemSolicitacao(vIseSituacao);
		getSolicitacaoExameFacade().atualizar(aelItemSolicitacaoExames, nomeMicrocomputador);
	}

	/**
	 * ORADB aelp_cancela_dep_obr
	 * @param aelItemSolicitacaoExames
	 * @throws BaseException 
	 *  
	 */
	private void cancelaDepOrb(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador) throws ApplicationBusinessException, BaseException {
		
		List<AelItemSolicitacaoExames> aelItensFilhos =  aelItemSolicitacaoExames.getItemSolicitacaoExames();
			
		for (AelItemSolicitacaoExames filho : aelItensFilhos) {
		
			if (!filho.getAelAmostraItemExames().isEmpty()) {
		
				atualizarItemSolicitacaoExameComSituacao(filho, AghuParametrosEnum.P_SITUACAO_A_COLETAR, nomeMicrocomputador);
			
			} else {
				
				atualizarItemSolicitacaoExameComSituacao(filho, AghuParametrosEnum.P_SITUACAO_A_EXECUTAR, nomeMicrocomputador);
			
			}
		
		}
		
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}

	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	protected ExamesCancelarRN getExamesCancelarRN(){
		return examesCancelarRN;
	}
	
	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO(){
		return aelSitItemSolicitacoesDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected AelAmostrasDAO getAelAmostrasDAO() {
		return aelAmostrasDAO;
	}
	
	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
		return aelAmostraItemExamesDAO;
	}	

	public AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

}