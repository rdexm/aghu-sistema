package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Calendar;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterPrescricaoDietaRNTest extends AGHUBaseUnitTest<ManterPrescricaoDietaRN>{
	
	private static final Log log = LogFactory.getLog(ManterPrescricaoDietaRNTest.class);

	@Test
	public void sobreposicaoDatas() {

		Calendar cal = Calendar.getInstance();
		
		Calendar dtInicio = Calendar.getInstance();
		dtInicio.setTime(cal.getTime());
		
		Calendar dtFim = Calendar.getInstance();
		dtFim.setTime(cal.getTime());
		
		Calendar dtInicioOld = Calendar.getInstance();
		dtInicioOld.setTime(cal.getTime());
		
		Calendar dtFimOld = Calendar.getInstance();
		dtFimOld.setTime(cal.getTime());
		
		
		boolean result;

		dtInicio.set(2010, 5, 6, 10, 00, 00);
		dtFim.set(2010, 5, 10, 10, 00, 00);
		dtInicioOld.set(2010, 5, 5, 10, 00, 00);
		
		/*
		 * dthr_inicio MENOR que a nova data de inicio ou a nova de
		 * fim e que a dthr_fim existente é nula, ou seja, ainda está em vigor 
		 */		
		try {
			result = systemUnderTest.verificarSobreposicaoDatas(
					dtInicio.getTime(), dtFim.getTime(), dtInicioOld.getTime(),
					null);
			Assert.assertTrue(result);
		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail();
		}
		
		/*
		 * Data de início gravada igual a nova data de início e data de início
		 * gravada menor que nova data de fim
		 */
		dtInicioOld.set(2010, 5, 5, 10, 00, 00);
		dtFimOld.set(2010, 5, 10, 10, 00, 00);
		dtInicio.set(2010, 5, 5, 10, 00, 00);
		dtFim.set(2010, 5, 10, 10, 00, 00);

		try {
			result = systemUnderTest.verificarSobreposicaoDatas(
					dtInicio.getTime(), dtFim.getTime(), dtInicioOld.getTime(),
					dtFimOld.getTime());
			Assert.assertTrue(result);
		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail();
		}

		/*
		 * Data de fim gravada maior que a nova data de início e data de fim
		 * gravada igual nova data de fim
		 */		
		dtInicioOld.set(2010, 5, 4, 10, 00, 00);
		dtFimOld.set(2010, 5, 10, 10, 00, 00);
		dtInicio.set(2010, 5, 5, 10, 00, 00);
		dtFim.set(2010, 5, 10, 10, 00, 00);

		try {
			result = systemUnderTest.verificarSobreposicaoDatas(
					dtInicio.getTime(), dtFim.getTime(), dtInicioOld.getTime(),
					dtFimOld.getTime());
			Assert.assertTrue(result);
		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail();
		}
		
		/*
		 * Data de fim gravada igual a nova data de início e data de fim
		 * gravada maior que nova data de fim
		 */		
		dtInicioOld.set(2010, 5, 4, 10, 00, 00);
		dtFimOld.set(2010, 5, 10, 10, 00, 00);
		dtInicio.set(2010, 5, 10, 10, 00, 00);
		dtFim.set(2010, 5, 10, 9, 00, 00);
		

		try {
			result = systemUnderTest.verificarSobreposicaoDatas(
					dtInicio.getTime(), dtFim.getTime(), dtInicioOld.getTime(),
					dtFimOld.getTime());
			Assert.assertTrue(result);
		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail();
		}
		
		/*
		 * Data de fim gravada igual a nova data de início e data de fim
		 * gravada maior que nova data de fim
		 */				
		dtInicioOld.set(2010, 5, 4, 10, 00, 00);
		dtFimOld.set(2010, 5, 11, 10, 00, 00);
		dtInicio.set(2010, 5, 10, 10, 00, 00);
		dtFim.set(2010, 5, 10, 9, 00, 00);
		
		try {
			result = systemUnderTest.verificarSobreposicaoDatas(
					dtInicio.getTime(), null, dtInicioOld.getTime(),
					dtFimOld.getTime());
			Assert.assertTrue(result);
		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail();
		}

		/*
		 * Data de fim gravada igual a nova data de início e data de fim
		 * gravada maior que nova data de fim
		 */				
		dtInicioOld.set(2010, 5, 4, 10, 00, 00);
		dtFimOld.set(2010, 5, 11, 10, 00, 00);
		dtInicio.set(2010, 5, 10, 10, 00, 00);
		dtFim.set(2010, 5, 10, 9, 00, 00);
		
		try {
			result = systemUnderTest.verificarSobreposicaoDatas(
					dtInicio.getTime(), null, dtInicioOld.getTime(),
					null);
			Assert.assertTrue(result);
		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail();
		}
		
			/*
			AghAtendimentos atendimento = new AghAtendimentos();
			AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
			unidadeFuncional.setSeq((short) 1);
			atendimento.setUnidadeFuncional(unidadeFuncional);

			final AghParametros parametro = new AghParametros();
			parametro.setVlrTexto("S");

			mockingContext.checking(new Expectations() {
				{
					allowing(mockedAghuFacade).buscarAghParametro(
							with(AghuParametrosEnum.P_BLOQUEIA_PAC_EMERG));
					will(returnValue(parametro));
				}
			});

			mockingContext.checking(new Expectations() {
				{
					allowing(mockedAghCaractUnidFuncionaisDAO)
							.verificarCaracteristicaDaUnidadeFuncional(
									with(any(Short.class)),
									with(ConstanteAghCaractUnidFuncionais.ATEND_EMERG_TERREO));
					will(returnValue(DominioSimNao.S));
				}
			});

			try {
				boolean result = this.systemUnderTest
						.pacienteUnidadeEmergencia(atendimento);

				Assert.assertTrue(result);

			} catch (AGHUNegocioException e) {
				Assert.fail();
			} catch (Exception e) {
				log.error(e.getMessage());
				Assert.fail(e.getClass().toString());
			}

			// TODO: prepara dados para chamada do método a ser testado e
			// expectations
			// MpmItemModeloBasicoDieta itemDieta = new MpmItemModeloBasicoDieta();
			// final MpmModeloBasicoDieta modeloBasicoDieta = new
			// MpmModeloBasicoDieta(
			// new MpmModeloBasicoDietaId(1, 1));
			// final AnuTipoItemDieta tipoItemDieta = new AnuTipoItemDieta(1);
			// final MpmModeloBasicoPrescricao modeloBasico = new
			// MpmModeloBasicoPrescricao(
			// 1);
			//
			// modeloBasicoDieta.setModeloBasicoPrescricao(modeloBasico);
			// itemDieta.setModeloBasicoDieta(modeloBasicoDieta);
			// itemDieta.setTipoItemDieta(tipoItemDieta);

			// try {
			// //TODO: invoca método a ser testado
			// //systemUnderTest.
			//
			// // TODO: asserts, verificações de resultado
			// // Assert.assertNotNull(itemDieta.getServidor());
			// // Assert.assertNotNull(itemDieta.getCriadoEm());
			//
			// } catch (AGHUNegocioException e) {
			// log.error(e.getMessage());
			// Assert.fail(e.getMessage());
			// }
			 * 
			 */
		}			
	
}

