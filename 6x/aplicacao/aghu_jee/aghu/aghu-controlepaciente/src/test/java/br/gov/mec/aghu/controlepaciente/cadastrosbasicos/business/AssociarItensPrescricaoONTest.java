package br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controlepaciente.dao.EcpItemControleDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpItemCtrlCuidadoEnfDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpItemCtrlCuidadoMedicoDAO;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.EcpItemCtrlCuidadoEnf;
import br.gov.mec.aghu.model.EcpItemCtrlCuidadoMedico;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Classe responsável pelos testes unitários da classe
 * ConfigurarListaPacientesON.<br>
 * 
 * @author cvagheti
 * 
 */
public class AssociarItensPrescricaoONTest extends AGHUBaseUnitTest<AssociarItensPrescricaoON>{
	
	private static final Log log = LogFactory.getLog(AssociarItensPrescricaoONTest.class);

	@Mock
	private EcpItemCtrlCuidadoMedicoDAO mockedEcpItemCtrlCuidadoMedicoDAO;
	@Mock
	private EcpItemCtrlCuidadoEnfDAO mockedEcpItemCtrlCuidadoEnfDAO;
	@Mock
	private EcpItemControleDAO mockedEcpItemControleDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
    @Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;	
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	@Test
	public void salvarListaAssociacaoCuidadosMedicosTest() {

		try {

			// servidor que será utilizado para a criação dos cuidados
			RapServidores servidor = new RapServidores(new RapServidoresId(1, (short) 1));

			// cria o cuidado 1 que será utilizado para simular a inclusão
			MpmCuidadoUsual cuidado = new MpmCuidadoUsual();
			cuidado.setSeq(1);
			cuidado.setRapServidores(servidor);
			cuidado.setDescricao("TESTE DE GRAVACAO");

			// adiciona o cuidado na lista de inclusão
			final List<MpmCuidadoUsual> listaCuidadosIncluir = new ArrayList<MpmCuidadoUsual>();
			listaCuidadosIncluir.add(cuidado);

			// cria o cuidado 2 que será utilizado para simular a exclusão
			cuidado = new MpmCuidadoUsual();
			cuidado.setSeq(2);
			cuidado.setRapServidores(servidor);
			cuidado.setDescricao("TESTE DE GRAVACAO");

			// adiciona o cuidado na lista de exclusão
			final List<MpmCuidadoUsual> listaCuidadosExcluir = new ArrayList<MpmCuidadoUsual>();
			listaCuidadosExcluir.add(cuidado);

			// cria o item de controle que receberá a associação
			EcpItemControle itemControle = new EcpItemControle();
			itemControle.setSeq((short) 1);
			itemControle.setDescricao("TESTE ITEM CONTROLE");

			// cria uma associação entre o item de controle e o cuidado que será
			// excluído
			final EcpItemCtrlCuidadoMedico itemCtrlCuidadoMedico = new EcpItemCtrlCuidadoMedico();
			itemCtrlCuidadoMedico.setSeq(1);
			itemCtrlCuidadoMedico.setItemControle(itemControle);
			itemCtrlCuidadoMedico.setCuidadoUsual(cuidado);

			whenObterServidorLogado();

			Mockito.when(
					mockedEcpItemCtrlCuidadoMedicoDAO.obterCuidadoMedicoPorItemControleECuidado(Mockito.any(EcpItemControle.class),
							Mockito.any(MpmCuidadoUsual.class))).thenReturn(itemCtrlCuidadoMedico);
			systemUnderTest.salvarAssociacaoCuidadosMedicos(itemControle, listaCuidadosIncluir, listaCuidadosExcluir);

		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.fail("TESTE FALHOU!");
		}

	}

	@Test
	public void salvarListaAssociacaoCuidadosEnfermagemTest() {

		try {

			// servidor que será utilizado para a criação dos cuidados
			RapServidores servidor = new RapServidores(new RapServidoresId(1, (short) 1));

			// cria o cuidado 1 que será utilizado para simular a inclusão
			EpeCuidados cuidado = new EpeCuidados();
			cuidado.setSeq((short) 1);
			cuidado.setServidor(servidor);
			cuidado.setDescricao("TESTE DE GRAVACAO");

			// adiciona o cuidado na lista de inclusão
			final List<EpeCuidados> listaCuidadosIncluir = new ArrayList<EpeCuidados>();
			listaCuidadosIncluir.add(cuidado);

			// cria o cuidado 2 que será utilizado para simular a exclusão
			cuidado = new EpeCuidados();
			cuidado.setSeq((short) 2);
			cuidado.setServidor(servidor);
			cuidado.setDescricao("TESTE DE GRAVACAO");

			// adiciona o cuidado na lista de exclusão
			final List<EpeCuidados> listaCuidadosExcluir = new ArrayList<EpeCuidados>();
			listaCuidadosExcluir.add(cuidado);

			// cria o item de controle que receberá a associação
			EcpItemControle itemControle = new EcpItemControle();
			itemControle.setSeq((short) 1);
			itemControle.setDescricao("TESTE ITEM CONTROLE");

			// cria uma associação entre o item de controle e o cuidado que será
			// excluído
			final EcpItemCtrlCuidadoEnf itemCtrlCuidadoEnfermagem = new EcpItemCtrlCuidadoEnf();
			itemCtrlCuidadoEnfermagem.setSeq(1);
			itemCtrlCuidadoEnfermagem.setItemControle(itemControle);
			itemCtrlCuidadoEnfermagem.setCuidado(cuidado);

			Mockito.when(
					mockedEcpItemCtrlCuidadoEnfDAO.obterCuidadoEnfPorItemControleECuidado(Mockito.any(EcpItemControle.class),
							Mockito.any(EpeCuidados.class))).thenReturn(itemCtrlCuidadoEnfermagem);

			whenObterServidorLogado();

			systemUnderTest.salvarAssociacaoCuidadosEnfermagem(itemControle, listaCuidadosIncluir, listaCuidadosExcluir);

		} catch (BaseException e) {
			log.error(e.getMessage());
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
