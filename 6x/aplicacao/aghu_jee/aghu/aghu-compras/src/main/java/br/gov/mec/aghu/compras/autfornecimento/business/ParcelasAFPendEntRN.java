package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoContatoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.dao.ScoProgrAcessoFornAfpDAO;
import br.gov.mec.aghu.compras.vo.FiltroParcelasAFPendEntVO;
import br.gov.mec.aghu.compras.vo.ParcelasAFPendEntVO;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class ParcelasAFPendEntRN extends BaseBusiness {

	/**
	 * 
	 */
	
	
	private static final Log LOG = LogFactory.getLog(ParcelasAFPendEntRN.class);
	
	@Inject
	private ScoContatoFornecedorDAO scoContatoFornecedorDAO;
	
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	
	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;
	
	@Inject
	private ScoProgrAcessoFornAfpDAO scoProgrAcessoFornAfpDAO;
	
	private static final long serialVersionUID = 153237391533209871L;

	private ScoContatoFornecedorDAO getContatoFornecedorDAO() {
		return scoContatoFornecedorDAO;
	}

   private ScoProgrAcessoFornAfpDAO getScoProgrAcessoFornAfpDAO() {
		return scoProgrAcessoFornAfpDAO;
	}

	private ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {
		return scoAutorizacaoFornDAO;
	}

	private ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() {
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}

	public List<ParcelasAFPendEntVO> listarParcelasAFsPendentes(
			FiltroParcelasAFPendEntVO filtro, Integer firstResult, Integer maxResult,
			String order, boolean asc) {
		// c1
		List<ParcelasAFPendEntVO> resultado = getScoAutorizacaoFornDAO().listarParcelasAFsPendentes(filtro, firstResult, maxResult, order, asc);
		Integer indice = 0;
		for (ParcelasAFPendEntVO parcelasAFPendEntVO : resultado) {
			parcelasAFPendEntVO.setIndiceLista(indice);
			indice++;
			obterAcessoPortal(parcelasAFPendEntVO);
			montarMascaraFornecedor(parcelasAFPendEntVO);
			obterLicitacao(parcelasAFPendEntVO);
			obterModalidade(parcelasAFPendEntVO);
			obterPublicacaoAFP(parcelasAFPendEntVO);
			obterEnviado(parcelasAFPendEntVO);
			parcelasAFPendEntVO.setMaterial(filtro.getMaterial());
			parcelasAFPendEntVO.setGrupoMaterial(filtro.getGrupoMaterial());

			Integer numeroAFN = parcelasAFPendEntVO.getAfeAfn();
			Integer numeroAFP = parcelasAFPendEntVO.getNumeroAFP();

			parcelasAFPendEntVO
					.setAtrasado(getScoProgEntregaItemAutorizacaoFornecimentoDAO()
							.verificarAtraso(numeroAFN, numeroAFP));
			Hibernate.initialize(parcelasAFPendEntVO.getPessoaGestor());
			//1 - azul
			Long entregaNConfirmada = getScoProgEntregaItemAutorizacaoFornecimentoDAO()
					.verificarEntregaNaoConfirmada(numeroAFN, numeroAFP);
			if (entregaNConfirmada > 0) {
				setRecebimentoNaoConfirmado(parcelasAFPendEntVO);
				continue;
			}
			//2 - Laranja
			Long entregaLiberadaMenor = getScoProgEntregaItemAutorizacaoFornecimentoDAO()
					.verificarEntregaLiberadaEfetMenor(numeroAFN, numeroAFP);
			if (entregaLiberadaMenor > 0) {
				setRecebimentoParcial(parcelasAFPendEntVO);
				continue;
			}
			// 3 - vermelho
			Long parcelaVencida = getScoProgEntregaItemAutorizacaoFornecimentoDAO()
					.verificarParcelaVencida(numeroAFN, numeroAFP);
			if (parcelaVencida > 0) {
				setParcelaVencida(parcelasAFPendEntVO);
				continue;
			}
			//4 - verde
			Long entregaLiberada = getScoProgEntregaItemAutorizacaoFornecimentoDAO()
					.verificarEntregaLiberada(numeroAFN, numeroAFP);
			if (entregaLiberada > 0) {
				setEntregaLiberada(parcelasAFPendEntVO);
				continue;
			}
		}
		return resultado;
	}

	private void obterAcessoPortal(ParcelasAFPendEntVO parcelasAFPendEntVO) {
		Date data = getScoProgrAcessoFornAfpDAO()
				.obterUltimoAcessoFornecedorPortal(
						parcelasAFPendEntVO.getNumeroAFP(),
						parcelasAFPendEntVO.getNumeroAFN());

		if (data == null) {
			parcelasAFPendEntVO.setPortalForn("Não");
		} else {
			String dataStr = DateUtil.obterDataFormatada(data, "dd/MM/yyyy");
			parcelasAFPendEntVO.setPortalForn(dataStr);
		}
	}

	private void setEntregaLiberada(ParcelasAFPendEntVO parcelasAFPendEntVO) {
		parcelasAFPendEntVO.setCorSituacaoEntrega("#00FF00");// verde
		parcelasAFPendEntVO.setHintSituacaoEntrega("Entrega Liberada");
		parcelasAFPendEntVO.setSituacaoEntrega("L");
	}

	private void setParcelaVencida(ParcelasAFPendEntVO parcelasAFPendEntVO) {
		parcelasAFPendEntVO.setCorSituacaoEntrega("#FF0000");// vermelho
		parcelasAFPendEntVO.setHintSituacaoEntrega("Parcela Vencida");
		parcelasAFPendEntVO.setSituacaoEntrega("V");
	}

	private void setRecebimentoParcial(ParcelasAFPendEntVO parcelasAFPendEntVO) {
		parcelasAFPendEntVO.setCorSituacaoEntrega("#FFA500");// laranja
		parcelasAFPendEntVO
				.setHintSituacaoEntrega("Recebimento Parcial de parcela atrasada");
		parcelasAFPendEntVO.setSituacaoEntrega("PA");
	}

	private void setRecebimentoNaoConfirmado(
			ParcelasAFPendEntVO parcelasAFPendEntVO) {
		parcelasAFPendEntVO.setCorSituacaoEntrega("#0000FF");//azul
		parcelasAFPendEntVO
				.setHintSituacaoEntrega("Recebimento ainda não confirmado");
		parcelasAFPendEntVO.setSituacaoEntrega("R");
	}

	private void obterEnviado(ParcelasAFPendEntVO parcelasAFPendEntVO) {
		Date envioFornecedor = parcelasAFPendEntVO.getDtEnvioForn();
		if (envioFornecedor == null) {
			parcelasAFPendEntVO.setEnviado("-");
		} else {
			parcelasAFPendEntVO.setEnviado(null);
		}
	}

	private void obterPublicacaoAFP(ParcelasAFPendEntVO parcelasAFPendEntVO) {
		Date dtPublicacao = parcelasAFPendEntVO.getDtPublicacao();
		if (dtPublicacao == null) {
			parcelasAFPendEntVO
					.setPublicacaoAFP("Não");
		} else {
			parcelasAFPendEntVO
					.setPublicacaoAFP("Sim");
		}
	}

	private void obterModalidade(ParcelasAFPendEntVO parcelasAFPendEntVO) {
		DominioModalidadeEmpenho modalidadeEmpenho = parcelasAFPendEntVO
				.getModalidadeEmpenho();
		if (modalidadeEmpenho == null) {
			parcelasAFPendEntVO.setDescricaoModalidadeEmp("");
		} else {
			parcelasAFPendEntVO.setDescricaoModalidadeEmp(modalidadeEmpenho
					.getDescricao());
		}
	}

	private void obterLicitacao(ParcelasAFPendEntVO parcelasAFPendEntVO) {
		ScoLicitacao licitacao = parcelasAFPendEntVO.getLicitacao();
		if (licitacao == null || licitacao.getNumEdital() == null
				|| licitacao.getAnoComplemento() == null) {
			parcelasAFPendEntVO.setNumeroEditalAno("");
		} else {
			parcelasAFPendEntVO.setNumeroEditalAno(licitacao.getNumEdital()
					+ "/" + licitacao.getAnoComplemento());
		}
	}

	public void montarMascaraFornecedor(
			ParcelasAFPendEntVO parcelasAFPendEntVO) {
		ScoFornecedor fornecedor = parcelasAFPendEntVO.getFornecedor();
		if (fornecedor != null) {
			parcelasAFPendEntVO.setRazaoSocialNumeroFornecedor(fornecedor
					.getNumero() + " - " + fornecedor.getRazaoSocial());
		}
	}
	
	public ScoContatoFornecedor obterPrimeiroContatoPorFornecedor(ScoFornecedor fornecedor) {
		return getContatoFornecedorDAO().pesquisarContatoPorFornecedor(fornecedor.getNumero());
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
}
