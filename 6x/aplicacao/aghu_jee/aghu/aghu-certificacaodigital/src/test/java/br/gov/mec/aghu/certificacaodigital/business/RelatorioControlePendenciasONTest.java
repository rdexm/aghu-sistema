package br.gov.mec.aghu.certificacaodigital.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.certificacaodigital.business.RelatorioControlePendenciasON.RelatorioControlePendenciasONExceptionCode;
import br.gov.mec.aghu.certificacaodigital.dao.AghVersaoDocumentoDAO;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioControlePendencias;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class RelatorioControlePendenciasONTest extends AGHUBaseUnitTest<RelatorioControlePendenciasON>{
	
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private AghVersaoDocumentoDAO mockedAghVersaoDocumentoDAO;
	@Mock
	private ICadastrosBasicosInternacaoFacade mockedCadastrosBasicosInternacaoFacade;
	@Mock
	private ICascaFacade mockedCascaFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	
	@Before
	public void before() throws BaseException{
		whenObterServidorLogado();
	}
	
	@Test
	public void testVerificarParametroCertificacaoDigitalNaoHabilitadaError() throws BaseException {
		
		final AghParametros param = new AghParametros();
		param.setVlrTexto("N");
		
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);

		Mockito.when(mockedCadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal()).thenReturn("Nome da instituição");

		List<Object[]> retorno = new ArrayList<Object[]>();
		Mockito.when(mockedAghVersaoDocumentoDAO.pesquisaPendenciaAssinaturaDigital(Mockito.any(RapServidores.class), Mockito.any(FccCentroCustos.class), 
				Mockito.any(DominioOrdenacaoRelatorioControlePendencias.class))).thenReturn(retorno);
		
		try {
			systemUnderTest.pesquisaPendenciaAssinaturaDigital(new RapServidores(), new FccCentroCustos(), DominioOrdenacaoRelatorioControlePendencias.M);
			fail("testVerificarParametroCertificacaoDigitalNaoHabilitadaError: Exeção esperada não gerada.");
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), RelatorioControlePendenciasONExceptionCode.MENSAGEM_CERTIFICACAO_DIGITAL_NAO_HABILITADA);
		}
	}
	
	@Test
	public void testVerificarParametroCertificacaoDigitalNaoHabilitadaError02() throws BaseException {
		
		final AghParametros param = new AghParametros();
		param.setVlrTexto("S");
		
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);

		Mockito.when(mockedCadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal()).thenReturn("Nome da instituição");

		List<Object[]> retorno = new ArrayList<Object[]>();
		Mockito.when(mockedAghVersaoDocumentoDAO.pesquisaPendenciaAssinaturaDigital(Mockito.any(RapServidores.class), Mockito.any(FccCentroCustos.class), 
				Mockito.any(DominioOrdenacaoRelatorioControlePendencias.class))).thenReturn(retorno);

		try {
			systemUnderTest.pesquisaPendenciaAssinaturaDigital(new RapServidores(), new FccCentroCustos(), null);
		} catch (ApplicationBusinessException e) {
			assertEquals(e.getCode(), RelatorioControlePendenciasONExceptionCode.MENSAGEM_USUARIO_SEM_PERMISSAO_PARA_PESQUISA);
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
