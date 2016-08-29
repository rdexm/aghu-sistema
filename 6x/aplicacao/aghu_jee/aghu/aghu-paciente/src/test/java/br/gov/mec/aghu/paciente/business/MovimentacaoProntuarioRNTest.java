package br.gov.mec.aghu.paciente.business;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.dominio.DominioSituacaoMovimentoProntuario;
import br.gov.mec.aghu.dominio.DominioTipoEnvioProntuario;
import br.gov.mec.aghu.dominio.DominioTodosUltimo;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacUnidFuncionalSalas;
import br.gov.mec.aghu.model.AacUnidFuncionalSalasId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipSolicitantesProntuario;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.dao.AipMovimentacaoProntuarioDAO;
import br.gov.mec.aghu.paciente.dao.AipPacienteProntuarioDAO;
import br.gov.mec.aghu.paciente.dao.AipSolicitantesProntuarioDAO;
import br.gov.mec.aghu.paciente.vo.SolicitanteVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

public class MovimentacaoProntuarioRNTest extends AGHUBaseUnitTest<MovimentacaoProntuarioRN>{

	@Mock
	private IAmbulatorioFacade mockedAmbulatorioFacade;
	@Mock
	private AipSolicitantesProntuarioDAO mockedAipSolicitantesProntuarioDAO;
	@Mock
	private AipMovimentacaoProntuarioDAO mockedAipMovimentacaoProntuarioDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
    @Mock
    private AipPacienteProntuarioDAO mockedAipPacienteProntuarioDAO;
    

    @Before
    public void inicar() throws BaseException {
    	whenObterServidorLogado();
    }
    
	/**
	 * Testa Procedure AIPP_GERA_MVOL_EVENT 
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test
	public void aippGeraMvolEvent() throws ApplicationBusinessException {
		List<AacConsultas> listaConsultas = new ArrayList<AacConsultas>();
		AacConsultas consulta = new AacConsultas();
		AipPacientes paciente = new AipPacientes();
		paciente.setVolumes((short) 2);
		paciente.setProntuario(1000);
		consulta.setPaciente(paciente);
		AacGradeAgendamenConsultas aacGradeAgendamenConsultas = new AacGradeAgendamenConsultas();
		AacUnidFuncionalSalas aacUnidFuncionalSala = new AacUnidFuncionalSalas();
		AacUnidFuncionalSalasId id = new AacUnidFuncionalSalasId((short) 1, (byte)(1));
		aacUnidFuncionalSala.setId(id);
		aacGradeAgendamenConsultas.setAacUnidFuncionalSala(aacUnidFuncionalSala);
		consulta.setGradeAgendamenConsulta(aacGradeAgendamenConsultas);
		listaConsultas.add(consulta);

		Mockito.when(mockedAmbulatorioFacade.executarCursorConsulta(Mockito.anyInt())).thenReturn(listaConsultas);

		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(new BigDecimal(VALOR_MAXIMO_PRONTUARIO));

		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_LIMIT_PRONT_VIRTUAL)).thenReturn(parametro);

		List<SolicitanteVO> listaSolicitanteVO = new ArrayList<SolicitanteVO>();
		SolicitanteVO vo = new SolicitanteVO();
		vo.setVolumesManuseados(DominioTodosUltimo.T);
		listaSolicitanteVO.add(vo);

		Mockito.when(mockedAmbulatorioFacade.executaCursorSolicitante(Mockito.anyShort(), Mockito.anyByte(), Mockito.any(Date.class))).thenReturn(listaSolicitanteVO);

		AipSolicitantesProntuario solicitantesProntuario = new AipSolicitantesProntuario();
		Mockito.when(mockedAipSolicitantesProntuarioDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(solicitantesProntuario);

		systemUnderTest.aippGeraMvolEvent(1);
	}	
	
	/**
	 * Testa Trigger AIPT_MVP_BRI 
	 * @throws AGHUNegocioExceptionSemRollback 
	 * @throws AGHUNegocioException 
	 */
	@Test
	public void executarAntesInserirMovimentacaoProntuario() throws ApplicationBusinessException {
		List<AipMovimentacaoProntuarios> listaMovPront = new ArrayList<AipMovimentacaoProntuarios>();
		AipMovimentacaoProntuarios mp1 = new AipMovimentacaoProntuarios();
		mp1.setSituacao(DominioSituacaoMovimentoProntuario.R);
		listaMovPront.add(mp1);
		
		AipMovimentacaoProntuarios mp2 = new AipMovimentacaoProntuarios();
		mp2.setSituacao(DominioSituacaoMovimentoProntuario.N);
		listaMovPront.add(mp2);
		
		AipMovimentacaoProntuarios mp3 = new AipMovimentacaoProntuarios();
		mp3.setSituacao(DominioSituacaoMovimentoProntuario.S);
		listaMovPront.add(mp3);
		
		AipMovimentacaoProntuarios mp4 = new AipMovimentacaoProntuarios();
		mp4.setSituacao(DominioSituacaoMovimentoProntuario.Q);
		listaMovPront.add(mp4);
	
		Mockito.when(mockedAipMovimentacaoProntuarioDAO.pesquisarMovimentacaoPacienteProntuarioPorCodigoEVolume(Mockito.anyInt(), Mockito.anyShort(), Mockito.any(DominioTipoEnvioProntuario.class)))
		.thenReturn(listaMovPront);
		
		AipMovimentacaoProntuarios newMovPront = new AipMovimentacaoProntuarios();
		newMovPront.setSituacao((DominioSituacaoMovimentoProntuario.R));
		newMovPront.setTipoEnvio(DominioTipoEnvioProntuario.T);
		newMovPront.setVolumes((short) 1);
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(1);
		newMovPront.setPaciente(paciente);
		systemUnderTest.executarAntesInserirMovimentacaoProntuario(newMovPront);
	}		
	

	@Test
	public void executarAntesInserirMovimentacaoProntuario2() throws ApplicationBusinessException {
		List<AipMovimentacaoProntuarios> listaMovPront = new ArrayList<AipMovimentacaoProntuarios>();
		AipMovimentacaoProntuarios mp1 = new AipMovimentacaoProntuarios();
		mp1.setSituacao(DominioSituacaoMovimentoProntuario.R);
		listaMovPront.add(mp1);
		
		AipMovimentacaoProntuarios mp2 = new AipMovimentacaoProntuarios();
		mp2.setSituacao(DominioSituacaoMovimentoProntuario.N);
		listaMovPront.add(mp2);
		
		AipMovimentacaoProntuarios mp3 = new AipMovimentacaoProntuarios();
		mp3.setSituacao(DominioSituacaoMovimentoProntuario.S);
		listaMovPront.add(mp3);
		
		AipMovimentacaoProntuarios mp4 = new AipMovimentacaoProntuarios();
		mp4.setSituacao(DominioSituacaoMovimentoProntuario.Q);
		listaMovPront.add(mp4);
		
		Mockito.when(mockedAipMovimentacaoProntuarioDAO.pesquisarMovimentacaoPacienteProntuarioPorCodigoEVolume(Mockito.anyInt(), Mockito.anyShort(), Mockito.any(DominioTipoEnvioProntuario.class)))
		.thenReturn(listaMovPront);

		AacConsultas consulta = new AacConsultas();
		List<AacConsultas> listaConsulta = new ArrayList<AacConsultas>();
		listaConsulta.add(consulta);

		Mockito.when(mockedAmbulatorioFacade.pesquisarAacConsultasPorCodigoEData(Mockito.anyInt(), Mockito.any(Date.class)))
		.thenReturn(listaConsulta);
		
		AipMovimentacaoProntuarios newMovPront = new AipMovimentacaoProntuarios();
		newMovPront.setSituacao((DominioSituacaoMovimentoProntuario.Q));
		newMovPront.setTipoEnvio(DominioTipoEnvioProntuario.T);
		newMovPront.setVolumes((short) 1);
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(1);
		newMovPront.setPaciente(paciente);
		
		systemUnderTest.executarAntesInserirMovimentacaoProntuario(newMovPront);
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
