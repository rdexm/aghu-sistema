package br.gov.mec.aghu.estoque.business;

import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.estoque.business.DevolucaoAlmoxarifadoRN.DevolucaoAlmoxarifadoRNExceptionCode;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceDevolucaoAlmoxarifado;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.SceTipoMovimentoId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * @author diego.pacheco
 *
 */
public class DevolucaoAlmoxarifadoRNTest extends AGHUBaseUnitTest<DevolucaoAlmoxarifadoRN>{

	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private SceTipoMovimentosRN mockedSceTipoMovimentosRN;
	@Mock
	private ManterTransferenciaMaterialRN mockedManterTransferenciaMaterialRN;
	@Mock
	private SceMovimentoMaterialRN mockedSceMovimentoMaterialRN;
	@Mock
	private ICentroCustoFacade mockedCentroCustoFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

    @Before
    public void iniciar() {
    	try {
			whenObterServidorLogado();
		} catch (BaseException e) {
			fail();
		}
    }
    
	@Test
	public void testVerificarEstorno() throws ApplicationBusinessException {
		Boolean estorno = true;	
		try{
			systemUnderTest.verificarEstorno(estorno);	
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode() == DevolucaoAlmoxarifadoRNExceptionCode.SCE_00508);
		}
	}
	
	@Test
	public void testVerificarAltaEstorno() throws ApplicationBusinessException {
		Boolean estorno = false;	
		try{
			systemUnderTest.verificarAltEstorno(estorno);	
		}
		catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode() == DevolucaoAlmoxarifadoRNExceptionCode.SCE_00297);
		}
	}
	
	/*@Test(expected=ApplicationBusinessException.class)
	public void testVerificarExclusaoConsulta() throws ApplicationBusinessException {
		mockingContext.checking(new Expectations() {{
				AacConsultas consulta = new AacConsultas();
				AacSituacaoConsultas situacaoConsulta = new AacSituacaoConsultas();
				situacaoConsulta.setSituacao("B");
				consulta.setIndSituacaoConsulta(situacaoConsulta);
				systemUnderTest.verificarExclusaoConsulta(consulta);
		}});
	}
	
	@Test
	public void testVerificarExclusaoConsulta_LancaExcecao_AAC_00102() throws ApplicationBusinessException {
		mockingContext.checking(new Expectations() {{
			AacConsultas consulta = new AacConsultas();
			AacSituacaoConsultas situacaoConsulta = new AacSituacaoConsultas();
			situacaoConsulta.setSituacao("M");
			consulta.setIndSituacaoConsulta(situacaoConsulta);
			consulta.setExcedeProgramacao(Boolean.FALSE);
			try {
				systemUnderTest.verificarExclusaoConsulta(consulta);
				Assert.fail("Deveria ocorrer ApplicationBusinessException");
			} catch (ApplicationBusinessException e) {
				Assert.assertTrue(e.getCode() == AmbulatorioConsultasRNExceptionCode.AAC_00102);
			}
		}});
	}
	
	@Test
	public void testVerificarExclusaoConsulta_Nao_LancaExcecao_AAC_00102() throws ApplicationBusinessException {
		mockingContext.checking(new Expectations() {{
			AacConsultas consulta = new AacConsultas();
			AacSituacaoConsultas situacaoConsulta = new AacSituacaoConsultas();
			situacaoConsulta.setSituacao("M");
			consulta.setIndSituacaoConsulta(situacaoConsulta);
			consulta.setExcedeProgramacao(Boolean.TRUE);
			try {
				systemUnderTest.verificarExclusaoConsulta(consulta);
				Assert.assertTrue(Boolean.TRUE.equals(consulta.getExcedeProgramacao()));
			} catch (ApplicationBusinessException e) {
				Assert.fail("Nao deveria ocorrer ApplicationBusinessException");
			}
		}});
	}*/

	
	
	@Test
	public void testVerificarTipoMovimentoAtivo() {
		final SceTipoMovimentoId tipoMovimentoId = new SceTipoMovimentoId();
		tipoMovimentoId.setSeq(Short.valueOf("2"));
		tipoMovimentoId.setComplemento(Byte.valueOf("3"));
		final SceTipoMovimento tipoMovimento = new SceTipoMovimento();
		tipoMovimento.setId(tipoMovimentoId);
		
		try {
			
			systemUnderTest.verificarTipoMovimentoAtivo(tipoMovimento);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	
	@Test
	public void testVerificarAlmoxarifadoAtivoSuccess() {
		final SceAlmoxarifado almoxarifado = new SceAlmoxarifado();
		almoxarifado.setSeq(Short.valueOf("3"));
		final SceDevolucaoAlmoxarifado devolucaoAlmoxarifado = new SceDevolucaoAlmoxarifado();
		devolucaoAlmoxarifado.setAlmoxarifado(almoxarifado);

		systemUnderTest.verificarAlmoxarifadoAtivo(devolucaoAlmoxarifado);
	}
	
	@Test
	public void testVerificarCentroCustoSuccess() {
		final FccCentroCustos centroCusto = new FccCentroCustos();
		final Integer codigo = 7;
		centroCusto.setCodigo(codigo);
		
		try {
			Mockito.when(mockedCentroCustoFacade.obterFccCentroCustosAtivos(codigo)).thenReturn(centroCusto);
			
			systemUnderTest.verificarCentroCusto(codigo);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testVerificarCentroCustoError() {
		final Integer codigo = 7;
		
		try {
			Mockito.when(mockedCentroCustoFacade.obterFccCentroCustosAtivos(codigo)).thenReturn(null);
			
			systemUnderTest.verificarCentroCusto(codigo);
			
		}	catch (ApplicationBusinessException e) {
				Assert.assertEquals(e.getCode(), 
					DevolucaoAlmoxarifadoRN.DevolucaoAlmoxarifadoRNExceptionCode.CENTRO_CUSTO_NAO_CADASTRADO_OU_INATIVO);
		}
	}
	
	@Test
	public void testAtualizarServidorDataGeracao() throws ApplicationBusinessException {
		final SceDevolucaoAlmoxarifado devolucaoAlmoxarifado = new SceDevolucaoAlmoxarifado();
		
		devolucaoAlmoxarifado.setServidor(new RapServidores());
		devolucaoAlmoxarifado.setDtGeracao(DateUtil.truncaData(new Date()));
		systemUnderTest.atualizarServidorDataGeracao(devolucaoAlmoxarifado);
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

