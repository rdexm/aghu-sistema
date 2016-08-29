package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.vo.ExcluirProgramacaoEntregaItemAFVO;
import br.gov.mec.aghu.dominio.DominioTipoExclusaoProgramacaoAf;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class ExcluirProgramacaoEntregaItensAfController extends ActionController {

	
	private static final String PESQUISAR_PLANJ_PROG_ENTREGA_ITENS_AF = "pesquisarPlanjProgEntregaItensAF";

	private static final long serialVersionUID = -3672912971943538394L;
	
	public enum ExcluirProgramacaoEntregaItensAfControllerExceptionCode implements BusinessExceptionCode {
		ALERTA_SEM_REGISTRO_SELECIONADO_AF_PROGRAMACAO, ALERTA_ITEM_SEM_PROGRAMACAO, MSG_M1_EXCLUIR_PROG;;
	}

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	private Integer numeroAF;
	private Short complemento;
	private String razaoSocial;
	private Long cgc;
	private DominioTipoExclusaoProgramacaoAf[] tipoExclusaoLabels = DominioTipoExclusaoProgramacaoAf.values();
	private DominioTipoExclusaoProgramacaoAf tipoExclusao = null;
	private List<ExcluirProgramacaoEntregaItemAFVO> listaItensProgramacao = null;
	private Boolean exclusaoItens = false;
	private Integer numeroLicitacao;
	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	public void setListaItensProgramacao(List<ExcluirProgramacaoEntregaItemAFVO> listaItensProgramacao) {
		this.listaItensProgramacao = listaItensProgramacao;
	}

	public List<ExcluirProgramacaoEntregaItemAFVO> getListaItensProgramacao() {
		return listaItensProgramacao;
	}

	public Integer getNumeroAF() {
		return numeroAF;
	}
	
	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}
	
	public Short getComplemento() {
		return complemento;
	}
	
	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}
	
	public String getRazaoSocial() {
		return razaoSocial;
	}
	
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	
	public Long getCgc() {
		return cgc;
	}
	
	public void setCgc(Long cgc) {
		this.cgc = cgc;
	}
	
	public void pesquisar() {
	 

	 

		setListaItensProgramacao(null);
		exclusaoItens = false;
		if(DominioTipoExclusaoProgramacaoAf.ITEM.equals(tipoExclusao)) {
			exclusaoItens = true;
			setListaItensProgramacao(autFornecimentoFacade.pesquisarListaProgrEntregaItensAfExclusao(numeroAF));
		}
	
	}
	
	
	public void processar() {
		
		try {
			if(tipoExclusao==null){
				throw new ApplicationBusinessException(ExcluirProgramacaoEntregaItensAfControllerExceptionCode.MSG_M1_EXCLUIR_PROG);
			}
			if(DominioTipoExclusaoProgramacaoAf.ITEM.equals(tipoExclusao) ) {
				if(listaItensProgramacao==null || listaItensProgramacao.isEmpty()){
					throw new ApplicationBusinessException(ExcluirProgramacaoEntregaItensAfControllerExceptionCode.ALERTA_ITEM_SEM_PROGRAMACAO);
				}
				List<ExcluirProgramacaoEntregaItemAFVO> listaItensProgramacaoExclusao = buscaSomenteItensSelecionados();
				if(listaItensProgramacaoExclusao == null || listaItensProgramacaoExclusao.isEmpty()) {
					throw new ApplicationBusinessException(ExcluirProgramacaoEntregaItensAfControllerExceptionCode.ALERTA_SEM_REGISTRO_SELECIONADO_AF_PROGRAMACAO);
				}
				autFornecimentoFacade.excluirListaProgrEntregaItensAf(numeroAF, listaItensProgramacaoExclusao);
			} else {
				autFornecimentoFacade.excluirProgrEntregaItensAf(numeroAF);
			}
			apresentarMsgNegocio(Severity.INFO, "PROGRAMACAO_EXCLUIDA_COM_SUCESSO");
			this.pesquisar();
		} catch (RuntimeException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_EXCLUSAO_PENDENCIAS_PROGRAMACAO_ITEM");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private List<ExcluirProgramacaoEntregaItemAFVO> buscaSomenteItensSelecionados() {
		List<ExcluirProgramacaoEntregaItemAFVO> listaItensProgramacaoExclusao = new ArrayList<ExcluirProgramacaoEntregaItemAFVO>() ;
		for(ExcluirProgramacaoEntregaItemAFVO vo : listaItensProgramacao) {
			if(Boolean.TRUE.equals(vo.getSelecionado())) {
				listaItensProgramacaoExclusao.add(vo);
			}
		}	
		return listaItensProgramacaoExclusao;
	}

	public void setTipoExclusao(DominioTipoExclusaoProgramacaoAf tipoExclusao) {
		this.tipoExclusao = tipoExclusao;
	}

	public DominioTipoExclusaoProgramacaoAf getTipoExclusao() {
		return tipoExclusao;
	}
	
	public DominioTipoExclusaoProgramacaoAf[] getTipoExclusaoLabels() {
		return this.tipoExclusaoLabels;
	}
	
	public String voltar() {
		listaItensProgramacao = null;
		tipoExclusao = null;
		return PESQUISAR_PLANJ_PROG_ENTREGA_ITENS_AF;
	}

	public void setExclusaoItens(Boolean exclusaoItens) {
		this.exclusaoItens = exclusaoItens;
	}

	public Boolean getExclusaoItens() {
		return exclusaoItens;
	}
	
	public Integer getNumeroLicitacao() {
		return numeroLicitacao;
	}
	public void setNumeroLicitacao(Integer numeroLicitacao) {
		this.numeroLicitacao = numeroLicitacao;
	}
}
