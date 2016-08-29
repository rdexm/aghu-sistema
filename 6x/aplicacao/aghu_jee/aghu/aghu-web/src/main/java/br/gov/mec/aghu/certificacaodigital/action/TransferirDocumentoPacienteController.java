package br.gov.mec.aghu.certificacaodigital.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * @author rhrosa Controller responsável pelo fluxo da modal de transferência de
 *         responsabilidade de documento
 * 
 */


public class TransferirDocumentoPacienteController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7993858351679607209L;

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	private Integer seqAghVersaoDocumento;
	private AghVersaoDocumento aghVersaoDocumento;
	
	private String nomePaciente;
	private Integer prontuarioPaciente;

	// SB transferencia responsabilidade
	private Integer vinCodigo;
	private RapServidores responsavel;
	private boolean fecharModal;
	private boolean botaoConfirmaTransferenciaHabilitado = false;

	private enum TransferirDocumentoPacienteControllerExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_PARAMETROS_VINCODIGO_NULO, 
		MENSAGEM_PARAMETRO_RESPONSAVEL_NULO, 
		MENSAGEM_VERSAO_DOC_NULO, 
		MENSAGEM_SUCESSO_ALTERACAO_RESPONSABILIDADE_DOCUMENTO, 
		MENSAGEM_SEQ_AGH_VERSAO_DOCUMENTO_NULO, 
		MENSAGEM_BUSCA_AGH_VERSAO_DOCUMENTO_NAO_RETORNOU_DADOS,
		MENSAGEM_PACIENTE_NULO;
	}

	public void iniciar() {

		if (this.seqAghVersaoDocumento == null) {
			apresentarMsgNegocio(
							Severity.ERROR,
							TransferirDocumentoPacienteControllerExceptionCode.MENSAGEM_SEQ_AGH_VERSAO_DOCUMENTO_NULO.toString());
			return;
		}

		//this.aghVersaoDocumento = new AghVersaoDocumento();
		this.aghVersaoDocumento = this
				.buscarAghVersaoDocumento(this.seqAghVersaoDocumento);
		this.buscarPaciente();
		this.responsavel = null;

	}

	/**
	 * 
	 * Busca o paciente baseado no arco de fks(atd_seq, crg_seq, fic_seq,
	 * npo_seq) atd_seq - agh_atendimentos crg_seq - mbc_cirurgias fic_seq -
	 * mbc_ficha_anestesias npo_seq - mam_nota_adicional_evolucao
	 * 
	 * @return
	 */
	private void buscarPaciente() {
		
		if (this.aghVersaoDocumento == null) {
			apresentarMsgNegocio(
							Severity.ERROR,
							TransferirDocumentoPacienteControllerExceptionCode.MENSAGEM_BUSCA_AGH_VERSAO_DOCUMENTO_NAO_RETORNOU_DADOS.toString());
			return;
		}		

		AipPacientes paciente = new AipPacientes();

		if (this.aghVersaoDocumento.getAghDocumentos().getAghAtendimento() != null) {

			// busca o paciente do atendimento do documento
			paciente = this.aghVersaoDocumento.getAghDocumentos()
					.getAghAtendimento().getPaciente();

		} else if (this.aghVersaoDocumento.getAghDocumentos().getCirurgia() != null) {

			// busca o paciente da cirurgia
			paciente = this.aghVersaoDocumento.getAghDocumentos().getCirurgia()
					.getPaciente();

		} else if (this.aghVersaoDocumento.getAghDocumentos()
				.getFichaAnestesia() != null) {

			// busca o paciente da ficha anestesica
			paciente = this.aghVersaoDocumento.getAghDocumentos()
					.getFichaAnestesia().getPaciente();

		} else if (this.aghVersaoDocumento.getAghDocumentos()
				.getNotaAdicionalEvolucao() != null) {

			// busca o paciente da nota adicional de evolucão
			paciente = this.aghVersaoDocumento.getAghDocumentos()
					.getNotaAdicionalEvolucao().getPaciente();

		}
		
		if(paciente == null){
			apresentarMsgNegocio(
					Severity.ERROR,
					TransferirDocumentoPacienteControllerExceptionCode.MENSAGEM_PACIENTE_NULO.toString());
			return;
		}
			
		
		this.nomePaciente = paciente.getNome();
		this.prontuarioPaciente = paciente.getProntuario();

	}

	public void limpar() {
		this.vinCodigo = null;
		this.responsavel = null;
		this.aghVersaoDocumento = null;
		this.nomePaciente = null;
		this.prontuarioPaciente = null;
		this.setBotaoConfirmaTransferenciaHabilitado(false);
	}

	private AghVersaoDocumento buscarAghVersaoDocumento(
			Integer seqAghVersaoDocumento) {

		if (seqAghVersaoDocumento == null) {

			apresentarMsgNegocio(
							Severity.ERROR,
							TransferirDocumentoPacienteControllerExceptionCode.MENSAGEM_SEQ_AGH_VERSAO_DOCUMENTO_NULO.toString());
			return null;
		}

		AghVersaoDocumento documento = this.certificacaoDigitalFacade
				.obterPorChavePrimariaTranferirResponsavel(seqAghVersaoDocumento);

		/*if (documento == null) {
			this.getStatusMessages()
					.addFromResourceBundle(
							Severity.ERROR,
							TransferirDocumentoPacienteControllerExceptionCode.MENSAGEM_BUSCA_AGH_VERSAO_DOCUMENTO_NAO_RETORNOU_DADOS
									.toString());
			return null;
		}*/

		return documento;

	}

	/**
	 * Realiza a transferencia de responsabilidade do documento
	 */
	public String transferirResponsavelDocumento() {
		
		if (this.aghVersaoDocumento == null) {
			apresentarMsgNegocio(
							Severity.ERROR,
							TransferirDocumentoPacienteControllerExceptionCode.MENSAGEM_BUSCA_AGH_VERSAO_DOCUMENTO_NAO_RETORNOU_DADOS
									.toString());
			return null;
		}

		if (this.vinCodigo == null) {

			apresentarMsgNegocio(
							Severity.ERROR,
							TransferirDocumentoPacienteControllerExceptionCode.MENSAGEM_PARAMETROS_VINCODIGO_NULO
									.toString());
			return null;
		}

		if (this.responsavel == null) {

			apresentarMsgNegocio(
							Severity.ERROR,
							TransferirDocumentoPacienteControllerExceptionCode.MENSAGEM_PARAMETRO_RESPONSAVEL_NULO
									.toString());
			return null;
		}

		if (this.aghVersaoDocumento == null) {

			apresentarMsgNegocio(
					Severity.ERROR,
							TransferirDocumentoPacienteControllerExceptionCode.MENSAGEM_VERSAO_DOC_NULO
									.toString());
			return null;
		}
		
		this.aghVersaoDocumento.setServidorResp(this.responsavel);
		this.certificacaoDigitalFacade
				.transferirResponsavelDocumento(this.aghVersaoDocumento);

		//Para visualizar uma descricao da alteracao na mensagem
		//Responsabilidade do documento do tipo {0} para o prontuário {1} alterada com sucesso.
				
		String descricao = aghVersaoDocumento.getAghDocumentos().getTipo()
				.getDescricao().toString();

		String prontuario = aghVersaoDocumento.getAghDocumentos()
				.getAghAtendimento().getProntuario().toString();

		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_ALTERACAO_RESPONSABILIDADE_DOCUMENTO",
				descricao, prontuario);
		
		this.limpar();
		this.fecharModal = true;
		return "pesquisarDocumentosPaciente";		
	}

	/**
	 * Método da suggestion box para pesquisa de servidores
	 * 
	 * @param parametro
	 * @return
	 * @throws BaseException
	 */
	public List<RapServidores> pesquisarServidores(String parametro)
			throws BaseException {
		return this.returnSGWithCount(this.certificacaoDigitalFacade
				.pesquisarServidorComCertificacaoDigital(parametro),this.pesquisarServidorComCertificacaoDigitalCount(parametro));
	}

	public Long pesquisarServidorComCertificacaoDigitalCount(String param) {
		return this.certificacaoDigitalFacade
				.pesquisarServidorComCertificacaoDigitalCount(param);
	}

	/**
	 * Setar o servidor selecionado pelo usuário
	 */
	public void selecionouServidor() {
		this.setVinCodigo(this.responsavel.getVinculo().getCodigo().intValue());
		this.setBotaoConfirmaTransferenciaHabilitado(true);
	}

	public void limparDadosServidor() {
		this.vinCodigo = null;
		this.responsavel = null;
		this.setBotaoConfirmaTransferenciaHabilitado(false);
	}

	public AghVersaoDocumento getAghVersaoDocumento() {
		return aghVersaoDocumento;
	}

	public void setAghVersaoDocumento(AghVersaoDocumento aghVersaoDocumento) {
		this.aghVersaoDocumento = aghVersaoDocumento;
	}

	public Integer getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Integer vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public RapServidores getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(RapServidores responsavel) {
		this.responsavel = responsavel;
	}

	public Integer getSeqAghVersaoDocumento() {
		return seqAghVersaoDocumento;
	}

	public void setSeqAghVersaoDocumento(Integer seqAghVersaoDocumento) {
		this.seqAghVersaoDocumento = seqAghVersaoDocumento;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getProntuarioPaciente() {
		return prontuarioPaciente;
	}

	public void setProntuarioPaciente(Integer prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}

	public boolean isFecharModal() {
		return fecharModal;
	}

	public void setFecharModal(boolean fecharModal) {
		this.fecharModal = fecharModal;
	}

	public boolean isBotaoConfirmaTransferenciaHabilitado() {
		return botaoConfirmaTransferenciaHabilitado;
	}

	public void setBotaoConfirmaTransferenciaHabilitado(
			boolean botaoConfirmaTransferenciaHabilitado) {
		this.botaoConfirmaTransferenciaHabilitado = botaoConfirmaTransferenciaHabilitado;
	}

}
