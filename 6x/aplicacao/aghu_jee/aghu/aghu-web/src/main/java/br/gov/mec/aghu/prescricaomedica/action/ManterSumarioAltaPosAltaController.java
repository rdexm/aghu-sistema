package br.gov.mec.aghu.prescricaomedica.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.action.DisponibilidadeHorariosPaginatorController;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.exames.solicitacao.action.SolicitacaoExameController;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.util.AltaCadastradaVOComparator;
import br.gov.mec.aghu.prescricaomedica.util.AltaNaoCadastradoVOComparator;
import br.gov.mec.aghu.prescricaomedica.vo.AltaCadastradaVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaPrincReceitasVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaRecomendacaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaPosAltaMotivoVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaPosAltaVO;


@SuppressWarnings("PMD.AghuTooManyMethods")
public class ManterSumarioAltaPosAltaController extends ActionController {

	

	private static final String PAGE_PRESCRICAOMEDICA_MANTER_SUMARIO_ALTA = "prescricaomedica-manterSumarioAlta";

	/**
	 * 
	 */
	private static final long serialVersionUID = -1913940914114857315L;

	@Inject
	private ListaPacientesInternadosController listaPacientesInternadosController;

	@Inject
	private SolicitacaoExameController solicitacaoExameController;

	@Inject
	private DisponibilidadeHorariosPaginatorController disponibilidadeHorariosPaginatorController;
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private Boolean focoCabecalho;
	private Boolean focoConsultaExames;

	private final String PAGE_DISPONIBILIDADE_HORARIOS_CONSULTA = "ambulatorio-disponibilidadeHorarios";
	private final String PAGE_CADASTRO_RECOMENDACOES = "prescricaomedica-recomendacoesAltaPorUsuarioList";
	private static final Log LOG = LogFactory.getLog(ManterSumarioAltaPosAltaController.class);
	
	private MpmAltaSumario altaSumario;

	// Variaveis para controles genericos (nao especificos de um slider)
	private Integer currentSlider;

	// Slider de Motivo alta
	private String descricaoPosAlta;
	private SumarioAltaPosAltaVO itemEdicaoCirurgias;
	private Integer indiceComboCirurgias;
	private List<SumarioAltaPosAltaMotivoVO> listaComboMotivoAlta;
	private List<SumarioAltaPosAltaMotivoVO> listaComboPlanoAlta;

	private List<SumarioAltaPosAltaMotivoVO> listaAltaMotivo;
	private List<SumarioAltaPosAltaMotivoVO> listaAltaPlano;

	private SumarioAltaPosAltaMotivoVO sumarioAltaPosAltaMotivoVO = new SumarioAltaPosAltaMotivoVO();
	private SumarioAltaPosAltaMotivoVO planoPosAltaVO = new SumarioAltaPosAltaMotivoVO();
	private SumarioAltaPosAltaMotivoVO itemMotivoAltaSelecionado;
	private SumarioAltaPosAltaMotivoVO indiceSelecionadoAltaMotivos;
	private SumarioAltaPosAltaMotivoVO indiceSelecionadoPlanoPosAlta;	

	// private List<SumarioAltaProcedimentosCrgVO> listaGridCirurgias;

	// Slider Plano Pós Alta
	private String complementoPlanoPosAlta;
	private SumarioAltaPosAltaMotivoVO itemPlanoPosAltaSelecionado;

	// slider alta não cadastrada
	private AltaRecomendacaoVO altaNaoCadastradoVO;
	private AltaNaoCadastradoVOComparator comparatorAltaNaoCadastrado;
	private List<AltaRecomendacaoVO> listaAltaNaoCadastrado = new LinkedList<AltaRecomendacaoVO>();
	private AltaRecomendacaoVO itemAltaRecomendacaoSelecionado;
	
	// Slider Alta Cadastrada
	private AltaCadastradaVO altaCadastradaVO;
	private AltaCadastradaVOComparator altaCadastradaComparator;
	private List<AltaCadastradaVO> listaAltasCadastradas = new LinkedList<AltaCadastradaVO>();
	private List<AltaCadastradaVO> listaAltasCadastradasGravadas = new LinkedList<AltaCadastradaVO>();
	private AltaCadastradaVO altaRecomendacaoSelecionado;
	
	
	//slider Alta Itens de Prescricao
	private String descricaoItemPrescricaoMedica;
	private List<ItemPrescricaoMedicaVO> listItemPrescricaoMedicaVO = new LinkedList<ItemPrescricaoMedicaVO>(); 
	private List<ItemPrescricaoMedicaVO> listItemPrescricaoMedicaGravadaVO = new LinkedList<ItemPrescricaoMedicaVO>();
	private ItemPrescricaoMedicaVO itemPrescricaoMedicaSelecionado;
	
	//Medicamentos Prescritos na Alta
	private List<AltaPrincReceitasVO>  listaAltaPrincReceitas;
	private List<AltaPrincReceitasVO>  listaAltaPrincReceitasAux1;
	private List<AltaPrincReceitasVO>  listaAltaPrincReceitasAux2;
	private DualListModel<AltaPrincReceitasVO> altaPrincReceitas;  
	private Integer asuApaAtdSeq;
	private Integer asuApaSeq;
	private Integer atdSeq;
	
	//Slider consultas
	List<AacConsultas> listaConsultas = null;
	private String labelZonaSala;

	private Integer numeroConsulta;

	public List<ItemPrescricaoMedicaVO> getListItemPrescricaoMedicaGravadaVO() {
		return listItemPrescricaoMedicaGravadaVO;
	}

	public void setListItemPrescricaoMedicaGravadaVO(
			List<ItemPrescricaoMedicaVO> listItemPrescricaoMedicaGravadaVO) {
		this.listItemPrescricaoMedicaGravadaVO = listItemPrescricaoMedicaGravadaVO;
	}

	@PostConstruct
	public void init() {
		begin(conversation);
		try {
    		labelZonaSala = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto() + 
    				"/" +  parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_SALA).getVlrTexto();
    		
    	} catch (ApplicationBusinessException e) {
    		apresentarExcecaoNegocio(e);    		
    	}

	}
	
	/**
	 * @param altaSumario
	 * @throws BaseException
	 */
	public void renderPosAlta(MpmAltaSumario altaSumarioAtual) throws BaseException {
		if(this.getCurrentSlider()==null){
			this.setCurrentSlider(0);	
		}
		
		//#46129 - Limpar dados da aba pós alta.
		this.indiceSelecionadoAltaMotivos = null;
		this.indiceSelecionadoPlanoPosAlta = null;
		
		if (this.getAltaSumario() == null || (altaSumarioAtual != null && !this.getAltaSumario().getId().equals(altaSumarioAtual.getId()))) {
			this.setAltaSumario(altaSumarioAtual);
			this.inicializaDados(altaSumarioAtual);
		}else{
			initAltaCadastrada(altaSumarioAtual);
			initAltaNaoCadastrado(altaSumarioAtual);
		}
	}

	/**
	 * Inicializa os dados de todos os sliders.
	 * 
	 * @param id
	 */
	private void inicializaDados(MpmAltaSumario altaSumario)
			throws BaseException {
		// Slider Motivos da Alta
		this.initMotivoAlta();

		// Slider Plano Pos Alta
		this.initPlanoPosAlta();

		// Slider Alta Não cadastrados
		this.initAltaNaoCadastrado(altaSumario);
		
		// Slider Alta Cadastrada
		this.initAltaCadastrada(altaSumario);
		
		//slider AltaItemPrescricao
		this.initAltaItemPrescricao(altaSumario);
		
		//slider MedicamentosPrescritos
		this.obterListaMedicamentos(altaSumario, false);
		
		//slider Consultas
		this.initConsultas(altaSumario);
	}

	private void initConsultas(MpmAltaSumario altaSumario) {
		if(altaSumario != null && altaSumario.getAtendimento() != null ){
			AghAtendimentos atendimentoRetornoProxy = prescricaoMedicaFacade.obterAtendimentoPorSeq(altaSumario.getAtendimento().getSeq());
			if(atendimentoRetornoProxy != null && atendimentoRetornoProxy.getPaciente() != null){
				Integer pacCodigo = atendimentoRetornoProxy.getPaciente().getCodigo();
				this.listaConsultas = ambulatorioFacade.listarConsultasPaciente(0, 99999, null, true, pacCodigo, null, 
						null, null, null, null, null, new Date(), null, false);
			}
		}
	}

	public String novaConsulta(){
		disponibilidadeHorariosPaginatorController.limparPesquisa();
        disponibilidadeHorariosPaginatorController.setMesInicio(getMesAnoAtual());
		return PAGE_DISPONIBILIDADE_HORARIOS_CONSULTA;
	}
	
	public Date getMesAnoAtual(){
		Calendar calendar = DateUtil.getCalendarBy(new Date());
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return DateUtil.truncaData(calendar.getTime());
	}
	public String realizarChamadaSolicitarExame(){
		AghAtendimentos atendimento = aghuFacade.buscaAtendimentoPorNumeroConsulta(this.numeroConsulta);
		if(atendimento != null && atendimento.getSeq() != null){
			solicitacaoExameController.setAtendimentoSeq(atendimento.getSeq());
			solicitacaoExameController.setAtendimento(atendimento);
			solicitacaoExameController.setPaginaChamadora(PAGE_PRESCRICAOMEDICA_MANTER_SUMARIO_ALTA);
			this.numeroConsulta = null;
			return listaPacientesInternadosController.realizarChamadaSolicitarExame(atendimento.getSeq(), true);
		}else {
			this.numeroConsulta = null;
			return null;
		}
	}
	public String getDthrMarcacao(Date dataMarcacao) {
		if(dataMarcacao != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			return sdf.format(dataMarcacao);
		}
		else {
			return "-";
		}
	}

	public String obterDiaSemana(Date dtConsulta){
		String retorno = "";
		if(dtConsulta != null) {
		    Calendar cal = Calendar.getInstance();  
		    cal.setTime(dtConsulta);  
		    int day = cal.get(Calendar.DAY_OF_WEEK);  
		    switch (day) {
			case 1:
				retorno = "Domingo";
				break;
			case 2:
				retorno = "Segunda-Feira";
				break;
			case 3:
				retorno = "Terça-Feira";
				break;
			case 4:
				retorno = "Quarta-Feira";
				break;
			case 5:
				retorno = "Quinta-Feira";
				break;
			case 6:
				retorno = "Sexta-Feira";
				break;
			case 7:
				retorno = "Sábado";
				break;
			default:
				break;
			}
		}
		return retorno;
	}

	/**
     * Obtém o nome do responsável pela marcação da consulta.
     */
	public String obterResponsavelMarcacaoConsulta(Integer consulta) {
		return ambulatorioFacade.obterNomeResponsavelMarcacaoConsulta(consulta);
	}

	public void setSliderPosAlta() {
		LOG.info("currentSlider: " + currentSlider);
	}
	
	
	//###############################################
	// Motivo Alta
	//###############################################

	/**
	 * Inicializa os dados para o slider de Motivo de Alta
	 * 
	 * @param id
	 */
	private void initMotivoAlta() throws ApplicationBusinessException {
		if (this.getAltaSumario() != null) {
			sumarioAltaPosAltaMotivoVO = new SumarioAltaPosAltaMotivoVO();
			indiceSelecionadoAltaMotivos = null;
			this.loadListasMotivoAlta();
		}
	}

	/**
	 * Carrega as listas do tipo  <code>SumarioAltaPosAltaMotivoVO</code>
	 * para a combo de motivo e itens.
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void loadListasMotivoAlta() throws ApplicationBusinessException {
		this.listaComboMotivoAlta = prescricaoMedicaFacade.listaMotivoAltaMedica(this.getAltaSumario());
		this.listaAltaMotivo = prescricaoMedicaFacade.buscaAltaMotivo(this.getAltaSumario());
	}
	
	public void gravarMotivoAlta() {
		try {
			this.prescricaoMedicaFacade.gravarAltaMotivo(this.sumarioAltaPosAltaMotivoVO.getModelMotivoAlta());
			this.initMotivoAlta();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_POSALTA_001");
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Método que altera as informações 
	 * de <code> MpmAltaMotivo </code> 
	 *
	 * @param altaMotivo
	 * @throws BaseException
	 */
	public void alterarMotivoAlta() {
		this.gravarMotivoAlta();
	}

	/**
	 * Cancela uma ação na tela de Motivo 
	 * da Alta e inicializa a tela. 
	 *
	 * @param altaMotivo
	 * @throws BaseException
	 */
	public void cancelarMotivoAlta() throws ApplicationBusinessException {
		try {
			this.initMotivoAlta();
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Exclui um <code> MpmAltaMotivo </code>
	 * do banco.
	 * 
	 * @param vo
	 */
	public void excluirMotivoAlta() {
		try {
			this.prescricaoMedicaFacade.removerAltaMotivo(getItemMotivoAltaSelecionado().getModelMotivoAlta());
			this.initMotivoAlta();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_POSALTA_002");
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Pega um <code>SumarioAltaPosAltaMotivoVO</code>
	 * da lista na tela para edição 
	 * 
	 * @param vo
	 */
	public void editarMotivoAlta() {
		this.sumarioAltaPosAltaMotivoVO = getItemMotivoAltaSelecionado();
	}

	/**
	 * Este método é executado quando a 
	 * combo de motivos realiza a ação de onchange
	 * na tela
	 * 
	 * @param event
	 */
	public void changeComboMotivoAlta() {
		//Se o texto Selecione foi selecionado, não entra nas opções abaixo e marca 
		//o seleciona na tela. Antes desta validação ocorria o erro na issue #1080
		this.sumarioAltaPosAltaMotivoVO = getIndiceSelecionadoAltaMotivos();
		if(!this.sumarioAltaPosAltaMotivoVO.getExigeComplemento()){
			this.gravarMotivoAlta();
		} else {
			this.sumarioAltaPosAltaMotivoVO.setExigeComplemento(Boolean.TRUE);
			this.sumarioAltaPosAltaMotivoVO.setIdItem(null);
			this.sumarioAltaPosAltaMotivoVO.setComplemento(null);
			this.sumarioAltaPosAltaMotivoVO.setDescricao("Selecione");
		}			
	}

	//###############################################
	// Plano Pos Alta
	//###############################################

	private void initPlanoPosAlta() throws ApplicationBusinessException {
		if (this.getAltaSumario() != null) {
			this.planoPosAltaVO = new SumarioAltaPosAltaMotivoVO();			
			this.loadListasPlanoAlta();
		}
	}
	
	private void loadListasPlanoAlta() throws ApplicationBusinessException {
		this.listaComboPlanoAlta = prescricaoMedicaFacade.listaPlanoPosAlta(this.getAltaSumario());
		this.listaAltaPlano = prescricaoMedicaFacade.buscaAltaPlano(this.getAltaSumario());
	}
	
	public void changeComboPlanoPosAlta() throws ApplicationBusinessException {
		this.planoPosAltaVO = this.getIndiceSelecionadoPlanoPosAlta();
		if (!this.planoPosAltaVO.getExigeComplemento()) {
			this.gravarPlanoPosAlta();
		} else {
			this.planoPosAltaVO.setExigeComplemento(Boolean.TRUE);
			this.planoPosAltaVO.setIdItem(null);						
			this.planoPosAltaVO.setComplemento(null);						
		}
	}
	
	public void gravarPlanoPosAlta() {
		try {
			this.prescricaoMedicaFacade.gravarAltaPlano(this.planoPosAltaVO.getModelPlanoPosAlta());
			this.initPlanoPosAlta();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_POSALTA_003");
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public void cancelarPlanoPosAlta() {
		try {
			this.initPlanoPosAlta();
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public void atualizarPlanoPosAlta() {
		this.gravarPlanoPosAlta();
	}
	
	public void editarPlanoPosAlta() {
		this.planoPosAltaVO.setExigeComplemento(true);
		this.planoPosAltaVO = this.getItemPlanoPosAltaSelecionado();
	}
	
	public void excluirPlanoPosAlta() {
		try {
			this.prescricaoMedicaFacade.removerAltaPlano(this.getItemPlanoPosAltaSelecionado().getModelPlanoPosAlta());
			this.initPlanoPosAlta();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_POSALTA_004");
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	
	//###############################################
	// Recomendacoes na Alta Cadastrada
	//###############################################

	/**
	 * Inicializa os dados de Alta Cadastrada
	 * 
	 * @param id
	 */
	public void initAltaCadastrada(MpmAltaSumario altaSumario) throws BaseException {
		this.setAltaCadastradaVO(new AltaCadastradaVO());
		this.altaCadastradaComparator = new AltaCadastradaVOComparator();

		// carregar a lista
		this.setListaAltaCadastrada(this.prescricaoMedicaFacade.listarAltaCadastrada(altaSumario));
		this.setListaAltasCadastradasGravadas(this.prescricaoMedicaFacade.listarAltaCadastradaGravada(altaSumario));
		this.obterListaMedicamentos(altaSumario, false);

		// ordenar
		Collections.sort(this.listaAltasCadastradas,this.altaCadastradaComparator);
		Collections.sort(this.listaAltasCadastradasGravadas,this.altaCadastradaComparator);
	}
	
	/**
	 * Método que grava um alta cadastrada
	 * 
	 */
	public void gravarAltasCadastradasSelecionadas() {
		try {
			this.prescricaoMedicaFacade.gravarAltasCadastradasSelecionadas(this
					.getListaAltaCadastrada(), altaSumario);
			this.initAltaCadastrada(altaSumario);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_POSALTA_005");
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Método que inativa as altas cadastradas
	 * 
	 */
	public void removerAltasCadastradasSelecionadas() {
		try {
			this.prescricaoMedicaFacade.inativarAltaRecomendacaoCadastrada(altaSumario, getAltaRecomendacaoSelecionado().getAltaRecomendacaoId());
			this.initAltaCadastrada(altaSumario);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_POSALTA_006");
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	
	//###############################################
	// Recomendacao de Alta Nao Cadastrada
	//###############################################
	
	/**
	 * Inicializa os dados de Alta não Cadastrada
	 * 
	 * @param id
	 */
	public void initAltaNaoCadastrado(MpmAltaSumario altaSumario)throws BaseException {
		this.setAltaNaoCadastradoVO(new AltaRecomendacaoVO());
		this.comparatorAltaNaoCadastrado = new AltaNaoCadastradoVOComparator();
		this.altaNaoCadastradoVO.setAltaSumario(altaSumario);
		// carregar a lista
		this.setListaAltaNaoCadastrado(this.prescricaoMedicaFacade.listarAltaRecomendacao(altaSumario.getId()));
		// ordenar
		Collections.sort(this.listaAltaNaoCadastrado,this.comparatorAltaNaoCadastrado);
	}
	
	//###############################################
	// Recomentacoes na Alta Itens de Prescricao
	//###############################################
	
	/**
	 * Este método inicializa a aba
	 * de Alta Itens Precrição
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumario
	 * @throws BaseException 
	 */
	private void initAltaItemPrescricao(MpmAltaSumario altaSumario) throws BaseException {
		try {
			MpmPrescricaoMedica prescricaoMedica = this.prescricaoMedicaFacade.
				obterUltimaPrescricaoAtendimento(altaSumario.getAtendimento().getSeq(),	new Date(), new Date());
			
			if (prescricaoMedica != null) {
				//carrega a lista
				loadListaAltaItemPrescricao(altaSumario);
			}
			
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Grava elementos da lista de
	 * Item Prescriao
	 * @throws BaseException 
	 * 
	 */
	public void gravarAltaItemPrescricao() throws BaseException {
		try {
			List<ItemPrescricaoMedicaVO> listItemPrescricaoMedicaInsert = new ArrayList<ItemPrescricaoMedicaVO>();
			for(ItemPrescricaoMedicaVO item : listItemPrescricaoMedicaVO) {
				if(item.getMarcado()) {
					listItemPrescricaoMedicaInsert.add(item);
					item.setMarcado(Boolean.FALSE);
				}
			}
			this.prescricaoMedicaFacade.gravarRecomendacaoPlanoPosAltaPrescricaoMedica(altaSumario, listItemPrescricaoMedicaInsert);
			this.initAltaItemPrescricao(altaSumario);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_POSALTA_007");
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Atualiza um item de prescrição
	 * @param itemPrescricaoMedicaVO
	 */
	public void atualizarAltaItemPrescricao() {
		try {
			getItemPrescricaoMedicaSelecionado().setDescricao(descricaoItemPrescricaoMedica);
			this.prescricaoMedicaFacade.atualizarAltaItemPrescricao(altaSumario, getItemPrescricaoMedicaSelecionado());
			this.initAltaItemPrescricao(altaSumario);
			getItemPrescricaoMedicaSelecionado().setEmEdicao(Boolean.FALSE);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_POSALTA_008");
		} catch(BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Método que inativa um item de prescrição
	 * 
	 */
	public void removerAltaItemPrescricao() {
		try {
			listItemPrescricaoMedicaGravadaVO.remove(getItemPrescricaoMedicaSelecionado());
			getItemPrescricaoMedicaSelecionado().setMarcado(Boolean.FALSE);
			this.prescricaoMedicaFacade.inativarAltaItemPrescricao(altaSumario, getItemPrescricaoMedicaSelecionado());
			this.initAltaItemPrescricao(altaSumario);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_POSALTA_009");
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	
	/**
	 * Edita um item prescricao cadastrado.
	 * 
	 * @param item
	 * @throws BaseException
	 */
	public void editarAltaItemPrescricao() throws BaseException {
		this.descricaoItemPrescricaoMedica = getItemPrescricaoMedicaSelecionado().getDescricaoDB();
		for(ItemPrescricaoMedicaVO itemPrescricao: this.listItemPrescricaoMedicaGravadaVO){
			itemPrescricao.setEmEdicao(Boolean.FALSE);
		}
		getItemPrescricaoMedicaSelecionado().setEmEdicao(Boolean.TRUE);
	}
	
	/**
	 * Atualiza a edicao de um item prescricao cadastrado.
	 * 
	 * @param item
	 */
	public void atualizarDescricaoItemPrescricaoMedica(ItemPrescricaoMedicaVO item) {
		for(ItemPrescricaoMedicaVO itemVO: this.listItemPrescricaoMedicaGravadaVO){
			if(itemVO == item) { //Compara referência (pois pode haver mais de uma objeto com mesmo ID)
				itemVO.setDescricao(this.descricaoItemPrescricaoMedica.toUpperCase());
				itemVO.setEmEdicao(Boolean.FALSE);
			}
		}
		
		this.descricaoItemPrescricaoMedica = null;
	}
	
	/**
	 * Cancela a edicao de um item prescricao cadastrado.
	 * 
	 * @param item
	 */
	public void cancelarDescricaoItemPrescricaoMedica() {
		getItemPrescricaoMedicaSelecionado().setEmEdicao(Boolean.FALSE);
		this.descricaoItemPrescricaoMedica = null;
	}
	
	
	/**
	 * Carrega a lista de Item Prescrição
	 * 
	 * @param altaSumario
	 * @throws BaseException
	 */
	private void loadListaAltaItemPrescricao(MpmAltaSumario altaSumario) throws BaseException {
		this.listItemPrescricaoMedicaVO = this.prescricaoMedicaFacade.buscaItensRecomendacaoPlanoPosAltaPrescricaoMedica(altaSumario);
		this.listItemPrescricaoMedicaGravadaVO = this.prescricaoMedicaFacade.buscaItensPrescricaoMedicaMarcado(altaSumario);
	}

	/**
	 * Método que grava um alta não cadastrada
	 * 
	 */
	public void gravarAltaNaoCadastrado() throws BaseException {
		try {
			this.prescricaoMedicaFacade.gravarAltaNaoCadastrado(this.altaNaoCadastradoVO);
			this.initAltaNaoCadastrado(this.altaNaoCadastradoVO .getAltaSumario());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_POSALTA_010");
        } catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método que edita uma alta não cadastrada
	 * 
	 * @param vo
	 */
	public void editarAltaNaoCadastrado() throws ApplicationBusinessException {
		getItemAltaRecomendacaoSelecionado().setEmEdicao(Boolean.TRUE);
		this.altaNaoCadastradoVO = getItemAltaRecomendacaoSelecionado();
	}
	
	/**
	 * Método que edita uma alta cadastrada
	 * 
	 * @param vo
	 */
	public void editarAltaCadastrada()
			throws ApplicationBusinessException {
		for (AltaCadastradaVO altaCadastradaVO : listaAltasCadastradasGravadas) {
			altaCadastradaVO.setEmEdicao(Boolean.FALSE);
		}
		this.getAltaRecomendacaoSelecionado().setEmEdicao(Boolean.TRUE);
		this.altaCadastradaVO = this.getAltaRecomendacaoSelecionado();
	}

	/**
	 * Método que Cancela uma edição de alta não cadastrada.
	 * 
	 */
	public void cancelarAltaNaoCadastrado() throws BaseException {
		this.initAltaNaoCadastrado(this.altaNaoCadastradoVO.getAltaSumario());
	}
	
	/**
	 * Método que Cancela uma edição de alta cadastrada.
	 * 
	 */
	public void cancelarAltaCadastrada() throws BaseException {
		this.initAltaCadastrada(this.altaCadastradaVO.getAltaSumario());
	}

	/**
	 * Método que Atualiza uma alta não cadastrada
	 * 
	 */
	public void atualizarAltaNaoCadastrado() {
		try {
			this.prescricaoMedicaFacade.atualizarAltaNaoCadastrado(this.altaNaoCadastradoVO);
			this.initAltaNaoCadastrado(this.altaNaoCadastradoVO.getAltaSumario());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_POSALTA_011");
		} catch(BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Método que Atualiza uma alta cadastrada
	 * 
	 */
	public void atualizarAltaCadastrada() {
		try {
			this.prescricaoMedicaFacade.atualizarAltaCadastrada(this.altaCadastradaVO);
			this.initAltaCadastrada(this.altaCadastradaVO.getAltaSumario());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_POSALTA_012");
		} catch(BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public String cadastroRecomendacoes() {
		return PAGE_CADASTRO_RECOMENDACOES;
	}

	/**
	 * Método que exclui alta não cadastrada
	 * 
	 * @param vo
	 */
	public void excluirAltaNaoCadastradao() {
		try {
			this.prescricaoMedicaFacade.excluirAltaNaoCadastrado(getItemAltaRecomendacaoSelecionado());
			this.initAltaNaoCadastrado(this.altaNaoCadastradoVO.getAltaSumario());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_POSALTA_013");
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public DualListModel<AltaPrincReceitasVO> obterListaMedicamentos(MpmAltaSumario altaSumario, boolean carregarMedicamentosPrescristosPeloReceituario){
		
		this.listaAltaPrincReceitas = new ArrayList<AltaPrincReceitasVO>();
		this.listaAltaPrincReceitasAux1 = new ArrayList<AltaPrincReceitasVO>();
		this.listaAltaPrincReceitasAux2 = new ArrayList<AltaPrincReceitasVO>();
		
		if(carregarMedicamentosPrescristosPeloReceituario){
			this.prescricaoMedicaFacade.validaListaMedicamentosPrescritosAlta(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), 
					altaSumario.getId().getSeqp(), atdSeq);
		}
		
 		this.listaAltaPrincReceitas = this.prescricaoMedicaFacade.obterListaMedicamentosPrescritosAlta(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq());
		
		if (!this.listaAltaPrincReceitas.isEmpty()) {
			for (AltaPrincReceitasVO medicamentos : this.listaAltaPrincReceitas) {
				if (medicamentos.getIndCarga().equalsIgnoreCase("S")) {
					this.listaAltaPrincReceitasAux1.add(medicamentos);
				} else {
					this.listaAltaPrincReceitasAux2.add(medicamentos);
				}
			}
		}
 		this.altaPrincReceitas = new DualListModel<AltaPrincReceitasVO>(this.listaAltaPrincReceitasAux1 , this.listaAltaPrincReceitasAux2); 
		
		return this.altaPrincReceitas;
	}
	
	public void gravarMpmAlta() throws BaseException{
		try{
			if(this.altaPrincReceitas.getSource().isEmpty() && this.altaPrincReceitas.getTarget().isEmpty()){
				apresentarMsgNegocio(Severity.WARN, "MENSAGEM_ERRO_MPM", "Medicamentos Prescritos na Alta");
			
			}else{
				
				this.listaAltaPrincReceitasAux1 = new ArrayList<AltaPrincReceitasVO>();
				this.listaAltaPrincReceitasAux2 = new ArrayList<AltaPrincReceitasVO>();
				this.listaAltaPrincReceitasAux1.addAll(this.altaPrincReceitas.getSource());
				this.listaAltaPrincReceitasAux2.addAll(this.altaPrincReceitas.getTarget());
				
				this.prescricaoMedicaFacade.persistirMpmAlta(this.listaAltaPrincReceitasAux1, this.listaAltaPrincReceitasAux2, this.altaSumario);
				this.altaPrincReceitas = new DualListModel<AltaPrincReceitasVO>();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_MPM", "Medicamentos Prescritos na Alta");
				
				this.obterListaMedicamentos(this.getAltaSumario(), false);
			}
				
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public void onTransfer(TransferEvent event) {
		event.getItems();
      
    } 
	
	public void limparParametros(){
		this.altaPrincReceitas = new DualListModel<AltaPrincReceitasVO>();
		this.setCurrentSlider(null);
	}
	
	public void desabilitarFocoCabecalho() {
		this.focoCabecalho = false;
	}
 
	public void desabilitarFocoConsultaExames() {
		this.focoConsultaExames = false;
	}
	
	//###############################################
	// Outros Metodos
	//###############################################

	public Integer getCurrentSlider() {return this.currentSlider;}
	public void setCurrentSlider(Integer currentSlider) {this.currentSlider = currentSlider;}
	public String getDescricaoPosAlta() {return this.descricaoPosAlta;}
	public void setDescricaoPosAlta(String descricaoPosAlta) {this.descricaoPosAlta = descricaoPosAlta;}
	public SumarioAltaPosAltaVO getItemEdicaoCirurgias() {return this.itemEdicaoCirurgias;}
	public void setItemEdicaoCirurgias(SumarioAltaPosAltaVO itemEdicaoCirurgias) {this.itemEdicaoCirurgias = itemEdicaoCirurgias;}
	public Integer getIndiceComboCirurgias() {return this.indiceComboCirurgias;}
	public void setIndiceComboCirurgias(Integer indiceComboCirurgias) {this.indiceComboCirurgias = indiceComboCirurgias;}
	public List<SumarioAltaPosAltaMotivoVO> getListaComboMotivoAlta() {return this.listaComboMotivoAlta;}
	public void setListaComboMotivoAlta(List<SumarioAltaPosAltaMotivoVO> listaComboMotivoAlta) {this.listaComboMotivoAlta = listaComboMotivoAlta;}
	public void setComplementoPlanoPosAlta(String complementoPlanoPosAlta) {this.complementoPlanoPosAlta = complementoPlanoPosAlta;}
	public String getComplementoPlanoPosAlta() {return this.complementoPlanoPosAlta;}
	public List<AltaRecomendacaoVO> getListaAltaNaoCadastrado() {return listaAltaNaoCadastrado;}
	public void setListaAltaNaoCadastrado(List<AltaRecomendacaoVO> listaAltaNaoCadastrado) {this.listaAltaNaoCadastrado = listaAltaNaoCadastrado;}
	public AltaRecomendacaoVO getAltaNaoCadastradoVO() {return altaNaoCadastradoVO;}
	public void setAltaNaoCadastradoVO(AltaRecomendacaoVO altaNaoCadastradoVO) {this.altaNaoCadastradoVO = altaNaoCadastradoVO;}
	public void setAltaSumario(MpmAltaSumario altaSumario) {this.altaSumario = altaSumario;}
	public MpmAltaSumario getAltaSumario() {return altaSumario;}
	public void setListaComboPlanoAlta(List<SumarioAltaPosAltaMotivoVO> listaComboPlanoAlta) {this.listaComboPlanoAlta = listaComboPlanoAlta;}
	public List<SumarioAltaPosAltaMotivoVO> getListaComboPlanoAlta() {return listaComboPlanoAlta;}
	public void setListaAltaPlano(List<SumarioAltaPosAltaMotivoVO> listaAltaPlano) {this.listaAltaPlano = listaAltaPlano;}
	public List<SumarioAltaPosAltaMotivoVO> getListaAltaPlano() {return listaAltaPlano;}
	public void setListaAltaMotivo(List<SumarioAltaPosAltaMotivoVO> listaAltaMotivo) {this.listaAltaMotivo = listaAltaMotivo;}
	public List<SumarioAltaPosAltaMotivoVO> getListaAltaMotivo() {return listaAltaMotivo;}
	public void setSumarioAltaPosAltaMotivoVO(SumarioAltaPosAltaMotivoVO sumarioAltaPosAltaMotivoVO) {this.sumarioAltaPosAltaMotivoVO = sumarioAltaPosAltaMotivoVO;}
	public SumarioAltaPosAltaMotivoVO getSumarioAltaPosAltaMotivoVO() {return sumarioAltaPosAltaMotivoVO;}
	public void setPlanoPosAltaVO(SumarioAltaPosAltaMotivoVO planoPosAltaVO) {this.planoPosAltaVO = planoPosAltaVO;}
	public SumarioAltaPosAltaMotivoVO getPlanoPosAltaVO() {return planoPosAltaVO;}
	public List<AltaCadastradaVO> getListaAltaCadastrada() {return listaAltasCadastradas;}
	public void setListaAltaCadastrada(List<AltaCadastradaVO> listaAltasCadastradas) {this.listaAltasCadastradas = listaAltasCadastradas;}
	public AltaCadastradaVO getAltaCadastradaVO() {return altaCadastradaVO;}
	public void setAltaCadastradaVO(AltaCadastradaVO altaCadastradaVO) {this.altaCadastradaVO = altaCadastradaVO;}
	public List<ItemPrescricaoMedicaVO> getListItemPrescricaoMedicaVO() {return listItemPrescricaoMedicaVO;}
	public void setListItemPrescricaoMedicaVO(List<ItemPrescricaoMedicaVO> listItemPrescricaoMedicaVO) {this.listItemPrescricaoMedicaVO = listItemPrescricaoMedicaVO;}
	public List<AltaCadastradaVO> getListaAltasCadastradasGravadas() {return listaAltasCadastradasGravadas;}
	public void setListaAltasCadastradasGravadas(List<AltaCadastradaVO> listaAltasCadastradasGravadas) {this.listaAltasCadastradasGravadas = listaAltasCadastradasGravadas;}
	public String getDescricaoItemPrescricaoMedica() {return descricaoItemPrescricaoMedica;}
	public void setDescricaoItemPrescricaoMedica(String descricaoItemPrescricaoMedica) {this.descricaoItemPrescricaoMedica = descricaoItemPrescricaoMedica;}
	public SumarioAltaPosAltaMotivoVO getItemMotivoAltaSelecionado() {return itemMotivoAltaSelecionado;}
	public void setItemMotivoAltaSelecionado(SumarioAltaPosAltaMotivoVO itemMotivoAltaSelecionado) {this.itemMotivoAltaSelecionado = itemMotivoAltaSelecionado;}
	public SumarioAltaPosAltaMotivoVO getItemPlanoPosAltaSelecionado() {return itemPlanoPosAltaSelecionado;}
	public void setItemPlanoPosAltaSelecionado(SumarioAltaPosAltaMotivoVO itemPlanoPosAltaSelecionado) {this.itemPlanoPosAltaSelecionado = itemPlanoPosAltaSelecionado;}
	public SumarioAltaPosAltaMotivoVO getIndiceSelecionadoAltaMotivos() {return indiceSelecionadoAltaMotivos;}
	public void setIndiceSelecionadoAltaMotivos(SumarioAltaPosAltaMotivoVO indiceSelecionadoAltaMotivos) {this.indiceSelecionadoAltaMotivos = indiceSelecionadoAltaMotivos;}
	public SumarioAltaPosAltaMotivoVO getIndiceSelecionadoPlanoPosAlta() {return indiceSelecionadoPlanoPosAlta;}
	public void setIndiceSelecionadoPlanoPosAlta(SumarioAltaPosAltaMotivoVO indiceSelecionadoPlanoPosAlta) {this.indiceSelecionadoPlanoPosAlta = indiceSelecionadoPlanoPosAlta;}
	public AltaCadastradaVO getAltaRecomendacaoSelecionado() {return altaRecomendacaoSelecionado;}
	public void setAltaRecomendacaoSelecionado(AltaCadastradaVO altaRecomendacaoSelecionado) {this.altaRecomendacaoSelecionado = altaRecomendacaoSelecionado;}
	public ItemPrescricaoMedicaVO getItemPrescricaoMedicaSelecionado() {return itemPrescricaoMedicaSelecionado;}
	public void setItemPrescricaoMedicaSelecionado(ItemPrescricaoMedicaVO itemPrescricaoMedicaSelecionado) {this.itemPrescricaoMedicaSelecionado = itemPrescricaoMedicaSelecionado;}
	public AltaRecomendacaoVO getItemAltaRecomendacaoSelecionado() {return itemAltaRecomendacaoSelecionado;}
	public void setItemAltaRecomendacaoSelecionado(AltaRecomendacaoVO itemAltaRecomendacaoSelecionado) {this.itemAltaRecomendacaoSelecionado = itemAltaRecomendacaoSelecionado;}
	public List<AltaPrincReceitasVO> getListaAltaPrincReceitas() {return listaAltaPrincReceitas;}
	public void setListaAltaPrincReceitas(List<AltaPrincReceitasVO> listaAltaPrincReceitas) {this.listaAltaPrincReceitas = listaAltaPrincReceitas;}
	public DualListModel<AltaPrincReceitasVO> getAltaPrincReceitas() {return altaPrincReceitas;}
	public void setAltaPrincReceitas(DualListModel<AltaPrincReceitasVO> altaPrincReceitas) {this.altaPrincReceitas = altaPrincReceitas;}
	public List<AltaPrincReceitasVO> getListaAltaPrincReceitasAux1() {return listaAltaPrincReceitasAux1;}
	public void setListaAltaPrincReceitasAux1(List<AltaPrincReceitasVO> listaAltaPrincReceitasAux1) {this.listaAltaPrincReceitasAux1 = listaAltaPrincReceitasAux1;}
	public List<AltaPrincReceitasVO> getListaAltaPrincReceitasAux2() {return listaAltaPrincReceitasAux2;}
	public void setListaAltaPrincReceitasAux2(List<AltaPrincReceitasVO> listaAltaPrincReceitasAux2) {this.listaAltaPrincReceitasAux2 = listaAltaPrincReceitasAux2;}
	public Integer getAsuApaAtdSeq() {return asuApaAtdSeq;}
	public void setAsuApaAtdSeq(Integer asuApaAtdSeq) {this.asuApaAtdSeq = asuApaAtdSeq;}
	public Integer getAsuApaSeq() {return asuApaSeq;}
	public void setAsuApaSeq(Integer asuApaSeq) {this.asuApaSeq = asuApaSeq;}
	public List<AacConsultas> getListaConsultas() {return listaConsultas;}
	public void setListaConsultas(List<AacConsultas> listaConsultas) {this.listaConsultas = listaConsultas;}
	public String getLabelZonaSala() {return labelZonaSala;}
	public void setLabelZonaSala(String labelZonaSala) {this.labelZonaSala = labelZonaSala;}
	public Integer getNumeroConsulta() {return numeroConsulta;}
	public void setNumeroConsulta(Integer numeroConsulta) {this.numeroConsulta = numeroConsulta;}
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Boolean getFocoCabecalho() {
		return focoCabecalho;
	}

	public void setFocoCabecalho(Boolean focoCabecalho) {
		this.focoCabecalho = focoCabecalho;
	}

	public Boolean getFocoConsultaExames() {
		return focoConsultaExames;
	}

	public void setFocoConsultaExames(Boolean focoConsultaExames) {
		this.focoConsultaExames = focoConsultaExames;
	}
}