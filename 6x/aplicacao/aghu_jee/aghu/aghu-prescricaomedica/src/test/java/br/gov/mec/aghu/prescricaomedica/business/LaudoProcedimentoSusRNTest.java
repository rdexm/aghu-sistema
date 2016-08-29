package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class LaudoProcedimentoSusRNTest extends AGHUBaseUnitTest<LaudoProcedimentoSusRN>{
	
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	
	@Before
	public void inciar() {
		try {
			whenObterServidorLogado();
		} catch (BaseException e) {
			fail();
		}
	}
	
	/**
	 * AghParametro nulo
	 * @throws NumberFormatException
	 * @throws AGHUNegocioException
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test
	public void validarBuscaConselhoProfissionalServidorVOAghParametroNulo() throws NumberFormatException, ApplicationBusinessException{
		
		AghParametros param = new AghParametros();
		param.setVlrTexto("1");
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);

		Mockito.when(mockedRegistroColaboradorFacade.buscaConselhosProfissionalServidor(Mockito.anyInt(), Mockito.anyShort())).thenReturn(null);

		Mockito.when(mockedRegistroColaboradorFacade.buscaConselhosProfissionalServidor(Mockito.anyInt(), Mockito.anyShort(), Mockito.any(Boolean.class))).thenReturn(null);

		systemUnderTest.buscaConselhoProfissionalServidorVO(Integer.valueOf("123"), Short.valueOf("123"));
	}

	/**
	 * 
	 * @throws NumberFormatException
	 * @throws AGHUNegocioException
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test
	public void validarBuscaConselhoProfissionalServidorVO() throws NumberFormatException, ApplicationBusinessException {
		
		AghParametros param = new AghParametros();
		param.setVlrTexto("1");
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);
		
		Mockito.when(mockedRegistroColaboradorFacade.buscaConselhosProfissionalServidor(Mockito.anyInt(), Mockito.anyShort())).thenReturn(null);

		Mockito.when(mockedRegistroColaboradorFacade.buscaConselhosProfissionalServidor(Mockito.anyInt(), Mockito.anyShort(), Mockito.any(Boolean.class))).thenReturn(null);
		
		systemUnderTest.buscaConselhoProfissionalServidorVO(Integer.valueOf("123"), Short.valueOf("123"));
	}

	/**
	 * 
	 * @throws NumberFormatException
	 * @throws AGHUNegocioException
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test
	public void validarBuscaConselhoProfissionalServidorVOComConselhoProfissional() throws NumberFormatException, ApplicationBusinessException {
		
		AghParametros param = new AghParametros();
		param.setVlrTexto("1");
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);

		List<ConselhoProfissionalServidorVO> listaConselhos = new ArrayList<ConselhoProfissionalServidorVO>();
		ConselhoProfissionalServidorVO conselho = new ConselhoProfissionalServidorVO();
		conselho.setCodigoConselho(Short.valueOf("1"));
		conselho.setNome("aa");
		conselho.setCpf(Long.valueOf("00044433388"));
		conselho.setSiglaConselho("ABC");
		conselho.setNumeroRegistroConselho("123");
		conselho.setSexo(DominioSexo.M);
		conselho.setTituloMasculino("DR");
		listaConselhos.add(conselho);
		Mockito.when(mockedRegistroColaboradorFacade.buscaConselhosProfissionalServidor(Mockito.anyInt(), Mockito.anyShort())).thenReturn(listaConselhos);

		Mockito.when(mockedRegistroColaboradorFacade.buscaConselhosProfissionalServidor(Mockito.anyInt(), Mockito.anyShort(), Mockito.any(Boolean.class))).thenReturn(null);
		
		
		systemUnderTest.buscaConselhoProfissionalServidorVO(Integer.valueOf("123"), Short.valueOf("123"));
	}
	
	/**
	 * 
	 * @throws NumberFormatException
	 * @throws AGHUNegocioException
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test
	public void validarBuscaConselhoProfissionalServidorVOComConselhoProfissionalSexoF() throws NumberFormatException, ApplicationBusinessException {
		
		AghParametros param = new AghParametros();
		param.setVlrTexto("1");
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);

		List<ConselhoProfissionalServidorVO> listaConselhos = new ArrayList<ConselhoProfissionalServidorVO>();
		ConselhoProfissionalServidorVO conselho = new ConselhoProfissionalServidorVO();
		conselho.setCodigoConselho(Short.valueOf("1"));
		conselho.setNome("aa");
		conselho.setCpf(Long.valueOf("00044433388"));
		conselho.setSiglaConselho("ABC");
		conselho.setNumeroRegistroConselho("123");
		conselho.setSexo(DominioSexo.F);
		conselho.setTituloFeminino("DR");
		listaConselhos.add(conselho);
		Mockito.when(mockedRegistroColaboradorFacade.buscaConselhosProfissionalServidor(Mockito.anyInt(), Mockito.anyShort())).thenReturn(listaConselhos);

		Mockito.when(mockedRegistroColaboradorFacade.buscaConselhosProfissionalServidor(Mockito.anyInt(), Mockito.anyShort(), Mockito.any(Boolean.class))).thenReturn(null);
	
		systemUnderTest.buscaConselhoProfissionalServidorVO(Integer.valueOf("123"), Short.valueOf("123"));
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
