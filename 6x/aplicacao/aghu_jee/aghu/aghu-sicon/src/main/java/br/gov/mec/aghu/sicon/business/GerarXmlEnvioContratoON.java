package br.gov.mec.aghu.sicon.business;

import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.dominio.DominioOrigemContrato;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterialSicon;
import br.gov.mec.aghu.model.ScoServicoSicon;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.business.EnvioContratoSiasgSiconON.EnvioContratoSiasgSiconONExceptionCode;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.sicon.dao.ScoContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoItensContratoDAO;
import br.gov.mec.aghu.sicon.util.Cnet;
import br.gov.mec.aghu.sicon.util.Cnet.Itens.Item;
import br.gov.mec.aghu.sicon.util.ObjectFactory;
import br.gov.mec.aghu.sicon.util.SiconUtil;
import br.gov.mec.aghu.sicon.vo.DadosEnvioVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.AtributoEmSeamContextManager" })
@Stateless
public class GerarXmlEnvioContratoON extends BaseBusiness {

	private static final String DD_M_MYYYY = "ddMMyyyy";

	private static final String _00 = "#.00";

	private static final Log LOG = LogFactory.getLog(GerarXmlEnvioContratoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPacFacade pacFacade;

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@Inject
	private ScoContratoDAO scoContratoDAO;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private ScoItensContratoDAO scoItensContratoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4326637574044070794L;

	static final String AGHU_SICON = "aghuSicon";

	private Writer xmlEnvio;

	// Variáveis de controle para aquisições de materias/serviços em contratos
	// automáticos
	private boolean aquisicaoMaterial;
	private boolean aquisicaoServico;

	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public DadosEnvioVO gerarXml(Integer seqContrato, String autenticacaoSicon) throws ApplicationBusinessException,
			ApplicationBusinessException {

		// Inicializa variáveis de controle de materias/serviços em contratos
		// automáticos.
		this.setAquisicaoMaterial(false);
		this.setAquisicaoServico(false);

		DadosEnvioVO dadosEnvioVO = new DadosEnvioVO();
		Marshaller marshaller = null;

		try {
			JAXBContext context = JAXBContext.newInstance("br.gov.mec.aghu.sicon.util");
			marshaller = context.createMarshaller();

		} catch (JAXBException e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(

			EnvioContratoSiasgSiconONExceptionCode.ERRO_ENVIO_XML);
		}

		ScoContrato contrato = getScoContratoDAO().obterPorChavePrimaria(seqContrato);

		if (contrato == null) {
			throw new ApplicationBusinessException(EnvioContratoSiasgSiconONExceptionCode.CONTRATO_NAO_ENCONTRADO);
		}

		Cnet cnet = new ObjectFactory().createCnet();

		AghParametros url = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AMB_INTEGRACAO_SICON);

		// Tags XML

		cnet.setAmbiente(url.getVlrTexto().toLowerCase());

		cnet.setSistema(AGHU_SICON);

		cnet.setAcao(SiconUtil.obtemAcao(contrato));

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();
		
		Long cpf = servidorLogado.getPessoaFisica().getCpf();

		if (cpf != null) {
			String strCpf = cpf.toString();
			strCpf = StringUtils.leftPad(strCpf, 11, "0");
			cnet.setCpf(strCpf);

		} else {
			throw new ApplicationBusinessException(EnvioContratoSiasgSiconONExceptionCode.CPF_NAO_ENCONTRADO);
		}

		cnet.setSenha(autenticacaoSicon);

		cnet.setUasg(getAghuFacade().getUasg());

		if (contrato.getUasgSubrog() != null) {
			cnet.setUasgSubrog(new ObjectFactory().createCnetUasgSubrog(contrato.getUasgSubrog().toString()));
		} else {
			cnet.setUasgSubrog(new ObjectFactory().createCnetUasgSubrog(""));
		}

		if (contrato.getCodInternoUasg() == null || StringUtils.isBlank(contrato.getCodInternoUasg())) {

			String codInterno = contrato.getNrContrato().toString() + SiconUtil.obtemAnoString(contrato.getDtAssinatura());

			cnet.setCodInternoUasg(new ObjectFactory().createCnetCodInternoUasg(codInterno));

		} else {
			cnet.setCodInternoUasg(new ObjectFactory().createCnetCodInternoUasg(contrato.getCodInternoUasg()));
		}

		cnet.setTipo(contrato.getTipoContratoSicon().obtemCodigoSicon());

		cnet.setNumero(contrato.getNrContrato().intValue());

		if (contrato.getDtAssinatura() != null) {
			cnet.setAno(SiconUtil.obtemAnoXGC(contrato.getDtAssinatura()));
		}

		// Tags preenchidas em branco porém são obrigatórias para envio (contém
		// valores quando for aditivos)
		cnet.setTipoContratoOriginal(new ObjectFactory().createCnetTipoContratoOriginal(""));
		cnet.setNumeroContratoOriginal(new ObjectFactory().createCnetNumeroContratoOriginal(""));
		cnet.setAnoContratoOriginal(new ObjectFactory().createCnetAnoContratoOriginal(""));

		if (contrato.getUasgLicit() != null) {
			cnet.setUasgLicit(new ObjectFactory().createCnetUasgLicit(contrato.getUasgLicit().toString()));
		}

		if (contrato.getModalidadeLicitacao() != null && contrato.getModalidadeLicitacao().getCodigo() != null) {
			cnet.setModalidadeLicit(new ObjectFactory().createCnetModalidadeLicit(contrato.getModalidadeLicitacao().obtemCodigoModalidade()));
		}

		// Licitação aceita até o valor 99.999
		if (contrato.getLicitacao() != null && contrato.getLicitacao().getNumero() != null && contrato.getLicitacao().getNumero() <= 99999) {

			cnet.setNumeroLicit(new ObjectFactory().createCnetNumeroLicit(contrato.getLicitacao().getNumero().toString()));
		} else {
			cnet.setNumeroLicit(new ObjectFactory().createCnetNumeroLicit(""));
		}

		if (contrato.getLicitacao() != null && contrato.getLicitacao().getDtPublicacao() != null) {
			cnet.setAnoLicit(new ObjectFactory().createCnetAnoLicit(SiconUtil.obtemAnoString(contrato.getLicitacao().getDtPublicacao()).toString()));
		}

		if (contrato.getInciso() != null) {
			cnet.setInciso(new ObjectFactory().createCnetInciso(contrato.getInciso().toString()));
		}

		if (contrato.getDtPublicacao() != null) {
			cnet.setDtPublicacao(new ObjectFactory().createCnetDtPublicacao(DateUtil.obterDataFormatada(contrato.getDtPublicacao(), DD_M_MYYYY)));
		}

		if (contrato.getObjetoContrato() != null) {
			cnet.setObjeto(new ObjectFactory().createCnetObjeto(contrato.getObjetoContrato().toString()));
		}

		if (contrato.getFornecedor() != null && contrato.getFornecedor().getCgc() != null) {

			String strCgc = SiconUtil.removeCaracteresCNPJCPF(contrato.getFornecedor().getCgc().toString());
			strCgc = StringUtils.leftPad(strCgc, 14, "0");

			cnet.setCnpjcpfContratado(new ObjectFactory().createCnetCnpjcpfContratado(strCgc));
		} else if (contrato.getFornecedor() != null && contrato.getFornecedor().getCpf() != null) {

			String strCpf = SiconUtil.removeCaracteresCNPJCPF(contrato.getFornecedor().getCpf().toString());
			strCpf = StringUtils.leftPad(strCpf, 11, "0");

			cnet.setCnpjcpfContratado(new ObjectFactory().createCnetCnpjcpfContratado(strCpf));
		}

		if (contrato.getFornecedor().getRazaoSocial() != null) {
			cnet.setRazaoSocialContratado(new ObjectFactory().createCnetRazaoSocialContratado(contrato.getFornecedor().getRazaoSocial().toString()));
		}

		if (getAghuFacade().getCgc() != null) {

			String strCgc = SiconUtil.removeCaracteresCNPJCPF(getAghuFacade().getCgc());
			strCgc = StringUtils.leftPad(strCgc, 14, "0");

			cnet.setCnpjContratante(new ObjectFactory().createCnetCnpjContratante(strCgc));
		}

		if (contrato.getFundamentoLegal() != null) {
			cnet.setFundamentoLegal(new ObjectFactory().createCnetFundamentoLegal(contrato.getFundamentoLegal().toString()));
		}

		if (contrato.getDtInicioVigencia() != null) {
			cnet.setDtInicioVigencia(new ObjectFactory().createCnetDtInicioVigencia(DateUtil.obterDataFormatada(contrato.getDtInicioVigencia(), DD_M_MYYYY)));
		}

		if (contrato.getDtFimVigencia() != null) {
			cnet.setDtFimVigencia(new ObjectFactory().createCnetDtFimVigencia(DateUtil.obterDataFormatada(contrato.getDtFimVigencia(), DD_M_MYYYY)));
		}

		if (contrato.getDtAssinatura() != null) {
			cnet.setDtAssinatura(new ObjectFactory().createCnetDtAssinatura(DateUtil.obterDataFormatada(contrato.getDtAssinatura(), DD_M_MYYYY)));
		}

		if (contrato.getValorTotal() != null) {
			String valorFormatado = AghuNumberFormat.formatarValor(contrato.getValorTotal(), _00);
			valorFormatado = removePontoVirgula(valorFormatado);
			cnet.setValorTotal(new ObjectFactory().createCnetValorTotal(valorFormatado));
		}

		cnet.setTipoEmergencial("");

		List<ScoItensContrato> itensContratosList;
		cnet.setItens(new ObjectFactory().createCnetItens());

		if (contrato.getIndOrigem() == DominioOrigemContrato.M) {

			itensContratosList = contrato.getItensContrato();

			if (itensContratosList.size() > 0) {

				addItensOrigemManual(itensContratosList, cnet);
				setAquisicaoOrigemManual(itensContratosList, cnet);
			}

		} else if (contrato.getIndOrigem() == DominioOrigemContrato.A) {

			if (contrato.getValEfetAfs() != null && contrato.getSituacao().equals(DominioSituacaoEnvioContrato.AR)
					&& contrato.getValEfetAfs().compareTo(contrato.getValorTotal()) > 0) {

				addItensOrigemAutomaticaMaior(contrato, cnet);

			} else {

				addItensOrigemAutomaticaMenor(contrato, cnet);
			}

			setAquisicaoOrigemAutomatica(contrato, cnet);
		}

		xmlEnvio = new StringWriter();

		try {
			marshaller.marshal(cnet, xmlEnvio);

		} catch (JAXBException e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(EnvioContratoSiasgSiconONExceptionCode.ERRO_CRIACAO_XML);
		}

		dadosEnvioVO.setXmlEnvio(xmlEnvio.toString());
		dadosEnvioVO.setCnet(cnet);

		return dadosEnvioVO;
	}

	protected void setAquisicaoOrigemAutomatica(ScoContrato contrato, Cnet cnet) {

		if (isAquisicaoMaterial() && isAquisicaoServico()) {
			cnet.setAquisicao(new ObjectFactory().createCnetAquisicao("MS"));
		} else {
			if (isAquisicaoMaterial()) {
				cnet.setAquisicao(new ObjectFactory().createCnetAquisicao("M"));
			} else {
				if (isAquisicaoServico()) {
					cnet.setAquisicao(new ObjectFactory().createCnetAquisicao("S"));
				}
			}
		}
	}

	protected void setAquisicaoOrigemManual(List<ScoItensContrato> itensContratosList, Cnet cnet) {

		boolean material = false;
		boolean servico = false;

		for (ScoItensContrato itemContrato : itensContratosList) {

			if (itemContrato.getMaterial() != null) {
				material = true;
			} else {
				if (itemContrato.getServico() != null) {
					servico = true;
				}
			}
		}

		if (material && servico) {
			cnet.setAquisicao(new ObjectFactory().createCnetAquisicao("MS"));

		} else {

			if (material) {
				cnet.setAquisicao(new ObjectFactory().createCnetAquisicao("M"));
			} else {
				if (servico) {
					cnet.setAquisicao(new ObjectFactory().createCnetAquisicao("S"));
				}
			}
		}

	}

	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	protected void addItensOrigemAutomaticaMenor(ScoContrato contrato, Cnet cnet) {

		Integer cont2 = 0;
		
		for (ScoAfContrato afContratos : contrato.getScoAfContratos()) {		

			for (ScoItemAutorizacaoForn itensAutorizacaoForn : afContratos.getScoAutorizacoesForn().getItensAutorizacaoForn()) {

				for (ScoFaseSolicitacao faseSolicitacao : itensAutorizacaoForn.getScoFaseSolicitacao()) {

					Item item = new ObjectFactory().createCnetItensItem();

					boolean tagMarca = false;

					ScoLicitacao scoLicitacao = getPacFacade().obterLicitacao(contrato.getLicitacao().getNumero());

					if (faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor() != null
							&& faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getValorUnitario() != null) {
						BigDecimal vlUnitario = faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getValorUnitario();

						if (vlUnitario.equals(BigDecimal.ZERO)) {
							item = null;
							continue;
						}
					}

					cont2 += 1;
					item.setNumeroItem(cont2);

					if (faseSolicitacao.getSolicitacaoDeCompra() != null && faseSolicitacao.getSolicitacaoDeCompra().getMaterial() != null
							&& faseSolicitacao.getSolicitacaoDeCompra().getMaterial().getCodigo() != null) {

						ScoMaterialSicon scoMaterialSicon = this.getCadastrosBasicosSiconFacade().pesquisarMaterialSicon(null,
								faseSolicitacao.getSolicitacaoDeCompra().getMaterial(), null, null);

						if (scoMaterialSicon != null) {
							item.setCodigoMatServ(new ObjectFactory().createCnetItensItemCodigoMatServ(scoMaterialSicon.getCodigoSicon().toString()));
						}

						item.setIndicadorMatServ(new ObjectFactory().createCnetItensItemIndicadorMatServ("M"));

						tagMarca = true;
						this.setAquisicaoMaterial(true);
					}

					if (faseSolicitacao.getSolicitacaoDeCompra() != null && faseSolicitacao.getSolicitacaoDeCompra().getMaterial() != null
							&& faseSolicitacao.getSolicitacaoDeCompra().getMaterial().getNome() != null) {
						item.setDescricao(new ObjectFactory().createCnetItensItemDescricao(faseSolicitacao.getSolicitacaoDeCompra().getMaterial()
								.getNome()));
					}

					if (faseSolicitacao.getSolicitacaoServico() != null && faseSolicitacao.getSolicitacaoServico().getServico() != null
							&& faseSolicitacao.getSolicitacaoServico().getServico().getCodigo() != null) {

						List<ScoServicoSicon> listScoServicoSicon = this.getCadastrosBasicosSiconFacade().obterPorCodigoServico(
								faseSolicitacao.getSolicitacaoServico().getServico());

						if (listScoServicoSicon.size() > 0) {
							item.setCodigoMatServ(new ObjectFactory().createCnetItensItemCodigoMatServ(listScoServicoSicon.get(0).getCodigoSicon()
									.toString()));
						}

						item.setIndicadorMatServ(new ObjectFactory().createCnetItensItemIndicadorMatServ("S"));

						tagMarca = false;
						this.setAquisicaoServico(true);
					}

					if (faseSolicitacao.getSolicitacaoServico() != null && faseSolicitacao.getSolicitacaoServico().getServico() != null
							&& faseSolicitacao.getSolicitacaoServico().getServico().getNome() != null) {
						item.setDescricao(new ObjectFactory().createCnetItensItemDescricao(faseSolicitacao.getSolicitacaoServico().getServico().getNome()));
					}

					if (faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor() != null
							&& faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getMarcaComercial() != null
							&& faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getMarcaComercial().getDescricao() != null) {

						// Prencher Tag Marca somente para item de Material
						// Para ítens de serviço Tag Marca fica em branco
						if (tagMarca) {
							item.setMarca(new ObjectFactory().createCnetItensItemMarca(faseSolicitacao.getItemAutorizacaoForn()
									.getItemPropostaFornecedor().getMarcaComercial().getDescricao().toString()));
						} else {
							item.setMarca(new ObjectFactory().createCnetItensItemMarca(""));
						}

					}

					if (faseSolicitacao.getItemAutorizacaoForn() != null && faseSolicitacao.getItemAutorizacaoForn().getQtdeSolicitada() != null) {
						item.setQuantidade(new ObjectFactory().createCnetItensItemQuantidade(faseSolicitacao.getItemAutorizacaoForn()
								.getItemPropostaFornecedor().getQuantidade().toString()));
					}

					if (faseSolicitacao.getItemAutorizacaoForn() != null && faseSolicitacao.getItemAutorizacaoForn().getUmdCodigoForn() != null) {
						String unidade = faseSolicitacao.getItemAutorizacaoForn().getUmdCodigoForn().toString();

						if (unidade.length() > 20) {
							unidade = unidade.substring(0, 20);
						}

						item.setUnidade(new ObjectFactory().createCnetItensItemUnidade(unidade));
					} else {
						if (itensAutorizacaoForn.getItemPropostaFornecedor() != null
								&& itensAutorizacaoForn.getItemPropostaFornecedor().getUnidadeMedida() != null) {
							String unidade = itensAutorizacaoForn.getItemPropostaFornecedor().getUnidadeMedida().getDescricao();

							if (unidade.length() > 20) {
								unidade = unidade.substring(0, 20);
							}

							item.setUnidade(new ObjectFactory().createCnetItensItemUnidade(unidade));
						}
					}

					if (faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor() != null
							&& faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getValorUnitario() != null) {
						BigDecimal vlUnitario = faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getValorUnitario();

						BigDecimal quantidade = new BigDecimal(faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getQuantidade());
						BigDecimal freqEntrega = new BigDecimal(scoLicitacao.getFrequenciaEntrega() != null ? scoLicitacao.getFrequenciaEntrega() : 1);
						BigDecimal vlTotal = vlUnitario.multiply(quantidade).multiply(freqEntrega);

						String valorFormatado = AghuNumberFormat.formatarValor(vlTotal, _00);
						valorFormatado = removePontoVirgula(valorFormatado);

						item.setValorTotal(new ObjectFactory().createCnetItensItemValorTotal(valorFormatado));

					}

					if (item != null) {
						cnet.getItens().getItem().add(item);
					}

				}

			}

		}
	}

	/**
	 * Adiciona os itens de origem automático em que a soma das AFs ultrapassou
	 * o valor total do contrato
	 * 
	 * @param contrato
	 * @param cnet
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	protected void addItensOrigemAutomaticaMaior(ScoContrato contrato, Cnet cnet) {
		for (ScoAfContrato afContratos : contrato.getScoAfContratos()) {

			Integer cont1 = 0;

			for (ScoItemAutorizacaoForn itensAutorizacaoForn : afContratos.getScoAutorizacoesForn().getItensAutorizacaoForn()) {

				for (ScoFaseSolicitacao faseSolicitacao : itensAutorizacaoForn.getScoFaseSolicitacao()) {

					Item item = new ObjectFactory().createCnetItensItem();

					boolean tagMarca = false;

					ScoLicitacao scoLicitacao = getPacFacade().obterLicitacao(contrato.getLicitacao().getNumero());

					if (faseSolicitacao.getItemAutorizacaoForn() != null && faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor() != null
							&& faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getValorUnitario() != null
							&& faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getQuantidade() != null
							&& scoLicitacao.getFrequenciaEntrega() != null) {

						BigDecimal vlUnitario = faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getValorUnitario();

						if (vlUnitario.equals(BigDecimal.ZERO)) {
							item = null;
							continue;
						}
					}

					cont1 += 1;
					item.setNumeroItem(cont1);

					if (faseSolicitacao.getSolicitacaoDeCompra() != null && faseSolicitacao.getSolicitacaoDeCompra().getMaterial() != null
							&& faseSolicitacao.getSolicitacaoDeCompra().getMaterial().getCodigo() != null) {

						ScoMaterialSicon scoMaterialSicon = this.getCadastrosBasicosSiconFacade().pesquisarMaterialSicon(null,
								faseSolicitacao.getSolicitacaoDeCompra().getMaterial(), null, null);

						if (scoMaterialSicon != null) {
							item.setCodigoMatServ(new ObjectFactory().createCnetItensItemCodigoMatServ(scoMaterialSicon.getCodigoSicon().toString()));

						}

						item.setIndicadorMatServ(new ObjectFactory().createCnetItensItemIndicadorMatServ("M"));

						tagMarca = true;
						this.setAquisicaoMaterial(true);

					}

					// FIXME Verificar a utilidade deste if, havia alguma regra
					// de negócio que faltou ser implementada?
					// if (faseSolicitacao.getSolicitacaoDeCompra() != null
					// && faseSolicitacao.getSolicitacaoDeCompra()
					// .getMaterial() != null
					// && faseSolicitacao.getSolicitacaoDeCompra()
					// .getMaterial().getNome() != null) {
					//
					// }

					if (faseSolicitacao.getSolicitacaoDeCompra() != null && faseSolicitacao.getSolicitacaoDeCompra().getMaterial() != null
							&& faseSolicitacao.getSolicitacaoDeCompra().getMaterial().getNome() != null) {
						item.setDescricao(new ObjectFactory().createCnetItensItemDescricao(faseSolicitacao.getSolicitacaoDeCompra().getMaterial()
								.getNome()));

					}
					if (faseSolicitacao.getSolicitacaoServico() != null && faseSolicitacao.getSolicitacaoServico().getServico() != null
							&& faseSolicitacao.getSolicitacaoServico().getServico().getCodigo() != null) {

						List<ScoServicoSicon> listScoServicoSicon = this.getCadastrosBasicosSiconFacade().obterPorCodigoServico(
								faseSolicitacao.getSolicitacaoServico().getServico());

						if (listScoServicoSicon.size() > 0) {
							item.setCodigoMatServ(new ObjectFactory().createCnetItensItemCodigoMatServ(listScoServicoSicon.get(0).getCodigoSicon()
									.toString()));
						}

						item.setIndicadorMatServ(new ObjectFactory().createCnetItensItemIndicadorMatServ("S"));

						tagMarca = false;
						this.setAquisicaoServico(true);
					}

					if (faseSolicitacao.getSolicitacaoServico() != null && faseSolicitacao.getSolicitacaoServico().getServico() != null
							&& faseSolicitacao.getSolicitacaoServico().getServico().getNome() != null) {
						item.setDescricao(new ObjectFactory().createCnetItensItemDescricao(faseSolicitacao.getSolicitacaoServico().getServico().getNome()));
					}

					if (faseSolicitacao.getItemAutorizacaoForn() != null && faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor() != null
							&& faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getMarcaComercial() != null
							&& faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getMarcaComercial().getDescricao() != null) {

						// Prencher Tag Marca somente para item de Material
						// Para ítens de serviço Tag Marca fica em branco
						if (tagMarca) {
							item.setMarca(new ObjectFactory().createCnetItensItemMarca(faseSolicitacao.getItemAutorizacaoForn()
									.getItemPropostaFornecedor().getMarcaComercial().getDescricao().toString()));
						} else {
							item.setMarca(new ObjectFactory().createCnetItensItemMarca(""));
						}

					}

					if (faseSolicitacao.getItemAutorizacaoForn() != null && faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor() != null
							&& faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getQuantidade() != null) {
						item.setQuantidade(new ObjectFactory().createCnetItensItemQuantidade(faseSolicitacao.getItemAutorizacaoForn()
								.getItemPropostaFornecedor().getQuantidade().toString()));
					}

					if (faseSolicitacao.getItemAutorizacaoForn() != null && faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor() != null
							&& faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getUnidadeMedida() != null) {
						String unidade = faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getUnidadeMedida().getCodigo().toString();
						if (unidade.length() > 20) {
							unidade = unidade.substring(0, 20);
						}

						item.setUnidade(new ObjectFactory().createCnetItensItemUnidade(unidade));
					} else {
						if (itensAutorizacaoForn.getItemPropostaFornecedor() != null
								&& itensAutorizacaoForn.getItemPropostaFornecedor().getUnidadeMedida() != null) {
							String unidade = itensAutorizacaoForn.getItemPropostaFornecedor().getUnidadeMedida().getDescricao();

							if (unidade.length() > 20) {
								unidade = unidade.substring(0, 20);
							}

							item.setUnidade(new ObjectFactory().createCnetItensItemUnidade(unidade));
						}
					}

					if (faseSolicitacao.getItemAutorizacaoForn() != null && faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor() != null
							&& faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getValorUnitario() != null
							&& faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getQuantidade() != null
							&& scoLicitacao.getFrequenciaEntrega() != null) {

						BigDecimal vlUnitario = faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getValorUnitario();
						BigDecimal quantidade = new BigDecimal(faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getQuantidade());
						BigDecimal freqEntrega = new BigDecimal(scoLicitacao.getFrequenciaEntrega() != null ? scoLicitacao.getFrequenciaEntrega() : 1);
						BigDecimal vlTotal = vlUnitario.multiply(quantidade).multiply(freqEntrega);

						String valorFormatado = AghuNumberFormat.formatarValor(vlTotal, _00);
						valorFormatado = removePontoVirgula(valorFormatado);

						item.setValorTotal(new ObjectFactory().createCnetItensItemValorTotal(valorFormatado));
					}

					if (item != null) {
						cnet.getItens().getItem().add(item);
					}

				}
			}
		}
	}

	/**
	 * Adiciona os itens de origem manual fornecidos.
	 * 
	 * @param cnet
	 * @param itens
	 *            itens do contrato
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	protected void addItensOrigemManual(List<ScoItensContrato> itens, Cnet cnet) {

		for (ScoItensContrato scoItensContrato : itens) {

			if (scoItensContrato.getVlTotal().equals(BigDecimal.ZERO)) {
				continue;
			}

			ScoItensContrato itensContrato = getScoItensContratoDAO().obterPorChavePrimaria(scoItensContrato.getSeq());

			Item item = new ObjectFactory().createCnetItensItem();

			boolean tagMarca = false;

			item.setNumeroItem(scoItensContrato.getNrItem());

			if (itensContrato.getMaterial() != null) {

				ScoMaterialSicon scoMaterialSicon = this.getCadastrosBasicosSiconFacade().pesquisarMaterialSicon(null, itensContrato.getMaterial(), null, null);

				if (scoMaterialSicon != null) {
					item.setCodigoMatServ(new ObjectFactory().createCnetItensItemCodigoMatServ(scoMaterialSicon.getCodigoSicon().toString()));
				}

			} else if (itensContrato.getServico() != null) {

				List<ScoServicoSicon> listScoServicoSicon = this.getCadastrosBasicosSiconFacade().obterPorCodigoServico(itensContrato.getServico());

				if (listScoServicoSicon.size() > 0) {
					item.setCodigoMatServ(new ObjectFactory().createCnetItensItemCodigoMatServ(listScoServicoSicon.get(0).getCodigoSicon().toString()));
				}
			}

			if (itensContrato.getMaterial() != null) {
				item.setIndicadorMatServ(new ObjectFactory().createCnetItensItemIndicadorMatServ("M"));

				tagMarca = true;

			} else if (itensContrato.getServico() != null) {
				item.setIndicadorMatServ(new ObjectFactory().createCnetItensItemIndicadorMatServ("S"));

				tagMarca = false;
			}

			if (scoItensContrato.getMarcaComercial() != null && scoItensContrato.getMarcaComercial().getDescricao() != null) {

				// Prencher Tag Marca somente para item de Material
				// Para ítens de serviço Tag Marca fica em branco
				if (tagMarca) {
					item.setMarca(new ObjectFactory().createCnetItensItemMarca(scoItensContrato.getMarcaComercial().getDescricao().toString()));
				} else {
					item.setMarca(new ObjectFactory().createCnetItensItemMarca(""));
				}

			}

			if (scoItensContrato.getQuantidade() != null) {
				item.setQuantidade(new ObjectFactory().createCnetItensItemQuantidade(scoItensContrato.getQuantidade().toString()));
			}

			if (scoItensContrato.getUnidade() != null) {
				item.setUnidade(new ObjectFactory().createCnetItensItemUnidade(scoItensContrato.getUnidade().toString()));
			}

			if (scoItensContrato.getVlTotal() != null) {

				String valorFormatado = AghuNumberFormat.formatarValor(scoItensContrato.getVlTotal(), _00);
				valorFormatado = removePontoVirgula(valorFormatado);

				item.setValorTotal(new ObjectFactory().createCnetItensItemValorTotal(valorFormatado));
			}

			if (scoItensContrato.getDescricao() != null) {
				item.setDescricao(new ObjectFactory().createCnetItensItemDescricao(scoItensContrato.getDescricao().toString()));
			}

			if (item != null) {
				cnet.getItens().getItem().add(item);
			}

		}

	}

	/**
	 * Retira caracteres de ponto ou vírgula e retorna uma string formatada
	 * Exemplo: String valor = 100.10 , retornará String formatada = 10010
	 * 
	 */
	private String removePontoVirgula(String valor) {
		String valorFormatado = valor;

		if (valorFormatado != null) {
			valorFormatado = valorFormatado.replace(".", "");
			valorFormatado = valorFormatado.replace(",", "");
		}

		return valorFormatado;
	}

	public Writer getXmlEnvio() {
		return xmlEnvio;
	}

	public void setXmlEnvio(Writer xmlEnvio) {
		this.xmlEnvio = xmlEnvio;
	}

	protected ScoContratoDAO getScoContratoDAO() {
		return scoContratoDAO;
	}

	protected ScoItensContratoDAO getScoItensContratoDAO() {
		return scoItensContratoDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}

	protected ICadastrosBasicosSiconFacade getCadastrosBasicosSiconFacade() {
		return cadastrosBasicosSiconFacade;
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	protected IPacFacade getPacFacade() {
		return pacFacade;
	}

	public boolean isAquisicaoMaterial() {
		return aquisicaoMaterial;
	}

	public void setAquisicaoMaterial(boolean aquisicaoMaterial) {
		this.aquisicaoMaterial = aquisicaoMaterial;
	}

	public boolean isAquisicaoServico() {
		return aquisicaoServico;
	}

	public void setAquisicaoServico(boolean aquisicaoServico) {
		this.aquisicaoServico = aquisicaoServico;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
