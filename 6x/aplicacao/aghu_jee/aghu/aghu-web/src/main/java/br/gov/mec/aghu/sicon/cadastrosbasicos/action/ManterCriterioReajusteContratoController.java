package br.gov.mec.aghu.sicon.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoCriterioReajusteContrato;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;



public class ManterCriterioReajusteContratoController extends ActionController {

	private static final long serialVersionUID = 9014211806702873955L;
	
	private static final String PAGE_PESQUISAR_CRITERIO_REAJUSTE_CONTRATO = "pesquisarCriterioReajusteContrato"; 

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;

	private ScoCriterioReajusteContrato scoCriterioReajusteContrato;
	
	private Integer codigoCriterio;
	private boolean alterar;
	private boolean reajusteVinculado;
	

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	public void iniciar() {
	 


		setReajusteVinculado(false);

		// Caso seja enviado um codigoCriterio(seq) trata-se de uma edição
		if (codigoCriterio != null) {
			scoCriterioReajusteContrato = cadastrosBasicosSiconFacade.obterCriterioReajusteContrato(codigoCriterio);

			if (scoCriterioReajusteContrato != null) {

				this.setAlterar(true);
				setReajusteVinculado(cadastrosBasicosSiconFacade
						.verificarAssociacaoContratoComReajuste(scoCriterioReajusteContrato));
			} else {
				apresentarExcecaoNegocio(new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO));
				scoCriterioReajusteContrato = new ScoCriterioReajusteContrato();
				scoCriterioReajusteContrato.setSituacao(DominioSituacao.A);
				this.setAlterar(false);
			}

			// Inclusão de novo critério
		} else {
			scoCriterioReajusteContrato = new ScoCriterioReajusteContrato();
			scoCriterioReajusteContrato.setSituacao(DominioSituacao.A);
			this.setAlterar(false);
		}
	
	}

	public String gravar() {

		try {

			if (isAlterar()) {
				cadastrosBasicosSiconFacade
						.alterarCriterioReajusteContrato(scoCriterioReajusteContrato);
				apresentarMsgNegocio(Severity.INFO,
								"MENSAGEM_SUCESSO_ALTERACAO_CRITERIO_REAJUSTE_CONTRATO");
			} else {
				cadastrosBasicosSiconFacade
						.inserirCriterioReajusteContrato(scoCriterioReajusteContrato);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_INCLUSAO_CRITERIO_REAJUSTE_CONTRATO");
			}
				
				

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		codigoCriterio = null;
		return PAGE_PESQUISAR_CRITERIO_REAJUSTE_CONTRATO;
	}


	public String voltar() {
		codigoCriterio = null;
		
		return PAGE_PESQUISAR_CRITERIO_REAJUSTE_CONTRATO;
	}
	
	
	public ScoCriterioReajusteContrato getScoCriterioReajusteContrato() {
		return scoCriterioReajusteContrato;
	}

	public void setScoCriterioReajusteContrato(
			ScoCriterioReajusteContrato scoCriterioReajusteContrato) {
		this.scoCriterioReajusteContrato = scoCriterioReajusteContrato;
	}

	public Integer getCodigoCriterio() {
		return codigoCriterio;
	}

	public void setCodigoCriterio(Integer codigoCriterio) {
		this.codigoCriterio = codigoCriterio;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	public boolean isReajusteVinculado() {
		return reajusteVinculado;
	}

	public void setReajusteVinculado(boolean reajusteVinculado) {
		this.reajusteVinculado = reajusteVinculado;
	}

}
