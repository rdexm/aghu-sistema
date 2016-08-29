package br.gov.mec.aghu.business.bancosangue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.gov.mec.aghu.bancosangue.dao.AbsSolicitacaoHemoterapicaJnDAO;
import br.gov.mec.aghu.model.AbsMotivosCancelaColetas;
import br.gov.mec.aghu.model.AbsSolicitacaoHemoterapicaJn;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicasId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@RunWith(PowerMockRunner.class)
public class SolicitacaoHemoterapicaJournalRNTest extends AGHUBaseUnitTest<SolicitacaoHemoterapicaJournalRN>{

	@Mock
	private AbsSolicitacaoHemoterapicaJnDAO mockedAbsSolicitacaoHemoterapicaJnDAO;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	/**
	 * Testa a execucao de dois laudos identicos, com MotivoCancelaColeta diferentes  
	 */
	@Test
	@PrepareForTest( BaseJournalFactory.class )
	public void realizarLaudoJournalMotivoCancelaColetaDiferentesTest() throws BaseException {
	
		AbsSolicitacoesHemoterapicas solHemNew = new AbsSolicitacoesHemoterapicas(); 
		AbsSolicitacoesHemoterapicasId solHemNewId = new AbsSolicitacoesHemoterapicasId();
		solHemNewId.setSeq(1);
		solHemNew.setId(solHemNewId);
		
		AbsSolicitacoesHemoterapicas solHemOld = new AbsSolicitacoesHemoterapicas(); 
		AbsSolicitacoesHemoterapicasId solHemOldId = new AbsSolicitacoesHemoterapicasId();
		solHemOldId.setSeq(1);
		solHemOld.setId(solHemOldId);
		
		AghAtendimentos atd = new AghAtendimentos();
		atd.setSeq(1);
		solHemOld.setPrescricaoMedica(new MpmPrescricaoMedica());
		solHemOld.getPrescricaoMedica().setAtendimento(atd);
		
		AbsMotivosCancelaColetas mcc1 = new AbsMotivosCancelaColetas();
		mcc1.setSeq((short)1);
		
		solHemNew.setMotivoCancelaColeta(null);
		solHemOld.setMotivoCancelaColeta(mcc1);
		
		whenObterServidorLogado();
		AbsSolicitacaoHemoterapicaJn journal = new AbsSolicitacaoHemoterapicaJn();
		PowerMockito.mockStatic(BaseJournalFactory.class);
		PowerMockito.when(BaseJournalFactory.getBaseJournal(Mockito.any(DominioOperacoesJournal.class), Mockito.any(Class.class), Mockito.anyString()))
		.thenReturn(journal);
		
		systemUnderTest.realizarSolicitacaoHemoterapicaJournal(solHemNew, solHemOld, DominioOperacoesJournal.UPD);
	}
	
	/**
	 * Testa a execucao de dois laudos identicos, com MotivoCancelaColeta identicos  
	 */
	@Test
	public void realizarLaudoJournalMotivoCancelaColetaIdenticosTest() throws ApplicationBusinessException  {
		
		AbsSolicitacoesHemoterapicas solHemNew = new AbsSolicitacoesHemoterapicas(); 
		AbsSolicitacoesHemoterapicasId solHemNewId = new AbsSolicitacoesHemoterapicasId();
		solHemNewId.setSeq(1);
		solHemNew.setId(solHemNewId);
		
		AbsSolicitacoesHemoterapicas solHemOld = new AbsSolicitacoesHemoterapicas(); 
		AbsSolicitacoesHemoterapicasId solHemOldId = new AbsSolicitacoesHemoterapicasId();
		solHemOldId.setSeq(1);
		solHemOld.setId(solHemOldId);
		
		AghAtendimentos atd = new AghAtendimentos();
		atd.setSeq(1);
		solHemOld.setPrescricaoMedica(new MpmPrescricaoMedica());
		solHemOld.getPrescricaoMedica().setAtendimento(atd);
		
		AbsMotivosCancelaColetas mcc1 = new AbsMotivosCancelaColetas();
		mcc1.setSeq((short)1);
		
		solHemNew.setMotivoCancelaColeta(mcc1);
		solHemOld.setMotivoCancelaColeta(mcc1);
		
		systemUnderTest.realizarSolicitacaoHemoterapicaJournal(solHemNew, solHemOld, DominioOperacoesJournal.UPD);
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
