package br.gov.mec.aghu.faturamento.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.faturamento.dao.FatArqEspelhoProcedAmbDAO;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoProcedAmbDAO;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class FaturamentoFatkAmbRNTest extends AGHUBaseUnitTest<FaturamentoFatkAmbRN>{

	@Mock
	private FatCompetenciaDAO mockedFatCompetenciaDAO;
	@Mock
	private FatArqEspelhoProcedAmbDAO mockedFatArqEspelhoProcedAmbDAO;
	@Mock
	private FatEspelhoProcedAmbDAO mockedFatEspelhoProcedAmbDAO;
	@Mock
	private FaturamentoFatkPmrRN mockedFaturamentoFatkPmrRN;
	@Mock
	private FaturamentoON mockedFaturamentoON;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private ISchedulerFacade mockedSchedulerFacade;
	@Mock
	private IPacienteFacade mockedPacienteFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	
	@Before
	public void doBefore() throws Exception {
		whenObterServidorLogado();
	}

	@Test(expected = ApplicationBusinessException.class)
	public void rnFatpEncAmbNew1() throws Exception {

		List<FatCompetencia> listaCompetencias = new ArrayList<FatCompetencia>();
		Mockito.when(mockedFatCompetenciaDAO.obterCompetenciasPorModuloESituacoes(Mockito.any(DominioModuloCompetencia.class), Mockito.any(DominioSituacaoCompetencia.class)))
		.thenReturn(listaCompetencias);

		systemUnderTest.rnFatpEncAmbNew(Boolean.TRUE, new Date(), new AghJobDetail(), "AGHU_2K46", new Date());
	}
	
//	@Test(expected = ApplicationBusinessException.class)
//	public void rnFatpEncAmbNew2() throws Exception {
//	
//		mockingContext.checking(new Expectations() {
//			{
//				List<FatCompetencia> listaCompetencias = new ArrayList<FatCompetencia>();
//				FatCompetencia competencia = new FatCompetencia();
//				listaCompetencias.add(competencia);
//				
//				allowing(mockedFatCompetenciaDAO).obterCompetenciasPorModuloESituacoes(with(DominioModuloCompetencia.AMB), with(any(DominioSituacaoCompetencia.class)));
//				will(returnValue(listaCompetencias));
//				
//				List<FatCompetencia> listaCompetencias2 = new ArrayList<FatCompetencia>();
//				
//				allowing(mockedFatCompetenciaDAO).obterCompetenciasPorModuloESituacoes(with(DominioModuloCompetencia.APAC), with(any(DominioSituacaoCompetencia.class)));
//				will(returnValue(listaCompetencias2));
//			}
//		});
//		
//		systemUnderTest.rnFatpEncAmbNew(Boolean.TRUE, new Date(), new AghJobDetail());
//	}	

//	@Test
//	public void rnFatpEncAmbNewPrevia() throws Exception {
//	
//		mockingContext.checking(new Expectations() {
//			{
//				List<FatCompetencia> listaCompetencias = new ArrayList<FatCompetencia>();
//				FatCompetencia competencia = new FatCompetencia();
//				FatCompetenciaId id = new FatCompetenciaId(DominioModuloCompetencia.AMB, 1, 2001, new Date());
//				competencia.setId(id);
//				listaCompetencias.add(competencia);
//				
//				allowing(mockedFatCompetenciaDAO).obterCompetenciasPorModuloESituacoes(with(any(DominioModuloCompetencia.class)), with(any(DominioSituacaoCompetencia.class)));
//				will(returnValue(listaCompetencias));
//			
//				allowing(mockedFatArqEspelhoProcedAmbDAO).removeArqEspelhoProcedAmb(
//						with(any(Date.class)), 
//						with(any(Integer.class)), 
//						with(any(Integer.class)), 
//						with(any(DominioModuloCompetencia.class)), 
//						with(any(DominioTipoFormularioDataSus.class)));
//				
//				allowing(mockedFatEspelhoProcedAmbDAO).removeEspelhoProcedAmb(
//						with(any(Date.class)), 
//						with(any(Integer.class)), 
//						with(any(Integer.class)), 
//						with(any(DominioModuloCompetencia.class)), 
//						with(any(DominioTipoFormularioDataSus.class)));
//			
//				allowing(mockedFaturamentoFatkPmrRN).geraEspelhoFaturamentoAmbulatorio(
//						with(any(FatCompetencia.class)), 
//						with(any(Date.class)), 
//						with(any(Boolean.class)),
//						with(any(AghJobDetail.class)),
//						with(any(String.class)),
//						with(any(RapServidores.class)),
//						with(any(Date.class)));
//				
//				allowing(mockedSchedulerFacade).adicionarLog(with(any(AghJobDetail.class)), with(any(String.class)));
//			}			
//		});
//		
//		systemUnderTest.rnFatpEncAmbNew(Boolean.TRUE, new Date(), new AghJobDetail(), "AGHU_2K46", null, new Date());
//	}	

    private void whenObterServidorLogado() throws BaseException {
		RapServidores rap =  new RapServidores(new RapServidoresId(1, (short) 1)) ;
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
    }

}
