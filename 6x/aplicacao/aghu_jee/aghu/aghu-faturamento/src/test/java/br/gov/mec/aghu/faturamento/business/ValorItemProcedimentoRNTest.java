package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ValorItemProcedimentoRNTest extends AGHUBaseUnitTest<ValorItemProcedimentoRN>{
	
	private static final Log log = LogFactory.getLog(ValorItemProcedimentoRNTest.class);
	
	private Date data;
	@Mock
	private ValorItemProcedHospCompsRN mockedValorItemProcedHospCompsRN;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

    @Before
    public void inicar() throws BaseException {
    	whenObterServidorLogado();
    }

	@Test
	public void testProcessarAntesInserirFattIpcBri() throws BaseException {
		FatVlrItemProcedHospComps entidade = new FatVlrItemProcedHospComps();
		systemUnderTest.processarAntesInserirFattIpcBri(entidade);
	}

	@Test
	public void testProcessarAntesAtualizarFattIpcAsu() throws BaseException {
		FatVlrItemProcedHospComps entidade = new FatVlrItemProcedHospComps();
		systemUnderTest.processarAntesAtualizarFattIpcAsu(entidade);
	}

	@Test
	public void testProcessarAntesAtualizarFattIpcBru() throws BaseException {
		FatVlrItemProcedHospComps entidade = new FatVlrItemProcedHospComps();
		systemUnderTest.processarAntesAtualizarFattIpcBru(entidade);
	}

	@Test
	public void testProcessarAposInserirFattIpcAsi() throws BaseException {
		FatVlrItemProcedHospComps entidade = new FatVlrItemProcedHospComps();
		systemUnderTest.processarAposInserirFattIpcAsi(entidade);
	}
	
	@Test
	public void testFatpEnforceIpcRulesInsercao() throws BaseException {
		FatVlrItemProcedHospComps entidade = new FatVlrItemProcedHospComps();
		systemUnderTest.fatpEnforceIpcRules(entidade,DominioOperacoesJournal.INS);
	}

	@Test
	public void testFatpEnforceIpcRulesAlteracao() throws BaseException {
		FatVlrItemProcedHospComps entidade = new FatVlrItemProcedHospComps();
		systemUnderTest.fatpEnforceIpcRules(entidade,DominioOperacoesJournal.UPD);
	}

    private void whenObterServidorLogado() throws BaseException {
		RapServidores rap =  new RapServidores(new RapServidoresId(1, (short) 1)) ;
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		rap.setUsuario("");
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
    }

}
