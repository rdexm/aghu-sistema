package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.dominio.DominioCoresPacientesTriagem;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.VAfaPrcrDispMdtos;
import br.gov.mec.aghu.model.VAfaPrcrDispMdtosId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;

@Ignore
public class PesquisarPacientesParaDispensacaoRNTest extends AGHUBaseUnitTest<PesquisarPacientesParaDispensacaoRN> {
	
	//Daos e Facades a serem mockadas
	@Mock
	private AfaDispensacaoMdtosDAO mockedAfaDispensacaoMdtosDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	
//	@Before
//	public void doBeforeEachTestCase() {
//
//		// contexto dos mocks, usado para criar os mocks e definir a expectativa
//		// de chamada dos métodos.
//		mockingContext = new Mockery() {
//			{
//				setImposteriser(ClassImposteriser.INSTANCE);
//			}
//		};
//		
//		// mocking cada uma das DAOs utilizadas
//		mockedAfaDispensacaoMdtosDAO = mockingContext.mock(AfaDispensacaoMdtosDAO.class);
//		mockedAghuFacade = mockingContext.mock(IAghuFacade.class);
//		mockedPrescricaoMedicaFacade = mockingContext.mock(IPrescricaoMedicaFacade.class);
//
//		// criação do objeto da classe a ser testada, com os devidos métodos
//		// sobrescritos.
//		systemUnderTest = new PesquisarPacientesParaDispensacaoRN() {
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = -3395402130506979061L;
//
//			@Override
//			protected AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO() {
//				return mockedAfaDispensacaoMdtosDAO;
//			};
//			
//			@Override
//			protected IAghuFacade getAghuFacade() {
//				return mockedAghuFacade;
//			};
//			
//			@Override
//			protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
//				return mockedPrescricaoMedicaFacade;
//			}
//		};
//	}
	
	@Test
	public void afacOrdenaDispVermelhoComProntuario(){
		esperaObterDispensacaoMdtos(TiposRetornoDispensacao.ANY);
		esperaObterAtendimento(false);
		VAfaPrcrDispMdtos view = new VAfaPrcrDispMdtos();
		VAfaPrcrDispMdtosId id = new VAfaPrcrDispMdtosId();
		id.setAtdSeq(1);
		id.setSeq(2);
		view.setId(id);
		
		DominioCoresPacientesTriagem resultadoEsperado = DominioCoresPacientesTriagem.VERMELHO;
		DominioCoresPacientesTriagem resultadoObtido = systemUnderTest.afacOrdenaDisp(view);
		
		Assert.assertEquals(getMsgResultado(resultadoEsperado, resultadoObtido), resultadoEsperado, resultadoObtido);
		
	}
	
	@Test
	public void afacOrdenaDispVermelhoComAltaSumario(){
		esperaObterDispensacaoMdtos(TiposRetornoDispensacao.ANY);
		esperaObterAtendimento(true);
		esperaObterAltaSumario(false);
		VAfaPrcrDispMdtos view = new VAfaPrcrDispMdtos();
		VAfaPrcrDispMdtosId id = new VAfaPrcrDispMdtosId();
		id.setAtdSeq(1);
		id.setSeq(2);
		view.setId(id);
		DominioCoresPacientesTriagem resultadoEsperado = DominioCoresPacientesTriagem.VERMELHO;
		DominioCoresPacientesTriagem resultadoObtido = systemUnderTest.afacOrdenaDisp(view);
		
		Assert.assertEquals(getMsgResultado(resultadoEsperado, resultadoObtido), resultadoEsperado, resultadoObtido);
	}
	
	@Test
	public void afacOrdenaDispAzul(){
		esperaObterDispensacaoMdtos(TiposRetornoDispensacao.ANY);
		esperaObterAtendimento(true);
		esperaObterAltaSumario(true);
		VAfaPrcrDispMdtos view = new VAfaPrcrDispMdtos();
		VAfaPrcrDispMdtosId id = new VAfaPrcrDispMdtosId();
		id.setAtdSeq(1);
		id.setSeq(2);
		view.setId(id);
		DominioCoresPacientesTriagem resultadoEsperado = DominioCoresPacientesTriagem.AZUL;
		DominioCoresPacientesTriagem resultadoObtido = systemUnderTest.afacOrdenaDisp(view);
		
		Assert.assertEquals(getMsgResultado(resultadoEsperado, resultadoObtido), resultadoEsperado, resultadoObtido);
	}
	
	@Test
	public void afacOrdenaDispAmarelo(){
		esperaObterDispensacaoMdtos(TiposRetornoDispensacao.AMARELO);
		esperaObterAtendimento(true);
		esperaObterAltaSumario(true);
		VAfaPrcrDispMdtos view = new VAfaPrcrDispMdtos();
		VAfaPrcrDispMdtosId id = new VAfaPrcrDispMdtosId();
		id.setAtdSeq(1);
		id.setSeq(2);
		view.setId(id);
		DominioCoresPacientesTriagem resultadoEsperado = DominioCoresPacientesTriagem.AMARELO;
		DominioCoresPacientesTriagem resultadoObtido = systemUnderTest.afacOrdenaDisp(view);
		
		Assert.assertEquals(getMsgResultado(resultadoEsperado, resultadoObtido), resultadoEsperado, resultadoObtido);
	}
	
	@Test
	public void afacOrdenaDispRoxo(){
		esperaObterDispensacaoMdtos(TiposRetornoDispensacao.ROXO);
		esperaObterAtendimento(true);
		esperaObterAltaSumario(true);
		VAfaPrcrDispMdtos view = new VAfaPrcrDispMdtos();
		VAfaPrcrDispMdtosId id = new VAfaPrcrDispMdtosId();
		id.setAtdSeq(1);
		id.setSeq(2);
		view.setId(id);
		DominioCoresPacientesTriagem resultadoEsperado = DominioCoresPacientesTriagem.ROXO;
		DominioCoresPacientesTriagem resultadoObtido = systemUnderTest.afacOrdenaDisp(view);
		
		Assert.assertEquals(getMsgResultado(resultadoEsperado, resultadoObtido), resultadoEsperado, resultadoObtido);
	}
	
	@Test
	public void afacOrdenaDispMarrom(){
		esperaObterDispensacaoMdtos(TiposRetornoDispensacao.MARROM);
		esperaObterAtendimento(true);
		esperaObterAltaSumario(true);
		VAfaPrcrDispMdtos view = new VAfaPrcrDispMdtos();
		VAfaPrcrDispMdtosId id = new VAfaPrcrDispMdtosId();
		id.setAtdSeq(1);
		id.setSeq(2);
		view.setId(id);
		DominioCoresPacientesTriagem resultadoEsperado = DominioCoresPacientesTriagem.MARROM;
		DominioCoresPacientesTriagem resultadoObtido = systemUnderTest.afacOrdenaDisp(view);
		
		Assert.assertEquals(getMsgResultado(resultadoEsperado, resultadoObtido), resultadoEsperado, resultadoObtido);
	}
	
	@Test
	public void afacOrdenaDispBranco(){
		esperaObterDispensacaoMdtos(TiposRetornoDispensacao.BRANCO);
		esperaObterAtendimento(true);
		esperaObterAltaSumario(true);
		VAfaPrcrDispMdtos view = new VAfaPrcrDispMdtos();
		VAfaPrcrDispMdtosId id = new VAfaPrcrDispMdtosId();
		id.setAtdSeq(1);
		id.setSeq(2);
		view.setId(id);
		DominioCoresPacientesTriagem resultadoEsperado = DominioCoresPacientesTriagem.BRANCO;
		DominioCoresPacientesTriagem resultadoObtido = systemUnderTest.afacOrdenaDisp(view);
		
		Assert.assertEquals(getMsgResultado(resultadoEsperado, resultadoObtido), resultadoEsperado, resultadoObtido);
	}
	
	@Test
	public void afacOrdenaDispCinza(){
		esperaObterDispensacaoMdtos(TiposRetornoDispensacao.CINZA);
		esperaObterAtendimento(true);
		esperaObterAltaSumario(true);
		DominioCoresPacientesTriagem resultadoEsperado = DominioCoresPacientesTriagem.CINZA;
		VAfaPrcrDispMdtos view = new VAfaPrcrDispMdtos();
		VAfaPrcrDispMdtosId id = new VAfaPrcrDispMdtosId();
		id.setAtdSeq(1);
		id.setSeq(2);
		view.setId(id);
		DominioCoresPacientesTriagem resultadoObtido = systemUnderTest.afacOrdenaDisp(view);
		
		Assert.assertEquals(getMsgResultado(resultadoEsperado, resultadoObtido), resultadoEsperado, resultadoObtido);
	}
	
	@Test
	public void notNullEMaiorQueZeroNulo(){
		Boolean resultadoEsperado = Boolean.FALSE;
		Boolean resultadoObtido = systemUnderTest.notNullEMaiorQueZero(null);
		
		Assert.assertEquals(resultadoEsperado, resultadoObtido);
	}
	
	@Test
	public void notNullEMaiorQueZeroNegativo(){
		Boolean resultadoEsperado = Boolean.FALSE;
		Boolean resultadoObtido = systemUnderTest.notNullEMaiorQueZero(new BigDecimal(-1));
		
		Assert.assertEquals(resultadoEsperado, resultadoObtido);
	}
	
	@Test
	public void notNullEMaiorQueZeroPositivo(){
		Boolean resultadoEsperado = Boolean.TRUE;
		Boolean resultadoObtido = systemUnderTest.notNullEMaiorQueZero(BigDecimal.ONE);
		
		Assert.assertEquals(resultadoEsperado, resultadoObtido);
	}
	
	@Test
	public void notNullEMaiorQueZeroZero(){
		Boolean resultadoEsperado = Boolean.FALSE;
		Boolean resultadoObtido = systemUnderTest.notNullEMaiorQueZero(BigDecimal.ZERO);
		
		Assert.assertEquals(resultadoEsperado, resultadoObtido);
	}
	
	@Test
	public void afacRetornaTrpSeq(){
		AghAtendimentos att = new AghAtendimentos();
		att.setTrpSeq(123);
		Integer resultadoObtido = systemUnderTest.afacRetornaTrpSeq(att);
		Assert.assertNotNull(resultadoObtido);
	}
	
	@Test
	public void afacRetornaTrpSeqNull(){
		AghAtendimentos att = new AghAtendimentos();
		att.setTrpSeq(null);
		Integer resultadoObtido = systemUnderTest.afacRetornaTrpSeq(att);
		Assert.assertNull(resultadoObtido);
	}
	
	@Test
	public void afacRetornaTrpSeqAtendimentoNull(){
		Integer resultadoObtido = systemUnderTest.afacRetornaTrpSeq(null);
		Assert.assertNull(resultadoObtido);
	}
	
	private String getMsgResultado(
			DominioCoresPacientesTriagem resultadoEsperado,
			DominioCoresPacientesTriagem resultadoObtido) {
		return "O resultado esperado era ==> " + resultadoEsperado + " mas o resultado obtido foi ==> " + resultadoObtido;
	}
	
	private void esperaObterDispensacaoMdtos(final TiposRetornoDispensacao tipoRetornoDispensacao) {
//		mockingContext.checking(new Expectations(){{
//			allowing(mockedPrescricaoMedicaFacade).obterMpmPrescricaoMedicaPorChavePrimaria((with(any(MpmPrescricaoMedicaId.class))));
//			will(returnValue(new MpmPrescricaoMedica()));
//			allowing(mockedAfaDispensacaoMdtosDAO).pesquisarDispensacaoMdtosComIMOPorPrescricao(with(any(MpmPrescricaoMedica.class)));
//			will(returnValue(getDispensacaoMdtoByTipoRetorno(tipoRetornoDispensacao)));
//		}});
	}
	
	protected List<AfaDispensacaoMdtos> getDispensacaoMdtoByTipoRetorno(
			TiposRetornoDispensacao tipoRetornoDispensacao) {
		
		List<AfaDispensacaoMdtos> retorno = new ArrayList<AfaDispensacaoMdtos>();
		switch (tipoRetornoDispensacao) {
		case ANY:
			break;
		case AMARELO:
			AfaDispensacaoMdtos adm1 = new AfaDispensacaoMdtos();
			adm1.setIndSituacao(DominioSituacaoDispensacaoMdto.S);
			retorno.add(adm1);
			AfaDispensacaoMdtos adm2 = new AfaDispensacaoMdtos();
			adm2.setIndSituacao(DominioSituacaoDispensacaoMdto.D);
			retorno.add(adm2);
			break;
		case ROXO:
			AfaDispensacaoMdtos adm3 = new AfaDispensacaoMdtos();
			adm3.setIndSituacao(DominioSituacaoDispensacaoMdto.T);
			adm3.setQtdeDispensada(new BigDecimal(123));
			adm3.setTipoOcorrenciaDispensacao(null);
			retorno.add(adm3);
			break;
		case MARROM:
			AfaDispensacaoMdtos adm4 = new AfaDispensacaoMdtos();
			adm4.setIndSituacao(DominioSituacaoDispensacaoMdto.T);
			adm4.setQtdeEstornada(new BigDecimal(123));
			adm4.setTipoOcorrenciaDispensacao(new AfaTipoOcorDispensacao());
			retorno.add(adm4);
			break;
		case BRANCO:
			AfaDispensacaoMdtos adm5 = new AfaDispensacaoMdtos();
			adm5.setIndSituacao(DominioSituacaoDispensacaoMdto.T);
			adm5.setQtdeDispensada(new BigDecimal(123));
			adm5.setTipoOcorrenciaDispensacao(null);
			retorno.add(adm5);
			AfaDispensacaoMdtos adm6 = new AfaDispensacaoMdtos();
			adm6.setIndSituacao(DominioSituacaoDispensacaoMdto.D);
			retorno.add(adm6);
			break;
		case CINZA:
			AfaDispensacaoMdtos adm7 = new AfaDispensacaoMdtos();
			adm7.setIndSituacao(DominioSituacaoDispensacaoMdto.S);
			retorno.add(adm7);
			break;
		default:
			break;
		}
		return retorno;
	}

	private void esperaObterAtendimento(final Boolean atendimentoNulo) {
//		mockingContext.checking(new Expectations(){{
//			allowing(mockedAghuFacade).obterAtendimento(with(any(Integer.class)), with(any(DominioPacAtendimento.class)), with(any(List.class)));
//			if(atendimentoNulo) {
//				will(returnValue(null));
//			} else {
//				will(returnValue(getAghAtendimentoComProntuario()));
//			}
//		}});
	}
	
	protected AghAtendimentos getAghAtendimentoComProntuario() {
		AghAtendimentos att = new AghAtendimentos();
		att.setProntuario(123);
		return att;
	}
	
	private void esperaObterAltaSumario(final Boolean altaSumarioNulo) {
//		mockingContext.checking(new Expectations(){{
//			allowing(mockedPrescricaoMedicaFacade).obterAltaSumarioConcluidaPeloAtendimento(with(any(Integer.class)));
//			if(altaSumarioNulo) {
//				will(returnValue(null));
//			} else {
//				will(returnValue(new MpmAltaSumario()));
//			}
//		}});
	}
	
	public static enum TiposRetornoDispensacao {
		ANY, AMARELO, ROXO, MARROM, BRANCO, CINZA;
	}
}
