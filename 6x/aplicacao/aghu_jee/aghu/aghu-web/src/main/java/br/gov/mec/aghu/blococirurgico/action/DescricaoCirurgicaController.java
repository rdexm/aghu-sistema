package br.gov.mec.aghu.blococirurgico.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcAvalPreSedacao;
import br.gov.mec.aghu.model.MbcAvalPreSedacaoId;
import br.gov.mec.aghu.model.MbcDescricaoItens;
import br.gov.mec.aghu.model.MbcDescricaoTecnicas;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioDescricaoCirurgiaController;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AvaliacaoPreSedacaoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class DescricaoCirurgicaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(DescricaoCirurgicaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -5078428678012061717L;
	
	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	private final Integer TAB_0=0, TAB_2=2, TAB_6=6, TAB_7=7, TAB_8=8; 
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	//@EJB
	private CirurgiaVO crgSelecionada;

	//@Out(required = false, scope = ScopeType.SESSION)
	private DescricaoCirurgicaVO descricaoCirurgicaVO;

	@Inject
	private DescricaoCirurgicaEquipeController descricaoCirurgicaEquipeController;
	
	@Inject
	private DescricaoCirurgicaDiagnosticoController descricaoCirurgicaDiagnosticoController;
	
	@Inject
	private DescricaoCirurgicaAchadosOperatoriosController descricaoCirurgicaAchadosOperatoriosController; 	
	
	@Inject
	private DescricaoCirurgicaAvalPreSedacaoController descricaoCirurgicaAvalPreSedacaoController;
	
	@Inject
	private DescricaoCirurgicaCirRealizadaController descricaoCirurgicaCirRealizadaController; 

	@Inject
	private DescricaoCirurgicaTecnicaController descricaoCirurgicaTecnicaController;

	@Inject
	private DescricaoCirurgicaNotaAdicionalController descricaoCirurgicaNotaAdicionalController; 
	
	@Inject
	private	RelatorioDescricaoCirurgiaController relatorioDescricaoCirurgiaController;
	
	@Inject
	private ListarCirurgiasController listarCirurgiasController;
	
	@Inject
	private DescricaoCirurgicaMateriaisConsumidosController descricaoCirurgicaMateriaisConsumidosController;
	
	private Integer abaSelecionada;
	
	private Integer abaAnterior;
	
	// Parametros de page.xml
	private Integer dcgCrgSeq;
	private Short dcgSeqp;
	private Short unfSeq;
	private Integer cidSeqPre;
	private Integer cidSeqPos;	
	private boolean showNotaAdicional;
	private boolean disparaGravar;

	private boolean prazoEdicaoEstourado;
	private AvaliacaoPreSedacaoVO avaliacaoPreSedacaoVO;
	private MbcAvalPreSedacaoId id;
	private MbcAvalPreSedacao avalPreSedacao;
	private ProfDescricaoCirurgicaVO executorSedacao;
	private String descAvaliacaoClinica;
	private Integer dcgCrgSeqAux;
	private Short dcgSeqpAux;
	private MbcAvalPreSedacaoId idAux;
	private Boolean habilitarAbaOPMe;
	private boolean focusPrincipal;
	private Boolean isEdicao;
	
	private final String PAGE_BLOCO_LISTA_CIRURGIAS = "listarCirurgias";
	
	public void iniciar() {
		if(cidSeqPos != null || cidSeqPre != null){
			descricaoCirurgicaDiagnosticoController.setarParametros(cidSeqPre, cidSeqPos);
			cidSeqPos = null;
			cidSeqPre = null;
		} else {
			Integer pacCodigo = crgSelecionada.getPacCodigo();
			AipPacientes paciente = null;
			if (pacCodigo != null) {
				paciente = pacienteFacade.obterPacientePorCodigo(pacCodigo);
			}
			
			// VO utilizado no cabeçalho da Descrição Cirúrgica
			descricaoCirurgicaVO = new DescricaoCirurgicaVO();
			if (paciente != null) {
				StringBuilder sbDescricaoPaciente = new StringBuilder(150);
				Integer prontuario = paciente.getProntuario();
				if (prontuario != null) {
					sbDescricaoPaciente.append(prontuario.toString())
					.append(' ');
				}
				sbDescricaoPaciente.append(paciente.getNome())
				.append(' ')
				.append(blocoCirurgicoFacade.obterIdadePorDataNascimento(paciente
						.getDtNascimento()));
				
				descricaoCirurgicaVO.setDescricaoPaciente(sbDescricaoPaciente.toString());		
				descricaoCirurgicaVO.setProntuario(prontuario);
				descricaoCirurgicaVO.setPacCodigo(paciente.getCodigo());
			}
			
			descricaoCirurgicaVO.setLeito(crgSelecionada.getLeito());
			descricaoCirurgicaVO.setDataCirurgia(crgSelecionada.getCrgData());
			descricaoCirurgicaVO.setDescricaoEspecialidade( crgSelecionada.getSiglaEspecialidade() + " - "+ 
														    crgSelecionada.getNomeEspecialidade()
														   );
			
			if (unfSeq != null) {
				AghUnidadesFuncionais unidadeFuncional = aghuFacade.obterUnidadeFuncional(unfSeq);	
				descricaoCirurgicaVO.setUnidadeFuncional(unidadeFuncional);
			}
			
			// Seta os atributos de Id da instância de MbcDescricaoCirurgica criada anteriormente na Lista de Cirurgicas 
			descricaoCirurgicaVO.setDcgCrgSeq(dcgCrgSeq);
			descricaoCirurgicaVO.setDcgSeqp(dcgSeqp);

			try{
				
				showNotaAdicional = blocoCirurgicoFacade.habilitarNotasAdicionais(dcgCrgSeq, dcgSeqp);
				
				
				
			} catch (ApplicationBusinessException e) {
				if(e.getCode().toString().equals("ERRO_PRAZO_EDICAO_DESCRICAO_CIRURGICA_VENCIDO")){
					showNotaAdicional = true;
					prazoEdicaoEstourado = true;
					renderAbas();
				}
				
				apresentarExcecaoNegocio(e);
				
			}
		}
		
		setFocusPrincipal(true);
		habilitarAbaOPMe = true;
		if(crgSelecionada != null){
			if(crgSelecionada.getEspSeq() != null){
				List<MbcGrupoAlcadaAvalOpms> alcadas = this.blocoCirurgicoFacade.listarGrupoAlcadaFiltro(null,
						this.aghuFacade.obterAghEspecialidadesPorChavePrimaria(crgSelecionada.getEspSeq()), 
						null, 
						null, 
						null,
						DominioSituacao.A);
				 
			    //desabilita aba OPME
			    if(alcadas != null){
			    	if(alcadas.size()>0){
			    		habilitarAbaOPMe = false;
			    	}
			    }
			}
		}
		renderAbas();
		
	}
	

	public String visualizarDescricaoCirurgicaOuPDT() {
		
		this.id = new MbcAvalPreSedacaoId(descricaoCirurgicaVO.getDcgCrgSeq(), descricaoCirurgicaVO.getDcgSeqp());
		this.avalPreSedacao = blocoCirurgicoFacade.pesquisarMbcAvalPreSedacaoPorDdtSeq(id);
		
		obterDadosAvalPreSedacao(avalPreSedacao, executorSedacao);
		
		listarCirurgiasController.setAvaliacaoPreSedacaoVO(avaliacaoPreSedacaoVO);
		return listarCirurgiasController.visualizarDescricaoCirurgicaOuPDT(crgSelecionada);
	}

	public boolean habilitaBotoesDescricaoCirurgica(boolean permissaoDescricaoCirurgica, boolean permissaoDescricaoCirurgicaPDT){
		return listarCirurgiasController.habilitaBotoesDescricaoCirurgica(crgSelecionada.isOutraDescricao(), permissaoDescricaoCirurgica, permissaoDescricaoCirurgicaPDT);
	}
	
	public String cancelarDescricaoCirurgica(){
		try {
			blocoCirurgicoFacade.desfazCarregamentoDescricaoCirurgica(descricaoCirurgicaVO.getDcgCrgSeq(), descricaoCirurgicaVO.getDcgSeqp());
			limparTipoAnestesiaAbaCrgRealizada();
			return PAGE_BLOCO_LISTA_CIRURGIAS;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
		return null;
	}
	
	public void visualizarDescricaoCirurgica(){
		disparaGravar = true;
		setAbaSelecionada(TAB_8);
		renderAbas();
		if(disparaGravar){
			substituirDescricaoTecnica();
		}	
	}
	
	public String concluirDescricaoCirurgica() {
		
		if (this.validarAbasCamposObrigatorios()) {
			try {

				String nomeMicrocomputador = null;
				MbcDescricaoItens descricaoItens = null;
				String descricaoTecnicadesc = null;
				boolean	isPrintDescCirg = false;

				final RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(obterLoginUsuarioLogado());

				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e1) {
					LOG.error("Exceção capturada:", e1);
				}

				if (descricaoCirurgicaVO.getDcgCrgSeq()!=null && descricaoCirurgicaVO.getDcgSeqp()!=null){
					descricaoItens = blocoCirurgicoFacade.buscarDescricaoItens(dcgCrgSeq, dcgSeqp);
					
					MbcDescricaoTecnicas descricaoTecnica = blocoCirurgicoFacade.buscarDescricaoTecnicas(dcgCrgSeq, dcgSeqp);
					if(descricaoTecnica != null){
						descricaoTecnicadesc = descricaoTecnica.getDescricaoTecnica();
					}
					
				if (descricaoItens!=null){
						isPrintDescCirg =  blocoCirurgicoFacade.pFinalizaDescricao(descricaoCirurgicaVO.getDcgCrgSeq(), 
								descricaoCirurgicaVO.getDcgSeqp(), nomeMicrocomputador, 
								descricaoCirurgicaAchadosOperatoriosController.getIndIntercorrencia(), 
								descricaoCirurgicaAchadosOperatoriosController.getIndPerdaSangue(),
								descricaoCirurgicaAchadosOperatoriosController.getIntercorrenciaClinica(),
								descricaoCirurgicaAchadosOperatoriosController.getVolumePerdaSangue(), 
								descricaoItens.getAchadosOperatorios(),
								descricaoTecnicadesc);
					}
					else {
						isPrintDescCirg =  blocoCirurgicoFacade.pFinalizaDescricao(descricaoCirurgicaVO.getDcgCrgSeq(), 
								descricaoCirurgicaVO.getDcgSeqp(), nomeMicrocomputador, 
								descricaoCirurgicaAchadosOperatoriosController.getIndIntercorrencia(), 
								descricaoCirurgicaAchadosOperatoriosController.getIndPerdaSangue(),
								descricaoCirurgicaAchadosOperatoriosController.getIntercorrenciaClinica(),
								descricaoCirurgicaAchadosOperatoriosController.getVolumePerdaSangue(),
								descricaoCirurgicaAchadosOperatoriosController.getAchadosOperatorios(),
								descricaoCirurgicaTecnicaController.getDescricaoTecnica());
					}

				} else {
						isPrintDescCirg =  blocoCirurgicoFacade.pFinalizaDescricao(descricaoCirurgicaVO.getDcgCrgSeq(), 
							descricaoCirurgicaVO.getDcgSeqp(), nomeMicrocomputador, 
							descricaoCirurgicaAchadosOperatoriosController.getIndIntercorrencia(), 
							descricaoCirurgicaAchadosOperatoriosController.getIndPerdaSangue(),
							descricaoCirurgicaAchadosOperatoriosController.getIntercorrenciaClinica(),
							descricaoCirurgicaAchadosOperatoriosController.getVolumePerdaSangue(),
							descricaoCirurgicaAchadosOperatoriosController.getAchadosOperatorios(),
							descricaoCirurgicaTecnicaController.getDescricaoTecnica());
				}

				if(isPrintDescCirg){
					relatorioDescricaoCirurgiaController.setCrgSeq(descricaoCirurgicaVO.getDcgCrgSeq());
					relatorioDescricaoCirurgiaController.setSeqpMbcDescCrg(descricaoCirurgicaVO.getDcgSeqp());
					relatorioDescricaoCirurgiaController.setCirurgia(this.blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(descricaoCirurgicaVO.getDcgCrgSeq()));
					relatorioDescricaoCirurgiaController.directPrint();
				}
				
				
				//Verifica se o usuario tem permissao de assinar

				final boolean vHabilitaCertif = certificacaoDigitalFacade.verificaAssituraDigitalHabilitada();
				// Verifica se o usuário tem  assinatura digital de documentos AGH RN002
				final List<RapServidores> servidores = certificacaoDigitalFacade.pesquisarServidorComCertificacaoDigital(servidorLogado.getPessoaFisica().getNome());
		

				if(vHabilitaCertif && (servidores!=null && servidores.size()>0)){
					relatorioDescricaoCirurgiaController.geraPendenciasCertificacaoDigital();
				}
				
				//#42268
				if(certificacaoDigitalFacade.verificaProfissionalHabilitado()){
					relatorioDescricaoCirurgiaController.geraPendenciasCertificacaoDigital();
				}
				
				limparTipoAnestesiaAbaCrgRealizada();
				
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_CONCLUSAO_DESCRICAO_CIRURGICA");
				
				listarCirurgiasController.setDispararPesquisa(true);
				return PAGE_BLOCO_LISTA_CIRURGIAS;
			} catch (BaseException e) {			
				apresentarExcecaoNegocio(e);
			} catch (BaseRuntimeException e) {			
				apresentarExcecaoNegocio(e);
			}
		}
		return null;
	}

	private void limparTipoAnestesiaAbaCrgRealizada() {
		descricaoCirurgicaCirRealizadaController.setTipoAnestesia(null);		
	}

	public void onTabChange(TabChangeEvent event) {
		String abaSelecionada = event.getTab().getId();
		if(StringUtils.isNotBlank(abaSelecionada)) {
			Integer indiceAbaSelecionada = Integer.valueOf(StringUtils.replace(abaSelecionada, "aba", ""));
			setAbaSelecionada(indiceAbaSelecionada);
			
			/*if (TAB_3 == (getAbaAnterior()) && !(TAB_3 == (getAbaSelecionada()))) {
				descricaoCirurgicaAvalPreSedacaoController.gravarAvalPreSedacao();
			}*/
			
			//Melhoria  #53251 - Ao sair da da aba Cirurgia Realizada, notificar o usuario se ultrapassa o procedimento ultrapassa o dobro do tempo minimo
			if(TAB_2.equals(getAbaAnterior())) {
				verificaUltrapassaTempoMinimo();
			}
		//	renderAbas();
			atualizarAbaAnterior();
		}
	}
	
	
	private void verificaUltrapassaTempoMinimo() {
		Short tempoMinimo = getTempoMinimo();
		Date dataInicio = getDataInicio();
		Date dataFim = getDataFim();

		if(tempoProcedimentoUltrapassaDobroMinino(dataInicio, dataFim, tempoMinimo)) {
			super.openDialog("modalConfirmarUltrapassaTempoMinimo");
		}
	}
	
	private Short getTempoMinimo() {
		MbcProcedimentoCirurgicos procedimentoCirurgico = getProcedimentoCirurgico();
		if (procedimentoCirurgico != null) {
			return procedimentoCirurgico.getTempoMinimo();
		}
		return null;
	}
	
	private MbcProcedimentoCirurgicos getProcedimentoCirurgico() {
		MbcProcedimentoCirurgicos procedimentoCirurgico = null;
		
		if(descricaoCirurgicaCirRealizadaController.getProcDescricoes() != null && !descricaoCirurgicaCirRealizadaController.getProcDescricoes().isEmpty()) {
			procedimentoCirurgico = descricaoCirurgicaCirRealizadaController.getProcDescricoes().get(0).getProcedimentoCirurgico();
		}
		
		return procedimentoCirurgico;
	}
	
	private Date getDataInicio() {
		if(descricaoCirurgicaCirRealizadaController.getDescricaoItem() != null) {
			return descricaoCirurgicaCirRealizadaController.getDescricaoItem().getDthrInicioCirg();
		}
		return null;
	}

	private Date getDataFim() {
		if(descricaoCirurgicaCirRealizadaController.getDescricaoItem() != null) {
			return descricaoCirurgicaCirRealizadaController.getDescricaoItem().getDthrFimCirg();
		}
		return null;
	}

	private boolean tempoProcedimentoUltrapassaDobroMinino(Date dataInicioCirurgia, Date dataFimCirurgia, Short tempoMinimo) {
		return blocoCirurgicoFacade.validarTempoMinimoCirurgia(dataInicioCirurgia, dataFimCirurgia, tempoMinimo);
	}

	/**
	 * Identifica a tab selecionada e executa o metodo de render desta tab.<br>
	 * Utiliza a variavel <code>currentTabIndex</code>.
	 */
	public void renderAbas() {
		
		this.initFlagAbas();
		
		if (descricaoCirurgicaTecnicaController.getDescricaoTecnica()!=null 
				&& descricaoCirurgicaTecnicaController.getDescricaoTecnica().length()>4000){
			this.setAbaSelecionada(TAB_6);
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_DESCRICAO_TECNICA_MAIOR");
			disparaGravar = false;
		}

		descricaoCirurgicaEquipeController.iniciar(descricaoCirurgicaVO);
		descricaoCirurgicaDiagnosticoController.iniciar(descricaoCirurgicaVO);
		descricaoCirurgicaCirRealizadaController.iniciar(descricaoCirurgicaVO);
		descricaoCirurgicaAvalPreSedacaoController.iniciar(descricaoCirurgicaVO);
		descricaoCirurgicaAchadosOperatoriosController.iniciar(descricaoCirurgicaVO);
		descricaoCirurgicaTecnicaController.setEspecialidade(this.aghuFacade.obterAghEspecialidadesPorChavePrimaria(crgSelecionada.getEspSeq()));
		descricaoCirurgicaTecnicaController.iniciar(descricaoCirurgicaVO);
		descricaoCirurgicaNotaAdicionalController.iniciar(descricaoCirurgicaVO);
		relatorioDescricaoCirurgiaController.setCrgSeq(descricaoCirurgicaVO.getDcgCrgSeq());
		relatorioDescricaoCirurgiaController.setSeqpMbcDescCrg(descricaoCirurgicaVO.getDcgSeqp());
		relatorioDescricaoCirurgiaController.inicio();
		descricaoCirurgicaMateriaisConsumidosController.setCirurgiaSeq(descricaoCirurgicaVO.getDcgCrgSeq());
		descricaoCirurgicaMateriaisConsumidosController.iniciar();
	}
	
	private void atualizarAbaAnterior(){
		this.setAbaAnterior(getAbaSelecionada());
	}

	public boolean validarAbasCamposObrigatorios() {
		boolean validou = true;
		
		validou = descricaoCirurgicaAvalPreSedacaoController.validarCamposObrigatorios();
		validou = descricaoCirurgicaAchadosOperatoriosController.validarCamposObrigatorios();
		validou = descricaoCirurgicaTecnicaController.validarCamposObrigatorios();
		
		return validou;
	}
	
	
	public void substituirDescricaoTecnica(){
		descricaoCirurgicaTecnicaController.gravarDescricaoTecnica();
	//	renderAbas();
	}
	
	public void adicionarDescricaoTecnica(){
		descricaoCirurgicaTecnicaController.adicionarDescricao();
		renderAbas();
	}
	
	/**
	 * Marca todas as abas como nao selecionadas.
	 */
	private void initFlagAbas() {
		/* Coloca aba 'Notas Adicionais' em foco ao entrar na tela, caso habilitada */
		if (getAbaSelecionada() == null && showNotaAdicional) {
			setAbaSelecionada(TAB_7);
		}
		if (getAbaSelecionada() == null) {
			setAbaSelecionada(TAB_0);
		}
	}
	
	public Boolean isConclusao() {
		return TAB_8 == (getAbaSelecionada());
	}
	
	public Boolean isNotaAdicional() {
		return TAB_7 == (getAbaSelecionada());
	}
	
	public Boolean isDescTecnica() {
		return TAB_6 == (getAbaSelecionada());
	}

	public boolean isShowNotaAdicional() {
		return showNotaAdicional;
	}

	
	public void obterDadosAvalPreSedacao(MbcAvalPreSedacao avalPreSedacao, ProfDescricaoCirurgicaVO executorSedacao){
		this.avaliacaoPreSedacaoVO = new AvaliacaoPreSedacaoVO();
		
		if(avalPreSedacao != null){
			
			if(avalPreSedacao.getViaAereas() != null && avalPreSedacao.getViaAereas().getdescricao() != null){
				avaliacaoPreSedacaoVO.setDescViaAereas(avalPreSedacao.getViaAereas().getdescricao());
			}
			if(avalPreSedacao.getAsa() != null){
				avaliacaoPreSedacaoVO.setAsa(avalPreSedacao.getAsa());
			}
			if(avalPreSedacao.getAvaliacaoClinica() != null){
				avaliacaoPreSedacaoVO.setAvaliacaoClinica(avalPreSedacao.getAvaliacaoClinica());
			}
			if(avalPreSedacao.getComorbidades() != null){
				avaliacaoPreSedacaoVO.setComorbidades(avalPreSedacao.getComorbidades());
			}
			if(avalPreSedacao.getExameFisico() != null){
				avaliacaoPreSedacaoVO.setExameFisico(avalPreSedacao.getExameFisico());
			}
		
		if(executorSedacao != null){
			if(executorSedacao.getNome() != null){
					avaliacaoPreSedacaoVO.setNome(executorSedacao.getNome());
				}
			if(executorSedacao.getNroRegConselho() != null){
					avaliacaoPreSedacaoVO.setNroRegConselho(executorSedacao.getNroRegConselho());
				}
			}
			
		if(avalPreSedacao.getIndParticAvalCli()){
				this.setDescAvaliacaoClinica("SEM PARTICULARIDADES");
				avaliacaoPreSedacaoVO.setAvaliacaoClinica(this.descAvaliacaoClinica);
		}
	}
}

	public void setShowNotaAdicional(boolean showNotaAdicional) {
		this.showNotaAdicional = showNotaAdicional;
	}
	public DescricaoCirurgicaVO getDescricaoCirurgicaVO() {
		return descricaoCirurgicaVO;
	}

	public void setDescricaoCirurgicaVO(DescricaoCirurgicaVO descricaoCirurgicaVO) {
		this.descricaoCirurgicaVO = descricaoCirurgicaVO;
	}

	public  Integer getAbaSelecionada() {
		return abaSelecionada;
	}

	public void setAbaSelecionada(Integer abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}

	public CirurgiaVO getCrgSelecionada() {
		return crgSelecionada;
	}

	public void setCrgSelecionada(CirurgiaVO crgSelecionada) {
		this.crgSelecionada = crgSelecionada;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Integer getCidSeqPre() {
		return cidSeqPre;
	}

	public void setCidSeqPre(Integer cidSeqPre) {
		this.cidSeqPre = cidSeqPre;
	}

	public Integer getCidSeqPos() {
		return cidSeqPos;
	}

	public void setCidSeqPos(Integer cidSeqPos) {
		this.cidSeqPos = cidSeqPos;
	}

	public Integer getDcgCrgSeq() {
		return dcgCrgSeq;
	}

	public void setDcgCrgSeq(Integer dcgCrgSeq) {
		this.dcgCrgSeq = dcgCrgSeq;
	}

	public Short getDcgSeqp() {
		return dcgSeqp;
	}

	public void setDcgSeqp(Short dcgSeqp) {
		this.dcgSeqp = dcgSeqp;
	}


	public boolean isPrazoEdicaoEstourado() {
		return prazoEdicaoEstourado;
	}


	public void setPrazoEdicaoEstourado(boolean prazoEdicaoEstourado) {
		this.prazoEdicaoEstourado = prazoEdicaoEstourado;
	}


	public Integer getAbaAnterior() {
		return abaAnterior;
	}


	public void setAbaAnterior(Integer abaAnterior) {
		this.abaAnterior = abaAnterior;
	}
	
	public boolean isDisparaGravar() {
		return disparaGravar;
	}


	public void setDisparaGravar(boolean disparaGravar) {
		this.disparaGravar = disparaGravar;
	}


	public DescricaoCirurgicaMateriaisConsumidosController getDescricaoCirurgicaMateriaisConsumidosController() {
		return descricaoCirurgicaMateriaisConsumidosController;
}


	public void setDescricaoCirurgicaMateriaisConsumidosController(
			DescricaoCirurgicaMateriaisConsumidosController descricaoCirurgicaMateriaisConsumidosController) {
		this.descricaoCirurgicaMateriaisConsumidosController = descricaoCirurgicaMateriaisConsumidosController;
	}


	public AvaliacaoPreSedacaoVO getAvaliacaoPreSedacaoVO() {
		return avaliacaoPreSedacaoVO;
	}

	public void setAvaliacaoPreSedacaoVO(AvaliacaoPreSedacaoVO avaliacaoPreSedacaoVO) {
		this.avaliacaoPreSedacaoVO = avaliacaoPreSedacaoVO;
	}

	public MbcAvalPreSedacaoId getId() {
		return id;
	}

	public void setId(MbcAvalPreSedacaoId id) {
		this.id = id;
	}

	public MbcAvalPreSedacao getAvalPreSedacao() {
		return avalPreSedacao;
	}

	public void setAvalPreSedacao(MbcAvalPreSedacao avalPreSedacao) {
		this.avalPreSedacao = avalPreSedacao;
	}

	public ProfDescricaoCirurgicaVO getExecutorSedacao() {
		return executorSedacao;
	}

	public void setExecutorSedacao(ProfDescricaoCirurgicaVO executorSedacao) {
		this.executorSedacao = executorSedacao;
	}

	public String getDescAvaliacaoClinica() {
		return descAvaliacaoClinica;
	}

	public void setDescAvaliacaoClinica(String descAvaliacaoClinica) {
		this.descAvaliacaoClinica = descAvaliacaoClinica;
	}

	public Integer getDcgCrgSeqAux() {
		return dcgCrgSeqAux;
	}

	public void setDcgCrgSeqAux(Integer dcgCrgSeqAux) {
		this.dcgCrgSeqAux = dcgCrgSeqAux;
	}

	public Short getDcgSeqpAux() {
		return dcgSeqpAux;
	}

	public void setDcgSeqpAux(Short dcgSeqpAux) {
		this.dcgSeqpAux = dcgSeqpAux;
	}

	public MbcAvalPreSedacaoId getIdAux() {
		return idAux;
	}

	public void setIdAux(MbcAvalPreSedacaoId idAux) {
		this.idAux = idAux;
	}
								
	public boolean isFocusPrincipal() {
		return focusPrincipal;
	}
	
	public void setFocusPrincipal(boolean focusPrincipal) {
		this.focusPrincipal = focusPrincipal;
	}		
		
	public void focusSetFalse(){
		setFocusPrincipal(false);
	}

	public Boolean getHabilitarAbaOPMe() {
		return habilitarAbaOPMe;
	}


	public void setHabilitarAbaOPMe(Boolean habilitarAbaOPMe) {
		this.habilitarAbaOPMe = habilitarAbaOPMe;
	}


	public Boolean getIsEdicao() {
		return isEdicao;
	}


	public void setIsEdicao(Boolean isEdicao) {
		this.isEdicao = isEdicao;
	}

	
	
}
