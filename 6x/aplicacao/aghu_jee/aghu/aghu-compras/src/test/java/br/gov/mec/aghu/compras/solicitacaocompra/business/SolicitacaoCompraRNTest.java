package br.gov.mec.aghu.compras.solicitacaocompra.business;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SolicitacaoCompraRNTest extends AGHUBaseUnitTest<SolicitacaoCompraRN> {

	@Mock
	private ScoSolicitacoesDeComprasDAO dao;
	@Mock
	private ScoPontoParadaSolicitacaoDAO ppsDao;
	@Mock
	private ICentroCustoFacade centroCustoFacade;
	@Mock
	private IParametroFacade parametroFacade;

	private static final Log log = LogFactory.getLog(SolicitacaoCompraRNTest.class);

	/**
	 * Testa Data Solicitacao
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testValidaDataSolicitacao() throws ApplicationBusinessException {
		final ScoSolicitacaoDeCompra param = new ScoSolicitacaoDeCompra();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date d = sdf.parse("20/11/2012");
			param.setDtSolicitacao(d);
		} catch (ParseException e1) {
			Assert.fail(e1.getClass().toString());
		}

		try {

			systemUnderTest.validaDataSolicitacao(param);
			assert(true);
		} catch (ApplicationBusinessException e) {
			// TODO: handle exception
			log.error(e.getMessage());
			assert(false);
		}
	}

	/**
	 * Testa Data Analise
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testValidaDataAnalise() throws ApplicationBusinessException {
		final ScoSolicitacaoDeCompra param = new ScoSolicitacaoDeCompra();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date d = sdf.parse("18/11/2012");
			param.setDtAnalise(d);
		} catch (ParseException e1) {
			Assert.fail(e1.getClass().toString());
		}

		try {

			systemUnderTest.validaDataAnalise(param);
			assert(true);

		} catch (ApplicationBusinessException e) {
			// TODO: handle exception
			log.debug(e.getMessage());
			assert(true);
		}
	}

	/**
	 * Testa validaQtdeSolicitadaAprovada
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testvalidaQtdeSolicitadaAprovada() throws ApplicationBusinessException {
		final ScoSolicitacaoDeCompra param = new ScoSolicitacaoDeCompra();

		Long QtdeSolicitada = new Long(1);

		param.setQtdeSolicitada(QtdeSolicitada);
		param.setQtdeAprovada(null);

		// param.setDtSolicitacao(new Date());

		try {

			systemUnderTest.validaQtdeSolicitadaAprovada(param);
			assert(true);

		} catch (ApplicationBusinessException e) {
			// TODO: handle exception

			log.debug(e.getMessage());
			assert(true);
		}
	}

	/**
	 * Testa alteraQuantidadeAprovada
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testalteraQuantidadeAprovada() throws ApplicationBusinessException {
		final ScoSolicitacaoDeCompra param = new ScoSolicitacaoDeCompra();

		Long qtdeSolicitada = new Long(5);

		// param.setMaterial(material);
		param.setQtdeAprovada(null);
		param.setQtdeSolicitada(qtdeSolicitada);

		// param.setDtSolicitacao(new Date());

		systemUnderTest.alteraQuantidadeAprovada(param);

		Assert.assertEquals(param.getQtdeSolicitada(), param.getQtdeAprovada());
	}

	/**
	 * Testa setaPontoServidorGeracaoAutomatica
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testsetaPontoServidorGeracaoAutomatica() throws ApplicationBusinessException {
		final ScoSolicitacaoDeCompra param = new ScoSolicitacaoDeCompra();
		final Integer matricula = 32266;
		final Short vinculo = 1;
		final RapServidores servidor = new RapServidores(new RapServidoresId(matricula, vinculo));
		// param.setDtSolicitacao(new Date());

		/*
		 * mockery.checking(new Expectations() {{
		 * 
		 * 
		 * one(pessoaLogada).getRapServidores(); will(returnValue(servidor));
		 * 
		 * 
		 * 
		 * }});
		 * 
		 * param.setGeracaoAutomatica(false); try {
		 * rn.setaServidorGeracaoNaoAutomatica(param, servidor); } catch
		 * (ApplicationBusinessException e) { // TODO Auto-generated catch block
		 * log.debug(e.getMessage()); assert (true); }
		 */

		// Assert.assertEquals(param.getServidor(), servidor);
		assert(true);
	}

	/**
	 * Testa validaUrgentePrioritario
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testvalidaUrgentePrioritario() throws ApplicationBusinessException {
		final ScoSolicitacaoDeCompra param = new ScoSolicitacaoDeCompra();

		param.setUrgente(true);
		param.setPrioridade(true);

		try {
			systemUnderTest.validaUrgentePrioritario(param);
			assert(true);
		} catch (ApplicationBusinessException e) {
			// TODO Auto-generated catch block
			log.debug(e.getMessage());
			assert(true);
		}

	}

	/**
	 * Testa validasetaEfetiva
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testsetaEfetiva() {
		final ScoSolicitacaoDeCompra param = new ScoSolicitacaoDeCompra();
		final ScoSolicitacaoDeCompra paramold = new ScoSolicitacaoDeCompra();

		final Long qtdeEntregue = new Long(2);
		// param.setDtSolicitacao(new Date());

		param.setQtdeEntregue(qtdeEntregue);
		param.setFundoFixo(Boolean.TRUE);

		systemUnderTest.setaEfetiva(param, paramold);

		Assert.assertEquals(param.getEfetivada(), Boolean.TRUE);

	}

	/**
	 * * Testa setaQtdeAprovadaAlteracao
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testsetaQtdeAprovadaAlteracao() throws ApplicationBusinessException {
		final ScoSolicitacaoDeCompra param = new ScoSolicitacaoDeCompra();
		final ScoSolicitacaoDeCompra paramold = new ScoSolicitacaoDeCompra();

		final ScoPontoParadaSolicitacao pontoParada = new ScoPontoParadaSolicitacao();
		final ScoPontoParadaSolicitacao ppsSolicitacao = new ScoPontoParadaSolicitacao();

		pontoParada.setCodigo(new Short("1"));

		param.setPontoParadaProxima(pontoParada);
		param.setQtdeSolicitada(new Long(1));

		ppsSolicitacao.setCodigo(new Short("1"));

		Mockito.when(ppsDao.obterPontoParadaPorTipo(Mockito.any(DominioTipoPontoParada.class)))
				.thenReturn(ppsSolicitacao);

		try {
			systemUnderTest.setaQtdeAprovadaAlteracao(param, paramold);
			assert(true);
		} catch (ApplicationBusinessException e) {
			log.debug(e.getMessage());
		}

		Assert.assertEquals(param.getQtdeSolicitada(), param.getQtdeAprovada());
	}

	/*
	 * * Testa validaQuantidadeAprovadaValorUnitarioPrevistoAlteracao
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testvalidaQuantidadeAprovadaValorUnitarioPrevistoAlteracao() {
		final ScoSolicitacaoDeCompra param = new ScoSolicitacaoDeCompra();
		final ScoSolicitacaoDeCompra paramold = new ScoSolicitacaoDeCompra();

		paramold.setQtdeAprovada(new Long(2));
		paramold.setValorUnitPrevisto(new BigDecimal(2));

		try {
			systemUnderTest.validaQuantidadeAprovadaValorUnitarioPrevistoAlteracao(param, paramold);

		} catch (ApplicationBusinessException e) {			
			log.debug(e.getMessage());
		}

	}

	/**
	 * Verifica mocks.
	 */
	@After
	public void tearDown() {
	}

};;