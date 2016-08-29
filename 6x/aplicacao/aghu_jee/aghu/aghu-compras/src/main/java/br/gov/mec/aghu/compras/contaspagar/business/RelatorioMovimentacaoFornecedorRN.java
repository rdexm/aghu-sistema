package br.gov.mec.aghu.compras.contaspagar.business;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.contaspagar.dao.FcpRetencaoAliquotaDAO;
import br.gov.mec.aghu.compras.contaspagar.dao.FcpTituloDAO;
import br.gov.mec.aghu.compras.contaspagar.vo.RelatorioMovimentacaoFornecedorVO;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.estoque.dao.FcpValorTributosDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.parametrosistema.dao.AghParametrosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.Parametro;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.mail.AnexoEmail;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioMovimentacaoFornecedorRN extends BaseBusiness {

	private static final long serialVersionUID = -5266964997238334317L;

	@Inject
	private FcpTituloDAO fcpTituloDAO;

	@Inject
	private FcpValorTributosDAO fcpValorTributosDAO;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private FcpRetencaoAliquotaDAO fcpRetencaoAliquotaDAO;

	@Inject
	private ScoFornecedorDAO scoFornecedorDao;

	@Inject
	private AghParametrosDAO aghParametrosDAO;

	@Inject
	private EmailUtil emailUtil;
	
	@Inject
	@Parametro("mail_session_host")
	private String hostName;

	@Override
	protected Log getLogger() {
		return null;
	}

	private String[] obterParametroImposto() throws ApplicationBusinessException {
		AghParametros parametro = this.parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_LISTA_IMPOSTO_RETENCAO_ALIQUOTA);
		if (parametro == null) {
			throw new IllegalArgumentException();
		}
		String[] valor = parametro.getVlrTexto().split(","); 
				
		return valor;
	}

	// C2
	private Double obterTotalTributosPorNrsSeqEImposto(Integer nrsSeq)
			throws ApplicationBusinessException {
		Double total = Double.valueOf("0");
		if (nrsSeq != null) {
			total = fcpRetencaoAliquotaDAO.obterTotalTributosPorNrsSeqEImposto(
					nrsSeq, obterParametroImposto());
		}
		return (total == null ? Double.valueOf("0") : total);
	}

	// C3
	private Double obterTotalTributosPorNrsSeqETitulo(Integer nrsSeq,
			Integer titulo) throws ApplicationBusinessException {
		Double total = Double.valueOf("0");
		if (nrsSeq != null && titulo != null) {
			total = fcpValorTributosDAO.obterTotalTributosPorNrsSeqETitulo(
					titulo, nrsSeq);
		}
		return (total == null ? Double.valueOf("0") : total);
	}

	// RN1 VALOR_TRIBUTOS
	private Double calculaValorTributos(Integer nrsSeq, Integer titulo)
			throws ApplicationBusinessException {
		Double total = null;
		Double totalC2 = obterTotalTributosPorNrsSeqEImposto(nrsSeq);
		Double totalC3 = obterTotalTributosPorNrsSeqETitulo(nrsSeq, titulo);
		if (nrsSeq != null && titulo != null) {
			total = totalC2 + totalC3;
		}
		return (total == null ? Double.valueOf("0") : total);
	}

	// RN2 VALOR_PAGAMENTO
	private Double calcularValorPagamento(
			RelatorioMovimentacaoFornecedorVO relatorioMovimentacaoFornecedorVO)
			throws ApplicationBusinessException {
		Double campo1 = relatorioMovimentacaoFornecedorVO.getValorTitulo();
		Double campo2 = Double.valueOf("0");
		Double campo3 = Double.valueOf("0");
		Double campo4 = Double.valueOf("0");
		Double campo5 = Double.valueOf("0");
		Double valorConsultaC2 = Double.valueOf("0");
		Double valorConsultaC3 = Double.valueOf("0");
		Double calculoS1;
		Double calculoS2;

		if (relatorioMovimentacaoFornecedorVO.getNrsSeq() != null) {
			valorConsultaC2 = obterTotalTributosPorNrsSeqEImposto(relatorioMovimentacaoFornecedorVO
					.getNrsSeq());
		}
		if (relatorioMovimentacaoFornecedorVO.getDesconto() != null) {
			campo2 = relatorioMovimentacaoFornecedorVO.getDesconto();
		}
		if (relatorioMovimentacaoFornecedorVO.getVlrAcresc() != null) {
			campo3 = relatorioMovimentacaoFornecedorVO.getVlrAcresc();
		}
		if (valorConsultaC2 != null) {
			campo4 = valorConsultaC2;
		}

		calculoS1 = campo1 - campo2 + campo3 - campo4;

		if (relatorioMovimentacaoFornecedorVO.getNrsSeq() != null
				&& relatorioMovimentacaoFornecedorVO.getTitulo() != null) {
			valorConsultaC3 = obterTotalTributosPorNrsSeqETitulo(
					relatorioMovimentacaoFornecedorVO.getNrsSeq(),
					relatorioMovimentacaoFornecedorVO.getTitulo());
		}
		if (valorConsultaC3 != null) {
			campo5 = valorConsultaC3;
		}
		calculoS2 = campo1 - campo2 + campo3 - campo5;
		return calculoS1 + calculoS2;
	}

	// RN3 Tributos
	private Double calcularValorLiquido(
			RelatorioMovimentacaoFornecedorVO relatorioMovimentacaoFornecedorVO)
			throws ApplicationBusinessException {
		Double campo1 = Double.valueOf("0");
		Double campo2 = Double.valueOf("0");
		Double campo3 = Double.valueOf("0");
		Double campo4 = Double.valueOf("0");

		if (relatorioMovimentacaoFornecedorVO.getValorTitulo() != null) {
			campo1 = relatorioMovimentacaoFornecedorVO.getValorTitulo();
		}
		if (relatorioMovimentacaoFornecedorVO.getVlrAcresc() != null) {
			campo2 = relatorioMovimentacaoFornecedorVO.getVlrAcresc();
		}
		if (relatorioMovimentacaoFornecedorVO.getDesconto() != null) {
			campo3 = relatorioMovimentacaoFornecedorVO.getDesconto();
		}
		if (relatorioMovimentacaoFornecedorVO.getValorDf() != null) {
			campo4 = relatorioMovimentacaoFornecedorVO.getValorDf();
		}
		return campo1 + campo2 - campo3 - campo4;
	}

	/**
	 * Método reponsável por retornar a coleção com dados do relatório
	 * 
	 * @param fornecedor
	 *            dados referente ao fornecedor
	 * @return coleção com os dados do relatório
	 * @throws ApplicationBusinessException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public List<RelatorioMovimentacaoFornecedorVO> pesquisaMovimentacaoFornecedor(
			Object vFornecedor, Date dataInicio, Date dataFim)
			throws ApplicationBusinessException, IllegalAccessException,
			InvocationTargetException {
		VScoFornecedor vScoFornecedor = (VScoFornecedor) vFornecedor;
		ScoFornecedor fornecedor = this.scoFornecedorDao
				.obterFornecedorPorNumero(vScoFornecedor.getNumero());

		List<RelatorioMovimentacaoFornecedorVO> colecao = fcpTituloDAO
				.listarMovimentacaoFornecedor(fornecedor, dataInicio, dataFim);

		List<RelatorioMovimentacaoFornecedorVO> retorno = new ArrayList<RelatorioMovimentacaoFornecedorVO>();

		for (RelatorioMovimentacaoFornecedorVO vo : colecao) {
			RelatorioMovimentacaoFornecedorVO relatorioMovimentacaoFornecedorVO = new RelatorioMovimentacaoFornecedorVO();

			BeanUtils.copyProperties(relatorioMovimentacaoFornecedorVO, vo);

			// RN1
			relatorioMovimentacaoFornecedorVO.setTributos(calculaValorTributos(
					relatorioMovimentacaoFornecedorVO.getNrsSeq(),
					relatorioMovimentacaoFornecedorVO.getTitulo()));

			// RN2
			relatorioMovimentacaoFornecedorVO
					.setValorPagamento(calcularValorPagamento(relatorioMovimentacaoFornecedorVO));

			// RN3
			relatorioMovimentacaoFornecedorVO
					.setValorLiquido(calcularValorLiquido(relatorioMovimentacaoFornecedorVO));

			if (relatorioMovimentacaoFornecedorVO.getCpf() != null
					&& relatorioMovimentacaoFornecedorVO.getCgc() != null) {
				relatorioMovimentacaoFornecedorVO
						.setCpfCnpj(relatorioMovimentacaoFornecedorVO
								.getCgc()
								.toString()
								.concat(relatorioMovimentacaoFornecedorVO
										.getCpf().toString()));
			}

			if (relatorioMovimentacaoFornecedorVO.getNfNumero() != null
					&& relatorioMovimentacaoFornecedorVO.getNfSerie() != null) {
				relatorioMovimentacaoFornecedorVO
						.setNf(relatorioMovimentacaoFornecedorVO
								.getNfNumero()
								.toString()
								.concat("/")
								.concat(relatorioMovimentacaoFornecedorVO
										.getNfSerie()));
			}

			if (relatorioMovimentacaoFornecedorVO.getEstornado() != null
					&& ("S").equals(relatorioMovimentacaoFornecedorVO
							.getEstornado())) {
				relatorioMovimentacaoFornecedorVO.setEstornado("Estornado");
			} else {
				relatorioMovimentacaoFornecedorVO.setEstornado("");
			}

			if (relatorioMovimentacaoFornecedorVO.getDtPagto() != null) {
				relatorioMovimentacaoFornecedorVO.setDtPagtoFormatada(DateUtil
						.obterDataFormatada(
								relatorioMovimentacaoFornecedorVO.getDtPagto(),
								DateConstants.DATE_PATTERN_DDMMYYYY));
			}

			if (relatorioMovimentacaoFornecedorVO.getDtEmissao() != null) {
				relatorioMovimentacaoFornecedorVO
						.setDtEmissaoFormatada(DateUtil.obterDataFormatada(
								relatorioMovimentacaoFornecedorVO
										.getDtEmissao(),
								DateConstants.DATE_PATTERN_DDMMYYYY));
			}

			if (relatorioMovimentacaoFornecedorVO.getDtVencto() != null) {
				relatorioMovimentacaoFornecedorVO
						.setDtVenctoFormatada(DateUtil.obterDataFormatada(
								relatorioMovimentacaoFornecedorVO.getDtVencto(),
								DateConstants.DATE_PATTERN_DDMMYYYY));
			}

			retorno.add(relatorioMovimentacaoFornecedorVO);
		}

		return retorno;
	}

	/**
	 * Método responsável por retornar objeto com as informações do hospital
	 * 
	 * @param parametro
	 * @return
	 */
	public AghParametros pesquisarHospital(Object parametro) {
		return aghParametrosDAO.pesquisaHospital(parametro);
	}

	public void enviarEmailMovimentacaoFornecedor(String contatoEmail,
			byte[] jasper) throws ApplicationBusinessException {
		AghParametros paramEmailSetorFinanceiro = parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_E_MAIL_SETOR_FINANCEIRO);
		AghParametros paramEmailMovForn = parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_E_MAIL_MOVIMENTACAO_FORNECEDOR);
		AghParametros paramSetorFinanceiro = parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_SETOR_FINANCEIRO);

		String remetente = paramEmailSetorFinanceiro.getVlrTexto();
		String assunto = paramEmailMovForn.getVlrTexto();
		String conteudo = super.getResourceBundleValue("CONTEUDO_EMAIL_MOVIMENTACAO_FORNECEDOR");
		String email = MessageFormat.format(conteudo, paramSetorFinanceiro.getVlrTexto());

		AnexoEmail anexoEmail = new AnexoEmail(assunto,jasper,"application/pdf");
		emailUtil.enviaEmail(remetente, contatoEmail, null, assunto, email, anexoEmail);
	}

	public AghParametrosDAO getAghParametrosDAO() {
		return aghParametrosDAO;
	}

	public void setAghParametrosDAO(AghParametrosDAO aghParametrosDAO) {
		this.aghParametrosDAO = aghParametrosDAO;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

}
