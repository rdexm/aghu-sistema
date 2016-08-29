package br.gov.mec.aghu.exames.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemHorarioAgendadoId;
import br.gov.mec.aghu.model.AelTipoMarcacaoExame;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

public class AelHorarioExameDispRNTest extends AGHUBaseUnitTest<AelHorarioExameDispRN>{

	@Mock
	private IAghuFacade mockedAghuFacade;
	
	/**
	 * Passa as datas de criado em diferentes
	 * logo deve subir a exceção.
	 * @throws BaseException
	 */
	@Test(expected=BaseException.class)
	public void verificarAlteracaoCamposCriadoEmOuServidorCriadoEmDiferenteTest() throws BaseException {
		
		AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		horarioExameDisp.setCriadoEm(new Date());
		
		AelHorarioExameDisp horarioExameDispOriginal = new AelHorarioExameDisp();
		horarioExameDispOriginal.setCriadoEm(DateUtil.adicionaDias(new Date(), 2));
		
		systemUnderTest.verificarAlteracaoCamposCriadoEmOuServidor(horarioExameDisp, horarioExameDispOriginal);
		
	}
	
	/**
	 * Passa sevidores diferentes
	 * logo deve subir a exceção.
	 * @throws BaseException
	 */
	@Test(expected=BaseException.class)
	public void verificarAlteracaoCamposCriadoEmOuServidorDiferenteTest() throws BaseException {
		
		RapServidoresId servidorId1 = new RapServidoresId();
		servidorId1.setMatricula(Integer.valueOf("1"));
		servidorId1.setVinCodigo(Short.valueOf("1"));
		
		RapServidoresId servidorId2 = new RapServidoresId();
		servidorId2.setMatricula(Integer.valueOf("2"));
		servidorId2.setVinCodigo(Short.valueOf("2"));
		
		RapServidores servidor1 = new RapServidores();
		servidor1.setId(servidorId1);
		
		RapServidores servidor2 = new RapServidores();
		servidor2.setId(servidorId2);
		
		AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		horarioExameDisp.setServidor(servidor1);
		horarioExameDisp.setCriadoEm(new Date());
		
		AelHorarioExameDisp horarioExameDispOriginal = new AelHorarioExameDisp();
		horarioExameDispOriginal.setServidor(servidor2);
		horarioExameDispOriginal.setCriadoEm(new Date());
		
		systemUnderTest.verificarAlteracaoCamposCriadoEmOuServidor(horarioExameDisp, horarioExameDispOriginal);
		
	}
	
	/**
	 * Passa grade inativa.
	 * @throws BaseException
	 */
	@Test(expected=BaseException.class)
	public void verificarGradeInativoTest() throws BaseException {
		
		AelGradeAgendaExame gradeAgendaExame = new AelGradeAgendaExame();
		gradeAgendaExame.setSituacao(DominioSituacao.I);
		
		
		AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		horarioExameDisp.setGradeAgendaExame(gradeAgendaExame);
		
 		systemUnderTest.verificarGrade(horarioExameDisp);
		
	}
	
	/**
	 * Passa tipo marcação inativo.
	 * @throws BaseException
	 */
	@Test(expected=BaseException.class)
	public void verificarTipoMarcacaoInativoTest() throws BaseException {
		
		AelTipoMarcacaoExame tipoMarcacaoExame = new AelTipoMarcacaoExame();
		tipoMarcacaoExame.setIndSituacao(DominioSituacao.I);
		
		
		AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		horarioExameDisp.setTipoMarcacaoExame(tipoMarcacaoExame);
		
 		systemUnderTest.verificarTipoMarcacao(horarioExameDisp);
		
	}
	
	/**
	 * Passa com lista vazia e situação horário M.
	 * @throws BaseException
	 */
	@Test
	public void verificarSituacaoHorarioMListaVaziaTest() throws BaseException {
		
		final Set<AelItemHorarioAgendado> setItemHorarioAgendado = new HashSet<AelItemHorarioAgendado>(0);
		
		AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		horarioExameDisp.setSituacaoHorario(DominioSituacaoHorario.M);
		horarioExameDisp.setItemHorarioAgendados(setItemHorarioAgendado);
	
		try {

			systemUnderTest.verificarSituacao(horarioExameDisp);
			fail("Deve subir exceção de código AEL_00522");
			
		} catch (BaseException e) {

			assertEquals("AEL_00522", e.getMessage());

		}
 		
	}
	
	/**
	 * Passa com lista vazia e situação horário E.
	 * @throws BaseException
	 */
	@Test
	public void verificarSituacaoHorarioEListaVaziaTest() throws BaseException {
		
		final Set<AelItemHorarioAgendado> setItemHorarioAgendado = new HashSet<AelItemHorarioAgendado>(0);
		
		AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		horarioExameDisp.setSituacaoHorario(DominioSituacaoHorario.E);
		horarioExameDisp.setItemHorarioAgendados(setItemHorarioAgendado);

		try {
			
			systemUnderTest.verificarSituacao(horarioExameDisp);
			fail("Deve subir exceção de código AEL_00522");
			
		} catch (BaseException e) {

			assertEquals("AEL_00522", e.getMessage());

		}
 		
	}
	
	/**
	 * Passa com lista com mais de 1 objeto e situação horário G.
	 * @throws BaseException
	 */
	@Test
	public void verificarSituacaoHorarioGListaComObjetoTest() throws BaseException {
		
		AelItemHorarioAgendadoId itemHorarioAgendadoId = new AelItemHorarioAgendadoId();
		
		AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(itemHorarioAgendadoId);
		
		final Set<AelItemHorarioAgendado> setItemHorarioAgendado = new HashSet<AelItemHorarioAgendado>();
		setItemHorarioAgendado.add(itemHorarioAgendado);
		setItemHorarioAgendado.add(new AelItemHorarioAgendado());
		
		AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		horarioExameDisp.setSituacaoHorario(DominioSituacaoHorario.G);
		horarioExameDisp.setItemHorarioAgendados(setItemHorarioAgendado);
		
		try {
			
			systemUnderTest.verificarSituacao(horarioExameDisp);
			fail("Deve subir exceção de código AEL_00523");
			
		} catch (BaseException e) {

			assertEquals("AEL_00523", e.getMessage());

		}
 		
	}
	
	/**
	 * Passa com lista com mais de 1 objeto e situação horário L.
	 * @throws BaseException
	 */
	@Test
	public void verificarSituacaoHorarioLListaComObjetoTest() throws BaseException {
		
		AelItemHorarioAgendadoId itemHorarioAgendadoId = new AelItemHorarioAgendadoId();
		
		AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(itemHorarioAgendadoId);
		
		final Set<AelItemHorarioAgendado> setItemHorarioAgendado = new HashSet<AelItemHorarioAgendado>();
		setItemHorarioAgendado.add(itemHorarioAgendado);
		setItemHorarioAgendado.add(new AelItemHorarioAgendado());
		
		AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		horarioExameDisp.setSituacaoHorario(DominioSituacaoHorario.L);
		horarioExameDisp.setItemHorarioAgendados(setItemHorarioAgendado);		
		
		try {
			
			systemUnderTest.verificarSituacao(horarioExameDisp);
			fail("Deve subir exceção de código AEL_00523");
			
		} catch (BaseException e) {

			assertEquals("AEL_00523", e.getMessage());

		}
 		
	}
	
	/**
	 * Passa com lista com mais de 1 objeto e situação horário B.
	 * @throws BaseException
	 */
	@Test
	public void verificarSituacaoHorarioBListaComObjetoTest() throws BaseException {
		
		AelItemHorarioAgendadoId itemHorarioAgendadoId = new AelItemHorarioAgendadoId();
		
		AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(itemHorarioAgendadoId);
		
		final Set<AelItemHorarioAgendado> setItemHorarioAgendado = new HashSet<AelItemHorarioAgendado>();
		setItemHorarioAgendado.add(itemHorarioAgendado);
		setItemHorarioAgendado.add(new AelItemHorarioAgendado());
		
		AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		horarioExameDisp.setSituacaoHorario(DominioSituacaoHorario.B);
		horarioExameDisp.setItemHorarioAgendados(setItemHorarioAgendado);
		
		try {
			
			systemUnderTest.verificarSituacao(horarioExameDisp);
			fail("Deve subir exceção de código AEL_00523");
			
		} catch (BaseException e) {

			assertEquals("AEL_00523", e.getMessage());

		}
 		
	}
	
	/**
	 * Deve atualizar data de Alterado EM
	 * paora data corrente da aplicação.
	 * @throws BaseException
	 */
	@Test
	public void atualizarServidorEAlteradoEmTest() throws BaseException {
		
		AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		
		systemUnderTest.atualizarServidorEAlteradoEm(horarioExameDisp);
		assertEquals(DateUtil.truncaData(new Date()), DateUtil.truncaData(horarioExameDisp.getAlteradoEm()));
 		
	}
	
	

}
