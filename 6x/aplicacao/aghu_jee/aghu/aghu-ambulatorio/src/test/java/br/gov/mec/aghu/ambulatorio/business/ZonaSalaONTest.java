package br.gov.mec.aghu.ambulatorio.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AelPedidoExame;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmAltaPedidoExame;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ZonaSalaONTest extends AGHUBaseUnitTest<ZonaSalaON>{

	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IAdministracaoFacade mockedAdministracaoFacade;
	@Mock
	private IExamesFacade mockedExamesFacade;
	@Mock
	private AacGradeAgendamenConsultasDAO mockedAacGradeAgendamenConsultasDAO;
    @Mock
    private IParametroFacade mockedParametroFacade;
	
		
			/**
	 * Testa verificação de dependencia 
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test(expected = BaseException.class)
	public void verificaDependendia0() throws Exception {
		
		whenObterParametro();
		
				List<MpmAltaPedidoExame> mpmAltaPedidoExame = new ArrayList<MpmAltaPedidoExame>();
				mpmAltaPedidoExame.add(new MpmAltaPedidoExame());
		
		Mockito.when(mockedPrescricaoMedicaFacade.pesquisarMpmAltaPedidoExamePorZonaESala(Mockito.anyShort(), Mockito.anyByte())).thenReturn(mpmAltaPedidoExame);
		
		systemUnderTest.verificaDependendia((short) 1, (byte)1);
	}	

	/**
	 * Testa verificação de dependencia 
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test(expected = BaseException.class)
	public void verificaDependendia1() throws Exception {
	
		whenObterParametro();
		
				List<MpmAltaPedidoExame> mpmAltaPedidoExame = new ArrayList<MpmAltaPedidoExame>();
		Mockito.when(mockedPrescricaoMedicaFacade.pesquisarMpmAltaPedidoExamePorZonaESala(Mockito.anyShort(), Mockito.anyByte())).thenReturn(mpmAltaPedidoExame);

				List<AghMicrocomputador> aghMicrocomputador = new ArrayList<AghMicrocomputador>();
				aghMicrocomputador.add(new AghMicrocomputador());
		Mockito.when(mockedAdministracaoFacade.pesquisarAghMicrocomputadorPorSala(Mockito.anyByte())).thenReturn(aghMicrocomputador);
		
		systemUnderTest.verificaDependendia((short) 1, (byte)1);
	}		
	
	/**
	 * Testa verificação de dependencia 
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test(expected = BaseException.class)
	public void verificaDependendia2() throws Exception {
	
		whenObterParametro();
		
				List<MpmAltaPedidoExame> mpmAltaPedidoExame = new ArrayList<MpmAltaPedidoExame>();
		Mockito.when(mockedPrescricaoMedicaFacade.pesquisarMpmAltaPedidoExamePorZonaESala(Mockito.anyShort(), Mockito.anyByte())).thenReturn(mpmAltaPedidoExame);

				List<AghMicrocomputador> aghMicrocomputador = new ArrayList<AghMicrocomputador>();
				aghMicrocomputador.add(new AghMicrocomputador());
		Mockito.when(mockedAdministracaoFacade.pesquisarAghMicrocomputadorPorSala(Mockito.anyByte())).thenReturn(aghMicrocomputador);
				
		Mockito.when(mockedAdministracaoFacade.pesquisarAghMicrocomputadorPorZonaESala(Mockito.anyShort(), Mockito.anyByte())).thenReturn(aghMicrocomputador);
		
		systemUnderTest.verificaDependendia((short) 1, (byte)1);
	}		
		
	/**
	 * Testa verificação de dependencia 
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test(expected = BaseException.class)
	public void verificaDependendia3() throws Exception {
	
		whenObterParametro();
		
				List<MpmAltaPedidoExame> mpmAltaPedidoExame = new ArrayList<MpmAltaPedidoExame>();
		Mockito.when(mockedPrescricaoMedicaFacade.pesquisarMpmAltaPedidoExamePorZonaESala(Mockito.anyShort(), Mockito.anyByte())).thenReturn(mpmAltaPedidoExame);

				List<AghMicrocomputador> aghMicrocomputador = new ArrayList<AghMicrocomputador>();
				aghMicrocomputador.add(new AghMicrocomputador());
		Mockito.when(mockedAdministracaoFacade.pesquisarAghMicrocomputadorPorSala(Mockito.anyByte())).thenReturn(aghMicrocomputador);
				
		Mockito.when(mockedAdministracaoFacade.pesquisarAghMicrocomputadorPorZonaESala(Mockito.anyShort(), Mockito.anyByte())).thenReturn(aghMicrocomputador);

				List<AelPedidoExame> aelPedidoExame = new ArrayList<AelPedidoExame>();
				aelPedidoExame.add(new AelPedidoExame());
		Mockito.when(mockedExamesFacade.pesquisarAelPedidoExamePorZonaESala(Mockito.anyShort(), Mockito.anyByte())).thenReturn(aelPedidoExame);
		
		systemUnderTest.verificaDependendia((short) 1, (byte)1);
	}	
	
	/**
	 * Testa verificação de dependencia 
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test(expected = BaseException.class)
	public void verificaDependendia4() throws Exception {
	
		whenObterParametro();
		
				List<MpmAltaPedidoExame> mpmAltaPedidoExame = new ArrayList<MpmAltaPedidoExame>();
		Mockito.when(mockedPrescricaoMedicaFacade.pesquisarMpmAltaPedidoExamePorZonaESala(Mockito.anyShort(), Mockito.anyByte())).thenReturn(mpmAltaPedidoExame);

				List<AghMicrocomputador> aghMicrocomputador = new ArrayList<AghMicrocomputador>();
				aghMicrocomputador.add(new AghMicrocomputador());
		Mockito.when(mockedAdministracaoFacade.pesquisarAghMicrocomputadorPorSala(Mockito.anyByte())).thenReturn(aghMicrocomputador);
				
		Mockito.when(mockedAdministracaoFacade.pesquisarAghMicrocomputadorPorZonaESala(Mockito.anyShort(), Mockito.anyByte())).thenReturn(aghMicrocomputador);

				List<AelPedidoExame> aelPedidoExame = new ArrayList<AelPedidoExame>();
		aelPedidoExame.add(new AelPedidoExame());
		Mockito.when(mockedExamesFacade.pesquisarAelPedidoExamePorZonaESala(Mockito.anyShort(), Mockito.anyByte())).thenReturn(aelPedidoExame);

				List<AacGradeAgendamenConsultas> aacGradeAgendamenConsultas = new ArrayList<AacGradeAgendamenConsultas>();
				aacGradeAgendamenConsultas.add(new AacGradeAgendamenConsultas());
		Mockito.when(mockedAacGradeAgendamenConsultasDAO.pesquisarAacGradeAgendamenConsultasPorZonaESala(Mockito.anyShort(), Mockito.anyByte())).thenReturn(aacGradeAgendamenConsultas);
		
		systemUnderTest.verificaDependendia((short) 1, (byte)1);
	}	
	
	/**
	 * Testa verificação de dependencia 
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test
	public void verificaDependendia5() throws Exception {
	
		whenObterParametro();
		
				List<MpmAltaPedidoExame> mpmAltaPedidoExame = new ArrayList<MpmAltaPedidoExame>();
		Mockito.when(mockedPrescricaoMedicaFacade.pesquisarMpmAltaPedidoExamePorZonaESala(Mockito.anyShort(), Mockito.anyByte())).thenReturn(mpmAltaPedidoExame);

				List<AghMicrocomputador> aghMicrocomputador = new ArrayList<AghMicrocomputador>();
		Mockito.when(mockedAdministracaoFacade.pesquisarAghMicrocomputadorPorSala(Mockito.anyByte())).thenReturn(aghMicrocomputador);
				
		Mockito.when(mockedAdministracaoFacade.pesquisarAghMicrocomputadorPorZonaESala(Mockito.anyShort(), Mockito.anyByte())).thenReturn(aghMicrocomputador);

				List<AelPedidoExame> aelPedidoExame = new ArrayList<AelPedidoExame>();
		Mockito.when(mockedExamesFacade.pesquisarAelPedidoExamePorZonaESala(Mockito.anyShort(), Mockito.anyByte())).thenReturn(aelPedidoExame);

				List<AacGradeAgendamenConsultas> aacGradeAgendamenConsultas = new ArrayList<AacGradeAgendamenConsultas>();
		Mockito.when(mockedAacGradeAgendamenConsultasDAO.pesquisarAacGradeAgendamenConsultasPorZonaESala(Mockito.anyShort(), Mockito.anyByte())).thenReturn(aacGradeAgendamenConsultas);
		
		systemUnderTest.verificaDependendia((short) 1, (byte)1);
	}			

	private void whenObterParametro() throws BaseException{
		AghParametros aghParametro = new AghParametros();
		aghParametro.setVlrNumerico(BigDecimal.ONE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(aghParametro);
}

	
}
