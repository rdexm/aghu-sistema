package br.gov.mec.aghu.prescricaomedica.business;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

public class AtualizarPrescricaoPendenteRNTest extends AGHUBaseUnitTest<AtualizarPrescricaoPendenteRN> {

    private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

    @Mock
    private MpmPrescricaoMdtoDAO mockedMpmPrescricaoMdtoDAO;
    @Mock
    private MpmPrescricaoDietaDAO mockedMpmPrescricaoDietaDAO;
    @Mock
    private MpmPrescricaoCuidadoDAO mockedMpmPrescricaoCuidadoDAO;
    @Mock
    private MpmPrescricaoProcedimentoDAO mockedMpmPrescricaoProcedimentoDAO;
    @Mock
    private IBancoDeSangueFacade mockedBancoDeSangueFacade;
    @Mock
    private MpmPrescricaoMedicaDAO mockedMpmPrescricaoMedicaDAO;
    @Mock
    private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
    @Mock
    private PrescreverProcedimentoEspecialRN mockedPrescreverProcedimentoEspecialRN;

    /**
     * Indica que o mock do DAO de Prescricao Médica espera que o método flush
     * seja chamado ao menos uma vez na execução do teste.
     */
    private void expectFlush() {
	verify(mockedMpmPrescricaoMedicaDAO, atLeast(1)).flush();
    }

    /**
     * Indica ao mock do DAO de Prescricao Médica que o método flush não deverá
     * ser chamado na execução dos teste.
     */
    private void expectNoFlush() {
	verify(mockedMpmPrescricaoMedicaDAO, never()).flush();
    }

    /**
     * Indica ao mock para esperar uma chamada do método
     * buscarSolicitacoesHemoterapicasPendentes com quaisquer parâmetros
     * adequados, retornando a lista passada por parâmetro a este método.
     * 
     * @param solicitacoes
     */
    private void esperarListagemSolicitacoes(final List<AbsSolicitacoesHemoterapicas> solicitacoes) {
	when(mockedBancoDeSangueFacade.buscarSolicitacoesHemoterapicasPendentes(any(MpmPrescricaoMedica.class), any(Date.class)))
		.thenReturn(solicitacoes);
    }

    /**
     * Indica ao mock para esperar uma chamada do método
     * listarProcedimentosPrescricaoPendente com quaisquer parâmetros adequados,
     * retornando a lista passada por parâmetro a este método.
     * 
     * @param solicitacoes
     */
    private void esperarListagemProcedimentos(final List<MpmPrescricaoProcedimento> procedimentos) {
	when(mockedMpmPrescricaoProcedimentoDAO.listarProcedimentosPrescricaoPendente(any(MpmPrescricaoMedica.class), any(Date.class)))
		.thenReturn(procedimentos);
    }

    /**
     * Indica ao mock para esperar uma chamada do método obterPrescricaoPendente
     * com quaisquer parâmetros adequados, retornando a lista passada por
     * parâmetro a este método.
     * 
     * @param solicitacoes
     */
    private void esperarListagemMedicamentos(final List<MpmPrescricaoMdto> medicamentos) {
	when(mockedMpmPrescricaoMdtoDAO.listarPrescricaoMedicamentoPendente(any(MpmPrescricaoMedica.class), any(Date.class))).thenReturn(
		medicamentos);
    }

    /**
     * Indica ao mock para esperar uma chamada ao método
     * persistirPrescricaoMedicamento com qualquer parâmetro adequado.
     * 
     */
    // private void esperarPersistirMedicamento() {
    // try {
    // mockingContext.checking(new Expectations() {
    //
    // {
    // allowing(mockedPrescricaoMedicaFacade).persistirPrescricaoMedicamentos(with(any(List.class)),
    // with(any(String.class)));
    // }
    // });
    // } catch (BaseException e) {
    // getLog().debug("Exceção ignorada.");
    // }
    // }

    /**
     * Indica ao mock para esperar uma chamada do método obterPrescricaoPendente
     * com quaisquer parâmetros adequados, retornando a lista passada por
     * parâmetro a este método.
     * 
     * @param solicitacoes
     */
    private void esperarListagemCuidados(final List<MpmPrescricaoCuidado> cuidados) {
	when(mockedMpmPrescricaoCuidadoDAO.listarPrescricaoCuidadoPendente(any(MpmPrescricaoMedica.class), any(Date.class))).thenReturn(
		cuidados);
    }

    /**
     * Indica ao mock para esperar uma chamada do método obterPrescricaoPendente
     * com quaisquer parâmetros adequados, retornando a lista passada por
     * parâmetro a este método.
     * 
     * @param solicitacoes
     */
    private void esperarListagemDietas(final List<MpmPrescricaoDieta> dietas) {
	when(mockedMpmPrescricaoDietaDAO.listarPrescricaoDietaPendente(any(MpmPrescricaoMedica.class), any(Date.class))).thenReturn(dietas);
    }

    /**
     * Indica ao mock para esperar uma chamada do método obterPorChavePrimaria
     * com quaisquer parâmetros adequados, retornando a a prescrição passado
     * como parâmetro para este método.
     * 
     * @param solicitacoes
     */
    private void esperarObterPrescricaoChavePrimaria(final MpmPrescricaoMedica prescricao) {
	when(mockedMpmPrescricaoMedicaDAO.obterPorChavePrimaria(any(MpmPrescricaoMedicaId.class))).thenReturn(prescricao);
    }

    /**
     * Testa o caminho feliz da funcionalidade de tornar uma prescrição
     * pendente.
     */
    @Test
    public void atualizarPrescricaoPendenteTest() {

	Date dataAtual = new Date();

	MpmPrescricaoMedica prescricaoPendete = new MpmPrescricaoMedica();

	prescricaoPendete.setDthrMovimento(dataAtual);
	prescricaoPendete.setSituacao(DominioSituacaoPrescricao.U);

	List<AbsSolicitacoesHemoterapicas> listaSolicitacoes = new ArrayList<AbsSolicitacoesHemoterapicas>();
	AbsSolicitacoesHemoterapicas solicitacao1 = new AbsSolicitacoesHemoterapicas();
	AbsSolicitacoesHemoterapicas solicitacao2 = new AbsSolicitacoesHemoterapicas();
	listaSolicitacoes.add(solicitacao1);
	listaSolicitacoes.add(solicitacao2);

	List<MpmPrescricaoProcedimento> listaProcedimentos = new ArrayList<MpmPrescricaoProcedimento>();
	MpmPrescricaoProcedimento procedimento1 = new MpmPrescricaoProcedimento();
	MpmPrescricaoProcedimento procedimento2 = new MpmPrescricaoProcedimento();
	listaProcedimentos.add(procedimento1);
	listaProcedimentos.add(procedimento2);

	List<MpmPrescricaoMdto> listaMedicamentos = new ArrayList<MpmPrescricaoMdto>();
	MpmPrescricaoMdto medicamento1 = new MpmPrescricaoMdto();
	MpmPrescricaoMdto mdicamento2 = new MpmPrescricaoMdto();
	listaMedicamentos.add(medicamento1);
	listaMedicamentos.add(mdicamento2);

	List<MpmPrescricaoCuidado> listaCuidados = new ArrayList<MpmPrescricaoCuidado>();
	MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
	MpmPrescricaoCuidado cuidado2 = new MpmPrescricaoCuidado();
	listaCuidados.add(cuidado);
	listaCuidados.add(cuidado2);

	List<MpmPrescricaoDieta> listaDietas = new ArrayList<MpmPrescricaoDieta>();
	MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
	MpmPrescricaoDieta dieta2 = new MpmPrescricaoDieta();
	listaDietas.add(dieta);
	listaDietas.add(dieta2);

	try {
	    // expectations

	    this.esperarListagemSolicitacoes(listaSolicitacoes);

	    this.esperarListagemProcedimentos(listaProcedimentos);

	    this.esperarListagemMedicamentos(listaMedicamentos);

	    // this.esperarPersistirMedicamento();

	    this.esperarListagemCuidados(listaCuidados);

	    this.esperarListagemDietas(listaDietas);
	    
	    when(mockedPrescreverProcedimentoEspecialRN.atualizarPrescricaoProcedimento(any(MpmPrescricaoProcedimento.class), anyString()))
	    .thenReturn(null);

	    // esperarRefreshPrescricaoMedica();

	    systemUnderTest.atualizarPrescricaoPendente(prescricaoPendete, NOME_MICROCOMPUTADOR);
	    
	    expectFlush();

	    Assert.assertEquals("Situação da prescrição deve ser Livre", prescricaoPendete.getSituacao(), DominioSituacaoPrescricao.L);

	    Assert.assertNull("DthrMovimento deve ser null", prescricaoPendete.getDthrMovimento());

	    Assert.assertEquals("dthrInicioMvtoPendete deve ser igual a antiga data de movimento",
		    prescricaoPendete.getDthrInicioMvtoPendente(), dataAtual);

	    for (MpmPrescricaoProcedimento procedimento : listaProcedimentos) {
		Assert.assertEquals("Os procedimentos devem estar pendentes", procedimento.getIndPendente(),
			DominioIndPendenteItemPrescricao.P);
	    }

	    for (MpmPrescricaoMdto medicamento : listaMedicamentos) {
		Assert.assertEquals("Os medicamentos devem estar pendentes", medicamento.getIndPendente(),
			DominioIndPendenteItemPrescricao.P);
	    }

	    for (MpmPrescricaoCuidado cuidadoI : listaCuidados) {
		Assert.assertEquals("Os cuidados devem estar pendentes", cuidadoI.getIndPendente(), DominioIndPendenteItemPrescricao.P);
	    }

	    for (MpmPrescricaoDieta dietaI : listaDietas) {
		Assert.assertEquals("As dietas devem estar pendentes", dietaI.getIndPendente(), DominioIndPendenteItemPrescricao.P);
	    }

	} catch (BaseException e) {
	    Assert.fail(e.getLocalizedMessage());
	}

    }

    /**
     * Testa o caminho feliz da funcionalidade de tornar uma prescrição pendente
     * quando apenas as chaves primárias são informadas.
     */
    @Test
    public void atualizarPrescricaoPendentePorIdTest() {

	Date dataAtual = new Date();

	MpmPrescricaoMedica prescricaoPendete = new MpmPrescricaoMedica();

	prescricaoPendete.setDthrMovimento(dataAtual);
	prescricaoPendete.setSituacao(DominioSituacaoPrescricao.U);

	List<AbsSolicitacoesHemoterapicas> listaSolicitacoes = new ArrayList<AbsSolicitacoesHemoterapicas>();
	AbsSolicitacoesHemoterapicas solicitacao1 = new AbsSolicitacoesHemoterapicas();
	AbsSolicitacoesHemoterapicas solicitacao2 = new AbsSolicitacoesHemoterapicas();
	listaSolicitacoes.add(solicitacao1);
	listaSolicitacoes.add(solicitacao2);

	List<MpmPrescricaoProcedimento> listaProcedimentos = new ArrayList<MpmPrescricaoProcedimento>();
	MpmPrescricaoProcedimento procedimento1 = new MpmPrescricaoProcedimento();
	MpmPrescricaoProcedimento procedimento2 = new MpmPrescricaoProcedimento();
	listaProcedimentos.add(procedimento1);
	listaProcedimentos.add(procedimento2);

	List<MpmPrescricaoMdto> listaMedicamentos = new ArrayList<MpmPrescricaoMdto>();
	MpmPrescricaoMdto medicamento1 = new MpmPrescricaoMdto();
	MpmPrescricaoMdto mdicamento2 = new MpmPrescricaoMdto();
	listaMedicamentos.add(medicamento1);
	listaMedicamentos.add(mdicamento2);

	List<MpmPrescricaoCuidado> listaCuidados = new ArrayList<MpmPrescricaoCuidado>();
	MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
	MpmPrescricaoCuidado cuidado2 = new MpmPrescricaoCuidado();
	listaCuidados.add(cuidado);
	listaCuidados.add(cuidado2);

	List<MpmPrescricaoDieta> listaDietas = new ArrayList<MpmPrescricaoDieta>();
	MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
	MpmPrescricaoDieta dieta2 = new MpmPrescricaoDieta();
	listaDietas.add(dieta);
	listaDietas.add(dieta2);

	try {
	    // expectations
	    

	    this.esperarObterPrescricaoChavePrimaria(prescricaoPendete);

	    this.esperarListagemSolicitacoes(listaSolicitacoes);

	    this.esperarListagemProcedimentos(listaProcedimentos);

	    this.esperarListagemMedicamentos(listaMedicamentos);

	    // this.esperarPersistirMedicamento();

	    this.esperarListagemCuidados(listaCuidados);

	    this.esperarListagemDietas(listaDietas);
	    
	    when(mockedPrescreverProcedimentoEspecialRN.atualizarPrescricaoProcedimento(any(MpmPrescricaoProcedimento.class), anyString()))
	    .thenReturn(null);

	    // esperarRefreshPrescricaoMedica();

	    systemUnderTest.atualizarPrescricaoPendente(1, 1, NOME_MICROCOMPUTADOR);
	    
	    expectFlush();

	    Assert.assertEquals("Situação da prescrição deve ser Livre", prescricaoPendete.getSituacao(), DominioSituacaoPrescricao.L);

	    Assert.assertNull("DthrMovimento deve ser null", prescricaoPendete.getDthrMovimento());

	    Assert.assertEquals("dthrInicioMvtoPendete deve ser igual a antiga data de movimento",
		    prescricaoPendete.getDthrInicioMvtoPendente(), dataAtual);

	    for (MpmPrescricaoProcedimento procedimento : listaProcedimentos) {
		Assert.assertEquals("Os procedimentos devem estar pendentes", procedimento.getIndPendente(),
			DominioIndPendenteItemPrescricao.P);
	    }

	    for (MpmPrescricaoMdto medicamento : listaMedicamentos) {
		Assert.assertEquals("Os medicamentos devem estar pendentes", medicamento.getIndPendente(),
			DominioIndPendenteItemPrescricao.P);
	    }

	    for (MpmPrescricaoCuidado cuidadoI : listaCuidados) {
		Assert.assertEquals("Os cuidados devem estar pendentes", cuidadoI.getIndPendente(), DominioIndPendenteItemPrescricao.P);
	    }

	    for (MpmPrescricaoDieta dietaI : listaDietas) {
		Assert.assertEquals("As dietas devem estar pendentes", dietaI.getIndPendente(), DominioIndPendenteItemPrescricao.P);
	    }

	} catch (BaseException e) {
	    Assert.fail(e.getLocalizedMessage());
	}

    }

    /**
     * Testa a funcionalidade de tornar uma prescrição pendente quando a
     * prescricao passada e nula.
     */
    @Test
    public void atualizarPrescricaoPendenteNulaTest() {
	MpmPrescricaoMedica prescricaoPendete = null;

	try {
	    systemUnderTest.atualizarPrescricaoPendente(prescricaoPendete, NOME_MICROCOMPUTADOR);
	    
	    expectNoFlush();

	    Assert.fail();

	} catch (BaseException e) {
	    Assert.assertEquals(e.getCode(), AtualizarPrescricaoPendenteRN.AtualizarPrescricaoPendenteExceptionCode.PRESCRICAO_NULA);
	}

    }

    /**
     * Testa o caminho feliz da funcionalidade de tornar uma prescrição
     * pendente.
     */
    @Test
    public void atualizarPrescricaoPendenteDtHrMvtPendenteNaoNuloTest() {

	Date dataAtual = new Date();
	Date dataMvtoPendente = DateUtil.adicionaDias(dataAtual, -1);

	MpmPrescricaoMedica prescricaoPendete = new MpmPrescricaoMedica();

	prescricaoPendete.setDthrMovimento(dataAtual);
	prescricaoPendete.setSituacao(DominioSituacaoPrescricao.U);
	prescricaoPendete.setDthrInicioMvtoPendente(dataMvtoPendente);

	List<AbsSolicitacoesHemoterapicas> listaSolicitacoes = new ArrayList<AbsSolicitacoesHemoterapicas>();
	AbsSolicitacoesHemoterapicas solicitacao1 = new AbsSolicitacoesHemoterapicas();
	AbsSolicitacoesHemoterapicas solicitacao2 = new AbsSolicitacoesHemoterapicas();
	listaSolicitacoes.add(solicitacao1);
	listaSolicitacoes.add(solicitacao2);

	List<MpmPrescricaoProcedimento> listaProcedimentos = new ArrayList<MpmPrescricaoProcedimento>();
	MpmPrescricaoProcedimento procedimento1 = new MpmPrescricaoProcedimento();
	MpmPrescricaoProcedimento procedimento2 = new MpmPrescricaoProcedimento();
	listaProcedimentos.add(procedimento1);
	listaProcedimentos.add(procedimento2);

	List<MpmPrescricaoMdto> listaMedicamentos = new ArrayList<MpmPrescricaoMdto>();
	MpmPrescricaoMdto medicamento1 = new MpmPrescricaoMdto();
	MpmPrescricaoMdto mdicamento2 = new MpmPrescricaoMdto();
	listaMedicamentos.add(medicamento1);
	listaMedicamentos.add(mdicamento2);

	List<MpmPrescricaoCuidado> listaCuidados = new ArrayList<MpmPrescricaoCuidado>();
	MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
	MpmPrescricaoCuidado cuidado2 = new MpmPrescricaoCuidado();
	listaCuidados.add(cuidado);
	listaCuidados.add(cuidado2);

	List<MpmPrescricaoDieta> listaDietas = new ArrayList<MpmPrescricaoDieta>();
	MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
	MpmPrescricaoDieta dieta2 = new MpmPrescricaoDieta();
	listaDietas.add(dieta);
	listaDietas.add(dieta2);

	try {

	    this.esperarListagemSolicitacoes(listaSolicitacoes);

	    this.esperarListagemProcedimentos(listaProcedimentos);

	    this.esperarListagemMedicamentos(listaMedicamentos);

	    // this.esperarPersistirMedicamento();

	    this.esperarListagemCuidados(listaCuidados);

	    this.esperarListagemDietas(listaDietas);

	    when(mockedPrescreverProcedimentoEspecialRN.atualizarPrescricaoProcedimento(any(MpmPrescricaoProcedimento.class), anyString()))
		    .thenReturn(null);

	    // esperarRefreshPrescricaoMedica();

	    systemUnderTest.atualizarPrescricaoPendente(prescricaoPendete, NOME_MICROCOMPUTADOR);
	    
	    expectFlush();

	    Assert.assertEquals("Situação da prescrição deve ser Livre", prescricaoPendete.getSituacao(), DominioSituacaoPrescricao.L);

	    Assert.assertNull("DthrMovimento deve ser null", prescricaoPendete.getDthrMovimento());

	    Assert.assertFalse("dthrInicioMvtoPendete ñ deve ser igual a data de movimento", prescricaoPendete.getDthrInicioMvtoPendente()
		    .equals(prescricaoPendete.getDthrMovimento()));

	    for (MpmPrescricaoProcedimento procedimento : listaProcedimentos) {
		Assert.assertEquals("Os procedimentos devem estar pendentes", procedimento.getIndPendente(),
			DominioIndPendenteItemPrescricao.P);
	    }

	    for (MpmPrescricaoMdto medicamento : listaMedicamentos) {
		Assert.assertEquals("Os medicamentos devem estar pendentes", medicamento.getIndPendente(),
			DominioIndPendenteItemPrescricao.P);
	    }

	    for (MpmPrescricaoCuidado cuidadoI : listaCuidados) {
		Assert.assertEquals("Os cuidados devem estar pendentes", cuidadoI.getIndPendente(), DominioIndPendenteItemPrescricao.P);
	    }

	    for (MpmPrescricaoDieta dietaI : listaDietas) {
		Assert.assertEquals("As dietas devem estar pendentes", dietaI.getIndPendente(), DominioIndPendenteItemPrescricao.P);
	    }

	} catch (BaseException e) {
	    Assert.fail(e.getLocalizedMessage());
	}

    }

    protected Log getLog() {
	return LogFactory.getLog(this.getClass());
    }

}
