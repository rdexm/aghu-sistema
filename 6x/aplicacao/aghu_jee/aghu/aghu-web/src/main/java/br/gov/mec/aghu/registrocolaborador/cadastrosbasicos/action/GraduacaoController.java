package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoQualificacao;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;

public class GraduacaoController extends ActionController {

	private static final long serialVersionUID = 7569871442764985185L;

	private static final Short NIVEL_CURSO_GRADUACAO_ID = 3;

	private static final String PESQUISAR_GRADUACAO = "pesquisarGraduacao";

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	private RapTipoQualificacao rapTipoQualificacao;

	private boolean altera;

	private Enum[] fetchArgsLeftJoin = {RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL}; 

	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 


		if (rapTipoQualificacao != null && rapTipoQualificacao.getCodigo() != null) {

			try {
				rapTipoQualificacao = cadastrosBasicosFacade.obterGraduacao(rapTipoQualificacao.getCodigo(), null, fetchArgsLeftJoin);

				if(rapTipoQualificacao == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return cancelar();
				}
				
				altera = true;
			} catch(BaseRuntimeException br){
				apresentarExcecaoNegocio(br);
				return cancelar();
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				return cancelar();
			}
		} else {
			this.rapTipoQualificacao = new RapTipoQualificacao();
			this.rapTipoQualificacao.setIndSituacao(DominioSituacao.A);
		}
		
		return null;
	
	}


	public String salvar() {

		// se o tipo escolhido for "csc", não pode escolher um conselho. nesse caso, impede o registro e mostra mensagem na tela. 
		try {
			this.cadastrosBasicosFacade.validarRegistroGraduacaoConselho (this.rapTipoQualificacao);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
		try {

			if (this.rapTipoQualificacao.getTipoQualificacao() == DominioTipoQualificacao.CCC) {
				this.rapTipoQualificacao.setCccNivelCurso(NIVEL_CURSO_GRADUACAO_ID);
				this.rapTipoQualificacao.setCscNivelCurso(null);
				this.rapTipoQualificacao.setEveCargaHoraria(null);
				
			} else if (this.rapTipoQualificacao.getTipoQualificacao() == DominioTipoQualificacao.CSC) {
				this.rapTipoQualificacao.setCscNivelCurso(NIVEL_CURSO_GRADUACAO_ID);
				this.rapTipoQualificacao.setCccNivelCurso(null);
				this.rapTipoQualificacao.setEveCargaHoraria(null);
			}

			if (altera) {
				this.cadastrosBasicosFacade.alterar(this.rapTipoQualificacao);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EDICAO_GRADUACAO", this.rapTipoQualificacao.getDescricao());
				
			} else {
				this.rapTipoQualificacao.setIndIntermedioHcpaCapacitacao(false);
				this.rapTipoQualificacao.setIndIntermedioHcpaEvento(false);
				this.rapTipoQualificacao.setIndIntermedioServidorNacional(false);
				this.rapTipoQualificacao.setIndIntermedioServidorExterior(false);
				this.rapTipoQualificacao.setIndIntermedioServidorFuncao(false);
				this.rapTipoQualificacao.setIndIntermedioServidorCurriculo(false);
				this.rapTipoQualificacao.setIndTreinamentoServico(false);
				this.cadastrosBasicosFacade.salvar(this.rapTipoQualificacao);

				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_GRADUACAO", this.rapTipoQualificacao.getDescricao());
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return cancelar();
	}

	public String cancelar() {
		altera = false;
		rapTipoQualificacao = null;
		return PESQUISAR_GRADUACAO;
	}

	/**
	 * Pesquisa conselhos para a suggestion box.
	 */
	public List<RapConselhosProfissionais> pesquisarConselhosPorDescricao(String descricao) {
		return cadastrosBasicosFacade.pesquisarConselhosPorDescricao((String) descricao);
	}

	public RapTipoQualificacao getRapTipoQualificacao() {
		return rapTipoQualificacao;
	}

	public void setRapTipoQualificacao(RapTipoQualificacao rapTipoQualificacao) {
		this.rapTipoQualificacao = rapTipoQualificacao;
	}

	public boolean isAltera() {
		return altera;
	}

	public void setAltera(boolean altera) {
		this.altera = altera;
	}
}