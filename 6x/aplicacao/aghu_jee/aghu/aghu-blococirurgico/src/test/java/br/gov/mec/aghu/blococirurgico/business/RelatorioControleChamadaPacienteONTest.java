package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;

@Ignore
public class RelatorioControleChamadaPacienteONTest extends AGHUBaseUnitTest<RelatorioControleChamadaPacienteON> {

	@Mock
	private MbcCirurgiasDAO mockMbcCirurgiasDAO;
	@Mock
	private EscalaCirurgiasON mockEscalaCirurgiasON;	
		

//	@Before
//	public void doBeforeEachTestCase() {
//		mockingContext = new Mockery() {{
//			setImposteriser(ClassImposteriser.INSTANCE);
//		}};
//
//		mockMbcCirurgiasDAO = mockingContext.mock(MbcCirurgiasDAO.class);
//		mockEscalaCirurgiasON = mockingContext.mock(EscalaCirurgiasON.class);		
//		
//		systemUnderTest = new RelatorioControleChamadaPacienteON(){
//
//			private static final long serialVersionUID = 1L;
//
//			protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
//				return mockMbcCirurgiasDAO;
//			};
//
//			protected EscalaCirurgiasON getEscalaCirurgiasON() {
//				return mockEscalaCirurgiasON;
//			};			
//		};
//	}

	@Test
	public void testRecuperarRelatorioControleChamadaPacienteVO() throws ApplicationBusinessException {

//		mockingContext.checking(new Expectations() {{
//			oneOf(mockMbcCirurgiasDAO).pesquisarCirurgiasPorUnidadeDataSolicitacao(Short.valueOf("131"), null, new Date(2000-01-01), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, MbcCirurgias.Fields.NOME);
//			will(returnValue(getListaCirurgias()));
//
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockEscalaCirurgiasON).pesquisaQuarto(with(any(Integer.class))); 
//			will(returnValue(with(any(String.class))));
//		}});

		Assert.assertNotNull(systemUnderTest.recuperarRelatorioControleChamadaPacienteVO(Short.valueOf("131"), new Date(2000-01-01)));
	}

	private List<MbcCirurgias> getListaCirurgias() {
		
		List<MbcCirurgias> list = new ArrayList<MbcCirurgias>();
		MbcCirurgias cirurgia = new MbcCirurgias();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setNome("Maria");
		
		MbcSalaCirurgicaId salaId = new MbcSalaCirurgicaId (Short.valueOf("131"),Short.valueOf("1"));
		MbcSalaCirurgica sala = new MbcSalaCirurgica();
		sala.setId(salaId);

		cirurgia.setDataInicioCirurgia(new Date());
		cirurgia.setPaciente(paciente);
		cirurgia.setSalaCirurgica(sala);
		cirurgia.setOrigemPacienteCirurgia(DominioOrigemPacienteCirurgia.A);
		cirurgia.setNumeroAgenda(Short.valueOf("123"));
		cirurgia.setPaciente(new AipPacientes());
		cirurgia.getPaciente().setCodigo(Integer.valueOf("123456"));
		cirurgia.setSeq(Integer.valueOf("123"));
		
		list.add(cirurgia);
		return list;	
	}	

}