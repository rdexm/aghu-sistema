package br.gov.mec.aghu.estoque.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueGeralDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SceEstoqueAlmoxarifadoRNTest extends AGHUBaseUnitTest<SceEstoqueAlmoxarifadoRN>{

	@Mock
	private SceEstoqueAlmoxarifadoDAO mockedSceEstoqueAlmoxarifadoDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private IComprasFacade mockedComprasFacade;
	@Mock
	private SceEstoqueGeralDAO mockedSceEstoqueGeralDAO;
	@Mock
	private SceEstoqueGeralRN mockedSceEstoqueGeralRN;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	

	@Test
	public void inserirTest(){
		final SceEstoqueAlmoxarifado estoqueAlmox = new SceEstoqueAlmoxarifado();
		estoqueAlmox.setIndEstocavel(false);
		
		final ScoMaterial material = new ScoMaterial();
		estoqueAlmox.setMaterial(material);
		
		try {
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(new AghParametros());

			Mockito.when(mockedComprasFacade.obterScoMaterial(Mockito.anyInt())).thenReturn(material);

			whenObterServidorLogado();
			
			systemUnderTest.inserir(estoqueAlmox);
		} catch (BaseException e) {
			getLog().debug("Exceção ignorada.");
		}
	}

	protected Log getLog() {
		return LogFactory.getLog(this.getClass());
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