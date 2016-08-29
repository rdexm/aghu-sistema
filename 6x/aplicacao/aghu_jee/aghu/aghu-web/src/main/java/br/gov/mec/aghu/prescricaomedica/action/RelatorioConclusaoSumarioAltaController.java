package br.gov.mec.aghu.prescricaomedica.action;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.model.MpmAltaPedidoExame;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ReceitaMedicaVO;

/**
 * Controller pai das abas que contem os relatórios para sumário de alta. 
 * 
 * Após a adoção do CUPS o uso dessa classe foi descartada e suas rotinas de
 * visualização das abas foram migradas para os relatórios. Caso seja necessário
 * visualizar os relatórios dento do navegador novamente, reativar essa classe.
 * 
 */

@Deprecated
public class RelatorioConclusaoSumarioAltaController extends ActionController {

	/**
	 * 
	 */
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	private static final long serialVersionUID = 7127191792659009251L;
	
	private static final String PAGE_PRESCRICAOMEDICA_MANTER_DIAGNOSTICOS_PACIENTE = "prescricaomedica-manterDiagnosticosPaciente";

	private static final String TAB_0 = "tabs-0";
	private static final String TAB_1 = "tabs-1";
	private static final String TAB_2 = "tabs-2";
	private static final String TAB_3 = "tabs-3";

	private static enum AbaRelatorioSumarioAlta {
		SUMARIO(0), RECEITUARIO(1), PEDIDO_EXAMES(2), LAUDO_SUS(3), ;

		private final Integer value;

		private AbaRelatorioSumarioAlta(Integer value) {
			this.value = value;
		}

		public static AbaRelatorioSumarioAlta fromValue(Integer value) {
			AbaRelatorioSumarioAlta retorno = null;
			for (AbaRelatorioSumarioAlta aba : AbaRelatorioSumarioAlta.values()) {
				if (aba.getValue().equals(value)) {
					retorno = aba;
					break;
				}
			}
			return retorno;
		}

		public Integer getValue() {
			return this.value;
		}
	}
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	private Integer atdSeq;
	private Integer apaSeq; 
	private Short seqp;
	/**
	 * Tipo Sumario: ALTA, OBITO, ANTECIPACAO.
	 */
	private String altaTipoOrigem;
	
	private MpmAltaSumario altaSumario;
	
	/**
	 * Retorno
	 */
	private String voltarPara;

	/**
 	 * Flag para controle da apresentacao da aba Sumario
 	 */
	private Boolean renderSumario = false;
 	/**
 	 * Flag para controle da apresentacao da aba Receituario
 	 */
	private Boolean renderReceituario = false;
 	/**
 	 * Flag para controle da apresentacao da aba PedidosExames
 	 */
	private Boolean renderPedidosExames = false;
 	/**
 	 * Flag para controle da apresentacao da aba LaudoSUS
 	 */
	private Boolean renderLaudoSUS = false;
	
	
	/**
	 * Flag para controle da apresentacao da aba Exames
	 */
	private Boolean mostrarPedidosExames = false;
	
 	/**
 	 * Flag para controle da apresentacao da aba LaudoSUS
 	 */
	private Boolean mostrarLaudoSUS = false;
	
	/**
 	 * Flag para controle da apresentacao da aba de Receitas
 	 */
	private Boolean mostrarReceitas = false;
	
	
	
	/**
 	 * Aba corrente
 	 */
 	private Integer currentTabIndex;
 	
 	/**
 	 * Slider corrente
 	 */
 	private Integer currentSlider;
 	
 	
 	@Inject
 	private  RelatorioConclusaoAbaLaudoSus relatorioConclusaoAbaLaudoSus;
 	
 	@Inject
 	private  RelatorioConclusaoAbaPedidosExames relatorioConclusaoAbaPedidosExames;
 	
 	@Inject
 	private  RelatorioConclusaoAbaReceituario relatorioConclusaoAbaReceituario;
 	
 	@Inject
 	private  RelatorioConclusaoAbaSumario relatorioConclusaoAbaSumario;
 	
// 	@Inject
//	private RelatorioAtestadosController relatorioAtestadosController;
 	
 	
 	
	/**
	 * 	Inicializa variaveis da controller.
	 * @throws MECBaseException 
	 */
	private void initController() throws BaseException {
		MpmAltaSumarioId id = new MpmAltaSumarioId();
		id.setApaAtdSeq(this.getAtdSeq());
		id.setApaSeq(this.getApaSeq());
		id.setSeqp(this.getSeqp()); 
		this.setAltaSumario(prescricaoMedicaFacade.pesquisarAltaSumarios(id));
		
		this.currentSlider = 0;
		this.currentTabIndex = 0;
		
		this.setMostrarLaudoSUS(false);
		this.setMostrarPedidosExames(false);
		this.setMostrarReceitas(false);
		
		/**
		 * Item 16 da estória #5660 Somente mostrar a aba de pedido de exames se
		 * o hospital tem ambulatório e existir algum pedido de consulta ou de
		 * exame.
		*/
		final MpmAltaPedidoExame altaPedidoExame = this.prescricaoMedicaFacade
				.obterMpmAltaPedidoExame(this.altaSumario.getId().getApaAtdSeq(), this.altaSumario
						.getId().getApaSeq(), this.altaSumario.getId().getSeqp());
		
		final Boolean existeAmbulatorio = this.prescricaoMedicaFacade.existeAmbulatorio();
		
		if(altaPedidoExame != null && existeAmbulatorio) {
			this.setMostrarPedidosExames(Boolean.TRUE);
		}
		
		/**
		 * Item 17 da estória #5660 Mostrar a aba de Laudo Sus se o grupo
		 * convênio for do tipo SUS e se houver dados para o relatório.
		 */
		if (this.altaSumario.getConvenioSaudePlano() != null
				&& this.altaSumario.getConvenioSaudePlano().getConvenioSaude() != null
				&& DominioGrupoConvenio.S == this.altaSumario.getConvenioSaudePlano()
						.getConvenioSaude().getGrupoConvenio()) {
			boolean temDados = this.prescricaoMedicaFacade
					.existeProcedimentosComLaudoJustificativaParaImpressao(this.altaSumario
							.getAtendimento());
			this.setMostrarLaudoSUS(temDados);
		}
		
		/**
		 * Mostrar a aba de Receitas se existir conteúdo para mesma.
		 */
		List<ReceitaMedicaVO> listaReceitaMedicaVO = ambulatorioFacade.imprimirReceita(
				altaSumario, false);
		if(listaReceitaMedicaVO != null && !listaReceitaMedicaVO.isEmpty()){
			this.setMostrarReceitas(true);
		}
		
	}
	
	/**
	 * Método de entrada do Relatorio do Sumário de Alta.<br>
	 * Prepara a aba de Sumario.<br>
	 * Chamado apenas uma vez na requisicao inicial da pagina.
	 * @throws MECBaseException 
	 * 
	 */
	public void inicio() throws BaseException {
		this.initController();
		this.renderAbas();
		// #46253 - Envia para a impressora todos os atestados.
//		try {
//			List<AtestadoVO> atestados = this.prescricaoMedicaFacade.obterListaDocumentosPacienteAtestados(this.atdSeq, this.apaSeq, this.seqp, null);
//			for (AtestadoVO atestado : atestados) {
//				this.relatorioAtestadosController.setAtestado(atestado);
//				this.relatorioAtestadosController.setDescricaoDocumento(atestado.getDescricaoAtestado());
//				this.relatorioAtestadosController.directPrint();
//	}
//		} catch (ApplicationBusinessException e) {
//			apresentarExcecaoNegocio(e);
//		}
	}
 	
	/**
	 * Caso a tela tenha slider.
	 */
 	public void registrarSliderAtual() {
		// do nothing
	}

	public void tabChange(TabChangeEvent event) {
		if(event != null && event.getTab() != null) {
			if(TAB_0.equals(event.getTab().getId())) {
				this.currentTabIndex = 0;
			} else if(TAB_1.equals(event.getTab().getId())) {
				this.currentTabIndex = 1;
			} else if(TAB_2.equals(event.getTab().getId())) {
				this.currentTabIndex = 2;
			} else if(TAB_3.equals(event.getTab().getId())) {
				this.currentTabIndex = 3;
			} 
			renderAbas();			
		}
	}

 	/**
	 * Identifica a tab selecionada e executa o metodo de render desta tab.<br>
	 * Utiliza a variavel <code>currentTabIndex</code>.
	 */
	public void renderAbas() {
		try {
			if (this.currentTabIndex == null || this.getAltaSumario() == null) {
				throw new IllegalStateException(
						"Este metodo foi chamado em um momento invalido da controller.");
			}
			
			AbaRelatorioSumarioAlta abaSelecionada = AbaRelatorioSumarioAlta
					.fromValue(this.currentTabIndex);

			this.initFlagAbas();

			switch (abaSelecionada) {
				case SUMARIO:
					this.setRenderSumario(true);
				this.relatorioConclusaoAbaSumario.renderAba(this.getAltaSumario(),
						this.getAltaTipoOrigem());
					break;
				case RECEITUARIO:
					this.setRenderReceituario(true);
				this.relatorioConclusaoAbaReceituario.renderAba(this.getAltaSumario(),
						this.getAltaTipoOrigem());
					break;
				case PEDIDO_EXAMES:
					this.setRenderPedidosExames(true);
				this.relatorioConclusaoAbaPedidosExames.renderAba(this.getAltaSumario(),
						this.getAltaTipoOrigem());
					break;
				case LAUDO_SUS:
					this.setRenderLaudoSUS(true);
				this.relatorioConclusaoAbaLaudoSus.renderAba(this.getAltaSumario(),
						this.getAltaTipoOrigem());
					break;
			}
					
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Marca todas as abas como nao selecionadas.
	 */
	private void initFlagAbas() {
		this.setRenderSumario(false);
		this.setRenderReceituario(false);
		this.setRenderPedidosExames(false);
		this.setRenderLaudoSUS(false);
	}
	
	public String voltar() {
		return PAGE_PRESCRICAOMEDICA_MANTER_DIAGNOSTICOS_PACIENTE;
	}

	/**
	 * @param currentTabIndex
	 *            the currentTabIndex to set
	 */
	public void setCurrentTabIndex(Integer currentTabIndex) {
		this.currentTabIndex = currentTabIndex;
	}

	/**
	 * @return the currentTabIndex
	 */
	public Integer getCurrentTabIndex() {
		return currentTabIndex;
	}

	/**
	 * @param currentSlider
	 *            the currentSlider to set
	 */
	public void setCurrentSlider(Integer currentSlider) {
		this.currentSlider = currentSlider;
	}

	/**
	 * @return the currentSlider
	 */
	public Integer getCurrentSlider() {
		return currentSlider;
	}

	public Boolean getRenderSumario() {
		return renderSumario;
	}

	public void setRenderSumario(Boolean renderSumario) {
		this.renderSumario = renderSumario;
	}

	public Boolean getRenderReceituario() {
		return renderReceituario;
	}

	public void setRenderReceituario(Boolean renderReceituario) {
		this.renderReceituario = renderReceituario;
	}

	public Boolean getRenderPedidosExames() {
		return renderPedidosExames;
	}

	public void setRenderPedidosExames(Boolean renderPedidosExames) {
		this.renderPedidosExames = renderPedidosExames;
	}

	public Boolean getRenderLaudoSUS() {
		return renderLaudoSUS;
	}

	public void setRenderLaudoSUS(Boolean renderLaudoSUS) {
		this.renderLaudoSUS = renderLaudoSUS;
	}

	/**
	 * @param seqp
	 *            the seqp to set
	 */
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	/**
	 * @return the seqp
	 */
	public Short getSeqp() {
		return seqp;
	}

	/**
	 * @param apaSeq
	 *            the apaSeq to set
	 */
	public void setApaSeq(Integer apaSeq) {
		this.apaSeq = apaSeq;
	}

	/**
	 * @return the apaSeq
	 */
	public Integer getApaSeq() {
		return apaSeq;
	}

	/**
	 * @param atdSeq
	 *            the atdSeq to set
	 */
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	/**
	 * @return the atdSeq
	 */
	public Integer getAtdSeq() {
		return atdSeq;
	}

	/**
	 * @param altaSumario
	 *            the altaSumario to set
	 */
	public void setAltaSumario(MpmAltaSumario altaSumario) {
		this.altaSumario = altaSumario;
	}

	/**
	 * @return the altaSumario
	 */
	public MpmAltaSumario getAltaSumario() {
		return altaSumario;
	}

	/**
	 * @param altaTipoOrigem
	 *            the altaTipoOrigem to set
	 */
	public void setAltaTipoOrigem(String altaTipoOrigem) {
		this.altaTipoOrigem = altaTipoOrigem;
	}

	/**
	 * @return the altaTipoOrigem
	 */
	public String getAltaTipoOrigem() {
		return altaTipoOrigem;
	}

	/**
	 * @param mostrarPedidosExames
	 *            the mostrarPedidosExames to set
	 */
	public void setMostrarPedidosExames(Boolean mostrarPedidosExames) {
		this.mostrarPedidosExames = mostrarPedidosExames;
	}

	/**
	 * @return the mostrarPedidosExames
	 */
	public Boolean getMostrarPedidosExames() {
		return mostrarPedidosExames;
	}

	/**
	 * @param mostrarLaudoSUS
	 *            the mostrarLaudoSUS to set
	 */
	public void setMostrarLaudoSUS(Boolean mostrarLaudoSUS) {
		this.mostrarLaudoSUS = mostrarLaudoSUS;
	}

	/**
	 * @return the mostrarLaudoSUS
	 */
	public Boolean getMostrarLaudoSUS() {
		return mostrarLaudoSUS;
	}

	public Boolean getMostrarReceitas() {
		return mostrarReceitas;
	}

	public void setMostrarReceitas(Boolean mostrarReceitas) {
		this.mostrarReceitas = mostrarReceitas;
	}
	
	
	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}


}
