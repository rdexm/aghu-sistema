package br.gov.mec.aghu.prescricaomedica.justificativa.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseAgravoOutrasDoencas;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseTipoNotificacao;
import br.gov.mec.aghu.dominio.DominioTelaPrescreverItemMdto;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.SinamVO;
import br.gov.mec.aghu.prescricaomedica.vo.VerificarDadosItensJustificativaPrescricaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class CadastroSinamController extends ActionController {

	@PostConstruct
	public void init() {
		begin(conversation);
	}

	private static final long serialVersionUID = -8744132114966769464L;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@Inject
	private PrescreverItemController prescreverItemController;

	private Integer ntbSeq;
	private SinamVO vo = new SinamVO(); // Campos da tela
	private boolean exibirModalAlertaGravar;

	/*
	 * Navegação no Wizard
	 */
	private Integer indiceAtual;
	private Boolean processado = false;

	public void iniciar() {
		vo = this.prescricaoMedicaFacade.obterNotificacaoTuberculostatica(ntbSeq);
		informarValorPadrao(); // Seta valores padrão

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

	/**
	 * Seta todos os valores padrão da tela
	 */
	private void informarValorPadrao() {

		Date dataAtual = new Date();
		this.vo.setDataNotificacao(dataAtual);
		this.vo.setTipoNotificacao(DominioNotificacaoTuberculoseTipoNotificacao.INDIVIDUAL);

		try {
			AghParametros ufSedeHu = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_UF_SEDE_HU);
			AghParametros cidadePadrao = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CIDADE_PADRAO);
			AghParametros hospital = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_CLINICAS);
			AghParametros cnes = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CNES_HCPA);
			AghParametros pCid = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_NOTIFICACAO_TB_CID);
			AghParametros pDoenca = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_NOTIFICACAO_TB);

			this.vo.setUfHospital(ufSedeHu.getVlrTexto());
			this.vo.setMunicipioNotificacao(cidadePadrao.getVlrTexto());
			this.vo.setUnidadeSaude(hospital.getVlrTexto());
			this.vo.setCodigo(cnes.getVlrNumerico().intValue());
			this.vo.setDoenca(pDoenca.getVlrTexto());
			this.vo.setCodigoCid(pCid.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Gravar
	 */
	public void gravar() {
		try {
			this.prescricaoMedicaFacade.gravarFormularioSinam(this.vo);
			this.exibirModalAlertaGravar = true;
			this.openDialog("modalAlertaBotaoGravarWG");
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Confirmacao modal
	 */
	public String confirmar() {
		VerificarDadosItensJustificativaPrescricaoVO retorno = null;
		try {
			exibirModalAlertaGravar = false;
			this.prescricaoMedicaFacade.persistirNotificacaoTuberculostatico(this.vo);
			
			if(Boolean.FALSE.equals(this.processado)){
				retorno = this.prescricaoMedicaFacade.mpmpVerDadosItens(this.vo.getAtdSeq());
				if (retorno == null) {
					return this.prescreverItemController.confirmar();
				}
				this.processado = true;
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
		this.prescreverItemController.atribuirAtendimentoController(retorno, this.getVo().getAtdSeq());
		return (!processado) ? null : retorno.getTela().getXhtml();
	}

	/**
	 * TODO REMOVER
	 */
	public void pesquisar() {
		vo = this.prescricaoMedicaFacade.obterNotificacaoTuberculostatica(ntbSeq);
		informarValorPadrao();
	}

	public void limpar() {
		this.vo = null;
		this.ntbSeq = null;
		this.exibirModalAlertaGravar = false;
	}

	/**
	 * Avança no Wizard
	 * 
	 * @return
	 */
	public String avancar() {
		if (Boolean.FALSE.equals(this.processado)) {
			// Grava somente quando jamais processado
			gravar();
		} else {
			return confirmar();
		}
		return null;
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

	protected void limparParametros() {
		this.ntbSeq = null;
		this.vo = null;
		this.exibirModalAlertaGravar = false;
		this.indiceAtual = null;
		this.processado = false;
	}

	public boolean obterNotificacao() {
		return DominioNotificacaoTuberculoseAgravoOutrasDoencas.SIM.equals(vo.getIndOutrasDoencas()) ? true : false;
	}

	/*
	 * Getters and setters
	 */

	public SinamVO getVo() {
		return vo;
	}

	public void setVo(SinamVO vo) {
		this.vo = vo;
	}

	public boolean isExibirModalAlertaGravar() {
		return exibirModalAlertaGravar;
	}

	public void setExibirModalAlertaGravar(boolean exibirModalAlertaGravar) {
		this.exibirModalAlertaGravar = exibirModalAlertaGravar;
	}

	public Integer getNtbSeq() {
		return ntbSeq;
	}

	public void setNtbSeq(Integer ntbSeq) {
		this.ntbSeq = ntbSeq;
	}

	public PrescreverItemController getPrescreverItemController() {
		return prescreverItemController;
	}

	public Integer getIndiceAtual() {
		return indiceAtual;
	}

	public Boolean getProcessado() {
		return processado;
	}
}