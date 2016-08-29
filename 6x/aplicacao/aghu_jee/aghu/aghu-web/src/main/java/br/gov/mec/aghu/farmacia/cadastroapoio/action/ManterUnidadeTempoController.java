package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.model.MpmUnidadeTempo;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterUnidadeTempoController extends ActionController {
	
	private static final long serialVersionUID = -2964721890798744231L;
	
	private final String REDIRECIONA_PESQUISAR_UNIDADE_TEMPO_LIST = "pesquisarUnidadeTempo";
	
	private MpmUnidadeTempo unidadeTempo;
	
	@Inject	
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@PostConstruct
	public void init(){

		if(unidadeTempo == null){
			unidadeTempo = new MpmUnidadeTempo();
		}
	}
	
	public String cancelar() {
		return REDIRECIONA_PESQUISAR_UNIDADE_TEMPO_LIST;
	}

	public String confirmar() {
		try {
			this.prescricaoMedicaFacade.persistirMpmUnidadeTempo(this.unidadeTempo);

			if(unidadeTempo.getSeq() == null) {
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_GRAVAR_UNIDADE_TEMPO",
						this.unidadeTempo.getDescricao());
			} else {
				// Alteração
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_ATUALIZAR_UNIDADE_TEMPO",
						this.unidadeTempo.getDescricao());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return REDIRECIONA_PESQUISAR_UNIDADE_TEMPO_LIST;
	}

	public MpmUnidadeTempo getUnidadeTempo() {
		return unidadeTempo;
	}

	public void setUnidadeTempo(MpmUnidadeTempo unidadeTempo) {
		this.unidadeTempo = unidadeTempo;
	}
	
}
