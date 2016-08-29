package br.gov.mec.aghu.exames.business;

import static org.junit.Assert.fail;

import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasJnDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAmostrasId;
import br.gov.mec.aghu.model.AelAmostrasJn;
import br.gov.mec.aghu.model.AelConfigMapa;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

@PrepareForTest( BaseJournalFactory.class )
@RunWith(PowerMockRunner.class)
public class AelAmostrasRNTest extends AGHUBaseUnitTest<AelAmostrasRN>{
	
	private static final Log log = LogFactory.getLog(AelAmostrasRNTest.class);

	@Mock
	private AelAmostrasDAO mockedAelAmostrasDAO;
	@Mock
	private ICadastrosApoioExamesFacade mockedCadastrosApoioExamesFacade;
	@Mock
	private AelAmostrasJnDAO mockedAelAmostrasJnDAO;
	@Mock
	private AelSolicitacaoExameDAO mockedAelSolicitacaoExameDAO;
	@Mock
	private AelExtratoAmostrasON mockedAelExtratoAmostrasON;
	@Mock
	private IInternacaoFacade mockedInternacaoFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IExamesFacade mockedExamesFacade;
	@Mock
	private BaseJournalFactory mockedBaseJournalFactory;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	
	@Before
	public void doBeforeEachTestCase() throws BaseException{
		whenObterServidorLogado();
		
		PowerMockito.mockStatic(BaseJournalFactory.class);
		PowerMockito.when(BaseJournalFactory.getBaseJournal(Mockito.any(DominioOperacoesJournal.class), Mockito.any(Class.class), Mockito.anyString()))
		.thenReturn(new AelAmostrasJn());

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void afterUpdateAelAmostrasTest001() {
		
		final AelAmostras amostras = new AelAmostras();
		AelSolicitacaoExames aelSolicitacaoExames = new AelSolicitacaoExames();
		aelSolicitacaoExames.setSeq(0);
		amostras.setSituacao(DominioSituacaoAmostra.R);
		amostras.setNroUnico(1);
		AelAmostrasId id = new AelAmostrasId();
		id.setSoeSeq(Integer.valueOf("1"));
		id.setSeqp(Short.valueOf("1"));
		amostras.setId(id);
		
		
		
		try {
			final AelAmostras amostrasOld = (AelAmostras) BeanUtils.cloneBean(amostras);
			amostrasOld.setSituacao(DominioSituacaoAmostra.C);
			amostrasOld.setNroUnico(2);
		
			Mockito.when(mockedAelAmostrasDAO.obterPorChavePrimaria(Mockito.any(AelAmostrasId.class))).thenReturn(amostrasOld);

			Mockito.when(mockedAelSolicitacaoExameDAO.obterPeloId(Mockito.anyInt())).thenReturn(new AelSolicitacaoExames());

			Mockito.when(mockedExamesFacade.buscarLaudoCodigoPaciente(Mockito.any(AelSolicitacaoExames.class))).thenReturn(Integer.valueOf(1));

			Mockito.when(mockedExamesFacade.buscarLaudoCodigoPaciente(Mockito.any(AelSolicitacaoExames.class))).thenReturn(Integer.valueOf(2));
    	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
		
    	try {
    		final AelAmostras amostrasOld2 = new AelAmostras();
    		amostrasOld2.setId(new AelAmostrasId());
    		amostrasOld2.getId().setSoeSeq(1);
    		amostrasOld2.setSituacao(DominioSituacaoAmostra.R);
    		amostrasOld2.setNroUnico(2);
    		    		
			systemUnderTest.afterUpdateAelAmostras(amostras,amostrasOld2);
			
		} catch (BaseException atual) {
			fail();
		} 
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void afterUpdateAelAmostrasTest002() {
		
		AelAmostras amostras = new AelAmostras();
		amostras.setSituacao(DominioSituacaoAmostra.R);
		amostras.setNroUnico(1);
		AelAmostrasId id = new AelAmostrasId();
		id.setSoeSeq(Integer.valueOf("1"));
		id.setSeqp(Short.valueOf("1"));
		amostras.setId(id);
		amostras.setConfigMapa(new AelConfigMapa());
			
		try {
			final AelAmostras amostrasOld = (AelAmostras) BeanUtils.cloneBean(amostras);
			amostrasOld.setSituacao(DominioSituacaoAmostra.E);
			amostrasOld.setNroUnico(2);
		
			Mockito.when(mockedAelAmostrasDAO.obterPorChavePrimaria(Mockito.any(AelAmostrasId.class))).thenReturn(amostrasOld);

			Mockito.when(mockedAelAmostrasDAO.buscarAmostrasPorId(Mockito.anyInt(), Mockito.anyShort())).thenReturn(amostrasOld);
    	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
		
		try{
    		final AelAmostras amostrasOld2 = new AelAmostras();
    		amostrasOld2.setId(amostras.getId());
    		amostrasOld2.setConfigMapa(amostras.getConfigMapa());
    		
			systemUnderTest.afterUpdateAelAmostras(amostras,amostrasOld2);
			Assert.assertTrue(true);
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertFalse(true);			
		}
		
	}
	
	@Test
	/**
	 * Testa passando situação como NULO.
	 * Não deve setar a data.
	 */
	public void atualizarDataHoraEntradaSituacaoNulaTest() {
		
		AelAmostras amostra = new AelAmostras();
		systemUnderTest.atualizarDataHoraEntrada(amostra);
		Assert.assertNull(amostra.getDthrEntrada());
		
	}
	
	@Test
	/**
	 * Testa passando situação diferente de R.
	 * Não deve setar a data.
	 */
	public void atualizarDataHoraEntradaSituacaoDiferenteDeRTest() {
		
		AelAmostras amostra = new AelAmostras();
		amostra.setSituacao(DominioSituacaoAmostra.A);
		systemUnderTest.atualizarDataHoraEntrada(amostra);
		Assert.assertNull(amostra.getDthrEntrada());
		
	}
	
	@Test
	/**
	 * Testa passando situação igual R.
	 * Deve setar a data.
	 */
	public void atualizarDataHoraEntradaSituacaoIgualaRTest() {
		
		AelAmostras amostra = new AelAmostras();
		amostra.setSituacao(DominioSituacaoAmostra.R);
		systemUnderTest.atualizarDataHoraEntrada(amostra);
		Assert.assertEquals(DateUtil.truncaData(new Date()), DateUtil.truncaData(amostra.getDthrEntrada()));
		
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
