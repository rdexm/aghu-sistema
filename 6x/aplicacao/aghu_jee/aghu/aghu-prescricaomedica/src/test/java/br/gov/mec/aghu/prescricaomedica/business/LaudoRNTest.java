package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.model.MpmLaudo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmLaudoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class LaudoRNTest extends AGHUBaseUnitTest<LaudoRN>{

	@Mock
	private MpmLaudoDAO mockedMpmLaudoDAO;
	@Mock
	private LaudoJournalRN mockedLaudoJournalRN;
	

	/**
	 * Testa um fluxo normal de execucao do metodo verificarDatasLaudo. 
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarDatasLaudoTest() throws ApplicationBusinessException {
		
		Calendar dataInicio = Calendar.getInstance();
		dataInicio.add(Calendar.YEAR, -1);
		
		systemUnderTest.verificarDatasLaudo(dataInicio.getTime(), new Date(), new Date());
	}

	/**
	 * Testa o lancamento de exception quando a data de inicio eh maior que a data de fim
	 * @throws AGHUNegocioException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarDatasLaudoDataInicioMaiorQueDataFimTest() throws ApplicationBusinessException {
		
		Calendar dataInicio = Calendar.getInstance();
		dataInicio.add(Calendar.YEAR, 1);
		
		systemUnderTest.verificarDatasLaudo(dataInicio.getTime(), new Date(), null);
	}

	/**
	 * Testa o lancamento de exception quando a data de inicio eh maior que a data prevista.
	 * @throws AGHUNegocioException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarDatasLaudoDataInicioMaiorQueDataPrevistaTest() throws ApplicationBusinessException {
		
		Calendar dataInicio = Calendar.getInstance();
		dataInicio.add(Calendar.YEAR, 1);
		
		systemUnderTest.verificarDatasLaudo(dataInicio.getTime(), null, new Date());
	}

	/**
	 * Testa um fluxo normal de execucao do metodo verificarLaudoManual. 
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarLaudoManualTest() throws ApplicationBusinessException {
		
		systemUnderTest.verificarLaudoManual(false, "teste", true);
	}

	/**
	 * Testa um fluxo de execucao do metodo verificarLaudoManual no qual acontece um nullPointer. 
	 * @throws AGHUNegocioException
	 */
	@Test(expected = NullPointerException.class)
	public void verificarLaudoManualNullTest() throws ApplicationBusinessException {
		
		systemUnderTest.verificarLaudoManual(null, null, true);
	}

	/**
	 * Testa o fluxo de execucao do metodo verificarLaudoManual, 
	 * se o laudo for manual e a justificativa estiver preenchida. 
	 * @throws AGHUNegocioException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarLaudoManualJustificativaTest() throws ApplicationBusinessException {
		
		systemUnderTest.verificarLaudoManual(true, "teste", true);
	}

	/**
	 * Testa o fluxo de execucao do metodo verificarLaudoManual, 
	 * se o laudo for manual e a justificativa nao estiver preenchida. 
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarLaudoManualSemJustificativaTest() throws ApplicationBusinessException {
		
		systemUnderTest.verificarLaudoManual(true, null, true);
	}

	/**
	 * Testa o fluxo de execucao do metodo verificarLaudoManual, 
	 * se o laudo nao for manual e a justificativa nao estiver preenchida. 
	 * @throws AGHUNegocioException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarLaudoNaoManualJustificativaTest() throws ApplicationBusinessException {
		
		systemUnderTest.verificarLaudoManual(false, null, true);
	}

	/**
	 * Testa o fluxo de execucao do metodo verificarLaudoManual, 
	 * se o laudo nao for manual e a justificativa estiver preenchida e for impresso. 
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarLaudoNaoManualComJustificativaEImpressoTest() throws ApplicationBusinessException {
		
		systemUnderTest.verificarLaudoManual(false, "teste", true);
	}

	/**
	 * Testa o fluxo de execucao normal do metodo verificarServidorManualLaudo, 
	 *  quando o laudo eh manual e se possui servidor laudo manual
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarServidorManualLaudoTest() throws ApplicationBusinessException {
		RapServidores servidor = new RapServidores();
		RapServidoresId id = new RapServidoresId(Integer.valueOf("1"), Short.valueOf("4"));
		servidor.setId(id);
		systemUnderTest.verificarServidorManualLaudo(true, servidor);
	}

	/**
	 * Testa o fluxo de execucao normal do metodo verificarServidorManualLaudo,
	 * quando o laudo nao eh manual e nao existe servidor laudo manual. 
	 *  
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarSemServidorManualLaudoTest() throws ApplicationBusinessException {
		
		systemUnderTest.verificarServidorManualLaudo(false, null);
	}

	/**
	 * Testa o fluxo de execucao do metodo verificarServidorManualLaudo,
	 * quando o laudo nao eh manual e existe servidor laudo manual.
	 * @throws AGHUNegocioException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarServidorManualSemLaudoManualTest() throws ApplicationBusinessException {
		RapServidores servidor = new RapServidores();
		RapServidoresId id = new RapServidoresId(Integer.valueOf("1"), Short.valueOf("4"));
		servidor.setId(id);
		systemUnderTest.verificarServidorManualLaudo(false, servidor);
	}

	/**
	 * Testa o fluxo de execucao do metodo verificarServidorManualLaudo,
	 * quando o laudo eh manual e nao existe servidor laudo manual.
	 * @throws AGHUNegocioException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarServidorManualComLaudoManualTest() throws ApplicationBusinessException {
		RapServidores servidor = new RapServidores();
		RapServidoresId id = new RapServidoresId(Integer.valueOf("1"), Short.valueOf("4"));
		servidor.setId(id);
		systemUnderTest.verificarServidorManualLaudo(true, null);
	}

	/**
	 * Testa o fluxo de execucao do metodo executarTriggerAntesDeUpdate.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void executarTriggerAntesDeUpdateTest() throws ApplicationBusinessException {
		
		
		Calendar dataInicio = Calendar.getInstance();
		dataInicio.add(Calendar.YEAR, -1);
		
			
		RapServidores servidor = new RapServidores();
		RapServidoresId id = new RapServidoresId(Integer.valueOf("1"), Short.valueOf("4"));
		servidor.setId(id);
		
		MpmLaudo laudo = new MpmLaudo();
		laudo.setDthrInicioValidade(dataInicio.getTime());
		laudo.setDthrFimValidade(new Date());
		laudo.setDthrFimPrevisao(new Date());
		laudo.setLaudoManual(true);
		laudo.setJustificativa(null);
		laudo.setImpresso(true);
		laudo.setServidorFeitoManual(servidor);
		
		systemUnderTest.executarTriggerAntesDeUpdate(laudo);
		
	}

	/**
	 * Testa o fluxo de execucao do metodo executarTriggerAntesDeUpdate.
	 * @throws BaseException 
	 */
	@Test
	public void atualizarLaudo() throws BaseException {
		
		
		Calendar dataInicio = Calendar.getInstance();
		dataInicio.add(Calendar.YEAR, -1);
		
		
		RapServidores servidor = new RapServidores();
		RapServidoresId id = new RapServidoresId(Integer.valueOf("1"), Short.valueOf("4"));
		servidor.setId(id);
		
		MpmLaudo laudo = new MpmLaudo();
		laudo.setDthrInicioValidade(dataInicio.getTime());
		laudo.setDthrFimValidade(new Date());
		laudo.setDthrFimPrevisao(new Date());
		laudo.setLaudoManual(true);
		laudo.setJustificativa(null);
		laudo.setImpresso(true);
		laudo.setServidorFeitoManual(servidor);
		

		systemUnderTest.atualizarLaudo(laudo, laudo);
		
	}

}
