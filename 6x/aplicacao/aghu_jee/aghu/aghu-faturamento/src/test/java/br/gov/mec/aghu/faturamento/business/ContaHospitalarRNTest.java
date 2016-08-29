package br.gov.mec.aghu.faturamento.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.faturamento.dao.FatCaractItemProcHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatContaHospitalarJnDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudeDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemProcHospTranspDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoSaidaPacienteDAO;
import br.gov.mec.aghu.faturamento.dao.FatPacienteTransplantesDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedimentoCboDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedimentoRegistroDAO;
import br.gov.mec.aghu.faturamento.dao.FatSituacaoSaidaPacienteDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoCaractItensDAO;
import br.gov.mec.aghu.faturamento.dao.VFatAssociacaoProcedimentoDAO;
import br.gov.mec.aghu.internacao.vo.FatItemContaHospitalarVO;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 *
 *
 */
public class ContaHospitalarRNTest extends AGHUBaseUnitTest<ContaHospitalarRN>{
	
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private FaturamentoON mockedFaturamentoON;
	@Mock
	private FaturamentoRN mockedFaturamentoRN;
	@Mock
	private VerificacaoFaturamentoSusRN mockedVerificacaoFaturamentoSusRN;
	@Mock
	private FatContasHospitalaresDAO mockedFatContasHospitalaresDAO;
	@Mock
	private FatItemContaHospitalarDAO mockedFatItemContaHospitalarDAO;
	@Mock
	private FatItensProcedHospitalarDAO mockedFatItensProcedHospitalarDAO;
	@Mock
	private FatConvenioSaudeDAO mockedFatConvenioSaudeDAO;
	@Mock
	private VFatAssociacaoProcedimentoDAO mockedVFatAssociacaoProcedimentoDAO;
	@Mock
	private FatMotivoSaidaPacienteDAO mockedFatMotivoSaidaPacienteDAO;
	@Mock
	private FatSituacaoSaidaPacienteDAO mockedFatSituacaoSaidaPacienteDAO;
	@Mock
	private FatLogErrorON mockedLogErrorON;
	@Mock
	private FatContaHospitalarJnDAO mockedFatContaHospitalarJnDAO;
	@Mock
	private FatCaractItemProcHospDAO mockedFatCaractItemProcHospDAO;
	@Mock
	private FatTipoCaractItensDAO mockedFatTipoCaractItensDAO;
	@Mock
	private FatContasInternacaoDAO mockedFatContasInternacaoDAO;
	@Mock
	private FatItemProcHospTranspDAO mockedFatItemProcHospTranspDAO;
	@Mock
	private FatPacienteTransplantesDAO mockedFatPacienteTransplantesDAO;
	@Mock
	private FaturamentoFatkInterfaceMcoRN mockedFaturamentoFatkInterfaceMcoRN;
	@Mock
	private FaturamentoFatkInterfaceAfaRN mockedFaturamentoFatkInterfaceAfaRN;
	@Mock
	private FaturamentoFatkInterfaceAnuRN mockedFaturamentoFatkInterfaceAnuRN;
	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
	@Mock
	private FatProcedimentoCboDAO mockedFatProcedimentoCboDAO;
	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;
	@Mock
	private ItemContaHospitalarON mockedItemContaHospitalarON;
	@Mock	
	private FatProcedimentoRegistroDAO mockedFatProcedimentoRegistroDAO;
	@Mock
	private IServidorLogadoFacade mockedServidorLogadoFacade;

	//################################################//
	//## TESTES PARA executarCancelamentoDeNtpDupla ##//
	//################################################//
	
	/**
	 * Testa a execucao com dados nulos
	 * @throws BaseException 
	 */
	@Test
	public void executarCancelamentoDeNtpDuplaDadosNulosTest() throws BaseException{
		Mockito.when(mockedFatItemContaHospitalarDAO.listarFatItemContaHospitalarPorContaHospitalar(Mockito.anyInt())).thenReturn(null);

		systemUnderTest.executarCancelamentoDeNtpDupla(null, new Date());
	}

	/**
	 * Testa a execucao com dados válidos.
	 * @throws Exception 
	 */
	@Test
	public void executarCancelamentoDeNtpDuplaDadosValidosTest() throws Exception{

		List<FatItemContaHospitalarVO> listaItemContaHospitalarVO = new ArrayList<FatItemContaHospitalarVO>();
		FatItemContaHospitalarVO vo = new FatItemContaHospitalarVO();
		vo.setCount(2);
		listaItemContaHospitalarVO.add(vo);
		Mockito.when(mockedFatItemContaHospitalarDAO.listarFatItemContaHospitalarPorContaHospitalar(Mockito.anyInt())).thenReturn(listaItemContaHospitalarVO);

		List<FatItemContaHospitalar> listaItensContaHospitalar = new ArrayList<FatItemContaHospitalar>();
		FatItemContaHospitalar ich = new FatItemContaHospitalar();
		FatItemContaHospitalarId id = new FatItemContaHospitalarId(12, (short)1);
		ich.setId(id);
		listaItensContaHospitalar.add(ich);
		Mockito.when(mockedFatItemContaHospitalarDAO.listarItensContaHospAtivaPorProcedHospInternoDthrRealizadoEContaHosp(Mockito.anyInt(), Mockito.any(Date.class), Mockito.anyInt())).thenReturn(listaItensContaHospitalar);

		Mockito.when(mockedFaturamentoFacade.clonarItemContaHospitalar(Mockito.any(FatItemContaHospitalar.class))).thenReturn(ich);
		
		systemUnderTest.executarCancelamentoDeNtpDupla(12, new Date());
	}
	
//	/**
//	 * Testa a execucao com dados válidos.
//	 *  
//	 */
//	@Test
//	public void rnCthcValidaCboRespTest() throws BaseException{
//		
//		final RapServidores servidor = obterRapServidores();
//		
//		final RapPessoaTipoInformacoes pessoaTI = new RapPessoaTipoInformacoes();
//		RapPessoaTipoInformacoesId id = new RapPessoaTipoInformacoesId(PES_CODIGO, TII_SEQ);
//		pessoaTI.setId(id);
//		pessoaTI.setValor(TII_VALOR);
//		
//		final List<RapPessoaTipoInformacoes> listaPessoaTI = Collections.singletonList(pessoaTI);
//		
//		final FatCaractFinanciamento fatCaractFinanciamento = new FatCaractFinanciamento();
//		fatCaractFinanciamento.setCodigo(TII_VALOR);
//		
//		RnCthcValidaCboRespVO rnCthcValidaCboRespExpected = new RnCthcValidaCboRespVO();		
//		rnCthcValidaCboRespExpected.setCpf(servidor.getPessoaFisica().getCpf());
//		rnCthcValidaCboRespExpected.setMatriculaProf(servidor.getId().getMatricula());
//		rnCthcValidaCboRespExpected.setVinculoProf(servidor.getId().getVinCodigo());
//		rnCthcValidaCboRespExpected.setCbo(pessoaTI.getValor());
//		rnCthcValidaCboRespExpected.setRetorno(Boolean.TRUE);
//		
//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedRegistroColaboradorFacade).obterRapServidoresPorChavePrimaria(with(any(RapServidoresId.class)));
//				will(returnValue(servidor));
//				
//				oneOf(mockedRegistroColaboradorFacade).listarPorPessoaFisicaTipoInformacao(with(any(Integer.class)), with(any(Short[].class)));
//				will(returnValue(listaPessoaTI));
//				
//				oneOf(mockedAghuFacade).buscarAghParametro(AghuParametrosEnum.P_CBO_CIRURGIAO);
//				will(returnValue(new AghParametros(TIPO)));
//				
//				oneOf(mockedAghuFacade).buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
//				will(returnValue(new AghParametros(SIT_LIB)));
//				
//				oneOf(mockedAghuFacade).buscarAghParametro(AghuParametrosEnum.P_ATO_ANESTESISTA);
//				will(returnValue(new AghParametros(TIPO_ATO)));
//				
//				oneOf(mockedAghuFacade).buscarAghParametro(AghuParametrosEnum.P_COMPLEX_MDO_FINANCIAMENTO);
//				will(returnValue(new AghParametros(TII_VALOR)));
//				
//				oneOf(mockedAghuFacade).buscarAghParametro(AghuParametrosEnum.P_MEDICO_ANESTESIOLOGISTA);
//				will(returnValue(new AghParametros(ANESTES)));
//								
//				oneOf(mockedFaturamentoRN).verificarCaracteristicaExame(with(any(Integer.class)), with(any(Short.class)), with(any(DominioFatTipoCaractItem.class)));
//				will(returnValue(Boolean.TRUE));
//				
//				oneOf(mockedFatProcedimentoCboDAO).obterPorChavePrimaria(with(any(FatProcedimentoCboId.class)));
//				will(returnValue(new FatProcedimentoCbo()));
//				
//				oneOf(mockedFatItensProcedHospitalarDAO).buscarComplexidade(with(any(Short.class)), with(any(Integer.class)), with(any(DominioSituacao.class)));
//				will(returnValue(fatCaractFinanciamento));				
//			}
//		});
//		
//		RnCthcValidaCboRespVO rnCthcValidaCboRespVO = systemUnderTest.rnCthcValidaCboResp(1, MATRICULA,VIN_CODIGO, null, null, null,null, TIPO, (short)1, 1, TIPO_ATO.byteValue());
//		
//		Assert.assertEquals(rnCthcValidaCboRespExpected, rnCthcValidaCboRespVO);
//		
//	}	
//	
//	/** Testa a execucao com dados válidos.
//	 *  
//	 */
//	@Test
//	public void rnCthcBuscaDadosProfTest() throws BaseException{
//						
//		RnCthcBuscaDadosProfVO rnCthcValidaCboRespExpected = new RnCthcBuscaDadosProfVO();		
//		rnCthcValidaCboRespExpected.setRetorno(Boolean.TRUE);
//		
//		mockingContext.checking(new Expectations() {
//			{	
//				oneOf(mockedAghuFacade).buscarAghParametro(AghuParametrosEnum.P_SERVICOS_PROFISSIONAIS_ESPELHO);
//				will(returnValue(new AghParametros(ATO_PROC_ESP)));
//								
//				oneOf(mockedFatProcedimentoRegistroDAO).listarPorCodigosRegistroEPorIph(with(any(String[].class)), with(any(Short.class)), with(any(Integer.class)));
//				will(returnValue(null));				
//			}
//		});
//		
//		RnCthcBuscaDadosProfVO rnCthcBuscaDadosProfVO = systemUnderTest.rnCthcBuscaDadosProf(null, null, null, null, null, null, null, null, null, null, null, null);
//		
//		Assert.assertEquals(rnCthcValidaCboRespExpected, rnCthcBuscaDadosProfVO);
//	}	
}
