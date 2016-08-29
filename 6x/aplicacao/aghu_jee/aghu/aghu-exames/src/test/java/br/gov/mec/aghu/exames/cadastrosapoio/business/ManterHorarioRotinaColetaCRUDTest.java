package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelHorarioRotinaColetasDAO;
import br.gov.mec.aghu.exames.dao.AelPermissaoUnidSolicDAO;
import br.gov.mec.aghu.model.AelHorarioRotinaColetas;
import br.gov.mec.aghu.model.AelHorarioRotinaColetasId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author lsamberg
 *
 */
public class ManterHorarioRotinaColetaCRUDTest extends AGHUBaseUnitTest<ManterHorarioRotinaColetaCRUD>{

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AelHorarioRotinaColetasDAO mockedAelHorarioRotinaColetasDAO;
	@Mock
	private AelPermissaoUnidSolicDAO mockedAelPermissaoUnidSolicDAO;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	
	@Before
	public void doBeforeEachTestCase() throws Exception{

		whenObterServidorLogado();
	}
	
	@Test
	public void obterAelHorarioRotinaColetaPorIdTest() {

			AelHorarioRotinaColetasId horarioId = new AelHorarioRotinaColetasId();
			
			Mockito.when(mockedAelHorarioRotinaColetasDAO.obterAelHorarioRotinaColetaPorId(Mockito.any(AelHorarioRotinaColetasId.class))).thenReturn(new AelHorarioRotinaColetas());

			systemUnderTest.obterAelHorarioRotinaColetaPorId(horarioId);
	}
	
	@Test
	public void obterAelHorarioRotinaColetasPorParametrosTest() {

			Short unidadeColeta = Short.valueOf("0");
			Short unidadeSolicitante = Short.valueOf("0");
			Date dataHora = new Date();
			String dia = new String();
			DominioSituacao situacao = DominioSituacao.A;
			
			systemUnderTest.obterAelHorarioRotinaColetasPorParametros(unidadeColeta,unidadeSolicitante,dataHora,dia,situacao);
	}
	
	@Test
	public void persistirTest() throws ApplicationBusinessException {

		AelHorarioRotinaColetas aelHorarioRotinaColetas = new AelHorarioRotinaColetas();
		
		Mockito.when(mockedAelHorarioRotinaColetasDAO.obterAelHorarioRotinaColetaPorId(Mockito.any(AelHorarioRotinaColetasId.class))).thenReturn(null);
			
		try {
			systemUnderTest.persistir(aelHorarioRotinaColetas);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
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
