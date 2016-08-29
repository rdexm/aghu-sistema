package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.checagemeletronica.business.IChecagemEletronicaFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EceJustificativaMdto;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class JustificativaMedicamentoController extends ActionController {

	private static final long serialVersionUID = -9080940970337693983L;

	private static final String PAGE_JUSTIFICATIVA_MEDICAMENTO_LIST = "justificativaMedicamentoList";

	@EJB
	private IChecagemEletronicaFacade checagemEletronicaFacade;

	@Inject
	private JustificativaMedicamentoPaginatorController justificativaMedicamentoPaginatorController;

	private EceJustificativaMdto justificativaMdto;

	private Short justificativaMdtoSeq;

	private Boolean ativo;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 


		if (justificativaMdtoSeq != null) {
			justificativaMdto = this.checagemEletronicaFacade.obterEceJustificativaMdtoPorSeq(justificativaMdtoSeq);
			if (justificativaMdto.getIndSituacao().equals(DominioSituacao.A)) {
				this.ativo = true;
			} else {
				this.ativo = false;
			}
		} else {
			justificativaMdto = new EceJustificativaMdto();
			this.ativo = true;
		}
	
	}

	public String confirmar() {
		// Reinicia o paginator da tela anterior
		justificativaMedicamentoPaginatorController.getDataModel().reiniciarPaginator();
		try {
			// Verifica se é edição ou criação
			boolean create = this.justificativaMdto.getSeq() == null;
			setarDominioSituacao(justificativaMdto, ativo);
			checagemEletronicaFacade.persistirJustificativaMdto(this.justificativaMdto);
			if (create) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_JUSTIFICATIVA",
						this.justificativaMdto.getDescricao());
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_JUSTIFICATIVA",
						this.justificativaMdto.getDescricao());
			}
			this.justificativaMdto = new EceJustificativaMdto();
			this.justificativaMdtoSeq = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return PAGE_JUSTIFICATIVA_MEDICAMENTO_LIST;
	}

	private void setarDominioSituacao(EceJustificativaMdto justificativaMdto, Boolean ativo) {
		if (ativo) {
			justificativaMdto.setIndSituacao(DominioSituacao.A);
		} else {
			justificativaMdto.setIndSituacao(DominioSituacao.I);
		}
	}

	public String cancelar() {
		justificativaMdtoSeq = null;
		return PAGE_JUSTIFICATIVA_MEDICAMENTO_LIST;
	}

	// ### Getters e Setters ###

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public EceJustificativaMdto getJustificativaMdto() {
		return justificativaMdto;
	}

	public Short getJustificativaMdtoSeq() {
		return justificativaMdtoSeq;
	}

	public void setJustificativaMdto(EceJustificativaMdto justificativaMdto) {
		this.justificativaMdto = justificativaMdto;
	}

	public void setJustificativaMdtoSeq(Short justificativaMdtoSeq) {
		this.justificativaMdtoSeq = justificativaMdtoSeq;
	}

	public void setChecagemEletronicaFacade(IChecagemEletronicaFacade checagemEletronicaFacade) {
		this.checagemEletronicaFacade = checagemEletronicaFacade;
	}

}