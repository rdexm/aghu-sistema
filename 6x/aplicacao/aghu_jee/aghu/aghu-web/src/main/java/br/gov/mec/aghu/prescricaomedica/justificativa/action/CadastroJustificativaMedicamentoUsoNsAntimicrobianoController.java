package br.gov.mec.aghu.prescricaomedica.justificativa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTelaPrescreverItemMdto;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmJustificativaUsoMdto;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.JustificativaMedicamentoUsoGeralVO;
import br.gov.mec.aghu.prescricaomedica.vo.VerificarDadosItensJustificativaPrescricaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class CadastroJustificativaMedicamentoUsoNsAntimicrobianoController extends ActionController {

	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 6389928024983329614L;

	public enum CadastroJustificativaMedicamentoUsoNsAntimicrobianoControllerExceptionCode implements BusinessExceptionCode {
		MPM_03573;
	}

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private PrescreverItemController prescreverItemController;

	private Integer atdSeq;
	private List<JustificativaMedicamentoUsoGeralVO> listaMedicamentos;
	private JustificativaMedicamentoUsoGeralVO primeiroRegistro;
	private JustificativaMedicamentoUsoGeralVO selecao;

	// TODO - remover apos integrar com estoria 45722
	private Short gupSeq;
	private MpmJustificativaUsoMdto justificativaUsoMdto = new MpmJustificativaUsoMdto();

	// aba
	private String selectedTab;
	private Integer currentTabIndex;

	/*
	 * Navegação no Wizard
	 */
	private Integer indiceAtual;
	private Boolean processado = false;
	
	private DominioSimNao sondaVesicalDemora;
	private DominioSimNao pacImunodeprimido;
	private DominioSimNao insufHepatica;
	private DominioSimNao gestante;
	private DominioSimNao funcRenalComprometida;
	private DominioSimNao usoCronicoPrevInt;

	public void iniciar() {
		if (this.justificativaUsoMdto.getSeq() != null) {
			//return;
			this.justificativaUsoMdto = new MpmJustificativaUsoMdto();
			this.processado = false;
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

		try {
			AghParametros parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_GRPO_USO_MDTO_NS);
			if (parametro != null && parametro.getVlrNumerico() != null) {
				gupSeq = Short.valueOf(parametro.getVlrNumerico().toString());
			} else {
				throw new ApplicationBusinessException(CadastroJustificativaMedicamentoUsoNsAntimicrobianoControllerExceptionCode.MPM_03573);
			}
			this.listaMedicamentos = prescricaoMedicaFacade.obterListaMedicamentosUsoRestritoPorAtendimento(atdSeq, "S", Boolean.FALSE, gupSeq);
			listarUnicoRegistroPorVez();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void listarUnicoRegistroPorVez() {
		if(this.listaMedicamentos != null && !this.listaMedicamentos.isEmpty()){
			this.primeiroRegistro = this.listaMedicamentos.get(0);
			this.listaMedicamentos.clear();
			this.listaMedicamentos.add(this.primeiroRegistro);
		}
	}

	public boolean gravar() {
		AghAtendimentos atendimento = this.aghuFacade.obterAghAtendimentoPorChavePrimaria(atdSeq);
		justificativaUsoMdto.setAtendimento(atendimento);

		try {
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

	/**
	 * Limpar parâmetos da tela
	 */
	protected void limparParametros() {
		this.atdSeq = null;
		this.listaMedicamentos = null;
		this.selecao = null;
		this.gupSeq = null; // TODO - remover apos integrar com estoria 45722
		this.justificativaUsoMdto = new MpmJustificativaUsoMdto();
		this.selectedTab = null;
		this.currentTabIndex = null;
		this.indiceAtual = null;
		this.processado = false;
	}

	/*
	 * Getters and Setters
	 */
	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public List<JustificativaMedicamentoUsoGeralVO> getListaMedicamentos() {
		return listaMedicamentos;
	}

	public void setListaMedicamentos(List<JustificativaMedicamentoUsoGeralVO> listaMedicamentos) {
		this.listaMedicamentos = listaMedicamentos;
	}

	public MpmJustificativaUsoMdto getJustificativaUsoMdto() {
		return justificativaUsoMdto;
	}

	public void setJustificativaUsoMdto(MpmJustificativaUsoMdto justificativaUsoMdto) {
		this.justificativaUsoMdto = justificativaUsoMdto;
	}

	public JustificativaMedicamentoUsoGeralVO getSelecao() {
		return selecao;
	}

	public void setSelecao(JustificativaMedicamentoUsoGeralVO selecao) {
		this.selecao = selecao;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public Integer getCurrentTabIndex() {
		return currentTabIndex;
	}

	public void setCurrentTabIndex(Integer currentTabIndex) {
		this.currentTabIndex = currentTabIndex;
	}

	public Short getGupSeq() {
		return gupSeq;
	}

	public void setGupSeq(Short gupSeq) {
		this.gupSeq = gupSeq;
	}

	public Integer getIndiceAtual() {
		return indiceAtual;
	}

	public Boolean getProcessado() {
		return processado;
	}
	
	private Boolean converterDominioSimNao(DominioSimNao dominio) {
		return dominio == null ? null : DominioSimNao.S.equals(dominio) ? Boolean.TRUE : Boolean.FALSE;
	}
	
	public DominioSimNao getSondaVesicalDemora() {
		return this.sondaVesicalDemora;
	}
	
	public void setSondaVesicalDemora(DominioSimNao sondaVesicalDemora) {
		this.sondaVesicalDemora = sondaVesicalDemora;
		this.justificativaUsoMdto.setSondaVesicalDemora(converterDominioSimNao(sondaVesicalDemora));
	}

	public DominioSimNao getGestante() {
		return this.gestante;
	}
	
	public void setGestante(DominioSimNao gestante) {
		this.gestante = gestante;
		this.justificativaUsoMdto.setGestante(converterDominioSimNao(gestante));
	}
	
	public DominioSimNao getFuncRenalComprometida() {
		return this.funcRenalComprometida;
	}
	
	public void setFuncRenalComprometida(DominioSimNao funcRenalComprometida) {
		this.funcRenalComprometida = funcRenalComprometida;
		this.justificativaUsoMdto.setFuncRenalComprometida(converterDominioSimNao(funcRenalComprometida));
	}
	
	public DominioSimNao getInsufHepatica() {
		return this.insufHepatica;
	}
	
	public void setInsufHepatica(DominioSimNao insufHepatica) {
		this.insufHepatica = insufHepatica;
		this.justificativaUsoMdto.setInsufHepatica(converterDominioSimNao(insufHepatica));
	}
	
	public DominioSimNao getPacImunodeprimido() {
		return this.pacImunodeprimido;
	}
	
	public void setPacImunodeprimido(DominioSimNao pacImunodeprimido) {
		this.pacImunodeprimido = pacImunodeprimido;
		this.justificativaUsoMdto.setPacImunodeprimido(converterDominioSimNao(pacImunodeprimido));
	}
	
	public DominioSimNao getUsoCronicoPrevInt() {
		return this.usoCronicoPrevInt;
	}
	
	public void setUsoCronicoPrevInt(DominioSimNao usoCronicoPrevInt) {
		this.usoCronicoPrevInt = usoCronicoPrevInt;
		this.justificativaUsoMdto.setUsoCronicoPrevInt(converterDominioSimNao(usoCronicoPrevInt));
	}
}