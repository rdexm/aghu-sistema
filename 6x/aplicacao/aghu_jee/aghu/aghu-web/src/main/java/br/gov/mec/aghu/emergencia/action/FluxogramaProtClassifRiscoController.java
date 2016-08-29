package br.gov.mec.aghu.emergencia.action;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.ItemObrigatorioVO;
import br.gov.mec.aghu.model.MamDescritor;
import br.gov.mec.aghu.model.MamFluxograma;
import br.gov.mec.aghu.model.MamGravidade;
import br.gov.mec.aghu.model.MamItemExame;
import br.gov.mec.aghu.model.MamItemGeral;
import br.gov.mec.aghu.model.MamItemMedicacao;
import br.gov.mec.aghu.model.MamProtClassifRisco;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

/**
 * Controller das ações da pagina de fluxogramas de protocolos de classificação de risco
 * 
 * @author luismoura
 * 
 */
public class FluxogramaProtClassifRiscoController extends ActionController {
	private static final long serialVersionUID = 1476508964354561882L;
	
	// ----- PAGINAS
	private final String PAGE_PROT_CLASSIF_RISCO = "protClassifRiscoCRUD";
	private final String ABA_SINAL_VITAL = "sinalVital", ABA_EXAME = "exame", ABA_MEDICACAO = "medicacao", ABA_DADOS_GERAIS = "dadosGerais";
	private final Integer TAB_ABA_DADOS_GERAIS = 0, TAB_ABA_SINAL_VITAL = 1, TAB_ABA_EXAME = 2, TAB_ABA_MEDICACAO = 3;
	
	
	private final Integer PANEL_FILTRO_FLUX = 0, PANEL_LIST_FLUX = 1, PANEL_DESCR_FLUX = 2, PANEL_ITENS_FLUX = 3;
	
	private static final String SLIDER_0 = "slider_0";
	private static final String SLIDER_1 = "slider_1";
	private static final String SLIDER_2 = "slider_2";
	private static final String SLIDER_3 = "slider_3";
	
	
	// ----- FAÇADES
	@Inject
	private IEmergenciaFacade emergenciaFacade;
	
	// ----- FILTRO
	private MamProtClassifRisco mamProtClassifRisco;
	
	// ----- CONTROLE DE TELA	
	private Integer abaSelecionada;
	private Integer currentSlider;
	private boolean botaoVoltar = false;
	private boolean permissaoManter = false;
	
	// ----- LISTAGEM DE FLUXOGRAMA
	private List<MamFluxograma> dataModelFluxograma = new ArrayList<MamFluxograma>();
	private boolean pesquisaAtivaFluxograma;
	
	// ----- NOVO ou EDICAO DE FLUXOGRAMA
	private MamFluxograma mamFluxograma;
	private MamFluxograma mamFluxogramaSelecionado;
	private boolean edicaoFluxograma = false;
	private Boolean indSituacaoFluxograma = true;
	
	// ----- LISTAGEM DE DESCRITOR
	private List<MamDescritor> dataModelDescritor = new ArrayList<MamDescritor>();
	private boolean pesquisaAtivaDescritor;
	
	// ----- NOVO ou EDICAO DE DESCRITOR
	private MamDescritor mamDescritor;
	private MamDescritor mamDescritorSelecionado;
	private boolean edicaoDescritor = false;
	private Boolean indSituacaoDescritor = true;
	
	// ----- LISTAGEM DE OBRIGATORIEDADES
	private List<ItemObrigatorioVO> dataModelItemSinalVital = new ArrayList<ItemObrigatorioVO>();
	private List<ItemObrigatorioVO> dataModelItemMedicacao = new ArrayList<ItemObrigatorioVO>();
	private List<ItemObrigatorioVO> dataModelItemExame = new ArrayList<ItemObrigatorioVO>();
	private List<ItemObrigatorioVO> dataModelItemGeral = new ArrayList<ItemObrigatorioVO>();
	private boolean pesquisaAtivaItensObrigatorios;
	
	// ----- ABA SINAIS VITAIS
	private ItemObrigatorioVO itemSinalVitalInput;
	private Boolean situacaoItemSinalVitalInput;
	private ItemObrigatorioVO itemSinalVitalSelecionado;
	
	// ----- ABA EXAMES
	private MamItemExame itemExameInput;
	private Boolean situacaoItemExameInput;
	private ItemObrigatorioVO itemExameSelecionado;

	// ----- ABA MEDICACOES
	private MamItemMedicacao itemMedicacaoInput;
	private Boolean situacaoItemMedicacaoInput;
	private ItemObrigatorioVO itemMedicacaoSelecionado;
	
	// ----- ABA ITENSGERAIS
	private MamItemGeral itemGeralInput;
	private Boolean situacaoItemGeralInput;
	private ItemObrigatorioVO itemGeralSelecionado;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}

	public void inicio() {
		
		this.permissaoManter = this.usuarioTemPermissao("manterFluxogramaDosProtocolosClassificacaoRisco", "gravar");
		this.abaSelecionada = TAB_ABA_DADOS_GERAIS;
		this.situacaoItemGeralInput = Boolean.TRUE;
		this.situacaoItemSinalVitalInput = Boolean.TRUE;
		this.situacaoItemExameInput = Boolean.TRUE;
		this.situacaoItemMedicacaoInput = Boolean.TRUE;
		this.permissaoManter = this.usuarioTemPermissao("manterFluxogramaDosProtocolosClassificacaoRisco", "gravar");
		this.setCurrentSlider(currentSlider != null ? currentSlider : PANEL_FILTRO_FLUX);
		
		if (this.edicaoDescritor) {
			this.setCurrentSlider(PANEL_DESCR_FLUX);
		} else if (this.edicaoFluxograma) {
			this.setCurrentSlider(PANEL_LIST_FLUX);
		} else if (this.mamProtClassifRisco != null && mamFluxogramaSelecionado != null && mamDescritorSelecionado != null) {
			this.abaSelecionada = TAB_ABA_DADOS_GERAIS;
			this.pesquisarItensObrigatorios();
		} else if (this.mamProtClassifRisco != null && mamFluxogramaSelecionado != null) {
			this.pesquisarDescritor();
		} else if (this.mamProtClassifRisco != null) {
			this.setCurrentSlider(PANEL_LIST_FLUX);
			this.pesquisarFluxograma();
		} else {
			this.setCurrentSlider(PANEL_FILTRO_FLUX);
		}
	}
	
	/**
	 * Usado para popular a SUGGESTION
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<MamProtClassifRisco> pesquisarMamProtClassifRisco(String param) {
		return  this.returnSGWithCount(emergenciaFacade.pesquisarProtocolosClassificacaoRiscoAtivosPorCodigoDescricao(param, 100),pesquisarMamProtClassifRiscoCount(param));
	}

	/**
	 * Usado para popular a SUGGESTION
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public Long pesquisarMamProtClassifRiscoCount(String param) {
		return emergenciaFacade.pesquisarProtocolosClassificacaoRiscoAtivosPorCodigoDescricaoCount(param);
	}

	/**
	 * Usado para popular a SUGGESTION GRAVIDADE
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<MamGravidade> pesquisarMamGravidade(String param) {
		return  this.returnSGWithCount(emergenciaFacade.pesquisarGravidadesAtivasPorCodigoDescricao(param,
				mamProtClassifRisco.getSeq() != null ? mamProtClassifRisco.getSeq() : null),pesquisarMamGravidadeCount(param));
	}

	/**
	 * Usado para popular a SUGGESTION GRAVIDADE
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public Long pesquisarMamGravidadeCount(String param) {
		return emergenciaFacade.pesquisarGravidadesAtivasPorCodigoDescricaoCount(param,
				mamProtClassifRisco.getSeq() != null ? mamProtClassifRisco.getSeq() : null);
	}

	/**
	 * Ação do botão PESQUISAR
	 */
	public void pesquisar() {
		this.mamFluxogramaSelecionado = null;
		this.mamDescritorSelecionado = null;
		inicio();
	}

	public void pesquisarFluxograma() {
		if (this.mamProtClassifRisco == null) {
			this.dataModelFluxograma.clear();
		} else {
			this.pesquisaAtivaFluxograma = true;
			this.dataModelFluxograma = emergenciaFacade.pesquisarFluxogramaPorProtocolo(this.mamProtClassifRisco);
		}
		this.limparCamposEdicaoFluxograma();
		this.limparPesquisaDescritor();
	}

	public void pesquisarDescritor() {
		
		if (this.mamFluxogramaSelecionado == null) {
			this.dataModelDescritor.clear();
		} else {
			this.pesquisaAtivaDescritor = true;
			this.dataModelDescritor = emergenciaFacade.pesquisarDescritorPorFluxograma(this.mamFluxogramaSelecionado);
		}
		this.limparCamposEdicaoDescritor();
		this.limparPesquisaItensObrigatorios();
	}

	/**
	 * Ação do botão LIMPAR
	 */
	public void limpar() {
		this.mamProtClassifRisco = null;
		this.limparPesquisaFluxograma();
		this.limparPesquisaDescritor();
		this.limparCamposItensObrigatorios();
		inicio();
	}

	/**
	 * Ação do botão LIMPAR
	 */
	public void limparPesquisaFluxograma() {
		this.pesquisaAtivaFluxograma = false;
		this.dataModelDescritor.clear();
		this.limparEdicaoFluxograma();
		this.limparPesquisaDescritor();
	}

	/**
	 * Ação do botão LIMPAR
	 */
	public void limparPesquisaDescritor() {
		this.pesquisaAtivaDescritor = false;
		this.dataModelDescritor.clear();
		this.limparEdicaoDescritor();
		this.limparPesquisaItensObrigatorios();
	}

	public void limparPesquisaItensObrigatorios() {
		this.limparCamposItensObrigatorios();
		this.pesquisaAtivaItensObrigatorios = false;
		this.dataModelItemSinalVital.clear();
		this.dataModelItemExame.clear();
		this.dataModelItemMedicacao.clear();
		this.dataModelItemGeral.clear();
	}
	
	public void limparCamposItensObrigatorios() {

		// ----- ABA SINAIS VITAIS
		itemSinalVitalInput = null;
		situacaoItemSinalVitalInput = Boolean.TRUE;
		itemSinalVitalSelecionado = null;

		// ----- ABA EXAMES
		itemExameInput = null;
		situacaoItemExameInput = Boolean.TRUE;
		itemExameSelecionado = null;

		// ----- ABA MEDICACOES
		itemMedicacaoInput = null;
		situacaoItemMedicacaoInput = Boolean.TRUE;
		itemMedicacaoSelecionado = null;

		// ----- ABA ITENSGERAIS
		itemGeralInput = null;
		situacaoItemGeralInput = Boolean.TRUE;
		itemGeralSelecionado = null;

		this.abaSelecionada = TAB_ABA_DADOS_GERAIS;
	}

	/**
	 * Ação do botão EDITAR
	 * 
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void editarFluxograma(MamFluxograma item) throws ReflectiveOperationException {
		this.edicaoFluxograma = true;
		this.edicaoDescritor = false;
		this.mamFluxograma = new MamFluxograma();
		PropertyUtils.copyProperties(this.mamFluxograma, item);
		
		this.indSituacaoFluxograma = mamFluxograma.getIndSituacao().isAtivo();
		this.mamDescritorSelecionado = null;
		this.limparEdicaoDescritor();
		this.pesquisaAtivaDescritor = false;
		this.pesquisaAtivaItensObrigatorios = false;
	}

	/**
	 * Ação do botão EDITAR
	 * 
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void editarDescritor(MamDescritor item) throws ReflectiveOperationException{
		this.edicaoDescritor = true;
		this.edicaoFluxograma = false;
		this.mamDescritor = new MamDescritor();
		PropertyUtils.copyProperties(this.mamDescritor, item);
		
		this.indSituacaoDescritor = mamDescritor.getIndSituacao().isAtivo();
		this.pesquisaAtivaItensObrigatorios = false;
	}

	/**
	 * Ação do radio SELECIONAR
	 * 
	 * @return
	 */
	public void selecionarFluxograma() {
		this.setCurrentSlider(PANEL_DESCR_FLUX);
		if (!edicaoFluxograma) {
			this.mamFluxograma = new MamFluxograma();
			this.indSituacaoFluxograma = true;
			this.edicaoDescritor = false;
		}
		inicio();
	}

	/**
	 * Ação do radio SELECIONAR
	 * 
	 * @return
	 */
	public void selecionarDescritor() {
		this.setCurrentSlider(PANEL_ITENS_FLUX);
		if (!edicaoDescritor) {
			this.mamDescritor = new MamDescritor();
			this.indSituacaoDescritor = true;
			this.edicaoDescritor = false;
			this.edicaoFluxograma = false;
		}
		this.inicio();
	}

	/**
	 * Ação do botão VOLTAR
	 * 
	 * @return
	 */
	public String voltar() {
		this.limparPesquisaFluxograma();
		return PAGE_PROT_CLASSIF_RISCO;
	}

	/**
	 * Ação do segundo botão ADICIONAR/ALTERAR
	 */
	public void confirmarFluxogramaProtClassifRisco() {
		try {
			// Monta o objeto
			this.mamFluxograma.setIndSituacao(DominioSituacao.getInstance(this.getIndSituacaoFluxograma()));
			this.mamFluxograma.setProtClassifRisco(mamProtClassifRisco);

			boolean create = (this.mamFluxograma.getSeq() == null);

			// Chama RN01 para persistir
			this.emergenciaFacade.persistirMamFluxograma(this.mamFluxograma);

			// Refaz a pesquisa
			this.pesquisarFluxograma();

			// Limpa os campos
			this.limparCamposEdicaoFluxograma();
			
			this.mamDescritorSelecionado = null;
			this.mamFluxogramaSelecionado = null;
			this.limparEdicaoDescritor();
			this.pesquisaAtivaDescritor = false;
			this.pesquisaAtivaItensObrigatorios = false;
			//Define a aba aberta
			inicio();

			// Apresenta mensagem de sucesso
			if (create) {
				super.apresentarMsgNegocio(Severity.INFO, "MAM_MENSAGEM_INCLUSAO_FLUXOGRAMA", this.mamProtClassifRisco.getDescricao());
			} else {
				super.apresentarMsgNegocio(Severity.INFO, "MAM_MENSAGEM_ALTERACAO_FLUXOGRAMA", this.mamProtClassifRisco.getDescricao());
			}

		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do segundo botão ADICIONAR/ALTERAR
	 */
	public void confirmarDescritorFluxograma() {
		try {
			// Monta o objeto
			this.mamDescritor.setIndSituacao(DominioSituacao.getInstance(this.getIndSituacaoDescritor()));
			this.mamDescritor.setFluxograma(this.mamFluxogramaSelecionado);

			boolean create = (this.mamDescritor.getSeq() == null);

			// Chama RN01 para persistir
			this.emergenciaFacade.persistirMamDescritor(this.mamDescritor);

			// Refaz a pesquisa
			this.pesquisarDescritor();

			// Limpa os campos
			this.limparCamposEdicaoDescritor();
			
			this.mamDescritorSelecionado = null;
			this.pesquisaAtivaItensObrigatorios = false;
			//Define a aba aberta
			inicio();

			// Apresenta mensagem de sucesso
			if (create) {
				super.apresentarMsgNegocio(Severity.INFO, "MAM_MENSAGEM_INCLUSAO_DESCRITOR", this.mamFluxogramaSelecionado.getDescricao(),
						mamProtClassifRisco.getDescricao());
			} else {
				super.apresentarMsgNegocio(Severity.INFO, "MAM_MENSAGEM_ALTERACAO_DESCRITOR", this.mamFluxogramaSelecionado.getDescricao(),
						mamProtClassifRisco.getDescricao());
			}

		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public void onSliderChange(TabChangeEvent event) {
		
		if(event != null && event.getTab() != null) {
			if(SLIDER_0.equals(event.getTab().getId())) {
				this.currentSlider = 0;
			} else if(SLIDER_1.equals(event.getTab().getId())) {
				this.currentSlider = 1;
			} else if(SLIDER_2.equals(event.getTab().getId())) {
				this.currentSlider = 2;
			} else if(SLIDER_3.equals(event.getTab().getId())) {
				this.currentSlider = 3;
			}
		}
		
	}
		
	/**
	 * Limpa os campos de edição
	 */
	public void limparEdicaoFluxograma() {
		this.mamFluxogramaSelecionado = null;
		this.limparCamposEdicaoFluxograma();
	}
	
	public void cancelarEdicaoFluxograma() {
		limparEdicaoFluxograma();
		this.edicaoDescritor = false;
	}
	/**
	 * Limpa os campos de edição
	 */
	public void limparCamposEdicaoFluxograma() {
		this.mamFluxograma = new MamFluxograma();
		this.indSituacaoFluxograma = true;
		this.edicaoFluxograma = false;
	}

	/**
	 * Limpa os campos de edição de Descritor
	 */
	public void limparEdicaoDescritor() {
		this.mamDescritorSelecionado = null;
		this.limparCamposEdicaoDescritor();
	}

	/**
	 * Limpa os campos de edição de Descritor
	 */
	public void limparCamposEdicaoDescritor() {
		this.mamDescritor = new MamDescritor();
		this.indSituacaoDescritor = true;
		this.edicaoDescritor = false;
	}
	
	public void cancelarCamposEdicaoDescritor() {
		limparEdicaoDescritor();
	}
	
	

	/**
	 * Popula as listas de itens obrigatórios
	 */
	public void pesquisarItensObrigatorios() {
		this.pesquisaAtivaItensObrigatorios = true;
		if (this.mamDescritorSelecionado == null) {
			this.dataModelItemSinalVital.clear();
			this.dataModelItemExame.clear();
			this.dataModelItemMedicacao.clear();
			this.dataModelItemGeral.clear();
		} else {
			try {
				emergenciaFacade.popularItensObrigatorios(mamDescritorSelecionado, dataModelItemSinalVital, dataModelItemExame, dataModelItemMedicacao,
						dataModelItemGeral);
			} catch (ApplicationBusinessException e) {
				super.apresentarExcecaoNegocio(e);
			}
		}
	}

	/**
	 * Persiste um registro de MamObrigatoriedade para item
	 */
	private void adicionarObrigatoriedadeItem(Boolean situacaoItemInput, Integer itemSeq, String mensagem, String aba) {
		try {
			ItemObrigatorioVO item = new ItemObrigatorioVO();
			item.setIndSituacaoObr(situacaoItemInput);
			item.setSeqItem(itemSeq);
			if (ABA_SINAL_VITAL.equals(aba)) {
				this.setAbaSelecionada(TAB_ABA_SINAL_VITAL);
				this.emergenciaFacade.inserirObrigatoriedadeSinalVital(mamDescritorSelecionado.getSeq(), item);
			} else if (ABA_EXAME.equals(aba)) {
				this.setAbaSelecionada(TAB_ABA_EXAME);
				this.emergenciaFacade.inserirObrigatoriedadeExame(mamDescritorSelecionado.getSeq(), item);
			} else if (ABA_MEDICACAO.equals(aba)) {
				this.setAbaSelecionada(TAB_ABA_MEDICACAO);
				this.emergenciaFacade.inserirObrigatoriedadeMedicacao(mamDescritorSelecionado.getSeq(), item);
			} else if (ABA_DADOS_GERAIS.equals(aba)) {
				this.setAbaSelecionada(TAB_ABA_DADOS_GERAIS);
				this.emergenciaFacade.inserirObrigatoriedadeGeral(mamDescritorSelecionado.getSeq(), item);
			}
			this.limparInclusaoItem(aba);
			this.pesquisarItensObrigatorios();
			super.apresentarMsgNegocio(Severity.INFO, mensagem);
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do botão ATIVAR/INATIVAR de itens
	 */
	private void ativarInativarItem(ItemObrigatorioVO item, String mensagem, String aba) {
		try {
			if (item != null) {
				this.emergenciaFacade.ativarDesativarMamObrigatoriedade(item);
				this.limparItemSelecionado(aba);
				this.pesquisarItensObrigatorios();
				super.apresentarMsgNegocio(Severity.INFO, mensagem);
			}
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do botão EXCLUIR de itens
	 */
	private void excluirItem(ItemObrigatorioVO item, String mensagem, String aba) {
		try {
			if (item != null) {
				this.emergenciaFacade.removerMamObrigatoriedade(item);
				this.limparItemSelecionado(aba);
				this.pesquisarItensObrigatorios();
				super.apresentarMsgNegocio(Severity.INFO, mensagem);
			}
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	private void limparItemSelecionado(String aba) {
		if (ABA_SINAL_VITAL.equals(aba)) {
			this.itemSinalVitalSelecionado = null;
		} else if (ABA_EXAME.equals(aba)) {
			this.itemExameSelecionado = null;
		} else if (ABA_MEDICACAO.equals(aba)) {
			this.itemMedicacaoSelecionado = null;
		} else if (ABA_DADOS_GERAIS.equals(aba)) {
			this.itemGeralSelecionado = null;
		}
	}

	/**
	 * Limpa os campos de edição de item de sinal vital
	 */
	private void limparInclusaoItem(String aba) {
		if (ABA_SINAL_VITAL.equals(aba)) {
			this.situacaoItemSinalVitalInput = Boolean.TRUE;
			this.itemSinalVitalInput = null;
		} else if (ABA_EXAME.equals(aba)) {
			this.situacaoItemExameInput = Boolean.TRUE;
			this.itemExameInput = null;
		} else if (ABA_MEDICACAO.equals(aba)) {
			this.situacaoItemMedicacaoInput = Boolean.TRUE;
			this.itemMedicacaoInput = null;
		} else if (ABA_DADOS_GERAIS.equals(aba)) {
			this.situacaoItemGeralInput = Boolean.TRUE;
			this.itemGeralInput = null;
		}
	}

	// ========================================= SINAIS VITAIS

	/**
	 * Para popular a suggestionbox
	 * 
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ItemObrigatorioVO> pesquisarSinaisVitais(String parametro) {
		try {
			return  this.returnSGWithCount(emergenciaFacade.pesquisarSinaisVitais((String) parametro),pesquisarSinaisVitaisCount(parametro));
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
			return Collections.emptyList();
		}
	}

	/**
	 * Para popular a suggestionbox
	 * 
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarSinaisVitaisCount(String parametro) {
		try {
			return emergenciaFacade.pesquisarSinaisVitaisCount((String) parametro);
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
			return 0l;
		}
	}

	/**
	 * Persiste um registro de MamObrigatoriedade para Sinal Vital, executando trigger de pré-insert
	 */
	public void adicionarObrigatoriedadeItemSinalVital() {
		this.adicionarObrigatoriedadeItem(situacaoItemSinalVitalInput, itemSinalVitalInput.getSeqItem(), "MENSAGEM_SUCESSO_ADICAO_SINAL_VITAL", ABA_SINAL_VITAL);
	}

	/**
	 * Ação do botão ATIVAR/INATIVAR de sinais vitais
	 */
	public void ativarInativarItemSinalVital() {
		this.ativarInativarItem(this.itemSinalVitalSelecionado, "SUCESSO_ALTERACAO_SITUACAO_SINAL_VITAL", ABA_SINAL_VITAL);
	}

	/**
	 * Ação do botão EXCLUIR de sinais vitais
	 */
	public void excluirItemSinalVital() {
		this.excluirItem(this.itemSinalVitalSelecionado, "MENSAGEM_SUCESSO_EXCLUSAO_SINAL_VITAL", ABA_SINAL_VITAL);
	}

	// ========================================= EXAMES

	/**
	 * Para popular a suggestionbox
	 * 
	 * @param param
	 * @return
	 */
	public List<MamItemExame> pesquisarMamItemExameAtivosPorSeqOuDescricao(String parametro) {
		return  this.returnSGWithCount(emergenciaFacade.pesquisarMamItemExameAtivosPorSeqOuDescricao((String) parametro, 100),pesquisarMamItemExameAtivosPorSeqOuDescricaoCount(parametro));
	}

	/**
	 * Para popular a suggestionbox
	 * 
	 * @param param
	 * @return
	 */
	public Long pesquisarMamItemExameAtivosPorSeqOuDescricaoCount(String parametro) {
		return emergenciaFacade.pesquisarMamItemExameAtivosPorSeqOuDescricaoCount((String) parametro);
	}

	/**
	 * Persiste um registro de MamObrigatoriedade para Exame, executando trigger de pré-insert
	 */
	public void adicionarObrigatoriedadeItemExame() {
		this.adicionarObrigatoriedadeItem(situacaoItemExameInput, itemExameInput.getSeq(), "MENSAGEM_SUCESSO_ADICAO_EXAME", ABA_EXAME);
	}

	/**
	 * Ação do botão ATIVAR/INATIVAR de exames
	 */
	public void ativarInativarItemExame() {
		this.ativarInativarItem(this.itemExameSelecionado, "SUCESSO_ALTERACAO_SITUACAO_EXAME", ABA_EXAME);
	}

	/**
	 * Ação do botão EXCLUIR de exames
	 */
	public void excluirItemExame() {
		this.excluirItem(this.itemExameSelecionado, "MENSAGEM_SUCESSO_EXCLUSAO_EXAME", ABA_EXAME);
	}

	// ========================================= MEDICACOES

	/**
	 * Para popular a suggestionbox
	 * 
	 * @param param
	 * @return
	 */
	public List<MamItemMedicacao> pesquisarMamItemMedicacaoAtivosPorSeqOuDescricao(String parametro) {
		return  this.returnSGWithCount(emergenciaFacade.pesquisarMamItemMedicacaoAtivosPorSeqOuDescricao((String) parametro, 100),pesquisarMamItemMedicacaoAtivosPorSeqOuDescricaoCount(parametro));
	}

	/**
	 * Para popular a suggestionbox
	 * 
	 * @param param
	 * @return
	 */
	public Long pesquisarMamItemMedicacaoAtivosPorSeqOuDescricaoCount(String parametro) {
		return emergenciaFacade.pesquisarMamItemMedicacaoAtivosPorSeqOuDescricaoCount((String) parametro);
	}

	/**
	 * Persiste um registro de MamObrigatoriedade para Medicacao, executando trigger de pré-insert
	 */
	public void adicionarObrigatoriedadeItemMedicacao() {
		this.adicionarObrigatoriedadeItem(situacaoItemMedicacaoInput, itemMedicacaoInput.getSeq(), "MENSAGEM_SUCESSO_ADICAO_MEDICACAO", ABA_MEDICACAO);
	}

	/**
	 * Ação do botão ATIVAR/INATIVAR de Medicacoes
	 */
	public void ativarInativarItemMedicacao() {
		this.ativarInativarItem(this.itemMedicacaoSelecionado, "SUCESSO_ALTERACAO_SITUACAO_MEDICACAO", ABA_MEDICACAO);
	}

	/**
	 * Ação do botão EXCLUIR de Medicacoes
	 */
	public void excluirItemMedicacao() {
		this.excluirItem(this.itemMedicacaoSelecionado, "MENSAGEM_SUCESSO_EXCLUSAO_MEDICACAO", ABA_MEDICACAO);
	}

	// ========================================= GERAIS

	/**
	 * Para popular a suggestionbox
	 * 
	 * @param param
	 * @return
	 */
	public List<MamItemGeral> pesquisarMamItemGeralAtivosPorSeqOuDescricao(String parametro) {
		return  this.returnSGWithCount(emergenciaFacade.pesquisarMamItemGeralAtivosPorSeqOuDescricao((String) parametro, 100),pesquisarMamItemGeralAtivosPorSeqOuDescricaoCount(parametro));
	}

	/**
	 * Para popular a suggestionbox
	 * 
	 * @param param
	 * @return
	 */
	public Long pesquisarMamItemGeralAtivosPorSeqOuDescricaoCount(String parametro) {
		return emergenciaFacade.pesquisarMamItemGeralAtivosPorSeqOuDescricaoCount((String) parametro);
	}

	/**
	 * Persiste um registro de MamObrigatoriedade para Geral, executando trigger de pré-insert
	 */
	public void adicionarObrigatoriedadeItemGeral() {
		this.adicionarObrigatoriedadeItem(situacaoItemGeralInput, itemGeralInput.getSeq(), "MENSAGEM_SUCESSO_ADICAO_DADO_GERAL", ABA_DADOS_GERAIS);
	}

	/**
	 * Ação do botão ATIVAR/INATIVAR de dados gerais
	 */
	public void ativarInativarItemGeral() {
		this.ativarInativarItem(this.itemGeralSelecionado, "SUCESSO_ALTERACAO_SITUACAO_DADO_GERAL", ABA_DADOS_GERAIS);
	}

	/**
	 * Ação do botão EXCLUIR de dados gerais
	 */
	public void excluirItemGeral() {
		this.excluirItem(this.itemGeralSelecionado, "MENSAGEM_SUCESSO_EXCLUSAO_DADO_GERAL", ABA_DADOS_GERAIS);
	}

	// ----- AUX

	/**
	 * Cria um Boolean à partir de um DominioSituacao
	 * 
	 * @param dominioSituacao
	 * @return
	 */
	public Boolean getBoolDominioSituacao(DominioSituacao dominioSituacao) {
		return dominioSituacao != null && dominioSituacao.equals(DominioSituacao.A);
	}

	public boolean usuarioTemPermissao(String componente, String metodo) {
		return getPermissionService().usuarioTemPermissao(this.obterLoginUsuarioLogado(), componente, metodo);
	}

	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}
	// ----- GETS e SETS
	public List<MamFluxograma> getDataModelFluxograma() {
		return dataModelFluxograma;
	}
	public void setDataModelFluxograma(List<MamFluxograma> dataModelFluxograma) {
		this.dataModelFluxograma = dataModelFluxograma;
	}

	public MamProtClassifRisco getMamProtClassifRisco() {
		return mamProtClassifRisco;
	}
	public void setMamProtClassifRisco(MamProtClassifRisco mamProtClassifRisco) {
		this.mamProtClassifRisco = mamProtClassifRisco;
		this.limparPesquisaFluxograma();
	}
	public boolean isPesquisaAtivaFluxograma() {
		return pesquisaAtivaFluxograma;
	}
	public void setPesquisaAtivaFluxograma(boolean pesquisaAtivaFluxograma) {
		this.pesquisaAtivaFluxograma = pesquisaAtivaFluxograma;
	}
	public Boolean getIndSituacaoFluxograma() {
		return indSituacaoFluxograma;
	}
	public void setIndSituacaoFluxograma(Boolean indSituacaoFluxograma) {
		this.indSituacaoFluxograma = indSituacaoFluxograma;
	}
	public boolean isBotaoVoltar() {
		return botaoVoltar;
	}
	public void setBotaoVoltar(boolean botaoVoltar) {
		this.botaoVoltar = botaoVoltar;
	}

	public MamFluxograma getMamFluxograma() {
		return mamFluxograma;
	}
	public void setMamFluxograma(MamFluxograma mamFluxograma) {
		this.mamFluxograma = mamFluxograma;
	}
	public MamFluxograma getMamFluxogramaSelecionado() {
		return mamFluxogramaSelecionado;
	}
	public void setMamFluxogramaSelecionado(MamFluxograma mamFluxogramaSelecionado) {
		this.mamFluxogramaSelecionado = mamFluxogramaSelecionado;
	}
	public boolean isEdicaoFluxograma() {
		return edicaoFluxograma;
	}
	public void setEdicaoFluxograma(boolean edicaoFluxograma) {
		this.edicaoFluxograma = edicaoFluxograma;
	}
	public List<MamDescritor> getDataModelDescritor() {
		return dataModelDescritor;
	}
	public void setDataModelDescritor(List<MamDescritor> dataModelDescritor) {
		this.dataModelDescritor = dataModelDescritor;
	}
	public MamDescritor getMamDescritor() {
		return mamDescritor;
	}
	public void setMamDescritor(MamDescritor mamDescritor) {
		this.mamDescritor = mamDescritor;
	}
	public MamDescritor getMamDescritorSelecionado() {
		return mamDescritorSelecionado;
	}
	public void setMamDescritorSelecionado(MamDescritor mamDescritorSelecionado) {
		this.mamDescritorSelecionado = mamDescritorSelecionado;
	}
	public boolean isEdicaoDescritor() {
		return edicaoDescritor;
	}
	public void setEdicaoDescritor(boolean edicaoDescritor) {
		this.edicaoDescritor = edicaoDescritor;
	}
	public Boolean getIndSituacaoDescritor() {
		return indSituacaoDescritor;
	}
	public void setIndSituacaoDescritor(Boolean indSituacaoDescritor) {
		this.indSituacaoDescritor = indSituacaoDescritor;
	}
	public boolean isPesquisaAtivaDescritor() {
		return pesquisaAtivaDescritor;
	}
	public void setPesquisaAtivaDescritor(boolean pesquisaAtivaDescritor) {
		this.pesquisaAtivaDescritor = pesquisaAtivaDescritor;
	}
	public boolean isPermissaoManter() {
		return permissaoManter;
	}
	public void setPermissaoManter(boolean permissaoManter) {
		this.permissaoManter = permissaoManter;
	}
	public Integer getAbaSelecionada() {
		return abaSelecionada;
	}
	public void setAbaSelecionada(Integer abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}
	public List<ItemObrigatorioVO> getDataModelItemSinalVital() {
		return dataModelItemSinalVital;
	}
	public void setDataModelItemSinalVital(List<ItemObrigatorioVO> dataModelItemSinalVital) {
		this.dataModelItemSinalVital = dataModelItemSinalVital;
	}
	public List<ItemObrigatorioVO> getDataModelItemMedicacao() {
		return dataModelItemMedicacao;
	}
	public void setDataModelItemMedicacao(List<ItemObrigatorioVO> dataModelItemMedicacao) {
		this.dataModelItemMedicacao = dataModelItemMedicacao;
	}
	public List<ItemObrigatorioVO> getDataModelItemExame() {
		return dataModelItemExame;
	}
	public void setDataModelItemExame(List<ItemObrigatorioVO> dataModelItemExame) {
		this.dataModelItemExame = dataModelItemExame;
	}
	public List<ItemObrigatorioVO> getDataModelItemGeral() {
		return dataModelItemGeral;
	}
	public void setDataModelItemGeral(List<ItemObrigatorioVO> dataModelItemGeral) {
		this.dataModelItemGeral = dataModelItemGeral;
	}
	public ItemObrigatorioVO getItemSinalVitalInput() {
		return itemSinalVitalInput;
	}
	public void setItemSinalVitalInput(ItemObrigatorioVO itemSinalVitalInput) {
		this.itemSinalVitalInput = itemSinalVitalInput;
	}
	public Boolean getSituacaoItemSinalVitalInput() {
		return situacaoItemSinalVitalInput;
	}
	public void setSituacaoItemSinalVitalInput(Boolean situacaoItemSinalVitalInput) {
		this.situacaoItemSinalVitalInput = situacaoItemSinalVitalInput;
	}
	public ItemObrigatorioVO getItemSinalVitalSelecionado() {
		return itemSinalVitalSelecionado;
	}
	public void setItemSinalVitalSelecionado(ItemObrigatorioVO itemSinalVitalSelecionado) {
		this.itemSinalVitalSelecionado = itemSinalVitalSelecionado;
	}
	public MamItemExame getItemExameInput() {
		return itemExameInput;
	}
	public void setItemExameInput(MamItemExame itemExameInput) {
		this.itemExameInput = itemExameInput;
	}
	public Boolean getSituacaoItemExameInput() {
		return situacaoItemExameInput;
	}
	public void setSituacaoItemExameInput(Boolean situacaoItemExameInput) {
		this.situacaoItemExameInput = situacaoItemExameInput;
	}
	public ItemObrigatorioVO getItemExameSelecionado() {
		return itemExameSelecionado;
	}
	public void setItemExameSelecionado(ItemObrigatorioVO itemExameSelecionado) {
		this.itemExameSelecionado = itemExameSelecionado;
	}
	public MamItemMedicacao getItemMedicacaoInput() {
		return itemMedicacaoInput;
	}
	public void setItemMedicacaoInput(MamItemMedicacao itemMedicacaoInput) {
		this.itemMedicacaoInput = itemMedicacaoInput;
	}
	public Boolean getSituacaoItemMedicacaoInput() {
		return situacaoItemMedicacaoInput;
	}
	public void setSituacaoItemMedicacaoInput(Boolean situacaoItemMedicacaoInput) {
		this.situacaoItemMedicacaoInput = situacaoItemMedicacaoInput;
	}
	public ItemObrigatorioVO getItemMedicacaoSelecionado() {
		return itemMedicacaoSelecionado;
	}
	public void setItemMedicacaoSelecionado(ItemObrigatorioVO itemMedicacaoSelecionado) {
		this.itemMedicacaoSelecionado = itemMedicacaoSelecionado;
	}
	public MamItemGeral getItemGeralInput() {
		return itemGeralInput;
	}
	public void setItemGeralInput(MamItemGeral itemGeralInput) {
		this.itemGeralInput = itemGeralInput;
	}
	public Boolean getSituacaoItemGeralInput() {
		return situacaoItemGeralInput;
	}
	public void setSituacaoItemGeralInput(Boolean situacaoItemGeralInput) {
		this.situacaoItemGeralInput = situacaoItemGeralInput;
	}
	public ItemObrigatorioVO getItemGeralSelecionado() {
		return itemGeralSelecionado;
	}
	public void setItemGeralSelecionado(ItemObrigatorioVO itemGeralSelecionado) {
		this.itemGeralSelecionado = itemGeralSelecionado;
	}
	public boolean isPesquisaAtivaItensObrigatorios() {
		return pesquisaAtivaItensObrigatorios;
	}
	public void setPesquisaAtivaItensObrigatorios(boolean pesquisaAtivaItensObrigatorios) {
		this.pesquisaAtivaItensObrigatorios = pesquisaAtivaItensObrigatorios;
	}

	public Integer getCurrentSlider() {
		return currentSlider;
	}

	public void setCurrentSlider(Integer currentSlider) {
		this.currentSlider = currentSlider;
	}

}
