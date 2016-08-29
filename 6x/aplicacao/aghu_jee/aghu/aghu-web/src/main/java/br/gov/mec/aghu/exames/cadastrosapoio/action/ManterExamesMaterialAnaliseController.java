package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioNaturezaExame;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioResponsavelColetaExames;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaAmostra;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.faturamento.action.RelacionarPHISSMController;
import br.gov.mec.aghu.faturamento.action.RelacionarPHISSMController.RelacionarPHISSMControllerExceptionCode;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.VFatConvPlanoGrupoProcedVO;
import br.gov.mec.aghu.model.AelAnticoagulante;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelModeloCartas;
import br.gov.mec.aghu.model.AelRecipienteColeta;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterExamesMaterialAnaliseController extends ActionController {

	private static final String _HIFEN_ = " - ";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	private static final Log LOG = LogFactory.getLog(ManterExamesMaterialAnaliseController.class);

	private static final long serialVersionUID = 1551324472178719977L;

	private static final String PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD = "exames-manterDadosBasicosExamesCRUD";
	private static final String PAGE_EXAMES_TIPO_AMOSTRA_CONVENIO_CRUD = "exames-tipoAmostraConvenioCRUD";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	

	// Injeção necessária para facilitar a atualização da lista de materiais de análise na tela de dados básicos de exame
	@Inject
	private ManterDadosBasicosExamesController manterDadosBasicosExamesController;

	private Integer currentSlider = 0;
	private AelExames exame;
	private String sigla;
	private Integer manSeq;
	private boolean desabilitarCodigo = false;
    private static final Integer VALOR_ACCORDION_FLUXOGRAMA_ABERTO = 0;
    private static final Integer VALOR_ACCORDION_FLUXOGRAMA_FECHADO = -1;
    private Integer openToggle = -1;
    private Integer openToggle2 = -1;

	// Referências de #5754 Manter as características dos materias de anális
	private AelExamesMaterialAnalise examesMaterialAnalise;
	private AelExamesMaterialAnalise examesMaterialAnaliseAux;

	// Referências de #5335 Manter Tipos de Amostras por Exame
	private List<AelTipoAmostraExame> listaTiposAmostraExame;
	private AelTipoAmostraExame tipoAmostraExame;
	private AelTipoAmostraExame tipoAmostraExameExcluir;
	private AelTipoAmostraExame tipoAmostraExameAntigo;

	private boolean editandoTipoAmostraExame;
	private boolean voltouManterTipoAmostraConvenio;
	
	// 38816
		@Inject
		private RelacionarPHISSMController relacionarPHISSMController;

		

		private Boolean exibirPainelInferior = false;
		private Boolean alterouItensProcedimento = false;
		private VFatConvPlanoGrupoProcedVO convenio;
		private List<FatConvGrupoItemProced> convGrupoItemProcedList;
		private List<FatConvGrupoItemProced> convGrupoItemProcedListOriginal;
		private List<FatConvGrupoItemProced> convGrupoItemProcedListRemover;
		private Integer phi;
		private FatConvenioSaudePlano plano;
		private FatConvGrupoItemProced fatConvGrupoItemProcedSelecionado;

		private boolean obrigatoriedadeSigtap;
		
		
		public enum ProcedimentoCirurgicoControllerExceptionCode implements BusinessExceptionCode {
			ERRO_ADICIONAR_PROCEDIMENTO_SUS, ERRO_PROCEDIMENTO_SUS_JA_CADASTRADO
		}

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
	 
         this.openToggle=VALOR_ACCORDION_FLUXOGRAMA_ABERTO;
         this.openToggle2=VALOR_ACCORDION_FLUXOGRAMA_FECHADO;

		// this.currentSlider = 0;
		this.editandoTipoAmostraExame = false;

		if (StringUtils.isNotBlank(this.sigla) || this.exame != null) {
			this.exame = this.examesFacade.obterAelExamesPeloId((this.exame != null) ? this.exame.getSigla() : this.sigla);
		}

		if ((StringUtils.isNotBlank(this.sigla) || this.exame != null) && this.manSeq != null) {
			this.examesMaterialAnalise = this.examesFacade.buscarAelExamesMaterialAnalisePorId((this.exame != null) ? this.exame.getSigla() : this.sigla, this.manSeq);
		}

		if (this.examesMaterialAnalise == null) {
			// Instancia um novo material de análise de exame
			this.examesMaterialAnalise = new AelExamesMaterialAnalise();

			// Seta o exame novo material de análise de exame
			this.examesMaterialAnalise.setAelExames(this.exame);

			// Seta atributos do material de análise de exame com o valor default
			this.examesMaterialAnalise.setIndImpTicketPaciente(true);
			this.examesMaterialAnalise.setIndSituacao(DominioSituacao.A);
			this.examesMaterialAnalise.setNatureza(DominioNaturezaExame.EXAME_COMPLEMENTAR);
			this.examesMaterialAnalise.setPertenceSumario(DominioSumarioExame.S);
			this.examesMaterialAnalise.setIndPermiteSolicAlta(DominioSimNao.N);

			// Inicializa uma nova lista de tipos de amostra de exame
			this.listaTiposAmostraExame = new LinkedList<AelTipoAmostraExame>();

		} else {

			if (this.examesMaterialAnalise.getId() != null && !this.voltouManterTipoAmostraConvenio) {
				// Resgata a lista de tipos de amostra de material de análise de exame em edição
				this.listaTiposAmostraExame = this.examesFacade.buscarListaAelTipoAmostraExamePorEmaExaSiglaEmaManSeq((this.exame != null) ? this.exame.getSigla() : this.sigla, this.manSeq);
			}

		}

		// Instancia um novo tipo de amostra de material de análise de exame
		gerarNovoTipoAmostraExame();

		setExamesMaterialAnaliseAux(examesMaterialAnalise);
	}

	/**
	 * Verifica se o item da lista foi selecionado
	 * 
	 * @param itemSelecionado
	 * @return
	 */
	public boolean verificarItemSelecionadoEdicao(AelTipoAmostraExame itemSelecionado) {

		if (itemSelecionado != null) {
            DominioOrigemAtendimento org1 = this.tipoAmostraExame.getOrigemAtendimento();
            DominioOrigemAtendimento org2 = itemSelecionado.getOrigemAtendimento();

            Integer matSeq1 = this.tipoAmostraExame.getMaterialAnalise() != null ? this.tipoAmostraExame.getMaterialAnalise().getSeq() : null;
            Integer matSeq2 = itemSelecionado.getMaterialAnalise() != null ? itemSelecionado.getMaterialAnalise().getSeq() : null;

            if (org1.equals(org2) && matSeq1 != null && matSeq1.equals(matSeq2)) {
                return true;
            }
        }
		return false;
	}

	/**
	 * Verifica se o item da lista foi selecionado
	 * 
	 * @param itemSelecionado
	 * @return
	 */
	public String manterTipoAmostraConvenio(AelTipoAmostraExame itemSelecionado) {

//		if (verificarAlteradoOutroUsuario(itemSelecionado)) {
//			return null;
//		}

		if (itemSelecionado == null || itemSelecionado.getId() == null) {
			this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_NECESSARIO_GRAVAR_TIPO_AMOSTRA_CONVENIO");
			return null;
		}

		return PAGE_EXAMES_TIPO_AMOSTRA_CONVENIO_CRUD;
	}

	/**
	 * Confirma a persisência das informações
	 * 
	 * @return
	 */
	public String confirmar() {
		if (editandoTipoAmostraExame) {

			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EDICAO_AMOSTRAS_EXAME");
			return null;
		}
		
		if (exibirPainelInferior && obrigatoriedadeSigtap && (convGrupoItemProcedList == null || convGrupoItemProcedList.isEmpty())) {
			if (!gravarRelacionamentoPHIePROCEDSUS(obrigatoriedadeSigtap)) {
				return "";
			}
		}

		try {
			// Determina o tipo de operação de persistencia
			String descricaoMaterialAnalise = this.examesMaterialAnalise.getAelMateriaisAnalises().getDescricao();
			if (this.examesMaterialAnalise.getId() == null) {
				this.cadastrosApoioExamesFacade.inserirAelExamesMaterialAnaliseTipoAmostraExame(this.examesMaterialAnalise, this.listaTiposAmostraExame);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_MATERIAL_ANALISE_EXAMES", descricaoMaterialAnalise);
			} else {
				this.cadastrosApoioExamesFacade.atualizarAelExamesMaterialAnaliseTiposAmostraExame(this.examesMaterialAnalise, this.examesMaterialAnaliseAux, this.listaTiposAmostraExame);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_MATERIAL_ANALISE_EXAMES", descricaoMaterialAnalise);
			}

			// Limpa instâncias utilizadas durante a conversação			
			this.examesMaterialAnalise = null;
			this.examesMaterialAnaliseAux = null;
			this.exame = null;
			this.manSeq = null;
			this.desabilitarCodigo = false;

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		// Atualiza a lista de materiais de análise na tela de dados básicos de exame
		this.manterDadosBasicosExamesController.montaListaMateriais();
		this.limparParametros();
		
		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}

	/**
	 * Excluir
	 */
	public void excluir() {
		try {

			AelExamesMaterialAnalise examesMaterialAnaliseExclusao = null;

			if (StringUtils.isNotBlank(this.sigla) && this.manSeq != null) {
				examesMaterialAnaliseExclusao = this.examesFacade.buscarAelExamesMaterialAnalisePorId(this.sigla, this.manSeq);
			}

			if (examesMaterialAnaliseExclusao != null) {
				this.cadastrosApoioExamesFacade.removerAelExamesMaterialAnalise(examesMaterialAnaliseExclusao);

				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_MATERIAL_ANALISE_EXAMES", examesMaterialAnalise.getAelMateriaisAnalises().getDescricao());
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_REMOCAO_MATERIAL_ANALISE_EXAMES");
			}

		} catch (BaseListException e) {
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = (BaseException) errors.next();
				super.apresentarExcecaoNegocio(aghuNegocioException);
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método que realiza a ação do botão cancelar
	 */
	public String cancelar() {
		try {
			if (this.examesMaterialAnalise != null && this.examesMaterialAnalise.getId() != null && this.examesMaterialAnalise.getAelMateriaisAnalises() != null) {
				this.cadastrosApoioExamesFacade.verificarTiposAmostraExame(this.examesMaterialAnalise, this.listaTiposAmostraExame);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		// Atualiza a lista de materiais de análise na tela de dados básicos de exame
		this.manterDadosBasicosExamesController.montaListaMateriais();
		this.limparParametros();
		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}

	private void limparParametros() {
		this.currentSlider = 0;
		this.exame = null;
		this.sigla = null;
		manSeq = null;
		this.desabilitarCodigo = false;

		this.examesMaterialAnalise = null;
		this.examesMaterialAnaliseAux = null;

		this.listaTiposAmostraExame = null;
		this.tipoAmostraExame = null;
		this.tipoAmostraExameExcluir = null;
		this.tipoAmostraExameAntigo = null;

		this.editandoTipoAmostraExame = false;
		this.voltouManterTipoAmostraConvenio = false;
	}

    public void colapsePanel(){
        if(openToggle == VALOR_ACCORDION_FLUXOGRAMA_ABERTO){
            openToggle=VALOR_ACCORDION_FLUXOGRAMA_FECHADO;
        } else {
            openToggle=VALOR_ACCORDION_FLUXOGRAMA_ABERTO;
        }
    }

    public void colapsePanel2(){
        if(openToggle2 == VALOR_ACCORDION_FLUXOGRAMA_ABERTO){
            openToggle2=VALOR_ACCORDION_FLUXOGRAMA_FECHADO;
        } else {
            openToggle2=VALOR_ACCORDION_FLUXOGRAMA_ABERTO;
        }
    }

	/*
	 * Metódos para Suggestion Box de #5754 Manter as características dos materias de análise...
	 */

	// Metódo para Suggestion Box de Material de Análise de Exames
	public List<AelMateriaisAnalises> obterMateriaisAnalise(String parametro) {
		return this.examesFacade.listarAelMateriaisAnalises(parametro);
	}

	// Metódo para Suggestion Box de Emissão de Carta de Coleta
	public List<AelModeloCartas> obterModelosCartasColeta(String parametro) {
		return this.examesFacade.listarAelModeloCartas(parametro);
	}

	/*
	 * Metódos para Suggestion Box de #5335 Manter Tipos de Amostras por Exame...
	 */

	// Metódo para Suggestion Box de Material de Análise de Exames
	public List<AelMateriaisAnalises> obterMaterialAnaliseAtivoColetavel(String parametro) {
		return this.examesFacade.listarAelMateriaisAnalisesAtivoColetavel(parametro);
	}

	// Metódo para Suggestion Box de Recipiente
	public List<AelRecipienteColeta> obterRecipienteAtivoColetavel(String parametro) {
		return this.examesFacade.listarAelRecipienteAtivoColetavel(parametro);
	}

	// Metódo para Suggestion Box de Anticoagulante
	public List<AelAnticoagulante> obterAnticoagulanteAtivo(String parametro) {
		return this.examesFacade.listarAelAnticoagulanteAtivo(parametro);
	}

	// Metódo para Suggestion Box de Unidade de Coleta (Unidade Funcional)
	public List<AghUnidadesFuncionais> obterUnidadeColeta(String parametro) {
		return this.aghuFacade.listarAghUnidadesFuncionaisAtivasColetaveis(parametro);
	}

	/**
	 * Adiciona/Edita item do tipo de amostra de exame da lista
	 */
	public void adicionarItemTipoAmostraExame() {

		// Valida a adição de itens na lista evitando a indução ao erro do usuário.
		try {

			this.cadastrosApoioExamesFacade.validarAdicionarItemTipoAmostraExame(tipoAmostraExame, examesMaterialAnalise, listaTiposAmostraExame);

			// Testa tipo de operação
			if (!this.isEditandoTipoAmostraExame()) { // Inserir

				this.listaTiposAmostraExame.add(this.tipoAmostraExame);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ADICAO_TIPO_AMOSTRA_EXAMES", this.tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_
						+ this.tipoAmostraExame.getMaterialAnalise().getDescricao());

			} else { // Alterar

				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_TIPO_AMOSTRA_EXAMES", this.tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_
						+ this.tipoAmostraExame.getMaterialAnalise().getDescricao());
				this.editandoTipoAmostraExame = false;

			}

			// Reseta instâncias de controle...
			this.tipoAmostraExameAntigo = null;
			this.gerarNovoTipoAmostraExame();

		} catch (ApplicationBusinessException e) {

			apresentarExcecaoNegocio(e);

		}

	}

	/**
	 * Editar item do tipo de amostra de exame da lista
	 */
	public void editarItemTipoAmostraExame(AelTipoAmostraExame aelTipoAmostraExame) {

//		if (verificarAlteradoOutroUsuario(aelTipoAmostraExame)) {
//			return;
//		}

		this.tipoAmostraExame = aelTipoAmostraExame;
		this.tipoAmostraExameAntigo = new AelTipoAmostraExame();

		try {

			this.tipoAmostraExameAntigo = (AelTipoAmostraExame) BeanUtils.cloneBean(tipoAmostraExame);

		} catch (Exception e) {
			LOG.error("Exceção caputada:", e);
		}

	}

	/**
	 * Cancela a edição item do tipo de amostra de exame da lista
	 */
	public void cancelarEdicaoItemTipoAmostraExame() {

		this.editandoTipoAmostraExame = false;

		for (int i = 0; i < this.listaTiposAmostraExame.size(); i++) {
			if (this.verificarItemSelecionadoEdicao(this.listaTiposAmostraExame.get(i))) {
				this.listaTiposAmostraExame.set(i, this.tipoAmostraExameAntigo);
			}
		}

		this.tipoAmostraExameAntigo = null;
		this.gerarNovoTipoAmostraExame();

	}

	/**
	 * Remove item do tipo de amostra de exame da lista
	 */
	public void excluirItemTipoAmostraExame() {

		try {

//			if (verificarAlteradoOutroUsuario(this.tipoAmostraExameExcluir)) {
//				return;
//			}

			// Remove item da lista armazenado somente na memória
			if (this.tipoAmostraExameExcluir.getId() == null) {
				this.listaTiposAmostraExame.remove(this.tipoAmostraExameExcluir);
			} else { // Remove item da lista armazenado no banco
				this.cadastrosApoioExamesFacade.removerAelTipoAmostraExame(this.tipoAmostraExameExcluir);
				this.listaTiposAmostraExame.remove(this.tipoAmostraExameExcluir);
			}
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_TIPO_AMOSTRA_EXAMES", this.tipoAmostraExameExcluir.getOrigemAtendimento().getDescricao() + _HIFEN_
					+ this.tipoAmostraExameExcluir.getMaterialAnalise().getDescricao());
		} catch (BaseListException e) {
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = (BaseException) errors.next();
				super.apresentarExcecaoNegocio(aghuNegocioException);
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_REMOCAO_TIPO_AMOSTRA_EXAMES", this.tipoAmostraExameExcluir.getOrigemAtendimento().getDescricao() + _HIFEN_
					+ this.tipoAmostraExameExcluir.getMaterialAnalise().getDescricao());
		} finally {
			// Limpa instancia de item da lista
			this.tipoAmostraExameExcluir = null;
		}

	}
	/**
	 * Gera uma nova instância de tipo de amostra de exame com valores default
	 */
	private void gerarNovoTipoAmostraExame() {

		// Instancia um novo tipo de amostra de material de análise de exame
		this.tipoAmostraExame = new AelTipoAmostraExame();

		// Seta atributos do tipo de amostra de material de análise de exame com o valor default
		this.tipoAmostraExame.setOrigemAtendimento(DominioOrigemAtendimento.A);
		this.tipoAmostraExame.setUnidadeMedidaAmostra(DominioUnidadeMedidaAmostra.ML);
		this.tipoAmostraExame.setResponsavelColeta(DominioResponsavelColetaExames.C);
		this.tipoAmostraExame.setIndCongela(DominioSimNao.N);
	}

//	private boolean verificarAlteradoOutroUsuario(AelTipoAmostraExame entidade) {
//		if (entidade == null || this.examesFacade.obterAelTipoAmostraExame(entidade.getId()) == null) {
//			apresentarMsgNegocio(Severity.INFO, "OPTIMISTIC_LOCK");
//			this.listaTiposAmostraExame = this.examesFacade.buscarListaAelTipoAmostraExamePorEmaExaSiglaEmaManSeq((this.exame != null) ? this.exame.getSigla() : this.sigla, this.manSeq);
//			return true;
//		}
//		return false;
//	}

	/*
	 * Getters and Setters de #5754 Manter as características dos materias de análise...
	 */

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public boolean isDesabilitarCodigo() {
		return desabilitarCodigo;
	}

	public void setDesabilitarCodigo(boolean desabilitarCodigo) {
		this.desabilitarCodigo = desabilitarCodigo;
	}

	public AelExamesMaterialAnalise getExamesMaterialAnalise() {
		return examesMaterialAnalise;
	}

	public void setExamesMaterialAnalise(AelExamesMaterialAnalise examesMaterialAnalise) {
		this.examesMaterialAnalise = examesMaterialAnalise;
	}

	public Integer getManSeq() {
		return manSeq;
	}

	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}

	public AelExames getExame() {
		return exame;
	}

	public void setExame(AelExames exame) {
		this.exame = exame;
	}

	public AelExamesMaterialAnalise getExamesMaterialAnaliseAux() {
		return examesMaterialAnaliseAux;
	}

	public void setExamesMaterialAnaliseAux(AelExamesMaterialAnalise exaMatAux) {
		this.examesMaterialAnaliseAux = new AelExamesMaterialAnalise();

		examesMaterialAnaliseAux.setCriadoEm(exaMatAux.getCriadoEm());
		examesMaterialAnaliseAux.setAelMateriaisAnalises(exaMatAux.getAelMateriaisAnalises());
		examesMaterialAnaliseAux.setId(exaMatAux.getId());
		examesMaterialAnaliseAux.setIndCci(exaMatAux.getIndCci());
		examesMaterialAnaliseAux.setIndComedi(exaMatAux.getIndComedi());
		examesMaterialAnaliseAux.setIndDependente(exaMatAux.getIndDependente());
		examesMaterialAnaliseAux.setIndDietaDiferenciada(exaMatAux.getIndDietaDiferenciada());
		examesMaterialAnaliseAux.setIndExigeRegiaoAnatomica(exaMatAux.getIndExigeRegiaoAnatomica());
		examesMaterialAnaliseAux.setIndFormaRespiracao(exaMatAux.getIndFormaRespiracao());
		examesMaterialAnaliseAux.setIndGeraItemPorColetas(exaMatAux.getIndGeraItemPorColetas());
		examesMaterialAnaliseAux.setIndImpTicketPaciente(exaMatAux.getIndImpTicketPaciente());
		examesMaterialAnaliseAux.setIndJejum(exaMatAux.getIndJejum());
		examesMaterialAnaliseAux.setIndLimitaSolic(exaMatAux.getIndLimitaSolic());
		examesMaterialAnaliseAux.setIndNpo(exaMatAux.getIndNpo());
		examesMaterialAnaliseAux.setIndPermiteSolicAlta(exaMatAux.getIndPermiteSolicAlta());
		examesMaterialAnaliseAux.setIndPertenceContador(exaMatAux.getIndPertenceContador());
		examesMaterialAnaliseAux.setIndPreparo(exaMatAux.getIndPreparo());
		examesMaterialAnaliseAux.setIndSituacao(exaMatAux.getIndSituacao());
		examesMaterialAnaliseAux.setIndSolicInformaColetas(exaMatAux.getIndSolicInformaColetas());
		examesMaterialAnaliseAux.setIndSolSistema(exaMatAux.getIndSolSistema());
		examesMaterialAnaliseAux.setIndTipoTelaLiberaResu(exaMatAux.getIndTipoTelaLiberaResu());
		examesMaterialAnaliseAux.setIndUsaIntervaloCadastrado(exaMatAux.getIndUsaIntervaloCadastrado());
		examesMaterialAnaliseAux.setIndVerificaMedicacao(exaMatAux.getIndVerificaMedicacao());
		examesMaterialAnaliseAux.setAelUnfExecutaExames(exaMatAux.getAelUnfExecutaExames());
		examesMaterialAnaliseAux.setTempoHoraAmostraDefault(exaMatAux.getTempoHoraAmostraDefault());
		examesMaterialAnaliseAux.setUnidTempoLimiteSol(exaMatAux.getUnidTempoLimiteSol());
		examesMaterialAnaliseAux.setUnidTempoColetaAmostras(exaMatAux.getUnidTempoColetaAmostras());
		examesMaterialAnaliseAux.setUnidTempoLimitePeriodo(exaMatAux.getUnidTempoLimitePeriodo());
		examesMaterialAnaliseAux.setNatureza(exaMatAux.getNatureza());
		examesMaterialAnaliseAux.setPertenceSumario(exaMatAux.getPertenceSumario());
		examesMaterialAnaliseAux.setTempoDiaAmostraDefault(exaMatAux.getTempoDiaAmostraDefault());
		examesMaterialAnaliseAux.setServidor(exaMatAux.getServidor());
		try {
			examesMaterialAnaliseAux.setServidorAlterado(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
		} catch (ApplicationBusinessException e) {
			examesMaterialAnaliseAux.setServidorAlterado(null);
		}
		examesMaterialAnaliseAux.setAelExames(exaMatAux.getAelExames());
		examesMaterialAnaliseAux.setAelModeloCartas(exaMatAux.getAelModeloCartas());
		examesMaterialAnaliseAux.setTempoSolicAlta(exaMatAux.getTempoSolicAlta());
		examesMaterialAnaliseAux.setTempoSolicAltaCompl(exaMatAux.getTempoSolicAltaCompl());
	}

	/*
	 * Getters and Setters de #5335 Manter Tipos de Amostras por Exame......
	 */

	public AelTipoAmostraExame getTipoAmostraExame() {
		return tipoAmostraExame;
	}

	public void setTipoAmostraExame(AelTipoAmostraExame tipoAmostraExame) {
		this.tipoAmostraExame = tipoAmostraExame;
	}

	public Integer getCurrentSlider() {
		return currentSlider;
	}

	public void setCurrentSlider(Integer currentSlider) {
		this.currentSlider = currentSlider;
	}

	public List<AelTipoAmostraExame> getListaTiposAmostraExame() {
		return listaTiposAmostraExame;
	}

	public void setListaTiposAmostraExame(List<AelTipoAmostraExame> listaTiposAmostraExame) {
		this.listaTiposAmostraExame = listaTiposAmostraExame;
	}

	public boolean isEditandoTipoAmostraExame() {
		return editandoTipoAmostraExame;
	}

	public void setEditandoTipoAmostraExame(boolean editandoTipoAmostraExame) {
		this.editandoTipoAmostraExame = editandoTipoAmostraExame;
	}

	public AelTipoAmostraExame getTipoAmostraExameAntigo() {
		return tipoAmostraExameAntigo;
	}

	public void setTipoAmostraExameAntigo(AelTipoAmostraExame tipoAmostraExameAntigo) {
		this.tipoAmostraExameAntigo = tipoAmostraExameAntigo;
	}

	public AelTipoAmostraExame getTipoAmostraExameExcluir() {
		return tipoAmostraExameExcluir;
	}

	public void setTipoAmostraExameExcluir(AelTipoAmostraExame tipoAmostraExameExcluir) {
		this.tipoAmostraExameExcluir = tipoAmostraExameExcluir;
	}

	public boolean isVoltouManterTipoAmostraConvenio() {
		return voltouManterTipoAmostraConvenio;
	}

	public void setVoltouManterTipoAmostraConvenio(boolean voltouManterTipoAmostraConvenio) {
		this.voltouManterTipoAmostraConvenio = voltouManterTipoAmostraConvenio;
	}
	

		private void iniciarVariaveisProcedimentosHospInternos() {
			relacionarPHISSMController.setConvGrupoItemProced(new FatConvGrupoItemProced());
			relacionarPHISSMController.setItemProcedHospSus(null);
			relacionarPHISSMController.setEdicao(false);
		}

		public void adicionar() {
			if (getConvenio() == null) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SUCESSO_REMOCAO_GRUPO_CARACTERISTICA", ProcedimentoCirurgicoControllerExceptionCode.ERRO_ADICIONAR_PROCEDIMENTO_SUS.toString());
				return;
			}
			
			if (relacionarPHISSMController.getItemProcedHospSus() == null) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SUCESSO_REMOCAO_GRUPO_CARACTERISTICA", ProcedimentoCirurgicoControllerExceptionCode.ERRO_ADICIONAR_PROCEDIMENTO_SUS.toString());
				return;
			}

			relacionarPHISSMController.setConvenio(convenio);

			if (relacionarPHISSMController.getEdicao()) {
				//Remove o Item da Lista e adiciona na lista de itens a serem removidos no momento da gravação.
				excluirProcedimentoRelacionado(fatConvGrupoItemProcedSelecionado);

				FatConvenioSaudePlanoId fatConvenioSaudePlanoId = new FatConvenioSaudePlanoId();
				fatConvenioSaudePlanoId.setSeq(plano.getId().getSeq());
				FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
				fatConvenioSaudePlano.setId(fatConvenioSaudePlanoId);
				fatConvenioSaudePlano.setDescricao(plano.getDescricao());
				
				FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
				fatConvenioSaude.setCodigo(convenio.getCphCspCnvCodigo());
				fatConvenioSaude.setDescricao(convenio.getCnvDescricao());
				fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);
				
				popularValoresPadroesConvGrupoItemProced();
				FatConvGrupoItemProced fatConvGrupoItemProced = new FatConvGrupoItemProced(relacionarPHISSMController.getConvGrupoItemProced());
				fatConvGrupoItemProced.setItemProcedHospitalar(relacionarPHISSMController.getItemProcedHospSus());
				fatConvGrupoItemProced.setConvenioSaudePlano(fatConvenioSaudePlano);
				convGrupoItemProcedList.add(fatConvGrupoItemProced);
			} else {
				boolean add;
				for (VFatConvPlanoGrupoProcedVO plano : relacionarPHISSMController.listarPlanos("")) {
					FatConvenioSaudePlanoId fatConvenioSaudePlanoId = new FatConvenioSaudePlanoId();
					fatConvenioSaudePlanoId.setSeq(plano.getCphCspSeq());
					FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
					fatConvenioSaudePlano.setId(fatConvenioSaudePlanoId);
					fatConvenioSaudePlano.setDescricao(plano.getCspDescricao());
					FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
					fatConvenioSaude.setCodigo(convenio.getCphCspCnvCodigo());
					fatConvenioSaude.setDescricao(convenio.getCnvDescricao());
					fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

					popularValoresPadroesConvGrupoItemProced();
					FatConvGrupoItemProced fatConvGrupoItemProced = new FatConvGrupoItemProced(relacionarPHISSMController.getConvGrupoItemProced());
					fatConvGrupoItemProced.setItemProcedHospitalar(relacionarPHISSMController.getItemProcedHospSus());
					add = true;
					for (FatConvGrupoItemProced iterable_element : convGrupoItemProcedList) {
						if (iterable_element.getItemProcedHospitalar().getCodTabela().equals(fatConvGrupoItemProced.getItemProcedHospitalar().getCodTabela())
								&& iterable_element.getConvenioSaudePlano().getId().getSeq().equals(plano.getCphCspSeq())) {
							add = false;
							break;
						}
					}
					if (add) {
						fatConvGrupoItemProced.setConvenioSaudePlano(fatConvenioSaudePlano);
						convGrupoItemProcedList.add(fatConvGrupoItemProced);
					} else {
						this.apresentarMsgNegocio(Severity.ERROR, "FAT_00073");
					}
				}
			}
			alterouItensProcedimento = true;
			iniciarVariaveisProcedimentosHospInternos();
		}

		private Boolean gravarRelacionamentoPHIePROCEDSUS(Boolean obrigatoriedadeSigtap) {
			if (convGrupoItemProcedList == null || convGrupoItemProcedList.isEmpty()) {
				if (obrigatoriedadeSigtap) {
					this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PROCEDIMENTO_OBRIGATORIO");
					return false;
				} else {
					for (FatConvGrupoItemProced fatConvGrupoItemProced : convGrupoItemProcedListRemover) {
						relacionarPHISSMController.removerProcedimento(fatConvGrupoItemProced);
					}
					relacionarPHISSMController.criarNotificacoesUsuarios(getPhi(), null);
					return true;
				}
			}
			try {
				List<FatConvGrupoItemProced> convGrupoItemProcedListClone = clonarConvGrupoItemProcedList();
				for (FatConvGrupoItemProced fatConvGrupoItemProced : convGrupoItemProcedListRemover) {
					relacionarPHISSMController.removerProcedimento(fatConvGrupoItemProced);
				}
				for (Iterator<FatConvGrupoItemProced> iterator = convGrupoItemProcedListClone.iterator(); iterator.hasNext();) {
					FatConvGrupoItemProced fatConvGrupoItemProced = iterator.next();
					if (convenio != null) {
						relacionarPHISSMController.setConvenio(convenio);
					}
					VFatConvPlanoGrupoProcedVO plano = new VFatConvPlanoGrupoProcedVO();
					plano.setCphCspSeq(fatConvGrupoItemProced.getConvenioSaudePlano().getId().getSeq());
					plano.setCspDescricao(fatConvGrupoItemProced.getConvenioSaudePlano().getDescricao());
					relacionarPHISSMController.setPlano(plano);
					relacionarPHISSMController.setItemProcedHospSus(fatConvGrupoItemProced.getItemProcedHospitalar());
					FatProcedHospInternos fatProcedHospInternos = faturamentoFacade.obterFatProcedHospInternosPorMaterial(examesMaterialAnalise.getId());
					
					if (fatProcedHospInternos != null) {
						relacionarPHISSMController.setProcedHospInterno(fatProcedHospInternos);
					}
					
					relacionarPHISSMController.setConvGrupoItemProced(fatConvGrupoItemProced);
					relacionarPHISSMController.adicionarProcedimento();
				}
				relacionarPHISSMController.criarNotificacoesUsuarios(getPhi(),convGrupoItemProcedList);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				return false;
			} catch (Exception e) {
				LOG.error("Exceção caputada:", e);
				
				this.apresentarMsgNegocio(Severity.ERROR, RelacionarPHISSMControllerExceptionCode.ERRO_RELACIONAMENTO_PHI_SSM.toString());
				return false;
			}
			return true;
		}
		
		private List<FatConvGrupoItemProced> clonarConvGrupoItemProcedList() {
			List<FatConvGrupoItemProced> convGrupoItemProcedListClone = new ArrayList<FatConvGrupoItemProced>();
			for (FatConvGrupoItemProced fatConvGrupoItemProced : convGrupoItemProcedList) {
				if (!convGrupoItemProcedListOriginal.contains(fatConvGrupoItemProced)) {
					FatConvGrupoItemProced item = new FatConvGrupoItemProced(fatConvGrupoItemProced);
					item.setItemProcedHospitalar(fatConvGrupoItemProced.getItemProcedHospitalar());
					item.setOperacao(DominioOperacoesJournal.INS);
					if (fatConvGrupoItemProced.getIndCobrancaFracionada() != null) {
						item.setIndCobrancaFracionada(fatConvGrupoItemProced.getIndCobrancaFracionada());
					} else {
						item.setIndCobrancaFracionada(false);
					}
					if (fatConvGrupoItemProced.getIndExigeAutorPrevia() != null) {
						item.setIndExigeAutorPrevia(fatConvGrupoItemProced.getIndExigeAutorPrevia());
					} else {
						item.setIndExigeAutorPrevia(false);
					}
					if (fatConvGrupoItemProced.getIndExigeJustificativa() != null) {
						item.setIndExigeJustificativa(fatConvGrupoItemProced.getIndExigeJustificativa());
					} else {
						item.setIndExigeJustificativa(false);
					}
					if (fatConvGrupoItemProced.getIndExigeNotaFiscal() != null) {
						item.setIndExigeNotaFiscal(fatConvGrupoItemProced.getIndExigeNotaFiscal());
					} else {
						item.setIndExigeNotaFiscal(false);
					}
					if (fatConvGrupoItemProced.getIndImprimeLaudo() != null) {
						item.setIndImprimeLaudo(fatConvGrupoItemProced.getIndImprimeLaudo());
					} else {
						item.setIndImprimeLaudo(false);
					}
					if (fatConvGrupoItemProced.getIndInformaTempoTrat() != null) {
						item.setIndInformaTempoTrat(fatConvGrupoItemProced.getIndInformaTempoTrat());
					} else {
						item.setIndInformaTempoTrat(false);
					}
					if (fatConvGrupoItemProced.getIndPaga() != null) {
						item.setIndPaga(fatConvGrupoItemProced.getIndPaga());
					} else {
						item.setIndPaga(false);
					}
					if (fatConvGrupoItemProced.getTempoValidade() != null) {
						item.setTempoValidade(fatConvGrupoItemProced.getTempoValidade());
					} else {
						item.setTempoValidade(Short.valueOf("0"));
					}
					item.setConvenioSaudePlano(fatConvGrupoItemProced.getConvenioSaudePlano());
					convGrupoItemProcedListClone.add(item);
				}
			}
			return convGrupoItemProcedListClone;
		}

		private void popularValoresPadroesConvGrupoItemProced() {
			relacionarPHISSMController.getConvGrupoItemProced().setIndCobrancaFracionada(false);
			relacionarPHISSMController.getConvGrupoItemProced().setIndExigeAutorPrevia(false);
			relacionarPHISSMController.getConvGrupoItemProced().setIndExigeJustificativa(false);
			relacionarPHISSMController.getConvGrupoItemProced().setIndExigeNotaFiscal(false);
			relacionarPHISSMController.getConvGrupoItemProced().setIndImprimeLaudo(false);
			relacionarPHISSMController.getConvGrupoItemProced().setIndInformaTempoTrat(false);
			relacionarPHISSMController.getConvGrupoItemProced().setIndPaga(false);
		}

		public void excluirProcedimentoRelacionado(FatConvGrupoItemProced item) {
			relacionarPHISSMController.setAlterou(true);
			convGrupoItemProcedListRemover.add(item);
			convGrupoItemProcedList.remove(item);
			alterouItensProcedimento = true;
		}

		public void editarProcedimentoRelacionado(FatConvGrupoItemProced item) {
			fatConvGrupoItemProcedSelecionado = item;
			relacionarPHISSMController.setConvGrupoItemProced(item);
			relacionarPHISSMController.setProcedHospInterno(item.getProcedimentoHospitalarInterno());
			relacionarPHISSMController.setItemProcedHospSus(item.getItemProcedHospitalar());
			plano = item.getConvenioSaudePlano();
			FatConvenioSaude fatConvenioSaude = item.getConvenioSaudePlano().getConvenioSaude();
			VFatConvPlanoGrupoProcedVO vo = new VFatConvPlanoGrupoProcedVO();
			vo.setCphCspCnvCodigo(fatConvenioSaude.getCodigo());
			vo.setCnvDescricao(fatConvenioSaude.getDescricao());
			convenio = vo;
			relacionarPHISSMController.setEdicao(true);
		}

		public void cancelarEdicao() {
			relacionarPHISSMController.cancelarEdicao();		
		}

		public List<VFatConvPlanoGrupoProcedVO> listarConvenios(Object objPesquisa) {
			return this.faturamentoFacade.listarConvenios(objPesquisa, relacionarPHISSMController.getCpgGrcSeq(), relacionarPHISSMController.getTabela().getCphPhoSeq());
		}

		public Long listarConveniosCount(Object objPesquisa) {
			return this.faturamentoFacade.listarConveniosCount(objPesquisa, relacionarPHISSMController.getCpgGrcSeq(), relacionarPHISSMController.getTabela().getCphPhoSeq());
		}

		public List<FatItensProcedHospitalar> listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeq(String objPesquisa){
			return relacionarPHISSMController.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeq(objPesquisa);
		}
		
		public Long listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(String objPesquisa){
			return relacionarPHISSMController.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa);
		}
		
		public Boolean getEdicao() {
			return relacionarPHISSMController.getEdicao();
		}

		public void setEdicao(Boolean edicao) {
			relacionarPHISSMController.setEdicao(edicao);
		}

		public FatItensProcedHospitalar getItemProcedHosp() {
			return relacionarPHISSMController.getItemProcedHosp();
		}

		public void setItemProcedHosp(FatItensProcedHospitalar itemProcedHosp) {
			relacionarPHISSMController.setItemProcedHosp(itemProcedHosp);
		}

		public FatItensProcedHospitalar getItemProcedHospSus() {
			return relacionarPHISSMController.getItemProcedHospSus();
		}

		public void setItemProcedHospSus(FatItensProcedHospitalar itemProcedHospSus) {
			relacionarPHISSMController.setItemProcedHospSus(itemProcedHospSus);
		}

		public VFatConvPlanoGrupoProcedVO getConvenio() {
			return convenio;
		}

		public void setConvenio(VFatConvPlanoGrupoProcedVO convenio) {
			this.convenio = convenio;
		}

		public Integer getPhi() {
			if (examesMaterialAnalise != null
					&& examesMaterialAnalise.getId() != null
					&& examesMaterialAnalise.getId().getExaSigla() != null
					&& examesMaterialAnalise.getId().getManSeq() != null) {
				FatProcedHospInternos fatProcedHospInternos = faturamentoFacade.obterFatProcedHospInternosPorMaterial(examesMaterialAnalise.getId());
				if (fatProcedHospInternos != null) {
					phi = fatProcedHospInternos.getSeq();
				}
			}
			return phi;
		}

		public void setPhi(Integer phi) {
			this.phi = phi;
		}

		public List<FatConvGrupoItemProced> getConvGrupoItemProcedList() {
			return convGrupoItemProcedList;
		}

		public void setConvGrupoItemProcedList(List<FatConvGrupoItemProced> convGrupoItemProcedList) {
			this.convGrupoItemProcedList = convGrupoItemProcedList;
		}
		
		public Boolean getExibirPainelInferior() {
			return exibirPainelInferior;
		}

		public void setExibirPainelInferior(Boolean exibirPainelInferior) {
			this.exibirPainelInferior = exibirPainelInferior;
		}

		public Boolean getAlterouItensProcedimento() {
			return alterouItensProcedimento;
		}

		public void setAlterouItensProcedimento(Boolean alterouItensProcedimento) {
			this.alterouItensProcedimento = alterouItensProcedimento;
		}

		public List<FatConvGrupoItemProced> getConvGrupoItemProcedListOriginal() {
			return convGrupoItemProcedListOriginal;
		}

		public void setConvGrupoItemProcedListOriginal(
				List<FatConvGrupoItemProced> convGrupoItemProcedListOriginal) {
			this.convGrupoItemProcedListOriginal = convGrupoItemProcedListOriginal;
		}

		public List<FatConvGrupoItemProced> getConvGrupoItemProcedListRemover() {
			return convGrupoItemProcedListRemover;
		}

		public void setConvGrupoItemProcedListRemover(
				List<FatConvGrupoItemProced> convGrupoItemProcedListRemover) {
			this.convGrupoItemProcedListRemover = convGrupoItemProcedListRemover;
		}

		public FatConvenioSaudePlano getPlano() {
			return plano;
		}

		public void setPlano(FatConvenioSaudePlano plano) {
			this.plano = plano;
		}

		public FatConvGrupoItemProced getFatConvGrupoItemProcedSelecionado() {
			return fatConvGrupoItemProcedSelecionado;
		}

		public void setFatConvGrupoItemProcedSelecionado(
				FatConvGrupoItemProced fatConvGrupoItemProcedSelecionado) {
			this.fatConvGrupoItemProcedSelecionado = fatConvGrupoItemProcedSelecionado;
		}

		public boolean isObrigatoriedadeSigtap() {
			return obrigatoriedadeSigtap;
		}

		public void setObrigatoriedadeSigtap(boolean obrigatoriedadeSigtap) {
			this.obrigatoriedadeSigtap = obrigatoriedadeSigtap;
		}

        public Integer getOpenToggle() {
        return openToggle;
    }

        public void setOpenToggle(Integer openToggle) {
        this.openToggle = openToggle;
    }

        public Integer getOpenToggle2() {
        return openToggle2;
    }

        public void setOpenToggle2(Integer openToggle2) {
        this.openToggle2 = openToggle2;
    }
}