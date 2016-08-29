package br.gov.mec.aghu.procedimentoterapeutico.cadastroapoio.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.model.MptProtocoloAssociacao;
import br.gov.mec.aghu.model.MptProtocoloSessao;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.protocolos.vo.ProtocoloAssociadoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastrarRelacionamentoProtocoloPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -520181730954081834L;
	
	private MptProtocoloSessao protocoloSelecionado;
	
	private List<MptProtocoloSessao> sugConsultado;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	private static final String PAGE_PESQUISAR_PROTOCOLO = "pesquisaProtocoloList.xhtml";
	
	private MptProtocoloAssociacao associacao;
	
	private MptProtocoloAssociacao associacao2;
	
	private Integer seqProtocolo;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	@Paginator
	private DynamicDataModel<ProtocoloAssociadoVO> dataModel;
	
	private Boolean impedirRelacionamento;
	
	private Boolean existeRelacionamento = false;
	
	private ProtocoloAssociadoVO itemExclusao;
	
	private String descricaoTitulo;
	
	public void iniciar(){
		this.dataModel.reiniciarPaginator();		
		this.associacao = new MptProtocoloAssociacao();
		this.associacao2 = new MptProtocoloAssociacao();
	}
	
	public void adicionarProtocolo(){
		if(protocoloSelecionado == null){
			apresentarMsgNegocio(Severity.INFO, "LABEL_MS01");
		}else{
			try {
				this.associacao.setProtocoloSessao(this.procedimentoTerapeuticoFacade.buscarProtocoloPesquisa(this.protocoloSelecionado.getSeq()));
				this.associacao2.setProtocoloSessao(this.procedimentoTerapeuticoFacade.buscarProtocoloPesquisa(this.seqProtocolo));
				
				List<MptProtocoloAssociacao> protocolosAssociados = this.procedimentoTerapeuticoFacade.buscarAssociacoes(this.seqProtocolo);
				
				for(MptProtocoloAssociacao item: protocolosAssociados){
					if(item.getProtocoloSessao().equals(this.seqProtocolo)){
						this.existeRelacionamento = true;
						break;
					}
				}
				if(!this.existeRelacionamento){
					if(protocolosAssociados.size() > 0){
						this.associacao.setAgrupador(protocolosAssociados.get(0).getAgrupador());
					}else{
						this.associacao.setAgrupador(this.procedimentoTerapeuticoFacade.gerarNovoAgrupador());
						this.associacao2.setAgrupador(this.procedimentoTerapeuticoFacade.gerarNovoAgrupador());
					}
					this.associacao.setCriadoEm(new Date());
					this.associacao2.setCriadoEm(new Date());
					this.associacao.setServidor(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
					this.associacao2.setServidor(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
					this.procedimentoTerapeuticoFacade.persistirRelacionamento(this.associacao);					
					if(protocolosAssociados.size() < 1){
						this.procedimentoTerapeuticoFacade.persistirRelacionamento(this.associacao2);
					}
				}else{
					apresentarMsgNegocio(Severity.INFO, "LABEL_MS02");
				}
			} catch (ApplicationBusinessException e) {				 
				apresentarExcecaoNegocio(e);
			}
			this.iniciar();
			this.protocoloSelecionado = null;
		}		
	}
	
	public List<MptProtocoloSessao> listarProtocolos(final String strPesquisa) {
//		Integer remocao = 0;
//		sugConsultado = this.procedimentoTerapeuticoFacade.listarProtocolos(strPesquisa, seqProtocolo);
//		if(sugConsultado != null){
//			for(MptProtocoloSessao item: sugConsultado){
//				if(item.getSeq().equals(seqProtocolo)){
//					sugConsultado.remove(item);
//					remocao = 1;
//					break;
//				}
//			}			
//		}
		return this.returnSGWithCount(this.procedimentoTerapeuticoFacade.listarProtocolos(strPesquisa, seqProtocolo),	this.procedimentoTerapeuticoFacade.listarProtocolosCount(strPesquisa, seqProtocolo));
	}
	
	public void preparaRetorno(){
		RequestContext.getCurrentInstance().execute("PF('modal_retornar').show()");		
	}
	
	public String voltar(){
		this.protocoloSelecionado = null;
		return PAGE_PESQUISAR_PROTOCOLO;
	}
	
	/**
	 * Prepara exclusão.
	 * @param itemExclusao
	 */
	public void prepararExclusao(ProtocoloAssociadoVO item) {
		this.itemExclusao = item;
		RequestContext.getCurrentInstance().execute("PF('modal_excluir').show()");
	}
	
	/**
	 * Exclui relacionamento.
	 */
	public void excluir() {
		List<MptProtocoloAssociacao> item = this.procedimentoTerapeuticoFacade.buscarAssociacoesPorAgrupador(itemExclusao.getAgrupador());
		if(item.size() == 2){
			for(MptProtocoloAssociacao exclusao: item){
				this.procedimentoTerapeuticoFacade.excluirRelacionamento(exclusao.getSeq());				
			}			
		}else{
			this.procedimentoTerapeuticoFacade.excluirRelacionamento(itemExclusao.getSeq());			
		}
		this.apresentarMsgNegocio(Severity.INFO, "MSG_PROTOCOLO_EXCLUIDO");
	}

	@Override
	public Long recuperarCount() {		
		Long count = procedimentoTerapeuticoFacade.listarProtocolosAssociacaoCount(seqProtocolo);
		if(count > 0){
			return count;			
		}else{
			return Long.valueOf("1");
		}
	}

	@Override
	public List<ProtocoloAssociadoVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {		
		List<ProtocoloAssociadoVO> item = procedimentoTerapeuticoFacade.listarProtocolosAssociacao(firstResult, maxResults, orderProperty, asc, seqProtocolo);
		if(item != null){
			for(ProtocoloAssociadoVO elemento : item){
				if(elemento.getSeqProtocoloAssociacao().equals(seqProtocolo)){
					item.remove(elemento);
					item.add(0, elemento);
					break;
				}
			}			
		}else{
			item = new ArrayList<ProtocoloAssociadoVO>();
			ProtocoloAssociadoVO elemento = new ProtocoloAssociadoVO();
			elemento.setSeq(1);
			elemento.setAgrupador(1);
			elemento.setSeqProtocoloAssociacao(seqProtocolo);
			elemento.setTitulo(this.descricaoTitulo);
			item.add(elemento);
		}
		return item;
	}	

//	GETTERS E SETTERS
	public MptProtocoloSessao getProtocoloSelecionado() {
		return protocoloSelecionado;
	}

	public void setProtocoloSelecionado(MptProtocoloSessao protocoloSelecionado) {
		this.protocoloSelecionado = protocoloSelecionado;
	}
	
	public Integer getSeqProtocolo() {
		return seqProtocolo;
	}

	public void setSeqProtocolo(Integer seqProtocolo) {
		this.seqProtocolo = seqProtocolo;
	}

	public DynamicDataModel<ProtocoloAssociadoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ProtocoloAssociadoVO> dataModel) {
		this.dataModel = dataModel;
	}

	public List<MptProtocoloSessao> getSugConsultado() {
		return sugConsultado;
	}

	public void setSugConsultado(List<MptProtocoloSessao> sugConsultado) {
		this.sugConsultado = sugConsultado;
	}

	public MptProtocoloAssociacao getAssociacao() {
		return associacao;
	}

	public void setAssociacao(MptProtocoloAssociacao associacao) {
		this.associacao = associacao;
	}

	public Boolean getImpedirRelacionamento() {
		return impedirRelacionamento;
	}

	public void setImpedirRelacionamento(Boolean impedirRelacionamento) {
		this.impedirRelacionamento = impedirRelacionamento;
	}

	public Boolean getExisteRelacionamento() {
		return existeRelacionamento;
	}

	public void setExisteRelacionamento(Boolean existeRelacionamento) {
		this.existeRelacionamento = existeRelacionamento;
	}

	public MptProtocoloAssociacao getAssociacao2() {
		return associacao2;
	}

	public void setAssociacao2(MptProtocoloAssociacao associacao2) {
		this.associacao2 = associacao2;
	}

	public ProtocoloAssociadoVO getItemExclusao() {
		return itemExclusao;
	}

	public void setItemExclusao(ProtocoloAssociadoVO itemExclusao) {
		this.itemExclusao = itemExclusao;
	}

	public String getDescricaoTitulo() {
		return descricaoTitulo;
	}

	public void setDescricaoTitulo(String descricaoTitulo) {
		this.descricaoTitulo = descricaoTitulo;
	}
	
}
