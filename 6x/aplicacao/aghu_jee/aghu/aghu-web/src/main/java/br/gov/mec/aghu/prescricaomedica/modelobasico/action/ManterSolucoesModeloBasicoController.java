package br.gov.mec.aghu.prescricaomedica.modelobasico.action;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioDuracaoCalculo;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPadronizado;
import br.gov.mec.aghu.dominio.DominioTipoCalculoDose;
import br.gov.mec.aghu.dominio.DominioTipoMedicaoPeso;
import br.gov.mec.aghu.dominio.DominioUnidadeBaseParametroCalculo;
import br.gov.mec.aghu.dominio.DominioUnidadeHorasMinutos;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.internacao.vo.MedicamentoVO;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmItemModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.MpmParamCalculoPrescricao;
import br.gov.mec.aghu.model.MpmParamCalculoPrescricaoId;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.prescricaomedica.action.ManterPrescricaoMedicamentoExceptionCode;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade;
import br.gov.mec.aghu.prescricaomedica.modelobasico.vo.ModeloBasicoMedicamentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.DadosPesoAlturaVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.view.VMpmDosagem;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class ManterSolucoesModeloBasicoController extends ActionController {

	private static final long serialVersionUID = -3181085455411892222L;
	private static final Log LOG = LogFactory.getLog(ManterSolucoesModeloBasicoController.class);

	private final String PAGE_MANTER_ITENS = "manterItensModeloBasico";

	@EJB
	private IModeloBasicoFacade modeloBasicoFacade;

	// Chave do Modelo de Medicamento recebida via Parametro.
	// Parametros base para a manutencao da Controller.
	private Integer modeloBasicoPrescricaoSeq;
	private Integer seqItemModelo;

	// O Modelo Basico ao qual as Solucoes criadas/editadas nesta tela serao
	// associadas. Obrigatorio.
	private MpmModeloBasicoPrescricao modeloBasico;
	// Lista de Solucoes existentes associadas ao Modelo basico.
	private List<ModeloBasicoMedicamentoVO> listaSolucoesDoModeloBasico;

	// A Solucao que esta em edicao no momento.
	private MpmModeloBasicoMedicamento modeloBasicoMedicamento;
	// Os itens filhos da Solucao em exclusao.
	private List<MpmItemModeloBasicoMedicamento> itensModeloBasicoMedicamentoExcluidos;
	// Os itens filhos da Solucao em edicao.
	private List<MpmItemModeloBasicoMedicamento> itensModeloBasicoMedicamento;
	// De-Para, necessario devido a re-utilizacao da tela de Solucao da
	// Prescricao de Solucao.
	private Map<MpmItemPrescricaoMdtoId, MpmItemModeloBasicoMedicamento> mapItensModeloBasicoMedicamento;

	private boolean confirmaVoltar;

	// Campos para o controle de alteração no formulário do item
	private boolean campoAlteradoFormularioItem;
	private ModeloBasicoMedicamentoVO itemEdicao;
	
	private DadosPesoAlturaVO dadosPesoAlturaVO;
	
	private MpmItemPrescricaoMdto itemSelecionadoExclusao;

	private enum ManterSolucoesModeloBasicoControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_MODELO_NAO_INFORMADO, MPM_01128, MENSAGEM_AO_MENOS_UM_MEDICAMENTO_DEVE_SER_INFORMADO, MENSAGEM_VIA_NAO_PERMITE_BI;
	}

	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	/**
	 * Metodo utilizado ao iniciar a tela.<br>
	 * e ao re-iniciar tambem (ex. acao limpar).
	 * 
	 */
	public void iniciar() {
	 

		try {
			if (this.modeloBasicoPrescricaoSeq == null) {
				throw new ApplicationBusinessException(ManterSolucoesModeloBasicoControllerExceptionCode.MENSAGEM_MODELO_NAO_INFORMADO);
			}
			this.initController();

			this.modeloBasico = this.modeloBasicoFacade.obterModeloBasico(modeloBasicoPrescricaoSeq);

			this.loadListaDeItensSolucaoDoModeloSelecionado();

			this.loadDadosEdicaoDaSolucaoSelecionada();

		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}
	
	}

	private void initController() {
		this.inicio();

		this.modeloBasico = null;
		this.listaSolucoesDoModeloBasico = null;
		this.modeloBasicoMedicamento = null;

		this.itensModeloBasicoMedicamentoExcluidos = null;
		this.itensModeloBasicoMedicamento = null;
		this.mapItensModeloBasicoMedicamento = null;
		this.dadosPesoAlturaVO = new DadosPesoAlturaVO();
	}

	/**
	 * Se existe um Item selecionado busca os dados deste item<br>
	 * e delega o set nos campos da tela para um metodo gancho.
	 */
	private void loadDadosEdicaoDaSolucaoSelecionada() {
		if (this.getSeqItemModelo() != null) {
			this.setModeloBasicoMedicamento(this.getModeloBasicoFacade().obterModeloBasicoSolucao(this.getModeloBasicoPrescricaoSeq(), this.getSeqItemModelo()));
			this.doSetModel();
		}
	}

	/**
	 * Necessita que o Model tenha sido setado.<br>
	 * Atraves do metodo setModeloBasicoMedicamento().
	 * 
	 */
	private void doSetModel() {
		// O Model deve estar setado.
		if (this.getModeloBasicoMedicamento() != null) {

			this.setEdicaoPrescricaoMedicamento(true);
			this.setEdicao(false);

			// = prescricaoMedicamento.getViaAdministracao();
			this.setVia(this.getModeloBasicoMedicamento().getViaAdministracao());
			// = prescricaoMedicamento.getFrequencia();
			this.setFrequencia(this.getModeloBasicoMedicamento().getFrequencia());
			// = prescricaoMedicamento.getQtdeHorasCorrer();
			this.setQtdeHorasCorrer((this.getModeloBasicoMedicamento().getQuantidadeHorasCorrer() != null) ? this.getModeloBasicoMedicamento().getQuantidadeHorasCorrer().shortValue() : null);
			// this.setUnidHorasCorrer();//=
			// prescricaoMedicamento.getUnidHorasCorrer();
			// = prescricaoMedicamento.getGotejo();
			this.setGotejo(this.getModeloBasicoMedicamento().getGotejo());
			// = prescricaoMedicamento.getTipoVelocAdministracao();
			this.setTipoVelocAdministracao(this.getModeloBasicoMedicamento().getTipoVelocidadeAdministracao());
			// = prescricaoMedicamento.getIndBombaInfusao();
			// this.setIndBombaInfusao(this.getModeloBasicoMedicamento().getIndSolucao());
			// = prescricaoMedicamento.getObservacao();
			this.setObservacao(this.getModeloBasicoMedicamento().getObservacao());
			// = (prescricaoMedicamento.getHoraInicioAdministracao() != null)
			// ? (new
			// SimpleDateFormat("HH:mm")).format(prescricaoMedicamento.getHoraInicioAdministracao())
			// :null;
			this.setHoraAdministracao((getModeloBasicoMedicamento().getHoraInicioAdministracao() != null) ? (getModeloBasicoMedicamento()
					.getHoraInicioAdministracao()) : null);
			// = prescricaoMedicamento.getIndSeNecessario();
			this.setIndSeNecessario(this.getModeloBasicoMedicamento().getIndSeNecessario());

			this.setTipoAprazamento(this.getModeloBasicoMedicamento().getTipoFrequenciaAprazamento());

			this.setUnidHorasCorrer(getModeloBasicoMedicamento().getUnidHorasCorrer());
			this.setIndBombaInfusao(getModeloBasicoMedicamento().getIndBombaInfusao());

			// Transforma MpmItemModeloBasicoMedicamento em
			// MpmItemPrescricaoMdto
			List<MpmItemPrescricaoMdto> list = new LinkedList<MpmItemPrescricaoMdto>();

			List<MpmItemModeloBasicoMedicamento> itemModBasMeds = new ArrayList<MpmItemModeloBasicoMedicamento>();
			try {
				itemModBasMeds = this.modeloBasicoFacade.obterItemMedicamento(getModeloBasicoMedicamento().getId().getModeloBasicoPrescricaoSeq(), getModeloBasicoMedicamento().getId().getSeq());
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}

			for (MpmItemModeloBasicoMedicamento itemModBasMed : itemModBasMeds) {
				MpmItemPrescricaoMdto itemPrescMdto = new MpmItemPrescricaoMdto();

				MpmItemPrescricaoMdtoId id = new MpmItemPrescricaoMdtoId();
				id.setPmdAtdSeq(itemModBasMed.getId().getModeloBasicoPrescricaoSeq());
				id.setPmdSeq(itemModBasMed.getId().getModeloBasicoMedicamentoSeq().longValue());
				id.setMedMatCodigo(itemModBasMed.getId().getMedicamentoMaterialCodigo());
				id.setSeqp(itemModBasMed.getId().getSeqp().shortValue());

				itemPrescMdto.setId(id);
				itemPrescMdto.setMedicamento(itemModBasMed.getMedicamento());
				itemPrescMdto.setDose(itemModBasMed.getDose());
				itemPrescMdto.setFormaDosagem(itemModBasMed.getFormaDosagem());
				itemPrescMdto.setObservacao(itemModBasMed.getObservacao());

				list.add(itemPrescMdto);
				this.getMapItensModeloBasicoMedicamento().put(id, itemModBasMed);
			}
			// = new
			// ArrayList<MpmItemPrescricaoMdto>(prescricaoMedicamento.getItensPrescricaoMdtos());
			this.setListaMedicamentosSolucao(list);
		}
	}

	/**
	 * Faz a carga dos dos itens de solucao associados ao modelo basico
	 * apresentado nesta tela.
	 */
	private void loadListaDeItensSolucaoDoModeloSelecionado() {
		List<MpmModeloBasicoMedicamento> list = this.getModeloBasicoFacade().obterListaSolucoesDoModeloBasico(this.getModeloBasico());

		List<ModeloBasicoMedicamentoVO> vOs = new LinkedList<ModeloBasicoMedicamentoVO>();
		for (MpmModeloBasicoMedicamento modeloBasicoMedicamento : list) {
			vOs.add(new ModeloBasicoMedicamentoVO(modeloBasicoMedicamento));
		}

		this.setListaSolucoesDoModeloBasico(vOs);
	}

	public void prepararAlterar(ModeloBasicoMedicamentoVO item) {

		this.itemEdicao = item;
		if (Boolean.FALSE.equals(this.isCampoAlteradoFormularioItem())) {
			this.editarItem();
		}
	}

	// Editar
	public void editarItem() {
		this.desmarcarAlteracaoCampoFormularioItem();
		this.editarSolucao(this.itemEdicao);
	}

	/**
	 * Acao de editar uma Solucao existente.<br>
	 * Acionada pelo icone do lapis na lista que esta no final da pagina.
	 * 
	 * @param modBasMedicamentoVO
	 */
	public void editarSolucao(ModeloBasicoMedicamentoVO modBasMedicamentoVO) {
		this.modeloBasicoPrescricaoSeq = modBasMedicamentoVO.getModeloBasicoMedicamento().getId().getModeloBasicoPrescricaoSeq();
		this.seqItemModelo = modBasMedicamentoVO.getModeloBasicoMedicamento().getId().getSeq();
		this.iniciar();
	}

	/**
	 * Metodo principal da tela.<br>
	 * Utilizado para: <b>Inserir</b> e <b>Editar</b> Solucao.
	 */
	public void gravar() {

		try {
			if (this.getQtdeHorasCorrer() != null && this.getUnidHorasCorrer() == null) {
				throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.OBRIGATORIO_PREENCHER_UNIDADE_AO_PREENCHER_CORRER_EM);
			} else if (this.getQtdeHorasCorrer() == null && this.getUnidHorasCorrer() != null) {
				throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.OBRIGATORIO_PREENCHER_CORRER_EM_AO_PREENCHER_UNIDADE);
			} else if (this.getTipoAprazamento() == null) {
				apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Tipo de Aprazamento");
				return;
			} else if (this.verificaRequiredFrequencia() && this.getFrequencia() == null) {
				apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Frequência");
				return;
			} else if (getIndBombaInfusao() != null && getIndBombaInfusao() && !getVia().getIndPermiteBi()) {
				throw new ApplicationBusinessException(ManterSolucoesModeloBasicoControllerExceptionCode.MENSAGEM_VIA_NAO_PERMITE_BI);
			} else {
				this.validaDados();

				MpmModeloBasicoMedicamento solucao = this.getModel();
				List<MpmItemModeloBasicoMedicamento> listaExcluidos = this.getItensModeloBasicoMedicamentoExcluidos();

				String msg = this.getModeloBasicoFacade().gravarSolucao(solucao, getItensModeloBasicoMedicamento(), listaExcluidos);

				this.limpar();
				apresentarMsgNegocio(Severity.INFO, msg);
			}
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);

			if (getModeloBasicoMedicamento() != null && getModeloBasicoMedicamento().getId() == null) {
				setModeloBasicoMedicamento(null);
			}

			super.apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

	}

	protected void validaDados() throws BaseException {
		if (this.getListaMedicamentosSolucao() == null || this.getListaMedicamentosSolucao().isEmpty()) {
			throw new ApplicationBusinessException(ManterSolucoesModeloBasicoControllerExceptionCode.MENSAGEM_AO_MENOS_UM_MEDICAMENTO_DEVE_SER_INFORMADO);
		}

		this.validaDadosParte2();
	}

	/**
	 * Metodo utilizado para recuperar um Solucao com as alteracoes efetuadas na
	 * tela.
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private MpmModeloBasicoMedicamento getModel() throws ApplicationBusinessException {
		if (this.getModeloBasicoMedicamento() == null) {
			this.setModeloBasicoMedicamento(new MpmModeloBasicoMedicamento());
		}

		this.getModeloBasicoMedicamento().setModeloBasicoPrescricao(this.getModeloBasico());
		if (!this.getListaMedicamentosSolucao().isEmpty() && this.getListaMedicamentosSolucao().size() > 1) {
			this.getModeloBasicoMedicamento().setIndSolucao(Boolean.TRUE);
		} else {
			this.getModeloBasicoMedicamento().setIndSolucao(Boolean.FALSE);
		}

		this.getModeloBasicoMedicamento().setViaAdministracao(this.getVia());
		this.getModeloBasicoMedicamento().setFrequencia(this.getFrequencia());
		this.getModeloBasicoMedicamento().setQuantidadeHorasCorrer((this.getQtdeHorasCorrer() != null) ? this.getQtdeHorasCorrer().byteValue() : null);
		// this.setUnidHorasCorrer();//=
		// prescricaoMedicamento.getUnidHorasCorrer();
		this.getModeloBasicoMedicamento().setGotejo(this.getGotejo());
		this.getModeloBasicoMedicamento().setTipoVelocidadeAdministracao(this.getTipoVelocAdministracao());
		this.getModeloBasicoMedicamento().setObservacao(this.getObservacao());

        String horaFormatada = DateUtil.dataToString(getHoraAdministracao(), "HH:mm");

		this.getModeloBasicoMedicamento().setHoraInicioAdministracao((StringUtils.isNotBlank(horaFormatada)) ? this.populaDataHora(horaFormatada) : null);
		this.getModeloBasicoMedicamento().setIndSeNecessario(this.getIndSeNecessario());
		this.getModeloBasicoMedicamento().setTipoFrequenciaAprazamento(this.getTipoAprazamento());
		this.getModeloBasicoMedicamento().setUnidHorasCorrer(getUnidHorasCorrer());
		this.getModeloBasicoMedicamento().setIndBombaInfusao(getIndBombaInfusao());

		for (MpmItemPrescricaoMdto itemPrescMdto : this.getListaMedicamentosSolucao()) {
			MpmItemModeloBasicoMedicamento itemModBasMed;
			if (itemPrescMdto.getId() == null) {
				// Se for um item novo, o id eh nulo.
				itemModBasMed = new MpmItemModeloBasicoMedicamento();

				itemModBasMed.setMedicamento(itemPrescMdto.getMedicamento());
				itemModBasMed.setDose(itemPrescMdto.getDose());
				itemModBasMed.setFormaDosagem(itemPrescMdto.getFormaDosagem());
				itemModBasMed.setObservacao(itemPrescMdto.getObservacao());

				this.adicionarItemModeloMedicamento(itemModBasMed);
			} else {
				// Se tiver um id eh item existente, bastanta atualiar.
				itemModBasMed = this.getMapItensModeloBasicoMedicamento().get(itemPrescMdto.getId());

				itemModBasMed.setMedicamento(itemPrescMdto.getMedicamento());
				itemModBasMed.setDose(itemPrescMdto.getDose());
				itemModBasMed.setFormaDosagem(itemPrescMdto.getFormaDosagem());
				itemModBasMed.setObservacao(itemPrescMdto.getObservacao());
				itemModBasMed.setServidor(this.servidorLogadoFacade.obterServidorLogado());
				// Nao precisa adicionar na lista, pois jah esta lah;
				// O objeto que esta no Map eh o mesmo (referencia) da lista.
				getItensModeloBasicoMedicamento().add(itemModBasMed);
			}
		}
		return this.getModeloBasicoMedicamento();
	}

	public void removerItemModeloMedicamento(MpmItemModeloBasicoMedicamento itemModBasMed) {
		getItensModeloBasicoMedicamento().remove(itemModBasMed);
	}

	public void adicionarItemModeloMedicamento(MpmItemModeloBasicoMedicamento itemModBasMed) {
		if (itemModBasMed != null) {
			itemModBasMed.setId(null);
			itemModBasMed.setModeloBasicoMedicamento(this.modeloBasicoMedicamento);
			getItensModeloBasicoMedicamento().add(itemModBasMed);
		}
	}

	/**
	 * Prepara a tela para um nova insercao.
	 * 
	 */
	public void limpar() {
		this.seqItemModelo = null;
		this.confirmaVoltar = false;
		this.limparParte2();
		this.iniciar();
		desmarcarAlteracaoCampoFormularioItem();
	}

	public void adicionarMedicamento() {

		this.dose = this.dose.setScale(4, RoundingMode.HALF_EVEN).stripTrailingZeros();
		List<VMpmDosagem> dosagens = this.prescricaoMedicaFacade.buscarDosagensMedicamento(this.medicamento.getMatCodigo());
		if (!dosagens.isEmpty()) {
			unidadeDosagem = dosagens.get(0);
		}

		this.adicionarMedicamentoParte2();
		// Verifica se o medicamento foi adicionado
		if (this.getMedicamento() == null) {
			this.marcarAlteracaoCampoFormularioItem();
		}
	}

	public void alterarMedicamento() {
		this.alterarMedicamentoParte2();
		// Verifica se o medicamentofoi adicionado
		if (this.getMedicamento() == null) {
			this.marcarAlteracaoCampoFormularioItem();
		}
	}

	/**
	 * Acao para remocao de Solucoes.<br>
	 * Acionado pelo botao Excluir no final da pagina.<br>
	 * Necessita que algum item tenha sido selecionado para exclusao.
	 * 
	 */
	public void excluir(ModeloBasicoMedicamentoVO item) {
		try {

			this.setModeloBasicoMedicamento(this.getModeloBasicoFacade().obterModeloBasicoSolucao(item.getModeloBasicoMedicamento().getId().getModeloBasicoPrescricaoSeq(), item.getModeloBasicoMedicamento().getId().getSeq()));

			this.modeloBasicoFacade.removerSolucoesSelecionadas(modeloBasicoMedicamento);
			this.loadListaDeItensSolucaoDoModeloSelecionado();
			this.limpar();
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			super.apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public void cancelarModal() {
		this.seqItemModelo = null;
		this.modeloBasicoPrescricaoSeq = null;
	}

	/**
	 * Metodo gancho: utilizado aqui para controle de removidos.<br>
	 * Guarda um Medicamento existente que foi excuido na tela,<br>
	 * para posterior exclusao.
	 * 
	 */
	protected void doRemoverMedicamento(MpmItemPrescricaoMdto itemModBasMedFake) {
		if (itemModBasMedFake != null && itemModBasMedFake.getId() != null) {
			MpmItemPrescricaoMdtoId key = itemModBasMedFake.getId();
			if (this.getMapItensModeloBasicoMedicamento().containsKey(key)) {
				MpmItemModeloBasicoMedicamento itemModBasSolucao = this.getMapItensModeloBasicoMedicamento().get(key);
				this.getItensModeloBasicoMedicamentoExcluidos().add(itemModBasSolucao);
			}
		}
	}

	/**
	 * Metodo re-implementado, pois a logica de busca de medicamentos eh
	 * diferente nesta tela.
	 * 
	 * @see br.gov.mec.aghu.prescricaomedica.action.ManterPrescricaoSolucaoController#listarViasMedicamento(java.lang.String)
	 */
	public List<AfaViaAdministracao> listarViasMedicamento(String strPesquisa) {
		//List<AfaViaAdministracao> lista = null;

		if (this.getTodasAsVias()) {
			//lista = getFarmaciaFacade().getListaTodasAsVias((String) strPesquisa);
			return returnSGWithCount(getFarmaciaFacade().getListaTodasAsVias((String) strPesquisa), listarViasMedicamentoCount(strPesquisa));
		} else {
			if (getListaMedicamentosSolucao() != null && !getListaMedicamentosSolucao().isEmpty()) {
				List<Integer> listaDeIds = new ArrayList<Integer>();
				for (MpmItemPrescricaoMdto itemMedicamento : getListaMedicamentosSolucao()) {
					listaDeIds.add(itemMedicamento.getMedicamento().getMatCodigo());
				}
				//lista = getFarmaciaFacade().getListaViasMedicamento((String) strPesquisa, listaDeIds);
				return returnSGWithCount(getFarmaciaFacade().getListaViasMedicamento((String) strPesquisa, listaDeIds), listarViasMedicamentoCount(strPesquisa));
			}
		}
		return new ArrayList<AfaViaAdministracao>();
	}

	/**
	 * Metodo auxiliar ao <b>listarViasMedicamento</b>
	 */
	public Long listarViasMedicamentoCount(String strPesquisa) {
		Long count = 0L;

		if (getTodasAsVias()) {
			count = getFarmaciaFacade().getListaTodasAsViasCount((String) strPesquisa);
		} else {
			if (getListaMedicamentosSolucao() != null && !getListaMedicamentosSolucao().isEmpty()) {
				List<Integer> listaDeIds = new ArrayList<Integer>();
				for (MpmItemPrescricaoMdto itemMedicamento : getListaMedicamentosSolucao()) {
					listaDeIds.add(itemMedicamento.getMedicamento().getMatCodigo());
				}
				count = getFarmaciaFacade().getListaViasMedicamentoCount((String) strPesquisa, listaDeIds);
			}
		}
		return count;
	}

	/**
	 * Metodo re-implementado para ajustar a inicializacao da controller pai.
	 * 
	 */
	public void inicio() {
		this.setEdicaoPrescricaoMedicamento(false);

		this.setListaMedicamentosAuxDominioPadronizadoSim();
		
		if(this.prescricaoMedica != null){
			AghAtendimentos atdAmbulatorial= aghuFacade.obterAtendimento(this.prescricaoMedica.getId().getAtdSeq(),null,	DominioOrigemAtendimento.getOrigensDePrescricaoAmbulatorial());
			if(atdAmbulatorial != null){
				prescricaoAmbulatorial = Boolean.TRUE;
			}
		}
	}

	public void verificarViaAssociadaAoMedicamento() {
		this.marcarAlteracaoCampoFormularioItem();

		this.setExibirModal(false);
		this.verificarViaAssociadaAoMedicamentoExibirModal();

		// this.verificarViaAssociadaAoMedicamentoBombaInfusao();

		// return this.getExibirModal();
	}

	public String voltar() {
		limpar();
		return PAGE_MANTER_ITENS;
	}

	public String verificaPendencias() {
		if (this.getMedicamentoVO() != null) {
			confirmaVoltar = true;
			return null;
		} else {
			confirmaVoltar = false;
			return PAGE_MANTER_ITENS;
		}
	}

	public void verificarFrequencia() {
		this.marcarAlteracaoCampoFormularioItem();
		if (!this.verificaRequiredFrequencia()) {
			this.frequencia = null;
		}
	}

	// marcar e desmarcarAlteração
	public void marcarAlteracaoCampoFormularioItem() {
		this.setCampoAlteradoFormularioItem(true);
	}

	public void desmarcarAlteracaoCampoFormularioItem() {
		this.setCampoAlteradoFormularioItem(false);
	}
	
	public void removerMedicamentoSolucao() {
		this.setEdicao(false);
		setMatCodigo(null);
		for (Iterator<MpmItemPrescricaoMdto> i = getListaMedicamentosSolucao().iterator(); i.hasNext(); ) {
			MpmItemPrescricaoMdto itemPrs = (MpmItemPrescricaoMdto)i.next();
			if(itemPrs.getMedicamento().getMatCodigo().equals(itemSelecionadoExclusao.getMedicamento().getMatCodigo())) {
				MpmItemModeloBasicoMedicamento itemModBasMed =  this.getMapItensModeloBasicoMedicamento().get(itemPrs.getId());
				itemModBasMed.setMedicamento(itemPrs.getMedicamento());
				itemModBasMed.setDose(itemPrs.getDose());
				itemModBasMed.setFormaDosagem(itemPrs.getFormaDosagem());
				itemModBasMed.setObservacao(itemPrs.getObservacao());
				getItensModeloBasicoMedicamentoExcluidos().add(itemModBasMed);
				i.remove();
				break;
			}
		}
	}

	public IModeloBasicoFacade getModeloBasicoFacade() {
		return modeloBasicoFacade;
	}

	public void setModeloBasicoFacade(IModeloBasicoFacade modeloBasicoFacade) {
		this.modeloBasicoFacade = modeloBasicoFacade;
	}

	public Integer getModeloBasicoPrescricaoSeq() {
		return modeloBasicoPrescricaoSeq;
	}

	public void setModeloBasicoPrescricaoSeq(Integer modeloBasicoPrescricaoSeq) {
		this.modeloBasicoPrescricaoSeq = modeloBasicoPrescricaoSeq;
	}

	public MpmModeloBasicoPrescricao getModeloBasico() {
		return modeloBasico;
	}

	public void setModeloBasico(MpmModeloBasicoPrescricao modeloBasico) {
		this.modeloBasico = modeloBasico;
	}

	public void setSeqItemModelo(Integer seqItemModelo) {
		this.seqItemModelo = seqItemModelo;
	}

	public Integer getSeqItemModelo() {
		return seqItemModelo;
	}

	public void setModeloBasicoMedicamento(MpmModeloBasicoMedicamento modeloBasicoMedicamento) {
		this.modeloBasicoMedicamento = modeloBasicoMedicamento;
	}

	public MpmModeloBasicoMedicamento getModeloBasicoMedicamento() {
		return modeloBasicoMedicamento;
	}

	public void setListaSolucoesDoModeloBasico(List<ModeloBasicoMedicamentoVO> listaSolucoesDoModeloBasico) {
		this.listaSolucoesDoModeloBasico = listaSolucoesDoModeloBasico;
	}

	public List<ModeloBasicoMedicamentoVO> getListaSolucoesDoModeloBasico() {
		return listaSolucoesDoModeloBasico;
	}

	/**
	 * Metodo com inicializacao tardia.<br>
	 * Utilizacao interna da classe. Nao deve ser exposto.
	 * 
	 * @return the itensModeloBasicoMedicamentoExcluidos
	 */
	protected List<MpmItemModeloBasicoMedicamento> getItensModeloBasicoMedicamentoExcluidos() {
		if (this.itensModeloBasicoMedicamentoExcluidos == null) {
			this.itensModeloBasicoMedicamentoExcluidos = new LinkedList<MpmItemModeloBasicoMedicamento>();
		}
		return this.itensModeloBasicoMedicamentoExcluidos;
	}

	/**
	 * Metodo com inicializacao tardia.<br>
	 * Utilizacao interna da classe. Nao deve ser exposto.
	 * 
	 * @return the itensModeloBasicoMedicamento
	 */
	public List<MpmItemModeloBasicoMedicamento> getItensModeloBasicoMedicamento() {
		if (this.itensModeloBasicoMedicamento == null) {
			this.itensModeloBasicoMedicamento = new LinkedList<MpmItemModeloBasicoMedicamento>();
		}
		return this.itensModeloBasicoMedicamento;

	}

	/**
	 * Metodo com inicializacao tardia.<br>
	 * Utilizacao interna da classe. Nao deve ser exposto.
	 * 
	 * @return the mapItensModeloBasicoMedicamento
	 */
	protected Map<MpmItemPrescricaoMdtoId, MpmItemModeloBasicoMedicamento> getMapItensModeloBasicoMedicamento() {
		if (this.mapItensModeloBasicoMedicamento == null) {
			this.mapItensModeloBasicoMedicamento = new HashMap<MpmItemPrescricaoMdtoId, MpmItemModeloBasicoMedicamento>();
		}
		return this.mapItensModeloBasicoMedicamento;
	}

	/**
	 * @return the confirmaVoltar
	 */
	public boolean isConfirmaVoltar() {
		return confirmaVoltar;
	}

	/**
	 * @param confirmaVoltar
	 *            the confirmaVoltar to set
	 */
	public void setConfirmaVoltar(boolean confirmaVoltar) {
		this.confirmaVoltar = confirmaVoltar;
	}

	public void setCampoAlteradoFormularioItem(boolean campoAlteradoFormularioItem) {
		this.campoAlteradoFormularioItem = campoAlteradoFormularioItem;
	}

	public boolean isCampoAlteradoFormularioItem() {
		return campoAlteradoFormularioItem;
	}

	/*
	 * CLASSE PAI
	 */

	// private static final String PAGINA_MANTER_PRESCRICAO_MEDICA =
	// "prescricaomedica-manterPrescricaoMedica";

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private PrescricaoMedicaVO prescricaoMedicaVO;

	private MpmPrescricaoMedica prescricaoMedica;

	private MpmPrescricaoMdto prescricaoMedicamento = new MpmPrescricaoMdto();

	private List<MpmPrescricaoMdto> listaPrescricaoSolucoes = new ArrayList<MpmPrescricaoMdto>(0);

	private Map<MpmPrescricaoMdto, Boolean> listaPrescricaoSolucoesSelecionadas = new HashMap<MpmPrescricaoMdto, Boolean>();

	private List<MpmItemPrescricaoMdto> listaMedicamentosSolucao = new ArrayList<MpmItemPrescricaoMdto>(0);

	private List<VMpmDosagem> listaDosagens = new ArrayList<VMpmDosagem>(0);

	private BigDecimal dose;

	private MpmUnidadeMedidaMedica unidade;

	private VMpmDosagem unidadeDosagem;

	private AfaViaAdministracao via;

	private Boolean todasAsVias = false;

	private AghUnidadesFuncionais unidadeFuncional;

	private Boolean listaMedicamentos;// Padronizados ou não padronizados.

	private DominioPadronizado listaMedicamentosAux;

	private MedicamentoVO medicamentoVO;// Usado na suggestionBox de
										// medicamentos.

	private AfaMedicamento medicamento;// Usado na suggestionBox de
										// medicamentos.

	private Boolean exibirModal;

	private String mensagemExibicaoModal;

	private Short frequencia;

	private MpmTipoFrequenciaAprazamento tipoAprazamento;

	private DominioUnidadeHorasMinutos unidHorasCorrer;

	private Short qtdeHorasCorrer;

	private AfaTipoVelocAdministracoes tipoVelocAdministracao;

	private BigDecimal gotejo;

	private Boolean indBombaInfusao;

	private Date horaAdministracao;

	private Boolean indSeNecessario;

	private String observacao;

	private String informacoesFarmacologicas;

	private String complemento;

	private int indice = 0;

	private Integer matCodigo = null;

	private Boolean edicao = false;

	private Boolean edicaoPrescricaoMedicamento = false;

	private Long seq = null;

	private Date dataInicioTratamento;

	private Integer numMedicamentos = 0;

	private BigDecimal dosePediatrica;

	private MpmUnidadeMedidaMedica unidadeDosagemPediatrica;

	private DominioUnidadeBaseParametroCalculo unidadeBaseParametroCalculo;

	private DominioTipoCalculoDose tipoCalculoDose;

	private String mensagemModal;

	private MpmParamCalculoPrescricao parametroCalculo;

	private BigDecimal peso;

	private BigDecimal altura;

	private BigDecimal sc;

	private BigDecimal doseCalculada;

	private DominioTipoMedicaoPeso tipoMedicao;

	private Integer duracao;

	private DominioDuracaoCalculo unidadeTempo;

	private AfaViaAdministracao viaDosePed;

	private Short frequenciaDosePed;

	private MpmTipoFrequenciaAprazamento tipoAprazamentoDosePed;

	private int idConversacaoAnterior;

	private Boolean exibirModalPesoAltura;

	private Boolean exibirCalculoDosePediatrica;

	private Boolean possuiPesoAlturaDia;

	private Boolean ocultarModalPesoAltura;

	private Integer matCodigoMedicamentoEdicao;

	private Boolean dosePediatricaCalculada;
	
	//Gap #34801
	private Boolean prescricaoAmbulatorial;

	private enum ManterPrescricaoSolucaoControllerExceptionCode implements BusinessExceptionCode {
		OBRIGATORIO_PREENCHER_VIA, DOSE_PRECISA_SER_MAIOR_QUE_ZERO, HORA_INVALIDA, OBRIGATORIO_PREENCHER_COMPLEMENTO_MEDICAMENTO_SOLUCAO, OBRIGATORIO_PREENCHER_UNIDADE_DOSAGEM_AO_PREENCHER_DOSE, OBRIGATORIO_PREENCHER_UNIDADE_DOSAGEM_AO_PREENCHER_DOSE_MEDICAMENTO_SOLUCAO, MPM_01128, MPM_01128_MEDICAMENTO_SOLUCAO, OBRIGATORIO_PREENCHER_DOSE_AO_PREENCHER_UNIDADE_DOSAGEM, OBRIGATORIO_PREENCHER_DOSE_AO_PREENCHER_UNIDADE_DOSAGEM_MEDICAMENTO_SOLUCAO, OBRIGATORIO_PREENCHER_FREQUENCIA_PARA_ESTE_APRAZAMENTO, OBRIGATORIO_PREENCHER_UNIDADE_AO_PREENCHER_CORRER_EM, OBRIGATORIO_PREENCHER_CORRER_EM_AO_PREENCHER_UNIDADE, OBRIGATORIO_PREENCHER_UNIDADE_INFUSAO_AO_PREENCHER_VELOCIDADE_INFUSAO, PERIODO_PRESCRICAO_MEDICAMENTO_INVALIDO, MSG_0336_UNIDADE_NAO_PERMITE_BOMBA_DE_INFUSAO_NA_PRESCRICAO, PRESCRICAO_SOLUCAO_INSERIDA_SUCESSO, PRESCRICAO_SOLUCAO_ALTERADA_SUCESSO, LISTA_MEDICAMENTOS_SOLUCAO_VAZIA, MEDICAMENTO_SELECIONADO_CRUD, UNIDADE_CALCULO_DOSE_PED_DV_SER_IGUAL_UNIDADE_MED;
	}

	private enum LabelMensagemModalCode {
		MSG_MODAL_CONFIRMACAO_VIA, MSG_MODAL_PESO_ALTURA_MEDICAMENTO_OU_SOLUCAO;
	}

	public void calculoDosePediatrica() {
		// this.editarMedicamento(item);
		if (this.prescricaoMedicaVO.getIndPacPediatrico()) {
			exibirCalculoDosePediatrica = !exibirCalculoDosePediatrica;
			if (!this.possuiPesoAlturaDia) {
				this.pesoAlturaPaciente();
			} else {
				this.exibirModalPesoAltura = false;
			}
		}
	}
	@SuppressWarnings("PMD.NPathComplexity")
	public void calculoDose() {
		if (dosePediatrica != null && unidadeBaseParametroCalculo != null && tipoCalculoDose != null && this.possuiPesoAlturaDia && (peso != null && tipoMedicao != null)) {
			Object[] o = this.prescricaoMedicaFacade.calculoDose(frequenciaDosePed, tipoAprazamentoDosePed != null ? tipoAprazamentoDosePed.getSeq() : null, dosePediatrica,
					unidadeBaseParametroCalculo, tipoCalculoDose, duracao, unidadeTempo, peso != null ? peso : BigDecimal.ZERO, altura != null ? altura : BigDecimal.ZERO, sc != null ? sc
							: BigDecimal.ZERO);
			if (o != null) {
				dose = (BigDecimal) o[0];
				doseCalculada = (BigDecimal) o[0];
				this.dosePediatricaCalculada = true;
				if (unidadeDosagemPediatrica != null) {
					List<VMpmDosagem> dosagens = this.prescricaoMedicaFacade.buscarDosagensMedicamento(this.medicamento.getMatCodigo());
					for (VMpmDosagem d : dosagens) {
						if (d.getSeqUnidade().equals(unidadeDosagemPediatrica.getDescricao())) {
							unidadeDosagem = d;
							break;
						}
					}
				} else {
					unidadeDosagem = null;
				}

				if (frequenciaDosePed != null) {
					frequencia = frequenciaDosePed;
				} else {
					frequencia = null;
				}

				if (tipoAprazamentoDosePed != null) {
					tipoAprazamento = tipoAprazamentoDosePed;
				} else {
					tipoAprazamento = null;
				}

				if (viaDosePed != null) {
					via = viaDosePed;
				} else {
					via = null;
				}
			} else {
				doseCalculada = null;
			}
		} else {
			doseCalculada = null;
		}
	}

	public void pesoAlturaPaciente() {
		this.exibirModalPesoAltura = true;
		this.processarDadosPesoAltura();
	}

	public void processarDadosPesoAltura() {
		this.ocultarModalPesoAltura = false;
		this.prescricaoMedicaFacade.inicializarCadastroPesoAltura(this.prescricaoMedicaVO.getId().getAtdSeq(), dadosPesoAlturaVO);

		parametroCalculo = this.prescricaoMedicaFacade.obterParamCalculoPrescricoesAtivoCriadoHojePeloAtendimento(this.prescricaoMedicaVO.getId().getAtdSeq());
		if (parametroCalculo != null) {
			if (parametroCalculo.getAipPesoPaciente() != null) {
				peso = parametroCalculo.getAipPesoPaciente().getPeso();
				tipoMedicao = parametroCalculo.getAipPesoPaciente().getRealEstimado();
			}
			if (parametroCalculo.getAipAlturaPaciente() != null) {
				altura = parametroCalculo.getAipAlturaPaciente().getAltura();
			}
			sc = parametroCalculo.getSc();
		} else {
			parametroCalculo = new MpmParamCalculoPrescricao();
			parametroCalculo.setId(new MpmParamCalculoPrescricaoId(null, new Date()));
		}

		mensagemModal = WebUtil.initLocalizedMessage(LabelMensagemModalCode.MSG_MODAL_PESO_ALTURA_MEDICAMENTO_OU_SOLUCAO.toString(), null);
	}

	public void calcularSC() {
		DadosPesoAlturaVO vo = dadosPesoAlturaVO;//this.prescricaoMedicaFacade.obterDadosPesoAlturaVO();
		if (CoreUtil.modificados(peso, vo.getPeso()) || CoreUtil.modificados(altura, vo.getAltura())) {
			sc = this.prescricaoMedicaFacade.calculaSC(prescricaoMedicaVO.getIndPacPediatrico(), peso, altura);
		}
	}

	public void persistirDadosPesoAltura() {
		try {
			this.prescricaoMedicaFacade.atualizarDadosPesoAltura(prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getPaciente().getCodigo(), prescricaoMedicaVO.getId().getAtdSeq(), peso,
					tipoMedicao, altura, null, sc, sc, dadosPesoAlturaVO);
			parametroCalculo = this.prescricaoMedicaFacade.obterParamCalculoPrescricoesAtivoCriadoHojePeloAtendimento(this.prescricaoMedicaVO.getId().getAtdSeq());
			this.possuiPesoAlturaDia = true;
			this.ocultarModalPesoAltura = true;
			this.calculoDose();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public List<MedicamentoVO> obterMedicamentos(String strPesquisa) {

		if (DominioPadronizado.S.equals(this.listaMedicamentosAux)) {
			this.listaMedicamentos = true;
		} else {
			this.listaMedicamentos = false;
		}

		return this.returnSGWithCount(this.farmaciaFacade.obterMedicamentosVO(strPesquisa, this.listaMedicamentos, null, prescricaoAmbulatorial, false),obterMedicamentosCount(strPesquisa));
		// List<AfaMedicamento> lista =
		// this.prescricaoMedicaFacade.obterMedicamentos(strPesquisa,
		// this.listaMedicamentos);

		// return lista;
	}



	public Long obterMedicamentosCount(String strPesquisa) {
		if (DominioPadronizado.S.equals(this.listaMedicamentosAux)) {
			this.listaMedicamentos = true;
		} else {
			this.listaMedicamentos = false;
		}
		return this.farmaciaFacade.obterMedicamentosVOCount(strPesquisa, this.listaMedicamentos, null, prescricaoAmbulatorial, false);
		// return
		// this.prescricaoMedicaFacade.obterMedicamentosCount(strPesquisa,
		// this.listaMedicamentos);
	}

	protected void setListaMedicamentosAuxDominioPadronizadoSim() {
		this.setListaMedicamentosAux(DominioPadronizado.S);
	}

	public boolean permiteDoseFracionada() {
		return this.medicamento == null || this.medicamento.getIndPermiteDoseFracionada();
	}

	public List<VMpmDosagem> buscarDosagens() {
		if (this.medicamento != null) {
			return this.prescricaoMedicaFacade.buscarDosagensMedicamento(this.medicamento.getMatCodigo());
		}
		return new ArrayList<VMpmDosagem>();
	}

	public List<MpmUnidadeMedidaMedica> buscarDosagensPed() {
		if (this.medicamento != null) {
			return this.prescricaoMedicaFacade.getListaUnidadeMedidaMedicaAtivasPeloMedFmDosagPeloCodigoOuDescricao(this.medicamento.getMatCodigo());
		}
		return new ArrayList<MpmUnidadeMedidaMedica>();
	}

	public DominioUnidadeBaseParametroCalculo[] getListaUnidadeBaseParametroCalculo() {
		return new DominioUnidadeBaseParametroCalculo[] { DominioUnidadeBaseParametroCalculo.K, DominioUnidadeBaseParametroCalculo.M };
	}

	public DominioTipoCalculoDose[] getListaTipoCalculoDose() {
		return new DominioTipoCalculoDose[] { DominioTipoCalculoDose.D, DominioTipoCalculoDose.M, DominioTipoCalculoDose.H };
	}

	public DominioDuracaoCalculo[] getListaDuracaoCalculo() {
		return new DominioDuracaoCalculo[] { DominioDuracaoCalculo.H, DominioDuracaoCalculo.M };
	}

	public List<AfaViaAdministracao> listarViasMedicamentoPed(Object strPesquisa) {
		List<AfaViaAdministracao> lista = new ArrayList<AfaViaAdministracao>(0);
		if (this.medicamentoVO != null) {
			List<Integer> listaDeIds = new ArrayList<Integer>();
			listaDeIds.add(medicamentoVO.getMatCodigo());
			lista = this.farmaciaFacade.listarViasMedicamento((String) strPesquisa, listaDeIds, this.unidadeFuncional.getSeq());
			if (lista != null && lista.size() > 0) {
				lista = new ArrayList<AfaViaAdministracao>(new HashSet<AfaViaAdministracao>(lista));
			}
		}
		return lista;
	}

	public Long listarViasMedicamentoPedCount(Object strPesquisa) {
		Long count = 0L;

		if (this.medicamentoVO != null) {
			List<Integer> listaDeIds = new ArrayList<Integer>();
			listaDeIds.add(medicamentoVO.getMatCodigo());
			count = this.farmaciaFacade.listarViasMedicamentoCount((String) strPesquisa, listaDeIds, this.unidadeFuncional.getSeq());
		}
		return count;
	}

	protected void verificarViaAssociadaAoMedicamentoExibirModal() {
		Boolean viaAssociada = true;
		if (Boolean.TRUE.equals(this.todasAsVias) && this.listaMedicamentosSolucao != null && !this.listaMedicamentosSolucao.isEmpty()) {
			for (MpmItemPrescricaoMdto item : this.listaMedicamentosSolucao) {
				viaAssociada = this.farmaciaFacade.verificarViaAssociadaAoMedicamento(item.getMedicamento().getMatCodigo(), this.via.getSigla());
				if (!viaAssociada) {// Se nao esta associada.
					this.exibirModal = true;
					final String msg = WebUtil.initLocalizedMessage(LabelMensagemModalCode.MSG_MODAL_CONFIRMACAO_VIA.toString(), null);
					this.mensagemExibicaoModal = MessageFormat.format(msg, item.getMedicamento().getDescricaoEditada());
					break;
				}
			}
		}
	}

	protected void verificarViaAssociadaAoMedicamentoBombaInfusao() {
		Boolean viaAssociada = true;
		if (this.listaMedicamentosSolucao != null && !this.listaMedicamentosSolucao.isEmpty() && this.via != null) {
			for (MpmItemPrescricaoMdto item : this.listaMedicamentosSolucao) {
				viaAssociada = this.farmaciaFacade.verificarBombaInfusaoDefaultViaAssociadaAoMedicamento(item.getMedicamento().getMatCodigo(), this.via.getSigla());
				if (viaAssociada && prescricaoMedicaFacade.validaBombaInfusao(this.unidadeFuncional, this.via, item.getMedicamento())) {
					this.indBombaInfusao = true;
					break;
				} else {
					this.indBombaInfusao = false;
				}
			}
		}
	}

	public void limparAtributosViaAssociadaAoMedicamento() {
		this.indBombaInfusao = false;
	}

	public boolean verificaRequiredFrequencia() {
		return this.tipoAprazamento != null && this.tipoAprazamento.getIndDigitaFrequencia();
	}

	public void verificarFrequenciaPed() {
		if (!(this.tipoAprazamentoDosePed != null && this.tipoAprazamentoDosePed.getIndDigitaFrequencia())) {
			this.frequenciaDosePed = null;
		}
		this.calculoDose();
	}

	public List<MpmTipoFrequenciaAprazamento> buscarTiposFrequenciaAprazamento(String strPesquisa) {
		return this.prescricaoMedicaFacade.buscarTipoFrequenciaAprazamento((String) strPesquisa);
	}

	public String getDescricaoTipoFrequenciaAprazamento() {
		return buscaDescricaoTipoFrequenciaAprazamento(this.tipoAprazamento);
	}

	public String getDescricaoTipoFrequenciaAprazamentoPed() {
		return buscaDescricaoTipoFrequenciaAprazamentoPed(this.tipoAprazamentoDosePed);
	}

	public String buscaDescricaoTipoFrequenciaAprazamento(MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		return tipoFrequenciaAprazamento != null ? tipoFrequenciaAprazamento.getDescricaoSintaxeFormatada(this.frequencia) : "";
	}

	public String buscaDescricaoTipoFrequenciaAprazamentoPed(MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamentoPed) {
		return tipoFrequenciaAprazamentoPed != null ? tipoFrequenciaAprazamentoPed.getDescricaoSintaxeFormatada(this.frequenciaDosePed) : "";
	}

	public List<AfaTipoVelocAdministracoes> buscarTiposVelocidadeAdministracao() {
		return this.farmaciaFacade.obtemListaTiposVelocidadeAdministracao();
	}

	public void desabilitarExibicaoModal() {
		this.exibirModal = false;
	}

	/**
	 * Limpa os campos da tela.
	 */
	public void limparParte2() {
		this.numMedicamentos = 0;
		this.confirmaVoltar = false;
		this.edicao = false;
		this.edicaoPrescricaoMedicamento = false;
		this.prescricaoMedicamento = new MpmPrescricaoMdto();
		this.seq = null;
		this.matCodigo = null;
		this.listaDosagens = null;
		this.listaMedicamentos = true;
		this.listaMedicamentosAux = DominioPadronizado.S;
		this.medicamento = null;
		this.medicamentoVO = null;
		this.dose = null;
		this.dataInicioTratamento = null;
		this.unidade = null;
		this.unidadeDosagem = null;
		this.via = null;
		this.todasAsVias = false;
		this.qtdeHorasCorrer = null;
		this.unidHorasCorrer = null;
		this.gotejo = null;
		this.tipoVelocAdministracao = null;
		this.indBombaInfusao = false;
		this.horaAdministracao = null;
		this.indSeNecessario = false;
		this.observacao = null;
		this.informacoesFarmacologicas = null;
		this.frequencia = null;
		this.tipoAprazamento = null;
		this.exibirModal = false;
		this.mensagemExibicaoModal = "";
		this.complemento = null;
		this.listaMedicamentosAux = DominioPadronizado.S;
		this.exibirCalculoDosePediatrica = false;
		this.dosePediatrica = null;
		this.unidadeDosagemPediatrica = null;
		this.unidadeBaseParametroCalculo = null;
		this.tipoCalculoDose = null;
		this.doseCalculada = null;
		this.duracao = null;
		this.unidadeTempo = null;
		this.frequenciaDosePed = null;
		this.tipoAprazamentoDosePed = null;
		this.viaDosePed = null;
		// Contexts.getConversationContext().remove("listaDosagens"); //TODO
		// MIGRAÇÃO: Verificar onde esse parâmetro é utilizado
		// Contexts.getConversationContext().remove("listaDosagensPed"); //TODO
		// MIGRAÇÃO: Verificar onde esse parâmetro é utilizado
		this.listaMedicamentosSolucao = new ArrayList<MpmItemPrescricaoMdto>(0);// limpa
																				// a
																				// lista
																				// de
																				// medicamentos
																				// soluções
	}

	public void removerPrescricaoSolucoesSelecionadas() {

		try {

			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}

			int nroPrescricoesMedicamentoRemovidas = 0;
			for (MpmPrescricaoMdto prescMed : listaPrescricaoSolucoes) {
				if (listaPrescricaoSolucoesSelecionadas.get(prescMed) == true) {
					MpmPrescricaoMdto prescricaoMedicamentoOriginal = 
							this.prescricaoMedicaFacade.obterPrescricaoMedicamento(prescricaoMedicamento.getId().getAtdSeq(), prescricaoMedicamento.getId().getSeq());
					this.prescricaoMedicaFacade.desatachar(prescricaoMedicamentoOriginal);
					
					this.prescricaoMedicaFacade.removerPrescricaoMedicamento(this.prescricaoMedica, prescMed, nomeMicrocomputador,prescricaoMedicamentoOriginal);
					nroPrescricoesMedicamentoRemovidas++;
				}
			}
			if (nroPrescricoesMedicamentoRemovidas > 0) {
				if (nroPrescricoesMedicamentoRemovidas > 1) {
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_PRESCRICAO_SOLUCOES");
				} else {
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_PRESCRICAO_SOLUCAO");
				}
			} else {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_NENHUMA_PRESCRICAO_SOLUCAO_SELECIONADA_REMOCAO");
			}
			// Limpa a tela
			this.limpar();
			listaPrescricaoSolucoes = prescricaoMedicaFacade.obterListaMedicamentosPrescritosPelaChavePrescricao(prescricaoMedicaVO.getId(), prescricaoMedicaVO.getDthrFim(), true);
			for (MpmPrescricaoMdto prescricaoMdto : listaPrescricaoSolucoes) {
				listaPrescricaoSolucoesSelecionadas.put(prescricaoMdto, false);
			}
			final ComparatorChain chainSorter = new ComparatorChain();
			final BeanComparator antimicrobianoSorter = new BeanComparator("indAntiMicrobiano", new ReverseComparator(new NullComparator(false)));
			final BeanComparator descricaoSorter = new BeanComparator("descricaoFormatada", new NullComparator(false));
			chainSorter.addComparator(antimicrobianoSorter);
			chainSorter.addComparator(descricaoSorter);
			if (listaPrescricaoSolucoes != null && !listaPrescricaoSolucoes.isEmpty()) {
				Collections.sort(listaPrescricaoSolucoes, chainSorter);
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	public void editar(Long seq) {
		try {
			this.limpar();

			this.edicaoPrescricaoMedicamento = true;
			this.edicao = false;
			this.seq = seq;
			prescricaoMedicamento = prescricaoMedicaFacade.obterPrescricaoMedicamento(this.prescricaoMedica.getId().getAtdSeq(), seq);

			this.via = prescricaoMedicamento.getViaAdministracao();
			this.frequencia = prescricaoMedicamento.getFrequencia();
			this.qtdeHorasCorrer = prescricaoMedicamento.getQtdeHorasCorrer();
			this.unidHorasCorrer = prescricaoMedicamento.getUnidHorasCorrer();
			this.gotejo = prescricaoMedicamento.getGotejo();
			this.tipoVelocAdministracao = prescricaoMedicamento.getTipoVelocAdministracao();
			this.indBombaInfusao = prescricaoMedicamento.getIndBombaInfusao();
			this.observacao = prescricaoMedicamento.getObservacao();
			this.horaAdministracao = (prescricaoMedicamento.getHoraInicioAdministracao() != null) ? (prescricaoMedicamento.getHoraInicioAdministracao()) : null;
			this.indSeNecessario = prescricaoMedicamento.getIndSeNecessario();
			this.tipoAprazamento = prescricaoMedicamento.getTipoFreqAprazamento();

			this.dataInicioTratamento = prescricaoMedicamento.getDthrInicioTratamento();

			if (DominioIndPendenteItemPrescricao.N.equals(prescricaoMedicamento.getIndPendente())) {
				this.listaMedicamentosSolucao = prescricaoMedicaFacade.clonarItensPrescricaoMedicamento(prescricaoMedicamento);
			} else {
				this.listaMedicamentosSolucao = prescricaoMedicamento.getItensPrescricaoMdtos();
			}

			this.numMedicamentos = (this.listaMedicamentosSolucao != null) ? this.listaMedicamentosSolucao.size() : 0;
			this.listaMedicamentosAux = DominioPadronizado.S;
			this.dosePediatricaCalculada = false;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	@SuppressWarnings("PMD.NPathComplexity")
	protected void validaDadosParte2() throws BaseException {

		if (this.medicamento != null) {
			throw new ApplicationBusinessException(ManterPrescricaoSolucaoControllerExceptionCode.MEDICAMENTO_SELECIONADO_CRUD, medicamento.getDescricaoEditada());
		}

		if (this.listaMedicamentosSolucao == null || this.listaMedicamentosSolucao.isEmpty()) {
			throw new ApplicationBusinessException(ManterPrescricaoSolucaoControllerExceptionCode.LISTA_MEDICAMENTOS_SOLUCAO_VAZIA);
		}

		if (this.via == null) {
			throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.OBRIGATORIO_PREENCHER_VIA);
		}

		Boolean validarBombaInf = this.unidadeFuncional != null;

		// Verificacoes dos itensPrescricaoMedicamento
		for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : this.listaMedicamentosSolucao) {
			AfaMedicamento med = itemPrescricaoMedicamento.getMedicamento();

			if (validarBombaInf) {
				if (Boolean.TRUE.equals(this.indBombaInfusao)) {
					if (prescricaoMedicaFacade.validaBombaInfusao(this.unidadeFuncional, this.via, med)) {
						validarBombaInf = false;
					}
				} else {
					validarBombaInf = false;
				}
			}

			if (validarBombaInf && Boolean.TRUE.equals(this.indBombaInfusao) && !prescricaoMedicaFacade.validaBombaInfusao(this.unidadeFuncional, this.via, med)) {
				throw new ApplicationBusinessException(ManterPrescricaoSolucaoControllerExceptionCode.MSG_0336_UNIDADE_NAO_PERMITE_BOMBA_DE_INFUSAO_NA_PRESCRICAO);
			}

			if (med.getIndExigeObservacao() && StringUtils.isBlank(itemPrescricaoMedicamento.getObservacao())) {
				throw new ApplicationBusinessException(ManterPrescricaoSolucaoControllerExceptionCode.OBRIGATORIO_PREENCHER_COMPLEMENTO_MEDICAMENTO_SOLUCAO);
			}

			if (itemPrescricaoMedicamento.getDose() != null && itemPrescricaoMedicamento.getFormaDosagem() == null) {
				throw new ApplicationBusinessException(ManterPrescricaoSolucaoControllerExceptionCode.OBRIGATORIO_PREENCHER_UNIDADE_DOSAGEM_AO_PREENCHER_DOSE_MEDICAMENTO_SOLUCAO,
						itemPrescricaoMedicamento.getMedicamento().getDescricaoEditada());
			}

		}// fim do for

		if (validarBombaInf) {
			throw new ApplicationBusinessException(ManterPrescricaoSolucaoControllerExceptionCode.MSG_0336_UNIDADE_NAO_PERMITE_BOMBA_DE_INFUSAO_NA_PRESCRICAO);
		}

		// Verificacoes do prescricaoMedicamento
		if (verificaRequiredFrequencia() && this.frequencia == null) {
			throw new ApplicationBusinessException(ManterPrescricaoSolucaoControllerExceptionCode.OBRIGATORIO_PREENCHER_FREQUENCIA_PARA_ESTE_APRAZAMENTO);
		}

		if (this.qtdeHorasCorrer != null && this.unidHorasCorrer == null) {
			throw new ApplicationBusinessException(ManterPrescricaoSolucaoControllerExceptionCode.OBRIGATORIO_PREENCHER_UNIDADE_AO_PREENCHER_CORRER_EM);
		}

		if (this.qtdeHorasCorrer == null && this.unidHorasCorrer != null) {
			throw new ApplicationBusinessException(ManterPrescricaoSolucaoControllerExceptionCode.OBRIGATORIO_PREENCHER_CORRER_EM_AO_PREENCHER_UNIDADE);
		}

		if (this.gotejo != null && this.tipoVelocAdministracao == null) {
			throw new ApplicationBusinessException(ManterPrescricaoSolucaoControllerExceptionCode.OBRIGATORIO_PREENCHER_UNIDADE_INFUSAO_AO_PREENCHER_VELOCIDADE_INFUSAO);
		}

		validarDataInicioTratamento();
	}

	private void validarDataInicioTratamento() throws BaseException {
		if (this.dataInicioTratamento != null && !this.getEdicaoPrescricaoMedicamento()) {
			// Esta data não poderá ser maior que o fim da prescrição médica.
			if (DateUtil.truncaData(this.getDataInicioTratamento()).after(DateUtil.truncaData(this.prescricaoMedica.getDthrFim()))) {
				throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.DATA_INICIO_TRATAMENTO_MAIOR_FIM_PRESCRICAO);
			}
			// A data não pode ser menor que o dia atual, menos o valor
			// informado no parâmetro P_DIAS_RETROATIVOS_INICIO_TRATAMENTO
			Date dataInicioTratamentoRetroativoPermitido = calcularDataInicioTratamentoRetroativoPermitido();
			if (DateUtil.truncaData(this.getDataInicioTratamento()).before(dataInicioTratamentoRetroativoPermitido)) {
				throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.DATA_INICIO_TRATAMENTO_MENOR_PARAMETRO,
						getDataInicioTratamentoRetroativoPermitidoFormatada(dataInicioTratamentoRetroativoPermitido));
			}
			// A data não pode ser menor que a data de nascimento do paciente.
			if (DateUtil.truncaData(this.getDataInicioTratamento()).before(getDtNascimentoPaciente())) {
				throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.DATA_INICIO_TRATAMENTO_MENOR_NASCIMENTO);
			}
		}
	}

	private String getDataInicioTratamentoRetroativoPermitidoFormatada(Date dataInicioTratamentoRetroativoPermitido) {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return dateFormat.format(dataInicioTratamentoRetroativoPermitido);
	}

	private Date getDtNascimentoPaciente() {
		Date dtNascimentoPaciente1 = getPrescricaoMedicaVO().getPrescricaoMedica().getAtendimento().getPaciente().getDtNascimento();

		return DateUtil.truncaData(dtNascimentoPaciente1);
	}

	/**
	 * Calcula a data que o sistema permite lançar inicio de tratamento
	 * retroativo.
	 * 
	 * Busca o parâmetro P_DIAS_RETROATIVOS_INICIO_TRATAMENTO e subtrai da data
	 * atual.
	 * 
	 * @return Date com hora truncada.
	 * @throws ApplicationBusinessException
	 */
	private Date calcularDataInicioTratamentoRetroativoPermitido() throws ApplicationBusinessException {
		Calendar dataInicioTratamentoRetroativoPermitido = Calendar.getInstance();
		AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_RETROATIVOS_INICIO_TRATAMENTO);

		if (parametro.getVlrNumerico() != null) {
			dataInicioTratamentoRetroativoPermitido.add(Calendar.DAY_OF_MONTH, -(parametro.getVlrNumerico().intValue()));
		}

		return DateUtil.truncaData(dataInicioTratamentoRetroativoPermitido.getTime());
	}

	public boolean medicamentoSelecionadoAntimicrobiano() {
		boolean retorno = false;
		retorno = temAntimicrobianoListaMedicamentosSolucao();
		if (retorno && getDataInicioTratamento() == null) {
			try {
                String horaFormatada = DateUtil.dataToString(this.horaAdministracao,"HH:mm");
				setDataInicioTratamento(prescricaoMedicaFacade.atualizaInicioTratamento(getDataHoraInicio(), populaDataHora(horaFormatada)));
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
			}
		}
		return retorno;
	}

	/**
	 * Verifica se algum registro da lista de medicamentos possui antimicrobiano
	 * 
	 * @return
	 */
	private boolean temAntimicrobianoListaMedicamentosSolucao() {
		boolean retorno = false;
		if (getListaMedicamentosSolucao() != null) {
			for (MpmItemPrescricaoMdto solucao : getListaMedicamentosSolucao()) {
				if (solucao.getIndAntiMicrobiano()) {
					retorno = true;
					break;
				}
			}
		}

		if (!retorno) {
			this.dataInicioTratamento = null;
		}

		return retorno;
	}

	private Date getDataHoraInicio() throws ApplicationBusinessException {
		return (this.prescricaoMedicaFacade.isPrescricaoVigente(this.prescricaoMedica.getDthrInicio(), this.prescricaoMedica.getDthrFim()) ? this.prescricaoMedica.getDthrMovimento()
				: this.prescricaoMedica.getDthrInicio());
	}

	/**
	 * Recebe uma String hora (formato HH:mm) e retorna uma data que representa
	 * esta hora.
	 * 
	 * @param hora
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected Date populaDataHora(String hora) throws ApplicationBusinessException {
		try {
			if (StringUtils.isNotBlank(hora)) {
				String[] arrayHora = hora.split(":");
				if (arrayHora.length == 2) {
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.DAY_OF_MONTH, 1);
					cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(arrayHora[0]));
					cal.set(Calendar.MINUTE, Integer.valueOf(arrayHora[1]));
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);

					return cal.getTime();
				}
			}
			return null;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.HORA_INVALIDA);
		}
	}

	public void limparCamposRelacionados() {
		this.medicamentoVO = null;
		this.medicamento = null;
		this.via = null;
		this.unidade = null;
		this.unidadeDosagem = null;
		this.dose = null;
		this.informacoesFarmacologicas = null;
		this.dataInicioTratamento = null;
	}

	protected void verificarExisteMensagemCadastrada() {
		try {
			if (DominioPadronizado.N.equals(this.listaMedicamentosAux)) {
				AghParametros p = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_MSG_MED_NAO_CADASTRADO);

				String str = StringUtils.replace(StringUtils.replace(
						StringUtils.replace(p.getVlrTexto(), "#3", (this.medicamento.getMpmUnidadeMedidaMedicas() != null) ? this.medicamento.getMpmUnidadeMedidaMedicas().getDescricao() : ""), "#2",
						(this.medicamento.getConcentracao() != null) ? this.medicamento.getConcentracaoFormatada() : "0"), "#1", this.medicamento.getDescricao());

				this.apresentarMsgNegocio(Severity.INFO, str);
			}

		} catch (ApplicationBusinessException e) {
			// Caso o parâmetro não seja encontrado, a princípio, para essa
			// situação em específico, nada deve acontecer.
			LOG.error(e.getMessage(), e);
		}
	}

	public void realizarVerificacoesMedicamento() {
		// Contexts.getConversationContext().remove("listaDosagens"); //TODO
		// MIGRAÇÃO: Verificar onde esse parâmetro é utilizado

		if (this.medicamentoVO != null) {
			this.medicamentoVO.setMedicamento(farmaciaFacade.obterMedicamento(this.getMedicamentoVO().getMatCodigo()));

			StringBuilder returnValue = new StringBuilder("");
			if (medicamentoVO.getDescricaoMat() != null) {
				returnValue.append(medicamentoVO.getDescricaoMedicamento());
				returnValue.append(' ');
			}
			if (medicamentoVO.getConcentracao() != null) {
				Locale locBR = new Locale("pt", "BR");// Brasil
				DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
				dfSymbols.setDecimalSeparator(',');
				DecimalFormat format;
				if (this.medicamentoVO.getConcentracao() != null && this.medicamentoVO.getConcentracao().stripTrailingZeros().scale() <= 0) {
					format = new DecimalFormat("#,###,###,###,##0.###############", dfSymbols);
					returnValue.append(format.format(this.medicamentoVO.getConcentracao()));
					returnValue.append(' ');
				} else if (this.medicamentoVO.getConcentracao() != null) {
					format = new DecimalFormat("#,###,###,###,##0.0##############", dfSymbols);
					returnValue.append(format.format(this.medicamentoVO.getConcentracao()));
					returnValue.append(' ');
				}
			}
			if (medicamentoVO.getDescricaoUnidadeMedica() != null) {
				returnValue.append(this.medicamentoVO.getDescricaoUnidadeMedica());
			}
			medicamentoVO.setDescricaoEditada(returnValue.toString());

			this.medicamento = farmaciaFacade.obterMedicamento(medicamentoVO.getMatCodigo());

			this.verificarExisteMensagemCadastrada();
			List<Integer> medMatCodigoList = new ArrayList<Integer>();
			medMatCodigoList.add(this.medicamento.getMatCodigo());

			// this.medicamento =
			// this.farmaciaFacade.obterMedicamentoPorId(this.medicamento.getMatCodigo());
			if (!this.medicamento.getIndPermiteDoseFracionada() && this.dose != null && !(this.dose.stripTrailingZeros().scale() <= 0)) {
				apresentarMsgNegocio(Severity.ERROR, ManterPrescricaoSolucaoControllerExceptionCode.MPM_01128.toString());
				this.dose = null;
			}

			this.informacoesFarmacologicas = this.farmaciaFacade.obterInfromacoesFarmacologicas(medicamento);

			listaDosagens = this.prescricaoMedicaFacade.buscarDosagensMedicamento(this.medicamento.getMatCodigo());
			// VERIFICA UNIDADE MEDIDA PADRÃO PARA O MEDICAMENTO
			if (!listaDosagens.isEmpty()) {
				if (listaDosagens.size() == 1) {
					this.unidadeDosagem = listaDosagens.get(0);// SE só existe
																// uma dosagem
																// possivel para
																// o medicamento
																// esta deve vir
																// selecionada
				} else {
					AfaFormaDosagem formaDosagem = this.farmaciaFacade.buscarDosagenPadraoMedicamento(this.medicamento.getMatCodigo());
					if (formaDosagem != null) {
						for (int i = 0; i < listaDosagens.size(); i++) {
							if (formaDosagem.getSeq().equals(listaDosagens.get(i).getFormaDosagem().getSeq())) {
								this.unidadeDosagem = listaDosagens.get(i);
							}
						}
					}
				}
			}
		}
	}

	public void verificarDose() {
		if (this.dose != null && this.dose.doubleValue() <= 0) {
			this.apresentarMsgNegocio(Severity.ERROR, ManterPrescricaoSolucaoControllerExceptionCode.DOSE_PRECISA_SER_MAIOR_QUE_ZERO.toString());
		}
	}

	public void validarUnidadeDsPed() throws BaseException {
		if (Boolean.TRUE.equals(this.dosePediatricaCalculada) && (!CoreUtil.igual(unidadeDosagemPediatrica, this.unidadeDosagem.getFormaDosagem().getUnidadeMedidaMedicas()))) {
			throw new ApplicationBusinessException(ManterPrescricaoSolucaoControllerExceptionCode.UNIDADE_CALCULO_DOSE_PED_DV_SER_IGUAL_UNIDADE_MED);
		}
	}

	private boolean validarObrigatoriosAdicaoMedicamento() {

		if (Boolean.TRUE.equals(this.dosePediatricaCalculada) && (!CoreUtil.igual(unidadeDosagemPediatrica, this.unidadeDosagem.getFormaDosagem().getUnidadeMedidaMedicas()))) {
			apresentarMsgNegocio(Severity.ERROR, "UNIDADE_CALCULO_DOSE_PED_DV_SER_IGUAL_UNIDADE_MED");
			return true;
		}

		return false;
	}

	public void adicionarMedicamentoParte2() {

		if (validarObrigatoriosAdicaoMedicamento()) {
			return;
		}

		try {
			this.prescricaoMedicaFacade.verificaDoseFracionada(medicamento.getMatCodigo(), dose, this.unidadeDosagem.getFormaDosagem().getSeq());

			boolean isCadastrado = false;
			this.setEdicao(false);
			matCodigo = null;

			isCadastrado = validarListaMedicamentosSolucao();

			if (!isCadastrado) {
				MpmItemPrescricaoMdto medicamentoSolucao = new MpmItemPrescricaoMdto();
				medicamentoSolucao.setMedicamento(farmaciaFacade.obterMedicamento(medicamento.getMatCodigo()));
				medicamentoSolucao.setDose(dose);
				// Comentado devido ao bug registrado em
				// http://qos-aghu.mec.gov.br/mantis/view.php?id=356
				// if(this.unidadeDosagem != null &&
				// this.unidadeDosagem.getFdsUmmSeq() != null) {
				// medicamentoSolucao.setUnidadeMedidaMedica(this.prescricaoMedicaFacade
				// .obterUnidadeMedicaPorId(this.unidadeDosagem.getFdsUmmSeq()));
				// }
				medicamentoSolucao.setObservacao((StringUtils.isEmpty(complemento) || complemento == null) ? null : complemento);
				medicamentoSolucao.setFormaDosagem(this.unidadeDosagem.getFormaDosagem());
				medicamentoSolucao.setMdtoAguardaEntrega(false);
				medicamentoSolucao.setOrigemJustificativa(false);

				medicamentoSolucao.setDoseCalculada(this.doseCalculada != null ? this.doseCalculada.doubleValue() : null);
				medicamentoSolucao.setTipoCalculoDose(this.tipoCalculoDose);
				medicamentoSolucao.setQtdeParamCalculo(this.dosePediatrica != null ? this.dosePediatrica.doubleValue() : null);
				medicamentoSolucao.setBaseParamCalculo(this.unidadeBaseParametroCalculo);
				medicamentoSolucao.setParamCalculoPrescricao(this.parametroCalculo);
				medicamentoSolucao.setUnidadeMedidaMedica(this.unidadeDosagemPediatrica);
				medicamentoSolucao.setUnidDuracaoCalculo(unidadeTempo);
				medicamentoSolucao.setDuracaoParamCalculo(duracao);

				// TODO - AJUSTAR VALORES CÁLCULO DOSE PEDIÁTRICA

				listaMedicamentosSolucao.add(medicamentoSolucao);

				medicamento = null;
				medicamentoVO = null;
				unidade = null;
				unidadeDosagem = null;
				dose = null;
				complemento = null;
				informacoesFarmacologicas = null;
				this.doseCalculada = null;
				this.dosePediatrica = null;
				this.unidadeDosagemPediatrica = null;
				this.unidadeBaseParametroCalculo = null;
				this.tipoCalculoDose = null;
				this.duracao = null;
				this.unidadeTempo = null;
				this.viaDosePed = null;
				this.frequenciaDosePed = null;
				this.tipoAprazamentoDosePed = null;
				this.exibirCalculoDosePediatrica = false;
				this.listaMedicamentosAux = DominioPadronizado.S;
				this.dosePediatricaCalculada = false;
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private boolean validarListaMedicamentosSolucao() {
		for (MpmItemPrescricaoMdto item : listaMedicamentosSolucao) {
			if (item.getMedicamento().getMatCodigo().equals(medicamento.getMatCodigo())) {
				this.apresentarMsgNegocio(Severity.ERROR, "Medicamento " + medicamento.getDescricaoEditada() + " já adicionado a solução.");
				dose = null;
				complemento = null;
				unidade = null;
				medicamento = null;
				this.medicamentoVO = null;
				return true;
			}
		}

		return false;
	}

	private boolean validarListaMedicamentosSolucaoComparandoIndice() {
		if (!listaMedicamentosSolucao.isEmpty()) {
			int indiceAux = 0;
			for (MpmItemPrescricaoMdto item : listaMedicamentosSolucao) {
				indiceAux++;
				if (item.getMedicamento().getMatCodigo().equals(medicamento.getMatCodigo()) && indice != indiceAux) {
					this.apresentarMsgNegocio(Severity.ERROR, "Medicamento " + medicamento.getDescricaoEditada() + " já adicionado a solução.");
					return true;
				}
			}
		}
		return false;
	}

	public void alterarMedicamentoParte2() {
		try {

			validarUnidadeDsPed();

			this.prescricaoMedicaFacade.verificaDoseFracionada(medicamento.getMatCodigo(), dose, this.unidadeDosagem.getFormaDosagem().getSeq());

			boolean isCadastrado = false;
			this.setEdicao(false);
			matCodigo = null;

			isCadastrado = validarListaMedicamentosSolucaoComparandoIndice();

			if (!isCadastrado) {
				listaMedicamentosSolucao.get(indice - 1).setMedicamento(farmaciaFacade.obterMedicamento(medicamento.getMatCodigo()));
				listaMedicamentosSolucao.get(indice - 1).setDose(dose);
				// Comentado devido ao bug registrado em
				// http://qos-aghu.mec.gov.br/mantis/view.php?id=356
				// if(this.unidadeDosagem != null &&
				// this.unidadeDosagem.getFdsUmmSeq() != null) {
				// listaMedicamentosSolucao.get(indice -
				// 1).setUnidadeMedidaMedica(this.prescricaoMedicaFacade
				// .obterUnidadeMedicaPorId(this.unidadeDosagem.getFdsUmmSeq()));
				// } else {
				listaMedicamentosSolucao.get(indice - 1).setUnidadeMedidaMedica(null);
				// }
				listaMedicamentosSolucao.get(indice - 1).setFormaDosagem(this.unidadeDosagem.getFormaDosagem());
				listaMedicamentosSolucao.get(indice - 1).setObservacao((StringUtils.isEmpty(complemento) || complemento == null) ? null : complemento);

				listaMedicamentosSolucao.get(indice - 1).setDoseCalculada(this.doseCalculada != null ? this.doseCalculada.doubleValue() : null);
				listaMedicamentosSolucao.get(indice - 1).setTipoCalculoDose(this.tipoCalculoDose);
				listaMedicamentosSolucao.get(indice - 1).setQtdeParamCalculo(this.dosePediatrica != null ? this.dosePediatrica.doubleValue() : null);
				listaMedicamentosSolucao.get(indice - 1).setBaseParamCalculo(this.unidadeBaseParametroCalculo);
				listaMedicamentosSolucao.get(indice - 1).setParamCalculoPrescricao(this.parametroCalculo);
				listaMedicamentosSolucao.get(indice - 1).setUnidDuracaoCalculo(unidadeTempo);
				listaMedicamentosSolucao.get(indice - 1).setDuracaoParamCalculo(duracao);
				listaMedicamentosSolucao.get(indice - 1).setUnidadeMedidaMedica(this.unidadeDosagemPediatrica);

				medicamento = null;
				medicamentoVO = null;
				unidade = null;
				dose = null;
				complemento = null;
				unidade = null;
				unidadeDosagem = null;
				this.doseCalculada = null;
				this.dosePediatrica = null;
				this.unidadeDosagemPediatrica = null;
				this.unidadeBaseParametroCalculo = null;
				this.tipoCalculoDose = null;
				this.duracao = null;
				this.unidadeTempo = null;
				this.viaDosePed = null;
				this.frequenciaDosePed = null;
				this.tipoAprazamentoDosePed = null;
				this.exibirCalculoDosePediatrica = false;
				this.listaMedicamentosAux = DominioPadronizado.S;
			}

		} catch (BaseException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
		}
	}

	public void removerMedicamento(MpmItemPrescricaoMdto item) {
		this.setEdicao(false);
		matCodigo = null;
		for (Iterator<MpmItemPrescricaoMdto> i = listaMedicamentosSolucao.iterator(); i.hasNext();) {
			MpmItemPrescricaoMdto itemPrs = (MpmItemPrescricaoMdto) i.next();
			if (itemPrs.getMedicamento().getMatCodigo().equals(item.getMedicamento().getMatCodigo())) {
				this.doRemoverMedicamento(itemPrs);
				i.remove();
				break;
			}
		}
	}

	private Boolean isDosePed(MpmItemPrescricaoMdto itemPrs) {
		return itemPrs.getDoseCalculada() != null ? true : false;
	}

	/**
	 * Metodo gancho.<br>
	 * Deve ser chamado apenas quando for remover um item de Medicament da
	 * lista.<br>
	 * Responsabilidade da Sub-classe.
	 * 
	 * @param itemPrs
	 * 
	 *            protected void doRemoverMedicamento(MpmItemPrescricaoMdto
	 *            itemPrs) { }
	 */

	public void editarMedicamento(MpmItemPrescricaoMdto item) {
		this.setEdicao(true);
		indice = 0;
		for (Iterator<MpmItemPrescricaoMdto> i = listaMedicamentosSolucao.iterator(); i.hasNext();) {
			MpmItemPrescricaoMdto itemPrs = (MpmItemPrescricaoMdto) i.next();
			indice++;
			if (itemPrs.getMedicamento().getMatCodigo().equals(item.getMedicamento().getMatCodigo())) {
				matCodigo = itemPrs.getMedicamento().getMatCodigo();
				dose = itemPrs.getDose();
				complemento = itemPrs.getObservacao();
				medicamento = farmaciaFacade.obterMedicamento(itemPrs.getMedicamento().getMatCodigo());
				medicamentoVO = new MedicamentoVO();
				medicamentoVO.setMatCodigo(medicamento.getMatCodigo());
				medicamentoVO.setMedicamento(medicamento);
				unidadeDosagem = prescricaoMedicaFacade.obterVMpmDosagem(this.medicamento.getMatCodigo(), itemPrs.getFormaDosagem().getSeq());

				populaFieldsMpmItemPrescricaoMdto(itemPrs);

				StringBuilder returnValue = popularFieldsMedicamento();
				if (medicamento.getMpmUnidadeMedidaMedicas() != null) {
					returnValue.append(this.medicamento.getMpmUnidadeMedidaMedicas().getDescricao());
				}
				medicamentoVO.setDescricaoEditada(returnValue.toString());

				break;
			}
		}
	}

	private StringBuilder popularFieldsMedicamento() {
		StringBuilder returnValue = new StringBuilder("");
		if (medicamento.getDescricao() != null) {
			returnValue.append(medicamento.getDescricao());
			returnValue.append(' ');
		}
		if (medicamento.getConcentracao() != null) {
			Locale locBR = new Locale("pt", "BR");// Brasil
			DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
			dfSymbols.setDecimalSeparator(',');
			DecimalFormat format;
			if (this.medicamento.getConcentracao() != null && this.medicamento.getConcentracao().stripTrailingZeros().scale() <= 0) {
				format = new DecimalFormat("#,###,###,###,##0.###############", dfSymbols);
				returnValue.append(format.format(this.medicamento.getConcentracao()));
				returnValue.append(' ');
			} else if (this.medicamento.getConcentracao() != null) {
				format = new DecimalFormat("#,###,###,###,##0.0##############", dfSymbols);
				returnValue.append(format.format(this.medicamento.getConcentracao()));
				returnValue.append(' ');
			}
		}
		return returnValue;
	}

	private void populaFieldsMpmItemPrescricaoMdto(MpmItemPrescricaoMdto itemPrs) {
		if (itemPrs != null) {
			this.dosePediatrica = itemPrs.getQtdeParamCalculo() != null ? BigDecimal.valueOf(itemPrs.getQtdeParamCalculo()) : null;
			this.unidadeBaseParametroCalculo = itemPrs.getBaseParamCalculo();
			this.tipoCalculoDose = itemPrs.getTipoCalculoDose();
			this.duracao = itemPrs.getDuracaoParamCalculo();
			this.unidadeTempo = itemPrs.getUnidDuracaoCalculo();
			this.doseCalculada = itemPrs.getDoseCalculada() != null ? BigDecimal.valueOf(itemPrs.getDoseCalculada()) : null;
			this.unidadeDosagemPediatrica = itemPrs.getUnidadeMedidaMedica();
			this.frequenciaDosePed = this.frequencia;
			this.tipoAprazamentoDosePed = this.tipoAprazamento;
			this.viaDosePed = this.via;
			this.dosePediatricaCalculada = isDosePed(itemPrs);
		}
	}

	public void cancelarEdiMed() {
		this.setEdicao(false);
		matCodigo = null;
		medicamentoVO = null;
		medicamento = null;
		unidade = null;
		unidadeDosagem = null;
		dose = null;
		complemento = null;
		this.doseCalculada = null;
		this.dosePediatrica = null;
		this.unidadeDosagemPediatrica = null;
		this.unidadeBaseParametroCalculo = null;
		this.tipoCalculoDose = null;
		this.duracao = null;
		this.unidadeTempo = null;
		this.viaDosePed = null;
		this.frequenciaDosePed = null;
		this.tipoAprazamentoDosePed = null;
		this.exibirCalculoDosePediatrica = false;
		this.listaMedicamentosAux = DominioPadronizado.S;
	}

	public boolean verificarSeDoseFracionaria(Object valor) {
		boolean retorno = true;
		if (valor != null && ((BigDecimal) valor).stripTrailingZeros().scale() <= 0) {
			retorno = false;
		}
		return retorno;
	}

	public String obtemDescricaoUnidadeDosagem(Integer matCodigo, Integer seqFormaDosagem) {
		if (matCodigo != null && seqFormaDosagem != null) {
			VMpmDosagem dosagem = prescricaoMedicaFacade.obterVMpmDosagem(matCodigo, seqFormaDosagem);
			return dosagem != null ? dosagem.getSiglaUnidadeMedidaMedica() : null;
		}
		return null;
	}

	public String cancelar() {

		this.confirmaVoltar = false;

		if (this.medicamentoVO != null || (this.listaMedicamentosSolucao != null && this.listaMedicamentosSolucao.size() != numMedicamentos)) {
			this.confirmaVoltar = true;
			return null;
		}

		return this.voltar();
	}

	public List<SelectItem> getSelectListaDosagens() {
		List<SelectItem> lista = new LinkedList<SelectItem>();
		if (this.medicamento != null) {
			List<VMpmDosagem> dosagens = this.prescricaoMedicaFacade.buscarDosagensMedicamento(this.medicamento.getMatCodigo());
			for (VMpmDosagem dosagem : dosagens) {
				// lista.add(new SelectItem(dosagem,
				// dosagem.getDescricaoUnidade()));
				lista.add(new SelectItem(dosagem, dosagem.getSiglaUnidadeMedidaMedica()));

			}
			return lista;
		} else {
			return new LinkedList<SelectItem>();
		}
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public List<MpmPrescricaoMdto> getListaPrescricaoSolucoes() {
		return listaPrescricaoSolucoes;
	}

	public void setListaPrescricaoSolucoes(List<MpmPrescricaoMdto> listaPrescricaoSolucoes) {
		this.listaPrescricaoSolucoes = listaPrescricaoSolucoes;
	}

	public Map<MpmPrescricaoMdto, Boolean> getListaPrescricaoSolucoesSelecionadas() {
		return listaPrescricaoSolucoesSelecionadas;
	}

	public void setListaPrescricaoSolucoesSelecionadas(Map<MpmPrescricaoMdto, Boolean> listaPrescricaoSolucoesSelecionadas) {
		this.listaPrescricaoSolucoesSelecionadas = listaPrescricaoSolucoesSelecionadas;
	}

	public AfaViaAdministracao getVia() {
		return via;
	}

	public void setVia(AfaViaAdministracao via) {
		this.via = via;
	}

	public Boolean getTodasAsVias() {
		return todasAsVias;
	}

	public void setTodasAsVias(Boolean todasAsVias) {
		this.todasAsVias = todasAsVias;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public Boolean getExibirModal() {
		return exibirModal;
	}

	public void setExibirModal(Boolean exibirModal) {
		this.exibirModal = exibirModal;
	}

	public String getMensagemExibicaoModal() {
		return mensagemExibicaoModal;
	}

	public void setMensagemExibicaoModal(String mensagemExibicaoModal) {
		this.mensagemExibicaoModal = mensagemExibicaoModal;
	}

	public Short getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	public MpmTipoFrequenciaAprazamento getTipoAprazamento() {
		return tipoAprazamento;
	}

	public void setTipoAprazamento(MpmTipoFrequenciaAprazamento tipoAprazamento) {
		this.tipoAprazamento = tipoAprazamento;
	}

	public DominioUnidadeHorasMinutos getUnidHorasCorrer() {
		return unidHorasCorrer;
	}

	public void setUnidHorasCorrer(DominioUnidadeHorasMinutos unidHorasCorrer) {
		this.unidHorasCorrer = unidHorasCorrer;
	}

	public Short getQtdeHorasCorrer() {
		return qtdeHorasCorrer;
	}

	public void setQtdeHorasCorrer(Short qtdeHorasCorrer) {
		this.qtdeHorasCorrer = qtdeHorasCorrer;
	}

	public AfaTipoVelocAdministracoes getTipoVelocAdministracao() {
		return tipoVelocAdministracao;
	}

	public void setTipoVelocAdministracao(AfaTipoVelocAdministracoes tipoVelocAdministracao) {
		this.tipoVelocAdministracao = tipoVelocAdministracao;
	}

	public BigDecimal getGotejo() {
		return gotejo;
	}

	public void setGotejo(BigDecimal gotejo) {
		this.gotejo = gotejo;
	}

	public Boolean getIndBombaInfusao() {
		return indBombaInfusao;
	}

	public void setIndBombaInfusao(Boolean indBombaInfusao) {
		this.indBombaInfusao = indBombaInfusao;
	}

	public Date getHoraAdministracao() {
		return horaAdministracao;
	}

	public void setHoraAdministracao(Date horaAdministracao) {
		this.horaAdministracao = horaAdministracao;
	}

	public Boolean getIndSeNecessario() {
		return indSeNecessario;
	}

	public void setIndSeNecessario(Boolean indSeNecessario) {
		this.indSeNecessario = indSeNecessario;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getInformacoesFarmacologicas() {
		return informacoesFarmacologicas;
	}

	public void setInformacoesFarmacologicas(String informacoesFarmacologicas) {
		this.informacoesFarmacologicas = informacoesFarmacologicas;
	}

	public BigDecimal getDose() {
		return dose;
	}

	public void setDose(BigDecimal dose) {
		this.dose = dose;
	}

	public MpmUnidadeMedidaMedica getUnidade() {
		return unidade;
	}

	public void setUnidade(MpmUnidadeMedidaMedica unidade) {
		this.unidade = unidade;
	}

	public VMpmDosagem getUnidadeDosagem() {
		return unidadeDosagem;
	}

	public void setUnidadeDosagem(VMpmDosagem unidadeDosagem) {
		this.unidadeDosagem = unidadeDosagem;
	}

	public Boolean getListaMedicamentos() {
		return listaMedicamentos;
	}

	public void setListaMedicamentos(Boolean listaMedicamentos) {
		this.listaMedicamentos = listaMedicamentos;
	}

	public DominioPadronizado getListaMedicamentosAux() {
		return listaMedicamentosAux;
	}

	public void setListaMedicamentosAux(DominioPadronizado listaMedicamentosAux) {
		this.listaMedicamentosAux = listaMedicamentosAux;
	}

	public List<VMpmDosagem> getListaDosagens() {
		return listaDosagens;
	}

	public void setListaDosagens(List<VMpmDosagem> listaDosagens) {
		this.listaDosagens = listaDosagens;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento != null ? complemento.toUpperCase() : null;
	}

	public List<MpmItemPrescricaoMdto> getListaMedicamentosSolucao() {
		return listaMedicamentosSolucao;
	}

	public void setListaMedicamentosSolucao(List<MpmItemPrescricaoMdto> listaMedicamentosSolucao) {
		this.listaMedicamentosSolucao = listaMedicamentosSolucao;
	}

	public Boolean getEdicaoPrescricaoMedicamento() {
		return edicaoPrescricaoMedicamento;
	}

	public void setEdicaoPrescricaoMedicamento(Boolean edicaoPrescricaoMedicamento) {
		this.edicaoPrescricaoMedicamento = edicaoPrescricaoMedicamento;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}

	public IFarmaciaFacade getFarmaciaFacade() {
		return farmaciaFacade;
	}

	public MedicamentoVO getMedicamentoVO() {
		return medicamentoVO;
	}

	public void setMedicamentoVO(MedicamentoVO medicamentoVO) {
		this.medicamentoVO = medicamentoVO;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public int getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(int idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}

	public Date getDataInicioTratamento() {
		return dataInicioTratamento;
	}

	public void setDataInicioTratamento(Date dataInicioTratamento) {
		this.dataInicioTratamento = dataInicioTratamento;
	}

	public BigDecimal getDosePediatrica() {
		return dosePediatrica;
	}

	public void setDosePediatrica(BigDecimal dosePediatrica) {
		this.dosePediatrica = dosePediatrica;
	}

	public MpmUnidadeMedidaMedica getUnidadeDosagemPediatrica() {
		return unidadeDosagemPediatrica;
	}

	public void setUnidadeDosagemPediatrica(MpmUnidadeMedidaMedica unidadeDosagemPediatrica) {
		this.unidadeDosagemPediatrica = unidadeDosagemPediatrica;
	}

	public DominioUnidadeBaseParametroCalculo getUnidadeBaseParametroCalculo() {
		return unidadeBaseParametroCalculo;
	}

	public void setUnidadeBaseParametroCalculo(DominioUnidadeBaseParametroCalculo unidadeBaseParametroCalculo) {
		this.unidadeBaseParametroCalculo = unidadeBaseParametroCalculo;
	}

	public DominioTipoCalculoDose getTipoCalculoDose() {
		return tipoCalculoDose;
	}

	public void setTipoCalculoDose(DominioTipoCalculoDose tipoCalculoDose) {
		this.tipoCalculoDose = tipoCalculoDose;
	}

	public String getMensagemModal() {
		return mensagemModal;
	}

	public void setMensagemModal(String mensagemModal) {
		this.mensagemModal = mensagemModal;
	}

	public MpmParamCalculoPrescricao getParametroCalculo() {
		return parametroCalculo;
	}

	public void setParametroCalculo(MpmParamCalculoPrescricao parametroCalculo) {
		this.parametroCalculo = parametroCalculo;
	}

	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	public BigDecimal getAltura() {
		return altura;
	}

	public void setAltura(BigDecimal altura) {
		this.altura = altura;
	}

	public BigDecimal getSc() {
		return sc;
	}

	public void setSc(BigDecimal sc) {
		this.sc = sc;
	}

	public BigDecimal getDoseCalculada() {
		return doseCalculada;
	}

	public void setDoseCalculada(BigDecimal doseCalculada) {
		this.doseCalculada = doseCalculada;
	}

	public DominioTipoMedicaoPeso getTipoMedicao() {
		return tipoMedicao;
	}

	public void setTipoMedicao(DominioTipoMedicaoPeso tipoMedicao) {
		this.tipoMedicao = tipoMedicao;
	}

	public Boolean getExibirModalPesoAltura() {
		return exibirModalPesoAltura;
	}

	public void setExibirModalPesoAltura(Boolean exibirModalPesoAltura) {
		this.exibirModalPesoAltura = exibirModalPesoAltura;
	}

	public Boolean getExibirCalculoDosePediatrica() {
		return exibirCalculoDosePediatrica;
	}

	public void setExibirCalculoDosePediatrica(Boolean exibirCalculoDosePediatrica) {
		this.exibirCalculoDosePediatrica = exibirCalculoDosePediatrica;
	}

	public Boolean getPossuiPesoAlturaDia() {
		return possuiPesoAlturaDia;
	}

	public void setPossuiPesoAlturaDia(Boolean possuiPesoAlturaDia) {
		this.possuiPesoAlturaDia = possuiPesoAlturaDia;
	}

	public Boolean getOcultarModalPesoAltura() {
		return ocultarModalPesoAltura;
	}

	public void setOcultarModalPesoAltura(Boolean ocultarModalPesoAltura) {
		this.ocultarModalPesoAltura = ocultarModalPesoAltura;
	}

	public Integer getDuracao() {
		return duracao;
	}

	public void setDuracao(Integer duracao) {
		this.duracao = duracao;
	}

	public DominioDuracaoCalculo getUnidadeTempo() {
		return unidadeTempo;
	}

	public void setUnidadeTempo(DominioDuracaoCalculo unidadeTempo) {
		this.unidadeTempo = unidadeTempo;
	}

	public AfaViaAdministracao getViaDosePed() {
		return viaDosePed;
	}

	public void setViaDosePed(AfaViaAdministracao viaDosePed) {
		this.viaDosePed = viaDosePed;
	}

	public Short getFrequenciaDosePed() {
		return frequenciaDosePed;
	}

	public void setFrequenciaDosePed(Short frequenciaDosePed) {
		this.frequenciaDosePed = frequenciaDosePed;
	}

	public MpmTipoFrequenciaAprazamento getTipoAprazamentoDosePed() {
		return tipoAprazamentoDosePed;
	}

	public void setTipoAprazamentoDosePed(MpmTipoFrequenciaAprazamento tipoAprazamentoDosePed) {
		this.tipoAprazamentoDosePed = tipoAprazamentoDosePed;
	}

	public Integer getMatCodigoMedicamentoEdicao() {
		return matCodigoMedicamentoEdicao;
	}

	public void setMatCodigoMedicamentoEdicao(Integer matCodigoMedicamentoEdicao) {
		this.matCodigoMedicamentoEdicao = matCodigoMedicamentoEdicao;
	}

	public Boolean getPrescricaoAmbulatorial() {
		return prescricaoAmbulatorial;
	}

	public void setPrescricaoAmbulatorial(Boolean prescricaoAmbulatorial) {
		this.prescricaoAmbulatorial = prescricaoAmbulatorial;
	}
	public MpmItemPrescricaoMdto getItemSelecionadoExclusao() {
		return itemSelecionadoExclusao;
	}
	public void setItemSelecionadoExclusao(
			MpmItemPrescricaoMdto itemSelecionadoExclusao) {
		this.itemSelecionadoExclusao = itemSelecionadoExclusao;
	}
}