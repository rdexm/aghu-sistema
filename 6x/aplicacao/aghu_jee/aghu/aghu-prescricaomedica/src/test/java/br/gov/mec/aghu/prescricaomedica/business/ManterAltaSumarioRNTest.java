package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioUnidTempo;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AhdHospitaisDia;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.business.ManterAltaDiagSecundarioRN.ManterAltaDiagSecundarioRNExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCidAtendimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmAltaSumarioVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * @author bsoliveira
 *
 */
public class ManterAltaSumarioRNTest extends AGHUBaseUnitTest<ManterAltaSumarioRN>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	@Mock
	private IInternacaoFacade mockedInternacaoFacade;
	@Mock
	private MpmAltaSumarioDAO mockedMpmAltaSumarioDAO;
	@Mock
	private MpmCidAtendimentoDAO mockedMpmCidAtendimentoDAO;
	@Mock
	private MpmSolicitacaoConsultoriaDAO mockedSolicitacaoConsultoriaDAO;
	@Mock
	private IAghuFacade mockedFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	
	/**
	 * Teste de nulo.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarDataTRFTest001() throws ApplicationBusinessException {
		
		final AghAtendimentos aghAtendimentos = new AghAtendimentos();
		
    	Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);
    	
		systemUnderTest.verificarDataTRF(null, null);
		
	}
	
	/**
	 * Testa exceção.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void verificarDataTRFTest002() throws ApplicationBusinessException {
		
		final AghAtendimentos aghAtendimentos = new AghAtendimentos();
		final Date d1 = new Date();
		
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d1);
		c2.add(Calendar.DAY_OF_MONTH, -1);
		Date d2 = c2.getTime();
		
		AinAtendimentosUrgencia atendimentoUrgencia = new AinAtendimentosUrgencia();
		atendimentoUrgencia.setSeq(Integer.valueOf(1));
		aghAtendimentos.setAtendimentoUrgencia(atendimentoUrgencia);
		
    	Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);
    	
    	Mockito.when(mockedInternacaoFacade.obterDataAtendimento(Mockito.anyInt())).
		thenReturn(d1);
    	
		systemUnderTest.verificarDataTRF(null, d2);
		
	}
	
	/**
	 * Testa exceção.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=NullPointerException.class)
	public void verificarAltaFuturaTest001() throws ApplicationBusinessException {
		
		final AghAtendimentos aghAtendimentos = new AghAtendimentos();
		AinAtendimentosUrgencia atendimentoUrgencia = new AinAtendimentosUrgencia();
		atendimentoUrgencia.setSeq(Integer.valueOf(1));
		aghAtendimentos.setAtendimentoUrgencia(atendimentoUrgencia);
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);
		
		systemUnderTest.verificarAltaFutura(null, null, null);
		
	}
	
	/**
	 * Testa exceção.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void verificarAltaFuturaTest002() throws ApplicationBusinessException {
		
		Calendar c1 = Calendar.getInstance();
		c1.setTime(new Date());
		c1.add(Calendar.DAY_OF_MONTH, 1);
		Date d1 = c1.getTime();
		
		final AghAtendimentos aghAtendimentos = new AghAtendimentos();
		AinAtendimentosUrgencia atendimentoUrgencia = new AinAtendimentosUrgencia();
		atendimentoUrgencia.setSeq(Integer.valueOf(1));
		aghAtendimentos.setAtendimentoUrgencia(atendimentoUrgencia);
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		aghAtendimentos.setUnidadeFuncional(unidadeFuncional);
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);
    	
		systemUnderTest.verificarAltaFutura(null, d1, null);
		
	}
	
	/**
	 * Testa exceção de data.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void verificarAltaFuturaTest003() throws ApplicationBusinessException {
		
		Calendar c1 = Calendar.getInstance();
		c1.setTime(new Date());
		c1.add(Calendar.DAY_OF_MONTH, 3);
		Date d1 = c1.getTime();
		
		final AghAtendimentos aghAtendimentos = new AghAtendimentos();
		AinAtendimentosUrgencia atendimentoUrgencia = new AinAtendimentosUrgencia();
		atendimentoUrgencia.setSeq(Integer.valueOf(1));
		aghAtendimentos.setAtendimentoUrgencia(atendimentoUrgencia);
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setNroUnidTempoPmeAdiantadas(Short.valueOf("2"));
		unidadeFuncional.setIndUnidTempoPmeAdiantada(DominioUnidTempo.D);
		aghAtendimentos.setUnidadeFuncional(unidadeFuncional);
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);

		systemUnderTest.verificarAltaFutura(null, d1, null);
		
	}
	
	/**
	 * Testa exceção de horas.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void verificarAltaFuturaTest004() throws ApplicationBusinessException {
		
		Calendar c1 = Calendar.getInstance();
		c1.setTime(new Date());
		c1.add(Calendar.DAY_OF_MONTH, 3);
		Date d1 = c1.getTime();
		
		final AghAtendimentos aghAtendimentos = new AghAtendimentos();
		AinAtendimentosUrgencia atendimentoUrgencia = new AinAtendimentosUrgencia();
		atendimentoUrgencia.setSeq(Integer.valueOf(1));
		aghAtendimentos.setAtendimentoUrgencia(atendimentoUrgencia);
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setNroUnidTempoPmeAdiantadas(Short.valueOf("1"));
		unidadeFuncional.setIndUnidTempoPmeAdiantada(DominioUnidTempo.H);
		aghAtendimentos.setUnidadeFuncional(unidadeFuncional);
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);
    	
		systemUnderTest.verificarAltaFutura(null, d1, null);
		
	}
	
	/**
	 * Teste de nulo.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void obterLocalPacienteTest001() throws ApplicationBusinessException {
		
		final AghAtendimentos aghAtendimentos = null;
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);
    	
		systemUnderTest.obterLocalPaciente(null);
		
	}

	/**
	 * Teste de retorno de string correta.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void obterLocalPacienteTestLeitos002() throws ApplicationBusinessException {
		
		final AghAtendimentos aghAtendimentos = new AghAtendimentos();
		AinLeitos leito = new AinLeitos();
		leito.setLeitoID("1");
		aghAtendimentos.setLeito(leito);
		
		String expected = "L:1";
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);		
    	
		String atual = systemUnderTest.obterLocalPaciente(null);
		
		assertEquals(expected, atual);
		
	}
	
	/**
	 * Teste de retorno de string correta.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void obterLocalPacienteTestQuartos003() throws ApplicationBusinessException {
		
		final AghAtendimentos aghAtendimentos = new AghAtendimentos();
		AinQuartos quarto = new AinQuartos();
		quarto.setNumero(Short.valueOf("2"));
		quarto.setDescricao("2");
		aghAtendimentos.setQuarto(quarto);
		
		String expected = "Q:2";
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);
    	
		String atual = systemUnderTest.obterLocalPaciente(null);
		
		assertEquals(expected, atual);
		
	}
	
	/**
	 * Teste de retorno de string correta.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void obterLocalPacienteTestUnidadeFuncional004() throws ApplicationBusinessException {
		
		final AghAtendimentos aghAtendimentos = new AghAtendimentos();
		AghUnidadesFuncionais  unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setAndar("1");
		aghAtendimentos.setUnidadeFuncional(unidadeFuncional);
		
		String expected = "01  - ";
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);
    	
		String atual = systemUnderTest.obterLocalPaciente(null);
		
		assertEquals(expected, atual);
		
	}
	
	/**
	 * Teste retorno data Atendimento de Urgência atu_seq.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void obterDataInternacaoTest001() throws ApplicationBusinessException {
		
		final Date expected = new Date();		
		
		Mockito.when(mockedInternacaoFacade.obterDataAtendimento(Mockito.anyInt())).
		thenReturn(expected);
    	
		Date atual = systemUnderTest.obterDataInternacao(null, Integer.valueOf(1), null);
		
		assertEquals(expected.toString(), atual.toString());
		
	}
	
	/**
	 * Teste retorno data Internação int_seq.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void obterDataInternacaoTest002() throws ApplicationBusinessException {
		
		final Date expected = new Date();		
		
		Mockito.when(mockedInternacaoFacade.obterDataInternacao(Mockito.anyInt())).
		thenReturn(expected);

		Date atual = systemUnderTest.obterDataInternacao(Integer.valueOf(1), null, null);
		
		assertEquals(expected.toString(), atual.toString());
		
	}
	
	/**
	 * Teste retorno data Internação int_seq.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void obterDataInternacaoTest003() throws ApplicationBusinessException {
		
		final Date expected = new Date();		
		
		Mockito.when(mockedInternacaoFacade.obterDataInternacao(Mockito.anyInt())).
		thenReturn(expected);

		Date atual = systemUnderTest.obterDataInternacao(null, null, Integer.valueOf(1));
		
		assertEquals(expected.toString(), atual.toString());
		
	}
	
	/**
	 * Teste retorno nulo.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void obterDataInternacaoTest004() throws ApplicationBusinessException {
		
		Date atual = systemUnderTest.obterDataInternacao(null, null, null);
		
		assertNull(atual);
		
	}
	
	/**
	 * Teste retorno nulo.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void obterDataInternacao2Test001() throws ApplicationBusinessException {
		
		final AghAtendimentos aghAtendimentos = null;
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);
		
		systemUnderTest.obterDataInternacao2(null);
		
	}
	
	/**
	 * Teste retorno de data inicio atendimento.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void obterDataInternacao2Test002() throws ApplicationBusinessException {
		
		Date expected = new Date();
		final AghAtendimentos aghAtendimentos = new AghAtendimentos();
		aghAtendimentos.setOrigem(DominioOrigemAtendimento.N);
		aghAtendimentos.setDthrInicio(expected);
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);

		Date atual = systemUnderTest.obterDataInternacao2(null);
		
		assertEquals(expected.toString(), atual.toString());
		
	}
	
	/**
	 * Teste retorno de data internação.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void obterDataInternacao2Test003() throws ApplicationBusinessException {
		
		final Date expected = new Date();
		final AghAtendimentos aghAtendimentos = new AghAtendimentos();
		AinInternacao internacao = new AinInternacao();
		internacao.setSeq(Integer.valueOf(1));
		aghAtendimentos.setInternacao(internacao);
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);

		Mockito.when(mockedInternacaoFacade.obterDataInternacao(Mockito.anyInt())).
		thenReturn(expected);
		
		Date atual = systemUnderTest.obterDataInternacao2(null);
		
		assertEquals(expected.toString(), atual.toString());
		
	}
	
	/**
	 * Teste retorno de data antendimento de urgência.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void obterDataInternacao2Test004() throws ApplicationBusinessException {
		
		final Date expected = new Date();
		final AghAtendimentos aghAtendimentos = new AghAtendimentos();
		AinAtendimentosUrgencia atendimentoUrgencia = new AinAtendimentosUrgencia();
		atendimentoUrgencia.setSeq(Integer.valueOf(1));
		aghAtendimentos.setAtendimentoUrgencia(atendimentoUrgencia);
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);

		Mockito.when(mockedInternacaoFacade.obterDataAtendimento(Mockito.anyInt())).
		thenReturn(expected);

		Date atual = systemUnderTest.obterDataInternacao2(null);
		
		assertEquals(expected.toString(), atual.toString());
		
	}
	
	/**
	 * Teste retorno de data hospitais dia.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void obterDataInternacao2Test005() throws ApplicationBusinessException {
		
		final Date expected = new Date();
		final AghAtendimentos aghAtendimentos = new AghAtendimentos();
		AhdHospitaisDia hospitalDia = new AhdHospitaisDia();
		hospitalDia.setSeq(Integer.valueOf(1));
		aghAtendimentos.setHospitalDia(hospitalDia);
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);

		Mockito.when(mockedInternacaoFacade.obterDataInternacao(Mockito.anyInt())).
		thenReturn(expected);

		Date atual = systemUnderTest.obterDataInternacao2(null);
		
		assertEquals(expected.toString(), atual.toString());
		
	}
	
	/**
	 * Testa exceção.
	 * if (atendimento.getDthrFim() != null)
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void verificarDataAltaTest001() throws ApplicationBusinessException {
		
		Calendar c1 = Calendar.getInstance();
		c1.setTime(new Date());
		Date d1 = c1.getTime();
		
		Calendar c2 = Calendar.getInstance();
		c2.setTime(new Date());
		c2.add(Calendar.DAY_OF_MONTH, -1);
		Date d2 = c2.getTime();
		
		Calendar c3 = Calendar.getInstance();
		c3.setTime(new Date());
		c3.add(Calendar.DAY_OF_MONTH, -10);
		Date d3 = c3.getTime();
		
		final AghAtendimentos aghAtendimentos = new AghAtendimentos();
		aghAtendimentos.setIndPacAtendimento(DominioPacAtendimento.N);
		aghAtendimentos.setDthrFim(d1);
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);

		systemUnderTest.verificarDataAlta(Integer.valueOf(1), d3, d2, DominioOperacoesJournal.UPD, DominioIndTipoAltaSumarios.ALT);
		
	}
	
	/**
	 * Testa exceção.
	 * if (dthrNovaDataAlta.isAfter(dthrFim))
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void verificarDataAltaTest002() throws ApplicationBusinessException {
		
		Calendar c1 = Calendar.getInstance();
		c1.setTime(new Date());
		Date d1 = c1.getTime();
		
		Calendar c2 = Calendar.getInstance();
		c2.setTime(new Date());
		c2.add(Calendar.DAY_OF_MONTH, -1);
		Date d2 = c2.getTime();
		
		Calendar c3 = Calendar.getInstance();
		c3.setTime(new Date());
		c3.add(Calendar.DAY_OF_MONTH, 10);
		Date d3 = c3.getTime();
		
		final AghAtendimentos aghAtendimentos = new AghAtendimentos();
		aghAtendimentos.setIndPacAtendimento(DominioPacAtendimento.N);
		aghAtendimentos.setDthrFim(d1);
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);

		systemUnderTest.verificarDataAlta(Integer.valueOf(1), d3, d2, DominioOperacoesJournal.UPD, DominioIndTipoAltaSumarios.ALT);
		
	}
	
	/**
	 * Testa exceção.
	 * if (dthrNovaDataAlta.isAfter(dthrFim))
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void verificarDataAltaTest003() throws ApplicationBusinessException {
		
		Calendar c1 = Calendar.getInstance();
		c1.setTime(new Date());
		Date d1 = c1.getTime();
		
		Calendar c2 = Calendar.getInstance();
		c2.setTime(new Date());
		c2.add(Calendar.DAY_OF_MONTH, -1);
		Date d2 = c2.getTime();
		
		Calendar c3 = Calendar.getInstance();
		c3.setTime(new Date());
		c3.add(Calendar.DAY_OF_MONTH, -10);
		Date d3 = c3.getTime();
		
		final AghAtendimentos aghAtendimentos = new AghAtendimentos();
		aghAtendimentos.setIndPacAtendimento(DominioPacAtendimento.O);
		aghAtendimentos.setDthrFim(d1);
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(aghAtendimentos);

		systemUnderTest.verificarDataAlta(Integer.valueOf(1), d3, d2, DominioOperacoesJournal.UPD, DominioIndTipoAltaSumarios.ALT);
		
	}
	
	/**
	 * Testa exceção.
	 * if (altaSumario.getServidorAltera() == null)
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void atualizarAltaSumarioTest001() throws ApplicationBusinessException {
		
		MpmAltaSumario altaSumario = new MpmAltaSumario();
		final MpmAltaSumarioVO altaSumarioOriginal = new MpmAltaSumarioVO();
		AghAtendimentos aghAtendimentos = new AghAtendimentos();
		altaSumario.setAtendimento(aghAtendimentos);
		altaSumario.setConcluido(DominioIndConcluido.S);
		altaSumario.setSituacao(DominioSituacao.I);
		
		MpmAltaSumarioId id = new MpmAltaSumarioId();
		altaSumario.setId(id);
		
		//Implementação do Mock
		
		Mockito.when(mockedMpmAltaSumarioDAO.obterAltaSumarioOriginal(Mockito.any(MpmAltaSumario.class))).
		thenReturn(altaSumarioOriginal);

		systemUnderTest.atualizarAltaSumario(altaSumario, NOME_MICROCOMPUTADOR);
		
	}
	
	/**
	 * Verifica se o parametro altaSumario.getTransfConcluida()
	 * tem retorno falso.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void inserirAltaSumarioTest001() throws ApplicationBusinessException {
		
		MpmAltaSumario altaSumario = new MpmAltaSumario();
		final MpmAltaSumario altaSumarioOriginal = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		AghAtendimentos aghAtendimentos = new AghAtendimentos();
		altaSumario.setAtendimento(aghAtendimentos);
		

		Mockito.when(mockedMpmAltaSumarioDAO.obterAltaSumarioPeloId(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).
		thenReturn(altaSumarioOriginal);

		try {
			whenObterServidorLogado();
		} catch (BaseException e) {
			fail();
		}
		
		systemUnderTest.inserirAltaSumario(altaSumario);
		
		assertFalse(altaSumario.getTransfConcluida().booleanValue());
		
	}
	
	
	/**
	 * Verifica se os parametro DthrElaboracaoTransf é nulo.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void inserirAltaSumarioTest003() throws ApplicationBusinessException {
		
		MpmAltaSumario altaSumario = new MpmAltaSumario();
		final MpmAltaSumario altaSumarioOriginal = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		AghAtendimentos aghAtendimentos = new AghAtendimentos();
		altaSumario.setAtendimento(aghAtendimentos);

		Mockito.when(mockedMpmAltaSumarioDAO.obterAltaSumarioPeloId(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).
		thenReturn(altaSumarioOriginal);
    	
		try {
			whenObterServidorLogado();
		} catch (BaseException e) {
			fail();
		}
		
		systemUnderTest.inserirAltaSumario(altaSumario);
		
		assertNull(altaSumario.getDthrElaboracaoTransf());
		
	}
	
	/**
	 * Teste exceção.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarAltaSumarioTest001() throws ApplicationBusinessException {

		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		
		systemUnderTest.verificarAltaSumarioAtivo(altaSumario);

	}
	
	/**
	 * Teste exceção.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarTipoAlteracaoTest001() throws ApplicationBusinessException {

		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setTipo(DominioIndTipoAltaSumarios.OBT);

		Mockito.when(mockedMpmAltaSumarioDAO.obterAltaSumarioPeloIdESituacao(Mockito.any(MpmAltaSumarioId.class), Mockito.any(DominioSituacao.class))).
		thenReturn(altaSumario);

		systemUnderTest.verificarTipoAlteracao(null);

	}
	
	/**
	 * Teste exceção.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarCidAtendimentoTest001() throws ApplicationBusinessException {

		Mockito.when(mockedMpmCidAtendimentoDAO.obterCidAtendimentoPeloId(Mockito.anyInt())).
		thenReturn(null);

		systemUnderTest.verificarCidAtendimento(Integer.valueOf("1"));

	}
	
	/**
	 * Teste deve retornar exceção.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void verificarIndCargaTest001() throws ApplicationBusinessException {

		systemUnderTest.verificarIndCarga(Boolean.TRUE, Boolean.FALSE, ManterAltaDiagSecundarioRNExceptionCode.MPM_02615);

	}
	
	/**
	 * Teste deve retornar exceção.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void verificarIndCargaTest002() throws ApplicationBusinessException {

		systemUnderTest.verificarIndCarga(Boolean.FALSE, Boolean.TRUE, ManterAltaDiagSecundarioRNExceptionCode.MPM_02615);

	}
	
	/**
	 * Teste deve retornar exceção.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void verificarIndCargaTest003() throws ApplicationBusinessException {

		systemUnderTest.verificarIndCarga(null, Boolean.TRUE, ManterAltaDiagSecundarioRNExceptionCode.MPM_02615);

	}
	
	/**
	 * Teste deve retornar exceção.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void verificarIndCargaTest004() throws ApplicationBusinessException {

		systemUnderTest.verificarIndCarga(Boolean.TRUE, null, ManterAltaDiagSecundarioRNExceptionCode.MPM_02615);

	}
	
	/**
	 * Teste não deve retornar exceção.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarIndCargaTest005() throws ApplicationBusinessException {

		systemUnderTest.verificarIndCarga(null, null, ManterAltaDiagSecundarioRNExceptionCode.MPM_02615);

	}
	
	/**
	 * Teste não deve retornar exceção.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarIndCargaTest006() throws ApplicationBusinessException {

		systemUnderTest.verificarIndCarga(Boolean.TRUE, Boolean.TRUE, ManterAltaDiagSecundarioRNExceptionCode.MPM_02615);

	}
	
	/**
	 * Teste não deve retornar exceção.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarIndCargaTest007() throws ApplicationBusinessException {

		systemUnderTest.verificarIndCarga(Boolean.FALSE, Boolean.FALSE, ManterAltaDiagSecundarioRNExceptionCode.MPM_02615);

	}
	
	/**
	 * Teste deve retornar exceção.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void verificarSituacaoTest001() throws ApplicationBusinessException {

		systemUnderTest.verificarSituacao(DominioSituacao.A, DominioSituacao.I, ManterAltaDiagSecundarioRNExceptionCode.MPM_02615);

	}
	
	/**
	 * Teste não deve retornar exceção.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarSituacaoTest002() throws ApplicationBusinessException {

		systemUnderTest.verificarSituacao(null, DominioSituacao.I, ManterAltaDiagSecundarioRNExceptionCode.MPM_02615);

	}
	
	/**
	 * Teste não deve retornar exceção.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarSituacaoTest003() throws ApplicationBusinessException {

		systemUnderTest.verificarSituacao(null, null, ManterAltaDiagSecundarioRNExceptionCode.MPM_02615);

	}
	
	/**
	 * Teste não deve retornar exceção.
	 * 
	 * Pois a data passada está entre as datas.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarDtHrTest001() throws BaseException {
		
		final AghAtendimentos atendimento = new AghAtendimentos();
		AhdHospitaisDia hospitalDia = new AhdHospitaisDia();
		hospitalDia.setSeq(Integer.valueOf(1));
		atendimento.setHospitalDia(hospitalDia);
		atendimento.setDthrFim(DateUtil.adicionaDias(new Date(), 10));
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(atendimento);
		
		Mockito.when(mockedInternacaoFacade.obterDataInternacao(Mockito.anyInt())).
		thenReturn(new Date());

		Mockito.when(mockedFacade.obterAtendimentoPeloSeq(Mockito.anyInt())).
		thenReturn(atendimento);

    	systemUnderTest.verificarDtHr(null, DateUtil.adicionaDias(new Date(), 1));
		
	}
	
	/**
	 * Teste deve retornar exceção.
	 * 
	 * Mensagem de código MPM_02874.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarDtHrTest002() throws BaseException {
		
		final AghAtendimentos atendimento = new AghAtendimentos();
		AhdHospitaisDia hospitalDia = new AhdHospitaisDia();
		hospitalDia.setSeq(Integer.valueOf(1));
		atendimento.setHospitalDia(hospitalDia);
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(atendimento);
		
		Mockito.when(mockedInternacaoFacade.obterDataInternacao(Mockito.anyInt())).
		thenReturn(new Date());

		Mockito.when(mockedFacade.obterAtendimentoPeloSeq(Mockito.anyInt())).
		thenReturn(atendimento);
		
    	try {
    	
    		systemUnderTest.verificarDtHr(null, DateUtil.adicionaDias(new Date(), 1));
    		fail("Deveria ter ocorrido uma exceção!!!");
    		
    	} catch (ApplicationBusinessException atual) {
    		
    		assertEquals("MPM_02874", atual.getMessage());
    		
    	}
		
	}
	
	/**
	 * Teste deve retornar exceção.
	 * 
	 * Mensagem de código MPM_02875.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarDtHrTest003() throws BaseException {
		
		final AghAtendimentos atendimento = new AghAtendimentos();
		AhdHospitaisDia hospitalDia = new AhdHospitaisDia();
		hospitalDia.setSeq(Integer.valueOf(1));
		atendimento.setHospitalDia(hospitalDia);
		
		final Date data = new Date();
		
		Mockito.when(mockedFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).
		thenReturn(atendimento);
		
		Mockito.when(mockedInternacaoFacade.obterDataInternacao(Mockito.anyInt())).
		thenReturn(data);

		Mockito.when(mockedFacade.obterAtendimentoPeloSeq(Mockito.anyInt())).
		thenReturn(null);
		
    	try {
    	
    		systemUnderTest.verificarDtHr(null, DateUtil.adicionaDias(new Date(), 1));
    		fail("Deveria ter ocorrido uma exceção!!!");
    		
    	} catch (ApplicationBusinessException atual) {
    		
    		assertEquals("MPM_02875", atual.getMessage());
    		
    	}
		
	}
	
	/**
	 * Teste deve retornar exceção.
	 * 
	 * Mensagem de código MPM_00355.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarSolicitacaoConsultoria001() throws ApplicationBusinessException {
		
		Mockito.when(mockedSolicitacaoConsultoriaDAO.obterSolicitacaoConsultoriaPeloSeq(Mockito.anyInt())).
		thenReturn(null);
		
    	try {
    	
    		systemUnderTest.verificarSolicitacaoConsultoria(Integer.valueOf("1"));
    		fail("Deveria ter ocorrido uma exceção!!!");
    		
    	} catch (ApplicationBusinessException atual) {
    		
    		assertEquals("MPM_00355", atual.getMessage());
    		
    	}
		
	}

    private void whenObterServidorLogado() throws BaseException {
		RapServidores rap =  new RapServidores(new RapServidoresId(1, (short) 1)) ;
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
    }
}
