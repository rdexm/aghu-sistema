package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacaoHorarioSessao;
import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.dominio.DominioTipoLocal;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.dominio.DominioTurnoTodos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MptAgendamentoSessao;
import br.gov.mec.aghu.model.MptExtratoSessao;
import br.gov.mec.aghu.model.MptHorarioSessao;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptAgendamentoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptExtratoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptHorarioSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptJustificativaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptLocalAtendimentoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloCicloDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AlterarHorariosVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.DiasAgendadosVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorariosAgendamentoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloCicloVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.TransferirDiaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * RN de #44228 – Realizar Manutenção deAgendamento de Sessão Terapêutica
 * 
 * @author aghu
 */
@Stateless
public class ManutencaoAgendamentoSessaoTerapeuticaRN extends BaseBusiness {

	private static final long serialVersionUID = -3936636353124087893L;

	private static final Log LOG = LogFactory.getLog(ManutencaoAgendamentoSessaoTerapeuticaRN.class);

	private static final String SEPARADOR = " - ";
	
	public enum ManutencaoAgendamentoSessaoTerapeuticaRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_DATA_MENOR_QUE_ATUAL_MANUTENCAO, MENSAGEM_ERRO_DATA_FORA_LACUNA;
	}

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private DisponibilidadeHorarioAgendamentoSessaoRN disponibilidadeHorarioAgendamentoSessaoRN;

	@Inject
	private MptAgendamentoSessaoDAO mptAgendamentoSessaoDAO;

	@Inject
	private MptExtratoSessaoDAO mptExtratoSessaoDAO;

	@Inject
	private MptHorarioSessaoDAO mptHorarioSessaoDAO;

	@Inject
	private MptSessaoDAO mptSessaoDAO;

	@Inject
	private MptJustificativaDAO mptJustificativaDAO;

	@Inject
	private MptProtocoloCicloDAO mptProtocoloCicloDAO;

	@Inject
	private MptLocalAtendimentoDAO mptLocalAtendimentoDAO;

	/**
	 * 
	 * @param codigoPaciente
	 * @param diasValidadePrecricaoQuimio
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<AlterarHorariosVO> pesquisarPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(final Integer codigoPaciente) throws ApplicationBusinessException {
		AghParametros parametroDiasValidadePrecricaoQuimio = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_VALIDADE_PRECRICAO_QUIMIO);
		final int diasValidadePrecricaoQuimio = parametroDiasValidadePrecricaoQuimio.getVlrNumerico().intValue();
		List<AlterarHorariosVO> dadosPaciente = this.mptAgendamentoSessaoDAO.pesquisarPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(codigoPaciente, diasValidadePrecricaoQuimio);
		for (AlterarHorariosVO vo : dadosPaciente) {
			obterResponsavel(vo); // RN1
			obterProtocolo(vo); // RN2
		}
		List<AlterarHorariosVO> listaReservasHorarios = this.mptAgendamentoSessaoDAO.pesquisarReservasPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(codigoPaciente);
		if (dadosPaciente.isEmpty() && !listaReservasHorarios.isEmpty()) {
			for(AlterarHorariosVO vo : listaReservasHorarios){
				dadosPaciente.add(vo);
			}
		}
		
		for (Iterator<AlterarHorariosVO> vo = dadosPaciente.iterator(); vo.hasNext(); ) {
			AlterarHorariosVO item = vo.next();
			List<DiasAgendadosVO> listaDiasAgendados = this.mptHorarioSessaoDAO.pesquisarDiasPrescricaoManutencaoAgendamentoSessaoTerapeutica(item.getSeqAgendamento());
			if(listaDiasAgendados.isEmpty()){
				vo.remove();
			}
		}
		
		return dadosPaciente;
	}

	/**
	 * RN01: Determina a origem do campo Responsável
	 * 
	 * @param vo
	 */
	private void obterResponsavel(AlterarHorariosVO vo) {
		if (StringUtils.isNotBlank(vo.getResponsavel1())) {
			vo.setResponsavel(vo.getResponsavel1());
		} else {
			vo.setResponsavel(vo.getResponsavel2());
		}
	}

	/**
	 * RN02: Protocolo
	 * 
	 * @param vo
	 */
	private void obterProtocolo(AlterarHorariosVO vo) {
		List<ProtocoloCicloVO> listaProtocolos = this.mptProtocoloCicloDAO.obterProtocolo(vo.getCloSeq());
		StringBuffer protocolo = new StringBuffer(256);
		if (!listaProtocolos.isEmpty()) {
			if (listaProtocolos.size() == 1) {
				String descricao = listaProtocolos.get(0).getDescricao();
				if (StringUtils.isNotBlank(descricao)) {
					protocolo.append(descricao); // Se o campo descrição estiver preenchido, mostrar o mesmo
				} else {
					protocolo.append(listaProtocolos.get(0).getTitulo()); // Caso contrário mostrar o campo titulo
				}
			} else {
				/*
				 * Se a consulta retornar mais de um registro, concatenar o conteúdo do campo título exibindo as informações separadas por '-'
				 */
				for (ProtocoloCicloVO item : listaProtocolos) {
					if (protocolo.toString().isEmpty()) {
						protocolo.append(item.getTitulo());
						
					} else {
						protocolo.append(SEPARADOR).append(item.getTitulo());
					}
				}
			}
		}
		vo.setProtocolo(protocolo.toString());
	}

	/**
	 * RN04 Exclusão de Dia: Atualiza individualmente
	 * 
	 * @param seqHorario
	 * @param seqAgendamento
	 * @param seqSessao
	 * @param seqMotivo
	 * @param justificativa
	 */
	public void removerDiaManutencaoAgendamentoSessaoTerapeutica(final Short seqHorario, final Short seqAgendamento, final Integer seqSessao, final Short seqMotivo, final String justificativa) {
		atualizarHorarioSessao(seqHorario, seqSessao, seqMotivo, justificativa); // Atualiza horário individualmente
	}

	/**
	 * RN05 Exclusão de Ciclo: Atualiza todos os dias
	 * 
	 * @param seqAgendamento
	 * @param seqSessao
	 * @param seqMotivo
	 * @param justificativa
	 */
	public void removerCicloManutencaoAgendamentoSessaoTerapeutica(final Short seqAgendamento, final Integer seqSessao, final Short seqMotivo, final String justificativa) {
		List<DiasAgendadosVO> listaDias = this.mptHorarioSessaoDAO.pesquisarDiasPrescricaoManutencaoAgendamentoSessaoTerapeutica(seqAgendamento);
		for (DiasAgendadosVO vo : listaDias) {
			atualizarHorarioSessao(vo.getSeqHorario(), vo.getSesSeq(), seqMotivo, justificativa);
		}
	}

	/**
	 * Reuso da atualização de horários é a continuação da RN04 e RN05: Exclusão de Dia e Ciclo
	 * 
	 * @param seqHorario
	 * @param seqSessao
	 * @param seqMotivo
	 * @param justificativa
	 */
	private void atualizarHorarioSessao(final Short seqHorario, final Integer seqSessao, final Short seqMotivo, final String justificativa) {
		MptHorarioSessao horarioSessao = this.mptHorarioSessaoDAO.obterPorChavePrimaria(seqHorario);
		if (horarioSessao != null) {

			// Atualiza o Horário da Sessão com a situação Cancelada
			cancelarHorarioSessao(horarioSessao);

			if (seqSessao != null) {
				MptSessao sessao = this.mptSessaoDAO.obterPorChavePrimaria(seqSessao);
				if (sessao != null) {

					sessao.setIndSituacaoSessao(DominioSituacaoSessao.SSO); // Solicitada
					this.mptSessaoDAO.persistir(sessao); // Atualiza a Sessão com a situação Solicitada

					MptExtratoSessao extrato = new MptExtratoSessao();

					extrato.setCriadoEm(new Date());
					extrato.setIndSituacao(DominioSituacaoSessao.SCA);
					if(seqMotivo != null){
						extrato.setMotivo(this.mptJustificativaDAO.obterPorChavePrimaria(seqMotivo));
					}
					extrato.setJustificativa(justificativa);
					extrato.setMptSessao(sessao);
					extrato.setServidor(this.servidorLogadoFacade.obterServidorLogado());

					this.mptExtratoSessaoDAO.persistir(extrato); // Grava extrato
				}
			}
		}
	}

	/**
	 * RN04 e RN07: Cancelar Horário da Sessão
	 * 
	 * @param horarioSessao
	 */
	private void cancelarHorarioSessao(MptHorarioSessao horarioSessao) {
		if (horarioSessao == null) {
			throw new IllegalArgumentException();
		}
		horarioSessao.setIndSituacao(DominioSituacaoHorarioSessao.C); // Cancelado
		this.mptHorarioSessaoDAO.persistir(horarioSessao); // Atualiza o Horário da Sessão com a situação Cancelada
	}

	/**
	 * RN7: Transferir dia
	 * 
	 * @param transferir
	 * @param ciclo
	 * @param dia
	 * @throws ApplicationBusinessException
	 */
	public void transferirDia(TransferirDiaVO transferencia, AlterarHorariosVO ciclo, DiasAgendadosVO dia) throws ApplicationBusinessException {

		if (DateUtil.validaDataMenor(transferencia.getDataTransferencia(), DateUtil.truncaData(new Date()))) {
			throw new ApplicationBusinessException(ManutencaoAgendamentoSessaoTerapeuticaRNExceptionCode.MENSAGEM_ERRO_DATA_MENOR_QUE_ATUAL_MANUTENCAO);
		}
		
		if (!transferencia.getRestricaoDatas().isEmpty()) {
			if (DateUtil.validaDataMaior(transferencia.getDataTransferencia(), transferencia.getRestricaoFim())
					|| DateUtil.validaDataMenor(transferencia.getDataTransferencia(), transferencia.getRestricaoInicio())) {
				
				throw new ApplicationBusinessException(ManutencaoAgendamentoSessaoTerapeuticaRNExceptionCode.MENSAGEM_ERRO_DATA_FORA_LACUNA);
			}
		}
		
		// Se Valor igual a TODOS define Poltrona
		if (DominioTipoLocal.T.equals(transferencia.getAcomodacao())) {
			transferencia.setAcomodacao(DominioTipoLocal.P);
		}

		// Verifica disponibilidade de horário
		HorariosAgendamentoSessaoVO horarioSugerido = this.obterHorarioAgendamentoSessao(transferencia, dia);

		// Insere novo horário em MPT_HORARIO_SESSAO
		inserirHorarioSessao(ciclo, dia, horarioSugerido);

		// RN04: CANCELA horário transferido
		MptHorarioSessao horarioSessao = this.mptHorarioSessaoDAO.obterPorChavePrimaria(dia.getSeqHorario());
		cancelarHorarioSessao(horarioSessao);

	}

	/**
	 * RN7: Verifica disponibilidade de horário
	 * 
	 * @param transferencia
	 * @param dia
	 * @throws ApplicationBusinessException
	 */
	private HorariosAgendamentoSessaoVO obterHorarioAgendamentoSessao(TransferirDiaVO transferencia,
			DiasAgendadosVO dia) throws ApplicationBusinessException {

		final Short tpsSeq = dia.getTpsSeq();
		final DiasAgendadosVO horario = dia;
		final Date dataTransferencia = DateUtil.truncaData(transferencia.getDataTransferencia());
		final DominioTipoLocal acomodacao = transferencia.getAcomodacao();
		
		// Se Valor diferente a TODOS define valor convertido (A RN de disponibilidade trata Nulo como TODOS)
		DominioTurno turno = null;
		if (!DominioTurnoTodos.TODOS.equals(transferencia.getTurno())) {
			turno = transferencia.getTurno().getDominioTurno();
		}
		return this.disponibilidadeHorarioAgendamentoSessaoRN
				.sugerirNovoAgendamentoSessao(tpsSeq, horario, dataTransferencia, acomodacao, turno);
	}
	
	/**
	 * RN7: Insere novo horário em MPT_HORARIO_SESSAO
	 * 
	 * @param ciclo
	 * @param dia
	 * @throws ApplicationBusinessException
	 */
	private void inserirHorarioSessao(AlterarHorariosVO ciclo, DiasAgendadosVO dia, HorariosAgendamentoSessaoVO horarioSugerido) throws ApplicationBusinessException {

		MptHorarioSessao horarioSessao = new MptHorarioSessao();

		MptAgendamentoSessao agendamentoSessao = this.mptAgendamentoSessaoDAO.obterPorChavePrimaria(dia.getSeqAgendamento());
		horarioSessao.setAgendamentoSessao(agendamentoSessao);

		MptLocalAtendimento localAtendimento = this.mptLocalAtendimentoDAO.obterPorChavePrimaria(horarioSugerido.getLoaSeq());
		horarioSessao.setLocalAtendimento(localAtendimento);

		MptSessao sessao = this.mptSessaoDAO.obterPorChavePrimaria(dia.getSesSeq());
		horarioSessao.setMptSessao(sessao);

		RapServidores servidorLogado = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		horarioSessao.setServidor(servidorLogado);

		horarioSessao.setIndSituacao(DominioSituacaoHorarioSessao.M);
		horarioSessao.setDia(horarioSugerido.getDia());
		horarioSessao.setTempo(dia.getTempo());
		horarioSessao.setDataInicio(horarioSugerido.getDataInicio());
		horarioSessao.setDataFim(horarioSugerido.getDataFim());
		horarioSessao.setCiclo(ciclo.getCloSeq().shortValue());
		horarioSessao.setCriadoEm(new Date());

		this.mptHorarioSessaoDAO.persistir(horarioSessao);

	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

}
