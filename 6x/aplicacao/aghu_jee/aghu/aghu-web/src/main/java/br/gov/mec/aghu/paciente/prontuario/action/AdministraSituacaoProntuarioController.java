package br.gov.mec.aghu.paciente.prontuario.action;

import java.net.UnknownHostException;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.dominio.DominioTipoImpressao;
import br.gov.mec.aghu.dominio.DominioTipoProntuario;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;



public class AdministraSituacaoProntuarioController extends ActionController{

	
	private static final long serialVersionUID = 6654904243478099654L;
	private static final Log LOG = LogFactory.getLog(AdministraSituacaoProntuarioController.class);
	private static final String REDIRECT_PESQUISA_SITUACAO_PRONTUARIO = "administraSituacaoProntuarioList";
	
	private enum AdministraSituacaoProntuarioControllerExceptionCode implements BusinessExceptionCode {
		COMPUTADOR_SEM_UNIDADE_FUNCIONAL;
	}

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IExamesLaudosFacade examesLaudosFacade;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IAdministracaoFacade administracaoFacade;
	
	@Inject
	private AdministraSituacaoProntuarioPaginatorController administraSituacaoProntuarioPaginatorController;
	
	private AipPacientes paciente = new AipPacientes();

	private Integer aipPacienteCodigo;

	private DominioTipoImpressao selecao;

	private Integer numeroVolume;

	private Integer numeroVolumeImpressao;

	private Boolean operacaoConcluida = false;

	private Boolean operacaoImpressaoConcluida = false;

	private Boolean salvarVolumePaciente = false;

	private Boolean chamarImpressao = false;
	
	private Boolean indBloqueioPacienteUbs = false;
	
	private String motivoBloqueio = null;

	private String zpl;
	
	public void inicio(Integer codigoPaciente) {
		
		this.aipPacienteCodigo = codigoPaciente;
		
		if (paciente.getCodigo() == null && aipPacienteCodigo == null) {
			// Apresentar erro - não é possível entrar na tela sem um paciente
			this.apresentarMsgNegocio(Severity.ERROR,
					"MENSAGEM_ERRO_ACESSAR_SITUACAO_PRONTUARIO", this.paciente.getProntuario());
		} else {
			// Edição
			try {
				Integer codigo = paciente.getCodigo() == null ? aipPacienteCodigo : paciente.getCodigo();

				this.paciente = pacienteFacade.obterPacientePorCodigoOuProntuario(null, codigo, null);
							
				if (paciente.getVolumes() != null) {
					this.numeroVolume = paciente.getVolumes().intValue();
				}

			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public String salvar() {
		if (paciente.getCodigo() == null) {
			this.apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_ERRO_ACESSAR_SITUACAO_PRONTUARIO", this.paciente.getProntuario());
		} else {
			try {

				// Atribuição do volume ao paciente.volumes().
				// Isso não é feito diretamente através da página, pois o
				// componente intpuNumero sobe uma ClassCastException para
				// campos Short.
				if (this.numeroVolume == null) {
					paciente.setVolumes(null);
				} else {
					paciente.setVolumes(numeroVolume.shortValue());
				}

				this.cadastroPacienteFacade.atualizarSitCadPaciente(paciente, obterLoginUsuarioLogado());
				
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_PERSISTIR_SITUACAO_PRONTUARIO",this.paciente.getProntuario());

				this.limpar();
				administraSituacaoProntuarioPaginatorController.reiniciarPaginator();
				return REDIRECT_PESQUISA_SITUACAO_PRONTUARIO;

			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}

		return null;
	}

	private void limpar() {
		this.paciente = new AipPacientes();
		this.numeroVolumeImpressao = null;
		this.numeroVolume = null;
		this.selecao = null;

		administraSituacaoProntuarioPaginatorController.reiniciarPaginator();
	}

	public void salvarImprimir() {
		try {
			this.pacienteFacade.atualizaVolume(this.paciente.getCodigo(),this.numeroVolumeImpressao.shortValue());
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_SALVAR_IMPRIMIR_ETIQUETAS");
		}
		this.paciente.setVolumes(this.numeroVolumeImpressao.shortValue());
		this.numeroVolume = this.numeroVolumeImpressao;

		// Envia ZPL para impressora Zebra instalada no CUPS.
		this.enviarEtqCups();
	}

	/**
	 * Envia ZPL para impressora Zebra instalada no CUPS.
	 */
	private void enviarEtqCups() {
		AghMicrocomputador computador;
		try {
			computador = getComputador();
			if(computador != null) {
				AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais(
						computador.getAghUnidadesFuncionais().getSeq());

				this.sistemaImpressao.imprimir(zpl, unidadeFuncional,
						TipoDocumentoImpressao.ETIQUETAS_BARRAS_PRONTUARIO);

				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRIMIR_ETIQUETAS");
			}
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
		}

	}

	private void imprimir() {
		this.operacaoImpressaoConcluida = false;

		try {
			Integer numeroVolumesPaciente = null;
			if (this.salvarVolumePaciente) {
				numeroVolumesPaciente = this.numeroVolumeImpressao;
			} else {
				numeroVolumesPaciente = paciente.getVolumes().intValue();
			}

			this.zpl = cadastroPacienteFacade.imprimirEtiquetasPaciente(selecao, numeroVolumeImpressao,
					numeroVolumesPaciente, paciente.getProntuario(), paciente.getNome());

			this.operacaoImpressaoConcluida = true;

		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}
	}

	public String cancelar() {
		this.limpar();
		this.administraSituacaoProntuarioPaginatorController.reiniciarPaginator();
		return REDIRECT_PESQUISA_SITUACAO_PRONTUARIO;
	}

	/**
	 * Método chamado na tela de confirmação de recuperação de um registro de
	 * histórico de pacienet quando o usuário clicar no botão "Cancelar",
	 * cancelando a operação.
	 */
	public void cancelarModal() {
		this.operacaoConcluida = true;
	}

	public String pesquisarProtocolosNeurologiaPendentes() {
		String protocolos = "";
		if (paciente.getCodigo() != null) {
			protocolos = examesLaudosFacade.pesquisarProtocolosPorPacienteString(paciente.getCodigo());
		}

		return protocolos;
	}

	public Boolean atualizarExibicaoMensagem() {
		Boolean retorno = true;

		if (DominioTipoProntuario.E.equals(paciente.getPrntAtivo())
				|| DominioTipoProntuario.H.equals(paciente.getPrntAtivo())) {
			retorno = true;
		} else {
			retorno = false;
		}

		return retorno;
	}

	/**
	 * Método para verificar se precisa salvar volume no cadastro do paciente
	 * antes de imprimí-lo.
	 * 
	 * @return
	 */
	public void verificarSalvarVolume() {

		this.salvarVolumePaciente = false;
		this.chamarImpressao = true;

		try {
			Integer nroVolumesPaciente = paciente.getVolumes() == null ? null : paciente.getVolumes().intValue();

			this.cadastroPacienteFacade.validarImpressaoEtiquetas(this.selecao,this.numeroVolumeImpressao, nroVolumesPaciente);

			if (nroVolumesPaciente == null) {
				// Precisa salvar (apresenta modal)
				this.salvarVolumePaciente = true;
			} else if (this.numeroVolumeImpressao != null && nroVolumesPaciente < this.numeroVolumeImpressao) {
				// Precisa salvar (apresenta modal)
				this.salvarVolumePaciente = true;
			}

			this.chamarImpressao = true;
			this.imprimir();

			// Envia ZPL para impressora Zebra instalada no CUPS.
			this.enviarEtqCups();

		} catch (Exception e) {
			this.chamarImpressao = false;
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}
	}
	
	/**
	 * Retorna o computador do usuário logado
	 * 
	 * @return
	 */
	private AghMicrocomputador getComputador() throws ApplicationBusinessException{
		AghMicrocomputador computador = new AghMicrocomputador();

		String nome = null;
		try {
			nome = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR);
		}
		
		computador = this.administracaoFacade.obterAghMicroComputadorPorNomeOuIPException(nome);

		// Obtém a unidade funcional da estação
		if (computador == null || computador.getAghUnidadesFuncionais() == null) {
			throw new ApplicationBusinessException(AdministraSituacaoProntuarioControllerExceptionCode.COMPUTADOR_SEM_UNIDADE_FUNCIONAL);

		}
		return computador;
	}


	// SET's e GET's
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Integer getAipPacienteCodigo() {
		return aipPacienteCodigo;
	}

	public void setAipPacienteCodigo(Integer aipPacienteCodigo) {
		this.aipPacienteCodigo = aipPacienteCodigo;
	}

	public DominioTipoImpressao getSelecao() {
		return selecao;
	}

	public void setSelecao(DominioTipoImpressao selecao) {
		this.selecao = selecao;
	}

	public Integer getNumeroVolume() {
		return numeroVolume;
	}

	public void setNumeroVolume(Integer numeroVolume) {
		this.numeroVolume = numeroVolume;
	}

	public Integer getNumeroVolumeImpressao() {
		return numeroVolumeImpressao;
	}

	public void setNumeroVolumeImpressao(Integer numeroVolumeImpressao) {
		this.numeroVolumeImpressao = numeroVolumeImpressao;
	}

	public Boolean getOperacaoConcluida() {
		return operacaoConcluida;
	}

	public void setOperacaoConcluida(Boolean operacaoConcluida) {
		this.operacaoConcluida = operacaoConcluida;
	}

	public String getZpl() {
		return zpl;
	}

	public void setZpl(String zpl) {
		this.zpl = zpl;
	}

	public Boolean getOperacaoImpressaoConcluida() {
		return operacaoImpressaoConcluida;
	}

	public void setOperacaoImpressaoConcluida(Boolean operacaoImpressaoConcluida) {
		this.operacaoImpressaoConcluida = operacaoImpressaoConcluida;
	}

	public Boolean getSalvarVolumePaciente() {
		return salvarVolumePaciente;
	}

	public void setSalvarVolumePaciente(Boolean salvarVolumePaciente) {
		this.salvarVolumePaciente = salvarVolumePaciente;
	}

	public Boolean getChamarImpressao() {
		return chamarImpressao;
	}

	public void setChamarImpressao(Boolean chamarImpressao) {
		this.chamarImpressao = chamarImpressao;
	}
	
	public Boolean getIndBloqueioPacienteUbs() {
		return indBloqueioPacienteUbs;
	}

	public void setIndBloqueioPacienteUbs(Boolean indBloqueioPacienteUbs) {
		motivoBloqueio = null;
		this.indBloqueioPacienteUbs = indBloqueioPacienteUbs;
	}
	
	public String getMotivoBloqueio() {
		return motivoBloqueio;
	}

	public void setMotivoBloqueio(String motivoBloqueio) {
		this.motivoBloqueio = motivoBloqueio;
	}
	
    public Boolean verificarComputadorUbs(){
    	Boolean retorno = false;
    	try {
			AghMicrocomputador micro = administracaoFacade
					.obterAghMicroComputadorPorNomeOuIP(
							getEnderecoRedeHostRemoto(),
							DominioCaracteristicaMicrocomputador.PERFIL_UBS);
			if (micro != null){
				retorno = true;
			}
		} catch (UnknownHostException e) {
			LOG.error("Exceção capturada: ", e);
		}
    	return retorno;
    }

}