package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptAgendamentoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptCaracteristicaTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptHorarioSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptLocalAtendimentoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTurnoTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AcomodacaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorarioAcomodacaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class AgendaDetalhadaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AgendaDetalhadaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	private class GeradorId {
		private Integer id;
		private HorarioAcomodacaoVO itemAnterior;
		private boolean percorreuTurno;
		
		public GeradorId(Integer id, HorarioAcomodacaoVO itemAnterior, boolean percorreuTurno) {
			this.id = id;
			this.itemAnterior = itemAnterior;
			this.percorreuTurno = percorreuTurno;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public HorarioAcomodacaoVO getItemAnterior() {
			return itemAnterior;
		}

		public void setItemAnterior(HorarioAcomodacaoVO itemAnterior) {
			this.itemAnterior = itemAnterior;
		}

		public boolean isPercorreuTurno() {
			return percorreuTurno;
		}

		public void setPercorreuTurno(boolean percorreuTurno) {
			this.percorreuTurno = percorreuTurno;
		}
	}
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private MptTipoSessaoDAO mptTipoSessaoDAO;
	
	@Inject
	private MptHorarioSessaoDAO mptHorarioSessaoDAO;
	
	@Inject
	private MptLocalAtendimentoDAO mptLocalAtendimentoDAO;
	
	@Inject
	private MptAgendamentoSessaoDAO mptAgendamentoSessaoDAO;
	
	@Inject
	private MptTurnoTipoSessaoDAO mptTurnoTipoSessaoDAO;
	
	@Inject
	private MptCaracteristicaTipoSessaoDAO mptCaracteristicaTipoSessaoDAO;
	
	private static final String QUEBRA_LINHA = "<br/>";
	
	private static final long serialVersionUID = -8031546926159862626L;
	
	public List<AcomodacaoVO> obterListaHorariosAgendadosPorAcomodacao(Short tpsSeq, Short salSeq, Date dataMapeamento) {
		List<AcomodacaoVO> listaAcomodacoes = this.getMptLocalAtendimentoDAO().pesquisarAcomodacaoVOPorSala(salSeq);
		
		GeradorId geradorId = new GeradorId(1, null, false);
		
		List<MptTurnoTipoSessao> turnos = this.getMptTurnoTipoSessaoDAO().obterTurnosPorTipoSessaoOrdenado(tpsSeq);
		Date inicioTurno = DateUtil.comporDiaHora(dataMapeamento, turnos.get(0).getHoraInicio());
		Date fimTurno = DateUtil.comporDiaHora(dataMapeamento, turnos.get(turnos.size()-1).getHoraFim());
		
		for (AcomodacaoVO acomodacao : listaAcomodacoes) {
			List<HorarioAcomodacaoVO> listaHorariosReservados = obterListaHorariosReservados(salSeq, inicioTurno, fimTurno, acomodacao, geradorId);
			List<HorarioAcomodacaoVO> listaHorariosMarcados = obterListaHorariosMarcados(salSeq, inicioTurno, fimTurno, acomodacao, geradorId);
			
			acomodacao.setListaHorariosReservados(listaHorariosReservados);
			acomodacao.setListaHorariosMarcados(listaHorariosMarcados);
			
			List<HorarioAcomodacaoVO> listaHorariosReservadosEMarcados = new ArrayList<HorarioAcomodacaoVO>();
			listaHorariosReservadosEMarcados.addAll(listaHorariosReservados);
			listaHorariosReservadosEMarcados.addAll(listaHorariosMarcados);
			
			Collections.sort(listaHorariosReservadosEMarcados, new Comparator<HorarioAcomodacaoVO>() {
				@Override
				public int compare(HorarioAcomodacaoVO o1, HorarioAcomodacaoVO o2) {
					return o1.getDataInicio().compareTo(o2.getDataInicio());
				}
			});
			
			List<HorarioAcomodacaoVO> listaHorariosLivres = this.obterListaHorariosLivres(geradorId, dataMapeamento,
					tpsSeq, listaHorariosReservadosEMarcados);
			acomodacao.setListaHorariosLivres(listaHorariosLivres);
		}
		return listaAcomodacoes;
	}

	private List<HorarioAcomodacaoVO> obterListaHorariosReservados(Short salSeq, Date inicioTurno, Date fimTurno, AcomodacaoVO acomodacao, GeradorId geradorId) {
		List<HorarioAcomodacaoVO> listaHorariosReservados = this.getMptAgendamentoSessaoDAO()
				.obterListaHorariosReservadosMarcados(salSeq, acomodacao.getLoaSeq(), inicioTurno, fimTurno, true);
		
		for (HorarioAcomodacaoVO vo : listaHorariosReservados) {
			vo.setId(geradorId.getId());
			AipPacientes paciente = this.getPacienteFacade().obterAipPacientesPorChavePrimaria(vo.getPacCodigo());
			vo.setDescricaoHorario(this.obterDescricaoHorario(vo.getProtocolo(), paciente.getNome()));
			vo.setHintHorario(this.obterHintHorarioSimples(paciente.getProntuario(), paciente.getNome(), vo.getProtocolo(), vo.getCiclo(), vo.getDia()));
			
			geradorId.setId(geradorId.getId() + 1);
		}
		return listaHorariosReservados;
	}
	
	private List<HorarioAcomodacaoVO> obterListaHorariosMarcados(Short salSeq, Date inicioTurno, Date fimTurno, AcomodacaoVO acomodacao, GeradorId geradorId) {
		List<HorarioAcomodacaoVO> listaHorariosMarcados = this.getMptAgendamentoSessaoDAO()
				.obterListaHorariosReservadosMarcados(salSeq, acomodacao.getLoaSeq(), inicioTurno, fimTurno, false);
		
		List<HorarioAcomodacaoVO> listaFinal = new ArrayList<HorarioAcomodacaoVO>();
		HorarioAcomodacaoVO itemAnterior = null;
		
		for (HorarioAcomodacaoVO vo : listaHorariosMarcados) {
			AipPacientes paciente = this.getPacienteFacade().obterAipPacientesPorChavePrimaria(vo.getPacCodigo());
			
			if (itemAnterior == null || !DateUtil.isDatasIguais(itemAnterior.getDataInicio(), vo.getDataInicio())) {
				vo.setId(geradorId.getId());
				vo.setDescricaoHorario(this.obterDescricaoHorario(vo.getProtocolo(), paciente.getNome()));
				vo.setHintHorario(this.obterHintHorarioSimples(paciente.getProntuario(), paciente.getNome(), vo.getProtocolo(), vo.getCiclo(), vo.getDia()));
				itemAnterior = vo;
				
				listaFinal.add(vo);
				
			} else {
				listaFinal.remove(itemAnterior);
				itemAnterior.setHintHorario(this.obterHintHorarioComposto(paciente.getProntuario(), paciente.getNome(),
						itemAnterior.getProtocolo(), vo.getProtocolo(), itemAnterior.getCiclo(), itemAnterior.getDia()));
				
				listaFinal.add(itemAnterior);
				
			}
			geradorId.setId(geradorId.getId() + 1);
		}
		return listaFinal;
	}
	
	private List<HorarioAcomodacaoVO> obterListaHorariosLivres(GeradorId geradorId, Date dataMapeamento, Short tpsSeq,
			List<HorarioAcomodacaoVO> listaHorariosReservadosEMarcados) {
		
		List<MptTurnoTipoSessao> turnos = this.getMptTurnoTipoSessaoDAO().obterTurnosPorTipoSessaoOrdenado(tpsSeq);
		if (this.getMptCaracteristicaTipoSessaoDAO().existeCaracteristicaTipoSessao("TUCO", tpsSeq)) {
			MptTurnoTipoSessao trn = new MptTurnoTipoSessao();
			trn.setHoraInicio(turnos.get(0).getHoraInicio());
			trn.setHoraFim(turnos.get(turnos.size()-1).getHoraFim());
			
			turnos.clear();
			turnos.add(trn);
		}
		
		List<HorarioAcomodacaoVO> listaHorariosLivres = new ArrayList<HorarioAcomodacaoVO>();
		List<HorarioAcomodacaoVO> listaRemovidos = new ArrayList<HorarioAcomodacaoVO>();
		
		if (!listaHorariosReservadosEMarcados.isEmpty()) {
			for (MptTurnoTipoSessao turno : turnos) {
				geradorId.setPercorreuTurno(false);
				// Remove todos os horários já tratados.
				listaHorariosReservadosEMarcados.removeAll(listaRemovidos);
				listaRemovidos.clear();
				
				for (HorarioAcomodacaoVO horario : listaHorariosReservadosEMarcados) {
					// Já gerou pelo menos um horário livre, verifica se ainda existe horário livre para o turno.
					if (geradorId.getItemAnterior() != null) {
						HorarioAcomodacaoVO itemAnterior = geradorId.getItemAnterior();
						
						if (DateUtil.validaHoraMaior(horario.getDataInicio(), turno.getHoraFim())) {
							listaHorariosLivres.add(this.popularHorarioAcomodacaoVO(geradorId, dataMapeamento, itemAnterior.getDataFim(), turno.getHoraFim()));
							geradorId.setItemAnterior(null);
							geradorId.setPercorreuTurno(true);
							break;
							
						// Teste se existe tempo livre entre os horários marcados no turno
						} else if (DateUtil.validaHoraMaior(horario.getDataInicio(), itemAnterior.getDataFim())) {
							listaHorariosLivres.add(this.popularHorarioAcomodacaoVO(geradorId, dataMapeamento, itemAnterior.getDataFim(), horario.getDataInicio()));
							
							if (DateUtil.isDatasIguais(DateUtil.comporDiaHora(dataMapeamento, turno.getHoraFim()), horario.getDataFim())) {
								listaRemovidos.add(horario);
								geradorId.setItemAnterior(null);
								geradorId.setPercorreuTurno(true);
								break;
								
							// Verifica se horário pertence ao próximo turno
							} else if (DateUtil.isDatasIguais(DateUtil.comporDiaHora(dataMapeamento, turno.getHoraFim()), horario.getDataInicio())) {
								geradorId.setItemAnterior(null);
								geradorId.setPercorreuTurno(true);
								break;
							}
						} else if (DateUtil.isDatasIguais(DateUtil.comporDiaHora(dataMapeamento, turno.getHoraFim()), horario.getDataFim())) {
							listaRemovidos.add(horario);
							geradorId.setItemAnterior(null);
							geradorId.setPercorreuTurno(true);
							break;
						}
					} else
					// Primeiro horário marcado no dia é maior que o início do turno.
					// Neste caso deve gerar um horário "livre" respeitando as regras abaixo.
					if (DateUtil.validaHoraMaior(horario.getDataInicio(), turno.getHoraInicio())) {
						HorarioAcomodacaoVO vo = new HorarioAcomodacaoVO();
						vo.setId(geradorId.getId());
						geradorId.setId(geradorId.getId() + 1);
						// Se primeiro horário marcado for em outro turno, gera horário livre para o turno todo.
						if (DateUtil.validaHoraMaior(horario.getDataInicio(), turno.getHoraFim())) {
							vo.setDataInicio(DateUtil.comporDiaHora(dataMapeamento, turno.getHoraInicio()));
							vo.setDataFim(DateUtil.comporDiaHora(dataMapeamento, turno.getHoraFim()));
							vo.setDescricaoHorario(this.obterHintHorarioLivre(turno.getHoraInicio(), turno.getHoraFim()));
							vo.setHintHorario(this.obterHintHorarioLivre(turno.getHoraInicio(), turno.getHoraFim()));
							listaHorariosLivres.add(vo);
							geradorId.setItemAnterior(null);
							geradorId.setPercorreuTurno(true);
							break;
							
						} else {
							vo.setDataInicio(DateUtil.comporDiaHora(dataMapeamento, turno.getHoraInicio()));
							vo.setDataFim(horario.getDataInicio());
							vo.setDescricaoHorario(this.obterHintHorarioLivre(turno.getHoraInicio(), horario.getDataInicio()));
							vo.setHintHorario(this.obterHintHorarioLivre(turno.getHoraInicio(), horario.getDataInicio()));
							listaHorariosLivres.add(vo);
							
							if (DateUtil.isDatasIguais(DateUtil.comporDiaHora(dataMapeamento, turno.getHoraFim()), horario.getDataFim())) {
								listaRemovidos.add(horario);
								geradorId.setItemAnterior(null);
								geradorId.setPercorreuTurno(true);
								break;
								
							// Verifica se horário pertence ao próximo turno
							} else if (DateUtil.isDatasIguais(DateUtil.comporDiaHora(dataMapeamento, turno.getHoraFim()), horario.getDataInicio())) {
								geradorId.setItemAnterior(null);
								geradorId.setPercorreuTurno(true);
								break;
							}
						}
					}
					geradorId.setItemAnterior(horario);
					listaRemovidos.add(horario);
				}
				// Não percorreu todo o turno, preenche o que sobrou com horário livre
				if (!geradorId.isPercorreuTurno()) {
					if (!listaHorariosReservadosEMarcados.isEmpty()) {
						HorarioAcomodacaoVO itemAnterior = geradorId.getItemAnterior();
						// Só preenche se possuir tempo disponível.
						if (DateUtil.validaDataMenor(itemAnterior.getDataFim(), DateUtil.comporDiaHora(dataMapeamento, turno.getHoraFim()))) {
							listaHorariosLivres.add(this.popularHorarioAcomodacaoVO(geradorId, dataMapeamento, itemAnterior.getDataFim(), turno.getHoraFim()));
						}
						
					} else {
						listaHorariosLivres.add(this.popularHorarioAcomodacaoVO(geradorId, dataMapeamento, turno.getHoraInicio(), turno.getHoraFim()));
					}
				}
				
			}
			geradorId.setItemAnterior(null);
		} else {
			for (MptTurnoTipoSessao turno : turnos) {
				listaHorariosLivres.add(this.popularHorarioAcomodacaoVO(geradorId, dataMapeamento, turno.getHoraInicio(), turno.getHoraFim()));
			}
		}
		return listaHorariosLivres;
	}
	
	private HorarioAcomodacaoVO popularHorarioAcomodacaoVO(GeradorId geradorId, Date dataMapeamento, Date dataInicio, Date dataFim) {
		HorarioAcomodacaoVO vo = new HorarioAcomodacaoVO();
		vo.setId(geradorId.getId());
		geradorId.setId(geradorId.getId() + 1);
		vo.setDescricaoHorario(this.obterHintHorarioLivre(dataInicio, dataFim));
		vo.setDataInicio(DateUtil.comporDiaHora(dataMapeamento, dataInicio));
		vo.setDataFim(DateUtil.comporDiaHora(dataMapeamento, dataFim));
		vo.setHintHorario(this.obterHintHorarioLivre(dataInicio, dataFim));
		
		return vo;
	}
	
	private String obterDescricaoHorario(String protocolo, String nomePaciente) {
		String retorno = null;
		if (protocolo != null) {
			retorno = protocolo.concat(QUEBRA_LINHA).concat(nomePaciente);
			
		} else {
			retorno = nomePaciente;
		}
		return retorno;
	}
	
	private String obterHintHorarioSimples(Integer prontuario, String nomePaciente, String protocolo, Short ciclo, Short dia) {
		protocolo = protocolo != null ? protocolo : "";
		return "Prontuário: ".concat(CoreUtil.formataProntuario(prontuario)).concat(QUEBRA_LINHA)
				.concat("Paciente: ").concat(nomePaciente).concat(QUEBRA_LINHA)
				.concat("Protocolo: ").concat(protocolo).concat(QUEBRA_LINHA)
				.concat("Ciclo: ").concat(ciclo != null ? ciclo.toString() : "").concat(QUEBRA_LINHA)
				.concat("Dia: ").concat(dia != null ? dia.toString() : "");
	}
	
	private String obterHintHorarioComposto(Integer prontuario, String nomePaciente, String protocoloAnterior,
			String protocoloNovo, Short ciclo, Short dia) {
		
		return "Prontuário: ".concat(CoreUtil.formataProntuario(prontuario)).concat(QUEBRA_LINHA)
				.concat("Paciente: ").concat(nomePaciente).concat(QUEBRA_LINHA)
				.concat("Protocolos: ").concat(protocoloAnterior).concat(", ").concat(protocoloNovo).concat(QUEBRA_LINHA)
				.concat("Ciclo: ").concat(ciclo != null ? ciclo.toString() : "").concat(QUEBRA_LINHA)
				.concat("Dia: ").concat(dia != null ? dia.toString() : "");
	}
	
	private String obterHintHorarioLivre(Date horaInicio, Date horaFim) {
		String hrIni = DateUtil.obterDataFormatada(horaInicio, "HH:mm");
		String hrFim = DateUtil.obterDataFormatada(horaFim, "HH:mm");
		
		return "Horário livre: ".concat(hrIni).concat(" - ").concat(hrFim);
	}
	
	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public MptTipoSessaoDAO getMptTipoSessaoDAO() {
		return mptTipoSessaoDAO;
	}
	
	public MptHorarioSessaoDAO getMptHorarioSessaoDAO() {
		return mptHorarioSessaoDAO;
	}

	public MptLocalAtendimentoDAO getMptLocalAtendimentoDAO() {
		return mptLocalAtendimentoDAO;
	}

	public MptAgendamentoSessaoDAO getMptAgendamentoSessaoDAO() {
		return mptAgendamentoSessaoDAO;
	}

	public MptTurnoTipoSessaoDAO getMptTurnoTipoSessaoDAO() {
		return mptTurnoTipoSessaoDAO;
	}
	
	public MptCaracteristicaTipoSessaoDAO getMptCaracteristicaTipoSessaoDAO() {
		return mptCaracteristicaTipoSessaoDAO;
	}
}
