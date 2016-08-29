package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghDocumentosAssinados;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoCirurgicaId;
import br.gov.mec.aghu.model.MbcExtratoCirurgia;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcMotivoCancelamento;
import br.gov.mec.aghu.model.MbcQuestao;
import br.gov.mec.aghu.model.MbcValorValidoCanc;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class CirurgiasInternacaoPOLRNTest extends AGHUBaseUnitTest<CirurgiasInternacaoPOLRN> {

	@Mock
	private IAmbulatorioFacade mockedAmbulatorioFacade;
	@Mock
	private IBlocoCirurgicoFacade mockedBlocoCirurgicoFacade;
	@Mock
	private IBlocoCirurgicoProcDiagTerapFacade mockedBlocoCirurgicoProcDiagTerapFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private IExamesFacade mockedExamesFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private ConsultaNotasPolRN mockedConsultarNotasPolRN;

	/**
	 * Método executado antes da execução dos testes. Criar os mocks e uma
	 * instancia da classe a ser testada (Com os devidos métodos sobrescritos)
	 */
	@Before
	public void doBeforeEachTestCase() {
	}

	@Test
	public void buscarMotivoCancelCirurgiaTest() {
		final Integer seq = 123456;
		esperarBuscarMotivoCancelCirurgia();
		esperarObterDescricaoCidCapitalizada();
		Assert.assertNotNull(systemUnderTest.buscarMotivoCancelCirurgia(seq));
	}

	// Expectations para MbcExtratoCirurgiaDAO
	private void esperarBuscarMotivoCancelCirurgia() {
		Mockito.when(mockedBlocoCirurgicoFacade.buscarMotivoCancelCirurgia(Mockito.anyInt())).thenReturn(getExtratoCirurgia());
	}

	private MbcExtratoCirurgia getExtratoCirurgia() {

		RapPessoasFisicas pessoaFisica = new RapPessoasFisicas();
		RapServidores servidor = new RapServidores();

		MbcExtratoCirurgia extratoCirurgia = new MbcExtratoCirurgia();
		MbcCirurgias cirurgia = new MbcCirurgias();
		MbcMotivoCancelamento motivoCancelamento = new MbcMotivoCancelamento();
		MbcQuestao questao = new MbcQuestao();
		MbcValorValidoCanc valorCanc = new MbcValorValidoCanc();

		extratoCirurgia.setCriadoEm(new Date());

		extratoCirurgia.setServidor(servidor);
		servidor.setPessoaFisica(pessoaFisica);
		pessoaFisica.setNome("Maria da Silva");

		extratoCirurgia.setCirurgia(cirurgia);
		cirurgia.setMotivoCancelamento(motivoCancelamento);
		motivoCancelamento.setDescricao("PACIENTE FEBRIL");

		cirurgia.setQuestao(questao);
		questao.setDescricao("Diagnóstico:");

		valorCanc.setValor("123");
		cirurgia.setValorValidoCanc(valorCanc);

		cirurgia.setComplementoCanc("preparo immcompleto");

		// extratoCirurgia.setDescricaoMotivoCancelamentoEditado("Paciente febril");

		return extratoCirurgia;
	}

	// Expectations para AmbulatorioFacade
	private void esperarObterDescricaoCidCapitalizada() {
		Mockito.when(mockedAmbulatorioFacade.obterDescricaoCidCapitalizada(Mockito.anyString(), Mockito.any(CapitalizeEnum.class)))
				.thenReturn(getDescricaoMinusculo());
	}

	private String getDescricaoMinusculo() {

		String descricaoMinusculo = "Paciente febril";

		return descricaoMinusculo;
	}

	// ----------------- Teste botões: botaoDescricao, botaoAtoAnestesico,
	// botaoExameAnatomopatologico, botaoDocAssinado

	@Test
	public void habilitarBotaoDescricaoTest() {
		final Integer seq = 1;
		esperarListarDescricaoCirurgicaPorSeqCirurgiaSituacao();
		esperarListarDescricaoPorSeqCirurgiaSituacao();
		Assert.assertTrue(systemUnderTest.habilitarBotaoDescricao(seq));
	}

	// Expectations para MbcDescricaoCirurgicaDAO
	private void esperarListarDescricaoCirurgicaPorSeqCirurgiaSituacao() {
		Mockito.when(
				mockedBlocoCirurgicoFacade.listarDescricaoCirurgicaPorSeqCirurgiaSituacao(Mockito.anyInt(),
						Mockito.any(DominioSituacaoDescricaoCirurgia.class))).thenReturn(getMbcDescricaoCirurgica());
	}

	// Expectations para PdtDescricaoDAO
	private void esperarListarDescricaoPorSeqCirurgiaSituacao() {
		Mockito.when(
				mockedBlocoCirurgicoProcDiagTerapFacade.listarDescricaoPorSeqCirurgiaSituacao(1, new DominioSituacaoDescricao[] {
						DominioSituacaoDescricao.PRE, DominioSituacaoDescricao.DEF })).thenReturn(getPdtDescricao());
	}

	// private List<MbcDescricaoCirurgica> getMbcDescricaoCirurgica() {
	//
	// MbcDescricaoCirurgica descricaoCirurgica = new MbcDescricaoCirurgica();
	// List<MbcDescricaoCirurgica> listaDescricaoCirurgia = new
	// ArrayList<MbcDescricaoCirurgica>();
	//
	// listaDescricaoCirurgia.add(descricaoCirurgica);
	//
	// return listaDescricaoCirurgia;
	//
	// }

	private List<PdtDescricao> getPdtDescricao() {

		PdtDescricao descricao = new PdtDescricao();
		List<PdtDescricao> listaDescricao = new ArrayList<PdtDescricao>();
		descricao.setSeq(1);

		listaDescricao.add(descricao);

		return listaDescricao;

	}

	@Test
	public void habilitarBotaoAtoAnestesicoTest() {
		final Integer seq = 1;
		esperarListarFichasAnestesiasPorSeqCirurgiaPendenteDthrMvtoNull();
		Assert.assertTrue(systemUnderTest.habilitarBotaoAtoAnestesico(seq));
	}

	// Expectations para MbcFichaAnestesiasDAO
	private void esperarListarFichasAnestesiasPorSeqCirurgiaPendenteDthrMvtoNull() {
		Mockito.when(
				mockedBlocoCirurgicoFacade.listarFichasAnestesiasPorSeqCirurgiaPendenteDthrMvtoNull(Mockito.anyInt(),
						Mockito.any(DominioIndPendenteAmbulatorio.class))).thenReturn(getMbcFichaAnestesias());
	}

	private List<MbcFichaAnestesias> getMbcFichaAnestesias() {

		MbcFichaAnestesias fichaAnestesia = new MbcFichaAnestesias();
		List<MbcFichaAnestesias> listaFichaAnestesia = new ArrayList<MbcFichaAnestesias>();

		listaFichaAnestesia.add(fichaAnestesia);

		return listaFichaAnestesia;

	}

	@Test
	public void habilitarBotaoExameAnatopatologicoTest() throws ApplicationBusinessException {
		final Integer seq = 1;
		esperarBuscarAghParametro();
		esperarListarItemSolicitacaoExamePorSeqCirurgiaSitCodigo();
		Assert.assertTrue(systemUnderTest.habilitarBotaoExameAnatopatologico(seq));

	}

	// Expectations para ParametroFacade
	private void esperarBuscarAghParametro() throws ApplicationBusinessException {
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(getAghParametro());
	}

	private AghParametros getAghParametro() {

		AghParametros aghParametro = new AghParametros();

		aghParametro.setVlrTexto("AG");

		return aghParametro;
	}

	// Expectations para AelItemSolicitacaoExameDAO
	private void esperarListarItemSolicitacaoExamePorSeqCirurgiaSitCodigo() {
		Mockito.when(mockedExamesFacade.listarItemSolicitacaoExamePorSeqCirurgiaSitCodigo(1, new String[] { "AG", "AG" })).thenReturn(
				getAelItemSolicitacaoExame());
	}

	private List<AelItemSolicitacaoExames> getAelItemSolicitacaoExame() {

		AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		List<AelItemSolicitacaoExames> listaItemSolicitacaoExame = new ArrayList<AelItemSolicitacaoExames>();

		listaItemSolicitacaoExame.add(itemSolicitacaoExame);

		return listaItemSolicitacaoExame;

	}

	@Test
	public void habilitarBotaoDocAssinadoTest() {
		final Integer seq = 1;
		esperarListarDocAssPorSeqCirurgiaDocAssinadoIsNotNull();
		Assert.assertTrue(systemUnderTest.habilitarBotaoDocAssinado(seq));

	}

	// Expectations para IAghuFacade
	private void esperarListarDocAssPorSeqCirurgiaDocAssinadoIsNotNull() {
		Mockito.when(mockedAghuFacade.listarDocAssPorSeqCirurgiaDocAssinadoIsNotNull(Mockito.anyInt())).thenReturn(
				getAghDocumentosAssinados());
	}

	private List<AghDocumentosAssinados> getAghDocumentosAssinados() {

		AghDocumentosAssinados docAssinado = new AghDocumentosAssinados();
		List<AghDocumentosAssinados> listaDocAssinado = new ArrayList<AghDocumentosAssinados>();

		listaDocAssinado.add(docAssinado);

		return listaDocAssinado;
	}

	@Test
	public void verificarSeDocumentoDescricaoCirugiaAssinadoTest() throws ApplicationBusinessException {
		final Integer seq = 1;
		esperarVerificarCertificadoAssinado();
		Assert.assertNotNull(systemUnderTest.verificarSeDocumentoDescricaoCirugiaAssinado(seq));
	}

	// Expectations para ConsultarNotasPolRN
	private void esperarVerificarCertificadoAssinado() {
		Mockito.when(mockedConsultarNotasPolRN.verificarCertificadoAssinado(Mockito.anyInt(), Mockito.any(DominioTipoDocumento.class)))
				.thenReturn(getDocumentosAssinados());
	}

	private Boolean getDocumentosAssinados() {

		AghVersaoDocumento docAssinado = new AghVersaoDocumento();
		List<AghVersaoDocumento> listaDocAssinado = new ArrayList<AghVersaoDocumento>();

		listaDocAssinado.add(docAssinado);
		if (listaDocAssinado != null && listaDocAssinado.size() > 0) {
			return true;
		} else {
			return false;
		}

	}

	@Test
	public void imprimirDescricaoTestMbc() throws ApplicationBusinessException {
		final Integer seq = 1;
		esperarListarDescricaoCirurgicaPorSeqCirurgia(); // MbcDescricaoCirurgica
		esperarBuscarAghParametro2();
		Object[] ret = systemUnderTest.imprimirDescricao(seq);

	}

	// Expectations para MbcDescricaoCirurgicaDAO
	private void esperarListarDescricaoCirurgicaPorSeqCirurgia() {
		Mockito.when(mockedBlocoCirurgicoFacade.listarDescricaoCirurgicaPorSeqCirurgia(Mockito.anyInt())).thenReturn(
				getMbcDescricaoCirurgica());
	}

	private List<MbcDescricaoCirurgica> getMbcDescricaoCirurgica() {
		MbcDescricaoCirurgica descricaoCirurgica = new MbcDescricaoCirurgica();
		descricaoCirurgica.setId(new MbcDescricaoCirurgicaId(123, Short.valueOf("321")));
		return Arrays.asList(descricaoCirurgica);

	}

	// Expectations para ParametroFacade
	private void esperarBuscarAghParametro2() throws ApplicationBusinessException {
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(getAghParametroAdm07());
	}

	private AghParametros getAghParametroAdm07() {

		AghParametros aghParametro = new AghParametros();

		aghParametro.setVlrTexto("ADM07");

		return aghParametro;
	}

	@Test
	public void imprimirDescricaoTestPdtr() throws ApplicationBusinessException {
		final Integer seq = 1;
		esperarListarDescricaoCirurgicaPorSeqCirurgiaNull(); // MbcDescricaoCirurgica
		esperarListarDescricaoPorSeqCirurgiaSituacao2(seq);
		esperarBuscarAghParametro2();
		Object[] ret = systemUnderTest.imprimirDescricao(seq);
	}

	private void esperarListarDescricaoCirurgicaPorSeqCirurgiaNull() {
		Mockito.when(mockedBlocoCirurgicoFacade.listarDescricaoCirurgicaPorSeqCirurgia(Mockito.anyInt())).thenReturn(null);
	}

	private void esperarListarDescricaoPorSeqCirurgiaSituacao2(Integer seq) {
		Mockito.when(
				mockedBlocoCirurgicoProcDiagTerapFacade.listarDescricaoPorSeqCirurgiaSituacao(1, new DominioSituacaoDescricao[] {
						DominioSituacaoDescricao.PRE, DominioSituacaoDescricao.DEF }, PdtDescricao.Fields.COMPLEMENTO)).thenReturn(
				getPdtDescricao());
	}

}
