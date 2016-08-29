package br.gov.mec.aghu.internacao.leitos.business;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.dao.AinExtratoLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ExtratoLeitoON extends BaseBusiness {


@EJB
private ExtratoLeitoRN extratoLeitoRN;

private static final Log LOG = LogFactory.getLog(ExtratoLeitoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinExtratoLeitosDAO ainExtratoLeitosDAO;

@Inject
private AinLeitosDAO ainLeitosDAO;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8042346772486577665L;

	/**
	 * Enumeracao com os codigos de mensagens de excecoes negociais.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 */
	private enum BloqueiaLeitoONExceptionCode implements BusinessExceptionCode {
		AIN_00176, AIN_00174, AIN_00031, BLOQUEIO_NULO, RESPONSAVEL_NULO, JUSTIFICATIVA_NULO, JUSTIFICATIVA_LIBERACAO_LEITO, BLOQUEIO_TECNICO, LIBERACAO_NULO
	   ,RESERVA_LEITO_OBRIGATORIO ,RESERVA_NULO, RESPONSAVEL_RESERVA, LEITO_NULO, RESERVA_ORIGEM, AIN_00628, MENSAGEM_DATA_LANCAMENTO,
	   ERRO_REMOVER_EXTRATOS_LEITOS_INTERNACAO, MENSAGEM_RESPONSAVEL_INVALIDO, ERRO_SITUACAO_LEITO_OCUPADO;
	}

	/**
	 * Valida os dados informados pelo usuário em Bloquear Leito
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param codigoLeito
	 * @param codigoStatus
	 * @param codigoResponsavel
	 * @param dataLancamento
	 * @param justificativa
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void validarDadosBloqueio(String codigoLeito, Integer codigoStatus,
			RapServidores rapServidores, Date dataLancamento,
			String justificativa, AinTiposMovimentoLeito tiposMovimentoLeito,
			AinLeitos leito) throws ApplicationBusinessException {

		// Valida Leito
		if (leito == null || leito.getLeitoID() == null) {
			throw new ApplicationBusinessException(
					BloqueiaLeitoONExceptionCode.AIN_00031);
		}
		
		//Ocupado
		if(leito.getTipoMovimentoLeito().getGrupoMvtoLeito() == DominioMovimentoLeito.O){
			throw new ApplicationBusinessException(BloqueiaLeitoONExceptionCode.ERRO_SITUACAO_LEITO_OCUPADO);
		}

		
		if (tiposMovimentoLeito == null) {
			throw new ApplicationBusinessException(
					BloqueiaLeitoONExceptionCode.AIN_00174);
		}

		// Valida Código de bloqueio
		if (codigoStatus != null) {
			if (tiposMovimentoLeito.getIndBloqueioPaciente() == DominioSimNao.S
					&& validarBloqueioPaciente(leito.getQuarto().getNumero())) {
				throw new ApplicationBusinessException(
						BloqueiaLeitoONExceptionCode.BLOQUEIO_TECNICO);
			}
		} else {
			throw new ApplicationBusinessException(
					BloqueiaLeitoONExceptionCode.BLOQUEIO_NULO);
		}

		// Valida Data Lançamento
		if (dataLancamento != null && dataLancamento.after(new Date())) {
			throw new ApplicationBusinessException(
					BloqueiaLeitoONExceptionCode.AIN_00176);
		}

		// Valida Justificativa
		if (tiposMovimentoLeito.getIndExigeJustificativa() == DominioSimNao.S
				&& (justificativa == null || StringUtils.isBlank(justificativa))) {
			throw new ApplicationBusinessException(
					BloqueiaLeitoONExceptionCode.JUSTIFICATIVA_NULO);
		}

		// Valida a matricula do responsável
		if (rapServidores == null || (rapServidores != null && rapServidores.getId() == null)) {
			throw new ApplicationBusinessException(
					BloqueiaLeitoONExceptionCode.RESPONSAVEL_NULO);
		}
	}

	/**
	 * Valida os dados informados pelo usuário em Liberar Leito
	 * 
	 * @param codigoLeito
	 * @param codigoStatus
	 * @param codigoResponsavel
	 * @param dataLancamento
	 * @param justificativa
	 */
	public void validarDadosLiberacao(AinLeitos leito, AinTiposMovimentoLeito codigoStatus,
			RapServidores rapServidores, Date dataLancamento,String justificativa) throws ApplicationBusinessException {

		// Valida Leito
		if (leito == null || leito.getLeitoID() == null) {
			throw new ApplicationBusinessException(
					BloqueiaLeitoONExceptionCode.AIN_00031);
		}

		if (leito.getTipoMovimentoLeito() == null) {
			throw new ApplicationBusinessException(
					BloqueiaLeitoONExceptionCode.AIN_00174);
		}

		// Valida Código de bloqueio
		if (codigoStatus == null) {
			throw new ApplicationBusinessException(
					BloqueiaLeitoONExceptionCode.LIBERACAO_NULO);
		}

		// Valida Data Lançamento
		if (dataLancamento != null && dataLancamento.after(new Date())) {
			throw new ApplicationBusinessException(
					BloqueiaLeitoONExceptionCode.AIN_00176);
		}

		// Valida Justificativa
		if (leito.getTipoMovimentoLeito().getIndExigeJustLiberacao() == DominioSimNao.S
				&& (justificativa == null || StringUtils.isBlank(justificativa))) {
			throw new ApplicationBusinessException(
					BloqueiaLeitoONExceptionCode.JUSTIFICATIVA_LIBERACAO_LEITO);
		}

		// Valida a matricula do responsável
		if (rapServidores == null || (rapServidores != null && rapServidores.getId() == null)) {
			throw new ApplicationBusinessException(
					BloqueiaLeitoONExceptionCode.RESPONSAVEL_NULO);
		}
	}

	/**
	 * Atualiza o leito gerando o novo extrato.
	 * 
	 * @dbtables AinLeitos select,insert,update
	 * @dbtables AinQuartos select,insert,update
	 * @dbtables AinExtratoLeitos select,insert,update
	 * @dbtables AinTiposMovimentoLeito select
	 * @dbtables AghCaractUnidFuncionais select
	 * @dbtables AinExtratoLeitos select
	 *  
	 * @param leito
	 * @param tiposMovimentoLeito
	 * @param rapServidor
	 * @param rapServidorResponsavel
	 * @param justificativa
	 * @param dataLancamento
	 * @param paciente
	 */
	@Secure("#{s:hasPermission('leito','alterarExtrato')}")
	public void inserirExtrato(AinLeitos leito, AinTiposMovimentoLeito tiposMovimentoLeito, RapServidores rapServidor,
			RapServidores rapServidorResponsavel, String justificativa, Date dataLancamento, Date criadoEm, AipPacientes paciente,
			AinInternacao internacao, Short tempoReserva, AinAtendimentosUrgencia atendimentoUrgencia, AghOrigemEventos origemEventos)
			throws ApplicationBusinessException {
		
		getExtratoLeitoRN().inserirExtrato(leito, tiposMovimentoLeito, rapServidor, rapServidorResponsavel, justificativa,
				dataLancamento, criadoEm, paciente, internacao, tempoReserva, atendimentoUrgencia, origemEventos);
	}
	
	/**
	 * Método que remove os extratos leitos de uma internação
	 * @param extratosLeitos
	 * @throws ApplicationBusinessException
	 */
	@Secure("#{s:hasPermission('leito','excluirExtrato')}")
	public void removerExtratosLeitos(Set<AinExtratoLeitos> extratosLeitos) throws ApplicationBusinessException{
		try{
			AinExtratoLeitosDAO ainExtratoLeitosDAO = this.getAinExtratoLeitosDAO();
			
			for (AinExtratoLeitos extratoLeito : extratosLeitos) {
				AinExtratoLeitos item = getAinExtratoLeitosDAO().obterPorChavePrimaria(extratoLeito.getSeq());
				ainExtratoLeitosDAO.remover(item);
				ainExtratoLeitosDAO.flush();
			}
		}
		catch (Exception e) {		
			logError("Exceção capturada: ", e);
			Throwable cause = ExceptionUtils.getCause(e);
			if (cause instanceof ConstraintViolationException) {
				throw new ApplicationBusinessException(
						BloqueiaLeitoONExceptionCode.ERRO_REMOVER_EXTRATOS_LEITOS_INTERNACAO, ((ConstraintViolationException) cause).getConstraintName());
			}
			else{
				throw new ApplicationBusinessException(
						BloqueiaLeitoONExceptionCode.ERRO_REMOVER_EXTRATOS_LEITOS_INTERNACAO, "");
			}
		}
		
	}

	/**
	 * ORADB PROCEDURE EVT_WHEN_VALIDATE_ITEM
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param quartoNumero
	 * @return
	 */
	private boolean validarBloqueioPaciente(Short quartoNumero) {
		Long count = getAinLeitosDAO().pesquisarLeitoPorNumeroQuartoInternacao(quartoNumero);
		if (count == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Verifica se o leito necessita de limpeza
	 * 
	 * @dbtables AinTiposMovimentoLeito select 
	 * 
	 * @param leito
	 */
	public boolean verificarLimpeza(AinLeitos leito) {

		if (leito.getIndBloqLeitoLimpeza()
				&& leito.getTipoMovimentoLeito()
						.isIndNecessitaLimpezaCheckBox()
				&& (leito.getTipoMovimentoLeito().getGrupoMvtoLeito() == DominioMovimentoLeito.B
						|| leito.getTipoMovimentoLeito().getGrupoMvtoLeito() == DominioMovimentoLeito.BI || leito
						.getTipoMovimentoLeito().getGrupoMvtoLeito() == DominioMovimentoLeito.R)) {
			return true;
		}

		return false;
	}

	/**
	 * Retorna rapServidores de acordo com a matricula ou nome passado por
	 * parametro
	 * 
	 * @dbtables RapServidores select
	 * 
	 * @param responsavel
	 * @return
	 */
	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public List<RapServidores> pesquisarResponsaveis(Object responsavel) {
		return getRegistroColaboradorFacade().pesquisarResponsaveis(responsavel);
	}
	
	/**
	 * Valida os dados informados pelo usuário em Liberar Leito
	 * 
	 * @param codigoLeito
	 * @param codigoTipoReserva
	 * @param codigoResponsavel
	 * @param matriculaResponsavel
	 * @param dataLancamento
	 * @param justificativa
	 * @param tiposMovimentoLeito
	 * @param leito
	 * @param codigoProntuario
	 * @param paciente
	 */
	@SuppressWarnings({"PMD.NPathComplexity"})
	public void validarDadosReserva(AinLeitos codigoLeito, AinTiposMovimentoLeito codigoTipoReserva, RapServidores codigoResponsavel,
			RapServidores matriculaResponsavel, RapServidores rapServidores, Date dataLancamento, String justificativa,
			AinTiposMovimentoLeito tiposMovimentoLeito, AinLeitos leito, Integer codigoProntuario, AipPacientes paciente,
			Integer codigoOrigem, AghOrigemEventos origemEventos) throws ApplicationBusinessException {

		//Valida código do leito para reserva
		if (codigoLeito == null){ 
			throw new ApplicationBusinessException(BloqueiaLeitoONExceptionCode.LEITO_NULO);
		}
		 
		//Valida leito para reservar
		if (leito == null || leito.getLeitoID() == null) {
			throw new ApplicationBusinessException(BloqueiaLeitoONExceptionCode.AIN_00031);
		}

		//Valida vínculo e matricula do responsável
		if (codigoResponsavel == null || matriculaResponsavel == null || rapServidores == null) {
			throw new ApplicationBusinessException(BloqueiaLeitoONExceptionCode.RESPONSAVEL_RESERVA);
		}
		
		if (rapServidores != null && rapServidores.getId() == null){
			throw new ApplicationBusinessException(BloqueiaLeitoONExceptionCode.MENSAGEM_RESPONSAVEL_INVALIDO);
		}
		
		//Valida código de reserva
		if (codigoTipoReserva == null) {
			throw new ApplicationBusinessException(BloqueiaLeitoONExceptionCode.RESERVA_NULO);
		}
		
		//Valida tipo de movimento de reserva
		if (tiposMovimentoLeito == null) {
			throw new ApplicationBusinessException(BloqueiaLeitoONExceptionCode.AIN_00174);
		}
		
		//Valida origem do evento
		if(codigoOrigem != null && origemEventos == null){
			throw new ApplicationBusinessException(BloqueiaLeitoONExceptionCode.RESERVA_ORIGEM);
		}
		
		//Valida prontuário de paciente associado na reserva
		if(codigoProntuario != null && paciente == null){
			throw new ApplicationBusinessException(BloqueiaLeitoONExceptionCode.AIN_00628);
		}
		
		if(dataLancamento == null){
			throw new ApplicationBusinessException(BloqueiaLeitoONExceptionCode.MENSAGEM_DATA_LANCAMENTO);
		}
		
		//Valida Data Lançamento
		if(dataLancamento != null && dataLancamento.after(new Date())) {
			throw new ApplicationBusinessException(BloqueiaLeitoONExceptionCode.AIN_00176);
		}
		
		//Valida Justificativa
		if (tiposMovimentoLeito.getIndExigeJustificativa() == DominioSimNao.S &&
			(justificativa == null || StringUtils.isBlank(justificativa))) {
			throw new ApplicationBusinessException(BloqueiaLeitoONExceptionCode.JUSTIFICATIVA_NULO);
		}
	}
	
	protected ExtratoLeitoRN getExtratoLeitoRN(){
		return extratoLeitoRN;
	}
	
	protected AinExtratoLeitosDAO getAinExtratoLeitosDAO(){
		return ainExtratoLeitosDAO;
	}
	
	protected AinLeitosDAO getAinLeitosDAO(){
		return ainLeitosDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}
