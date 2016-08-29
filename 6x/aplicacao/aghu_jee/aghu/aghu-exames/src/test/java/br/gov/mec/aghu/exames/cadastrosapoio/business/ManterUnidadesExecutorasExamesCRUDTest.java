package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNaoRestritoAreaExecutora;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesJnDAO;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.AelUnfExecutaExamesJn;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
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

/**
 * 
 * @author lsamberg
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( BaseJournalFactory.class )
public class ManterUnidadesExecutorasExamesCRUDTest extends AGHUBaseUnitTest<ManterUnidadesExecutorasExamesCRUD>{
	
	private static final Log log = LogFactory.getLog(ManterUnidadesExecutorasExamesCRUDTest.class);

	@Mock
	private AelUnfExecutaExamesDAO mockedAelUnfExecutaExamesDAO;
	@Mock
	private AelUnfExecutaExamesJnDAO mockedAelUnfExecutaExamesJnDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AelTipoAmostraExameDAO mockedAelTipoAmostraExameDAO;
	@Mock
	private BaseJournalFactory mockedBaseJournalFactory;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

    @Before
    public void inicar() throws BaseException {
    	whenObterServidorLogado();
    	
		PowerMockito.mockStatic(BaseJournalFactory.class);
		PowerMockito.when(BaseJournalFactory.getBaseJournal(Mockito.any(DominioOperacoesJournal.class), Mockito.any(Class.class), Mockito.anyString()))
		.thenReturn(new AelUnfExecutaExamesJn());

    }
    
	private AelUnfExecutaExames buscarAelUnfExecutaExamesPopulado() {
		AelUnfExecutaExames aelUnfExecutaExames = new AelUnfExecutaExames();
		aelUnfExecutaExames.setIndDesativaTemp(Boolean.FALSE);
		aelUnfExecutaExames.setIndSituacao(DominioSituacao.A);
		aelUnfExecutaExames.setMotivoDesativacao("");
		aelUnfExecutaExames.setId(new AelUnfExecutaExamesId());
		aelUnfExecutaExames.getId().setUnfSeq(new AghUnidadesFuncionais());
		aelUnfExecutaExames.getId().getUnfSeq().setIndSitUnidFunc(DominioSituacao.A);
		aelUnfExecutaExames.setIndAgendamPrevioInt(DominioSimNaoRestritoAreaExecutora.N);
		aelUnfExecutaExames.setIndAgendamPrevioNaoInt(DominioSimNaoRestritoAreaExecutora.N);
		
		return aelUnfExecutaExames;
	}
	
	@Test
	public void removerUnidadeExecutoraExamesTest() {

		AelUnfExecutaExames pojo = new AelUnfExecutaExames();
		pojo.setId(new AelUnfExecutaExamesId());
		pojo.getId().setUnfSeq(new AghUnidadesFuncionais());
		Mockito.when(mockedAelUnfExecutaExamesDAO.buscaAelUnfExecutaExames(Mockito.anyString(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(pojo);

		List<AelTipoAmostraExame> lista = new ArrayList<AelTipoAmostraExame>();
		lista.add(new AelTipoAmostraExame());
		Mockito.when(mockedAelTipoAmostraExameDAO.buscarListaAelTipoAmostraExamePorEmaExaSiglaEmaManSeq(Mockito.anyString(), Mockito.anyInt())).thenReturn(lista);

		Mockito.when(mockedAelUnfExecutaExamesDAO.pesquisarAelUnfExecutaExamesExecutaPlantaoPorEmaExaSiglaEmaManSeq(Mockito.anyString(), Mockito.anyInt())).thenReturn(null);
				
		AelUnfExecutaExames aelUnfExecutaExames = buscarAelUnfExecutaExamesPopulado();
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		examesMaterialAnalise.setId(new AelExamesMaterialAnaliseId());
		
		try {
			
			systemUnderTest.removerUnidadeExecutoraExames(aelUnfExecutaExames.getId().getEmaExaSigla(), aelUnfExecutaExames.getId().getEmaManSeq(), aelUnfExecutaExames.getId().getUnfSeq().getSeq());
		
			
		} catch (ApplicationBusinessException e) {
			log.error(e.getMessage());
			
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
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
