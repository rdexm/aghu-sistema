package br.gov.mec.aghu.exames.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelAnticoagulante;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterAnticoagulanteController extends ActionController {

	private static final long serialVersionUID = 4117168355378439712L;

	private static final String MANTER_ANTICOAGULANTE_PESQUISA = "manterAnticoagulantePesquisa";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	private AelAnticoagulante anticoagulante;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String iniciar() {
	 

		if(anticoagulante != null && anticoagulante.getSeq() != null) {
			anticoagulante = examesFacade.obterAelAnticoagulantePeloId(anticoagulante.getSeq());

			if(anticoagulante == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		} else {
			limpar();
		}
		return null;
	
	}

	public String gravar() {
		try {
			cadastrosApoioExamesFacade.persistirAnticoagulante(anticoagulante);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_ANTICOAGULANTE", anticoagulante.getDescricao());
			return cancelar();
			
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public String cancelar() {
		anticoagulante = null;
		return MANTER_ANTICOAGULANTE_PESQUISA;
	}

	public void limpar() {
		anticoagulante = new AelAnticoagulante();
		anticoagulante.setIndSituacao(DominioSituacao.A);
	}

	public AelAnticoagulante getAnticoagulante() {
		return anticoagulante;
	}

	public void setAnticoagulante(AelAnticoagulante anticoagulante) {
		this.anticoagulante = anticoagulante;
	}
}