package br.gov.mec.aghu.controlepaciente.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.controlepaciente.dao.EcpServidorUnidFuncionalDAO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.EcpServidorUnidFuncional;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Classe responsável pelos testes unitários da estória #6733 - Configurar lista
 * de pacientes de enfermagem
 * 
 * @author ptneto
 * 
 */
public class ConfigurarListaPacientesEnfermagemONTest extends AGHUBaseUnitTest<ConfigurarListaPacientesEnfermagemON> {

	private static final Log log = LogFactory.getLog(ConfigurarListaPacientesEnfermagemONTest.class);

	@Mock
	private EcpServidorUnidFuncionalDAO mockedEcpServidorUnidFuncionalDAO;

	@Test
	public void inserirConfiguracaoOkTest() {

		try {

			List<EcpServidorUnidFuncional> lista = new ArrayList<EcpServidorUnidFuncional>(0);
			final List<AghUnidadesFuncionais> listaUnidadesFuncionais = new ArrayList<AghUnidadesFuncionais>();
			final RapServidores servidor = new RapServidores(new RapServidoresId(1, (short) 1));
			AghUnidadesFuncionais aghUnidadesFuncionais = new AghUnidadesFuncionais();

			listaUnidadesFuncionais.add(aghUnidadesFuncionais);

			Mockito.when(mockedEcpServidorUnidFuncionalDAO.pesquisarAssociacoesPorServidor(Mockito.any(RapServidores.class))).thenReturn(
					lista);

			systemUnderTest.salvarListaUnidadesFuncionais(listaUnidadesFuncionais, servidor);

		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.fail("TESTE FALHOU!");
		}
	}

	@Test
	public void inserirConfiguracaoNullTest() {

		try {
			final List<AghUnidadesFuncionais> listaUnidadesFuncionais = new ArrayList<AghUnidadesFuncionais>();
			AghUnidadesFuncionais aghUnidadesFuncionais = new AghUnidadesFuncionais();

			listaUnidadesFuncionais.add(aghUnidadesFuncionais);

			systemUnderTest.salvarListaUnidadesFuncionais(listaUnidadesFuncionais, null);
		} catch (BaseException e) {
			log.error(e.getMessage());
		}
	}
}
