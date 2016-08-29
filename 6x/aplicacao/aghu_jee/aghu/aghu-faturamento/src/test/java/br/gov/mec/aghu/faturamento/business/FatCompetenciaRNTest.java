package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatCompetenciaId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

public class FatCompetenciaRNTest extends AGHUBaseUnitTest<FatCompetenciaRN>{

	@Mock
	private FaturamentoFatkCpeRN mockedFaturamentoFatkCpeRN;
	@Mock
	private FatCompetenciaDAO mockedFatCompetenciaDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

    @Before
    public void inicio() throws BaseException {
    	whenObterServidorLogado();
    }
	
	@Test
	public void inserirFatCompetencia(){
		final Date data = new Date();
		
		final FatCompetencia fatCompetencia = new FatCompetencia();
		fatCompetencia.setId(new FatCompetenciaId());
		fatCompetencia.setDtHrFim(data);
		fatCompetencia.getId().setDtHrInicio(DateUtil.adicionaDias(data, 2));
		
		try {
			
			systemUnderTest.inserirFatCompetencia(fatCompetencia, true);
			
		} catch (ApplicationBusinessException e) {
		}
	}
	
	@Test
	public void atualizarFatCompetencia(){
		Date data = new Date();
		data = DateUtil.adicionaDias(data, 30);
		
		final FatCompetencia fatCompetencia = new FatCompetencia();
		fatCompetencia.setId(new FatCompetenciaId(DominioModuloCompetencia.AMB, 1, 1, new Date()));
		fatCompetencia.setDtHrFim(data);
		
		try {
			
			systemUnderTest.atualizarFatCompetencia(fatCompetencia);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Not expecting exception: " + e.getCode());
		}
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