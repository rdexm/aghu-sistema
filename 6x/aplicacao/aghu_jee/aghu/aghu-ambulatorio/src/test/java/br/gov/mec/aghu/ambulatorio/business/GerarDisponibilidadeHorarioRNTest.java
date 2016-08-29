package br.gov.mec.aghu.ambulatorio.business;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.ambulatorio.dao.AacSituacaoConsultasDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacHorarioGradeConsulta;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GerarDisponibilidadeHorarioRNTest extends AGHUBaseUnitTest<GerarDisponibilidadeHorarioRN>{

    @Mock
	private IAghuFacade mockedAghuFacade;
    @Mock
    private AacSituacaoConsultasDAO mockedAacSituacaoConsultasDAO;
    @Mock
	private IAmbulatorioFacade mockedAmbulatorioFacade;
	
	
	private static final Log log = LogFactory.getLog(GerarDisponibilidadeHorarioRNTest.class);
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	@Test
	public void criarConsultaTest() {
		
		//Define data e hora de um feriado qualquer
		final Calendar dthrFeriado = Calendar.getInstance();
		dthrFeriado.set(2011, 3, 11, 10, 0);
		final AghFeriados feriado = new AghFeriados(dthrFeriado.getTime(), DominioTurno.M); 
		final AacSituacaoConsultas situacaoConsulta = new AacSituacaoConsultas();

		Mockito.when(mockedAghuFacade.obterFeriado(Mockito.any(Date.class))).thenReturn(feriado);

		Mockito.when(mockedAacSituacaoConsultasDAO.obterSituacaoConsultaPeloId(Mockito.anyString())).thenReturn(situacaoConsulta);
		
		try {
			Mockito.when(mockedAmbulatorioFacade.inserirConsulta(Mockito.any(AacConsultas.class), Mockito.anyString(), 
					Mockito.anyBoolean())).thenReturn(Mockito.any(AacConsultas.class));
		} catch (BaseException e) {
			log.error(e.getMessage());
		}
		
		try {
					AghEspecialidades especialidade = new AghEspecialidades();
					especialidade.setIndicHoraDispFeriado(false);

					AacGradeAgendamenConsultas grade = new AacGradeAgendamenConsultas();
					grade.setEspecialidade(especialidade);
					grade.setProjetoPesquisa(new AelProjetoPesquisas());

					AacFormaAgendamento formaAgendamento = new AacFormaAgendamento(
							new AacCondicaoAtendimento(),
							new AacTipoAgendamento(), new AacPagador());

					AacHorarioGradeConsulta horario = new AacHorarioGradeConsulta();
					horario.setGradeAgendamentoConsulta(grade);
					horario.setFormaAgendamento(formaAgendamento);
					horario.setTpsTipo("G");

					Calendar dthrAtualCal = Calendar.getInstance();
					dthrAtualCal.set(2011, 3, 11, 10, 0);
					Calendar dthrAnteriorCal = Calendar.getInstance();
					dthrAnteriorCal.set(2011, 3, 11, 10, 0);
					
					Integer quantidadeGerada = 0;
					Calendar duracao = Calendar.getInstance();

					/* 
					 * Verifica quantidade gerada passando indicHoraDispFeriado = false no 
					 * turno da manhã que é feriado.
					 * 
					 * Aqui não deve ser gerado nenhuma consulta, portanto quantidadeGerada
					 * deve permanecer 0
					 */
					quantidadeGerada = systemUnderTest.criarConsulta(
							dthrAtualCal, dthrAnteriorCal, horario,
							quantidadeGerada, duracao, NOME_MICROCOMPUTADOR);
					assertEquals(quantidadeGerada, Integer.valueOf(0));
					
					/* 
					 * Verifica quantidade gerada passando indicHoraDispFeriado = false mas 
					 * com horario no turno da tarde que não é feriado.
					 * 
					 * Aqui deve ser gerada uma consulta, portanto quantidadeGerada
					 * deve ter valor 1
					 */
					dthrAtualCal.set(Calendar.AM_PM, Calendar.PM);
					
					quantidadeGerada = systemUnderTest.criarConsulta(
							dthrAtualCal, dthrAnteriorCal, horario,
							quantidadeGerada, duracao, NOME_MICROCOMPUTADOR);
					
					assertEquals(quantidadeGerada, Integer.valueOf(1));
					
					/* 
					 * Verifica quantidade gerada passo um indicHoraDispFeriado = true
					 * portanto consulta deve ser gerada mesmo sendo um feriado
					 * 
					 * Aqui deve ser gerada uma consulta, portanto quantidadeGerada
					 * deve ter valor 2
					 */
					
					dthrAtualCal.set(Calendar.AM_PM, Calendar.AM);
					
					horario.getGradeAgendamentoConsulta().getEspecialidade()
							.setIndicHoraDispFeriado(true);
					
					quantidadeGerada = systemUnderTest.criarConsulta(
							dthrAtualCal, dthrAnteriorCal, horario,
							quantidadeGerada, duracao, NOME_MICROCOMPUTADOR);
					
					assertEquals(quantidadeGerada, Integer.valueOf(2));
		} catch (BaseException e) {
			log.error(e.getMessage());
		}
	}
}
