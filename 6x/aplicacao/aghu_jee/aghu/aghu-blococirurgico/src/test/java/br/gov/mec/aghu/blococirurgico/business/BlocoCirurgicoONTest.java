package br.gov.mec.aghu.blococirurgico.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class BlocoCirurgicoONTest extends AGHUBaseUnitTest<BlocoCirurgicoON>{

	@Mock
	private IAdministracaoFacade mockIAdministracaoFacade;
	@Mock
	private IAghuFacade mockIAghuFacade;

			/**
	 * Teste caminho feliz. Se micro tiver unidade funcional com caracteristica de cirurgica retornar a unid. cirurgica
	 */
	@Test
	public void testPreUpdateAnticoagulanteSuccess() {
		try {
			final AghMicrocomputador micro = new AghMicrocomputador();
			AghUnidadesFuncionais aghUnidadesFuncionais = new AghUnidadesFuncionais();
			aghUnidadesFuncionais.setSeq(Short.parseShort("1"));
			micro.setAghUnidadesFuncionais(aghUnidadesFuncionais);
			
			Mockito.when(mockIAdministracaoFacade.obterAghMicroComputadorPorNomeOuIP(Mockito.anyString(), Mockito.any(DominioCaracteristicaMicrocomputador.class))).thenReturn(micro);
			
			Mockito.when(mockIAghuFacade.verificarCaracteristicaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(Boolean.TRUE);
			
			Assert.assertEquals(micro.getAghUnidadesFuncionais(), systemUnderTest.obterUnidadeFuncionalCirurgia(""));
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exeção gerada. " + e.getCode());
		}
	}
	
	/**
	 * Se não houver caracteristica de cirurgica, retornar nulo.
	 */
	@Test
	public void testPreUpdateAnticoagulanteSuccess02() {
		try {
			final AghMicrocomputador micro = new AghMicrocomputador();
			AghUnidadesFuncionais aghUnidadesFuncionais = new AghUnidadesFuncionais();
			aghUnidadesFuncionais.setSeq(Short.parseShort("1"));
			micro.setAghUnidadesFuncionais(aghUnidadesFuncionais);
			
			
			Mockito.when(mockIAdministracaoFacade.obterAghMicroComputadorPorNomeOuIP(Mockito.anyString(), Mockito.any(DominioCaracteristicaMicrocomputador.class))).thenReturn(micro);
			
			Mockito.when(mockIAghuFacade.verificarCaracteristicaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(Boolean.FALSE);

			Assert.assertNull(systemUnderTest.obterUnidadeFuncionalCirurgia(""));
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exeção gerada. " + e.getCode());
		}
	}
	
	/**
	 * Se Micro não tiver cadastrado retorna null
	 */
	@Test
	public void testPreUpdateAnticoagulanteSuccess03() {
		try {
			
			Mockito.when(mockIAdministracaoFacade.obterAghMicroComputadorPorNomeOuIP(Mockito.anyString(), Mockito.any(DominioCaracteristicaMicrocomputador.class))).thenReturn(null);
			
			Assert.assertNull(systemUnderTest.obterUnidadeFuncionalCirurgia(""));
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exeção gerada. " + e.getCode());
		}
	}
	
	/**
	 * Se não houver unidade funcional cadastrada, retorna null
	 */
	@Test
	public void testPreUpdateAnticoagulanteSuccess04() {
		try {
			final AghMicrocomputador micro = new AghMicrocomputador();
			micro.setAghUnidadesFuncionais(null);
			
			Mockito.when(mockIAdministracaoFacade.obterAghMicroComputadorPorNomeOuIP(Mockito.anyString(), Mockito.any(DominioCaracteristicaMicrocomputador.class))).thenReturn(micro);
			
			Assert.assertNull(systemUnderTest.obterUnidadeFuncionalCirurgia(""));
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exeção gerada. " + e.getCode());
		}
	}
}
