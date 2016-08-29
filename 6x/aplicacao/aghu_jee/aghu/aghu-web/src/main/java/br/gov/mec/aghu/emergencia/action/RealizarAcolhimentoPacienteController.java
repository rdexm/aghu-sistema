package br.gov.mec.aghu.emergencia.action;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.DescritorTrgGravidadeVO;
import br.gov.mec.aghu.emergencia.vo.MamTriagemVO;
import br.gov.mec.aghu.emergencia.vo.TrgGravidadeFluxogramaVO;
import br.gov.mec.aghu.model.MamFluxograma;
import br.gov.mec.aghu.model.MamItemExame;
import br.gov.mec.aghu.model.MamItemGeral;
import br.gov.mec.aghu.model.MamItemMedicacao;
import br.gov.mec.aghu.model.MamOrigemPaciente;
import br.gov.mec.aghu.model.MamTrgExames;
import br.gov.mec.aghu.model.MamTrgGerais;
import br.gov.mec.aghu.model.MamTrgGravidade;
import br.gov.mec.aghu.model.MamTrgMedicacoes;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.prescricaomedica.vo.PostoSaude;
import br.gov.mec.aghu.service.ServiceException;
/**
 * Controller das ações da pagina de protocolos e classificação de risco
 * @author israel.haas
 */
public class RealizarAcolhimentoPacienteController extends ActionController {
	private static final String ESPECIALIDADE2 = "especialidade";
	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";
	private static final long serialVersionUID = 5696509444357451854L;
	private static final Log LOG = LogFactory.getLog(RealizarAcolhimentoPacienteController.class);
	private static final Integer ABA_INTERNO = 0;
	private static final Integer ABA_EXTERNO = 1;
	private static final Integer ABA_AGUARDANDO = 2;
	private static final String TAB_INTERNO = "abaInterno";
	private static final String TAB_EXTERNO = "abaExterno";
	//selecoes para pintar linha
	private MamTrgGerais selecaoGerais;
	private MamTrgMedicacoes selecaoMedicacoes;
	private MamTrgExames selecaoExames;
	private final String REDIRECT_LISTA_PACIENTES_ACOLHIMENTO = "listaPacientesEmergencia";
	private final static String NAO_INFORMADO = "Não Informado";
	@EJB
	private IEmergenciaFacade emergenciaFacade;
	@Inject
	private ImprimirFormularioEncExternoReportGenerator imprimirEncExterno;
	@Inject
	private GerarBoletimAtendimentoReportGenerator gerarBoletimAtendimentoReportGenerator;
	@Inject
	private ListaPacientesEmergenciaPaginatorController listaPacientesEmergenciaPaginatorController;
	@Inject
	private ListarControlesPacienteController listarControlesPacienteController;
	@EJB
	private IPermissionService permissionService;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	// ----- FILTRO
	private Long trgSeq;
	private Integer pacCodigo;
	/*** DETALHE - CAMPOS ESPECIALIDADE ***/
	private Especialidade especialidade;	
	private List<Short> listaEspId;
	// PERMISSÕES
	private boolean permRealizarAcolhimento;
	private boolean permVisualizarAcolhimento;
	private boolean permEncaminhamento;
	private MamTriagemVO mamTriagemVO;
	private MamTriagemVO mamTriagemVOOriginal;
	private PostoSaude postoSaude;
	private String observacao;
	private String especialidadeSaudeExterno;
	private Integer abaSelecionada;
	private Boolean unidadeSaudeObrigatorio;
	private Boolean obsObrigatorio;
	private Boolean showModalConfirmacao = false;
	private Boolean isEncaminhamentoInterno = false;
	private Boolean isEncaminhamentoExterno = false;
	private String hostName = "Não foi possível obter o hostName.";
	// Fluxograma selecionado na suggestionbox de informarFluxograma
	private MamFluxograma mamFluxograma;
	// Controla exibição do check para checagem em lote
	private boolean permiteChecagemLote;
	// Marcação de checagem em lote
	private boolean checagemLote = false;
	// Item clicado na modal
	private DescritorTrgGravidadeVO itemSelecionado;
	//Controle de tela, utilizado para saber se deu erro na gravacao
	private boolean gravouItemSelecionado = false;
	// Lista de descritores ativos do fluxograma selecionado
	private List<DescritorTrgGravidadeVO> listaDescritores = new ArrayList<DescritorTrgGravidadeVO>();
	private List<DescritorTrgGravidadeVO> copiaListaDescritores = new ArrayList<DescritorTrgGravidadeVO>();
	// Gravidade anteriormente selecionada
	private MamTrgGravidade mamTrgGravidade;
	private MamOrigemPaciente hospitalInternado;
	private Paciente paciente;
	//#36386
	private boolean tudoDesabilitado = false;
	//#36387
	private boolean reclassificar = false;
	private MamUnidAtendem unidadeTransferencia;
	private Boolean focoCabecalho = true;
	private Boolean renderizaTableFluxograma = false;
	@PostConstruct
	public void init() {
		begin(conversation);
		setListaEspId(null);
		this.setAbaSelecionada(0);
		this.setShowModalConfirmacao(false);
	}
	public void inicio(){
		renderizaTableFluxograma = false;
		String trgSeq = super.getRequestParameter("trgSeq");
		if (StringUtils.isNotBlank(trgSeq)){
			setTrgSeq(Long.valueOf(trgSeq));
		}
		String pacCodigo = super.getRequestParameter("pacCodigo");
		if (StringUtils.isNotBlank(pacCodigo)){
			setPacCodigo(Integer.valueOf(pacCodigo));
		}
		String tudoDesabilitado = super.getRequestParameter("tudoDesabilitado");
		if (StringUtils.isNotBlank(tudoDesabilitado)){
			setTudoDesabilitado(Boolean.valueOf(tudoDesabilitado));
		} else {
			setTudoDesabilitado(Boolean.FALSE);
		}
		if(this.isTudoDesabilitado() || this.isReclassificar()){
			try {
				this.preparaTela();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} catch (ReflectiveOperationException e) {
				return;
			}
		}
	}
	
	public String manterControlesPaciente(){
		return "controlepaciente-manterRegistros";
	}

	public void preparaTela() throws ApplicationBusinessException, ReflectiveOperationException {
		setListaEspId(this.emergenciaFacade.pesquisarEspecialidadesTriagem(this.getTrgSeq()));
		try {
	    	hostName = super.getEnderecoIPv4HostRemoto().getHostName();
		} catch (UnknownHostException e) {
			apresentarMsgNegocio(Severity.ERROR,"Não foi possível obter hostName");
		}
		this.permRealizarAcolhimento = permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "realizarAcolhimento", "executar");
		this.permVisualizarAcolhimento = permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "visualizarAcolhimento", "visualizar");
		this.permEncaminhamento = (permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "realizarAcolhimento", "executar") 
				&& permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "marcarConsultaAmbulatorioZona12", "executar"));
		this.postoSaude = null;
		this.unidadeSaudeObrigatorio = true;
		this.obsObrigatorio = false;
		this.observacao = null;
		this.especialidadeSaudeExterno = null;
		this.especialidade = null;
		this.mamFluxograma = null;
		try {
			this.mamTriagemVO = this.emergenciaFacade.obterTriagemVOPorSeq(this.trgSeq, this.pacCodigo);
			if(mamTriagemVO.getSeqHospitalInternado() != null) {
				this.hospitalInternado = emergenciaFacade.obterOrigemPacientePorChavePrimaria(mamTriagemVO.getSeqHospitalInternado());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		this.setUnidadeTransferencia(mamTriagemVO.getMamUnidAtendem());
		// #29973
		carregaListasDadosGeraisExamesMedicamentos();
		listarControlesPacienteController.setPacCodigo(pacCodigo);
		listarControlesPacienteController.setTrgSeq(trgSeq);
		listarControlesPacienteController.setUnfSeq(mamTriagemVO.getUnfSeq());
		listarControlesPacienteController.buscaMonitorizacoes();
		// Faz uma cópia de MamTriagemVO para testar se algum campo será alterado.
		this.mamTriagemVOOriginal = new MamTriagemVO();
		
		PropertyUtils.copyProperties(this.mamTriagemVOOriginal, this.mamTriagemVO);
		
		this.prepararModalInformarFluxograma();
		List<Especialidade> listEspecialidade = pesquisarEspecialidadeListaSeq(null);
		if(listEspecialidade != null && listEspecialidade.size() == 1) {
			this.especialidade = listEspecialidade.get(0);
		}
		if (this.isTudoDesabilitado()) {
			super.closeDialog("modalInformarFluxogramaWG");
			//renderizaTableFluxograma = false;
		}
	}
	
	private void carregaListasDadosGeraisExamesMedicamentos() {
		this.mamTriagemVO.setListMamTrgGerais(this.emergenciaFacade.listarDadosGerais(this.trgSeq, this.hostName));
		this.mamTriagemVO.setListMamTrgExames(this.emergenciaFacade.listarExames(this.trgSeq, this.hostName));
		this.mamTriagemVO.setListMamTrgMedicacoes(this.emergenciaFacade.listarMedicacoes(this.trgSeq, this.hostName));
	}
	public Boolean verificaCampoEditavelGrade(Boolean indConsistenciaOk, Date dtHrConsistenciaOk) {
		Calendar dataMenosMeiaHora = Calendar.getInstance();
		dataMenosMeiaHora.add(Calendar.MINUTE, -30);
		if (indConsistenciaOk.equals(Boolean.TRUE)
				&& DateUtil.validaDataMenorIgual(dtHrConsistenciaOk, dataMenosMeiaHora.getTime())) {
			return true;
		}
		return false;
	}
	public List<MamOrigemPaciente> listarOrigemPaciente(String pesquisa) {
		return this.returnSGWithCount(this.emergenciaFacade.listarOrigemPaciente(pesquisa),listarOrigemPacienteCount(pesquisa));
	}
	public Long listarOrigemPacienteCount(String pesquisa) {
		return this.emergenciaFacade.listarOrigemPacienteCount(pesquisa);
	}
	public List<MamUnidAtendem> pesquisarUnidadeFuncional(String objPesquisa) {
		return  this.returnSGWithCount(this.emergenciaFacade.listarUnidadesFuncionais(objPesquisa, false, "descricao", true),pesquisarUnidadeFuncionalCount(objPesquisa));
	}
	public Long pesquisarUnidadeFuncionalCount(String objPesquisa) {
		return this.emergenciaFacade.listarUnidadesFuncionaisCount(objPesquisa, true, true);
	}
	public List<PostoSaude> listarMpmPostoSaudePorSeqDescricao(final Object parametro) throws ApplicationBusinessException {
		try {
			return this.emergenciaFacade.listarMpmPostoSaudePorSeqDescricao(parametro);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	public Long listarMpmPostoSaudePorSeqDescricaoCount(final Object parametro) throws ApplicationBusinessException {
		try {
			return this.emergenciaFacade.listarMpmPostoSaudePorSeqDescricaoCount(parametro);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	public void adicionarDadoGeral(MamItemGeral item) {
		MamTrgGerais mamTrgGerais = this.emergenciaFacade.inserirTrgGeral(this.trgSeq, item.getSeq(), this.hostName);
		this.mamTriagemVO.getListMamTrgGerais().add(mamTrgGerais);
	}
	public void excluirDadoGeral(MamTrgGerais item) {
		try {
			this.emergenciaFacade.excluirTrgGeral(item);
			this.mamTriagemVO.getListMamTrgGerais().remove(item);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	public void adicionarExame(MamItemExame item) {
		MamTrgExames mamTrgExames = this.emergenciaFacade.inserirTrgExame(this.trgSeq, item.getSeq(), this.hostName);
		this.mamTriagemVO.getListMamTrgExames().add(mamTrgExames);
	}
	public void excluirExame(MamTrgExames item) {
		try {
			this.emergenciaFacade.excluirTrgExame(item);
			this.mamTriagemVO.getListMamTrgExames().remove(item);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	public void adicionarMedicacao(MamItemMedicacao item) {
		MamTrgMedicacoes mamTrgMedicacoes = this.emergenciaFacade.inserirTrgMedicacao(this.trgSeq, item.getSeq(), this.hostName);
		this.mamTriagemVO.getListMamTrgMedicacoes().add(mamTrgMedicacoes);
	}
	public void excluirMedicacao(MamTrgMedicacoes item) {
		try {
			this.emergenciaFacade.excluirTrgMedicacao(item);
			this.mamTriagemVO.getListMamTrgMedicacoes().remove(item);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	public void validarDadosEncaminhamentoAtendimento() throws ApplicationBusinessException{
		if (validarCampoAlergia()){
			if(this.reclassificar){
				this.showModalConfirmacao = true;
				super.openDialog("modalConfirmacaoWG");
				return;
			}
			verificarTipoAtendimento();
		}
	}
	public String gravarDadosAcolhimento() throws ApplicationBusinessException {
		try {
			String mensagem = "MENSAGEM_SUCESSO_RECLASSIFICACAO_PACIENTE";
			LOG.info("EMERGENCIA1: Entrou RealizarAcolhimentoPacienteController.gravarDadosAcolhimento()");
			if(this.validaPreenchimenControlePaciente()){
				this.mamTriagemVO.setSeqHospitalInternado(this.hospitalInternado != null ? this.hospitalInternado.getSeq() : null);
				Boolean isServicoCPIndisponivel = this.emergenciaFacade.gravarDadosAcolhimento(this.mamTriagemVO, this.mamTriagemVOOriginal, true, this.hostName);
				if (!this.reclassificar && isEncaminhamentoInterno) { // 29859 && 34842
					LOG.info("EMERGENCIA1: eh encaminhamento interno");
					this.gravarEncaminhamentoInterno();
					LOG.info("EMERGENCIA1: gravou encaminhamentoInterno com sucesso");
					return retornaListaPaciente(isServicoCPIndisponivel, "MENSAGEM_SUCESSO_ACOLHIMENTO_PACIENTE", mamTriagemVO.getNomePaciente());
				} else if (!this.reclassificar && isEncaminhamentoExterno) { // 29858
					LOG.info("EMERGENCIA1: nao eh encaminhamento interno");
					this.emergenciaFacade.existeTriagem(this.trgSeq);
					encaminhaPacienteExterno();
					mensagem = "MENSAGEM_SUCESSO_ACOLHIMENTO_PACIENTE";
					return retornaListaPaciente(isServicoCPIndisponivel, "MENSAGEM_SUCESSO_ACOLHIMENTO_PACIENTE", mamTriagemVO.getNomePaciente());
				}
				return retornaListaPaciente(isServicoCPIndisponivel, mensagem, null);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	private boolean validaPreenchimenControlePaciente() {
		boolean retorno =  true;
		if (mamTriagemVO.getQueixaPrincipal() == null || NAO_INFORMADO.equals(mamTriagemVO.getQueixaPrincipal())) {
			this.apresentarMsgNegocio(ESPECIALIDADE2, Severity.ERROR, CAMPO_OBRIGATORIO, "queixa principal");
			retorno = false;
		}
		if (this.mamTriagemVO.getDataQueixa() == null && this.mamTriagemVO.getIndDataQueixaObr()){
			this.apresentarMsgNegocio(ESPECIALIDADE2, Severity.ERROR, CAMPO_OBRIGATORIO, "inicio da queixa principal");
			retorno = false;
		}
		if (this.mamTriagemVO.getHoraQueixa() == null && this.mamTriagemVO.getIndHoraQueixaObr()){
			this.apresentarMsgNegocio(ESPECIALIDADE2, Severity.ERROR, CAMPO_OBRIGATORIO, "hora da queixa principal");
			retorno = false;
		}
		if (this.mamTriagemVO.getIndInternado() == null){
			this.apresentarMsgNegocio("indInternado", Severity.ERROR, CAMPO_OBRIGATORIO, "esteve internado nos últimos 90 dias");
			retorno = false;
		} else if (this.mamTriagemVO.getIndInternado() && this.hospitalInternado == null){
			this.apresentarMsgNegocio("sbHospital", Severity.ERROR, CAMPO_OBRIGATORIO, "qual hospital");
			retorno = false;
		}
		if(this.mamTriagemVO.getDataQueixa() != null && this.mamTriagemVO.getHoraQueixa() != null) {
			Date dataHora = DateUtil.comporDiaHora(this.mamTriagemVO.getDataQueixa(), this.mamTriagemVO.getHoraQueixa());
			if(DateUtil.validaDataMaior(dataHora, new Date())) {
				this.apresentarMsgNegocio("dataQueixa", Severity.ERROR, "ERRO_INICIO_QUEIXA_MAIOR_DATA_ATUAL");
				retorno = false;
			}
		} else if(this.mamTriagemVO.getDataQueixa() != null) {
			Date horarioTruncado = DateUtil.truncaHorario(this.mamTriagemVO.getDataQueixa());
			if(DateUtil.validaDataMaior(horarioTruncado, new Date())) {
				this.apresentarMsgNegocio("dataQueixa", Severity.ERROR, "ERRO_INICIO_QUEIXA_MAIOR_DATA_ATUAL");
				retorno = false;
			}
		} else if(this.mamTriagemVO.getHoraQueixa() != null) {
			Date dataHora = DateUtil.comporDiaHora(new Date(), this.mamTriagemVO.getHoraQueixa());
			if(DateUtil.validaDataMaior(dataHora, new Date())) {
				this.apresentarMsgNegocio("dataQueixa", Severity.ERROR, "ERRO_INICIO_QUEIXA_MAIOR_DATA_ATUAL");
				retorno = false;
			}
		}
		return retorno;
	}
	public void encaminhaPacienteExterno() {
		this.verificaCodSituacaoEmergenciaDoEncExt();//aplica RN09
		Short seqp = this.emergenciaFacade.obtemUltimoSEQPDoAcolhimento(this.trgSeq);
		try{
			this.emergenciaFacade.gravarEncaminhamentoExterno(this.trgSeq, this.getEspecialidadeSaudeExterno(), this.getPostoSaude(),this.getObservacao(), this.hostName, seqp);//RN08 e grava
			DominioSimNao isImprimirFormulario = this.emergenciaFacade.obterParametroFormularioExterno();
			if (DominioSimNao.S.equals(isImprimirFormulario)) {
				imprimeBoletimEncaminhamentoExt(seqp);
			} 
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}
	private String retornaListaPaciente(Boolean isServicoCPIndisponivel, String mensagem, String param) {
		if(this.reclassificar) {
			this.listaPacientesEmergenciaPaginatorController.pesquisarPacientesAguardandoAtendimento();
		} else {
			this.listaPacientesEmergenciaPaginatorController.pesquisarPacientesAcolhimento();
		}
		this.listaPacientesEmergenciaPaginatorController.retornoAcolhimento(Severity.INFO, mensagem, param, isServicoCPIndisponivel);
		return REDIRECT_LISTA_PACIENTES_ACOLHIMENTO;
	}
	private void imprimeBoletimEncaminhamentoExt(Short seqp) {
		try {
			imprimirEncExterno.setTriagem(trgSeq);
			imprimirEncExterno.setSeqp(seqp);
			imprimirEncExterno.gerarBoletim(super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "SUCESSO_IMPRESSAO_FORMULARIO");
		} catch (UnknownHostException e) {
			super.apresentarMsgNegocio(e.getMessage());
		} catch (ServiceException e) {
			super.apresentarMsgNegocio(e.getMessage());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (JRException e) {
			super.apresentarMsgNegocio(e.getMessage());
		}
	}
	private boolean validaPreenchimentoAbaExterno() {
		if (!this.emergenciaFacade.existeTriagem(this.trgSeq)){
			this.apresentarMsgNegocio(Severity.ERROR, "MAM_PACIENTE_NAO_ESTA_EM_TRIAGEM");
			return false;
		}
		if (this.especialidadeSaudeExterno == null || especialidadeSaudeExterno.isEmpty()) {
			this.setAbaSelecionada(ABA_EXTERNO);
			this.apresentarMsgNegocio(ESPECIALIDADE2, Severity.ERROR, CAMPO_OBRIGATORIO, "Especialidade");
			return false;
		}
		if (this.unidadeSaudeObrigatorio.equals(Boolean.TRUE) && this.postoSaude == null) {
			this.setAbaSelecionada(ABA_EXTERNO);
			this.apresentarMsgNegocio("unidadeSaudeExterno", Severity.ERROR, CAMPO_OBRIGATORIO, "Unidade Saúde"); 
			return false;
		} else if (this.obsObrigatorio.equals(Boolean.TRUE) && this.observacao == null) {
			this.setAbaSelecionada(ABA_EXTERNO);
			this.apresentarMsgNegocio("observacaoAbaExterno", Severity.ERROR, CAMPO_OBRIGATORIO, "Observação"); 
			return false;
		}
		return true;
	}
	private boolean validaPreenchimentoAbaInterno() {
		if (this.especialidade == null) {
			this.setAbaSelecionada(ABA_INTERNO);
			this.apresentarMsgNegocio("agenda", Severity.ERROR, CAMPO_OBRIGATORIO, "Agenda");
			return false;
		}		
		return true;
	}
	private String realizaEncaminhamentoExternoValidandoGravidade()throws ApplicationBusinessException {
		try {
			return this.emergenciaFacade.realizaEncaminhamentoExternoValidandoGravidade(this.observacao, this.postoSaude, this.especialidadeSaudeExterno);
		} catch (ServiceException e) {
			super.apresentarMsgNegocio(e.getMessage());
		}
		return DominioSimNao.N.toString();
	} 
	public String transferirPacienteUnidade() throws ApplicationBusinessException {
		if (validarCampoAlergia() && unidadeTransferencia != null) {
			try {
				this.mamTriagemVO.setMamUnidAtendem(unidadeTransferencia);
				this.mamTriagemVO.setSeqHospitalInternado(this.hospitalInternado != null ? this.hospitalInternado.getSeq() : null);
				this.emergenciaFacade.transferirPacienteUnidade(this.mamTriagemVO, this.mamTriagemVOOriginal, this.hostName, this.paciente);
				this.listaPacientesEmergenciaPaginatorController.pesquisarPacientesAcolhimento();
				this.listaPacientesEmergenciaPaginatorController.retornoAcolhimento(Severity.INFO, "MENSAGEM_SUCESSO_TRANSFERIR_PACIENTE_UNIDADE", this.mamTriagemVO.getMamUnidAtendem().getDescricao(), false);
				return REDIRECT_LISTA_PACIENTES_ACOLHIMENTO;
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}			
		} else if (unidadeTransferencia == null) {
			this.apresentarMsgNegocio("sbUnidade", Severity.ERROR, CAMPO_OBRIGATORIO, "Unidade"); 
		}
		return null;
	}
	public String naoTransferirPacienteUnidade() throws ApplicationBusinessException {
		if (validarCampoAlergia()) {
			try {
				this.mamTriagemVO.setSeqHospitalInternado(this.hospitalInternado != null ? this.hospitalInternado.getSeq() : null);
				this.emergenciaFacade.naoTransferirPacienteUnidade(this.mamTriagemVO, this.mamTriagemVOOriginal, this.hostName);
				this.listaPacientesEmergenciaPaginatorController.pesquisarPacientesAcolhimento();
				return REDIRECT_LISTA_PACIENTES_ACOLHIMENTO;
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return null;
	}
	private boolean validarCampoAlergia() {
		if (this.mamTriagemVOOriginal.getAlergias() != null
				&& (this.mamTriagemVO.getAlergias() == null || this.mamTriagemVO.getAlergias().equals(""))) {
			this.apresentarMsgNegocio("alergias", Severity.ERROR, CAMPO_OBRIGATORIO, "Alergias");
			return false;
		}
		return true;
	}
	public String cancelar() {
		this.mamTriagemVO = new MamTriagemVO();
		if(this.reclassificar) {
			this.listaPacientesEmergenciaPaginatorController.pesquisarPacientesAguardandoAtendimento();
			this.listaPacientesEmergenciaPaginatorController.setAbaSelecionada(ABA_AGUARDANDO);
		} else {
			this.listaPacientesEmergenciaPaginatorController.pesquisarPacientesAcolhimento();
		}
		return REDIRECT_LISTA_PACIENTES_ACOLHIMENTO;
	}
	public List<Especialidade> pesquisarEspecialidadeListaSeq(String objPesquisa) {
		this.setAbaSelecionada(0);
		return returnSGWithCount(this.emergenciaFacade.pesquisarEspecialidadeListaSeq(getListaEspId(), objPesquisa), 
			this.emergenciaFacade.pesquisarEspecialidadeListaSeqCount(getListaEspId(), objPesquisa));
	}
	public List<PostoSaude> pesquisarUnidadeSaudeExterno(String objPesquisa) {
		try {
			List<PostoSaude> listaRetorno =  this.emergenciaFacade.pesquisarUnidadeSaudeExterno(objPesquisa);
			if (listaRetorno == null || listaRetorno.isEmpty()) {
				this.obsObrigatorio = true;
			}
			return listaRetorno;			
		} catch (ApplicationBusinessException e) {
			this.obsObrigatorio = true;
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	private boolean verificarTipoAtendimento() throws ApplicationBusinessException {
		this.showModalConfirmacao = false;
		if (validaMultiplosEncaminhamentos()) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_NAO_PERMITE_MULTIPLOS_ENCAMINHAMENTOS");
			return false;
		} else if (this.especialidade == null && ((this.especialidadeSaudeExterno == null || this.especialidadeSaudeExterno.isEmpty()) && this.postoSaude == null)){
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_OBRIGATORIEDADE_ENCAMINHAMENTO");
			return false;
		} else if (this.especialidade != null && ((this.especialidadeSaudeExterno == null || this.especialidadeSaudeExterno.isEmpty())|| this.postoSaude == null)){
			this.isEncaminhamentoInterno = true;
			if (validaPreenchimentoAbaInterno()){
				this.showModalConfirmacao = true;	
				super.openDialog("modalConfirmacaoWG");
			}
		} else if (this.especialidade == null && ((this.especialidadeSaudeExterno != null || 
				(this.especialidadeSaudeExterno != null && !this.especialidadeSaudeExterno.isEmpty())) || this.postoSaude != null || (this.observacao != null))){
			this.isEncaminhamentoExterno = true;
			if (validaPreenchimentoAbaExterno()){
				if(realizaEncaminhamentoExternoValidandoGravidade().equalsIgnoreCase(DominioSimNao.S.toString())) {
					this.showModalConfirmacao= true;
					super.openDialog("modalConfirmacaoWG");
				} else{
					this.apresentarMsgNegocio(Severity.ERROR, "MAM_NAO_E_POSSIVEL_ENCAMINHAR_PACIENTE_EXTERNO");
				}
			}	
		} else {
			return false;
		}
		return true;
	}
	/**
	 * SE na primeira aba, especialidade preenchida,
	 * E na segunda aba, se especialidade OU observação OU posto de saúde preenchidos
	 * Deve estourar erro na tela.
	 * @return true se as duas abas foram preenchidas (ERRO), false caso contrário.
	 */
	private boolean validaMultiplosEncaminhamentos() {
		if ((this.especialidade != null) && 
			((this.especialidadeSaudeExterno != null && !this.especialidadeSaudeExterno.isEmpty())
					|| (this.observacao != null && !this.observacao.isEmpty())
					|| this.postoSaude != null)){
				return true;
		}
		return  false;
	}
		
	public void gravarEncaminhamentoInterno(){
		LOG.info("EMERGENCIA1: entrou em RealizarAcolhimentoPacienteController.gravarEncaminhamentoInterno()");
		String hostName = null;
		try {
			hostName = this.getEnderecoIPv4HostRemoto().getHostName();
			LOG.info("EMERGENCIA1: obteve hostname");
		} catch (UnknownHostException e) {
			LOG.info("EMERGENCIA1: erro obter hostname");
			apresentarMsgNegocio(Severity.ERROR,"Não foi possivel pegar o servidor logado");
		}
		Integer numeroConsulta;
		try {
			numeroConsulta = this.emergenciaFacade.gravarEncaminhamentoInterno(this.especialidade.getSeq(), this.getTrgSeq(), hostName);
			LOG.info("EMERGENCIA1: gravou this.emergenciaFacade.gravarEncaminhamentoInterno, com a consulta: "+ numeroConsulta);
			Short unfSeq = this.emergenciaFacade.obterUnidadeAssociadaAgendaPorNumeroConsulta(numeroConsulta);
			if (this.emergenciaFacade.verificaAgendaEmergencia(unfSeq)){
				gerarBoletimAtendimentoReportGenerator.setConsulta(numeroConsulta);
				// #54032 - Não imprimir Boletim de Atendimento quando encaminhamento for interno no acolhimento
				Boolean isAcolhimentoInternoCustom = (parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_EMERG_CUSTOM) != null && parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_EMERG_CUSTOM).equals("S"));
				if (emergenciaFacade.imprimirEmitirBoletimAtendimento(unfSeq) && !isAcolhimentoInternoCustom) {
					LOG.info("EMERGENCIA1: vai chamar emergenciaFacade.imprimirEmitirBoletimAtendimento");
					imprimirBoletimEncaminhamentoInterno();
					LOG.info("EMERGENCIA1: imprimiu boletim de atendimento no encaminhamento interno");
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (ServiceException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		}
	}
	private void imprimirBoletimEncaminhamentoInterno() throws ServiceException {
		try {
			gerarBoletimAtendimentoReportGenerator.gerarBoletim(super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "SUCESSO_IMPRESSAO_BA");
		} catch (UnknownHostException e) {
			super.apresentarMsgNegocio(e.getMessage());
		} catch (ServiceException e) {
			super.apresentarMsgNegocio(e.getMessage());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (JRException e) {
			super.apresentarMsgNegocio(e.getMessage());
		}
	}
	
	public void verificarConsultasEmergencia() {
		try {
			List<Integer> consultasMarcadasELivres = this.emergenciaFacade
					.verificarConsultasEmergencia(this.especialidade.getSeq(), this.getTrgSeq());
			if (!consultasMarcadasELivres.isEmpty()) {
				apresentarMsgNegocio(Severity.INFO,	"MENSAGEM_CONSULTAS_DISPONIVEIS", consultasMarcadasELivres.get(0),
						consultasMarcadasELivres.get(1));
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void prepararModalInformarFluxograma() {
		renderizaTableFluxograma = true;
		Long trgSeq = null;
		Short unfSeq = null;
		this.checagemLote = false;
		if (this.mamTriagemVO != null) {
			trgSeq = this.mamTriagemVO.getTrgSeq();
			if (this.mamTriagemVO.getMamUnidAtendem() != null) {
				unfSeq = this.mamTriagemVO.getMamUnidAtendem().getSeq();
				this.listaPacientesEmergenciaPaginatorController.setMamUnidAtendem(this.mamTriagemVO.getMamUnidAtendem());
			}
		}
		TrgGravidadeFluxogramaVO trgGravidadeFluxogramaVO = emergenciaFacade.obterClassificacaoRiscoPaciente(trgSeq, unfSeq);
		if (trgGravidadeFluxogramaVO != null) {
			this.mamFluxograma = trgGravidadeFluxogramaVO.getFluxograma();
			this.mamTrgGravidade = trgGravidadeFluxogramaVO.getTrgGravidade();
			this.posSelectionMamFluxograma();
		} else {
			this.mamFluxograma = null;
			this.mamTrgGravidade = null;
			this.posDeleteMamFluxograma();
		}
	}
	public void prepararModalInformarFluxogramaClose(){
		prepararModalInformarFluxograma();
		renderizaTableFluxograma = false;
	}

	public List<MamFluxograma> pesquisarMamFluxograma(String param) {
		Integer protSeq = null;
		if (this.mamTriagemVO != null && this.mamTriagemVO.getMamUnidAtendem() != null
				&& this.mamTriagemVO.getMamUnidAtendem().getMamProtClassifRisco() != null) {
			protSeq = this.mamTriagemVO.getMamUnidAtendem().getMamProtClassifRisco().getSeq();
		}
		return  this.returnSGWithCount(emergenciaFacade.pesquisarFluxogramaAtivoPorProtocolo((String) param, protSeq),pesquisarMamFluxogramaCount(param));
	}
	
	public Long pesquisarMamFluxogramaCount(String param) {
		Integer protSeq = null;
		if (this.mamTriagemVO != null && this.mamTriagemVO.getMamUnidAtendem() != null
				&& this.mamTriagemVO.getMamUnidAtendem().getMamProtClassifRisco() != null) {
			protSeq = this.mamTriagemVO.getMamUnidAtendem().getMamProtClassifRisco().getSeq();
		}
		return emergenciaFacade.pesquisarFluxogramaAtivoPorProtocoloCount((String) param, protSeq);
	}

	public void posSelectionMamFluxograma() {
		this.listaDescritores = emergenciaFacade.pesquisarDescritorAtivoPorFluxogramaGravidadeAtivaTriagem(this.mamFluxograma, this.mamTrgGravidade);
		this.copiarListaDescritores(this.listaDescritores, this.copiaListaDescritores);
		this.checagemLote = false;
		this.itemSelecionado = null;
		if (this.permRealizarAcolhimento){
			validaMesmoProtocoloDaUnidadeFluxograma();
		}
		renderizaTableFluxograma = true;
	}
	
	private void validaMesmoProtocoloDaUnidadeFluxograma() {
		if (mamTriagemVO != null
				&& mamTriagemVO.getMamUnidAtendem() != null
				&& mamTriagemVO.getMamUnidAtendem().getMamProtClassifRisco() != null
				&& mamTriagemVO.getMamUnidAtendem().getMamProtClassifRisco().getSeq() != null
				&& mamTriagemVO.getMamUnidAtendem().getMamProtClassifRisco().getSeq()
							.equals(this.mamFluxograma.getProtClassifRisco().getSeq())) {
			permiteChecagemEmLote();
		}
	}
	
	private void permiteChecagemEmLote() {
		if(this.mamFluxograma.getProtClassifRisco().getIndPermiteChecagem().isAtivo()){
			this.permiteChecagemLote = true;
		} else {
			this.permiteChecagemLote = false;
		}
	}

	public void posDeleteMamFluxograma() {
		this.listaDescritores = new ArrayList<DescritorTrgGravidadeVO>();
		this.copiaListaDescritores = new ArrayList<DescritorTrgGravidadeVO>();
		this.checagemLote = false;
		this.itemSelecionado = null;
		this.permiteChecagemLote = false;
	}

	public String tempoMaximoEspera(Date tempo) {
		return emergenciaFacade.criarMensagemTempoMaximoEspera(tempo);
	}

	public void clicarItem(DescritorTrgGravidadeVO item) {
		// indice do item selecionado na lista
		int indice = listaDescritores.indexOf(item);
		// Só processa se foi alterado
		if (CoreUtil.modificados(listaDescritores.get(indice).getTrgGravidade(), copiaListaDescritores.get(indice).getTrgGravidade())) {
			if (Boolean.TRUE.equals(item.getTrgGravidade())) {
				// Foi selecionado para sim, habilita todos e seta como false
				for (int i = 0; i < listaDescritores.size(); i++) {
					if (i != indice) {
						listaDescritores.get(i).setTrgGravidade(false);
						listaDescritores.get(i).setHabilitado(true);
					}
				}
			} else {
				// Foi selecionado para não, habilita o proximo
				if (indice < listaDescritores.size() - 1) {
					// Caso usuário tenha selecionado um Radio Button 'SIM' e, após, clique no radioButton 'NÃO' para a mesma linha
					if (Boolean.TRUE.equals(copiaListaDescritores.get(indice).getTrgGravidade())) {
						// remover a seleção dos itens abaixo deste
						for (int i = indice + 1; i < listaDescritores.size(); i++) {
							listaDescritores.get(i).setTrgGravidade(null);
							listaDescritores.get(i).setHabilitado(false);
						}
					}
					// Habilitar o radioButtonGroup do primeiro item abaixo da linha corrente.
					listaDescritores.get(indice + 1).setHabilitado(true);
				}
			}
			this.copiarListaDescritores(this.listaDescritores, this.copiaListaDescritores);
		}
	}
	/**
	 * Ação ao clicar no check de checagem em lote
	 */
	public void clicarChecagemLote() {
		for (int i = 0; i < listaDescritores.size(); i++) {
			listaDescritores.get(i).setTrgGravidade(checagemLote ? false : null);
			listaDescritores.get(i).setHabilitado(checagemLote);
		}
		if (!checagemLote) {
			// Foi desmarcado checagem em lote
			listaDescritores.get(0).setHabilitado(true);
		} else {
			super.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_INFO_CHECAGEM_LOTE");
		}
		this.copiarListaDescritores(this.listaDescritores, this.copiaListaDescritores);
	}
	/**
	 * Ação do botão gravar
	 */
	public void gravarItemSelecionado() {
		this.gravouItemSelecionado = false;
		DescritorTrgGravidadeVO itemMarcadoSim = null;
		for (DescritorTrgGravidadeVO item : listaDescritores) {
			if (Boolean.TRUE.equals(item.getTrgGravidade())) {
				itemMarcadoSim = item;
				break;
			}
		}
		try {
			hostName = super.getEnderecoIPv4HostRemoto().getHostName();
		} catch (UnknownHostException e) {
			super.apresentarMsgNegocio(Severity.ERROR, "Não foi possível obter hostName");
			this.prepararModalInformarFluxograma();
			return;
		}
		try {
			emergenciaFacade.gravarMamTrgGravidade(trgSeq, itemMarcadoSim, hostName);
			this.carregaListasDadosGeraisExamesMedicamentos();
			this.mamTriagemVO.setIndDataQueixaObr(itemMarcadoSim.getDescritor().getIndDtQueixaObgt());
			this.mamTriagemVO.setIndHoraQueixaObr(itemMarcadoSim.getDescritor().getIndHrQueixaObgt());
			// 7. Remover o texto default “Não informado” no textarea da queixa principal (item 2 do quadro descritivo) se ainda for esse o texto salvo na base
			if (NAO_INFORMADO.equals(mamTriagemVO.getQueixaPrincipal())) {
				mamTriagemVO.setQueixaPrincipal("");
			}
			mamTriagemVO.setCodCor(itemMarcadoSim.getDescritor().getGravidade().getCodCor());
			// 10. Apresentar mensagem MENSAGEM_SUCESSO_SELECAO_GRAVIDADE – apresentar nome do paciente no parâmetro #1
			this.gravouItemSelecionado = true;
			super.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_SELECAO_GRAVIDADE", this.mamTriagemVO.getNomePaciente());	
			super.closeDialog("modalInformarFluxogramaWG");
			renderizaTableFluxograma = false;
		} catch (ApplicationBusinessException e) {
			this.gravouItemSelecionado = false;
			super.apresentarExcecaoNegocio(e);
		}
	}
	public void verificaCodSituacaoEmergenciaDoEncExt(){
		try {
			this.emergenciaFacade.verificaCodSituacaoEmergenciaDoEncExt(this.hostName, this.getTrgSeq());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	public void verificarIndInternado(){
		if (!this.mamTriagemVO.getIndInternado()){
			this.setHospitalInternado(null);
		}
	}
	private void copiarListaDescritores(List<DescritorTrgGravidadeVO> source, List<DescritorTrgGravidadeVO> target) {
		target.clear();
		if (source != null && !source.isEmpty()) {
			for (DescritorTrgGravidadeVO desc : source) {
				target.add(new DescritorTrgGravidadeVO(desc.getDescritor(), desc.getTrgGravidade(), desc.isHabilitado()));
			}
		}
	}
	public void tabChange(TabChangeEvent event) {
		if(event != null && event.getTab() != null) {
			if(TAB_INTERNO.equals(event.getTab().getId())) {
				abaSelecionada = ABA_INTERNO;
			} else if(TAB_EXTERNO.equals(event.getTab().getId())) {
				abaSelecionada = ABA_EXTERNO;
			}
		}
	}
	public void desativarFocoCabecalho(){
		this.focoCabecalho = false;
	}
	public Long getTrgSeq() {
		return trgSeq;
	}
	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public boolean isPermRealizarAcolhimento() {
		return permRealizarAcolhimento;
	}
	public void setPermRealizarAcolhimento(boolean permRealizarAcolhimento) {
		this.permRealizarAcolhimento = permRealizarAcolhimento;
	}
	public boolean isPermVisualizarAcolhimento() {
		return permVisualizarAcolhimento;
	}
	public void setPermVisualizarAcolhimento(boolean permVisualizarAcolhimento) {
		this.permVisualizarAcolhimento = permVisualizarAcolhimento;
	}
	public boolean isPermEncaminhamento() {
		return permEncaminhamento;
	}
	public void setPermEncaminhamento(boolean permEncaminhamento) {
		this.permEncaminhamento = permEncaminhamento;
	}
	public MamTriagemVO getMamTriagemVO() {
		return mamTriagemVO;
	}
	public void setMamTriagemVO(MamTriagemVO mamTriagemVO) {
		this.mamTriagemVO = mamTriagemVO;
	}
	public MamTriagemVO getMamTriagemVOOriginal() {
		return mamTriagemVOOriginal;
	}
	public void setMamTriagemVOOriginal(MamTriagemVO mamTriagemVOOriginal) {
		this.mamTriagemVOOriginal = mamTriagemVOOriginal;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public Especialidade getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}
	public List<Short> getListaEspId() {
		return listaEspId;
	}
	public void setListaEspId(List<Short> listaEspId) {
		this.listaEspId = listaEspId;
	}
	public MamFluxograma getMamFluxograma() {
		return mamFluxograma;
	}
	public void setMamFluxograma(MamFluxograma mamFluxograma) {
		this.mamFluxograma = mamFluxograma;
	}
	public boolean isPermiteChecagemLote() {
		return permiteChecagemLote;
	}
	public void setPermiteChecagemLote(boolean permiteChecagemLote) {
		this.permiteChecagemLote = permiteChecagemLote;
	}
	public boolean isChecagemLote() {
		return checagemLote;
	}
	public void setChecagemLote(boolean checagemLote) {
		this.checagemLote = checagemLote;
	}
	public DescritorTrgGravidadeVO getItemSelecionado() {
		return itemSelecionado;
	}
	public void setItemSelecionado(DescritorTrgGravidadeVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
	public List<DescritorTrgGravidadeVO> getListaDescritores() {
		return listaDescritores;
	}
	public void setListaDescritores(List<DescritorTrgGravidadeVO> listaDescritores) {
		this.listaDescritores = listaDescritores;
	}
	public MamTrgGravidade getMamTrgGravidade() {
		return mamTrgGravidade;
	}
	public void setMamTrgGravidade(MamTrgGravidade mamTrgGravidade) {
		this.mamTrgGravidade = mamTrgGravidade;
	}
	public PostoSaude getPostoSaude() {
		return postoSaude;
	}
	public void setPostoSaude(PostoSaude postoSaude) {
		this.postoSaude = postoSaude;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public String getEspecialidadeSaudeExterno() {
		return especialidadeSaudeExterno;
	}
	public void setEspecialidadeSaudeExterno(String especialidadeSaudeExterno) {
		this.especialidadeSaudeExterno = especialidadeSaudeExterno;
	}
	public Integer getAbaSelecionada() {
		return abaSelecionada;
	}
	public void setAbaSelecionada(Integer abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}
	public Boolean getUnidadeSaudeObrigatorio() {
		return unidadeSaudeObrigatorio;
	}
	public void setUnidadeSaudeObrigatorio(Boolean unidadeSaudeObrigatorio) {
		this.unidadeSaudeObrigatorio = unidadeSaudeObrigatorio;
	}
	public Boolean getObsObrigatorio() {
		return obsObrigatorio;
	}
	public void setObsObrigatorio(Boolean obsObrigatorio) {
		this.obsObrigatorio = obsObrigatorio;
	}
	public boolean isGravouItemSelecionado() {
		return gravouItemSelecionado;
	}
	public void setGravouItemSelecionado(boolean gravouItemSelecionado) {
		this.gravouItemSelecionado = gravouItemSelecionado;
	}
	public Boolean getShowModalConfirmacao() {
		return showModalConfirmacao;
	}
	public void setShowModalConfirmacao(Boolean showModalConfirmacao) {
		this.showModalConfirmacao = showModalConfirmacao;
	}
	public Boolean getIsEncaminhamentoInterno() {
		return isEncaminhamentoInterno;
	}
	public void setIsEncaminhamentoInterno(Boolean isEncaminhamentoInterno) {
		this.isEncaminhamentoInterno = isEncaminhamentoInterno;
	}
	public Boolean getIsEncaminhamentoExterno() {
		return isEncaminhamentoExterno;
	}
	public void setIsEncaminhamentoExterno(Boolean isEncaminhamentoExterno) {
		this.isEncaminhamentoExterno = isEncaminhamentoExterno;
	}	
	public Paciente getPaciente() {
		return paciente;
	}
	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}
	public boolean isTudoDesabilitado() {
		return tudoDesabilitado;
	}
	public void setTudoDesabilitado(boolean tudoDesabilitado) {
		this.tudoDesabilitado = tudoDesabilitado;
	}
	public boolean isReclassificar() {
		return reclassificar;
	}
	public void setReclassificar(boolean reclassificar) {
		this.reclassificar = reclassificar;
	}
	public MamOrigemPaciente getHospitalInternado() {
		return hospitalInternado;
	}
	public void setHospitalInternado(MamOrigemPaciente hospitalInternado) {
		this.hospitalInternado = hospitalInternado;
	}
	public MamUnidAtendem getUnidadeTransferencia() {
		return unidadeTransferencia;
	}
	public void setUnidadeTransferencia(MamUnidAtendem unidadeTransferencia) {
		this.unidadeTransferencia = unidadeTransferencia;
	}
	public MamTrgGerais getSelecaoGerais() {
		return this.selecaoGerais;
	}
	public void setSelecaoGerais(MamTrgGerais selecaoGerais) {
		this.selecaoGerais = selecaoGerais;
	}
	public MamTrgExames getSelecaoExames() {
		return this.selecaoExames;
	}
	public void setSelecaoExames(MamTrgExames selecaoExames) {
		this.selecaoExames = selecaoExames;
	}
	public MamTrgMedicacoes getSelecaoMedicacoes() {
		return this.selecaoMedicacoes;
	}
	public void setSelecaoMedicacoes(MamTrgMedicacoes selecaoMedicacoes) {
		this.selecaoMedicacoes = selecaoMedicacoes;
	}
	public Boolean getFocoCabecalho() {
		return focoCabecalho;
	}
	public Boolean getRenderizaTableFluxograma() {
		return renderizaTableFluxograma;
	}
	public void setRenderizaTableFluxograma(Boolean renderizaTableFluxograma) {
		this.renderizaTableFluxograma = renderizaTableFluxograma;
	}
}