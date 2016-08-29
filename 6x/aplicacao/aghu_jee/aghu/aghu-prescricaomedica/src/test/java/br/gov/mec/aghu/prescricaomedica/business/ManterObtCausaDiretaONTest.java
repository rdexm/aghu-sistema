package br.gov.mec.aghu.prescricaomedica.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmObtCausaDireta;
import br.gov.mec.aghu.model.MpmObtCausaDiretaId;
import br.gov.mec.aghu.prescricaomedica.business.ManterObtCausaDiretaON.ManterObtCausaDiretaONExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtCausaDiretaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterObtCausaDiretaONTest extends AGHUBaseUnitTest<ManterObtCausaDiretaON>{
	
	private static final Log log = LogFactory.getLog(ManterObtCausaDiretaONTest.class);

	// Mocks
	@Mock
	private MpmObtCausaDiretaDAO mockedMpmObtCausaDiretaDAO;
	@Mock
	private ManterObtCausaDiretaRN mockedManterObtCausaDiretaRN;
	@Mock
	private MpmAltaSumarioDAO mockedMpmAltaSumarioDAO;
	
	
	// Instâncias reutilizadas nos métodos de teste
	private SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO = new SumarioAltaDiagnosticosCidVO();
	private MpmAltaSumarioId id = new MpmAltaSumarioId();
	private MpmObtCausaDireta obtCausaDireta = new MpmObtCausaDireta();
	private MpmAltaSumario altaSumario = new MpmAltaSumario();
	private final static String STRING_TESTE = "STRING_TESTE";
	private final static String STRING_TESTE_EM_BRANCO = "";
	private static Integer INTEGER_TESTE = 1;
	private static Short SHORT_TESTE = 1;

	@Before
	public void doBeforeEachTestCase() {
		// Inicializa instâncias reutilizadas nos métodos de teste..
		this.id.setApaAtdSeq(INTEGER_TESTE);
		this.id.setApaSeq(INTEGER_TESTE);
		this.id.setSeqp(SHORT_TESTE);
		this.sumarioAltaDiagnosticosCidVO.setId(id);
		this.obtCausaDireta.setId(new MpmObtCausaDiretaId());
		this.sumarioAltaDiagnosticosCidVO.setObitoCausaDireta(this.obtCausaDireta);
		this.altaSumario.setId(id);

		try {
			Mockito.when(mockedMpmObtCausaDiretaDAO.obterObtCausaDireta(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(this.obtCausaDireta);
			Mockito.when(mockedManterObtCausaDiretaRN.obterObtCausaDireta(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(this.obtCausaDireta);
		} catch (BaseException e) {
			log.error(e.getMessage());
		}

	}
	
	@Test
	public void versionarObtCausaDireta(){
		try {
			systemUnderTest.versionarObtCausaDireta(this.altaSumario, SHORT_TESTE);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());	
		}
	}
	
	@Test
	public void versionarObtCausaDiretaAltaSumarioNulo(){
		try {
			systemUnderTest.versionarObtCausaDireta(null, SHORT_TESTE);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());	
		}
	}


	@Test
	public void obterObtCausaDireta(){
		try {
			MpmObtCausaDireta retorno = systemUnderTest.obterObtCausaDireta(INTEGER_TESTE,INTEGER_TESTE,SHORT_TESTE);
			Assert.assertNotNull(retorno);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());	
		}
	}
	
	@Test
	public void obterObtCausaDiretaNulo(){
		try {
			MpmObtCausaDireta retorno = systemUnderTest.obterObtCausaDireta(null,null,null);
			Assert.assertNotNull(retorno);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());	
		}
	}
	
	@Test
	public void preinserirObtCausaDireta(){
		try {
			String retorno = systemUnderTest.preinserirObtCausaDireta(this.sumarioAltaDiagnosticosCidVO);
			Assert.assertNotNull(retorno);
		} catch (BaseException e) {
			Assert.assertEquals(ManterObtCausaDiretaONExceptionCode.MENSAGEM_EXCEDEU_LIMITE_CAUSA_DIRETA_MORTE, 
					e.getCode());		
		}
	}
	
	@Test
	public void preinserirObtCausaDiretaCiaSeq(){
		try {
			this.sumarioAltaDiagnosticosCidVO.setCiaSeq(INTEGER_TESTE);
			String retorno = systemUnderTest.preinserirObtCausaDireta(this.sumarioAltaDiagnosticosCidVO);
			Assert.assertNotNull(retorno);
		} catch (BaseException e) {
			Assert.assertEquals(ManterObtCausaDiretaONExceptionCode.MENSAGEM_EXCEDEU_LIMITE_CAUSA_DIRETA_MORTE, 
					e.getCode());		
		}
	}
	
	@Test
	public void preinserirObtCausaDiretaCiaSeqNulo(){
		try {
			this.sumarioAltaDiagnosticosCidVO.setCiaSeq(null);
			String retorno = systemUnderTest.preinserirObtCausaDireta(this.sumarioAltaDiagnosticosCidVO);
			Assert.assertNotNull(retorno);
		} catch (BaseException e) {
			Assert.assertEquals(ManterObtCausaDiretaONExceptionCode.MENSAGEM_EXCEDEU_LIMITE_CAUSA_DIRETA_MORTE, 
					e.getCode());		
		}
	}

	@Test
	public void inserirObtCausaDireta() {

		try {
			systemUnderTest.inserirObtCausaDireta(this.obtCausaDireta);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void inserirObtCausaDiretaNulo() {
		try {
			systemUnderTest.inserirObtCausaDireta(null);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void preatualizarObtCausaDiretaIndCargaSim() {
		this.sumarioAltaDiagnosticosCidVO.setObitoCausaDireta(this.obtCausaDireta);
		this.obtCausaDireta.setIndCarga(DominioSimNao.S);
		try {
			systemUnderTest.preatualizarObtCausaDireta(this.sumarioAltaDiagnosticosCidVO);
		} catch (BaseException e) {
			Assert.assertEquals(ManterObtCausaDiretaONExceptionCode.ERRO_ALTERAR_REGISTRO_OBITO_CAUSA_DIRETA,e.getCode());
		}
	}

	@Test
	public void preatualizarObtCausaDiretaIndCargaNao() {
		this.sumarioAltaDiagnosticosCidVO.setObitoCausaDireta(this.obtCausaDireta);
		this.obtCausaDireta.setIndCarga(DominioSimNao.N);
		try {
			systemUnderTest.preatualizarObtCausaDireta(this.sumarioAltaDiagnosticosCidVO);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void preatualizarObtCausaDiretaComplemento() {
		this.obtCausaDireta.setIndCarga(DominioSimNao.N);
		this.sumarioAltaDiagnosticosCidVO.setComplemento(STRING_TESTE);
		this.sumarioAltaDiagnosticosCidVO.setComplementoEditado(STRING_TESTE);
		try {
			systemUnderTest.preatualizarObtCausaDireta(this.sumarioAltaDiagnosticosCidVO);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void preatualizarObtCausaDiretaComplementoNulo() {
		this.obtCausaDireta.setIndCarga(DominioSimNao.N);
		this.sumarioAltaDiagnosticosCidVO.setComplemento(null);
		this.sumarioAltaDiagnosticosCidVO.setComplementoEditado(null);
		try {
			systemUnderTest.preatualizarObtCausaDireta(this.sumarioAltaDiagnosticosCidVO);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void preatualizarObtCausaDiretaComplementoEmBranco() {
		this.obtCausaDireta.setIndCarga(DominioSimNao.N);
		this.sumarioAltaDiagnosticosCidVO.setComplemento(STRING_TESTE_EM_BRANCO);
		this.sumarioAltaDiagnosticosCidVO.setComplementoEditado(STRING_TESTE_EM_BRANCO);
		try {
			systemUnderTest.preatualizarObtCausaDireta(this.sumarioAltaDiagnosticosCidVO);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void removerObtCausaDireta() {
		try {
			systemUnderTest.removerObtCausaDireta(this.sumarioAltaDiagnosticosCidVO);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void removerObtCausaDiretaMpmObtCausaDiretaNulo() {
		try {
			systemUnderTest.removerObtCausaDireta(this.sumarioAltaDiagnosticosCidVO);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void removerObtCausaDiretaAltaSumario() {
		try {
			systemUnderTest.removerObtCausaDireta(this.altaSumario);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void removerObtCausaDiretaAltaSumarioNulo() {
		this.altaSumario = null;
		try {
			systemUnderTest.removerObtCausaDireta(this.altaSumario); // Obs. Chamada de método é Ambíguo
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}

}
