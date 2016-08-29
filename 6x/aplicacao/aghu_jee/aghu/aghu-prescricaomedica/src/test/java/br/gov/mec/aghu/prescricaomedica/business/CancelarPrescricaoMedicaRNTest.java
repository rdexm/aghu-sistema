package br.gov.mec.aghu.prescricaomedica.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.prescricaomedica.business.CancelarPrescricaoMedicaRN.CancelarPrescricaoMedicaRNExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class CancelarPrescricaoMedicaRNTest extends AGHUBaseUnitTest<CancelarPrescricaoMedicaRN>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	@Mock
	private MpmPrescricaoMedicaDAO mockedMpmPrescricaoMedicaDAO;
	@Mock
	private MpmPrescricaoCuidadoDAO mockedMpmPrescricaoCuidadoDAO;
	@Mock
	private MpmPrescricaoDietaDAO mockedMpmPrescricaoDietaDAO;
	@Mock
	private MpmItemPrescricaoDietaDAO mockedMpmItemPrescricaoDietaDAO;
	@Mock
	private MpmPrescricaoMdtoDAO mockedMpmPrescricaoMdtoDAO;
	@Mock
	private MpmSolicitacaoConsultoriaDAO mockedMpmSolicitacaoConsultoriaDAO;
	@Mock
	private MpmPrescricaoNptDAO mockedMpmPrescricaoNptDAO;
	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	
	/**
	 * Testa a validação da situação da prescrição médica que é chamada no início do método
	 * cancelarPrescricao
	 */
	@Test
	public void validarSituacaoPrescricaoMedica() {

		Date dataAtual = new Date();

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		prescricao.setDthrMovimento(dataAtual);
		prescricao.setSituacao(DominioSituacaoPrescricao.L);

		try {
			systemUnderTest.validarSituacaoPrescricao(prescricao);
			Assert.fail("Deveria ter lançado exceção MPM-01256");

		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("Exceção lançada deveria ser a MPM-01256", e
					.getCode(),
					CancelarPrescricaoMedicaRNExceptionCode.MPM_01256);
		}
	}
	
	/**
	 * Testa a validação da data de movimento da prescrição médica que é chamada no início do método
	 * cancelarPrescricao
	 */
	@Test
	public void validarDtMovimentoPrescricaoMedica() {

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		prescricao.setDthrMovimento(null);

		try {
			systemUnderTest.validarDthrMovimentoPrescricao(prescricao);
			Assert.fail("Deveria ter lançado exceção MPM-01257");

		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("Exceção lançada deveria ser a MPM-01257", e
					.getCode(),
					CancelarPrescricaoMedicaRNExceptionCode.MPM_01257);
		}
	}
	
	
	/**
	 * Testa as regras de atualização para quando o indPendente for B
	 *  
	 */
	@Test
	public void testarIndPendenteB(){
		try{
			
			MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
			cuidado.setIndPendente(DominioIndPendenteItemPrescricao.B);
			
			List<MpmPrescricaoCuidado> listaCuidados = new ArrayList<MpmPrescricaoCuidado>();
			listaCuidados.add(cuidado);

			systemUnderTest.cancelarItens(listaCuidados, NOME_MICROCOMPUTADOR);
		}
		catch (BaseException e) {
			Assert.fail();
		}
	}
	
	/*------------------------------------ TESTES PARA ÍTENS COPIADOS------------------------------------*/
	
	/**
	 * Testa as regras de atualização para quando o indPendente for A
	 */
	@Test
	public void testarCopiadoIndPendenteA(){
		try{
			
			MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
			cuidado.setIndPendente(DominioIndPendenteItemPrescricao.A);
			
			MpmPrescricaoMedica prescricaoMedica = new MpmPrescricaoMedica();
			prescricaoMedica.setDthrFim(new Date());
			cuidado.setPrescricaoMedica(prescricaoMedica);
			
			List<MpmPrescricaoCuidado> listaCuidados = new ArrayList<MpmPrescricaoCuidado>();
			listaCuidados.add(cuidado);

			systemUnderTest.cancelarItens(listaCuidados, NOME_MICROCOMPUTADOR);
			Assert.assertEquals(cuidado.getIndPendente(),DominioIndPendenteItemPrescricao.N);
			Assert.assertEquals(cuidado.getAlteradoEm(),null);
			Assert.assertTrue(cuidado.getDthrFim().compareTo(prescricaoMedica.getDthrFim()) == 0);
			Assert.assertEquals(cuidado.getServidorMovimentado(),null);
		}
		catch (BaseException e) {
			Assert.fail();
		}
	}
	
	/**
	 * Testa as regras de atualização para quando o indPendente for E
	 */
	@Test
	public void testarCopiadoIndPendenteE(){
		try{
			MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
			cuidado.setIndPendente(DominioIndPendenteItemPrescricao.E);
			
			MpmPrescricaoMedica prescricaoMedica = new MpmPrescricaoMedica();
			prescricaoMedica.setDthrFim(new Date());
			cuidado.setPrescricaoMedica(prescricaoMedica);
			
			List<MpmPrescricaoCuidado> listaCuidados = new ArrayList<MpmPrescricaoCuidado>();
			listaCuidados.add(cuidado);
	
			systemUnderTest.cancelarItens(listaCuidados, NOME_MICROCOMPUTADOR);
			Assert.assertEquals(cuidado.getIndPendente(),DominioIndPendenteItemPrescricao.N);
			Assert.assertEquals(cuidado.getAlteradoEm(),null);
			Assert.assertTrue(cuidado.getDthrFim().compareTo(prescricaoMedica.getDthrFim()) == 0);
			Assert.assertEquals(cuidado.getServidorMovimentado(),null);
		}
		catch (BaseException e) {
			Assert.fail();
		}
	}
	
	/**
	 * Testa as regras de atualização para quando o indPendente for P
	 */
	@Test
	public void testarCopiadoIndPendenteP(){
		try{
			
			MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
			cuidado.setIndPendente(DominioIndPendenteItemPrescricao.P);
	
			List<MpmPrescricaoCuidado> listaCuidados = new ArrayList<MpmPrescricaoCuidado>();
			listaCuidados.add(cuidado);

			systemUnderTest.cancelarItens(listaCuidados, NOME_MICROCOMPUTADOR);
		}
		catch (BaseException e) {
			Assert.fail();
		}
	}
	
	/**
	 * Testa as regras de atualização para quando o indPendente for R
	 */
	@Test
	public void testarCopiadoIndPendenteR(){
		try{
			
			MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
			cuidado.setIndPendente(DominioIndPendenteItemPrescricao.R);
	
			List<MpmPrescricaoCuidado> listaCuidados = new ArrayList<MpmPrescricaoCuidado>();
			listaCuidados.add(cuidado);

			systemUnderTest.cancelarItens(listaCuidados, NOME_MICROCOMPUTADOR);
		}
		catch (BaseException e) {
			Assert.fail();
		}
	}
	
	/**
	 * Testa as regras de atualização para quando o indPendente for Y
	 */
	@Test
	public void testarCopiadoIndPendenteY(){
		try{
			
			MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
			cuidado.setIndPendente(DominioIndPendenteItemPrescricao.Y);
	
			List<MpmPrescricaoCuidado> listaCuidados = new ArrayList<MpmPrescricaoCuidado>();
			listaCuidados.add(cuidado);

			systemUnderTest.cancelarItens(listaCuidados, NOME_MICROCOMPUTADOR);
		}
		catch (BaseException e) {
			Assert.fail();
		}
	}
	
	/**
	 * Testa a exception que deve ser mostrada no caso de o indPendente for
	 * um valor não esperado
	 */
	@Test
	public void testarCopiadoIndPendenteInvalido() {

		MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
		cuidado.setIndPendente(DominioIndPendenteItemPrescricao.N);

		List<MpmPrescricaoCuidado> listaCuidados = new ArrayList<MpmPrescricaoCuidado>();
		listaCuidados.add(cuidado);

		try {
			systemUnderTest.cancelarItens(listaCuidados, NOME_MICROCOMPUTADOR);
			Assert.fail("Deveria ter lançado exceção IND_PENDENTE_CUIDADO_INVALIDO");

		} catch (BaseException e) {
			Assert.assertEquals("Exceção lançada deveria ser a IND_PENDENTE_CUIDADO_INVALIDO", e
					.getCode(),
					CancelarPrescricaoMedicaRNExceptionCode.IND_PENDENTE_CUIDADO_INVALIDO);
		}

	}
	
	/* ----------------------TESTES PARA ÍTENS NÃO COPIADOS------------------------------- */
	
	
	/**
	 * Testa as regras de atualização para quando o indPendente for B
	 */
	@Test
	public void testarNaoCopiadoIndPendenteB(){
		try{
			
			MpmSolicitacaoConsultoria consultoria = new MpmSolicitacaoConsultoria();
			consultoria.setIndPendente(DominioIndPendenteItemPrescricao.B);
	
			List<MpmSolicitacaoConsultoria> listaConsultorias = new ArrayList<MpmSolicitacaoConsultoria>();
			listaConsultorias.add(consultoria);

			systemUnderTest.cancelarItens(listaConsultorias, NOME_MICROCOMPUTADOR);
		}
		catch (BaseException e) {
			Assert.fail();
		}
	}
	
	
	/**
	 * Testa as regras de atualização para quando o indPendente for A
	 */
	@Test
	public void testarNaoCopiadoIndPendenteA(){
		try{
			
			MpmSolicitacaoConsultoria consultoria = new MpmSolicitacaoConsultoria();
			consultoria.setIndPendente(DominioIndPendenteItemPrescricao.A);
			
			MpmPrescricaoMedica prescricaoMedica = new MpmPrescricaoMedica();
			prescricaoMedica.setDthrFim(new Date());
			consultoria.setPrescricaoMedica(prescricaoMedica);
			
			List<MpmSolicitacaoConsultoria> listaConsultorias = new ArrayList<MpmSolicitacaoConsultoria>();
			listaConsultorias.add(consultoria);
		
			Mockito.when(mockedMpmSolicitacaoConsultoriaDAO.atualizar(Mockito.any(MpmSolicitacaoConsultoria.class))).thenReturn(Mockito.any(MpmSolicitacaoConsultoria.class));

			systemUnderTest.cancelarItens(listaConsultorias, NOME_MICROCOMPUTADOR);
			Assert.assertEquals(consultoria.getIndPendente(),DominioIndPendenteItemPrescricao.N);
			Assert.assertEquals(consultoria.getAlteradoEm(),null);
			Assert.assertEquals(consultoria.getDthrFim(),null);
			Assert.assertEquals(consultoria.getServidorMovimentado(),null);
		}
		catch (BaseException e) {
			Assert.fail();
		}
	}
	
	/**
	 * Testa as regras de atualização para quando o indPendente for E
	 */
	@Test
	public void testarNaoCopiadoIndPendenteE(){
		try{
			
			MpmSolicitacaoConsultoria consultoria = new MpmSolicitacaoConsultoria();
			consultoria.setIndPendente(DominioIndPendenteItemPrescricao.E);
			
			MpmPrescricaoMedica prescricaoMedica = new MpmPrescricaoMedica();
			prescricaoMedica.setDthrFim(new Date());
			consultoria.setPrescricaoMedica(prescricaoMedica);
			
			List<MpmSolicitacaoConsultoria> listaConsultorias = new ArrayList<MpmSolicitacaoConsultoria>();
			listaConsultorias.add(consultoria);
		
			Mockito.when(mockedMpmSolicitacaoConsultoriaDAO.atualizar(Mockito.any(MpmSolicitacaoConsultoria.class))).thenReturn(Mockito.any(MpmSolicitacaoConsultoria.class));
			
			systemUnderTest.cancelarItens(listaConsultorias, NOME_MICROCOMPUTADOR);
			Assert.assertEquals(consultoria.getIndPendente(),DominioIndPendenteItemPrescricao.N);
			Assert.assertEquals(consultoria.getAlteradoEm(),null);
			Assert.assertEquals(consultoria.getDthrFim(),null);
			Assert.assertEquals(consultoria.getServidorMovimentado(),null);
		}
		catch (BaseException e) {
			Assert.fail();
		}
	}
	
	/**
	 * Testa as regras de atualização para quando o indPendente for B
	 */
	@Test
	public void testarNaoCopiadoIndPendenteP(){
		try{
			
			MpmSolicitacaoConsultoria consultoria = new MpmSolicitacaoConsultoria();
			consultoria.setIndPendente(DominioIndPendenteItemPrescricao.P);
	
			List<MpmSolicitacaoConsultoria> listaConsultorias = new ArrayList<MpmSolicitacaoConsultoria>();
			listaConsultorias.add(consultoria);
			systemUnderTest.cancelarItens(listaConsultorias, NOME_MICROCOMPUTADOR);
		}
		catch (BaseException e) {
			Assert.fail();
		}
	}
	
	/* ----------------------------------------------------------------------------- */
	
	/**
	 * Testa o cancelamento da prescrição com atualização
	 */
	@Test
	public void testarCancelarAtualizandoPrescricao(){
		MpmPrescricaoMedica prescricaoMedica = new MpmPrescricaoMedica();

		try {
			prescricaoMedica.setDthrInicioMvtoPendente(new Date());
			prescricaoMedica.setDthrMovimento(new SimpleDateFormat("dd/MM/yyyy").parse("10/11/2010"));
			
			Date dthrMovimentoPendenteAntigo = prescricaoMedica.getDthrInicioMvtoPendente();
			systemUnderTest.cancelarAtualizandoPrescricao(prescricaoMedica);
			
			Assert.assertEquals(prescricaoMedica.getDthrMovimento(),null);
			Assert.assertTrue(prescricaoMedica.getDthrInicioMvtoPendente().compareTo(dthrMovimentoPendenteAntigo) == 0);
			Assert.assertEquals(prescricaoMedica.getSituacao(),DominioSituacaoPrescricao.L);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Assert.fail();
		}
	}
}

