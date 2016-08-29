package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelExameHorarioColeta;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * @author amalmeida
 * 
 */
public class ManterHorarioColetaExameController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -6337757643199076189L;

	private static final String PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD = "exames-manterDadosBasicosExamesCRUD";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	private AelExameHorarioColeta exameHorarioColeta;

	private AelExamesMaterialAnalise examesMaterialAnalise;

	private String sigla;

	private Integer manSeq;

	private Integer seqp;

	/**
	 * Lista de Horários de Coleta do Exame
	 */
	private List<AelExameHorarioColeta> listaHorariosColetaExame;

	private AelExameHorarioColeta parametroSelecionado;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
	 


		if (StringUtils.isNotBlank(this.sigla) && this.manSeq != null) {
			this.examesMaterialAnalise = this.examesFacade.buscarAelExamesMaterialAnalisePorId(this.sigla, this.manSeq);
			this.atualizarLista(sigla, manSeq);
			this.exameHorarioColeta = new AelExameHorarioColeta();

		}

	
	}

	/**
	 * Atualiza a lista de Horários de Coleta de exames
	 * 
	 * @param sigla
	 * @param manSeq
	 */
	private void atualizarLista(String sigla, Integer manSeq) {
		this.listaHorariosColetaExame = this.examesFacade.listaHorariosColetaExames(sigla, manSeq);
	}

	/**
	 * Cancela o cadastro
	 */
	public String cancelar() {
		
		this.exameHorarioColeta = null;
		this.examesMaterialAnalise = null;
		this.sigla = null;
		this.manSeq = null;
		this.seqp = null;
		this.listaHorariosColetaExame = null;
		this.parametroSelecionado = null;
		
		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}

	public Integer getManSeq() {
		return manSeq;
	}

	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}

	/**
	 * Método que realiza a ação do botão gravar na tela de cadastro de Horário de Coleta de Exame
	 */
	public void gravar() {

		// Verifica se a ação é de criação ou edição
		boolean criando = exameHorarioColeta.getId() == null;

		try {

			if (!criando && verificarAlteradoOutroUsuario(exameHorarioColeta)) {
				this.limpar();
				return;
			}

			// Vincula examesMaterialAnalise com ExameHorarioColeta
			this.exameHorarioColeta.setExamesMaterialAnalise(examesMaterialAnalise);
			// Submete o procedimento para ser persistido
			cadastrosApoioExamesFacade.persistirHorarioColetaExame(exameHorarioColeta);

			if (criando) {
				// Apresenta as mensagens de acordo
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_HORARIO_EXAMES");
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_HORARIO_EXAMES");
			}

		} catch (BaseException e) {

			apresentarExcecaoNegocio(e);

		}

		limpar();

	}

	/**
	 * Edita o registro
	 * 
	 * @param AelExameHorarioColeta
	 */
	public void editar(AelExameHorarioColeta oldExameHorarioColeta) {

		if (verificarAlteradoOutroUsuario(oldExameHorarioColeta)) {
			atualizarLista(sigla, manSeq);
			return;
		}

		this.seqp = oldExameHorarioColeta.getId().getSeqp();
		this.exameHorarioColeta.setVersion(oldExameHorarioColeta.getVersion());
		this.exameHorarioColeta.setDiaSemana(oldExameHorarioColeta.getDiaSemana());
		this.exameHorarioColeta.setHorarioFinal(oldExameHorarioColeta.getHorarioFinal());
		this.exameHorarioColeta.setHorarioInicial(oldExameHorarioColeta.getHorarioInicial());
		this.exameHorarioColeta.setCriadoEm(oldExameHorarioColeta.getCriadoEm());
		this.exameHorarioColeta.setIndSituacao(oldExameHorarioColeta.getIndSituacao());
		this.exameHorarioColeta.setServidor(oldExameHorarioColeta.getServidor());
		this.exameHorarioColeta.setExamesMaterialAnalise(oldExameHorarioColeta.getExamesMaterialAnalise());
		this.exameHorarioColeta.setId(oldExameHorarioColeta.getId());

	}

	public void limpar() {

		this.seqp = null;
		this.exameHorarioColeta = new AelExameHorarioColeta();
		atualizarLista(sigla, manSeq);

	}

	/**
	 * Exclui o registro
	 * 
	 * @param recomendacaoExame
	 */
	public void excluir() {

		if (verificarAlteradoOutroUsuario(this.parametroSelecionado)) {
			atualizarLista(sigla, manSeq);
			return;
		}

		try {
			AelExameHorarioColeta horarioColetaExame = examesFacade.obterExameHorarioColetaPorID(this.parametroSelecionado.getId());
			cadastrosApoioExamesFacade.removerHorarioColetaExame(horarioColetaExame);
			limpar();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_HORARIO_EXAMES");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			this.parametroSelecionado = null;
		}

	}

	private boolean verificarAlteradoOutroUsuario(AelExameHorarioColeta entidade) {
		if (entidade == null || this.examesFacade.obterExameHorarioColetaPorID(entidade.getId()) == null) {
			apresentarMsgNegocio(Severity.INFO, "REGISTRO_NULO_EXCLUSAO");
			return true;
		}
		return false;
	}

	public AelExamesMaterialAnalise getExamesMaterialAnalise() {
		return examesMaterialAnalise;
	}

	public void setExamesMaterialAnalise(AelExamesMaterialAnalise examesMaterialAnalise) {
		this.examesMaterialAnalise = examesMaterialAnalise;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public AelExameHorarioColeta getExameHorarioColeta() {
		return exameHorarioColeta;
	}

	public void setExameHorarioColeta(AelExameHorarioColeta exameHorarioColeta) {
		this.exameHorarioColeta = exameHorarioColeta;
	}

	public List<AelExameHorarioColeta> getListaHorariosColetaExame() {
		return listaHorariosColetaExame;
	}

	public void setListaHorariosColetaExame(List<AelExameHorarioColeta> listaHorariosColetaExame) {
		this.listaHorariosColetaExame = listaHorariosColetaExame;
	}

	public Integer getSeqp() {
		return seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	public AelExameHorarioColeta getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(AelExameHorarioColeta parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
}
