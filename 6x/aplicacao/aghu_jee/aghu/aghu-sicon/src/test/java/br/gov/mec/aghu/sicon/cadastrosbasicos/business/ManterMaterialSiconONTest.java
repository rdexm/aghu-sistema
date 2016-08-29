package br.gov.mec.aghu.sicon.cadastrosbasicos.business;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ManterMaterialSiconON.ManterMaterialSiconONExceptionCode;
import br.gov.mec.aghu.sicon.dao.ScoMaterialSiconDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterMaterialSiconONTest extends AGHUBaseUnitTest<ManterMaterialSiconON>{

	@Mock
	private ScoMaterialSiconDAO mockedMaterialSiconDao;
	@Mock
	private IComprasFacade mockedComprasFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

    @Before
    public void inicar() throws BaseException {
    	whenObterServidorLogado();
    }

	@Test
	public void alterarComException() throws BaseException {

		try {
			this.systemUnderTest.alterar(null);
			Assert.fail("Deveria ter gerado uma exception");
		} catch (BaseException e) {
			Assert.assertEquals(
					ManterMaterialSiconONExceptionCode.MENSAGEM_PARAM_OBRIG,
					e.getCode());
		}
	}
	
	@Test
	public void verificaCodigoUnicoComErro() {
		
		
		if (this.systemUnderTest.verificaCodigoUnico(1)) {
			Assert.assertFalse("Código duplicado", false);
		}
	}
	
	@Test
	public void validaMaterialComErro() {
		
		ScoMaterial mat = new ScoMaterial();		
		
		
//		mockingContext.checking(new Expectations() {
//			{
//				allowing(mockedMaterialDao).obterPorChavePrimaria(with(any(Integer.class)));
//			}
//		});

		
		if (this.systemUnderTest.validaMaterial(mat)) {
			Assert.assertFalse("Material já existente", false);
		}
	}
	
	@Test
	public void pesquisarMateriaisPorFiltroComString() throws BaseException {
		
		String param = "nn";
		
		try {
			systemUnderTest.pesquisarMateriaisPorFiltro(param);
			fail("Deveria ter ocorrido uma exceção!!!");

		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					ManterMaterialSiconONExceptionCode.MENSAGEM_NRO_MINIMO_CARACTERES,
					e.getCode());

		}
	}
	
	@Test
	public void pesquisarMateriaisPorFiltroComStringCompleta() throws BaseException {
		
		String param = "nnnnnnnnn";
		
		
		try {
			systemUnderTest.pesquisarMateriaisPorFiltro(param);

		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					ManterMaterialSiconONExceptionCode.MENSAGEM_NRO_MINIMO_CARACTERES,
					e.getCode());

		}
	}
	
	@Test
	public void pesquisarMateriaisPorFiltroComCodigo() throws BaseException {
		
		String param = "123";
		
		
		try {
			systemUnderTest.pesquisarMateriaisPorFiltro(param);

		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					ManterMaterialSiconONExceptionCode.MENSAGEM_NRO_MINIMO_CARACTERES,
					e.getCode());

		}
	}
	
	@Test
	public void inserirComException() throws BaseException {

		try {
			this.systemUnderTest.inserir(null);
			Assert.fail("Deveria ter gerado uma exception");
		} catch (BaseException e) {
			Assert.assertEquals(
					ManterMaterialSiconONExceptionCode.MENSAGEM_PARAM_OBRIG,
					e.getCode());
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
