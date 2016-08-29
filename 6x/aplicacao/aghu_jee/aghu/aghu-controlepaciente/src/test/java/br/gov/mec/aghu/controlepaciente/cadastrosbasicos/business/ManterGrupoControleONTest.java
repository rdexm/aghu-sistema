package br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.controlepaciente.dao.EcpGrupoControleDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EcpGrupoControle;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Classe responsável pelos testes unitários da estória #6462 - Manter Grupos de
 * Controle.
 * 
 * @author ptneto
 * 
 */
public class ManterGrupoControleONTest extends AGHUBaseUnitTest<ManterGrupoControleON>{
	
	private static final Log log = LogFactory.getLog(ManterGrupoControleONTest.class);
	
	@Mock
	private EcpGrupoControleDAO mockedEcpGrupoControleDAO;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	@Test
	public void inserirOkTest() {

		try {
			EcpGrupoControle grupo = new EcpGrupoControle();

			grupo.setSeq(1);
			grupo.setDescricao("Grupo de Teste Unitário");
			grupo.setOrdem((short) 3);
			grupo.setSituacao(DominioSituacao.A);
			
			whenObterServidorLogado();
			
			Mockito.when(mockedEcpGrupoControleDAO.obterDescricao(Mockito.anyString())).thenReturn(grupo);
			Mockito.when(mockedEcpGrupoControleDAO.obterOrdem(Mockito.anyShort())).thenReturn(grupo);

			systemUnderTest.inserir(grupo);
//			Assert.fail("Grupo Existente");
		} catch (BaseException e) {
			log.error(e.getMessage());
		}
	}
	
	@Test
	public void incluirNuloTest() throws BaseException {

		try {
			whenObterServidorLogado();
			
			systemUnderTest.inserir(null);

			Assert.fail("Falha ao incluir - grupo nulo");
		} catch (IllegalArgumentException e) {

			log.error(e.getMessage());
		} catch (ApplicationBusinessException exp) {

			Assert.fail(exp.getMessage());
			exp.getStackTrace();
		}

	}
		
	@Test
	public void excluirOkTest() {

		try {
			EcpGrupoControle grupo = new EcpGrupoControle();

			grupo.setSeq(1);
			grupo.setDescricao("Grupo de Teste Unitário");
			grupo.setOrdem((short) 3);
			grupo.setSituacao(DominioSituacao.A);

			List<EcpItemControle> itens = new ArrayList<EcpItemControle>(0); 
			
			whenObterServidorLogado();
			
			Mockito.when(mockedEcpGrupoControleDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(grupo);
			Mockito.when(mockedEcpGrupoControleDAO.listarItensGrupoControle(Mockito.any(EcpGrupoControle.class))).thenReturn(itens);

			systemUnderTest.excluir(grupo.getSeq());

		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.fail("TESTE FALHOU!");
		}

	}
	
	@Test
	public void excluirNuloTest() {

		try {
			systemUnderTest.excluir(null);

			Assert.fail("Falha ao excluir - grupo nulo");
		} catch (IllegalArgumentException e) {

			log.error(e.getMessage());
		} catch (ApplicationBusinessException exp) {

			Assert.fail(exp.getMessage());
			exp.getStackTrace();
		}
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
