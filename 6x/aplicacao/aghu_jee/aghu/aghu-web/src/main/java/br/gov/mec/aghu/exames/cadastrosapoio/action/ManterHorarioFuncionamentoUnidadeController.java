package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AghHorariosUnidFuncional;
import br.gov.mec.aghu.model.AghHorariosUnidFuncionalId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterHorarioFuncionamentoUnidadeController extends ActionController {

	private static final long serialVersionUID = -9063350405796880069L;

	private static final String MANTER_HORARIO_FUNCIONAMENTO_UNIDADE_LIST = "manterHorarioFuncionamentoUnidadeList";

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	private AghUnidadesFuncionais unidadeFuncional;

	
	private List<AghUnidadesFuncionais> listaUnidadesFuncionais;

	private AghHorariosUnidFuncional horariosUnidFuncional;
	
	//parametros
	private Boolean emEdicao = Boolean.FALSE;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		emEdicao = false;
		
		if (horariosUnidFuncional != null && horariosUnidFuncional.getId() != null)  {
			this.horariosUnidFuncional = aghuFacade.obterHorarioUnidadeFuncionalPorId(horariosUnidFuncional.getId(), null, null);

			if(horariosUnidFuncional == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			emEdicao = true;
			this.unidadeFuncional = horariosUnidFuncional.getUnidadeFuncional();
		} else {
			horariosUnidFuncional = new AghHorariosUnidFuncional();
			horariosUnidFuncional.setId(new AghHorariosUnidFuncionalId());
		}
		
		return null;
	
	}

	public String cancelar() {
		horariosUnidFuncional = null;
		unidadeFuncional = null;
		listaUnidadesFuncionais = null;
		emEdicao = false;
		return MANTER_HORARIO_FUNCIONAMENTO_UNIDADE_LIST;
	}

	public String gravar() {
		
		this.horariosUnidFuncional.getId().setUnidadeFuncional(this.unidadeFuncional);
		
		if (getUnidadeFuncional() == null) {
			return null;
		}

		try {
			if (!this.emEdicao){
				this.cadastrosApoioExamesFacade.inserirHorarioFuncionamentoUnidade(horariosUnidFuncional);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_HORARIO_FUNCIONAMENTO");

			} else {
				this.cadastrosApoioExamesFacade.atualizarHorarioFuncionamentoUnidade(horariosUnidFuncional);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_HORARIO_FUNCIONAMENTO");
			}
			
			return cancelar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(String param) {
		return this.aghuFacade.pesquisarUnidadeFuncionalPorUnidEmergencia((String)param, true);
	}

	// GETS AND SETS
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public List<AghUnidadesFuncionais> getListaUnidadesFuncionais() {
		return listaUnidadesFuncionais;
	}

	public void setListaUnidadesFuncionais(
			List<AghUnidadesFuncionais> listaUnidadesFuncionais) {
		this.listaUnidadesFuncionais = listaUnidadesFuncionais;
	}

	public AghHorariosUnidFuncional getHorariosUnidFuncional() {
		return horariosUnidFuncional;
	}

	public void setHorariosUnidFuncional(
			AghHorariosUnidFuncional horariosUnidFuncional) {
		this.horariosUnidFuncional = horariosUnidFuncional;
	}

	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	} 
}