package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoParamAutorizacaoSc;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class ParamAutorizacaoScController extends ActionController {

	private static final Log LOG = LogFactory.getLog(ParamAutorizacaoScController.class);

	private static final long serialVersionUID = 1655092998556857302L;

	private static final String PARAM_AUTORIZACAO_SC_LIST = "paramAutorizacaoScList";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private ScoParamAutorizacaoSc paramAutorizacao;
	private Integer seq;
	private Boolean indHierarquiaCCusto;
	private Boolean indAtivo;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
		if (this.getSeq() == null) {
			this.setParamAutorizacao(new ScoParamAutorizacaoSc());
			this.getParamAutorizacao().setIndSituacao(DominioSituacao.A);
			this.getParamAutorizacao().setIndHierarquiaCCusto(false);
			this.setIndHierarquiaCCusto(false); 
			
		} else {
			paramAutorizacao = comprasCadastrosBasicosFacade.obterParamAutorizacaoSc(this.seq);

			if(paramAutorizacao == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			if (this.getParamAutorizacao().getIndSituacao() == DominioSituacao.A) {
				this.setIndAtivo(true);
			}
			else {
				this.setIndAtivo(false);
			}
			//Verifica o campo indHierarquiaCCusto para converter de Booleano para DominioSimNao
			if (this.getParamAutorizacao().getIndHierarquiaCCusto() != null) {
				this.setIndHierarquiaCCusto(this.getParamAutorizacao().getIndHierarquiaCCusto());
			}
		}
		
		return null;
	
	}

	public String gravar() {
		try {
			
			if (this.getParamAutorizacao() != null) {
				
				if (this.indAtivo) {
					this.getParamAutorizacao().setIndSituacao(DominioSituacao.A);
				}
				else {
					this.getParamAutorizacao().setIndSituacao(DominioSituacao.I);
				}
				final boolean novo = this.getParamAutorizacao().getSeq() == null;
				
				//Verifica o campo indHierarquiaCCusto para converter de DominioSimNao para Booleano
				if (this.indHierarquiaCCusto != null) {
					this.getParamAutorizacao().setIndHierarquiaCCusto(indHierarquiaCCusto);
				}
				
				if (novo) {
					this.comprasCadastrosBasicosFacade.inserirParamAutorizacaoSc(this.getParamAutorizacao());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PARAM_AUTORIZACAO_SC_INSERT_SUCESSO");
				} else {
					this.comprasCadastrosBasicosFacade.alterarParamAutorizacaoSc(this.getParamAutorizacao());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PARAM_AUTORIZACAO_SC_UPDATE_SUCESSO");
				}

				return cancelar();
			}
		} catch (final BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
			
		} catch (final BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public String cancelar() {
		seq = null;
		paramAutorizacao = null;
		return PARAM_AUTORIZACAO_SC_LIST;
	}

	// Métodos para carregar suggestions
	//Suggestion Centro Custo Solicitante e Centro Custo Aplicação
	public List<FccCentroCustos> pesquisarCentroCustoAtivos(String parametro) throws BaseException {
		return centroCustoFacade.pesquisarCentroCustosAtivosOrdemDescricao(parametro);
	}
	
	//Suggestion Servidor Solicitante e Servidor Autorizador
	public List<RapServidores> pesquisarServidorVinculados(String objPesquisa) {
		try {
			return this.registroColaboradorFacade.pesquisarServidoresVinculados(objPesquisa);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<RapServidores>();
	}

	//Suggestion Ponto Parada Origem e Ponto Parada Destino
	public List<ScoPontoParadaSolicitacao> pesquisarOrigDestParadaAtivos(String pontoParadaSolic) {
		return this.comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoPorCodigoOuDescricaoAtivos((String)pontoParadaSolic);
	}
	
	//Suggestion Servidor Comprador
	public List<RapServidores> pesquisarCompradorAtivoPorMatriculaNome(String parametro) {
		return this.registroColaboradorFacade.pesquisarServidoresCompradorAtivoPorMatriculaNome(parametro);
	}

	// gets and sets
	public ScoParamAutorizacaoSc getParamAutorizacao() {
		return paramAutorizacao;
	}

	public void setParamAutorizacao(ScoParamAutorizacaoSc paramAutorizacao) {
		this.paramAutorizacao = paramAutorizacao;
	}
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	
	public Boolean getIndHierarquiaCCusto() {
		return indHierarquiaCCusto;
	}

	public void setIndHierarquiaCCusto(Boolean indHierarquiaCCusto) {
		this.indHierarquiaCCusto = indHierarquiaCCusto;
	}

	public Boolean getIndAtivo() {
		return indAtivo;
	}

	public void setIndAtivo(Boolean indAtivo) {
		this.indAtivo = indAtivo;
	}
}