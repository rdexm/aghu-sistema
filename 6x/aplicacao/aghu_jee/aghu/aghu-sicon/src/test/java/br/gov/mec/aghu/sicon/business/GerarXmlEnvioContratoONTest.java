package br.gov.mec.aghu.sicon.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.dominio.DominioOrigemContrato;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialSicon;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoServicoSicon;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoTipoContratoSicon;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.sicon.dao.ScoContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoItensContratoDAO;
import br.gov.mec.aghu.sicon.util.Cnet;
import br.gov.mec.aghu.sicon.util.ObjectFactory;
import br.gov.mec.aghu.sicon.vo.DadosEnvioVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GerarXmlEnvioContratoONTest extends AGHUBaseUnitTest<GerarXmlEnvioContratoON>{
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private ScoContratoDAO mockedScoContratoDAO;
	@Mock
	private ScoItensContratoDAO mockedScoItensContratoDAO;
	@Mock
	private IComprasFacade mockedComprasFacade;
	@Mock
	private IPacFacade mockedPacFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private ICadastrosBasicosSiconFacade mockedCadastrosBasicosSiconFacade;

	/**
	 * Testa caso feliz.
	 */
	//@Test
	public void gerarXmlTest() {

		final ScoContrato contrato = new ScoContrato();
		final AghParametros url = new AghParametros("url");
		final RapServidores servidor = new RapServidores();
		 RapPessoasFisicas rapPessoasFisicas = new RapPessoasFisicas(0);
		 rapPessoasFisicas.setCpf(0L);
		 servidor.setPessoaFisica(rapPessoasFisicas);

		final List<ScoItensContrato> itens = new ArrayList<ScoItensContrato>();
		List<ScoAfContrato> afContratos = new ArrayList<ScoAfContrato>();

		contrato.setSituacao(DominioSituacaoEnvioContrato.A);

		contrato.setUasgSubrog(0);
		contrato.setTipoContratoSicon(new ScoTipoContratoSicon());
		contrato.getTipoContratoSicon().setCodigoSicon(0);
		contrato.getTipoContratoSicon().setDescricao("descricao");
		contrato.setNrContrato(0L);
		contrato.setDtAssinatura(new Date());
		contrato.setUasgLicit(0);
		contrato.setModalidadeLicitacao(new ScoModalidadeLicitacao());
		contrato.getModalidadeLicitacao().setDescricao("");
		contrato.setLicitacao(new ScoLicitacao());
		contrato.getLicitacao().setNumero(0);
		contrato.getLicitacao().setDtPublicacao(new Date());
		contrato.setInciso("");
		contrato.setDtPublicacao(new Date());
		contrato.setObjetoContrato("");
		contrato.setFornecedor(new ScoFornecedor());
		contrato.getFornecedor().setCgc(0L);
		contrato.getFornecedor().setCpf(0L);
		contrato.getFornecedor().setRazaoSocial("");
		contrato.setFundamentoLegal("");
		contrato.setDtInicioVigencia(new Date());
		contrato.setDtFimVigencia(new Date());
		contrato.setValorTotal(BigDecimal.ZERO);
		contrato.setIndOrigem(DominioOrigemContrato.A);
		contrato.setScoAfContratos(afContratos);

		Mockito.when(mockedScoContratoDAO.obterPorChavePrimaria(Mockito.any(ScoContrato.class))).thenReturn(contrato);
		
		try {
			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_URL_INTEGRACAO_SICON)).thenReturn(url);
		} catch (ApplicationBusinessException e1) {
			getLog().error(e1.getMessage());
		}

		Mockito.when(mockedAghuFacade.getUasg()).thenReturn(0);

		Mockito.when(mockedAghuFacade.getCgc()).thenReturn("");

		Mockito.when(mockedScoItensContratoDAO.getItensContratoByContrato(contrato)).thenReturn(itens);

		try {
			
			DadosEnvioVO xml = this.systemUnderTest.gerarXml(1, "senha");
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void addItensOrigemManualTest() {
		final ScoContrato contrato = new ScoContrato();

		final Cnet cnet = new ObjectFactory().createCnet();

		final List<ScoItensContrato> itensList = new ArrayList<ScoItensContrato>();

		final ScoItensContrato itensContrato = new ScoItensContrato();

		itensContrato.setContrato(contrato);
		itensContrato.setNrItem(0);
		itensContrato.setMarcaComercial(new ScoMarcaComercial());
		itensContrato.getMarcaComercial().setDescricao("");
		itensContrato.setQuantidade(0);
		itensContrato.setUnidade("");
		itensContrato.setVlTotal(BigDecimal.ZERO);
		itensContrato.setDescricao("");
		// itensContrato.setMaterial(new ScoMaterial());
		// itensContrato.getMaterial().setCodigo(0);
		ScoServico servico = new ScoServico();
		servico.setCodigo(0);
		itensContrato.setServico(servico);

		itensList.add(itensContrato);
		
		
		ScoServicoSicon scoServicoSicon = new ScoServicoSicon();
		
		scoServicoSicon.setServico(servico);
		scoServicoSicon.setCodigoSicon(0);
		
		final List<ScoServicoSicon> listaScoServicoSicon = new ArrayList<ScoServicoSicon>();
		listaScoServicoSicon.add(scoServicoSicon);

		cnet.setItens(new ObjectFactory().createCnetItens());

		Mockito.when(mockedScoItensContratoDAO.obterPorChavePrimaria(Mockito.any(ScoContrato.class))).thenReturn(itensContrato);
		
		Mockito.when(mockedCadastrosBasicosSiconFacade.obterPorCodigoServico(Mockito.any(ScoServico.class))).thenReturn(listaScoServicoSicon);
		
		
		this.systemUnderTest.addItensOrigemManual(itensList, cnet);

	}

	@Test
	public void addItensOrigemAutomaticaMaiorTest() {

		final ScoContrato contrato = new ScoContrato();

		final Cnet cnet = new ObjectFactory().createCnet();

		final List<ScoAfContrato> afContratoList = new ArrayList<ScoAfContrato>();

		final ScoAfContrato afContrato = new ScoAfContrato();

		final List<ScoItemAutorizacaoForn> itensAutorizacaoFornList = new ArrayList<ScoItemAutorizacaoForn>();

		final ScoItemAutorizacaoForn itensAutorizacaoForn = new ScoItemAutorizacaoForn();

		final List<ScoFaseSolicitacao> faseSolicitacaoList = new ArrayList<ScoFaseSolicitacao>();

		final ScoFaseSolicitacao faseSolicitacao = new ScoFaseSolicitacao();

		final ScoLicitacao licitacao = new ScoLicitacao();
		licitacao.setNumero(123);
		
		contrato.setLicitacao(licitacao);
		contrato.setScoAfContratos(afContratoList);
		afContrato.setScoContrato(contrato);
		afContrato.setScoAutorizacoesForn(new ScoAutorizacaoForn());

		afContrato.getScoAutorizacoesForn().setItensAutorizacaoForn(
				itensAutorizacaoFornList);

		faseSolicitacao.setItemAutorizacaoForn(itensAutorizacaoForn);

		faseSolicitacaoList.add(faseSolicitacao);

		itensAutorizacaoForn.setScoFaseSolicitacao(faseSolicitacaoList);

		itensAutorizacaoFornList.add(itensAutorizacaoForn);

		afContratoList.add(afContrato);

		faseSolicitacao
				.setSolicitacaoDeCompra(new ScoSolicitacaoDeCompra());
		faseSolicitacao.getSolicitacaoDeCompra().setMaterial(
				new ScoMaterial());
		faseSolicitacao.getSolicitacaoDeCompra().getMaterial().setCodigo(0);

		faseSolicitacao.setSolicitacaoServico(new ScoSolicitacaoServico());
		faseSolicitacao.getSolicitacaoServico()
				.setServico(new ScoServico());
		faseSolicitacao.getSolicitacaoServico().getServico().setCodigo(0);

		faseSolicitacao
				.setItemAutorizacaoForn(new ScoItemAutorizacaoForn());
		faseSolicitacao.getItemAutorizacaoForn()
				.setItemPropostaFornecedor(new ScoItemPropostaFornecedor());
		faseSolicitacao.getItemAutorizacaoForn()
				.getItemPropostaFornecedor()
				.setMarcaComercial(new ScoMarcaComercial());
		faseSolicitacao.getItemAutorizacaoForn()
				.getItemPropostaFornecedor().getMarcaComercial()
				.setDescricao("");
		faseSolicitacao.getItemAutorizacaoForn()
				.getItemPropostaFornecedor().setQuantidade(0L);
		ScoUnidadeMedida unMedida = new ScoUnidadeMedida();
		unMedida.setCodigo("0");
		faseSolicitacao.getItemAutorizacaoForn()
				.getItemPropostaFornecedor()
				.setUnidadeMedida(unMedida);

		faseSolicitacao.getItemAutorizacaoForn()
				.getItemPropostaFornecedor()
				.setValorUnitario(BigDecimal.ZERO);
		
		final ScoMaterialSicon scoMaterialSicon = new ScoMaterialSicon();
		scoMaterialSicon.setCodigoSicon(0);
		
		ScoServico servico = new ScoServico();
		servico.setCodigo(0);
		
		
		ScoServicoSicon scoServicoSicon = new ScoServicoSicon();
		
		scoServicoSicon.setServico(servico);
		scoServicoSicon.setCodigoSicon(0);
		
		final List<ScoServicoSicon> listaScoServicoSicon = new ArrayList<ScoServicoSicon>();
		listaScoServicoSicon.add(scoServicoSicon);

		Mockito.when(mockedPacFacade.obterLicitacao(Mockito.anyInt())).thenReturn(licitacao);

		Mockito.when(mockedCadastrosBasicosSiconFacade.pesquisarMaterialSicon(Mockito.anyInt(), Mockito.any(ScoMaterial.class), Mockito.any(DominioSituacao.class),
				Mockito.any(ScoGrupoMaterial.class))).thenReturn(scoMaterialSicon);

		Mockito.when(mockedCadastrosBasicosSiconFacade.obterPorCodigoServico(Mockito.any(ScoServico.class))).thenReturn(listaScoServicoSicon);

		cnet.setItens(new ObjectFactory().createCnetItens());

		this.systemUnderTest.addItensOrigemAutomaticaMaior(contrato, cnet);
	}

	@Test
	public void addItensOrigemAutomaticaMenorTest() {

		final ScoContrato contrato = new ScoContrato();

		final Cnet cnet = new ObjectFactory().createCnet();

		final List<ScoAfContrato> afContratoList = new ArrayList<ScoAfContrato>();

		final ScoAfContrato afContrato = new ScoAfContrato();

		final List<ScoItemAutorizacaoForn> itensAutorizacaoFornList = new ArrayList<ScoItemAutorizacaoForn>();

		final ScoItemAutorizacaoForn itensAutorizacaoForn = new ScoItemAutorizacaoForn();

		final List<ScoFaseSolicitacao> faseSolicitacaoList = new ArrayList<ScoFaseSolicitacao>();

		final ScoFaseSolicitacao faseSolicitacao = new ScoFaseSolicitacao();

		final ScoLicitacao licitacao = new ScoLicitacao();

		contrato.setScoAfContratos(afContratoList);
		afContrato.setScoContrato(contrato);
		afContrato.setScoAutorizacoesForn(new ScoAutorizacaoForn());

		afContrato.getScoAutorizacoesForn().setItensAutorizacaoForn(
				itensAutorizacaoFornList);

		faseSolicitacao.setItemAutorizacaoForn(itensAutorizacaoForn);

		faseSolicitacaoList.add(faseSolicitacao);

		itensAutorizacaoForn.setScoFaseSolicitacao(faseSolicitacaoList);

		itensAutorizacaoFornList.add(itensAutorizacaoForn);

		afContratoList.add(afContrato);

		faseSolicitacao
				.setSolicitacaoDeCompra(new ScoSolicitacaoDeCompra());
		faseSolicitacao.getSolicitacaoDeCompra().setMaterial(
				new ScoMaterial());
		faseSolicitacao.getSolicitacaoDeCompra().getMaterial().setCodigo(0);

		faseSolicitacao.setSolicitacaoServico(new ScoSolicitacaoServico());
		faseSolicitacao.getSolicitacaoServico()
				.setServico(new ScoServico());
		faseSolicitacao.getSolicitacaoServico().getServico().setCodigo(0);

		faseSolicitacao
				.setItemAutorizacaoForn(new ScoItemAutorizacaoForn());
		faseSolicitacao.getItemAutorizacaoForn()
				.setItemPropostaFornecedor(new ScoItemPropostaFornecedor());
		faseSolicitacao.getItemAutorizacaoForn()
				.getItemPropostaFornecedor()
				.setMarcaComercial(new ScoMarcaComercial());
		faseSolicitacao.getItemAutorizacaoForn()
				.getItemPropostaFornecedor().getMarcaComercial()
				.setDescricao("");

		faseSolicitacao.getItemAutorizacaoForn().setQtdeSolicitada(0);

		faseSolicitacao.getItemAutorizacaoForn().setUnidadeMedida(new ScoUnidadeMedida());

		licitacao.setNumero(123);
		
		contrato.setLicitacao(licitacao);

		faseSolicitacao.getItemAutorizacaoForn()
				.getItemPropostaFornecedor().setQuantidade(0L);

		faseSolicitacao.getItemAutorizacaoForn()
				.getItemPropostaFornecedor()
				.setValorUnitario(BigDecimal.ZERO);
		
		final ScoMaterialSicon scoMaterialSicon = new ScoMaterialSicon();
		scoMaterialSicon.setCodigoSicon(0);
		
		ScoServico servico = new ScoServico();
		servico.setCodigo(0);
		
		
		ScoServicoSicon scoServicoSicon = new ScoServicoSicon();
		
		scoServicoSicon.setServico(servico);
		scoServicoSicon.setCodigoSicon(0);
		
		final List<ScoServicoSicon> listaScoServicoSicon = new ArrayList<ScoServicoSicon>();
		listaScoServicoSicon.add(scoServicoSicon);

		Mockito.when(mockedPacFacade.obterLicitacao(Mockito.anyInt())).thenReturn(licitacao);

		Mockito.when(mockedCadastrosBasicosSiconFacade.pesquisarMaterialSicon(Mockito.anyInt(), Mockito.any(ScoMaterial.class), Mockito.any(DominioSituacao.class),
				Mockito.any(ScoGrupoMaterial.class))).thenReturn(scoMaterialSicon);

		Mockito.when(mockedCadastrosBasicosSiconFacade.obterPorCodigoServico(Mockito.any(ScoServico.class))).thenReturn(listaScoServicoSicon);
		
		cnet.setItens(new ObjectFactory().createCnetItens());

		this.systemUnderTest.addItensOrigemAutomaticaMenor(contrato, cnet);
	}

	@Test
	public void setAquisicaoOrigemAutomaticaTest() {

		final ScoContrato contrato = new ScoContrato();

		final Cnet cnet = new ObjectFactory().createCnet();

		final List<ScoAfContrato> afContratoList = new ArrayList<ScoAfContrato>();

		final ScoAfContrato afContrato = new ScoAfContrato();

		final List<ScoItemAutorizacaoForn> itensAutorizacaoFornList = new ArrayList<ScoItemAutorizacaoForn>();

		final ScoItemAutorizacaoForn itensAutorizacaoForn = new ScoItemAutorizacaoForn();

		final List<ScoFaseSolicitacao> faseSolicitacaoList = new ArrayList<ScoFaseSolicitacao>();

		final ScoFaseSolicitacao faseSolicitacao = new ScoFaseSolicitacao();

		contrato.setScoAfContratos(afContratoList);
		afContrato.setScoContrato(contrato);
		afContrato.setScoAutorizacoesForn(new ScoAutorizacaoForn());

		afContrato.getScoAutorizacoesForn().setItensAutorizacaoForn(
				itensAutorizacaoFornList);

		faseSolicitacao.setItemAutorizacaoForn(itensAutorizacaoForn);

		faseSolicitacaoList.add(faseSolicitacao);

		itensAutorizacaoForn.setScoFaseSolicitacao(faseSolicitacaoList);

		itensAutorizacaoFornList.add(itensAutorizacaoForn);

		afContratoList.add(afContrato);

		faseSolicitacao
				.setSolicitacaoDeCompra(new ScoSolicitacaoDeCompra());
		faseSolicitacao.getSolicitacaoDeCompra().setMaterial(
				new ScoMaterial());
		faseSolicitacao.getSolicitacaoDeCompra().getMaterial().setCodigo(0);

		faseSolicitacao.setSolicitacaoServico(new ScoSolicitacaoServico());
		faseSolicitacao.getSolicitacaoServico()
				.setServico(new ScoServico());
		faseSolicitacao.getSolicitacaoServico().getServico().setCodigo(0);

		this.systemUnderTest.setAquisicaoOrigemAutomatica(contrato, cnet);

	}

	@Test
	public void setAquisicaoOrigemManualTest() {
		final ScoContrato contrato = new ScoContrato();

		final Cnet cnet = new ObjectFactory().createCnet();

		final List<ScoItensContrato> itensList = new ArrayList<ScoItensContrato>();

		final ScoItensContrato itensContrato = new ScoItensContrato();

		itensContrato.setContrato(contrato);
		itensContrato.setServico(new ScoServico());
		itensContrato.getServico().setCodigo(0);

		itensList.add(itensContrato);

		cnet.setItens(new ObjectFactory().createCnetItens());

		Mockito.when(mockedScoItensContratoDAO.obterPorChavePrimaria(Mockito.any(ScoContrato.class))).thenReturn(itensContrato);

		this.systemUnderTest.setAquisicaoOrigemManual(itensList, cnet);
	}
	
	
	protected Log getLog() {
		return LogFactory.getLog(GerarXmlEnvioContratoONTest.class);
	}
}
