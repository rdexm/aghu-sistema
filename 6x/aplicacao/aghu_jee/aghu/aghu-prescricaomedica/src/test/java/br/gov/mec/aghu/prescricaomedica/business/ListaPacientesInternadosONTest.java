package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmAltaMotivo;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.model.MpmSumarioAlta;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Classe responsável pelos testes unitários da classe
 * ListaPacientesInternadosON.<br>
 * Criado com base no teste {@link VerificarPrescricaoONTest}.
 * 
 * @author cvagheti
 * 
 */
public class ListaPacientesInternadosONTest extends AGHUBaseUnitTest<ListaPacientesInternadosON>{
	
	private static final Log log = LogFactory.getLog(ListaPacientesInternadosONTest.class);

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private MpmPrescricaoMedicaDAO mockedMpmPrescricaoMedicaDAO;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private MpmAltaSumarioDAO mockedMpmAltaSumarioDAO;

	@Test
	public void pacienteUnidadeEmergencia() throws ApplicationBusinessException {

		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq((short) 1);
		atendimento.setUnidadeFuncional(unidadeFuncional);

		final AghParametros parametro = new AghParametros();
		parametro.setVlrTexto("S");

		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_BLOQUEIA_PAC_EMERG)).thenReturn(parametro);

		Mockito.when(mockedAghuFacade.possuiCaracteristicaPorUnidadeEConstante(Short.valueOf("1"), 
				ConstanteAghCaractUnidFuncionais.ATEND_EMERG_TERREO)).thenReturn(true);

		try {
			boolean result = this.systemUnderTest
					.pacienteUnidadeEmergencia(atendimento, true);

			Assert.assertTrue(result);

		} catch (ApplicationBusinessException e) {
			Assert.fail();
		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail(e.getClass().toString());
		}

	}

	@Test
	public void pacienteUnidadeEmergenciaNaoBloqueado()
			throws ApplicationBusinessException {

		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq((short) 1);
		atendimento.setUnidadeFuncional(unidadeFuncional);

		final AghParametros parametro = new AghParametros();
		parametro.setVlrTexto("N");

		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_BLOQUEIA_PAC_EMERG)).thenReturn(parametro);
		
		Mockito.when(mockedAghuFacade.possuiCaracteristicaPorUnidadeEConstante(Short.valueOf("1"), ConstanteAghCaractUnidFuncionais.ATEND_EMERG_TERREO)).
		thenReturn(true);

		try {
			boolean result = this.systemUnderTest
					.pacienteUnidadeEmergencia(atendimento, false);

			Assert.assertFalse(result);

		} catch (ApplicationBusinessException e) {
			Assert.fail();
		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail(e.getClass().toString());
		}

	}

	@Test
	public void pacienteDiferenteUnidadeEmergencia()
			throws ApplicationBusinessException {

		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq((short) 1);
		atendimento.setUnidadeFuncional(unidadeFuncional);

		final AghParametros parametro = new AghParametros();
		parametro.setVlrTexto("S");

		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_BLOQUEIA_PAC_EMERG)).thenReturn(parametro);
		
		Mockito.when(mockedAghuFacade.possuiCaracteristicaPorUnidadeEConstante(Short.valueOf("1"), ConstanteAghCaractUnidFuncionais.ATEND_EMERG_TERREO)).
		thenReturn(false);

		try {
			boolean result = this.systemUnderTest
					.pacienteUnidadeEmergencia(atendimento,false);

			Assert.assertFalse(result);

		} catch (ApplicationBusinessException e) {
			Assert.fail();
		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail(e.getClass().toString());
		}

	}

	@Test
	public void motivoAltaObito() throws ApplicationBusinessException {

		final AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq((short) 1);
		atendimento.setUnidadeFuncional(unidadeFuncional);

		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		MpmAltaMotivo altaMotivo = new MpmAltaMotivo();
		altaMotivo.setMotivoAltaMedicas(new MpmMotivoAltaMedica());
		altaMotivo.getMotivoAltaMedicas().setIndObito(true);

		altaSumario.setId(new MpmAltaSumarioId(1, 1, (short) 1));
		altaSumario.setAltaMotivos(altaMotivo);

		Mockito.when(mockedAghuFacade.obterAtendimentoPeloSeq(Mockito.anyInt())).thenReturn(atendimento);
		
		Mockito.when(mockedMpmAltaSumarioDAO.obterAltaSumarios(Mockito.anyInt())).thenReturn(altaSumario);
		
		try {
			boolean result = this.systemUnderTest.isMotivoAltaObito(1);

			Assert.assertTrue(result);

		} catch (ApplicationBusinessException e) {
			Assert.fail();
		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail(e.getClass().toString());
		}

	}

	@Test
	public void existeSumarioAltas() throws ApplicationBusinessException {

		final AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq((short) 1);
		atendimento.setUnidadeFuncional(unidadeFuncional);

		Set<MpmSumarioAlta> lista = new HashSet<MpmSumarioAlta>(0);
		MpmSumarioAlta sumarioAlta = new MpmSumarioAlta();
		sumarioAlta.setMotivoAltaMedica(new MpmMotivoAltaMedica());
		lista.add(sumarioAlta);
		atendimento.setSumariosAlta(lista);

		try {
			boolean result = this.systemUnderTest
					.existeSumarioAltaComAltaMedica(atendimento);

			Assert.assertTrue(result);

		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail(e.getClass().toString());
		}

	}

	@Test
	public void existeSumarioAltasSemAltaMedica() throws ApplicationBusinessException {

		final AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq((short) 1);
		atendimento.setUnidadeFuncional(unidadeFuncional);

		Set<MpmSumarioAlta> lista = new HashSet<MpmSumarioAlta>(0);
		MpmSumarioAlta sumarioAlta = new MpmSumarioAlta();
		lista.add(sumarioAlta);
		atendimento.setSumariosAlta(lista);

		try {
			boolean result = this.systemUnderTest
					.existeSumarioAltaComAltaMedica(atendimento);

			Assert.assertFalse(result);

		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail(e.getClass().toString());
		}

	}

	@Test
	public void existeSumarioAltasComSumarioAltasNull()
			throws ApplicationBusinessException {

		final AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq((short) 1);
		atendimento.setUnidadeFuncional(unidadeFuncional);

		try {
			boolean result = this.systemUnderTest
					.existeSumarioAltaComAltaMedica(atendimento);

			Assert.assertFalse(result);

		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail(e.getClass().toString());
		}

	}

	@Test
	public void existeAltaSumarioConcluido() throws ApplicationBusinessException {

		final AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq((short) 1);
		atendimento.setUnidadeFuncional(unidadeFuncional);

		List<MpmAltaSumario> lista = new ArrayList<MpmAltaSumario>();
		MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setConcluido(DominioIndConcluido.S);
		lista.add(altaSumario);
		atendimento.setAltasSumario(lista);

		try {
			Mockito.when(mockedMpmAltaSumarioDAO.verificarAltaSumariosConcluido(Mockito.anyInt())).thenReturn(true);
			
			boolean result = this.systemUnderTest
					.existeAltaSumarioConcluido(atendimento);

			Assert.assertTrue(result);

		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail(e.getClass().toString());
		}

	}

	@Test
	public void existeAltaSumarioConcluidoSemAltaSumario()
			throws ApplicationBusinessException {

		final AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq((short) 1);
		atendimento.setUnidadeFuncional(unidadeFuncional);

		try {
			boolean result = this.systemUnderTest
					.existeAltaSumarioConcluido(atendimento);

			Assert.assertFalse(result);

		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail();
		}

	}

	@Test
	public void existeAltaSumarioNaoConcluido() throws ApplicationBusinessException {

		final AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq((short) 1);
		atendimento.setUnidadeFuncional(unidadeFuncional);

		List<MpmAltaSumario> lista = new ArrayList<MpmAltaSumario>();
		MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setConcluido(DominioIndConcluido.N);
		lista.add(altaSumario);
		atendimento.setAltasSumario(lista);

		try {
			boolean result = this.systemUnderTest
					.existeAltaSumarioConcluido(atendimento);

			Assert.assertFalse(result);

		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail(e.getClass().toString());
		}

	}

}
