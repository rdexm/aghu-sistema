package br.gov.mec.aghu.faturamento.business;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioModoLancamentoFat;
import br.gov.mec.aghu.faturamento.dao.FatCaractItemProcHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarJnDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoCaractItensDAO;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 *
 *
 */
public class ItensProcedHospitalarRNTest extends AGHUBaseUnitTest<ItensProcedHospitalarRN>{
	
	@Mock
	private IAghuFacade mockedAghuFacade; 
	@Mock
	private FatItensProcedHospitalarJnDAO mockedFatItensProcedHospitalarJnDAO;
	@Mock
	private FatCaractItemProcHospDAO mockedFatCaractItemProcHospDAO;
	@Mock
	private TipoCaracteristicaItemRN mockedTipoCaracteristicaItemRN;
	@Mock
	private FatItensProcedHospitalarDAO mockedFatItensProcedHospitalarDAO;
	@Mock
	private FatTipoCaractItensDAO mockedFatTipoCaractItensDAO;


	@Before
	public void doBeforeEachTestCase() {
	}
	

	//##############################################################//
	//## TESTES PARA o método executarEnforceItemProdecHospitalar ##//
	//##############################################################//
	
	/**
	 * Testa a execucao com dados nulos
	 *  
	 */
	@Test
	public void executarEnforceItemProdecHospitalarDadosNulosTest() throws ApplicationBusinessException{
		systemUnderTest.executarEnforceItemProdecHospitalar(null, null, null);
	}

	/**
	 * Testa a execucao com dados válidos para update.
	 *  
	 */
	@Test
	public void executarEnforceItemProdecHospitalarDadosValidosUpdate1Test() throws ApplicationBusinessException{
		FatCaractItemProcHosp ciph = new FatCaractItemProcHosp(); 
		Mockito.when(mockedFatCaractItemProcHospDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(ciph);

		Integer vTctSeq = 123;
		Mockito.when(mockedTipoCaracteristicaItemRN.obterTipoCaractItemSeq(Mockito.any(DominioFatTipoCaractItem.class))).thenReturn(vTctSeq);

		FatTipoCaractItens caract = new FatTipoCaractItens(); 
		Mockito.when(mockedFatTipoCaractItensDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(caract);

		
		FatItensProcedHospitalar newIph = new FatItensProcedHospitalar();
		FatItensProcedHospitalarId newIphId = new FatItensProcedHospitalarId();
		newIphId.setPhoSeq((short)123);
		newIphId.setSeq(123);
		newIph.setId(newIphId);
		
		newIph.setMaxQtdApac((short)0);
		newIph.setModoLancamentoFat(DominioModoLancamentoFat.O);
		newIph.setCobraExcedenteBpa(Boolean.FALSE);
		newIph.setCobrancaApac(Boolean.FALSE);
		newIph.setProcPrincipalApac(Boolean.FALSE);
		newIph.setCobrancaAmbulatorio(Boolean.FALSE);
		
		FatItensProcedHospitalar oldIph = new FatItensProcedHospitalar();
		FatItensProcedHospitalarId oldIphId = new FatItensProcedHospitalarId();
		oldIphId.setPhoSeq((short)12);
		oldIphId.setSeq(12);
		oldIph.setId(oldIphId);
		oldIph.setMaxQtdApac((short)12);
		
		systemUnderTest.executarEnforceItemProdecHospitalar(newIph, oldIph, DominioOperacoesJournal.UPD);
	}
	

	/**
	 * Testa a execucao com dados válidos para update.
	 *  
	 */
	@Test
	public void executarEnforceItemProdecHospitalarDadosValidosUpdate2Test() throws ApplicationBusinessException{
		FatCaractItemProcHosp ciph = new FatCaractItemProcHosp(); 
		Mockito.when(mockedFatCaractItemProcHospDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(ciph);

		Integer vTctSeq = 123;
		Mockito.when(mockedTipoCaracteristicaItemRN.obterTipoCaractItemSeq(Mockito.any(DominioFatTipoCaractItem.class))).thenReturn(vTctSeq);

		FatTipoCaractItens caract = new FatTipoCaractItens(); 
		Mockito.when(mockedFatTipoCaractItensDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(caract);

		FatItensProcedHospitalar newIph = new FatItensProcedHospitalar();
		FatItensProcedHospitalarId newIphId = new FatItensProcedHospitalarId();
		newIphId.setPhoSeq((short)123);
		newIphId.setSeq(123);
		newIph.setId(newIphId);
		
		newIph.setMaxQtdApac((short)1123);
		newIph.setModoLancamentoFat(null);
		newIph.setCobraExcedenteBpa(Boolean.TRUE);
		newIph.setCobrancaApac(Boolean.TRUE);
		newIph.setProcPrincipalApac(Boolean.TRUE);
		newIph.setCobrancaAmbulatorio(Boolean.TRUE);
		
		FatItensProcedHospitalar oldIph = new FatItensProcedHospitalar();
		FatItensProcedHospitalarId oldIphId = new FatItensProcedHospitalarId();
		oldIphId.setPhoSeq((short)12);
		oldIphId.setSeq(12);
		oldIph.setId(oldIphId);
		
		oldIph.setMaxQtdApac((short)0);
		oldIph.setModoLancamentoFat(DominioModoLancamentoFat.O);
		
		systemUnderTest.executarEnforceItemProdecHospitalar(newIph, oldIph, DominioOperacoesJournal.UPD);
	}
	

	/**
	 * Testa a execucao com dados válidos para update.
	 *  
	 */
	@Test
	public void executarEnforceItemProdecHospitalarDadosValidosUpdate3Test() throws ApplicationBusinessException{
		FatCaractItemProcHosp ciph = new FatCaractItemProcHosp(); 
		Mockito.when(mockedFatCaractItemProcHospDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(ciph);

		Integer vTctSeq = 123;
		Mockito.when(mockedTipoCaracteristicaItemRN.obterTipoCaractItemSeq(Mockito.any(DominioFatTipoCaractItem.class))).thenReturn(vTctSeq);

		FatTipoCaractItens caract = new FatTipoCaractItens(); 
		Mockito.when(mockedFatTipoCaractItensDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(caract);
		
		FatItensProcedHospitalar newIph = new FatItensProcedHospitalar();
		FatItensProcedHospitalarId newIphId = new FatItensProcedHospitalarId();
		newIphId.setPhoSeq((short)123);
		newIphId.setSeq(123);
		newIph.setId(newIphId);
		
		newIph.setMaxQtdApac((short)1123);
		newIph.setModoLancamentoFat(null);
		newIph.setCobraExcedenteBpa(Boolean.TRUE);
		newIph.setCobrancaApac(Boolean.TRUE);
		newIph.setProcPrincipalApac(Boolean.TRUE);
		newIph.setCobrancaAmbulatorio(Boolean.TRUE);
		
		FatItensProcedHospitalar oldIph = new FatItensProcedHospitalar();
		FatItensProcedHospitalarId oldIphId = new FatItensProcedHospitalarId();
		oldIphId.setPhoSeq((short)12);
		oldIphId.setSeq(12);
		oldIph.setId(oldIphId);
		
		oldIph.setMaxQtdApac((short)123);
		oldIph.setModoLancamentoFat(DominioModoLancamentoFat.A);
		
		systemUnderTest.executarEnforceItemProdecHospitalar(newIph, oldIph, DominioOperacoesJournal.UPD);
	}
	
	
	/**
	 * Testa a execucao com dados válidos para insert.
	 *  
	 */
	@Test
	public void executarEnforceItemProdecHospitalarDadosValidosInsertTest() throws ApplicationBusinessException{
		FatCaractItemProcHosp ciph = new FatCaractItemProcHosp(); 
		Mockito.when(mockedFatCaractItemProcHospDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(ciph);

		Integer vTctSeq = 123;
		Mockito.when(mockedTipoCaracteristicaItemRN.obterTipoCaractItemSeq(Mockito.any(DominioFatTipoCaractItem.class))).thenReturn(vTctSeq);

		FatTipoCaractItens caract = new FatTipoCaractItens(); 
		Mockito.when(mockedFatTipoCaractItensDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(caract);

		FatItensProcedHospitalar newIph = new FatItensProcedHospitalar();
		FatItensProcedHospitalarId newIphId = new FatItensProcedHospitalarId();
		newIphId.setPhoSeq((short)123);
		newIphId.setSeq(123);
		newIph.setId(newIphId);
		
		newIph.setMaxQtdApac((short)123);
		newIph.setModoLancamentoFat(DominioModoLancamentoFat.A);
		newIph.setCobraExcedenteBpa(Boolean.TRUE);
		newIph.setCobrancaApac(Boolean.TRUE);
		newIph.setProcPrincipalApac(Boolean.TRUE);
		newIph.setCobrancaAmbulatorio(Boolean.TRUE);
		
		
		systemUnderTest.executarEnforceItemProdecHospitalar(newIph, null, DominioOperacoesJournal.INS);
	}
	

	
}
