package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmHorarioInicAprazamento;
import br.gov.mec.aghu.model.MpmHorarioInicAprazamentoId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.business.ManterHorarioInicAprazamentoRN.ManterHorarioInicAprazamentoRNExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmHorarioInicAprazamentJnDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmHorarioInicAprazamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterHorarioInicAprazamentoONTest extends AGHUBaseUnitTest<ManterHorarioInicAprazamentoON>{

	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade; 
	@Mock
	private MpmHorarioInicAprazamentoDAO mpmHorarioInicAprazamentoDAO;
	@Mock
	private IAghuFacade aghuFacade;
	@Mock
	private MpmHorarioInicAprazamentJnDAO mpmHorarioInicAprazamentJnDAO;
	@Mock
	private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;
	@Mock
	private ManterHorarioInicAprazamentoRN mockedManterHorarioInicAprazamentoRN;
	
	private MpmHorarioInicAprazamento obterHorarioAprazamentoMock(){
		MpmHorarioInicAprazamento horarioAprazamentoMock = new MpmHorarioInicAprazamento();
		MpmHorarioInicAprazamentoId idMock = new MpmHorarioInicAprazamentoId();
		MpmTipoFrequenciaAprazamento tipoFreqAprazamentoMock = new MpmTipoFrequenciaAprazamento();
		RapServidores servidorMock = new RapServidores();
		AghUnidadesFuncionais unidadeFuncionalMock = new AghUnidadesFuncionais();
		
		//Cria mock para id
		idMock.setFrequencia(Short.valueOf("3"));
		idMock.setTfqSeq(Short.valueOf("3"));
		idMock.setUnfSeq(Short.valueOf("128"));
		
		
		//Cria mock para tipoFrequenciaAprazamento
		tipoFreqAprazamentoMock.setSeq(Short.valueOf("3"));
		tipoFreqAprazamentoMock.setIndSituacao(DominioSituacao.A);
		tipoFreqAprazamentoMock.setIndDigitaFrequencia(Boolean.TRUE);
		
		//Cria mock de servidor
		RapServidoresId servidorIdMock = new RapServidoresId();
		servidorMock.setId(servidorIdMock);
		
		//cria mock de unidade funcional
		unidadeFuncionalMock.setSeq(Short.valueOf("128"));
		unidadeFuncionalMock.setIndSitUnidFunc(DominioSituacao.A);
		
		//cria mock de horario de início aprazamento
		horarioAprazamentoMock.setCriadoEm(new Date());
		horarioAprazamentoMock.setHorarioInicio(new Date());
		horarioAprazamentoMock.setId(idMock);
		horarioAprazamentoMock.setIndSituacao(DominioSituacao.A);
		horarioAprazamentoMock.setServidor(servidorMock);
		horarioAprazamentoMock.setTipoFreqAprazamento(tipoFreqAprazamentoMock);
		horarioAprazamentoMock.setUnidadeFuncional(unidadeFuncionalMock);
		
		return horarioAprazamentoMock;
	}
	
	
	/**
	 * Testa o salvamento do horário aprazamento, sem validações
	 * @throws ParseException 
	 * @author clayton.bras
	 * @throws BaseException 
	 */
	@Test
	public void gravarHorarioInicioAprazamentoTest() throws BaseException {
		final MpmHorarioInicAprazamento horarioAprazamentoMock = obterHorarioAprazamentoMock();
		final List<AghUnidadesFuncionais> unidades = new ArrayList<AghUnidadesFuncionais>();
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		unidades.add(unidade);
		horarioAprazamentoMock.setCriadoEm(null);
		
		final MpmTipoFrequenciaAprazamento tipoFreqAprazamentoMock = new MpmTipoFrequenciaAprazamento();
		final AghUnidadesFuncionais unidadeFuncionalMock = new AghUnidadesFuncionais();
		
		//Cria mock para tipoFrequenciaAprazamento
		tipoFreqAprazamentoMock.setSeq(Short.valueOf("3"));
		tipoFreqAprazamentoMock.setIndSituacao(DominioSituacao.A);
		tipoFreqAprazamentoMock.setIndDigitaFrequencia(Boolean.TRUE);
		
		//cria mock de unidade funcional
		unidadeFuncionalMock.setSeq(Short.valueOf("128"));
		unidadeFuncionalMock.setIndSitUnidFunc(DominioSituacao.A);
		
		horarioAprazamentoMock.setUnidadeFuncional(unidadeFuncionalMock);
		horarioAprazamentoMock.setTipoFreqAprazamento(tipoFreqAprazamentoMock);
		
		Mockito.when(mpmHorarioInicAprazamentoDAO.obterPorChavePrimaria(horarioAprazamentoMock.getId())).thenReturn(null);

		Mockito.when(aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(horarioAprazamentoMock.getId().getUnfSeq())).thenReturn(unidadeFuncionalMock);
		
		Mockito.when(mpmTipoFrequenciaAprazamentoDAO.obterPorChavePrimaria(horarioAprazamentoMock.getId().getTfqSeq())).thenReturn(tipoFreqAprazamentoMock);
		
		try {
			systemUnderTest.persistirHorarioInicioAprazamento(horarioAprazamentoMock);
		} catch (BaseException e) {
			fail("Erro ao executar gravarHorarioInicioAprazamentoTest: " + e.getMessage());
		} 
	}
	
	/**
	 * Testa o salvamento do horario inicio aprazamento com validação de campos não nulos
	 * @throws ParseException
	 * @author clayton.bras
	 * @throws BaseException 
	 */
	@Test
	public void gravarHorarioInicioAprazamentoValidacoesCamposNulosTest() throws  ParseException, BaseException{
		
		final int EXCEPTIONS_ESPERADAS = 3;
		
		final MpmHorarioInicAprazamento horarioAprazamentoMock = obterHorarioAprazamentoMock();
		horarioAprazamentoMock.setCriadoEm(null);//para o caso onde não é edição
		//Manipula o horario aprazamento para gerar os erros de validaçãos
		
		horarioAprazamentoMock.getId().setTfqSeq(null);
		horarioAprazamentoMock.getId().setUnfSeq(null);
		horarioAprazamentoMock.setHorarioInicio(null);
		horarioAprazamentoMock.setTipoFreqAprazamento(null);
		horarioAprazamentoMock.setUnidadeFuncional(null);
		
		
		Mockito.when(mpmHorarioInicAprazamentoDAO.obterPorChavePrimaria(horarioAprazamentoMock.getId())).thenReturn(null);

		Mockito.when(aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(horarioAprazamentoMock.getId().getUnfSeq())).thenReturn(new AghUnidadesFuncionais());
		
		
		try {
			systemUnderTest.persistirHorarioInicioAprazamento(horarioAprazamentoMock);
		} catch (BaseListException e) {
			catchGravarHorarioInicioAprazamento(EXCEPTIONS_ESPERADAS, e);
		} catch (BaseException e) {
			fail("Erro ao executar gravarHorarioInicioAprazamentoValidacoesCamposNulosTest: " + e.getMessage());
		} 
	}
	
	
	/**
	 * Testa o salvamento do horario inicio aprazamento com validação de situações de unidade funcional e tipo frequencia
	 * @throws ParseException
	 * * @author clayton.bras
	 * @throws BaseException 
	 */
	@Test
	public void gravarHorarioInicioAprazamentoValidacoesSituacoesUnidadeFuncionalTipoFrequenciaTest() throws  ParseException, BaseException{
		
		final int EXCEPTIONS_ESPERADAS = 3;
		
		final MpmHorarioInicAprazamento horarioAprazamentoMock = obterHorarioAprazamentoMock();
		final List<AghUnidadesFuncionais> unidades = new ArrayList<AghUnidadesFuncionais>();
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		unidades.add(unidade);
		horarioAprazamentoMock.setCriadoEm(null);//para o caso onde não é edição
		final MpmTipoFrequenciaAprazamento tipoFreqAprazamentoMock = new MpmTipoFrequenciaAprazamento();
		final AghUnidadesFuncionais unidadeFuncionalMock = new AghUnidadesFuncionais();
		
		//Cria mock para tipoFrequenciaAprazamento
		tipoFreqAprazamentoMock.setSeq(Short.valueOf("3"));
		tipoFreqAprazamentoMock.setIndSituacao(DominioSituacao.I);
		tipoFreqAprazamentoMock.setIndDigitaFrequencia(Boolean.FALSE);
		
		//cria mock de unidade funcional
		unidadeFuncionalMock.setSeq(Short.valueOf("128"));
		unidadeFuncionalMock.setIndSitUnidFunc(DominioSituacao.I);
		
		horarioAprazamentoMock.setUnidadeFuncional(unidadeFuncionalMock);
		horarioAprazamentoMock.setTipoFreqAprazamento(tipoFreqAprazamentoMock);
		

		Mockito.when(mpmHorarioInicAprazamentoDAO.obterPorChavePrimaria(horarioAprazamentoMock.getId())).thenReturn(null);

		Mockito.when(aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(null)).thenReturn(new AghUnidadesFuncionais());

		Mockito.when(aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(horarioAprazamentoMock.getId().getUnfSeq())).thenReturn(unidadeFuncionalMock);

		Mockito.when(mpmTipoFrequenciaAprazamentoDAO.obterPorChavePrimaria(horarioAprazamentoMock.getId().getTfqSeq())).thenReturn(tipoFreqAprazamentoMock);

		try {
			systemUnderTest.persistirHorarioInicioAprazamento(horarioAprazamentoMock);
		} catch (BaseListException e) {
			
			catchGravarHorarioInicioAprazamento(EXCEPTIONS_ESPERADAS, e);
		} catch (BaseException e) {
			fail("Erro ao executar gravarHorarioInicioAprazamentoValidacoesSituacoesUnidadeFuncionalTipoFrequenciaTest: " + e.getMessage());
		} 
	}
	
	
	/**
	 * Testa o salvamento do horario inicio aprazamento já existente
	 * @throws ParseException
	 * @author clayton.bras
	 * @throws BaseException 
	 */
	@Test
	public void gravarHorarioInicioAprazamentoValidacoesHorarioAprazamentoJaExistenteTest() throws ParseException, BaseException{
		final int EXCEPTIONS_ESPERADAS = 1;
		
		final MpmHorarioInicAprazamento horarioAprazamentoMock = obterHorarioAprazamentoMock();
		
		final MpmTipoFrequenciaAprazamento tipoFreqAprazamentoMock = new MpmTipoFrequenciaAprazamento();
		final AghUnidadesFuncionais unidadeFuncionalMock = new AghUnidadesFuncionais();
		
		//Cria mock para tipoFrequenciaAprazamento
		tipoFreqAprazamentoMock.setSeq(Short.valueOf("3"));
		tipoFreqAprazamentoMock.setIndSituacao(DominioSituacao.A);
		tipoFreqAprazamentoMock.setIndDigitaFrequencia(Boolean.TRUE);
		
		//cria mock de unidade funcional
		unidadeFuncionalMock.setSeq(Short.valueOf("128"));
		unidadeFuncionalMock.setIndSitUnidFunc(DominioSituacao.A);
		
		horarioAprazamentoMock.setUnidadeFuncional(unidadeFuncionalMock);
		horarioAprazamentoMock.setTipoFreqAprazamento(tipoFreqAprazamentoMock);
		
		horarioAprazamentoMock.setCriadoEm(null);//para o caso onde não é edição

		Mockito.when(mpmHorarioInicAprazamentoDAO.obterPorChavePrimaria(horarioAprazamentoMock.getId())).thenReturn(horarioAprazamentoMock);

		Mockito.when(aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(horarioAprazamentoMock.getId().getUnfSeq())).thenReturn(unidadeFuncionalMock);

		Mockito.when(mpmTipoFrequenciaAprazamentoDAO.obterPorChavePrimaria(horarioAprazamentoMock.getId().getTfqSeq())).thenReturn(tipoFreqAprazamentoMock);
		
		try {
			systemUnderTest.persistirHorarioInicioAprazamento(horarioAprazamentoMock);
		} catch (BaseListException e) {
			catchGravarHorarioInicioAprazamento(EXCEPTIONS_ESPERADAS, e);
		} catch (BaseException e) {
			if(e.getCode() != ManterHorarioInicAprazamentoRNExceptionCode.ERRO_HORARIO_APRAZAMENTO_JA_EXISTENTE){
				fail("gravarHorarioInicioAprazamentoValidacoesHorarioAprazamentoJaExistenteTest:  exceções não esperadas foram lançadas.");
			}
		} 
	}
	
	/**
	 * Testa edição do horário aprazamento
	 * @throws ParseException 
	 * @author clayton.bras
	 * @throws BaseException 
	 */
	@Test
	public void gravarHorarioInicioAprazamentoEdicaoSemAlteracaoCamposTest() throws ParseException, BaseException {
		final MpmHorarioInicAprazamento horarioAprazamentoMock = obterHorarioAprazamentoMock();
		final MpmHorarioInicAprazamento horarioAprazamentoOriginalMock = obterHorarioAprazamentoMock();
		final List<AghUnidadesFuncionais> unidades = new ArrayList<AghUnidadesFuncionais>();
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		unidades.add(unidade);
		
		final MpmTipoFrequenciaAprazamento tipoFreqAprazamentoMock = new MpmTipoFrequenciaAprazamento();
		final AghUnidadesFuncionais unidadeFuncionalMock = new AghUnidadesFuncionais();
		
		//Cria mock para tipoFrequenciaAprazamento
		tipoFreqAprazamentoMock.setSeq(Short.valueOf("3"));
		tipoFreqAprazamentoMock.setIndSituacao(DominioSituacao.A);
		tipoFreqAprazamentoMock.setIndDigitaFrequencia(Boolean.TRUE);
		
		//cria mock de unidade funcional
		unidadeFuncionalMock.setSeq(Short.valueOf("128"));
		unidadeFuncionalMock.setIndSitUnidFunc(DominioSituacao.A);
		
		horarioAprazamentoMock.setUnidadeFuncional(unidadeFuncionalMock);
		horarioAprazamentoMock.setTipoFreqAprazamento(tipoFreqAprazamentoMock);
		final RapServidoresId servidorIdMock = new RapServidoresId();
		servidorIdMock.setMatricula(99998);
		servidorIdMock.setVinCodigo(Short.valueOf("5"));
		final RapServidores servidorMock = new RapServidores();
		servidorMock.setId(servidorIdMock);
		
		Mockito.when(mpmHorarioInicAprazamentoDAO.atualizar(horarioAprazamentoMock)).thenReturn(horarioAprazamentoMock);

		Mockito.when(mpmHorarioInicAprazamentoDAO.obterPorChavePrimaria(horarioAprazamentoMock.getId())).thenReturn(horarioAprazamentoMock);
		
		Mockito.when(aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(horarioAprazamentoMock.getId().getUnfSeq())).thenReturn(unidadeFuncionalMock);

		Mockito.when(mpmTipoFrequenciaAprazamentoDAO.obterPorChavePrimaria(horarioAprazamentoMock.getId().getTfqSeq())).thenReturn(tipoFreqAprazamentoMock);
			
		try {
			systemUnderTest.persistirHorarioInicioAprazamento(horarioAprazamentoMock);
		} catch (BaseListException e) {
			fail("Erro ao executar gravarHorarioInicioAprazamentoAlteracaoTest: " + e.getMessage());
		} 
	}
	
	/**
	 * Testa edição do horário aprazamento
	 * @throws ParseException 
	 * @author clayton.bras
	 * @throws BaseException 
	 */
	@Test
	public void gravarHorarioInicioAprazamentoEdicaoComAlteracaoCamposTest() throws ParseException, BaseException {
		final MpmHorarioInicAprazamento horarioAprazamentoMock = obterHorarioAprazamentoMock();
		final MpmHorarioInicAprazamento horarioAprazamentoOriginalMock = obterHorarioAprazamentoMock();
		final List<AghUnidadesFuncionais> unidades = new ArrayList<AghUnidadesFuncionais>();
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		unidades.add(unidade);
		final RapServidoresId servidorIdMock = new RapServidoresId();
		servidorIdMock.setMatricula(99998);
		servidorIdMock.setVinCodigo(Short.valueOf("5"));
		final RapServidores servidorMock = new RapServidores();
		servidorMock.setId(servidorIdMock);
		servidorMock.setPessoaFisica(new RapPessoasFisicas());
		servidorMock.getPessoaFisica().setCodigo(222);
		servidorMock.getPessoaFisica().setNome("NOME_QUALQUER");
		horarioAprazamentoOriginalMock.setServidor(servidorMock);
		
		horarioAprazamentoMock.setCriadoEm(new Date());//alteração da data de criação
		horarioAprazamentoMock.setHorarioInicio(new Date());//alteração do horário de início
		horarioAprazamentoMock.setIndSituacao(DominioSituacao.I);//alteração da situação
		horarioAprazamentoMock.getServidor().getId().setMatricula(7777);//altera servidor
		horarioAprazamentoMock.getServidor().getId().setVinCodigo(Short.valueOf("1"));//altera servidor
		horarioAprazamentoMock.getUnidadeFuncional().setSeq(Short.valueOf("99"));//alteração da unidade funcional
		horarioAprazamentoMock.getTipoFreqAprazamento().setSeq(Short.valueOf("1"));//alteração da tipo frequencia
		horarioAprazamentoMock.getId().setFrequencia(Short.valueOf("9"));//alteração da frequencia
		horarioAprazamentoMock.setServidor(servidorMock);
		
		
		final MpmTipoFrequenciaAprazamento tipoFreqAprazamentoMock = new MpmTipoFrequenciaAprazamento();
		final AghUnidadesFuncionais unidadeFuncionalMock = new AghUnidadesFuncionais();
		
		//Cria mock para tipoFrequenciaAprazamento
		tipoFreqAprazamentoMock.setSeq(Short.valueOf("3"));
		tipoFreqAprazamentoMock.setIndSituacao(DominioSituacao.A);
		tipoFreqAprazamentoMock.setIndDigitaFrequencia(Boolean.TRUE);
		
		//cria mock de unidade funcional
		unidadeFuncionalMock.setSeq(Short.valueOf("128"));
		unidadeFuncionalMock.setIndSitUnidFunc(DominioSituacao.A);
		
		horarioAprazamentoMock.setUnidadeFuncional(unidadeFuncionalMock);
		horarioAprazamentoMock.setTipoFreqAprazamento(tipoFreqAprazamentoMock);


		Mockito.when(mpmHorarioInicAprazamentoDAO.atualizar(horarioAprazamentoMock)).thenReturn(horarioAprazamentoMock);

		Mockito.when(mpmHorarioInicAprazamentoDAO.obterPorChavePrimaria(horarioAprazamentoMock.getId())).thenReturn(horarioAprazamentoOriginalMock);
		
		Mockito.when(aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(horarioAprazamentoMock.getId().getUnfSeq())).thenReturn(unidadeFuncionalMock);

		Mockito.when(mpmTipoFrequenciaAprazamentoDAO.obterPorChavePrimaria(horarioAprazamentoMock.getId().getTfqSeq())).thenReturn(tipoFreqAprazamentoMock);

		try {
			systemUnderTest.persistirHorarioInicioAprazamento(horarioAprazamentoMock);
		} catch (BaseListException e) {
			fail("Erro ao executar gravarHorarioInicioAprazamentoAlteracaoTest: " + e.getMessage());
		} catch (BaseException e) {
			fail("Erro ao executar gravarHorarioInicioAprazamentoAlteracaoTest: " + e.getMessage());
		} 
	}
	
	/**
	 * Testa exclusão do horário aprazamento
	 * @throws ParseException 
	 * @author clayton.bras
	 */
	@Test
	public void excluirHorarioInicioAprazamentoTest() throws ParseException {
		final MpmHorarioInicAprazamento horarioAprazamentoMock = obterHorarioAprazamentoMock();
		
		final RapServidoresId servidorIdMock = new RapServidoresId();
		servidorIdMock.setMatricula(99998);
		servidorIdMock.setVinCodigo(Short.valueOf("5"));
		final RapServidores servidorMock = new RapServidores();
		servidorMock.setId(servidorIdMock);
		servidorMock.setPessoaFisica(new RapPessoasFisicas());
		servidorMock.getPessoaFisica().setCodigo(222);
		servidorMock.getPessoaFisica().setNome("NOME_QUALQUER");
		horarioAprazamentoMock.setServidor(servidorMock);
		
		Mockito.when(mpmHorarioInicAprazamentoDAO.obterPorChavePrimaria(horarioAprazamentoMock.getId())).thenReturn(horarioAprazamentoMock);
		
		systemUnderTest.removerHorarioAprazamento(horarioAprazamentoMock.getId());
	}
	
	/**
	 * Testa a pesquisa de horarios inícios de aprazamentos
	 * @author clayton.bras
	 */
	@Test
	public void obterListaHorariosInicioAprazamentosTest(){
		
		final Short seq = null;//seq da unidade funcional
		final DominioSituacao situacao = null;//situacao da unidade funcional
		final Integer firstResult = null;
		final Integer maxResult = null;
		final String orderProperty = null;
		final boolean asc = false;

		Mockito.when(mpmHorarioInicAprazamentoDAO.pesquisarHorariosInicioAprazamentos(firstResult, maxResult, orderProperty, asc, seq, situacao)).thenReturn(new ArrayList<MpmHorarioInicAprazamento>());

		Mockito.when(mpmHorarioInicAprazamentoDAO.pesquisarHorariosInicioAprazamentosCount(seq, situacao)).thenReturn(99L);
		
		systemUnderTest.pesquisarHorariosInicioAprazamentos(firstResult, maxResult, orderProperty, asc, seq, situacao);
		
		systemUnderTest.pesquisarHorariosInicioAprazamentosCount(seq, situacao);
	}
	
	
	/**
	 * 
	 * @param EXCEPTIONS_ESPERADAS
	 * @param e
	 */
	private void catchGravarHorarioInicioAprazamento(final int EXCEPTIONS_ESPERADAS,
			BaseListException e) {
		Iterator<BaseException> itExcept;
		BaseException except;
		int contExceptions = 0,
		contNaoEsperadas = 0;
		itExcept = e.iterator();
		while(itExcept.hasNext()){
			except = itExcept.next();
			if(		except.getCode() ==	ManterHorarioInicAprazamentoRNExceptionCode.ERRO_UNIDADE_FUNCIONAL_OBRIGATORIA ||
					except.getCode() ==	ManterHorarioInicAprazamentoRNExceptionCode.MPM_00766 ||
					except.getCode() ==	ManterHorarioInicAprazamentoRNExceptionCode.MPM_01274 ||
					except.getCode() ==	ManterHorarioInicAprazamentoRNExceptionCode.ERRO_TIPO_FREQUENCIA_APRAZAMENTO_OBRIGATORIO ||
					except.getCode() ==	ManterHorarioInicAprazamentoRNExceptionCode.MPM_00777 ||
					except.getCode() ==	ManterHorarioInicAprazamentoRNExceptionCode.MPM_00906 ||
					except.getCode() ==	ManterHorarioInicAprazamentoRNExceptionCode.ERRO_HORARIO_INICIO_OBRIGATORIO ||
					except.getCode() == ManterHorarioInicAprazamentoRNExceptionCode.ERRO_HORARIO_APRAZAMENTO_JA_EXISTENTE){
				++contExceptions;
			}
			else{
				++contNaoEsperadas;
			}
		}
		
		if(contExceptions != EXCEPTIONS_ESPERADAS)
		{
			fail("Erro ao executar teste: as " + 
					EXCEPTIONS_ESPERADAS + " exceções esperadas não " +
					"foram lançadas, total de lançadas: " + contExceptions + ".");	
		}			
		if(contNaoEsperadas > 0)
		{
			fail("Erro ao executar teste: " + contNaoEsperadas + " exceção(ões)" +
					" não esperada(s) foi(ram) lançada(s).");
		}
	}
	
}
