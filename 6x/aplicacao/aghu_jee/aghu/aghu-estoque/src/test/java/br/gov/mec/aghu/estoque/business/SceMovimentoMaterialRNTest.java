package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.SceMovimentoMaterialRN.SceMovimentoMaterialRNExceptionCode;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceHistoricoProblemaMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceTipoMovimentosDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SceMovimentoMaterialRNTest extends AGHUBaseUnitTest<SceMovimentoMaterialRN> {

	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
	private static final RapServidores SERVIDOR_LOGADO = new RapServidores(new RapServidoresId(1, (short) 1));

	@Mock
	private SceTipoMovimentosRN mockedSceTipoMovimentosRN;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
	@Mock
	private IServidorLogadoFacade mockedServidorLogadoFacade;
	@Mock
	private SceTipoMovimentosDAO mockedSceTipoMovimentosDAO;
	@Mock
	private SceEstoqueAlmoxarifadoRN mockedSceEstoqueAlmoxarifadoRN;
	@Mock
	private SceEstoqueAlmoxarifadoDAO mockedSceEstoqueAlmoxarifadoDAO;
	@Mock
	private IComprasFacade mockedComprasFacade;
	@Mock
	private SceAlmoxarifadoDAO mockedSceAlmoxarifadoDAO;

	@Test
	public void inserirTest() throws BaseException {
		final AghParametros aghParametros = new AghParametros();
		aghParametros.setVlrNumerico(new BigDecimal(2));
		aghParametros.setVlrTexto("N");

		final SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado = new SceEstoqueAlmoxarifado();
		sceEstoqueAlmoxarifado.setIndSituacao(DominioSituacao.A);
		sceEstoqueAlmoxarifado.setQtdeDisponivel(1);
		sceEstoqueAlmoxarifado.setQtdeBloqueada(2);
		sceEstoqueAlmoxarifado.setQtdeBloqEntrTransf(3);
		sceEstoqueAlmoxarifado.setQtdeEmUso(4);

		whenObterServidorLogado();

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
				.thenReturn(aghParametros);
		Mockito.when(mockedSceEstoqueAlmoxarifadoDAO
				.pesquisarEstoqueAlmPorMaterialAlmoxFornecedor(Mockito.any(SceMovimentoMaterial.class)))
				.thenReturn(sceEstoqueAlmoxarifado);

		try {
			systemUnderTest.inserir(new SceMovimentoMaterial(), NOME_MICROCOMPUTADOR, false);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertEquals(SceMovimentoMaterialRNExceptionCode.MENSAGEM_TIPO_MOVIMENTO_NAO_CADASTRADO,
					e.getCode());
		}
	}

	private void whenObterServidorLogado() throws BaseException {
		RapServidores rap = new RapServidores(new RapServidoresId(1, (short) 1));
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);

		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
	}
}
