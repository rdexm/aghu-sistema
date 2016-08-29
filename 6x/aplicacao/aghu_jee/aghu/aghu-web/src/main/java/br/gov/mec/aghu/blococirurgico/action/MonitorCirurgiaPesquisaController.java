package br.gov.mec.aghu.blococirurgico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class MonitorCirurgiaPesquisaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = -2621793761904886583L;
	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MonitorCirurgiaPesquisa1Controller monitorCirurgiaPesquisa1Controller;
	
	private static final int TEMPO_PADRAO_MONITOR_CIRURGIA = 15; // O tempo padrão é de 15 segundos

	private AghUnidadesFuncionais unidadeFuncional;
	
	/**
	 * Resgata o tempo padrão do monitor para pesquisa automática
	 */
	public int getTempoMonitor() {
		try {
			AghParametros parametroQtdePadrao = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_TEMPO_MONITOR_CIRURGIA);
			if (parametroQtdePadrao != null && CoreUtil.maior(parametroQtdePadrao.getVlrNumerico().intValue(), 0)) {
				return parametroQtdePadrao.getVlrNumerico().intValue() / 1000;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return TEMPO_PADRAO_MONITOR_CIRURGIA;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutoraMonitorCirurgia(String objPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarUnidadeExecutoraMonitorCirurgia(objPesquisa),pesquisarUnidadeExecutoraMonitorCirurgiaCount(objPesquisa));
	}

	public Integer pesquisarUnidadeExecutoraMonitorCirurgiaCount(String objPesquisa) {
		return this.aghuFacade.pesquisarUnidadeExecutoraMonitorCirurgiaCount(objPesquisa);		
	}
	
	public void atribuirUnidadeFuncional() {
		monitorCirurgiaPesquisa1Controller.setUnfSeq(this.unidadeFuncional.getSeq());
		monitorCirurgiaPesquisa1Controller.inicio();
	}
	
	/*
	 * Getters e Setters
	 */

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}
}