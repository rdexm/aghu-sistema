package br.gov.mec.aghu.exames.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.business.GrupoExamesUsuaisRN.GrupoExamesUsuaisRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelGrupoExameUsualDAO;
import br.gov.mec.aghu.model.AelGrupoExameUsual;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

public class GrupoExamesUsuaisRNTest extends AGHUBaseUnitTest<GrupoExamesUsuaisRN>{
	
	@Mock
	private AelGrupoExameUsualDAO mockedAelGrupoExameUsualDAO;
	@Mock
	private IParametroFacade mockedParametroFacade;
	
	@Test
	public void testVerificarDelecaoSuccess() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		final Date dataCriadoEm = DateUtil.truncaData(cal.getTime());
		final AghParametros paramDiasDel = new AghParametros();
		paramDiasDel.setVlrNumerico(new BigDecimal(300));
		
		RapServidoresId id = new RapServidoresId();
		id.setMatricula(123);
		id.setVinCodigo(Short.valueOf("2"));
		
		RapServidores servidor = new RapServidores();
		servidor.setId(id);
		
		final AelGrupoExameUsual aelGrupoExameUsualNew = new AelGrupoExameUsual();
		aelGrupoExameUsualNew.setDescricao("Teste");
		aelGrupoExameUsualNew.setServidor(servidor);
		aelGrupoExameUsualNew.setCriadoEm(dataCriadoEm);
		
		try {
			Mockito.when(mockedAelGrupoExameUsualDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(aelGrupoExameUsualNew);

			Mockito.when(mockedParametroFacade.obterAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL)).thenReturn(paramDiasDel);

			systemUnderTest.executarBeforeDeleteGrupoExamesUsuais(1);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testVerificarDelecaoError1() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		final Date dataCriadoEm = DateUtil.truncaData(cal.getTime());
		
		RapServidoresId id = new RapServidoresId();
		id.setMatricula(13);
		id.setVinCodigo(Short.valueOf("2"));
		
		RapServidores servidor = new RapServidores();
		servidor.setId(id);
		
		final AelGrupoExameUsual aelGrupoExameUsualNew = new AelGrupoExameUsual();
		aelGrupoExameUsualNew.setDescricao("Teste1");
		aelGrupoExameUsualNew.setServidor(servidor);
		aelGrupoExameUsualNew.setCriadoEm(dataCriadoEm);

		final AghParametros aghParametros = new AghParametros();
		aghParametros.setVlrNumerico(new BigDecimal(0));
		
		try {
			Mockito.when(mockedAelGrupoExameUsualDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(aelGrupoExameUsualNew);

			Mockito.when(mockedParametroFacade.obterAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL)).thenReturn(aghParametros);

			systemUnderTest.executarBeforeDeleteGrupoExamesUsuais(1);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExamesUsuaisRNExceptionCode.AEL_00343);
		}
	}
	
	@Test
	public void testVerificarDelecaoError2() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		final Date dataCriadoEm = DateUtil.truncaData(cal.getTime());
		
		RapServidoresId id = new RapServidoresId();
		id.setMatricula(13);
		id.setVinCodigo(Short.valueOf("2"));
		
		RapServidores servidor = new RapServidores();
		servidor.setId(id);
		
		final AelGrupoExameUsual aelGrupoExameUsualNew = new AelGrupoExameUsual();
		aelGrupoExameUsualNew.setDescricao("Teste1");
		aelGrupoExameUsualNew.setServidor(servidor);
		aelGrupoExameUsualNew.setCriadoEm(dataCriadoEm);
		
		try {
			Mockito.when(mockedAelGrupoExameUsualDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(aelGrupoExameUsualNew);

			Mockito.when(mockedParametroFacade.obterAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL))
			.thenThrow(new ApplicationBusinessException(GrupoExamesUsuaisRNExceptionCode.AEL_00344));
			
			systemUnderTest.executarBeforeDeleteGrupoExamesUsuais(1);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExamesUsuaisRNExceptionCode.AEL_00344);
		}
	}
	
	@Test
	public void testExecutarBeforeInsertGrupoExamesUsuaisSuccess() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		final Date dataCriadoEm = DateUtil.truncaData(cal.getTime());
		
		RapServidoresId id = new RapServidoresId();
		id.setMatricula(123);
		id.setVinCodigo(Short.valueOf("2"));
		
		RapServidores servidor = new RapServidores();
		servidor.setId(id);
		
		final AelGrupoExameUsual aelGrupoExameUsualNew = new AelGrupoExameUsual();
		aelGrupoExameUsualNew.setDescricao("Teste");
		aelGrupoExameUsualNew.setServidor(servidor);
		aelGrupoExameUsualNew.setCriadoEm(dataCriadoEm);
		
		try {
			systemUnderTest.executarBeforeInsertGrupoExamesUsuais(aelGrupoExameUsualNew);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testExecutarBeforeInsertGrupoExamesUsuaisError() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		final Date dataCriadoEm = DateUtil.truncaData(cal.getTime());
		
		final AelGrupoExameUsual aelGrupoExameUsualNew = new AelGrupoExameUsual();
		aelGrupoExameUsualNew.setDescricao("Teste");
		aelGrupoExameUsualNew.setCriadoEm(dataCriadoEm);
		
		try {
			systemUnderTest.executarBeforeInsertGrupoExamesUsuais(aelGrupoExameUsualNew);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExamesUsuaisRNExceptionCode.AEL_00353);
		}
	}
	
	@Test
	public void testExecutarBeforeUpdateGrupoExamesUsuaisSuccess() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		final Date dataCriadoEm = DateUtil.truncaData(cal.getTime());
		
		RapServidoresId id = new RapServidoresId();
		id.setMatricula(123);
		id.setVinCodigo(Short.valueOf("2"));
		
		RapServidores servidor = new RapServidores();
		servidor.setId(id);
		
		final AelGrupoExameUsual aelGrupoExameUsualNew = new AelGrupoExameUsual();
		aelGrupoExameUsualNew.setDescricao("Teste");
		aelGrupoExameUsualNew.setServidor(servidor);
		aelGrupoExameUsualNew.setCriadoEm(dataCriadoEm);
		
		try {
			Mockito.when(mockedAelGrupoExameUsualDAO.obterOriginal(Mockito.anyInt())).thenReturn(aelGrupoExameUsualNew);
			
			systemUnderTest.executarBeforeUpdateGrupoExamesUsuais(aelGrupoExameUsualNew);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testExecutarBeforeUpdateGrupoExamesUsuaisError1() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		final Date dataCriadoEm = DateUtil.truncaData(cal.getTime());
		
		RapServidoresId id = new RapServidoresId();
		id.setMatricula(123);
		id.setVinCodigo(Short.valueOf("2"));
		
		RapServidores servidor = new RapServidores();
		servidor.setId(id);
		
		final AelGrupoExameUsual aelGrupoExameUsualOld = new AelGrupoExameUsual();
		aelGrupoExameUsualOld.setDescricao("Teste");
		aelGrupoExameUsualOld.setServidor(servidor);
		aelGrupoExameUsualOld.setCriadoEm(dataCriadoEm);
		
		final AelGrupoExameUsual aelGrupoExameUsualNew = new AelGrupoExameUsual();
		aelGrupoExameUsualNew.setDescricao("Teste2");
		
		try {
			Mockito.when(mockedAelGrupoExameUsualDAO.obterOriginal(Mockito.anyInt())).thenReturn(aelGrupoExameUsualOld);
			
			systemUnderTest.executarBeforeUpdateGrupoExamesUsuais(aelGrupoExameUsualNew);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExamesUsuaisRNExceptionCode.AEL_00346);
		}
	}
	
	@Test
	public void testExecutarBeforeUpdateGrupoExamesUsuaisError2() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		final Date dataCriadoEm = DateUtil.truncaData(cal.getTime());
		
		RapServidoresId id = new RapServidoresId();
		id.setMatricula(123);
		id.setVinCodigo(Short.valueOf("2"));
		
		RapServidores servidor = new RapServidores();
		servidor.setId(id);
		
		final AelGrupoExameUsual aelGrupoExameUsualOld = new AelGrupoExameUsual();
		aelGrupoExameUsualOld.setDescricao("Teste");
		aelGrupoExameUsualOld.setServidor(servidor);
		aelGrupoExameUsualOld.setCriadoEm(dataCriadoEm);
		
		final AelGrupoExameUsual aelGrupoExameUsualNew = new AelGrupoExameUsual();
		aelGrupoExameUsualNew.setDescricao("Teste");
		
		try {
			Mockito.when(mockedAelGrupoExameUsualDAO.obterOriginal(Mockito.anyInt())).thenReturn(aelGrupoExameUsualOld);
			
			systemUnderTest.executarBeforeUpdateGrupoExamesUsuais(aelGrupoExameUsualNew);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExamesUsuaisRNExceptionCode.AEL_00369);
		}
	}
	
}
