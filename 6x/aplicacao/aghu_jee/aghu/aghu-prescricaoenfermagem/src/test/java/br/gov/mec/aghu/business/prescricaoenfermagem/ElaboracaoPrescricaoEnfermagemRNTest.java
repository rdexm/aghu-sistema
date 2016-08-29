package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeDiagnosticoId;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelDiagnosticoId;
import br.gov.mec.aghu.model.EpeFatRelacionado;
import br.gov.mec.aghu.model.EpePrescCuidDiagnostico;
import br.gov.mec.aghu.model.EpePrescCuidDiagnosticoId;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeFatRelDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeFatRelacionadoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescCuidDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoEtiologiaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ElaboracaoPrescricaoEnfermagemRNTest extends AGHUBaseUnitTest<ElaboracaoPrescricaoEnfermagemRN> {

	@Mock
	private EpeDiagnosticoDAO mockedEpeDiagnosticoDAO;	
	@Mock
	private EpeFatRelacionadoDAO mockedEpeFatRelacionadoDAO;	
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private EpeFatRelDiagnosticoDAO mockedEpeFatRelDiagnosticoDAO;
	@Mock
	private EpePrescCuidDiagnosticoDAO mockedEpePrescCuidDiagnosticoDAO;
	@Mock
	private IAmbulatorioFacade mockedAmbulatorioFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	
	/**
	 * Deve retornar objeto nulo.
	 * 
	 */
	@Test
	public void testGerarDiagnosticosEnfermagem()
			throws BaseException {

		whenObterServidorLogado();
		
		AghAtendimentos aghAtendimento = new AghAtendimentos();
		
		List<EpePrescCuidDiagnostico> listaPrescCuidDiagnostico = new ArrayList<EpePrescCuidDiagnostico>();
		EpePrescCuidDiagnostico epePrescCuidDiagnostico = new EpePrescCuidDiagnostico();
		EpePrescCuidDiagnosticoId epePrescCuidDiagnosticoId = new EpePrescCuidDiagnosticoId();
		epePrescCuidDiagnosticoId.setCdgCuiSeq(Short.valueOf("1"));
		epePrescCuidDiagnosticoId.setCdgFdgDgnSequencia(Short.valueOf("1"));
		epePrescCuidDiagnosticoId.setCdgFdgDgnSnbGnbSeq(Short.valueOf("1"));
		epePrescCuidDiagnosticoId.setCdgFdgDgnSnbSequencia(Short.valueOf("1"));
		epePrescCuidDiagnosticoId.setPrcAtdSeq(1);
		epePrescCuidDiagnosticoId.setPrcSeq(1);
		epePrescCuidDiagnostico.setId(epePrescCuidDiagnosticoId);
		listaPrescCuidDiagnostico.add(epePrescCuidDiagnostico);
		EpeDiagnostico diagnostico = new EpeDiagnostico();
		diagnostico.setDescricao("descricao diagnostico");
		EpeFatRelacionado fatRelacionado = new EpeFatRelacionado();
		fatRelacionado.setDescricao("descricao fatRelacionado");
		EpeFatRelDiagnosticoId fatRelDiagnosticoId = new EpeFatRelDiagnosticoId();
		fatRelDiagnosticoId.setDgnSequencia(Short.valueOf("1"));
		fatRelDiagnosticoId.setDgnSnbGnbSeq(Short.valueOf("1"));
		fatRelDiagnosticoId.setDgnSnbSequencia(Short.valueOf("1"));
		fatRelDiagnosticoId.setFreSeq(Short.valueOf("1"));
		EpeFatRelDiagnostico fatRelDiagnostico = new EpeFatRelDiagnostico();
		fatRelDiagnostico.setId(fatRelDiagnosticoId);
		MamDiagnostico diagnosticoAux = new MamDiagnostico();
		diagnosticoAux.setFatRelDiagnostico(fatRelDiagnostico);
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(1);
		diagnosticoAux.setPaciente(paciente);
		List<MamDiagnostico> listaDiagnostico = new ArrayList<MamDiagnostico>();
		listaDiagnostico.add(diagnosticoAux);
		
		Mockito.when(mockedAmbulatorioFacade.listarDiagnosticoValidadoPorAtendimento(Mockito.anyInt())).thenReturn(listaDiagnostico);
		Mockito.when(mockedEpeDiagnosticoDAO.obterPorChavePrimaria(Mockito.any(EpeDiagnosticoId.class))).thenReturn(diagnostico);
		Mockito.when(mockedEpeFatRelacionadoDAO.obterPorChavePrimaria(Mockito.anyShort())).thenReturn(fatRelacionado);
		Mockito.when(mockedAghuFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).thenReturn(new AghAtendimentos());
		Mockito.when(mockedEpeFatRelDiagnosticoDAO.obterPorChavePrimaria(Mockito.any(EpeFatRelDiagnosticoId.class))).thenReturn(fatRelDiagnostico);
		Mockito.when(mockedEpePrescCuidDiagnosticoDAO.listarGerarMamDignosticos(Mockito.anyInt())).thenReturn(new ArrayList<DiagnosticoEtiologiaVO>());
		
		systemUnderTest.gerarDiagnosticosEnfermagem(Integer.valueOf(1));
		
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
