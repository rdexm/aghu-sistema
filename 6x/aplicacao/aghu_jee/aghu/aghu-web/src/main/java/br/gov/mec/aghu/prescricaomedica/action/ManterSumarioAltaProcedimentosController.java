package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOutrosFarmacos;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.VMpmMdtoPrescNew;
import br.gov.mec.aghu.model.VMpmOtrProcedSum;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.util.AfaMedicamentoPrescricaoVOComparator;
import br.gov.mec.aghu.prescricaomedica.util.AltaSumarioInfoComplVOComparator;
import br.gov.mec.aghu.prescricaomedica.util.SumarioAltaProcedimentosVOComparator;
import br.gov.mec.aghu.prescricaomedica.vo.AfaMedicamentoPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaSumarioInfoComplVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaPrescricaoProcedimentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosConsultoriasVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosCrgListasVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosCrgVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosVO;
import br.gov.mec.aghu.view.VAfaDescrMdto;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
public class ManterSumarioAltaProcedimentosController extends ActionController  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7366388377929425851L;

	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";
	
	private static final Integer NAO_SELECIONADO = -1;

	private static enum Slider {
		CIRURGIAS_REALIZADAS(0), //
		PROCEDIMENTOS(1), //
		OUTROS_PROCEDIMENTOS(2), //
		CONSULTORIAS(3), //
		PRINCIPAIS_FARMACOS(4), //
		INFO_COMPLEMENTARES(5); //

		private final Integer value;

		private Slider(Integer value) {
			this.value = value;
		}

		public static Slider fromValue(Integer value) {
			Slider retorno = null;
			for(Slider slider: Slider.values()) {
				if (slider.getValue().equals(value)) {
					retorno = slider;
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
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;

	// Variaveis para controles genericos (nao especificos de um slider)
	private SumarioAltaProcedimentosVOComparator comparator;
	private Integer currentSlider;

	// Slider de Cirurgias Realizadas
	private Date dataCirurgia;
	private MbcProcedimentoCirurgicos procedimentoCirurgicos;
	private SumarioAltaProcedimentosCrgVO itemCirurgiasSelecionado;
	private SumarioAltaProcedimentosCrgVO itemEdicaoCirurgias;
	private Integer indiceComboCirurgias;
	private List<SumarioAltaProcedimentosCrgVO> listaComboCirurgias;
	private List<SumarioAltaProcedimentosCrgVO> listaGridCirurgias;

	// Slider de Procedimentos
	private Date dataProcedimento;
	private VMpmOtrProcedSum vMpmOtrProcedSum;
	private Integer indiceComboProcedimentos;
	private SumarioAltaPrescricaoProcedimentoVO itemProcedimentosSelecionado;
	private SumarioAltaPrescricaoProcedimentoVO itemEdicaoProcedimentos;
	private List<SumarioAltaPrescricaoProcedimentoVO> listaComboProcedimentos;
	private List<SumarioAltaPrescricaoProcedimentoVO> listaGridProcedimentos;
	private SumarioAltaPrescricaoProcedimentoVO itemSelecionadoComboProcedimentos;
	private SumarioAltaProcedimentosCrgVO itemSelecionadoComboCirurgias;
	private SumarioAltaProcedimentosConsultoriasVO itemSelecionadoComboConsultoria;

	// Slider de Outros Procedimentos
	private Date dataOutroProcedimento;
	private String descricaoOutroProcedimento;
	private SumarioAltaProcedimentosVO itemOutrosProcedimentosSelecionado;
	private SumarioAltaProcedimentosVO itemEdicaoOutrosProcedimentos;
	private List<SumarioAltaProcedimentosVO> listaGridOutrosProcedimentos;

	// Slider de Consultorias
	private Date dataConsultoria;
	private AghEspecialidades especialidade;
	private Integer indiceComboConsultorias;
	private SumarioAltaProcedimentosConsultoriasVO itemConsultoriasSelecionado;
	private SumarioAltaProcedimentosConsultoriasVO itemEdicaoConsultorias;
	private List<SumarioAltaProcedimentosConsultoriasVO> listaComboConsultorias;
	private List<SumarioAltaProcedimentosConsultoriasVO> listaGridConsultorias;

	// Slider de Principais Farmacos
	private AfaMedicamentoPrescricaoVOComparator comparatorPrincipaisFarmacos;
	private AfaMedicamentoPrescricaoVO afaMedicamentoPrescricaoVO;
	private AfaMedicamentoPrescricaoVO listaAfaMedicamentoPrescricaoVO;
	
	/** Lista de todos os Medicamentos */
	private List<AfaMedicamentoPrescricaoVO> listaMdtos = new ArrayList<AfaMedicamentoPrescricaoVO>();
	private List<VMpmMdtoPrescNew> listaVMpmMdtoPrescNewDescricao = new ArrayList<VMpmMdtoPrescNew>();
	private List<VMpmMdtoPrescNew> listaVMpmMdtoPrescNew = new ArrayList<VMpmMdtoPrescNew>();
	
	/** Lista de Medicamentos selecionados pelo usuario */
	private List<AfaMedicamentoPrescricaoVO> listaMdtosSelecionados = new ArrayList<AfaMedicamentoPrescricaoVO>();

	private AfaMedicamentoPrescricaoVO mdtoSelecionadoOrigem;

	private AfaMedicamentoPrescricaoVO mdtoSelecionadoDestino;

	// Slider de Informações complementares
	private AltaSumarioInfoComplVO informacaoComplementarVO = new AltaSumarioInfoComplVO();
	private AltaSumarioInfoComplVOComparator infoComplVOComparator = new AltaSumarioInfoComplVOComparator();
	private List<AltaSumarioInfoComplVO> listaInformacaoComplementar = new LinkedList<AltaSumarioInfoComplVO>();
	private String complementoInformacoesComplementares;

	private Boolean mostrarPanelComboCirurgias;
	private Boolean mostrarPanelOutrosItens;
	
	private Boolean mostrarPanelComboProcedimento;
	private Boolean mostrarPanelOutrosItensProcedimento;
	
	private Boolean mostrarPanelComboConsultoria;
	private Boolean mostrarPanelOutrosItensConsultoria;

	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	/**
	 * @param altaSumario
	 */
	public void renderProcedimentos(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		this.setCurrentSlider(0);
		this.inicializaDados(altaSumario.getId());
	}

	/**
	 * Inicializa os dados de todos os sliders.
	 *
	 * @param id
	 */
	public void inicializaDados(MpmAltaSumarioId id) throws ApplicationBusinessException {
		this.comparator = new SumarioAltaProcedimentosVOComparator();
		this.comparatorPrincipaisFarmacos = new AfaMedicamentoPrescricaoVOComparator();
		this.prepararListasCirurgias(id);
		this.prepararListasProcedimentos(id);
		this.prepararListasOutrosProcedimentos(id);
		this.prepararListasConsultorias(id);
		//		this.prepararListasPrincipaisFarmacos(id);
		this.prepararListasInformacoesComplementares(id);
		this.prepararListaslistaMdtos(id);
	}
	
	public void acaoOutroItem() {
		this.setMostrarPanelOutrosItens(!this.getMostrarPanelOutrosItens());
	}

	public void acaoOutroItemProcedimento() {
		this.setMostrarPanelOutrosItensProcedimento(!this.getMostrarPanelOutrosItensProcedimento());
	}
	
	public void acaoOutroItemConsultoria() {
		this.setMostrarPanelOutrosItensConsultoria(!this.getMostrarPanelOutrosItensConsultoria());
	}
	

	private void limparDadosEdicaoCirurgias(MpmAltaSumarioId id) {
		this.dataCirurgia = null;
		this.procedimentoCirurgicos = null;
		if (this.itemEdicaoCirurgias != null) {
			this.itemEdicaoCirurgias.setEmEdicao(Boolean.FALSE);
		}
		this.setItemEdicaoCirurgias(new SumarioAltaProcedimentosCrgVO(id));
	}

	private void limparDadosEdicaoProcedimentos(MpmAltaSumarioId id) {
		this.dataProcedimento = null;
		this.vMpmOtrProcedSum = null;
		if (this.itemEdicaoProcedimentos != null) {
			this.itemEdicaoProcedimentos.setEmEdicao(Boolean.FALSE);
		}
		this.setItemEdicaoProcedimentos(new SumarioAltaPrescricaoProcedimentoVO(id));
	}

	private void limparDadosEdicaoOutrosProcedimentos(MpmAltaSumarioId id) {
		this.dataOutroProcedimento = null;
		this.descricaoOutroProcedimento = null;
		if (this.itemEdicaoOutrosProcedimentos != null) {
			this.itemEdicaoOutrosProcedimentos.setEmEdicao(Boolean.FALSE);
		}
		this.setItemEdicaoOutrosProcedimentos(new SumarioAltaProcedimentosVO(id));
	}

	private void limparDadosEdicaoConsultorias(MpmAltaSumarioId id) {
		this.dataConsultoria = null;
		this.especialidade = null;
		if (this.itemEdicaoConsultorias != null) {
			this.itemEdicaoConsultorias.setEmEdicao(Boolean.FALSE);
		}
		this.setItemEdicaoConsultorias(new SumarioAltaProcedimentosConsultoriasVO(id));
	}

	private void prepararListasCirurgias(MpmAltaSumarioId id) throws ApplicationBusinessException {
		// Limpar dados de edição
		this.limparDadosEdicaoCirurgias(id);
		
		SumarioAltaProcedimentosCrgListasVO listasComboGridCirurgias = this.prescricaoMedicaFacade.pesquisarCirurgiasRealizadas(id);
		
		// Pegar lista da combo
		this.listaComboCirurgias = listasComboGridCirurgias.getListaComboCirurgias();
		// Pegar lista da grid
		this.listaGridCirurgias = listasComboGridCirurgias.getListaGridCirurgias();
		
		// Ordenar as listas
		Collections.sort(this.listaComboCirurgias, this.comparator);
		Collections.sort(this.listaGridCirurgias, this.comparator);
		
		// Marcar outros itens para exibicao
		boolean hasCirurgias = (this.listaComboCirurgias != null && !this.listaComboCirurgias.isEmpty());
		this.setMostrarPanelOutrosItens( !hasCirurgias );
		this.setMostrarPanelComboCirurgias(hasCirurgias);
		
		// Limpa indicador de indice da combo
		this.indiceComboCirurgias = null;
		// Adiciona indice aos itens da combo
		this.adicionarIndiceCombo(this.listaComboCirurgias);
	}

	private void prepararListasConsultorias(MpmAltaSumarioId id) throws ApplicationBusinessException {
		// Limpar dados de edição
		this.limparDadosEdicaoConsultorias(id);
		// Pegar lista da combo
		this.listaComboConsultorias = this.prescricaoMedicaFacade.pesquisarConsultoriasCombo(id);
		// Pegar lista da grid
		this.listaGridConsultorias = this.prescricaoMedicaFacade.pesquisarConsultoriasGrid(id);
		// Remover da combo os itens presentes na grid
		this.listaComboConsultorias.removeAll(this.listaGridConsultorias);
		// Ordenar as listas
		Collections.sort(this.listaComboConsultorias, this.comparator);
		Collections.sort(this.listaGridConsultorias, this.comparator);
		
		// Marcar outros itens para exibicao
		boolean hasItens = (this.listaComboConsultorias != null && !this.listaComboConsultorias.isEmpty());
		this.setMostrarPanelOutrosItensConsultoria( !hasItens );
		this.setMostrarPanelComboConsultoria(hasItens);
		
		// Limpa indicador de indice da combo
		this.indiceComboConsultorias = null;
		// Adiciona indice aos itens da combo
		this.adicionarIndiceCombo(this.listaComboConsultorias);
	}

	private void prepararListasProcedimentos(MpmAltaSumarioId id) throws ApplicationBusinessException {
		// Limpar dados de edição
		this.limparDadosEdicaoProcedimentos(id);
		// Pegar lista da combo
		this.listaComboProcedimentos = this.prescricaoMedicaFacade.pesquisarPrescricaoProcedimentoCombo(id);
		// Pegar lista da grid
		this.listaGridProcedimentos = this.prescricaoMedicaFacade.pesquisarPrescricaoProcedimentoGrid(id);
		// Remover da combo os itens presentes na grid
		this.listaComboProcedimentos.removeAll(this.listaGridProcedimentos);

		// Popula dados restantes
		this.prescricaoMedicaFacade.populaListaComboPrescricaoProcedimento(this.listaComboProcedimentos, id);

		// Marcar outros itens para exibicao
		boolean hasItens = (this.listaComboProcedimentos != null && !this.listaComboProcedimentos.isEmpty());
		this.setMostrarPanelOutrosItensProcedimento( !hasItens );
		this.setMostrarPanelComboProcedimento(hasItens);

		// Ordenar as listas
		Collections.sort(this.listaComboProcedimentos, this.comparator);
		Collections.sort(this.listaGridProcedimentos, this.comparator);

		// Limpa indicador de indice da combo
		this.setIndiceComboProcedimentos(null);
		// Adiciona indice aos itens da combo
		this.adicionarIndiceCombo(this.listaComboProcedimentos);
	}

	private void prepararListasOutrosProcedimentos(MpmAltaSumarioId id) throws ApplicationBusinessException {
		// 1. Limpar dados de edição
		this.limparDadosEdicaoOutrosProcedimentos(id);
		// 2. Pegar lista da grid
		this.listaGridOutrosProcedimentos = this.prescricaoMedicaFacade.pesquisarPrescricaoOutrosProcedimentoGrid(id);
		// 3. Ordenar a lista da grid
		Collections.sort(this.listaGridOutrosProcedimentos, this.comparator);
	}


	private void prepararListasInformacoesComplementares(MpmAltaSumarioId id) throws ApplicationBusinessException {
		initInformacaoComplementar(id);
	}


	/**
	 * Inicializa os dados de informações 
	 * complementares.
	 * 
	 * @author gfmenezes
	 * 
	 * @param id
	 */
	private void initInformacaoComplementar(MpmAltaSumarioId id) throws ApplicationBusinessException {
		this.setInformacaoComplementarVO(new AltaSumarioInfoComplVO());
		this.informacaoComplementarVO.setRadioInformacoesComplementares(DominioOutrosFarmacos.CADASTRADO);
		this.informacaoComplementarVO.setIdAltaSumario(id);
		this.complementoInformacoesComplementares = prescricaoMedicaFacade.obterComplementoInformacaoComplementar(id);
		
		this.setListaInformacaoComplementar(
				prescricaoMedicaFacade.findListaInformacaoComplementar(id)
		);

		//ordenar a lista
		Collections.sort(this.listaInformacaoComplementar, this.infoComplVOComparator);

		if (this.getListaInformacaoComplementar() != null && !this.getListaInformacaoComplementar().isEmpty()){
			this.informacaoComplementarVO.setIdAltaComplFarmaco(this.getListaInformacaoComplementar().get(0).getIdAltaComplFarmaco());
		}

		this.valueChangeRadioInformacoesComplementares();
	}

	/**
	 * Método responsável por trocar a mostra
	 * de campos na tela de informações complementares 
	 * conforme opção selecionada: cadastrado ou não cadastrado.
	 * 
	 * @author gfmenezes
	 * 
	 */
	public void valueChangeRadioInformacoesComplementares(){
		this.informacaoComplementarVO.setEhListaDescrMdtoAtivos(
				this.informacaoComplementarVO.getRadioInformacoesComplementares() == DominioOutrosFarmacos.CADASTRADO
		);

		this.informacaoComplementarVO.setEhDescricaoMedicamento(
				this.informacaoComplementarVO.getRadioInformacoesComplementares() == DominioOutrosFarmacos.NAO_CADASTRADO
		);

	}

	/**
	 * Método responsável pela adição de uma
	 * informação complementar na lista.
	 * @throws  
	 */
	public void adicionarInformacaoComplementar() {
		try {
			this.prescricaoMedicaFacade.validarInformacoesComplementares(this.complementoInformacoesComplementares, this.informacaoComplementarVO); 
			this.prescricaoMedicaFacade.gravarAltaSumarioInformacaoComplementar(this.complementoInformacoesComplementares, this.informacaoComplementarVO);
			MpmAltaSumarioId id = this.informacaoComplementarVO.getIdAltaSumario();
			this.initInformacaoComplementar(id);
			if(this.informacaoComplementarVO.getIdAltaComplFarmaco() == null) {
				apresentarMsgNegocio(Severity.INFO, "Informacões Complementares salvas com sucesso.");
			}else{
				apresentarMsgNegocio(Severity.INFO, "Informacões Complementares atualizadas com sucesso.");				
			}
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método responsável pela alteração de uma
	 * informação complementar na lista.
	 */
	public void alterarInformacaoComplementar() {
		adicionarInformacaoComplementar();
	}

	/**
	 * Método responsável pelo cancelamento de uma
	 * edição de uma informação complementar na lista.
	 */
	public void cancelarAlteracaoInformacaoComplementar() {
		try {
			MpmAltaSumarioId id = this.getInformacaoComplementarVO().getIdAltaSumario();
			this.initInformacaoComplementar(id);
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método responsável pelo início de uma alteração
	 * de uma informação complementar a partir da lista.
	 * 
	 * @param vo
	 *  
	 */
	public void editarInformacaoComplementar() throws ApplicationBusinessException {
		this.getInformacaoComplementarVO().setEmEdicao(Boolean.TRUE);
		this.informacaoComplementarVO = this.getInformacaoComplementarVO();
		this.valueChangeRadioInformacoesComplementares();
	}

	/**
	 * Método responsável pela exclusão de uma informação 
	 * complementar a partir da lista.
	 * @param vo
	 */
	public void excluirInformacaoComplementar(){
		try {
			this.prescricaoMedicaFacade.removerAltaSumarioInformacaoComplementar(this.getInformacaoComplementarVO());
			this.initInformacaoComplementar(this.informacaoComplementarVO.getIdAltaSumario());
			apresentarMsgNegocio(Severity.INFO, "Informação Complementar excluída com sucesso.");
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	private void adicionarIndiceCombo(List<? extends SumarioAltaProcedimentosVO> lista) {
		for(Integer i = 0; i <= lista.size() - 1; i++) {
			SumarioAltaProcedimentosVO vo = lista.get(i);
			vo.setIndiceCombo(i);
		}
	}

	private SumarioAltaProcedimentosVO obterItemCombo(List<? extends SumarioAltaProcedimentosVO> lista, Integer indice) {
		SumarioAltaProcedimentosVO voSelecionado = null;
		for(SumarioAltaProcedimentosVO vo: lista) {
			if (vo.getIndiceCombo().equals(indice)) {
				voSelecionado = vo;
				break;
			}
		}
		return voSelecionado;
	}

	/**
	 * Método utilizado em caso de retorno a tela após navegação.
	 *
	 * Obs.: Não foi constatada ainda a necessidade deste método para a aba de procedimentos,
	 * porém já fica encaminhado caso ocorra situação semelhante a pesquisa de CID da aba Diagnósticos.
	 *
	 * @param id
	 * @author mtocchetto
	 */
	public void restaurarDados(MpmAltaSumarioId id) {
		Slider slider = Slider.fromValue(this.currentSlider);
		switch (slider) {
		case CIRURGIAS_REALIZADAS:
		case PROCEDIMENTOS:
		case CONSULTORIAS:
		default:
			break;
		}
	}

	private void prepararListaslistaMdtos(MpmAltaSumarioId id) throws ApplicationBusinessException {

		if(id.getApaAtdSeq() != null){

			this.listaMdtos = this.prescricaoMedicaFacade.prepararListasMedicamento(id);
			this.listaMdtosSelecionados = this.prescricaoMedicaFacade.obterListaAltaPrincFarmacoPorIndCarga(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp(), true);

			Collections.sort(this.listaMdtos,this.comparatorPrincipaisFarmacos);
			Collections.sort(this.listaMdtosSelecionados,this.comparatorPrincipaisFarmacos);
		}
	}
	
	//Metódo para SB
	public List<MbcProcedimentoCirurgicos> obterProcedimentoCirurgicos(String objPesquisa){
		return this.returnSGWithCount(this.blocoCirurgicoFacade.listarProcedimentoCirurgicos(objPesquisa),this.blocoCirurgicoFacade.listarProcedimentoCirurgicosCount(objPesquisa));
	}

	//Metódo para SB
	public List<AghEspecialidades> obterEspecialidade(String objPesquisa){
		return this.returnSGWithCount(this.prescricaoMedicaFacade.listarEspecialidades(objPesquisa), this.prescricaoMedicaFacade.listarEspecialidadesCount(objPesquisa));
	}

	//Metódo para SB
	public List<VMpmOtrProcedSum> listarVMpmOtrProcedSum(String objPesquisa){
		return this.returnSGWithCount(prescricaoMedicaFacade.listarVMpmOtrProcedSum((String) objPesquisa), prescricaoMedicaFacade.listarVMpmOtrProcedSumCount((String) objPesquisa));
	}

	//Metódo para SB
	public List<VAfaDescrMdto> obtemListaDescrMdtoAtivos(String parametro) {
		return this.returnSGWithCount(this.farmaciaFacade.obtemListaDescrMdtoAtivos(parametro),this.farmaciaFacade.obtemListaDescrMdtoAtivosCount(parametro));
	}

	private void desmarcarItensEmEdicao(List<? extends SumarioAltaProcedimentosVO> lista) {
		for (SumarioAltaProcedimentosVO vo : lista) {
			vo.setEmEdicao(Boolean.FALSE);
		}
	}
	
	public void adicionarItemCirurgias() {
		if (itemSelecionadoComboCirurgias != null) {
			setIndiceComboCirurgias(itemSelecionadoComboCirurgias.getIndiceCombo());
			try {
				gravarItemEdicaoCirurgiasRealizadas();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	public void gravarCirurgiasRealizadas() {
		try {
			gravarItemEdicaoCirurgiasRealizadas();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void gravarItemEdicaoCirurgiasRealizadas() throws BaseException{
			
		Date data = null;
		SumarioAltaProcedimentosCrgVO itemEdicao = null;
	
		if (this.indiceComboCirurgias != null) {
			if (this.indiceComboCirurgias > NAO_SELECIONADO) {
				itemEdicao = (SumarioAltaProcedimentosCrgVO) this.obterItemCombo(this.listaComboCirurgias, this.indiceComboCirurgias);
				data = itemEdicao.getData();
			}
			this.indiceComboCirurgias = null;
			this.itemSelecionadoComboCirurgias = null;
		} else {
			itemEdicao = this.itemEdicaoCirurgias;
			data = this.dataCirurgia;
	
			if (this.procedimentoCirurgicos != null) {
				itemEdicao.setSeqProcedimentoCirurgico(this.procedimentoCirurgicos.getSeq());
				itemEdicao.setDescricao(montaDescricaoProcedComPrimeiraMaiuscula(this.procedimentoCirurgicos.getDescricao()));
			} else {
				itemEdicao.setSeqProcedimentoCirurgico(null);
				itemEdicao.setDescricao(null);
			}
		}
	
		if (itemEdicao != null ) {
			itemEdicao.setDescricao(montaDescricaoProcedComPrimeiraMaiuscula(itemEdicao.getDescricao()));
			this.prescricaoMedicaFacade.inserirAltaCirgRealizada(itemEdicao, data);
			apresentarMsgNegocio(Severity.INFO, "Cirurgias Realizadas salvas com sucesso.");
			// Atualizar os dados do slider
			this.prepararListasCirurgias(itemEdicao.getId());
		}
	}

	public void adicionarItemProcedimento() {
		if (itemSelecionadoComboProcedimentos != null) {
			setIndiceComboProcedimentos(itemSelecionadoComboProcedimentos.getIndiceCombo());
			try {
				gravarItemEdicaoProcedimentos();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	public void gravarProcedimentos() {
		try {
			gravarItemEdicaoProcedimentos();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravarItemEdicaoProcedimentos() throws BaseException {
		try {
			Date data = null;
			SumarioAltaPrescricaoProcedimentoVO itemEdicao = null;
	
			if (this.indiceComboProcedimentos != null) {
				if (this.indiceComboProcedimentos > NAO_SELECIONADO) {
					itemEdicao = (SumarioAltaPrescricaoProcedimentoVO) this.obterItemCombo(this.listaComboProcedimentos, this.indiceComboProcedimentos);
					data = itemEdicao.getData();
				}
				this.indiceComboProcedimentos = null;
				this.itemSelecionadoComboProcedimentos = null;
			} else {
				itemEdicao = this.itemEdicaoProcedimentos;
				data = this.dataProcedimento;
	
				if (this.vMpmOtrProcedSum != null) {
					Integer matCodigo = null;
					String matNome = null;
					Integer pciSeq = null;
					String descProcedCirurgico = null;
					Short pedSeq = null;
					String descProcedEspecial = null;
	
					String descricao = this.vMpmOtrProcedSum.getDescricao();
	
					if (this.vMpmOtrProcedSum.getMatCodigo() != null) {
						matCodigo = this.vMpmOtrProcedSum.getMatCodigo().intValue();
						matNome = descricao;
					}
					if (this.vMpmOtrProcedSum.getPciSeq() != null) {
						pciSeq = this.vMpmOtrProcedSum.getPciSeq().intValue();
						descProcedCirurgico = descricao;
					}
					if (this.vMpmOtrProcedSum.getPedSeq() != null) {
						pedSeq = this.vMpmOtrProcedSum.getPedSeq().shortValue();
						descProcedEspecial = descricao;
					}
	
					itemEdicao.setDescricao(montaDescricaoProcedComPrimeiraMaiuscula(descricao));
					itemEdicao.setMatCodigo(matCodigo);
					itemEdicao.setMatNome(matNome);
					itemEdicao.setPciSeq(pciSeq);
					itemEdicao.setDescProcedCirurgico(montaDescricaoProcedComPrimeiraMaiuscula(descProcedCirurgico));
					itemEdicao.setPedSeq(pedSeq);
					itemEdicao.setDescProcedEspecial(montaDescricaoProcedComPrimeiraMaiuscula(descProcedEspecial));
				} else {
					itemEdicao.setDescricao(null);
					itemEdicao.setMatCodigo(null);
					itemEdicao.setMatNome(null);
					itemEdicao.setPciSeq(null);
					itemEdicao.setDescProcedCirurgico(null);
					itemEdicao.setPedSeq(null);
					itemEdicao.setDescProcedEspecial(null);
				}
			}
	
			if (itemEdicao != null ) {
				itemEdicao.setDescricao(montaDescricaoProcedComPrimeiraMaiuscula(itemEdicao.getDescricao()));
				this.prescricaoMedicaFacade.inserirAltaOtrProcedimento(itemEdicao, data);
				apresentarMsgNegocio(Severity.INFO, "Procedimentos salvos com sucesso.");
				// Atualizar os dados do slider
				this.prepararListasProcedimentos(itemEdicao.getId());
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void gravarItemEdicaoOutrosProcedimentos() {
		try {
			boolean continuaInsert = true;
	
			if(this.dataOutroProcedimento == null){
				apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, "Data");
				continuaInsert = false;
			}
	
			if(this.descricaoOutroProcedimento == null || this.descricaoOutroProcedimento.trim().equals("")){
				apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, "Descrição");
				continuaInsert = false;
			}
	
			if(continuaInsert){
				SumarioAltaProcedimentosVO itemEdicao = this.itemEdicaoOutrosProcedimentos;
				itemEdicao.setData(this.dataOutroProcedimento);
				itemEdicao.setDescricao(montaDescricaoProcedComPrimeiraMaiuscula(this.descricaoOutroProcedimento));
	
				this.prescricaoMedicaFacade.inserirAltaOtrProcedimento(itemEdicao);
				apresentarMsgNegocio(Severity.INFO, "Outros Procedimentos salvos com sucesso.");
				// Atualizar os dados do slider
				this.itemEdicaoOutrosProcedimentos = new SumarioAltaProcedimentosVO(new MpmAltaSumarioId());
				this.prepararListasOutrosProcedimentos(itemEdicao.getId());
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void adicionarItemConsultorias() {
		if (itemSelecionadoComboConsultoria != null) {
			setIndiceComboConsultorias(itemSelecionadoComboConsultoria.getIndiceCombo());
			try {
				gravarItemEdicaoConsultorias();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}	
	}
	
	public void gravarItemConsultorias() {
		try {
			gravarItemEdicaoConsultorias();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravarItemEdicaoConsultorias() throws BaseException {
		Date data = null;
		SumarioAltaProcedimentosConsultoriasVO itemEdicao = null;
	
		if (this.indiceComboConsultorias != null) {
			if (this.indiceComboConsultorias > NAO_SELECIONADO) {
				itemEdicao = (SumarioAltaProcedimentosConsultoriasVO) this.obterItemCombo(this.listaComboConsultorias, this.indiceComboConsultorias);
				data = itemEdicao.getData();
			}
			this.indiceComboConsultorias = null;
			this.itemSelecionadoComboConsultoria = null;
		} else {
			itemEdicao = this.itemEdicaoConsultorias;
			data = this.dataConsultoria;
	
			if (this.especialidade != null) {
				itemEdicao.setSeqEspecialidade(this.especialidade.getSeq());
				itemEdicao.setDescricao(montaDescricaoProcedComPrimeiraMaiuscula(this.especialidade.getNomeEspecialidade()));
				//itemEdicao.setDescricao(this.especialidade.getNomeEspecialidade());
			} else {
				itemEdicao.setSeqEspecialidade(null);
				itemEdicao.setDescricao(null);
			}
		}
	
		if (itemEdicao != null ) {
			itemEdicao.setDescricao(montaDescricaoProcedComPrimeiraMaiuscula(itemEdicao.getDescricao()));
			this.prescricaoMedicaFacade.inserirAltaConsultoria(itemEdicao, data);
			apresentarMsgNegocio(Severity.INFO, "Consultorias salvas com sucesso.");
			// Atualizar os dados do slider
			this.prepararListasConsultorias(itemEdicao.getId());
		}
	}
	
	public void atualizarItemEmEdicaoCirurgiasRealizadas(){
		try {
			// Não seta a data no vo, pois se ocorrer erro irá ficar com a data alterada na grid.
			this.prescricaoMedicaFacade.atualizarAltaCirgRealizada(this.itemEdicaoCirurgias, this.getDataCirurgia());
			this.prepararListasCirurgias(this.itemEdicaoCirurgias.getId());
			apresentarMsgNegocio(Severity.INFO, "Cirurgia Realizada alterada com sucesso.");
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void atualizarItemEmEdicaoProcedimentos(){
		// Não seta a data no vo, pois se ocorrer erro irá ficar com a data alterada na grid.
		try {
			this.prescricaoMedicaFacade.atualizarAltaOtrProcedimento(this.itemEdicaoProcedimentos, this.getDataProcedimento());
			this.prepararListasProcedimentos(this.itemEdicaoProcedimentos.getId());
			apresentarMsgNegocio(Severity.INFO, "Procedimento alterado com sucesso.");		
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void atualizarItemEmEdicaoOutrosProcedimentos(){
		try {
			boolean continuaInsert = true;	
			if(this.dataOutroProcedimento == null){
				apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, "Data");
				continuaInsert = false;
			}	
			if(this.descricaoOutroProcedimento == null || this.descricaoOutroProcedimento.trim().equals("")){
				apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, "Descrição");
				continuaInsert = false;
			}	
			if(continuaInsert){
				// Não seta a data no vo, pois se ocorrer erro irá ficar com a data alterada na grid.
				this.prescricaoMedicaFacade.atualizarAltaOtrProcedimento(this.itemEdicaoOutrosProcedimentos, this.getDataOutroProcedimento(), this.getDescricaoOutroProcedimento());
				this.prepararListasOutrosProcedimentos(this.itemEdicaoOutrosProcedimentos.getId());
				apresentarMsgNegocio(Severity.INFO, "Outro Procedimento alterado com sucesso.");
			}			
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void atualizarItemEmEdicaoConsultorias() {
		try {
			// Não seta a data no vo, pois se ocorrer erro irá ficar com a data alterada na grid.
			this.prescricaoMedicaFacade.atualizarAltaConsultoria(this.itemEdicaoConsultorias, this.getDataConsultoria());
			this.prepararListasConsultorias(this.itemEdicaoConsultorias.getId());
			apresentarMsgNegocio(Severity.INFO, "Consultoria alterada com sucesso.");
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void cancelarItemEmEdicaoCirurgiasRealizadas() {
		this.limparDadosEdicaoCirurgias(this.itemEdicaoCirurgias.getId());
	}
	
	public void cancelarItemEmEdicaoProcedimentos() {
		this.limparDadosEdicaoProcedimentos(this.itemEdicaoProcedimentos.getId());		
	}
	
	public void cancelarItemEmEdicaoOutrosProcedimentos() {
		this.limparDadosEdicaoOutrosProcedimentos(this.itemEdicaoOutrosProcedimentos.getId());		
	}

	public void cancelarItemEmEdicaoConsultorias() {
		this.limparDadosEdicaoConsultorias(this.itemEdicaoConsultorias.getId());		
	}
	
	public void editarItemGridCirurgiasRealizadas() {
		List<? extends SumarioAltaProcedimentosVO> listaEmEdicao = null;
		this.editarItemGridCirurgiasRealizadas(getItemCirurgiasSelecionado());
		listaEmEdicao = this.listaGridCirurgias;
		
		if (listaEmEdicao != null) {
			this.desmarcarItensEmEdicao(listaEmEdicao);
		}
		getItemCirurgiasSelecionado().setEmEdicao(Boolean.TRUE);
	}
	
	public void editarItemGridProcedimentos() {
		List<? extends SumarioAltaProcedimentosVO> listaEmEdicao = null;
		this.editarProcedimentosSelecionado(getItemProcedimentosSelecionado());
		listaEmEdicao = this.listaGridProcedimentos;
		if (listaEmEdicao != null) {
			this.desmarcarItensEmEdicao(listaEmEdicao);
		}
		getItemProcedimentosSelecionado().setEmEdicao(Boolean.TRUE);
	}
	
	public void editarItemGridOutrosProcedimentos() {
		List<? extends SumarioAltaProcedimentosVO> listaEmEdicao = null;
		this.editarOutrosProcedimentosSelecionado(getItemOutrosProcedimentosSelecionado());
		listaEmEdicao = this.listaGridOutrosProcedimentos;
		
		if (listaEmEdicao != null) {
			this.desmarcarItensEmEdicao(listaEmEdicao);
		}
		getItemOutrosProcedimentosSelecionado().setEmEdicao(Boolean.TRUE);
	}
	
	public void editarItemGridConsultorias() {
		List<? extends SumarioAltaProcedimentosVO> listaEmEdicao = null;
		this.editarConsultoriasSelecionado(getItemConsultoriasSelecionado());
		listaEmEdicao = this.listaGridConsultorias;
		
		if (listaEmEdicao != null) {
			this.desmarcarItensEmEdicao(listaEmEdicao);
		}
		getItemConsultoriasSelecionado().setEmEdicao(Boolean.TRUE);
	}
	
	private void editarItemGridCirurgiasRealizadas(
			SumarioAltaProcedimentosVO itemGrid) {
		// Popula dados da tela
		this.dataCirurgia = itemGrid.getData();
		Integer seqProcedimentoCirurgico =  ((SumarioAltaProcedimentosCrgVO) itemGrid).getSeqProcedimentoCirurgico();
		this.procedimentoCirurgicos = this.blocoCirurgicoFacade.obterProcedimentoCirurgico(seqProcedimentoCirurgico);

		// Identifica o item em edicao da grid
		this.itemEdicaoCirurgias = (SumarioAltaProcedimentosCrgVO) itemGrid;

		// Desmarca item selecionado na combo
		this.indiceComboCirurgias = null;
	}

	private void editarProcedimentosSelecionado(SumarioAltaProcedimentosVO itemGrid) {
		SumarioAltaPrescricaoProcedimentoVO vo = (SumarioAltaPrescricaoProcedimentoVO) itemGrid;

		// Popula dados da tela
		this.dataProcedimento = vo.getData();
		this.vMpmOtrProcedSum = this.prescricaoMedicaFacade.obterVMpmOtrProcedSum(vo.getMatCodigo(), vo.getPciSeq(), vo.getPedSeq());

		// Identifica o item em edicao da grid
		this.itemEdicaoProcedimentos = vo;

		// Desmarca item selecionado na combo
		this.indiceComboProcedimentos = null;
	}

	private void editarOutrosProcedimentosSelecionado(SumarioAltaProcedimentosVO itemGrid) {
		// Popula dados da tela
		this.dataOutroProcedimento = itemGrid.getData();
		this.descricaoOutroProcedimento = itemGrid.getDescricao();

		// Identifica o item em edicao da grid
		this.itemEdicaoOutrosProcedimentos = itemGrid;
	}

	private void editarConsultoriasSelecionado(SumarioAltaProcedimentosVO itemGrid) {
		// Popula dados da tela
		this.dataConsultoria = itemGrid.getData();
		Short seqEspecialidade =  ((SumarioAltaProcedimentosConsultoriasVO) itemGrid).getSeqEspecialidade();
		this.especialidade = this.aghuFacade.obterAghEspecialidadesPorChavePrimaria(seqEspecialidade);

		this.itemEdicaoConsultorias = (SumarioAltaProcedimentosConsultoriasVO) itemGrid;

		// Desmarca item selecionado na combo
		this.indiceComboConsultorias = null;
	}
	
	public void excluirItemGridCirurgiasRealizadas() {
		try{
			MpmAltaSumarioId idCirurgiasRealizadas = getItemCirurgiasSelecionado().getId();
			this.prescricaoMedicaFacade.removerAltaCirgRealizada((SumarioAltaProcedimentosCrgVO) getItemCirurgiasSelecionado());
			this.prepararListasCirurgias(idCirurgiasRealizadas);
			apresentarMsgNegocio(Severity.INFO, "Cirurgia Realizada excluída com sucesso.");
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void excluirItemGridProcedimentos() {
		try{
			MpmAltaSumarioId idProcedimentos = getItemProcedimentosSelecionado().getId();
			this.prescricaoMedicaFacade.removerAltaOtrProcedimento((SumarioAltaPrescricaoProcedimentoVO) getItemProcedimentosSelecionado());
			this.prepararListasProcedimentos(idProcedimentos);
			apresentarMsgNegocio(Severity.INFO, "Procedimento excluído com sucesso.");
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void excluirItemGridOutrosProcedimentos() {
		try{
			MpmAltaSumarioId idOutrosProcedimentos = getItemOutrosProcedimentosSelecionado().getId();
			this.prescricaoMedicaFacade.removerAltaOtrProcedimento(getItemOutrosProcedimentosSelecionado());
			this.prepararListasOutrosProcedimentos(idOutrosProcedimentos);
			apresentarMsgNegocio(Severity.INFO, "Outros Procedimentos excluídos com sucesso.");				
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void excluirItemGridConsultorias() {
		try{
			MpmAltaSumarioId idConsultorias = getItemConsultoriasSelecionado().getId();
			this.prescricaoMedicaFacade.removerAltaConsultoria((SumarioAltaProcedimentosConsultoriasVO) getItemConsultoriasSelecionado());
			this.prepararListasConsultorias(idConsultorias);
			apresentarMsgNegocio(Severity.INFO, "Consultoria excluída com sucesso.");
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void moverMdtoParaListaDestino(){
		try {
			this.prescricaoMedicaFacade.inserirMpmAltaPrincFarmaco(this.mdtoSelecionadoOrigem);
			this.listaMdtos.remove(this.mdtoSelecionadoOrigem);


			this.listaMdtosSelecionados = this.prescricaoMedicaFacade.obterListaAltaPrincFarmacoPorIndCarga(this.mdtoSelecionadoOrigem.getAsuApaAtdSeq(), this.mdtoSelecionadoOrigem.getAsuApaSeq(), this.mdtoSelecionadoOrigem.getAsuSeqp(), true);

			//this.listaMdtosSelecionados.add(this.mdtoSelecionadoOrigem);
			Collections.sort(this.listaMdtosSelecionados,this.comparatorPrincipaisFarmacos);
			apresentarMsgNegocio(Severity.INFO, "Principais Fármacos salvos com sucesso.");
			
			this.mdtoSelecionadoOrigem = null;
		} catch (BaseException  e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void moverMdtoParaListaOrigem(){	    
		try {
			this.prescricaoMedicaFacade.removerMpmAltaPrincFarmaco(this.mdtoSelecionadoDestino);
			this.listaMdtosSelecionados.remove(this.mdtoSelecionadoDestino);
			this.listaMdtos.add(this.mdtoSelecionadoDestino);
			Collections.sort(this.listaMdtos,this.comparatorPrincipaisFarmacos);
			apresentarMsgNegocio(Severity.INFO, "Farmaco excluído com sucesso.");
			
			this.mdtoSelecionadoDestino = null;
		} catch (BaseException  e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void setarMdtoSelecionadoDestino(){
		setMdtoSelecionadoDestino(this.listaAfaMedicamentoPrescricaoVO);
		setMdtoSelecionadoOrigem(null);
	}
	
	public void setarMdtoSelecionadoOrigem(){
		setMdtoSelecionadoDestino(null);
		setMdtoSelecionadoOrigem(this.listaAfaMedicamentoPrescricaoVO);
	}
	
	private String montaDescricaoProcedComPrimeiraMaiuscula(String descricaoProced){
		return (descricaoProced!=null)?descricaoProced.trim().substring(0,1).toUpperCase()+descricaoProced.trim().substring(1).toLowerCase():null;
	}

	public Integer getCurrentSlider() {
		return this.currentSlider;
	}

	public void setCurrentSlider(Integer currentSlider) {
		this.currentSlider = currentSlider;
	}

	public Date getDataCirurgia() {
		return this.dataCirurgia;
	}

	public void setDataCirurgia(Date dataCirurgia) {
		this.dataCirurgia = dataCirurgia;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return this.prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public MbcProcedimentoCirurgicos getProcedimentoCirurgicos() {
		return this.procedimentoCirurgicos;
	}

	public void setProcedimentoCirurgicos(
			MbcProcedimentoCirurgicos procedimentoCirurgicos) {
		this.procedimentoCirurgicos = procedimentoCirurgicos;
	}

	public SumarioAltaProcedimentosCrgVO getItemEdicaoCirurgias() {
		return this.itemEdicaoCirurgias;
	}

	public void setItemEdicaoCirurgias(
			SumarioAltaProcedimentosCrgVO itemEdicaoCirurgias) {
		this.itemEdicaoCirurgias = itemEdicaoCirurgias;
	}

	public Integer getIndiceComboCirurgias() {
		return this.indiceComboCirurgias;
	}

	public void setIndiceComboCirurgias(Integer indiceComboCirurgias) {
		this.indiceComboCirurgias = indiceComboCirurgias;
	}

	public List<SumarioAltaProcedimentosCrgVO> getListaComboCirurgias() {
		return this.listaComboCirurgias;
	}

	public void setListaComboCirurgias(
			List<SumarioAltaProcedimentosCrgVO> listaComboCirurgias) {
		this.listaComboCirurgias = listaComboCirurgias;
	}

	public List<SumarioAltaProcedimentosCrgVO> getListaGridCirurgias() {
		return this.listaGridCirurgias;
	}

	public void setListaGridCirurgias(
			List<SumarioAltaProcedimentosCrgVO> listaGridCirurgias) {
		this.listaGridCirurgias = listaGridCirurgias;
	}

	public Date getDataProcedimento() {
		return this.dataProcedimento;
	}

	public void setDataProcedimento(Date dataProcedimento) {
		this.dataProcedimento = dataProcedimento;
	}

	public VMpmOtrProcedSum getvMpmOtrProcedSum() {
		return this.vMpmOtrProcedSum;
	}

	public void setvMpmOtrProcedSum(VMpmOtrProcedSum vMpmOtrProcedSum) {
		this.vMpmOtrProcedSum = vMpmOtrProcedSum;
	}

	public Integer getIndiceComboProcedimentos() {
		return this.indiceComboProcedimentos;
	}

	public void setIndiceComboProcedimentos(Integer indiceComboProcedimentos) {
		this.indiceComboProcedimentos = indiceComboProcedimentos;
	}

	public SumarioAltaPrescricaoProcedimentoVO getItemEdicaoProcedimentos() {
		return this.itemEdicaoProcedimentos;
	}

	public void setItemEdicaoProcedimentos(
			SumarioAltaPrescricaoProcedimentoVO itemEdicaoProcedimentos) {
		this.itemEdicaoProcedimentos = itemEdicaoProcedimentos;
	}

	public List<SumarioAltaPrescricaoProcedimentoVO> getListaComboProcedimentos() {
		return this.listaComboProcedimentos;
	}

	public void setListaComboProcedimentos(
			List<SumarioAltaPrescricaoProcedimentoVO> listaComboProcedimentos) {
		this.listaComboProcedimentos = listaComboProcedimentos;
	}

	public List<SumarioAltaPrescricaoProcedimentoVO> getListaGridProcedimentos() {
		return this.listaGridProcedimentos;
	}

	public void setListaGridProcedimentos(
			List<SumarioAltaPrescricaoProcedimentoVO> listaGridProcedimentos) {
		this.listaGridProcedimentos = listaGridProcedimentos;
	}

	public Date getDataConsultoria() {
		return this.dataConsultoria;
	}

	public void setDataConsultoria(Date dataConsultoria) {
		this.dataConsultoria = dataConsultoria;
	}

	public AghEspecialidades getEspecialidade() {
		return this.especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public Integer getIndiceComboConsultorias() {
		return this.indiceComboConsultorias;
	}

	public void setIndiceComboConsultorias(Integer indiceComboConsultorias) {
		this.indiceComboConsultorias = indiceComboConsultorias;
	}

	public SumarioAltaProcedimentosConsultoriasVO getItemEdicaoConsultorias() {
		return this.itemEdicaoConsultorias;
	}

	public void setItemEdicaoConsultorias(
			SumarioAltaProcedimentosConsultoriasVO itemEdicaoConsultorias) {
		this.itemEdicaoConsultorias = itemEdicaoConsultorias;
	}

	public List<SumarioAltaProcedimentosConsultoriasVO> getListaComboConsultorias() {
		return this.listaComboConsultorias;
	}

	public void setListaComboConsultorias(
			List<SumarioAltaProcedimentosConsultoriasVO> listaComboConsultorias) {
		this.listaComboConsultorias = listaComboConsultorias;
	}

	public List<SumarioAltaProcedimentosConsultoriasVO> getListaGridConsultorias() {
		return this.listaGridConsultorias;
	}

	public void setListaGridConsultorias(
			List<SumarioAltaProcedimentosConsultoriasVO> listaGridConsultorias) {
		this.listaGridConsultorias = listaGridConsultorias;
	}

	public AfaMedicamentoPrescricaoVO getAfaMedicamentoPrescricaoVO() {
		return this.afaMedicamentoPrescricaoVO;
	}

	public void setAfaMedicamentoPrescricaoVO(
			AfaMedicamentoPrescricaoVO afaMedicamentoPrescricaoVO) {
		this.afaMedicamentoPrescricaoVO = afaMedicamentoPrescricaoVO;
	}

	public List<AfaMedicamentoPrescricaoVO> getListaMdtos() {
		return this.listaMdtos;
	}

	public void setListaMdtos(List<AfaMedicamentoPrescricaoVO> listaMdtos) {
		this.listaMdtos = listaMdtos;
	}

	public List<VMpmMdtoPrescNew> getListaVMpmMdtoPrescNewDescricao() {
		return this.listaVMpmMdtoPrescNewDescricao;
	}

	public void setListaVMpmMdtoPrescNewDescricao(
			List<VMpmMdtoPrescNew> listaVMpmMdtoPrescNewDescricao) {
		this.listaVMpmMdtoPrescNewDescricao = listaVMpmMdtoPrescNewDescricao;
	}

	public List<VMpmMdtoPrescNew> getListaVMpmMdtoPrescNew() {
		return this.listaVMpmMdtoPrescNew;
	}

	public void setListaVMpmMdtoPrescNew(
			List<VMpmMdtoPrescNew> listaVMpmMdtoPrescNew) {
		this.listaVMpmMdtoPrescNew = listaVMpmMdtoPrescNew;
	}

	public List<AfaMedicamentoPrescricaoVO> getListaMdtosSelecionados() {
		return this.listaMdtosSelecionados;
	}

	public void setListaMdtosSelecionados(
			List<AfaMedicamentoPrescricaoVO> listaMdtosSelecionados) {
		this.listaMdtosSelecionados = listaMdtosSelecionados;
	}

	public void setMdtoSelecionadoOrigem(AfaMedicamentoPrescricaoVO mdtoSelecionadoOrigem) {
		this.mdtoSelecionadoOrigem = mdtoSelecionadoOrigem;
	}

	public AfaMedicamentoPrescricaoVO getMdtoSelecionadoOrigem() {
		return this.mdtoSelecionadoOrigem;
	}

	public void setMdtoSelecionadoDestino(AfaMedicamentoPrescricaoVO mdtoSelecionadoDestino) {
		this.mdtoSelecionadoDestino = mdtoSelecionadoDestino;
	}

	public AfaMedicamentoPrescricaoVO getMdtoSelecionadoDestino() {
		return this.mdtoSelecionadoDestino;
	}

	public Date getDataOutroProcedimento() {
		return this.dataOutroProcedimento;
	}

	public void setDataOutroProcedimento(Date dataOutroProcedimento) {
		this.dataOutroProcedimento = dataOutroProcedimento;
	}

	public String getDescricaoOutroProcedimento() {
		return this.descricaoOutroProcedimento;
	}

	public void setDescricaoOutroProcedimento(String descricaoOutroProcedimento) {
		this.descricaoOutroProcedimento = descricaoOutroProcedimento;
	}

	public SumarioAltaProcedimentosVO getItemEdicaoOutrosProcedimentos() {
		return this.itemEdicaoOutrosProcedimentos;
	}

	public void setItemEdicaoOutrosProcedimentos(
			SumarioAltaProcedimentosVO itemEdicaoOutrosProcedimentos) {
		this.itemEdicaoOutrosProcedimentos = itemEdicaoOutrosProcedimentos;
	}

	public List<SumarioAltaProcedimentosVO> getListaGridOutrosProcedimentos() {
		return this.listaGridOutrosProcedimentos;
	}

	public void setListaGridOutrosProcedimentos(
			List<SumarioAltaProcedimentosVO> listaGridOutrosProcedimentos) {
		this.listaGridOutrosProcedimentos = listaGridOutrosProcedimentos;
	}

	public AltaSumarioInfoComplVO getInformacaoComplementarVO() {
		return informacaoComplementarVO;
	}

	public void setInformacaoComplementarVO(
			AltaSumarioInfoComplVO informacaoComplementarVO) {
		this.informacaoComplementarVO = informacaoComplementarVO;
	}



	public List<AltaSumarioInfoComplVO> getListaInformacaoComplementar() {
		return listaInformacaoComplementar;
	}



	public void setListaInformacaoComplementar(
			List<AltaSumarioInfoComplVO> listaInformacaoComplementar) {
		this.listaInformacaoComplementar = listaInformacaoComplementar;
	}
	
	public String getComplementoInformacoesComplementares() {
		return complementoInformacoesComplementares;
	}
	
	public void setComplementoInformacoesComplementares(String complementoInformacoesComplementares) {
		this.complementoInformacoesComplementares = complementoInformacoesComplementares;
	}

	public void setMostrarPanelOutrosItens(Boolean mostrarPanelOutrosItens) {
		this.mostrarPanelOutrosItens = mostrarPanelOutrosItens;
	}

	public Boolean getMostrarPanelOutrosItens() {
		return mostrarPanelOutrosItens;
	}

	public void setMostrarPanelComboCirurgias(Boolean mostrarPanelComboCirurgias) {
		this.mostrarPanelComboCirurgias = mostrarPanelComboCirurgias;
	}

	public Boolean getMostrarPanelComboCirurgias() {
		return mostrarPanelComboCirurgias;
	}

	public void setMostrarPanelOutrosItensProcedimento(
			Boolean mostrarPanelOutrosItensProcedimento) {
		this.mostrarPanelOutrosItensProcedimento = mostrarPanelOutrosItensProcedimento;
	}

	public Boolean getMostrarPanelOutrosItensProcedimento() {
		return mostrarPanelOutrosItensProcedimento;
	}

	public void setMostrarPanelComboProcedimento(
			Boolean mostrarPanelComboProcedimento) {
		this.mostrarPanelComboProcedimento = mostrarPanelComboProcedimento;
	}

	public Boolean getMostrarPanelComboProcedimento() {
		return mostrarPanelComboProcedimento;
	}

	public void setMostrarPanelOutrosItensConsultoria(
			Boolean mostrarPanelOutrosItensConsultoria) {
		this.mostrarPanelOutrosItensConsultoria = mostrarPanelOutrosItensConsultoria;
	}

	public Boolean getMostrarPanelOutrosItensConsultoria() {
		return mostrarPanelOutrosItensConsultoria;
	}

	public void setMostrarPanelComboConsultoria(
			Boolean mostrarPanelComboConsultoria) {
		this.mostrarPanelComboConsultoria = mostrarPanelComboConsultoria;
	}

	public Boolean getMostrarPanelComboConsultoria() {
		return mostrarPanelComboConsultoria;
	}
	public SumarioAltaProcedimentosCrgVO getItemCirurgiasSelecionado() {
		return itemCirurgiasSelecionado;
	}
	public void setItemCirurgiasSelecionado(SumarioAltaProcedimentosCrgVO itemCirurgiasSelecionado) {
		this.itemCirurgiasSelecionado = itemCirurgiasSelecionado;
	}
	public SumarioAltaPrescricaoProcedimentoVO getItemProcedimentosSelecionado() {
		return itemProcedimentosSelecionado;
	}
	public void setItemProcedimentosSelecionado(
			SumarioAltaPrescricaoProcedimentoVO itemProcedimentosSelecionado) {
		this.itemProcedimentosSelecionado = itemProcedimentosSelecionado;
	}
	public SumarioAltaProcedimentosVO getItemOutrosProcedimentosSelecionado() {
		return itemOutrosProcedimentosSelecionado;
	}
	public void setItemOutrosProcedimentosSelecionado(
			SumarioAltaProcedimentosVO itemOutrosProcedimentosSelecionado) {
		this.itemOutrosProcedimentosSelecionado = itemOutrosProcedimentosSelecionado;
	}
	public SumarioAltaProcedimentosConsultoriasVO getItemConsultoriasSelecionado() {
		return itemConsultoriasSelecionado;
	}
	public void setItemConsultoriasSelecionado(
			SumarioAltaProcedimentosConsultoriasVO itemConsultoriasSelecionado) {
		this.itemConsultoriasSelecionado = itemConsultoriasSelecionado;
	}
	public AfaMedicamentoPrescricaoVO getListaAfaMedicamentoPrescricaoVO() {
		return listaAfaMedicamentoPrescricaoVO;
	}
	public void setListaAfaMedicamentoPrescricaoVO(
			AfaMedicamentoPrescricaoVO listaAfaMedicamentoPrescricaoVO) {
		this.listaAfaMedicamentoPrescricaoVO = listaAfaMedicamentoPrescricaoVO;
	}
	public SumarioAltaPrescricaoProcedimentoVO getItemSelecionadoComboProcedimentos() {
		return itemSelecionadoComboProcedimentos;
	}
	public void setItemSelecionadoComboProcedimentos(
			SumarioAltaPrescricaoProcedimentoVO itemSelecionadoComboProcedimentos) {
		this.itemSelecionadoComboProcedimentos = itemSelecionadoComboProcedimentos;
	}
	public SumarioAltaProcedimentosCrgVO getItemSelecionadoComboCirurgias() {
		return itemSelecionadoComboCirurgias;
	}
	public void setItemSelecionadoComboCirurgias(
			SumarioAltaProcedimentosCrgVO itemSelecionadoComboCirurgias) {
		this.itemSelecionadoComboCirurgias = itemSelecionadoComboCirurgias;
	}
	public SumarioAltaProcedimentosConsultoriasVO getItemSelecionadoComboConsultoria() {
		return itemSelecionadoComboConsultoria;
	}
	public void setItemSelecionadoComboConsultoria(
			SumarioAltaProcedimentosConsultoriasVO itemSelecionadoComboConsultoria) {
		this.itemSelecionadoComboConsultoria = itemSelecionadoComboConsultoria;
	}

}