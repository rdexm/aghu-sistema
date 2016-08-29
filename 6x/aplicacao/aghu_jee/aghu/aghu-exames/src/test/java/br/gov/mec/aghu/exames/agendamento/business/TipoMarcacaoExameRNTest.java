package br.gov.mec.aghu.exames.agendamento.business;

import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.agendamento.business.TipoMarcacaoExameRN.TipoMarcacaoExameRNExceptionCode;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelHorarioExameDispDAO;
import br.gov.mec.aghu.exames.dao.AelHorarioGradeExameDAO;
import br.gov.mec.aghu.exames.dao.AelTipoMarcacaoExameDAO;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelHorarioGradeExame;
import br.gov.mec.aghu.model.AelTipoMarcacaoExame;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class TipoMarcacaoExameRNTest extends AGHUBaseUnitTest<TipoMarcacaoExameRN>{
	
	@Mock
	private AelTipoMarcacaoExameDAO mockedAelTipoMarcacaoExameDAO;
	@Mock
	private AelHorarioExameDispDAO mockedAelHorarioExameDispDAO;
	@Mock
	private AelHorarioGradeExameDAO mockedAelHorarioGradeExameDAO;
	@Mock
	private IExamesFacade mockedExamesFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
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
	public void testExecutarBeforeInsertTipoMarcacaoExame() throws ApplicationBusinessException {
		try {
			systemUnderTest.executarBeforeInsertTipoMarcacaoExame(new AelTipoMarcacaoExame());

		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}

	@Test
	public void testExecutarBeforeUpdateTipoMarcacaoExameSuccess() {
		final AelTipoMarcacaoExame tipoMarcacaoExameOld = new AelTipoMarcacaoExame();
		tipoMarcacaoExameOld.setDescricao("AAA");

		final AelTipoMarcacaoExame tipoMarcacaoExameNew = new AelTipoMarcacaoExame();
		tipoMarcacaoExameNew.setDescricao("AAA");

		try {
			systemUnderTest.executarBeforeUpdateTipoMarcacaoExame(tipoMarcacaoExameOld, tipoMarcacaoExameNew);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}

	@Test
	public void testExecutarBeforeUpdateTipoMarcacaoExameError01() {
		final AelTipoMarcacaoExame tipoMarcacaoExameOld = new AelTipoMarcacaoExame();
		tipoMarcacaoExameOld.setDescricao("AAA");

		final AelTipoMarcacaoExame tipoMarcacaoExameNew = new AelTipoMarcacaoExame();
		tipoMarcacaoExameNew.setDescricao("BBB");

		try {
			systemUnderTest.executarBeforeUpdateTipoMarcacaoExame(tipoMarcacaoExameOld, tipoMarcacaoExameNew);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), TipoMarcacaoExameRN.TipoMarcacaoExameRNExceptionCode.AEL_00346);
		}
	}	

	@Test
	public void testExecutarBeforeUpdateTipoMarcacaoExameError02() {
		final RapServidores servidorOld = new RapServidores();
		servidorOld.setId(new RapServidoresId(1, Short.valueOf("2")));
		final AelTipoMarcacaoExame tipoMarcacaoExameOld = new AelTipoMarcacaoExame();
		tipoMarcacaoExameOld.setServidor(servidorOld);

		final RapServidores servidorNew = new RapServidores();
		servidorNew.setId(new RapServidoresId(3, Short.valueOf("4")));
		final AelTipoMarcacaoExame tipoMarcacaoExameNew = new AelTipoMarcacaoExame();
		tipoMarcacaoExameNew.setServidor(servidorNew);

		try {
			systemUnderTest.executarBeforeUpdateTipoMarcacaoExame(tipoMarcacaoExameOld, tipoMarcacaoExameNew);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), TipoMarcacaoExameRN.TipoMarcacaoExameRNExceptionCode.AEL_00369);
		}
	}	





	@Test
	public void testVerificarDelecaoSuccess() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		final Date dataCriadoEm = cal.getTime();
		final AghParametros paramDiasDel = new AghParametros();
		paramDiasDel.setVlrNumerico(new BigDecimal(300));

		try {
			Mockito.when(mockedParametroFacade.obterAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL)).thenReturn(paramDiasDel);

			systemUnderTest.verificarDelecao(dataCriadoEm);

		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}

	@Test
	public void testVerificarDelecaoError() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -12);
		final Date dataCriadoEm = cal.getTime();
		final AghParametros paramDiasDel = new AghParametros();
		paramDiasDel.setVlrNumerico(new BigDecimal(300));

		try {
			Mockito.when(mockedParametroFacade.obterAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL)).thenReturn(paramDiasDel);

			systemUnderTest.verificarDelecao(dataCriadoEm);

		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), TipoMarcacaoExameRNExceptionCode.AEL_00343);
		}
	}

	@Test
	public void testVerificarDependentesGradeExameSuccess() {
		try {
			final List<AelHorarioGradeExame> list = new ArrayList<AelHorarioGradeExame>();

			Mockito.when(mockedAelHorarioGradeExameDAO.pesquisaPorTipoMarcacaoExame(Mockito.any(AelTipoMarcacaoExame.class))).thenReturn(list);
			
			systemUnderTest.verificarDependentesGradeExame(new AelTipoMarcacaoExame());
		} catch (ApplicationBusinessException e) {
			fail("Exceção nao esperada");
		}
	}

	@Test (expected=ApplicationBusinessException.class)
	public void testVerificarDependentesGradeExameError() throws ApplicationBusinessException {
		final List<AelHorarioGradeExame> list = new ArrayList<AelHorarioGradeExame>();
		list.add(new AelHorarioGradeExame());
		
		Mockito.when(mockedAelHorarioGradeExameDAO.pesquisaPorTipoMarcacaoExame(Mockito.any(AelTipoMarcacaoExame.class))).thenReturn(list);

		systemUnderTest.verificarDependentesGradeExame(new AelTipoMarcacaoExame());
		fail("Exceção esperada não lançada");
	}

	@Test 
	public void testVerificarDependentesExameDispSuccess() {
		try {
			final List<AelHorarioExameDisp> list = new ArrayList<AelHorarioExameDisp>();
			
			Mockito.when(mockedAelHorarioExameDispDAO.pesquisaPorTipoMarcacaoExame(Mockito.any(AelTipoMarcacaoExame.class))).thenReturn(list);

			systemUnderTest.verificarDependentesExameDisp(new AelTipoMarcacaoExame());
		} catch (ApplicationBusinessException e) {
			fail("Exceção nao esperada");
		}
	}

	@Test (expected=ApplicationBusinessException.class)
	public void testVerificarDependentesExameDispError() throws ApplicationBusinessException {
		final List<AelHorarioExameDisp> list = new ArrayList<AelHorarioExameDisp>();
		list.add(new AelHorarioExameDisp());
		
		Mockito.when(mockedAelHorarioExameDispDAO.pesquisaPorTipoMarcacaoExame(Mockito.any(AelTipoMarcacaoExame.class))).thenReturn(list);

		systemUnderTest.verificarDependentesExameDisp(new AelTipoMarcacaoExame());
		fail("Exceção esperada não lançada");
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
