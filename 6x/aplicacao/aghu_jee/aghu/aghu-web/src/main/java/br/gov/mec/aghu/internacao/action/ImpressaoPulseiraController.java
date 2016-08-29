package br.gov.mec.aghu.internacao.action;

import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class ImpressaoPulseiraController extends ActionController {

	private static final long serialVersionUID = 1738827315988850475L;
	private enum ImpressaoPulseiraControllerExceptionCode implements
			BusinessExceptionCode {
		COMPUTADOR_SEM_UNIDADE_FUNCIONAL, MENSAGEM_SUCESSO_IMPRESSAO_ETIQUETAS;
	}

	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private IAdministracaoFacade administracaoFacade;	

	@EJB private IPacienteFacade pacienteFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;


	private AipPacientes paciente;

	private AinInternacao internacao;

	private Integer internacaoCodigo;

	private Integer aipPacCodigo;

	private Integer atdSeq;
	
	private Boolean operacaoRealizada;

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	public void imprimePulseira() throws ApplicationBusinessException {

		operacaoRealizada = Boolean.TRUE;

		if (aipPacCodigo != null) {
			this.paciente = this.pacienteFacade
					.obterPaciente(this.aipPacCodigo);
		}

		// se a chamada veio de Internar paciente ou Censo de pacientes atdSeq = null
		// se a chamada veio da Lista de Enfermagem atdSeq != null
		if (this.atdSeq == null) {

			if (internacaoCodigo != null) {
				this.internacao = this.internacaoFacade
						.obterInternacao(internacaoCodigo);

				if (this.internacao != null
						&& this.internacao.getAtendimento() != null
						&& this.internacao.getAtendimento().getSeq() != null) {
					this.atdSeq = this.internacao.getAtendimento().getSeq();
				}
			}
		}

		this.gerarEtiquetas();
	}

	/**
	 * Chamada da geração do codigo para Zebra.
	 * @throws ApplicationBusinessException 
	 */
	private void gerarEtiquetas() throws ApplicationBusinessException {

		AghMicrocomputador computador;
		try {
			computador = getComputador();
			if(computador != null) {
				AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais(
						computador.getAghUnidadesFuncionais().getSeq());
				
				String zpl = this.pacienteFacade.gerarEtiquetaPulseira(this.paciente,
						this.atdSeq);
				
				this.imprimeEtiquetas(unidadeFuncional, zpl,
						TipoDocumentoImpressao.ETIQUETA_PULSEIRA_PACIENTE);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			operacaoRealizada = Boolean.FALSE;
		}

	}

	/**
	 * Chamada da impressão para Zebra.
	 */
	private void imprimeEtiquetas(AghUnidadesFuncionais unidadeFuncional,
			String zpl, TipoDocumentoImpressao tipo) {
		try {
			this.sistemaImpressao.imprimir(zpl, unidadeFuncional, tipo);

			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO_ETIQUETAS");
			
			operacaoRealizada = Boolean.TRUE;
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			operacaoRealizada = Boolean.FALSE;
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
			throw new ApplicationBusinessException(ImpressaoPulseiraControllerExceptionCode.COMPUTADOR_SEM_UNIDADE_FUNCIONAL);

		}
		return computador;
	}

	public Integer getInternacaoCodigo() {
		return internacaoCodigo;
	}

	public void setInternacaoCodigo(Integer internacaoCodigo) {
		this.internacaoCodigo = internacaoCodigo;
	}

	public Integer getAipPacCodigo() {
		return aipPacCodigo;
	}

	public void setAipPacCodigo(Integer aipPacCodigo) {
		this.aipPacCodigo = aipPacCodigo;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Boolean getOperacaoRealizada() {
		return operacaoRealizada;
	}

	public void setOperacaoRealizada(Boolean operacaoRealizada) {
		this.operacaoRealizada = operacaoRealizada;
	}

}
