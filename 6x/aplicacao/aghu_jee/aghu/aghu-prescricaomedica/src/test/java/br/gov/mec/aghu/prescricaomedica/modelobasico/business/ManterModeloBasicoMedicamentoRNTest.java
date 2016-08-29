package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoPrescricaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterModeloBasicoMedicamentoRNTest extends AGHUBaseUnitTest<ManterModeloBasicoMedicamentoRN>{
	
	@Mock
	private MpmModeloBasicoPrescricaoDAO mockedModeloBasicoPrescricaoDAO;
	@Mock
	private MpmTipoFrequenciaAprazamentoDAO mockedTipoFrequenciaAprazamentoDAO;
	@Mock
	private IFarmaciaFacade mockedFarmaciaFacade;

	/**
	 * Teste deve retornar exceção.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void verificarExclusaoTest001() throws BaseException {
		
		final MpmModeloBasicoPrescricao modeloBasicoPrescricao = new MpmModeloBasicoPrescricao();
		RapServidores rapServidores = new RapServidores();
		RapServidoresId rapServidoresId = new RapServidoresId();
		rapServidoresId.setMatricula(Integer.valueOf("1111"));
		rapServidoresId.setVinCodigo(Short.valueOf("1111"));
		rapServidores.setId(rapServidoresId);
		modeloBasicoPrescricao.setServidor(rapServidores);

		Mockito.when(mockedModeloBasicoPrescricaoDAO.obterModeloBasicoPrescricaoPeloId(Mockito.anyInt())).thenReturn(modeloBasicoPrescricao);
		systemUnderTest.verificarExclusividade(1, 1111, Short.valueOf("22222"));
		
	}
	
	/**
	 * Teste deve retornar exceção.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void verificarExclusaoTest002() throws BaseException {
		
		final MpmModeloBasicoPrescricao modeloBasicoPrescricao = new MpmModeloBasicoPrescricao();
		RapServidores rapServidores = new RapServidores();
		RapServidoresId rapServidoresId = new RapServidoresId();
		rapServidoresId.setMatricula(Integer.valueOf("1111"));
		rapServidoresId.setVinCodigo(Short.valueOf("1111"));
		rapServidores.setId(rapServidoresId);
		modeloBasicoPrescricao.setServidor(rapServidores);

		Mockito.when(mockedModeloBasicoPrescricaoDAO.obterModeloBasicoPrescricaoPeloId(Mockito.anyInt())).thenReturn(modeloBasicoPrescricao);
		systemUnderTest.verificarExclusividade(1, 2222, Short.valueOf("1111"));
		
	}
	
	/**
	 * Teste deve passar.
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarExclusaoTest003() throws BaseException {
		
		final MpmModeloBasicoPrescricao modeloBasicoPrescricao = new MpmModeloBasicoPrescricao();
		RapServidores rapServidores = new RapServidores();
		RapServidoresId rapServidoresId = new RapServidoresId();
		rapServidoresId.setMatricula(Integer.valueOf("1111"));
		rapServidoresId.setVinCodigo(Short.valueOf("1111"));
		rapServidores.setId(rapServidoresId);
		modeloBasicoPrescricao.setServidor(rapServidores);

		Mockito.when(mockedModeloBasicoPrescricaoDAO.obterModeloBasicoPrescricaoPeloId(Mockito.anyInt())).thenReturn(modeloBasicoPrescricao);
		systemUnderTest.verificarExclusividade(1, 1111, Short.valueOf("1111"));
		
	}
	
	/**
	 * Teste deve retornar exceção.
	 * @throws AGHUNegocioException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void verificarExclusaoTest004() throws BaseException {
		
		final MpmModeloBasicoPrescricao modeloBasicoPrescricao = new MpmModeloBasicoPrescricao();
		RapServidores rapServidores = new RapServidores();
		RapServidoresId rapServidoresId = new RapServidoresId();
		rapServidoresId.setMatricula(Integer.valueOf("1111"));
		rapServidoresId.setVinCodigo(Short.valueOf("1111"));
		rapServidores.setId(rapServidoresId);
		modeloBasicoPrescricao.setServidor(rapServidores);

		Mockito.when(mockedModeloBasicoPrescricaoDAO.obterModeloBasicoPrescricaoPeloId(Mockito.anyInt())).thenReturn(modeloBasicoPrescricao);
		systemUnderTest.verificarExclusividade(null, Mockito.eq(1111), Mockito.eq(Short.valueOf("1111")));

	}
	
	/**
	 * Teste deve retornar exceção.
	 * @throws AGHUNegocioException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void verificarExclusaoTest005() throws BaseException {
		
		final MpmModeloBasicoPrescricao modeloBasicoPrescricao = new MpmModeloBasicoPrescricao();
		RapServidores rapServidores = new RapServidores();
		RapServidoresId rapServidoresId = new RapServidoresId();
		rapServidoresId.setMatricula(Integer.valueOf("1111"));
		rapServidoresId.setVinCodigo(Short.valueOf("1111"));
		rapServidores.setId(rapServidoresId);
		modeloBasicoPrescricao.setServidor(rapServidores);
		
		Mockito.when(mockedModeloBasicoPrescricaoDAO.obterModeloBasicoPrescricaoPeloId(Mockito.anyInt())).thenReturn(modeloBasicoPrescricao);
		systemUnderTest.verificarExclusividade(null, null, Mockito.eq(Short.valueOf("1111")));
	}
	
	/**
	 * Teste deve retornar exceção.
	 * @throws AGHUNegocioException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void verificarExclusaoTest006() throws BaseException {
		
		final MpmModeloBasicoPrescricao modeloBasicoPrescricao = new MpmModeloBasicoPrescricao();
		RapServidores rapServidores = new RapServidores();
		RapServidoresId rapServidoresId = new RapServidoresId();
		rapServidoresId.setMatricula(Integer.valueOf("1111"));
		rapServidoresId.setVinCodigo(Short.valueOf("1111"));
		rapServidores.setId(rapServidoresId);
		modeloBasicoPrescricao.setServidor(rapServidores);
		
		Mockito.when(mockedModeloBasicoPrescricaoDAO.obterModeloBasicoPrescricaoPeloId(Mockito.anyInt())).thenReturn(modeloBasicoPrescricao);
		systemUnderTest.verificarExclusividade(null, null, null);
	}
	
	/**
	 * Teste deve retornar exceção MPM_00905.
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarTipoFrequenciaTest001() throws BaseException {
		
		final MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = new MpmTipoFrequenciaAprazamento();

		Mockito.when(mockedTipoFrequenciaAprazamentoDAO.obterTipoFrequenciaAprazamentoPeloId(Mockito.anyShort())).thenReturn(tipoFrequenciaAprazamento);

    	try {

    		systemUnderTest.verificarTipoFrequencia(Short.valueOf("1"));
    		fail("Deveria ter ocorrido uma exceção!!!");
    		
    	} catch (ApplicationBusinessException atual) {

    		assertEquals("MPM_00905", atual.getMessage());
    		
		}
		
	}
	
	/**
	 * Teste deve retornar exceção MPM_00925.
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarTipoFrequenciaTest002() throws BaseException {
		
		final MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = null;

		Mockito.when(mockedTipoFrequenciaAprazamentoDAO.obterTipoFrequenciaAprazamentoPeloId(Mockito.anyShort())).thenReturn(tipoFrequenciaAprazamento);
    	
    	try {

    		systemUnderTest.verificarTipoFrequencia(Short.valueOf("1"));
    		fail("Deveria ter ocorrido uma exceção!!!");
    		
    	} catch (ApplicationBusinessException atual) {

    		assertEquals("MPM_00925", atual.getMessage());
    		
		}
		
	}
	
	/**
	 * Teste deve passar.
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarTipoFrequenciaTest003() throws BaseException {
		
		final MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = new MpmTipoFrequenciaAprazamento();
		tipoFrequenciaAprazamento.setIndSituacao(DominioSituacao.A);
		
		Mockito.when(mockedTipoFrequenciaAprazamentoDAO.obterTipoFrequenciaAprazamentoPeloId(Mockito.anyShort())).thenReturn(tipoFrequenciaAprazamento);
    	
    	systemUnderTest.verificarTipoFrequencia(Short.valueOf("1"));
    			
	}
	
	/**
	 * Teste deve retornar exceção MPM_00928.
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarViaAdministracaoTest001() throws BaseException {
		
		final AfaViaAdministracao afaViaAdministracao = new AfaViaAdministracao();

		Mockito.when(mockedFarmaciaFacade.obterViaAdministracao(Mockito.anyString())).thenReturn(afaViaAdministracao);

    	try {

    		systemUnderTest.verificarViaAdministracao("teste");
    		fail("Deveria ter ocorrido uma exceção!!!");
    		
    	} catch (ApplicationBusinessException atual) {

    		assertEquals("MPM_00928", atual.getMessage());
    		
		}
		
	}
	
	/**
	 * Teste deve retornar exceção MPM_00929.
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarViaAdministracaoTest002() throws BaseException {
		
		final AfaViaAdministracao afaViaAdministracao = null;

		Mockito.when(mockedFarmaciaFacade.obterViaAdministracao(Mockito.anyString())).thenReturn(afaViaAdministracao);

    	try {

    		systemUnderTest.verificarViaAdministracao("teste");
    		fail("Deveria ter ocorrido uma exceção!!!");
    		
    	} catch (ApplicationBusinessException atual) {

    		assertEquals("MPM_00929", atual.getMessage());
    		
		}
		
	}
	
	/**
	 * Teste deve passar.
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarViaAdministracaoTest003() throws BaseException {
		
		final AfaViaAdministracao afaViaAdministracao = new AfaViaAdministracao();
		afaViaAdministracao.setIndSituacao(DominioSituacao.A);

		Mockito.when(mockedFarmaciaFacade.obterViaAdministracao(Mockito.anyString())).thenReturn(afaViaAdministracao);

    	systemUnderTest.verificarViaAdministracao("teste");
    			
	}
	
	/**
	 * Teste deve retornar exceção MPM_00905.
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarFrequenciaTest001() throws BaseException {
		
		final MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = new MpmTipoFrequenciaAprazamento();

		Mockito.when(mockedTipoFrequenciaAprazamentoDAO.obterTipoFrequenciaAprazamentoPeloId(Mockito.anyShort())).thenReturn(tipoFrequenciaAprazamento);

    	try {

    		systemUnderTest.verificarFrequencia(Short.valueOf("1"), Short.valueOf("1"));
    		fail("Deveria ter ocorrido uma exceção!!!");
    		
    	} catch (ApplicationBusinessException atual) {

    		assertEquals("MPM_00905", atual.getMessage());
    		
		}
		
	}
	
	/**
	 * Teste deve retornar exceção MPM_00930.
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarFrequenciaTest002() throws BaseException {
		
		final MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = new MpmTipoFrequenciaAprazamento();
		tipoFrequenciaAprazamento.setIndSituacao(DominioSituacao.A);
		tipoFrequenciaAprazamento.setIndDigitaFrequencia(Boolean.TRUE);

		Mockito.when(mockedTipoFrequenciaAprazamentoDAO.obterTipoFrequenciaAprazamentoPeloId(Mockito.anyShort())).thenReturn(tipoFrequenciaAprazamento);
    	
    	try {

    		systemUnderTest.verificarFrequencia(Short.valueOf("1"), null);
    		fail("Deveria ter ocorrido uma exceção!!!");
    		
    	} catch (ApplicationBusinessException atual) {

    		assertEquals("MPM_00930", atual.getMessage());
    		
		}
		
	}
	
	/**
	 * Teste deve retornar exceção MPM_00931.
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarFrequenciaTest003() throws BaseException {
		
		final MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = new MpmTipoFrequenciaAprazamento();
		tipoFrequenciaAprazamento.setIndSituacao(DominioSituacao.A);
		tipoFrequenciaAprazamento.setIndDigitaFrequencia(Boolean.FALSE);

		Mockito.when(mockedTipoFrequenciaAprazamentoDAO.obterTipoFrequenciaAprazamentoPeloId(Mockito.anyShort())).thenReturn(tipoFrequenciaAprazamento);
    	try {

    		systemUnderTest.verificarFrequencia(Short.valueOf("1"), Short.valueOf("1"));
    		fail("Deveria ter ocorrido uma exceção!!!");
    		
    	} catch (ApplicationBusinessException atual) {

    		assertEquals("MPM_00931", atual.getMessage());
    		
		}
		
	}
	
	/**
	 * Teste deve passar.
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarFrequenciaTest004() throws BaseException {
		
		final MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = new MpmTipoFrequenciaAprazamento();
		tipoFrequenciaAprazamento.setIndSituacao(DominioSituacao.A);
		tipoFrequenciaAprazamento.setIndDigitaFrequencia(Boolean.TRUE);

		Mockito.when(mockedTipoFrequenciaAprazamentoDAO.obterTipoFrequenciaAprazamentoPeloId(Mockito.anyShort())).thenReturn(tipoFrequenciaAprazamento);

    	systemUnderTest.verificarFrequencia(Short.valueOf("1"), Short.valueOf("1"));
    			
	}
	
	/**
	 * Teste deve retornar exceção MPM_00932.
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarGotejoTest001() throws BaseException {

		Mockito.when(mockedFarmaciaFacade.obtemSituacaoTipoVelocidade(Mockito.anyShort())).thenReturn(DominioSituacao.I);

    	try {

    		systemUnderTest.verificarGotejo(BigDecimal.ONE, new AfaTipoVelocAdministracoes());
    		fail("Deveria ter ocorrido uma exceção!!!");
    		
    	} catch (ApplicationBusinessException atual) {

    		assertEquals("MPM_00932", atual.getMessage());
    		
		}
		
	}
	
	/**
	 * Teste deve retornar exceção MPM_00933.
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarGotejoTest002() throws BaseException {

		Mockito.when(mockedFarmaciaFacade.obtemSituacaoTipoVelocidade(Mockito.anyShort())).thenReturn(null);

		try {

    		systemUnderTest.verificarGotejo(BigDecimal.ONE, new AfaTipoVelocAdministracoes());
    		fail("Deveria ter ocorrido uma exceção!!!");
    		
    	} catch (ApplicationBusinessException atual) {

    		assertEquals("MPM_00933", atual.getMessage());
    		
		}
		
	}
	
	/**
	 * Teste deve passar.
	 * @throws AGHUNegocioException
	 */
	@Test
	public void verificarGotejoTest003() throws BaseException {
		
		Mockito.when(mockedFarmaciaFacade.obtemSituacaoTipoVelocidade(Mockito.anyShort())).thenReturn(DominioSituacao.A);
    	
    	systemUnderTest.verificarGotejo(BigDecimal.ONE, new AfaTipoVelocAdministracoes());
    	    			
	}

}
