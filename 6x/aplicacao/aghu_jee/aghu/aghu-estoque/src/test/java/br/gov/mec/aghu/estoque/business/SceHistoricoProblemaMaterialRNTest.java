package br.gov.mec.aghu.estoque.business;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.dao.SceHistoricoProblemaMaterialDAO;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceHistoricoProblemaMaterial;
import br.gov.mec.aghu.model.SceMotivoProblema;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SceHistoricoProblemaMaterialRNTest extends AGHUBaseUnitTest<SceHistoricoProblemaMaterialRN>{

	@Mock
	private SceHistoricoProblemaMaterialDAO mockedSceHistoricoProblemaMaterialDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	
	@Test
	public void inserirTest() throws BaseException{
		SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado = new SceEstoqueAlmoxarifado();
		sceEstoqueAlmoxarifado.setIndSituacao(DominioSituacao.A);
		
		ScoFornecedor scoFornecedor = new ScoFornecedor();
		scoFornecedor.setSituacao(DominioSituacao.A);
		
		SceMotivoProblema sceMotivoProblema = new SceMotivoProblema();
		sceMotivoProblema.setIndSituacao(DominioSituacao.A);
		
		SceHistoricoProblemaMaterial sceHistoricoProblemaMaterial = new SceHistoricoProblemaMaterial();
		sceHistoricoProblemaMaterial.setQtdeDf(1);
		sceHistoricoProblemaMaterial.setQtdeDesbloqueada(1);
		sceHistoricoProblemaMaterial.setQtdeProblema(3);
		sceHistoricoProblemaMaterial.setSceEstqAlmox(sceEstoqueAlmoxarifado);
		sceHistoricoProblemaMaterial.setFornecedor(scoFornecedor);
		sceHistoricoProblemaMaterial.setMotivoProblema(sceMotivoProblema);
		
		whenObterServidorLogado();
		
		systemUnderTest.inserir(sceHistoricoProblemaMaterial, true);
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
