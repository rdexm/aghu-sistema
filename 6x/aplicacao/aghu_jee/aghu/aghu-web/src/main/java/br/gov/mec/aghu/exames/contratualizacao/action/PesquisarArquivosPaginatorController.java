package br.gov.mec.aghu.exames.contratualizacao.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.exames.contratualizacao.business.IContratualizacaoFacade;
import br.gov.mec.aghu.model.AelArquivoIntegracao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class PesquisarArquivosPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 7125574343156680508L;
	
	@EJB
	private IContratualizacaoFacade contratualizacaoFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AelArquivoIntegracao> dataModel;
		
	//Campos filtro
	private Date dataInicial;
	private Date dataFinal;
	private String nomeArquivo;
	private boolean solicitacaoComErro;
	private boolean solicitacaoSemAgenda;
	private Integer seq;

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}	
	
	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		try {
			if ((this.dataInicial == null || this.dataFinal == null) && StringUtils.isEmpty(this.nomeArquivo)) {
				throw new ApplicationBusinessException("MENSAGEM_FILTRO_OBRIGATORIO", Severity.ERROR);
			}
			dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
			
	}
	
	/**
	 * Método utilizado para redirecionar para a tela de pesquisa de solicitações
	 * @param seq
	 * @return
	 */
	public String pesquisarSolicitacao(Integer seq){
		this.setSeq(seq);
		return "exames-pesquisarSolicitacao";
	}
	
	
	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		//Limpa filtro	
		dataInicial = null;
		dataFinal = null;
		nomeArquivo = null;
		solicitacaoComErro = false;
		solicitacaoSemAgenda = false;
		dataModel.limparPesquisa();
	}
	
//	private boolean validarDatas() {
//		
//		if ((this.dataInicial != null && this.dataFinal == null)
//				|| (this.dataInicial == null && this.dataFinal != null)) {
//			this.apresentarMsgNegocio(Severity.ERROR,	"MENSAGEM_DATA_INICIAL_FINAL_OBRIGATORIO");
//			return false;
//		}		
//		
//		if (this.dataInicial != null && this.dataFinal != null) {
//			this.dataInicial = DateUtil
//					.obterDataComHoraInical(this.dataInicial);
//			this.dataFinal = DateUtil.obterDataComHoraFinal(this.dataFinal);
//
//			if (dataFinal.before(dataInicial) || dataFinal.equals(dataInicial)) {
//				this.apresentarMsgNegocio(Severity.ERROR,
//						"MENSAGEM_DATA_RECEBIMENTO_FINAL_MAIOR_DATA_INICIAL");
//				return false;
//			}
//		}
//		
//		return true;
//	}
	
	@Override
	public List<AelArquivoIntegracao> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return this.contratualizacaoFacade.pesquisarArquivosIntegracao(
				firstResult, maxResult, orderProperty, asc, dataInicial,
				dataFinal, nomeArquivo, solicitacaoComErro,
				solicitacaoSemAgenda);
	}
	
	@Override
	public Long recuperarCount() {
		return this.contratualizacaoFacade.pesquisarArquivosIntegracaoCount(
				dataInicial, dataFinal, nomeArquivo, solicitacaoComErro,
				solicitacaoSemAgenda);
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public boolean isSolicitacaoComErro() {
		return solicitacaoComErro;
	}

	public void setSolicitacaoComErro(boolean solicitacaoComErro) {
		this.solicitacaoComErro = solicitacaoComErro;
	}

	public boolean isSolicitacaoSemAgenda() {
		return solicitacaoSemAgenda;
	}

	public void setSolicitacaoSemAgenda(boolean solicitacaoSemAgenda) {
		this.solicitacaoSemAgenda = solicitacaoSemAgenda;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
 	public DynamicDataModel<AelArquivoIntegracao> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelArquivoIntegracao> dataModel) {
	 this.dataModel = dataModel;
	}
}