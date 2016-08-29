package br.gov.mec.aghu.blococirurgico.business;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.AgendaProcedimentosON.AgendaProcedimentosONExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaractSalaEspDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PesquisaAgendarProcedimentosVO;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável pelas regras de BANCO de #22460: Agendar procedimentos eletivo, urgência ou emergência
 * 
 * @author aghu
 * 
 */
@Stateless
public class AgendaProcedimentosRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AgendaProcedimentosRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCaracteristicaSalaCirgDAO mbcCaracteristicaSalaCirgDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcCaractSalaEspDAO mbcCaractSalaEspDAO;

	
	private static final long serialVersionUID = -2599903670048898437L;
	
	public List<PesquisaAgendarProcedimentosVO> pesquisarMbcDisponibilidadeHorario(
			AghUnidadesFuncionais unidadeFuncional,
			MbcSalaCirurgica salaCirurgica, AghEspecialidades especialidade,
			Date dataAgenda) throws ApplicationBusinessException {
		/* Busca dia da semana da data da agenda */
		DominioDiaSemana fetDiaSem = getDiaDaSemanaByDate(dataAgenda);
		
		Short unfSeq= unidadeFuncional != null 	? unidadeFuncional.getSeq() : null;
		Short sciSeqp = salaCirurgica != null 	? salaCirurgica.getId().getSeqp() : null;
		String siglaEspecialidade = especialidade != null 	? especialidade.getSigla() : null;
		
		List<MbcCaracteristicaSalaCirg> salasCirgs = 
				getMbcCaracteristicaSalaCirgDAO()
					.pesquisarMbcSalaCirurgica(unfSeq, Boolean.TRUE,
						DominioSituacao.A, fetDiaSem, DominioSituacao.A,
						sciSeqp);
		desatacharETruncaHorarioInicialMbcCaracteristicaSalaCirg(salasCirgs);
		
		List<Object[]> caracts = getMbcCaractSalaEspDAO()
				.pesquisarSiglaEspETurnoPorUnidadeSalaTurnoDiaSemana(
						unfSeq,	sciSeqp, DominioSituacao.A,
						DominioSituacao.A, null,
						getDiaDaSemanaByDate(dataAgenda),
						Boolean.TRUE, null,DominioSituacao.A, DominioSituacao.A);
		
		List<MbcCirurgias> cirurgiasAgendadas = getMbcCirurgiasDAO()
				.pesquisarMbcCirurgiasByPrevisaoInicioEFim(dataAgenda,
						dataAgenda, unfSeq, sciSeqp, DominioSituacaoCirurgia.CANC,
						Boolean.FALSE, Boolean.TRUE,
						MbcCirurgias.Fields.UNF_SEQ.toString(),
						MbcCirurgias.Fields.SCI_SEQP.toString(),
						MbcCirurgias.Fields.DTHR_PREV_INICIO.toString());
		
		List<PesquisaAgendarProcedimentosVO> lista = processarTodasDisponibilidadesDisponiveis(
				salasCirgs, caracts, siglaEspecialidade);
		
		//Se não tiver nenhuma cirugia, então retorna lista sem nenhum restrição
		if(!cirurgiasAgendadas.isEmpty()){
		
			removePrevisoesDeInicioEFimForaDosLimites(cirurgiasAgendadas,	dataAgenda);
			
			ordenaERemoveHorarioDeCirugiasIntercaladas(cirurgiasAgendadas, lista);
			
			List<PesquisaAgendarProcedimentosVO> disponibilidadesFinais = 
					gerarDisponibilidadeDeHorariosbyCirugiasAgendadas(cirurgiasAgendadas, lista);
			
			lista.clear();
			lista.addAll(disponibilidadesFinais);
		}
		
		CoreUtil.ordenarLista(lista, "hrInicio", Boolean.TRUE);
		CoreUtil.ordenarLista(lista, "ordemTurno", Boolean.TRUE);
		CoreUtil.ordenarLista(lista, "sala", Boolean.TRUE);
		return lista;
	}

	public DominioDiaSemana getDiaDaSemanaByDate(Date dataAgenda) {
		if(dataAgenda != null) {
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(dataAgenda);
			return DominioDiaSemana.getDiaDaSemana(gc.get(GregorianCalendar.DAY_OF_WEEK));
		}
		return null;
	}
	
	/**
	 * @param cirurgiasAgendadas
	 * @param lista
	 * @return
	 */
	public List<PesquisaAgendarProcedimentosVO> gerarDisponibilidadeDeHorariosbyCirugiasAgendadas(
			List<MbcCirurgias> cirurgiasAgendadas,
			List<PesquisaAgendarProcedimentosVO> lista) {
		List<PesquisaAgendarProcedimentosVO> disponibilidadesFinais = new ArrayList<PesquisaAgendarProcedimentosVO>();
		
		for(MbcCirurgias cirg : cirurgiasAgendadas){
			Short seqpSalaAgend  = cirg.getSciSeqp();
			Date dtInicioCirgAgend = DateUtil.truncaHorario(cirg.getDataPrevisaoInicio());//truncaHorarioComMeiaNoite(cirg.getDataPrevisaoInicio());
			Date dtFimCirgAgend = truncaHorarioComMeiaNoite(cirg.getDataPrevisaoFim());
			Boolean cirgAdicionadaNasDisponibilidades = Boolean.FALSE;
			//for(PesquisaAgendarProcedimentosVO voDisponibilidade : lista){
			for(int i =0 ; i < lista.size();i++){
				PesquisaAgendarProcedimentosVO voDisponibilidade  = lista.get(i);
				Short seqpSalaDisp  = voDisponibilidade.getSala();
				Date dtInicioCirgDisp = DateUtil.truncaHorario(voDisponibilidade.getHrInicio());//truncaHorarioComMeiaNoite(voDisponibilidade.getHrInicio());
				Date dtFimCirgDisp = truncaHorarioComMeiaNoite(voDisponibilidade.getHrFim());
				if(seqpSalaDisp.equals(seqpSalaAgend)//Disponibilidade de sala a mesma da cirurgia
						&& dtInicioCirgAgend.before(dtFimCirgDisp)//Horário fim da cirurgia é menor que o fim da disponibilidade
						&& !cirgAdicionadaNasDisponibilidades
						//&& dtFimCirgAgend.after(DateUtil.truncaHorario(dtInicioCirgDisp))
						){
					
					if(dtInicioCirgAgend.equals(dtInicioCirgDisp)){//Caso o ínicio da cirurgia seja o mesmo da disponibilidade,
						if((dtFimCirgAgend.after(dtFimCirgDisp))
								&& i < lista.size()-1){
							voDisponibilidade = null;
							i++;
							PesquisaAgendarProcedimentosVO temp = lista.get(i);
							disponibilidadesFinais.add(
									new PesquisaAgendarProcedimentosVO(
											seqpSalaAgend, temp.getEspecialidade(),
											dtFimCirgAgend, temp.getHrFim(), temp.getOrdemTurno()));
						}else{
							//então altera o ínicio da disponibilidade para fim da cirurgia
							voDisponibilidade.setHrInicio(dtFimCirgAgend);
							voDisponibilidade.setHrFim(dtFimCirgDisp);
						}
					}else{
						voDisponibilidade.setHrFim(dtInicioCirgAgend);
						disponibilidadesFinais.add(
								new PesquisaAgendarProcedimentosVO(
										seqpSalaAgend, voDisponibilidade.getEspecialidade(),
										dtFimCirgAgend, dtFimCirgDisp, voDisponibilidade.getOrdemTurno()));
					}
					cirgAdicionadaNasDisponibilidades = Boolean.TRUE;
				}
				disponibilidadesFinais.add(voDisponibilidade);
			}
			disponibilidadesFinais.remove(null);
			for(PesquisaAgendarProcedimentosVO voDisponibilidade1 : disponibilidadesFinais){
				voDisponibilidade1.setHrInicio(DateUtil.truncaHorario(voDisponibilidade1.getHrInicio()));
				voDisponibilidade1.setHrFim(DateUtil.truncaHorario(voDisponibilidade1.getHrFim()));
			}
			efetuarLimpezaDisponibilidades(lista, disponibilidadesFinais);
		}
		
		for(PesquisaAgendarProcedimentosVO voF : lista){
			if(voF.getHrInicio().equals(voF.getHrFim())){
				lista.set(lista.indexOf(voF), null);
			}
		}
		while(lista.remove(null)){
			lista.remove(null);
		}
		
		disponibilidadesFinais.addAll(lista);
		return disponibilidadesFinais;
	}

	/**
	 * Para que a lógica do cálculo de disponibilidade fique "simples"
	 * Após o calculo é feito uma limpeza nas situações de exceção.
	 * @param lista
	 * @param disponibilidadesFinais
	 */
	private void efetuarLimpezaDisponibilidades(
			List<PesquisaAgendarProcedimentosVO> lista,
			List<PesquisaAgendarProcedimentosVO> disponibilidadesFinais) {
		CoreUtil.ordenarLista(disponibilidadesFinais, "hrInicio", Boolean.TRUE);
		CoreUtil.ordenarLista(disponibilidadesFinais, "sala", Boolean.TRUE);
		lista.clear();
		lista.addAll(disponibilidadesFinais);
		disponibilidadesFinais.clear();
		
		//limpeza
		for(int i =0; i< lista.size()-1 ;i++){
			PesquisaAgendarProcedimentosVO vo   = lista.get(i);
			PesquisaAgendarProcedimentosVO vo1  = lista.get(i+1);
				if(vo.getSala().equals(vo1.getSala())
						&& (DateUtil.truncaHorario(vo.getHrInicio()).equals(DateUtil.truncaHorario(vo1.getHrFim())))
						){
							vo.setHrInicio(vo1.getHrInicio());
							lista.set(i+1, null);
							break;
				}else if(vo.getSala().equals(vo1.getSala())
						&& ((DateUtil.truncaHorario(vo1.getHrInicio()).equals(DateUtil.truncaHorario(vo1.getHrFim())))
						)){
						lista.set(i+1, null);
						break;
						}else if(vo.getSala().equals(vo1.getSala())
								&& (DateUtil.truncaHorario(vo.getHrFim()).before(DateUtil.truncaHorario(vo.getHrInicio())))
								&& (!DateUtil.dataToString(vo.getHrFim(), "HHmm").equals("0000"))
								){
								lista.set(i, null);
								break;
								}
		}
		while(lista.remove(null)){
			lista.remove(null);
		}
	}

	/**
	 * Ordena uma lista de cirugias considerando a disponibilidade de horário;
	 * Armazena horário início da 1º disponibilidade, exemplo 07:00
	 * Armazena horário início da última disponibilidade, exemplo 00:00
	 * O método vai ordenar as cirurgias garantindo que as cirurgias com previsão antes das 07:00, não fiquem antes dos das 00:00
	 * pois a primeira disponibilidade de horário começa as 07:00
	 * 
	 * Retorna o horário da primeira disponibilidade
	 * @param cirurgiasAgendadas
	 * @param lista
	 * @return
	 */
	public Date ordenarCirurgiasParaDisponibilidade(
			List<MbcCirurgias> cirurgiasAgendadas,
			List<PesquisaAgendarProcedimentosVO> lista) {
		Date dtHrInicioPrimeiraDisponibilidade = DateUtil.truncaHorario(lista.get(0).getHrInicio());
		Date dtHrInicioUltimaDisponibilidade = DateUtil.truncaHorario(lista.get(lista.size()-1).getHrInicio());
		if(dtHrInicioPrimeiraDisponibilidade.after(dtHrInicioUltimaDisponibilidade)){
			for(MbcCirurgias cirg : cirurgiasAgendadas){
				getMbcCirurgiasDAO().desatachar(cirg);
				cirg.setDataPrevisaoInicio(truncaHorarioComMeiaNoite(cirg.getDataPrevisaoInicio()));
				if(truncaHorarioComMeiaNoite(cirg.getDataPrevisaoFim()).before(truncaHorarioComMeiaNoite(dtHrInicioPrimeiraDisponibilidade))){
					cirg.setDataPrevisaoInicio(DateUtil.adicionaDias(cirg.getDataPrevisaoInicio(), 1));
				}
			}
			CoreUtil.ordenarLista(cirurgiasAgendadas, MbcCirurgias.Fields.DTHR_PREV_INICIO.toString(), Boolean.TRUE);
		}
		return dtHrInicioPrimeiraDisponibilidade;
	}
	
	/**
	 * Alterada a data para dataAtual, mantendo o horário.
	 * Caso o horário seja meiaNoite, adiciona um dia.
	 * */
	public Date truncaHorarioComMeiaNoite(Date date) {
		Date retorno = DateUtil.truncaHorario(date);
		
		if(DateUtil.dataToString(retorno, "HHmm").equals("0000")){
			retorno = DateUtil.adicionaDias(retorno, 1);
		}
		return retorno;
	}
	
	public void desatacharETruncaHorarioInicialMbcCaracteristicaSalaCirg(
			List<MbcCaracteristicaSalaCirg> disponibilidadesSalas) {
		for(MbcCaracteristicaSalaCirg carac : disponibilidadesSalas){
			getMbcCaracteristicaSalaCirgDAO().desatachar(carac);
			/*carac.getMbcHorarioTurnoCirg().setHorarioInicial(
					truncaHorarioComMeiaNoite(carac.getMbcHorarioTurnoCirg()
							.getHorarioInicial()));*/
			carac.getMbcHorarioTurnoCirg().setHorarioInicial(
					DateUtil.truncaHorario(carac.getMbcHorarioTurnoCirg()
							.getHorarioInicial()));
		}
		
		CoreUtil.ordenarLista(disponibilidadesSalas, 
				MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString() +"."+ MbcHorarioTurnoCirg.Fields.HORARIO_INICIAL
				, Boolean.TRUE);
		CoreUtil.ordenarLista(disponibilidadesSalas, MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA_ID_SEQP.toString(), Boolean.TRUE);
	}
	
	/**
	 * Separa as cirurgias em grupos de sala;
	 * Em seguida remove os horários intercalos destas cirurgias, que estão na mesma sala;
	 * Adiciona novamente na lista final que é retornada.
	 * @param cirurgiasAgendadas
	 * @param lista 
	 * @param dataAgendaSelecionada 
	 * @return 
	 * @throws ApplicationBusinessException
	 */
	public void ordenaERemoveHorarioDeCirugiasIntercaladas(
			List<MbcCirurgias> cirurgiasAgendadas, List<PesquisaAgendarProcedimentosVO> lista )throws ApplicationBusinessException {
		
		List<MbcCirurgias> listaCirurgiasAgrupadasFinal = new ArrayList<MbcCirurgias>();
		
		for(int i =0; i< cirurgiasAgendadas.size(); i++){
			MbcCirurgias indice1 = cirurgiasAgendadas.get(i);
			
			List<MbcCirurgias> cirgAgrupadasporSala = new ArrayList<MbcCirurgias>();
			for(MbcCirurgias indice2 : cirurgiasAgendadas){
				if(indice1.getSalaCirurgica().equals(indice2.getSalaCirurgica())){
					cirgAgrupadasporSala.add(indice2);
				}
			}
			i = cirurgiasAgendadas.indexOf(cirgAgrupadasporSala.get(cirgAgrupadasporSala.size()-1));
			//Até aqui agrupou as cirurgias por sala
			
			//Remove horários intercalos das cirurgias agrupadas
			preparaCirugiasComHorarioIntercalado(cirgAgrupadasporSala);
			
			//ordenarCirurgiasParaDisponibilidade(cirgAgrupadasporSala, lista);
			
			listaCirurgiasAgrupadasFinal.addAll(cirgAgrupadasporSala);
		}
		
		cirurgiasAgendadas.clear();
		cirurgiasAgendadas.addAll(listaCirurgiasAgrupadasFinal);
	}
	
	private void preparaCirugiasComHorarioIntercalado(
			List<MbcCirurgias> cirgAgrupadasporSala)
			throws ApplicationBusinessException {
		try {
			int sizeCirg = 0;
			do{
				sizeCirg = cirgAgrupadasporSala.size();
				processarCirurgiasComHorarioIntercaladoNaMesmaSala(cirgAgrupadasporSala);
			}while(sizeCirg != cirgAgrupadasporSala.size());
		} catch (Exception e){
			throw new ApplicationBusinessException(
					new ApplicationBusinessException(
							AgendaProcedimentosONExceptionCode.PROBLEMA_GERAR_DISPONIBILIDADE));
		}
	}

	/**
	 * As cirurgias que possuem horário de previsão início e fim fora dos limites de horário vão ser zerados.
	 * Exemplo : Data Selecionada no filtro = 21/10/2013
	 * 			 Previsão de Início	: 20/10/2013 22:00
	 * 			 Previsão de Fim	: 21/10/2013 02:00
	 * Este método vai alterar a previsão de início para 21/10/2013 00:00
	 * @param cirgAgrupadasporSala
	 * @param dataAgendaSelecionada
	 */
	private void removePrevisoesDeInicioEFimForaDosLimites(
			List<MbcCirurgias> cirgAgrupadasporSala, Date dataAgendaSelecionada) {
		for(MbcCirurgias indice1 : cirgAgrupadasporSala){
			getMbcCirurgiasDAO().desatachar(indice1);
			if(DateUtil.truncaData(dataAgendaSelecionada).after(indice1.getDataPrevisaoInicio())){
				indice1.setDataPrevisaoInicio(DateUtil.truncaData(dataAgendaSelecionada));
			}
			if(DateUtil.truncaDataFim(dataAgendaSelecionada).before(indice1.getDataPrevisaoFim())){
				indice1.setDataPrevisaoFim(DateUtil.truncaData(DateUtil.adicionaDias(dataAgendaSelecionada, 1)));
			}
		}
	}

	/**
	 * Remove cirurgias com previsão de ínicio intercalado na lista.
	 * Exempl:  Cirurgia A, início = 08:00   fim = 10:00
	 * 			Cirurgia B, início = 07:00   fim = 10:00
	 * 			Cirurgia C, início = 07:30   fim = 11:00
	 * 
	 * O resultado deve ser uma cirurgia X com
	 * 			início = 07:00    fim = 11:00
	 * 
	 * Importante salientar que as cirurgias devem estar na mesma sala
	 * @param cirurgiasAgendadas
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws NoSuchMethodException 
	 */
	private void processarCirurgiasComHorarioIntercaladoNaMesmaSala(
			List<MbcCirurgias> cirurgiasAgendadas) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{
		List<MbcCirurgias> novasAgendas = new ArrayList<MbcCirurgias>();
		Date ultimaHoraFim = DateUtil.obterData(1900, 0, 1);
		
		for(MbcCirurgias indice1 : cirurgiasAgendadas){
			Date horaInicio = indice1.getDataPrevisaoInicio();
			Date horaFim = indice1.getDataPrevisaoFim();
			for(MbcCirurgias indice2 : cirurgiasAgendadas){
					if(!indice1.equals(indice2)
							&& horaInicio.before(indice2.getDataPrevisaoInicio())
							&& horaFim.before(indice2.getDataPrevisaoFim())
							&& (horaFim.before(indice2.getDataPrevisaoInicio()) || horaFim.equals(indice2.getDataPrevisaoInicio()))
							&& horaFim.after(ultimaHoraFim)
							){
						MbcCirurgias newCirg = copyPropertiesNewCirg(indice1);
						novasAgendas.add(newCirg);
						ultimaHoraFim = newCirg.getDataPrevisaoFim();
						break;
					}else{
						if(!indice1.equals(indice2)
								&& (indice2.getDataPrevisaoInicio().before(horaInicio) || indice2.getDataPrevisaoInicio().equals(horaInicio))
								&& indice2.getDataPrevisaoFim().after(horaFim)
								){
							MbcCirurgias newCirg = copyPropertiesNewCirg(indice1);
							newCirg.setDataPrevisaoInicio(indice2.getDataPrevisaoInicio());
							newCirg.setDataPrevisaoFim(indice2.getDataPrevisaoFim());
							novasAgendas.add(newCirg);
							ultimaHoraFim = newCirg.getDataPrevisaoFim();
							break;
						}else{
							if(!indice1.equals(indice2)
									&& horaFim.after(indice2.getDataPrevisaoInicio())
									&& indice2.getDataPrevisaoFim().after(horaFim)
									&& (indice2.getDataPrevisaoInicio().after(horaInicio) || indice2.getDataPrevisaoInicio().equals(horaInicio))
									){
								MbcCirurgias newCirg = copyPropertiesNewCirg(indice1);
								newCirg.setDataPrevisaoFim(indice2.getDataPrevisaoFim());
								novasAgendas.add(newCirg);
								ultimaHoraFim = newCirg.getDataPrevisaoFim();
								break;
							}else{
								if(cirurgiasAgendadas.get(cirurgiasAgendadas.size()-1).equals(indice1)){//se é o ultimo registro
									if(indice2.getDataPrevisaoInicio().after(ultimaHoraFim)
											|| indice2.getDataPrevisaoInicio().equals(ultimaHoraFim)){
										MbcCirurgias newCirg = copyPropertiesNewCirg(indice1);
										novasAgendas.add(newCirg);
										ultimaHoraFim = newCirg.getDataPrevisaoFim();
										break;
									}
								}
							}
						}
					}
			}
		}
		cirurgiasAgendadas.clear();
		cirurgiasAgendadas.addAll(novasAgendas);
	}

	private MbcCirurgias copyPropertiesNewCirg(MbcCirurgias indice1)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		MbcCirurgias newCirg = new MbcCirurgias();
		PropertyUtils.copyProperties(newCirg, indice1);
		return newCirg;
	}

	public List<PesquisaAgendarProcedimentosVO> processarTodasDisponibilidadesDisponiveis(
			List<MbcCaracteristicaSalaCirg> salasCirgs, List<Object[]> caracts, String siglaEspecialidadeSelecionada) {
		List<PesquisaAgendarProcedimentosVO> lista = new ArrayList<PesquisaAgendarProcedimentosVO>();
		for(MbcCaracteristicaSalaCirg salaDisp : salasCirgs){
			Short seqSalaDisp = salaDisp.getMbcSalaCirurgica().getId().getSeqp();
			String turnoDisp = salaDisp.getMbcTurnos().getTurno();
			List<String> especialidadesByTurnoSala = new ArrayList<String>();
			Boolean salaPossuiEspSelecionada = Boolean.FALSE;
			for(Object[] dispEsp : caracts){
				Short salaEsp = (Short) dispEsp[2];
				String turnoEsp = (String) dispEsp[1];
				String siglaEsp = (String) dispEsp[0];
				if(seqSalaDisp.equals(salaEsp) 
						&& turnoDisp.equals(turnoEsp)){
					especialidadesByTurnoSala.add(siglaEsp);
					if(siglaEsp.equals(siglaEspecialidadeSelecionada)){
						salaPossuiEspSelecionada = Boolean.TRUE;
					}
				}
			}
			if(siglaEspecialidadeSelecionada == null || (salaPossuiEspSelecionada && !especialidadesByTurnoSala.isEmpty())){
				lista.add(new PesquisaAgendarProcedimentosVO(seqSalaDisp, 
						formataEspecialidades(especialidadesByTurnoSala,salaDisp.getMbcTurnos().getDescricao()), 
						salaDisp.getMbcHorarioTurnoCirg().getHorarioInicial(),
						salaDisp.getMbcHorarioTurnoCirg().getHorarioFinal(),
						salaDisp.getMbcHorarioTurnoCirg().getMbcTurnos().getOrdem()));
			}
		}
		return lista;
	}
	
	private String formataEspecialidades(List<String> especialidadesByTurnoSala, String descricaoTurno) {
		if(especialidadesByTurnoSala.isEmpty()){
			return descricaoTurno + ": Nenhuma Especialidade Cadastrada";
		}else{
			StringBuffer buf = new StringBuffer();
			buf.append(descricaoTurno).append(':');
			for(String esp : especialidadesByTurnoSala){
				buf.append(esp);
				buf.append('/');
			}
			buf.append("*/");
			return buf.toString().replace("/*/", "");
			
		}
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO() {
		return mbcCaracteristicaSalaCirgDAO;
	}
	
	private MbcCaractSalaEspDAO getMbcCaractSalaEspDAO(){
		return mbcCaractSalaEspDAO;
	}
}
