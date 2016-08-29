package br.gov.mec.aghu.prescricaomedica.justificativa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSexo;
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

public class CadastroJustificativaMedicamentoUsoRestritoAntimicrobianoController extends ActionController {

	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 3355619399993393323L;

	public enum CadastroJustificativaMedicamentoUsoRestritoAntimicrobianoControllerExceptionCode implements BusinessExceptionCode {
		MPM_03572;
	}

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private PrescreverItemController prescreverItemController;

	private Integer atdSeq;
	private List<JustificativaMedicamentoUsoGeralVO> listaMedicamentos;
	private JustificativaMedicamentoUsoGeralVO selecao;
	private MpmJustificativaUsoMdto justificativaUsoMdto = new MpmJustificativaUsoMdto();
	private Short gupSeq;
	
	private DominioSimNao sondaVesicalDemora;
	private DominioSimNao pacImunodeprimido;
	private DominioSimNao insufHepatica;
	private DominioSimNao funcaoRenal;
	private DominioSimNao gestacao;
	private DominioSimNao condutaBaseProtAssist;
	private boolean habilitaGestacao = true;

	/*
	 * Navegação no Wizard
	 */
	private Integer indiceAtual;
	private Boolean processado = false;

	public void iniciar() {
		if (justificativaUsoMdto.getSeq() != null) {
			return;
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
			AghParametros parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_GRPO_USO_MDTO_UR);
			if (parametro != null && parametro.getVlrNumerico() != null) {
				gupSeq = Short.valueOf(parametro.getVlrNumerico().toString());
			} else {
				throw new ApplicationBusinessException(CadastroJustificativaMedicamentoUsoRestritoAntimicrobianoControllerExceptionCode.MPM_03572);
			}
			this.listaMedicamentos = prescricaoMedicaFacade.obterListaMedicamentosUsoRestritoPorAtendimento(atdSeq, "S", Boolean.FALSE, gupSeq);
			
			definiNaoEDesabilitaComboGestacaoSePacienteMasculino();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void definiNaoEDesabilitaComboGestacaoSePacienteMasculino() {
		AghAtendimentos atendimento = aghuFacade.obterAghAtendimentoPorChavePrimaria(atdSeq);
		
		if (!atendimento.getPaciente().getSexo().equals(DominioSexo.F))  {
			habilitaGestacao = false;
			gestacao = DominioSimNao.N;
		}
	}	

	public boolean gravar() {
		try {
			AghAtendimentos atendimento = this.aghuFacade.obterAtendimentoPeloSeq(this.atdSeq);
			this.justificativaUsoMdto.setAtendimento(atendimento);
			this.justificativaUsoMdto = this.prescricaoMedicaFacade.persistirMpmJustificativaUsoMdto(this.justificativaUsoMdto, this.listaMedicamentos);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return false;
		}
		return true;
	}

	public void limpar() {
		this.atdSeq = null;
		this.listaMedicamentos = null;
		this.selecao = null;
		this.justificativaUsoMdto = null;
		this.gupSeq = null;
		this.sondaVesicalDemora = null;
		this.pacImunodeprimido = null;
		this.insufHepatica = null;
		this.funcaoRenal = null;
		this.gestacao = null;
		this.condutaBaseProtAssist = null;
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
		this.justificativaUsoMdto = new MpmJustificativaUsoMdto();
		this.gupSeq = null;
		this.indiceAtual = null;
		this.processado = false;
		this.sondaVesicalDemora = null;
		this.pacImunodeprimido = null;
		this.insufHepatica = null;
		this.funcaoRenal = null;
		this.gestacao = null;
		this.condutaBaseProtAssist = null;
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

	public JustificativaMedicamentoUsoGeralVO getSelecao() {
		return selecao;
	}

	public void setSelecao(JustificativaMedicamentoUsoGeralVO selecao) {
		this.selecao = selecao;
	}

	public MpmJustificativaUsoMdto getJustificativaUsoMdto() {
		return justificativaUsoMdto;
	}

	public void setJustificativaUsoMdto(MpmJustificativaUsoMdto justificativaUsoMdto) {
		this.justificativaUsoMdto = justificativaUsoMdto;
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

	public DominioSimNao getSondaVesicalDemora() {
		return sondaVesicalDemora;
	}

	public void setSondaVesicalDemora(DominioSimNao sondaVesicalDemora) {		
		this.sondaVesicalDemora = sondaVesicalDemora;
		this.justificativaUsoMdto.setSondaVesicalDemora(converterDominioSimNao(sondaVesicalDemora));
	}

	public DominioSimNao getPacImunodeprimido() {
		return pacImunodeprimido;
	}

	public void setPacImunodeprimido(DominioSimNao pacImunodeprimido) {
		this.pacImunodeprimido = pacImunodeprimido;
		this.justificativaUsoMdto.setPacImunodeprimido(converterDominioSimNao(pacImunodeprimido));
	}

	public DominioSimNao getInsufHepatica() {
		return insufHepatica;
	}

	public void setInsufHepatica(DominioSimNao insufHepatica) {
		this.insufHepatica = insufHepatica;
		this.justificativaUsoMdto.setInsufHepatica(converterDominioSimNao(insufHepatica));
	}

	public DominioSimNao getFuncaoRenal() {
		return funcaoRenal;
	}

	public void setFuncaoRenal(DominioSimNao funcaoRenal) {
		this.funcaoRenal = funcaoRenal;
		this.justificativaUsoMdto.setFuncRenalComprometida(converterDominioSimNao(funcaoRenal));
	}

	public DominioSimNao getGestacao() {
		return gestacao;
	}

	public void setGestacao(DominioSimNao gestacao) {
		this.gestacao = gestacao;
		this.justificativaUsoMdto.setGestante(converterDominioSimNao(gestacao));
	}

	public DominioSimNao getCondutaBaseProtAssist() {
		return condutaBaseProtAssist;
	}

	public void setCondutaBaseProtAssist(DominioSimNao condutaBaseProtAssist) {
		this.condutaBaseProtAssist = condutaBaseProtAssist;
		this.justificativaUsoMdto.setCondutaBaseProtAssist(converterDominioSimNao(condutaBaseProtAssist));
	}
	
	private Boolean converterDominioSimNao(DominioSimNao dominio) {
		return dominio == null ? null : DominioSimNao.S.equals(dominio) ? Boolean.TRUE : Boolean.FALSE;
	}

	public boolean isHabilitaGestacao() {
		return habilitaGestacao;
	}

	public void setHabilitaGestacao(boolean habilitaGestacao) {
		this.habilitaGestacao = habilitaGestacao;
	}

}
