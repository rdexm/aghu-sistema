package br.gov.mec.aghu.exames.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelUnidMedValorNormal;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterUnidadeMedidaController extends ActionController {

	private static final long serialVersionUID = 1582702759646415153L;

	private static final String MANTER_UNIDADE_MEDIDA_PESQUISA = "manterUnidadeMedidaPesquisa";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	private AelUnidMedValorNormal unidadeMedida;
	
	private boolean iniciouTela;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if(iniciouTela){
			return null;
		}
		
		iniciouTela = true;
		
		if(unidadeMedida != null && unidadeMedida.getSeq() != null) {
			unidadeMedida = examesFacade.obterAelUnidMedValorNormalPeloId(unidadeMedida.getSeq());
			
			if(unidadeMedida == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
		} else {
			limpar();
		}
		
		return null;
	
	}

	public String cancelar() {
		unidadeMedida = null;
		iniciouTela = false;
		return MANTER_UNIDADE_MEDIDA_PESQUISA;
	}

	public String gravar() {

		try {
			cadastrosApoioExamesFacade.persistirUnidadeMedida(unidadeMedida);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_UNIDADE_MEDIDA", unidadeMedida.getDescricao());
			
			return cancelar();
			
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public void limpar() {
		unidadeMedida = new AelUnidMedValorNormal();
		unidadeMedida.setIndSituacao(DominioSituacao.A); 
	}

	public AelUnidMedValorNormal getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(AelUnidMedValorNormal unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public boolean isIniciouTela() {
		return iniciouTela;
	}

	public void setIniciouTela(boolean iniciouTela) {
		this.iniciouTela = iniciouTela;
	}
}