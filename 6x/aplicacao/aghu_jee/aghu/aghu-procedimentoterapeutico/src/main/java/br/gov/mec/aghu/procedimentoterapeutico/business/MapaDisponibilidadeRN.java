package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.MptHorarioSessao;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptAgendamentoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptHorarioSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTurnoTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MapaDisponibilidadeVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MapaDisponibilidadeRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1569200352799567206L;

	private static final Log LOG = LogFactory.getLog(MapaDisponibilidadeRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private MptAgendamentoSessaoDAO mptAgendamentoSessaoDAO;
	
	@Inject
	private MptHorarioSessaoDAO mptHorarioSessaoDAO;
	
	@Inject
	private MptTurnoTipoSessaoDAO mptTurnoTipoSessaoDAO;
	
	/**
	 * Consulta principal, retorna lista de Mapa de Disponibilidade. 
	 */
	public List<MapaDisponibilidadeVO> consultarMapaDisponibilidade(Short tipoSessaoSeq, Short salaSeq, Date dataInicio, DominioTurno turno) 
			throws ParseException {
		List<MapaDisponibilidadeVO> listaVO = null;
		List<String> listaDataString = this.mptHorarioSessaoDAO.consultarDiasMapa(salaSeq, dataInicio, turno);
		if (listaDataString != null && !listaDataString.isEmpty()) {
			// C6
			List<MptTurnoTipoSessao> listaTurnos = this.mptTurnoTipoSessaoDAO.obterHorariosConfiguradosNosTurnos(tipoSessaoSeq, turno);
			listaVO = new ArrayList<MapaDisponibilidadeVO>();
			String subSelect = montarSubSelectSoma(salaSeq);
			for (String dataString : listaDataString) {
				Date dataVO = DateUtils.parseDate(dataString, new String[] { "dd/MM/yyyy" });
				MapaDisponibilidadeVO objetoVO = obterObjetoMapaDisponibilidadeVO(salaSeq, dataVO, turno, subSelect, listaTurnos);
				listaVO.add(objetoVO);
			}
		} 
		return listaVO;
	}

	/**
	 * Monta sub select utilizado nas consultas C9 e C10.
	 */
	private String montarSubSelectSoma(Short salaSeq) {
		StringBuilder sb = new StringBuilder(" (select sum((HR_FIM - HR_INICIO) * 1440) ")
		.append(" from AGH.MPT_TURNO_TIPO_SESSAO TTS ")
		.append(" inner join AGH.MPT_TIPO_SESSAO TSE on TSE.SEQ = TTS.TPS_SEQ ")
		.append(" inner join AGH.MPT_SALAS SAL on SAL.TPS_SEQ = TSE.SEQ ")
		.append(" inner join AGH.MPT_LOCAL_ATENDIMENTO LAT on LAT.SEQ_SAL = SAL.SEQ ")
		.append(" where SAL.SEQ = ").append(salaSeq)
		.append(" and SAL.IND_SITUACAO = 'A' and LAT.IND_SITUACAO = 'A') ");
		return sb.toString();
	}
	
	/**
	 * Obtem os valores das variaveis para criar um objeto do tipo Mapa de Disponibilidade. 
	 */
	private MapaDisponibilidadeVO obterObjetoMapaDisponibilidadeVO(Short salaSeq, Date dataInicio, DominioTurno turno, String subSelect, List<MptTurnoTipoSessao> listaTurnos) {
		
		MapaDisponibilidadeVO retorno = new MapaDisponibilidadeVO();
		retorno.setDataInicio(dataInicio);
		// C9
		Object[] minutosDisponiveis = this.mptAgendamentoSessaoDAO.obterMinutosDisponiveis(salaSeq, dataInicio, turno, subSelect);
		if (minutosDisponiveis != null) {
			Integer somaDifHoras = NumberUtil.getIntegerFromNumericObject(minutosDisponiveis[0]);
			Integer somaDifDatas = NumberUtil.getIntegerFromNumericObject(minutosDisponiveis[1]);
			retorno.setMinutosDisponiveis(somaDifHoras - somaDifDatas);
		}
		// C10
		Object[] numPacienteDisp = this.mptAgendamentoSessaoDAO.obterNumeroPacientesDisponibilidade(salaSeq, dataInicio, turno, subSelect);
		if (numPacienteDisp != null) {
			Integer somaDifDatas = NumberUtil.getIntegerFromNumericObject(numPacienteDisp[0]);
			Integer somaDifHoras = NumberUtil.getIntegerFromNumericObject(numPacienteDisp[1]);
			retorno.setPercentual(somaDifDatas / somaDifHoras);
			retorno.setNumeroPacientes(NumberUtil.getIntegerFromNumericObject(numPacienteDisp[2]));
		}
		// C7
		retorno.setExtras(this.mptAgendamentoSessaoDAO.obterQuantidadePacientesExtras(salaSeq, dataInicio, turno));
		// RN04
		retorno.setTempoSerie(this.obterTempoSerieMapaDisponibilidade(salaSeq, dataInicio, turno, listaTurnos));
		
		return retorno;
	}

	/**
	 * Obtem o valor para popular a variavel Tempo Serie do Mapa de Disponibilidade.
	 */
	private Integer obterTempoSerieMapaDisponibilidade(Short salaSeq, Date dataInicio, DominioTurno turno, List<MptTurnoTipoSessao> listaTurnos) {
		Integer tempoSerie = 0;
		if (listaTurnos != null && !listaTurnos.isEmpty()) {
			MapaDisponibilidadeTurnos mTurno = obterMapaDisponibilidadeTurnos(dataInicio, listaTurnos);
			
			// C5
			List<MptHorarioSessao> listaHorarios = this.mptHorarioSessaoDAO.obterHorariosAgendadosNoDia(salaSeq, dataInicio, turno);
			if (listaHorarios != null && !listaHorarios.isEmpty()) {
				
				MapaDisponibilidadeControle manhaControle = new MapaDisponibilidadeControle();
				MapaDisponibilidadeControle tardeControle = new MapaDisponibilidadeControle();
				MapaDisponibilidadeControle noiteControle = new MapaDisponibilidadeControle();

				manhaControle.setInicio(mTurno.getManhaInicio());
				manhaControle.setTempo(0);
				tardeControle.setInicio(mTurno.getTardeInicio());
				tardeControle.setTempo(0);
				noiteControle.setInicio(mTurno.getNoiteInicio());
				noiteControle.setTempo(0);
				
				for (MptHorarioSessao hse : listaHorarios) {
					if (hse.getAgendamentoSessao() != null && hse.getAgendamentoSessao().getTurno() != null) {
						if (hse.getAgendamentoSessao().getTurno().equals(DominioTurno.M)) {
							manhaControle.setFim(hse.getDataInicio());
							manhaControle.setTempo(this.obterMaiorTempoSerieDaSessaoPorTurno(manhaControle));
							manhaControle.setInicio(hse.getDataFim());
						} else if (hse.getAgendamentoSessao().getTurno().equals(DominioTurno.T)) {
							tardeControle.setFim(hse.getDataInicio());
							tardeControle.setTempo(this.obterMaiorTempoSerieDaSessaoPorTurno(tardeControle));
							tardeControle.setInicio(hse.getDataFim());
						} else if (hse.getAgendamentoSessao().getTurno().equals(DominioTurno.N)) {
							noiteControle.setFim(hse.getDataInicio());
							noiteControle.setTempo(this.obterMaiorTempoSerieDaSessaoPorTurno(noiteControle));
							noiteControle.setInicio(hse.getDataFim());
						}  
					}
				}
				
				manhaControle.setFim(mTurno.getManhaFim());
				manhaControle.setTempo(this.obterMaiorTempoSerieDaSessaoPorTurno(manhaControle));
				tardeControle.setFim(mTurno.getTardeFim());
				tardeControle.setTempo(this.obterMaiorTempoSerieDaSessaoPorTurno(tardeControle));
				noiteControle.setFim(mTurno.getNoiteFim());
				noiteControle.setTempo(this.obterMaiorTempoSerieDaSessaoPorTurno(noiteControle));
				tempoSerie = this.obterMaiorTempoSerieDoDia(manhaControle.getTempo(), tardeControle.getTempo(), noiteControle.getTempo());
			}
		}
		return tempoSerie;
	}
	
	/**
	 * Obtem o maior tempo em serie de uma sessao entre todos os turnos retornados por C6.
	 */
	private Integer obterMaiorTempoSerieDoDia(Integer manha, Integer tarde, Integer noite) {
		if (manha > tarde) {
			if (manha > noite) {
				return manha;
			} else {
				return noite;
			}
		} else {
			if (tarde > noite) {
				return tarde;
			} else {
				return noite;
			}
		}
	}

	/**
	 * Obtem o maior tempo em serie de uma sessão de um dado turno.
	 */
	private Integer obterMaiorTempoSerieDaSessaoPorTurno(MapaDisponibilidadeControle controle) {
		Integer retorno = controle.getTempo();
		Integer aux = 0;
		if (controle.getInicio() != null && controle.getFim() != null) {
			aux = DateUtil.obterQtdMinutosEntreDuasDatas(controle.getInicio(), controle.getFim());
		}
		if (aux > controle.getTempo()) {
			retorno = aux;
		}
		return retorno;
	}

	/**
	 * Obtem os horarios de inicio e fim para cada turno retornado por C6.
	 */
	private MapaDisponibilidadeTurnos obterMapaDisponibilidadeTurnos(Date dataInicio, List<MptTurnoTipoSessao> listaTurnos) {
		MapaDisponibilidadeTurnos mTurno = new MapaDisponibilidadeTurnos();
		for (MptTurnoTipoSessao turno : listaTurnos) {
			if (turno.getTurno() != null) {
				if (turno.getTurno().equals(DominioTurno.M)) {
					mTurno.setManhaInicio(DateUtil.comporDiaHora(dataInicio, turno.getHoraInicio()));
					mTurno.setManhaFim(DateUtil.comporDiaHora(dataInicio, turno.getHoraFim()));
				} else if (turno.getTurno().equals(DominioTurno.T)) {
					mTurno.setTardeInicio(DateUtil.comporDiaHora(dataInicio, turno.getHoraInicio()));
					mTurno.setTardeFim(DateUtil.comporDiaHora(dataInicio, turno.getHoraFim()));
				} else if (turno.getTurno().equals(DominioTurno.N)) {
					mTurno.setNoiteInicio(DateUtil.comporDiaHora(dataInicio, turno.getHoraInicio()));
					mTurno.setNoiteFim(DateUtil.comporDiaHora(dataInicio, turno.getHoraFim()));
				}
			}
		}
		return mTurno;
	}
	
	/**
	 * Classe auxiliar para armazenamento dos horarios de inicio e fim de cada turno de um dado tipo de sessão.
	 * 
	 * @author rafael.silvestre
	 */
	private class MapaDisponibilidadeTurnos {
		
		private Date manhaInicio;
		private Date manhaFim;
		private Date tardeInicio;
		private Date tardeFim;
		private Date noiteInicio;
		private Date noiteFim;
		
		public Date getManhaInicio() {
			return manhaInicio;
		}
		public void setManhaInicio(Date manhaInicio) {
			this.manhaInicio = manhaInicio;
		}
		public Date getManhaFim() {
			return manhaFim;
		}
		public void setManhaFim(Date manhaFim) {
			this.manhaFim = manhaFim;
		}
		public Date getTardeInicio() {
			return tardeInicio;
		}
		public void setTardeInicio(Date tardeInicio) {
			this.tardeInicio = tardeInicio;
		}
		public Date getTardeFim() {
			return tardeFim;
		}
		public void setTardeFim(Date tardeFim) {
			this.tardeFim = tardeFim;
		}
		public Date getNoiteInicio() {
			return noiteInicio;
		}
		public void setNoiteInicio(Date noiteInicio) {
			this.noiteInicio = noiteInicio;
		}
		public Date getNoiteFim() {
			return noiteFim;
		}
		public void setNoiteFim(Date noiteFim) {
			this.noiteFim = noiteFim;
		}
	}
	
	/**
	 * Classe auxiliar para calculo e armazenamento do tempo máximo disponivel em sequencia de um dado turno.
	 *  
	 * @author rafael.silvestre
	 */
	private class MapaDisponibilidadeControle {
		
		private Date inicio;
		private Date fim;
		private Integer tempo;
		
		public Date getInicio() {
			return inicio;
		}
		public void setInicio(Date inicio) {
			this.inicio = inicio;
		}
		public Date getFim() {
			return fim;
		}
		public void setFim(Date fim) {
			this.fim = fim;
		}
		public Integer getTempo() {
			return tempo;
		}
		public void setTempo(Integer tempo) {
			this.tempo = tempo;
		}
	}
}
