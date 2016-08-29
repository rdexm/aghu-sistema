package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamControlesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamLogEmUsosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoRealizadoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituariosDAO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamControles;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamNotaAdicionalAnamneses;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.MamProcedimentoRealizado;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class CancelamentoAtendimentoRNTest extends AGHUBaseUnitTest<CancelamentoAtendimentoRN>{

    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	@Mock
	private MamLogEmUsosDAO mockedMamLogEmUsosDAO;
	@Mock
	private MamAnamnesesDAO mockedMamAnamnesesDAO;
	@Mock
	private MamNotaAdicionalAnamnesesDAO mockedMamNotaAdicionalAnamnesesDAO;
	@Mock
	private MamEvolucoesDAO mockedMamEvolucoesDAO;
	@Mock
	private MamNotaAdicionalEvolucoesDAO mockedMamNotaAdicionalEvolucoesDAO;
	@Mock
	private MamProcedimentoRealizadoDAO mockedMamProcedimentoRealizadoDAO;
	@Mock
	private MamReceituariosDAO mockedMamReceituariosDAO;
	@Mock
	private MamControlesDAO mockedMamControlesDAO;

	@Test
	public void testObterDataUltimaModificacao() throws BaseException {
		whenObterServidorLogado();
		
		Mockito.when(mockedMamLogEmUsosDAO.obterUltimaDataPorConsultaEServidor(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort()))
		.thenReturn(new Date());
			
		final Integer conNumero = Integer.valueOf(0);
		final RapServidores servidor = new RapServidores();
		RapServidoresId id = new RapServidoresId();
		servidor.setId(id);
		try {
			//TODO Toni Wickert arrumar a logica desta metodo.
			systemUnderTest.obterDataUltimoMovimentacao(conNumero, servidor);
			//Assert.fail();
		} catch (ApplicationBusinessException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void pendenciaAnamnese() {

				List<MamAnamneses> listaAnamnese = new ArrayList<MamAnamneses>();
				MamAnamneses anamnese1 = new MamAnamneses();
				MamAnamneses anamnese2 = new MamAnamneses();
				anamnese2.setSeq(1L);
				anamnese1.setAnamnese(anamnese2);
				listaAnamnese.add(anamnese1);
				
				
		Mockito.when(mockedMamAnamnesesDAO.pesquisarAnamnesePorNumeroSeqEPendencia(Mockito.anyInt(), Mockito.anyLong(), Mockito.any(DominioIndPendenteAmbulatorio.class)))
		.thenReturn(listaAnamnese);
				
		Mockito.when(mockedMamAnamnesesDAO.obterPorChavePrimaria(Mockito.anyLong()))
		.thenReturn(anamnese1);
				
		Mockito.when(mockedMamAnamnesesDAO.pesquisarAnamnesePorAnaSeq(Mockito.anyLong()))
		.thenReturn(listaAnamnese);
		
		try {
			systemUnderTest.pendenciaAnamnese(1, 1L);
		} catch (Exception e) {
			Assert.fail("Exceção gerada. " + e.getMessage());
		}
	}
	
	@Test
	public void pendenciaNotasAnamnese() {

				List<MamNotaAdicionalAnamneses> listaAnamnese = new ArrayList<MamNotaAdicionalAnamneses>();
				MamNotaAdicionalAnamneses anamnese1 = new MamNotaAdicionalAnamneses();
				MamNotaAdicionalAnamneses anamnese2 = new MamNotaAdicionalAnamneses();
				anamnese2.setSeq(1);
				anamnese1.setNotaAdicionalAnamnese(anamnese2);
				listaAnamnese.add(anamnese1);
				
		Mockito.when(mockedMamNotaAdicionalAnamnesesDAO.pesquisarNotaAdicionalAnamnesePorNumeroSeqEPendencia(Mockito.anyInt(), Mockito.anyLong(), Mockito.any(DominioIndPendenteAmbulatorio.class)))
		.thenReturn(listaAnamnese);
				
		Mockito.when(mockedMamNotaAdicionalAnamnesesDAO.obterPorChavePrimaria(Mockito.anyLong()))
		.thenReturn(anamnese1);
				
		try {
			systemUnderTest.pendenciaNotasAnamnese(1, 1L);
		} catch (Exception e) {
			Assert.fail("Exceção gerada. " + e.getMessage());
		}
	}	
	
	@Test
	public void pendenciaEvolucao() {

				List<MamEvolucoes> listaEvolucoes = new ArrayList<MamEvolucoes>();
				MamEvolucoes evolucao1 = new MamEvolucoes();
				MamEvolucoes evolucao2 = new MamEvolucoes();
				evolucao2.setSeq(1L);
				evolucao1.setEvolucao(evolucao2);
				listaEvolucoes.add(evolucao1);
				
		Mockito.when(mockedMamEvolucoesDAO.pesquisarEvolucoesPorNumeroSeqEPendencia(Mockito.anyInt(), Mockito.anyLong(), Mockito.any(DominioIndPendenteAmbulatorio.class)))
		.thenReturn(listaEvolucoes);
				
		Mockito.when(mockedMamEvolucoesDAO.pesquisarMamEvolucoesPorEvoSeq(Mockito.anyLong()))
		.thenReturn(listaEvolucoes);
				
		Mockito.when(mockedMamEvolucoesDAO.obterPorChavePrimaria(Mockito.anyLong()))
		.thenReturn(evolucao1);
				
		try {
			systemUnderTest.pendenciaEvolucao(1, 1L);
		} catch (Exception e) {
			Assert.fail("Exceção gerada. " + e.getMessage());
		}
	}
	
	@Test
	public void pendenciaNotasEvolucao() {

				List<MamNotaAdicionalEvolucoes> listaEvolucoes = new ArrayList<MamNotaAdicionalEvolucoes>();
				MamNotaAdicionalEvolucoes evolucao1 = new MamNotaAdicionalEvolucoes();
				MamNotaAdicionalEvolucoes evolucao2 = new MamNotaAdicionalEvolucoes();
				evolucao2.setSeq(1);
				evolucao1.setNotaAdicionalEvolucao(evolucao2);
				listaEvolucoes.add(evolucao1);
				
		Mockito.when(mockedMamNotaAdicionalEvolucoesDAO.pesquisarNotaAdicionalEvolucoesPorNumeroSeqEPendencia(Mockito.anyInt(), Mockito.anyLong(), Mockito.any(DominioIndPendenteAmbulatorio.class)))
		.thenReturn(listaEvolucoes);
				
		Mockito.when(mockedMamNotaAdicionalEvolucoesDAO.obterPorChavePrimaria(Mockito.anyLong()))
		.thenReturn(evolucao1);
				
		try {
			systemUnderTest.pendenciaNotasEvolucao(1, 1L);
		} catch (Exception e) {
			Assert.fail("Exceção gerada. " + e.getMessage());
		}
	}
	
	@Test
	public void pendenciaProcedimento() {

				List<MamProcedimentoRealizado> listaProcedimento = new ArrayList<MamProcedimentoRealizado>();
				MamProcedimentoRealizado procedimento1 = new MamProcedimentoRealizado();
				MamProcedimentoRealizado procedimento2 = new MamProcedimentoRealizado();
				procedimento2.setSeq(1L);
				procedimento1.setProcedimentoRealizado(procedimento2);
				listaProcedimento.add(procedimento1);
				
		Mockito.when(mockedMamProcedimentoRealizadoDAO.pesquisarPendenciaProcedimentoRealizadoPorNumeroSeqEPendencia(Mockito.anyInt(), Mockito.anyLong(), Mockito.any(DominioIndPendenteAmbulatorio.class)))
		.thenReturn(listaProcedimento);
				
		Mockito.when(mockedMamProcedimentoRealizadoDAO.obterPorChavePrimaria(Mockito.anyLong()))
		.thenReturn(procedimento1);
				
		try {
			systemUnderTest.pendenciaProcedimento(1, 1L);
		} catch (Exception e) {
			Assert.fail("Exceção gerada. " + e.getMessage());
		}
	}	
	
	@Test
	public void pendenciaReceituario() {

				List<MamReceituarios> listaReceituario = new ArrayList<MamReceituarios>();
				MamReceituarios receituario1 = new MamReceituarios();
				MamReceituarios receituario2 = new MamReceituarios();
				receituario2.setSeq(1L);
				receituario1.setReceituario(receituario2);
				listaReceituario.add(receituario1);
				
		Mockito.when(mockedMamReceituariosDAO.pesquisarReceituarioPorNumeroSeqEPendencia(Mockito.anyInt(), Mockito.anyLong(), Mockito.any(DominioIndPendenteAmbulatorio.class)))
		.thenReturn(listaReceituario);
				
		Mockito.when(mockedMamReceituariosDAO.obterPorChavePrimaria(Mockito.anyLong()))
		.thenReturn(receituario1);
				
		try {
			systemUnderTest.pendenciaReceituario(1, 1L);
		} catch (Exception e) {
			Assert.fail("Exceção gerada. " + e.getMessage());
		}
	}	
	
	@Test
	public void pendenciaControle() {

				List<MamControles> listaControles = new ArrayList<MamControles>();
				MamControles controle = new MamControles();
				listaControles.add(controle);
				
		Mockito.when(mockedMamControlesDAO.pesquisarControlesPorNumeroConsultaESituacao(Mockito.anyInt()))
		.thenReturn(listaControles);
				
		try {
			systemUnderTest.pendenciaControle(1);
		} catch (Exception e) {
			Assert.fail("Exceção gerada. " + e.getMessage());
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
