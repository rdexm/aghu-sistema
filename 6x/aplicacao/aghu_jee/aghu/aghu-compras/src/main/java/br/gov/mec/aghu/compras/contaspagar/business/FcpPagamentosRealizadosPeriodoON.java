package br.gov.mec.aghu.compras.contaspagar.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.compras.contaspagar.vo.PagamentosRealizadosPeriodoPdfSubDocsVO;
import br.gov.mec.aghu.compras.contaspagar.vo.PagamentosRealizadosPeriodoPdfSubLicitacoesVO;
import br.gov.mec.aghu.compras.contaspagar.vo.PagamentosRealizadosPeriodoPdfVO;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class FcpPagamentosRealizadosPeriodoON extends BaseBusiness{

	private static final long serialVersionUID = 8873956744097578208L;
	
	/** Injeção do FcpPagamentosRealizadosPeriodoRN */
	@Inject
	private FcpPagamentosRealizadosPeriodoRN pagamentosRealizadosPeriodoRN;
	
	/**
	 * Método responsável por retornar a coleção de dados para gerar o relatório em PDF
	 * @param inicioPeriodo
	 * @param finalPeriodo
	 * @param codVerbaGestao
	 * @return coleção de dados do relatório
	 * @throws ApplicationBusinessException
	 */
	public List<PagamentosRealizadosPeriodoPdfVO> pesquisarPagamentosRealizadosPeriodoPDF(
			Date inicioPeriodo, Date finalPeriodo, Integer codVerbaGestao) throws ApplicationBusinessException {
		
		List<PagamentosRealizadosPeriodoPdfVO> colecao = null;
		
		if (isFiltroValido(inicioPeriodo, finalPeriodo)) {
			if (isPeridoValido(inicioPeriodo, finalPeriodo)) {
				Date dtInicio = DateUtil.truncaData(inicioPeriodo);
				Date dtFinal = DateUtil.obterDataComHoraFinal(finalPeriodo);
				colecao = this.getFcpPagamentosRealizadosPeriodoRN().pesquisarPagamentosRealizadosPeriodoPDF(dtInicio, dtFinal, codVerbaGestao);
				
				if (colecao == null || colecao.isEmpty()) {
					throw new ApplicationBusinessException("MENSAGEM_REGRA_DADOS_NAO_ENCONTADO", Severity.ERROR, colecao);
				}
				colecao = preencherSubRelatorios(colecao);
			}
		} else {
			throw new ApplicationBusinessException("MENSAGEM_FILTRO_OBRIGATORIO_NAO_PREENCHIDO", Severity.ERROR, colecao);
		}
		
		return colecao;
	}
	
	private List<PagamentosRealizadosPeriodoPdfVO> preencherSubRelatorios(List<PagamentosRealizadosPeriodoPdfVO> colecao) {
		String cgcCpf = null;
		Date dtPagamento = null;
		List<PagamentosRealizadosPeriodoPdfSubDocsVO> listDocs = new ArrayList<PagamentosRealizadosPeriodoPdfSubDocsVO>();
		List<PagamentosRealizadosPeriodoPdfSubLicitacoesVO> listLicitacoes = new ArrayList<PagamentosRealizadosPeriodoPdfSubLicitacoesVO>();
		PagamentosRealizadosPeriodoPdfVO itemAnterior = null;
		// Controle do último registro
		PagamentosRealizadosPeriodoPdfVO ultimoRegistro = null;
		
		List<PagamentosRealizadosPeriodoPdfVO> listaRetorno = new ArrayList<PagamentosRealizadosPeriodoPdfVO>();
		
		for (PagamentosRealizadosPeriodoPdfVO item : colecao) {
			ultimoRegistro = item;
			if (cgcCpf == null) {
				cgcCpf = item.getCgcCpfFornecedor();
				dtPagamento = item.getDtPagamento();
				itemAnterior = item;
			}
			if (cgcCpf.equals(item.getCgcCpfFornecedor()) && dtPagamento.equals(item.getDtPagamento())) {
				PagamentosRealizadosPeriodoPdfSubDocsVO doc = montaVODocs(item);
				listDocs.add(doc);
				
				PagamentosRealizadosPeriodoPdfSubLicitacoesVO lic = montaVOLicitacoes(item);
				listLicitacoes.add(lic);
				
			} else {
				cgcCpf = item.getCgcCpfFornecedor();
				dtPagamento = item.getDtPagamento();
				itemAnterior.setSubRelatorioDocs(listDocs);
				itemAnterior.setSubRelatorioLicitacoes(listLicitacoes);
				// Adiciona na lista final
				listaRetorno.add(itemAnterior);
				// Reseta
				itemAnterior = item;
				listDocs = new ArrayList<PagamentosRealizadosPeriodoPdfSubDocsVO>();
				listLicitacoes = new ArrayList<PagamentosRealizadosPeriodoPdfSubLicitacoesVO>();
				
				PagamentosRealizadosPeriodoPdfSubDocsVO doc = montaVODocs(item);
				listDocs.add(doc);
				
				PagamentosRealizadosPeriodoPdfSubLicitacoesVO lic = montaVOLicitacoes(item);
				listLicitacoes.add(lic);
			}
		}
		// Após percorrer toda a lista, adiciona o último registro na lista final.
		ultimoRegistro.setSubRelatorioDocs(listDocs);
		ultimoRegistro.setSubRelatorioLicitacoes(listLicitacoes);
		listaRetorno.add(ultimoRegistro);
		
		return listaRetorno;
	}
	
	private PagamentosRealizadosPeriodoPdfSubDocsVO montaVODocs(PagamentosRealizadosPeriodoPdfVO item) {
		PagamentosRealizadosPeriodoPdfSubDocsVO doc = new PagamentosRealizadosPeriodoPdfSubDocsVO();
		doc.setDoc(item.getDoc());
		doc.setNumero(item.getNumero());
		doc.setTitulo(item.getTitulo());
		doc.setNr(item.getNr());
		doc.setValorTitulo(item.getValorTitulo());
		doc.setDesconto(item.getDesconto());
		doc.setTributos(item.getTributos());
		doc.setVlrDft(item.getVlrDft());
		doc.setLicitacao(item.getLicitacao());
		doc.setValorPagamento(item.getValorPagamento());
		
		return doc;
	}
	
	private PagamentosRealizadosPeriodoPdfSubLicitacoesVO montaVOLicitacoes(PagamentosRealizadosPeriodoPdfVO item) {
		PagamentosRealizadosPeriodoPdfSubLicitacoesVO lic = new PagamentosRealizadosPeriodoPdfSubLicitacoesVO();
		lic.setLicitacao(item.getLicitacao());
		lic.setComplemento(item.getComplemento());
		lic.setCodVerba(item.getCodVerba());
		lic.setDescVerba(item.getDescVerba());
		lic.setCodGrupoNatureza(item.getCodGrupoNatureza());
		lic.setCodNatureza(item.getCodNatureza());
		lic.setNtdDescricao(item.getNtdDescricao());
		
		return lic;
	}
	
	/**
	 * Válida de se os filtros obrigátorios foram selecionadas
	 * @param inicioPeriodo
	 * @param finalPeriodo
	 */
	private boolean isFiltroValido(Date inicioPeriodo, Date finalPeriodo) {
		if (inicioPeriodo == null || finalPeriodo == null) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Válida se o período selecionado é válido
	 * @param dataInicial
	 * @param dataFinal
	 */
	private boolean isPeridoValido(Date dataInicial, Date dataFinal) throws ApplicationBusinessException {
		if (!DateUtil.validaDataMenorIgual(dataInicial, dataFinal)) 
		{
			throw new ApplicationBusinessException("MENSAGEM_DATA_INICIAL_MAIOR_DATA_FINAL_PAGAMENTOS_REALIZADOS", Severity.ERROR, DateUtil.obterDataFormatada(dataInicial, "dd/MM/yyyy"), DateUtil.obterDataFormatada(dataFinal, "dd/MM/yyyy") );
		}
		if(!DateUtil.validaDataMenorIgual(dataInicial, new Date()) || !DateUtil.validaDataMenorIgual(dataFinal, new Date())) {
			throw new ApplicationBusinessException("MENSAGEM_DATA_INICIAL_MAIOR_DATA_ATUAL", Severity.ERROR);		
		}
		return true;
	}
	
	/**
	 * Método para retorno de verbas de gestão
	 * @param paramPesquisa
	 * @return coleçãoo de verbas gestão
	 */
	public List<FsoVerbaGestao> pesquisarVerbaGestaoPorSeqOuDescricao(Object paramPesquisa) {
		return this.getFcpPagamentosRealizadosPeriodoRN().pesquisarVerbaGestaoPorSeqOuDescricao(paramPesquisa);
	}
	
	public FcpPagamentosRealizadosPeriodoRN getFcpPagamentosRealizadosPeriodoRN() {
		return pagamentosRealizadosPeriodoRN;
	}

	@Override
	protected Log getLogger() {
		return null;
	}
}
