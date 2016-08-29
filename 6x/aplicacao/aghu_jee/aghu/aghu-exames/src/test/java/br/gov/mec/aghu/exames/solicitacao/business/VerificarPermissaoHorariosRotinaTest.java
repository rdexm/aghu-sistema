package br.gov.mec.aghu.exames.solicitacao.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.UnfExecutaSinonimoExameVO;
import br.gov.mec.aghu.model.AelSinonimoExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class VerificarPermissaoHorariosRotinaTest extends AGHUBaseUnitTest<VerificarPermissaoHorariosRotina>{
	
	@Mock
	private ItemSolicitacaoExameVO itemSolicitacaoExameVO;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;

	
	/**
	 * Testa quando o ind_permite_programar exames não for R tem que desenhar um calendario.
	 */
	@Test
	public void testProcessRequest02() {
		AghUnidadesFuncionais unidad = new AghUnidadesFuncionais();
		unidad.setSeq(Short.valueOf("1"));
		
		AelUnfExecutaExames unfExecutaExame = new AelUnfExecutaExames();
		unfExecutaExame.setUnidadeFuncional(unidad);
		unfExecutaExame.setId(new AelUnfExecutaExamesId());
		UnfExecutaSinonimoExameVO exameVO = new UnfExecutaSinonimoExameVO(unfExecutaExame, new AelSinonimoExame()); 
		itemSolicitacaoExameVO.setUnfExecutaExame(exameVO);
		
		try {
			whenObterParametro();
			Mockito.when(mockedAghuFacade.obterUnidadeFuncional(Mockito.anyShort())).thenReturn(Mockito.any(AghUnidadesFuncionais.class));
			assertEquals(TipoCampoDataHoraISE.CALENDAR, systemUnderTest.processRequest(itemSolicitacaoExameVO));
		} catch (BaseException e) {
			fail("Exceção gerada: "+ e.getMessage());
		}
	}
	
	private void whenObterParametro() throws BaseException{
		AghParametros aghParametro = new AghParametros();
		aghParametro.setVlrNumerico(BigDecimal.ONE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(aghParametro);
	}

}
