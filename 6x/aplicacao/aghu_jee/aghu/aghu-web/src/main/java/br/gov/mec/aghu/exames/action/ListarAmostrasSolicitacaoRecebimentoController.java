package br.gov.mec.aghu.exames.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.CupsException.CupsIndisponivelExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioApAnterior;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.exames.agendamento.action.RelatorioFichaTrabalhoPatologiaController;
import br.gov.mec.aghu.exames.business.IExamesBeanFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAndamentoVO;
import br.gov.mec.aghu.exames.vo.AelAmostrasVO;
import br.gov.mec.aghu.exames.vo.ImprimeEtiquetaVO;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.impressao.expectioncode.SistemaImpressaoExceptionCode;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength" })
public class ListarAmostrasSolicitacaoRecebimentoController extends ActionController {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada:";

	private static final String CANCELAR_EXAMES_AREA_EXECUTORA = "cancelarExamesAreaExecutora";

	private static final String EMISSAO_MAPA_TRABALHO = "emissaoMapaTrabalho";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	private static final Log LOG = LogFactory.getLog(ListarAmostrasSolicitacaoRecebimentoController.class);

	private static final long serialVersionUID = 2429190503692487316L;

	private static final String INFORMAR_RESPONSAVEL_AP = "informarResponsavelAP";

	enum ListarAmostrasSolicitacaoRecebimentoExceptionCode implements BusinessExceptionCode {
		MSG_ERRO_IMPRIMIR_NRO_AP;
	}

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IExamesBeanFacade examesBeanFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@EJB
	private IExamesLaudosFacade examesLaudoFacade;

	@Inject
	private RelatorioFichaTrabalhoPatologiaController relatorioFichaTrabalhoPatologiaController;

	@Inject
	private RelatorioFichaTrabalhoPorExameController relatorioFichaTrabalhoPorExameController;

	@Inject
	private RelatorioFichaTrabalhoAmostraController relatorioFichaTrabalhoAmostraController;

	@Inject
	private ReimprimirEtiquetasController reimprimirEtiquetasController;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	// Campos de filtro para pesquisa
	private AghUnidadesFuncionais unidadeExecutora;
	private boolean verificaUnidadePatologica;
	private AelUnidExecUsuario usuarioUnidadeExecutora;
	private AelConfigExLaudoUnico configExameOrigem;

	/*
	 * Campos de filtro para pesquisa que delegam o tipo de leitura realizada. São dois tipo, por entrada do teclado ou leitor de codigo de barras. Obs. Atuam como "intermediarios"
	 * entre o numero da solicitacao e o valor da amostra, assim impedindo problemas quando o usuario alternar o tipo de leitura em uma mesma conversacao.
	 */
	private Long valorEntradaCampoSolicitacao;
	private Short valorEntradaCampoAmostra;

	// Campos READY ONLY exbidos no fieldset de solicitacao de exame e
	// utilizados pelas operacoes de recebimento de amostras
	private Integer solicitacaoNumero;
	private Short solicitacaoAmostra;
	private String solicitacaoConvenio;
	private String solicitacaoOrigem;
	private String solicitacaoPaciente;
	private Integer solicitacaoPacCodigo;
	private Integer solicitacaoProntuario;
	private String solicitacaoUnidade;
	private String solicitacaoQuarto;
	private String solicitacaoLeito;
	private String solicitacaoInformacoes;
	
	// lista de exames para indicação de origem (antigo AP de origem)
	private List<ExameAndamentoVO> listaExameOrigem;
	private Set<ExameAndamentoVO> listaExamesSelecionados;
	private ExameAndamentoVO exameOrigemSelecionado;
	
	// Campos que controlam a selecao atual na lista de amostras
	private Integer amostraSoeSeqSelecionada;
	private Short amostraSeqpSelecionada;

	/*
	 * Essa instancia armazena a descricao do material de analise da amostra logo reutilizada durante a listagem de exames amostra ou items de exame amostra. Vide coluna: Exames +
	 * "/" + Amostra no XHTML
	 */
	private String amostraMaterialAnalise;

	// Instancias para resultados da pesquisa
	List<AelAmostrasVO> listaAmostras;
	private AelAmostrasVO amostraRecebida;
	private AelAmostrasVO amostraSelecionada;
	List<AelAmostraItemExames> listaExamesAmostras;

	// Impressao
	private AelAmostras amostraImpressaoEtiquetas;
	private String mensagemConfirmacaoImpressaoEtiquetas;

	private static final String AGRUPAR_EXAMES = "agruparExames";
	private static final String VOLTAR_PARA = "voltar";
	
	@Inject
	private AgruparExamesController agruparExamesController;
	
	// #5875
	private Integer numeroApRespAp;
	private boolean laudoUnico;
	private DominioApAnterior dominioApAnterior;

	private boolean pesquisar;
	
	private boolean naoMostrarMensagemSucessoQndoExaRedomeForSolicitado;

	private AelAmostrasVO amostraParaReimpressao;

	/**
	 * Chamado no inicio de cada conversacao
	 */
	public void iniciar() {
		// Obtem o usuario da unidade executora
		try {
			this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
		} catch (ApplicationBusinessException e) {
			this.usuarioUnidadeExecutora = null;
		}

		// Resgata a unidade executora associada ao usuario
		if (this.usuarioUnidadeExecutora != null) {
			this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
		}

		if (this.valorEntradaCampoSolicitacao != null && this.isPesquisar()) {
			this.pesquisar();
			this.pesquisar = false;
		}
		
		this.naoMostrarMensagemSucessoQndoExaRedomeForSolicitado = false;
	
		listaExamesSelecionados = new LinkedHashSet<ExameAndamentoVO>();
	}
	
	public boolean desabilitarVoltarTodasAmostras() {
		return this.listaExamesAmostras == null || this.listaExamesAmostras.isEmpty();
	}

	public boolean desabilitarReceberTodasAmostras() {

		for (AelAmostrasVO amostras : listaAmostras) {
			if (!amostras.getSituacao().equals(DominioSituacaoAmostra.R)
					&& !amostras.getSituacao().equals(DominioSituacaoAmostra.E)
					&& !amostras.getSituacao().equals(DominioSituacaoAmostra.U)) {
				return false;
			}
		}

		return true;
	}
	
//	private List<Integer> getSeqpAmostrasRecebidas() {
//		return examesFacade.getSeqpAmostrasRecebidas(listaAmostras);
//	}
	
	
	/**
	 * Seleciona um exame na grid como selecionado
	 * 
	 * @param exameAndamento exame selecionado na grid
	 */
	public void selecionarExameOrigem(ExameAndamentoVO exameOrigem) {
		this.exameOrigemSelecionado = exameOrigem;
		examesFacade.selecionarExameAndamento(exameOrigem, listaExameOrigem, listaExamesSelecionados);		
	}
	
	/**
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		try {
			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.unidadeExecutora);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Pesquisa principal de solicitacao de exame e suas respectivas amostras
	 */
	public void pesquisar() {

		if (this.valorEntradaCampoSolicitacao != null && this.valorEntradaCampoSolicitacao > 0) {
			
			Short seqUnidadePatologia = null;
			try {
				seqUnidadePatologia = parametroFacade.buscarValorShort(AghuParametrosEnum.P_UNID_PATOL_CIRG);
			} catch (ApplicationBusinessException e1) {
				apresentarExcecaoNegocio(e1);
			}
			
			if (unidadeExecutora.getSeq().equals(seqUnidadePatologia)) {
				valorEntradaCampoAmostra = null;
			}

			this.limparDadosSomenteLeituraSolicitacaoExame();
			this.listaAmostras = null;
			this.listaExamesAmostras = null;

			// Armazena temporariamente a solicitacao e sequencial da amostra
			// selecionada na tabela da view
			final String valorLidoLeitorCodigoBarras = this.valorEntradaCampoSolicitacao.toString();
			final int solicitacaoSeqQuantidadeCaracteres = valorLidoLeitorCodigoBarras.length();

			// Verifica o tipo de entrada que o usuario utilizou
			if (solicitacaoSeqQuantidadeCaracteres > 8) {

				// Pesquisa e RECEBIMENTO AUTOMATICO atraves do LEITOR DE CODIGO
				// DE BARRAS
				this.pesquisaLeitorCodigoBarra(valorLidoLeitorCodigoBarras);

			} else { // Pesquisa atraves do campo de solicitacao ou TECLADO

				// Obtem o valor solicitacao
				this.solicitacaoNumero = this.valorEntradaCampoSolicitacao.intValue();

				// Obtem o valor da amostra
				this.solicitacaoAmostra = this.valorEntradaCampoAmostra != null ? this.valorEntradaCampoAmostra.shortValue() : null;

				// Pesquisa de amostras atraves da teclado
				this.pesquisarTeclado(this.solicitacaoNumero, this.solicitacaoAmostra);
			}

			// preenche a lista de exames de origem
			pesquisarExamesOrigem();
			
		} else {
			this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_SOLICITACAO_AMOSTRA_OBRIGATORIA");
		}
	}
	
	private void pesquisarExamesOrigem(){
		if (this.isHabilitarNumeroAp()) {
			try {
				listaExameOrigem = examesFacade.obterExamesAndamento(this.solicitacaoPacCodigo, unidadeExecutora.getSeq());
			} catch (BaseException e) {
				listaExameOrigem = null;
			}
		}		
	}

	/**
	 * Pesquisa e recebimento AUTOMATICO de amostras atraves do leitor de código de barras
	 * 
	 * @param valorLidoLeitorCodigoBarras
	 */
	private void pesquisaLeitorCodigoBarra(final String valorLidoLeitorCodigoBarras) {

		// Obtem o valor solicitacao
		// this.valorEntradaLeitorCodigoBarras = Long.parseLong();

		// Obtem o valor solicitacao
		this.solicitacaoNumero = Integer.parseInt(valorLidoLeitorCodigoBarras.substring(0, valorLidoLeitorCodigoBarras.length() - 3));

		// Obtem o o valor sequencial da amostra
		this.solicitacaoAmostra = Short.parseShort(valorLidoLeitorCodigoBarras.substring(valorLidoLeitorCodigoBarras.length() - 3, valorLidoLeitorCodigoBarras.length()));

		// Reutiliza e Realiza a pesquisa automatica das amostras de exame
		final boolean retornou = this.pesquisarTeclado(this.solicitacaoNumero, this.solicitacaoAmostra);

		try {

			// Realiza a pesquisa automatica dos itens de exames amostras
			if (retornou && this.listaAmostras != null && !this.listaAmostras.isEmpty()) {

				// Recebe AUTOMATICAMENTE a amostra
				this.receberAmostraCodigoBarra(this.listaAmostras.get(0));

				this.pesquisarTeclado(this.solicitacaoNumero, this.solicitacaoAmostra);

			} else {
				// Limpa valores de entrada do usuario
				this.valorEntradaCampoSolicitacao = null;
				this.valorEntradaCampoAmostra = null;
			}

		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
			// Limpa a lista de resultados caso ocorra uma excecao de negocio
			this.listaAmostras = null;
		} finally {
			// Limpa valores de entrada do usuar
			this.valorEntradaCampoSolicitacao = null;
			this.valorEntradaCampoAmostra = null;
		}
	}

	/**
	 * Pesquisa de amostras através da teclado
	 * 
	 * @param amoSoeSeq
	 * @param amoSeqp
	 * @return
	 */
	private boolean pesquisarTeclado(final Integer amoSoeSeq, final Short amoSeqp) {

		// adicionado para resolver bug:
		// #9931 quando ocorria erro de regra de negocio ao voltar uma Amostra,
		// e o usuário tentava pesquisar alguma outra solicitação.
		this.amostraSoeSeqSelecionada = amoSoeSeq;
		this.amostraSeqpSelecionada = amoSeqp;

		// Resgata a solicitacao de exame atraves da id
		final AelSolicitacaoExames solicitacaoExame = this.examesFacade.obterAelSolicitacaoExamesPeloId(amoSoeSeq);

		if (solicitacaoExame != null) {

			// Popula dados da solicitacao de exame que serao exibidos nos
			// campos READY ONLY da tela
			this.popularDadosSomenteLeituraSolicitacaoExame(solicitacaoExame);

			// Pesquisa amostras
			try {
				this.listaAmostras = this.examesFacade.buscarAmostrasVOPorSolicitacaoExame(solicitacaoExame, this.solicitacaoAmostra);
			} catch (final BaseException e) {
				apresentarExcecaoNegocio(e);
			}

			// Realiza a pesquisa automatica dos itens de exames amostras
			if (this.listaAmostras != null && !this.listaAmostras.isEmpty()) {

				// Obtem o primeiro item da lista...
				AelAmostrasVO itemSelecionadoListaAmostras = null;
				/*
				 * Caso nenhum item da lista de amostras esteja selecionado: A consulta de items de amostra de exame tera como criterio o primeiro item da lista
				 */
				if (this.amostraSeqpSelecionada == null) {
					itemSelecionadoListaAmostras = this.listaAmostras.get(0);
				} else {
					/*
					 * Caso algum item da lista de amostras esteja selecionado: A consulta de items de amostra de exame tera com criterio o item selecionado
					 */
					for (final AelAmostrasVO item : listaAmostras) {
						if (item.getSeqp().equals(this.amostraSeqpSelecionada)) {
							itemSelecionadoListaAmostras = item;
						}
					}
				}

				// Pesquisa de exames amostra (items de amostra de exame)
				this.pesquisarExamesAmostra(itemSelecionadoListaAmostras);
				
				// preenche a lista de exames de origem
				pesquisarExamesOrigem();
				
				return true;

			} else {
				this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_SOLICITACAO_EXAME_SEM_AMOSTRAS", amoSoeSeq.toString());
				return false;
			}

		} else {
			this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_NENHUMA_SOLICITACAO_EXAME_ENCONTRADA", amoSoeSeq.toString());
			return false;
		}

	}

	public void pesquisarExamesAmostra(){
		if (amostraSelecionada != null){
			pesquisarExamesAmostra(amostraSelecionada);
		}
	}
	/**
	 * Pesquisa de exames amostra (items de amostra de exame)
	 * 
	 * @param amostra
	 */
	public void pesquisarExamesAmostra(final AelAmostrasVO vo) {

		this.amostraSoeSeqSelecionada = vo.getSoeSeq();
		this.amostraSeqpSelecionada = vo.getSeqp();

		final AelAmostras amostra = this.examesFacade.buscarAmostrasPorId(vo.getSoeSeq(), vo.getSeqp());

		/*
		 * Resgata a descricao do material de analise da amostra. Este valor sera concatenado com a descricao do exame na listagem. Vide coluna: Exames + "/" + Amostra no XHTML
		 */
		if (amostra.getMateriaisAnalises() != null) {
			this.amostraMaterialAnalise = amostra.getMateriaisAnalises().getDescricao();
		}

		this.listaExamesAmostras = this.examesFacade.buscarAelAmostraItemExamesPorAmostra(amostra.getId().getSoeSeq(), amostra.getId()
				.getSeqp().intValue());

		// #35979
		try {
			this.examesFacade.verificaStatusExame(amostra);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		//this.isLaudoUnico();
		// this.listaExamesAmostras = amostra.getAelAmostraItemExameses();
	}

	private boolean isLaudoUnico(List<AelAmostrasVO> listaAmostras) {
		this.configExameOrigem = null;
		// this.numeroApOrigem = null;
		// this.exameOrigemSelecionado = null;
		this.laudoUnico = false;
		
		for (AelAmostrasVO vo : listaAmostras) {
		
			final AelAmostras amostra = this.examesFacade.buscarAmostrasPorId(
					vo.getSoeSeq(), vo.getSeqp());
			
			List<AelAmostraItemExames> lista = this.examesFacade
					.buscarAelAmostraItemExamesPorAmostra(amostra.getId()
							.getSoeSeq(), amostra.getId()
							.getSeqp().intValue());
			
			for (final AelAmostraItemExames aelAmostraItemExames : lista) {

				final AelUnfExecutaExames aelUnfExecutaExames = aelAmostraItemExames
						.getAelItemSolicitacaoExames().getAelUnfExecutaExames();
				
				AelConfigExLaudoUnico configExLaudoUnico = examesPatologiaFacade.obterConfigExLaudoUnico(aelAmostraItemExames.getAelItemSolicitacaoExames());
				
				if (configExLaudoUnico != null) {
					this.laudoUnico = true;
					this.dominioApAnterior = aelUnfExecutaExames
							.getIndNumApAnterior();
	
					return this.laudoUnico;
				}
			}
		}
		return this.laudoUnico;
	}

	public String chamarRespAp() {
		if (this.listaExamesAmostras != null && !this.listaExamesAmostras.isEmpty()) {
			for (final AelAmostraItemExames aelAmostraItemExames : this.listaExamesAmostras) {
				if (DominioSimNao.S.equals(aelAmostraItemExames.getAelItemSolicitacaoExames().getAelUnfExecutaExames().getIndLaudoUnico())) {
					final AelAnatomoPatologico aelAnatomoPatologico = examesPatologiaFacade.obterAelAnatomoPatologicoPorItemSolic(aelAmostraItemExames.getAelItemSolicitacaoExames().getId().getSoeSeq(), aelAmostraItemExames.getAelItemSolicitacaoExames().getId().getSeqp());
					
					if (aelAnatomoPatologico == null || aelAnatomoPatologico.getNumeroAp() == null) {
						this.apresentarMsgNegocio(Severity.WARN, "AEL_02731");
						return null;
					} else {
						return INFORMAR_RESPONSAVEL_AP;
					}
				}
			}
		}

		this.apresentarMsgNegocio(Severity.WARN, "AEL_02732");
		return null;
	}

	/**
	 * 
	 */
	private void popularDadosSomenteLeituraSolicitacaoExame(final AelSolicitacaoExames solicitacaoExame) {

		// Caso a solicitacao de exame exista realiza a consulta de itens de
		// solicitacao de exame
		if (solicitacaoExame != null) {

			this.solicitacaoNumero = solicitacaoExame.getSeq();

			// Resgata dados do atendimento da solicitacao de exame
			final AghAtendimentos atendimento = solicitacaoExame.getAtendimento();

			// Popula os atributos relacionados ao atendimento
			if (solicitacaoExame.getAtendimento() != null) {

				// Resgata o convenio
				FatConvenioSaudePlano convenioSaudePlano = this.pacienteFacade.obterConvenioSaudePlanoAtendimento(solicitacaoExame.getAtendimento().getSeq());

				if (convenioSaudePlano != null) {
					this.solicitacaoConvenio = convenioSaudePlano.getDescricao();
				}

				this.solicitacaoOrigem = atendimento.getOrigem().getDescricao();
				this.solicitacaoPaciente = atendimento.getPaciente().getNome();
				this.solicitacaoPacCodigo = atendimento.getPaciente().getCodigo();
				this.solicitacaoProntuario = atendimento.getPaciente().getProntuario();

				if (atendimento.getQuarto() != null) {
					this.solicitacaoQuarto = atendimento.getQuarto().getDescricao();
				}

				if (atendimento.getLeito() != null) {
					this.solicitacaoLeito = atendimento.getLeito().getLeitoID();
				}

			}
			else if (solicitacaoExame.getAtendimentoDiverso() != null) {
				final AelAtendimentoDiversos atendimentoDiverso = examesLaudoFacade.obterAtendimentoDiversoComPaciente(solicitacaoExame.getAtendimentoDiverso().getSeq());
				
				if (atendimentoDiverso.getAipPaciente() != null) {
					this.solicitacaoPaciente = atendimentoDiverso.getAipPaciente().getNome();
					this.solicitacaoPacCodigo = atendimentoDiverso.getAipPaciente().getCodigo();
					this.solicitacaoProntuario = atendimentoDiverso.getAipPaciente().getProntuario();
				}
				else {
					this.solicitacaoPaciente = atendimentoDiverso.getNomePaciente();
				}

			}

			// Resgata a unidade funcional da solicitacao de exame
			this.solicitacaoUnidade = solicitacaoExame.getUnidadeFuncional().getDescricao();

			// Resgata dados das informacoes clinicas da solicitacao de exame
			if (solicitacaoExame.getInformacoesClinicas() != null) {
				this.solicitacaoInformacoes = solicitacaoExame.getInformacoesClinicas();
			}
		}
	}

	/*
	 * Metodos para Suggestion Box
	 */

	/**
	 * Metodo para pesquisa na suggestion box de unidade executora
	 */
	public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(final String objPesquisa) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa);
	}

	/**
	 * Limpa os filtros da consulta
	 */
	public void limparPesquisa() {

		if (this.usuarioUnidadeExecutora != null) {
			// Reseta a unidade executora associada ao usuario
			this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
		}

		// Limpa campos de filtro para pesquisa
		this.valorEntradaCampoSolicitacao = null;
		this.valorEntradaCampoAmostra = null;
		this.amostraSoeSeqSelecionada = null;
		this.amostraSeqpSelecionada = null;
		this.configExameOrigem = null;
		//this.numeroApOrigem = null;
		this.exameOrigemSelecionado = null;
		this.verificaUnidadePatologica = false;

		// Limpa campos READY ONLY exbidos no fieldset de solicitacao de exame
		this.limparDadosSomenteLeituraSolicitacaoExame();

		// Limpa listas contendo resultados de pesquisa
		this.listaAmostras = null;
		this.listaExamesAmostras = null;

		this.laudoUnico = false;
		this.dominioApAnterior = null;
	}

	/**
	 * Limpa campos READY ONLY exbidos no fieldset de solicitacao de exame
	 */
	private void limparDadosSomenteLeituraSolicitacaoExame() {

		this.solicitacaoNumero = null;
		this.solicitacaoAmostra = null;
		this.solicitacaoConvenio = null;
		this.solicitacaoOrigem = null;
		this.solicitacaoPaciente = null;
		this.solicitacaoProntuario = null;
		this.solicitacaoPacCodigo = null;
		this.solicitacaoUnidade = null;
		this.solicitacaoQuarto = null;
		this.solicitacaoLeito = null;
		this.solicitacaoInformacoes = null;
		this.amostraMaterialAnalise = null;

	}

	public void salvarNroFrasco(AelAmostrasVO amostraVo) {

		if (amostraVo != null) {

			final AelAmostras amostra = this.examesFacade.buscarAmostrasPorId(amostraVo.getSoeSeq(), amostraVo.getSeqp());
			amostra.setNroFrascoFabricante(amostraVo.getNroFrascoFabricante());

			try {
				this.examesFacade.atualizarAmostra(amostra, true);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}

	}

	/**
	 * Recebe todas amostras
	 * 
	 * @return
	 */
	public String receberTodasAmostras() {

		// Armazena temporariamente a solicitacao e sequencial da amostra
		// selecionada na tabela da view
		int amostraSoeSeqSelecionadaTemporario = 0;
		short amostraSeqpSelecionadaTemporario = 0;
		try {
			boolean isRecebeuTodasAmostras = false;
			ImprimeEtiquetaVO etiquetaVO = null;

			if (this.isLaudoUnico(listaAmostras) && this.isVerificaUnidadePatologica()
					&& this.listaExamesAmostras != null
					&& !this.listaExamesAmostras.isEmpty()) {
					// encaminha para realizar agrupamento e informar
					// responsáveis
				return this.agruparExamesReceberTodasAmostras();
			} else { // receber amostras

				if (this.listaAmostras != null && !this.listaAmostras.isEmpty()) {
					/*
					 * Chamada antecipada da procedure AELP_TESTA_NRO_FRASCO_SOL
					 * A validação dos frascos é feita aqui devido ao
					 * preenchimento dos frascos na tela
					 */
					this.examesFacade.validarNumeroFrascoSolicitacao(
							this.unidadeExecutora, this.listaAmostras);

					AelAmostrasVO vo = this.listaAmostras.get(0);

					final AelAmostras amostra = this.examesFacade
							.buscarAmostrasPorId(vo.getSoeSeq(), vo.getSeqp());

					// Armazena temporariamente a solicitacao e sequencial da
					// amostra selecionada na tabela da view
					amostraSoeSeqSelecionadaTemporario = vo.getSoeSeq();
					amostraSeqpSelecionadaTemporario = vo.getSeqp();

					String nomeMicrocomputador = null;
					try {
						nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
					} catch (UnknownHostException e) {
						LOG.error(EXCECAO_CAPTURADA, e);
					}

					// Recebe a amostra através do botão receber todas amostas
//					if (this.exameOrigemSelecionado != null) {
//						etiquetaVO = this.examesBeanFacade
//								.receberAmostraSolicitacao(
//										this.unidadeExecutora, amostra,
//										listaExameOrigem,
//										nomeMicrocomputador);
//					} else {
//						etiquetaVO = this.examesBeanFacade
//								.receberAmostraSolicitacao(
//										this.unidadeExecutora, amostra,
//										listaExameOrigem,
//										nomeMicrocomputador);
//					}

					etiquetaVO = this.examesBeanFacade.receberAmostraSolicitacao(this.unidadeExecutora, amostra, listaExameOrigem, nomeMicrocomputador);
					
					
					// Imprime etiqueta
					if (etiquetaVO != null && etiquetaVO.getNroAp() != null
							&& Boolean.TRUE.equals(etiquetaVO.getImprimir())) {
						this.aelpImprimeEtiqAp(etiquetaVO);
					}

					// Ocorreu o recebimento de amostra
					isRecebeuTodasAmostras = true;

				}

				if (etiquetaVO != null) {
					// Imprime FICHA DE TRABALHO
					this.imprimirFichaTrabalho(etiquetaVO);
				}

				if (isRecebeuTodasAmostras) {

					this.pesquisarTeclado(amostraSoeSeqSelecionadaTemporario,
							amostraSeqpSelecionadaTemporario);

					this.apresentarMsgNegocio(
							Severity.WARN,
							"MENSAGEM_AMOSTRAS_RECEBIDAS_COM_SUCESSO",
							this.unidadeExecutora.getSeq());

					if (this.laudoUnico && this.verificaUnidadePatologica
							&& this.listaExamesAmostras != null
							&& !this.listaExamesAmostras.isEmpty()) {
						return VOLTAR_PARA;
					}

				}

			}
		} catch (BaseException e) {
			//this.pesquisar();
			if (e.getCode().equals(CupsIndisponivelExceptionCode.MENSAGEM_FALHA_ENVIAR_CUPS)){
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CUPS_INVALIDO");
			} else {
				apresentarExcecaoNegocio(e);
			}
			//this.pesquisar();
		} finally {

			// Preserva a solicitacao e sequencial da amostra selecionada na
			// tabela da view
			this.amostraSoeSeqSelecionada = amostraSoeSeqSelecionadaTemporario;
			this.amostraSeqpSelecionada = amostraSeqpSelecionadaTemporario;
		}
		return null;
		
		// TODO PENDENCIA MIGRACAO NOVA ARQUITETURA: CORRIGIDO NA REVISION: r157619
//		// Armazena temporariamente a solicitacao e sequencial da amostra
//		// selecionada na tabela da view
//		int amostraSoeSeqSelecionadaTemporario = 0;
//		short amostraSeqpSelecionadaTemporario = 0;
//		try {
//			boolean isRecebeuTodasAmostras = false;
//			ImprimeEtiquetaVO etiquetaVO = null;
//
//			if (this.listaAmostras != null && !this.listaAmostras.isEmpty()) {
//
//				/*
//				 * Chamada antecipada da procedure AELP_TESTA_NRO_FRASCO_SOL A validação dos frascos é feita aqui devido ao preenchimento dos frascos na tela
//				 */
//				this.examesFacade.validarNumeroFrascoSolicitacao(this.unidadeExecutora, this.listaAmostras);
//
//				AelAmostrasVO vo = this.listaAmostras.get(0);
//
//				final AelAmostras amostra = this.examesFacade.buscarAmostrasPorId(vo.getSoeSeq(), vo.getSeqp());
//
//				// Armazena temporariamente a solicitacao e sequencial da
//				// amostra selecionada na tabela da view
//				amostraSoeSeqSelecionadaTemporario = vo.getSoeSeq();
//				amostraSeqpSelecionadaTemporario = vo.getSeqp();
//
//				String nomeMicrocomputador = null;
//				try {
//					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
//				} catch (UnknownHostException e) {
//					LOG.error("Exceção caputada:", e);
//				}
//
//				// Recebe a amostra através do botão receber todas amostas
////				etiquetaVO = this.examesBeanFacade.receberAmostraSolicitacao(this.unidadeExecutora, amostra, configExameOrigem, this.numeroApOrigem, nomeMicrocomputador);
//
//				// Imprime etiqueta
//				if (etiquetaVO != null && etiquetaVO.getNroAp() != null && Boolean.TRUE.equals(etiquetaVO.getImprimir())) {
//					this.aelpImprimeEtiqAp(etiquetaVO.getNroAp());
//				}
//
//				// Ocorreu o recebimento de amostra
//				isRecebeuTodasAmostras = true;
//
//			}
//
//			if (etiquetaVO != null) {
//				// Imprime FICHA DE TRABALHO
//				if (etiquetaVO.getSoeSeq() != null && etiquetaVO.getUnfSeq() != null && Boolean.TRUE.equals(etiquetaVO.getImprimir())) {
//					if (Boolean.TRUE.equals(etiquetaVO.getImprimirFichaTrabPat())) {
//						aelpImprimeFichaTrabalhoPat(etiquetaVO.getSoeSeq(), etiquetaVO.getUnfSeq());
//					} else if (Boolean.TRUE.equals(etiquetaVO.getImprimirFichaTrabLab())) {
//						aelpImprimeFichaTrabalhoLab(etiquetaVO.getSoeSeq(), etiquetaVO.getUnfSeq(), etiquetaVO.getAmoSeqP(), etiquetaVO.getReceberAmostra());
//					} else if (Boolean.TRUE.equals(etiquetaVO.getImprimirFichaTrabAmo())) {
//						aelpImprimeFichaTrabalhoAmo(etiquetaVO.getSoeSeq(), etiquetaVO.getUnfSeq(), etiquetaVO.getAmoSeqP());
//					}
//				}
//				if (etiquetaVO.getQtdEtiquetas() != null) { // Imprime Etiquetas
//					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_IMPRESSAO_ETIQUETA_AMOSTRAS", etiquetaVO.getQtdEtiquetas());
//					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NRO_UNICO");
//				}
//			}
//
//			if (isRecebeuTodasAmostras) {
//
//				this.pesquisarTeclado(amostraSoeSeqSelecionadaTemporario, amostraSeqpSelecionadaTemporario);
//
//				this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_AMOSTRAS_RECEBIDAS_COM_SUCESSO", this.unidadeExecutora.getSeq());
//
//				// Chamada a Resp AP
//				if (this.laudoUnico && this.verificaUnidadePatologica && this.listaExamesAmostras != null && !this.listaExamesAmostras.isEmpty()) {
//					return this.chamarRespAp();
//				}
//
//			}
//
//		} catch (BaseException e) {
//			// this.pesquisar();
//			apresentarExcecaoNegocio(e);
//			// this.pesquisar();
//		} finally {
//
//			// Preserva a solicitacao e sequencial da amostra selecionada na
//			// tabela da view
//			this.amostraSoeSeqSelecionada = amostraSoeSeqSelecionadaTemporario;
//			this.amostraSeqpSelecionada = amostraSeqpSelecionadaTemporario;
//		}

	}

	protected void imprimirFichaTrabalho(final ImprimeEtiquetaVO etiquetaVO) throws BaseException {
		if (etiquetaVO.getSoeSeq() != null && etiquetaVO.getUnfSeq() != null && Boolean.TRUE.equals(etiquetaVO.getImprimir())) {
			if (Boolean.TRUE.equals(etiquetaVO.getImprimirFichaTrabPat())) {
				aelpImprimeFichaTrabalhoPat(etiquetaVO.getSoeSeq(), etiquetaVO.getUnfSeq());
			} else if (Boolean.TRUE.equals(etiquetaVO.getImprimirFichaTrabLab())) {
				aelpImprimeFichaTrabalhoLab(etiquetaVO.getSoeSeq(), etiquetaVO.getUnfSeq(), etiquetaVO.getAmoSeqP(), etiquetaVO.getReceberAmostra());
			} else if (Boolean.TRUE.equals(etiquetaVO.getImprimirFichaTrabAmo())) {
				aelpImprimeFichaTrabalhoAmo(etiquetaVO.getSoeSeq(), etiquetaVO.getUnfSeq(), etiquetaVO.getAmoSeqP());
			}
		}
		if (etiquetaVO.getQtdEtiquetas() != null) { // Imprime Etiquetas
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_IMPRESSAO_ETIQUETA_AMOSTRAS", etiquetaVO.getQtdEtiquetas());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NRO_UNICO");
		}
	}
	
	/**
	 * Recebe amostra individualmente
	 * 
	 * @param vo
	 * @return
	 */
	public String receberAmostra(final AelAmostrasVO vo) {
		final AelAmostras amostra = this.examesFacade.buscarAmostrasPorId(vo.getSoeSeq(), vo.getSeqp());

		final String nroFrascoFabricanteTemporario = vo.getNroFrascoFabricante();

		// Armazena temporariamente a solicitacao e sequencial da amostra
		// selecionada na tabela da view
		final int amostraSoeSeqSelecionadaTemporario = vo.getSoeSeq();
		final short amostraSeqpSelecionadaTemporario = vo.getSeqp();
		
		List<AelAmostrasVO> lista = new ArrayList<AelAmostrasVO>();
		lista.add(vo);
		
		try {
			if (this.isLaudoUnico(lista) && this.isVerificaUnidadePatologica()
					&& this.listaExamesAmostras != null
					&& !this.listaExamesAmostras.isEmpty()) {

				// encaminha para realizar agrupamento e informar
				// responsáveis
				return this.agruparExameReceberAmostra(vo);
			} 
			else { // receber amostras
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error(EXCECAO_CAPTURADA, e);
				}

				// Recebe a amostra através da lista
				ImprimeEtiquetaVO etiquetaVO = null;

				// if (this.exameOrigemSelecionado != null) {
				// etiquetaVO =
				// this.examesBeanFacade.receberAmostra(this.unidadeExecutora,
				// amostra, vo.getNroFrascoFabricante(),
				// configExameOrigem,
				// this.exameOrigemSelecionado.getNumeroExame(),
				// nomeMicrocomputador, servidorLogado);
				// } else {
				// etiquetaVO =
				// this.examesBeanFacade.receberAmostra(this.unidadeExecutora,
				// amostra, vo.getNroFrascoFabricante(),
				// configExameOrigem, null, nomeMicrocomputador,
				// servidorLogado);
				// }

				etiquetaVO = this.examesBeanFacade.receberAmostra(
						this.unidadeExecutora, amostra,
						vo.getNroFrascoFabricante(),
						listaExameOrigem, nomeMicrocomputador);

				this.imprimirFichasTrabalho(etiquetaVO);

				this.pesquisarTeclado(amostraSoeSeqSelecionadaTemporario,
						amostraSeqpSelecionadaTemporario);
				
				this.apresentarMsgNegocio(Severity.INFO,
						"AEL_01085", amostra.getId().getSeqp());

				// chamada a Resp AP
				if (this.laudoUnico && this.verificaUnidadePatologica
						&& this.listaExamesAmostras != null
						&& !this.listaExamesAmostras.isEmpty()) {
					return VOLTAR_PARA;
				}
			}
		} catch (BaseException e) {

			vo.setNroFrascoFabricante(nroFrascoFabricanteTemporario);
			//this.pesquisar();
			if (e.getCode().equals(SistemaImpressaoExceptionCode.ERRO_IMPRESSAO)){
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CUPS_INVALIDO");
			} else {
				apresentarExcecaoNegocio(e);
			}
			//this.pesquisar();

		} finally {

			// Preserva a solicitacao e sequencial da amostra selecionada na
			// tabela da view
			this.amostraSoeSeqSelecionada = amostraSoeSeqSelecionadaTemporario;
			this.amostraSeqpSelecionada = amostraSeqpSelecionadaTemporario;
		}
		return null;
		// TODO PENDENCIA MIGRACAO NOVA ARQUITETURA: CORRIGIDO NA REVISION: r157619
		
//		final AelAmostras amostra = this.examesFacade.buscarAmostrasPorId(vo.getSoeSeq(), vo.getSeqp());
//
//		final String nroFrascoFabricanteTemporario = vo.getNroFrascoFabricante();
//
//		// Armazena temporariamente a solicitacao e sequencial da amostra
//		// selecionada na tabela da view
//		final int amostraSoeSeqSelecionadaTemporario = vo.getSoeSeq();
//		final short amostraSeqpSelecionadaTemporario = vo.getSeqp();
//
//		String nomeMicrocomputador = null;
//		try {
//			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
//		} catch (UnknownHostException e) {
//			LOG.error("Exceção caputada:", e);
//		}
//
//		try {
//
//			// Recebe a amostra através da lista
//			final ImprimeEtiquetaVO etiquetaVO = null;
////			final ImprimeEtiquetaVO etiquetaVO = this.examesBeanFacade.receberAmostra(this.unidadeExecutora, amostra, vo.getNroFrascoFabricante(), configExameOrigem, this.numeroApOrigem,
////					nomeMicrocomputador);
//
//			if (etiquetaVO != null) {
//
//				// Imprime etiqueta
//				if (etiquetaVO != null && etiquetaVO.getNroAp() != null && Boolean.TRUE.equals(etiquetaVO.getImprimir())) {
//					aelpImprimeEtiqAp(etiquetaVO.getNroAp());
//				}
//
//				// Imprime FICHA DE TRABALHO
//				if (etiquetaVO.getSoeSeq() != null && etiquetaVO.getUnfSeq() != null && Boolean.TRUE.equals(etiquetaVO.getImprimir())) {
//					if (Boolean.TRUE.equals(etiquetaVO.getImprimirFichaTrabPat())) {
//						aelpImprimeFichaTrabalhoPat(etiquetaVO.getSoeSeq(), etiquetaVO.getUnfSeq());
//					} else if (Boolean.TRUE.equals(etiquetaVO.getImprimirFichaTrabLab())) {
//						aelpImprimeFichaTrabalhoLab(etiquetaVO.getSoeSeq(), etiquetaVO.getUnfSeq(), etiquetaVO.getAmoSeqP(), etiquetaVO.getReceberAmostra());
//					} else if (Boolean.TRUE.equals(etiquetaVO.getImprimirFichaTrabAmo())) {
//						aelpImprimeFichaTrabalhoAmo(etiquetaVO.getSoeSeq(), etiquetaVO.getUnfSeq(), etiquetaVO.getAmoSeqP());
//					}
//				}
//				if (etiquetaVO.getQtdEtiquetas() != null) { // Imprime Etiquetas
//					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_IMPRESSAO_ETIQUETA_AMOSTRA_UNICA");
//					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NRO_UNICO");
//				}
//			}
//
//			this.pesquisarTeclado(amostraSoeSeqSelecionadaTemporario, amostraSeqpSelecionadaTemporario);
//
//			// chamada a Resp AP
//			if (this.laudoUnico && this.verificaUnidadePatologica && this.listaExamesAmostras != null && !this.listaExamesAmostras.isEmpty()) {
//				return this.chamarRespAp();
//			}
//
//			this.apresentarMsgNegocio(Severity.INFO, "AEL_01085", amostra.getId().getSeqp());
//
//		} catch (BaseException e) {
//
//			vo.setNroFrascoFabricante(nroFrascoFabricanteTemporario);
//			// this.pesquisar();
//			apresentarExcecaoNegocio(e);
//			// this.pesquisar();
//
//		} finally {
//
//			// Preserva a solicitacao e sequencial da amostra selecionada na
//			// tabela da view
//			this.amostraSoeSeqSelecionada = amostraSoeSeqSelecionadaTemporario;
//			this.amostraSeqpSelecionada = amostraSeqpSelecionadaTemporario;
//		}
	}
	
	private void imprimirFichasTrabalho(ImprimeEtiquetaVO etiquetaVO)
			throws BaseException {
		if (etiquetaVO != null) {

			// Imprime etiqueta
			if (etiquetaVO != null && etiquetaVO.getNroAp() != null
					&& Boolean.TRUE.equals(etiquetaVO.getImprimir())) {
				aelpImprimeEtiqAp(etiquetaVO);
			}

			if (etiquetaVO.getSoeSeq() != null
					&& etiquetaVO.getUnfSeq() != null
					&& Boolean.TRUE.equals(etiquetaVO.getImprimir())) {
				if (Boolean.TRUE.equals(etiquetaVO.getImprimirFichaTrabPat())) {
					this.aelpImprimeFichaTrabalhoPat(etiquetaVO.getSoeSeq(),
							etiquetaVO.getUnfSeq());
				} else if (Boolean.TRUE.equals(etiquetaVO
						.getImprimirFichaTrabLab())) {
					this.aelpImprimeFichaTrabalhoLab(etiquetaVO.getSoeSeq(),
							etiquetaVO.getUnfSeq(), etiquetaVO.getAmoSeqP(),
							etiquetaVO.getReceberAmostra());
				} else if (Boolean.TRUE.equals(etiquetaVO
						.getImprimirFichaTrabAmo())) {
					this.aelpImprimeFichaTrabalhoAmo(etiquetaVO.getSoeSeq(),
							etiquetaVO.getUnfSeq(), etiquetaVO.getAmoSeqP());
				}
			}
			if (etiquetaVO.getQtdEtiquetas() != null) { // Imprime Etiquetas
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_IMPRESSAO_ETIQUETA_AMOSTRA_UNICA");
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_NRO_UNICO");
			}
			// Imprime FICHA DE TRABALHO

		}
	}	

	/**
	 * Receber amostra com o tratamento adequado para o leitor de codigo de barras
	 * 
	 * @param amostra
	 */
	public void receberAmostraCodigoBarra(final AelAmostrasVO vo) throws BaseException {

		final AelAmostras amostra = this.examesFacade.buscarAmostrasPorId(vo.getSoeSeq(), vo.getSeqp());

		// Armazena temporariamente a solicitacao e sequencial da amostra
		// selecionada na tabela da view
		final int amostraSoeSeqSelecionadaTemporario = vo.getSoeSeq();
		final short amostraSeqpSelecionadaTemporario = vo.getSeqp();

		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
		}

		try {
			
			// Recebe a amostra
			ImprimeEtiquetaVO etiquetaVO = null;
			
			
			etiquetaVO = this.examesBeanFacade.receberAmostra(
					this.unidadeExecutora, amostra,
					vo.getNroFrascoFabricante(), listaExameOrigem,
					nomeMicrocomputador);
			
//			if (this.exameOrigemSelecionado != null) {
//				etiquetaVO = this.examesBeanFacade
//						.receberAmostra(this.unidadeExecutora, amostra,
//								vo.getNroFrascoFabricante(), configExameOrigem,
//								this.exameOrigemSelecionado.getNumeroExame(), nomeMicrocomputador,
//								servidorLogado);
//			} else {
//				etiquetaVO = this.examesBeanFacade
//						.receberAmostra(this.unidadeExecutora, amostra,
//								vo.getNroFrascoFabricante(), configExameOrigem,
//								null, nomeMicrocomputador,
//								servidorLogado);
//			}
			

			if (etiquetaVO != null){

				//Imprime etiqueta
				if(etiquetaVO != null && etiquetaVO.getNroAp() != null && Boolean.TRUE.equals(etiquetaVO.getImprimir())){
					aelpImprimeEtiqAp(etiquetaVO);
				}

				//Imprime FICHA DE TRABALHO
				if (etiquetaVO.getSoeSeq() != null && etiquetaVO.getUnfSeq() != null && Boolean.TRUE.equals(etiquetaVO.getImprimir())) {
					if(Boolean.TRUE.equals(etiquetaVO.getImprimirFichaTrabPat())) {
						aelpImprimeFichaTrabalhoPat(etiquetaVO.getSoeSeq(), etiquetaVO.getUnfSeq());
					}
					else if(Boolean.TRUE.equals(etiquetaVO.getImprimirFichaTrabLab())) {
						aelpImprimeFichaTrabalhoLab(etiquetaVO.getSoeSeq(), etiquetaVO.getUnfSeq(), etiquetaVO.getAmoSeqP(), etiquetaVO.getReceberAmostra());
					}
					else if(Boolean.TRUE.equals(etiquetaVO.getImprimirFichaTrabAmo())) {
						aelpImprimeFichaTrabalhoAmo(etiquetaVO.getSoeSeq(), etiquetaVO.getUnfSeq(), etiquetaVO.getAmoSeqP());
					}
				}

			} 

			this.apresentarMsgNegocio(Severity.INFO, "AEL_01085", amostra.getId().getSeqp());

		} finally {

			// Preserva a solicitacao e sequencial da amostra selecionada na
			// tabela da view
			this.amostraSoeSeqSelecionada = amostraSoeSeqSelecionadaTemporario;
			this.amostraSeqpSelecionada = amostraSeqpSelecionadaTemporario;
		}

		
		// TODO PENDENCIA MIGRACAO NOVA ARQUITETURA: CORRIGIDO NA REVISION: r157619
		
//		final AelAmostras amostra = this.examesFacade.buscarAmostrasPorId(vo.getSoeSeq(), vo.getSeqp());
//
//		// Armazena temporariamente a solicitacao e sequencial da amostra
//		// selecionada na tabela da view
//		final int amostraSoeSeqSelecionadaTemporario = vo.getSoeSeq();
//		final short amostraSeqpSelecionadaTemporario = vo.getSeqp();
//
//		String nomeMicrocomputador = null;
//		try {
//			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
//		} catch (UnknownHostException e) {
//			LOG.error("Exceção caputada:", e);
//		}
//
//		try {
//
//			// Recebe a amostra
//			ImprimeEtiquetaVO etiquetaVO = null;
////			final ImprimeEtiquetaVO etiquetaVO = this.examesBeanFacade.receberAmostra(this.unidadeExecutora, amostra, vo.getNroFrascoFabricante(), configExameOrigem, this.numeroApOrigem,
////					nomeMicrocomputador);
//
//			if (etiquetaVO != null) {
//
//				// Imprime etiqueta
//				if (etiquetaVO != null && etiquetaVO.getNroAp() != null && Boolean.TRUE.equals(etiquetaVO.getImprimir())) {
//					aelpImprimeEtiqAp(etiquetaVO.getNroAp());
//				}
//
//				// Imprime FICHA DE TRABALHO
//				if (etiquetaVO.getSoeSeq() != null && etiquetaVO.getUnfSeq() != null && Boolean.TRUE.equals(etiquetaVO.getImprimir())) {
//					if (Boolean.TRUE.equals(etiquetaVO.getImprimirFichaTrabPat())) {
//						aelpImprimeFichaTrabalhoPat(etiquetaVO.getSoeSeq(), etiquetaVO.getUnfSeq());
//					} else if (Boolean.TRUE.equals(etiquetaVO.getImprimirFichaTrabLab())) {
//						aelpImprimeFichaTrabalhoLab(etiquetaVO.getSoeSeq(), etiquetaVO.getUnfSeq(), etiquetaVO.getAmoSeqP(), etiquetaVO.getReceberAmostra());
//					} else if (Boolean.TRUE.equals(etiquetaVO.getImprimirFichaTrabAmo())) {
//						aelpImprimeFichaTrabalhoAmo(etiquetaVO.getSoeSeq(), etiquetaVO.getUnfSeq(), etiquetaVO.getAmoSeqP());
//					}
//				}
//
//			}
//
//			this.apresentarMsgNegocio(Severity.INFO, "AEL_01085", amostra.getId().getSeqp());
//
//		} finally {
//
//			// Preserva a solicitacao e sequencial da amostra selecionada na
//			// tabela da view
//			this.amostraSoeSeqSelecionada = amostraSoeSeqSelecionadaTemporario;
//			this.amostraSeqpSelecionada = amostraSeqpSelecionadaTemporario;
//		}

	}

	/**
	 * Volta todas as amostras recebidas
	 * 
	 * @return
	 */
	public String voltarTodasAmostras() {

		// Armazena temporariamente a solicitacao e sequencial da amostraselecionada na tabela da view
		int amostraSoeSeqSelecionadaTemporario = 0;
		short amostraSeqpSelecionadaTemporario = 0;

		try {

			boolean isVoltouTodasAmostras = false;

			if (this.listaAmostras != null && !this.listaAmostras.isEmpty()) {

				AelAmostrasVO vo = this.listaAmostras.get(0);

				final AelAmostras amostra = this.examesFacade.buscarAmostrasPorId(vo.getSoeSeq(), vo.getSeqp());

				// Armazena temporariamente a solicitacao e sequencial da amostra selecionada na tabela da view
				amostraSoeSeqSelecionadaTemporario = vo.getSoeSeq();
				amostraSeqpSelecionadaTemporario = vo.getSeqp();

				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error(EXCECAO_CAPTURADA, e);
				}

				// Recebe a amostra através do botão receber todas amostas
				isVoltouTodasAmostras = this.examesBeanFacade.voltarSituacaoAmostraSolicitacao(this.unidadeExecutora, amostra, nomeMicrocomputador);

			}

			if (isVoltouTodasAmostras) {

				this.pesquisarTeclado(amostraSoeSeqSelecionadaTemporario, amostraSeqpSelecionadaTemporario);

				this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_AMOSTRAS_VOLTARAM_COM_SUCESSO", this.unidadeExecutora.getSeq());

				// Chamar responsável AP
				if (this.laudoUnico && this.verificaUnidadePatologica && this.listaExamesAmostras != null && !this.listaExamesAmostras.isEmpty()) {
					return this.chamarRespAp();
				}

			} else {
				// Nenhuma amostra está apta para o recebimento
				this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_NENHUMA_AMOSTRA_ALTERADA", this.unidadeExecutora.getSeq());
			}

		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		} finally {

			// Preserva a solicitacao e sequencial da amostra selecionada na
			// tabela da view
			this.amostraSoeSeqSelecionada = amostraSoeSeqSelecionadaTemporario;
			this.amostraSeqpSelecionada = amostraSeqpSelecionadaTemporario;
		}
		return null;
	}

	/**
	 * Volta individualmente amostra recebida
	 * 
	 * @param vo
	 */
	public void voltarAmostra(final AelAmostrasVO vo) {

		final AelAmostras amostra = this.examesFacade.buscarAmostrasPorId(vo.getSoeSeq(), vo.getSeqp());

		// Armazena temporariamente a solicitacao e sequencial da amostra
		// selecionada na tabela da view
		final int amostraSoeSeqSelecionadaTemporario = vo.getSoeSeq();
		final short amostraSeqpSelecionadaTemporario = vo.getSeqp();

		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
		}

		try {
			boolean isVoltouAmostra = this.examesBeanFacade.voltarAmostra(this.unidadeExecutora, amostra, nomeMicrocomputador);

			if (isVoltouAmostra) {
				this.apresentarMsgNegocio(Severity.WARN, "AEL_01088", amostra.getId().getSeqp());
				this.pesquisarTeclado(amostraSoeSeqSelecionadaTemporario, amostraSeqpSelecionadaTemporario);
			}

		} catch (final BaseException e) {

			apresentarExcecaoNegocio(e);

		} finally {

			// Preserva a solicitacao e sequencial da amostra selecionada na
			// tabela da view
			this.amostraSoeSeqSelecionada = amostraSoeSeqSelecionadaTemporario;
			this.amostraSeqpSelecionada = amostraSeqpSelecionadaTemporario;
		}

	}

	/**
	 * Calcula o numero de etiquetas impressas para uma amostra e constroi a mensagem quem sera exibida na modal para confirmacao de impressao
	 * 
	 * @param amostra
	 */
	public void calcularNumeroImpressoes(final AelAmostrasVO vo) {

		// guarda o valor da amostra para reimpressão
		this.amostraParaReimpressao = vo;

		// Limpa a instancia ATUAL da amostra relacionada a impressao de
		// etiquetas
		this.amostraImpressaoEtiquetas = null;
		// Limpa mensagem de confirmacao para impressao de etiquetas de uma
		// amostra
		this.mensagemConfirmacaoImpressaoEtiquetas = "";

		// Obtem a instancia ATUAL da amostra relacionada a impressao de
		// etiquetas
		this.amostraImpressaoEtiquetas = this.examesFacade.buscarAmostrasPorId(vo.getSoeSeq(), vo.getSeqp());

		try {

			// Obtem a mensagem de confirmacao da impressao de etiquetas de uma
			// amostra
			this.mensagemConfirmacaoImpressaoEtiquetas = this.examesFacade.comporMensagemConfirmacaoImpressaoEtiquetas(amostraImpressaoEtiquetas);

		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Imprime a etiqueta de uma amostra
	 * 
	 * @param amostra
	 */
//	public void imprimirEtiquetaAmostra() {
//		try {
//			String nomeMicrocomputador = getEnderecoRedeHostRemoto();
//			Integer qtdAmostras = this.examesFacade.imprimirEtiquetaAmostra(this.amostraImpressaoEtiquetas, this.unidadeExecutora, nomeMicrocomputador);
//
//			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_IMPRESSAO_ETIQUETA_AMOSTRAS", qtdAmostras);
//
//		} catch (final BaseException e) {
//			apresentarExcecaoNegocio(e);
//		} catch (UnknownHostException e) {
//			LOG.error(e.getMessage(), e);
//		}
//	}

	/**
	 * Reimprime uma etiqueta de amostra.
	 */
	public void reimprimirEtiquetaAmostra() {

		List<AelAmostrasVO> amostras = new ArrayList<AelAmostrasVO>();

		amostras.add(this.amostraParaReimpressao);

		try {

			{ // Impressão de etiquetas - ínicio
				//Verifica se existe etiquetas de número AP
				final List<ImprimeEtiquetaVO> etiquetas = this.examesFacade
						.gerarEtiquetasAmostrasRecebidasUnf(amostras,
								unidadeExecutora);

				boolean imprimiuSucesso = false;
				for (final ImprimeEtiquetaVO etiqueta : etiquetas) {
					imprimiuSucesso = this.aelpImprimeEtiqAp(etiqueta);
				}

				if(imprimiuSucesso){
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_IMPRESSAO_ETIQUETA", this.amostraParaReimpressao.getSeqp());
				}else{
					//Imprime etiquetas amostras normais
					reimprimirEtiquetasController.setAmostraSoeSeqSelecionada(amostraParaReimpressao.getSoeSeq());
					reimprimirEtiquetasController.setAmostraSeqpSelecionada(amostraParaReimpressao.getSeqp());
					reimprimirEtiquetasController.setUnidadeExecutora(unidadeExecutora);
					reimprimirEtiquetasController.reimprimirAmostra();
				}

			}// Impressão de etiquetas - Fim
		} catch (final BaseException e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_IMPRESSAO_ETIQUETA_ERRO", this.amostraParaReimpressao.getSeqp());
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_IMPRESSAO_ETIQUETA_ERRO", this.amostraParaReimpressao.getSeqp());
		}

	}	
	/**
	 * Cancela a edicao no numero do frasco do fornecedor e restaura o valor original do mesmo
	 * 
	 * @param amostra
	 */
	public void cancelarEdicaoFrasco(final AelAmostrasVO vo) {
		final AelAmostras amostra = this.examesFacade.buscarAmostrasPorId(vo.getSoeSeq(), vo.getSeqp());
		vo.setNroFrascoFabricante(this.examesFacade.cancelarEdicaoFrasco(amostra.getId()));
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CANCELADA_EDICAO_FRASCO", vo.getSeqp());
	}

	/**
	 * Verifica se a amostra possui uma situacao que permita o recebimento de amostra
	 * 
	 * @param vo
	 * @return
	 */
	public boolean verificaAmostraSituacaoReceber(final AelAmostrasVO vo) {
		final DominioSituacaoAmostra situacao = vo.getSituacao();
		if (!DominioSituacaoAmostra.R.equals(situacao) && !DominioSituacaoAmostra.E.equals(situacao) && !DominioSituacaoAmostra.U.equals(situacao)) {
			return true;
		}
		return false;
	}

	/**
	 * Verifica se a amostra possui a situacao executada
	 * 
	 * @param vo
	 * @return
	 */
	public boolean verificaSituacaoExecutada(final AelAmostrasVO vo) {
		return DominioSituacaoAmostra.E.equals(vo.getSituacao()) || this.verificaSituacaoCancelada(vo);
	}

	public boolean verificaSituacaoCancelada(final AelAmostrasVO vo) {
		return DominioSituacaoAmostra.A.equals(vo.getSituacao());
	}

	/**
	 * Verifica se a unidade executora de exames é uma UNIDADE PATOLOGICA
	 * 
	 * @return
	 */
	public boolean isVerificaUnidadePatologica() {
		if (this.unidadeExecutora != null) {
			this.unidadeExecutora = this.aghuFacade.obterUnidadeFuncionalComCaracteristica(this.unidadeExecutora.getSeq());
			//			this.unidadeExecutora = this.aghuFacade.obterUnidadeFuncionalPorChavePrimaria(this.unidadeExecutora);
			this.verificaUnidadePatologica = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_PATOLOGIA);
			return this.verificaUnidadePatologica;
		}
		return false;
	}

	public boolean isObrigatorioNumeroAp() {
		return DominioApAnterior.O.equals(this.dominioApAnterior);
	}

	/**
	 * Impressão das Etiquetas de Amostra Por Solicitações de Exames
	 * 
	 * @param unidadeExec
	 *            , solicitacoesExame
	 */
	// @Observer("imprimirEtiquetasAmostra")
	public void imprimirEtiquetaAmostraPorSolicitacao(final AghUnidadesFuncionais unidadeExec, final List<AelSolicitacaoExames> solicitacoesExame) throws BaseException, JRException, SystemException,
			IOException {
		this.unidadeExecutora = unidadeExec;
		for (final AelSolicitacaoExames solicitacao : solicitacoesExame) {
			final List<AelAmostras> listaAmostrasSolicitacao = this.examesFacade.buscarAmostrasPorSolicitacaoExame(solicitacao, null);
			if (!listaAmostrasSolicitacao.isEmpty()) {
				for (final AelAmostras amostra : listaAmostrasSolicitacao) {
					final AelAmostrasVO amostraVO = new AelAmostrasVO();
					amostraVO.setSoeSeq(solicitacao.getSeq());
					amostraVO.setSeqp(amostra.getId().getSeqp());
					this.calcularNumeroImpressoes(amostraVO);
					//this.imprimirEtiquetaAmostra();
					reimprimirEtiquetaAmostra();
				}
			}
		}
	}
	
	public String redirecionarEmissaoMapaTrabalho(){
		return EMISSAO_MAPA_TRABALHO;
	}
	
	public String redirecionarCancelarExamesAreaExecutora(){
		return CANCELAR_EXAMES_AREA_EXECUTORA;
	}

	/*
	 * Getters e Setters
	 */

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(final AghUnidadesFuncionais unidadeExecutora) {
		if (unidadeExecutora == null) {
			this.unidadeExecutora = null;
		} else {
			if (!unidadeExecutora.equals(this.unidadeExecutora) && this.amostraSoeSeqSelecionada != null) {
				this.pesquisarTeclado(this.amostraSoeSeqSelecionada, this.amostraSeqpSelecionada);
			}
			this.unidadeExecutora = aghuFacade.obterUnidadeFuncionalComCaracteristica(unidadeExecutora.getSeq());
		}
	}

	public Long getValorEntradaCampoSolicitacao() {
		return valorEntradaCampoSolicitacao;
	}

	public void setValorEntradaCampoSolicitacao(final Long valorEntradaCampoSolicitacao) {
		this.valorEntradaCampoSolicitacao = valorEntradaCampoSolicitacao;
	}

	public Short getValorEntradaCampoAmostra() {
		return valorEntradaCampoAmostra;
	}

	public void setValorEntradaCampoAmostra(final Short valorEntradaCampoAmostra) {
		this.valorEntradaCampoAmostra = valorEntradaCampoAmostra;
	}

	public AelConfigExLaudoUnico getConfigExameOrigem() {
		return configExameOrigem;
	}

	public void setConfigExameOrigem(AelConfigExLaudoUnico configExameOrigem) {
		this.configExameOrigem = configExameOrigem;
	}

/*	public Integer getNumeroApOrigem() {
		return numeroApOrigem;
	}

	public void setNumeroApOrigem(final Integer numeroApOrigem) {
		this.numeroApOrigem = numeroApOrigem;
	}*/

	public String getSolicitacaoOrigem() {
		return solicitacaoOrigem;
	}

	public void setSolicitacaoOrigem(final String solicitacaoOrigem) {
		this.solicitacaoOrigem = solicitacaoOrigem;
	}

	public String getSolicitacaoPaciente() {
		return solicitacaoPaciente;
	}

	public void setSolicitacaoPaciente(final String solicitacaoPaciente) {
		this.solicitacaoPaciente = solicitacaoPaciente;
	}

	public Integer getSolicitacaoProntuario() {
		return solicitacaoProntuario;
	}

	public void setSolicitacaoProntuario(final Integer solicitacaoProntuario) {
		this.solicitacaoProntuario = solicitacaoProntuario;
	}

	public String getSolicitacaoUnidade() {
		return solicitacaoUnidade;
	}

	public void setSolicitacaoUnidade(final String solicitacaoUnidade) {
		this.solicitacaoUnidade = solicitacaoUnidade;
	}

	public String getSolicitacaoQuarto() {
		return solicitacaoQuarto;
	}

	public void setSolicitacaoQuarto(final String solicitacaoQuarto) {
		this.solicitacaoQuarto = solicitacaoQuarto;
	}

	public String getSolicitacaoLeito() {
		return solicitacaoLeito;
	}

	public void setSolicitacaoLeito(final String solicitacaoLeito) {
		this.solicitacaoLeito = solicitacaoLeito;
	}

	public String getSolicitacaoConvenio() {
		return solicitacaoConvenio;
	}

	public void setSolicitacaoConvenio(final String solicitacaoConvenio) {
		this.solicitacaoConvenio = solicitacaoConvenio;
	}

	public String getSolicitacaoInformacoes() {
		return solicitacaoInformacoes;
	}

	public void setSolicitacaoInformacoes(final String solicitacaoInformacoes) {
		this.solicitacaoInformacoes = solicitacaoInformacoes;
	}

	public List<AelAmostrasVO> getListaAmostras() {
		return listaAmostras;
	}

	public void setListaAmostras(final List<AelAmostrasVO> listaAmostras) {
		this.listaAmostras = listaAmostras;
	}

	public List<AelAmostraItemExames> getListaExamesAmostras() {
		return listaExamesAmostras;
	}

	public void setListaExamesAmostras(final List<AelAmostraItemExames> listaExamesAmostras) {
		this.listaExamesAmostras = listaExamesAmostras;
	}

	public String getAmostraMaterialAnalise() {
		return amostraMaterialAnalise;
	}

	public void setAmostraMaterialAnalise(final String amostraMaterialAnalise) {
		this.amostraMaterialAnalise = amostraMaterialAnalise;
	}

	public void setVerificaUnidadePatologica(final boolean verificaUnidadePatologica) {
		this.verificaUnidadePatologica = verificaUnidadePatologica;
	}

	public Integer getAmostraSoeSeqSelecionada() {
		return amostraSoeSeqSelecionada;
	}

	public void setAmostraSoeSeqSelecionada(final Integer amostraSoeSeqSelecionada) {
		this.amostraSoeSeqSelecionada = amostraSoeSeqSelecionada;
	}

	public Short getAmostraSeqpSelecionada() {
		return amostraSeqpSelecionada;
	}

	public void setAmostraSeqpSelecionada(final Short amostraSeqpSelecionada) {
		this.amostraSeqpSelecionada = amostraSeqpSelecionada;
	}

	public Integer getSolicitacaoNumero() {
		return solicitacaoNumero;
	}

	public void setSolicitacaoNumero(final Integer solicitacaoNumero) {
		this.solicitacaoNumero = solicitacaoNumero;
	}

	public Short getSolicitacaoAmostra() {
		return solicitacaoAmostra;
	}

	public void setSolicitacaoAmostra(final Short solicitacaoAmostra) {
		this.solicitacaoAmostra = solicitacaoAmostra;
	}

	public AelAmostras getAmostraImpressaoEtiquetas() {
		return amostraImpressaoEtiquetas;
	}

	public void setAmostraImpressaoEtiquetas(final AelAmostras amostraImpressaoEtiquetas) {
		this.amostraImpressaoEtiquetas = amostraImpressaoEtiquetas;
	}

	public String getMensagemConfirmacaoImpressaoEtiquetas() {
		return mensagemConfirmacaoImpressaoEtiquetas;
	}

	public void setMensagemConfirmacaoImpressaoEtiquetas(final String mensagemConfirmacaoImpressaoEtiquetas) {
		this.mensagemConfirmacaoImpressaoEtiquetas = mensagemConfirmacaoImpressaoEtiquetas;
	}

	public void setNumeroApRespAp(final Integer numeroApRespAp) {
		this.numeroApRespAp = numeroApRespAp;
	}

	public Integer getNumeroApRespAp() {
		return numeroApRespAp;
	}

	public void setPesquisar(final boolean pesquisar) {
		this.pesquisar = pesquisar;
	}

	public boolean isPesquisar() {
		return pesquisar;
	}

	public boolean isHabilitarNumeroAp() {
		return DominioApAnterior.O.equals(this.dominioApAnterior) || DominioApAnterior.P.equals(this.dominioApAnterior);
	}

	public List<ExameAndamentoVO> getListaExameOrigem() {
		return listaExameOrigem;
	}

	public void setListaExameOrigem(List<ExameAndamentoVO> listaExameOrigem) {
		this.listaExameOrigem = listaExameOrigem;
	}
	
	public AelAmostrasVO getAmostraRecebida() {
		return amostraRecebida;
	}

	public void setAmostraRecebida(AelAmostrasVO amostraRecebida) {
		this.amostraRecebida = amostraRecebida;
	}

	public ExameAndamentoVO getExameOrigemSelecionado() {
		return exameOrigemSelecionado;
	}

	public void setExameOrigemSelecionado(ExameAndamentoVO exameOrigemSelecionado) {
		this.exameOrigemSelecionado = exameOrigemSelecionado;
	}
	
	public Set<ExameAndamentoVO> getListaExamesSelecionados() {
		return listaExamesSelecionados;
	}

	public void setListaExamesSelecionados(
			Set<ExameAndamentoVO> listaExamesSelecionados) {
		this.listaExamesSelecionados = listaExamesSelecionados;
	}

	public boolean aelpImprimeEtiqAp(final ImprimeEtiquetaVO imprimeEtiquetaVO) throws BaseException {
		try {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
			}
			
			String nomeImpressora = solicitacaoExameFacade.obterNomeImpressoraEtiquetas(nomeMicrocomputador);
			
			getSistemaImpressao().imprimir(this.gerarZPL(imprimeEtiquetaVO),
					nomeImpressora);
			return true;
		} catch (SistemaImpressaoException e) {
			this.apresentarMsgNegocio(Severity.ERROR,
					"MSG_ERRO_IMPRIMIR_NRO_AP", e.getMessage());
		}
		return false;
	}
	
	private String gerarZPL(final ImprimeEtiquetaVO imprimeEtiquetaVO)
			throws BaseException {
		return solicitacaoExameFacade
				.gerarZPLEtiquetaNumeroExame(imprimeEtiquetaVO);
	}	

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public void aelpImprimeFichaTrabalhoPat(final Integer soeSeq, final Short unfSeq) throws BaseException {

		this.relatorioFichaTrabalhoPatologiaController.setSoeSeq(soeSeq);
		this.relatorioFichaTrabalhoPatologiaController.setUnidadeExecutora(this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq));
		this.relatorioFichaTrabalhoPatologiaController.directPrint();
	}

	public void aelpImprimeFichaTrabalhoLab(final Integer soeSeq, final Short unfSeq, final Short amoSeqP, final Boolean receberAmostra) throws BaseException {

		this.relatorioFichaTrabalhoPorExameController.setSoeSeq(soeSeq);
		this.relatorioFichaTrabalhoPorExameController.setAmostra(amoSeqP);
		this.relatorioFichaTrabalhoPorExameController.setReceberAmostra(receberAmostra);
		this.relatorioFichaTrabalhoPorExameController.setUnidadeExecutora(this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq));
		this.relatorioFichaTrabalhoPorExameController.directPrint();
	}

	public void aelpImprimeFichaTrabalhoAmo(final Integer soeSeq, final Short unfSeq, final Short amoSeqP) throws BaseException {

		this.relatorioFichaTrabalhoAmostraController.setSoeSeq(soeSeq);
		this.relatorioFichaTrabalhoAmostraController.setAmostra(amoSeqP);
		this.relatorioFichaTrabalhoAmostraController.setUnidadeExecutora(this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq));
		this.relatorioFichaTrabalhoAmostraController.directPrint();
	}

	public List<AelConfigExLaudoUnico> pesquisarAelConfigExLaudoUnico(Object value) {
		return examesPatologiaFacade.pesquisarAelConfigExLaudoUnico(AelConfigExLaudoUnico.Fields.NOME.toString(), (String) value);
	}

	public Long pesquisarAelConfigExLaudoUnicoCount(Object value) {
		return examesPatologiaFacade.pesquisarAelConfigExLaudoUnicoCount(AelConfigExLaudoUnico.Fields.NOME.toString(), (String) value);
	}
	

	public void setAgruparExamesController(AgruparExamesController agruparExamesController) {
		this.agruparExamesController = agruparExamesController;
	}

	public AgruparExamesController getAgruparExamesController() {
		return agruparExamesController;
	}	

	public String agruparExamesReceberTodasAmostras() {
		if(this.listaAmostras != null && !this.listaAmostras.isEmpty()) {
			this.agruparExamesController.setListaAmostrasRecebidas(this.listaAmostras);
		}
		return redirecionarAgruparExames();
	}
	
	public String agruparExameReceberAmostra(final AelAmostrasVO vo) {
		final List<AelAmostrasVO> listaAmostras = new ArrayList<AelAmostrasVO>(1);
		listaAmostras.add(vo);
		this.agruparExamesController.setListaAmostrasRecebidas(listaAmostras);
		this.agruparExamesController.setAmostraRecebida(vo);
		return redirecionarAgruparExames();
	}
	
	private String redirecionarAgruparExames(){
		this.agruparExamesController.setSolicitacaoNumero(solicitacaoNumero);
		this.agruparExamesController.setSeqp(valorEntradaCampoAmostra);
		this.agruparExamesController.setSolicitacaoProntuario(solicitacaoProntuario);
		this.agruparExamesController.setSolicitacaoPacCodigo(solicitacaoPacCodigo);
		this.agruparExamesController.setSolicitacaoPaciente(solicitacaoPaciente);
		
		this.agruparExamesController.setUnidadeSeq(unidadeExecutora.getSeq()); 
		return AGRUPAR_EXAMES;
	}
	
	public boolean isNaoMostrarMensagemSucessoQndoExaRedomeForSolicitado() {
		return naoMostrarMensagemSucessoQndoExaRedomeForSolicitado;
	}

	public void setNaoMostrarMensagemSucessoQndoExaRedomeForSolicitado(
			boolean naoMostrarMensagemSucessoQndoExaRedomeForSolicitado) {
		this.naoMostrarMensagemSucessoQndoExaRedomeForSolicitado = naoMostrarMensagemSucessoQndoExaRedomeForSolicitado;
	}

	public AelAmostrasVO getAmostraParaReimpressao() {
		return amostraParaReimpressao;
	}

	public void setAmostraParaReimpressao(AelAmostrasVO amostraParaReimpressao) {
		this.amostraParaReimpressao = amostraParaReimpressao;
	}

	public AelAmostrasVO getAmostraSelecionada() {
		return amostraSelecionada;
	}

	public void setAmostraSelecionada(AelAmostrasVO amostraSelecionada) {
		this.amostraSelecionada = amostraSelecionada;
	}

	public Integer getSolicitacaoPacCodigo() {
		return solicitacaoPacCodigo;
	}

	public void setSolicitacaoPacCodigo(Integer solicitacaoPacCodigo) {
		this.solicitacaoPacCodigo = solicitacaoPacCodigo;
	}

	
}