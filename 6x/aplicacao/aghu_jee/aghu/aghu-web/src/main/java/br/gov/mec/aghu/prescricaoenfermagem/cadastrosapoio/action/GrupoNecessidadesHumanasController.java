package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.IPrescricaoEnfermagemApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class GrupoNecessidadesHumanasController extends ActionController {

	private static final long serialVersionUID = -9080940970337693983L;

	private static final String PAGE_GRUPO_NECESSIDADES_LIST = "grupoNecessidadesHumanasList";

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

	@EJB
	private IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade;


	@Inject
	private GrupoNecessidadesHumanasPaginatorController grupoNecessidadesHumanasPaginatorController;

	private EpeGrupoNecesBasica grupoNecessidadesHumanas;

	private Short grupoNecessidadesHumanasSeq = null;

	private Boolean ativo;

	private boolean emEdicao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
	 


		if (this.grupoNecessidadesHumanasSeq != null) {
			this.grupoNecessidadesHumanas = this.prescricaoEnfermagemFacade
					.obterDescricaoGrupoNecessidadesHumanasPorSeq(grupoNecessidadesHumanasSeq);
			if (grupoNecessidadesHumanas.getSituacao().equals(DominioSituacao.A)) {
				this.ativo = true;
			} else {
				this.ativo = false;
			}
			this.setEmEdicao(true);
		} else {
			this.setEmEdicao(false);
			grupoNecessidadesHumanas = new EpeGrupoNecesBasica();
			grupoNecessidadesHumanasSeq = null;
			this.ativo = true;
		}
	
	}

	public void limpar() {
		inicio();
	}

	public String confirmar() {
		// Reinicia o paginator da tela anterior
		grupoNecessidadesHumanasPaginatorController.getDataModel().reiniciarPaginator();
		try {
			// Verifica se é edição ou criação
			boolean create = this.grupoNecessidadesHumanas.getSeq() == null;
			prescricaoEnfermagemApoioFacade.persistirGrupoNecessidadesHumanas(this.grupoNecessidadesHumanas, ativo);
			if (create) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_GRUPO_NECESSIDADES_HUMANAS",
						this.grupoNecessidadesHumanas.getDescricao());
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_GRUPO_NECESSIDADES_HUMANAS",
						this.grupoNecessidadesHumanas.getDescricao());
			}
			this.grupoNecessidadesHumanas = new EpeGrupoNecesBasica();
			this.grupoNecessidadesHumanasSeq = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return PAGE_GRUPO_NECESSIDADES_LIST;
	}

	public String cancelar() {
		grupoNecessidadesHumanasSeq = null;
		return PAGE_GRUPO_NECESSIDADES_LIST;
	}

	// ### Getters e Setters ###

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public EpeGrupoNecesBasica getGrupoNecessidadesHumanas() {
		return grupoNecessidadesHumanas;
	}

	public void setGrupoNecessidadesHumanas(EpeGrupoNecesBasica grupoNecessidadesHumanas) {
		this.grupoNecessidadesHumanas = grupoNecessidadesHumanas;
	}

	public Short getGrupoNecessidadesHumanasSeq() {
		return grupoNecessidadesHumanasSeq;
	}

	public void setGrupoNecessidadesHumanasSeq(Short grupoNecessidadesHumanasSeq) {
		this.grupoNecessidadesHumanasSeq = grupoNecessidadesHumanasSeq;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

}