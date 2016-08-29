package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterSalaExecutoraExameController extends ActionController {

	private static final long serialVersionUID = 3437163948708914150L;

	private static final String PESQUISA_SALA_EXECUTORA_EXAME = "pesquisaSalaExecutoraExame";
	
	private AelSalasExecutorasExames salaExecutora;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if (salaExecutora != null && salaExecutora.getId() != null) {
			this.salaExecutora = this.examesFacade.obterSalaExecutoraExamesPorId(salaExecutora.getId());

			if(salaExecutora == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			this.salaExecutora.setCodigo(this.salaExecutora.getId().getSeqp().intValue());
			
		} else {
			this.salaExecutora = new AelSalasExecutorasExames();
			salaExecutora.setSituacao(DominioSituacao.A);
		}
		
		return null;
	
	}
	
	public String gravar() {
		try {
			cadastrosApoioExamesFacade.persistirSalaExecutoraExame(this.salaExecutora);
			apresentarMsgNegocio(Severity.INFO,"MSG_SALA_EXECUTORA_GRAVADO_SUCESSO");
			return cancelar();
			
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String cancelar() {
		salaExecutora = null;
		return PESQUISA_SALA_EXECUTORA_EXAME;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadesExecutoras(String parametro) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorSeqDescricao(parametro);
	}

	public void setSalaExecutora(AelSalasExecutorasExames salaExecutora) {
		this.salaExecutora = salaExecutora;
	}

	public AelSalasExecutorasExames getSalaExecutora() {
		return salaExecutora;
	}
}