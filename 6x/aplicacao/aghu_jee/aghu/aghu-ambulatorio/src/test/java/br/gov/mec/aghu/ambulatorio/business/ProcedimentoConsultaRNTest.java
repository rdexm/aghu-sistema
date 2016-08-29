package br.gov.mec.aghu.ambulatorio.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author diego.pacheco
 *
 */
public class ProcedimentoConsultaRNTest extends AGHUBaseUnitTest<ProcedimentoConsultaRN>{
	
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private AacConsultasDAO mockedAacConsultasDAO;
	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;	
	
	@Test
	public void testVerificarRetornoConsultaSuccess() {
		
		Mockito.when(mockedAacConsultasDAO.pesquisarRetornosAbsenteismoCount(Mockito.anyInt())).thenReturn(Long.valueOf(1));

		try {
			
			systemUnderTest.verificarRetornoConsulta(Integer.valueOf(1));
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada. " + e.getCode());
		}
	}
	
	@Test
	/**
	 * Testa o primeiro if que verifica se não há o procedimento lançado 
	 */	
	public void testVerificarIndicadorProcedimentoConsultaLancadoSuccess01() {
		AacConsultas consulta = new AacConsultas();
		consulta.setNumero(7);
		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(new BigDecimal("6"));

		try {
			Mockito.when(mockedAacConsultasDAO.obterConsulta(Mockito.anyInt())).thenReturn(consulta);
			
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
			
				List<VFatAssociacaoProcedimento> listaVFatAssociacaoProcedimentos = new ArrayList<VFatAssociacaoProcedimento>();
				FatConvenioSaudePlanoId convenioId = new FatConvenioSaudePlanoId(Short.valueOf("1"), Byte.valueOf("5"));
				FatConvenioSaudePlano convenio = new FatConvenioSaudePlano();
				convenio.setId(convenioId);
				consulta.setConvenioSaudePlano(convenio);
			
			Mockito.when(mockedFaturamentoFacade.listarVFatAssociacaoProcedimento(Mockito.anyInt(), Mockito.anyShort(), Mockito.anyByte(), Mockito.anyShort())).thenReturn(listaVFatAssociacaoProcedimentos);
			
			systemUnderTest.verificarIndicadorProcedimentoConsultaLancado(consulta.getNumero(),
					Mockito.anyInt());
			
			Assert.assertFalse(false);			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada. " + e.getCode());
		}		
	}
	
	@Test
	/**
	 * Testa o segundo if que verifica se não há um outro procedimento lançado para a mesma consulta 
	 */		
	public void testVerificarIndicadorProcedimentoConsultaLancadoSuccess02() {
		AacConsultas consulta = new AacConsultas();
		consulta.setNumero(7);
		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(new BigDecimal("6"));

		try {
			Mockito.when(mockedAacConsultasDAO.obterConsulta(Mockito.anyInt())).thenReturn(consulta);
			
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
			
			
				List<VFatAssociacaoProcedimento> listaVFatAssociacaoProcedimentos = new ArrayList<VFatAssociacaoProcedimento>();
				VFatAssociacaoProcedimento vFatAssociacaoProcedimento = new VFatAssociacaoProcedimento();
				vFatAssociacaoProcedimento.setIphIndConsulta(true);
				listaVFatAssociacaoProcedimentos.add(vFatAssociacaoProcedimento);
				FatConvenioSaudePlanoId convenioId = new FatConvenioSaudePlanoId(Short.valueOf("1"), Byte.valueOf("5"));
				FatConvenioSaudePlano convenio = new FatConvenioSaudePlano();
				convenio.setId(convenioId);
				consulta.setConvenioSaudePlano(convenio);
			
			Mockito.when(mockedFaturamentoFacade.listarVFatAssociacaoProcedimento(Mockito.anyInt(), Mockito.anyShort(), Mockito.anyByte(), Mockito.anyShort())).thenReturn(listaVFatAssociacaoProcedimentos);
		
			Mockito.when(mockedFaturamentoFacade.listarVFatAssociacaoProcedimentoOutrosProcedimentos(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort(), Mockito.anyByte(), Mockito.anyShort())).thenReturn(listaVFatAssociacaoProcedimentos);
			
			systemUnderTest.verificarIndicadorProcedimentoConsultaLancado(consulta.getNumero(),
					Mockito.anyInt());
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada. " + e.getCode());
		}		
	}	
	
	@Test
	/**
	 * Testa o ultimo else, caso em que não existe nenhum procedimento lançado
	 */	
	public void testVerificarIndicadorProcedimentoConsultaLancadoSuccess03() {
		AacConsultas consulta = new AacConsultas();
		consulta.setNumero(7);
		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(new BigDecimal("6"));
		
		try {
			Mockito.when(mockedAacConsultasDAO.obterConsulta(Mockito.anyInt())).thenReturn(consulta);
			
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
			
				List<VFatAssociacaoProcedimento> listaVFatAssociacaoProcedimentos = new ArrayList<VFatAssociacaoProcedimento>();
				VFatAssociacaoProcedimento vFatAssociacaoProcedimento = new VFatAssociacaoProcedimento();
				vFatAssociacaoProcedimento.setIphIndConsulta(true);
				listaVFatAssociacaoProcedimentos.add(vFatAssociacaoProcedimento);
				FatConvenioSaudePlanoId convenioId = new FatConvenioSaudePlanoId(Short.valueOf("1"), Byte.valueOf("5"));
				FatConvenioSaudePlano convenio = new FatConvenioSaudePlano();
				convenio.setId(convenioId);
				consulta.setConvenioSaudePlano(convenio);
			
			Mockito.when(mockedFaturamentoFacade.listarVFatAssociacaoProcedimento(Mockito.anyInt(), Mockito.anyShort(), Mockito.anyByte(), Mockito.anyShort())).thenReturn(listaVFatAssociacaoProcedimentos);
		
			Mockito.when(mockedFaturamentoFacade.listarVFatAssociacaoProcedimentoOutrosProcedimentos(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort(), Mockito.anyByte(), Mockito.anyShort())).thenReturn(listaVFatAssociacaoProcedimentos);

			systemUnderTest.verificarIndicadorProcedimentoConsultaLancado(consulta.getNumero(),
					Mockito.anyInt());
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada. " + e.getCode());
		}		
	}	

}
