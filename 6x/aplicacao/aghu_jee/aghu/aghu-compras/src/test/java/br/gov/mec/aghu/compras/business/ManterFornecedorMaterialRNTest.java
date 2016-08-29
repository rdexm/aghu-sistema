package br.gov.mec.aghu.compras.business;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.SceFornecedorMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterFornecedorMaterialRNTest extends AGHUBaseUnitTest<ManterFornecedorMaterialRN>{

	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;


	/**
	 * Testa Trigger SCET_FMT_BRI 
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void executarAntesInserirFornecedorMaterial2() throws Exception {
	
		SceFornecedorMaterial fornecedorMaterial = new SceFornecedorMaterial();
		whenObterServidorLogado();
		systemUnderTest.executarAntesInserirFornecedorMaterial(fornecedorMaterial);
	}	
	
	/**
	 * Testa Trigger SCET_FMT_BRU 
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void executarAntesAtualizarFornecedorMaterial2() throws Exception {
	
		SceFornecedorMaterial fornecedorMaterial = new SceFornecedorMaterial();
		whenObterServidorLogado();
		systemUnderTest.executarAntesAtualizarFornecedorMaterial(fornecedorMaterial);
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
