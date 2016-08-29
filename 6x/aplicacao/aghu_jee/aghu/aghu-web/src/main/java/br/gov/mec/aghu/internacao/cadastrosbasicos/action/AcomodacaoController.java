package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class AcomodacaoController  extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3406546911146470232L;
	
	private final String PAGE_PESQUISAR_ACOMODACAO = "acomodacaoList";

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private AinAcomodacoes ainAcomodacao;
	
	@PostConstruct
	public void init() {
		begin(conversation);
		ainAcomodacao=new AinAcomodacoes();
	}
	
	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * acomodação
	 */
	public String confirmar() {
		try {
			boolean update = getAinAcomodacao().getSeq()!=null;
			
			this.cadastrosBasicosInternacaoFacade.persistirAcomodacoes(this.ainAcomodacao);

			if (!update) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_ACOMODACAO", this.ainAcomodacao.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_ACOMODACAO",this.ainAcomodacao.getDescricao());
			}
			return cancelar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de Acomodação
	 */
	public String cancelar() {
		ainAcomodacao=new AinAcomodacoes();
		return PAGE_PESQUISAR_ACOMODACAO;
	}

	public AinAcomodacoes getAinAcomodacao() {
		return ainAcomodacao;
	}

	public void setAinAcomodacao(AinAcomodacoes ainAcomodacao) {
		this.ainAcomodacao = ainAcomodacao;
	}

}