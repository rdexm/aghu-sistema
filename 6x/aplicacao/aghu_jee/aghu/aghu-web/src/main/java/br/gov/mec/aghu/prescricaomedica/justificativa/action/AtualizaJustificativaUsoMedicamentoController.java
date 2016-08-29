package br.gov.mec.aghu.prescricaomedica.justificativa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTelaPrescreverItemMdto;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmJustificativaUsoMdto;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.JustificativaMedicamentoUsoGeralVO;
import br.gov.mec.aghu.prescricaomedica.vo.VerificarDadosItensJustificativaPrescricaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class AtualizaJustificativaUsoMedicamentoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -2594749352094005742L;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@Inject
	private PrescreverItemController prescreverItemController;

	// Recebido por parâmetro
	private Integer atdSeq;

	private MpmJustificativaUsoMdto justificativaUsoMdto = new MpmJustificativaUsoMdto();
	private List<JustificativaMedicamentoUsoGeralVO> listaMedicamentos;
	private JustificativaMedicamentoUsoGeralVO selecao;

	/*
	 * Navegação no Wizard
	 */
	private Integer indiceAtual;
	private Boolean processado = false;

	public void inicio() {
		if (justificativaUsoMdto.getSeq() == null) {
			this.listaMedicamentos = prescricaoMedicaFacade.obterListaMedicamentosUsoRestritoPorAtendimento(atdSeq, "N", Boolean.FALSE, null);
		}

		/*
		 * Controla indices
		 */
		if (this.indiceAtual == null) {
			if (this.prescreverItemController.getTelasProcessadas().containsKey(0)) {
				this.indiceAtual = 1;
			} else {
				this.indiceAtual = this.prescreverItemController.getTelasProcessadas().size();
			}
		}
		if (this.prescreverItemController.getTelasProcessadas().containsKey(0)) {
			DominioTelaPrescreverItemMdto telaInicial = this.prescreverItemController.getTelasProcessadas().get(0);
			this.prescreverItemController.getTelasProcessadas().remove(0);
			this.prescreverItemController.getTelasProcessadas().put(1, telaInicial);
		}
	}

	public boolean gravar() {
		try {
			AghAtendimentos atendimento = this.aghuFacade.buscarAtendimentoPorSeq(atdSeq);
			// RF01
			this.justificativaUsoMdto.setAtendimento(atendimento);
			this.justificativaUsoMdto = this.prescricaoMedicaFacade.persistirMpmJustificativaUsoMdto(justificativaUsoMdto, listaMedicamentos);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return false;
		}
		return true;
	}

	public void limpar() {
		this.atdSeq = null;
		this.justificativaUsoMdto = null;
		this.listaMedicamentos = null;
		this.selecao = null;
	}

	/**
	 * Limpar parâmetos da tela
	 */
	protected void limparParametros() {
		this.atdSeq = null;
		this.justificativaUsoMdto = new MpmJustificativaUsoMdto();
		this.listaMedicamentos = null;
		this.selecao = null;
		this.indiceAtual = null;
		this.processado = false;
	}

	/**
	 * Avança no Wizard
	 * 
	 * @return
	 */
	public String avancar() {
		VerificarDadosItensJustificativaPrescricaoVO retorno = null;
		try {
			// Grava somente quando jamais processado
			if (Boolean.FALSE.equals(this.processado)) {
				this.processado = gravar();
				retorno = this.prescricaoMedicaFacade.mpmpVerDadosItens(this.atdSeq);
				if (retorno == null) {
					return this.prescreverItemController.confirmar();
				}
			}
			// Acrescenta retorno somente se não existir processamento
			final Integer indiceProximo = this.indiceAtual + 1;
			if (retorno != null && !this.prescreverItemController.getTelasProcessadas().containsKey(retorno.getTela())) {
				this.prescreverItemController.getTelasProcessadas().put(indiceProximo, retorno.getTela());
			} else {
				retorno = new VerificarDadosItensJustificativaPrescricaoVO();
				retorno.setTela(this.prescreverItemController.getTelasProcessadas().get(indiceProximo));
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			this.processado = false;
			return null;
		}
		this.prescreverItemController.atribuirAtendimentoController(retorno, this.atdSeq);
		return (!processado) ? null : retorno.getTela().getXhtml();
	}

	/**
	 * Retorna no Wizard
	 * 
	 * @return
	 */
	public String retroceder() {
		Integer indiceAnterior = this.indiceAtual - 1;
		DominioTelaPrescreverItemMdto retorno = this.prescreverItemController.getTelasProcessadas().get(indiceAnterior);
		return retorno.getXhtml() == null ? null : retorno.getXhtml();
	}

	/*
	 * GETTERS AND SETTERS
	 */

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public MpmJustificativaUsoMdto getJustificativaUsoMdto() {
		return justificativaUsoMdto;
	}

	public void setJustificativaUsoMdto(MpmJustificativaUsoMdto justificativaUsoMdto) {
		this.justificativaUsoMdto = justificativaUsoMdto;
	}

	public List<JustificativaMedicamentoUsoGeralVO> getListaMedicamentos() {
		return listaMedicamentos;
	}

	public void setListaMedicamentos(List<JustificativaMedicamentoUsoGeralVO> listaMedicamentos) {
		this.listaMedicamentos = listaMedicamentos;
	}

	public JustificativaMedicamentoUsoGeralVO getSelecao() {
		return selecao;
	}

	public void setSelecao(JustificativaMedicamentoUsoGeralVO selecao) {
		this.selecao = selecao;
	}

	public Integer getIndiceAtual() {
		return indiceAtual;
	}

	public Boolean getProcessado() {
		return processado;
	}

}
