package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class AlaController extends ActionController {

	private static final long serialVersionUID = -7494413075008769643L;
	
	private final String PAGE_PESQUISAR_ALA = "alaList";
	private AghAla ala;
	private boolean update;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@PostConstruct
	public void init() {
		this.setAla(new AghAla());
	}
	
	public void iniciar(){
	 

		update = (ala != null && ala.getCodigo() != null);
	
	}
	
	public String cancelar() {
		this.setAla(new AghAla());
		return PAGE_PESQUISAR_ALA;
	}
	
	public String confirmar() {
		try {
			this.cadastrosBasicosInternacaoFacade.persistirAghAla(this.getAla(), update);
			
			if(update){
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_ALA", ala.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_ALA", ala.getDescricao());
			}
			
			return cancelar();
			
		} catch (ApplicationBusinessException e) {
			if(!update){
				ala.setCodigo(null);
			}
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public void setAla(AghAla ala) {
		this.ala = ala;
	}

	public AghAla getAla() {
		return ala;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}
}