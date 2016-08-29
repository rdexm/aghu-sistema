package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptAgendamentoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptHorarioSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloCicloDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ImpressaoTicketAgendamentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ReservasVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ImprimirTicketAgendamentoRN extends BaseBusiness{
	
	/**
     * Obtem lista de ciclos e aplicar RN6 concatenar com RN07 
	 * 
	 */
	private static final long serialVersionUID = -4262211164654000968L;
	private static final Log LOG = LogFactory.getLog(ImprimirTicketAgendamentoRN.class);
	 public static final String HIFEN_ESPACO = " - ";
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private MptHorarioSessaoDAO mptHorarioSessaoDAO;
	
	@Inject
	private MptProtocoloCicloDAO mptProtocoloCicloDAO;
	
	@Inject
	private MptAgendamentoSessaoDAO mptAgendamentoSessaoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	/**
	 * Obtem lista de ciclos. 
	 */
	public List<ImpressaoTicketAgendamentoVO> obterListaCiclo(Integer codPaciente, List<Integer> listCloSeq, Integer numeroConsulta){
		
		String protocolo = StringUtils.EMPTY;
		
		if (listCloSeq != null && !listCloSeq.isEmpty()) {
			protocolo = obterProtocolos(listCloSeq) + " - ";
		}
		
		List<ImpressaoTicketAgendamentoVO> listaCiclo = new ArrayList<ImpressaoTicketAgendamentoVO>();
		
		List<ImpressaoTicketAgendamentoVO> listaCiclosPacienteInformado = this.mptHorarioSessaoDAO.obterTodosCiclosParaPacienteInformado(codPaciente, null, numeroConsulta);
		
		if (listaCiclosPacienteInformado != null && !listaCiclosPacienteInformado.isEmpty()) {
			for (ImpressaoTicketAgendamentoVO cicloPacienteInformado : listaCiclosPacienteInformado) {
				if (cicloPacienteInformado.getCloCiclo() != null) {
					
					ImpressaoTicketAgendamentoVO ciclo = new ImpressaoTicketAgendamentoVO();
					ciclo.setCloCiclo(cicloPacienteInformado.getCloCiclo());
					ciclo.setCloSeq(cicloPacienteInformado.getCloSeq());
					ciclo.setPtcDescricao(protocolo + cicloPacienteInformado.getCloCiclo().toString());
					listaCiclo.add(ciclo);
				}
			}
			
		}
		return listaCiclo;
	}

	/**
	 * Obtem lista de ciclos Reservas. 
	 */
	public List<ReservasVO> obterListaCicloReservas(Integer codPaciente, List<Integer> listCicloSeq){
		
		String protocolo = StringUtils.EMPTY;
		
		if (listCicloSeq != null && !listCicloSeq.isEmpty()) {
			protocolo = "RESERVA - ";
		}
		
		List<ReservasVO> listaCiclo = new ArrayList<ReservasVO>();
		
		List<ReservasVO> listaCiclosPacienteInformadoReservas = this.mptAgendamentoSessaoDAO.pesquisarConsultasReservas(codPaciente, 8);
		
		if (listaCiclosPacienteInformadoReservas != null && !listaCiclosPacienteInformadoReservas.isEmpty()) {
			ReservasVO ciclo = null;	
			int i = 1;
			for (ReservasVO cicloPacienteInformadoReservas : listaCiclosPacienteInformadoReservas) {
				if (cicloPacienteInformadoReservas.getSeq() != null) {
					ciclo = new ReservasVO();
					ciclo.setSeq(cicloPacienteInformadoReservas.getSeq());
					ciclo.setDescSessao(protocolo + i++);					
					listaCiclo.add(ciclo);
				}
			}			
		}
		return listaCiclo;
	}
	
	/**
	 * Metodo que aplicar RN07 retorna a descrição ou todos os titulos concatenado com '-'
	 * 
	 * @param listRegistroHorariosSessao
	 * @return
	 */
	public String obterProtocolos(List<Integer> listCloSeq){
		
		StringBuffer titulos = new StringBuffer();
		titulos.append("");
		List<ImpressaoTicketAgendamentoVO> listaCiclosPacienteInformado = this.mptProtocoloCicloDAO.obterProtocolosCicloInformado(listCloSeq);
		
		if (listaCiclosPacienteInformado != null && !listaCiclosPacienteInformado.isEmpty()) {
			
			for (ImpressaoTicketAgendamentoVO impressaoTicketAgendamentoVO : listaCiclosPacienteInformado) {
				if (impressaoTicketAgendamentoVO.getPtcDescricao() != null) {
					return impressaoTicketAgendamentoVO.getPtcDescricao();
				}
				else {
					if (impressaoTicketAgendamentoVO.getPtaTitulo() != null) {
						if (titulos != null && titulos.length() > 0) {
							titulos.append('-');
						}
						titulos.append(impressaoTicketAgendamentoVO.getPtaTitulo());
					}
				}
			}
			return titulos.toString();
		}
		return "";
	}

	/**
	 * 
	 * @param paciente
	 * @param codCiclo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String obterTextoTicket(AipPacientes paciente, List<Integer> codCiclo) throws ApplicationBusinessException {

		StringBuffer ticket = new StringBuffer(8000);
		String valorParametroNegrito = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CUPS_IMP_BEMATECH_NEGRITO).getVlrTexto();
		
		//Consulta carrega informações para impressão do ticket
		List<ImpressaoTicketAgendamentoVO> listInformacoesImpressaoTicket = this.mptHorarioSessaoDAO.obterTodosCiclosParaPacienteInformado(paciente.getCodigo(), codCiclo, 5);
		List<ImpressaoTicketAgendamentoVO> listInformacoesDias = this.mptHorarioSessaoDAO.obterHorariosSessaoPorPacientePrescricao(paciente.getCodigo(), codCiclo, 6);

		retornarHospitalAtendimentoPaciente(paciente, ticket, valorParametroNegrito, listInformacoesImpressaoTicket);

		retornarProntuarioProtocoloCiclo(paciente, codCiclo, ticket, listInformacoesImpressaoTicket);

		retornarListaDias(ticket, listInformacoesDias, valorParametroNegrito);

		retornarAvisoEspecialidadeResponsavel(ticket, valorParametroNegrito, listInformacoesImpressaoTicket);

		return ticket.toString();
	}

	/**
	 * 
	 * @param paciente
	 * @param codCiclo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String obterTextoTicketReservadas(AipPacientes paciente, List<Integer> listCicloSeq, Short seqAgendamento) throws ApplicationBusinessException {
		List<ReservasVO> consultaC9 = new ArrayList<ReservasVO>();
		StringBuffer ticket = new StringBuffer(8000);
		String valorParametroNegrito = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CUPS_IMP_BEMATECH_NEGRITO).getVlrTexto();
		
		//Consulta carrega informações para impressão do ticket
		List<ReservasVO> consultaC8 = this.mptAgendamentoSessaoDAO.pesquisarConsultasReservas(paciente.getCodigo(), 8);
		if (consultaC8 != null && !consultaC8.isEmpty()){
			if (seqAgendamento != null){
				consultaC9  = this.mptHorarioSessaoDAO.pesquisarConsultarDiasReservas(seqAgendamento);
			}else{				
				for (ReservasVO reservasVO : consultaC8) {
					consultaC9.addAll(mptHorarioSessaoDAO.pesquisarConsultarDiasReservas(reservasVO.getSeq()));
				}
			}
			String nomeCiclo = StringUtils.EMPTY;
			if (consultaC9.get(0).getCiclo() != null) {
				nomeCiclo = String.valueOf(consultaC9.get(0).getCiclo());
			}
			retornarHospitalAtendimentoPacienteReservas(paciente, ticket, valorParametroNegrito, consultaC8);
			
			retornarProntuarioProtocoloCicloReservas(paciente, listCicloSeq, ticket, consultaC8, nomeCiclo);
			
			retornarListaDiasReservas(ticket, consultaC9, valorParametroNegrito);
			
			retornarAvisoEspecialidadeResponsavelReservas(ticket, valorParametroNegrito, consultaC8);			
			
			adicionarAgendadoPor(ticket, consultaC8, consultaC9);
			
		}
		
		return ticket.toString();
	}

	private void adicionarAgendadoPor(StringBuffer ticket, List<ReservasVO> consultaC8, List<ReservasVO> consultaC9) {
		String responsavel = null;
		
		if(consultaC9 != null && !consultaC9.isEmpty()) {
			if(consultaC9.get(0) != null) {
				responsavel = consultaC9.get(0).getResponsavel();
			}
		} else if (consultaC8 != null && !consultaC8.isEmpty()) {
			if (consultaC8.get(0).getSeq() != null) {				
				responsavel = consultaC8.get(0).getResponsavel();
			}
		}
		
		ticket.append(StringUtils.LF);
		ticket.append("Agendado por ");
		ticket.append(responsavel);
	}

	private void retornarHospitalAtendimentoPaciente(AipPacientes paciente,	StringBuffer ticket, String valorParametroNegrito,
			List<ImpressaoTicketAgendamentoVO> listInformacoesImpressaoTicket)
					throws ApplicationBusinessException {
		iniciarNegrito(ticket, valorParametroNegrito);
		//Nome do Hospital
		String nomeHospital = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
		ticket.append(this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL) != null ? nomeHospital + StringUtils.LF : StringUtils.EMPTY);
		//Local do Atendimento
		if (listInformacoesImpressaoTicket != null && !listInformacoesImpressaoTicket.isEmpty()) {
			ticket.append(listInformacoesImpressaoTicket.get(0).retornaLocalAtendimento() + StringUtils.LF);
		}
		//Nome do Paciente
		ticket.append(paciente.getNome() != null ? paciente.getNome() + StringUtils.LF: StringUtils.EMPTY);
		finalizarNegrito(ticket, valorParametroNegrito);
	}
	
	private void retornarProntuarioProtocoloCiclo(AipPacientes paciente, List<Integer> codCiclo, StringBuffer ticket,
			List<ImpressaoTicketAgendamentoVO> listInformacoesImpressaoTicket) {
		//Prontuário
		ticket.append("Prontuário ");	
		ticket.append(paciente.getProntuario() != null ? paciente.getProntuario() + StringUtils.LF : StringUtils.LF);
		//Protocolo
		ticket.append("Protocolo ");
		if(codCiclo != null){
			String protocolo = obterProtocolos(codCiclo);
			ticket.append(protocolo);
		}
		//Ciclo
		ticket.append(StringUtils.LF);
		ticket.append("Ciclo ");
		if (listInformacoesImpressaoTicket != null && !listInformacoesImpressaoTicket.isEmpty()) {
			ticket.append(listInformacoesImpressaoTicket.get(0).getPteCiclo());
		}
	}
	
	private void retornarListaDias(StringBuffer ticket, List<ImpressaoTicketAgendamentoVO> listInformacoesDias, String valorParametroNegrito) {
		iniciarNegrito(ticket, valorParametroNegrito);
		//Informações de dias
		if (listInformacoesDias != null && !listInformacoesDias.isEmpty()) {
			for (ImpressaoTicketAgendamentoVO element : listInformacoesDias) {
				if (element.getHrsDataInicio() != null) {
					String data = DateUtil.obterDataFormatada(element.getHrsDataInicio(), "dd/MM/yy");
					String hora = DateUtil.obterDataFormatada(element.getHrsDataInicio(), DateConstants.DATE_PATTERN_HORA_MINUTO);
					DominioDiaSemana semana = CoreUtil.retornaDiaSemana(element.getHrsDataInicio());
					ticket.append(StringUtils.LF);
					ticket.append('D');
					ticket.append(element.getHrsDia());
					ticket.append(HIFEN_ESPACO);
					ticket.append(data);
					ticket.append(HIFEN_ESPACO);
					ticket.append(hora);
					ticket.append(HIFEN_ESPACO);
					ticket.append(semana.getDescricao().substring(0,3));
				}
			}
		}
		finalizarNegrito(ticket, valorParametroNegrito);
	}
	
	private void retornarAvisoEspecialidadeResponsavel(StringBuffer ticket,
			String valorParametroNegrito,
			List<ImpressaoTicketAgendamentoVO> listInformacoesImpressaoTicket) {
		if (listInformacoesImpressaoTicket != null && !listInformacoesImpressaoTicket.isEmpty()) {
			//Aviso
			if (listInformacoesImpressaoTicket.get(0).getTpsAviso() != null) {
				iniciarNegrito(ticket, valorParametroNegrito);
				ticket.append(StringUtils.LF);
				ticket.append(listInformacoesImpressaoTicket.get(0).getTpsAviso());
				finalizarNegrito(ticket, valorParametroNegrito);
			}
			//Especialidade
			if (listInformacoesImpressaoTicket.get(0).getEspNomeReduzido() != null) {
				ticket.append(StringUtils.LF);
				ticket.append(listInformacoesImpressaoTicket.get(0).getEspNomeReduzido());
			}
			//Agendado por
			if (listInformacoesImpressaoTicket.get(0).getAgsSeq() != null) {
				
				String responsavel = this.mptAgendamentoSessaoDAO.obterResponsavelPeloAgendamento(listInformacoesImpressaoTicket.get(0).getAgsSeq());
				ticket.append(StringUtils.LF);
				ticket.append("Agendado por ");
				ticket.append(responsavel);
			}
		}
	}
	
	private void iniciarNegrito(StringBuffer ticket, String valorParametroNegrito) {
//			ticket.append((char) 27).append((char) 69);// INICIA NEGRITO
	}
	
	private void finalizarNegrito(StringBuffer ticket,	String valorParametroNegrito) {
//			ticket.append((char) 27).append((char) 70);// FIM NEGRITO
	}
	
	
	private void retornarHospitalAtendimentoPacienteReservas(AipPacientes paciente,	StringBuffer ticket, String valorParametroNegrito,
			List<ReservasVO> listInformacoesImpressaoTicketReservas)
					throws ApplicationBusinessException {
		iniciarNegrito(ticket, valorParametroNegrito);
		//Nome do Hospital
		String nomeHospital = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
		ticket.append(this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL) != null ? nomeHospital + StringUtils.LF : StringUtils.EMPTY);
		//Local do Atendimento
		if (listInformacoesImpressaoTicketReservas != null && !listInformacoesImpressaoTicketReservas.isEmpty()) {
			ticket.append(listInformacoesImpressaoTicketReservas.get(0).retornaLocalAtendimento() + StringUtils.LF);
		}
		//Nome do Paciente
		ticket.append(paciente.getNome() != null ? paciente.getNome() + StringUtils.LF: StringUtils.EMPTY);
		finalizarNegrito(ticket, valorParametroNegrito);
	}
	
	private void retornarProntuarioProtocoloCicloReservas(AipPacientes paciente, List<Integer> listCicloSeq, StringBuffer ticket,
			List<ReservasVO> consultaC8, String nomeCiclo) {
		if (consultaC8 != null && !consultaC8.isEmpty()) {
			
				//Prontuário				
				ticket.append("Prontuário ");	
				ticket.append(paciente.getProntuario() != null ? paciente.getProntuario() + StringUtils.LF : StringUtils.LF);
				//Protocolo
				ticket.append("Protocolo ");
				if(listCicloSeq != null){
					if (consultaC8.get(0).getTitulo() != null){
						String protocolo = consultaC8.get(0).getTitulo();
						ticket.append(protocolo);						
					}
				}
				//Ciclo
				ticket.append(StringUtils.LF);
				ticket.append("Ciclo ");
				if (StringUtils.isNotEmpty(nomeCiclo)) {
					ticket.append(nomeCiclo);					
				}
							
		}
	}
	
	private void retornarListaDiasReservas(StringBuffer ticket, List<ReservasVO> consultaC9, String valorParametroNegrito) {
		iniciarNegrito(ticket, valorParametroNegrito);
		//Informações de dias
		if (consultaC9 != null && !consultaC9.isEmpty()) {
			for (ReservasVO element : consultaC9) {
				if (element.getDataInicio() != null) {
					String data = DateUtil.obterDataFormatada(element.getDataInicio(), "dd/MM/yy");
					String hora = DateUtil.obterDataFormatada(element.getDataInicio(), DateConstants.DATE_PATTERN_HORA_MINUTO);
					DominioDiaSemana semana = CoreUtil.retornaDiaSemana(element.getDataInicio());
					ticket.append(StringUtils.LF);
					ticket.append('D');
					ticket.append(element.getDia());
					ticket.append(HIFEN_ESPACO);
					ticket.append(data);
					ticket.append(HIFEN_ESPACO);
					ticket.append(hora);
					ticket.append(HIFEN_ESPACO);
					ticket.append(semana.getDescricao().substring(0,3));
				}
			}
		}
		finalizarNegrito(ticket, valorParametroNegrito);
	}
	
	private void retornarAvisoEspecialidadeResponsavelReservas(StringBuffer ticket,
			String valorParametroNegrito,
			List<ReservasVO> consultaC8) {
		if (consultaC8 != null && !consultaC8.isEmpty()) {
			//Aviso
			if (consultaC8.get(0).getAviso() != null) {
				iniciarNegrito(ticket, valorParametroNegrito);
				ticket.append(StringUtils.LF);
				ticket.append(consultaC8.get(0).getAviso());
				finalizarNegrito(ticket, valorParametroNegrito);
			}
			//Especialidade
			ticket.append(StringUtils.LF);
			ticket.append("RESERVA");
		}
	}
}
