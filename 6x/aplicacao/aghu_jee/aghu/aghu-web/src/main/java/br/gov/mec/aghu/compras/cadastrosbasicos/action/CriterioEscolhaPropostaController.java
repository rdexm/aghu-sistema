package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoCriterioEscolhaProposta;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CriterioEscolhaPropostaController extends ActionController {

	private static final long serialVersionUID = -6876405164044629198L;

	private static final String CRITERIO_ESCOLHA_PROPOSTA_LIST = "criterioEscolhaPropostaList";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;		
	
	private Short codigoCriterio;
	private ScoCriterioEscolhaProposta criterioEscolha;	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

	 
		
		if (this.codigoCriterio == null) {
			criterioEscolha = new ScoCriterioEscolhaProposta();
			criterioEscolha.setSituacao(DominioSituacao.A);
		} else {
			criterioEscolha =  comprasCadastrosBasicosFacade.obterCriterioEscolhaProposta(codigoCriterio);

			if(criterioEscolha == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}

			if (criterioEscolha != null) {
				this.codigoCriterio = criterioEscolha.getCodigo();
				/*this.situacaoCriterio = criterioEscolha.getSituacao();
				this.descricaoCriterio = criterioEscolha.getDescricao();*/
			}
		}
		return null;
	
	}
	
	public String gravar() {
		
		try {
			
			comprasCadastrosBasicosFacade.persistirCriterioEscolhaProposta(criterioEscolha);
			
			String codigoDescricao = criterioEscolha.getCodigo() + " - " + criterioEscolha.getDescricao();
			
			if (this.codigoCriterio == null) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CRITERIO_ESCOLHA_INSERT_SUCESSO", codigoDescricao);
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CRITERIO_ESCOLHA_UPDATE_SUCESSO", codigoDescricao);
			}
			
			return cancelar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public String cancelar() {
		codigoCriterio = null;
		criterioEscolha = null;
		return CRITERIO_ESCOLHA_PROPOSTA_LIST;
	}	

	public Short getCodigoCriterio() {
		return codigoCriterio;
	}

	public void setCodigoCriterio(Short codigoCriterio) {
		this.codigoCriterio = codigoCriterio;
	}
	public ScoCriterioEscolhaProposta getCriterioEscolha() {
		return criterioEscolha;
	}

	public void setCriterioEscolha(ScoCriterioEscolhaProposta criterioEscolha) {
		this.criterioEscolha = criterioEscolha;
	}
}
