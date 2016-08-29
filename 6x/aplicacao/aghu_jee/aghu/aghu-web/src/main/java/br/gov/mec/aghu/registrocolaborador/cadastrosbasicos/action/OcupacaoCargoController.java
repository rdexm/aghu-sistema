package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapCargos;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapOcupacoesCargoId;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller implementa pesquisa, alteração e inclusão de ocupações do cargo no
 * módulo de registro do colaborador.
 * 
 * @author cvagheti
 * 
 */


public class OcupacaoCargoController extends ActionController {

	private static final long serialVersionUID = -1365635294790216296L;

	private static final String PESQUISAR_OCUPACAO_CARGO = "pesquisarOcupacaoCargo";

	
	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	private RapOcupacaoCargo ocupacaoCargo = new RapOcupacaoCargo();
	private boolean alterar = false;

	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 


		if (ocupacaoCargo != null && ocupacaoCargo.getId() != null) {
			try {
				
				ocupacaoCargo = cadastrosBasicosFacade.obterOcupacaoCargo(ocupacaoCargo.getId());
				
				if(ocupacaoCargo == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return cancelar();
				}
				alterar = true;
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		} else {
			// para inclusão
			this.ocupacaoCargo = new RapOcupacaoCargo();
			this.ocupacaoCargo.setIndSituacao(DominioSituacao.A);
		}
		
		return null;
	
	}

	public String salvar() {

		try {

			RapOcupacoesCargoId id = new RapOcupacoesCargoId(this.ocupacaoCargo.getCargo().getCodigo(), this.ocupacaoCargo.getCodigo());
			this.ocupacaoCargo.setId(id);

			if (this.ocupacaoCargo.getIndCbo() == null) {
				this.ocupacaoCargo.setIndCbo(DominioSimNao.N);
			}

			if (this.ocupacaoCargo.getIndCns() == null) {
				this.ocupacaoCargo.setIndCns(DominioSimNao.N);
			}

			if (isAlterar()) {
				cadastrosBasicosFacade.alterar(ocupacaoCargo);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_ALTERADA_SUCESSO");
				
			} else {
				cadastrosBasicosFacade.incluirOcupacaoCargo(ocupacaoCargo);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_INCLUIDA_SUCESSO");
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return cancelar();
	}

	public String cancelar() {
		this.alterar = false;
		this.ocupacaoCargo = null;
		return PESQUISAR_OCUPACAO_CARGO;
	}

	public List<RapCargos> pesquisarCargosPorDescricao(String descricao) {
		return cadastrosBasicosFacade.pesquisarCargosPorDescricao((String) descricao);
	}

	// getters & setters

	public RapOcupacaoCargo getOcupacaoCargo() {
		return ocupacaoCargo;
	}

	public void setOcupacaoCargo(RapOcupacaoCargo ocupacaoCargo) {
		this.ocupacaoCargo = ocupacaoCargo;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}
}