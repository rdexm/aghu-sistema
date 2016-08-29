package br.gov.mec.aghu.internacao.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jfree.util.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoDadosAltaPaciente;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.business.vo.FlagsValidacaoDadosAltaPacienteVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.DadosAltaSumarioVO;
import br.gov.mec.aghu.internacao.vo.VerificaPermissaoVO;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.HostRemotoCache;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class DadosAltaPacienteController extends ActionController {

	private static final long serialVersionUID = 6165678724204331032L;

	private static final String PAGE_PESQUISAR_CENSO_DIARIO_PACIENTES = "internacao-pesquisarCensoDiarioPacientes";
	private static final String PAGE_PESQUISAR_PACIENTE_INTERNADO_DETALHES = "internacao-pesquisarPacienteInternadoDetalhes";
	private static final String PAGE_PESQUISAR_PACIENTE_INTERNADO = "internacao-pesquisarPacienteInternado";
	private static final String PAGE_PESQUISA_LEITOS = "internacao-pesquisaLeito";
	private static final String PAGE_PESQUISAR_PACIENTE_ALTA = "darAltaPaciente";
	private static final String PESQUISAR_CENSO_DIARIO_PACIENTES = "pesquisarCensoDiarioPacientes";

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	@EJB
	private IInternacaoFacade internacaoFacade;
	@Inject
	private HostRemotoCache hostRemoto;

	private String cameFrom = "";

	// Usado na suggestion
	private AinTiposAltaMedica tipoAltaMedica;
	// Usado na suggestion
	private AghInstituicoesHospitalares instituicaoHospitalar;

	private DominioTipoDadosAltaPaciente tipoDadoAltaPaciente;
	private AinInternacao internacao;
	private Integer intSeq; // seq da internacao vindo das integracoes com
							// outras telas

	private Integer prontuario;
	private String nomePaciente;

	private Date dthrAltaMedica;
	private Date dtSaidaPaciente;
	private Integer docObito;

	private String descPlanoPosAlta;
	private boolean exibirDescPlanoPosAlta;
	private boolean liberarCampoTransferencia;

	private boolean readOnlyEdicaoDthrAltaMedica;
	private boolean readOnlyEdicaoTipoAltaMedica;
	private boolean disabledBotao;

	// Flags para verificar se os valores vieram preenchidos do BD.
	private boolean dtSaidaPacBD = false;
	private boolean dthrAltaMedicaBD = false;
	private boolean tipoAltaMedicaBD = false;
	private boolean exibeMensagem = true;
	private String labelBotao = WebUtil.initLocalizedMessage("LABEL_REGISTRAR_DATA_ALTA_SAIDA_PAC", null);

	// Flags para controle de exibicao das modais
	private FlagsValidacaoDadosAltaPacienteVO flagsValidacaoDadosAltaPacienteVO = new FlagsValidacaoDadosAltaPacienteVO();

	// Mensagem exibicao modal
	private String mensagemExibicaoModal = "";
	private boolean exibirModalAux = true;

	@PostConstruct
	public void init() {
		begin(conversation);
		internacao = new AinInternacao();
	}

	public void inicio() {
	 

		if (exibeMensagem) {
			this.exibirDescPlanoPosAlta = false;
			this.tipoAltaMedica = null;
			this.exibirModalAux = false;
			this.flagsValidacaoDadosAltaPacienteVO.setValidaDados(true);
			this.flagsValidacaoDadosAltaPacienteVO.setValidaDadosInformadosEstorno(true);
			this.flagsValidacaoDadosAltaPacienteVO.setValidaDadosInformados(true);
			this.flagsValidacaoDadosAltaPacienteVO.setValidaDadosFaturamento(true);
			this.flagsValidacaoDadosAltaPacienteVO.setValidaDadosDaAltaPaciente(true);
			this.flagsValidacaoDadosAltaPacienteVO.setValidaPermissoesDarAltaPaciente(true);
			try {
				if (intSeq != null) {
					internacao = this.internacaoFacade.obterInternacaoPorChavePrimaria(this.intSeq,
							new Enum[] { AinInternacao.Fields.PACIENTE },
							new Enum[] { AinInternacao.Fields.INSTITUICAO_HOSPITALAR_TRANSFERENCIA });
				}
				this.disabledBotao = true;
				this.readOnlyEdicaoTipoAltaMedica = true;
				this.liberarCampoTransferencia = true;
				
				this.nomePaciente = this.internacao.getPaciente().getNome();
				this.prontuario = this.internacao.getPaciente().getProntuario();

				if (this.internacao.getDthrAltaMedica() == null) {
					this.iniciarAltaMedica();
				} else {
					if (this.internacao.getIndAltaManual() == null || this.internacao.getIndAltaManual().equals(DominioSimNao.S)) {
						this.tipoDadoAltaPaciente = DominioTipoDadosAltaPaciente.M;// Alta
																					// Manual
					} else {
						this.tipoDadoAltaPaciente = DominioTipoDadosAltaPaciente.P;// Prescricao
																					// Medica
					}

					if (DominioSimNao.S.equals(this.internacao.getIndAltaManual())) {
						this.readOnlyEdicaoTipoAltaMedica = false;
						this.readOnlyEdicaoDthrAltaMedica = false;
					} else {
						this.readOnlyEdicaoTipoAltaMedica = true;
						this.readOnlyEdicaoDthrAltaMedica = true;
					}
					if (this.internacao.getDtSaidaPaciente() == null) {
						// this.labelBotao = "Registrar saída do paciente";
						this.labelBotao = WebUtil.initLocalizedMessage("LABEL_REGISTRAR_SAIDA_PAC", null);
						this.disabledBotao = false;
					}

					this.dtSaidaPaciente = this.internacao.getDtSaidaPaciente();
					this.dthrAltaMedica = this.internacao.getDthrAltaMedica();
					this.tipoAltaMedica = cadastrosBasicosInternacaoFacade.obterTipoAltaMedica(this.internacao.getTipoAltaMedica().getCodigo());

					if (this.internacao.getDthrAltaMedica() != null) {
						this.dthrAltaMedicaBD = true;
					}
					if (this.internacao.getDtSaidaPaciente() != null) {
						this.dtSaidaPacBD = true;
					}
					if (this.internacao.getTipoAltaMedica() != null) {
						this.tipoAltaMedicaBD = true;
					}
				}
				this.instituicaoHospitalar = this.internacao.getInstituicaoHospitalarTransferencia();
				this.docObito = this.internacao.getDocObito();

				AghParametros p = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CODIGO_TIPO_ALTA_MEDICA_C);
				String tamC = p.getVlrTexto();
				p = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CODIGO_TIPO_ALTA_MEDICA_D);
				String tamD = p.getVlrTexto();
				p = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CODIGO_TIPO_ALTA_MEDICA_O);
				String tamO = p.getVlrTexto();
				if (this.tipoAltaMedica != null
						&& (this.tipoAltaMedica.getCodigo().equalsIgnoreCase(tamC) || this.tipoAltaMedica.getCodigo()
								.equalsIgnoreCase(tamD))) {
					this.tipoAltaMedica = this.cadastrosBasicosInternacaoFacade.pesquisarTipoAltaMedicaPorCodigo(tamO, null);
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	
	}

	private void iniciarAltaMedica() throws ApplicationBusinessException {
		DadosAltaSumarioVO dadosAlta = this.internacaoFacade.buscarDadosAltaMedica(this.internacao.getSeq());
		if (dadosAlta != null && dadosAlta.getDthrAltaAsu() != null) {
			this.dthrAltaMedica = dadosAlta.getDthrAltaAsu();
			this.readOnlyEdicaoDthrAltaMedica = true;
			this.tipoDadoAltaPaciente = DominioTipoDadosAltaPaciente.P;// Prescricao
																		// Medica
			// this.labelBotao = "Registrar saída do paciente";
			this.labelBotao = WebUtil.initLocalizedMessage("LABEL_REGISTRAR_SAIDA_PAC", null);
			this.disabledBotao = false;
			this.tipoAltaMedica = this.cadastrosBasicosInternacaoFacade.pesquisarTipoAltaMedicaPorCodigo(
					dadosAlta.getCodigoTipoAltaMedica(), null);

			if (this.internacao.getDtSaidaPaciente() == null) {
				this.dtSaidaPaciente = getDataHoraAtual();
			}

			if (StringUtils.isNotBlank(dadosAlta.getDescPlanoPosAlta()) && this.tipoAltaMedica == null) {
				this.readOnlyEdicaoTipoAltaMedica = false;
				this.descPlanoPosAlta = dadosAlta.getDescPlanoPosAlta();
				this.exibirDescPlanoPosAlta = true;
			}
		} else {
			this.tipoDadoAltaPaciente = DominioTipoDadosAltaPaciente.M;
			// this.labelBotao = "Registrar datas Alta/Saída paciente";
			this.labelBotao = WebUtil.initLocalizedMessage("LABEL_REGISTRAR_DATA_ALTA_SAIDA_PAC", null);
			this.disabledBotao = false;
			this.readOnlyEdicaoTipoAltaMedica = false;
			this.readOnlyEdicaoDthrAltaMedica = false;

			this.dthrAltaMedica = getDataHoraAtual();
			if (this.internacao.getDtSaidaPaciente() == null) {
				this.dtSaidaPaciente = getDataHoraAtual();
			}
		}
		if (this.dtSaidaPaciente == null) {
			this.dtSaidaPaciente = this.internacao.getDtSaidaPaciente();
		}
		if (this.tipoAltaMedica == null) {
			this.tipoAltaMedica = this.internacao.getTipoAltaMedica();
		}
	}

	@SuppressWarnings("deprecation")
	private Date getDataHoraAtual() {
		Date dataHora = new Date();
		dataHora.setSeconds(0); // Remove segundos

		return dataHora;
	}

	public String gravar() {
		this.exibirModalAux = true;
		try {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = hostRemoto.getEnderecoIPv4HostRemoto().getHostName();
			} catch (UnknownHostException e) {
				Log.error(e.getMessage());
			}
			final Date dataFimVinculoServidor = new Date();
			
			VerificaPermissaoVO verificaPermissaoVO = new VerificaPermissaoVO();
			boolean isOk = internacaoFacade.gravarDataAltaPaciente(this.flagsValidacaoDadosAltaPacienteVO, verificaPermissaoVO, this.tipoDadoAltaPaciente,
					this.internacao, this.tipoAltaMedica, this.instituicaoHospitalar, this.dthrAltaMedica,
					this.dtSaidaPaciente, this.docObito, this.dtSaidaPacBD, this.dthrAltaMedicaBD, this.tipoAltaMedicaBD,
					nomeMicrocomputador, dataFimVinculoServidor);
			
			if(flagsValidacaoDadosAltaPacienteVO.getMensagem() != null && flagsValidacaoDadosAltaPacienteVO.getMensagem().equals("PEDE_CONFIRMACAO_PERMISSAO")){
				mensagemExibicaoModal = WebUtil.initLocalizedMessage("PEDE_CONFIRMACAO_PERMISSAO", null, verificaPermissaoVO.getDiasInt(), verificaPermissaoVO.getDiasPerm());
			}else{
				mensagemExibicaoModal = this.getBundle().getString(flagsValidacaoDadosAltaPacienteVO.getMensagem());
			}
			if (isOk) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_ALTA_PACIENTE");
				exibeMensagem = true;

				if (PAGE_PESQUISAR_CENSO_DIARIO_PACIENTES.equals(this.cameFrom)
						|| PESQUISAR_CENSO_DIARIO_PACIENTES.equalsIgnoreCase(this.cameFrom)) {
					return PAGE_PESQUISAR_CENSO_DIARIO_PACIENTES;
				} else if ("pesquisarPacienteInternadoDetalhes".equalsIgnoreCase(cameFrom)) {
					return PAGE_PESQUISAR_PACIENTE_INTERNADO_DETALHES;
				} else if ("pesquisarPacienteInternado".equalsIgnoreCase(cameFrom)) {
					return PAGE_PESQUISAR_PACIENTE_INTERNADO;
				} else {
					return PAGE_PESQUISAR_PACIENTE_ALTA;
				}
			} else { // TODO
				/*exibeMensagem = false;
				return null;*/
				openDialog("modalConfirmacaoWG");
				return null;
			}
		} catch (BaseException e) {
			if (e.getCode().toString().equals("MENSAGEM_LCTOS_FAT")) {
				readOnlyEdicaoDthrAltaMedica = false;
				tipoDadoAltaPaciente = DominioTipoDadosAltaPaciente.M;
			}

			// Foi necessário desatachar o objeto para que se possa obtê-lo
			// novamente do banco
			// em uma nova tentativa da alta
			internacaoFacade.desatacharObjetoInternacao(internacao);
			this.exibirModalAux = false;
			this.flagsValidacaoDadosAltaPacienteVO.setValidaDados(true);
			this.flagsValidacaoDadosAltaPacienteVO.setValidaDadosInformadosEstorno(true);
			this.flagsValidacaoDadosAltaPacienteVO.setValidaDadosInformados(true);
			this.flagsValidacaoDadosAltaPacienteVO.setValidaDadosFaturamento(true);
			this.flagsValidacaoDadosAltaPacienteVO.setValidaDadosDaAltaPaciente(true);
			this.flagsValidacaoDadosAltaPacienteVO.setValidaPermissoesDarAltaPaciente(true);
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public String cancelar() {
		this.dtSaidaPaciente = null;
		this.dthrAltaMedica = null;
		this.tipoAltaMedica = null;
		this.instituicaoHospitalar = null;
		this.docObito = null;
		exibeMensagem = true;

		if ("internacao-pesquisarCensoDiarioPacientes".equalsIgnoreCase(cameFrom)) {
			return PAGE_PESQUISAR_CENSO_DIARIO_PACIENTES;
		} else if ("pesquisarPacienteInternadoDetalhes".equalsIgnoreCase(cameFrom)) {
			return PAGE_PESQUISAR_PACIENTE_INTERNADO_DETALHES;
		} else if ("pesquisarPacienteInternado".equalsIgnoreCase(cameFrom)) {
			return PAGE_PESQUISAR_PACIENTE_INTERNADO;
		} else if (PAGE_PESQUISA_LEITOS.equalsIgnoreCase(cameFrom)) {
			return PAGE_PESQUISA_LEITOS;
		} else {
			return PAGE_PESQUISAR_PACIENTE_ALTA;
		}
	}

	public void limparCamposDeControle() {
		this.flagsValidacaoDadosAltaPacienteVO.setValidaDados(true);
		this.flagsValidacaoDadosAltaPacienteVO.setValidaDadosInformadosEstorno(true);
		this.flagsValidacaoDadosAltaPacienteVO.setValidaDadosInformados(true);
		this.flagsValidacaoDadosAltaPacienteVO.setValidaDadosFaturamento(true);
		this.flagsValidacaoDadosAltaPacienteVO.setValidaDadosDaAltaPaciente(true);
		this.flagsValidacaoDadosAltaPacienteVO.setValidaPermissoesDarAltaPaciente(true);
	}

	// ### Metodos para a suggestion de tipos de alta medica ###
	public boolean isMostrarLinkExcluirTipoAltaMedica() {
		return this.getTipoAltaMedica().getCodigo() != null;
	}
	
	public void liberaCampoTransferencia(){
		if(getTipoAltaMedica() != null && isMostrarLinkExcluirTipoAltaMedica() && this.getTipoAltaMedica().getCodigo().equalsIgnoreCase("E")){	
			liberarCampoTransferencia = false;
		} else {
			liberarCampoTransferencia = true;
			limparInstituicaoHospitalar();
		}
	}
	
	public void limparTipoAltaMedica() {
		this.tipoAltaMedica = null;
	}

	public List<AinTiposAltaMedica> pesquisarTipoAltaMedicaPorCodigoEDescricao(String param) throws ApplicationBusinessException {
		AghParametros p = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CODIGOS_TIPO_ALTA_MEDICA);

		String[] idsFiltrados = p.getVlrTexto().split(",");
		List<AinTiposAltaMedica> lista = this.cadastrosBasicosInternacaoFacade.pesquisarTipoAltaMedicaPorCodigoEDescricao(param,
				idsFiltrados);

		return lista;
	}

	// ### Metodos para a suggestion de instituicoes hostpitalares ###
	public boolean isMostrarLinkExcluirInstituicaoHospitalar() {
		return this.getInstituicaoHospitalar().getSeq() != null;
	}

	public void limparInstituicaoHospitalar() {
		this.instituicaoHospitalar = null;
	}

	public List<AghInstituicoesHospitalares> pesquisarInstituicaoHospitalarPorCodigoENome(String param) {
		return this.internacaoFacade.pesquisarInstituicaoHospitalarPorCodigoENome(param);
	}

	// ### GETS e SETs ###
	public String getDescricaoTipoAltaMedica() {
		String desc = "";
		if (this.tipoAltaMedica != null) {
			desc = this.tipoAltaMedica.getDescricao();
		}
		return desc;
	}

	public AinTiposAltaMedica getTipoAltaMedica() {
		return tipoAltaMedica;
	}

	public void setTipoAltaMedica(AinTiposAltaMedica tipoAltaMedica) {
		this.tipoAltaMedica = tipoAltaMedica;
	}

	public AghInstituicoesHospitalares getInstituicaoHospitalar() {
		return instituicaoHospitalar;
	}

	public void setInstituicaoHospitalar(AghInstituicoesHospitalares instituicaoHospitalar) {
		this.instituicaoHospitalar = instituicaoHospitalar;
	}

	public DominioTipoDadosAltaPaciente getTipoDadoAltaPaciente() {
		return tipoDadoAltaPaciente;
	}

	public void setTipoDadoAltaPaciente(DominioTipoDadosAltaPaciente tipoDadoAltaPaciente) {
		this.tipoDadoAltaPaciente = tipoDadoAltaPaciente;
	}

	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}

	public Date getDthrAltaMedica() {
		return dthrAltaMedica;
	}

	public void setDthrAltaMedica(Date dthrAltaMedica) {
		this.dthrAltaMedica = dthrAltaMedica;
	}

	public Date getDtSaidaPaciente() {
		return dtSaidaPaciente;
	}

	public void setDtSaidaPaciente(Date dtSaidaPaciente) {
		this.dtSaidaPaciente = dtSaidaPaciente;
	}

	public boolean isReadOnlyEdicaoDthrAltaMedica() {
		return readOnlyEdicaoDthrAltaMedica;
	}

	public void setReadOnlyEdicaoDthrAltaMedica(boolean readOnlyEdicaoDthrAltaMedica) {
		this.readOnlyEdicaoDthrAltaMedica = readOnlyEdicaoDthrAltaMedica;
	}

	public boolean isReadOnlyEdicaoTipoAltaMedica() {
		return readOnlyEdicaoTipoAltaMedica;
	}

	public void setReadOnlyEdicaoTipoAltaMedica(boolean readOnlyEdicaoTipoAltaMedica) {
		this.readOnlyEdicaoTipoAltaMedica = readOnlyEdicaoTipoAltaMedica;
	}

	public boolean isDisabledBotao() {
		return disabledBotao;
	}

	public void setDisabledBotao(boolean disabledBotao) {
		this.disabledBotao = disabledBotao;
	}

	public String getLabelBotao() {
		return labelBotao;
	}

	public void setLabelBotao(String labelBotao) {
		this.labelBotao = labelBotao;
	}

	public boolean isDtSaidaPacBD() {
		return dtSaidaPacBD;
	}

	public void setDtSaidaPacBD(boolean dtSaidaPacBD) {
		this.dtSaidaPacBD = dtSaidaPacBD;
	}

	public boolean isDthrAltaMedicaBD() {
		return dthrAltaMedicaBD;
	}

	public Integer getDocObito() {
		return docObito;
	}

	public void setDocObito(Integer docObito) {
		this.docObito = docObito;
	}

	public void setDthrAltaMedicaBD(boolean dthrAltaMedicaBD) {
		this.dthrAltaMedicaBD = dthrAltaMedicaBD;
	}

	public boolean isTipoAltaMedicaBD() {
		return tipoAltaMedicaBD;
	}

	public void setTipoAltaMedicaBD(boolean tipoAltaMedicaBD) {
		this.tipoAltaMedicaBD = tipoAltaMedicaBD;
	}

	public FlagsValidacaoDadosAltaPacienteVO getFlagsValidacaoDadosAltaPacienteVO() {
		return flagsValidacaoDadosAltaPacienteVO;
	}

	public void setFlagsValidacaoDadosAltaPacienteVO(FlagsValidacaoDadosAltaPacienteVO flagsValidacaoDadosAltaPacienteVO) {
		this.flagsValidacaoDadosAltaPacienteVO = flagsValidacaoDadosAltaPacienteVO;
	}

	public boolean isExibirModal() {
		return (!flagsValidacaoDadosAltaPacienteVO.isValidaDadosInformadosEstorno()
				|| !flagsValidacaoDadosAltaPacienteVO.isValidaDadosInformados()
				|| !flagsValidacaoDadosAltaPacienteVO.isValidaDadosFaturamento()
				|| !flagsValidacaoDadosAltaPacienteVO.isValidaDadosDaAltaPaciente() || !flagsValidacaoDadosAltaPacienteVO
					.isValidaPermissoesDarAltaPaciente()) && exibirModalAux;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public String getDescPlanoPosAlta() {
		return descPlanoPosAlta;
	}

	public void setDescPlanoPosAlta(String descPlanoPosAlta) {
		this.descPlanoPosAlta = descPlanoPosAlta;
	}

	public boolean isExibirDescPlanoPosAlta() {
		return exibirDescPlanoPosAlta;
	}

	public void setExibirDescPlanoPosAlta(boolean exibirDescPlanoPosAlta) {
		this.exibirDescPlanoPosAlta = exibirDescPlanoPosAlta;
	}

	public String getMensagemExibicaoModal() {
		return mensagemExibicaoModal;
	}

	public void setMensagemExibicaoModal(String mensagemExibicaoModal) {
		this.mensagemExibicaoModal = mensagemExibicaoModal;
	}

	public boolean isExibirModalAux() {
		return exibirModalAux;
	}

	public void setExibirModalAux(boolean exibirModalAux) {
		this.exibirModalAux = exibirModalAux;
	}

	public HostRemotoCache getHostRemoto() {
		return hostRemoto;
	}

	public void setHostRemoto(HostRemotoCache hostRemoto) {
		this.hostRemoto = hostRemoto;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public Integer getIntSeq() {
		return intSeq;
	}

	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}

	public boolean isExibeMensagem() {
		return exibeMensagem;
	}

	public void setExibeMensagem(boolean exibeMensagem) {
		this.exibeMensagem = exibeMensagem;
	}

	public boolean isLiberarCampoTransferencia() {
		return liberarCampoTransferencia;
	}

	public void setLiberarCampoTransferencia(boolean liberarCampoTransferencia) {
		this.liberarCampoTransferencia = liberarCampoTransferencia;
	}
}
