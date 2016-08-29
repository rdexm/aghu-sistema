package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoHorarioSessao;
import br.gov.mec.aghu.dominio.DominioTipoLocal;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.MptDiaTipoSessao;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptBloqueioDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptCaracteristicaTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptDiaTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptHorarioSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptLocalAtendimentoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTurnoTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AgendamentoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.DiasAgendadosVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorariosAgendamentoSessaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class DisponibilidadeHorarioAgendamentoSessaoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(DisponibilidadeHorarioAgendamentoSessaoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * Classe utilizada para armazenar temporariamente os horários marcados
	 * 
	 * @author israel.haas
	 *
	 */
	private class HorariosAgenda {

		private HorariosAgendamentoSessaoVO horarioAgendado;
		private boolean marcouHorario;
		private boolean percorreuDia;

		public HorariosAgenda() {
		}

		public HorariosAgendamentoSessaoVO getHorarioAgendado() {
			return horarioAgendado;
		}
		public void setHorarioAgendado(HorariosAgendamentoSessaoVO horarioAgendado) {
			this.horarioAgendado = horarioAgendado;
		}
		public boolean isMarcouHorario() {
			return marcouHorario;
		}
		public void setMarcouHorario(boolean marcouHorario) {
			this.marcouHorario = marcouHorario;
		}
		public boolean isPercorreuDia() {
			return percorreuDia;
		}
		public void setPercorreuDia(boolean percorreuDia) {
			this.percorreuDia = percorreuDia;
		}
	}

	@Inject
	private MptBloqueioDAO mptBloqueioDAO;

	@Inject
	private MptDiaTipoSessaoDAO mptDiaTipoSessaoDAO;

	@Inject
	private MptTurnoTipoSessaoDAO mptTurnoTipoSessaoDAO;

	@Inject
	private MptCaracteristicaTipoSessaoDAO mptCaracteristicaTipoSessaoDAO;

	@Inject
	private MptHorarioSessaoDAO mptHorarioSessaoDAO;

	@Inject
	private MptLocalAtendimentoDAO mptLocalAtendimentoDAO;

	private static final long serialVersionUID = 3987754454140226597L;

	public enum DisponibilidadeHorarioAgendamentoSessaoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_TURNO_NAO_CADASTRADO_AGENDAR_SESSAO, MENSAGEM_ERRO_TIPO_SESSAO_SEM_TURNO, MENSAGEM_SEM_AGENDA_DISPONIVEL_MANUTENCAO;
	}

	public HorariosAgendamentoSessaoVO sugerirNovoAgendamentoSessao(Short tpsSeq, DiasAgendadosVO horario, Date dataTransferencia,
			DominioTipoLocal acomodacao, DominioTurno turno) throws ApplicationBusinessException {

		List<Short> listaAcomodacoes = this.getMptLocalAtendimentoDAO().listarLocaisPorSala(horario.getSeqSala(), acomodacao);
		List<MptTurnoTipoSessao> turnos = this.getMptTurnoTipoSessaoDAO().obterTurnosPorTipoSessaoETurno(tpsSeq, turno);

		if (turnos.isEmpty()) {
			if (turno != null) {
				throw new ApplicationBusinessException(DisponibilidadeHorarioAgendamentoSessaoRNExceptionCode.MENSAGEM_ERRO_TURNO_NAO_CADASTRADO_AGENDAR_SESSAO);

			} else {
				throw new ApplicationBusinessException(DisponibilidadeHorarioAgendamentoSessaoRNExceptionCode.MENSAGEM_ERRO_TIPO_SESSAO_SEM_TURNO);
			}
		}

		if (this.getMptCaracteristicaTipoSessaoDAO().existeCaracteristicaTipoSessao("TUCO", tpsSeq)) {
			MptTurnoTipoSessao trn = new MptTurnoTipoSessao();
			trn.setHoraInicio(turnos.get(0).getHoraInicio());
			trn.setHoraFim(turnos.get(turnos.size() - 1).getHoraFim());

			turnos.clear();
			turnos.add(trn);
		}
		List<MptDiaTipoSessao> diasTipoSessao = this.getMptDiaTipoSessaoDAO().obterDiasPorTipoSessao(tpsSeq);
		List<DominioDiaSemana> diasSemana = this.obterListaDiasSemanaEmDominio(diasTipoSessao);

		if (!diasSemana.contains(CoreUtil.retornaDiaSemana(dataTransferencia))) {
			throw new ApplicationBusinessException(DisponibilidadeHorarioAgendamentoSessaoRNExceptionCode.MENSAGEM_SEM_AGENDA_DISPONIVEL_MANUTENCAO);
		}
		
		if (DateUtil.isDatasIguais(dataTransferencia, DateUtil.truncaData(new Date()))) {
			dataTransferencia = DateUtil.comporDiaHora(dataTransferencia, new Date());
			
		} else {
			dataTransferencia = DateUtil.comporDiaHora(dataTransferencia, turnos.get(0).getHoraInicio());
		}
		Integer horas = DateUtil.getHoras(horario.getTempo());
		Integer minutos = DateUtil.getMinutos(horario.getTempo()).intValue();
		Integer minutosReq = (horas * 60) + minutos;
		
		HorariosAgenda horariosAgenda = new HorariosAgenda();

		this.gerarNovoAgendamentoSessao(listaAcomodacoes, turnos, tpsSeq, horario, acomodacao, dataTransferencia, minutosReq, horariosAgenda);

		return horariosAgenda.getHorarioAgendado();
	}

	private void gerarNovoAgendamentoSessao(List<Short> listaAcomodacoes, List<MptTurnoTipoSessao> turnos, Short tpsSeq,
			DiasAgendadosVO horario, DominioTipoLocal acomodacao, Date dataTransferencia, Integer minutosReq,
			HorariosAgenda horariosAgenda) throws ApplicationBusinessException {

		for (Short loaSeq : listaAcomodacoes) {
			if (!horariosAgenda.isMarcouHorario()) {
				List<AgendamentoSessaoVO> horariosAgendados = this.getMptHorarioSessaoDAO().obterListaHorariosSessaoMarcados(horario.getSeqSala(), tpsSeq, loaSeq, acomodacao, DateUtil.truncaData(dataTransferencia), DateUtil.obterDataComHoraFinal(dataTransferencia));

				if (!horariosAgendados.isEmpty()) {
					horariosAgenda.setPercorreuDia(false);
					this.realizarManutencaoEntreHorariosExistentes(turnos, tpsSeq, horario, acomodacao, dataTransferencia, minutosReq, horariosAgenda, loaSeq, horariosAgendados);
				} else {
					this.realizarManutencaoLivreNoDia(turnos, dataTransferencia, minutosReq, horariosAgenda, tpsSeq, loaSeq, horario);
				}
			} else {
				break;
			}
		}
		// Testa se marcou o horário no dia.
		if (!horariosAgenda.isMarcouHorario()) {
			throw new ApplicationBusinessException(DisponibilidadeHorarioAgendamentoSessaoRNExceptionCode.MENSAGEM_SEM_AGENDA_DISPONIVEL_MANUTENCAO);
		}
	}

	private void realizarManutencaoEntreHorariosExistentes(List<MptTurnoTipoSessao> turnos, Short tpsSeq, DiasAgendadosVO horario,
			DominioTipoLocal acomodacao, Date dataTransferencia, Integer minutosReq, HorariosAgenda horariosAgenda,
			Short loaSeq, List<AgendamentoSessaoVO> horariosAgendados) {

		int count = 0;
		for (AgendamentoSessaoVO agendado : horariosAgendados) {
			if (!horariosAgenda.isMarcouHorario() && !horariosAgenda.isPercorreuDia()) {
				count++;
				// Percorre os turnos...
				for (MptTurnoTipoSessao turno : turnos) {
					Date horaInicialTurno = null;
					if (DateUtil.validaHoraMenorIgual(turno.getHoraInicio(), dataTransferencia)) {
						horaInicialTurno = dataTransferencia;

					} else {
						horaInicialTurno = DateUtil.comporDiaHora(dataTransferencia, turno.getHoraInicio());
					}
					Date horaFinalTurno = DateUtil.comporDiaHora(dataTransferencia, turno.getHoraFim());
					Integer minutosDisp = DateUtil.obterQtdMinutosEntreDuasDatas(horaInicialTurno, agendado.getDataInicio());
					// Se tiver tempo disponível marca horário.
					if (minutosReq.compareTo(minutosDisp) <= 0) {
						Date dataIni = DateUtil.comporDiaHora(dataTransferencia, horaInicialTurno);
						Date dtFim = DateUtil.adicionaMinutos(dataIni, minutosReq);

						// Só marca o horário se não ultrapassar o limite do turno e se não possuir bloqueio.
						if (DateUtil.validaHoraMenorIgual(dtFim, horaFinalTurno) && !this.getMptBloqueioDAO().possuiLocaisIndisponiveisNoPeriodo(tpsSeq, loaSeq, dataIni, dtFim)) {
							// Verifica se possui o horário no ambulatório.
							if (!this.possuiHorarioAgendado(horario.getSeqSala(), tpsSeq, loaSeq, acomodacao, dataIni, dtFim, DateUtil.truncaData(dataTransferencia), DateUtil.obterDataComHoraFinal(dataTransferencia))) {

								horariosAgenda.setHorarioAgendado(this.montaHorariosAgendamentoSessaoVO(loaSeq, horario, dataIni, dtFim));

								horariosAgenda.setMarcouHorario(true);
								break;
							}
						}

					} else {
						minutosDisp = DateUtil.obterQtdMinutosEntreDuasDatas(agendado.getDataFim(), horaFinalTurno);
						if (minutosReq.compareTo(minutosDisp) <= 0) {
							Date dataIni = null;
							// Se a hora é menor que o início do turno então seta o início do turno como dataIni.
							if (DateUtil.validaHoraMaior(horaInicialTurno, agendado.getDataFim())) {
								dataIni = DateUtil.comporDiaHora(dataTransferencia, horaInicialTurno);
							} else {
								dataIni = DateUtil.comporDiaHora(dataTransferencia, agendado.getDataFim());
							}
							Date dtFim = DateUtil.adicionaMinutos(dataIni, minutosReq);

							// Só marca o horário se não ultrapassar o limite do turno e se não possuir bloqueio.
							if (DateUtil.validaHoraMenorIgual(dtFim, horaFinalTurno) && !this.getMptBloqueioDAO().possuiLocaisIndisponiveisNoPeriodo(tpsSeq, loaSeq, dataIni, dtFim)) {
								// Faz busca no banco pois os próximos itens da lista podem estar utilizando o horário.
								if (horariosAgendados.size() > count && !this.possuiHorarioAgendado(horario.getSeqSala(), tpsSeq, loaSeq, acomodacao, dataIni, dtFim, DateUtil.truncaData(dataTransferencia), DateUtil.obterDataComHoraFinal(dataTransferencia))) {

									horariosAgenda.setHorarioAgendado(this.montaHorariosAgendamentoSessaoVO(loaSeq, horario, dataIni, dtFim));

									horariosAgenda.setMarcouHorario(true);
									break;

									// Último item da lista; pode inserir livremente.
								} else if (horariosAgendados.size() == count) {
									horariosAgenda.setHorarioAgendado(this.montaHorariosAgendamentoSessaoVO(loaSeq, horario, dataIni, dtFim));

									horariosAgenda.setMarcouHorario(true);
									break;
								}
							}
						}
					}
				}
			} else {
				break;
			}
		}
		horariosAgenda.setPercorreuDia(true);
	}

	private boolean possuiHorarioAgendado(Short salaSeq, Short tpsSeq, Short loaSeq, DominioTipoLocal acomodacao,
			Date dataIni, Date dtFim, Date inicioTurno, Date fimTurno) {

		List<AgendamentoSessaoVO> horariosAgendados = this.getMptHorarioSessaoDAO().obterListaHorariosSessaoMarcados(salaSeq, tpsSeq, loaSeq, acomodacao, inicioTurno, fimTurno);

		for (AgendamentoSessaoVO vo : horariosAgendados) {
			if (DateUtil.isDatasIguais(vo.getDataInicio(), dataIni)) {
				return true;

			} else if (DateUtil.validaHoraMaior(vo.getDataInicio(), dataIni) && DateUtil.validaHoraMaior(dtFim, vo.getDataInicio())) {
				return true;

			} else if (DateUtil.validaHoraMaior(dataIni, vo.getDataInicio()) && this.getMptHorarioSessaoDAO().possuiHorarioAgendado(salaSeq, tpsSeq, loaSeq, acomodacao, dataIni, dtFim)) {
				return true;
			}
		}
		return false;
	}

	private void realizarManutencaoLivreNoDia(List<MptTurnoTipoSessao> turnos, Date dataTransferencia, Integer minutosReq,
			HorariosAgenda horariosAgenda, Short tpsSeq, Short loaSeq, DiasAgendadosVO horario) {
		// Percorre os turnos...
		for (MptTurnoTipoSessao turno : turnos) {
			Date horaInicialTurno = null;
			if (DateUtil.validaHoraMenorIgual(turno.getHoraInicio(), dataTransferencia)) {
				horaInicialTurno = dataTransferencia;

			} else {
				horaInicialTurno = DateUtil.comporDiaHora(dataTransferencia, turno.getHoraInicio());
			}
			Date horaFinalTurno = DateUtil.comporDiaHora(dataTransferencia, turno.getHoraFim());
			Integer minutosDisp = DateUtil.obterQtdMinutosEntreDuasDatas(horaInicialTurno, horaFinalTurno);
			// Se tiver tempo disponível marca horário.
			if (minutosReq.compareTo(minutosDisp) <= 0) {
				Date dataIni = DateUtil.comporDiaHora(dataTransferencia, horaInicialTurno);
				Date dtFim = DateUtil.adicionaMinutos(dataIni, minutosReq);
				// Verifica se não possui bloqueios
				if (!this.getMptBloqueioDAO().possuiLocaisIndisponiveisNoPeriodo(tpsSeq, loaSeq, dataIni, dtFim)) {

					horariosAgenda.setHorarioAgendado(this.montaHorariosAgendamentoSessaoVO(loaSeq, horario, dataIni, dtFim));

					horariosAgenda.setMarcouHorario(true);
					break;
				}
			}
		}
	}

	private HorariosAgendamentoSessaoVO montaHorariosAgendamentoSessaoVO(Short loaSeq, DiasAgendadosVO horario, Date dataIni, Date dtFim) {
		HorariosAgendamentoSessaoVO vo = new HorariosAgendamentoSessaoVO();
		vo.setLoaSeq(loaSeq);
		MptLocalAtendimento local = this.getMptLocalAtendimentoDAO().obterPorChavePrimaria(loaSeq);
		vo.setAcomodacao(local.getTipoLocal());
		vo.setDia(horario.getDia());
		vo.setTempo(horario.getTempo());
		vo.setCiclo(horario.getSeqCiclo());
		vo.setSesSeq(horario.getSesSeq());
		vo.setDataInicio(dataIni);
		vo.setDataFim(dtFim);
		vo.setIndSituacao(DominioSituacaoHorarioSessao.M);

		return vo;
	}

	private List<DominioDiaSemana> obterListaDiasSemanaEmDominio(List<MptDiaTipoSessao> diasTipoSessao) {
		List<DominioDiaSemana> listaRetorno = new ArrayList<DominioDiaSemana>();
		for (MptDiaTipoSessao dts : diasTipoSessao) {
			switch (dts.getDia()) {
			case (byte) 1:
				listaRetorno.add(DominioDiaSemana.DOMINGO);
				break;
			case (byte) 2:
				listaRetorno.add(DominioDiaSemana.SEGUNDA);
				break;
			case (byte) 3:
				listaRetorno.add(DominioDiaSemana.TERCA);
				break;
			case (byte) 4:
				listaRetorno.add(DominioDiaSemana.QUARTA);
				break;
			case (byte) 5:
				listaRetorno.add(DominioDiaSemana.QUINTA);
				break;
			case (byte) 6:
				listaRetorno.add(DominioDiaSemana.SEXTA);
				break;
			case (byte) 7:
				listaRetorno.add(DominioDiaSemana.SABADO);
				break;
			}
		}
		return listaRetorno;
	}

	public MptBloqueioDAO getMptBloqueioDAO() {
		return mptBloqueioDAO;
	}

	public MptDiaTipoSessaoDAO getMptDiaTipoSessaoDAO() {
		return mptDiaTipoSessaoDAO;
	}

	public MptTurnoTipoSessaoDAO getMptTurnoTipoSessaoDAO() {
		return mptTurnoTipoSessaoDAO;
	}

	public MptCaracteristicaTipoSessaoDAO getMptCaracteristicaTipoSessaoDAO() {
		return mptCaracteristicaTipoSessaoDAO;
	}

	public MptHorarioSessaoDAO getMptHorarioSessaoDAO() {
		return mptHorarioSessaoDAO;
	}

	public MptLocalAtendimentoDAO getMptLocalAtendimentoDAO() {
		return mptLocalAtendimentoDAO;
	}
}
