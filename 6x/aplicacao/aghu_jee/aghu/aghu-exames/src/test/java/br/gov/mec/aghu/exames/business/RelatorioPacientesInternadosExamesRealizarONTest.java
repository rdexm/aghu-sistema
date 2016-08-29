package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelRecomendacaoExameDAO;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class RelatorioPacientesInternadosExamesRealizarONTest extends AGHUBaseUnitTest<RelatorioPacientesInternadosExamesRealizarON>{
	
	private static final Log log = LogFactory.getLog(RelatorioPacientesInternadosExamesRealizarONTest.class);
	
	@Mock
	private AelRecomendacaoExameDAO mockedAelRecomendacaoExameDAO;
	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	@Mock
	private AelItemSolicitacaoExameDAO mockedAelItemSolicitacaoExameDAO;
	@Mock
	private AelItemHorarioAgendadoDAO  mockedAelItemHorarioAgendadoDAO;
	
	
	//@Test
	public void pesquisarRelatorioPacientesInternadosExamesRealizar() throws BaseException{
		AghUnidadesFuncionais unidFunc = new AghUnidadesFuncionais();
		AelUnfExecutaExames unidade = new AelUnfExecutaExames();
		AelExamesMaterialAnalise material = new AelExamesMaterialAnalise();
		AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		materialAnalise.setDescricao("");
		material.setAelMateriaisAnalises(materialAnalise);
		material.setId(new AelExamesMaterialAnaliseId());
		unidade.setAelExamesMaterialAnalise(material);
		AelExames exame = new AelExames();
		exame.setDescricaoUsual("");
		final List<AelItemSolicitacaoExames> lista = new ArrayList<AelItemSolicitacaoExames>();
		AelItemSolicitacaoExames item = new AelItemSolicitacaoExames();
		item.setId(new AelItemSolicitacaoExamesId());
		item.setExame(exame);
		item.setAelUnfExecutaExames(unidade);

		// AutoFillPojos.fillFieldsRecursivo(item, 0, 2);		
		lista.add(item);
		
		Mockito.when(mockedAelItemSolicitacaoExameDAO.buscarRelatorioPacientesInternadosJejumNpo(Mockito.any(AghUnidadesFuncionais.class))).thenReturn(lista);
		Mockito.when(mockedAelItemHorarioAgendadoDAO.buscarMenorHedDthrAgenda(Mockito.anyInt(), Mockito.anyShort())).thenReturn(new Date());
		
//		mockingContext.checking(new Expectations() {{
//    		allowing(mockedAelItemSolicitacaoExameDAO).buscarRelatorioPacientesInternadosJejumNpo(with(any(AghUnidadesFuncionais.class)));
//    		will(returnValue(lista));
//    		allowing(mockedAelRecomendacaoExameDAO).obterRecomendacoesExameResponsavelP(with(any(String.class)),with(any(Integer.class)));
//    		allowing(mockedPrescricaoMedicaFacade).buscarResumoLocalPaciente(with(any(AghAtendimentos.class)));
//    		allowing(mockedAelItemSolicitacaoExameDAO).buscarRelatorioPacientesInternadosPreparo(with(any(AghUnidadesFuncionais.class)));
//    		allowing(mockedAelItemSolicitacaoExameDAO).buscarRelatorioPacientesInternadosDietaDiferenciada(with(any(AghUnidadesFuncionais.class)));
//    		allowing(mockedAelItemSolicitacaoExameDAO).buscarRelatorioPacientesInternadosUnidadeEmergencia(with(any(AghUnidadesFuncionais.class)));
//    		allowing(mockedAelItemSolicitacaoExameDAO).buscarRelatorioPacientesInternadosTodos(with(any(AghUnidadesFuncionais.class)));
//    		allowing(mockedAelItemHorarioAgendadoDAO).buscarMenorHedDthrAgenda(with(any(Integer.class)),with(any(Short.class)));will(returnValue(new Date()));
//    		
//		}});

    	try {
			systemUnderTest.pesquisarRelatorioPacientesInternadosExamesRealizar(unidFunc,DominioSimNao.S,true,true,true,true,true);
			
			Assert.assertTrue(true);
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertFalse(true);
			
		}
	}
	
}
