package br.gov.mec.aghu.controlepaciente.business;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controlepaciente.dao.EcpControlePacienteDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpHorarioControleDAO;
import br.gov.mec.aghu.controlepaciente.vo.RelatorioRegistroControlePacienteVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EcpControlePaciente;
import br.gov.mec.aghu.model.EcpHorarioControle;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;

@Stateless
public class RelatorioRegistroControlePacienteON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioRegistroControlePacienteON.class);
	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	private static final long serialVersionUID = -5878626079839072517L;
	
	private static final String NAO = "N";
	private static final String SIM = "S";
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private EcpHorarioControleDAO ecpHorarioControleDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private VisualizarRegistrosControleON visualizarRegistrosControleON;
	
	@Inject
	private EcpControlePacienteDAO ecpControlePacienteDAO;

	public enum RelatorioRegistroControlePacienteONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_DATA_HORA_FINAL_MAIOR_DATA_FINAL_ATENDIMENTO,
		MENSAGEM_DATA_HORA_INICIAL_MENOR_DATA_INICIO_ATENDIMENTO,
		MENSAGEM_DATA_FINAL_MENOR_DATA_INICIAL,
		MENSAGEM_DATA_FINAL_MAIOR_DATA_FINAL_DO_ATENDIMENTO, 
		MENSAGEM_DATA_FINAL_MAIOR_DATA_INICIAL, 
		MENSAGEM_TEMPO_PESQUISA_MAIOR_QUE_PERMITIDO, 
		MENSAGEM_ATD_SEQ_NULO,
		MENSAGEM_DATAS_NULAS,
		MENSAGEM_ATENDIMENTO_NULO, 
		MENSAGEM_DATA_HORA_INICIAL_NULO, 
		MENSAGEM_DATA_HORA_FINAL_NULO;
	}

	public List<RelatorioRegistroControlePacienteVO> criaRelatorioRegistroControlePaciente(
			Integer pacCodigo, Date dataHoraInicio, Date dataHoraFim, AghAtendimentos aghAtendimentos)
			throws ApplicationBusinessException {

		// busca paciente
		AipPacientes paciente = pacienteFacade.obterPacientePorCodigo(pacCodigo);
		// busca horarios controle
		List<EcpHorarioControle> horarioControle = null;

		// #48659 - Disponibilizando relatório em POL - Ambulatório
		String textoPeriodoAmbulatorio = null;
		if (aghAtendimentos != null){
			// busca horarios controle
			horarioControle = this.ecpHorarioControleDAO.listarHorarioControlePorConsulta(aghAtendimentos.getConsulta().getNumero());
			
			textoPeriodoAmbulatorio = String.format("Controles do Paciente - Consulta: %s Data: %s <br/>Especialidade: %s",
							aghAtendimentos.getConsulta().getNumero(),
							new SimpleDateFormat("dd/MM/yyyy").format(aghAtendimentos.getDthrInicio()),
							(aghAtendimentos.getEspecialidade().getEspecialidade() != null ? 
									aghAtendimentos.getEspecialidade().getEspecialidade().getNomeEspecialidade() : aghAtendimentos.getEspecialidade().getNomeEspecialidade()));
		}
		else{
			// busca horarios controle
			horarioControle = this.ecpHorarioControleDAO
					.listarHorarioControlePorPacientePeriodo(paciente,
							dataHoraInicio, dataHoraFim, true, null);
		}
		
		RelatorioRegistroControlePacienteVO relRegistroControlePacienteVO;
		List<RelatorioRegistroControlePacienteVO> listaRelatorioRegistroControlePacienteVO = this.montarListaDadosRelatorio(horarioControle, paciente, dataHoraInicio, dataHoraFim, textoPeriodoAmbulatorio);

        //se não encontrar dados no periodo solicitado imprimir mensagem de erro no Relatório
		if ((listaRelatorioRegistroControlePacienteVO.isEmpty())||(listaRelatorioRegistroControlePacienteVO==null)) {
			relRegistroControlePacienteVO = new RelatorioRegistroControlePacienteVO();

			// identificação
			relRegistroControlePacienteVO.setPacCodigo(pacCodigo);
			relRegistroControlePacienteVO
					.setDataHoraInicial(dataHoraInicio);
			relRegistroControlePacienteVO.setDataHoraFinal(dataHoraFim);
			relRegistroControlePacienteVO.setNome(paciente.getNome());
			relRegistroControlePacienteVO.setProntuario(CoreUtil
					.formataProntuarioRelatorio(paciente.getProntuario()));
			
			String datIni = new SimpleDateFormat(
					"dd/MM/yyyy 'às' HH:mm 'hs'")
					.format(dataHoraInicio);
			String datFim = new SimpleDateFormat(
					"dd/MM/yyyy 'às' HH:mm 'hs.'").format(dataHoraFim);

			// #48659 - Disponibilizando relatório em POL - Ambulatório
			if (StringUtils.isEmpty(textoPeriodoAmbulatorio)){
				String periodo = "Controles do Paciente - Período de "
						+ datIni + " até " + datFim;

				relRegistroControlePacienteVO.setPeriodo(periodo);
			}
			else{
				relRegistroControlePacienteVO.setPeriodo(textoPeriodoAmbulatorio);
			}			
			
			relRegistroControlePacienteVO.setSiglaUnidade("****************************************************");
			relRegistroControlePacienteVO.setAnotacoes("Não existe registro de controles no período de " + datIni + " até " +  datFim + ".");
			relRegistroControlePacienteVO.setValor("SIM"+ "******************************************************");

			// identificação rodape

			// paciente recém nacido requer prontuário da mãe no relatório
			defineRodapeProntuario(paciente, relRegistroControlePacienteVO);

			relRegistroControlePacienteVO.setNomeRodape(paciente.getNome());
			if (paciente.getProntuario() != null && paciente.getProntuario() > VALOR_MAXIMO_PRONTUARIO) {

				if (paciente.getMaePaciente() != null) {
					relRegistroControlePacienteVO
							.setProntuarioRodape(CoreUtil
									.formataProntuarioRelatorio(paciente
											.getProntuario())
									+ "          Mãe: "
									+ CoreUtil
											.formataProntuarioRelatorio(paciente
													.getMaePaciente()
													.getProntuario()));
				}
			}

			listaRelatorioRegistroControlePacienteVO.add(relRegistroControlePacienteVO);
			
		}
		
		return listaRelatorioRegistroControlePacienteVO;
	}
	
	private void defineRodapeProntuario(AipPacientes paciente, RelatorioRegistroControlePacienteVO relRegistroControlePacienteVO) {
		Integer prontuario = paciente.getProntuario();
		relRegistroControlePacienteVO.setProntuarioRodape(CoreUtil.formataProntuarioRelatorio(prontuario));
		relRegistroControlePacienteVO.setNomeRodape(paciente.getNome());

		if (prontuario != null && prontuario > VALOR_MAXIMO_PRONTUARIO) {
			if (paciente.getMaePaciente() != null) {
				relRegistroControlePacienteVO.setProntuarioRodape(CoreUtil.formataProntuarioRelatorio(prontuario)
						+ "          Mãe: "
						+ CoreUtil.formataProntuarioRelatorio(paciente.getMaePaciente().getProntuario()));
			}

		}
	
	}
	
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<RelatorioRegistroControlePacienteVO> montarListaDadosRelatorio(List<EcpHorarioControle> horarioControle, AipPacientes paciente, Date dataHoraInicio, Date dataHoraFim, String textoPeriodoAmbulatorio) {
		List<RelatorioRegistroControlePacienteVO> listaRelatorioRegistroControlePacienteVO = new ArrayList<RelatorioRegistroControlePacienteVO>();
		RelatorioRegistroControlePacienteVO relRegistroControlePacienteVO;
		
		// Acessa a tabela ecp_horario_controles pelo paciente e pelos horários
		for (EcpHorarioControle ecpHorarioControle : horarioControle) {

			// Para cada horário buscar as medições em ecp_controle_pacientes
			List<EcpControlePaciente> controlePaciente = ecpControlePacienteDAO.listarControlePacientePorHorario(ecpHorarioControle);
			
			if (controlePaciente.isEmpty()) {
				relRegistroControlePacienteVO = new RelatorioRegistroControlePacienteVO();
				identificacaoPaciente(paciente, relRegistroControlePacienteVO);
				
				defineHorarioControle(dataHoraInicio, dataHoraFim, relRegistroControlePacienteVO);
				
				// celula - anotacoes
				relRegistroControlePacienteVO.setAnotacoes(ecpHorarioControle.getAnotacoes());
				relRegistroControlePacienteVO.setProfissionais(montaNomeProfissionalRelatorio(ecpHorarioControle));
				
				// identificação rodape
				defineRodapeProntuario(paciente, relRegistroControlePacienteVO);
				
				// localização Leito ou Quarto
				defineLocalizacaoLeitoQuarto(relRegistroControlePacienteVO, ecpHorarioControle);
				
				relRegistroControlePacienteVO.setDataHora(ecpHorarioControle.getDataHora());
				listaRelatorioRegistroControlePacienteVO.add(relRegistroControlePacienteVO);
				
				continue;
				
			}

			for (EcpControlePaciente ecpControlePaciente : controlePaciente) {
				if (StringUtils.isNotBlank(ecpControlePaciente.getMedicaoFormatada())) {
					relRegistroControlePacienteVO = new RelatorioRegistroControlePacienteVO();

					identificacaoPaciente(paciente, relRegistroControlePacienteVO);
					
					defineHorarioControle(dataHoraInicio, dataHoraFim, relRegistroControlePacienteVO);

					// linha - horario
					relRegistroControlePacienteVO.setDataHora(ecpHorarioControle.getDataHora());

					// coluna - sigla e unidade;
					String sigla = ecpControlePaciente.getItem().getSigla();
					String unidade = null;
					if (ecpControlePaciente.getItem().getUnidadeMedidaMedica() != null) {
						unidade = ecpControlePaciente.getItem()
								.getUnidadeMedidaMedica().getDescricao();
					}
					Short ordemGrupo;
					if (ecpControlePaciente.getItem().getGrupo() != null) {
						ordemGrupo = ecpControlePaciente.getItem().getGrupo()
								.getOrdem() ;
					} else {
						ordemGrupo = 999;
					}
					Short ordemItem;
					if (ecpControlePaciente.getItem() != null) {
						ordemItem = ecpControlePaciente.getItem().getOrdem()  ;
					} else {
						ordemItem = 999;
					}
					
					ordemGrupo =(short) (ordemGrupo + 10000);
					ordemItem  = (short) (ordemItem + 10000);
					
					String vordem  = ordemGrupo.toString() +  ordemItem.toString();

					if (StringUtils.isNotBlank(unidade)) {
						relRegistroControlePacienteVO.setSiglaUnidade(vordem
								+ sigla + "\n ( " + unidade + ")");
					} else {
						relRegistroControlePacienteVO.setSiglaUnidade(vordem
								+ sigla);

					}

					// celula - anotacoes
					relRegistroControlePacienteVO.setAnotacoes(ecpHorarioControle.getAnotacoes());
					relRegistroControlePacienteVO.setProfissionais(montaNomeProfissionalRelatorio(ecpHorarioControle));

					// celula - valor
					// Fora do Limite Normal
					calculaForaLimiteNormal(relRegistroControlePacienteVO, ecpControlePaciente);

					// ordem para apresentar as medições
					relRegistroControlePacienteVO.setOrdem(ecpControlePaciente.getItem().getOrdem().intValue());

					// identificação rodape

					defineRodapeProntuario(paciente, relRegistroControlePacienteVO);
					
					// localização Leito ou Quarto
					defineLocalizacaoLeitoQuarto(relRegistroControlePacienteVO, ecpHorarioControle);

					listaRelatorioRegistroControlePacienteVO.add(relRegistroControlePacienteVO);
				}
			}
		}
		return listaRelatorioRegistroControlePacienteVO;
	}
	
	private void calculaForaLimiteNormal(RelatorioRegistroControlePacienteVO relRegistroControlePacienteVO,
			EcpControlePaciente ecpControlePaciente) {
		String medicaoFormatada = ecpControlePaciente.getMedicaoFormatada();
		if (ecpControlePaciente.getForaLimiteNormal() != null) {
			relRegistroControlePacienteVO.setForaLimiteNormal(ecpControlePaciente.getForaLimiteNormal());
			// celula - valor
			if (ecpControlePaciente.getForaLimiteNormal()) {
				relRegistroControlePacienteVO.setValor(SIM + medicaoFormatada);
			} else {
				relRegistroControlePacienteVO.setValor(NAO + medicaoFormatada);
			}
		} else {
			relRegistroControlePacienteVO.setForaLimiteNormal(Boolean.FALSE);
			// celula - valor
			relRegistroControlePacienteVO.setValor(NAO + medicaoFormatada);
		}
	}

	private void defineLocalizacaoLeitoQuarto(RelatorioRegistroControlePacienteVO relRegistroControlePacienteVO,
			EcpHorarioControle ecpHorarioControle) {
		if ((ecpHorarioControle.getLeito() != null) && (ecpHorarioControle.getLeito().getLeitoID() != null)) {
			relRegistroControlePacienteVO.setLocalRodape("Leito: " + ecpHorarioControle.getLeito().getLeitoID());
		} else if ((ecpHorarioControle.getQuarto() != null) && (ecpHorarioControle.getQuarto().getDescricao()!= null)) {
			relRegistroControlePacienteVO.setLocalRodape("Quarto: " + ecpHorarioControle.getQuarto().getDescricao());
		}
	}

	private void defineHorarioControle(Date dataHoraInicio, Date dataHoraFim,
			RelatorioRegistroControlePacienteVO relRegistroControlePacienteVO) {
		relRegistroControlePacienteVO.setDataHoraInicial(dataHoraInicio);
		relRegistroControlePacienteVO.setDataHoraFinal(dataHoraFim);

		String datIni = new SimpleDateFormat( "dd/MM/yyyy 'às' HH:mm 'hs'") .format(dataHoraInicio);
		String datFim = new SimpleDateFormat( "dd/MM/yyyy 'às' HH:mm 'hs.'").format(dataHoraFim);
		String periodo = "Controles do Paciente - Período de " + datIni + " até " + datFim;
		relRegistroControlePacienteVO.setPeriodo(periodo);
		
	}
	
	private String montaNomeProfissionalRelatorio(EcpHorarioControle ecpHorarioControle) {
		return visualizarRegistrosControleON.montarDescricaoAnotacao(ecpHorarioControle);
	}
	
	private void identificacaoPaciente(AipPacientes paciente, RelatorioRegistroControlePacienteVO relRegistroControlePacienteVO) {
		// identificação
		relRegistroControlePacienteVO.setPacCodigo(paciente.getCodigo());
		relRegistroControlePacienteVO.setNome(paciente.getNome());
		relRegistroControlePacienteVO.setProntuario(CoreUtil.formataProntuarioRelatorio(paciente.getProntuario()));
	}

	/**
	 * RN - Data Inicial do Atendimento para pesquisa Data/Hora Inicial da
	 * pesquisa não pode ser menor do que a data de inicio do atendimento
	 * 
	 * @param atdSeq
	 * @param dataHoraInicial
	 * @return
	 */
	public void validaDataInicial(Integer atdSeq, Date dataHoraInicial) throws ApplicationBusinessException {

		if (atdSeq == null) {
			throw new ApplicationBusinessException(RelatorioRegistroControlePacienteONExceptionCode.MENSAGEM_ATD_SEQ_NULO);
		}

		if (dataHoraInicial == null) {
			throw new ApplicationBusinessException(RelatorioRegistroControlePacienteONExceptionCode.MENSAGEM_DATA_HORA_INICIAL_NULO);
		}

		AghAtendimentos atendimento = this.aghuFacade.obterAghAtendimentoPorChavePrimaria(atdSeq);

		if (atendimento == null) {
			throw new ApplicationBusinessException(RelatorioRegistroControlePacienteONExceptionCode.MENSAGEM_ATENDIMENTO_NULO);
		}

		//Data inicial da pesquisa, se informada ou alterada,  não deve ser menor que a data de inicio do atendimento - RN 02
		GregorianCalendar dataHoraInicioSemSegundosMilesimos = new GregorianCalendar();
		
		dataHoraInicioSemSegundosMilesimos.setTime(dataHoraInicial);		
		dataHoraInicioSemSegundosMilesimos.set(GregorianCalendar.MILLISECOND, 0);
		dataHoraInicioSemSegundosMilesimos.set(GregorianCalendar.SECOND, 0);
		
		GregorianCalendar dataHoraInicioAtendimentoSemSegundosMilesimos = new GregorianCalendar();
		
		dataHoraInicioAtendimentoSemSegundosMilesimos.setTime(atendimento.getDthrInicio());
		dataHoraInicioAtendimentoSemSegundosMilesimos.set(GregorianCalendar.MILLISECOND, 0);
		dataHoraInicioAtendimentoSemSegundosMilesimos.set(GregorianCalendar.SECOND, 0);

		if (atendimento.getOrigem()== DominioOrigemAtendimento.I &&  dataHoraInicial.compareTo(atendimento.getDthrInicio()) < 0) {
			if (dataHoraInicioSemSegundosMilesimos.compareTo(dataHoraInicioAtendimentoSemSegundosMilesimos) < 0) {
				throw new ApplicationBusinessException(RelatorioRegistroControlePacienteONExceptionCode.MENSAGEM_DATA_HORA_INICIAL_MENOR_DATA_INICIO_ATENDIMENTO);
			}
		}
	}

	protected int obterParametroNroDiasPesquisa() throws BaseException {
		return parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_DIAS_PESQUISA_CONTROLES);
	}
	
	/**
	 * Valida o tempo de pesquisa a partir das datas de inicio e fim do atendimento
	 * informadas na tela ou alteradas pelo usuário na tela 
	 */
	public void verificaDuracaoAtendimento(Date dthrInicio, Date dthrFim) throws ApplicationBusinessException, BaseException {
		
		if(dthrInicio == null || dthrFim == null){
			throw new ApplicationBusinessException(RelatorioRegistroControlePacienteONExceptionCode.MENSAGEM_DATAS_NULAS);
		}
		
		final Integer parametroDiasPesquisa = obterParametroNroDiasPesquisa();
		
		if (((dthrFim.getTime() - dthrInicio.getTime()) / 86400000L) > parametroDiasPesquisa) {
			throw new ApplicationBusinessException(RelatorioRegistroControlePacienteONExceptionCode.MENSAGEM_TEMPO_PESQUISA_MAIOR_QUE_PERMITIDO, parametroDiasPesquisa);
		}		
	}
	
	public boolean datasForaIntervaloAtendimento(Date dthrInicio, Date dthrFim) {
		if (dthrInicio == null || dthrFim == null) {
			return false;
		}
		try {
			if (((dthrFim.getTime() - dthrInicio.getTime()) / 86400000L) > obterParametroNroDiasPesquisa()) {
				return true;
			} else {
				return false;
			}
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}	
	
	
	public void validaDatasInicialFinal(Integer atdSeq, Date dthrInicio, Date dthrFim) throws ApplicationBusinessException {
		
		if (dthrInicio.compareTo(dthrFim) >= 0) {
			throw new ApplicationBusinessException(RelatorioRegistroControlePacienteONExceptionCode.MENSAGEM_DATA_FINAL_MAIOR_DATA_INICIAL);
		}
		
		Date dthrFimAtendimento = aghuFacade.obterDataFimAtendimento(atdSeq);
		
		if(dthrFim.compareTo(dthrFimAtendimento) > 0 ){
			throw new ApplicationBusinessException(RelatorioRegistroControlePacienteONExceptionCode.MENSAGEM_DATA_FINAL_MAIOR_DATA_FINAL_DO_ATENDIMENTO);
		}		
	}
	            
	public void validaDatasInicialFinalInternacao(Date dthrInicio, Date dthrFim) throws ApplicationBusinessException {
		try {
			SimpleDateFormat format = new SimpleDateFormat(DD_MM_YYYY);
			
			Date dtIni = format.parse(format.format(dthrInicio));
			Date dtFim = format.parse(format.format(dthrFim));		
		
			if(dtFim.before(dtIni)){
				throw new ApplicationBusinessException(RelatorioRegistroControlePacienteONExceptionCode.MENSAGEM_DATA_FINAL_MENOR_DATA_INICIAL);
			}
		} catch (ParseException e) {
			LOG.error(e.getMessage(), e);
		}
	}

}
