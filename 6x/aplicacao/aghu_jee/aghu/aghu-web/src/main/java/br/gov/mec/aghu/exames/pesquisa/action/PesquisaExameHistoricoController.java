package br.gov.mec.aghu.exames.pesquisa.action;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.action.CarregarArquivoLaudoResultadoExameController;
import br.gov.mec.aghu.exames.action.ConsultarResultadosNotaAdicionalController;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.business.PesquisaExamesExceptionCode;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.sismama.action.VerificaQuestoesSismamaController;
import br.gov.mec.aghu.exames.sismama.business.ISismamaFacade;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity"})
public class PesquisaExameHistoricoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}


	private static final long serialVersionUID = 3661785096909101631L;
	
	@SuppressWarnings("unchecked")
	private static final Comparator PT_BR_COMPARATOR = new Comparator() {
		@Override
		public int compare(Object o1, Object o2) {

			final Collator collator = Collator.getInstance(new Locale("pt",
					"BR"));
			collator.setStrength(Collator.PRIMARY);

			return ((Comparable) o1).compareTo(o2);
		}
	};

	
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@EJB
	private ISismamaFacade sismamaFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private CarregarArquivoLaudoResultadoExameController carregarArquivoLaudoResultadoExameController;

	/*@EJB
	private ConsultarResultadosNotaAdicionalHistController consultarResultadosNotaAdicionalHistController;*/
	@Inject
	private ConsultarResultadosNotaAdicionalController consultarResultadosNotaAdicionalController;
	
	@Inject
	private VerificaQuestoesSismamaController verificaQuestoesSismamaController;
	
	private Map<Integer, Vector<Short>> solicitacoes = new HashMap<Integer, Vector<Short>>();
	
	private String paginaVoltarPol;

	private RapServidores servExame = new RapServidores();

	private Integer codigoSoeSelecionado;
	private Short iseSeqSelecionado;
	
	private String voltarPara;
	
	@Inject
	private PesquisaExameController pesquisaExameController;
	
	@Inject
	private PesquisaExamesPorPacienteController pesquisaExamesPorPacienteController;

	
	
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO nodoPol;
	
	private AipPacientes paciente;

	/**
	 * Retorna para a lista de pacientes da prescricao
	 */
	private boolean exibirBotaoVoltar = false;
	private boolean botaoImagens = false;

	private List<PesquisaExamesPacientesResultsVO> listaResultadoPesquisa;
	
	private Comparator<PesquisaExamesPacientesResultsVO> currentComparator;
	private String currentSortProperty;
	
	

	public void inicio() {
	 

		Integer prontuario = nodoPol.getProntuario();
		try {
			if (prontuario != null && prontuario > 0) {
				exibirBotaoVoltar = true;
				
				selecionarItemExame(null, null);
				setSolicitacoes(new HashMap<Integer, Vector<Short>>());

				this.paciente = this.pacienteFacade.obterPacientePorProntuario(prontuario);
				
				if (this.paciente == null) {
					throw new ApplicationBusinessException(
							PesquisaExamesExceptionCode.AEL_00475);
				} else {
					
					this.listaResultadoPesquisa = this.pesquisaExamesFacade.buscaExamesSolicitadosPorPacienteHist(this.paciente);
				}
			} else {
				throw new ApplicationBusinessException(
						PesquisaExamesExceptionCode.AEL_00833);
			}
		} catch (BaseException e) {
			this.listaResultadoPesquisa = null;
			apresentarExcecaoNegocio(e);
		}
	
	}

	@SuppressWarnings("unchecked")
	public void ordenar(String propriedade) {
		Comparator comparator = null;

		// se mesma propriedade, reverte a ordem
		if (this.currentComparator != null
				&& this.currentSortProperty.equals(propriedade)) {
			comparator = new ReverseComparator(this.currentComparator);
		} else {
			// cria novo comparator para a propriedade escolhida
			comparator = new BeanComparator(propriedade, new NullComparator(
					PT_BR_COMPARATOR, false));
		}

		Collections.sort(this.listaResultadoPesquisa, comparator);

		// guarda ordenação corrente
		this.currentComparator = comparator;
		this.currentSortProperty = propriedade;
	}

	public void selecionarItemExame(Integer codigoSoeSelecionado, Short iseSeqSelecionado) {
		setCodigoSoeSelecionado(codigoSoeSelecionado);
		setIseSeqSelecionado(iseSeqSelecionado);
	}

	public void selecionaItemExame(Integer codigoSoeSelecionado, Short iseSeqSelecionado) {
		this.selecionarItemExame(codigoSoeSelecionado, iseSeqSelecionado);
		if (this.solicitacoes.containsKey(codigoSoeSelecionado)){
			if (this.solicitacoes.get(codigoSoeSelecionado).contains(iseSeqSelecionado)) {
				this.solicitacoes.get(codigoSoeSelecionado).remove(iseSeqSelecionado);

				if (this.solicitacoes.get(codigoSoeSelecionado).size()==0) {
					this.solicitacoes.remove(codigoSoeSelecionado);
				}
			} else {
				this.solicitacoes.get(codigoSoeSelecionado).add(iseSeqSelecionado);
			}
		} else {
			this.solicitacoes.put(codigoSoeSelecionado, new Vector<Short>());
			this.solicitacoes.get(codigoSoeSelecionado).add(iseSeqSelecionado);
		}
	}
	
	public boolean isDisableButtonDetalheExame() {
		return codigoSoeSelecionado==null; 
	}

	/**
	 * Download do arquivo em anexo
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @return
	 */
	public String downloadAnexo(Integer iseSoeSeq, Short iseSeqp) {
		// Efetua download do arquivo em anexo
		String resultado = this.carregarArquivoLaudoResultadoExameController.downloadArquivoLaudo(iseSoeSeq, iseSeqp);

		try {
			
			//  Persiste o download ou visualizacao do documento anexado
			this.examesFacade.persistirVisualizacaoDownloadAnexo(iseSoeSeq, iseSeqp);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return resultado;
	}

	public String voltar() {
		if(this.voltarPara != null){
			return this.voltarPara;
		}
		return "voltaPesquisaExamesPOL";
	}
	
	/**
	 * Menu Detalhar Exames
	 * @return
	 */
	public String detalhesExames() {
		if (this.solicitacoes != null && !this.solicitacoes.isEmpty()) {
			if (!this.pesquisaExamesFacade.validaQuantidadeExamesSelecionados(solicitacoes)) {
				this.apresentarMsgNegocio(Severity.ERROR,"ERRO_SELECIONE_APENAS_UMA_SOLICITACAO_EXAME");
				return "";
			}

			Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
			if (it.hasNext()) {
				Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
				/*Item de solicitacao*/
				Integer solicitacao = paramCLValores.getKey();

				/*coleção de seqp da solicitacao acima*/
				Vector<Short> seqps = paramCLValores.getValue();
				selecionarItemExame(solicitacao, seqps.get(0));
			}
		}
		
		return "exames-detalharItemSolicitacaoExame";
	}
	
	// Metódo para Suggestion Box de Agrupamentos de pesquisa
	public List<AelAgrpPesquisas> obterAgrpPesquisaPac(Object objPesquisa){
		return this.pesquisaExamesFacade.buscaAgrupamentosPesquisa(objPesquisa);
	}

	public List<AacConsultas> obtemListSituacaoExames() {
		return examesFacade.listarSituacaoExames();
	}
	
	public boolean isDisableButton() {
		if (this.codigoSoeSelecionado != null && this.iseSeqSelecionado != null) {
			return false;
		} else {
			return true;
		}
	}

	public String verResultados() {
		consultarResultadosNotaAdicionalController.setIsHist(Boolean.TRUE);
		consultarResultadosNotaAdicionalController.setVoltarPara("exames-pesquisaExamesHistorico");
		pesquisaExamesPorPacienteController.setIsHist(Boolean.TRUE);
		pesquisaExameController.setIsHist(Boolean.TRUE);
		try {
			//Simula método pesquisar de pesquisaExameController; 
			pesquisaExameController.setListaPacientes(this.pesquisaExamesFacade.buscarAipPacientesPorParametros(paciente.getProntuario(), paciente.getNome(), null, null));
			pesquisaExameController.setTipoPesquisa(PesquisaExameController.TipoPesquisa.PACIENTE);
			pesquisaExameController.getFiltro().setConsultaPac(null);
			pesquisaExamesPorPacienteController.selecionarPaciente(paciente.getCodigo(), null, paciente.getProntuario());
			
			//Simula seleção de exames
			pesquisaExameController.setSolicitacoes(getSolicitacoes());
			
			return pesquisaExameController.verResultados();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	/*public String verResultados() {
		consultarResultadosNotaAdicionalController.setIsHist(Boolean.TRUE);
		if (this.solicitacoes != null && this.solicitacoes.size() > 0) {
			//this.consultarResultadosNotaAdicionalHistController.setSolicitacoes(this.solicitacoes);
			this.consultarResultadosNotaAdicionalController.setSolicitacoes(this.solicitacoes);
			return "consultarResultadoNotaAdicionalHist";
		}
		return null;
	}*/
/*	public String verResultados() {
		consultarResultadosNotaAdicionalController.setIsHist(Boolean.TRUE);
		if (this.solicitacoes != null && this.solicitacoes.size() > 0) {
			Map<Integer, Vector<Short>> solicitacoesTemp = new HashMap<Integer, Vector<Short>>();

			try{
				Verifica a situação dos itens
				this.pesquisaExamesFacade.validaSituacaoExamesSelecionados(solicitacoes);

				if(this.pesquisaExamesFacade.permiteVisualizarLaudoMedico()){
					consultarResultadosNotaAdicionalController.setSolicitacoes(solicitacoes);
					consultarResultadosNotaAdicionalController.setTipoLaudo(DominioTipoImpressaoLaudo.LAUDO_SAMIS);
					consultarResultadosNotaAdicionalController.setDominioSubTipoImpressaoLaudo(DominioSubTipoImpressaoLaudo.LAUDO_GERAL);
					return "verResultadosExames";

				}else if(this.pesquisaExamesFacade.permitevisualizarLaudoAtdExt() && this.pesquisaExamesFacade.permitevisualizarLaudoSamis()){
					
					PesquisaExamesFiltroVO filtro = new PesquisaExamesFiltroVO();
					filtro.setCodigoPac(paciente.getCodigo());
					filtro.setNomePacientePac(paciente.getNome());
					filtro.setProntuarioPac(paciente.getProntuario());
					filtro.setServidorLogado(registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date()));
					filtro.setIndMostraCanceladosInfo(DominioSimNao.N);
					filtro.setRestricao(DominioRestricaoUsuario.TD);
					List<PesquisaExamesPacientesResultsVO> listaPacientes = this.pesquisaExamesFacade
							.buscaExamesSolicitadosPorPaciente(paciente.getCodigo(),null, filtro, filtro.getServidorLogado());
					List<PesquisaExamesPacientesResultsVO> listaPacientes = null;
					
					if (this.tipoPesquisa!=null && this.tipoPesquisa.equals(TipoPesquisa.PACIENTE)) {
						listaPacientes = pesquisaExamesPorPacienteController.getListaResultadoPesquisa();
					}else{  pesquisa por solicitante 
						listaPacientes = pesquisaExamesPorSolicitanteController.getListaResultadoPesquisa();
					}
					
					Integer soeSeq = null;
					Short seqp = null;

					Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
						soeSeq = paramCLValores.getKey();
						Vector<Short> seqps = paramCLValores.getValue();
						for (Iterator iterator = seqps.iterator(); iterator.hasNext();) {
							seqp = (Short) iterator.next();
						}
					}
					
					boolean islaudoPacExterno = false, islaudoSamis = false;
					
					for (PesquisaExamesPacientesResultsVO listaResult : listaPacientes) {
						
						if(listaResult.getCodigoSoe().equals(soeSeq) && listaResult.getIseSeq().equals(seqp)){

							if(DominioOrigemAtendimento.X.equals(listaResult.getOrigemAtendimento())
									|| DominioOrigemAtendimento.D.equals(listaResult.getOrigemAtendimento())) {
								islaudoPacExterno = true;
								break;
							}

							if(!DominioOrigemAtendimento.X.equals(listaResult.getOrigemAtendimento())
									&& !DominioOrigemAtendimento.D.equals(listaResult.getOrigemAtendimento())) {
								islaudoSamis = true;
								break;
							}
						}
					}

					// Verifica se o paciente é atendimento externo
					if(islaudoPacExterno){
						this.verResultadosLaudoAtdExt(solicitacoesTemp); // Laudo atendimento externo
					} else if(islaudoSamis){
						this.verResultadosLaudoSamis(solicitacoesTemp); // Laudo SAMIS
					}

				}else if(this.pesquisaExamesFacade.permitevisualizarLaudoAtdExt()){ // Laudo atendimento externo
					this.verResultadosLaudoAtdExt(solicitacoesTemp);
				}else if(this.pesquisaExamesFacade.permitevisualizarLaudoSamis()){ // Laudo SAMIS
					this.verResultadosLaudoSamis(solicitacoesTemp);
				}else{
					this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_PERMISSAO_USUARIO");
					return null;
				}

				consultarResultadosNotaAdicionalController.setSolicitacoes(solicitacoesTemp);
				consultarResultadosNotaAdicionalController.setDominioSubTipoImpressaoLaudo(DominioSubTipoImpressaoLaudo.LAUDO_GERAL);
				consultarResultadosNotaAdicionalController.directPrint();

			}catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				return null;
			}
		}
		return null;
	}*/
	
	/**
	 * Valida de o usuario é o responsavel do exame para cancelamento
	 * @return
	 */
	public boolean validaResponsavelExameSelecionado() {
		boolean retorno = false;
		
		if (this.codigoSoeSelecionado != null && this.iseSeqSelecionado != null) {
			try{
				AelSolicitacaoExames solicitacao = examesFacade.obterAelSolicitacaoExamesPeloId(codigoSoeSelecionado);
				if (solicitacao != null && solicitacao.getServidor().getId().getMatricula().equals(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId().getMatricula())) {
					if (examesFacade.possuiExameCancelamentoSolicitante(codigoSoeSelecionado, iseSeqSelecionado)) {
						retorno = true;
					}
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				return false;
			}			
		}
		return retorno;
	}
	
	
	/**
	 * Monta a primeira parte da URL IMPAX. Nesta parte são considerados somente os dados do paciente
	 * 
	 * @param itensSelecionados
	 * @return
	 */
	private String getParte1UrlImpax() {
		String url = null;
		
		try {
			AghParametros paramverImgImpax = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_VER_IMAGENS_IMPAX);

			//if (solicitacoes != null && paramverImgImpax.getVlrNumerico().equals(BigDecimal.valueOf(1))) {
			if(solicitacoes!=null && paramverImgImpax.getVlrNumerico().equals(BigDecimal.valueOf(1))){
//				Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
//				if (it.hasNext()) {
//					Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
//					Integer solicitacao = paramCLValores.getKey();
//					Vector<Short> seqps = paramCLValores.getValue();
//
//					AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
//					id.setSoeSeq(solicitacao);
//					id.setSeqp(seqps.get(0));
//					AelItemSolicExameHist itemSolicExameHist = this.examesFacade.buscaItemSolicitacaoExamePorIdHist(id.getSoeSeq(), id.getSeqp());
//					accessiom = itemSolicExameHist.getPacOruAccNumber();
//					
//				}	
				
				if(this.paciente != null && this.paciente.getCodigo() != null){
					AghParametros paramEndWebImagens = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ENDERECO_WEB_IMAGENS);
					url =  paramEndWebImagens.getVlrTexto() + this.paciente.getCodigo();
				}

			}
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}

		return url;
	}

	/**
	 * Monta a segunda parte da URL IMPAX. Nesta parte são considerados os itens de solicitação de exames e seus respectivos PAC_ORU_ACC_NUMBER
	 * 
	 * @param itensSelecionados
	 * @return
	 */
	private String getParte2UrlImpax() {

		Set<Integer> keys = solicitacoes.keySet();
		StringBuilder accessioms = new StringBuilder();
		int countAccessioms = 0;
		boolean hasImage = false;

		for (Integer soeSeq : keys) {

			Vector<Short> seqps = solicitacoes.get(soeSeq);

			for (Short seqp : seqps) {

				AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
				id.setSoeSeq(soeSeq);
				id.setSeqp(seqp);
				AelItemSolicExameHist itemSolicitacaoExames = this.examesFacade.buscaItemSolicitacaoExamePorIdHist(id.getSoeSeq(), id.getSeqp());

				String pacOruAccNumber = itemSolicitacaoExames.getPacOruAccNumber();

				if (pacOruAccNumber != null) {
					if (countAccessioms > 0) {
						// Quando há mais de um PAC_ORU_ACC_NUMBER o delimitador IMPAX '|' deve ser utilizado
						accessioms.append('|');
					}
					accessioms.append(pacOruAccNumber);
					countAccessioms++;
					hasImage = true;
				}
			}
		}
		
		botaoImagens = hasImage;
		if(hasImage == false){
			return null;
		}

		// Retorna o parâmetro accession no seguint foramato: &accession=<PAC_ORU_ACC_NUMBER1>|<PAC_ORU_ACC_NUMBER2>|...', 'IMPAX');
		return "%26accession%3D" + accessioms ;
	}
	
	public String getUrlImpax() {
		// Monta as duas primeiras partes da URL IMPAX
		String parte1UrlImpax = this.getParte1UrlImpax(); 
		if(parte1UrlImpax == null){
			return null;
		}
		String parte2UrlImpax = this.getParte2UrlImpax();
		if(parte2UrlImpax == null){
			return null;
		}
		// Retorna URL IMPAX
		return parte1UrlImpax + parte2UrlImpax;
	}
		

	public Boolean habilitarBotaoQuestoesSismama() {		
		return examesLaudosFacade.habilitarBotaoQuestaoSismama(solicitacoes, Boolean.TRUE);
	}	
	
	public Boolean habilitarBotaoQuestoesSismamaBiopsia() {		
		return sismamaFacade.habilitarBotaoQuestaoSismamaBiopsia(solicitacoes, Boolean.TRUE);
	}
	
	public String verificarQuestoesSismama() {
		if (habilitarBotaoQuestoesSismama()) {
			//Carrega todas as resposta do questionário
			if (!solicitacoes.isEmpty() && solicitacoes.size() == 1) {
				Integer iseSoeSeq = solicitacoes.keySet().iterator().next();
				Short iseSeqp = null;
				if (solicitacoes.get(iseSoeSeq).size() == 1) {
					iseSeqp = solicitacoes.get(iseSoeSeq).iterator().next();
					verificaQuestoesSismamaController.preencherQuestionario(iseSoeSeq, iseSeqp, Boolean.TRUE);
				}
			}
			return "exames-verificaQuestoesSismama";
		} else {
			return verificarQuestoesSismamaBiopsia();
		}
	}
	
	public String verificarQuestoesSismamaBiopsia() {
		Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
		if (it.hasNext()) {
			Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
			/*Item de solicitacao*/
			Integer solicitacao = paramCLValores.getKey();
			/*coleção de seqp da solicitacao acima*/
			Vector<Short> seqps = paramCLValores.getValue();
			selecionarItemExame(solicitacao, seqps.get(0));
		}
		return "exames-visualizarQuestionarioSismamaBiopsia";
	}
	
	public String redirecionarRespostaQuestionario() {
		if (this.solicitacoes != null && !this.solicitacoes.isEmpty()) {
			try{
				if (!this.pesquisaExamesFacade.validaQuantidadeExamesSelecionados(solicitacoes)) {
					this.apresentarMsgNegocio(Severity.ERROR,"ERRO_SELECIONE_APENAS_UMA_SOLICITACAO_EXAME");
					return "";
				}
				Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();

				if (it.hasNext()) {

					Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();

					/*Item de solicitacao*/
					Integer solicitacao = paramCLValores.getKey();

					/*coleção de seqp da solicitacao acima*/
					Vector<Short> seqps = paramCLValores.getValue();

					selecionarItemExame(solicitacao, seqps.get(0));
				}
				this.pesquisaExamesFacade.validarExamesComRespostaHistorico(this.codigoSoeSelecionado, this.iseSeqSelecionado);
				return "exames-pesquisaRespostaQuestionario";
			} catch(ApplicationBusinessException e){
				this.apresentarMsgNegocio(Severity.ERROR,"ERRO_SELECIONE_UMA_SOLICITACAO_COM_RESPOSTA_QUESTIONARIO");
				return "";
			}
		}
		return "";
	}
	/*
	public String verificarQuestoesSismamaBiopsia() {
		Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
		if (it.hasNext()) {
			Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
			//Item de solicitacao
			Integer solicitacao = paramCLValores.getKey();
			//coleção de seqp da solicitacao acima
			Vector<Short> seqps = paramCLValores.getValue();
			selecionarItemExame(solicitacao, seqps.get(0));
		}
		return "verificarQuestoesSismamaBiopsia";
	}
	
	public String verificarQuestoesSismama() {
		//Carrega todas as resposta do questionário
		if (!solicitacoes.isEmpty() && solicitacoes.size() == 1) {
			Integer iseSoeSeq = solicitacoes.keySet().iterator().next();
			Short iseSeqp = null;
			if (solicitacoes.get(iseSoeSeq).size() == 1) {
				iseSeqp = solicitacoes.get(iseSoeSeq).iterator().next();
				verificaQuestoesSismamaController.preencherQuestionario(iseSoeSeq, iseSeqp);
			}
		}
		return "verificarQuestoesSismama";
	}
	*/
	
	
	

	public boolean isExibirBotaoVoltar() {
		return exibirBotaoVoltar;
	}

	public void setExibirBotaoVoltar(boolean exibirBotaoVoltar) {
		this.exibirBotaoVoltar = exibirBotaoVoltar;
	}

	public Map<Integer, Vector<Short>> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(Map<Integer, Vector<Short>> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public String getPaginaVoltarPol() {
		return paginaVoltarPol;
	}

	public void setPaginaVoltarPol(String paginaVoltarPol) {
		this.paginaVoltarPol = paginaVoltarPol;
	}

	public boolean isBotaoImagens() {
		return botaoImagens;
	}

	public void setBotaoImagens(boolean botaoImagens) {
		this.botaoImagens = botaoImagens;
	}

	public RapServidores getServExame() {
		return servExame;
	}
	
	public void setServExame(RapServidores servExame) {
		this.servExame = servExame;
	}

	public Integer getCodigoSoeSelecionado() {
		return codigoSoeSelecionado;
	}
	
	public String getVoltarPara() {
		return voltarPara;
	}
	
	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public void setCodigoSoeSelecionado(Integer codigoSoeSelecionado) {
		this.codigoSoeSelecionado = codigoSoeSelecionado;
	}

	public Short getIseSeqSelecionado() {
		return iseSeqSelecionado;
	}

	public void setIseSeqSelecionado(Short iseSeqSelecionado) {
		this.iseSeqSelecionado = iseSeqSelecionado;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	
	public List<PesquisaExamesPacientesResultsVO> getListaResultadoPesquisa() {
		return listaResultadoPesquisa;
	}

	public void setListaResultadoPesquisa(List<PesquisaExamesPacientesResultsVO> listaResultadoPesquisa) {
		this.listaResultadoPesquisa = listaResultadoPesquisa;
	}

	public NodoPOLVO getNodoPol() {
		return nodoPol;
	}

	public void setNodoPol(NodoPOLVO nodoPol) {
		this.nodoPol = nodoPol;
	}

}