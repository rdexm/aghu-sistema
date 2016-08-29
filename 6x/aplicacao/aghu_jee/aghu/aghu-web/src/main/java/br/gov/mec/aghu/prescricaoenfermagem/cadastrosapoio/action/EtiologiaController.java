package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.IPrescricaoEnfermagemApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeFatRelacionado;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class EtiologiaController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -9080940970337693983L;

	private static final String PAGE_ETIOLOGIA_LIST = "etiologiaList";

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

	@EJB
	private IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade;

	@Inject
	private EtiologiaPaginatorController etiologiaPaginatorController;

	private EpeFatRelacionado etiologia;

	private Short etiologiaSeq = null;

	private Boolean ativo;

	public void inicio() {
	 


		if (this.etiologiaSeq != null) {
			this.etiologia = this.prescricaoEnfermagemFacade.obterDescricaoEtiologiaPorSeq(etiologiaSeq);
			if (etiologia.getSituacao().equals(DominioSituacao.A)) {
				this.ativo = true;
			} else {
				this.ativo = false;
			}
		} else {
			etiologia = new EpeFatRelacionado();
			etiologiaSeq = null;
			this.ativo = true;
		}
	
	}

	public void limpar() {
		inicio();
	}

	public String confirmar() {
		// Reinicia o paginator da tela anterior
		etiologiaPaginatorController.getDataModel().reiniciarPaginator();
		try {
			// Verifica se é edição ou criação
			boolean create = this.etiologia.getSeq() == null;
			prescricaoEnfermagemApoioFacade.persistirEtiologia(this.etiologia, ativo);
			if (create) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_ETIOLOGIA", this.etiologia.getDescricao());
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_ETIOLOGIA", this.etiologia.getDescricao());
			}
			this.etiologia = new EpeFatRelacionado();
			this.etiologiaSeq = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return PAGE_ETIOLOGIA_LIST;
	}

	public String cancelar() {
		etiologiaSeq = null;
		return PAGE_ETIOLOGIA_LIST;
	}

	// ### Getters e Setters ###

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public EpeFatRelacionado getEtiologia() {
		return etiologia;
	}

	public void setEtiologia(EpeFatRelacionado etiologia) {
		this.etiologia = etiologia;
	}

	public Short getEtiologiaSeq() {
		return etiologiaSeq;
	}

	public void setEtiologiaSeq(Short etiologiaSeq) {
		this.etiologiaSeq = etiologiaSeq;
	}

}