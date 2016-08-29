package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmLaudo;
import br.gov.mec.aghu.model.MpmLaudoJn;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoNptId;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimentoId;
import br.gov.mec.aghu.model.MpmTipoLaudo;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmLaudoJnDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@PrepareForTest( BaseJournalFactory.class)
@RunWith(PowerMockRunner.class)
public class LaudoJournalRNTest extends AGHUBaseUnitTest<LaudoJournalRN>{
	
	@Mock
	private MpmLaudoJnDAO mockedMpmLaudoJnDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	/**
	 * Testa a execucao com dois laudos nulos 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false, pois os laudos são 'iguais'",
				retorno, false);
	}

	/**
	 * Testa a execucao de dois laudos com o mesmo servidor feito manual 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoServidorFeitoManualTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		RapServidores servidor = new RapServidores();
		RapServidoresId id = new RapServidoresId(Integer.valueOf("1"), Short.valueOf("4"));
		servidor.setId(id);
		
		laudoNew.setServidorFeitoManual(servidor);
		laudoOld.setServidorFeitoManual(servidor);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false, pois os laudos são 'iguais'",
				retorno, false);
	}

	/**
	 * Testa a execucao de dois laudos com diferentes 'servidores feito manual'  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoServidorFeitoManualDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		RapServidores servidor = new RapServidores();
		RapServidoresId id = new RapServidoresId(Integer.valueOf("1"), Short.valueOf("4"));
		servidor.setId(id);

		RapServidores servidor2 = new RapServidores();
		RapServidoresId id2 = new RapServidoresId(Integer.valueOf("1"), Short.valueOf("5"));
		servidor2.setId(id2);
		
		laudoNew.setServidorFeitoManual(servidor);
		laudoOld.setServidorFeitoManual(servidor2);
		laudoNew.setSeq(1);
		laudoOld.setSeq(1);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true, pois os laudos são possuem 'servidores feito manual' diferentes",
				retorno, true);
	}

	/**
	 * Testa a execucao de dois laudos com o mesmo seq  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoSeqTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setSeq(Integer.valueOf("1"));
		laudoOld.setSeq(Integer.valueOf("1"));
		
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false, pois os laudos possuem seq iguais.",
				retorno, false);
	}

	/**
	 * Testa a execucao de dois laudos com o seq diferente  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoSeqDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setSeq(Integer.valueOf("1"));
		laudoOld.setSeq(Integer.valueOf("2"));
		
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true, pois os laudos possuem seq diferente.",
				retorno, true);
	}
	
	/**
	 * Testa a execucao de dois laudos com o DthrInicioValidade igual  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoDthrInicioValidadeTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		Date date = new Date();
		
		laudoNew.setDthrInicioValidade(date);
		laudoOld.setDthrInicioValidade(date);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false",
				retorno, false);
	}

	/**
	 * Testa a execucao de dois laudos com o DthrInicioValidade diferentes  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoDthrInicioValidadeDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, 1);
		
		laudoNew.setDthrInicioValidade(new Date());
		laudoOld.setDthrInicioValidade(c.getTime());
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true",
				retorno, true);
	}

	/**
	 * Testa a execucao de dois laudos com o DthrFimValidade igual  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoDthrFimValidadeTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		Date data = new Date();
		laudoNew.setDthrFimValidade(data);
		laudoOld.setDthrFimValidade(data);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false",
				retorno, false);
	}
	
	/**
	 * Testa a execucao de dois laudos com o DthrFimValidade diferentes  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoDthrFimValidadeDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, 1);
		
		laudoNew.setDthrFimValidade(new Date());
		laudoOld.setDthrFimValidade(c.getTime());
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true",
				retorno, true);
	}
	
	
	/**
	 * Testa a execucao de dois laudos com o DthrFimPrevisao igual  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoDthrFimPrevisaoTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		Date fimValidade = new Date();
		
		laudoNew.setDthrFimValidade(fimValidade);
		laudoOld.setDthrFimValidade(fimValidade);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false",
				retorno, false);
	}
	
	/**
	 * Testa a execucao de dois laudos com o DthrFimPrevisao diferentes  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoDthrFimPrevisaoDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, 1);
		
		laudoNew.setDthrFimValidade(new Date());
		laudoOld.setDthrFimValidade(c.getTime());
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true",
				retorno, true);
	}

	
	/**
	 * Testa a execucao de dois laudos com o CriadoEm igual  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoCriadoEmTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		Date d = new Date();
		laudoNew.setCriadoEm(d);
		laudoOld.setCriadoEm(d);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false",
				retorno, false);
	}
	
	/**
	 * Testa a execucao de dois laudos com o CriadoEm diferente  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoCriadoEmDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, 1);
		
		laudoNew.setCriadoEm(new Date());
		laudoOld.setCriadoEm(c.getTime());
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true",
				retorno, true);
	}
	
	
	
	/**
	 * Testa a execucao de dois laudos com o Justificativa igual  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoJustificativaTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setJustificativa("aaa");
		laudoOld.setJustificativa("aaa");
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false",
				retorno, false);
	}
	
	/**
	 * Testa a execucao de dois laudos com o Justificativa diferente  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoJustificativaDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setJustificativa("aaa");
		laudoOld.setJustificativa("ccc");
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true",
				retorno, true);
	}
	
	
	
	
	
	/**
	 * Testa a execucao de dois laudos com o ContaDesdobrada igual  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoContaDesdobradaTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setContaDesdobrada(true);
		laudoOld.setContaDesdobrada(true);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false",
				retorno, false);
	}
	
	/**
	 * Testa a execucao de dois laudos com o ContaDesdobrada diferente  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoContaDesdobradaDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setContaDesdobrada(true);
		laudoOld.setContaDesdobrada(false);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true",
				retorno, true);
	}
	
	
	
	
	
	/**
	 * Testa a execucao de dois laudos com o Impresso igual  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoImpressoTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setImpresso(true);
		laudoOld.setImpresso(true);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false",
				retorno, false);
	}
	
	/**
	 * Testa a execucao de dois laudos com o Impresso diferente  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoImpressoDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setImpresso(true);
		laudoOld.setImpresso(false);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true",
				retorno, true);
	}
	
	
	
	
	/**
	 * Testa a execucao de dois laudos com o DuracaoTratSolicitado igual  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoDuracaoTratSolicitadoTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setDuracaoTratSolicitado(Short.valueOf("4"));
		laudoOld.setDuracaoTratSolicitado(Short.valueOf("4"));
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false",
				retorno, false);
	}
	
	/**
	 * Testa a execucao de dois laudos com o DuracaoTratSolicitado diferente  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoDuracaoTratSolicitadoDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setDuracaoTratSolicitado(Short.valueOf("4"));
		laudoOld.setDuracaoTratSolicitado(Short.valueOf("5"));
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true",
				retorno, true);
	}
	
	
	
	/**
	 * Testa a execucao de dois laudos com o LaudoManual igual  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoLaudoManualTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setLaudoManual(true);
		laudoOld.setLaudoManual(true);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false",
				retorno, false);
	}
	
	/**
	 * Testa a execucao de dois laudos com o LaudoManual diferente  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoLaudoManualDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setLaudoManual(true);
		laudoOld.setLaudoManual(false);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true",
				retorno, true);
	}
	

	/**
	 * Testa a execucao de dois laudos com o ProcedimentoHospitalarInterno igual  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoProcedimentoHospitalarInternoTest() throws ApplicationBusinessException {
		boolean retorno = false;

		FatProcedHospInternos phi = new FatProcedHospInternos();
		phi.setSeq(Integer.valueOf("1"));
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setProcedimentoHospitalarInterno(phi);
		laudoOld.setProcedimentoHospitalarInterno(phi);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false",
				retorno, false);
	}
	
	/**
	 * Testa a execucao de dois laudos com o ProcedimentoHospitalarInterno diferente  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoProcedimentoHospitalarInternoDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		FatProcedHospInternos phi = new FatProcedHospInternos();
		phi.setSeq(Integer.valueOf("1"));

		FatProcedHospInternos phi2 = new FatProcedHospInternos();
		phi2.setSeq(Integer.valueOf("2"));
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setProcedimentoHospitalarInterno(phi);
		laudoOld.setProcedimentoHospitalarInterno(phi2);
		
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true",
				retorno, true);
	}
	
	

	/**
	 * Testa a execucao de dois laudos com o Atendimento igual  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoAtendimentoTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		AghAtendimentos a = new AghAtendimentos();
		a.setSeq(Integer.valueOf("1"));
		
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setAtendimento(a);
		laudoOld.setAtendimento(a);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false",
				retorno, false);
	}
	
	/**
	 * Testa a execucao de dois laudos com o Atendimento diferente  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoAtendimentoDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		AghAtendimentos a = new AghAtendimentos();
		a.setSeq(Integer.valueOf("1"));
		
		AghAtendimentos a2 = new AghAtendimentos();
		a2.setSeq(Integer.valueOf("2"));
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setAtendimento(a);
		laudoOld.setAtendimento(a2);
		
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true",
				retorno, true);
	}
	
	/**
	 * Testa a execucao de dois laudos com o mesmo servidor 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoServidorTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		RapServidores servidor = new RapServidores();
		RapServidoresId id = new RapServidoresId(Integer.valueOf("1"), Short.valueOf("4"));
		servidor.setId(id);
		
		laudoNew.setServidor(servidor);
		laudoOld.setServidor(servidor);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false",
				retorno, false);
	}

	/**
	 * Testa a execucao de dois laudos com diferentes servidor  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoServidorDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		RapServidores servidor = new RapServidores();
		RapServidoresId id = new RapServidoresId(Integer.valueOf("1"), Short.valueOf("4"));
		servidor.setId(id);

		RapServidores servidor2 = new RapServidores();
		RapServidoresId id2 = new RapServidoresId(Integer.valueOf("1"), Short.valueOf("5"));
		servidor2.setId(id2);
		
		laudoNew.setServidor(servidor);
		laudoOld.setServidor(servidor2);
		laudoNew.setServidorFeitoManual(servidor);
		laudoOld.setServidorFeitoManual(servidor2);

		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true",
				retorno, true);
	}

	
	/**
	 * Testa a execucao de dois laudos com o mesmo Laudo 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoLaudoTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		MpmLaudo laudoAux = new MpmLaudo();
		laudoAux.setSeq(Integer.valueOf("1"));
		
		laudoNew.setLaudo(laudoAux);
		laudoOld.setLaudo(laudoAux);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false",
				retorno, false);
	}
	
	/**
	 * Testa a execucao de dois laudos com diferentes Laudos  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoLaudoDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		MpmLaudo laudoAux = new MpmLaudo();
		laudoAux.setSeq(Integer.valueOf("1"));
		MpmLaudo laudoAux2 = new MpmLaudo();
		laudoAux.setSeq(Integer.valueOf("2"));
		
		laudoNew.setLaudo(laudoAux);
		laudoOld.setLaudo(laudoAux2);		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true",
				retorno, true);
	}

	/**
	 * Testa a execucao de dois laudos com o mesmo TipoLaudo 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoTipoLaudoTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		MpmTipoLaudo tipoLaudoAux = new MpmTipoLaudo();
		tipoLaudoAux.setSeq(Short.valueOf("1"));
		
		laudoNew.setTipoLaudo(tipoLaudoAux);
		laudoOld.setTipoLaudo(tipoLaudoAux);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false",
				retorno, false);
	}
	
	/**
	 * Testa a execucao de dois laudos com diferentes TipoLaudo  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoTipoLaudoDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		MpmTipoLaudo tipoLaudo = new MpmTipoLaudo();
		tipoLaudo.setSeq(Short.valueOf("1"));
		MpmTipoLaudo tipoLaudo2 = new MpmTipoLaudo();
		tipoLaudo.setSeq(Short.valueOf("2"));
		
		laudoNew.setTipoLaudo(tipoLaudo);
		laudoOld.setTipoLaudo(tipoLaudo2);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true",
				retorno, true);
	}
	
	
	
	/**
	 * Testa a execucao de dois laudos com o mesmo PrescricaoProcedimento 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoPrescricaoProcedimentoTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		MpmPrescricaoProcedimento pp = new MpmPrescricaoProcedimento();
		MpmPrescricaoProcedimentoId ppId = new MpmPrescricaoProcedimentoId();
		ppId.setAtdSeq(Integer.valueOf("1"));
		ppId.setSeq(Long.valueOf("1"));
		pp.setId(ppId);

		
		laudoNew.setPrescricaoProcedimento(pp);
		laudoOld.setPrescricaoProcedimento(pp);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false",
				retorno, false);
	}
	
	/**
	 * Testa a execucao de dois laudos com diferentes PrescricaoProcedimento  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoPrescricaoProcedimentoDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		MpmPrescricaoProcedimento pp = new MpmPrescricaoProcedimento();
		MpmPrescricaoProcedimentoId ppId = new MpmPrescricaoProcedimentoId();
		ppId.setAtdSeq(Integer.valueOf("1"));
		ppId.setSeq(Long.valueOf("1"));
		pp.setId(ppId);

		MpmPrescricaoProcedimento pp2 = new MpmPrescricaoProcedimento();
		MpmPrescricaoProcedimentoId ppId2 = new MpmPrescricaoProcedimentoId();
		ppId.setAtdSeq(Integer.valueOf("2"));
		ppId.setSeq(Long.valueOf("2"));
		pp.setId(ppId2);
		
		laudoNew.setPrescricaoProcedimento(pp);
		laudoOld.setPrescricaoProcedimento(pp2);
				
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true",
				retorno, true);
	}
	
	
	/**
	 * Testa a execucao de dois laudos com o mesmo PrescricaoNpts 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoPrescricaoNptsTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		MpmPrescricaoNpt pn = new MpmPrescricaoNpt();
		MpmPrescricaoNptId pnId = new MpmPrescricaoNptId();
		pnId.setAtdSeq(Integer.valueOf("1"));
		pnId.setSeq(Integer.valueOf("1"));
		pn.setId(pnId);

		
		laudoNew.setPrescricaoNpts(pn);
		laudoOld.setPrescricaoNpts(pn);

		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false",
				retorno, false);
	}
	
	/**
	 * Testa a execucao de dois laudos com diferentes PrescricaoNpts  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoPrescricaoNptsDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		MpmPrescricaoNpt pn = new MpmPrescricaoNpt();
		MpmPrescricaoNptId pnId = new MpmPrescricaoNptId();
		pnId.setAtdSeq(Integer.valueOf("1"));
		pnId.setSeq(Integer.valueOf("1"));
		pn.setId(pnId);

		MpmPrescricaoNpt pn2 = new MpmPrescricaoNpt();
		MpmPrescricaoNptId pnId2 = new MpmPrescricaoNptId();
		pnId2.setAtdSeq(Integer.valueOf("2"));
		pnId2.setSeq(Integer.valueOf("2"));
		pn.setId(pnId2);
		
		laudoNew.setPrescricaoNpts(pn);
		laudoOld.setPrescricaoNpts(pn2);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true",
				retorno, true);
	}
	
	
	/**
	 * Testa a execucao de dois laudos com o mesmo ItemPrescricaoMdtos 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoItemPrescricaoMdtosTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		MpmItemPrescricaoMdto ipm = new MpmItemPrescricaoMdto();
		MpmItemPrescricaoMdtoId ipmId = new MpmItemPrescricaoMdtoId();
		ipmId.setMedMatCodigo(Integer.valueOf("1"));
		ipmId.setPmdAtdSeq(Integer.valueOf("1"));
		ipmId.setPmdSeq(Long.valueOf("1"));
		ipmId.setSeqp(Short.valueOf("1"));
		ipm.setId(ipmId);
		
		
		laudoNew.setItemPrescricaoMdtos(ipm);
		laudoOld.setItemPrescricaoMdtos(ipm);
		
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser false",
				retorno, false);
	}
	
	/**
	 * Testa a execucao de dois laudos com diferentes ItemPrescricaoMdtos  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarInsercaoLaudoItemPrescricaoMdtosDiferentesTest() throws ApplicationBusinessException {
		boolean retorno = false;
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		MpmItemPrescricaoMdto ipm = new MpmItemPrescricaoMdto();
		MpmItemPrescricaoMdtoId ipmId = new MpmItemPrescricaoMdtoId();
		ipmId.setMedMatCodigo(Integer.valueOf("1"));
		ipmId.setPmdAtdSeq(Integer.valueOf("1"));
		ipmId.setPmdSeq(Long.valueOf("1"));
		ipmId.setSeqp(Short.valueOf("1"));
		ipm.setId(ipmId);
		MpmItemPrescricaoMdto ipm2 = new MpmItemPrescricaoMdto();
		MpmItemPrescricaoMdtoId ipmId2 = new MpmItemPrescricaoMdtoId();
		ipmId2.setMedMatCodigo(Integer.valueOf("2"));
		ipmId2.setPmdAtdSeq(Integer.valueOf("2"));
		ipmId2.setPmdSeq(Long.valueOf("2"));
		ipmId2.setSeqp(Short.valueOf("2"));
		ipm2.setId(ipmId2);
		
		laudoNew.setItemPrescricaoMdtos(ipm);
		laudoOld.setItemPrescricaoMdtos(ipm2);
		
		retorno = systemUnderTest.verificarInsercaoLaudoJournal(laudoNew, laudoOld);
		
		Assert.assertEquals("o retorno deve ser true",
				retorno, true);
	}
	
	
	/**
	 * Testa a execucao de dois laudos identicos  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void realizarLaudoJournalTest() throws ApplicationBusinessException {
	
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setSeq(Integer.valueOf("1"));
		laudoOld.setSeq(Integer.valueOf("1"));
				
		systemUnderTest.realizarLaudoJournal(laudoNew, laudoOld, DominioOperacoesJournal.UPD);
	}

	/**
	 * Testa a execucao de dois laudos identicos  
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void realizarLaudoJournalLaudosDiferentesTest() throws ApplicationBusinessException {
		
		MpmLaudo laudoNew = new MpmLaudo(); 
		MpmLaudo laudoOld = new MpmLaudo();
		
		laudoNew.setSeq(Integer.valueOf("1"));
		laudoOld.setSeq(Integer.valueOf("2"));
		
		PowerMockito.mockStatic(BaseJournalFactory.class);
		PowerMockito.when(BaseJournalFactory.getBaseJournal(Mockito.any(DominioOperacoesJournal.class), Mockito.any(Class.class), Mockito.anyString()))
		.thenReturn(new MpmLaudoJn());

		try {
			whenObterServidorLogado();
		} catch (BaseException e) {
			fail();
		}
		
		systemUnderTest.realizarLaudoJournal(laudoNew, laudoOld, DominioOperacoesJournal.UPD);
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
