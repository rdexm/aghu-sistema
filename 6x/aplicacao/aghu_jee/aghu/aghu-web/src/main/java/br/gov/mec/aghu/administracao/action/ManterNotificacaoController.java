package br.gov.mec.aghu.administracao.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.business.scheduler.NotificacaoJobEnum;
import br.gov.mec.aghu.configuracao.vo.NotificacaoDestinoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioIndTerminoNotificacoes;
import br.gov.mec.aghu.model.AghNotificacoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

public class ManterNotificacaoController extends ActionController {


	private static final long serialVersionUID = 1434524362438842580L;
	
	private static final Log LOG = LogFactory.getLog(ManterNotificacaoController.class);

	private static final String NOTIFICACOES_PESQUISA = "notificacoesPesquisa";

	@EJB
    private IRegistroColaboradorFacade registroColaboradorFacade;
	@EJB
	private IAdministracaoFacade administracaoFacade;
	
	private List<NotificacaoDestinoVO> notificacaoDestinos = new ArrayList<NotificacaoDestinoVO>();
	private List<Integer> notificacaoDestinosExcluidos = new ArrayList<Integer>();
	
	private AghNotificacoes notificacao;
	private NotificacaoJobEnum processo;
	private RapServidores servidorDestino;
	private String celular;
	private NotificacaoDestinoVO destinoEdicao;
	
	
	private Boolean destinoEmEdicao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Chamado no inicio de "cada conversacao"
	 */
	public void iniciar() {
		if(notificacao == null) {
			notificacao = new AghNotificacoes();
		} else {
			notificacaoDestinos.clear();
			notificacaoDestinos.addAll(administracaoFacade.obtemDestinosDaNotificacao(notificacao.getSeq()));
			for(NotificacaoJobEnum not : NotificacaoJobEnum.values()) {
				if(not.getDescricao().equals(notificacao.getNomeProcesso())) {
					processo = not;
					break;
				}
			}
		}
	}
	
	public List<RapServidores> pesquisarServidorContato(String param) {
		return returnSGWithCount(this.registroColaboradorFacade.obterListaProfissionalPorMatriculaVinculoOuNome(param),
                this.registroColaboradorFacade.obterCountProfissionalPorMatriculaVinculoOuNome(param));
	}
	
	public void adicionarDestino() {
		try {
			administracaoFacade.validarDestino(servidorDestino, celular != null ? Long.valueOf(obtemCelularSemMascara()) : null);
			NotificacaoDestinoVO dest = new NotificacaoDestinoVO();
			carregarDadosDestinatario(dest);
			notificacaoDestinos.add(dest);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ADICIONAR_DESTINATARIO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
		servidorDestino = null;
		celular = null;
	}

	private String obtemCelularSemMascara() {
		String cel = celular;
		cel = cel.replace("(", "");
		cel = cel.replace(")", "");
		cel = cel.replace("-", "");
		cel = cel.replace(" ", "");
		return cel;
	}

	public void editarDestino(NotificacaoDestinoVO destino) {
		servidorDestino = registroColaboradorFacade.obterRapServidorPorVinculoMatricula(destino.getMatriculaContato(), destino.getVinCodigoContato());
		String celTmp = destino.getDddCelular().toString().concat(destino.getCelular().toString());
		celular = celTmp;
		destinoEdicao = destino;
		destinoEmEdicao = true;
	}
	
	public void alterarDestino() {
		try {
			administracaoFacade.validarDestino(servidorDestino, celular != null ? Long.valueOf(obtemCelularSemMascara()) : null);
			for(NotificacaoDestinoVO dest : notificacaoDestinos) {
				if(dest.equals(destinoEdicao)) {
					carregarDadosDestinatario(dest);
					break;
				}
			}
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_ALTERAR_DESTINATARIO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
		limparEdicaoDestino();
	}
	
	public void excluirDestino(NotificacaoDestinoVO destino) {
		if(destino.getSeq() != null) {
			notificacaoDestinosExcluidos.add(destino.getSeq());
		}
		notificacaoDestinos.remove(destino);
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_EXCLUIR_DESTINATARIO");
	}
	
	public void limparEdicaoDestino() {
		servidorDestino = null;
		celular = null;
		destinoEdicao = null;
		destinoEmEdicao = false;
	}
	
	public String gravar() {
		notificacao.setNomeProcesso(processo.getDescricao());
		try {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			administracaoFacade.gravarNotificacao(notificacao, notificacaoDestinos, notificacaoDestinosExcluidos, nomeMicrocomputador);
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_GRAVADA_NOTIFICACAO");
			limparTela();
			return NOTIFICACOES_PESQUISA;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public void atualizarTerminoOcorrencias() {
		if(DominioIndTerminoNotificacoes.ST.equals(notificacao.getIndTerminoNotificacoes())) {
			notificacao.setTerminaEm(null);
			notificacao.setTerminaApos(null);
		} else if(DominioIndTerminoNotificacoes.TA.equals(notificacao.getIndTerminoNotificacoes())) {
			notificacao.setTerminaEm(null);
		} else if(DominioIndTerminoNotificacoes.TE.equals(notificacao.getIndTerminoNotificacoes())) {
			notificacao.setTerminaApos(null);
		}
	}
	
	public void obtemCelularPessoaFisica() {
		if(servidorDestino.getPessoaFisica().getDddFoneCelular() != null
				&& servidorDestino.getPessoaFisica().getFoneCelular() != null) {
			String cel = servidorDestino.getPessoaFisica().getDddFoneCelular().toString();
			cel = cel.concat(servidorDestino.getPessoaFisica().getFoneCelular().toString());
			celular = cel;
		}
	}
	
	public void limparCelular() {
		celular = null;
	}
	
	public String formatarCelular(NotificacaoDestinoVO destino) {
		StringBuffer celFormatado = new StringBuffer();
		celFormatado.append('(').append(destino.getDddCelular().toString()).append(") ");
		
		String cel = destino.getCelular().toString();
		String prefix = cel.substring(0, cel.length() -4);
		String sufix = cel.substring(cel.length() -4, cel.length());
		
		celFormatado.append(prefix).append('-').append(sufix);
		return celFormatado.toString();
	}
	
	private void limparTela() {
		notificacao = null;
		processo = null;
		servidorDestino = null;
		destinoEdicao = null;
		destinoEmEdicao = false;
		celular = null;
		notificacaoDestinos.clear();
		notificacaoDestinosExcluidos.clear();
	}
	
	private void carregarDadosDestinatario(NotificacaoDestinoVO dest) {
		dest.setDddCelular(administracaoFacade.obtemDddCelular(Long.valueOf(obtemCelularSemMascara())));
		dest.setCelular(Long.valueOf(administracaoFacade.obtemNumeroCelular(Long.valueOf(obtemCelularSemMascara()))));
		dest.setMatriculaContato(servidorDestino.getId().getMatricula());
		dest.setVinCodigoContato(servidorDestino.getId().getVinCodigo());
		dest.setNomePessoaFisica(servidorDestino.getPessoaFisica().getNome());
	}
	
	/**
	 * Cancela a insercao ou alteracao na tela
	 */
	public String cancelar() {
		limparTela();
		return NOTIFICACOES_PESQUISA;
	}

	public AghNotificacoes getNotificacao() {
		return notificacao;
	}

	public void setNotificacao(AghNotificacoes notificacao) {
		this.notificacao = notificacao;
	}

	public NotificacaoJobEnum getProcesso() {
		return processo;
	}

	public void setProcesso(NotificacaoJobEnum processo) {
		this.processo = processo;
	}

	public RapServidores getServidorDestino() {
		return servidorDestino;
	}

	public void setServidorDestino(RapServidores servidorDestino) {
		this.servidorDestino = servidorDestino;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public Boolean getDestinoEmEdicao() {
		return destinoEmEdicao;
	}

	public void setDestinoEmEdicao(Boolean destinoEmEdicao) {
		this.destinoEmEdicao = destinoEmEdicao;
	}

	public NotificacaoDestinoVO getDestinoEdicao() {
		return destinoEdicao;
	}

	public void setDestinoEdicao(NotificacaoDestinoVO destinoEdicao) {
		this.destinoEdicao = destinoEdicao;
	}
	
	public List<NotificacaoDestinoVO> getNotificacaoDestinos() {
		return notificacaoDestinos;
	}

	public void setNotificacaoDestinos(
			List<NotificacaoDestinoVO> notificacaoDestinos) {
		this.notificacaoDestinos = notificacaoDestinos;
	}

	public List<Integer> getNotificacaoDestinosExcluidos() {
		return notificacaoDestinosExcluidos;
	}

	public void setNotificacaoDestinosExcluidos(
			List<Integer> notificacaoDestinosExcluidos) {
		this.notificacaoDestinosExcluidos = notificacaoDestinosExcluidos;
	}

}
