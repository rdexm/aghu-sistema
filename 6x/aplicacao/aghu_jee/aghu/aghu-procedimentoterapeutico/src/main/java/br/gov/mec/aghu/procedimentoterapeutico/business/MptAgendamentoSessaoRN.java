package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioConformidadeHorarioSessao;
import br.gov.mec.aghu.dominio.DominioTipoLocal;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpaVersaoProtAssistencial;
import br.gov.mec.aghu.model.MpaVersaoProtAssistencialId;
import br.gov.mec.aghu.model.MptAgendamentoSessao;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptAgendamentoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptSalasDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorarioReservadoVO;
import br.gov.mec.aghu.protocolo.business.IProtocoloFacade;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MptAgendamentoSessaoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MptAgendamentoSessaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	private static final long serialVersionUID = -7669468528955606045L;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IProtocoloFacade protocoloFacade;
	
	@Inject
	private MptAgendamentoSessaoDAO mptAgendamentoSessaoDAO;
	
	@Inject
	private MptTipoSessaoDAO mptTipoSessaoDAO;
	
	@Inject
	private MptSalasDAO mptSalasDAO;


	/**
	 * Obtém horários reservados para o Paciente informado.
	 * 
	 * @param pacCodigo - Código do Paciente
	 * @param listaHorarios - Lista de horários registrados na prescrição
	 * @return Lista de horários reservados
	 */
	public List<HorarioReservadoVO> obterReservasPacienteParaConfirmacaoCancelamento(Integer pacCodigo, List<CadIntervaloTempoVO> listaHorarios) {

		List<HorarioReservadoVO> retorno = getMptAgendamentoSessaoDAO().obterReservasPacienteParaConfirmacaoCancelamento(pacCodigo);
		List<CadIntervaloTempoVO> horariosSelecionados = new ArrayList<CadIntervaloTempoVO>();

		for (CadIntervaloTempoVO horario : listaHorarios) {
			if (horario.isAgendar()) {
				horariosSelecionados.add(horario);
			}
		}

		if (retorno.size() == horariosSelecionados.size()) {
			preencherListaReservaIgual(retorno, horariosSelecionados);
		} else {
			preencherListaReservaDiferente(retorno, horariosSelecionados);
		}

		return retorno;
	}

	private void preencherListaReservaIgual(List<HorarioReservadoVO> retorno, List<CadIntervaloTempoVO> listaHorarios) {

		for (int i = 0; i < retorno.size(); i++) {
			HorarioReservadoVO horarioReserva = retorno.get(i);
			CadIntervaloTempoVO horarioPrescrito = listaHorarios.get(i);

			if (horarioReserva.getDia().equals(horarioPrescrito.getDiaReferencia())) {
				horarioReserva.setHorarioPrescricao(horarioPrescrito);

				if (compararHoraMinuto(horarioReserva.getTempo(), horarioPrescrito.getQtdeHoras()) > 0) {
					horarioReserva.setConforme(true);

					String horaFormatada = calculaDiferencaTempo(horarioPrescrito.getQtdeHoras(), horarioReserva.getTempo());

					horarioReserva.setObservacao(getResourceBundleValue("LABEL_HORA_RESERVADA_MAIOR_PRESCRITA", horaFormatada));
					horarioReserva.setConformidade(DominioConformidadeHorarioSessao.HORAS_RESERVADAS_MAIORES_PRESCRITA);
				} else if (compararHoraMinuto(horarioReserva.getTempo(), horarioPrescrito.getQtdeHoras()) < 0) {
					horarioReserva.setConforme(false);
					horarioReserva.setObservacao(getResourceBundleValue("LABEL_HORA_PRESCRITA_MAIOR_RESERVADA"));
					horarioReserva.setConformidade(DominioConformidadeHorarioSessao.HORAS_PRESCRITAS_MAIORES_RESERVADA);
				} else {
					horarioReserva.setConforme(true);
					horarioReserva.setObservacao(getResourceBundleValue("LABEL_OBSERVACAO_CONFORME"));
					horarioReserva.setConformidade(DominioConformidadeHorarioSessao.SEM_DIFERENCAS);
				}
			} else {
				horarioReserva.setConforme(false);
				horarioReserva.setObservacao(getResourceBundleValue("LABEL_DIA_RESERVADO_DIFERENTE_PRESCRITO", horarioReserva.getDia()));
				horarioReserva.setConformidade(DominioConformidadeHorarioSessao.DIA_RESERVADO_DIFERENTE_PRESCRITO);
			}
		}
	}
	
	private void preencherListaReservaDiferente(List<HorarioReservadoVO> retorno, List<CadIntervaloTempoVO> listaHorarios) {
		
		for (HorarioReservadoVO horarioReserva : retorno) {
			Short maiorDiaPrescricao = null;
			boolean encontrado = false;
			
			for (CadIntervaloTempoVO horarioPrescrito : listaHorarios) {
				if (maiorDiaPrescricao == null || maiorDiaPrescricao.compareTo(horarioPrescrito.getDiaReferencia()) < 0) {
					maiorDiaPrescricao = horarioPrescrito.getDiaReferencia();
				}
				
				if (horarioReserva.getDia().equals(horarioPrescrito.getDiaReferencia())) {
					horarioReserva.setHorarioPrescricao(horarioPrescrito);
					
					if (compararHoraMinuto(horarioReserva.getTempo(), horarioPrescrito.getQtdeHoras()) > 0) {
						encontrado = true;
						horarioReserva.setConforme(true);
						
						String horaFormatada = calculaDiferencaTempo(horarioPrescrito.getQtdeHoras(), horarioReserva.getTempo());
						
						horarioReserva.setObservacao(getResourceBundleValue("LABEL_HORA_RESERVADA_MAIOR_PRESCRITA", horaFormatada));
						horarioReserva.setConformidade(DominioConformidadeHorarioSessao.HORAS_RESERVADAS_MAIORES_PRESCRITA);
					} else if (compararHoraMinuto(horarioReserva.getTempo(), horarioPrescrito.getQtdeHoras()) < 0) {
						encontrado = true;
						horarioReserva.setConforme(false);
						horarioReserva.setObservacao(getResourceBundleValue("LABEL_HORA_PRESCRITA_MAIOR_RESERVADA"));
						horarioReserva.setConformidade(DominioConformidadeHorarioSessao.HORAS_PRESCRITAS_MAIORES_RESERVADA);
					} else {
						encontrado = true;
						horarioReserva.setConforme(true);
						horarioReserva.setObservacao(getResourceBundleValue("LABEL_OBSERVACAO_CONFORME"));
						horarioReserva.setConformidade(DominioConformidadeHorarioSessao.SEM_DIFERENCAS);
					}
					break;
				}
			}
			
			if (!encontrado) {
				if (horarioReserva.getDia().compareTo(maiorDiaPrescricao) < 0) {
					horarioReserva.setConforme(false);
					horarioReserva.setObservacao(getResourceBundleValue("LABEL_DIA_RESERVADO_DIFERENTE_PRESCRITO", horarioReserva.getDia()));
					horarioReserva.setConformidade(DominioConformidadeHorarioSessao.DIA_RESERVADO_DIFERENTE_PRESCRITO);
				} else {
					horarioReserva.setConforme(true);
					horarioReserva.setObservacao(getResourceBundleValue("LABEL_DIA_RESERVADO_MAIOR_PRESCRITO"));
					horarioReserva.setConformidade(DominioConformidadeHorarioSessao.DIAS_RESERVADOS_MAIS_PRESCRICAO);
				}
			}
		}
	}

	private String calculaDiferencaTempo(Date dstart, Date dfinal) {

		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		
		cal1.setTime(dstart);
		cal2.setTime(dfinal);

		long diffHours = 0;
		long diffMinutes = cal2.get(Calendar.MINUTE) - cal1.get(Calendar.MINUTE);
		if (diffMinutes < 0 ) {
			diffMinutes += 60;
			diffHours--;
		}
		diffHours += cal2.get(Calendar.HOUR) - cal1.get(Calendar.HOUR);

		StringBuffer res = new StringBuffer();
		String format = "%1$02d";

		res.append(String.format(format, diffHours)).append(':')
			.append(String.format(format, diffMinutes));

		return res.toString();
	}

	/**
	 * Calcula diferença entre as datas informadas, considerando apenas as horas e os minutos.
	 * 
	 * @param data1 - Primeira data
	 * @param data2 - Segunda data
	 * @return um valor positivo caso a primeira data seja maior que a segunda,
	 * 		um valor negativo caso a segunda data seja maior que a primeira,
	 * 		0 caso as datas sejam iguais.
	 */
	private int compararHoraMinuto(Date data1, Date data2) {

		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		
		cal1.setTime(data1);
		cal2.setTime(data2);

		int diff = cal1.get(Calendar.HOUR) - cal2.get(Calendar.HOUR);
		if (diff == 0) {
			diff = cal1.get(Calendar.MINUTE) - cal2.get(Calendar.MINUTE);
		}
		
		return diff;
	}

	public MptAgendamentoSessao inserirMptAgendamentoSessao(Short tpsSeq, Short salaSeq, DominioTurno turno,
			Integer pacCodigo, Short vpaPtaSeq, Short vpaSeqp, DominioTipoLocal acomodacao,
			Date dataInicio, Date dataFim) throws ApplicationBusinessException {
		
		RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		
		MptAgendamentoSessao agendamentoSessao = new MptAgendamentoSessao();
		MptTipoSessao tipoSessao = this.getMptTipoSessaoDAO().obterPorChavePrimaria(tpsSeq);
		agendamentoSessao.setTipoSessao(tipoSessao);
		MptSalas sala = this.getMptSalasDAO().obterPorChavePrimaria(salaSeq);
		agendamentoSessao.setSala(sala);
		agendamentoSessao.setTurno(turno);
		AipPacientes paciente = this.getPacienteFacade().obterPacientePorCodigo(pacCodigo);
		agendamentoSessao.setPaciente(paciente);
		if (vpaPtaSeq != null) {
			MpaVersaoProtAssistencialId id = new MpaVersaoProtAssistencialId(vpaPtaSeq, vpaSeqp);
			MpaVersaoProtAssistencial versaoProtAssistencial = this.getProtocoloFacade().obterMpaVersaoProtAssistencialPorId(id);
			agendamentoSessao.setMpaVersaoProtAssistencial(versaoProtAssistencial);
		}
		agendamentoSessao.setTipoAcomodacao(acomodacao);
		agendamentoSessao.setaPartirDe(dataInicio);
		agendamentoSessao.setAte(dataFim);
		agendamentoSessao.setCriadoEm(new Date());
		agendamentoSessao.setServidor(servidorLogado);
		
		this.getMptAgendamentoSessaoDAO().persistir(agendamentoSessao);
		
		return agendamentoSessao;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}
	
	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public IProtocoloFacade getProtocoloFacade() {
		return protocoloFacade;
	}

	public MptAgendamentoSessaoDAO getMptAgendamentoSessaoDAO() {
		return mptAgendamentoSessaoDAO;
	}

	public MptTipoSessaoDAO getMptTipoSessaoDAO() {
		return mptTipoSessaoDAO;
	}

	public MptSalasDAO getMptSalasDAO() {
		return mptSalasDAO;
	}
}
