package br.gov.mec.aghu.prescricaomedica.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmLaudo;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmLaudoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ConcluirSumarioAltaRNTest extends AGHUBaseUnitTest<ConcluirSumarioAltaRN>{
	
	@Mock
	private AghAtendimentoDAO mockedAghAtendimentoDAO;
	@Mock
	private MpmAltaSumarioDAO mockedMpmAltaSumarioDAO;
	@Mock
	private ManterAltaMotivoRN mockedManterAltaMotivoRN;
	@Mock
	private ManterAltaPlanoRN  mockedManterAltaPlanoRN;
	@Mock
	private ManterAltaEstadoPacienteRN mockedManterAltaEstadoPacienteRN;
	@Mock
	private ManterAltaEvolucaoRN mockedManterAltaEvolucaoRN;
	@Mock
	private ManterAltaDiagMtvoInternacaoRN mockedManterAltaDiagMtvoInternacaoRN;
	@Mock
	private ManterAltaDiagPrincipalRN mockedManterAltaDiagPrincipalRN;
	@Mock
	private ConfirmarPrescricaoMedicaRN mockedConfirmarPrescricaoMedicaRN;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private MpmLaudoDAO mockedMpmLaudoDAO;
	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;
	@Mock
	private IExamesLaudosFacade mockedExamesLaudosFacade;
	
		
	/**
	 * Nao tem tipo setado, retorna IllegalArgumentException.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testarVerificarDataHoraAlta001() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		
		systemUnderTest.verificarDataHoraAlta(altaSumario);
				
	}
	
	/**
	 * Nao tem id setado, retorna IllegalArgumentException.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testarVerificarDataHoraAlta002() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
	
		
		systemUnderTest.verificarDataHoraAlta(altaSumario);	
	}
	
	/**
	 * O parametro eh nulo, retorna IllegalArgumentException.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testarVerificarDataHoraAlta003() {
		
		systemUnderTest.verificarDataHoraAlta(null);	
	}
	
	/**
	 * Possue data de alta.
	 * Tipo ALTA.
	 * NAO deve retornar erro.
	 */
	@Test
	public void testarVerificarDataHoraAlta005() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		altaSumario.setDthrAlta(new Date());
		altaSumario.setTipo(DominioIndTipoAltaSumarios.ALT);
		
		Mockito.when(mockedMpmAltaSumarioDAO.obterAltaSumarioPeloId(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(altaSumario);
    	
    	ApplicationBusinessException e = systemUnderTest.verificarDataHoraAlta(altaSumario);
		
		Assert.assertTrue(e == null);
	}
	
	/**
	 * Data de Alta NAO informada.
	 * Tipo ALTA.
	 * DEVE retornar erro.
	 */
	@Test
	public void testarVerificarDataHoraAlta006() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		altaSumario.setDthrAlta(null);
		altaSumario.setTipo(DominioIndTipoAltaSumarios.ALT);
		
		Mockito.when(mockedMpmAltaSumarioDAO.obterAltaSumarioPeloId(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(altaSumario);
    	ApplicationBusinessException e = systemUnderTest.verificarDataHoraAlta(altaSumario);
		
		Assert.assertTrue(e != null);		
	}
	
	/**
	 * Data de Alta NAO informada.
	 * Tipo OBTO.
	 * DEVE retornar erro. 
	 */
	@Test
	public void testarVerificarDataHoraAlta007() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		altaSumario.setDthrAlta(null);
		altaSumario.setTipo(DominioIndTipoAltaSumarios.OBT);

		Mockito.when(mockedMpmAltaSumarioDAO.obterAltaSumarioPeloId(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(altaSumario);
		
    	ApplicationBusinessException e = systemUnderTest.verificarDataHoraAlta(altaSumario);
		
		Assert.assertTrue(e != null);		
	}
	
	/**
	 * Data de Alta NAO informada.
	 * Tipo diferente de ALTA e OBTO.
	 * DEVE retornar erro.
	 */
	@Test
	public void testarVerificarDataHoraAlta008() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		altaSumario.setDthrAlta(null);
		altaSumario.setTipo(DominioIndTipoAltaSumarios.TRF);

		Mockito.when(mockedMpmAltaSumarioDAO.obterAltaSumarioPeloId(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(altaSumario);
    	ApplicationBusinessException e = systemUnderTest.verificarDataHoraAlta(altaSumario);
		
		Assert.assertTrue(e != null);		
	}
	
	
	//******************************************VALIDAR MOTIVO ALTA PACIENTE*************************************************//
	/**
	 * Valida se existe pelo menos um motivo 
	 * associado ao sumario de alta.
	 */
	@Test
	public void testarValidarMotivoAltaPaciente04(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		
		Mockito.when(mockedManterAltaMotivoRN.validarMotivoAltaPaciente(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.TRUE);

		ApplicationBusinessException e = systemUnderTest.validarMotivoAltaPaciente(altaSumario.getId());
		
		Assert.assertTrue(e == null);
	}
	
	/**
	 * Valida se não existe motivo 
	 * associado ao sumario de alta.
	 */
	@Test
	public void testarValidarMotivoAltaPaciente05(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());

		Mockito.when(mockedManterAltaMotivoRN.validarMotivoAltaPaciente(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.FALSE);
		
		
		ApplicationBusinessException e = systemUnderTest.validarMotivoAltaPaciente(altaSumario.getId());
		
		Assert.assertTrue(e != null);
	}
		
	/**
	 * Valida se o parâmetro 
	 * informado é válido para 
	 * o motivo de alta.
	 * 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testarValidarMotivoAltaPaciente07(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		
		Mockito.when(mockedManterAltaMotivoRN.validarMotivoAltaPaciente(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.TRUE);		
		
		ApplicationBusinessException e = systemUnderTest.validarMotivoAltaPaciente(altaSumario.getId());
		
		Assert.assertTrue(e == null);
	}
	//FIM ******************************************VALIDAR MOTIVO ALTA PACIENTE*************************************************//
	
	
	//******************************************VALIDAR ALTA PLANO*************************************************//
	/**
	 * Valida se existe 
	 * plano da alta do paciente.
	 */
	@Test
	public void testarValidarAltaPlano08() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		
		Mockito.when(mockedManterAltaPlanoRN.validarAltaPlano(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.TRUE);		
		
		ApplicationBusinessException e = systemUnderTest.validarAltaPlano(altaSumario.getId());
		
		Assert.assertTrue(e == null);
	}
	
	
	/**
	 * Valida se não existe 
	 * plano da alta do paciente.
	 */
	@Test
	public void testarValidarAltaPlano09(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		
		Mockito.when(mockedManterAltaPlanoRN.validarAltaPlano(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.FALSE);		

		ApplicationBusinessException e = systemUnderTest.validarAltaPlano(altaSumario.getId());
		
		Assert.assertTrue(e != null);
	}
	
	
	/**
	 * Valida se o parâmetro 
	 * informado é válido para o 
	 * plano da alta do paciente.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testarValidarAltaPlano10() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		
		Mockito.when(mockedManterAltaPlanoRN.validarAltaPlano(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.TRUE);		

		ApplicationBusinessException e = systemUnderTest.validarAltaPlano(altaSumario.getId());
		
		Assert.assertTrue(e == null);
	}
	
	//FIM ******************************************VALIDAR ALTA PLANO*************************************************//
	
	
	//******************************************VALIDAR ALTA ESTADO PACIENTE*************************************************//
		
	/**
	 * Valida se existe o estado clínico do paciente.
	 */
	@Test
	public void testarValidarAltaEstadoPaciente11(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		
		Mockito.when(mockedManterAltaEstadoPacienteRN.validarAltaEstadoPaciente(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.TRUE);		

		ApplicationBusinessException e = systemUnderTest.validarAltaEstadoPaciente(altaSumario.getId());
		
		Assert.assertTrue(e == null);
	}
	
	/**
	 * Valida se não existe o estado clínico do paciente.
	 */
	@Test
	public void testarValidarAltaEstadoPaciente12(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		
		Mockito.when(mockedManterAltaEstadoPacienteRN.validarAltaEstadoPaciente(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.FALSE);		

		ApplicationBusinessException e = systemUnderTest.validarAltaEstadoPaciente(altaSumario.getId());
		
		Assert.assertTrue(e != null);
	}
	
	
	/**
	 * Valida se parâmetro informado é válido 
	 * para o estado clínico do paciente.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testarValidarAltaEstadoPaciente13(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		
		Mockito.when(mockedManterAltaEstadoPacienteRN.validarAltaEstadoPaciente(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.TRUE);		
		
		ApplicationBusinessException e = systemUnderTest.validarAltaEstadoPaciente(altaSumario.getId());
		
		Assert.assertTrue(e == null);
	}
	
	//FIM ******************************************VALIDAR ALTA ESTADO PACIENTE*************************************************//
	
	//******************************************VALIDAR ALTA EVOLUÇÃO*************************************************//
	/**
	 * Valida se existe evolução da alta do paciente.
	 */
	@Test
	public void testarValidarAltaEvolucao14(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		
		Mockito.when(mockedManterAltaEvolucaoRN.validarAltaEvolucao(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.TRUE);		

		ApplicationBusinessException e = systemUnderTest.validarAltaEvolucao(altaSumario.getId());
		
		Assert.assertTrue(e == null);
	}
	
	/**
	 * Valida se não existe evolução da alta do paciente.
	 */
	@Test
	public void testarValidarAltaEvolucao15(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		
		Mockito.when(mockedManterAltaEvolucaoRN.validarAltaEvolucao(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.FALSE);		

		ApplicationBusinessException e = systemUnderTest.validarAltaEvolucao(altaSumario.getId());
		
		Assert.assertTrue(e != null);
	}
	
	/**
	 * Valida se o parâmetro informado é válido 
     * para a evolução da alta do paciente.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testarValidarAltaEvolucao16(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		
		Mockito.when(mockedManterAltaEvolucaoRN.validarAltaEvolucao(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.TRUE);		

		ApplicationBusinessException e = systemUnderTest.validarAltaEvolucao(altaSumario.getId());
		
		Assert.assertTrue(e == null);
	}
	
	//FIM ******************************************VALIDAR ALTA ALTA EVOLUÇÃO*************************************************//
	
	
	// ******************************************VALIDAR DIAGNÓSTICO DO MTVO*************************************************//
	
	/**
	 * Valida se existe diagnóstico de
	 * mtvo da internação da alta do paciente.
	 */
	@Test
	public void testarValidarAltaDiagMtvoInternacao17(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		
		Mockito.when(mockedManterAltaDiagMtvoInternacaoRN.validarAltaDiagMtvoInternacao(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.TRUE);		

		ApplicationBusinessException e = systemUnderTest.validarAltaDiagMtvoInternacao(altaSumario.getId());
		
		Assert.assertTrue(e == null);
	}
	
	/**
	 * Valida se não existe diagnóstico de
	 * mtvo da internação da alta do paciente.
	 */
	@Test
	public void testarValidarAltaDiagMtvoInternacao18(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		
		Mockito.when(mockedManterAltaDiagMtvoInternacaoRN.validarAltaDiagMtvoInternacao(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.FALSE);		
		
		ApplicationBusinessException e = systemUnderTest.validarAltaDiagMtvoInternacao(altaSumario.getId());
		
		Assert.assertTrue(e != null);
	}
	
	/**
	 * Valida se o parâmetro informado é válido 
	 * para o diagnóstico de mtvo da internação 
	 * da alta do paciente.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testarValidarAltaDiagMtvoInternacao19(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		
		Mockito.when(mockedManterAltaDiagMtvoInternacaoRN.validarAltaDiagMtvoInternacao(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.TRUE);		

		ApplicationBusinessException e = systemUnderTest.validarAltaDiagMtvoInternacao(altaSumario.getId());
		
		Assert.assertTrue(e == null);
	}	
		
	//FIM ******************************************VALIDAR DIAGNÓSTICO DO MTVO*************************************************//
	
	//******************************************VALIDAR DIAGNÓSTICO PRINCIPAL*************************************************//
	/**
	 * Valida se existe diagnóstico principal
	 * da alta do paciente. 
	 */
	@Test
	public void testarValidarAltaDiagPrincipal20(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		
		Mockito.when(mockedManterAltaDiagPrincipalRN.validarAltaDiagPrincipal(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.TRUE);		

		ApplicationBusinessException e = systemUnderTest.validarAltaDiagPrincipal(altaSumario.getId());
		
		Assert.assertTrue(e == null);
	}
	
	/**
	 * Valida se não existe diagnóstico principal
	 * da alta do paciente. 
	 */
	@Test
	public void testarValidarAltaDiagPrincipal21(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setId(new MpmAltaSumarioId());
		
		Mockito.when(mockedManterAltaDiagPrincipalRN.validarAltaDiagPrincipal(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.FALSE);		

		ApplicationBusinessException e = systemUnderTest.validarAltaDiagPrincipal(altaSumario.getId());
		
		Assert.assertTrue(e != null);
	}
	
	/**
	 * Valida se o parâmetro informado é válido
	 * para diagnóstico principal da alta do paciente. 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testarValidarAltaDiagPrincipal22(){
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		
		Mockito.when(mockedManterAltaDiagPrincipalRN.validarAltaDiagPrincipal(Mockito.any(MpmAltaSumarioId.class))).thenReturn(Boolean.TRUE);		

		ApplicationBusinessException e = systemUnderTest.validarAltaDiagPrincipal(altaSumario.getId());
		
		Assert.assertTrue(e == null);
	}
	
	//FIM ******************************************VALIDAR DIAGNÓSTICO PRINCIPAL*************************************************//
	
	/**
	 * Teste Parametro invalido pra objeto null.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testarEhConvenioSUS001() {
		
		systemUnderTest.ehConvenioSUS(null);
		
	}
	
	/**
	 * Teste Parametro invalido pra id do objeto null.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testarEhConvenioSUS002() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		
		systemUnderTest.ehConvenioSUS(altaSumario);
		
	}
	
	/**
	 * Teste para Grupo Convenio S.
	 */
	@Test
	public void testarEhConvenioSUS003() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setAtendimento(new AghAtendimentos());
		
		final FatConvenioSaude convenioSaude = new FatConvenioSaude ();
		convenioSaude.setGrupoConvenio(DominioGrupoConvenio.S);
		
		final FatConvenioSaudePlano convenio = new FatConvenioSaudePlano ();
		convenio.setConvenioSaude(convenioSaude);
		
		Mockito.when(mockedConfirmarPrescricaoMedicaRN.obterConvenioAtendimento(Mockito.any(AghAtendimentos.class))).thenReturn(convenio);

		boolean value = systemUnderTest.ehConvenioSUS(altaSumario);
		
		Assert.assertTrue(value);
	}

	/**
	 * Teste para Grupo Convenio diferente de S.
	 */
	@Test
	public void testarEhConvenioSUS004() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setAtendimento(new AghAtendimentos());
		
		final FatConvenioSaude convenioSaude = new FatConvenioSaude ();
		convenioSaude.setGrupoConvenio(DominioGrupoConvenio.P);
		
		final FatConvenioSaudePlano convenio = new FatConvenioSaudePlano ();
		convenio.setConvenioSaude(convenioSaude);
		
		Mockito.when(mockedConfirmarPrescricaoMedicaRN.obterConvenioAtendimento(Mockito.any(AghAtendimentos.class))).thenReturn(convenio);
		
		boolean value = systemUnderTest.ehConvenioSUS(altaSumario);
		
		Assert.assertTrue(value == false);
	}

	/**
	 * Teste para Grupo Convenio diferente de S.
	 */
	@Test
	public void testarEhConvenioSUS005() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setAtendimento(new AghAtendimentos());
		
		final FatConvenioSaude convenioSaude = new FatConvenioSaude ();
		convenioSaude.setGrupoConvenio(DominioGrupoConvenio.C);
		
		final FatConvenioSaudePlano convenio = new FatConvenioSaudePlano ();
		convenio.setConvenioSaude(convenioSaude);
		
		Mockito.when(mockedConfirmarPrescricaoMedicaRN.obterConvenioAtendimento(Mockito.any(AghAtendimentos.class))).thenReturn(convenio);
		
		boolean value = systemUnderTest.ehConvenioSUS(altaSumario);
		
		Assert.assertTrue(value == false);
	}
	
	
	/**
	 * Teste Parametro invalido pra objeto null.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testarExisteLaudoMenorPermanenciaPendenteJustificativa001() {
		
		try {
			systemUnderTest.existeLaudoMenorPermanenciaPendenteJustificativa(null);
		} catch (ApplicationBusinessException e) {
			Assert.assertFalse(true);
		}
	}
	
	/**
	 * Teste Parametro invalido pra id do objeto null.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testarExisteLaudoMenorPermanenciaPendenteJustificativa002() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		
		try {
			systemUnderTest.existeLaudoMenorPermanenciaPendenteJustificativa(altaSumario);
		} catch (ApplicationBusinessException e) {
			Assert.assertFalse(true);
		}
	}
	
	/**
	 * Testa retorno null para buscarAghParametro
	 * Busca de buscaLaudoMenorPermaneciaPendenteJustificativa retorno null.
	 * 
	 */
	@Test
	public void testarExisteLaudoMenorPermanenciaPendenteJustificativa003() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setAtendimento(new AghAtendimentos());
		
		try {
			//final AghParametros parametro = new AghParametros();
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(null);
		} catch (ApplicationBusinessException e1) {
			Assert.assertFalse("Erro ao criar mock do metodo buscarAghParametro.", true);
		}
		
		final List<MpmLaudo> list = null;
		Mockito.when(mockedMpmLaudoDAO.buscaLaudoMenorPermaneciaPendenteJustificativa(Mockito.any(AghAtendimentos.class), Mockito.anyShort())).thenReturn(list);
		
		try {
			boolean value = systemUnderTest.existeLaudoMenorPermanenciaPendenteJustificativa(altaSumario);
			Assert.assertTrue(value == false);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertFalse(true);
		}
	}

	/**
	 * Testa retorno null para buscarAghParametro.
	 * Busca de buscaLaudoMenorPermaneciaPendenteJustificativa retorno lista vazia.
	 * 
	 */
	@Test
	public void testarExisteLaudoMenorPermanenciaPendenteJustificativa004() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setAtendimento(new AghAtendimentos());
		
		try {
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(null);
		} catch (ApplicationBusinessException e1) {
			Assert.assertFalse("Erro ao criar mock do metodo buscarAghParametro.", true);
		}
		
		final List<MpmLaudo> list = new LinkedList<MpmLaudo>();
		Mockito.when(mockedMpmLaudoDAO.buscaLaudoMenorPermaneciaPendenteJustificativa(Mockito.any(AghAtendimentos.class), Mockito.anyShort())).thenReturn(list);
		
		try {
			boolean value = systemUnderTest.existeLaudoMenorPermanenciaPendenteJustificativa(altaSumario);
			Assert.assertTrue(value == false);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertFalse(true);
		}
	}
	
	/**
	 * Testa retorno null para buscarAghParametro.
	 * Busca de buscaLaudoMenorPermaneciaPendenteJustificativa retorno lista com objetos.
	 * 
	 */
	@Test
	public void testarExisteLaudoMenorPermanenciaPendenteJustificativa005() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setAtendimento(new AghAtendimentos());
		
		try {
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(null);
		} catch (ApplicationBusinessException e1) {
			Assert.assertFalse("Erro ao criar mock do metodo buscarAghParametro.", true);
		}
		
		final List<MpmLaudo> list = new LinkedList<MpmLaudo>();
		list.add(new MpmLaudo());
		Mockito.when(mockedMpmLaudoDAO.buscaLaudoMenorPermaneciaPendenteJustificativa(Mockito.any(AghAtendimentos.class), Mockito.anyShort())).thenReturn(list);
		
		try {
			boolean value = systemUnderTest.existeLaudoMenorPermanenciaPendenteJustificativa(altaSumario);
			Assert.assertTrue(value);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertFalse(true);
		}
	}
	
	/**
	 * Testa retorno NAO null para buscarAghParametro. Mas com valor null.
	 * Busca de buscaLaudoMenorPermaneciaPendenteJustificativa retorno lista com objetos.
	 * 
	 */
	@Test
	public void testarExisteLaudoMenorPermanenciaPendenteJustificativa006() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setAtendimento(new AghAtendimentos());
		
		try {
			final AghParametros parametro = new AghParametros();
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
			} catch (ApplicationBusinessException e1) {
			Assert.assertFalse("Erro ao criar mock do metodo buscarAghParametro.", true);
		}
		
		final List<MpmLaudo> list = new LinkedList<MpmLaudo>();
		list.add(new MpmLaudo());
		Mockito.when(mockedMpmLaudoDAO.buscaLaudoMenorPermaneciaPendenteJustificativa(Mockito.any(AghAtendimentos.class), Mockito.anyShort())).thenReturn(list);
		
		try {
			boolean value = systemUnderTest.existeLaudoMenorPermanenciaPendenteJustificativa(altaSumario);
			Assert.assertTrue(value);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertFalse(true);
		}
		
	}

	/**
	 * Testa retorno NAO null para buscarAghParametro. E valor NAO null.
	 * Busca de buscaLaudoMenorPermaneciaPendenteJustificativa retorno lista com objetos.
	 * 
	 */
	@Test
	public void testarExisteLaudoMenorPermanenciaPendenteJustificativa007() {
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setAtendimento(new AghAtendimentos());
		
		try {
			final AghParametros parametro = new AghParametros();
			parametro.setVlrNumerico(new BigDecimal("12"));
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
		} catch (ApplicationBusinessException e1) {
			Assert.assertFalse("Erro ao criar mock do metodo buscarAghParametro.", true);
		}
		
		final List<MpmLaudo> list = new LinkedList<MpmLaudo>();
		list.add(new MpmLaudo());
		Mockito.when(mockedMpmLaudoDAO.buscaLaudoMenorPermaneciaPendenteJustificativa(Mockito.any(AghAtendimentos.class), Mockito.anyShort())).thenReturn(list);
		
		try {
			boolean value = systemUnderTest.existeLaudoMenorPermanenciaPendenteJustificativa(altaSumario);
			Assert.assertTrue(value);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertFalse(true);
		}
		
	}

	/**
	 * Verifica se o hospital
	 * tem ambulatório ou não.
	 */
	@Test
	public void testarExisteAmbulatorio001(){
				
		try {
			final AghParametros parametro = new AghParametros();
			parametro.setVlrTexto("S");
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
		} catch (BaseException e1) {
			Assert.assertFalse("Erro ao criar mock do metodo buscarAghParametro.", true);
		}
		
		try {
			boolean value = systemUnderTest.existeAmbulatorio();
			Assert.assertTrue(value);
			
		} catch (BaseException e) {
			Assert.assertFalse(true);
		}
	}
	
	/**
	 * Verifica se o hospital
	 * tem ambulatório ou não.
	 */
	@Test
	public void testarExisteAmbulatorio002(){
				
		try {
			final AghParametros parametro = new AghParametros();
			parametro.setVlrTexto("N");
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
		} catch (BaseException e1) {
			Assert.assertFalse("Erro ao criar mock do metodo buscarAghParametro.", true);
		}
		
		try {
			boolean value = systemUnderTest.existeAmbulatorio();
			Assert.assertFalse(value);
			
		} catch (BaseException e) {
			Assert.assertFalse(true);
		}
	}
	
	/**
	 * Parametro Null, retorna IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testarExisteProcedimentosComLaudoJustificativaParaImpressao001() {
		try {
			systemUnderTest.existeProcedimentosComLaudoJustificativaParaImpressao(null);
		} catch (BaseException e) {
			Assert.assertFalse(true);
		}
	}
	
	/**
	 * Parametro AghAtendimentos valido e seq Null, retorna IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testarExisteProcedimentosComLaudoJustificativaParaImpressao002() {
		try {
			AghAtendimentos atendimento = new AghAtendimentos();
			systemUnderTest.existeProcedimentosComLaudoJustificativaParaImpressao(atendimento);
		} catch (BaseException e) {
			Assert.assertFalse(true);
		}
	}

	/**
	 * Parametro Valido.
	 * buscaProcedimentosComLaudoJustificativaParaImpressao retornando vazia.
	 * pesquisarSolicitacaoExamePorAtendimento retornando vazia.
	 * Deve retornar false.
	 */
	@Test
	public void testarExisteProcedimentosComLaudoJustificativaParaImpressao003() {
		try {
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setSeq(1);
			
			doMockTresBuscaParametroSistema();
			
			//Implementação do Mock - retornando lista vazia.
			final List<FatProcedHospInternos> list = new LinkedList<FatProcedHospInternos>();
			Mockito.when(mockedFaturamentoFacade.buscaProcedimentosComLaudoJustificativaParaImpressao(Mockito.any(AghAtendimentos.class))).thenReturn(list);
			
			//Implementação do Mock - retornando lista vazia.
			final List<AelSolicitacaoExames> listSoe = new LinkedList<AelSolicitacaoExames>();
			Mockito.when(mockedExamesLaudosFacade.pesquisarSolicitacaoExamePorAtendimento(Mockito.anyInt(), Mockito.anyList())).thenReturn(listSoe);
			boolean temDados = systemUnderTest.existeProcedimentosComLaudoJustificativaParaImpressao(atendimento);
			
			Assert.assertTrue(temDados == false);
		} catch (BaseException e) {
			Assert.assertFalse(true);
		}
	}
	
	/**
	 * Cria expectativas para busca de Parametro de Sistema.
	 */
	private void doMockTresBuscaParametroSistema() {
		try {
			final AghParametros parametro = new AghParametros();
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
		} catch (BaseException e1) {
			Assert.assertFalse("Erro ao criar mock do metodo buscarAghParametro.", true);
		}
	}

	/**
	 * Parametro Valido.
	 * buscaProcedimentosComLaudoJustificativaParaImpressao retornando null.
	 * pesquisarSolicitacaoExamePorAtendimento retornando vazia.
	 * Deve retornar false.
	 */
	@Test
	public void testarExisteProcedimentosComLaudoJustificativaParaImpressao004() {
		try {
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setSeq(1);
			
			doMockTresBuscaParametroSistema();
			
			//Implementação do Mock - retornando lista vazia.
			final List<FatProcedHospInternos> list = null;
			Mockito.when(mockedFaturamentoFacade.buscaProcedimentosComLaudoJustificativaParaImpressao(Mockito.any(AghAtendimentos.class))).thenReturn(list);

			//Implementação do Mock - retornando lista vazia.
			final List<AelSolicitacaoExames> listSoe = new LinkedList<AelSolicitacaoExames>();
			Mockito.when(mockedExamesLaudosFacade.pesquisarSolicitacaoExamePorAtendimento(Mockito.anyInt(), Mockito.anyList())).thenReturn(listSoe);

			boolean temDados = systemUnderTest.existeProcedimentosComLaudoJustificativaParaImpressao(atendimento);
			
			Assert.assertTrue(temDados == false);
		} catch (BaseException e) {
			Assert.assertFalse(true);
		}
	}
	
	/**
	 * Parametro Valido.
	 * buscaProcedimentosComLaudoJustificativaParaImpressao retornando vazia.
	 * pesquisarSolicitacaoExamePorAtendimento retornando null.
	 * Deve retornar false.
	 */
	@Test
	public void testarExisteProcedimentosComLaudoJustificativaParaImpressao005() {
		try {
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setSeq(1);
			
			doMockTresBuscaParametroSistema();
			
			//Implementação do Mock - retornando lista vazia.
			final List<FatProcedHospInternos> list = new LinkedList<FatProcedHospInternos>();
			Mockito.when(mockedFaturamentoFacade.buscaProcedimentosComLaudoJustificativaParaImpressao(Mockito.any(AghAtendimentos.class))).thenReturn(list);

			//Implementação do Mock - retornando lista vazia.
			final List<AelSolicitacaoExames> listSoe = null;
			Mockito.when(mockedExamesLaudosFacade.pesquisarSolicitacaoExamePorAtendimento(Mockito.anyInt(), Mockito.anyList())).thenReturn(listSoe);
			
			boolean temDados = systemUnderTest.existeProcedimentosComLaudoJustificativaParaImpressao(atendimento);
			
			Assert.assertTrue(temDados == false);
		} catch (BaseException e) {
			Assert.assertFalse(true);
		}
	}
	
	/**
	 * Parametro Valido.
	 * buscaProcedimentosComLaudoJustificativaParaImpressao retornando null.
	 * pesquisarSolicitacaoExamePorAtendimento retornando null.
	 * Deve retornar false.
	 */
	@Test
	public void testarExisteProcedimentosComLaudoJustificativaParaImpressao006() {
		try {
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setSeq(1);
			
			doMockTresBuscaParametroSistema();
			
			//Implementação do Mock - retornando lista vazia.
			final List<FatProcedHospInternos> list = null;
			Mockito.when(mockedFaturamentoFacade.buscaProcedimentosComLaudoJustificativaParaImpressao(Mockito.any(AghAtendimentos.class))).thenReturn(list);
			
			//Implementação do Mock - retornando lista vazia.
			final List<AelSolicitacaoExames> listSoe = null;
			Mockito.when(mockedExamesLaudosFacade.pesquisarSolicitacaoExamePorAtendimento(Mockito.anyInt(), Mockito.anyList())).thenReturn(listSoe);
			
			boolean temDados = systemUnderTest.existeProcedimentosComLaudoJustificativaParaImpressao(atendimento);
			
			Assert.assertTrue(temDados == false);
		} catch (BaseException e) {
			Assert.assertFalse(true);
		}
	}
	
	/**
	 * Parametro Valido.
	 * buscaProcedimentosComLaudoJustificativaParaImpressao retornando valores.
	 * pesquisarSolicitacaoExamePorAtendimento retornando vazia.
	 * Deve retornar true.
	 */
	@Test
	public void testarExisteProcedimentosComLaudoJustificativaParaImpressao007() {
		try {
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setSeq(1);
			
			doMockTresBuscaParametroSistema();
			
			//Implementação do Mock - retornando lista vazia.
			final List<FatProcedHospInternos> list = new LinkedList<FatProcedHospInternos>();
			list.add(new FatProcedHospInternos());
			Mockito.when(mockedFaturamentoFacade.buscaProcedimentosComLaudoJustificativaParaImpressao(Mockito.any(AghAtendimentos.class))).thenReturn(list);
			
			//Implementação do Mock - retornando lista vazia.
			final List<AelSolicitacaoExames> listSoe = new LinkedList<AelSolicitacaoExames>();
			Mockito.when(mockedExamesLaudosFacade.pesquisarSolicitacaoExamePorAtendimento(Mockito.anyInt(), Mockito.anyList())).thenReturn(listSoe);
			
			boolean temDados = systemUnderTest.existeProcedimentosComLaudoJustificativaParaImpressao(atendimento);
			
			Assert.assertTrue(temDados == true);
		} catch (BaseException e) {
			Assert.assertFalse(true);
		}
	}
	
	/**
	 * Parametro Valido.<br>
	 * buscaProcedimentosComLaudoJustificativaParaImpressao retornando vazia.<br>
	 * pesquisarSolicitacaoExamePorAtendimento retornando valores.<br>
	 * buscaLaudoOrigemPaciente retornando valor nao esperado.<br>
	 * Deve retornar true.
	 * 
	 */
	@Test
	public void testarExisteProcedimentosComLaudoJustificativaParaImpressao008() {
		try {
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setSeq(1);
			
			doMockTresBuscaParametroSistema();
			
			//Implementação do Mock - retornando lista vazia.
			final List<FatProcedHospInternos> list = new LinkedList<FatProcedHospInternos>();
			Mockito.when(mockedFaturamentoFacade.buscaProcedimentosComLaudoJustificativaParaImpressao(Mockito.any(AghAtendimentos.class))).thenReturn(list);
			
			//Implementação do Mock - retornando lista com um elemento.
			final List<AelSolicitacaoExames> listSoe = new LinkedList<AelSolicitacaoExames>();
			listSoe.add(new AelSolicitacaoExames());
			Mockito.when(mockedExamesLaudosFacade.pesquisarSolicitacaoExamePorAtendimento(Mockito.anyInt(), Mockito.anyList())).thenReturn(listSoe);
			
			//Implementação do Mock - retornando valor nao esperado.
			final DominioOrigemAtendimento dominio = DominioOrigemAtendimento.A;
			Mockito.when(mockedExamesLaudosFacade.buscaLaudoOrigemPacienteRN(Mockito.anyInt())).thenReturn(dominio);
			
			boolean temDados = systemUnderTest.existeProcedimentosComLaudoJustificativaParaImpressao(atendimento);
			
			Assert.assertTrue(temDados == false);
		} catch (BaseException e) {
			Assert.assertFalse(true);
		}
	}

	/**
	 * Parametro Valido.<br>
	 * buscaProcedimentosComLaudoJustificativaParaImpressao retornando vazia.<br>
	 * pesquisarSolicitacaoExamePorAtendimento retornando valores.<br>
	 * buscaLaudoOrigemPaciente retornando valor esperado.<br>
	 * buscaItemSolicitacaoExamesComRespostaQuestao retornando vazia.<br>
	 * Deve retornar false.
	 * 
	 */
	@Test
	public void testarExisteProcedimentosComLaudoJustificativaParaImpressao009() {
		try {
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setSeq(1);
			
			doMockTresBuscaParametroSistema();
			
			//Implementação do Mock - retornando lista vazia.
			final List<FatProcedHospInternos> list = new LinkedList<FatProcedHospInternos>();
			Mockito.when(mockedFaturamentoFacade.buscaProcedimentosComLaudoJustificativaParaImpressao(Mockito.any(AghAtendimentos.class))).thenReturn(list);
			
			//Implementação do Mock - retornando lista com um elemento.
			final List<AelSolicitacaoExames> listSoe = new LinkedList<AelSolicitacaoExames>();
			listSoe.add(new AelSolicitacaoExames());
			Mockito.when(mockedExamesLaudosFacade.pesquisarSolicitacaoExamePorAtendimento(Mockito.anyInt(), Mockito.anyList())).thenReturn(listSoe);
			
			//Implementação do Mock - retornando valor esperado.
			final DominioOrigemAtendimento dominio = DominioOrigemAtendimento.I;
			Mockito.when(mockedExamesLaudosFacade.buscaLaudoOrigemPacienteRN(Mockito.anyInt())).thenReturn(dominio);

			//Implementação do Mock - retornando lista vazia.
			final List<AelItemSolicitacaoExames> iseList = new LinkedList<AelItemSolicitacaoExames>();
			Mockito.when(mockedExamesLaudosFacade.buscaItemSolicitacaoExamesComRespostaQuestao(Mockito.anyList())).thenReturn(iseList);
			
			boolean temDados = systemUnderTest.existeProcedimentosComLaudoJustificativaParaImpressao(atendimento);
			
			Assert.assertTrue(temDados == false);
		} catch (BaseException e) {
			Assert.assertFalse(true);
		}
	}
	
	/**
	 * Parametro Valido.<br>
	 * buscaProcedimentosComLaudoJustificativaParaImpressao retornando vazia.<br>
	 * pesquisarSolicitacaoExamePorAtendimento retornando valores.<br>
	 * buscaLaudoOrigemPaciente retornando valor esperado.<br>
	 * buscaItemSolicitacaoExamesComRespostaQuestao retornando valores.<br>
	 * Deve retornar true.
	 * 
	 */
	@Test
	public void testarExisteProcedimentosComLaudoJustificativaParaImpressao010() {
		try {
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setSeq(1);
			
			doMockTresBuscaParametroSistema();
			
			//Implementação do Mock - retornando lista vazia.
			final List<FatProcedHospInternos> list = new LinkedList<FatProcedHospInternos>();
			Mockito.when(mockedFaturamentoFacade.buscaProcedimentosComLaudoJustificativaParaImpressao(Mockito.any(AghAtendimentos.class))).thenReturn(list);
			
			//Implementação do Mock - retornando lista com um elemento.
			final List<AelSolicitacaoExames> listSoe = new LinkedList<AelSolicitacaoExames>();
			listSoe.add(new AelSolicitacaoExames());
			Mockito.when(mockedExamesLaudosFacade.pesquisarSolicitacaoExamePorAtendimento(Mockito.anyInt(), Mockito.anyList())).thenReturn(listSoe);
			
			//Implementação do Mock - retornando valor esperado.
			final DominioOrigemAtendimento dominio = DominioOrigemAtendimento.I;
			Mockito.when(mockedExamesLaudosFacade.buscaLaudoOrigemPacienteRN(Mockito.anyInt())).thenReturn(dominio);

			//Implementação do Mock - retornando lista com um elemento.
			final List<AelItemSolicitacaoExames> iseList = new LinkedList<AelItemSolicitacaoExames>();
			iseList.add(new AelItemSolicitacaoExames());
			Mockito.when(mockedExamesLaudosFacade.buscaItemSolicitacaoExamesComRespostaQuestao(Mockito.anyList())).thenReturn(iseList);
			
			boolean temDados = systemUnderTest.existeProcedimentosComLaudoJustificativaParaImpressao(atendimento);
			
			Assert.assertTrue(temDados == true);
		} catch (BaseException e) {
			Assert.assertFalse(true);
		}
	}
	
}
