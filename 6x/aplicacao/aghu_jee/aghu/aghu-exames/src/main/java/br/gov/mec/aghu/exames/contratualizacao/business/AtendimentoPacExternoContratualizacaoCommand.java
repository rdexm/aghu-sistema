package br.gov.mec.aghu.exames.contratualizacao.business;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.contratualizacao.util.Header;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.AghMedicoExterno;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


public class AtendimentoPacExternoContratualizacaoCommand extends
		ContratualizacaoCommand {

	private static final long serialVersionUID = -4007537779282839689L;
	
	private static final Log LOG = LogFactory.getLog(AtendimentoPacExternoContratualizacaoCommand.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private IExamesFacade examesFacade;

	@Inject
	private IFaturamentoFacade faturamentoFacade;

	@Inject
	private IParametroFacade parametroFacade;

	@Inject
	private IAghuFacade aghuFacade;

	@Inject
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	public enum AtendimentoPacExternoContratualizacaoActionExceptionCode
			implements BusinessExceptionCode {
		MENSAGEM_DADOS_OBRIGATORIOS_ATENDIMENTO, MENSAGEM_CONVENIO_PLANO_INVALIDO, MENSAGEM_PARAMETRO_P_UNID_SOL_PREFEITURA_NAO_DEFINIDO, MENSAGEM_UNIDADE_FUNCIONAL_NAO_LOCALIZADA;
	}

	/**
	 * Inserir atendimento externo e atendimento
	 * 
	 * @param codigoPaciente
	 * @param seqMedicoExterno
	 * @param codigoConvenio
	 * @param codigoPlano
	 * @return
	 * @throws BaseException
	 */
	public AghAtendimentos inserirAtendimentoPacExterno(AipPacientes paciente,
			AghMedicoExterno medicoExterno, Short codigoConvenio, Byte seqPlano, String nomeMicrocomputador)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (paciente == null || medicoExterno == null || codigoConvenio == null || codigoConvenio == 0
				|| seqPlano == null || seqPlano == 0) {
			throw new ApplicationBusinessException(
					AtendimentoPacExternoContratualizacaoActionExceptionCode.MENSAGEM_DADOS_OBRIGATORIOS_ATENDIMENTO);
		}

		FatConvenioSaudePlano convenioSaudePlano = this
				.getFaturamentoFacade().obterConvenioSaudePlanoAtivo(
						codigoConvenio, seqPlano);

		if (convenioSaudePlano == null) {
			throw new ApplicationBusinessException(
					AtendimentoPacExternoContratualizacaoActionExceptionCode.MENSAGEM_CONVENIO_PLANO_INVALIDO);
		}

		AghAtendimentosPacExtern atendimentoPacExterno = this
				.carregarAtendimentoPacExtern(convenioSaudePlano, paciente,
						medicoExterno);

		AghAtendimentosPacExtern atdPacExtern = this.getExamesFacade()
				.gravarAghAtendimentoPacExtern(atendimentoPacExterno, nomeMicrocomputador, servidorLogado);

		return atdPacExtern != null && atdPacExtern.getAtendimentos() != null
				&& !atdPacExtern.getAtendimentos().isEmpty() ? atdPacExtern
				.getAtendimentos().get(0) : null;
	}

	/**
	 * Carregar o atendimento paciente externo
	 * 
	 * @param convenioSaudePlano
	 * @param paciente
	 * @param medicoExterno
	 * @return
	 * @throws BaseException
	 */
	protected AghAtendimentosPacExtern carregarAtendimentoPacExtern(
			FatConvenioSaudePlano convenioSaudePlano, AipPacientes paciente,
			AghMedicoExterno medicoExterno) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AghAtendimentosPacExtern atendimentoPacExterno = new AghAtendimentosPacExtern();
		atendimentoPacExterno.setServidorDigitado(servidorLogado);
		atendimentoPacExterno.setConvenioSaudePlano(convenioSaudePlano);
		atendimentoPacExterno.setPaciente(paciente);
		atendimentoPacExterno.setMedicoExterno(medicoExterno);
		atendimentoPacExterno.setUnidadeFuncional(this
				.obterUnidadeFuncionalSolicitante());
		atendimentoPacExterno.setCriadoEm(Calendar.getInstance().getTime());

		return atendimentoPacExterno;
	}

	/**
	 * Buscar a unidade funcional solicitante com base no parâmetro de sistema
	 * 
	 * @return
	 * @throws BaseException
	 */
	protected AghUnidadesFuncionais obterUnidadeFuncionalSolicitante()
			throws BaseException {

		AghParametros aghParametro = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_UNID_SOL_PREFEITURA);

		if (aghParametro != null && aghParametro.getVlrNumerico() != null) {

			AghUnidadesFuncionais unidadeFuncional = this
					.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(
							aghParametro.getVlrNumerico().shortValue());
			if (unidadeFuncional != null) {
				return unidadeFuncional;
			} else {
				throw new ApplicationBusinessException(
						AtendimentoPacExternoContratualizacaoActionExceptionCode.MENSAGEM_UNIDADE_FUNCIONAL_NAO_LOCALIZADA,
						aghParametro.getVlrNumerico().shortValue());
			}
		} else {
			throw new ApplicationBusinessException(
					AtendimentoPacExternoContratualizacaoActionExceptionCode.MENSAGEM_PARAMETRO_P_UNID_SOL_PREFEITURA_NAO_DEFINIDO);
		}

	}
	
	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}	

	@Override
	Map<String, Object> executar(Map<String, Object> parametros)
			throws NumberFormatException, BaseException, ParseException {

		AipPacientes paciente = null;
		if (parametros != null && parametros.containsKey(PACIENTE_AGHU)) {
			paciente = (AipPacientes) parametros.get(PACIENTE_AGHU);
		}

		AghMedicoExterno medicoExterno = null;
		if (parametros != null && parametros.containsKey(MEDICO_AGHU)) {
			medicoExterno = (AghMedicoExterno) parametros.get(MEDICO_AGHU);
		}

		Header headerIntegracao = null;
		if (parametros != null && parametros.containsKey(HEADER_INTEGRACAO)) {
			headerIntegracao = (Header) parametros.get(HEADER_INTEGRACAO);
		}
		
		String nomeMicrocomputador = (String) parametros.get(NOME_MICROCOMPUTADOR);

		if (paciente != null && medicoExterno != null
				&& headerIntegracao != null) {
			AghAtendimentos atendimento = this.inserirAtendimentoPacExterno(
					paciente, medicoExterno,
					(short) headerIntegracao.getConvenio(),
					headerIntegracao.getPlanoConvenio(), nomeMicrocomputador);

			parametros.put(ATENDIMENTO_AGHU, atendimento);
			return parametros;
		} else {
			return null;
		}

	}

	@Override
	boolean comitar() {		
		return true;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
