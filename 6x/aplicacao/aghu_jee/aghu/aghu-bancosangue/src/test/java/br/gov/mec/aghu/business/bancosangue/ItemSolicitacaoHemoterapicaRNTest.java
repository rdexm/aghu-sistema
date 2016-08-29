package br.gov.mec.aghu.business.bancosangue;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.bancosangue.dao.AbsComponenteSanguineoDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsItensSolHemoterapicasDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsProcedHemoterapicoDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsSolicitacoesHemoterapicasDAO;
import br.gov.mec.aghu.bancosangue.vo.AtualizaCartaoPontoVO;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsItemSolicitacaoHemoterapicaJustificativa;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicasId;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicasId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;

public class ItemSolicitacaoHemoterapicaRNTest extends AGHUBaseUnitTest<ItemSolicitacaoHemoterapicaRN>{

    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
    @Mock
    private AbsComponenteSanguineoDAO mockedAbsComponenteSanguineoDAO;
    @Mock
    private AbsProcedHemoterapicoDAO mockedAbsProcedHemoterapicoDAO;
    @Mock
    private BancoDeSangueRN mockedBancoDeSangueRN;
    @Mock
    private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
    @Mock
    private AbsSolicitacoesHemoterapicasDAO mockedAbsSolicitacoesHemoterapicasDAO;
    @Mock
    private AbsItensSolHemoterapicasDAO mockedAbsItensSolHemoterapicasDAO;
    @Mock
    private JustificativaItemSolicitacaoHemoterapicaRN mockedJustificativaItemSolicitacaoHemoterapicaRN;
	
	
	/**
	 * Testa a execucao com passagem de parâmetros nulos.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=NullPointerException.class)
	public void excluirItemSolicitacaoHemoterapicaNuloTest() throws ApplicationBusinessException {
		
		systemUnderTest.excluirItemSolicitacaoHemoterapica(null);
		
	}

	/**
	 * Testa a execucao com passagem de parâmetros validos.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void excluirItemSolicitacaoHemoterapicaSemItemSolicitacaoHemoterapicaJustificativaTest() throws ApplicationBusinessException {
		
		AbsItensSolHemoterapicas itemSolicitacaoHemoterapica = new AbsItensSolHemoterapicas();
		
		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		AbsSolicitacoesHemoterapicasId solicHemoId = new AbsSolicitacoesHemoterapicasId();
		solicHemoId.setAtdSeq(123);
		solicHemoId.setSeq(123);
		
		solicitacaoHemoterapica.setId(solicHemoId);
		
		itemSolicitacaoHemoterapica.setSolicitacaoHemoterapica(solicitacaoHemoterapica);

		Mockito.when(mockedAbsItensSolHemoterapicasDAO.pesquisarItensSolicitacaoHemoterapicaCount(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(Long.valueOf(1));
		Mockito.when(mockedAbsItensSolHemoterapicasDAO.obterPorChavePrimaria(Mockito.any(AbsItensSolHemoterapicasId.class))).thenReturn(itemSolicitacaoHemoterapica);
		
		
		systemUnderTest.excluirItemSolicitacaoHemoterapica(itemSolicitacaoHemoterapica);
		
	}
	
	
	/**
	 * Testa a execucao com passagem de parâmetros validos.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void excluirItemSolicitacaoHemoterapicaComItemSolicitacaoHemoterapicaJustificativaTest() throws ApplicationBusinessException {
		
		AbsItensSolHemoterapicas itemSolicitacaoHemoterapica = new AbsItensSolHemoterapicas();
		
		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		AbsSolicitacoesHemoterapicasId solicHemoId = new AbsSolicitacoesHemoterapicasId();
		solicHemoId.setAtdSeq(123);
		solicHemoId.setSeq(123);
		
		solicitacaoHemoterapica.setId(solicHemoId);
		
		itemSolicitacaoHemoterapica.setSolicitacaoHemoterapica(solicitacaoHemoterapica);
		
		List<AbsItemSolicitacaoHemoterapicaJustificativa> lista = new ArrayList<AbsItemSolicitacaoHemoterapicaJustificativa>(1);
		 
		AbsItemSolicitacaoHemoterapicaJustificativa itemSolHemJust = new AbsItemSolicitacaoHemoterapicaJustificativa();
		
		lista.add(itemSolHemJust);
		
		itemSolicitacaoHemoterapica.setItemSolicitacaoHemoterapicaJustificativas(lista);

		
		Mockito.when(mockedAbsItensSolHemoterapicasDAO.pesquisarItensSolicitacaoHemoterapicaCount(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(Long.valueOf(1));
		Mockito.when(mockedAbsItensSolHemoterapicasDAO.obterPorChavePrimaria(Mockito.any(AbsItensSolHemoterapicasId.class))).thenReturn(itemSolicitacaoHemoterapica);
		
		systemUnderTest.excluirItemSolicitacaoHemoterapica(itemSolicitacaoHemoterapica);
		
	}
	
	/**
	 * Testa a execucao com passagem de parâmetros nulos.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void atualizarMatriculaVinculoServidorNuloTest() throws BaseException {

		whenObterServidorLogado();
		
		systemUnderTest.atualizarMatriculaVinculoServidor(null, null, null, null);
		
	}

	/**
	 * Testa a execucao com passagem de parâmetros validos.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void atualizarMatriculaVinculoServidorSemAtualizaCartaoPontoVOTest() throws BaseException {
		
		whenObterServidorLogado();
		
		Mockito.when(mockedBancoDeSangueRN.atualizaCartaoPontoServidor()).thenReturn(null);

		systemUnderTest.atualizarMatriculaVinculoServidor("teste", new Date(), null, null);
		
	}

	/**
	 * Testa a execucao com passagem de parâmetros validos.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void atualizarMatriculaVinculoServidorComAtualizaCartaoPontoVOTest() throws BaseException {
		AtualizaCartaoPontoVO cartao = new AtualizaCartaoPontoVO();

		whenObterServidorLogado();
		
		Mockito.when(mockedBancoDeSangueRN.atualizaCartaoPontoServidor()).thenReturn(cartao);

		systemUnderTest.atualizarMatriculaVinculoServidor("teste", new Date(), null, null);
		
	}

	/**
	 * Testa a execucao com passagem de parâmetros nulos.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaAtributoNuloTest() throws ApplicationBusinessException {
		
		systemUnderTest.verificaAtributo(null,null,null,null);
		
	}

	/**
	 * Testa a execucao com passagem de parâmetro Comfrequencia.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaAtributoComfrequenciaTest() {
		
		try{
			systemUnderTest.verificaAtributo((short) 0,null,null,null);
		} catch (ApplicationBusinessException atual) {
			assertEquals("ABS_00379", atual.getMessage());
		}
		
	}

	/**
	 * Testa a execucao com passagem de parâmetro quantidadeAplicacoes.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaAtributoComQuantidadeAplicacoesTest() {
		
		try{
			systemUnderTest.verificaAtributo(null,(short) 0,null,null);
		} catch (ApplicationBusinessException atual) {
			assertEquals("ABS_00380", atual.getMessage());
		}
		
	}
	
	
	/**
	 * Testa a execucao com passagem de parâmetro quantidadeUnidades.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaAtributoComQuantidadeUnidadesTest() {
		
		try{
			systemUnderTest.verificaAtributo(null,null,(byte) 0,null);
		} catch (ApplicationBusinessException atual) {
			assertEquals("ABS_00381", atual.getMessage());
		}
		
	}
	
	
	/**
	 * Testa a execucao com passagem de parâmetro quantidadeMl.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaAtributoComQuantidadeMlTest(){
		
		try{
			systemUnderTest.verificaAtributo(null,null,null,(short) 0);
		} catch (ApplicationBusinessException atual) {
			assertEquals("ABS_00382", atual.getMessage());
		}
		
	}
	
	/**
	 * Testa a execucao com passagem de parâmetro null.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaComponenteSanguineoNuloTest() throws ApplicationBusinessException {
		
			systemUnderTest.verificaComponenteSanguineo(null);
		
	}
	
	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaComponenteSanguineoComComponenteSanguineoTest() throws ApplicationBusinessException {
		
		Mockito.when(mockedAbsComponenteSanguineoDAO.obterPorChavePrimaria(Mockito.anyString())).thenReturn(null);
		
		systemUnderTest.verificaComponenteSanguineo("CH");
		
	}
	
	
	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaComponenteSanguineoSemComponenteSanguineoTest() throws ApplicationBusinessException {
		AbsComponenteSanguineo compSang = new AbsComponenteSanguineo();
		compSang.setIndSituacao(DominioSituacao.A);

		Mockito.when(mockedAbsComponenteSanguineoDAO.obterPorChavePrimaria(Mockito.anyString())).thenReturn(compSang);

		systemUnderTest.verificaComponenteSanguineo("CH");
		
	}

	/**
	 * Testa a execucao com passagem de parâmetro nulo.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaIndicadoresComponenteSanguineoNuloTest() throws ApplicationBusinessException {
	
		systemUnderTest.verificaComponenteSanguineo(null);
		
	}

	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaIndicadoresComponenteSanguineoSemComponenteSanguineoTest() throws ApplicationBusinessException {
		Mockito.when(mockedAbsComponenteSanguineoDAO.obterPorChavePrimaria(Mockito.anyString())).thenReturn(null);
		
		systemUnderTest.verificaIndicadoresComponenteSanguineo("CH", null, null, null, null);
		
	}

	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaIndicadoresComponenteSanguineoComComponenteSanguineoTest() throws ApplicationBusinessException {
		AbsComponenteSanguineo compSang = new AbsComponenteSanguineo();
		Mockito.when(mockedAbsComponenteSanguineoDAO.obterPorChavePrimaria(Mockito.anyString())).thenReturn(compSang);
		
		systemUnderTest.verificaIndicadoresComponenteSanguineo("CH", null, null, null, null);
	}
	
	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaIndicadoresComponenteSanguineoComIndIrradiadoTest() throws ApplicationBusinessException {
		AbsComponenteSanguineo compSang = new AbsComponenteSanguineo();
		compSang.setIndIrradiado(false);
		Mockito.when(mockedAbsComponenteSanguineoDAO.obterPorChavePrimaria(Mockito.anyString())).thenReturn(compSang);
		
		
		try{
			systemUnderTest.verificaIndicadoresComponenteSanguineo("CH", true, null, null, null);
		}catch (ApplicationBusinessException e) {
			assertEquals("ABS_00387", e.getMessage());
		}
	}
	
	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaIndicadoresComponenteSanguineoComIndFiltradoTest() throws ApplicationBusinessException {
		AbsComponenteSanguineo compSang = new AbsComponenteSanguineo();
		compSang.setIndFiltrado(false);
		Mockito.when(mockedAbsComponenteSanguineoDAO.obterPorChavePrimaria(Mockito.anyString())).thenReturn(compSang);
		
		try{
			systemUnderTest.verificaIndicadoresComponenteSanguineo("CH", null, true, null, null);
		}catch (ApplicationBusinessException e) {
			
			assertEquals("ABS_00388", e.getMessage());
		}
	}
	
	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaIndicadoresComponenteSanguineoComIndLavadoTest() throws ApplicationBusinessException {
		AbsComponenteSanguineo compSang = new AbsComponenteSanguineo();
		compSang.setIndLavado(false);
		Mockito.when(mockedAbsComponenteSanguineoDAO.obterPorChavePrimaria(Mockito.anyString())).thenReturn(compSang);
		
		try{
			systemUnderTest.verificaIndicadoresComponenteSanguineo("CH", null, null, true, null);
		}catch (ApplicationBusinessException e) {
			
			assertEquals("ABS_00389", e.getMessage());
		}
	}
	
	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaIndicadoresComponenteSanguineoComIndAfereseTest() throws ApplicationBusinessException {
		AbsComponenteSanguineo compSang = new AbsComponenteSanguineo();
		compSang.setIndAferese(false);
		Mockito.when(mockedAbsComponenteSanguineoDAO.obterPorChavePrimaria(Mockito.anyString())).thenReturn(compSang);

		try{
			systemUnderTest.verificaIndicadoresComponenteSanguineo("CH", null, null, null, true);
		}catch (ApplicationBusinessException e) {
			
			assertEquals("ABS_00875", e.getMessage());
		}
	}

	/**
	 * Testa a execucao com passagem de parâmetro Nulo.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaProcedimentoHemoterapicoNuloTest() throws ApplicationBusinessException {
		
			systemUnderTest.verificaProcedimentoHemoterapico(null);
	}
	
	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaProcedimentoHemoterapicoSemProcedimentoHemoterapicoTest() throws ApplicationBusinessException {
		Mockito.when(mockedAbsProcedHemoterapicoDAO.obterPorChavePrimaria(Mockito.anyString())).thenReturn(null);
		
		systemUnderTest.verificaProcedimentoHemoterapico("CH");
	}
	
	
	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaProcedimentoHemoterapicoTest() throws ApplicationBusinessException {
		AbsProcedHemoterapico procHem = new AbsProcedHemoterapico();
		procHem.setIndSituacao(DominioSituacao.A);
		Mockito.when(mockedAbsProcedHemoterapicoDAO.obterPorChavePrimaria(Mockito.anyString())).thenReturn(procHem);

		try{
			systemUnderTest.verificaProcedimentoHemoterapico("CH");
		}catch (ApplicationBusinessException e) {
			
			assertEquals("ABS_00385", e.getMessage());
		}
	}
	

	
	/**
	 * Testa a execucao com passagem de parâmetro nulo.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaTipoFrequenciaNuloTest() throws ApplicationBusinessException {
		
			systemUnderTest.verificaTipoFrequencia(null, null);
		
	}
	
	
	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaTipoFrequenciaSemTipoFrequenciaAprazamentoTest() throws ApplicationBusinessException {
		Mockito.when(mockedPrescricaoMedicaFacade.obterTipoFrequenciaAprazamentoId(Mockito.anyShort())).thenReturn(null);
		
		systemUnderTest.verificaTipoFrequencia(null, (short)1);
	}
	
	
	
	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaTipoFrequenciaComTipoFrequenciaAprazamentoTest() throws ApplicationBusinessException {
		MpmTipoFrequenciaAprazamento tipoFreqApraz = new MpmTipoFrequenciaAprazamento();
		tipoFreqApraz.setIndSituacao(DominioSituacao.I);
		Mockito.when(mockedPrescricaoMedicaFacade.obterTipoFrequenciaAprazamentoId(Mockito.anyShort())).thenReturn(tipoFreqApraz);
		
		try{
			systemUnderTest.verificaTipoFrequencia(null, (short)1);
		}catch (ApplicationBusinessException e) {
			
			assertEquals("ABS_00376", e.getMessage());
		}
	}
	
	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaTipoFrequenciaIndUsoHemoterapiaTest() throws ApplicationBusinessException {
		MpmTipoFrequenciaAprazamento tipoFreqApraz = new MpmTipoFrequenciaAprazamento();
		tipoFreqApraz.setIndSituacao(DominioSituacao.A);
		Mockito.when(mockedPrescricaoMedicaFacade.obterTipoFrequenciaAprazamentoId(Mockito.anyShort())).thenReturn(tipoFreqApraz);

		try{
			systemUnderTest.verificaTipoFrequencia(null, (short)1);
		}catch (ApplicationBusinessException e) {
			
			assertEquals("ABS_00377", e.getMessage());
		}
	}
	
	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaTipoFrequenciaIndDigitaFrequenciaTest() throws ApplicationBusinessException {
		MpmTipoFrequenciaAprazamento tipoFreqApraz = new MpmTipoFrequenciaAprazamento();
		tipoFreqApraz.setIndSituacao(DominioSituacao.A);
		tipoFreqApraz.setIndDigitaFrequencia(true);
		tipoFreqApraz.setIndUsoHemoterapia(true);
		Mockito.when(mockedPrescricaoMedicaFacade.obterTipoFrequenciaAprazamentoId(Mockito.anyShort())).thenReturn(tipoFreqApraz);
		
		try{
			systemUnderTest.verificaTipoFrequencia(null, (short)1);
		}catch (ApplicationBusinessException e) {
			
			assertEquals("ABS_00378", e.getMessage());
		}
	}
	
	/**
	 * Testa a execucao com passagem de parâmetro Nulo.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaPendenteNuloTest() throws ApplicationBusinessException {
		Mockito.when(mockedAbsSolicitacoesHemoterapicasDAO.obterPorChavePrimaria(Mockito.any(AbsSolicitacoesHemoterapicasId.class))).thenReturn(null);
		
		systemUnderTest.verificaPendente(null, null);
		
	}
	
	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaPendenteSemIndPendenteTest() throws ApplicationBusinessException {
		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		solicitacaoHemoterapica.setIndPendente(DominioIndPendenteItemPrescricao.P);
		Mockito.when(mockedAbsSolicitacoesHemoterapicasDAO.obterPorChavePrimaria(Mockito.any(AbsSolicitacoesHemoterapicasId.class))).thenReturn(solicitacaoHemoterapica);
		
		systemUnderTest.verificaPendente(1, 2);
	
	}
	
	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaPendenteComIndPendenteTest() throws ApplicationBusinessException {
		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		solicitacaoHemoterapica.setIndPendente(DominioIndPendenteItemPrescricao.E);
		Mockito.when(mockedAbsSolicitacoesHemoterapicasDAO.obterPorChavePrimaria(Mockito.any(AbsSolicitacoesHemoterapicasId.class))).thenReturn(solicitacaoHemoterapica);
		
		try{
			systemUnderTest.verificaPendente(1, 1);
		}catch (ApplicationBusinessException e) {
			
			assertEquals("ABS_00390", e.getMessage());
		}
	}
	
	
	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaUltimoItemComCountMaiorQueZeroTest() throws ApplicationBusinessException {
		Mockito.when(mockedAbsItensSolHemoterapicasDAO.pesquisarItensSolicitacaoHemoterapicaCount(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(Long.valueOf("10"));
		
		systemUnderTest.verificaUltimoItem(1, 1);
	}
	
	/**
	 * Testa a execucao com passagem de parâmetro válido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaUltimoItemComCountIgualQueZeroTest() throws ApplicationBusinessException {
		Mockito.when(mockedAbsItensSolHemoterapicasDAO.pesquisarItensSolicitacaoHemoterapicaCount(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(Long.valueOf("0"));
		
		try{
			systemUnderTest.verificaUltimoItem(1, 1);
		}catch (ApplicationBusinessException e) {
			
			assertEquals("ABS_00477", e.getMessage());
		}
	}
	
	
	/**
	 * Testa a execucao com passagem de parâmetros nulos.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=NullPointerException.class)
	public void enforceItemSolicitacaoHemoterapicaNuloTest() throws ApplicationBusinessException {
		
		systemUnderTest.enforceItemSolicitacaoHemoterapica(null, TipoOperacaoEnum.DELETE);
	
	}

	
	/**
	 * Testa a execucao com passagem de parâmetros validos.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void enforceItemSolicitacaoHemoterapicaTest() throws ApplicationBusinessException {
		
		AbsItensSolHemoterapicas itemSolicitacaoHemoterapica = new AbsItensSolHemoterapicas();
		
		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		AbsSolicitacoesHemoterapicasId solicHemoId = new AbsSolicitacoesHemoterapicasId();
		solicHemoId.setAtdSeq(123);
		solicHemoId.setSeq(123);
		
		solicitacaoHemoterapica.setId(solicHemoId);
		
		itemSolicitacaoHemoterapica.setSolicitacaoHemoterapica(solicitacaoHemoterapica);

		Mockito.when(mockedAbsItensSolHemoterapicasDAO.pesquisarItensSolicitacaoHemoterapicaCount(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(Long.valueOf("1"));

		systemUnderTest.enforceItemSolicitacaoHemoterapica(itemSolicitacaoHemoterapica, TipoOperacaoEnum.DELETE);
		
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
