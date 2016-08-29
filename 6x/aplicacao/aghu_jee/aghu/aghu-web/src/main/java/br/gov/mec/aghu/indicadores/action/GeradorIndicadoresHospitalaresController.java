package br.gov.mec.aghu.indicadores.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.indicadores.business.IIndicadoresFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class GeradorIndicadoresHospitalaresController extends ActionController {

	private static final long serialVersionUID = 6786579766497094603L;
	
	private boolean isOracle = false;

	@EJB
	private IIndicadoresFacade indicadoresFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private Date mes;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		isOracle = aghuFacade.isOracle();
	}

	/**
	 * Método para gerar os indicadores gerais hospitalares
	 */
	public void gerarIndicadoresHospitalares() {
		try {
			this.indicadoresFacade.gravarIndicadoresResumidos(mes);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCCESSO_EXECUCAO_GERACAO_INDICADORES");
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EXECUCAO_GERACAO_INDICADORES");
		}

		this.mes = null;
	}

	/**
	 * Método para chamar rotina que foi migrada do PL/SQL para gerar os indicadores hospitalares da forma como era feito antes da implantação do BI (IG) no HCPA. Os dados
	 * utilizados nesse caso serão somente para contagem.
	 */
	public void gerarContadoresHospitalares() {
		try {
			this.indicadoresFacade.gerarIndicadoresHospitalares(mes);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCCESSO_EXECUCAO_GERACAO_INDICADORES");
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EXECUCAO_GERACAO_INDICADORES");
		}

		this.mes = null;
	}

	public Date getMes() {
		return mes;
	}

	public void setMes(Date mes) {
		this.mes = mes;
	}

	public boolean isOracle() {
		return isOracle;
	}

	public void setOracle(boolean isOracle) {
		this.isOracle = isOracle;
	}

}
