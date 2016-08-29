package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeSituacaoDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacGradeSituacao;
import br.gov.mec.aghu.model.AacGradeSituacaoId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterGradeAgendamentoRNTest extends AGHUBaseUnitTest<ManterGradeAgendamentoRN>{

	@Mock
	private AmbulatorioRN mockedAmbulatorioRN;
	@Mock
	private AacConsultasDAO mockedAacConsultasDAO;
	@Mock
	private AacGradeSituacaoDAO mockedAacGradeSituacaoDAO;
	@Mock
	private AacGradeAgendamenConsultasDAO mockedGradeAgendamentoConsultasDAO;

	@Test
	public void inserirSituacaoTest() throws ApplicationBusinessException {
		try{
		
			Mockito.when(mockedAmbulatorioRN.atualizaServidor(Mockito.any(RapServidores.class))).thenReturn(null);
		
			Mockito.when(mockedAacConsultasDAO.listarConsultasMarcadasNovoPeriodo(Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(null);
		
		final List<AacGradeAgendamenConsultas> listaGradeAgendamentoConsultas = new ArrayList<AacGradeAgendamenConsultas>();
		
			Mockito.when(mockedGradeAgendamentoConsultasDAO.listarAgendamentoConsultasSituacao(Mockito.any(AacGradeAgendamenConsultas.class))).thenReturn(listaGradeAgendamentoConsultas);
		
		final List<AacGradeSituacao> listaGradeSituacao = new ArrayList<AacGradeSituacao>();
		
			Mockito.when(mockedAacGradeSituacaoDAO.listarSituacaoGradePorDataDecrescente(Mockito.anyInt())).thenReturn(listaGradeSituacao);
		
			Mockito.when(mockedAacGradeSituacaoDAO.listarGradeSituacaoComProfCount(Mockito.any(AacGradeSituacao.class))).thenReturn(Long.valueOf(1));
		
			AacGradeSituacao gradeSituacao = new AacGradeSituacao();
			gradeSituacao.setSituacao(DominioSituacao.A);
			AacGradeSituacaoId gradeSituacaoId = new AacGradeSituacaoId();
			gradeSituacaoId.setDtInicioSituacao(new Date());
			
			RapServidores servidor = new RapServidores();
			servidor.setId(new RapServidoresId(1, (short) 1));
			RapPessoasFisicas pf = new RapPessoasFisicas();
			servidor.setPessoaFisica(pf);
			gradeSituacao.setServidor(servidor);
			AacGradeAgendamenConsultas gradeAgendamenConsulta = new AacGradeAgendamenConsultas();
			gradeAgendamenConsulta.setSeq(1);
			gradeAgendamenConsulta.setServidor(servidor);
			gradeSituacao.setGradeAgendamentoConsulta(gradeAgendamenConsulta);
			systemUnderTest.inserirSituacao(gradeSituacao);
		}
		catch(ApplicationBusinessException e){
			assert(true);
		}
	}
	

}
