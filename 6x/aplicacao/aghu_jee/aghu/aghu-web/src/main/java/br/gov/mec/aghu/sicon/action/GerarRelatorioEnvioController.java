package br.gov.mec.aghu.sicon.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.dominio.DominioOrigemContrato;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.vo.DadosEnvioVO;
import br.gov.mec.aghu.sicon.vo.EnvioContratoSiconVO;
import br.gov.mec.aghu.sicon.vo.ItemContratoVO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;


public class GerarRelatorioEnvioController extends ActionReport {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(GerarRelatorioEnvioController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 3260891495643182821L;

	@EJB
	private static ISiconFacade siconFacade;
	
	
	@EJB
	private IPacFacade pacFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;

	/**
	 * Dados que irão constar no relatório gravado em Banco de Dados.
	 */
	private List<EnvioContratoSiconVO> colecao = new ArrayList<EnvioContratoSiconVO>(0);

	public void geraRelatorio(DadosEnvioVO dadosEnvioVO)
			throws BaseException {

		// Geração do relatório
		colecao = retornaContratoVO(dadosEnvioVO);
		DocumentoJasper documento = gerarDocumento();

		// Gravação no banco
		try {
			byte[] byteArray = documento.getPdfByteArray(false);
			
			// Usado para auxílio em desenvolvimento
			// FileUtils.writeByteArrayToFile(new File("/tmp/relatorio.pdf"), byteArray);

			if (dadosEnvioVO.getLogEnvioSicon() != null) {
				dadosEnvioVO.getLogEnvioSicon().setArqRel(byteArray);
				siconFacade.atualizaLogEnvioSicon(dadosEnvioVO
						.getLogEnvioSicon());
			}

		} catch (Exception e) {
			LOG.error("Erro ao gerar relatorio contratos sicon", e);
			apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
		}
	}

	@Override
	public Collection<EnvioContratoSiconVO> recuperarColecao() {
		return this.colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/sicon/report/envioContrato.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("nomeRelatorio", "ENVIOCONTRATO");
		try {
			params.put("logoSusPath", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}		
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/sicon/report/");
		return params;
	}

	public List<EnvioContratoSiconVO> retornaContratoVO(
			DadosEnvioVO dadosEnvioVO) throws BaseException {

		ScoContrato contrato = siconFacade
				.obterContratoPorNumeroContrato(Long.valueOf(dadosEnvioVO.getCnet()
						.getNumero()));
		List<EnvioContratoSiconVO> listaEnvioContratoSiconVO = new ArrayList<EnvioContratoSiconVO>();
		EnvioContratoSiconVO envioContratoSiconVO = new EnvioContratoSiconVO();

		// Dados do Contrato
		String numContrato = contrato.getNrContrato().toString();
		envioContratoSiconVO.setNumeroContrato(NumberUtils
				.createInteger(numContrato));

		envioContratoSiconVO.setSituacao(dadosEnvioVO.getLogEnvioSicon()
				.getStatusEnvioFormatado());

		envioContratoSiconVO.setTipoContrato(contrato.getTipoContratoSicon()
				.getDescricao());

		envioContratoSiconVO.setCodInternoUasg(contrato.getCodInternoUasg());

		if (contrato.getModalidadeLicitacao() != null){
			envioContratoSiconVO.setModalidadeLicitacao(contrato.getModalidadeLicitacao().getDescricao());			
		}

		envioContratoSiconVO.setInciso(contrato.getInciso());

		if (contrato.getLicitacao() != null) {
			envioContratoSiconVO.setProcesso(contrato.getLicitacao()
					.getNumero());
		}

		if (contrato.getFornecedor() != null) {
			envioContratoSiconVO.setFornecedor(contrato.getFornecedor()
					.getCpfCnpj()
					+ " - "
					+ contrato.getFornecedor().getRazaoSocial());
		}

		envioContratoSiconVO
				.setDtInicioVigencia(contrato.getDtInicioVigencia());

		envioContratoSiconVO.setDtFimVigencia(contrato.getDtFimVigencia());

		envioContratoSiconVO.setValorTotal(contrato.getValorTotal());

		envioContratoSiconVO.setUasgSubrog(contrato.getUasgSubrog());

		envioContratoSiconVO.setUasgLicit(contrato.getUasgLicit());

		envioContratoSiconVO.setObjetoContrato(contrato.getObjetoContrato());

		envioContratoSiconVO.setFundamentoLegal(contrato.getFundamentoLegal());

		envioContratoSiconVO.setDtAssinatura(contrato.getDtAssinatura());

		envioContratoSiconVO.setDtPublicacao(contrato.getDtPublicacao());

		// Rodapé
		envioContratoSiconVO.setDtEnvio(new java.util.Date());

		envioContratoSiconVO.setAcao(dadosEnvioVO.getCnet().getAcao());

		envioContratoSiconVO.setUsuarioResponsavel(registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado()).getPessoaFisica().getNome());

		if (contrato.getIndOrigem() == DominioOrigemContrato.M) {
			envioContratoSiconVO.setOrigemDados("manual");

			envioContratoSiconVO.setItem(itensContratoOrigemManual(contrato));

		} else if (contrato.getIndOrigem() == DominioOrigemContrato.A) {
			
			if(dadosEnvioVO.getLogEnvioSicon().getIndVlrAf().equals(DominioSimNao.S)){
			
				envioContratoSiconVO.setOrigemDados("Envio conforme itens das propostas que originaram as AFs Vinculadas ao contrato");

				envioContratoSiconVO.setItem(this.itensContratoOrigemAutomaticaMenor(contrato, null));
			}else{
				envioContratoSiconVO
						.setOrigemDados("Envio conforme itens das AFs");

				envioContratoSiconVO
				.setItem(this.itensContratoOrigemAutomaticaMenor(contrato, "proposta"));
			}
		}

		listaEnvioContratoSiconVO.add(envioContratoSiconVO);

		return listaEnvioContratoSiconVO;
	}

	protected List<ItemContratoVO> itensContratoOrigemManual(ScoContrato contrato) {
		
		List<ScoItensContrato> itens = siconFacade.obterItensContratoPorContrato(contrato);

		List<ItemContratoVO> listaItemContratoVO = new ArrayList<ItemContratoVO>();

		for (ScoItensContrato itemContrato : itens) {
			ItemContratoVO itemContratoVO = new ItemContratoVO();

			itemContratoVO.setNumeroItem(itemContrato.getNrItem());

			itemContratoVO.setDescricaoItem(itemContrato.getDescricao());

			itemContratoVO.setQuantidade(itemContrato.getQuantidade());

			itemContratoVO.setValor(itemContrato.getVlUnitario());

			itemContratoVO.setUnidade(itemContrato.getUnidade());

			if (itemContrato.getMaterial() != null) {
				if (itemContrato.getMarcaComercial() != null) {
					itemContratoVO.setMarca(itemContrato.getMarcaComercial()
							.getDescricao());
				}
			} else {
				itemContratoVO.setMarca(null);
			}

			itemContratoVO.setOrigemContrato("proposta");

			listaItemContratoVO.add(itemContratoVO);
		}

		return listaItemContratoVO;
	}

	protected List<ItemContratoVO> itensContratoOrigemAutomaticaMenor(
			ScoContrato contrato, String origemContrato) {
		List<ItemContratoVO> listaItemContratoVO = new ArrayList<ItemContratoVO>();

		List<ScoAfContrato> listaAFs = siconFacade.obterAfByContrato(contrato);
		
		for (ScoAfContrato afContratos : listaAFs) {

			List<ScoItemAutorizacaoForn> listaItens = autFornecimentoFacade.pesquisarItemAfPorNumeroAf(afContratos.getScoAutorizacoesForn().getNumero());
			
			Integer numeroItem = 0;

			for (ScoItemAutorizacaoForn itensAutorizacaoForn : listaItens) {

				ItemContratoVO itemContratoVO = new ItemContratoVO();

				numeroItem++;

				itemContratoVO.setNumeroItem(numeroItem);

				for (ScoFaseSolicitacao faseSolicitacao : itensAutorizacaoForn
						.getScoFaseSolicitacao()) {

					itemContratoVO.setAfCp(afContratos.getScoAutorizacoesForn()
							.getPropostaFornecedor().getId().getLctNumero()
							.toString()
							+ "/"
							+ afContratos.getScoAutorizacoesForn()
									.getNroComplemento().toString());

					// Busca da origem do contrato, descrição e marca do item
					// sendo que busca-se a marca do item somente se ele for um material
					// quando item for um serviço a marca será 'null' e não será
					// impressa no relatório				
					if (faseSolicitacao.getSolicitacaoDeCompra() != null
							&& faseSolicitacao.getSolicitacaoDeCompra()
									.getMaterial() != null
							&& faseSolicitacao.getSolicitacaoDeCompra()
									.getMaterial().getNome() != null) {
						
						// Descrição do Material
						itemContratoVO.setDescricaoItem(faseSolicitacao
								.getSolicitacaoDeCompra().getMaterial()
								.getNome());
						
						// Marca do Material
						if (faseSolicitacao.getItemAutorizacaoForn() != null
								&& faseSolicitacao.getItemAutorizacaoForn()
										.getItemPropostaFornecedor() != null
								&& faseSolicitacao.getItemAutorizacaoForn()
										.getItemPropostaFornecedor()
										.getMarcaComercial() != null) {
							
							itemContratoVO.setMarca(faseSolicitacao
									.getItemAutorizacaoForn()
									.getItemPropostaFornecedor()
									.getMarcaComercial().getDescricao());

							itemContratoVO.setOrigemContrato(origemContrato);

						}

					} else {
						if (faseSolicitacao.getSolicitacaoServico() != null
								&& faseSolicitacao.getSolicitacaoServico()
										.getServico() != null
								&& faseSolicitacao.getSolicitacaoServico()
										.getServico().getNome() != null) {
							
							// Descrição do Serviço
							itemContratoVO.setDescricaoItem(faseSolicitacao
									.getSolicitacaoServico().getServico()
									.getNome());
						}
					}

					if (faseSolicitacao.getItemAutorizacaoForn() != null) {
						Long quantidade = faseSolicitacao
								.getItemAutorizacaoForn()
								.getItemPropostaFornecedor().getQuantidade();
							itemContratoVO.setQuantidade(quantidade.intValue());
					}

					if (faseSolicitacao.getItemAutorizacaoForn() != null && faseSolicitacao
							.getItemAutorizacaoForn().getUmdCodigoForn() != null) {
						itemContratoVO.setUnidade(faseSolicitacao
								.getItemAutorizacaoForn().getUmdCodigoForn().getCodigo());
					} else {
						itemContratoVO.setUnidade("");
					}

					BigDecimal valorTotal = this.calculaValorTotal(contrato,
							faseSolicitacao.getItemAutorizacaoForn());
					itemContratoVO.setValor(valorTotal);

				}

				listaItemContratoVO.add(itemContratoVO);
			}

		}

		return listaItemContratoVO;
	}

	/**
	 * Retorna o valor total do item.
	 * 
	 * @param contrato
	 * @param item
	 * @return
	 */
	private BigDecimal calculaValorTotal(ScoContrato contrato,
			ScoItemAutorizacaoForn item) {
		BigDecimal result = null;

		if (item != null
				&& item.getItemPropostaFornecedor().getValorUnitario() != null) {

			BigDecimal valorUnitario = item.getItemPropostaFornecedor()
					.getValorUnitario();
			BigDecimal quantidade = new BigDecimal(item
					.getItemPropostaFornecedor().getQuantidade());

			ScoLicitacao scoLicitacao = pacFacade.obterLicitacao(contrato
					.getLicitacao().getNumero());
			Integer freqEntrega = scoLicitacao.getFrequenciaEntrega();
			if (freqEntrega == null) {
				freqEntrega = 1;
			}

			// valor unitario * quantidade * frequencia
			result = valorUnitario.multiply(quantidade).multiply(
					new BigDecimal(freqEntrega));
		}
		return result;
	}
}
