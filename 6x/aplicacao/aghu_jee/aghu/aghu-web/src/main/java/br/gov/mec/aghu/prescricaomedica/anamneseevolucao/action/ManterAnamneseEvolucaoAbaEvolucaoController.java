package br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.MpmEvolucoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

public class ManterAnamneseEvolucaoAbaEvolucaoController extends
		ActionController {

	private static final long serialVersionUID = 1345476586786L;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@Inject
	private ManterEvolucaoController manterEvolucaoController;

	@Inject
	private ManterNotaAdicionalEvolucaoController manterNotaAdicionalEvolucaoController;

	private Long seqEvolucao;
	private Long seqAnamnese;
	private Integer seqAtendimento;
	private RapServidores servidorLogado;
	private MpmEvolucoes evolucao;
	private MpmAnamneses anamneses;

	public void iniciar() {
		try {
			// carrega informacao do usuario logado
			servidorLogado = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		evolucao = prescricaoMedicaFacade.buscarMpmEvolucoes(seqEvolucao);
		if (evolucao != null) {
			anamneses = evolucao.getAnamnese();
			seqAnamnese = anamneses.getSeq();
			iniciarSliderEvolucao();
			if (habilitaPainelNotaAdicional()) {
				iniciarSliderNotaAdicional();
			}
		}
	}

	private void iniciarSliderEvolucao() {
		this.manterEvolucaoController.setEvolucao(evolucao);
		this.manterEvolucaoController.setAnamneses(anamneses);
		this.manterEvolucaoController.setServidor(servidorLogado);
		this.manterEvolucaoController.iniciar();
	}

	private void iniciarSliderNotaAdicional() {
		this.manterNotaAdicionalEvolucaoController
				.setAtendimento(this.anamneses.getAtendimento());
		this.manterNotaAdicionalEvolucaoController
				.setServidor(this.servidorLogado);
		this.manterNotaAdicionalEvolucaoController.setEvolucao(this.evolucao);
		this.manterNotaAdicionalEvolucaoController.iniciar();
	}

	public boolean habilitaPainelNotaAdicional() {
		if (evolucao != null
				&& DominioIndPendenteAmbulatorio.V.equals(evolucao
						.getPendente())) {
			return true;
		}
		return false;
	}

	public boolean verificarAlteracao() {
		return manterEvolucaoController.verificarAlteracao()
				|| manterNotaAdicionalEvolucaoController.verificarAlteracao();
	}

	public boolean habilitarAbaEvolucoes() {
		return this.prescricaoMedicaFacade
				.verificarAnamneseValida(this.seqAnamnese);
	}

	public void removerEvolucao() throws ApplicationBusinessException {
		MpmEvolucoes evolucao = this.prescricaoMedicaFacade
				.buscarMpmEvolucoes(this.seqEvolucao);
		if (evolucao.getPendente().equals(DominioIndPendenteAmbulatorio.R)) {
			this.prescricaoMedicaFacade.removerEvolucao(evolucao.getSeq());
		}
	}

	public void setSeqEvolucao(Long seqEvolucao) {
		this.seqEvolucao = seqEvolucao;
	}

	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}

	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}

	public Long getSeqAnamnese() {
		return seqAnamnese;
	}

	public void setSeqAnamnese(Long seqAnamnese) {
		this.seqAnamnese = seqAnamnese;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	public MpmAnamneses getAnamneses() {
		return anamneses;
	}

	public void setAnamneses(MpmAnamneses anamneses) {
		this.anamneses = anamneses;
	}

}
