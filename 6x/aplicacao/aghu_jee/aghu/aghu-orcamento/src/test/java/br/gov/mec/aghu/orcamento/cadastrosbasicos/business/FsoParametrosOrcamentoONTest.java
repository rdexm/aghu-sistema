package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioAcaoParametrosOrcamento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.FsoParametrosOrcamentoON.ExceptionCode;
import br.gov.mec.aghu.orcamento.dao.FsoParametrosOrcamentoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Teste unitário da classe FsoGrupoNaturezaDespesaON.
 * 
 * @author mlcruz
 */
public class FsoParametrosOrcamentoONTest extends AGHUBaseUnitTest<FsoParametrosOrcamentoON>{
	@Mock
	private FsoParametrosOrcamentoDAO dao;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

    @Before
    public void inicio() throws BaseException {
    	whenObterServidorLogado();
    }
	
	/**
	 * Testa inclusão de parâmetros de orçamento.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testIncluir() throws ApplicationBusinessException {
		final FsoParametrosOrcamento param = new FsoParametrosOrcamento();
		
		param.setAcaoGnd(DominioAcaoParametrosOrcamento.S);
		param.setGrupoNaturezaDespesa(new FsoGrupoNaturezaDespesa());
		param.setIndSituacao(DominioSituacao.A);
		
		final FsoParametrosOrcamento naoConflitante = new FsoParametrosOrcamento();
		
		naoConflitante.setAcaoGnd(DominioAcaoParametrosOrcamento.R);
		naoConflitante.setGrupoNaturezaDespesa(new FsoGrupoNaturezaDespesa());

		Mockito.when(dao.pesquisarParametrosOrcamento(param)).thenReturn(Arrays.asList(naoConflitante));
		
		systemUnderTest.incluir(param);
	}
	
	/**
	 * Testa alteração de parâmeetros de orçamento.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testAlterar() throws ApplicationBusinessException {
		final FsoParametrosOrcamento param = new FsoParametrosOrcamento();
		
		param.setAcaoCct(DominioAcaoParametrosOrcamento.R);
		param.setCentroCustoReferencia(new FccCentroCustos());
		
		final RapServidores servidorInclusao = new RapServidores();
		param.setServidorInclusao(servidorInclusao);

		param.setIndSituacao(DominioSituacao.A);
		
		Mockito.when(dao.pesquisarParametrosOrcamento(param)).thenReturn(Collections.EMPTY_LIST);
		
		systemUnderTest.alterar(param);
	}
	
	/**
	 * Testa inclusão de parâmetros de orçamento sem uma ação definida (RN01).
	 * @throws ApplicationBusinessException 
	 */
	//@Test(expected = ApplicationBusinessException.class)	
	@Test
	public void testIncluirSemAcao() throws ApplicationBusinessException {
		FsoParametrosOrcamento param = new FsoParametrosOrcamento();
		
		param.setTpProcesso(DominioTipoSolicitacao.SS);
		
		try {			
			systemUnderTest.incluir(param);
			
			Assert.fail("Deveria ter gerado uma exception");
		} catch (ApplicationBusinessException e) {
			
			Assert.assertEquals(
					ExceptionCode.ERRO_ACAO_PARAMETRO_ORCAMENTO_OBRIGATORIA_SS,
					e.getCode());
		}
		
		param.setTpProcesso(DominioTipoSolicitacao.SC);
		
		try {			
			systemUnderTest.incluir(param);
			
			Assert.fail("Deveria ter gerado uma exception");
		} catch (ApplicationBusinessException e) {
			
			Assert.assertEquals(
					ExceptionCode.ERRO_ACAO_PARAMETRO_ORCAMENTO_OBRIGATORIA_SC,
					e.getCode());
		}
		
		
		//on.incluir(param);
	}
	
	/**
	 * Testa alteração de parâmeetros de orçamento com regras conflitantes.
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void testAlterarComConflito() throws ApplicationBusinessException {
		final FsoParametrosOrcamento param = new FsoParametrosOrcamento();
		
		param.setAcaoCct(DominioAcaoParametrosOrcamento.O);
		param.setCentroCustoReferencia(new FccCentroCustos());
		
		final RapServidores servidorInclusao = new RapServidores();
		param.setServidorInclusao(servidorInclusao);
		
		param.setIndSituacao(DominioSituacao.A);
		
		final FsoParametrosOrcamento conflitante = new FsoParametrosOrcamento();
		conflitante.setAcaoCct(DominioAcaoParametrosOrcamento.R);
		conflitante.setCentroCustoReferencia(new FccCentroCustos());
		
		Mockito.when(dao.pesquisarParametrosOrcamento(param)).thenReturn(Arrays.asList(conflitante));
		
		systemUnderTest.alterar(param);
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
