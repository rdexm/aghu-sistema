package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmObtOutraCausa;
import br.gov.mec.aghu.model.MpmObtOutraCausaId;
import br.gov.mec.aghu.prescricaomedica.business.ManterObtOutraCausaON.ManterObtOutraCausaONExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtOutraCausaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterObtOutraCausaONTest extends AGHUBaseUnitTest<ManterObtOutraCausaON>{
	
	private static final Log log = LogFactory.getLog(ManterObtOutraCausaONTest.class);
	
	// Mocks
	@Mock
	private MpmObtOutraCausaDAO mockedMpmObtOutraCausaDAO;
	@Mock
	private ManterObtOutraCausaRN mockedManterObtOutraCausaRN;
	@Mock
	private MpmAltaSumarioDAO mockedMpmAltaSumarioDAO;

	// Instâncias reutilizadas nos métodos de teste
	private SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO = new SumarioAltaDiagnosticosCidVO();
	private MpmObtOutraCausa obtOutraCausa = new MpmObtOutraCausa();
	private AghCid cids = new AghCid();
	private MpmAltaSumarioId id = new MpmAltaSumarioId();
	private MpmAltaSumario altaSumario = new MpmAltaSumario();
	private static Integer INTEGER_TESTE = 1;
	private static Short SHORT_TESTE = 1;
	
	
	@Before
	public void doBeforeEachTestCase() {
		
		// Inicializa instâncias reutilizadas nos métodos de teste..
		this.id.setApaAtdSeq(INTEGER_TESTE);
		this.id.setApaSeq(INTEGER_TESTE);
		this.id.setSeqp(SHORT_TESTE);
		this.obtOutraCausa.setIndCarga(DominioSimNao.S);
		this.sumarioAltaDiagnosticosCidVO.setId(this.id);
		this.sumarioAltaDiagnosticosCidVO.setObtOutraCausa(this.obtOutraCausa);
		this.sumarioAltaDiagnosticosCidVO.setCid(this.cids);
		this.altaSumario.setId(this.id);
		
		// Expectations
		try {
			Mockito.when(mockedMpmObtOutraCausaDAO.obterMpmObtOutraCausa(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort()))
			.thenReturn(new ArrayList<MpmObtOutraCausa>());
			
			Mockito.when(mockedMpmAltaSumarioDAO.obterAltaSumarioPeloId(Mockito.anyInt(), Mockito.anyInt(), 
					Mockito.anyShort())).thenReturn(this.altaSumario);

			Mockito.when(mockedMpmObtOutraCausaDAO.obterPorChavePrimaria(Mockito.any(MpmObtOutraCausaId.class)))
			.thenReturn(this.obtOutraCausa);

		} catch (BaseException e) {
			log.error(e.getMessage());
		}

	}
	@Test
	public void versionarObtOutraCausa(){
		try {
			systemUnderTest.versionarObtOutraCausa(this.altaSumario, SHORT_TESTE);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());	
		}
	}
	
	@Test
	public void versionarObtOutraCausaAltaSumarioNulo(){
		try {
			systemUnderTest.versionarObtOutraCausa(null, SHORT_TESTE);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());	
		}
	}
	
	@Test
	public void removerObtOutraCausa(){
		try {
			
			Mockito.when(mockedMpmObtOutraCausaDAO.obterMpmObtOutraCausa(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort(),
					Mockito.any(Boolean.class))).thenReturn(new ArrayList<MpmObtOutraCausa>());
			
			systemUnderTest.removerObtOutraCausa(this.altaSumario);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());	
		}
	}
	
	@Test
	public void removerObtOutraCausaAltaSumarioNulo(){
		try {
			MpmAltaSumario altaSumario = null;
			systemUnderTest.removerObtOutraCausa(altaSumario); // Obs. Chamada de método é Ambíguo
		} catch (BaseException e) {
			Assert.fail(e.getMessage());	
		}
		
	}
	
	@Test
	public void removerObtOutraCausaSumarioAltaDiagnosticosCidVO(){
		try {
			systemUnderTest.removerObtOutraCausa(this.sumarioAltaDiagnosticosCidVO); // Obs. Chamada de método é Ambíguo
		} catch (BaseException e) {
			Assert.fail(e.getMessage());	
		}
		
	}
	
	@Test
	public void persistirOutraCausa(){
		try {
			systemUnderTest.persistirOutraCausa(this.sumarioAltaDiagnosticosCidVO);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
		
	}
	
	@Test
	public void preAtualizarOutraCausa(){
		try {
			systemUnderTest.preAtualizarOutraCausa(this.sumarioAltaDiagnosticosCidVO);
		} catch (BaseException e) {
			Assert.assertEquals(ManterObtOutraCausaONExceptionCode.ERRO_ALTERAR_REGISTRO_OBITO_OUTRA_CAUSA,e.getCode());			}
	}
	
	@Test
	public void preInserirOutraCausa(){
		try {
			systemUnderTest.preInserirOutraCausa(this.sumarioAltaDiagnosticosCidVO);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void preInserirOutraCausaCiaSeq(){
		try {
			this.sumarioAltaDiagnosticosCidVO.setCiaSeq(INTEGER_TESTE);
			systemUnderTest.preInserirOutraCausa(this.sumarioAltaDiagnosticosCidVO);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void preInserirOutraCausaCiaSeqNulo(){
		try {
			this.sumarioAltaDiagnosticosCidVO.setCiaSeq(null);
			systemUnderTest.preInserirOutraCausa(this.sumarioAltaDiagnosticosCidVO);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
}
