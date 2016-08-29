package br.gov.mec.aghu.administracao.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.business.scheduler.NotificacaoJobEnum;
import br.gov.mec.aghu.configuracao.vo.NotificacaoDestinoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.model.AghNotificacoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


public class NotificacoesPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2101926008179747867L;

	@EJB
	private IAdministracaoFacade administracaoFacade;
	@EJB
    private IRegistroColaboradorFacade registroColaboradorFacade;
	
	// Campos de filtro para pesquisa
	private String descricao;
	private NotificacaoJobEnum processo;
	private RapServidores servidorContato;
	private Long celular;
	

	@Inject @Paginator
	private DynamicDataModel<AghNotificacoes> dataModel;
	
	private AghNotificacoes selecionado;
	
	private List<NotificacaoDestinoVO> notificacaoDestinos = new ArrayList<NotificacaoDestinoVO>();

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	
	public List<RapServidores> pesquisarServidorContato(String param) {
		return returnSGWithCount(this.registroColaboradorFacade.obterListaProfissionalPorMatriculaVinculoOuNome(param),
                this.registroColaboradorFacade.obterCountProfissionalPorMatriculaVinculoOuNome(param));
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return administracaoFacade.pesquisarNotificacoesCount(descricao,
				processo != null ? processo.getDescricao() : null, servidorContato, celular);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AghNotificacoes> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<AghNotificacoes> notificacoes = administracaoFacade.pesquisarNotificacoes(firstResult, maxResult, orderProperty, asc,
				descricao, processo != null ? processo.getDescricao() : null, servidorContato, celular);
		notificacaoDestinos.clear();
		for(AghNotificacoes notificacao : notificacoes) {
			notificacaoDestinos.addAll(administracaoFacade.obtemDestinosDaNotificacao(notificacao.getSeq()));
		}
		return notificacoes;
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		descricao = null;
		processo = null;
		servidorContato = null;
		celular = null;
	}
	
	public void excluir() {
		try {
			administracaoFacade.excluirNotificacao(selecionado);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_NOTIFICACAO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String obtemCelulares(Integer ntsSeq, Boolean tooltip) {
		StringBuffer destinos = new StringBuffer();
		Integer count = 0;
		for(NotificacaoDestinoVO destino : notificacaoDestinos) {
			if(destino.getNtsSeq().equals(ntsSeq)) {
				if(!tooltip && count >= 4) {
					destinos.append("...");
					break;
				}
				if(destinos.length() > 0) {
					destinos.append(" | ");
				}
				destinos.append('(').append(destino.getDddCelular().toString()).append(") ");
				
				String cel = destino.getCelular().toString();
				String prefix = cel.substring(0, cel.length() -4);
				String sufix = cel.substring(cel.length() -4, cel.length());
				
				destinos.append(prefix).append('-').append(sufix);
				count++;
			}
		}
		return destinos.toString();
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public NotificacaoJobEnum getProcesso() {
		return processo;
	}

	public void setProcesso(NotificacaoJobEnum processo) {
		this.processo = processo;
	}

	public RapServidores getServidorContato() {
		return servidorContato;
	}

	public void setServidorContato(RapServidores servidorContato) {
		this.servidorContato = servidorContato;
	}

	public Long getCelular() {
		return celular;
	}

	public void setCelular(Long celular) {
		this.celular = celular;
	}

	public DynamicDataModel<AghNotificacoes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghNotificacoes> dataModel) {
		this.dataModel = dataModel;
	}

	public AghNotificacoes getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AghNotificacoes selecionado) {
		this.selecionado = selecionado;
	}

	public List<NotificacaoDestinoVO> getNotificacaoDestinos() {
		return notificacaoDestinos;
	}

	public void setNotificacaoDestinos(
			List<NotificacaoDestinoVO> notificacaoDestinos) {
		this.notificacaoDestinos = notificacaoDestinos;
	}
	
}