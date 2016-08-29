/**
 * 
 */
package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoEquivalentesMedicamento;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoEquivalenteJnDAO;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalente;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalenteId;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalenteJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * @author ehgsilva
 *
 */
public class MedicamentoEquivalenteRNTest extends AGHUBaseUnitTest<MedicamentoEquivalenteRN>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
	
	//Daos e Facades a serem mockadas
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IFarmaciaApoioFacade mockedFarmaciaApoioFacade;
	@Mock
	private BaseJournalFactory mockedBaseJournalFactory;
	@Mock
	private AfaMedicamentoEquivalenteJnDAO mockedAfaMedicamentoEquivalenteJnDAO;
	@Mock
	private AfaMedicamentoEquivalenteJn mockedAfaMedicamentoEquivalenteJn;
	@Mock
	private IServidorLogadoFacade mockedServidorLogadoFacade;
	
	@Test
	public void verificarSetDtaCriacao(){
		AfaMedicamentoEquivalente entidade = new AfaMedicamentoEquivalente();
		systemUnderTest.setDtaCriacao(entidade);
		Assert.assertNotNull(entidade.getCriadoEm());
	}
	
	@Test
	public void validaBriPreInsercaoRow(){
		AfaMedicamentoEquivalente entidade = new AfaMedicamentoEquivalente();
		entidade.setRapServidores(new RapServidores());
		entidade.getRapServidores().setServidor(new RapServidores());
		try {
			systemUnderTest.briPreInsercaoRow(entidade, NOME_MICROCOMPUTADOR, new Date());
//			mockingContext.assertIsSatisfied();
		} catch (BaseException e) {
			Assert.assertFalse("Não deveria ter lançado exceção, pois o RapServidor não é nulo", true);
		}
	}
	
	@Test
	public void validaBriPreInsercaoRowException(){
		AfaMedicamentoEquivalente entidade = new AfaMedicamentoEquivalente();
		entidade.setRapServidores(new RapServidores());
		try {
			systemUnderTest.briPreInsercaoRow(entidade, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertFalse("Deveria ter lançado exceção AFA_00169, pois o RapServidor é nulo, ", true);
		} catch (BaseException e) {
//			mockingContext.assertIsSatisfied();
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void validaBruPreAtualizacaoRow(){
		AfaMedicamentoEquivalente entidade = new AfaMedicamentoEquivalente();
		entidade.setRapServidores(new RapServidores());
		entidade.getRapServidores().setServidor(new RapServidores());
		try {
			systemUnderTest.bruPreAtualizacaoRow(entidade, null, NOME_MICROCOMPUTADOR, new Date());
//			mockingContext.assertIsSatisfied();
		} catch (BaseException e) {
			Assert.assertFalse("Não deveria ter lançado exceção, pois o RapServidor não é nulo", true);
		}
	}
	
	@Test
	public void validaBruPreAtualizacaoRowException(){
		AfaMedicamentoEquivalente entidade = new AfaMedicamentoEquivalente();
		entidade.setRapServidores(new RapServidores());
		try {
			systemUnderTest.bruPreAtualizacaoRow(entidade, null, NOME_MICROCOMPUTADOR, new Date());
			Assert.assertFalse("Deveria ter lançado exceção AFA_00169, pois o RapServidor é nulo, ", true);
		} catch (BaseException e) {
//			mockingContext.assertIsSatisfied();
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void validaServidorNulo(){
		RapServidores servidor = new RapServidores();
		if(!systemUnderTest.validarServidorNulo(servidor)) {
//			mockingContext.assertIsSatisfied();
			Assert.assertTrue(true);
		} else {
			Assert.assertFalse("O retorno deveria ser true, pois o RapServidor não é nulo", true);
		}
	}
	
	@Test
	public void validaServidorNuloException(){
		RapServidores servidor = null;
		if(systemUnderTest.validarServidorNulo(servidor)) {
//			mockingContext.assertIsSatisfied();
			Assert.assertTrue(true);
		} else {
			Assert.assertFalse("O retorno deveria ser false, pois o RapServidor é nulo, ", true);
		}
	}
	
	@Test
	public void validaAruPosAtualizacaoRow_comAlteracao(){
		obterMedicamentoEquivalentePorChavePrimaria();
		
		RapServidores servidorLogado = new RapServidores();
		RapServidoresId id = new RapServidoresId(100200, Short.valueOf("955"));
		servidorLogado.setId(id);
		servidorLogado.setUsuario("AGHU");
		
		AfaMedicamentoEquivalente original = mockedFarmaciaApoioFacade.obterMedicamentoEquivalentePorChavePrimaria(1, 2);
		original.setRapServidores(servidorLogado);
		
		AfaMedicamentoEquivalente modificada = getAfaMedicamentoEquivalenteAlterado();
		try {
			Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(servidorLogado);
			
			Boolean houveAlteracao = systemUnderTest.aruPosAtualizacaoRow(original, modificada, NOME_MICROCOMPUTADOR, new Date());
			if(houveAlteracao) {
//				mockingContext.assertIsSatisfied();
				Assert.assertTrue(true);
			} else {
				Assert.assertFalse("A classe foi alterada e não detectou alteração", true);
			}
		} catch (BaseException e) {
			Assert.fail("Não deveria lançar exceção");
		}
	}

	//Expectations para farmaciaFacade
	private void obterMedicamentoEquivalentePorChavePrimaria(){

		AfaMedicamentoEquivalente entidade = new AfaMedicamentoEquivalente();
		
		Calendar c = Calendar.getInstance();
		c.set(2011,  4,  11);
		entidade.setCriadoEm(c.getTime());
//		entidade.setCriadoEm(DateUtil.obterData(2011, 05, 11));
		
		entidade.setId(new AfaMedicamentoEquivalenteId());
		entidade.setIndSituacao(DominioSituacao.A);
		entidade.setRapServidores(new RapServidores());
		entidade.setTipo(DominioTipoEquivalentesMedicamento.E);
			
		Mockito.when(mockedFarmaciaApoioFacade.obterMedicamentoEquivalentePorChavePrimaria(Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(entidade);
	}
	
	//Retorna AfaMedicamentoEquivalente
	private AfaMedicamentoEquivalente getAfaMedicamentoEquivalenteAlterado(){
		AfaMedicamentoEquivalente entidade = new AfaMedicamentoEquivalente();
		
		Calendar c = Calendar.getInstance();
		c.set(2011,  4,  11);
		entidade.setCriadoEm(c.getTime());
//		entidade.setCriadoEm(DateUtil.obterData(2011, 05, 11));
		entidade.setId(new AfaMedicamentoEquivalenteId());
		entidade.setIndSituacao(DominioSituacao.I); //Alterado
		entidade.setRapServidores(new RapServidores());
		entidade.setTipo(DominioTipoEquivalentesMedicamento.S); //Alterado
		return entidade;
	}
}